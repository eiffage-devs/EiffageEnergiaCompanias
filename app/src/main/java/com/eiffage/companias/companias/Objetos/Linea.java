package com.eiffage.companias.companias.Objetos;

public class Linea {

    String empresa, numLinea, codProducto, cant, descProducto, pedido, unMedida;

    public Linea(String empresa, String numLinea, String codProducto, String cant, String descProducto,
                 String pedido, String unMedida){
        this.empresa = empresa;
        this.numLinea = numLinea;
        this.codProducto = codProducto;
        this.cant = cant;
        this.descProducto = descProducto;
        this.pedido = pedido;
        this.unMedida = unMedida;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getNumLinea() {
        return numLinea;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public String getPedido() {
        return pedido;
    }

    public String getCant() {
        return cant;
    }

    public String getDescProducto() {
        return descProducto;
    }

    public String getUnMedida() {
        return unMedida;
    }
}
