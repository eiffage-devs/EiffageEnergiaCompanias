package com.eiffage.companias.Activities.FotosPedido;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.Activities.DelegarTarea;
import com.eiffage.companias.BuildConfig;
import com.eiffage.companias.Global.APIHelper;
import com.eiffage.companias.Global.Global;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class FotosPedidoModel {

    private Context context;
    private FotosPedidoPresenter presenter;
    private Global global;
    private APIHelper apiHelper;
    private RequestQueue queue;

    public FotosPedidoModel(Context context, FotosPedidoPresenter presenter){
        this.context = context;
        this.presenter = presenter;

        this.global = new Global(context);
        this.apiHelper = new APIHelper();
        queue = Volley.newRequestQueue(context);
    }

    public void getRutasFotos(final String codPedido){
        StringRequest sr = new StringRequest(Request.Method.POST, apiHelper.URL_RUTAS_FOTOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       Log.d("RutasFotos", response);
                       presenter.setRutasFotos(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                global.muestraAlert("Error obteniendo imágenes", error.networkResponse + " " + error.getMessage());
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
                params.put("usuarioApp", apiHelper.usuarioApp);
                params.put("passwordApp", apiHelper.passwordApp);
                params.put("empresa", new Global(context).myPrefs.getString("empresa", "-"));
                params.put("pedido", codPedido);
                Log.d("Params rutas fotos", params.toString());
                return params;
            }
        };
        queue.add(sr);
    }

    public void descargarFoto(final String path, final String rutaLocal){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiHelper.URL_DESCARGAR_FOTO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Longitud archivo", response.length() + "");
                        Log.d("Ruta local", context.getExternalFilesDir(null) + rutaLocal);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            String content = jsonObject.getString("content");

                            if(content.contains("ERROR")){
                                global.muestraAlert("El servidor dice", content).show();
                            }
                            else {
                                byte[] data = Base64.decode(content, Base64.DEFAULT);
                                File file = new File(context.getExternalFilesDir(null) + rutaLocal);
                                FileOutputStream outputStream;
                                outputStream = new FileOutputStream(file);
                                outputStream.write(data);
                                outputStream.close();
                                presenter.añadirFoto(context.getExternalFilesDir(null) + rutaLocal);
                            }
                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR DESCARGA FOTO:", error.toString());
                global.muestraAlert("Error al descargar", error.toString());
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
                params.put("usuarioApp", apiHelper.usuarioApp);
                params.put("passwordApp", apiHelper.passwordApp);
                params.put("pRuta", path);
                return params;
            }
        };
        stringRequest.setRetryPolicy((new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
        queue.add(stringRequest);
    }
}
