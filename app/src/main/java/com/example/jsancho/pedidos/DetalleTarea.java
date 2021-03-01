package com.eiffage.companias.companias;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.eiffage.companias.companias.Adapters.ListaFotosAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DetalleTarea extends AppCompatActivity {

    Button abrirCamara;
    ArrayList<Bitmap> myPictures;
    ListView listaFotos;
    ListaFotosAdapter listaFotosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_tarea);
        myPictures = new ArrayList<>();
        listaFotos = findViewById(R.id.listaFotos);

        abrirCamara = findViewById(R.id.btnAñadir);
        abrirCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){

                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 0);
                }else{
                    ActivityCompat.requestPermissions(DetalleTarea.this, new String[]{ Manifest.permission.CAMERA}, 0);
                }
            }
        });
    }

    public void informesGuardados(View v) {

    }

    public void descargarDatosDePedido(View v) {

    }

    public void guardarInforme(View v) {

    }

    public void enviarInforme(View v) {

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            try{
                //Guardar imagen en Bitmap.
                Bitmap image = (Bitmap) data.getExtras().get("data");

                //Recuperar nombre de fichero donde irá la imagen.
                SharedPreferences sp = getSharedPreferences("myPrefs", MODE_PRIVATE);
                String i= "" + sp.getInt("i", 0);

                //Guardar imagen en fichero
                guardarFoto(image, i);

                //Guardar el nombre del fichero donde irá la siguiente imagen.
                SharedPreferences.Editor editor = getSharedPreferences("myPrefs", MODE_PRIVATE).edit();
                int nuevoI = Integer.parseInt(i)+1;
                editor.putInt("i", nuevoI);
                editor.apply();

                for(int j=0; j<nuevoI; j++){
                    try {
                        FileInputStream f = openFileInput(""+j);
                        String s = f.toString();
                        Bitmap bitmap = BitmapFactory.decodeFile(s);
                        myPictures.add(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //myPictures.add(image);
                listaFotosAdapter= new ListaFotosAdapter(getApplicationContext(), myPictures);
                listaFotos.setAdapter(listaFotosAdapter);
            }
            catch (NullPointerException e){
                Toast.makeText(this, data.getExtras().get("data").toString(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String guardarFoto(Bitmap bitmapImage, String nameFile){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,nameFile + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private void cargarFoto(String path)
    {

        try {
            File f=new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
}
