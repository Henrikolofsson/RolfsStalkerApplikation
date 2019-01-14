package henrik.mau.rolfsstalkerapplikation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import Entities.Member;
import Entities.User;
import Fragment.*;
import Interfaces.ActivityListener;
import Interfaces.RolfsServerListener;

public class Controller implements ActivityListener {
    private MainActivity activity;

    private StartFragment startFragment;
    private DataFragment dataFragment;
    private MainFragment mainFragment;
    private RegisterFragment registerFragment;
    private UnregisterFragment unregisterFragment;
    private MapFragment mapFragment;

    private static String IP = "195.178.227.53";
    private static int port = 8443;
    private InetAddress inetAddress;
    private Socket connectedSocket;
    private DataOutputStream dos;
    private DataInputStream dis;

    private User user;
    private String username;
    private boolean registered = false;
    private String registeredGroup;

    private ArrayList<RolfsServerListener> serverListeners = new ArrayList<>();
    private JSONArray jsonArray;
    private ArrayList<String> groups = new ArrayList<>();
    private Location lastLocation;
    private Handler handler = new Handler(Looper.getMainLooper());
    private LocationManager locationManager;
    private LocationListener locationListener;

    private HashMap<String, ArrayList<Member>> memberHash = new HashMap<String, ArrayList<Member>>();
    private ArrayList<Member> tempArray;
    private Member member;

    /*
       The constructor of the controller. It first creates all the fragments.
       After that it sets the start fragment, and open up a connection with Rolf:s server.
       After opening up a connection, it listens on incoming object in a thread.
     */
    public Controller(MainActivity activity){
        this.activity = activity;
        initializeFragments();
        setFragment("StartFragment");
        connect();
        initServerListeners();
        initLocationSender();
    }


    /*
       Initializes the fragments, and sets itself as their controller.
     */
    private void initializeFragments(){
        initializeDataFragment();
        initializeStartFragment();
        initializeMainFragment();
        initializeRegisterFragment();
        initializeUnregisterFragment();
        initializeMapFragment();
    }

    private void initializeDataFragment(){
        dataFragment = (DataFragment) activity.getFragment("DataFragment");
        if (dataFragment == null) {
            dataFragment = new DataFragment();
            activity.addFragment(dataFragment, "DataFragment");
            dataFragment.setActiveFragment("StartFragment");
        }
    }

    private void initializeStartFragment(){
        startFragment = (StartFragment) activity.getFragment("StartFragment");
        if (startFragment == null) {
            startFragment = new StartFragment();
        }
        startFragment.setController(this);
    }

    private void initializeMainFragment(){
        mainFragment = (MainFragment) activity.getFragment("MainFragment");
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        mainFragment.setController(this);
    }

    private void initializeRegisterFragment(){
        registerFragment = (RegisterFragment) activity.getFragment("RegisterFragment");
        if (registerFragment == null) {
            registerFragment = new RegisterFragment();
        }
        registerFragment.setController(this);
    }

    private void initializeUnregisterFragment(){
        unregisterFragment = (UnregisterFragment) activity.getFragment("UnregisterFragment");
        if(unregisterFragment == null){
            unregisterFragment = new UnregisterFragment();
        }
        unregisterFragment.setController(this);
    }

    private void initializeMapFragment(){
        mapFragment = (MapFragment) activity.getFragment("MapFragment");
        if(mapFragment == null){
            mapFragment = new MapFragment();
        }
        mapFragment.setController(this);
    }

    private void initServerListeners(){
        serverListeners.add(mainFragment);
        serverListeners.add(mapFragment);
    }

    private void initLocationSender(){
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener();
        positionSender();
    }

    /*
       A method used for dynamically changing fragments in the main container.
       Sending the wanted fragments to the next setFragment method.
     */
    private void setFragment(String tag) {
        switch (tag) {
            case "StartFragment":
                setFragment(startFragment, tag);
                break;
            case "MainFragment":
                setFragment(mainFragment, tag);
                break;
            case "RegisterFragment":
                setFragment(registerFragment, tag);
                break;
            case "UnregisterFragment":
                setFragment(unregisterFragment, tag);
                break;
            case "MapFragment":
                setFragment(mapFragment, tag);
                break;
        }
    }

    /*
       Sends the wanted fragments to main activity to be changed in the main container.
     */
    private void setFragment(Fragment fragment, String tag) {
        activity.setFragment(fragment, tag);
        dataFragment.setActiveFragment(tag);
    }

    public boolean onBackPressed() {
        String activeFragment = dataFragment.getActiveFragment();
        if(activeFragment.equals("StartFragment")){
            return false;
        }

        switch(activeFragment){
            case "MainFragment":
                setFragment("StartFragment");
                break;

            case "MapFragment":
                setFragment("MainFragment");
                break;

            case "RegisterFragment":
                setFragment("MainFragment");
                break;

            case "UnregisterFragment":
                setFragment("MainFragment");
                break;
        }
        return false;
    }

    /*
       Creates a string for the username, so it can be reached from other fragments.
     */
    public void logIn(String username){
        user = new User(username);
        setFragment("MainFragment");
    }

    public String getUsername(){
        return user.getUserName();
    }

    public String getUserGroup(){
        return user.getGroup();
    }

    public void registerGroup(final String groupName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject register = new JSONObject();
                    register.put("type", "register");
                    register.put("group", groupName);
                    register.put("member", user.getUserName());
                    registeredGroup = groupName;
                    dos.writeUTF(register.toString());
                    dos.flush();
                } catch(JSONException e){
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void unregister(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject unregister = new JSONObject();
                    unregister.put("type", "unregister");
                    unregister.put("id", user.getUserID());
                    dos.writeUTF(unregister.toString());
                    dos.flush();
                    user.setUserID(null);
                    user.setGroup(null);
                    registeredGroup = null;
                } catch(JSONException e){
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public boolean getRegistered(){
        return registered;
    }

    /*
       Method that establish a connection with Rolf:s server and
       instantiate a thread which listens on the input.
     */
    private void connect(){
        final InputListener inputListener = new InputListener();
        final Thread inputListenerThread = new Thread(inputListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inetAddress = InetAddress.getByName(IP);
                    connectedSocket = new Socket(inetAddress, port);
                    dos = new DataOutputStream(connectedSocket.getOutputStream());
                    dis = new DataInputStream(connectedSocket.getInputStream());

                    if (connectedSocket != null) {
                        inputListenerThread.start();
                        startGroupListener();
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void checkInput(JSONObject incomingObject){
        Log.d("INCOMINGOBJECT:", incomingObject.toString());
        try {
            if (incomingObject.getString("type").equals("register")) {
                registered = true;
                user.setUserID(incomingObject.getString("id"));
                user.setGroup(registeredGroup);
            }

            else if(incomingObject.getString("type").equals("unregister")){
                registered = false;
            }

            else if(incomingObject.getString("type").equals("groups")){
                jsonArray = incomingObject.getJSONArray("groups");
                groups.clear();

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject individualGroup = jsonArray.getJSONObject(i);
                    groups.add(individualGroup.getString("group"));
                }

                for(final RolfsServerListener listener : serverListeners){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.groupRequest(groups);
                        }
                    });
                }


            }

            else if(incomingObject.getString("type").equals("members")){

            }

            else if(incomingObject.getString("type").equals("locations")){
                String group = incomingObject.getString("group");
                JSONArray array = incomingObject.getJSONArray("location");
                tempArray = new ArrayList<>();

                for(int i = 0; i <= array.length() - 1; i++){
                    JSONObject memberObject = array.getJSONObject(i);
                    String memberName = memberObject.getString("member");
                    String longitude = memberObject.getString("longitude");
                    String latitude = memberObject.getString("latitude");
                    member = new Member(memberName, Double.parseDouble(latitude), Double.parseDouble(longitude));
                    tempArray.add(member);
                    for(Member m : tempArray){
                        Log.d("Member", m.toString());
                    }
                    memberHash.put(group, tempArray);
                }

                if(dataFragment.getActiveFragment().equals("MapFragment")){
                    for(RolfsServerListener l : serverListeners){
                        l.onPositionsMessage(memberHash.get(user.getGroup()));
                    }
                }

            }

            else if(incomingObject.getString("type").equals("exception")){

            }

        } catch(JSONException e){
            e.printStackTrace();
        }
    }

   private void startGroupListener(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                getAvailableGroups();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 3000, 5000);
   }

   public void getAvailableGroups(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = new JSONObject();
                    json.put("type", "groups");
                    dos.writeUTF(json.toString());
                    dos.flush();
                } catch(JSONException e){
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
   }

    public void setRegisterGroupFragment(){
        setFragment("RegisterFragment");
   }

   public void setMainFragment(){
        setFragment("MainFragment");
   }

   public void setUnregisterFragment(){
        setFragment("UnregisterFragment");
   }

   public void setMapFragment(){
        setFragment("MapFragment");
   }

   private void positionSender(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(registered){
                    sendPosition();
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 1000, 10000);
   }

   /*
      Sends positions to Rolfs server
      -- Problem here is that it gives a lot of exceptions? Why? -- SOLVED!
       While-loop at registered user, overloading server.
    */
   private void sendPosition() {
       try {
           if (lastLocation != null) {
               JSONObject positionObject = new JSONObject();
               positionObject.put("type", "location");
               positionObject.put("id", user.getUserID());
               positionObject.put("longitude", Double.toString(lastLocation.getLongitude()));
               positionObject.put("latitude", Double.toString(lastLocation.getLatitude()));
               dos.writeUTF(positionObject.toString());
               dos.flush();
           }
       } catch(JSONException e){
           e.printStackTrace();
       } catch(IOException e){
           e.printStackTrace();
       }
   }

    private class InputListener implements Runnable {
        boolean running = true;
        @Override
        public void run(){
            while(running){
                try{
                    JSONObject incomingObject;
                    if(dis.available() > 0){
                        incomingObject = new JSONObject(dis.readUTF());
                        checkInput(incomingObject);
                    }
                } catch(IOException e){
                    e.printStackTrace();
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

    void onResume(){
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            lastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
