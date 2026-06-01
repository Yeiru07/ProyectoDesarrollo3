/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import proyectofinaldesarrolloIII.App;

/**
 *
 * @author sronn
 */
public class VistaLobbyDeLaPartidaController implements Initializable {
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    @FXML
    public void ListoVamos() throws IOException {
        App.setRoot("VistaLobbyJugador");
    }
    
}
