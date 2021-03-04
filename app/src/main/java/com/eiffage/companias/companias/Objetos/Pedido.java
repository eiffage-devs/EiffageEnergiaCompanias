package com.eiffage.companias.companias.Objetos;

public class Pedido {

    private String codigo, descripcion, fecha, marco, coordenadas, localidad, empresa;
    public Pedido(String codigo, String descripcion, String fecha, String marco, String coordenadas, String localidad,
                  String empresa){
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.marco = marco;
        this.coordenadas = coordenadas;
        this.localidad = localidad;
        this.empresa = empresa;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public String getMarco() {
        return marco;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getEmpresa() {
        return empresa;
    }
}
