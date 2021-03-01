package com.eiffage.companias.Activities.DetalleTarea;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.eiffage.companias.Activities.DelegarTarea;
import com.eiffage.companias.Activities.Documentacion;
import com.eiffage.companias.Activities.FotoPantallaCompleta;
import com.eiffage.companias.Activities.FotosPedido.FotosPedidoView;
import com.eiffage.companias.Activities.LineasPedido;
import com.eiffage.companias.Activities.ModificarDatosFoto.ModificarDatosFotoView;
import com.eiffage.companias.DTO.InfoPedidoDTO;
import com.eiffage.companias.Global.ImageHelper;
import com.eiffage.companias.Objetos.Foto;
import com.eiffage.companias.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class DetalleTareaPresenter {

    private Context context;
    protected DetalleTareaView view;
    private DetalleTareaModel model;
    private ImageHelper imageHelper;

    private String idTarea, codPedido, fecha, hora;

    protected ArrayList<Foto> myPictures;

    public DetalleTareaPresenter(Context context, DetalleTareaView view){
        this.context = context;
        this.view = view;

        model = new DetalleTareaModel(context, this);
        imageHelper = new ImageHelper(context);

        setAdapter(new ArrayList<Foto>());
    }

    public void manageIntent(Intent i){
        idTarea = i.getStringExtra("idTarea");
        codPedido = i.getStringExtra("cod_pedido");
    }

    public String getCreadaPor(){
        return model.getCreadaPor(idTarea);
    }

    public void getInfoPedido(){
        InfoPedidoDTO infoPedidoDTO = model.getInfoPedido(codPedido);

        view.setText("codPedido", infoPedidoDTO.codPedido);
        view.setText("descPedido", infoPedidoDTO.descPedido);
        view.setText("fechaPedido", infoPedidoDTO.fechaPedido);
        view.setText("fechaFinMeco", infoPedidoDTO.fechaFinMeco);
        view.setText("localidad", infoPedidoDTO.localidad);
    }

    public void cargarFotosLocales() {
        model.cargarFotosLocales(idTarea);
    }

    public void setAdapter(ArrayList<Foto> myPictures){
        this.myPictures = myPictures;
        view.setAdapter(myPictures);
    }

    public void guardarInforme(){
        model.guardarInforme(myPictures, idTarea);
    }

    //Abrir otras pantallas
    public void abrirDocumentacion(){
        Intent intent = new Intent(context, Documentacion.class);
        intent.putExtra("cod_pedido", codPedido);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void abrirFotosPedido(){
        Intent intent = new Intent(context, FotosPedidoView.class);
        intent.putExtra("pedido", codPedido);
        context.startActivity(intent);
    }

    public void abrirLineasPedido(){
        Intent i = new Intent(context, LineasPedido.class);
        i.putExtra("cod_pedido", codPedido);
        context.startActivity(i);
    }

    public void añadirFotoDesdeGaleria(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        view.startActivityForResult(intent, 3);
    }

    //OnActivityResult
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1){
            if(resultCode == RESULT_OK){
                String idFoto = data.getStringExtra("idFoto");
                String categ = data.getStringExtra("categoria");
                String subcateg = data.getStringExtra("subcategoria");
                String desc = data.getStringExtra("descripcion");

                model.actualizarValor(idFoto, "categoria", categ);
                model.actualizarValor(idFoto, "subcategoria", subcateg);
                model.actualizarValor(idFoto, "descripcion", desc);
                cargarFotosLocales();
            }
        }
        else if(requestCode == 2){
            if(resultCode == RESULT_OK){
                String categ = data.getStringExtra("categoria");
                String subcateg = data.getStringExtra("subcategoria");
                String desc = data.getStringExtra("descripcion");

                Foto cambio = myPictures.get(data.getIntExtra("posicion",0));
                cambio.setCategoria(categ);
                cambio.setSubcategoria(subcateg);
                cambio.setDescripcion(desc);

                myPictures.set(data.getIntExtra("posicion", 0), cambio);
                view.setAdapter(myPictures);
            }
        }
        else if(requestCode == 3){
            if(resultCode == RESULT_OK){
                Log.d("OK", "OK");
                try{
                    ClipData clip = data.getClipData();
                    for(int i = 0; i < clip.getItemCount(); i++) {
                        ClipData.Item item = clip.getItemAt(i);
                        Uri uri = item.getUri();
                        recogerFechaYHora();
                        String pathCopiaOptimizada = imageHelper.crearCopiaOptimizada(uri);
                        Foto nueva = new Foto("-", "-", "-", fecha, hora, "-", idTarea,
                                pathCopiaOptimizada);
                        myPictures.add(0, nueva);
                    }
                }
                catch (NullPointerException e){
                    Log.d("Fotos", "Solamente ha llegado una foto");
                    final Uri imageUri = data.getData();
                    recogerFechaYHora();
                    String pathCopiaOptimizada = imageHelper.crearCopiaOptimizada(imageUri);
                    Foto nueva = new Foto("-", "-", "-", fecha, hora, "-", idTarea,
                            pathCopiaOptimizada);
                    myPictures.add(0, nueva);
                }

                guardarInforme();
                setAdapter(myPictures);
            }
            else {
                Snackbar.make(view.scrollView, "No se ha seleccionado ninguna foto", Snackbar.LENGTH_LONG).show();
                Log.d("KO", "KO");
            }
        }
    }

    public void enviarYTerminar(boolean terminar){
        model.enviarYTerminar(terminar, idTarea, codPedido);
    }

    public void borrarTarea(){
        model.borrarTarea(idTarea, codPedido);
    }

    public void habilitarEnviar(){
        view.habilitarEnviar();
    }

    public void marcarFotosEnviadas(){
        model.marcarFotosEnviadas(idTarea);
    }

    public void borrarFotos(){
        model.borrarFotos(idTarea);
    }

    public void traspasar(){
        if(myPictures.size() == 0){
            Intent i = new Intent(context, DelegarTarea.class);
            i.putExtra("idTarea", idTarea);
            i.putExtra("cod_pedido", codPedido);
            context.startActivity(i);
        }
        else {
            Toast.makeText(context, "No puedes delegar una tarea con fotos pendientes.\nPor favor, envía o elimina las fotos actuales.", Toast.LENGTH_SHORT).show();
        }
    }

    public void mostrarOpcionesFoto(int pos){
        View dialogView = view.getLayoutInflater().inflate(R.layout.fragment_opciones_foto, null);
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(dialogView);
        dialog.show();
        new OpcionesFoto(context, dialog, pos, this);
    }

    public void editarDatos(int pos){
        Intent i = new Intent(context, ModificarDatosFotoView.class);
        i.putExtra("idFoto", String.valueOf(myPictures.get(pos).getId()));
        view.startActivityForResult(i, 1);
    }

    public void recogerFechaYHora(){
        //----------Fecha y hora de la foto----------\\
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-dd-MM", Locale.getDefault());
        Date date = new Date();
        fecha = dateFormat.format(date);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("CET"));
        String tempHora = "" + calendar.get(Calendar.HOUR_OF_DAY);
        if (tempHora.length() == 1) {
            tempHora = "0" + tempHora;
        }
        String tempMin = "" + calendar.get(Calendar.MINUTE);
        if (tempMin.length() == 1) {
            tempMin = "0" + tempMin;
        }
        String tempSeg = "" + calendar.get(Calendar.SECOND);
        if (tempSeg.length() == 1) {
            tempSeg = "0" + tempSeg;
        }

        hora = tempHora + ":" + tempMin + ":" + tempSeg;
    }

    public void borrarCopiaLocal(int pos){
        File file = new File(myPictures.get(pos).getUrlFoto());
        String idFoto = myPictures.get(pos).getId();
        model.borrarFoto(idFoto);
        Log.d("File deleted", myPictures.get(pos) + " -> " + file.delete());
    }

    public void finish(){
        view.finish();
    }
}

class OpcionesFoto implements View.OnClickListener {

    private Context context;
    MaterialCardView cardVerFoto, cardEditarDatos, cardEliminarFoto;
    DetalleTareaPresenter presenter;
    private int pos;
    private BottomSheetDialog dialog;

    public OpcionesFoto(Context context, BottomSheetDialog dialog, int pos, DetalleTareaPresenter presenter){
        this.context = context;
        this.pos = pos;
        this.presenter = presenter;
        this.dialog = dialog;
        cardVerFoto = dialog.findViewById(R.id.cardVerFoto);
        cardEditarDatos = dialog.findViewById(R.id.cardEditarDatos);
        cardEliminarFoto = dialog.findViewById(R.id.cardEliminarFoto);

        cardVerFoto.setOnClickListener(this);
        cardEditarDatos.setOnClickListener(this);
        cardEliminarFoto.setOnClickListener(this);
    };

    @Override
    public void onClick(View v) {
        if(v.getId() == cardVerFoto.getId()){
            Log.d("Pulsado", "Quiere ver foto a pantalla completa");
            Intent intent = new Intent(context, FotoPantallaCompleta.class);
            intent.putExtra("rutaLocal", presenter.myPictures.get(pos).getUrlFoto());
            context.startActivity(intent);
            dialog.dismiss();
        }
        else if(v.getId() == cardEditarDatos.getId()){
            presenter.editarDatos(pos);
            dialog.dismiss();
        }
        else if(v.getId() == cardEliminarFoto.getId()){
            Log.d("Pulsado", "Quiere eliminar foto");
            presenter.borrarCopiaLocal(pos);
            if(presenter.myPictures.size() == 1)
                presenter.setAdapter(new ArrayList<Foto>());
            else {
                presenter.view.listaFotosAdapter.notifyItemRemoved(pos);
                presenter.myPictures.remove(pos);
            }
            dialog.dismiss();
        }
    }
}