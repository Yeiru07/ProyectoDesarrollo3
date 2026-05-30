package Controlador;

import java.io.IOException;
import javafx.fxml.FXML;
import proyectofinaldesarrolloIII.App;


public class MotorJuego {
    
     @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

}
