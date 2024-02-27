package com.example.geopoli;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorDatos  extends RecyclerView.Adapter<AdaptadorDatos.ViewHolderDafos>{
    private ArrayList<UserCiclista> datos;
    private Context myCotext;

    public AdaptadorDatos(ArrayList<UserCiclista> datos, Context myCotext) {
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
        Button changePage;
        public ViewHolderDafos(@NonNull View itemView) {
            super(itemView);
             name =itemView.findViewById(R.id.itemName);
            lastName=itemView.findViewById(R.id.itemLastName);
            email =itemView.findViewById(R.id.itemEmail);
            changePage=itemView.findViewById(R.id.changeNext);

        }

        public void asigarDatos(UserCiclista userCiclista) {
            name.setText(userCiclista.getCiclista().getName());
             lastName.setText(userCiclista.getCiclista().getLastName());
             email.setText(userCiclista.getCiclista().getEmail());
            changePage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Params.useerApp=userCiclista.getCiclista();
                    Intent intent = new Intent(myCotext,LocationScreen.class);
                    intent.putExtra("userId",userCiclista.getCiclista().getId());
                    myCotext.startActivity(intent);
                }
            });
        }
    }

}
