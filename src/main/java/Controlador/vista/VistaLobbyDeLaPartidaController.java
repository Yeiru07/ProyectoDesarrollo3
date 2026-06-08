/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import Modelo.Sala;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import proyectofinaldesarrolloIII.App;

/**
 *
 * @author sronn
 */
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Sala sala = App.salaActual;

        if (sala != null) {

            lblPinSala.setText("PIN: " + sala.getCodigoSala());

            lblTotalJugadores.setText(
                    "👤 " + sala.getCantidadUsuarios() + " participantes"
            );
        }

        escucharServidor();
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

        new Thread(() -> {

            try {

                while (true) {

                    String mensaje = App.lector.readLine();

                    if (mensaje == null) {
                        break;
                    }

                    if (mensaje.startsWith("JUGADORES")) {

                        System.out.println(mensaje);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    @FXML
    public void ListoVamos() throws IOException {
        App.setRoot("VistaLobbyJugador");
    }

    @FXML
    public void regresar() throws IOException {
        App.setRoot("VistaGestorSalas");
    }
}
