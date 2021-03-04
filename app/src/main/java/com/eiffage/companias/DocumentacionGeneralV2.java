package com.eiffage.companias;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eiffage.companias.companias.DB.MySqliteOpenHelper;
import com.eiffage.companias.companias.Objetos.Documento;
import com.eiffage.companias.companias.Objetos.InputStreamVolleyRequest;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class DocumentacionGeneralV2 extends AppCompatActivity {

    private String URL_ACTUALIZAR_DOCUMENTOS = "-";
    ProgressDialog progressDialog;
    SharedPreferences sp;
    String token, delegacion;
    ArrayList<Documento> documentosLocales, documentosNavision, documentosParaDescargar;
    ListView listaDocs;
    MySqliteOpenHelper mySqliteOpenHelper;
    SQLiteDatabase db;
    TextView ultimaActualizacion;
    Button btnDescarga;

    boolean infoCompleta = false;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.act_completa, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Actualización completa
        if (id == R.id.actCompleta) {
            AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
            alertdialogobuilder
                    .setTitle("Actualización completa")
                    .setMessage("Si algún documento te da problemas o se abre el que no toca, se puede hacer una actualización completa de listado de documentos para intentar solucionarlo. \n¿Deseas hacer la actualización completa?")
                    .setCancelable(true)
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            //Borrar todos los documentos generales
                            mySqliteOpenHelper.borrarTodosDocumentosGenerales(db);
                            //Mostrar cambios en lista
                            llenarArrayListLocal();
                            //Actualizar todos los documentos generales
                            actualizarDocumentos(new View(DocumentacionGeneralV2.this));
                        }
                    })
                    .setNegativeButton("En otro momento", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .create();
            if (!DocumentacionGeneralV2.this.isFinishing()) {
                alertdialogobuilder.show();
            }
        }
        else if(id == R.id.infoFichero) {
            if(!infoCompleta)
                infoCompleta = true;
            else
                infoCompleta = false;

            llenarArrayListLocal();
        }

        return super.onOptionsItemSelected(item);
    }

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
        setContentView(R.layout.activity_documentacion_general_v2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        URL_ACTUALIZAR_DOCUMENTOS = getResources().getString(R.string.urlActualizarDocumentos);

        btnDescarga = findViewById(R.id.btnActualizarDocs);
        listaDocs = findViewById(R.id.listaDocsGeneral);
        ultimaActualizacion = findViewById(R.id.ultimaActualizacion);

        mostrarUltimaActualizacion();

        sp = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        token = sp.getString("token", "Sin valor");

        documentosLocales = new ArrayList<>();

        mySqliteOpenHelper = new MySqliteOpenHelper(DocumentacionGeneralV2.this);
        db = mySqliteOpenHelper.getWritableDatabase();

        llenarArrayListLocal();

        listaDocs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(verifyStoragePermissions(DocumentacionGeneralV2.this)){
                    String sourcePath = documentosLocales.get(position).getRutaLocal();
                    File source = new File(sourcePath);

                    String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Companias/" + documentosLocales.get(position).getNombreFichero();
                    File destination = new File(destinationPath);
                    try
                    {
                        FileUtils.copyFile(source, destination);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    Uri data = Uri.fromFile(destination);

                    String [] parts = documentosLocales.get(position).getNombreFichero().split(Pattern.quote("."));
                    String ext = parts[1];
                    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                    intent.setDataAndType(data, mimetype);
                    Intent i = Intent.createChooser(intent, "Elige un lector");
                    startActivity(i);
                }


            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    public void actualizacionCompletaDocumentos(View view){
        final View v = view;
        //Mostrar mensaje de advertencia
        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
        alertdialogobuilder
                .setTitle("Actualización completa")
                .setMessage("Si actualizas con esta opción, se borrarán todos los documentos y volverán a descargarse. \nEsta opción se recomienda cuando hay ficheros duplicados o algún fichero no abre el documento que debería.\n¿Deseas hacer la actualización completa?")
                .setCancelable(true)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        //Borrar todos los documentos generales
                        mySqliteOpenHelper.borrarTodosDocumentosGenerales(db);
                        //Mostrar cambios en lista
                        llenarArrayListLocal();
                        //Actualizar todos los documentos generales
                        actualizarDocumentos(v);
                    }
                })
                .setNegativeButton("En otro momento", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        if (!DocumentacionGeneralV2.this.isFinishing()){
            alertdialogobuilder.show();
        }

    }

    public void actualizarDocumentos(View view){
        btnDescarga.setEnabled(false);
        documentosParaDescargar = new ArrayList<>();
        muestraLoader("Preparando descarga de documentos...");
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.GET, URL_ACTUALIZAR_DOCUMENTOS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Respuesta docs general", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String content = jsonObject.getString("content");

                            JSONArray job = new JSONArray(content);
                            documentosNavision = new ArrayList<>();
                            for (int i = 0; i < job.length(); i++) {
                                JSONObject actual = (JSONObject) job.get(i);

                                String ruta = actual.getString("ruta");
                                String nombreFichero = actual.getString("fichero");
                                String numID = actual.getString("numID");
                                if(i==0){
                                    delegacion = actual.getString("delegacion");
                                    SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
                                    editor.putString("delegacion", delegacion);
                                    editor.apply();
                                }

                                String [] parts = actual.get("fichero").toString().split(Pattern.quote("."));
                                String ext = parts[1];
                                //Documento doc = new Documento(delegacion, "Docu_General_IBE", generarNombreFicheroPDF() + "." + ext, ruta, nombreFichero);
                                Documento doc = new Documento(delegacion, "Docu_General_IBE", numID + "." + ext, ruta, nombreFichero);

                                documentosNavision.add(doc);
                            }
                            progressDialog.dismiss();

                            muestraLoader("Comprobando cambios en la lista de ficheros...");

                             //---------------------------------------- AQUÍ SE COMPRUEBA SI EL FICHERO YA ESTÁ LOCALMENTE. ------------------------------------------------------ \\
                            //----------------------SI LO ESTÁ NO SE DESCARGA, SI NO ESTÁ SE AÑADE A LA LISTA DE FICHEROS PARA DESCARGAR ------------------------------------------ \\
                            for(int i=0; i< documentosNavision.size(); i++){
                                String nombre = documentosNavision.get(i).getNombreFichero();
                                boolean encontrado = false;
                                for(int j=0; j<documentosLocales.size(); j++){
                                    String nombreLocal = documentosLocales.get(j).getNombreFichero();
                                    if(nombre.equals(nombreLocal)){
                                        encontrado = true;
                                        j = documentosLocales.size() -1;
                                    }
                                }
                                if(!encontrado){
                                    documentosParaDescargar.add(documentosNavision.get(i));
                                }
                            }

                            //---------------------------- AQUÍ SE COMPRUEBA SI EL FICHERO LOCAL DEBE SEGUIR GUARDADO. ----------------------------------------------- \\
                            //----------------------SI ESTÁ EN LA LISTA DE NAV SE MANTIENE, SI NO ESTÁ SE ELIMINA LOCALMENTE ------------------------------------------ \\
                            for(int i=0; i< documentosLocales.size(); i++){
                                String nombre = documentosLocales.get(i).getNombreFichero();
                                boolean encontrado = false;
                                for(int j=0; j<documentosNavision.size(); j++){
                                    String nombreNAV = documentosNavision.get(j).getNombreFichero();
                                    if(nombre.equals(nombreNAV)){
                                        encontrado = true;
                                        j = documentosNavision.size() -1;
                                    }
                                }
                                if(!encontrado){
                                    try{
                                        db.rawQuery("DELETE FROM Documentos WHERE rutaInterna LIKE '" + documentosLocales.get(i).getRutaLocal() + "'", null);
                                    }
                                    catch (SQLiteException e){
                                        e.printStackTrace();
                                    }
                                }
                            }

                            //---------------------------- AQUÍ SE LLAMA A LA DESCARGA DE LOS FICHEROS ------------------------------------- \\
                            //------------------------------- QUE NO ESTÁN EN LOCAL PERO SÍ EN NAV ------------------------------------------ \\
                            progressDialog.dismiss();
                            RequestQueue colaFicheros = Volley.newRequestQueue(DocumentacionGeneralV2.this);

                            descargarNuevosFicheros(0, colaFicheros);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            btnDescarga.setEnabled(true);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                mensajeAlert("Error al conectar. Por favor, revisa tu conexión e inténtalo de nuevo");
                btnDescarga.setEnabled(true);
                Log.d("Error doc general", error.toString());
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

    public void descargarNuevosFicheros(final int pos, final RequestQueue colaFicheros){
        if(isOnline(DocumentacionGeneralV2.this)) {
            if(documentosParaDescargar.size() > pos){
                muestraLoader("Descargando fichero " + (pos+1) + " de " + documentosParaDescargar.size());
                final Documento actual = documentosParaDescargar.get(pos);
                Log.d("INSERCION DOC-POS NUEVO", "" + pos);

                final ArrayList<Documento> finalDocumentos = documentosParaDescargar;
                InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, actual.getUrl(),
                        new Response.Listener<byte[]>() {
                            @Override
                            public void onResponse(byte[] response) {
                                try {
                                    if (response != null) {

                                        String name = actual.getNombreFichero();
                                        String rutaLocal = actual.getRutaLocal();

                                        mySqliteOpenHelper.insertarDocumento(db, delegacion, getFilesDir().getAbsolutePath() + "/" + rutaLocal, name, "Docu_General_IBE");

                                        FileOutputStream outputStream;
                                        outputStream = openFileOutput(actual.getRutaLocal(), Context.MODE_PRIVATE);
                                        outputStream.write(response);
                                        outputStream.close();

                                        if(pos == finalDocumentos.size() - 1){
                                            llenarArrayListLocal();
                                            guardarActualizacion();
                                            mensajeAlert("Documentos actualizados");
                                            progressDialog.dismiss();
                                            btnDescarga.setEnabled(true);
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            descargarNuevosFicheros(pos+1, colaFicheros);
                                        }

                                    }
                                    else {
                                        progressDialog.dismiss();
                                        btnDescarga.setEnabled(true);
                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                                    e.printStackTrace();
                                    btnDescarga.setEnabled(true);
                                }
                            }
                        }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        btnDescarga.setEnabled(true);
                        Log.d("ERROR", error.toString());
                        Toast.makeText(DocumentacionGeneralV2.this, "Ha habido algún problema. Inténtalo de nuevo en unos minutos.", Toast.LENGTH_SHORT).show();

                    }
                }
                ) {
                    // httpbin.org needs proper accept headers
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String>  params = new HashMap<String, String>();
                        //params.put("Content-Type", "application/pdf");
                        params.put("Authorization", "Bearer " + token);

                        return params;
                    }

                };
                request.setRetryPolicy((new DefaultRetryPolicy(20 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
                colaFicheros.add(request);
            }
            else {
                progressDialog.dismiss();
                btnDescarga.setEnabled(true);
                if(documentosParaDescargar.size() == 0){
                    Toast.makeText(getApplicationContext(), "NO HAY DOCUMENTOS PARA DESCARGAR", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else {
            //Alert dialog diciendo que no hay conexión
            Toast.makeText(getApplicationContext(), "NO HAY INTERNET", Toast.LENGTH_SHORT).show();
            btnDescarga.setEnabled(true);
        }
    }

    public boolean verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
            return false;
        }
        return true;
    }

    public void llenarArrayListLocal() {
        SharedPreferences sp = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        delegacion = sp.getString("delegacion", "");

        documentosLocales = new ArrayList<>();
        documentosLocales = mySqliteOpenHelper.getDocumentos(db, delegacion, "Docu_General_IBE");
        mostrarFicherosLocales();

    }

    public void mostrarFicherosLocales() {

        try{
            List<String> lista = new ArrayList<>();
            if(infoCompleta){
                for(int i=0; i<documentosLocales.size(); i++){
                    lista.add(documentosLocales.get(i).getNombreFichero() + " \n \nRuta: " + documentosLocales.get(i).getRutaLocal().substring(40));
                }
            }
            else {
                for(int i=0; i<documentosLocales.size(); i++){
                    lista.add(documentosLocales.get(i).getNombreFichero());
                }
            }


            String[] values = new String[lista.size()];
            lista.toArray(values);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            listaDocs.setAdapter(adapter);

        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void muestraLoader(String message){
        progressDialog = new ProgressDialog(DocumentacionGeneralV2.this);
        progressDialog.setMessage(message); // Setting Message
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.setCancelable(false);
        progressDialog.show();
    }


    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public String generarNombreFicheroPDF() {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HMmmss_SSS", Locale.getDefault());
        Date Ahora = new Date();
        String nombreFichero = format.format(Ahora);
        return nombreFichero;
    }

    public void mensajeAlert(String message){
        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
        alertdialogobuilder
                .setTitle("Documentación general")
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        if (!DocumentacionGeneralV2.this.isFinishing()){
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

        ultimaActualizacion  = findViewById(R.id.ultimaActualizacion);
        ultimaActualizacion.setText("Última actualización: AHORA");
        ultimaActualizacion.setBackgroundColor(getResources().getColor(R.color.VerdeBootstrap));
        SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
        editor.putString("ultimaActualizacionDocGeneralFecha", fecha);
        editor.putString("ultimaActualizacionDocGeneralHora", hora);
        editor.apply();

    }

    public void mostrarUltimaActualizacion(){
        SharedPreferences myPrefs = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String f = myPrefs.getString("ultimaActualizacionDocGeneralFecha", "-");
        String s = myPrefs.getString("ultimaActualizacionDocGeneralHora", "-");
        ultimaActualizacion = findViewById(R.id.ultimaActualizacion);
        ultimaActualizacion.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        ultimaActualizacion  = findViewById(R.id.ultimaActualizacion);
        if(!f.equals("-") && !s.equals("-")){
            ultimaActualizacion.setText("Última actualización: " + f + ", " + s);
        }

    }

}
