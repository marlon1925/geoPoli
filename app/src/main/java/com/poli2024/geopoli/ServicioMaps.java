package com.poli2024.geopoli;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDateTime;

public class ServicioMaps extends Service {

    private static final String TAG = "ServicioMaps";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "onLocationResult: " + location.getLatitude() + "," + location.getLongitude());
                        Toast.makeText(getApplicationContext(), "location"+location.getLatitude()+":"+location.getLongitude(), Toast.LENGTH_SHORT).show();
                        saveLocationUser(location);
                    }
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos no están garantizados, iniciar la actividad de permission
            Intent permissionIntent = new Intent(this, PermissionActivity.class);
            permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissionIntent);
        } else {
            startLocationUpdates();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000); // 5 seconds
        request.setFastestInterval(2000); // 2 seconds
        request.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        request.setSmallestDisplacement(1); // 10 meters

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Si los permisos no están garantizados, iniciar la actividad de permission
            Intent permissionIntent = new Intent(this, PermissionActivity.class);
            permissionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissionIntent);
        } else {
            fusedLocationProviderClient.requestLocationUpdates(request, mLocationCallback, Looper.getMainLooper());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveLocationUser(Location location) {
        Toast.makeText(getApplicationContext(), "location"+location.getLatitude()+":"+location.getLongitude(), Toast.LENGTH_SHORT).show();
        if (location != null) {
            LocationUser lu = new LocationUser(Params.UserFirebaseId,
                    Double.toString(location.getLatitude()), Double.toString(location.getLongitude()),
                    LocalDateTime.now().toString());
            databaseReference.child("locations").child(Params.UserFirebaseId).setValue(lu);
        }
    }
}