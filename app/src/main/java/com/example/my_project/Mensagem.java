package com.example.my_project;

import android.net.Uri;

import java.util.Date;

class Mensagem implements Comparable<Mensagem> {

    @Override
    public int compareTo(Mensagem mensagem) {
        return this.data.compareTo(mensagem.data);
    }

    private String texto;
    private Date data;

    public Mensagem (Date data, String texto) {
        this.texto = texto;
        this.data = data;
    }

    public Mensagem(){

    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

}
