package Controlador;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import proyectofinaldesarrolloIII.App;

public class MotorJuego {

    @FXML
    private Button btnCrearSala;

    @FXML
    private Button btnIngresar;

    @FXML
    private Button btnPresentarJuego;

    @FXML
    private Button btnRegistrarse;

    @FXML
    private void irARegistro() throws IOException {
        App.setRoot("RegistroCreador");
    }

    @FXML
    private void crearUnaSala() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void presentarSala() throws IOException {
        App.setRoot("secondary");
    }

}
