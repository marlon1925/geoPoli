package com.poli2024.geopoli;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdaptadorTerrenos extends RecyclerView.Adapter<AdaptadorTerrenos.ViewHolderDatos> {
    private ArrayList<Terreno> terrenos;
    private Context mContext;
    private OnDeleteClickListener onDeleteClickListener;

    public AdaptadorTerrenos(ArrayList<Terreno> terrenos, Context mContext) {
        this.terrenos = terrenos;
        this.mContext = mContext;
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public void setOnDeleteClickListener(OnDeleteClickListener listener) {
        onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public AdaptadorTerrenos.ViewHolderDatos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_terreno, parent, false);
        return new ViewHolderDatos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderDatos holder, int position) {
        Terreno terreno = terrenos.get(position);
        holder.nombre.setText(terreno.getNombre());
        holder.area.setText(String.valueOf(terreno.getAreaFormatted()));
        holder.marcadores.setText(String.valueOf(terreno.getNumeroMarcadores()));

        DatabaseReference terrenoRef = FirebaseDatabase.getInstance().getReference("terrenos").child(Params.UserFirebaseId).child(terreno.getId());

        holder.eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                terrenoRef.removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Eliminación exitosa
                                Log.d("AdaptadorTerrenos", "Terreno eliminado correctamente");
                                terrenos.remove(position); // Remove terrain from the local list
                                notifyItemRemoved(position); // Notify adapter about specific item removal
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error al intentar eliminar el terreno
                                Log.w("AdaptadorTerrenos", "Error al eliminar el terreno", e);
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return terrenos.size();
    }

    public class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView area;
        TextView marcadores;
        Button eliminarButton;

        public ViewHolderDatos(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.itemNameTe);
            area = itemView.findViewById(R.id.itemarea);
            marcadores = itemView.findViewById(R.id.itemMarcadores);
            eliminarButton = itemView.findViewById(R.id.eliminarTerrenoBtn);
        }
    }
}
