package com.eiffage.companias.companias.Adapters;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.eiffage.companias.companias.Activities.Documentacion;
import com.eiffage.companias.companias.DB.MySqliteOpenHelper;
import com.eiffage.companias.companias.Objetos.Documento;
import com.eiffage.companias.R;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ListaEditablesAdapter extends ArrayAdapter<Documento> {

    private final Context context;
    private ArrayList<Documento> values;
    SQLiteDatabase db;
    MySqliteOpenHelper mySqliteOpenHelper;

    String origen, destino;

    public ListaEditablesAdapter(Context context, ArrayList<Documento> values){

        super(context, -1, values);
        this.context = context;
        this.values = values;

        mySqliteOpenHelper = new MySqliteOpenHelper(context);
        db = mySqliteOpenHelper.getWritableDatabase();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_editable_item, parent, false);

        mySqliteOpenHelper = new MySqliteOpenHelper(context);
        db = mySqliteOpenHelper.getReadableDatabase();

        final Documento d = values.get(position);
        String nombreFichero = d.getNombreFichero();

        //----------Identificamos los elementos de cada item de la list----------\\
        TextView nombre_doc =  rowView.findViewById(R.id.nombre_doc);
        Button eliminar_doc = rowView.findViewById(R.id.eliminar_doc);
        try{
            nombre_doc.setText(nombreFichero);
        }
        catch (NullPointerException e){
            Log.d("Nombre fic", nombreFichero);
            Log.d("Textview", nombre_doc.getId() + "");
        }

        nombre_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String sourcePath = context.getFilesDir().getAbsolutePath() + "/" + values.get(position).getRutaLocal() + ".pdf";
                    origen = sourcePath;
                    File source = new File(sourcePath);

                    String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + values.get(position).getNombreFichero();
                    destino = destinationPath;
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
                    String [] parts = values.get(position).getNombreFichero().split(Pattern.quote("."));
                    String ext = parts[1];
                    String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                    intent.setDataAndType(data, mimetype);
                    Intent i = Intent.createChooser(intent, "Elige un lector");
                    ((Activity) context).startActivityForResult(i, 1);
                }

        });

        eliminar_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mySqliteOpenHelper.borrarEditado(db, d.getRutaLocal());

                values.remove(position);
                notifyDataSetChanged();
            }
        });
        return rowView;
    }

    public void gestionFicheroEditado(){

                File source = new File(origen);
                File destination = new File(destino);
                boolean isTwoEqual;
                try {
                    isTwoEqual = FileUtils.contentEquals(source, destination);
                    if(!isTwoEqual){
                        ((Documentacion) context).guardarComo(destino);
                        notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

    }
}
