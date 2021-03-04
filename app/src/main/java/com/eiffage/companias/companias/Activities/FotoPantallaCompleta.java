package com.eiffage.companias.companias.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.transition.Explode;
import android.view.Window;
import android.view.WindowManager;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.eiffage.companias.companias.Objetos.TouchImageView;
import com.eiffage.companias.R;

public class FotoPantallaCompleta extends AppCompatActivity {

    TouchImageView fotoPantallaCompleta;

    //
    //      Método para usar flecha de atrás en Action Bar
    //
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_foto_pantalla_completa);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(null);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setExitTransition(new Explode());
        fotoPantallaCompleta = findViewById(R.id.fotoPantallaCompleta);

        Intent i = getIntent();
        String url = i.getStringExtra("urlImagen");
        String token = i.getStringExtra("token");
        String rutaLocal = i.getStringExtra("rutaLocal");

        if(rutaLocal != null){
            Bitmap myBitmap = BitmapFactory.decodeFile(rutaLocal);
            fotoPantallaCompleta.setImageBitmap(myBitmap);
        }
        else {
            if(token.equals("-")){
                Glide.with(this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        fotoPantallaCompleta.setImageBitmap(resource);
                    }
                });
            }
            else {
                GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build());

                Glide.with(this).asBitmap().load(glideUrl).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        fotoPantallaCompleta.setImageBitmap(resource);
                    }
                });
            }
        }
    }
}
