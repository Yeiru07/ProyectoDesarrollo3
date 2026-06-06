/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import Modelo.Sala;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Sala sala = App.salaActual;

        if (sala != null) {

            lblPinSala.setText("PIN: " + sala.getCodigoSala());

            lblTotalJugadores.setText("👤 " + sala.getCantidadUsuarios() + " participantes");

        }
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
