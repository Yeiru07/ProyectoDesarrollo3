/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controlador.vista;

import Modelo.Juego;
import Modelo.Sala;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import proyectofinaldesarrolloIII.App;

/**
 * FXML Controller class
 *
 * @author alexl
 */
public class VistaPresentarSalasController implements Initializable {

    @FXML
    private Button btnIniciarJuego;

    @FXML
    private Button btnRegresar;

    @FXML
    private FlowPane flowPaneJugadores;

    @FXML
    private Label lblPinSala;

    @FXML
    private Label lblTotalJugadores;

    Juego partida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Sala sala = App.salaActual;

        if (sala != null) {

            lblPinSala.setText("PIN: " + sala.getCodigoSala());

            lblTotalJugadores.setText(
                    "Jugadores conectados: "
                    + sala.getCantidadUsuarios()
            );
        }
    }

    @FXML
    public void regresarSalaas() throws IOException {
        App.setRoot("VistaGestorSalas");
    }

}
