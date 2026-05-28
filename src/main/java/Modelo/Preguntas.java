package Modelo;

import java.util.ArrayList;


public class Preguntas {
    private ArrayList<String> arregloDePreguntas;
    private ArrayList<Respuestas> arregloDeRespuestasParaPreguntas;

    public Preguntas(ArrayList<String> arregloDePreguntas, ArrayList<Respuestas> arregloDeRespuestasParaPreguntas) {
        this.arregloDePreguntas = new ArrayList<>();
        this.arregloDeRespuestasParaPreguntas = new ArrayList<>();
    }

    public ArrayList<String> getArregloDePreguntas() {
        return arregloDePreguntas;
    }

    public void setArregloDePreguntas(ArrayList<String> arregloDePreguntas) {
        this.arregloDePreguntas = arregloDePreguntas;
    }

    public ArrayList<Respuestas> getArregloDeRespuestasParaPreguntas() {
        return arregloDeRespuestasParaPreguntas;
    }

    public void setArregloDeRespuestasParaPreguntas(ArrayList<Respuestas> arregloDeRespuestasParaPreguntas) {
        this.arregloDeRespuestasParaPreguntas = arregloDeRespuestasParaPreguntas;
    }
    
}
