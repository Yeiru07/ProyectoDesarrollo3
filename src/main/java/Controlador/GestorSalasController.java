/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controlador;

import Modelo.Juego;
import Modelo.Sala;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import proyectofinaldesarrolloIII.App;
import static proyectofinaldesarrolloIII.App.partida;

/**
 * FXML Controller class
 *
 * @author alexl
 */
public class GestorSalasController implements Initializable {

    /**
     * Initializes the controller class.
     */
    Juego partida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.partida = App.partida;

    }

    public void verSalas() {
        for (Sala sala : App.usuarioActual.getSalasAdministradas()) {

            System.out.println(sala.getNombreSala());

        }
    }

}
