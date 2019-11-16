package com.example.my_project;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Date;

class Foto implements Comparable<Foto>{

    @Override
    public int compareTo(Foto foto) {
        return this.data.compareTo(foto.data);
    }

    private Bitmap picture;
    private Date data;
    private String encodedFoto;

    public Foto (Date data, Bitmap picture) {
        this.picture = picture;
        this.data = data;
    }

    public Foto(Date data, String encodedFoto){
        this.encodedFoto = encodedFoto;
        this.data = data;
    }

    public Foto(){
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public Date getData() { return data; }

    public void setData(Date data) {
        this.data = data;
    }

    public String getEncodedFoto() { return encodedFoto; }

    public void setEncodedFoto(String encodedFoto) {this.encodedFoto = encodedFoto;}

}
