package com.eiffage.companias.companias;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.eiffage.companias.companias.Adapters.MisPedidosAdapter;
import com.eiffage.companias.companias.Adapters.Pedido;

import java.util.ArrayList;

public class MisPedidos extends AppCompatActivity {

    ArrayList<Pedido> misPedidos;
    ArrayAdapter<Pedido> pedidoArrayAdapter;
    ListView listaPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pedidos);

        misPedidos = new ArrayList<Pedido>();

        misPedidos.add(new Pedido("456456456", "Este es un pedido de prueba"));
        misPedidos.add(new Pedido("292757895", "Este es otro pedido de prueba"));

        pedidoArrayAdapter = new MisPedidosAdapter(getApplicationContext(), misPedidos);

        listaPedidos = findViewById(R.id.listaPedidos);
        listaPedidos.setAdapter(pedidoArrayAdapter);
        listaPedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MisPedidos.this, DetallePedido.class);
                //intent.putExtra("miUsuario", miUsuario);
                startActivity(intent);
            }
        });
    }
}
