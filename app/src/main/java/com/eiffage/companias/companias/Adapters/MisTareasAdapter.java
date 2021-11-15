package com.eiffage.companias.companias.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.eiffage.companias.companias.DB.MySqliteOpenHelper;
import com.eiffage.companias.companias.Objetos.Tarea;
import com.eiffage.companias.R;

import java.util.ArrayList;

public class MisTareasAdapter extends ArrayAdapter<Tarea>{

    private final Context context;
    private ArrayList<Tarea> values;
    SQLiteDatabase db;
    MySqliteOpenHelper mySqliteOpenHelper;

    public MisTareasAdapter(Context context, ArrayList<Tarea> values){

        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    public void filter(ArrayList<Tarea> tareasFilter){
        values = tareasFilter;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item, parent, false);

        mySqliteOpenHelper = new MySqliteOpenHelper(context);
        db = mySqliteOpenHelper.getReadableDatabase();

        Tarea t = values.get(position);
        String descripcionTarea = t.getDescripcion();
        String descripcionPedido = "-";
        String codigoPedido = t.getCod_pedido();

        Cursor c = db.rawQuery("SELECT descripcion FROM Pedido WHERE codigo LIKE '" + codigoPedido + "'", null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            do {
                descripcionPedido = c.getString(0);
            } while (c.moveToNext());
        }


        //----------Identificamos los elementos de cada item de la list----------\\
        TextView numPedido =  rowView.findViewById(R.id.misTareasnumPedido);
        TextView descPedido = rowView.findViewById(R.id.misTareasDescPedido);
        TextView descTarea = rowView.findViewById(R.id.misTareasDescTarea);
        TextView hayFotos = rowView.findViewById(R.id.txtHayFotos);
        boolean b = hayFotos(values.get(position).getCod_tarea());
        numPedido.setText(codigoPedido);
        descPedido.setText(descripcionPedido);
        descTarea.setText(descripcionTarea);
        if(b){
            hayFotos.setVisibility(View.VISIBLE);
            hayFotos.setTextColor(context.getResources().getColor(android.R.color.holo_orange_light));
            hayFotos.setText("FOTOS PENDIENTES DE ENVIAR");
        }
        else
            hayFotos.setVisibility(View.GONE);
        return rowView;
    }

    public boolean hayFotos(String idTarea){
        Cursor c = mySqliteOpenHelper.recuperarFotos(db, idTarea);
        if(c.getCount() > 0){
            return true;
        }
        return false;
    }
}
