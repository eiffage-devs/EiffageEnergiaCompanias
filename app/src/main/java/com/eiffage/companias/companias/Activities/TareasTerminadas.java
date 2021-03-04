package com.eiffage.companias.companias.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.eiffage.companias.companias.Adapters.TareasTerminadasAdapter;
import com.eiffage.companias.companias.Objetos.Pedido;
import com.eiffage.companias.companias.Objetos.Tarea;
import com.eiffage.companias.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TareasTerminadas extends AppCompatActivity {

    ArrayList<Tarea> tareasTerminadas;
    ArrayList<Pedido> pedidosTareas;

    ListView listaTareasTerminadas;

    TareasTerminadasAdapter adapter;


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
        setContentView(R.layout.activity_tareas_terminadas);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listaTareasTerminadas = findViewById(R.id.listaTareasTerminadas);

        tareasTerminadas = new ArrayList<>();
        pedidosTareas = new ArrayList<>();

        Intent intent = getIntent();
        String content = intent.getStringExtra("tareas");

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray tareas = jsonObject.getJSONArray("tareas");

            for (int i = 0; i < tareas.length(); i++) {
                JSONObject nuevaTarea = tareas.getJSONObject(i);
                String cod_pedido = nuevaTarea.getString("Pedido");
                String descripcion = nuevaTarea.getString("Desc");

                Tarea t = new Tarea("-", descripcion, "-", cod_pedido, "-", "-");
                tareasTerminadas.add(t);

                Log.d("Tarea " + i, t.getDescripcion());
            }

            JSONArray pedidos = jsonObject.getJSONArray("pedidos");

            for (int i = 0; i < pedidos.length(); i++) {
                JSONObject nuevoPedido = pedidos.getJSONObject(i);
                String cod_pedido = nuevoPedido.getString("Pedido");
                String descripcion = nuevoPedido.getString("Desc");

                Pedido p = new Pedido(cod_pedido, descripcion, "-", "-", "-", "-", "-");
                pedidosTareas.add(p);

                Log.d("Pedido " + i, p.getDescripcion());
            }

            adapter = new TareasTerminadasAdapter(getApplicationContext(), tareasTerminadas, pedidosTareas);
            listaTareasTerminadas.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
