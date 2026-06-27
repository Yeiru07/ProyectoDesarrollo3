package Controlador.gestor;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import red.ClienteSocket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GestorPreguntasCliente {

    private Preguntas preguntaActual;
    private int numeroDePreguntaActual = -1;

    private final ClienteSocket cliente;
    private final PrintWriter escritor;
    private final BufferedReader lector;

    public GestorPreguntasCliente(ClienteSocket cliente) {
        this.cliente = cliente;
        this.escritor = cliente.getEscritor();
        this.lector = cliente.getLector();
    }

    //=========================================================
    // METODOS LOCALES
    //=========================================================
    public Preguntas crearNuevaPregunta(Sala sala, String tipoPregunta) {

        Preguntas pregunta = new Preguntas();
        pregunta.setTipoDePregunta(tipoPregunta);

        sala.getListaPreguntas().add(pregunta);

        preguntaActual = pregunta;
        numeroDePreguntaActual = sala.getListaPreguntas().size() - 1;

        return pregunta;
    }

    public void cargarPregunta(Sala sala, int indice) {

        if (indice >= 0 && indice < sala.getListaPreguntas().size()) {

            preguntaActual = sala.getListaPreguntas().get(indice);
            numeroDePreguntaActual = indice;

        }

    }

    public boolean tienePreguntas(Sala sala) {
        return sala != null && !sala.getListaPreguntas().isEmpty();
    }

    public ArrayList<Preguntas> obtenerPreguntas(Sala sala) {
        return sala.getListaPreguntas();
    }

    public void eliminarPregunta(Sala sala, int indice) {

        if (sala == null) {
            return;
        }

        if (indice >= 0 && indice < sala.getListaPreguntas().size()) {

            sala.getListaPreguntas().remove(indice);

            if (sala.getListaPreguntas().isEmpty()) {

                limpiarPreguntaActual();

            } else if (numeroDePreguntaActual >= sala.getListaPreguntas().size()) {

                numeroDePreguntaActual = sala.getListaPreguntas().size() - 1;
                preguntaActual = sala.getListaPreguntas().get(numeroDePreguntaActual);

            }

        }

    }

    //=========================================================
    // GUARDAR PREGUNTA QUIZ
    //=========================================================
    public boolean guardarPreguntaQuiz(
            Sala sala,
            String enunciado,
            String respuestaRojo,
            String respuestaAzul,
            String respuestaAmarillo,
            String respuestaVerde,
            int tiempo,
            int puntos,
            String tipo,
            boolean rojoSeleccionado,
            boolean azulSeleccionado,
            boolean amarilloSeleccionado,
            boolean verdeSeleccionado) {

        if (numeroDePreguntaActual < 0 || sala == null) {
            return false;
        }

        if (enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un enunciado.");
        }

        if (respuestaRojo.trim().isEmpty()
                || respuestaAzul.trim().isEmpty()
                || respuestaAmarillo.trim().isEmpty()
                || respuestaVerde.trim().isEmpty()) {

            throw new IllegalArgumentException("Debe ingresar las 4 respuestas.");

        }

        if (!rojoSeleccionado
                && !azulSeleccionado
                && !amarilloSeleccionado
                && !verdeSeleccionado) {

            throw new IllegalArgumentException("Debe seleccionar la respuesta correcta.");

        }

        Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);

        actualizarDatosPregunta(
                pregunta,
                enunciado,
                tiempo,
                puntos,
                tipo,
                sala.getCodigoSala());

        ArrayList<Respuestas> respuestas = new ArrayList<>();

        respuestas.add(new Respuestas(1, respuestaRojo, rojoSeleccionado));
        respuestas.add(new Respuestas(2, respuestaAzul, azulSeleccionado));
        respuestas.add(new Respuestas(3, respuestaAmarillo, amarilloSeleccionado));
        respuestas.add(new Respuestas(4, respuestaVerde, verdeSeleccionado));

        pregunta.setArregloDeRespuestasParaPreguntas(respuestas);

        return true;

    }

    //=========================================================
    // VERDADERO O FALSO
    //=========================================================
    public boolean guardarPreguntaVerdaderoFalso(
            Sala sala,
            String enunciado,
            String respuestaRojo,
            String respuestaAzul,
            int tiempo,
            int puntos,
            String tipo,
            boolean rojoSeleccionado,
            boolean azulSeleccionado) {

        if (numeroDePreguntaActual < 0 || sala == null) {
            return false;
        }

        if (enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un enunciado.");
        }

        if (respuestaRojo.trim().isEmpty()
                || respuestaAzul.trim().isEmpty()) {

            throw new IllegalArgumentException("Debe ingresar ambas respuestas.");

        }

        if (!rojoSeleccionado && !azulSeleccionado) {

            throw new IllegalArgumentException("Debe seleccionar la respuesta correcta.");

        }

        Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);

        actualizarDatosPregunta(
                pregunta,
                enunciado,
                tiempo,
                puntos,
                tipo,
                sala.getCodigoSala());

        ArrayList<Respuestas> respuestas = new ArrayList<>();

        respuestas.add(new Respuestas(1, respuestaRojo, rojoSeleccionado));
        respuestas.add(new Respuestas(2, respuestaAzul, azulSeleccionado));

        pregunta.setArregloDeRespuestasParaPreguntas(respuestas);

        return true;

    }

    //=========================================================
    // ACTUALIZAR DATOS
    //=========================================================
    private void actualizarDatosPregunta(
            Preguntas pregunta,
            String enunciado,
            int tiempo,
            int puntos,
            String tipo,
            int codigoSala) {

        pregunta.setEnunciado(enunciado);
        pregunta.setTiempoParaLasPreguntas(tiempo);
        pregunta.setValorPuntosPreguntas(puntos);
        pregunta.setTipoDePregunta(tipo);
        pregunta.setCodigoSala(codigoSala);

    }

    //=========================================================
    // PREPARAR TRAMA
    //=========================================================
    public String prepararTramaPregunta(Preguntas pregunta) {

        if (pregunta == null) {
            return null;
        }

        if (pregunta.getEnunciado() == null
                || pregunta.getEnunciado().trim().isEmpty()) {

            return null;

        }

        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();

        if (respuestas == null || respuestas.isEmpty()) {
            return null;
        }

        String resp1 = "";
        String resp2 = "";
        String resp3 = "";
        String resp4 = "";

        if (respuestas.size() >= 1) {
            resp1 = respuestas.get(0).getRespuestas();
        }

        if (respuestas.size() >= 2) {
            resp2 = respuestas.get(1).getRespuestas();
        }

        if (respuestas.size() >= 3) {
            resp3 = respuestas.get(2).getRespuestas();
        }

        if (respuestas.size() >= 4) {
            resp4 = respuestas.get(3).getRespuestas();
        }

        int correcta = 0;

        for (int i = 0; i < respuestas.size(); i++) {

            if (respuestas.get(i).isCorrecta()) {

                correcta = i + 1;
                break;

            }

        }

        return "Pregunta|"
                + pregunta.getEnunciado() + "|"
                + resp1 + "|"
                + resp2 + "|"
                + resp3 + "|"
                + resp4 + "|"
                + pregunta.getCodigoSala() + "|"
                + correcta + "|"
                + pregunta.getTipoDePregunta() + "|"
                + pregunta.getTiempoParaLasPreguntas() + "|"
                + pregunta.getValorPuntosPreguntas();

    }

    //=========================================================
    // ENVIAR UNA PREGUNTA
    //=========================================================
    public boolean enviarPreguntaAlServidor(Preguntas pregunta) {

        String trama = prepararTramaPregunta(pregunta);

        if (trama == null) {
            return false;
        }

        try {

            escritor.println(trama);

            System.out.println("Pregunta enviada: " + trama);

            String respuesta = lector.readLine();

            return respuesta != null
                    && respuesta.equals("GUARDADO_OK");

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    //=========================================================
    // ENVIAR TODAS
    //=========================================================
    public boolean enviarTodasLasPreguntas(Sala sala) {

        if (sala == null) {
            return false;
        }

        for (Preguntas pregunta : sala.getListaPreguntas()) {

            if (pregunta.getEnunciado() == null
                    || pregunta.getEnunciado().trim().isEmpty()) {

                continue;

            }

            if (!enviarPreguntaAlServidor(pregunta)) {
                return false;
            }

        }

        return true;

    }

    //=========================================================
    // GETTERS Y SETTERS
    //=========================================================
    public Preguntas getPreguntaActual() {
        return preguntaActual;
    }

    public int getNumeroDePreguntaActual() {
        return numeroDePreguntaActual;
    }

    public void setNumeroDePreguntaActual(int numero) {
        this.numeroDePreguntaActual = numero;
    }

    public void limpiarPreguntaActual() {

        preguntaActual = null;
        numeroDePreguntaActual = -1;

    }

}
