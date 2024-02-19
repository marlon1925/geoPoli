package com.example.geopoli;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorDatosLocation extends RecyclerView.Adapter<AdaptadorDatosLocation.ViewHolderDatos>{
    private ArrayList<LocationUser> datos;
    private Context myContext;
    public AdaptadorDatosLocation(ArrayList<LocationUser> datos,Context myContext) {
        this.datos = datos;
        this.myContext=myContext;
    }

    @NonNull
    @Override
    public AdaptadorDatosLocation.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location,null,false);
        return new AdaptadorDatosLocation.ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorDatosLocation.ViewHolderDatos holder, int position) {
        holder.asigarDatos(datos.get(position),position);


    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView latitud ;
        TextView longitud ;
        TextView date ;
        TextView counter;
        Button buttonGoMaps;
        Button buttonMapsInside;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            latitud =itemView.findViewById(R.id.itemLatitud);
            longitud= itemView.findViewById(R.id.itemLongitud);
            date =itemView.findViewById(R.id.itemDate);
            counter=itemView.findViewById(R.id.counter);
            buttonGoMaps = itemView.findViewById(R.id.goGoogleMapsB);
            buttonMapsInside=itemView.findViewById(R.id.goGoogleMapsInside);

        }

        public void asigarDatos(LocationUser locationUser,int position) {
            latitud.setText(locationUser.getLatitude());
            longitud.setText(locationUser.getLongitud());
            date.setText(locationUser.getDate());
            counter.setText(Integer.toString(position+1));
            buttonGoMaps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String urlMapaGo="https://www.google.com.ec/maps/@"+locationUser.getLatitude()+","+locationUser.getLongitud()+",15z";
                    Uri _link = Uri.parse(urlMapaGo);
                    Intent intent = new Intent(Intent.ACTION_VIEW, _link);
                    myContext.startActivity(intent);


                }
            });
            buttonMapsInside.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(myContext,MapsScreen.class);
                    intent.putExtra("type","ONE");
                    intent.putExtra("latitued",locationUser.getLatitude());
                    intent.putExtra("longitude",locationUser.getLongitud());
                    myContext.startActivity(intent);

                }
            });
        }

    }
}
