package com.eiffage.companias.companias.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.eiffage.companias.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PDFViewer extends AppCompatActivity {

    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        pdfView = findViewById(R.id.pdfView);

        Intent i = getIntent();
        String ruta = i.getStringExtra("rutaFichero");

        byte [] bytes = leerFichero(ruta);
       //pdfView.fromBytes(bytes).load();
    }

    public byte [] leerFichero(String ruta) {
        //Read file
        File file = new File(ruta);
        pdfView.fromFile(file).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {

            }
        })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        Log.d("Error cargando pdf", t.getMessage());
                    }
                })
                .load();

        int size = (int) file.length();
        byte [] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bytes;
    }
}
