package com.poli2024.geopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.maps.android.SphericalUtil;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;

public class LocationScreen extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ArrayList<LatLng> points = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Marker currentLocationMarker; // Referencia al marcador de la ubicación actual
    Mineros userMainApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_screen);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("terrenos");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button deleteLastMarkerButton = findViewById(R.id.deleteLastMarkerBtn);
        Button goToCurrentLocationButton = findViewById(R.id.goToCurrentLocationBtn);
        Button btnSave = findViewById(R.id.btnSave);
        Button gotoHistorial = findViewById(R.id.historial);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSaveTerrenoDialog();
            }
        });
        deleteLastMarkerButton.setOnClickListener(view -> deleteLastMarker());

        goToCurrentLocationButton.setOnClickListener(view -> goToCurrentLocation());

        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize LocationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    LatLng currentLatLng = new LatLng(locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude());
                    mMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
                }
            }
        };

        gotoHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LocationScreen.this, TerrenosHistorial.class);
                startActivity(intent);
            }
        });

    }
    private void showSaveTerrenoDialog() {
        if (points.size() >= 2) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_terreno, null);
            dialogBuilder.setView(dialogView);

            EditText editTextTerrenoName = dialogView.findViewById(R.id.editTextTerrenoName);
            Button buttonSave = dialogView.findViewById(R.id.buttonSave);

            AlertDialog dialog = dialogBuilder.create();
            dialog.show();

            buttonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String terrenoName = editTextTerrenoName.getText().toString().trim();
                    if (!terrenoName.isEmpty()) {
                        saveTerrenoToFirebase(terrenoName);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(LocationScreen.this, "Ingrese un nombre para el terreno", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(LocationScreen.this, "Debe haber al menos 3 marcadores para guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveTerrenoToFirebase(String terrenoName) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userTerrenoRef = mDatabase.child(userId).push();

            // Obtener el ID del terreno generado por push
            String terrenoId = userTerrenoRef.getKey();

            // Guardar el ID del terreno
            userTerrenoRef.child("id").setValue(terrenoId);

            // Luego, puedes guardar los demás campos del terreno
            userTerrenoRef.child("nombre").setValue(terrenoName);
            userTerrenoRef.child("area").setValue(SphericalUtil.computeArea(points));
            userTerrenoRef.child("numeroMarcadores").setValue(points.size());

            DatabaseReference marcadoresRef = userTerrenoRef.child("marcadores");
            for (int i = 0; i < points.size(); i++) {
                LatLng point = points.get(i);
                DatabaseReference marcadorRef = marcadoresRef.child("marcador" + (i + 1));
                marcadorRef.child("latitud").setValue(point.latitude);
                marcadorRef.child("longitud").setValue(point.longitude);
            }

            Toast.makeText(LocationScreen.this, "Terreno guardado exitosamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LocationScreen.this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        // Cambiar el tipo de mapa a Earth (satélite)
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        // Habilitar la ubicación del usuario y mostrar un botón para centrar el mapa en su ubicación
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        // Agregar un botón para dejar un marcador en la ubicación actual del usuario
        Button addMarkerButton = findViewById(R.id.addMarkerBtn);
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMap.getMyLocation() != null) {
                    LatLng currentLocation = new LatLng(mMap.getMyLocation().getLatitude(), mMap.getMyLocation().getLongitude());
                    points.add(currentLocation);
                    drawPolygon();
                    currentLocationMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .title("Mi Ubicación Actual")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))); // Marcador verde
                } else {
                    Toast.makeText(LocationScreen.this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void drawPolygon() {
        mMap.clear(); // Limpiar el mapa antes de volver a dibujar

        // Verificar si hay puntos para dibujar el polígono
        if (!points.isEmpty()) {
            PolygonOptions polygonOptions = new PolygonOptions();

            for (LatLng point : points) {
                mMap.addMarker(new MarkerOptions().position(point));
                polygonOptions.add(point);
            }

            if (currentLocationMarker != null) {
                LatLng currentLocation = currentLocationMarker.getPosition();
                mMap.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("Mi Ubicación Actual")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                polygonOptions.add(currentLocation);
            }

            TextView squareFeetTextView = findViewById(R.id.squareFeet);
            double area = SphericalUtil.computeArea(polygonOptions.getPoints());
            long roundedArea = Math.round(area);
            squareFeetTextView.setText("Área: " + roundedArea + " m²");

            polygonOptions.fillColor(getResources().getColor(R.color.purple_700));
            mMap.addPolygon(polygonOptions);
        }
    }
    private void deleteLastMarker() {
        if (!points.isEmpty()) {
            points.remove(points.size() - 1);
            drawPolygon(); // Volver a dibujar el polígono después de eliminar el marcador
        } else {
            Toast.makeText(this, "No hay marcadores para eliminar", Toast.LENGTH_SHORT).show();
        }

        // Eliminar el marcador verde (ubicación actual) del mapa si existe
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
            currentLocationMarker = null; // Liberar la referencia al marcador
        }
    }


    private void goToCurrentLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationProviderClient.requestLocationUpdates(createLocationRequest(), locationCallback, null);
    }

    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return locationRequest;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Agregar el nuevo punto a la lista de puntos
        points.add(latLng);

        // Dibujar el polígono con los puntos actualizados
        drawPolygon();

        // Agregar un marcador en la ubicación del clic
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Nuevo Marcador"));
    }

}
