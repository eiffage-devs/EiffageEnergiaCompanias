package com.eiffage.companias.companias.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.companias.Adapters.MisAveriasAdapter;
import com.eiffage.companias.companias.Adapters.MisTareasAdapter;
import com.eiffage.companias.companias.DB.MySqliteOpenHelper;
import com.eiffage.companias.companias.Objetos.Averia;
import com.eiffage.companias.companias.Objetos.Tarea;
import com.eiffage.companias.companias.Objetos.Usuario;
import com.eiffage.companias.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MisAverias extends AppCompatActivity {

    private String URL_ACTUALIZAR_AVERIAS = "-";

    ListView listaAverias;
    ArrayList<Averia> misAverias;
    MisAveriasAdapter adapter;
    Usuario miUsuario;
    SQLiteDatabase myDataBase;
    MySqliteOpenHelper mySqliteOpenHelper;
    Button actualizarAverias;
    TextView txtNoResult;
    ProgressDialog progressDialog;
    TextView ultimaActualizacion;
    String cod_recurso;
    Menu menu_;

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
        setContentView(R.layout.activity_mis_averias);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        URL_ACTUALIZAR_AVERIAS = getResources().getString(R.string.urlListaAverias);

        cod_recurso = "";

        try{
            Intent i = getIntent();
            miUsuario = i.getParcelableExtra("miUsuario");
            cod_recurso = miUsuario.getCod_recurso();
        }
        catch (NullPointerException e){
            e.printStackTrace();
        }

        mySqliteOpenHelper = new MySqliteOpenHelper(this);
        myDataBase = mySqliteOpenHelper.getWritableDatabase();

        listaAverias = findViewById(R.id.listaAverias);
        txtNoResult = findViewById(R.id.notFoundAv);

        actualizarAverias();
        mostrarAveriasLocales();
        mostrarUltimaActualizacion();
    }


    public void actualizarAverias() {

        progressDialog = new ProgressDialog(MisAverias.this);
        progressDialog.setMessage("Espere, por favor"); // Setting Message
        progressDialog.setTitle("Actualizando averías..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, URL_ACTUALIZAR_AVERIAS + cod_recurso,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject job = new JSONObject(response);
                            Log.d("response", response);
                            String content = job.getString("content");
                            String error = job.getString("error");

                            if (error.equals("false")) {
                                if (content.equals("null")) {
                                    mostrarMensaje("Mis averías", "No se han recibido datos");
                                } else {
                                    //----------TODO: Recibir tareas y guardarlas en local----------\\
                                    Log.d("Respuesta averias", content);
                                    JSONArray averias = new JSONArray(content);
                                    guardarAveriasEnLocal(averias);
                                    mostrarAveriasLocales();
                                    progressDialog.dismiss();
                                    mostrarMensaje("Mis averías", "Las averías se han actualizado");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            mostrarMensaje("Avería en las averías", "Ha ocurrido un problema.");
                        }

                            Log.d("Respuesta", response);
                            progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                mensajeAlert("Error al conectar. Por favor, revisa tu conexión e inténtalo de nuevo");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                //params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + miUsuario.getToken());

                return params;
            }
        };
        queue.add(sr);
    }

    public void crearAveria(View v) {
        Intent i = new Intent(MisAverias.this, CrearAveria.class);
        i.putExtra("miUsuario", miUsuario);
        startActivity(i);
    }

    public void mostrarAveriasLocales() {
        misAverias = new ArrayList<>();
        Cursor c = mySqliteOpenHelper.getAveriasLocales(myDataBase, cod_recurso);
        if (c.getCount() > 0) {
            c.moveToFirst();
            do {
                String cod_averia = c.getString(0);
                String descripcion = c.getString(1);
                String gestor = c.getString(2);
                String fecha = c.getString(3);
                String observaciones = c.getString(4);
                String localidad = c.getString(5);

                Averia t = new Averia(cod_recurso, cod_averia, descripcion, gestor, fecha, observaciones, localidad);
                misAverias.add(t);

            } while (c.moveToNext());


            //----------Pintamos en el ListView las tareas proporcionadas por la base de datos----------\\
            adapter = new MisAveriasAdapter(getApplicationContext(), misAverias);
            listaAverias.setAdapter(adapter);
        } else {
            mostrarMensaje("Mis averías", "No tienes averías activas");
        }
    }

    public void guardarAveriasEnLocal(JSONArray response) {
        //----------Limpiar tabla----------\\
        mySqliteOpenHelper.borrarAverias(myDataBase, cod_recurso);

        //----------Inserción de averías obtenidas a través del API----------\\
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject nuevaAveria = response.getJSONObject(i);
                String cod_averia = nuevaAveria.getString("Pedido");
                String descripcion = nuevaAveria.getString("Desc");
                String gestor = nuevaAveria.getString("gestor");
                String fecha = nuevaAveria.getString("fechaaveria");
                String observaciones = nuevaAveria.getString("observaciones");
                String localidad = nuevaAveria.getString("localidad");

                Averia a = new Averia(cod_recurso, cod_averia, descripcion, gestor, fecha, observaciones, localidad);
                mySqliteOpenHelper.insertarAveria(myDataBase, a);
            }
            guardarActualizacion();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onRestart() {
        actualizarAverias();

        try {

            MenuItem searchHerram = menu_.findItem(R.id.searchTools);
            SearchView sW = (SearchView) searchHerram.getActionView();
            searchHerram.collapseActionView();
            sW.setQuery("",false);
            sW.clearFocus();

        } catch (Exception e){}

        super.onRestart();
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
        if (!MisAverias.this.isFinishing()) {
            alertdialogobuilder.show();
        }
    }

    public void guardarActualizacion(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        String tempHora = "" + calendar.get(Calendar.HOUR_OF_DAY);
        if (tempHora.length() == 1) {
            tempHora = "0" + tempHora;
        }
        String tempMin = "" + calendar.get(Calendar.MINUTE);
        if (tempMin.length() == 1) {
            tempMin = "0" + tempMin;
        }
        String tempSeg = "" + calendar.get(Calendar.SECOND);
        if (tempSeg.length() == 1) {
            tempSeg = "0" + tempSeg;
        }

        String hora = tempHora + ":" + tempMin + ":" + tempSeg;

        ultimaActualizacion  = findViewById(R.id.ultimaActualizacionAverias);
        ultimaActualizacion.setText("Última actualización: AHORA");
        ultimaActualizacion.setBackgroundColor(getResources().getColor(R.color.VerdeBootstrap));
        SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
        editor.putString("ultimaActualizacionAveriaFecha", fecha);
        editor.putString("ultimaActualizacionAveriaHora", hora);
        editor.apply();

    }

    public void mostrarUltimaActualizacion(){
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String f = myPrefs.getString("ultimaActualizacionAveriaFecha", "-");
        String s = myPrefs.getString("ultimaActualizacionAveriaHora", "-");
        ultimaActualizacion = findViewById(R.id.ultimaActualizacionAverias);
        ultimaActualizacion.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        if(!f.equals("-") && !s.equals("-")){
            ultimaActualizacion.setText("Última actualización: " + f + ", " + s);
        }

    }

    //----------Mostrar mensaje mediante alert en la Activity----------\\

    public void mensajeAlert(String message){
        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
        alertdialogobuilder
                .setTitle("Mis averías")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        if (!MisAverias.this.isFinishing()){
            alertdialogobuilder.show();
        }
    }
    //Añadimos los items del menu a traves del inflate con el action_bar_menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.action_bar_menu, menu);
        menu_=menu;

        MenuItem searchTools = menu.findItem(R.id.searchTools);
        searchTools.setTitle(R.string.buscarAveria);
        SearchView searchView = (SearchView) searchTools.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.buscarAveria)+"...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Averia> averiasFilter = new ArrayList<Averia>();
                for(Averia averia : misAverias){
                    String descripcion = averia.getDescripcion();
                    if (descripcion.toLowerCase().contains(newText.toLowerCase())) {
                        averiasFilter.add(averia);
                    }
                }
                if(!averiasFilter.isEmpty()){
                    adapter = new MisAveriasAdapter(getApplicationContext(), averiasFilter);
                    listaAverias.setAdapter(adapter);
                    listaAverias.setVisibility(View.VISIBLE);
                    txtNoResult.setVisibility(View.GONE);
                }else{
                    listaAverias.setVisibility(View.GONE);
                    txtNoResult.setVisibility(View.VISIBLE);

                }

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}

