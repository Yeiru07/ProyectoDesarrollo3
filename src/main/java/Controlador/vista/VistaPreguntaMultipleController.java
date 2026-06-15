package Controlador.vista;

import Modelo.Preguntas;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import proyectofinaldesarrolloIII.App;

import java.net.URL;
import java.util.ResourceBundle;

public class VistaPreguntaMultipleController
        implements Initializable {

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

    @Override
    public void initialize(URL url,
            ResourceBundle rb) {

        System.out.println(
                "PREGUNTAS CARGADAS = "
                + App.preguntasActuales.size()
        );
        if (!App.preguntasActuales.isEmpty()) {
            Preguntas pregunta = App.preguntasActuales.get(0);
            lblEnunciado.setText(pregunta.getEnunciado());
            btnOpcion1.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(0).getRespuestas());
            btnOpcion2.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(1).getRespuestas());
            btnOpcion3.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(2).getRespuestas());
            btnOpcion4.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(3).getRespuestas());
        }
    }

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
