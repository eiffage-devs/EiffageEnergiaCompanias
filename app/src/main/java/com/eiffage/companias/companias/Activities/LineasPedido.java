package com.eiffage.companias.companias.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.companias.Adapters.LineaAdapter;
import com.eiffage.companias.companias.Objetos.Linea;
import com.eiffage.companias.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LineasPedido extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private String URL_LINEAS_PEDIDO = "-";

    ListView lineasPedido;
    String pedido;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.menuSearch);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Desc. de producto");
        searchView.setOnQueryTextListener(this);

        return true;
    }

    SharedPreferences myPrefs;
    LineaAdapter lineaAdapter;

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
        setContentView(R.layout.activity_lineas_pedido);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        URL_LINEAS_PEDIDO = getResources().getString(R.string.urlLineasPedido);

        lineasPedido = findViewById(R.id.listaLineasPedido);
        myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        final String tokenGuardado = myPrefs.getString("token", "Sin valor");

        Intent i = getIntent();
        final String cod_pedido = i.getStringExtra("cod_pedido");

        //----------Petición a la API para recuperar las tareas activas del usuario----------\\
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, URL_LINEAS_PEDIDO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", response);
                        ArrayList<Linea> misLineas = new ArrayList<>();
                        try {
                            JSONObject jo = new JSONObject(response);
                            JSONArray jsonArray = jo.getJSONArray("content");
                            for (int i =0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String empresa = jsonObject.getString("Empresa");
                                String numLinea = "" + jsonObject.getString("NumLinea");
                                String codProducto = jsonObject.getString("CodProducto");
                                String cant = "" + jsonObject.getString("Cant");
                                String descProducto = jsonObject.getString("DescProducto");
                                pedido = jsonObject.getString("Pedido");
                                String unMedida = jsonObject.getString("UnMedida");

                                Linea l = new Linea(empresa, numLinea, codProducto, cant, descProducto, pedido, unMedida);
                                misLineas.add(l);
                            }
                            getSupportActionBar().setTitle("Nº pedido: " + pedido);
                            lineaAdapter = new LineaAdapter(getApplicationContext(), misLineas);
                            lineasPedido.setAdapter(lineaAdapter);
                            Log.d("Líneas mostradas", misLineas.size() + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuarioApp", "eiffGest");
                params.put("passwordApp", "U6ObJm9iwHWjYxL");
                params.put("pedido", cod_pedido);
                String empresa = getSharedPreferences("myPrefs", MODE_PRIVATE).getString("empresa", "-");
                params.put("empresa", empresa);
                return params;
            }
        };
        queue.add(sr);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        lineaAdapter.getFilter().filter(newText.toLowerCase());
        return false;
    }
}
