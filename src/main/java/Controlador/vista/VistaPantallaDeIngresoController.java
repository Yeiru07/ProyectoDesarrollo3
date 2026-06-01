/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import java.io.IOException;
import javafx.fxml.FXML;
import proyectofinaldesarrolloIII.App;

/**
 *
 * @author sronn
 */
public class VistaPantallaDeIngresoController {

    @FXML
    public void initialize() {
    }

//      @FXML
//    private void irARegistro() throws IOException {
//        App.setRoot("RegistroCreador");
//    }
    @FXML
    private void crearUnaSala() throws IOException {
        App.setRoot("VistaCreacionQuiz");
    }

    @FXML
    private void presentarSala() throws IOException {
        App.setRoot("VistaGestorSalas");
    }

    @FXML
    private void ingresarPin() throws IOException {
        App.setRoot("VistaLobbyDeLaPartida");
    }

}
