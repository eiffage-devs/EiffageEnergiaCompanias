package com.eiffage.companias.companias.Global;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import androidx.appcompat.app.AlertDialog;

public class Global {

    private Context context;
    public SharedPreferences myPrefs;

    public Global(Context context){
        this.context = context;
        myPrefs = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
    }

    public AlertDialog muestraAlert(String cabecera, String mensaje){
        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(context);
        alertdialogobuilder
                .setTitle(cabecera)
                .setMessage(mensaje)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        return alertdialogobuilder.create();
    }

    public AlertDialog muestraLoader(String mensaje){
        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(context);
        alertdialogobuilder
                .setMessage(mensaje)
                .setCancelable(false)
                .create();
        return alertdialogobuilder.create();
    }
}
