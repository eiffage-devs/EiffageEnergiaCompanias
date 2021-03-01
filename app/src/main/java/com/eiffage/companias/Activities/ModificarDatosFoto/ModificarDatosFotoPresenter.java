package com.eiffage.companias.Activities.ModificarDatosFoto;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.eiffage.companias.Objetos.Foto;
import com.eiffage.companias.R;
import com.google.android.material.snackbar.Snackbar;

public class ModificarDatosFotoPresenter {

    private Context context;
    private ModificarDatosFotoView view;
    private ModificarDatosFotoModel model;

    public ModificarDatosFotoPresenter(Context context, ModificarDatosFotoView view){
        this.context = context;
        this.view = view;

        this.model = new ModificarDatosFotoModel(context, this);
    }

    public void getFoto(String idFoto){
        Log.d("IDFOTOPRESENTER", idFoto);
        try{
            Foto foto = model.getFoto(idFoto);
            view.setInfoFoto(foto);
        }
        catch (NullPointerException e){
            Toast.makeText(context, "No se pueden editar los datos de esta foto", Toast.LENGTH_SHORT).show();
            view.finish();
        }
    }

    public void precargarSpinners(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.categorias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        view.setAdapter("cat", adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        ArrayAdapter<CharSequence> adapter = null;
        if (pos == 0)
            adapter = ArrayAdapter.createFromResource(context, R.array.sub1, android.R.layout.simple_spinner_item);
        else if (pos == 1)
            adapter = ArrayAdapter.createFromResource(context, R.array.sub2, android.R.layout.simple_spinner_item);
        else if (pos == 2)
            adapter = ArrayAdapter.createFromResource(context, R.array.sub3, android.R.layout.simple_spinner_item);

        if(adapter!= null)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.view.setAdapter("sub", adapter);
    }

    public void onNothingSelected() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.sub1, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        view.setAdapter("sub", adapter);
    }
}
