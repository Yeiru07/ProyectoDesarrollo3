package Controlador.vista;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class VistaPreguntaMultipleController {

    @FXML
    private Label lblEnunciado;

    @FXML
    private Label lblCronometro;

    @FXML
    private Label lblEstadoSeleccion;

    @FXML
    private ProgressBar progressTiempo;

    @FXML
    private Button btnOpcion1;

    @FXML
    private Button btnOpcion2;

    @FXML
    private Button btnOpcion3;

    @FXML
    private Button btnOpcion4;

    @FXML
    private void onResponderOpcion1() {
        lblEstadoSeleccion.setText("Seleccionaste: " + btnOpcion1.getText());
    }

    @FXML
    private void onResponderOpcion2() {

        lblEstadoSeleccion.setText("Seleccionaste: " + btnOpcion2.getText());
    }

    @FXML
    private void onResponderOpcion3() {

        lblEstadoSeleccion.setText("Seleccionaste: " + btnOpcion3.getText());
    }

    @FXML
    private void onResponderOpcion4() {

        lblEstadoSeleccion.setText("Seleccionaste: " + btnOpcion4.getText());
    }
}
