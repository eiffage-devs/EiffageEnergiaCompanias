package com.eiffage.companias.companias.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eiffage.companias.companias.Objetos.Pedido;
import com.eiffage.companias.companias.Objetos.Tarea;
import com.eiffage.companias.R;

import java.util.ArrayList;

public class TareasTerminadasAdapter  extends ArrayAdapter<Tarea> {


    private final Context context;
    private ArrayList<Tarea> values;
    private ArrayList<Pedido> pedidos;

    public TareasTerminadasAdapter(Context context, ArrayList<Tarea> values, ArrayList<Pedido> pedidos){

        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.pedidos = pedidos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item, parent, false);


        Tarea t = values.get(position);
        String descripcionTarea = t.getDescripcion();
        String descripcionPedido = "-";
        String codigoPedido = t.getCod_pedido();

        boolean encontrado = false;
        int i = 0;
        while(!encontrado && i < pedidos.size()){
            if(pedidos.get(i).getCodigo().equals(values.get(position).getCod_pedido())){
                encontrado = true;
                descripcionPedido = pedidos.get(i).getDescripcion();
            }
            else {
                i++;
            }
        }

        //----------Identificamos los elementos de cada item de la list----------\\
        TextView numPedido =  rowView.findViewById(R.id.misTareasnumPedido);
        TextView descPedido = rowView.findViewById(R.id.misTareasDescPedido);
        TextView descTarea = rowView.findViewById(R.id.misTareasDescTarea);

        numPedido.setText(codigoPedido);
        descPedido.setText(descripcionPedido);
        descTarea.setText(descripcionTarea);

        return rowView;
    }

}
