package com.example.geopoli;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.telecom.Call;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Provider;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ServicioMaps extends Service {
    FirebaseDatabase database;
    private FusedLocationProviderClient ubication;
    Context thisConetext = this;
    Button serices;
    DatabaseReference databaseReference;
    DatabaseReference myRef;
    Timer timer;
    TimerTask timerTask;

    @Override
    public void onCreate() {
        timer = new Timer();
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public int onStartCommand(Intent intent, int flasg, int idProcess) {
        startTimer();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        timerTask.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void startTimer() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                getLastLocation();
                System.out.println("capturando...");
                // getCoordenada();

            }
        };
        timer.schedule(timerTask, 0, 6000);
    }

    public void getLastLocation() {
        System.out.println("capturando ubi");
        if (ContextCompat.checkSelfPermission(Params.contextService,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ubication = LocationServices.getFusedLocationProviderClient(Params.activityService);
            ubication.getLastLocation().addOnSuccessListener(Params.activityService, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LocationUser lu = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            lu = new LocationUser(Params.UserFirebaseId,
                                    Double.toString(location.getLatitude()), Double.toString(location.getLongitude()),
                                    LocalDateTime.now().toString());
                        }
                        Toast.makeText(getApplicationContext(),
                                "location" + location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_SHORT)
                                .show();
                        saveLocationUser(lu);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Enciende tu ubicación y si no funciona reinicia la APP, si el problema persiste abre la google maps y vuelve a abrir la appp",
                                Toast.LENGTH_SHORT).show();

                    }
                    System.out.println("location s" + location);

                }
            });
        }

    }

    public void saveLocationUser(LocationUser locationUser) {
        DatabaseReference myRef = database.getReference("locations/" + Params.UserFirebaseId);
        myRef.setValue(locationUser);
        // databaseReference.child("locations").child().setValue(locationUser);

    }
    /*
     * private void getCoordenada() {
     * 
     * try {
     * 
     * LocationRequest locationRequest = LocationRequest.create()
     * .setInterval(100)
     * .setFastestInterval(3000)
     * .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
     * .setMaxWaitTime(200);
     * 
     * 
     * if (ActivityCompat.checkSelfPermission(Params.contextService,
     * Manifest.permission.ACCESS_FINE_LOCATION) !=
     * PackageManager.PERMISSION_GRANTED) {
     * 
     * return;
     * }
     * // Toast.makeText(Params.contextService, "Servicio iniciado",
     * Toast.LENGTH_SHORT).show();
     * 
     * LocationServices.getFusedLocationProviderClient(Params.contextService).
     * requestLocationUpdates(locationRequest, new LocationCallback() {
     * 
     * @Override
     * public void onLocationResult(LocationResult locationResult) {
     * Toast.makeText(getApplicationContext(), "hay location",
     * Toast.LENGTH_SHORT).show();
     * 
     * super.onLocationResult(locationResult);
     * LocationServices.getFusedLocationProviderClient(Params.activityService).
     * removeLocationUpdates(this);
     * if (locationResult != null && locationResult.getLocations().size() > 0) {
     * int latestLocationIndex = locationResult.getLocations().size() - 1;
     * Location location = locationResult.getLocations().get(latestLocationIndex);
     * // double longitude =
     * locationResult.getLocations().get(latestLocationIndex).getLongitude();
     * LocationUser lu=new
     * LocationUser(Params.UserFirebaseId,Double.toString(location.getLatitude()),
     * Double.toString(location.getLongitude()), LocalDateTime.now().toString());
     * Toast.makeText(getApplicationContext(),
     * "location"+location.getLatitude()+":"+location.getLongitude(),
     * Toast.LENGTH_SHORT).show();
     * saveLocationUser(lu);
     * /* lat.setText(String.valueOf(latitud));
     * lon.setText(String.valueOf(longitude));
     * urlMapaGo=urlMapaGo+latitud+","+longitude+",15z";
     * }else{
     * Toast.makeText(getApplicationContext(), "error al obtener location",
     * Toast.LENGTH_SHORT).show();
     * 
     * }
     * }
     * 
     * }, Looper.myLooper());
     * 
     * }catch (Exception ex){
     * // Toast.makeText(Params.contextService, "Error al iniciar servicio",
     * Toast.LENGTH_SHORT).show();
     * 
     * System.out.println("Error es :" + ex);
     * }
     * 
     * }
     */
}
