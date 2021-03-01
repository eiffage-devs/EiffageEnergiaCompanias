package com.eiffage.companias.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.eiffage.companias.Objetos.Documento;
import com.eiffage.companias.R;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<Documento> {

    Context context;
    ArrayList<Documento> values;
    String token;

    public GridAdapter(@NonNull Context context, ArrayList<Documento> values, String token) {
        super(context, 0, values);
        this.context = context;
        this.values = values;
        this.token = token;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Documento actual = values.get(position);
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_grid_item, parent, false);
        }

        ImageView img = convertView.findViewById(R.id.itemImage);

        GlideUrl glideUrl = new GlideUrl(actual.getUrl(), new LazyHeaders.Builder()
                .addHeader("Authorization", "Bearer " + token)
                .build());

        try{
            Glide.with(context).load(glideUrl).into(img);
        }catch (Exception e){
            parent.removeViewInLayout(convertView);
            this.notifyDataSetChanged();
        }

        if(img.getDrawable() == null){
            parent.removeViewInLayout(convertView);
            this.notifyDataSetChanged();
        }



        Log.d("RECIBO FOTO", "La foto es la nº " + position);
        return convertView;
    }
}
