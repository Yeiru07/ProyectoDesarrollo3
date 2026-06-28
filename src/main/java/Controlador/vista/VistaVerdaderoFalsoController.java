/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

/**
 *
 * @author sronn
 */
public class VistaVerdaderoFalsoController {

    @FXML
    private Label lblEstadoSeleccion;
    @FXML
    private Button btnVerdadero;
    @FXML
    private Button btnFalso;
    @FXML
    private ProgressBar progressTiempo;

    @FXML
    public void initialize() {
        if (progressTiempo != null) {
            progressTiempo.setProgress(1.0);
        }
    }

    @FXML
    private void onResponderVerdadero(ActionEvent event) {
        registrarRespuesta("Verdadero");
    }

    @FXML
    private void onResponderFalso(ActionEvent event) {
        registrarRespuesta("Falso");
    }

    private void registrarRespuesta(String respuesta) {
        if (lblEstadoSeleccion != null) {
            lblEstadoSeleccion.setText("Respuesta seleccionada: " + respuesta);
        }
        if (btnVerdadero != null) {
            btnVerdadero.setDisable(true);
        }
        if (btnFalso != null) {
            btnFalso.setDisable(true);
        }
    }
}
