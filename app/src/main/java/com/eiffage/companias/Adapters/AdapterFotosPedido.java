package com.eiffage.companias.Adapters;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eiffage.companias.Activities.DetalleTarea.DetalleTareaView;
import com.eiffage.companias.Activities.FotoPantallaCompleta;
import com.eiffage.companias.Activities.FotosPedido.FotosPedidoView;
import com.eiffage.companias.Global.ImageHelper;
import com.eiffage.companias.Objetos.Foto;
import com.eiffage.companias.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class AdapterFotosPedido extends RecyclerView.Adapter<AdapterFotosPedido.MyViewHolder> {

    ArrayList<String> rutasLocales;
    Context context;
    Activity activity;
    private ImageHelper imageHelper;

    public AdapterFotosPedido(Context context, ArrayList<String> rutasLocales, Activity activity) {
        this.rutasLocales = rutasLocales;
        this.context = context;
        this.activity = activity;

        imageHelper = new ImageHelper(context);
        Log.d("CUANTASFOTOS", rutasLocales.size() + "");
    }

    @NonNull
    @Override
    public AdapterFotosPedido.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.foto_pedido, null, false);
        return new AdapterFotosPedido.MyViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFotosPedido.MyViewHolder myViewHolder, int i) {
        if(rutasLocales.size() > 0){
            myViewHolder.asignarValores(rutasLocales.get(i));
        }
        /*try{

        }
        catch (IndexOutOfBoundsException e){
            //myViewHolder.asignarValores(null);
        }*/
    }

    @Override
    public int getItemCount() {
        try {
            return rutasLocales.size();
        }
        catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView foto;

        public MyViewHolder(@NonNull final View itemView, final Activity act) {
            super(itemView);
            foto = itemView.findViewById(R.id.img);

            foto.setClickable(true);
            foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FotoPantallaCompleta.class);
                    intent.putExtra("rutaLocal", rutasLocales.get(getAdapterPosition()));
                    ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(activity, foto, "imagen");
                    context.startActivity(intent, options.toBundle());
                }
            });
        }

        public void asignarValores(final String actual) {
            Log.d("Ruta actual " + getAdapterPosition(), actual);
            Bitmap nuevaFoto;
            nuevaFoto = BitmapFactory.decodeFile(actual);
            nuevaFoto = imageHelper.getResizedBitmap(nuevaFoto, 400);
            foto.setImageBitmap(nuevaFoto);
        }
    }
}