package com.eiffage.companias.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
import com.eiffage.companias.Objetos.Usuario;
import com.eiffage.companias.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CrearAveria extends AppCompatActivity {

    String fechaYHora = "-";
    EditText txtPodac, txtJefeObra, txtGestor, txtLocalidad, txtDescripcion;
    String podac, jefeObra, gestor, localidad, descripcion;

    String token, cod_recurso;
    ProgressDialog progressDialog;

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
        setContentView(R.layout.activity_crear_averia);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtPodac = findViewById(R.id.txtPodac);
        txtJefeObra = findViewById(R.id.txtJefeObra);
        txtGestor = findViewById(R.id.txtGestor);
        txtLocalidad = findViewById(R.id.txtLocalidad);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        try {
            Intent i = getIntent();
            Usuario miUsuario = i.getParcelableExtra("miUsuario");
            token = miUsuario.getToken();
            cod_recurso = miUsuario.getCod_recurso();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public void generarAveria(View v){
        if(comprobarCampos()){
            calcularFecha();
            enviarNuevaAveria();
        }
    }

    public void calcularFecha(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CET"));
        fechaYHora = simpleDateFormat.format(new Date());
    }

    public void enviarNuevaAveria(){

        //Campos a enviar: Podac, jefe obra, gestor, localidad, descripcion

        progressDialog = new ProgressDialog(CrearAveria.this);
        progressDialog.setMessage("Espere, por favor"); // Setting Message
        progressDialog.setTitle("Creando nueva avería..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        progressDialog.show();



        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.urlBase) + getResources().getString(R.string.urlCrearAveria),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                        progressDialog.dismiss();

                        JSONObject job= null;
                        try {
                            job = new JSONObject(response);
                            String content=job.getString("content");
                            mostrarMensaje("Crear nueva avería", content);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(" ERROR RESPONSE", error.toString());
                progressDialog.dismiss();
                mostrarMensaje("Ha ocurrido un error", error.toString());
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);

                return params;
            }
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("descripcion" ,txtPodac.getText().toString());
                params.put("jefeobra" , txtJefeObra.getText().toString());
                params.put("gestor" ,txtGestor.getText().toString());
                params.put("observaciones" ,txtDescripcion.getText().toString());
                params.put("localidad", txtLocalidad.getText().toString());
                Log.d("Params averia", params.toString());
                return params;
            }
        };
        stringRequest.setTag("ENVIO_AVERIA");
        stringRequest.setRetryPolicy((new DefaultRetryPolicy(60 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));

        queue.add(stringRequest);
    }

    public boolean comprobarCampos(){
        podac = txtPodac.getText().toString();
        jefeObra = txtJefeObra.getText().toString();
        gestor = txtGestor.getText().toString();
        localidad = txtLocalidad.getText().toString();
        descripcion = txtDescripcion.getText().toString();

        if(podac.equals("")){
            txtPodac.requestFocus();
            txtPodac.setError("Este campo es necesario");
            return false;
        }
        else if(jefeObra.equals("")){
            txtJefeObra.requestFocus();
            txtJefeObra.setError("Este campo es necesario");
            return false;
        }
        else if(gestor.equals("")){
            txtGestor.requestFocus();
            txtGestor.setError("Este campo es necesario");
            return false;
        }
        else if(localidad.equals("")){
            txtLocalidad.requestFocus();
            txtLocalidad.setError("Este campo es necesario");
            return false;
        }
        else if(localidad.length() > 99){
            txtLocalidad.requestFocus();
            txtLocalidad.setError("No introduzcas más de 100 caracteres");
            return false;
        }
        else if(descripcion.equals("")){
            txtDescripcion.requestFocus();
            txtDescripcion.setError("Este campo es necesario");
            return false;
        }
        return true;
    }

    public void mostrarMensaje(String title, String message) {
        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
        alertdialogobuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        if (!CrearAveria.this.isFinishing()) {
            try{
                alertdialogobuilder.show();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
