package Controlador.vista;

import Modelo.Preguntas;
import Modelo.Respuestas;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import proyectofinaldesarrolloIII.App;

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

        System.out.println("PREGUNTAS CARGADAS = " + App.preguntasActuales.size());

        if (App.preguntasActuales.isEmpty()) {
            return;
        }

        Preguntas pregunta = App.preguntasActuales.get(0);

        lblEnunciado.setText(pregunta.getEnunciado());

        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();

        // Ocultamos todos primero
        btnOpcion1.setVisible(false);
        btnOpcion2.setVisible(false);
        btnOpcion3.setVisible(false);
        btnOpcion4.setVisible(false);

        // Mostramos segun la cantidad
        if (respuestas.size() >= 1) {
            btnOpcion1.setVisible(true);
            btnOpcion1.setText(respuestas.get(0).getRespuestas());
        }

        if (respuestas.size() >= 2) {
            btnOpcion2.setVisible(true);
            btnOpcion2.setText(respuestas.get(1).getRespuestas());
        }

        if (respuestas.size() >= 3) {
            btnOpcion3.setVisible(true);
            btnOpcion3.setText(respuestas.get(2).getRespuestas());
        }

        if (respuestas.size() >= 4) {
            btnOpcion4.setVisible(true);
            btnOpcion4.setText(respuestas.get(3).getRespuestas());
        }
    }

    @FXML
    private void onResponderOpcion1() {
        lblEstadoSeleccion.setText("Seleccionaste: " + btnOpcion1.getText());
        System.out.println("RESPUESTA: " + btnOpcion1.getText());
    }

    @FXML
    private void onResponderOpcion2() {
        lblEstadoSeleccion.setText("Seleccionaste: " + btnOpcion2.getText());
        System.out.println("RESPUESTA: " + btnOpcion2.getText());
    }

    @FXML
    private void onResponderOpcion3() {
        lblEstadoSeleccion.setText("Seleccionaste: " + btnOpcion3.getText());
        System.out.println("RESPUESTA: " + btnOpcion3.getText());
    }

    @FXML
    private void onResponderOpcion4() {
        lblEstadoSeleccion.setText("Seleccionaste: " + btnOpcion4.getText());
        System.out.println("RESPUESTA: " + btnOpcion4.getText());
    }
}
