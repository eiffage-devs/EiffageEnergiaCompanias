package com.eiffage.companias.companias.Adapters;

import android.os.Parcel;
import android.os.Parcelable;

public class Usuario implements Parcelable{

    private String token, email, empresa, nombre, delegacion, cod_recurso;

    public Usuario(String token, String email, String empresa, String nombre, String delegacion, String cod_recurso){
        this.token = token;
        this.email = email;
        this.empresa = empresa;
        this.nombre = nombre;
        this.delegacion = delegacion;
        this.cod_recurso = cod_recurso;
    }

    protected Usuario(Parcel in) {
        token = in.readString();
        email = in.readString();
        empresa = in.readString();
        nombre = in.readString();
        delegacion = in.readString();
        cod_recurso = in.readString();
    }

    public static final Creator<Usuario> CREATOR = new Creator<Usuario>() {
        @Override
        public Usuario createFromParcel(Parcel in) {
            return new Usuario(in);
        }

        @Override
        public Usuario[] newArray(int size) {
            return new Usuario[size];
        }
    };

    public String getToken() {
        return token;
    }

    public String getEmail(){
        return email;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDelegacion() {
        return delegacion;
    }

    public String getCod_recurso() {
        return cod_recurso;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(token);
        parcel.writeString(email);
        parcel.writeString(empresa);
        parcel.writeString(nombre);
        parcel.writeString(delegacion);
        parcel.writeString(cod_recurso);
    }
}
