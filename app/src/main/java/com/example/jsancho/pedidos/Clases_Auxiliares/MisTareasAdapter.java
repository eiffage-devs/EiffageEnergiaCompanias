package com.eiffage.companias.companias.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eiffage.companias.companias.R;

import java.util.ArrayList;

public class MisTareasAdapter extends ArrayAdapter<Tarea>{

    private final Context context;
    private ArrayList<Tarea> values;

    public MisTareasAdapter(Context context, ArrayList<Tarea> values){

        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item, parent, false);

        //----------Identificamos los elementos de cada item de la list----------\\
        TextView numPedido =  rowView.findViewById(R.id.misTareasnumPedido);
        TextView descPedido = rowView.findViewById(R.id.misTareasDescPedido);
        TextView descTarea = rowView.findViewById(R.id.misTareasDescTarea);

        numPedido.setText(values.get(position).getPedido());
        descPedido.setText(values.get(position).getDesc_pedido());
        descTarea.setText(values.get(position).getDesc_tarea());

        return rowView;
    }
}
