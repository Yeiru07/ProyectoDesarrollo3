package Modelo;

import java.util.ArrayList;


public class Partida {//singleton
    private ArrayList<Usuario> arrayDeUsuarios;
    private ArrayList<Sala> arrayDeSalas;

    public Partida(ArrayList<Usuario> arrayDeUsuarios, ArrayList<Sala> arrayDeSalas) {
        this.arrayDeUsuarios = arrayDeUsuarios;
        this.arrayDeSalas = arrayDeSalas;
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
    

}
