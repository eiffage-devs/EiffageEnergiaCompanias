package com.eiffage.companias.companias.Adapters;

public class Pedido {

    private String pedido, desc_pedido, desc_tarea;
    public Pedido(String pedido, String desc_pedido){
        this.pedido = pedido;
        this.desc_pedido = desc_pedido;
    }

    public String getPedido() {
        return pedido;
    }

    public String getDesc_pedido() {
        return desc_pedido;
    }
}
