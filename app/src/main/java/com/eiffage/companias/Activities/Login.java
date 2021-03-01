package com.eiffage.companias.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.Objetos.Usuario;
import com.eiffage.companias.R;
import com.google.android.material.button.MaterialButton;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    EditText user, pass;
    SharedPreferences myPrefs;
    MaterialButton login;
    private boolean validado = false;
    static String urlLogin;
    static String urlCheck;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        urlLogin = getResources().getString(R.string.urlBase) + getResources().getString(R.string.urlLogin);
        urlCheck = getResources().getString(R.string.urlBase) + getResources().getString(R.string.urlCheck);

        user = findViewById(R.id.loginEmail);
        pass = findViewById(R.id.loginPassword);
        login = findViewById(R.id.btnLogin);

        myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String tokenGuardado = myPrefs.getString("token", "Sin valor");
        if(tokenGuardado.equals("Sin valor")){

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        hacerLogin();
                    } catch (TimeoutError timeoutError) {
                        Toast.makeText(getApplicationContext(), "Fallo al conectar con el servidor.\nCompruebe su conexión y vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Espere, por favor"); // Setting Message
            progressDialog.setTitle("Iniciando sesión..."); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);
            progressDialog.show();
            recuperarUsuario(tokenGuardado);
        }
    }

    public void hacerLogin() throws TimeoutError {
        final String username = user.getText().toString();
        final String password = pass.getText().toString();

        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Espere, por favor"); // Setting Message
        progressDialog.setTitle("Iniciando sesión..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlLogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject job = new JSONObject(response);
                            String token = job.getString("token");
                            //Guardamos el token en Shared Preferences
                            SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
                            editor.putString("token", token);
                            editor.apply();
                            recuperarUsuario(token);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                falloDeLogin();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                params.put("username", username);
                params.put("password", password);

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void recuperarUsuario(final String token){
        final Usuario[] nuevoUsuario = new Usuario[1];
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
                            Log.d("DELEGACION", job.getString("delegacion"));
                            String cod_recurso=job.getString("cod_recurso");
                            SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
                            editor.putString("empresa", empresa);
                            editor.putString("recurso", cod_recurso);
                            editor.apply();

                            nuevoUsuario[0] = new Usuario(token, email, empresa, nombre, delegacion, cod_recurso);
                            progressDialog.dismiss();
                            //Enviar a la siguiente pantalla
                            Intent intent = new Intent(Login.this, Menu.class);
                            intent.putExtra("miUsuario", nuevoUsuario[0]);
                            startActivity(intent);
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                //falloDeDatos();
                //Enviar a la siguiente pantalla
                Intent intent = new Intent(Login.this, Menu.class);
                intent.putExtra("miUsuario", nuevoUsuario[0]);
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

    public void falloDeLogin(){

        final TextView message = new TextView(this);
        final SpannableString s = new SpannableString(" \n\t Usuario o contraseña incorrectos.\n\n\tSi es la primera vez que accede, debe activar su perfil \n\t accediendo a INET en el siguiente enlace");
        Pattern pattern = Pattern.compile("enlace");
        Linkify.addLinks(s, pattern , "http://inet.energia.eiffage.es?q=");
        message.setText(s);
        message.setMovementMethod(LinkMovementMethod.getInstance());

        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
        alertdialogobuilder
                .setTitle("Login")
                .setView(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        alertdialogobuilder.show();
    }

    public void falloDeDatos(){

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    hacerLogin();
                } catch (TimeoutError timeoutError) {
                    Toast.makeText(getApplicationContext(), "Fallo al conectar con el servidor.\nCompruebe su conexión y vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}