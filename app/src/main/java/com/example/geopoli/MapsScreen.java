package com.example.geopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsScreen extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double latitude=0;
    private double longitude=0;
    private String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_screen);
        type=getIntent().getExtras().getString("type");
        System.out.println("typo  "+type);
        if(type.equals("ONE")){
            latitude= Double.parseDouble(getIntent().getExtras().getString("latitued"));
            longitude=Double.parseDouble(getIntent().getExtras().getString("longitude"));
        }else{
            latitude=0;
            longitude=0;
        }
      // Obtain the SupportMapFragment and get notified when the map is ready to be used.
       SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        switch (type){
            case "ALL":{
                LatLng sydney = new LatLng(latitude, longitude);
                for (UserCiclista user:Params.userCiclistasAll
                     ) {
                    if(user.getLastLocations().size()>0){
                        sydney = new LatLng(Double.parseDouble(user.getLastLocation(user.getLastLocations().size()-1).getLatitude()),Double.parseDouble(user.getLastLocation(user.getLastLocations().size()-1).getLongitud()));
                        if(Params.userId.equals(user.getCiclista().getId())){
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Tú Ubicación :Coordenadas"+(user.getLastLocation(user.getLastLocations().size()-1).getLatitude())+":"+user.getLastLocation(user.getLastLocations().size()-1).getLongitud()));

                        }else{
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicación de "+(user.getCiclista().getName()+":Coordenadas"+user.getLastLocation(user.getLastLocations().size()-1).getLatitude())+":"+user.getLastLocation(user.getLastLocations().size()-1).getLongitud()));

                        }

                    }

                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

                break;


            }
            case "ONE":{
                LatLng sydney = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicación de "+Params.useerApp.getName()+":Coordenadas"+Double.toString(latitude)+":"+Double.toString(longitude)));

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));
                break;
            }
        }

    }
}