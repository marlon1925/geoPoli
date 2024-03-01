package com.poli2024.geopoli;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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
    private static final String CHANNEL_ID = "location_service_channel";
    private static final int NOTIFICATION_ID = 1234;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private DatabaseReference databaseReference;

    @Override
    public void onCreate() {
        super.onCreate();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        }

        mLocationCallback = new LocationCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "onLocationResult: " + location.getLatitude() + "," + location.getLongitude());
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
            showForegroundNotification();
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fusedLocationProviderClient != null) {
            fusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
        }
        removeForegroundNotification();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(Params.activityService, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
    }


    private void removeForegroundNotification() {
        stopForeground(true);
    }

    private void showForegroundNotification() {
        // Create the notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // Create the notification intent
        Intent notificationIntent = new Intent(this, MainActivity.class); // Replace with your desired activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Servicio de ubicación activo")
                .setContentText("Capturando ubicación en segundo plano")
                .setSmallIcon(R.drawable.ic_location_on) // Replace with your icon resource
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        // Show the notification
        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                "Canal de servicio de ubicación",
                NotificationManager.IMPORTANCE_LOW);

        notificationChannel.setDescription("Canal para notificar que el servicio de ubicación se está ejecutando en segundo plano");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

    private void startLocationUpdates() {
        LocationRequest request = new LocationRequest();
        request.setInterval(5000); // 5 seconds
        request.setFastestInterval(2000); // 2 seconds
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setSmallestDisplacement(1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
