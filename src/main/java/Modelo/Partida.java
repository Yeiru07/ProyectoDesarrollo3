package Modelo;

import java.util.ArrayList;

public class Partida {//singleton

    private ArrayList<Usuario> arrayDeUsuarios;
    private ArrayList<Sala> arrayDeSalas;
    private ArrayList<Preguntas> listaPreguntas = new ArrayList<>();


    public Partida() {
        this.arrayDeUsuarios = new ArrayList<>();
        this.arrayDeSalas = new ArrayList<>();
    }

    public ArrayList<Usuario> getArrayDeUsuarios() {
        return arrayDeUsuarios;
    }

    public void setArrayDeUsuarios(ArrayList<Usuario> arrayDeUsuarios) {
        this.arrayDeUsuarios = arrayDeUsuarios;
    }

    public ArrayList<Sala> getArrayDeSalas() {
        return arrayDeSalas;
    }

    public void setArrayDeSalas(ArrayList<Sala> arrayDeSalas) {
        this.arrayDeSalas = arrayDeSalas;
    }

    public ArrayList<Preguntas> getListaPreguntas() {
        return listaPreguntas;
    }

    public void setListaPreguntas(ArrayList<Preguntas> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }

}
