package com.eiffage.companias.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.DocumentacionGeneral;
import com.eiffage.companias.DocumentacionGeneralV2;
import com.eiffage.companias.Objetos.Usuario;
import com.eiffage.companias.R;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Menu extends AppCompatActivity {

    private String URL_DATOS_USUARIO = "-";
    private String URL_ULTIMA_VERSION = "-";

    private MaterialCardView cardDatosUsuario, cardMisTareas, cardMisPedidos, cardMisAverias;

    TextView txtusuario, txtempresa;
    Usuario miUsuario;
    static String token;

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.docGeneral:
                documentacionGeneral();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViews();

        URL_DATOS_USUARIO = getResources().getString(R.string.urlBase) + getResources().getString(R.string.urlCheck);
        URL_ULTIMA_VERSION = getResources().getString(R.string.urlUltimaVersion);

        Intent intent = getIntent();
        try{
            miUsuario = intent.getParcelableExtra("miUsuario");

            txtusuario.setText(miUsuario.getEmail());
            txtempresa.setText(miUsuario.getEmpresa());
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        comprobarDatos();
    }

    public void findViews(){
        cardDatosUsuario = findViewById(R.id.cardDatosUsuario);
        cardMisTareas = findViewById(R.id.cardMisTareas);
        cardMisPedidos = findViewById(R.id.cardMisPedidos);
        cardMisAverias = findViewById(R.id.cardMisAverias);

        txtusuario = findViewById(R.id.txtusuariomenu);
        txtempresa = findViewById(R.id.txtempresamenu);

        setListeners();
    }

    public void setListeners(){
        cardDatosUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datosUsuario(view);
            }
        });
        cardMisTareas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                misTareas(view);
            }
        });
        cardMisPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                misPedidos(view);
            }
        });
        cardMisAverias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                misAverias(view);
            }
        });
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
        intent.putExtra("filtradoPedido", "todo");
        startActivity(intent);
    }

    public void misPedidos(View v){
        Intent intent = new Intent(this, MisPedidos.class);
        intent.putExtra("miUsuario", miUsuario);
        startActivity(intent);
    }

    public void misAverias(View v){
        Intent intent = new Intent(this, MisAverias.class);
        intent.putExtra("miUsuario", miUsuario);
        startActivity(intent);
    }

    //----------Comprobación de seguridad de la activity----------\\

    public void comprobarDatos(){
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        token = myPrefs.getString("token", "Sin valor");

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, URL_DATOS_USUARIO,
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

                            txtusuario.setText(email);
                            txtempresa.setText(empresa);

                            //Comprobar actualizaciones
                            try{
                                PackageInfo packageInfo = Menu.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                                int buildVersion = packageInfo.versionCode;
                                ultimaVersion("" + buildVersion);
                                Log.d("Version code", buildVersion + "");
                            }catch (PackageManager.NameNotFoundException e){
                                e.printStackTrace();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e){
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(Menu.this, "Estás trabajando sin conexión", Toast.LENGTH_SHORT).show();
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

    public void documentacionGeneral(){
        //Intent i = new Intent(Menu.this, DocumentacionGeneral.class);
        Intent i = new Intent(Menu.this, DocumentacionGeneralV2.class);
        startActivity(i);
    }

    public void ultimaVersion(final String local){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ULTIMA_VERSION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jo = new JSONObject(response);
                            String ultima = jo.getString("content");

                            if(!ultima.equals(local)){
                                final AlertDialog.Builder builder = new AlertDialog.Builder(Menu.this);
                                builder.setTitle("Actualización disponible")
                                        .setMessage("Hay una nueva versión disponible de la aplicación.\n\t¿Quieres actualizarla ahora?")
                                        .setCancelable(false)
                                        .setNegativeButton("En otro momento", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        . setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.eiffage.companias"); // missing 'http://' will cause crashed
                                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                startActivity(intent);
                                            }
                                        });
                                builder.show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error VERSION APP", error.toString());

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + token);

                return params;
            }
        };
        queue.add(stringRequest);
        stringRequest.setRetryPolicy((new DefaultRetryPolicy(10 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
    }
}
