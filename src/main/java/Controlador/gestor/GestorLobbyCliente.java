package Controlador.gestor;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import proyectofinaldesarrolloIII.App;
import red.ClienteSocket;

public class GestorLobbyCliente {

    private Sala sala;
    private ClienteSocket clienteSocket;
    private FlowPane flowJugadores;
    private Label lblPinSala;
    private Label lblTotalJugadores;
    private Runnable onPreguntasRecibidas;
    private Runnable onErrorConexion;
    private boolean escuchandoServidor = true;

    // Callback para actualizar la UI de jugadores
    private List<String> jugadoresLobby = new ArrayList<>();

    public GestorLobbyCliente(Sala sala, FlowPane flowJugadores, Label lblPinSala, Label lblTotalJugadores) {
        this.sala = sala;
        this.flowJugadores = flowJugadores;
        this.lblPinSala = lblPinSala;
        this.lblTotalJugadores = lblTotalJugadores;
        this.clienteSocket = App.cliente;
    }

    public void iniciarLobby() {
        // Conectar al servidor si no está conectado
        if (clienteSocket.getLector() == null) {
            //esta en el main clienteSocket.conectar();
        }

        if (sala != null) {
            lblPinSala.setText("PIN: " + sala.getCodigoSala());
            App.salaActual = sala;
        }

        if (App.respuestaLobby != null) {

            procesarMensaje(App.respuestaLobby);

            App.respuestaLobby = null;
        }
        // Actualizar jugadores iniciales si existen
        if (!jugadoresLobby.isEmpty()) {
            actualizarJugadores(jugadoresLobby);
            lblTotalJugadores.setText("👤 " + jugadoresLobby.size() + " participantes");
        }

        escucharServidor();
    }

    public void actualizarJugadores(List<String> nombres) {
        Platform.runLater(() -> {
            flowJugadores.getChildren().clear();
            for (String nombre : nombres) {
                Label jugador = new Label(nombre);
                jugador.getStyleClass().add("player-tag");
                flowJugadores.getChildren().add(jugador);
            }
        });
    }

    private void escucharServidor() {
        new Thread(() -> {
            try {
                System.out.println("ESCUCHANDO SERVIDOR...");
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

                    System.out.println("MENSAJE RECIBIDO: " + mensaje);
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
        if (mensaje.startsWith("JUGADORES")) {
            procesarJugadores(mensaje);
        } else if (mensaje.startsWith("PREGUNTAS")) {
            procesarPreguntas(mensaje);
        }
    }

    private void procesarJugadores(String mensaje) {
        String[] partes = mensaje.split("\\|");

        if (partes.length > 1) {
            String[] nombres = partes[1].split(",");
            jugadoresLobby = Arrays.asList(nombres);

            Platform.runLater(() -> {
                actualizarJugadores(jugadoresLobby);
                lblTotalJugadores.setText("👤 " + nombres.length + " participantes");
            });
        }
    }

    private void procesarPreguntas(String mensaje) {
        System.out.println("RECIBI PREGUNTAS");

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

        // Guardar todas las preguntas recibidas
        App.preguntasActuales.clear();
        App.preguntasActuales.addAll(preguntasActuales);

        System.out.println("PREGUNTAS CARGADAS = " + App.preguntasActuales.size());

        // Notificar una sola vez
        if (onPreguntasRecibidas != null) {
            escuchandoServidor = false;
            Platform.runLater(() -> onPreguntasRecibidas.run());
        }
    }

    public void solicitarPreguntas() {
        String trama = "OBTENER_PREGUNTAS|" + sala.getCodigoSala();
        PrintWriter escritor = clienteSocket.getEscritor();
        if (escritor != null) {
            escritor.println(trama);
        }
    }

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

    public void setOnErrorConexion(Runnable callback) {
        this.onErrorConexion = callback;
    }

    public List<String> getJugadoresLobby() {
        return jugadoresLobby;
    }

    public void cerrarConexion() {
        //clienteSocket.cerrarConexion();
        escuchandoServidor = false;
        System.out.println("Cambio de vista, se mantiene la conexión.");

    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }
}
