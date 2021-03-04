package com.eiffage.companias.companias.Objetos;

public class Averia {

    private String cod_recurso, cod_averia, descripcion, fecha, observaciones, gestor, localidad;

    public Averia(String cod_recurso, String cod_averia, String descripcion, String gestor, String fecha, String observaciones, String localidad) {
        this.cod_averia = cod_averia;
        this.descripcion = descripcion;
        this.gestor = gestor;
        this.cod_recurso = cod_recurso;
        this.fecha = fecha;
        this.observaciones = observaciones;
        this.localidad = localidad;
    }

    public String getCod_averia() {
        return cod_averia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha(){
        return fecha;
    }

    public String getObservaciones() { return observaciones; }

    public String getGestor() {return gestor; }

    public String getCod_recurso() { return cod_recurso; }

    public String getLocalidad() { return localidad; }
}
