package com.eiffage.companias.companias.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.eiffage.companias.companias.Objetos.Linea;
import com.eiffage.companias.R;

import java.util.ArrayList;

public class LineaAdapter extends ArrayAdapter<Linea> implements Filterable {

    private final Context context;
    private ArrayList<Linea> values;

    private ArrayList<Linea> valuesFiltrados;
    public LineaAdapter(Context context, ArrayList<Linea> values){
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    public class LineaHolder {
        TextView codProducto;
        TextView cant;
        TextView descProducto;
        TextView unMedida;
    }

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Linea> results = new ArrayList<Linea>();
                if (valuesFiltrados == null)
                    valuesFiltrados = values;
                if (constraint != null) {
                    if (valuesFiltrados != null && valuesFiltrados.size() > 0) {
                        for (final Linea g : valuesFiltrados) {
                            if (g.getDescProducto().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                values = (ArrayList<Linea>) results.values;
                notifyDataSetChanged();
            }
        };
    }


    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Linea getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_list_item_lineas_pedido, parent, false);

        Linea l = values.get(position);

        TextView codProducto = rowView.findViewById(R.id.txtCodProductoLinea);
        codProducto.setText(l.getCodProducto());

        TextView cant = rowView.findViewById(R.id.txtCantidadLinea);
        cant.setText(l.getCant());

        TextView descProducto = rowView.findViewById(R.id.txtDescLinea);
        descProducto.setText(l.getDescProducto());

        TextView unMedida = rowView.findViewById(R.id.txtUnidadLinea);
        unMedida.setText(l.getUnMedida());



        return rowView;
    }
}
