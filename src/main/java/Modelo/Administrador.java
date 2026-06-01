package Modelo;

import java.util.ArrayList;

public class Administrador implements Interfaces.ItipoUsuario {

    private String nombreDelAdministrador;
private ArrayList<Sala> arregloDeSalas;

    public Administrador(String nombreDelAdministrador, ArrayList<Sala> arregloDeSalas) {
        this.nombreDelAdministrador = nombreDelAdministrador;
        this.arregloDeSalas = arregloDeSalas;
    }

   

    @Override
    public String getTipo() {
        return "Administrador";
    }

}
