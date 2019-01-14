package Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import Entities.Member;
import Interfaces.RolfsServerListener;
import henrik.mau.rolfsstalkerapplikation.Controller;
import henrik.mau.rolfsstalkerapplikation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, RolfsServerListener {
    private Controller controller;
    private MapView mapView;
    private GoogleMap map;
    private ArrayList<Member> members;
    private Button btnLeaveGroup;
    private String userName;
    private Handler handler = new Handler(Looper.getMainLooper());
    private LatLng latLng;
    private Marker marker;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,      //4
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view){
        mapView = view.findViewById(R.id.mapView);
    }

    public void setController(Controller controller){
        this.controller = controller;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
    }

    public void setMembers(ArrayList<Member> members){
        this.members = members;
    }

    public void setMapMarkers(){
        map.clear();
        for(int i = 0; i < members.size(); i++){
            latLng = new LatLng(members.get(i).getLatitude(), members.get(i).getLongitude());
            Log.d("INCOMINGOBJECT", latLng.toString());
            map.addMarker(new MarkerOptions().position(latLng).title(members.get(i).getName()));
            CameraPosition campos = new CameraPosition.Builder().target(latLng).zoom(15).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(campos));
        }
    }

    @Override
    public ArrayList<Member> memberRequest() {
        return null;
    }

    @Override
    public void groupRequest(ArrayList<String> groups) {

    }

    @Override
    public void onPositionsMessage(final ArrayList<Member> members) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setMembers(members);
                setMapMarkers();
            }
        });
    }
}
