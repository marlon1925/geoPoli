package com.poli2024.geopoli;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TerrenosHistorial extends AppCompatActivity {
    private ArrayList<Terreno> listTerrenos = new ArrayList<>();
    private RecyclerView recyclerView;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terrenos_historial);

        recyclerView = findViewById(R.id.listaTerrenos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getTerrenos();
    }

    public void getTerrenos() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("terrenos/" + Params.UserFirebaseId);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    listTerrenos.clear();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Terreno terreno = postSnapshot.getValue(Terreno.class);
                        if (terreno != null) {
                            listTerrenos.add(terreno);
                        }
                    }

                    // Crear el adaptador y asignarlo al RecyclerView
                    AdaptadorTerrenos adaptadorTerrenos = new AdaptadorTerrenos(listTerrenos, TerrenosHistorial.this);
                    recyclerView.setAdapter(adaptadorTerrenos);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de cancelación
            }
        });
    }
}
