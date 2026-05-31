/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Partida;
import Modelo.Preguntas;
import Modelo.Respuestas;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

    private int preguntaActual = -2;

    Partida partida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.partida = App.partida;
        btnAgregarPregunta.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                agregarPregunta();
            }
        });
        agregarPregunta();
    }

    private void agregarPregunta() {

        Preguntas nuevaPregunta = new Preguntas("", new ArrayList<>());
        partida.getListaPreguntas().add(nuevaPregunta);

        int numero = partida.getListaPreguntas().size();

        Button btnPregunta = new Button(numero + " Quiz\nPregunta");

        btnPregunta.setPrefWidth(180);
        btnPregunta.getStyleClass().add("question-card");

        btnPregunta.setUserData(numero - 1);

        btnPregunta.setOnAction(this::seleccionarPregunta);

        contenedorPreguntas.getChildren().add(btnPregunta);

        preguntaActual = numero - 1;
    }

    /*  private VBox crearTarjeta(int numero) {

        VBox card = new VBox();

        card.getStyleClass().add("question-card");

        Label lblNumero = new Label(numero + " Quiz");
        lblNumero.getStyleClass().add("quiz-number");

        Label lblPregunta = new Label("Pregunta");
        lblPregunta.getStyleClass().add("question-title");

        card.getChildren().addAll(lblNumero, lblPregunta);

        card.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                guardarPreguntaActual();

                preguntaActual = numero - 1;

                cargarPregunta(preguntaActual);
            }
        });

        return card;
    }*/
    @FXML
    private void seleccionarPregunta(ActionEvent event) {

        Button boton = (Button) event.getSource();

        int indice = (Integer) boton.getUserData();

        guardarPreguntaActual();

        cargarPregunta(indice);
    }

    private void guardarPreguntaActual() {

        if (preguntaActual < 0) {
            return;
        }

        Preguntas pregunta = partida.getListaPreguntas().get(preguntaActual);

        pregunta.setEnunciado(txtTituloPregunta.getText());

        pregunta.getArregloDeRespuestasParaPreguntas().clear();

        pregunta.getArregloDeRespuestasParaPreguntas().add(
                new Respuestas(1, txtRespuestaRojo.getText()));

        pregunta.getArregloDeRespuestasParaPreguntas().add(
                new Respuestas(2, txtRespuestaAzul.getText()));

        pregunta.getArregloDeRespuestasParaPreguntas().add(
                new Respuestas(3, txtRespuestaAmarillo.getText()));

        pregunta.getArregloDeRespuestasParaPreguntas().add(
                new Respuestas(4, txtRespuestaVerde.getText()));
    }

    private void cargarPregunta(int indice) {

        Preguntas pregunta = partida.getListaPreguntas().get(indice);

        preguntaActual = indice;

        txtTituloPregunta.setText(pregunta.getEnunciado());

        ArrayList<Respuestas> respuestas
                = pregunta.getArregloDeRespuestasParaPreguntas();

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
