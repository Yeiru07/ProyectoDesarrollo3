package Controlador.vista;

import Modelo.Preguntas;
import Modelo.Respuestas;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import proyectofinaldesarrolloIII.App;

public class VistaLobbyJugadorController implements Initializable {

    @FXML
    private Label lblEstadoSala;

    @FXML
    private Label lblNombreJugador;

    @FXML
    private ProgressIndicator progressLobby;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblNombreJugador.setText("Jugador: " + App.usuarioActual.getNombreUsuario());
        escucharServidor();
    }

    private void escucharServidor() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        String mensaje = App.lector.readLine();

                        if (mensaje == null) {
                            break;
                        }
                        System.out.println("RECIBIDO=" + mensaje);

                        // SI EL SERVIDOR ENVIA LAS PREGUNTAS
                        if (mensaje.startsWith("PREGUNTAS")) {
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
                        } else if (mensaje.equals("INICIO_PARTIDA")) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        App.setRoot("VistaPreguntaMultiple");
                                    } catch (IOException e) {
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
}
