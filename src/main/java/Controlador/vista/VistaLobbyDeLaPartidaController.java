package Controlador.vista;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import proyectofinaldesarrolloIII.App;

public class VistaLobbyDeLaPartidaController implements Initializable {

    @FXML
    private Button btnIniciarJuego;
    @FXML
    private Button btnRegresar;
    @FXML
    private Label lblPinSala;
    @FXML
    private Label lblTotalJugadores;
    @FXML
    private FlowPane flowJugadores;

    Sala sala;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.sala = App.salaActual;

        if (sala != null) {
            lblPinSala.setText("PIN: " + sala.getCodigoSala());
        }

        escucharServidor();
        if (App.jugadoresLobby != null) {
            String datos = App.jugadoresLobby.replace("JUGADORES|", "");
            String[] jugadores = datos.split(",");
            actualizarJugadores(Arrays.asList(jugadores));
            lblTotalJugadores.setText("👤 " + jugadores.length + " participantes");
        }
    }

    public void actualizarJugadores(List<String> nombres) {
        flowJugadores.getChildren().clear();
        for (String nombre : nombres) {
            Label jugador = new Label(nombre);
            jugador.getStyleClass().add("player-tag");
            flowJugadores.getChildren().add(jugador);
        }
    }

    private void escucharServidor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("ESCUCHANDO SERVIDOR...");

                    while (true) {
                        String mensaje = App.lector.readLine();
                        System.out.println("MENSAJE RECIBIDO: " + mensaje);
                        if (mensaje == null) {
                            break;
                        }

                        if (mensaje.startsWith("JUGADORES")) {
                            App.jugadoresLobby = mensaje;
                            String[] partes = mensaje.split("\\|");

                            if (partes.length > 1) {
                                final String[] nombres = partes[1].split(",");

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        actualizarJugadores(Arrays.asList(nombres));
                                        lblTotalJugadores.setText("👤 " + nombres.length + " participantes");
                                    }
                                });
                            }
                        } else if (mensaje.startsWith("PREGUNTAS")) {
                            System.out.println("RECIBI PREGUNTAS");
                            App.preguntasActuales.clear();

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

                                // El ultimo dato es el indice de la respuesta correcta
                                int indiceCorrecta = 0;
                                try {
                                    indiceCorrecta = Integer.parseInt(datos[datos.length - 1]);
                                } catch (NumberFormatException e) {
                                    indiceCorrecta = 0;
                                }

                                ArrayList<Respuestas> respuestas = new ArrayList<>();
                                // Las respuestas son datos[1] hasta datos[datos.length - 2]
                                for (int i = 1; i < datos.length - 1; i++) {
                                    boolean esCorrecta = (indiceCorrecta == i); // i es 1-based
                                    respuestas.add(new Respuestas(i, datos[i], esCorrecta));
                                }

                                p.setArregloDeRespuestasParaPreguntas(respuestas);
                                App.preguntasActuales.add(p);
                            }

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        App.setRoot("VistaPreguntaMultiple");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @FXML
    public void ListoVamos() {
        String trama = "OBTENER_PREGUNTAS|" + sala.getCodigoSala();
        App.escritor.println(trama);
    }

    @FXML
    public void regresar() throws IOException {
        App.setRoot("VistaGestorSalas");
    }
}
