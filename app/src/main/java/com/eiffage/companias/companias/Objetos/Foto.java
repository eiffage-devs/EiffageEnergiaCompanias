package com.eiffage.companias.companias.Objetos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Foto {

    private String descripcion;
    private String categoria, subcategoria;
    private String fecha, hora;
    private String coordenadasPedido;
    private String coordenadasFoto;
    private String urlFoto;
    String idTarea;
    private String id;

    public Foto(String descripcion, String categoria, String subcategoria, String fecha, String hora, String coordenadasFoto, String idTarea, String urlFoto){
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.fecha = fecha;
        this.hora = hora;
        this.coordenadasFoto = coordenadasFoto;
        this.idTarea = idTarea;
        this.urlFoto = urlFoto;

    }

    public Foto(int id, String categoria, String subcategoria, String fecha, String hora, String coordenadasFoto, String idTarea, String descripcion, String urlFoto){
        this.id = String.valueOf(id);
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.fecha = fecha;
        this.hora = hora;
        this.coordenadasFoto = coordenadasFoto;
        this.idTarea = idTarea;
        this.urlFoto = urlFoto;

    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getCoordenadasFoto() {
        return coordenadasFoto;
    }

    public String getCoordenadasPedido() {
        return coordenadasPedido;
    }

    public String getIdTarea() {
        return idTarea;
    }

    public String getId() { return id;}

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void setCoordenadasFoto(String coordenadasFoto) {
        this.coordenadasFoto = coordenadasFoto;
    }

    public void setCoordenadasPedido(String coordenadasPedido) {
        this.coordenadasPedido = coordenadasPedido;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    public void setUrlFoto(String urlFoto) {
        this.urlFoto = urlFoto;
    }

    public void setId(String id){this.id = id;}

    @Override
    public String toString() {
        String s =
                        this.getDescripcion() + "," +
                        this.getCategoria() + "," +
                        this.getSubcategoria() + "," +
                        this.getFecha() + "," +
                        this.getHora() + "," +
                        this.getCoordenadasFoto() + "," +
                        this.getId() + "," +
                this.getUrlFoto();
        return s;
    }


    public String toJSON(String cod_pedido){

        JSONObject jsonObject= new JSONObject();
        try {
            byte[] bytarray = Base64.decode(String.valueOf(this.getUrlFoto()), Base64.DEFAULT);
            Bitmap imagen = BitmapFactory.decodeByteArray(bytarray, 0,
                    bytarray.length);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imagen.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encodedImage = "holapaco, " + Base64.encodeToString(byteArray, Base64.DEFAULT);

            jsonObject.put("num_pedido", cod_pedido);
            jsonObject.put("num_tarea", idTarea);
            jsonObject.put("descripcion", getDescripcion());
            jsonObject.put("area", getCategoria());
            jsonObject.put("subarea", getSubcategoria());
            jsonObject.put("coordenadas", getCoordenadasFoto());
            jsonObject.put("fecha", getFecha() + ", " + getHora());
            jsonObject.put("foto", encodedImage);
            Log.d("JSON A ENVIAR", jsonObject.toString());
            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }

}
