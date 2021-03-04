package com.eiffage.companias.companias.Activities.DetalleTarea;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.companias.DB.MySqliteOpenHelper;
import com.eiffage.companias.companias.DTO.InfoPedidoDTO;
import com.eiffage.companias.companias.Global.APIHelper;
import com.eiffage.companias.companias.Global.Global;
import com.eiffage.companias.companias.Global.ImageHelper;
import com.eiffage.companias.companias.Objetos.Foto;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetalleTareaModel {

    private Context context;
    private DetalleTareaPresenter presenter;
    private APIHelper apiHelper;
    private Global global;
    private ImageHelper imageHelper;

    private MySqliteOpenHelper mySqliteOpenHelper;
    private SQLiteDatabase db;
    private RequestQueue queue;

    private ArrayList<Foto> myPictures;
    private String token;
    private boolean fallo = false;

    private ProgressDialog progressDialog;

    public DetalleTareaModel(Context context, DetalleTareaPresenter presenter){
        this.context = context;
        this.presenter = presenter;

        apiHelper = new APIHelper();
        global = new Global(context);
        imageHelper = new ImageHelper(context);
        mySqliteOpenHelper = new MySqliteOpenHelper(context);
        db = mySqliteOpenHelper.getWritableDatabase();
        queue = Volley.newRequestQueue(context);

        token = global.myPrefs.getString("token", "-");
    }

    public String getCreadaPor(String idTarea){
        String creadaPor = "-";
        Cursor c0 = db.rawQuery("SELECT creadaPor FROM Tarea WHERE cod_tarea LIKE '" + idTarea + "'", null);
        if (c0.getCount() > 0) {
            c0.moveToFirst();
            creadaPor = c0.getString(0);
        }
        c0.close();
        return creadaPor;
    }

    public InfoPedidoDTO getInfoPedido(String codPedido){
        InfoPedidoDTO infoPedidoDTO = new InfoPedidoDTO();
        infoPedidoDTO.codPedido = codPedido;
        Cursor c = db.rawQuery("SELECT * FROM Pedido WHERE codigo LIKE '" + codPedido + "'", null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            String descripcion = c.getString(1);
            String fecha = c.getString(2);
            String fechaFinMeco = c.getString(7);
            String localidad = c.getString(5);

            infoPedidoDTO.descPedido = descripcion;
            infoPedidoDTO.fechaPedido = fecha;
            infoPedidoDTO.fechaFinMeco = fechaFinMeco;
            infoPedidoDTO.localidad = localidad;
        }
        c.close();
        return infoPedidoDTO;
    }

    public void cargarFotosLocales(String idTarea){
        Cursor c = mySqliteOpenHelper.recuperarFotos(db, idTarea);
        Log.d("Nº DE FOTOS de TAREA", c.getCount() + "");
        myPictures = new ArrayList<>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id = c.getInt(0);
            String descripcion = c.getString(2);
            String categoria = c.getString(3);
            String subcategoria = c.getString(4);
            String fecha = c.getString(5);
            String hora = c.getString(6);
            String coordenadasFotos = c.getString(7);
            String urlFoto = c.getString(9);
            myPictures.add(new Foto(id, categoria, subcategoria, fecha, hora, coordenadasFotos, idTarea, descripcion, urlFoto));
            c.moveToNext();
        }
        c.close();
        Log.d("FOTOS A PONER", "Desde model");
        for(int i=0; i<myPictures.size(); i++){
            Log.d("Foto " + i, myPictures.get(i).toString());
        }
        presenter.setAdapter(myPictures);
    }

    public void guardarInforme(ArrayList<Foto> myPictures, String idTarea){
        this.myPictures = myPictures;
        //----------Borramos todas las fotos asociadas a la tarea antes de guardar los nuevos datos----------\\
        mySqliteOpenHelper.borrarTodasLasFotosDeTarea(db, idTarea);
        //---------------------------------------------------------------------------------------------------\\

        int i;
        for (i = 0; i < this.myPictures.size(); i++) {
            ContentValues c = new ContentValues();
            Foto actual = myPictures.get(i);
            c.put("descripcion", actual.getDescripcion());
            c.put("categoria", actual.getCategoria());
            c.put("subcategoria", actual.getSubcategoria());
            c.put("fecha", actual.getFecha());
            c.put("hora", actual.getHora());
            c.put("coordenadasFotos", actual.getCoordenadasFoto());
            c.put("idTarea", idTarea);
            c.put("id", actual.getId());
            c.put("urlFoto", String.valueOf(actual.getUrlFoto()));
            Log.d("Mi foto", c.toString());
            int id = mySqliteOpenHelper.insertarFoto(db, c);
            myPictures.get(i).setId(String.valueOf(id));
            Log.d("Guardado " + i, actual.getCategoria() + ", " + actual.getSubcategoria());
        }
    }

    public void enviarYTerminar(boolean terminar, String idTarea, String codPedido){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Espere, por favor"); // Setting Message
        progressDialog.setTitle("Enviando fotos..."); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        progressDialog.show();
        //Guardar cambios activos
        guardarInforme(myPictures, idTarea);
        //Enviar...
        Cursor c = mySqliteOpenHelper.recuperarFotos(db, idTarea);
        if (c.getCount() == 0) {
            try{
                if(terminar){
                    borrarTarea(idTarea, codPedido);
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(context, "No hay fotos para enviar", Toast.LENGTH_SHORT).show();
                    presenter.habilitarEnviar();
                }
            }
            catch (Exception e){
                fallo = true;
                mostrarResultado(true);
            }
            //mostrarResultado(true);

        } else {
            ArrayList<Foto> fotosParaEnviar = myPictures;
            fallo = false;
            for (int i = 0; i < fotosParaEnviar.size(); i++) {
                Foto fotoActual = fotosParaEnviar.get(i);

                if (i == fotosParaEnviar.size() - 1) {
                    if (terminar) {
                        enviarFoto(fotoActual, true, false, codPedido, idTarea);
                    } else {
                        enviarFoto(fotoActual, true, true, codPedido, idTarea);
                    }
                } else {
                    enviarFoto(fotoActual, false, false, codPedido, idTarea);
                }
            }
            mySqliteOpenHelper.fotosEnviadas(db, idTarea);

            //Terminar...
            if (terminar) {
                progressDialog.dismiss();
                borrarTarea(idTarea, codPedido);
            }
        }
    }

    public void enviarFoto(final Foto foto, final boolean ultimaFoto, final boolean mostrarResultado, final String codPedido, final String idTarea){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiHelper.URL_ENVIAR_FOTO_IIS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                        if(response.contains("ERROR")){
                            progressDialog.dismiss();
                            global.muestraAlert("El servidor dice", response).show();
                        }
                        else {
                            try {
                                JSONObject j = new JSONObject(response);
                                if (ultimaFoto) {
                                    {
                                        progressDialog.dismiss();
                                        if (mostrarResultado) {
                                            mostrarResultado(false);
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(" ERROR RESPONSE", error.toString());
                fallo = true;
                if (ultimaFoto) {
                    progressDialog.dismiss();
                    if (mostrarResultado) {
                        mostrarResultado(false);
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuarioApp", apiHelper.usuarioApp);
                params.put("passwordApp", apiHelper.passwordApp);
                params.put("empresa", global.myPrefs.getString("empresa", "-"));
                params.put("recurso", global.myPrefs.getString("recurso", "-"));
                params.put("pedido", codPedido);
                params.put("descripcion", foto.getDescripcion());
                params.put("area", foto.getCategoria());
                params.put("subarea", foto.getSubcategoria());
                params.put("numTarea", foto.getIdTarea());
                params.put("coordenadas", foto.getCoordenadasFoto());
                params.put("fecha", foto.getFecha());
                params.put("hora", foto.getHora());
                String foto64 = imageHelper.fotoBase64(foto.getUrlFoto());
                params.put("foto", foto64);
                params.put("extFoto", "jpg");
                params.put("md5Foto", imageHelper.md5(foto64));
                return params;
            }
        };
        stringRequest.setTag("ENVIO_FOTOS");
        stringRequest.setRetryPolicy((new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));

        queue.add(stringRequest);
    }

    public void borrarTarea(final String idTarea, final String codPedido){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiHelper.URL_BORRAR_TAREA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        mySqliteOpenHelper.borrarTodasLasFotosDeTarea(db, idTarea);
                        mySqliteOpenHelper.borrarTarea(db, idTarea);
                        Log.d("FINALIZAR TAREA", response);
                        try {
                            JSONObject j = new JSONObject(response);
                            mostrarResultado(true);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.d("FINALIZAR TAREA", error.toString());
                Toast.makeText(context, "No hay conexión, prueba más tarde", Toast.LENGTH_SHORT).show();
                presenter.habilitarEnviar();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("num_pedido", codPedido);
                params.put("num_tarea", idTarea);
                Log.d("Params borrado", params.toString());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void borrarTarea(String idTarea){
        mySqliteOpenHelper.borrarTarea(db, idTarea);
    }

    public void marcarFotosEnviadas(String idTarea){
        mySqliteOpenHelper.fotosEnviadas(db, idTarea);
    }

    public void borrarFotos(String idTarea){
        mySqliteOpenHelper.borrarTodasLasFotosDeTarea(db, idTarea);
    }

    public void mostrarResultado(final boolean terminar) {
        if (!terminar) {
            if (fallo) {
                AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(context);
                alertdialogobuilder
                        .setTitle("Error al enviar fotos")
                        .setMessage("Ha ocurrido un error.\nCompruebe su conexión y vuelva a intentarlo.\nTus fotos siguen guardadas.")
                        .setCancelable(false)
                        .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                presenter.habilitarEnviar();
                                presenter.finish();
                            }
                        })
                        .create();
                alertdialogobuilder.show();
            } else {
                final AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(context);
                alertdialogobuilder
                        .setTitle("Fotos enviadas")
                        .setMessage("Las fotos se han enviado correctamente.\nLa tarea sigue activa.")
                        .setCancelable(false)
                        .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                presenter.habilitarEnviar();
                                presenter.marcarFotosEnviadas();
                                presenter.borrarFotos();
                                presenter.cargarFotosLocales();
                                presenter.setAdapter(new ArrayList<Foto>());
                            }
                        })
                        .create();
                alertdialogobuilder.show();
            }
        } else {
            if (fallo) {
                AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(context);
                alertdialogobuilder
                        .setTitle("Error al enviar fotos")
                        .setMessage("Ha ocurrido un error.\nCompruebe su conexión y vuelva a intentarlo.\nTus fotos siguen guardadas.")
                        .setCancelable(false)
                        .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                presenter.habilitarEnviar();
                                presenter.finish();
                            }
                        })
                        .create();
                alertdialogobuilder.show();

            } else {
                final AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(context);
                alertdialogobuilder
                        .setTitle("Fotos enviadas")
                        .setMessage("Se han enviado correctamente las fotos.\nLa tarea ha finalizado.")
                        .setCancelable(false)
                        .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                presenter.habilitarEnviar();
                                presenter.marcarFotosEnviadas();
                                presenter.borrarFotos();
                                presenter.cargarFotosLocales();
                                presenter.setAdapter(new ArrayList<Foto>());
                                alertdialogobuilder.setMessage("");
                                presenter.finish();
                            }
                        })
                        .create();
                alertdialogobuilder.show();
            }
        }
    }

    public void actualizarValor(String idFoto, String clave, String valor){
        mySqliteOpenHelper.actualizarValor(db, idFoto, clave, valor);
    }

    public void borrarFoto(String idFoto){
        mySqliteOpenHelper.borrarFoto(db, idFoto);
    }
}
