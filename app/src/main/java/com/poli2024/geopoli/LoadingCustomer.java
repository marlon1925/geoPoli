package com.poli2024.geopoli;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;

public class LoadingCustomer {
    private Activity activity;
    private Dialog dialog;
    public LoadingCustomer(Activity activity) {
        this.activity=activity;
    }
    public void sartLoading(){
        AlertDialog.Builder builder= new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater=activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.custom_layout,null));
        builder.setCancelable(true);

        dialog = builder.create();
        dialog.show();
    }
    public void endLoading(){

        dialog.dismiss();
    }
}
