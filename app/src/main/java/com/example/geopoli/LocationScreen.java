package com.example.geopoli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Timer;

public class LocationScreen extends AppCompatActivity {
    private ArrayList<LocationUser> listUsers=new ArrayList<LocationUser>();
    private FusedLocationProviderClient ubication;
    private RecyclerView recyclerView;
    FirebaseDatabase database ;
    Button serices;
    DatabaseReference myRef  ;
    DatabaseReference databaseReference;
    TextView nameUser;
    TextView lastNameUser;
    TextView emailUser;
    TextView usersinLocation;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_screen);
        recyclerView=findViewById(R.id.listaLocations);
        recyclerView.setHasFixedSize(true);
        nameUser=findViewById(R.id.itemName);
        lastNameUser=findViewById(R.id.itemLastName);
        emailUser=findViewById(R.id.itemEmail);
        nameUser.setText(Params.useerApp.getName());
        lastNameUser.setText(Params.useerApp.getLastName());
        emailUser.setText(Params.useerApp.getEmail());
        usersinLocation=findViewById(R.id.userSinocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userId = getIntent().getExtras().getString("userId");
        getUserLocations();
        usersinLocation.setVisibility(View.VISIBLE);
    }
    public void getUserLocations(){
        database=  FirebaseDatabase.getInstance();
        myRef= database.getReference("locations/"+userId);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists()){
                    usersinLocation.setVisibility(View.INVISIBLE);
                    listUsers=new ArrayList<LocationUser>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        LocationUser cil = postSnapshot.getValue(LocationUser.class);

                        listUsers.add(cil);
                       //   Toast.makeText(getApplicationContext(), "hay datos en LOCATIONS"+postSnapshot.getValue(), Toast.LENGTH_LONG).show();

                    }
                    AdaptadorDatosLocation adaptadorDatos = new AdaptadorDatosLocation(listUsers,LocationScreen.this);
                    recyclerView.setAdapter(adaptadorDatos);
                }

                   /* for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        LocationUser cil = postSnapshot.getValue(LocationUser.class);

                        listUsers.add(cil);
                        //  Toast.makeText(getApplicationContext(), "hay datos en users"+cil.getId(), Toast.LENGTH_SHORT).show();

                    }*/
                  /*  AdaptadorDatosLocation adaptadorDatos = new AdaptadorDatosLocation(listUsers);
                    recyclerView.setAdapter(adaptadorDatos);*/


            };

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}