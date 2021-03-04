package com.eiffage.companias.companias.Objetos;

public class Documento {

    private String cod_pedido, categoria, rutaLocal, nombreQueSeMuestra, url;

    public Documento(String cod_pedido, String categoria, String rutaLocal, String url, String nombreQueSeMuestra){
        this.cod_pedido = cod_pedido;
        this.categoria = categoria;
        this.rutaLocal = rutaLocal;
        this.url = url;
        this.nombreQueSeMuestra = nombreQueSeMuestra;
    }

    public String getCod_pedido() {
        return cod_pedido;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getNombreFichero() {
        return nombreQueSeMuestra;
    }

    public String getUrl(){
        return url;
    }

    public void setNombreFichero(String nombreFichero){
        this.nombreQueSeMuestra = nombreFichero;
    }

    public String getRutaLocal(){
        return rutaLocal;
    }
}
