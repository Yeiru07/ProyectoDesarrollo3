package Controlador.gestor;

import Modelo.Preguntas;
import Modelo.Respuestas;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import proyectofinaldesarrolloIII.App;
import red.ClienteSocket;

public class GestorLobbyJugadorCliente {

    private ClienteSocket clienteSocket;
    private String nombreJugador;
    private Label lblEstadoSala;
    private Label lblNombreJugador;
    private ProgressIndicator progressLobby;

    private Runnable onPreguntasRecibidas;
    private Runnable onInicioPartida;
    private Runnable onErrorConexion;
    private boolean escuchandoServidor = true;

    public GestorLobbyJugadorCliente(String nombreJugador, Label lblEstadoSala,
            Label lblNombreJugador, ProgressIndicator progressLobby) {
        this.nombreJugador = nombreJugador;
        this.lblEstadoSala = lblEstadoSala;
        this.lblNombreJugador = lblNombreJugador;
        this.progressLobby = progressLobby;
        this.clienteSocket = App.cliente;
    }

    public void iniciarLobby() {
        // Conectar al servidor si no está conectado
        if (clienteSocket.getLector() == null) {
            //ESTA EN EL MAIN clienteSocket.conectar();
        }

        // Actualizar UI inicial
        Platform.runLater(() -> {
            lblNombreJugador.setText("Jugador: " + nombreJugador);
            lblEstadoSala.setText("Esperando inicio de partida...");
            progressLobby.setVisible(true);
        });

        escucharServidor();
    }

    private void escucharServidor() {
        new Thread(() -> {
            try {
                System.out.println("ESCUCHANDO SERVIDOR (JUGADOR)...");
                BufferedReader lector = clienteSocket.getLector();

                while (escuchandoServidor) {
                    String mensaje = lector.readLine();

                    if (mensaje == null) {
                        System.out.println("Servidor desconectado, cerrando escucha...");
                        if (onErrorConexion != null) {
                            Platform.runLater(onErrorConexion);
                        }
                        break;
                    }

                    System.out.println("MENSAJE RECIBIDO (JUGADOR): " + mensaje);
                    procesarMensaje(mensaje);
                }
            } catch (IOException e) {
                System.out.println("Conexión con servidor cerrada: " + e.getMessage());
                if (onErrorConexion != null) {
                    Platform.runLater(onErrorConexion);
                }
            }
        }).start();
    }

    private void procesarMensaje(String mensaje) {
        if (mensaje.startsWith("PREGUNTAS")) {
            procesarPreguntas(mensaje);
        } else if (mensaje.equals("INICIO_PARTIDA")) {
            procesarInicioPartida();
        }
    }

    private void procesarPreguntas(String mensaje) {
        System.out.println("RECIBI PREGUNTAS (JUGADOR)");
        List<Preguntas> preguntasActuales = new ArrayList<>();

        String contenido = mensaje.replace("PREGUNTAS|", "");
        String[] preguntas = contenido.split(";");

        for (String bloque : preguntas) {
            if (bloque.trim().isEmpty()) {
                continue;
            }

            Preguntas p = parsearPregunta(bloque);
            if (p != null) {
                preguntasActuales.add(p);
            }
        }

        // GUARDAR TODAS LAS PREGUNTAS RECIBIDAS
        App.preguntasActuales.clear();
        App.preguntasActuales.addAll(preguntasActuales);

        System.out.println("PREGUNTAS CARGADAS = " + App.preguntasActuales.size());

        if (onPreguntasRecibidas != null) {
            escuchandoServidor = false;
            Platform.runLater(onPreguntasRecibidas);
        }
    }

    private void procesarInicioPartida() {
        System.out.println("INICIO DE PARTIDA RECIBIDO (JUGADOR)");
        Platform.runLater(() -> {
            lblEstadoSala.setText("¡La partida ha comenzado!");
            if (onInicioPartida != null) {
                escuchandoServidor = false;
                onInicioPartida.run();
            }
        });
    }

    // Métodos para configurar callbacks
    private Preguntas parsearPregunta(String bloque) {
        String[] datos = bloque.split(",", -1);

        if (datos.length < 2) {
            return null;
        }

        Preguntas p = new Preguntas();
        p.setEnunciado(datos[0]);

        if (datos.length >= 6 && esTipoPregunta(datos[1])) {
            p.setTipoDePregunta(datos[1]);
            p.setTiempoParaLasPreguntas(parseEntero(datos[2], 20));
            p.setValorPuntosPreguntas(parseEntero(datos[3], 10));

            int indiceCorrecta = parseEntero(datos[4], 0);
            ArrayList<Respuestas> respuestas = new ArrayList<>();

            for (int i = 5; i < datos.length; i++) {
                if (datos[i] == null || datos[i].trim().isEmpty()) {
                    continue;
                }
                int numeroRespuesta = respuestas.size() + 1;
                respuestas.add(new Respuestas(numeroRespuesta, datos[i], indiceCorrecta == numeroRespuesta));
            }

            p.setArregloDeRespuestasParaPreguntas(respuestas);
            return p;
        }

        int indiceCorrecta = parseEntero(datos[datos.length - 1], 0);
        ArrayList<Respuestas> respuestas = new ArrayList<>();

        for (int i = 1; i < datos.length - 1; i++) {
            if (datos[i] == null || datos[i].trim().isEmpty()) {
                continue;
            }
            int numeroRespuesta = respuestas.size() + 1;
            respuestas.add(new Respuestas(numeroRespuesta, datos[i], indiceCorrecta == i));
        }

        p.setTipoDePregunta(respuestas.size() <= 2 ? "Verdadero O Falso" : "Quiz");
        p.setTiempoParaLasPreguntas(20);
        p.setValorPuntosPreguntas(10);
        p.setArregloDeRespuestasParaPreguntas(respuestas);
        return p;
    }

    private boolean esTipoPregunta(String valor) {
        return "Quiz".equals(valor) || "Verdadero O Falso".equals(valor);
    }

    private int parseEntero(String valor, int valorDefecto) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return valorDefecto;
        }
    }

    public void setOnPreguntasRecibidas(Runnable callback) {
        this.onPreguntasRecibidas = callback;
    }

    public void setOnInicioPartida(Runnable callback) {
        this.onInicioPartida = callback;
    }

    public void setOnErrorConexion(Runnable callback) {
        this.onErrorConexion = callback;
    }

    public void cerrarConexion() {
        //clienteSocket.cerrarConexion();
        escuchandoServidor = false;
        System.out.println("Cambio de vista, se mantiene la conexión.");

    }

    public String getNombreJugador() {
        return nombreJugador;
    }

    public void setNombreJugador(String nombreJugador) {
        this.nombreJugador = nombreJugador;
        Platform.runLater(() -> lblNombreJugador.setText("Jugador: " + nombreJugador));
    }
}
