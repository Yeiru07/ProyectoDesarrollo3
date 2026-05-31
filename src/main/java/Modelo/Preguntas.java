package Modelo;

import java.util.ArrayList;

public class Preguntas {

    private String enunciado;
    private ArrayList<Respuestas> arregloDeRespuestasParaPreguntas;

    public Preguntas(String enunciado, ArrayList<Respuestas> arregloDeRespuestasParaPreguntas) {
        this.enunciado = enunciado;
        this.arregloDeRespuestasParaPreguntas = new ArrayList<>();
    }

    public Preguntas() {
        this.enunciado = "";
        this.arregloDeRespuestasParaPreguntas = new ArrayList<>();
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public ArrayList<Respuestas> getArregloDeRespuestasParaPreguntas() {
        return arregloDeRespuestasParaPreguntas;
    }

    public void setArregloDeRespuestasParaPreguntas(ArrayList<Respuestas> arregloDeRespuestasParaPreguntas) {
        this.arregloDeRespuestasParaPreguntas = arregloDeRespuestasParaPreguntas;
    }

}
