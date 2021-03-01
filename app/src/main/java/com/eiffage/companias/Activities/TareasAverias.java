package com.eiffage.companias.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.eiffage.companias.R;

public class TareasAverias extends AppCompatActivity {

    String codAveria;
    TextView av;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas_averias);

        Intent i = getIntent();
        codAveria = i.getStringExtra("codAveria");

        av = findViewById(R.id.codigoAveria);
        av.setText("AVERÍA " + codAveria);

    }

    public void crearTarea(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nueva tarea");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
        input.setHint("Descripción");

        builder.setView(input);

        builder.setPositiveButton("Crear avería", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equals("")){
                    Toast.makeText(TareasAverias.this, "Es necesaria una descripción de tarea", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(TareasAverias.this, "Se ha creado la tarea \n'" + input.getText().toString() + "'", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setCancelable(true);

        builder.show();
    }
}
