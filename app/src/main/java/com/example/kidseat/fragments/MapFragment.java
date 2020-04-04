package com.example.kidseat.fragments;


import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kidseat.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = "MapFragment";

    private GoogleMap mMap;
    private LatLng eventLocation;
    private float zoom;
    private CameraUpdate cameraUpdate;


    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);

    }

    // This event is triggered soon after onCreateView().
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        zoom = 15;

        // Add a marker in Sydney and move the camera 37.573210, -84.286957
        eventLocation = new LatLng(37.573210, -84.286957);
        LatLng another = new LatLng(37.570693, -84.289817);
        LatLng loc2 = new LatLng(37.572000, -84.287500);
        LatLng loc3 = new LatLng(37.573700, -84.286000);
        mMap.addMarker(new MarkerOptions().position(eventLocation).title("Community Garden"));
        mMap.addMarker(new MarkerOptions().position(loc2).title("Deep Green Hall"));
        mMap.addMarker(new MarkerOptions().position(loc3).title("Woods-Penniman"));
        mMap.addMarker(new MarkerOptions()
                .position(another)
                .title("Community School"));


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, zoom));

    }

}
