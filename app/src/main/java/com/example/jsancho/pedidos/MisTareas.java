package com.eiffage.companias.companias;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.companias.Adapters.MisTareasAdapter;
import com.eiffage.companias.companias.Adapters.Tarea;
import com.eiffage.companias.companias.Adapters.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MisTareas extends AppCompatActivity {

    ListView listaTareas;
    ArrayList<Tarea> misTareas;
    MisTareasAdapter adapter;
    Usuario miUsuario;
    SQLiteDatabase myDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_tareas);
        misTareas = new ArrayList<>();

        //----------Recibir datos de usuario de la activity anterior----------\\

        Intent i = getIntent();
        miUsuario = i.getParcelableExtra("miUsuario");

        //----------CREAR O ABRIR LA BASE DE DATOS----------\\

        myDataBase = openOrCreateDatabase("Pedidos", MODE_PRIVATE, null);
        guardarTareasEnLocal();

        //----------CARGAR DATOS LOCALES----------\\
        listaTareas = findViewById(R.id.listaTareas);
        listaTareas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MisTareas.this, DetalleTarea.class);
                startActivity(intent);

            }
        });
        mostrarTareasLocales();

    }

    public void actualizarTareas(View v){
        //----------Petición a la API para recuperar las tareas activas del usuario----------\\

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, getResources().getString(R.string.urlBase) + getResources().getString(R.string.urlGetTareas),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject job=new JSONObject(response);

                            String content=job.getString("content");
                            String error=job.getString("error");

                            if(error.equals("false")){
                                if(content.equals("null")){
                                    mensajeAlert("Ya tienes toda la información actualizada");
                                }
                                else {
                                    //----------TODO: Recibir tareas y guardarlas en local----------\\
                                    mostrarTareasLocales();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mensajeAlert("Error al conectar. Por favor, revisa tu conexión e inténtalo de nuevo");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + miUsuario.getToken());

                return params;
            }
        };
        queue.add(sr);
    }

    //----------Mostrar mensaje mediante alert en la Activity----------\\

    public void mensajeAlert(String message){
        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
        alertdialogobuilder
                .setTitle("Mis tareas")
                .setMessage(message)
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

    //-----------------------------Guardado de tareas en local----------------------------\\
    //----------TODO: (PENDIENTE DE MODIFICAR CUANDO TRAIGAMOS LOS DATOS DESDE REMOTO)----------\\

    public void guardarTareasEnLocal(){

        //----------Limpiar tabla----------\\

        myDataBase.execSQL("DROP TABLE IF EXISTS Tareas");

        //----------CREAR TABLA TAREAS SI NO EXISTE----------\

        myDataBase.execSQL(
                "CREATE TABLE IF NOT EXISTS Tareas\n" +
                        "(\n" +
                        "id integer primary key autoincrement,\n" +
                        "pedido varchar(50) not null,\n" +
                        "desc_pedido varchar(500) not null, \n" +
                        "linea varchar(50) not null, \n" +
                        "cod_recurso int(10) not null, \n" +
                        "cargo_recurso varchar(150) not null, \n" +
                        "nom_recurso varchar(500) not null, \n" +
                        "estado_tarea varchar(150) not null, \n" +
                        "desc_tarea varchar(500) not null\n" +
                        ")");

        //----------Inserción de filas de prueba----------\\

        myDataBase.execSQL("INSERT INTO Tareas (pedido,desc_pedido,linea,cod_recurso,cargo_recurso,nom_recurso,estado_tarea,desc_tarea)\n" +
                "VALUES ('4504076447', 'V.64061 CAMBIO SECCIONADORES OBSOLETOS V.64061 ST2', '10000', '9190', 'Jefe de Obra', " +
                "'SANCHO GARCIA JUAN', 'PENDIENTE', 'Hacer fotos (BDD real)')");

        myDataBase.execSQL("INSERT INTO Tareas (pedido,desc_pedido,linea,cod_recurso,cargo_recurso,nom_recurso,estado_tarea,desc_tarea)\n" +
                "VALUES ('4504076447', 'V.64062 CAMBIO SECCIONADORES OBSOLETOS V.64062 ST2', '20000', '9190', 'Encargado', " +
                "'SANCHO GARCIA JUAN', 'PENDIENTE', 'Revisar documentación (BDD real)')");

        myDataBase.execSQL("INSERT INTO Tareas (pedido,desc_pedido,linea,cod_recurso,cargo_recurso,nom_recurso,estado_tarea,desc_tarea)\n" +
                "VALUES ('4504076447', 'V.64063 CAMBIO SECCIONADORES OBSOLETOS V.64063 ST2', '30000', '5012', 'Jefe de Obra', " +
                "'ANTONIO VILA', 'PENDIENTE', 'Hacer fotos (BDD real)')");


    }

    //---------------------Mostrar las tareas almacenadas localmente----------------------\\
    //----------(ESTE MÉTODO SE LLAMA AL ABRIR LA ACTIVITY Y AL ACTUALIZAR DATOS----------\\

    public void mostrarTareasLocales(){

        String [] args = new String[]{miUsuario.getCod_recurso()};
        Cursor c = myDataBase.rawQuery("SELECT * FROM Tareas WHERE cod_recurso=?", args);

        if(c.moveToFirst()){
            do{
                String pedido = c.getString(1);
                String desc_pedido = c.getString(2);
                String desc_tarea = c.getString(8);

                Tarea t = new Tarea(pedido, desc_pedido, desc_tarea);
                misTareas.add(t);
            }while (c.moveToNext());

            //----------Pintamos en el ListView las tareas proporcionadas por la base de datos----------\\
            adapter = new MisTareasAdapter(getApplicationContext(), misTareas);
            listaTareas.setAdapter(adapter);

        }
    }
}
