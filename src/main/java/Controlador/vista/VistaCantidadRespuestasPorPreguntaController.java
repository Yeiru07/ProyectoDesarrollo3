/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 *
 * @author sronn
 */
public class VistaCantidadRespuestasPorPreguntaController {

    @FXML
    private Label lblEstado;

    @FXML
    private void onAvanzar(ActionEvent event) {
        if (lblEstado != null) {
            lblEstado.setText("Avanzando a la siguiente pregunta...");
        }
    }
}
