/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Partida;
import Modelo.Preguntas;
import Modelo.Respuestas;
import Utilidades.AlertaParaUsar;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import proyectofinaldesarrolloIII.App;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author sronn
 */
public class GestorPreguntas implements Initializable {

    @FXML
    private Button btnAgregarPregunta;

    @FXML
    private ComboBox<?> cmbLimiteDeTiempo;

    @FXML
    private ComboBox<?> cmbTipoDePregunta;

    @FXML
    private RadioButton rbRespuestaAmarillo;

    @FXML
    private RadioButton rbRespuestaAzul;

    @FXML
    private RadioButton rbRespuestaRojo;

    @FXML
    private RadioButton rbRespuestaVerde;

    @FXML
    private VBox contenedorPreguntas;

    @FXML
    private TextField txtTituloPregunta;

    @FXML
    private TextField txtRespuestaRojo;

    @FXML
    private TextField txtRespuestaAzul;

    @FXML
    private TextField txtRespuestaAmarillo;

    @FXML
    private TextField txtRespuestaVerde;

    @FXML
    private TextField txtTituloSala;

    private int NumeroDePreguntaActual = -1;

    Partida partida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.partida = App.partida;
        btnAgregarPregunta.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                agregarBotonPregunta();
            }
        });
        agregarBotonPregunta();
    }

    @FXML
    public void crearNuevaPregunta() {
        try {
            String titulo = txtTituloPregunta.getText().trim();
            String respuestaRojo = txtRespuestaRojo.getText().trim();
            String respuestaVerde = txtRespuestaVerde.getText().trim();
            String respuestaAmarillo = txtRespuestaAmarillo.getText().trim();
            String respuestaAzul = txtRespuestaAzul.getText().trim();
            
            Preguntas pregunta = partida.getListaPreguntas().get(NumeroDePreguntaActual);
             pregunta.setEnunciado(titulo);

            pregunta.getArregloDeRespuestasParaPreguntas().clear();

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(1, respuestaRojo));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(2, respuestaAzul));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(3, respuestaAmarillo));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(4, respuestaVerde));
        } catch (Exception e) {
        }

    }

    private void agregarBotonPregunta() {

        Preguntas nuevaPregunta = new Preguntas("", new ArrayList<Respuestas>());

        partida.getListaPreguntas().add(nuevaPregunta);

        int numero = partida.getListaPreguntas().size();

        Button btnPregunta = new Button(numero + " Quiz\nPregunta");

        btnPregunta.setPrefWidth(180);
        btnPregunta.getStyleClass().add("question-card");

        btnPregunta.setUserData(numero - 1);

        btnPregunta.setOnAction(this::seleccionarPregunta);

        contenedorPreguntas.getChildren().add(btnPregunta);

        NumeroDePreguntaActual = numero - 1;
    }

    @FXML
    private void seleccionarPregunta(ActionEvent event) {

        Button boton = (Button) event.getSource();

        int indice = (Integer) boton.getUserData();

        guardarPreguntaActual();

        cargarPregunta(indice);
    }

    @FXML
    private void guardarPreguntaActual() {

        try {
            if (NumeroDePreguntaActual < 0) {
                return;
            }
            String titulo = txtTituloPregunta.getText().trim();
            String respuestaRojo = txtRespuestaRojo.getText().trim();
            String respuestaVerde = txtRespuestaVerde.getText().trim();
            String respuestaAmarillo = txtRespuestaAmarillo.getText().trim();
            String respuestaAzul = txtRespuestaAzul.getText().trim();

            Preguntas pregunta = partida.getListaPreguntas().get(NumeroDePreguntaActual);

            pregunta.setEnunciado(titulo);

            pregunta.getArregloDeRespuestasParaPreguntas().clear();

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(1, respuestaRojo));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(2, respuestaAzul));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(3, respuestaAmarillo));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(4, respuestaVerde));

        } catch (Exception e) {
            AlertaParaUsar.mostrar("Error", e.getMessage(), Alert.AlertType.NONE);
        }
    }

    private void cargarPregunta(int indice) {

        Preguntas pregunta = partida.getListaPreguntas().get(indice);

        NumeroDePreguntaActual = indice;

        txtTituloPregunta.setText(pregunta.getEnunciado());

        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();

        if (respuestas.size() >= 4) {

            txtRespuestaRojo.setText(respuestas.get(0).getRespuestas());
            txtRespuestaAzul.setText(respuestas.get(1).getRespuestas());
            txtRespuestaAmarillo.setText(respuestas.get(2).getRespuestas());
            txtRespuestaVerde.setText(respuestas.get(3).getRespuestas());

        } else {
            txtRespuestaRojo.clear();
            txtRespuestaAzul.clear();
            txtRespuestaAmarillo.clear();
            txtRespuestaVerde.clear();
        }
    }
}
