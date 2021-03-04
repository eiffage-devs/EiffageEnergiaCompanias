package com.eiffage.companias.companias.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.companias.DB.MySqliteOpenHelper;
import com.eiffage.companias.companias.Global.APIHelper;
import com.eiffage.companias.companias.Global.Global;
import com.eiffage.companias.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DelegarTarea extends AppCompatActivity {

    private APIHelper apiHelper;

    EditText campoBusqueda;
    Button buscar, delegar;
    TextView nombre,email,codRecurso,empresa;

    ProgressDialog progressDialog;
    String token, idTarea, cod_pedido, cod_recurso;

    boolean isDelegable = false;

    //
    //      Método para usar flecha de atrás en Action Bar
    //
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delegar_tarea);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        apiHelper = new APIHelper();

        campoBusqueda = findViewById(R.id.campoBusqueda);
        buscar = findViewById(R.id.btnbuscar);
        delegar = findViewById(R.id.btnDelegar);

        nombre = findViewById(R.id.txtNombre);
        email = findViewById(R.id.txtEmail);
        codRecurso = findViewById(R.id.txtCodRecurso);
        empresa = findViewById(R.id.txtEmpresa);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDelegable = true;
                buscar();
            }
        });

        delegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delegar();
            }
        });

        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        token = myPrefs.getString("token", "Sin valor");

        Intent i = getIntent();
        idTarea = i.getStringExtra("idTarea");
        cod_pedido = i.getStringExtra("cod_pedido");
        Log.d("idTarea", idTarea);
        Log.d("cod_pedido", cod_pedido);
    }

    public void buscar(){
        if(campoBusqueda.getText().toString().equals("")){
            campoBusqueda.setError("Este campo es necesario");
        }
        else {
            final String campo = campoBusqueda.getText().toString();

            progressDialog = new ProgressDialog(DelegarTarea.this);
            progressDialog.setMessage("Espere, por favor"); // Setting Message
            progressDialog.setTitle("Buscando datos de usuario..."); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);
            progressDialog.show();
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.GET, getResources().getString(R.string.urlBase) + getResources().getString(R.string.urlGetDatosUsuario) + campo,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {

                                Log.d("response", response);
                                JSONObject job = new JSONObject(response);
                                String content=job.getString("content");
                                String error=job.getString("error");

                                if(error.equals("false")){
                                    if(content.equals("null")){
                                        progressDialog.dismiss();
                                        Toast.makeText(DelegarTarea.this, "No hay datos de este usuario", Toast.LENGTH_SHORT).show();
                                        //mensajeAlert("Ya tienes toda la información actualizada");
                                        //guardarActualizacion();
                                    }
                                    else {
                                        JSONObject jo = new JSONObject(content);
                                        nombre.setText("Nombre: " + jo.getString("nombre"));
                                        email.setText("Email: " + jo.getString("email"));
                                        codRecurso.setText("Cod. recurso: " + jo.getString("cod_recurso"));
                                        cod_recurso = jo.getString("cod_recurso");
                                        empresa.setText("Empresa: " + jo.getString("empresa"));
                                        progressDialog.dismiss();
                                    }
                                    Log.d("idTarea", idTarea);
                                    Log.d("cod_pedido", cod_pedido);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(DelegarTarea.this, "Ya tienes toda la información actualizada", Toast.LENGTH_SHORT).show();
                                //mensajeAlert("Ya tienes toda la información actualizada");
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(DelegarTarea.this, "Error al conectar. Por favor, revisa tu conexión e inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                    //mensajeAlert("Error al conectar. Por favor, revisa tu conexión e inténtalo de nuevo");
                    //actualizarTareas.setEnabled(true);
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
    }

    public void delegar(){
        Log.d("idTarea", idTarea);
        Log.d("cod_pedido", cod_pedido);
        if(!nombre.getText().toString().equals("Nombre:")){
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest sr = new StringRequest(Request.Method.POST, apiHelper.URL_TRASPASAR_TAREA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response", response);
                            JSONObject job = null;
                            try {
                                job = new JSONObject(response);
                                String content=job.getString("content");
                                Toast.makeText(DelegarTarea.this, content, Toast.LENGTH_SHORT).show();
                                MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(DelegarTarea.this);
                                SQLiteDatabase db = mySqliteOpenHelper.getWritableDatabase();
                                mySqliteOpenHelper.borrarTarea(db, idTarea);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(DelegarTarea.this, "Error al conectar. Por favor, revisa tu conexión e inténtalo de nuevo", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    return params;
                }


                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    Log.d("Param idTarea", idTarea);
                    Log.d("Param cod_pedido", cod_pedido);
                    params.put("usuarioApp", apiHelper.usuarioApp);
                    params.put("passwordApp", apiHelper.passwordApp);
                    params.put("empresa", new Global(DelegarTarea.this).myPrefs.getString("empresa", "-"));
                    params.put("recurso", cod_recurso);
                    params.put("numTarea", idTarea);
                    params.put("pedido", cod_pedido);
                    Log.d("Params traspaso", params.toString());
                    return params;
                }
            };
            queue.add(sr);
        }
        else {
            Toast.makeText(this, "¿Has elegido a quién traspasar la tarea?", Toast.LENGTH_SHORT).show();
        }
    }
}
