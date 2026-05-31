package Modelo;

import java.util.ArrayList;


public class Respuestas {
private int numeroDeRespuesta;
private String respuestas;

    public Respuestas(int numeroDeRespuesta, String respuestas) {
        this.numeroDeRespuesta = numeroDeRespuesta;
        this.respuestas = respuestas;
    }

    public int getNumeroDeRespuesta() {
        return numeroDeRespuesta;
    }

    public void setNumeroDeRespuesta(int numeroDeRespuesta) {
        this.numeroDeRespuesta = numeroDeRespuesta;
    }

    public String getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(String respuestas) {
        this.respuestas = respuestas;
    }



}
