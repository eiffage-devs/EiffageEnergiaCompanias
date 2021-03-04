package com.eiffage.companias.companias.Activities.ModificarDatosFoto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eiffage.companias.companias.DB.MySqliteOpenHelper;
import com.eiffage.companias.companias.Objetos.Foto;

public class ModificarDatosFotoModel {

    private Context context;
    private ModificarDatosFotoPresenter presenter;

    private MySqliteOpenHelper mySqliteOpenHelper;
    private SQLiteDatabase db;


    public ModificarDatosFotoModel(Context context, ModificarDatosFotoPresenter presenter){
        this.context = context;
        this.presenter = presenter;

        mySqliteOpenHelper = new MySqliteOpenHelper(context);
        db = mySqliteOpenHelper.getWritableDatabase();
    }

    public Foto getFoto(String idFoto){
        Foto foto = mySqliteOpenHelper.getFoto(db, idFoto);
        Log.d("IDRECUPERADO", foto.getId());
        return foto;
    }
}
