package com.eiffage.companias.Activities.ModificarDatosFoto;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.provider.MediaStore;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.eiffage.companias.Objetos.Foto;
import com.eiffage.companias.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ModificarDatosFotoView extends AppCompatActivity {

    private ModificarDatosFotoPresenter presenter;

    private String mCurrentPhotoPath;
    private ImageView imagenFoto;
    private Spinner spinnerCategorias, spinnerSubcategorias;
    private EditText descripcion;

    private LocationManager locationManager;

    byte[] imagenParaIntent;
    Bitmap nuevaFoto;
    String categoria, subcategoria, desc, fecha, hora, lattitude, longitude;

    String idFoto;
    Foto foto;

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
        setContentView(R.layout.activity_nueva_foto);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(null);

        this.presenter = new ModificarDatosFotoPresenter(this, this);

        findViews();

        Intent intent = getIntent();
        idFoto = intent.getStringExtra("idFoto");
        Log.d("IDFOTO", idFoto);

        presenter.getFoto(idFoto);
        presenter.precargarSpinners();

        setListeners();
    }

    public void findViews(){
        imagenFoto = findViewById(R.id.imagenFoto);
        spinnerCategorias = findViewById(R.id.spinner);
        spinnerSubcategorias = findViewById(R.id.spinner2);
        descripcion = findViewById(R.id.descripcion);
    }

    public void setInfoFoto(Foto foto){
        this.foto = foto;
        Bitmap bitmap = BitmapFactory.decodeFile(foto.getUrlFoto());
        imagenFoto.setImageBitmap(bitmap);
        descripcion.setText(foto.getDescripcion());
    }

    public void cargarInfo(){


        if(!foto.getCategoria().equals("-")){

            String c = foto.getCategoria().substring(6);
            Log.d("categoria", c.substring(6));

            String s = foto.getSubcategoria();

            ArrayList cats = new ArrayList(Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.categorias)));
            spinnerCategorias.setSelection(cats.indexOf(c));

            if(cats.indexOf(c) == 0){
                ArrayAdapter<CharSequence> subadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sub1, android.R.layout.simple_spinner_item);
                subadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerSubcategorias.setAdapter(subadapter);

                ArrayList subcats = new ArrayList(Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.sub1)));
                spinnerSubcategorias.setSelection(subcats.indexOf(s));
            }
            else if(cats.indexOf(c) == 1){
                ArrayAdapter<CharSequence> subadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sub2, android.R.layout.simple_spinner_item);
                subadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerSubcategorias.setAdapter(subadapter);

                ArrayList subcats = new ArrayList(Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.sub2)));
                spinnerSubcategorias.setSelection(subcats.indexOf(s));
            }
            else if(cats.indexOf(c) == 2){
                ArrayAdapter<CharSequence> subadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sub3, android.R.layout.simple_spinner_item);
                subadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerSubcategorias.setAdapter(subadapter);

                ArrayList subcats = new ArrayList(Arrays.asList(getApplicationContext().getResources().getStringArray(R.array.sub3)));
                spinnerSubcategorias.setSelection(subcats.indexOf(s));
            }
            else {
                spinnerCategorias.setSelection(0);

                ArrayAdapter<CharSequence> subadapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sub1, android.R.layout.simple_spinner_item);
                subadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerSubcategorias.setAdapter(subadapter);
                spinnerSubcategorias.setSelection(0);
            }
        }

        //Atentos a los cambios en los spinner

        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sub1, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerSubcategorias
                            .setAdapter(adapter);

                } else if (pos == 1) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sub2, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerSubcategorias.setAdapter(adapter);

                } else if (pos == 2) {
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sub3, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerSubcategorias.setAdapter(adapter);

                } else {
                    spinnerSubcategorias.setAdapter(null);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.sub1, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubcategorias.setAdapter(adapter);
            }
        });
    }

    public void setAdapter(String sp, ArrayAdapter<CharSequence> adapter){
        switch (sp){
            case "cat":
                spinnerCategorias.setAdapter(adapter);
                break;
            case "sub":
                spinnerSubcategorias.setAdapter(adapter);
                break;
        }
    }

    public void setListeners(){
        spinnerCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                presenter.onItemSelected(parent, view, pos, id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                presenter.onNothingSelected();
            }
        });

    }

    public void aceptar(View v){
        recogerDatos();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("categoria", categoria);
        returnIntent.putExtra("subcategoria", subcategoria);
        returnIntent.putExtra("descripcion", desc);
        returnIntent.putExtra("idFoto", idFoto);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void cancelar(View view){
        finish();
    }

    public void recogerDatos() {

        //Categoría
        if (spinnerCategorias.getSelectedItem() == null) {
            categoria = "-";
        } else categoria = spinnerCategorias.getSelectedItem().toString();

        //Subcategoría
        if (spinnerSubcategorias.getSelectedItem() == null) {
            subcategoria = "-";
        } else subcategoria = spinnerSubcategorias.getSelectedItem().toString();

        //Descripción
        if (descripcion.getText().toString().equals("")) {
            desc = "-";
        } else desc = descripcion.getText().toString();

        //Recoger coordenadas
        //recogerCoordenadas();
    }

    public void recogerFechaYHora(){
        //----------Fecha y hora de la foto----------\\
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM", Locale.getDefault());
        Date date = new Date();
        fecha = dateFormat.format(date);

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

        hora = tempHora + ":" + tempMin + ":" + tempSeg;
    }

    public void recogerCoordenadas(){
        //----------Recoger coordenadas GPS----------\\
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mensajeDeAlertaGPS();

        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //ActivityCompat.requestPermissions(, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

                if (location != null) {
                    double latti = location.getLatitude();
                    double longi = location.getLongitude();
                    lattitude = String.valueOf(latti);
                    longitude = String.valueOf(longi);

                } else if (location1 != null) {
                    double latti = location1.getLatitude();
                    double longi = location1.getLongitude();
                    lattitude = String.valueOf(latti);
                    longitude = String.valueOf(longi);

                } else if (location2 != null) {
                    double latti = location2.getLatitude();
                    double longi = location2.getLongitude();
                    lattitude = String.valueOf(latti);
                    longitude = String.valueOf(longi);

                } else {
                    //Toast.makeText(this, "Imposible localizar tu posición", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }

    protected void mensajeDeAlertaGPS() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Por favor activa el GPS de tu dispositivo")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        getApplicationContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
