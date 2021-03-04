package com.eiffage.companias.companias.Activities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.eiffage.companias.companias.Objetos.Usuario;
import com.eiffage.companias.R;

public class CerrarSesion extends AppCompatActivity {

    Usuario miUsuario;
    TextView email, nombre, empresa, delegacion, cod_recurso;

    //
    //      Método para usar flecha de atrás en Action Bar
    //
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar_sesion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(!esTablet(CerrarSesion.this)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        try{
            Intent intent = getIntent();
            miUsuario = intent.getParcelableExtra("miUsuario");

            email = findViewById(R.id.txtemail);
            nombre = findViewById(R.id.txtusuario);
            empresa = findViewById(R.id.txtempresa);
            delegacion = findViewById(R.id.txtdelegacion);
            cod_recurso = findViewById(R.id.txtcod_usuario);

            email.setText(miUsuario.getEmail());
            nombre.setText(miUsuario.getNombre());
            empresa.setText(miUsuario.getEmpresa());
            delegacion.setText(miUsuario.getDelegacion());
            cod_recurso.setText(miUsuario.getCod_recurso());
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void cerrarSesion(View view){
        SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
        editor.putString("token", "Sin valor");
        editor.apply();
        miUsuario = null;
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    public static boolean esTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
