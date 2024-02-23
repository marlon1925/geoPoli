package com.example.geopoli;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MapsScreen extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double latitude = 0;
    private double longitude = 0;
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_screen);
        type = getIntent().getExtras().getString("type").toUpperCase(); // Convertir a mayúsculas
        System.out.println("typo  " + type);
        if (type.equals("ONE")) {
            latitude = Double.parseDouble(getIntent().getExtras().getString("latitued"));
            longitude = Double.parseDouble(getIntent().getExtras().getString("longitude"));
        } else {
            latitude = 0;
            longitude = 0;
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
        switch (type) {
            case "ALL": {
                displayAllUsersLocations();
                break;
            }
            case "ONE": {
                LatLng sydney = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(sydney).title("Ubicación de " + Params.useerApp.getName() + ":Coordenadas" + Double.toString(latitude) + ":" + Double.toString(longitude)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
                break;
            }
        }
    }

    private void displayAllUsersLocations() {
        FirebaseDatabase.getInstance().getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Crear una estructura para almacenar datos combinados
                        Map<String, String> userNamesByUserId = new HashMap<>();

                        // Obtener datos de usuarios
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId = userSnapshot.child("id").getValue(String.class);
                            String name = userSnapshot.child("name").getValue(String.class);
                            String lastName = userSnapshot.child("lastName").getValue(String.class);
                            String userName = name + " " + lastName;

                            // Almacenar nombre de usuario por ID
                            userNamesByUserId.put(userId, userName);

                            // Obtener datos de ubicaciones
                            FirebaseDatabase.getInstance().getReference("locations").child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String latitude = dataSnapshot.child("latitude").getValue(String.class);
                                                String longitude = dataSnapshot.child("longitud").getValue(String.class);
                                                String id = dataSnapshot.child("id").getValue(String.class);

                                                // Obtén el nombre de usuario del mapa usando el ID
                                                String userName = userNamesByUserId.get(id);

                                                // Agrega marcadores al mapa
                                                if (mMap != null && userName != null) {
                                                    LatLng location = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                                    MarkerOptions markerOptions = new MarkerOptions()
                                                            .position(location)
                                                            .title(userName); // Establecer el nombre como título del marcador
                                                    Marker marker = mMap.addMarker(markerOptions);

                                                    // Configurar el clic del marcador
                                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                        @Override
                                                        public boolean onMarkerClick(Marker marker) {
                                                            // Obtén el título del marcador (nombre del usuario)
                                                            String userName = marker.getTitle();

                                                            // Muestra el nombre en un Toast
                                                            Toast.makeText(getApplicationContext(), "Usuario: " + userName, Toast.LENGTH_SHORT).show();

                                                            // Devuelve true para indicar que el evento de clic en el marcador ha sido manejado
                                                            return true;
                                                        }
                                                    });

                                                    marker.showInfoWindow(); // Mostrar la información del marcador

                                                    // Mueve la cámara solo una vez
                                                    if (mMap.getCameraPosition().zoom < 12) {
                                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Manejar el error en caso de que la lectura de la base de datos falle
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar el error en caso de que la lectura de la base de datos falle
                    }
                });
    }
}
