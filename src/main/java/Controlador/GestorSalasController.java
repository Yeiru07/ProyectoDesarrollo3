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

    private void enviarSalaPorSocket(Sala s) {
        // Validación preventiva: No enviar si el objeto interno no se llenó correctamente
        if (s.getNombreSala().isEmpty() || s.getCodigoSala() == 0) {
            System.out.println("No se puede enviar: La SALA ESTA INCOMPLETA");
            return;
        }
        try {
            String trama = "Sala|"
                    + s.getNombreSala() + "|"
                    + s.getCodigoSala();

            proyectofinaldesarrolloIII.App.escritor.println(trama);
            System.out.println("Enviado exitosamente al servidor: " + trama);

        } catch (Exception e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }
}
