package com.example.geopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Permission;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class UsersScreen extends AppCompatActivity {
    private ArrayList<UserCiclista> listUsers = new ArrayList<UserCiclista>();
    private ArrayList<UserCiclista> listAllUsersComplete = new ArrayList<UserCiclista>();
    private FusedLocationProviderClient ubication;
    private RecyclerView recyclerView;
    FirebaseDatabase database;
    Button serices;
    DatabaseReference databaseReference;
    DatabaseReference myRef;
    Context thisConextext = this;
    Timer timer;
    Button signOut;
    Button goMapScreenUser;
    Button endServices;
    TextView nameUser;
    TextView lastNameUser;
    TextView emailUser;
    Ciclista userMainApp;
    Intent serviceMap;
    Button locationAlls;
    final LoadingCustomer loadingCustomer = new LoadingCustomer(UsersScreen.this);
    private Boolean isTouched = false;
    private int REQUEST_PERMISIONS1;
    private int REQUEST_PERMISIONS2;
    private int REQUEST_PERMISIONS3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_screen);

        Params.activityService = UsersScreen.this;
        Params.contextService = UsersScreen.this;
        recyclerView = findViewById(R.id.listaUsuarios);
        signOut = findViewById(R.id.signOut);
        goMapScreenUser = findViewById(R.id.changeNext);
        nameUser = findViewById(R.id.itemName);
        lastNameUser = findViewById(R.id.itemLastName);
        emailUser = findViewById(R.id.itemEmail);
        endServices = findViewById(R.id.endServices);
        locationAlls = findViewById(R.id.locationAlls);
        ubication = LocationServices.getFusedLocationProviderClient(this);

        endServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Servicio detenido", Toast.LENGTH_SHORT).show();

                stopService(new Intent(thisConextext, ServicioMaps.class));

                // MapsScreen
            }
        });
        locationAlls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUsersAllLocations();
                isTouched = true;
            }
        });

        timer = new Timer();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serices = findViewById(R.id.startServices);
        serices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermisos();

                // has();
            }
        });

        goMapScreenUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(UsersScreen.this, LocationScreen.class);
                Params.useerApp = userMainApp;
                intent.putExtra("userId", Params.useerApp.getId());
                startActivity(intent);
            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UsersScreen.this, LoginScreen.class);
                startActivity(intent);

            }
        });
        getUsersDataBase();
        // startTimer();

    }

    public void has() {
        Intent iniciar = new Intent(this, LocationScreen.class);
        startActivity(iniciar);

    }

    public void getUsersDataBase() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listUsers = new ArrayList<UserCiclista>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Ciclista cil = postSnapshot.getValue(Ciclista.class);
                        ArrayList<LocationUser> lu = new ArrayList<>();
                        UserCiclista uc = new UserCiclista(cil, lu);
                        if (cil.getId().equals(Params.UserFirebaseId)) {
                            userMainApp = cil;
                            System.out.println("NOMBRE: "+ cil.getName());
                            nameUser.setText(cil.getName());
                            lastNameUser.setText(cil.getLastName());
                            emailUser.setText(cil.getEmail());
                        } else {
                            listUsers.add(uc);
                        }

                        // Toast.makeText(getApplicationContext(), "hay datos en users"+cil.getId(),
                        // Toast.LENGTH_SHORT).show();

                    }
                    AdaptadorDatos adaptadorDatos = new AdaptadorDatos(listUsers, UsersScreen.this);
                    recyclerView.setAdapter(adaptadorDatos);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getLastLocation() {

        if (ContextCompat.checkSelfPermission(UsersScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            ubication = LocationServices.getFusedLocationProviderClient(UsersScreen.this);
            ubication.getLastLocation().addOnSuccessListener(UsersScreen.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    System.out.println("location +" + location);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        LocationUser lu = new LocationUser("s", Double.toString(location.getLatitude()),
                                Double.toString(location.getLongitude()), LocalDateTime.now().toString());
                    }
                    Toast.makeText(getApplicationContext(),
                                    "location" + location.getLatitude() + ":" + location.getLongitude(), Toast.LENGTH_SHORT)
                            .show();
                    // saveLocationUser(lu);
                }
            });
        }

    }

    public void getUsersAllLocations() {
        // Crear un usuario ciclista para el usuario principal con una lista vacía de ubicaciones
        UserCiclista userPrincipal = new UserCiclista(userMainApp, new ArrayList<LocationUser>());

        // Agregar el usuario principal a la lista de todos los usuarios
        listAllUsersComplete = listUsers;
        listAllUsersComplete.add(userPrincipal);
        System.out.println("MINEROS; "+ userMainApp);
        // Establecer el ID del usuario principal en Params
        Params.userId = userMainApp.getId();

        // Obtener las ubicaciones por usuario
        getLocationsByUser();
    }


    public void getLocationsByUser() {
        System.out.println("ENTRAAAAAAAAAAAAAA");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("locations").child(Params.UserFirebaseId);
        loadingCustomer.sartLoading();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String id = postSnapshot.child("id").getValue(String.class);
                        String latitude = postSnapshot.child("latitude").getValue(String.class);
                        String longitud = postSnapshot.child("longitud").getValue(String.class);
                        String date = postSnapshot.child("date").getValue(String.class);

                        LocationUser locationUser = new LocationUser(id, latitude, longitud, date);

                        int counter = 0;
                        int index = -1;
                        for (UserCiclista userCil : listAllUsersComplete) {
                            if (userCil.getCiclista().getId().equals(id)) {
                                index = counter;
                                break;
                            }
                            counter++;
                        }
                        if (index != -1) {
                            ArrayList<LocationUser> locationsUser = new ArrayList<>();
                            locationsUser.add(locationUser);
                            listAllUsersComplete.get(index).setLastLocations(locationsUser);
                        }
                    }
                    Params.userCiclistasAll = listAllUsersComplete;
                    changePageMapsAllUsers();
                }
                loadingCustomer.endLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de cancelación
            }
        });
    }

    // Método auxiliar para encontrar un usuario por su ID en la lista de todos los usuarios
    private UserCiclista findUserById(String userId) {
        for (UserCiclista userCiclista : listAllUsersComplete) {
            if (userCiclista.getCiclista().getId().equals(userId)) {
                return userCiclista;
            }
        }
        return null;
    }

    public void changePageMapsAllUsers() {
        if (isTouched) {
            Intent intent = new Intent(UsersScreen.this, MapsScreen.class);
            intent.putExtra("type", "ALL");
            startActivity(intent);
            isTouched = false;
        }
    }

    public void checkPermisos() {
        int permiso1 = ContextCompat.checkSelfPermission(UsersScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permiso2 = ContextCompat.checkSelfPermission(UsersScreen.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permiso3 = ContextCompat.checkSelfPermission(UsersScreen.this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        System.out.println("permiso 1" + permiso1);
        System.out.println("permiso 2" + permiso2);
        System.out.println("permiso 3" + permiso3);
        if (permiso1 == PackageManager.PERMISSION_GRANTED && permiso2 == PackageManager.PERMISSION_GRANTED
                && permiso3 == PackageManager.PERMISSION_GRANTED) {
            System.out.println("tiene todo los permisos");
            startService(new Intent(thisConextext, ServicioMaps.class));
            // getLastLocation();
            Toast.makeText(getApplicationContext(), "Permisos concedidos", Toast.LENGTH_SHORT).show();

        } else if (permiso1 != PackageManager.PERMISSION_GRANTED) {
            System.out.println("no tiene todo los permisos");
            Toast.makeText(getApplicationContext(), "Sin permisos", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION,

            }, REQUEST_PERMISIONS1);
        } else if (permiso2 != PackageManager.PERMISSION_GRANTED) {
            System.out.println("no tiene todo los permisos");
            Toast.makeText(getApplicationContext(), "Sin permisos", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION,

            }, REQUEST_PERMISIONS2);
        } else if (permiso3 != PackageManager.PERMISSION_GRANTED) {
            System.out.println("no tiene todo los permisos");
            Toast.makeText(getApplicationContext(), "Sin permisos", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            }, REQUEST_PERMISIONS3);
        }

    }
}