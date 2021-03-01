package com.eiffage.companias.companias;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.eiffage.companias.companias.Adapters.Usuario;

public class CerrarSesion extends AppCompatActivity {

    Usuario miUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cerrar_sesion);

        Intent intent = getIntent();
        miUsuario = intent.getParcelableExtra("miUsuario");
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
}
