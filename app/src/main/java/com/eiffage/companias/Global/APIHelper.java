package com.eiffage.companias.Global;

import com.eiffage.companias.R;

public class APIHelper {

    public String usuarioApp;
    public String passwordApp;

    public static final String URL_ENVIAR_FOTO = "https://inet.energia.eiffage.es:8002/api_iberdrola/creaIBE/fotos_IB";
    public static final String URL_BORRAR_TAREA = "https://inet.energia.eiffage.es:8002/api_iberdrola/finTareaIBE/tarea";
    public static final String URL_ENVIAR_FOTO_IIS = "https://serviciontg.energia.eiffage.es/api/codeunits/wsiberdrola/InsertarFoto";
    public static final String URL_TRASPASAR_TAREA = "https://serviciontg.energia.eiffage.es/api/codeunits/wsiberdrola/TraspasarTarea";
    public static final String URL_RUTAS_FOTOS = "https://serviciontg.energia.eiffage.es/api/pages/FotosIberdrola/ObtenerRutasFotos";
    public static final String URL_DESCARGAR_FOTO = "https://serviciontg.energia.eiffage.es/api/GestorDocumental/GetFile";

    public APIHelper(){
        usuarioApp = "eiffComp";
        passwordApp = "ER2NkhxjK7x2ZvJC";
    }
}
