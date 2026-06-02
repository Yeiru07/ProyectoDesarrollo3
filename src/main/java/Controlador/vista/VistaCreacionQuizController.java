/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import Modelo.Juego;
import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
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
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import proyectofinaldesarrolloIII.App;

/**
 *
 * @author sronn
 */
public class VistaCreacionQuizController implements Initializable {

    @FXML
    private Button btnAgregarPregunta;
    @FXML
    private HBox boxRespuestaAmarilla;

    @FXML
    private HBox boxRespuestaVerde;
    @FXML
    private ComboBox<Integer> cmbLimiteDeTiempo;
    @FXML
    private ComboBox<Integer> cmbPuntosParaPregunta;

    @FXML
    private ToggleGroup grupoRespuestas;

    @FXML
    private ComboBox<String> cmbTipoDePregunta;

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

        this.partida = App.partida;//Instancia de la partida

        sala = new Sala((int) (Math.random() * 900000) + 100000, "", true, 0);//codigo de ingreso para la sala

        partida.getArrayDeSalas().add(sala);//se agrega la sala a la partida
        if (App.usuarioActual != null) {
            App.usuarioActual.getSalasAdministradas().add(sala);//se le pone una sala al usuario actual
        }
        cmbPuntosParaPregunta.getItems().addAll(10, 20, 30, 40, 50);
        cmbLimiteDeTiempo.getItems().addAll(15, 20, 30);
        cmbLimiteDeTiempo.setValue(20);
        cmbPuntosParaPregunta.setValue(10);
        cmbTipoDePregunta.getItems().addAll("Quiz", "Verdadero O Falso");

        grupoRespuestas = new ToggleGroup();

        rbRespuestaRojo.setToggleGroup(grupoRespuestas);
        rbRespuestaAzul.setToggleGroup(grupoRespuestas);
        rbRespuestaAmarillo.setToggleGroup(grupoRespuestas);
        rbRespuestaVerde.setToggleGroup(grupoRespuestas);

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

        contenedorPreguntas.getChildren().add(boton);//Este es el vbox
    }

    public void seleccionarPregunta(ActionEvent event) {

        guardarPreguntaSegunTipo();

        Button boton = (Button) event.getSource();

        int indice = (Integer) boton.getUserData();

        cargarPregunta(indice);
    }

    @FXML
    public void crearNuevaPregunta() {

        String tipoDeQuiz = cmbTipoDePregunta.getValue();

        if (tipoDeQuiz == null) {
            AlertaParaUsar.mostrar("Error", "Seleccione un tipo de pregunta", Alert.AlertType.WARNING);
            return;
        }

        if (numeroDePreguntaActual >= 0) {
            guardarPreguntaSegunTipo();
        }
        Preguntas pregunta = new Preguntas();
        pregunta.setTipoDePregunta(tipoDeQuiz);
        sala.getListaPreguntas().add(pregunta);
        int indice = sala.getListaPreguntas().size() - 1;
        crearBotonPregunta(indice);
        numeroDePreguntaActual = indice;
        limpiarCampos();
    }

    private void guardarPreguntaSegunTipo() {

        String tipo = cmbTipoDePregunta.getValue();

        if (tipo == null || numeroDePreguntaActual < 0) {
            return;
        }

        if ("Quiz".equals(tipo)) {
            guardarPreguntaActual();
        } else {
            guardarPreguntaActualVerdaderoOFalso();
        }
    }

    @FXML
    private void cambiarTipoPregunta(ActionEvent event) {

        boolean esVF = "Verdadero O Falso".equals(cmbTipoDePregunta.getValue());

        boxRespuestaAmarilla.setVisible(!esVF);
        boxRespuestaAmarilla.setManaged(!esVF);

        boxRespuestaVerde.setVisible(!esVF);
        boxRespuestaVerde.setManaged(!esVF);

        if (esVF) {

            txtRespuestaRojo.setText("Verdadero");
            txtRespuestaAzul.setText("Falso");

            txtRespuestaAmarillo.clear();
            txtRespuestaVerde.clear();

        } else {

            txtRespuestaRojo.clear();
            txtRespuestaAzul.clear();
        }
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
            int tiempoParaLasPreguntas = cmbLimiteDeTiempo.getValue();
            int puntosParaPreguntas = cmbPuntosParaPregunta.getValue();
            String tipoPregunta = cmbTipoDePregunta.getValue();

            if (tituloPregunta.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar un enunciado a la pregunta");
            }
            if (respuestaAmarillo.isEmpty() || respuestaAzul.isEmpty() || respuestaRojo.isEmpty() || respuestaVerde.isEmpty()) {
                throw new IllegalArgumentException("Debe de ingresar las 4 respuestas");
            }

            Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);
            pregunta.setEnunciado(tituloPregunta);
            pregunta.setTiempoParaLasPreguntas(tiempoParaLasPreguntas);
            pregunta.setValorPuntosPreguntas(puntosParaPreguntas);
            pregunta.setTipoDePregunta(tipoPregunta);

            // Limpiamos
            if (pregunta.getArregloDeRespuestasParaPreguntas() == null) {
                pregunta.setArregloDeRespuestasParaPreguntas(new ArrayList<>());
            }
            pregunta.getArregloDeRespuestasParaPreguntas().clear();

            // Agregamos las respuestas de la pantalla
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(1, respuestaRojo, rbRespuestaRojo.isSelected()));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(2, respuestaAzul, rbRespuestaAzul.isSelected()));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(3, respuestaAmarillo, rbRespuestaAmarillo.isSelected()));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(4, respuestaVerde, rbRespuestaVerde.isSelected()));

            if (grupoRespuestas.getSelectedToggle() == null) {
                throw new IllegalArgumentException("Debe seleccionar una respuesta correcta");
            }
        } catch (Exception e) {
            AlertaParaUsar.mostrar("Error de datos", e.getMessage(), Alert.AlertType.WARNING);
            e.printStackTrace(); 
        }
    }

    public void guardarPreguntaActualVerdaderoOFalso() {
        if (numeroDePreguntaActual < 0) {
            return;
        }
        try {
            String tituloPregunta = txtTituloPregunta.getText().trim();
            String respuestaRojo = txtRespuestaRojo.getText().trim();
            String respuestaAzul = txtRespuestaAzul.getText().trim();

            int tiempoParaLasPreguntas = cmbLimiteDeTiempo.getValue();
            int puntosParaPreguntas = cmbPuntosParaPregunta.getValue();
            String tipoPregunta = cmbTipoDePregunta.getValue();

            if (tituloPregunta.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar un enunciado a la pregunta");
            }
            if (respuestaAzul.isEmpty() || respuestaRojo.isEmpty()) {
                throw new IllegalArgumentException("Debe de ingresar las 2 respuestas");
            }

            Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);
            pregunta.setEnunciado(tituloPregunta);
            pregunta.setTiempoParaLasPreguntas(tiempoParaLasPreguntas);
            pregunta.setValorPuntosPreguntas(puntosParaPreguntas);
            pregunta.setTipoDePregunta(tipoPregunta);

            // Limpiamos
            if (pregunta.getArregloDeRespuestasParaPreguntas() == null) {
                pregunta.setArregloDeRespuestasParaPreguntas(new ArrayList<>());
            }
            pregunta.getArregloDeRespuestasParaPreguntas().clear();

            //las respuestas de la pantalla
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(1, respuestaRojo, rbRespuestaRojo.isSelected()));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(2, respuestaAzul, rbRespuestaAzul.isSelected()));

            if (grupoRespuestas.getSelectedToggle() == null) {
                throw new IllegalArgumentException("Debe seleccionar una respuesta correcta");
            }
        } catch (Exception e) {
            AlertaParaUsar.mostrar("Error de datos", e.getMessage(), Alert.AlertType.WARNING);
            e.printStackTrace(); // Esto te dira si hay problemas en el controlador
        }
    }

    public void cargarPregunta(int indice) {

        Preguntas pregunta = sala.getListaPreguntas().get(indice);

        numeroDePreguntaActual = indice;

        txtTituloPregunta.setText(pregunta.getEnunciado());

        int cantidad = pregunta.getArregloDeRespuestasParaPreguntas().size();

        if (cantidad >= 2) {
            txtRespuestaRojo.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(0).getRespuestas());
            txtRespuestaAzul.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(1).getRespuestas());
        }

        if (cantidad >= 4) {
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

        if (p.getEnunciado().isEmpty() || p.getArregloDeRespuestasParaPreguntas().isEmpty()) {
            System.out.println("No se puede enviar: Pregunta incompleta.");
            return;
        }

        try {
            String trama = "Pregunta|" + p.getEnunciado();

            for (Respuestas r : p.getArregloDeRespuestasParaPreguntas()) {
                trama += "|" + r.getRespuestas();
            }

            App.escritor.println(trama);

            System.out.println("Enviado exitosamente: " + trama);

        } catch (Exception e) {
            AlertaParaUsar.mostrar("Error", "Error al enviar: " + e.getMessage(), Alert.AlertType.WARNING);
            e.printStackTrace();

        }
    }

    private void enviarSalaPorSocket(Sala s) {
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
            AlertaParaUsar.mostrar("Hecho", "Enviado exitosamente al servidor: " + trama, Alert.AlertType.CONFIRMATION);

        } catch (Exception e) {
            AlertaParaUsar.mostrar("Error", "No se pudo conectar al servidor: " + e.getMessage(), Alert.AlertType.WARNING);
            e.printStackTrace();

        }
    }

    @FXML
    public void guardarPregunta(ActionEvent event) {
        crearSala();
        guardarPreguntaSegunTipo();
        for (Preguntas pregunta : sala.getListaPreguntas()) {
            enviarPreguntaPorSocket(pregunta);
        }
        enviarSalaPorSocket(sala);
        AlertaParaUsar.mostrar("Hecho", "Se han enviado los datos", Alert.AlertType.CONFIRMATION);
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
