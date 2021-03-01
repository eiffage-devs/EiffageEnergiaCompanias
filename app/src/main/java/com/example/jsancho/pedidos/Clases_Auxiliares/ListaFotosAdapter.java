package com.eiffage.companias.companias.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.eiffage.companias.companias.R;

import java.util.ArrayList;

public class ListaFotosAdapter extends ArrayAdapter<Bitmap> {

    private final Context context;
    private ArrayList<Bitmap> values;

    public ListaFotosAdapter(Context context, ArrayList<Bitmap> values){
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item_photo, parent, false);

        //----------Identificamos los elementos de cada item de la list----------\\
        ImageView miFoto =  rowView.findViewById(R.id.imagenItem);
        miFoto.setImageBitmap(values.get(position));
        return rowView;
    }
}
