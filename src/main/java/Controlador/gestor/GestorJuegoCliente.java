package Controlador.gestor;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import red.ClienteSocket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestor de juego del lado del cliente.
 * Responsabilidades:
 * - Manejar preguntas y respuestas
 * - Enviar respuestas al servidor
 * - Obtener preguntas del servidor
 * - Gestionar el estado del juego
 */
public class GestorJuegoCliente {

    private final ClienteSocket cliente;
    private final PrintWriter escritor;
    private final BufferedReader lector;
    private Sala salaActual;
    private ArrayList<Preguntas> preguntasActuales = new ArrayList<>();
    private int preguntaActualIndex = 0;
    private boolean esPresentador = false;
    private boolean juegoTerminado = false;

    public GestorJuegoCliente(ClienteSocket cliente) {
        this.cliente = cliente;
        this.escritor = cliente.getEscritor();
        this.lector = cliente.getLector();
    }

    // ==================== METODOS DE PREGUNTAS ====================
    
    public void setSalaActual(Sala sala) {
        this.salaActual = sala;
        if (sala != null) {
            this.esPresentador = true;
        }
    }

    public void setPreguntasActuales(ArrayList<Preguntas> preguntas) {
        this.preguntasActuales = preguntas;
        this.preguntaActualIndex = 0;
    }

    public ArrayList<Preguntas> getPreguntasActuales() {
        return preguntasActuales;
    }

    public Preguntas getPreguntaActual() {
        if (preguntaActualIndex < preguntasActuales.size()) {
            return preguntasActuales.get(preguntaActualIndex);
        }
        return null;
    }

    public int getPreguntaActualIndex() {
        return preguntaActualIndex;
    }

    public boolean hayMasPreguntas() {
        return preguntaActualIndex < preguntasActuales.size() - 1;
    }

    public boolean esPresentador() {
        return esPresentador;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    // ==================== METODOS DE COMUNICACION ====================
    
    /**
     * Solicita las preguntas de una sala al servidor
     */
    public boolean solicitarPreguntas(int codigoSala) {
        String trama = "OBTENER_PREGUNTAS|" + codigoSala;
        try {
            escritor.println(trama);
            System.out.println("Solicitando preguntas: " + trama);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envia la respuesta del jugador al servidor
     */
    public boolean enviarRespuesta(String nombreUsuario, String respuesta, int tiempoRestante) {
        if (salaActual == null || preguntasActuales.isEmpty()) {
            return false;
        }

        Preguntas pregunta = getPreguntaActual();
        if (pregunta == null) {
            return false;
        }

        String trama = "RESPUESTA|" + salaActual.getCodigoSala() + "|"
                + nombreUsuario + "|" + respuesta + "|" + tiempoRestante;

        try {
            escritor.println(trama);
            System.out.println("Respuesta enviada: " + trama);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envia el tiempo agotado como respuesta
     */
    public boolean enviarTiempoAgotado(String nombreUsuario) {
        return enviarRespuesta(nombreUsuario, "TIEMPO_AGOTADO", 0);
    }

    /**
     * Notifica al servidor que el jugador abandona la partida
     */
    public boolean notificarAbandono(String nombreUsuario) {
        String trama = "ABANDONAR|" + nombreUsuario;
        try {
            escritor.println(trama);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Notifica al servidor que el presentador avanza a la siguiente pregunta
     */
    public boolean notificarSiguientePregunta(int codigoSala, int nuevoIndice) {
        String trama = "SIGUIENTE_PREGUNTA|" + codigoSala + "|" + nuevoIndice;
        try {
            escritor.println(trama);
            System.out.println("Presentador notifica: " + trama);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Inicia el juego en la sala
     */
    public boolean iniciarJuego(int codigoSala) {
        String trama = "INICIAR_JUEGO|" + codigoSala;
        try {
            escritor.println(trama);
            System.out.println("Iniciando juego: " + trama);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== METODOS DE PROCESAMIENTO DE TRAMAS ====================
    
    /**
     * Procesa una trama de preguntas recibida del servidor
     */
    public boolean procesarPreguntasRecibidas(String mensaje) {
        if (!mensaje.startsWith("PREGUNTAS")) {
            return false;
        }

        preguntasActuales.clear();
        String contenido = mensaje.replace("PREGUNTAS|", "");
        String[] preguntas = contenido.split(";");

        for (int idx = 0; idx < preguntas.length; idx++) {
            String bloque = preguntas[idx];
            if (bloque.trim().isEmpty()) {
                continue;
            }

            String[] datos = bloque.split(",");

            Preguntas p = new Preguntas();
            p.setEnunciado(datos[0]);

            int indiceCorrecta = 0;
            try {
                indiceCorrecta = Integer.parseInt(datos[datos.length - 1]);
            } catch (NumberFormatException e) {
                indiceCorrecta = 0;
            }

            ArrayList<Respuestas> respuestas = new ArrayList<>();
            for (int i = 1; i < datos.length - 1; i++) {
                boolean esCorrecta = (indiceCorrecta == i);
                respuestas.add(new Respuestas(i, datos[i], esCorrecta));
            }

            p.setArregloDeRespuestasParaPreguntas(respuestas);
            preguntasActuales.add(p);
        }

        return !preguntasActuales.isEmpty();
    }

    /**
     * Avanza a la siguiente pregunta
     */
    public boolean avanzarPregunta() {
        if (preguntaActualIndex < preguntasActuales.size() - 1) {
            preguntaActualIndex++;
            return true;
        }
        juegoTerminado = true;
        return false;
    }

    /**
     * Reinicia el estado del juego
     */
    public void reiniciarJuego() {
        preguntasActuales.clear();
        preguntaActualIndex = 0;
        juegoTerminado = false;
    }
}
