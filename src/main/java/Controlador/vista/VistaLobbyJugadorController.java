package Controlador.vista;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import proyectofinaldesarrolloIII.App;

import java.net.URL;
import java.util.ResourceBundle;

public class VistaLobbyJugadorController
        implements Initializable {

    @FXML
    private Label lblEstadoSala;

    @FXML
    private Label lblNombreJugador;

    @FXML
    private ProgressIndicator progressLobby;

    @Override
    public void initialize(URL url,
                           ResourceBundle rb) {

        lblNombreJugador.setText(
            "Jugador: "
            + App.usuarioActual.getNombreUsuario()
        );

        escucharServidor();
    }

    private void escucharServidor() {

        new Thread(() -> {

            try {

                while (true) {

                    String mensaje =
                            App.lector.readLine();

                    if (mensaje == null) {
                        break;
                    }

                    System.out.println(
                            "RECIBIDO="
                            + mensaje
                    );

                    if (mensaje.equals(
                            "INICIO_PARTIDA")) {

                        Platform.runLater(() -> {

                            try {

                                App.setRoot(
                                    "VistaPreguntaMultiple"
                                );

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}