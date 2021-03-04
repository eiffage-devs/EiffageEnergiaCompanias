package com.eiffage.companias.companias.Activities.DetalleTarea;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.eiffage.companias.companias.Adapters.AdapterFotosTarea;
import com.eiffage.companias.companias.Objetos.Foto;
import com.eiffage.companias.R;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.util.ArrayList;

public class DetalleTareaView extends AppCompatActivity {

    private DetalleTareaPresenter presenter;

    private TextView txtCreadaPor, txtCodPedido, txtDescPedido, txtFechaPedido, txtMarcoPedido, txtLocalidadPedido;
    public NestedScrollView scrollView;
    private CheckBox checkFinalizar;
    private MaterialCardView cardDocumentacion, cardFotosPedido, cardLineasPedido;

    Button abrirCamara, enviar, traspasar;
    ArrayList<Foto> myPictures;
    RecyclerView listaFotos;
    private RecyclerView.LayoutManager layoutManager;
    public AdapterFotosTarea listaFotosAdapter;

    private static final int REQUEST_LOCATION = 1;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opciones_tarea, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.traspasar:
                traspasar();
                return true;
            case R.id.eliminar:
                eliminarTarea();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //
    //      Método para usar flecha de atrás en Action Bar
    //
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @SuppressLint({"ClickableViewAccessibility", "WrongViewCast", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_tarea);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(null);

        findViews();
        presenter = new DetalleTareaPresenter(this, this);

        layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        listaFotos.setLayoutManager(layoutManager);

        //Carga inicial de datos
        presenter.manageIntent(getIntent());
        txtCreadaPor.setText("Tarea creada por: " + presenter.getCreadaPor());
        presenter.getInfoPedido();

        myPictures = new ArrayList<>();

        listaFotos.setScrollContainer(false);
        listaFotos.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        listaFotos.setHasFixedSize(true);
        listaFotos.setItemViewCacheSize(20);
        listaFotos.setDrawingCacheEnabled(true);
        listaFotos.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        presenter.cargarFotosLocales();

        cardDocumentacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.abrirDocumentacion();
            }
        });

        cardFotosPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(cardFotosPedido, "En breves estará disponible", Snackbar.LENGTH_SHORT).show();
                presenter.abrirFotosPedido();
            }
        });

        cardLineasPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.abrirLineasPedido();
            }
        });

        //----------Pedimos permisos GPS ----------\\
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
    }

    public void findViews(){
        cardDocumentacion = findViewById(R.id.cardDocumentacion);
        cardFotosPedido = findViewById(R.id.cardFotosPedido);
        enviar = findViewById(R.id.btnEnviar);
        traspasar = findViewById(R.id.btnTraspasar);

        txtCreadaPor = findViewById(R.id.txtCreadaPor);
        txtCodPedido = findViewById(R.id.numPedidoDT);
        txtDescPedido = findViewById(R.id.descPedidoDT);
        txtFechaPedido = findViewById(R.id.fechaPedidoDT);
        txtMarcoPedido = findViewById(R.id.marcoPedidoDT);
        txtLocalidadPedido = findViewById(R.id.localidadPedidoDT);
        cardLineasPedido = findViewById(R.id.cardLineasPedido);
        listaFotos = findViewById(R.id.listaFotos);

        abrirCamara = findViewById(R.id.btnAñadir);

        scrollView = findViewById(R.id.scrollTarea);

        checkFinalizar = findViewById(R.id.checkFinalizar);
    }

    public void setText(String clave, String valor){
        switch (clave){
            case "codPedido":
                txtCodPedido.setText(valor);
                break;
            case "descPedido":
                txtDescPedido.setText(valor);
                break;
            case "fechaPedido":
                txtFechaPedido.setText(valor);
                break;
            case "fechaFinMeco":
                txtMarcoPedido.setText(valor);
                break;
            case "localidad":
                txtLocalidadPedido.setText(valor);
                break;
        }
    }

    public void setAdapter(ArrayList<Foto> myPictures){
        listaFotosAdapter = new AdapterFotosTarea(this, myPictures,this);
        listaFotosAdapter.notifyDataSetChanged();
        listaFotos.setAdapter(listaFotosAdapter);
    }

    public void habilitarEnviar(){
        enviar.setEnabled(true);
        enviar.setClickable(true);
    }

    public void deshabilitarEnviar(){
        enviar.setEnabled(false);
        enviar.setClickable(false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    public void añadirFotoDesdeGaleria(View view) {
        presenter.añadirFotoDesdeGaleria();
    }

    public void mostrarOpcionesFoto(int pos){
        presenter.mostrarOpcionesFoto(pos);
    }

    public void eliminarTarea(){
        AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
        alertdialogobuilder
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que quieres eliminar la tarea?\nSe borrará la tarea y no aparecerá en tu listado.")
                .setCancelable(true)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        presenter.borrarTarea();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        alertdialogobuilder.show();
    }

    public void enviarInforme(View v) {
        deshabilitarEnviar();
        if (checkFinalizar.isChecked()) {
            AlertDialog.Builder alertdialogobuilder = new AlertDialog.Builder(this);
            alertdialogobuilder
                    .setTitle("Finalizar tarea")
                    .setMessage("¿Seguro que quieres finalizar la tarea?\nSe enviarán todas las fotos y se borrará la tarea.")
                    .setCancelable(true)
                    .setPositiveButton("Enviar y Terminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            try {
                                enviarYTerminar(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            alertdialogobuilder.show();
        } else {
            try {
                enviarYTerminar(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void enviarYTerminar(boolean terminar) throws IOException {
        presenter.enviarYTerminar(terminar);
    }

    public void traspasar() {
        presenter.traspasar();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
