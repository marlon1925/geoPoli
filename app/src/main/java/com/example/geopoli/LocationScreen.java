package com.example.geopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.maps.android.SphericalUtil;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.ArrayList;
import java.util.List;

public class LocationScreen extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private ArrayList<LatLng> points = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_screen);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("terrenos");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button deleteLastMarkerButton = findViewById(R.id.deleteLastMarkerBtn);
        Button goToCurrentLocationButton = findViewById(R.id.goToCurrentLocationBtn);
        Button btnSave = findViewById(R.id.btnSave);
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
            userTerrenoRef.child("nombre").setValue(terrenoName);
            userTerrenoRef.child("area").setValue(SphericalUtil.computeArea(points));

            // Guardar las posiciones de los marcadores
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
                    mMap.addMarker(new MarkerOptions()
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
        mMap.clear();
        PolygonOptions polygonOptions = new PolygonOptions();
        for (LatLng point : points) {
            mMap.addMarker(new MarkerOptions().position(point));
            polygonOptions.add(point);
        }
        TextView squareFeetTextView = findViewById(R.id.squareFeet);
        double area = SphericalUtil.computeArea(points);
        long roundedArea = Math.round(area); // Redondea el área al número entero más cercano

        squareFeetTextView.setText("Área: " + roundedArea + " m");
        polygonOptions.fillColor(getResources().getColor(R.color.purple_700));
        mMap.addPolygon(polygonOptions);
    }

    private void deleteLastMarker() {
        if (!points.isEmpty()) {
            points.remove(points.size() - 1);
            drawPolygon();
        } else {
            Toast.makeText(this, "No markers to delete", Toast.LENGTH_SHORT).show();
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
        points.add(latLng);
        drawPolygon();
    }
}
