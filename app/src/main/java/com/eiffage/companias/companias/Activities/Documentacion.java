package com.eiffage.companias.companias.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.eiffage.companias.companias.Adapters.GridAdapter;
import com.eiffage.companias.companias.Adapters.ListaEditablesAdapter;
import com.eiffage.companias.companias.DB.MySqliteOpenHelper;
import com.eiffage.companias.companias.Objetos.Documento;
import com.eiffage.companias.companias.Objetos.InputStreamVolleyRequest;
import com.eiffage.companias.R;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class Documentacion extends AppCompatActivity {

    private String URL_ENVIAR_DOCUMENTO = "-";

    //Views
    ListView listDocsTecnica, listPrevencion, listEditables, listEditados;
    GridView myGrid;
    TextView txtDocTecnica, txtPrevencion, txtEditables;
    LinearLayout txtFotos;
    ImageView imgDocTecnica, imgPrevencion, imgEditables, arrowDocTecnica, arrowPrevencion, arrowEditables, arrowFotos;
    boolean visibleDocTecnica = false, visiblePrevencion = false, visibleEditables = false,visibleFotos = false;

    //Traídos desde API
    ArrayList<Documento> rDocTecnica, rPrevencion, rFotos, rEditables, rEditados;

    //Traídos desde BBDD
    ArrayList<Documento> docTecnica, prevencion, editables, editados;

    //Variables importantes
    String cod_pedido = "-";
    String token = "-";
    String cat = "-";
    ProgressDialog progressDialog;
    boolean haPulsadoDocTecnica = false, haPulsadoPrevencion = false, haPulsadoEditables = false;

    //Manejo de BBDD
    MySqliteOpenHelper mySqliteOpenHelper;
    SQLiteDatabase db;

    //Editables
    String origen;
    String destino;
    String nombreQueSeMuestra;
    String ext = "pdf";
    ListaEditablesAdapter editablesAdapter;

    Button enviarEditables;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
        setContentView(R.layout.activity_documentacion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        URL_ENVIAR_DOCUMENTO = getResources().getString(R.string.urlEnviarDocumento);

        listDocsTecnica = findViewById(R.id.listaDocTecnica);
        listPrevencion = findViewById(R.id.listaPrevencion);
        listEditables = findViewById(R.id.listaEditables);
        listEditados = findViewById(R.id.listaEditados);
        myGrid = findViewById(R.id.gridFotos);
        txtDocTecnica = findViewById(R.id.txtDocTecnica);
        txtPrevencion = findViewById(R.id.txtPrevencion);
        txtEditables = findViewById(R.id.txtEditables);
        txtFotos = findViewById(R.id.documentos3);
        imgDocTecnica = findViewById(R.id.imgDownloadDocTecnica);
        imgPrevencion = findViewById(R.id.imgDownloadPrevencion);
        imgEditables = findViewById(R.id.imgDownloadEditables);

        arrowDocTecnica = findViewById(R.id.arrowDocTecnica);
        arrowPrevencion = findViewById(R.id.arrowPrevencion);
        arrowEditables = findViewById(R.id.arrowEditables);
        arrowFotos = findViewById(R.id.arrowFotos);

        enviarEditables = findViewById(R.id.enviarEditables);

        rDocTecnica = new ArrayList<>();
        rPrevencion = new ArrayList<>();
        rFotos = new ArrayList<>();
        rEditables = new ArrayList<>();
        rEditados = new ArrayList<>();

        docTecnica = new ArrayList<>();
        prevencion = new ArrayList<>();
        editables = new ArrayList<>();
        editados = new ArrayList<>();

        cod_pedido = getCod_pedido();
        token = getToken();

        setTitle("Doc. pedido " + cod_pedido);

        mySqliteOpenHelper = new MySqliteOpenHelper(Documentacion.this);
        db = mySqliteOpenHelper.getWritableDatabase();

        Log.d(null, getCod_pedido().toString());
        getFotos();
        llenarArrayListLocales(); //Llama a ----->  mostrarFicherosLocales()

        listDocsTecnica.setVisibility(View.GONE);
        listPrevencion.setVisibility(View.GONE);
        listEditables.setVisibility(View.GONE);
        listEditados.setVisibility(View.GONE);
        myGrid.setVisibility(View.GONE);

        arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
        arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
        arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
        arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));

        txtDocTecnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visibleDocTecnica){
                    listDocsTecnica.setVisibility(View.VISIBLE);
                    visibleDocTecnica = true;
                    listPrevencion.setVisibility(View.GONE);
                    visiblePrevencion = false;
                    listEditables.setVisibility(View.GONE);
                    listEditados.setVisibility(View.GONE);
                    visibleEditables = false;
                    myGrid.setVisibility(View.GONE);
                    visibleFotos = false;
                    Log.d("DOC TECNICA", "VISIBLE");
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
                else {
                    listDocsTecnica.setVisibility(View.GONE);
                    visibleDocTecnica = false;
                    Log.d("DOC TECNICA", "INVISIBLE");
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }
        });

        arrowDocTecnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visibleDocTecnica){
                    listDocsTecnica.setVisibility(View.VISIBLE);
                    visibleDocTecnica = true;
                    listPrevencion.setVisibility(View.GONE);
                    visiblePrevencion = false;
                    listEditables.setVisibility(View.GONE);
                    listEditados.setVisibility(View.GONE);
                    enviarEditables.setVisibility(View.GONE);
                    visibleEditables = false;
                    myGrid.setVisibility(View.GONE);
                    visibleFotos = false;
                    Log.d("DOC TECNICA", "VISIBLE");
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
                else {
                    listDocsTecnica.setVisibility(View.GONE);
                    visibleDocTecnica = false;
                    Log.d("DOC TECNICA", "INVISIBLE");
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }
        });

        txtPrevencion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visiblePrevencion){
                    listPrevencion.setVisibility(View.VISIBLE);
                    visiblePrevencion = true;
                    listDocsTecnica.setVisibility(View.GONE);
                    visibleDocTecnica = false;
                    listEditables.setVisibility(View.GONE);
                    listEditados.setVisibility(View.GONE);
                    enviarEditables.setVisibility(View.GONE);
                    visibleEditables = false;
                    myGrid.setVisibility(View.GONE);
                    visibleFotos = false;
                    Log.d("PREVENCION", "VISIBLE");
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
                else {
                    listPrevencion.setVisibility(View.GONE);
                    visiblePrevencion = false;
                    Log.d("PREVENCION", "INVISIBLE");
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }
        });

        arrowPrevencion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visiblePrevencion){
                    listPrevencion.setVisibility(View.VISIBLE);
                    visiblePrevencion = true;
                    listDocsTecnica.setVisibility(View.GONE);
                    visibleDocTecnica = false;
                    listEditables.setVisibility(View.GONE);
                    listEditados.setVisibility(View.GONE);
                    enviarEditables.setVisibility(View.GONE);
                    visibleEditables = false;
                    myGrid.setVisibility(View.GONE);
                    visibleFotos = false;
                    Log.d("PREVENCION", "VISIBLE");
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
                else {
                    listPrevencion.setVisibility(View.GONE);
                    visiblePrevencion = false;
                    Log.d("PREVENCION", "INVISIBLE");
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }
        });

        txtFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visibleFotos){
                    myGrid.setVisibility(View.VISIBLE);
                    visibleFotos = true;
                    listDocsTecnica.setVisibility(View.GONE);
                    visibleDocTecnica = false;
                    listPrevencion.setVisibility(View.GONE);
                    visiblePrevencion = false;
                    listEditables.setVisibility(View.GONE);
                    listEditados.setVisibility(View.GONE);
                    enviarEditables.setVisibility(View.GONE);
                    visibleEditables = false;
                    Log.d("FOTOS", "VISIBLE");
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
                else {
                    myGrid.setVisibility(View.GONE);
                    visibleFotos = false;
                    Log.d("FOTOS", "INVISIBLE");
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }
        });

        arrowFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visibleFotos){
                    myGrid.setVisibility(View.VISIBLE);
                    visibleFotos = true;
                    listDocsTecnica.setVisibility(View.GONE);
                    visibleDocTecnica = false;
                    listPrevencion.setVisibility(View.GONE);
                    visiblePrevencion = false;
                    listEditables.setVisibility(View.GONE);
                    listEditados.setVisibility(View.GONE);
                    enviarEditables.setVisibility(View.GONE);
                    visibleEditables = false;
                    Log.d("FOTOS", "VISIBLE");
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
                else {
                    myGrid.setVisibility(View.GONE);
                    visibleFotos = false;
                    Log.d("FOTOS", "INVISIBLE");
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }
        });

        txtEditables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visibleEditables){
                    myGrid.setVisibility(View.GONE);
                    visibleFotos = false;
                    listDocsTecnica.setVisibility(View.GONE);
                    visibleDocTecnica = false;
                    listPrevencion.setVisibility(View.GONE);
                    visiblePrevencion = false;
                    listEditables.setVisibility(View.VISIBLE);
                    listEditados.setVisibility(View.VISIBLE);
                    enviarEditables.setVisibility(View.VISIBLE);
                    visibleEditables = true;
                    Log.d("EDITABLE", "VISIBLE");
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
                else {
                    listEditables.setVisibility(View.GONE);
                    listEditados.setVisibility(View.GONE);
                    enviarEditables.setVisibility(View.GONE);
                    visibleEditables = false;
                    Log.d("EDITABLE", "INVISIBLE");
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }
        });

        arrowEditables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!visibleEditables){
                    myGrid.setVisibility(View.GONE);
                    visibleFotos = false;
                    listDocsTecnica.setVisibility(View.GONE);
                    visibleDocTecnica = false;
                    listPrevencion.setVisibility(View.GONE);
                    visiblePrevencion = false;
                    listEditables.setVisibility(View.VISIBLE);
                    listEditados.setVisibility(View.VISIBLE);
                    enviarEditables.setVisibility(View.VISIBLE);
                    visibleEditables = true;
                    Log.d("EDITABLES", "VISIBLE");
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_up_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
                else {
                    listEditables.setVisibility(View.GONE);
                    listEditados.setVisibility(View.GONE);
                    enviarEditables.setVisibility(View.GONE);
                    visibleEditables = false;
                    Log.d("EDITABLES", "INVISIBLE");
                    arrowFotos.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowPrevencion.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowEditables.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                    arrowDocTecnica.setImageDrawable(getResources().getDrawable(android.R.drawable.arrow_down_float));
                }
            }
        });

        listDocsTecnica.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if(verifyStoragePermissions(Documentacion.this)){
                        String sourcePath = docTecnica.get(position).getRutaLocal();
                        File source = new File(sourcePath);

                        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + docTecnica.get(position).getNombreFichero();
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
                        String [] parts = docTecnica.get(position).getNombreFichero().split(Pattern.quote("."));
                        String ext = parts[1];
                        String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                        intent.setDataAndType(data, mimetype);
                        Intent i = Intent.createChooser(intent, "Elige un lector");
                        startActivity(i);
                    }
                    //Se guardan los documentos editados en --> /sdcard/PERMISO DE TRABAJO.pdf
                }

        });

        listPrevencion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(verifyStoragePermissions(Documentacion.this)){
                    String sourcePath = prevencion.get(position).getRutaLocal();
                    File source = new File(sourcePath);

                    String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + prevencion.get(position).getNombreFichero();
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
                    String [] parts = prevencion.get(position).getNombreFichero().split(Pattern.quote("."));
                    String ext = parts[1];
                    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                    intent.setDataAndType(data, mimetype);
                    Intent i = Intent.createChooser(intent, "Elige un lector");
                    startActivity(i);
                }
            }
        });

        listEditables.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(verifyStoragePermissions(Documentacion.this)){
                    String sourcePath = editables.get(position).getRutaLocal();
                    origen = sourcePath;
                    File source = new File(sourcePath);

                    String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + editables.get(position).getNombreFichero();
                    destino = destinationPath;
                    nombreQueSeMuestra = editables.get(position).getNombreFichero();
                    File destination = new File(destinationPath);
                    try
                    {
                        Log.d("ORIGEN", sourcePath);
                        Log.d("DESTINO", destinationPath);

                        FileUtils.copyFile(source, destination);
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
                    Uri data = Uri.fromFile(destination);
                    String [] parts = editables.get(position).getNombreFichero().split(Pattern.quote("."));
                    ext = parts[1];
                    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                    intent.setDataAndType(data, mimetype);
                    Intent i = Intent.createChooser(intent, "Elige un lector");
                    startActivityForResult(i, 0);
                }
            }
        });

        imgDocTecnica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgDocTecnica.setClickable(false);
                imgDocTecnica.setEnabled(false);
                haPulsadoDocTecnica = true;
                //progressDialog = muestraLoader("Descargando documentación técnica...");


                descargarDocumentacionTecnica();
                imgDocTecnica.setImageDrawable(getResources().getDrawable(R.drawable.descarga_on));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        imgDocTecnica.setImageDrawable(getResources().getDrawable(R.drawable.flechadescarga));
                    }
                }, 150);
            }
        });

        imgPrevencion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPrevencion.setClickable(false);
                imgPrevencion.setEnabled(false);
                haPulsadoPrevencion = true;

                //progressDialog = muestraLoader("Descargando documentación de prevención...");


                descargarPrevencion();
                imgPrevencion.setImageDrawable(getResources().getDrawable(R.drawable.descarga_on));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        imgPrevencion.setImageDrawable(getResources().getDrawable(R.drawable.flechadescarga));
                    }
                }, 150);
            }
        });

        imgEditables.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgEditables.setClickable(false);
                imgEditables.setEnabled(false);
                haPulsadoEditables = true;

                //progressDialog = muestraLoader("Descargando documentación editable...");

                descargarEditables();
                imgEditables.setImageDrawable(getResources().getDrawable(R.drawable.descarga_on));
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        imgEditables.setImageDrawable(getResources().getDrawable(R.drawable.flechadescarga));
                    }
                }, 150);
            }
        });

        myGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Documentacion.this, FotoPantallaCompleta.class);
                intent.putExtra("urlImagen", rFotos.get(position).getUrl());
                intent.putExtra("token", token);
                startActivity(intent);
            }
        });

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 0) {
            if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED){
                guardarComo(destino);
            }
        }
        else if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED){
                editablesAdapter.gestionFicheroEditado();
            }
        }
    }

    public void guardarComo(final String dest){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        alert.setMessage("Introduce nombre de fichero");
        alert.setTitle("Guardar como");

        alert.setView(edittext);

        alert.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                if(!edittext.getText().toString().matches("[A-Za-z0-9-_ -]+")){
                    Toast.makeText(getApplicationContext(), "Escribe un nombre válido.\nLos caracteres aceptados son letras, números, espacio, guión y guión bajo.", Toast.LENGTH_SHORT).show();
                    guardarComo(dest);
                }
                else {
                    //  ----------------------------------------
                    //  OK PASO 1 --> RECOGER EL NOMBRE INTRODUCIDO
                    //  PASO 2 --> GENERAR NUEVA RUTA INTERNA
                    //  PASO 3 --> INSERTAR DOCUMENTO EN TABLA DE EDITADOS
                    //  PASO 4 --> AÑADIR LISTA DE EDITADOS A LA LISTA DE DOCUMENTOS LOCALES EDITADOS
                    //  ----------------------------------------
                    String nombreFichero = edittext.getText().toString() + "." + ext;
                    String rutaNuevoFichero = generarNombreFicheroPDF();

                    String sourcePath = dest;
                    File source = new File(sourcePath);

                    String destinationPath = getFilesDir().getAbsolutePath() + "/" + rutaNuevoFichero + "." + ext;
                    File destination = new File(destinationPath);
                    try
                    {
                        FileUtils.copyFile(source, destination);
                        mySqliteOpenHelper.insertarEditable(db, rutaNuevoFichero, nombreFichero, cod_pedido);

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    llenarArrayListLocales();
                    listEditados.setVisibility(View.VISIBLE);
                }

            }
        });

        alert.setNegativeButton("No guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }
    public String getCod_pedido() {
        Intent i = getIntent();
        return i.getStringExtra("cod_pedido");
    }

    public String getToken() {
        SharedPreferences myPrefs = Documentacion.this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        return myPrefs.getString("token", "Sin valor");
    }

    public void listaDocumentosDesdeAPI(final String cod_pedido, final String cat) {

        progressDialog = muestraLoader("Preparando documentos...");

        RequestQueue queue = Volley.newRequestQueue(Documentacion.this);
        StringRequest sr = new StringRequest(Request.Method.GET, Documentacion.this.getResources().getString(R.string.urlBase) + Documentacion.this.getResources().getString(R.string.urlDocumentos) + cod_pedido,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE", response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String content = jsonObject.getString("content");

                            JSONArray job = new JSONArray(content);
                            for (int i = 0; i < job.length(); i++) {
                                JSONObject actual = (JSONObject) job.get(i);

                                if (actual.get("Categoria").equals("Fotos")) {
                                    rFotos.add(new Documento(getCod_pedido(), "Fotos", "-", actual.getString("Ruta"), actual.getString("Fichero")));
                                } else if (actual.get("Categoria").equals("Prevencion")) {
                                    String [] parts = actual.get("Fichero").toString().split(Pattern.quote("."));
                                    String ext = parts[1];
                                    rPrevencion.add(new Documento(getCod_pedido(), "Prevención", generarNombreFicheroPDF() + "." + ext, actual.getString("Ruta"), actual.getString("Fichero")));
                                } else if (actual.get("Categoria").equals("DocTecnica")) {
                                    String [] parts = actual.get("Fichero").toString().split(Pattern.quote("."));
                                    String ext = parts[1];
                                    rDocTecnica.add(new Documento(getCod_pedido(), "Documentación Técnica", generarNombreFicheroPDF() + "." + ext, actual.getString("Ruta"), actual.getString("Fichero")));
                                } else if(actual.get("Categoria").equals("Editables")) {
                                    String [] parts = actual.get("Fichero").toString().split(Pattern.quote("."));
                                    String ext = parts[1];
                                    rEditables.add(new Documento(getCod_pedido(), "Editables", generarNombreFicheroPDF() + "." + ext, actual.getString("Ruta"), actual.getString("Fichero")));
                                }

                            }
                            Log.d("Nº fotos", "" + rFotos.size());
                            Log.d("Nº prevencion", "" + rPrevencion.size());
                            Log.d("Nº docTecnica", "" + rDocTecnica.size());
                            Log.d("Nº editables", "" + rEditables.size());

                            for(int i = 0; i< rPrevencion.size(); i++){
                                Log.d("Prevencion " + i, rPrevencion.get(i).getNombreFichero() + ", " + rPrevencion.get(i).getUrl());
                            }
                            if(isOnline(Documentacion.this)) {
                                mySqliteOpenHelper.borrarFicherosDePedido(db, getCod_pedido(), cat);
                            }

                            if(!cat.equals("Fotos")){
                                progressDialog.dismiss();
                                progressDialog = muestraLoader("Actualizando lista de documentos...");
                                if(cat.equals("Editables")){
                                    mySqliteOpenHelper.borrarEditados(db, cod_pedido);
                                }
                                peticionesDescargas(cat, 0);
                            }
                            else {
                                progressDialog.dismiss();
                                GridAdapter gridAdapter = new GridAdapter(Documentacion.this, rFotos, token);
                                myGrid.setAdapter(gridAdapter);
                            }


                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());
                progressDialog.dismiss();
                try{
                    Toast.makeText(getApplicationContext(), "No hay conexión, inténtalo más tarde.", Toast.LENGTH_SHORT).show();

                    imgDocTecnica.setEnabled(true);
                    imgDocTecnica.setClickable(true);

                    imgPrevencion.setEnabled(true);
                    imgPrevencion.setClickable(true);

                    imgEditables.setEnabled(true);
                    imgEditables.setClickable(true);
                }
                catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        queue.add(sr);
    }

    public void llenarArrayListLocales() {
        docTecnica = mySqliteOpenHelper.getDocumentos(db, cod_pedido, "DocTecnica");
        prevencion = mySqliteOpenHelper.getDocumentos(db, cod_pedido, "Prevencion");
        editables = mySqliteOpenHelper.getDocumentos(db, cod_pedido, "Editables");
        editados = mySqliteOpenHelper.getEditables(db, cod_pedido);
        mostrarFicherosLocales();
    }

    public void mostrarFicherosLocales() {

        try{
            List<String> listaDocTecnica = new ArrayList<>();
            for(int i=0; i<docTecnica.size(); i++){
                listaDocTecnica.add(docTecnica.get(i).getNombreFichero());
            }

            String[] values = new String[listaDocTecnica.size()];
            listaDocTecnica.toArray(values);


            List<String> listaPrevencion = new ArrayList<>();
            for(int i=0; i<prevencion.size(); i++){
                listaPrevencion.add(prevencion.get(i).getNombreFichero());
            }

            String[] values2 = new String[listaPrevencion.size()];
            listaPrevencion.toArray(values2);

            List<String> listaEditables = new ArrayList<>();
            for(int i=0; i<editables.size(); i++){
                listaEditables.add(editables.get(i).getNombreFichero());
            }

            String[] values3 = new String[listaEditables.size()];
            listaEditables.toArray(values3);

            editablesAdapter = new ListaEditablesAdapter(this, editados);
            listEditados.setAdapter(editablesAdapter);



            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            // Assign adapter to ListView
            listDocsTecnica.setAdapter(adapter);

            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values2);

            // Assign adapter to ListView
            listPrevencion.setAdapter(adapter2);

            ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values3);

            // Assign adapter to ListView
            listEditables.setAdapter(adapter3);


        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    //                 Este método es llamado al pulsar el botón "DESCARGAR" de Documentación técnica
    //-----------------------------INSERTA UNA NUEVA FILA EN LA TABLA DE DOCUMENTOS-------------------------------\\

    public void descargarDocumentacionTecnica() {

        rDocTecnica = new ArrayList<>();
        rPrevencion = new ArrayList<>();
        rEditables = new ArrayList<>();
        rFotos = new ArrayList<>();
        listaDocumentosDesdeAPI(cod_pedido, "DocTecnica");
    }

    public void descargarPrevencion() {

        rDocTecnica = new ArrayList<>();
        rPrevencion = new ArrayList<>();
        rEditables = new ArrayList<>();
        rFotos = new ArrayList<>();
        listaDocumentosDesdeAPI(cod_pedido, "Prevencion");
    }

    public void descargarEditables() {

        rDocTecnica = new ArrayList<>();
        rPrevencion = new ArrayList<>();
        rEditables = new ArrayList<>();
        rFotos = new ArrayList<>();

        if(mySqliteOpenHelper.hayEditables(db, cod_pedido)){
            AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(Documentacion.this);
            alertdialogobuilder
                    .setTitle("Atención")
                    .setMessage("Al descargar la documentación editable, perderás los cambios realizados.\n¿Deseas continuar?")
                    .setCancelable(false)
                    .setNegativeButton("Cancelar",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    progressDialog.dismiss();
                                    imgEditables.setEnabled(true);
                                    imgEditables.setClickable(true);
                                }
                            })
                    .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            listaDocumentosDesdeAPI(cod_pedido, "Editables");
                        }
                    })
                    .create();
            alertdialogobuilder.show();
        }
        else {
            listaDocumentosDesdeAPI(cod_pedido, "Editables");
        }
    }

    public String generarNombreFicheroPDF() {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyyyy_HMmmss_SSS", Locale.getDefault());
        Date Ahora = new Date();
        String nombreFichero = format.format(Ahora);
        return nombreFichero;
    }

    public void peticionesDescargas(final String cat, final int pos){

        if(isOnline(Documentacion.this)) {
            RequestQueue mRequestQueue = Volley.newRequestQueue(Documentacion.this);

            ArrayList<Documento> categorias = new ArrayList();
            if(cat.equals("DocTecnica")){
                categorias = rDocTecnica;
            }
            else if(cat.equals("Prevencion")){
                categorias = rPrevencion;
            }
            else if(cat.equals("Editables")){
                categorias = rEditables;
            }

            if(categorias.size() > pos){
                final Documento actual = categorias.get(pos);
                this.cat = cat;

                final ArrayList<Documento> finalCategorias = categorias;
                InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, actual.getUrl(),
                        new Response.Listener<byte[]>() {
                            @Override
                            public void onResponse(byte[] response) {
                                // TODO handle the response
                                try {
                                    if (response != null) {

                                        String name = actual.getNombreFichero();
                                        String rutaLocal = actual.getRutaLocal();

                                        mySqliteOpenHelper.insertarDocumento(db, cod_pedido, getFilesDir().getAbsolutePath() + "/" + actual.getRutaLocal(), name, cat);

                                        FileOutputStream outputStream;
                                        outputStream = openFileOutput(actual.getRutaLocal(), Context.MODE_PRIVATE);
                                        outputStream.write(response);
                                        outputStream.close();

                                        if(pos == finalCategorias.size() - 1){
                                            llenarArrayListLocales();
                                            imgDocTecnica.setClickable(true);
                                            imgDocTecnica.setEnabled(true);
                                            imgPrevencion.setClickable(true);
                                            imgPrevencion.setEnabled(true);
                                            imgEditables.setClickable(true);
                                            imgEditables.setEnabled(true);

                                            progressDialog.dismiss();
                                            if(haPulsadoDocTecnica){
                                                listDocsTecnica.setVisibility(View.VISIBLE);
                                                visibleDocTecnica = true;
                                                listPrevencion.setVisibility(View.GONE);
                                                visiblePrevencion = false;
                                                listEditables.setVisibility(View.GONE);
                                                enviarEditables.setVisibility(View.GONE);
                                                visibleEditables = false;
                                                myGrid.setVisibility(View.GONE);
                                                visibleFotos = false;
                                                haPulsadoDocTecnica = false;
                                            }
                                            else if(haPulsadoPrevencion){
                                                listDocsTecnica.setVisibility(View.GONE);
                                                visibleDocTecnica = false;
                                                listPrevencion.setVisibility(View.VISIBLE);
                                                visiblePrevencion = true;
                                                listEditables.setVisibility(View.GONE);
                                                enviarEditables.setVisibility(View.GONE);
                                                visibleEditables = false;
                                                myGrid.setVisibility(View.GONE);
                                                visibleFotos = false;
                                                haPulsadoPrevencion = false;
                                            }
                                            else if(haPulsadoEditables){
                                                listDocsTecnica.setVisibility(View.GONE);
                                                visibleDocTecnica = false;
                                                listPrevencion.setVisibility(View.GONE);
                                                visiblePrevencion = false;
                                                listEditables.setVisibility(View.VISIBLE);
                                                enviarEditables.setVisibility(View.VISIBLE);
                                                visibleEditables = true;
                                                myGrid.setVisibility(View.GONE);
                                                visibleFotos = false;
                                                haPulsadoEditables = false;
                                            }
                                            progressDialog.dismiss();
                                        }
                                        else {
                                            peticionesDescargas(cat, (pos+1));
                                        }


                                    }
                                    else {
                                        progressDialog.dismiss();
                                    }
                                } catch (Exception e) {
                                    progressDialog.dismiss();
                                    // TODO Auto-generated catch block
                                    Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        imgPrevencion.setEnabled(true);
                        imgPrevencion.setClickable(true);
                        imgDocTecnica.setEnabled(true);
                        imgDocTecnica.setClickable(true);
                        imgEditables.setEnabled(true);
                        imgEditables.setClickable(true);
                        Toast.makeText(Documentacion.this, "Hay ficheros pendientes de sincronizar. Inténtalo de nuevo en unos minutos.", Toast.LENGTH_SHORT).show();

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
                request.setRetryPolicy((new DefaultRetryPolicy(60 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));
                mRequestQueue.add(request);
            }
            else {
                progressDialog.dismiss();
                if(categorias.size() == 0){
                    Toast.makeText(getApplicationContext(), "NO HAY DOCUMENTOS", Toast.LENGTH_SHORT).show();
                    imgPrevencion.setEnabled(true);
                    imgPrevencion.setClickable(true);
                    imgDocTecnica.setEnabled(true);
                    imgDocTecnica.setClickable(true);
                    imgEditables.setEnabled(true);
                    imgEditables.setClickable(true);
                }
            }
        }
            else {
                //Alert dialog diciendo que no hay conexión
                Toast.makeText(getApplicationContext(), "NO HAY INTERNET", Toast.LENGTH_SHORT).show();
            imgPrevencion.setEnabled(true);
            imgPrevencion.setClickable(true);
            imgDocTecnica.setEnabled(true);
            imgDocTecnica.setClickable(true);
            imgEditables.setEnabled(true);
            imgEditables.setClickable(true);
            }
    }

    public void enviarEditados(View view) throws IOException {
        if(!mySqliteOpenHelper.hayEditables(db, cod_pedido)){
            Toast.makeText(getApplicationContext(), "No hay documentos editados", Toast.LENGTH_SHORT).show();
        }
        else {
            progressDialog = muestraLoader("Enviando documentación rellenada...");

            RequestQueue queue = Volley.newRequestQueue(this);

            editados = mySqliteOpenHelper.getEditables(db, cod_pedido);
            for(int i = 0; i < editados.size(); i++){
                final int actual = i;
                File file = new File(getFilesDir().getAbsolutePath() + "/" +  editados.get(i).getRutaLocal() + ".pdf");
                final String nombreFichero =  editados.get(i).getNombreFichero();
                Log.d("NOMBRE DEL FICHERO", nombreFichero);
                final byte[] ficheroBytes = FileUtils.readFileToByteArray(file);
                final String ficheroCodificado = "holapaco, " + Base64.encodeToString(ficheroBytes, Base64.DEFAULT);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ENVIAR_DOCUMENTO,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("ENVIO FICHEROS", response);
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String salida = jsonObject.getString("salida");
                                    if(salida.equals("OK")){
                                        mySqliteOpenHelper.borrarEditado(db, editados.get(actual).getRutaLocal());
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Algunos ficheros no se han enviado.", Toast.LENGTH_SHORT).show();
                                }

                                if(actual == editados.size() -1){
                                    progressDialog.dismiss();
                                    ArrayList<Documento> a = mySqliteOpenHelper.getEditables(db, cod_pedido);
                                    if(a.size() == 0){
                                        Toast.makeText(getApplicationContext(), "Se han enviado los ficheros.", Toast.LENGTH_SHORT).show();
                                        //----------Descarga de documentos editables----------\\
                                        descargarEditables();
                                    }
                                    ListaEditablesAdapter adapter = new ListaEditablesAdapter(Documentacion.this, a);
                                    listEditados.setAdapter(adapter);

                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + token);

                        return params;
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("num_pedido", cod_pedido);
                        params.put("nombre_fichero", nombreFichero);
                        Log.d("Params", params.toString());
                        params.put("foto", ficheroCodificado);
                        return params;
                    }
                };
                stringRequest.setTag("ENVIO_FICHERO EDITADO");
                stringRequest.setRetryPolicy((new DefaultRetryPolicy(60 * 1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)));

                queue.add(stringRequest);
            }
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public void getFotos(){
        listaDocumentosDesdeAPI(getCod_pedido(), "Fotos");
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

    public ProgressDialog muestraLoader(String mensaje){

        progressDialog = new ProgressDialog(Documentacion.this);
        progressDialog.setMessage("Espere, por favor"); // Setting Message
        progressDialog.setTitle(mensaje); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show(); // Display Progress Dialog
        progressDialog.setCancelable(false);
        progressDialog.show();

        return progressDialog;

    }
}