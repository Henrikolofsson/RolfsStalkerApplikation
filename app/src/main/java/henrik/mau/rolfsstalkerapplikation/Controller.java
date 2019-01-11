package henrik.mau.rolfsstalkerapplikation;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import Fragment.StartFragment;
import Fragment.DataFragment;
import Fragment.MainFragment;

public class Controller {
    private MainActivity activity;

    private StartFragment startFragment;
    private DataFragment dataFragment;
    private MainFragment mainFragment;

    private static String IP = "195.178.227.53";
    private static int port = 8443;
    private InetAddress inetAddress;
    private Socket connectedSocket;
    private DataOutputStream dos;
    private DataInputStream dis;

    private String username;

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
    }

    /*
       Initializes the fragments, and sets itself as their controller.
     */
    private void initializeFragments(){
        initializeDataFragment();
        initializeStartFragment();
        initializeMainFragment();
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
        return false;
    }

    /*
       Creates a string for the username, so it can be reached from other fragments.
     */
    public void logIn(String username){
        this.username = username;
        setFragment("MainFragment");
    }

    public String getUsername(){
        return username;
    }

    public void connect(){
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
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void checkInput(JSONObject incomingObject){
        Log.d("INCOMINGOBJECT:", incomingObject.toString());
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
}
