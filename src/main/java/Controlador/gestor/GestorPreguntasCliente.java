package Controlador.gestor;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import red.ClienteSocket;
import java.util.ArrayList;

public class GestorPreguntasCliente {

    private Preguntas preguntaActual;
    private int numeroDePreguntaActual = -1;
    private final ClienteSocket cliente;

    public GestorPreguntasCliente(ClienteSocket cliente) {
        this.cliente = cliente;
    }

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

    public boolean guardarPreguntaQuiz(Sala sala, String enunciado, String respuestaRojo,
            String respuestaAzul, String respuestaAmarillo,
            String respuestaVerde, int tiempo, int puntos,
            String tipo, boolean rojoSeleccionado,
            boolean azulSeleccionado, boolean amarilloSeleccionado,
            boolean verdeSeleccionado) {

        if (numeroDePreguntaActual < 0 || sala == null) {
            return false;
        }

        // Validaciones
        if (enunciado.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un enunciado a la pregunta");
        }
        if (respuestaAmarillo.isEmpty() || respuestaAzul.isEmpty()
                || respuestaRojo.isEmpty() || respuestaVerde.isEmpty()) {
            throw new IllegalArgumentException("Debe de ingresar las 4 respuestas");
        }
        if (!rojoSeleccionado && !azulSeleccionado && !amarilloSeleccionado && !verdeSeleccionado) {
            throw new IllegalArgumentException("Debe seleccionar una respuesta correcta (Radio Button)");
        }

        Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);
        actualizarDatosPregunta(pregunta, enunciado, tiempo, puntos, tipo, sala.getCodigoSala());

        ArrayList<Respuestas> respuestas = new ArrayList<>();
        respuestas.add(new Respuestas(1, respuestaRojo, rojoSeleccionado));
        respuestas.add(new Respuestas(2, respuestaAzul, azulSeleccionado));
        respuestas.add(new Respuestas(3, respuestaAmarillo, amarilloSeleccionado));
        respuestas.add(new Respuestas(4, respuestaVerde, verdeSeleccionado));
        pregunta.setArregloDeRespuestasParaPreguntas(respuestas);

        return true;
    }

    public boolean guardarPreguntaVerdaderoFalso(Sala sala, String enunciado,
            String respuestaRojo, String respuestaAzul,
            int tiempo, int puntos, String tipo,
            boolean rojoSeleccionado, boolean azulSeleccionado) {

        if (numeroDePreguntaActual < 0 || sala == null) {
            return false;
        }

        if (enunciado.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un enunciado a la pregunta");
        }
        if (respuestaAzul.isEmpty() || respuestaRojo.isEmpty()) {
            throw new IllegalArgumentException("Debe de ingresar las 2 respuestas");
        }
        if (!rojoSeleccionado && !azulSeleccionado) {
            throw new IllegalArgumentException("Debe seleccionar la opción correcta");
        }

        Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);
        actualizarDatosPregunta(pregunta, enunciado, tiempo, puntos, tipo, sala.getCodigoSala());

        ArrayList<Respuestas> respuestas = new ArrayList<>();
        respuestas.add(new Respuestas(1, respuestaRojo, rojoSeleccionado));
        respuestas.add(new Respuestas(2, respuestaAzul, azulSeleccionado));
        pregunta.setArregloDeRespuestasParaPreguntas(respuestas);

        return true;
    }

    private void actualizarDatosPregunta(Preguntas pregunta, String enunciado,
            int tiempo, int puntos, String tipo, int codigoSala) {
        pregunta.setEnunciado(enunciado);
        pregunta.setTiempoParaLasPreguntas(tiempo);
        pregunta.setValorPuntosPreguntas(puntos);
        pregunta.setTipoDePregunta(tipo);
        pregunta.setCodigoSala(codigoSala);
    }

    public String prepararTramaPregunta(Preguntas pregunta) {
        if (pregunta == null || pregunta.getEnunciado().isEmpty()
                || pregunta.getArregloDeRespuestasParaPreguntas().isEmpty()) {
            return null;
        }

        String trama = "Pregunta|" + pregunta.getEnunciado();
        for (Respuestas r : pregunta.getArregloDeRespuestasParaPreguntas()) {
            trama += "|" + r.getRespuestas();
        }
        trama += "|" + pregunta.getCodigoSala();
        return trama;
    }

    public boolean enviarPreguntaAlServidor(Preguntas pregunta) {
        String trama = prepararTramaPregunta(pregunta);
        if (trama == null) {
            return false;
        }

        try {
            cliente.getEscritor().println(trama);
            System.out.println("Pregunta enviada: " + trama);

            // Esperar confirmación
            String confirmacion = cliente.getLector().readLine();
            if (confirmacion != null && confirmacion.startsWith("OK")) {
                return true;
            } else {
                System.err.println("Error al enviar pregunta: " + confirmacion);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getNumeroDePreguntaActual() {
        return numeroDePreguntaActual;
    }

    public void setNumeroDePreguntaActual(int numero) {
        this.numeroDePreguntaActual = numero;
    }

    public Preguntas getPreguntaActual() {
        return preguntaActual;
    }

    public void limpiarPreguntaActual() {
        preguntaActual = null;
        numeroDePreguntaActual = -1;
    }
}
