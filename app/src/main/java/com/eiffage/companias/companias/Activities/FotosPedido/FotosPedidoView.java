package com.eiffage.companias.companias.Activities.FotosPedido;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eiffage.companias.companias.Adapters.AdapterFotosPedido;
import com.eiffage.companias.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;

public class FotosPedidoView extends AppCompatActivity {

    private FotosPedidoPresenter presenter;

    private RecyclerView listaFotos;
    public RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private Toolbar toolbar;
    private LinearLayout linearLoading;
    private TextView txtLoading;
    private LinearProgressIndicator progress;

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
        setContentView(R.layout.activity_fotos_pedido);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(null);

        findViews();
        presenter = new FotosPedidoPresenter(this, this);

        layoutManager = new GridLayoutManager(this, 3);
        listaFotos.setLayoutManager(layoutManager);
        presenter.getIntent(getIntent());
        presenter.getRutasFotos();
    }

    public void findViews(){
        linearLoading = findViewById(R.id.linearLoading);
        listaFotos = findViewById(R.id.gridFotos);
        txtLoading = findViewById(R.id.txtLoading);
        progress = findViewById(R.id.progress);
    }

    public void setAdapter(ArrayList<String> rutasLocales){
        if(rutasLocales.size() > 0){
            mAdapter = new AdapterFotosPedido(this, rutasLocales, this);
            listaFotos.setAdapter(mAdapter);
            animarAparicion();
        }
    }

    public void borrarFotosLocales(){
        presenter.borrarFotosPedido();
    }

    public void animarAparicion(){
        AlphaAnimation animation1 = new AlphaAnimation(0f, 1.0f);
        AlphaAnimation animation2 = new AlphaAnimation(1f, 0f);
        animation1.setDuration(2000);
        animation1.setFillAfter(true);
        animation2.setDuration(2000);
        animation2.setFillAfter(true);
        listaFotos.startAnimation(animation1);
        linearLoading.startAnimation(animation2);
    }

    public void setTextAnimacion(int actual, int total){
        if(total == 0){
            txtLoading.setText("No hay fotos");
            progress.setVisibility(View.INVISIBLE);
        }
        else
            txtLoading.setText("Cargando fotos (" + actual + " de " + total + ")");
    }

    public void abrirPantallaCompleta(int pos){
        presenter.abrirPantallaCompleta(pos);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        borrarFotosLocales();
    }
}