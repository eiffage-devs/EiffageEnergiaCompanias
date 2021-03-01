package com.eiffage.companias.companias;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.companias.Adapters.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Menu extends AppCompatActivity {

    TextView txtusuario, txtempresa;
    Usuario miUsuario;
    static String urlCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        miUsuario = intent.getParcelableExtra("miUsuario");

        txtusuario = findViewById(R.id.txtusuariomenu);
        txtempresa = findViewById(R.id.txtempresamenu);

        txtusuario.setText(miUsuario.getEmail());
        txtempresa.setText(miUsuario.getEmpresa());

        urlCheck = getResources().getString(R.string.urlBase) + getResources().getString(R.string.urlCheck);
        comprobarDatos();
    }

    //----------Lógica de los botones de la activity----------\\

    public void datosUsuario(View v){
        Intent intent = new Intent(this, CerrarSesion.class);
        intent.putExtra("miUsuario", miUsuario);
        startActivity(intent);
    }

    public void misTareas(View v){
        Intent intent = new Intent(this, MisTareas.class);
        intent.putExtra("miUsuario", miUsuario);
        startActivity(intent);
    }

    public void misPedidos(View v){
        Intent intent = new Intent(this, MisPedidos.class);
        intent.putExtra("miUsuario", miUsuario);
        startActivity(intent);
    }

    //----------Comprobación de seguridad de la activity----------\\

    public void comprobarDatos(){
        final String token = miUsuario.getToken();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, urlCheck,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject job=new JSONObject(response);
                            String email=job.getString("email");
                            String empresa=job.getString("empresa");
                            String nombre=job.getString("nombre");
                            String delegacion=job.getString("delegacion");
                            String cod_recurso=job.getString("cod_recurso");
                            miUsuario = new Usuario(token, email, empresa, nombre, delegacion, cod_recurso);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Intent intent = new Intent(Menu.this, Login.class);
                startActivity(intent);
                finish();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + token);

                return params;
            }
        };
        queue.add(sr);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String tokenGuardado = myPrefs.getString("token", "Sin valor");
        if(tokenGuardado.equals("Sin valor")){

            Intent intent = new Intent(Menu.this, Login.class);
            startActivity(intent);
            finish();
        }
        else {
            comprobarDatos();
        }
    }
}
