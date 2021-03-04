package com.eiffage.companias.companias.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eiffage.companias.companias.Objetos.Averia;
import com.eiffage.companias.R;

import java.util.ArrayList;

public class MisAveriasAdapter extends ArrayAdapter<Averia>{

    private Context context;
    private ArrayList<Averia> values;

    TextView cod_averia, fecha, desc;

    public MisAveriasAdapter(Context context, ArrayList<Averia> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.custom_list_item_averia, parent, false);

        cod_averia = rowView.findViewById(R.id.misAveriasCodAveria);
        fecha = rowView.findViewById(R.id.misAveriasFecha);
        desc = rowView.findViewById(R.id.misAveriasDescAveria);

        Averia actual = values.get(position);

        cod_averia.setText(actual.getCod_averia());
        fecha.setText(actual.getFecha());
        desc.setText(actual.getDescripcion());

        return rowView;
    }
}
