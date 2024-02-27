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
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_screen);
        type = getIntent().getExtras().getString("type").toUpperCase(); // Convertir a mayúsculas
        System.out.println("typo  " + type);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        // Mostrar todos los usuarios por defecto
        displayAllUsersLocations();
    }

    private void displayAllUsersLocations() {
        FirebaseDatabase.getInstance().getReference("users")
                .addValueEventListener(new ValueEventListener() {
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
                        }

                        // Obtener datos de ubicaciones en tiempo real
                        FirebaseDatabase.getInstance().getReference("locations")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // Eliminar marcadores antiguos
                                        mMap.clear();

                                        for (DataSnapshot locationSnapshot : dataSnapshot.getChildren()) {
                                            String userId = locationSnapshot.getKey(); // ID de usuario
                                            String latitude = locationSnapshot.child("latitude").getValue(String.class);
                                            String longitude = locationSnapshot.child("longitud").getValue(String.class);

                                            // Obtener nombre de usuario a partir del ID
                                            String userName = userNamesByUserId.get(userId);

                                            // Mostrar información combinada (nombre y ubicación)
                                            if (userName != null) {
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

                                                        // Devuelve true para indicar que el evento de clic en el marcador ha sido manej

                                                        return true;
                                                    }
                                                });

                                                marker.showInfoWindow(); // Mostrar la información del marcador
                                                if (mMap.getCameraPosition().zoom < 12) {
                                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Manejar errores
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Manejar errores
                    }
                });
    }
}
