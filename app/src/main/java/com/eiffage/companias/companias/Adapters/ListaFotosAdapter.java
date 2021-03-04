package com.eiffage.companias.companias.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eiffage.companias.companias.Activities.FotoPantallaCompleta;
import com.eiffage.companias.companias.Activities.ModificarDatosFoto.ModificarDatosFotoView;
import com.eiffage.companias.companias.Objetos.Foto;
import com.eiffage.companias.R;

import java.util.ArrayList;

public class ListaFotosAdapter extends ArrayAdapter<Foto> {

    //Del constructor...
    private final Context context;
    private ArrayList<Foto> values;

    //Views de cada row...
    private Button borrar, editar;

    //Objeto Foto
    private String imagen = "-";
    private String descFoto = "-";
    private String categoria="-";
    private String subcategoria = "-";


    public ListaFotosAdapter(Context context, ArrayList<Foto> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }



    public View getView(final int position, View convertView, final ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item_photo, parent, false);

        //----------Identificamos los elementos de cada item de la list----------\\

        ImageView miFoto = rowView.findViewById(R.id.imagenFoto);

        Glide.with(context)
                .load(values.get(position).getUrlFoto()) // Uri of the picture
                .into(miFoto);

        borrar = rowView.findViewById(R.id.btnBorrar);
        editar = rowView.findViewById(R.id.btnEditar);

        final TextView categoria = rowView.findViewById(R.id.txtCategoria);
        final TextView subcategoria = rowView.findViewById(R.id.txtSubcategoria);
        final TextView desc = rowView.findViewById(R.id.txtdesc);

        categoria.setText("Área: " + values.get(position).getCategoria());
        subcategoria.setText("Subárea: " + values.get(position).getSubcategoria());
        desc.setText("Descripción: " + values.get(position).getDescripcion());


        //----------Funcionalidad botones borrar y descripción----------\\

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(getItem(position));
                notifyDataSetChanged();
            }
        });

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("PATH ADAPTER", String.valueOf(values.get(position).getUrlFoto()));

                Intent intent = new Intent(context, ModificarDatosFotoView.class);
                intent.putExtra("esNuevo", "NO");
                intent.putExtra("foto", values.get(position).getUrlFoto());
                intent.putExtra("categoria", categoria.getText().toString());
                intent.putExtra("subcategoria", subcategoria.getText().toString());
                intent.putExtra("desc", desc.getText().toString());
                intent.putExtra("posicion", position);
                ((Activity) context).startActivityForResult(intent, 2);

            }
        });

        miFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(context, FotoPantallaCompleta.class);
                i.putExtra("token", "-");
                i.putExtra("urlImagen", values.get(position).getUrlFoto());
                context.startActivity(i);
            }
        });

        return rowView;

    }

}
