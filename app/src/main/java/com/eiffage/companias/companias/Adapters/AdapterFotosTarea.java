package com.eiffage.companias.companias.Adapters;

import android.app.Activity;
import android.content.Context;
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

import com.eiffage.companias.companias.Activities.DetalleTarea.DetalleTareaView;
import com.eiffage.companias.companias.Global.ImageHelper;
import com.eiffage.companias.companias.Objetos.Foto;
import com.eiffage.companias.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class AdapterFotosTarea extends RecyclerView.Adapter<AdapterFotosTarea.MyViewHolder> {

    ArrayList<Foto> myPictures;
    Context context;
    Activity activity;

    private ImageHelper imageHelper;

    public AdapterFotosTarea(Context context, ArrayList<Foto> myPictures, Activity activity) {
        this.myPictures = myPictures;
        this.context = context;
        this.activity = activity;

        imageHelper = new ImageHelper(context);

        Log.d("CUANTASFOTOS", myPictures.size() + "");
    }

    @NonNull
    @Override
    public AdapterFotosTarea.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_list_item_photo, null, false);
        return new AdapterFotosTarea.MyViewHolder(v, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFotosTarea.MyViewHolder myViewHolder, int i) {
        if(myPictures.size() > 0){
            myViewHolder.asignarValores(myPictures.get(i));
        }
        /*try{

        }
        catch (IndexOutOfBoundsException e){
            //myViewHolder.asignarValores(null);
        }*/
    }

    @Override
    public int getItemCount() {
        return myPictures.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private boolean desplegado = false;

        private MaterialCardView cardImagen, cardDatos;
        private ImageView foto, imgFlecha;
        TextView txtCategoria, txtSubcategoria, txtDesc;

        public MyViewHolder(@NonNull final View itemView, final Activity act) {
            super(itemView);
            foto = itemView.findViewById(R.id.imagenFoto);
            cardImagen = itemView.findViewById(R.id.cardImagen);
            cardDatos = itemView.findViewById(R.id.cardDatos);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtSubcategoria = itemView.findViewById(R.id.txtSubcategoria);
            txtDesc = itemView.findViewById(R.id.txtDesc);
            imgFlecha = itemView.findViewById(R.id.imgFlecha);
            cardImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Me estoy enterando", "Desplegado es: " + desplegado);
                    ((DetalleTareaView)act).mostrarOpcionesFoto(getAdapterPosition());
                }
            });

            cardDatos.setClickable(true);
            cardDatos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!desplegado){
                        txtCategoria.setVisibility(View.VISIBLE);
                        txtSubcategoria.setVisibility(View.VISIBLE);
                        txtDesc.setVisibility(View.VISIBLE);
                        cambiarIcono(imgFlecha, 0, 180);
                        desplegado = true;
                    }
                    else {
                        txtCategoria.setVisibility(View.GONE);
                        txtSubcategoria.setVisibility(View.GONE);
                        txtDesc.setVisibility(View.GONE);
                        cambiarIcono(imgFlecha, 180, 0);
                        desplegado = false;
                    }
                }
            });
        }

        public void asignarValores(Foto actual) {
            Bitmap nuevaFoto;
            String path = actual.getUrlFoto();
            nuevaFoto = BitmapFactory.decodeFile(path);
            foto.setImageBitmap(nuevaFoto);

            Log.d("DATOS", actual.getCategoria() + ", " + actual.getSubcategoria() + ", " + actual.getDescripcion());
            txtCategoria.setText("Categoría:\n\t" + actual.getCategoria());
            txtSubcategoria.setText("Subcategoría:\n\t" + actual.getSubcategoria());
            txtDesc.setText("Descripción:\n\t" + actual.getDescripcion());
        }

        public void cambiarIcono(ImageView imageView, int from, int to){
            RotateAnimation rotate = new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(150);
            rotate.setInterpolator(new LinearInterpolator());
            rotate.setFillAfter(true);
            imageView.startAnimation(rotate);
        }
    }
}