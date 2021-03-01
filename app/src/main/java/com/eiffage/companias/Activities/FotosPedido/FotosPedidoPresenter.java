package com.eiffage.companias.Activities.FotosPedido;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.eiffage.companias.Activities.FotoPantallaCompleta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FotosPedidoPresenter {

    private Context context;
    private FotosPedidoView view;
    private FotosPedidoModel model;

    private String codPedido;
    private ArrayList<String> rutasLocales;

    private int numFotos;

    public FotosPedidoPresenter(Context context, FotosPedidoView view){
        this.context = context;
        this.view = view;

        this.model = new FotosPedidoModel(context, this);
    }

    public void getIntent(Intent intent){
        this.codPedido = intent.getStringExtra("pedido");
    }

    public void getRutasFotos(){
        model.getRutasFotos(codPedido);
    }

    public void setRutasFotos(String response){
        rutasLocales = new ArrayList<>();
        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray array = jsonObject.getJSONArray("content");
            numFotos = array.length();
            if(numFotos == 0)
                view.setTextAnimacion(0, 0);
            for(int i=0; i<array.length(); i++){
                model.descargarFoto(array.getJSONObject(i).getString("Rutacompleta"), codPedido + i + ".jpeg");
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void añadirFoto(String ruta){
        rutasLocales.add(ruta);
        view.setTextAnimacion(rutasLocales.size(), numFotos);
        if(rutasLocales.size() == numFotos){
            view.setAdapter(rutasLocales);
        }
    }

    public void borrarFotosPedido(){
        for(int i=0;i<rutasLocales.size();i++){
            File file = new File(context.getExternalFilesDir(null) + rutasLocales.get(i));
            try {
                Log.d("Se borra", "Pues " + file.getCanonicalFile().delete());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void abrirPantallaCompleta(int pos){

    }
}
