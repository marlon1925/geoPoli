package com.poli2024.geopoli;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorDatos  extends RecyclerView.Adapter<AdaptadorDatos.ViewHolderDafos>{
    private ArrayList<Users> datos;
    private Context myCotext;

    public AdaptadorDatos(ArrayList<Users> datos, Context myCotext) {
        this.datos = datos;
        this.myCotext=myCotext;
    }
    @NonNull
    @Override
    public AdaptadorDatos.ViewHolderDafos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario,null,false);
        return new ViewHolderDafos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDatos.ViewHolderDafos holder, int position) {
        holder.asigarDatos(datos.get(position));

    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class ViewHolderDafos extends RecyclerView.ViewHolder {
        TextView name ;
        TextView lastName ;
        TextView email ;
        TextView latitud ;
        TextView longitud ;
        Button changePage;
        public ViewHolderDafos(@NonNull View itemView) {
            super(itemView);
            name =itemView.findViewById(R.id.itemName);
            lastName=itemView.findViewById(R.id.itemLastName);
            email =itemView.findViewById(R.id.itemEmail);
            changePage=itemView.findViewById(R.id.changeNext);

        }

        public void asigarDatos(Users users) {
            name.setText(users.getCiclista().getName());
            lastName.setText(users.getCiclista().getLastName());
            email.setText(users.getCiclista().getEmail());

            // Agregar funcionalidad al botón para cambiar de página
            changePage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String latitude = users.getCiclista().getLatitude();
                    String longitude = users.getCiclista().getLongitude();

                    if (latitude != null && longitude != null) {
                        // Construir la URL de Google Maps con las coordenadas de latitud y longitud
                        String urlMapaGo = "https://www.google.com.ec/maps/@" + latitude + "," + longitude + ",15z";

                        // Crear un Intent para abrir Google Maps con la URL
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlMapaGo));

                        // Especificar que se debe abrir en la aplicación de Google Maps si está disponible
                        intent.setPackage("com.google.android.apps.maps");

                        // Iniciar la actividad
                        myCotext.startActivity(intent);
                    } else {
                        // Mostrar un Toast indicando que el usuario no ha compartido su ubicación
                        Toast.makeText(myCotext, "El usuario no ha compartido su ubicación", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}