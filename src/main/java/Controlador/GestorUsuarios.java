/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import java.io.IOException;
import javafx.fxml.FXML;
import proyectofinaldesarrolloIII.App;

/**
 *
 * @author sronn
 */
public class GestorUsuarios {
   
    
    @FXML
    private void onVolver() throws IOException {
        App.setRoot("VistaPantallaDeIngreso");
    }

    @FXML
    private void onRegistrar() throws IOException {
        App.setRoot("RegistroCreador");
    }

    
}
