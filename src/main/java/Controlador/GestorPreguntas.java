/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Partida;
import Modelo.Preguntas;
import Modelo.Respuestas;
import MySQL.ConexionBaseDeDatos;
import Utilidades.AlertaParaUsar;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    private int numeroDePreguntaActual = -1;

    Partida partida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        partida = App.partida;

        btnAgregarPregunta.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                crearNuevaPregunta();
            }
        });

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

        partida.getListaPreguntas().add(pregunta);

        int indice = partida.getListaPreguntas().size() - 1;

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

            Preguntas pregunta = partida.getListaPreguntas().get(numeroDePreguntaActual);

            pregunta.setEnunciado(tituloPregunta);

            pregunta.getArregloDeRespuestasParaPreguntas().clear();

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(1, respuestaRojo));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(2, respuestaAzul));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(3, respuestaAmarillo));

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(4, respuestaVerde));
        } catch (Exception e) {
        }

    }

    public void cargarPregunta(int indice) {

        Preguntas pregunta = partida.getListaPreguntas().get(indice);

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

        try (Socket s = new Socket("localhost", 5000); PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {

            String trama
                    = "Pregunta|"
                    + p.getEnunciado() + "|"
                    + p.getArregloDeRespuestasParaPreguntas().get(0).getRespuestas() + "|"
                    + p.getArregloDeRespuestasParaPreguntas().get(1).getRespuestas() + "|"
                    + p.getArregloDeRespuestasParaPreguntas().get(2).getRespuestas() + "|"
                    + p.getArregloDeRespuestasParaPreguntas().get(3).getRespuestas();

            out.println(trama);

            System.out.println("Enviado: " + trama);

        } catch (Exception e) {
            System.out.println("No se pudo conectar al servidor: " + e.getMessage());
        }
    }

    @FXML
    public void guardarPregunta(ActionEvent event) {

        guardarPreguntaActual();

        Preguntas pregunta = partida.getListaPreguntas().get(numeroDePreguntaActual);

        enviarPreguntaPorSocket(pregunta);

        System.out.println("Pregunta mandada");
    }
    /*  public void guardarPreguntaEnBaseDeDatos(Preguntas p) {
        String sql = "INSERT INTO preguntas (enunciado, respuesta1, respuesta2, respuesta3, respuesta4) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexionBaseDeDatos.conectar(); PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, p.getEnunciado());
            pst.setString(2, p.getArregloDeRespuestasParaPreguntas().get(0).getRespuestas());
            pst.setString(3, p.getArregloDeRespuestasParaPreguntas().get(1).getRespuestas());
            pst.setString(4, p.getArregloDeRespuestasParaPreguntas().get(2).getRespuestas());
            pst.setString(5, p.getArregloDeRespuestasParaPreguntas().get(3).getRespuestas());

            pst.executeUpdate();
            System.out.println("¡Mascota guardada en la base de datos!");

        } catch (SQLException e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }*/

}
