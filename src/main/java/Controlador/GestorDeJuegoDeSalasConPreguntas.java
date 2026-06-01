/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Juego;
import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import Utilidades.AlertaParaUsar;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
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

public class GestorDeJuegoDeSalasConPreguntas implements Initializable {

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

    private int numeroDePreguntaActual = -1;

    Juego partida;
    Sala sala;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.partida = App.partida;

        sala = new Sala((int) (Math.random() * 900000) + 100000, "", true, 0);

        partida.getArrayDeSalas().add(sala);

        crearNuevaPregunta();
    }

    @FXML
    public void crearBotonPregunta(int indice) {

        Button boton = new Button((indice + 1) + " Quiz");

        boton.setPrefWidth(180);

        boton.getStyleClass().add("question-card");

        boton.setUserData(indice);

        boton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                seleccionarPregunta(event);
            }
        });

        contenedorPreguntas.getChildren().add(boton);
    }

    public void seleccionarPregunta(ActionEvent event) {

        guardarPreguntaActual();

        Button boton = (Button) event.getSource();

        int indice = (Integer) boton.getUserData();

        cargarPregunta(indice);
    }

    public void crearNuevaPregunta() {

        guardarPreguntaActual();

        Preguntas pregunta = new Preguntas();

        sala.getListaPreguntas().add(pregunta);

        int indice = sala.getListaPreguntas().size() - 1;

        crearBotonPregunta(indice);

        numeroDePreguntaActual = indice;

        limpiarCampos();
    }

    public void guardarPreguntaActual() {
        if (numeroDePreguntaActual < 0) {
            return;
        }
        try {
            String tituloPregunta = txtTituloPregunta.getText().trim();
            String respuestaRojo = txtRespuestaRojo.getText().trim();
            String respuestaAzul = txtRespuestaAzul.getText().trim();
            String respuestaAmarillo = txtRespuestaAmarillo.getText().trim();
            String respuestaVerde = txtRespuestaVerde.getText().trim();

            Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);
            pregunta.setEnunciado(tituloPregunta);

            // Limpiamos y aseguramos que el arreglo interno esté inicializado
            if (pregunta.getArregloDeRespuestasParaPreguntas() == null) {
                pregunta.setArregloDeRespuestasParaPreguntas(new ArrayList<>());
            }
            pregunta.getArregloDeRespuestasParaPreguntas().clear();

            // Agregamos las respuestas de la pantalla
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(1, respuestaRojo));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(2, respuestaAzul));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(3, respuestaAmarillo));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(4, respuestaVerde));

        } catch (Exception e) {
            System.out.println("ERROR CRÍTICO AL GUARDAR EN MEMORIA: " + e.getMessage());
            e.printStackTrace(); // Esto te dirá exactamente si el problema es en el controlador
        }
    }

    public void cargarPregunta(int indice) {

        Preguntas pregunta = sala.getListaPreguntas().get(indice);

        numeroDePreguntaActual = indice;

        txtTituloPregunta.setText(pregunta.getEnunciado());

        if (pregunta.getArregloDeRespuestasParaPreguntas().size() >= 4) {

            txtRespuestaRojo.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(0).getRespuestas());

            txtRespuestaAzul.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(1).getRespuestas());

            txtRespuestaAmarillo.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(2).getRespuestas());

            txtRespuestaVerde.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(3).getRespuestas());
        }
    }

    public void limpiarCampos() {
        txtTituloPregunta.clear();
        txtRespuestaRojo.clear();
        txtRespuestaAzul.clear();
        txtRespuestaAmarillo.clear();
        txtRespuestaVerde.clear();
    }

    private void enviarPreguntaPorSocket(Preguntas p) {
        // Validación preventiva: No enviar si el objeto interno no se llenó correctamente
        if (p.getEnunciado().isEmpty() || p.getArregloDeRespuestasParaPreguntas().size() < 4) {
            System.out.println("No se puede enviar: La pregunta en memoria está incompleta.");
            return;
        }

        try {

            String trama = "Pregunta|"
                    + p.getEnunciado() + "|"
                    + p.getArregloDeRespuestasParaPreguntas().get(0).getRespuestas() + "|"
                    + p.getArregloDeRespuestasParaPreguntas().get(1).getRespuestas() + "|"
                    + p.getArregloDeRespuestasParaPreguntas().get(2).getRespuestas() + "|"
                    + p.getArregloDeRespuestasParaPreguntas().get(3).getRespuestas();

            proyectofinaldesarrolloIII.App.escritor.println(trama);
            System.out.println("Enviado exitosamente al servidor: " + trama);

        } catch (Exception e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
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
                    + s.getCodigoSala() + "|"
                    + s.getCantidadJugadores();

            proyectofinaldesarrolloIII.App.escritor.println(trama);
            System.out.println("Enviado exitosamente al servidor: " + trama);

        } catch (Exception e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    @FXML
    public void guardarPregunta(ActionEvent event) {
        crearSala();
        guardarPreguntaActual();
        Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);
        enviarPreguntaPorSocket(pregunta);
        enviarSalaPorSocket(sala);
        System.out.println("SALA MANDADA");
        System.out.println("Pregunta mandada");
    }

    public void crearSala() {

        String titulo = txtTituloSala.getText().trim();

        if (titulo.isEmpty()) {
            AlertaParaUsar.mostrar("Error", "Debe ingresar un nombre para la sala", Alert.AlertType.WARNING);
            return;
        }
        sala.setNombreSala(titulo);
        AlertaParaUsar.mostrar("Hecho", "Sala creada y enviada a SQL", Alert.AlertType.CONFIRMATION);
    }

}
