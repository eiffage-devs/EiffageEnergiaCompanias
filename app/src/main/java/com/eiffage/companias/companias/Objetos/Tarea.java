package com.eiffage.companias.companias.Objetos;

public class Tarea {

    private String cod_tarea, descripcion, cod_recurso, cod_pedido, cargoRecurso, nombreRecurso;

    public Tarea(String cod_tarea, String descripcion, String cod_recurso, String cod_pedido, String cargoRecurso, String nombreRecurso){
        this.cod_tarea = cod_tarea;
        this.descripcion = descripcion;
        this.cod_recurso = cod_recurso;
        this.cod_pedido = cod_pedido;
        this.cargoRecurso = cargoRecurso;
        this.nombreRecurso = nombreRecurso;
    }

    public String getCod_tarea() {
        return cod_tarea;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCod_recurso() {
        return cod_recurso;
    }

    public String getCod_pedido() {
        return cod_pedido;
    }

    public String getCargoRecurso() {
        return cargoRecurso;
    }

    public String getNombreRecurso() {
        return nombreRecurso;
    }
}
