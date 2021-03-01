package com.eiffage.companias.companias.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eiffage.companias.companias.R;

import java.util.ArrayList;

public class MisPedidosAdapter extends ArrayAdapter<Pedido> {

    private final Context context;
    private ArrayList<Pedido> values;

    public MisPedidosAdapter(Context context, ArrayList<Pedido> values){

        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item_pedido, parent, false);

        //----------Identificamos los elementos de cada item de la list----------\\
        TextView numPedido =  rowView.findViewById(R.id.misPedidosNumPedido);
        TextView descPedido = rowView.findViewById(R.id.misPedidosDescPedido);

        numPedido.setText(values.get(position).getPedido());
        descPedido.setText(values.get(position).getDesc_pedido());

        return rowView;
    }
}

