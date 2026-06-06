/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Controlador.vista;

import Modelo.Juego;
import Modelo.Sala;
import Modelo.Usuario;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import proyectofinaldesarrolloIII.App;
import static proyectofinaldesarrolloIII.App.partida;

/**
 * FXML Controller class
 *
 * @author alexl
 */
public class VistaGestorSalasController implements Initializable {

    @FXML
    private FlowPane flowPaneSalas;

    @FXML
    private Label lblNoSalas;
    Juego partida;
    Sala salas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.partida = App.partida;
        verSalas();

    }

    public void verSalas() {

        flowPaneSalas.getChildren().clear();

        for (Sala sala : App.usuarioActual.getSalasAdministradas()) {

            if (sala == null) {
                continue;
            }

            VBox tarjeta = crearTarjetaSala(sala);

            flowPaneSalas.getChildren().add(tarjeta);
        }

        if (flowPaneSalas.getChildren().isEmpty()) {
            flowPaneSalas.getChildren().add(lblNoSalas);
        }
    }

    @FXML
    public void presentarYa() throws IOException {
        App.setRoot("VistaPresentarSalas");

    }

    @FXML
    public void regresar() throws IOException {
        App.setRoot("VistaPantallaDeIngreso");
    }

    private VBox crearTarjetaSala(Sala sala) {

        VBox card = new VBox(12);
        card.getStyleClass().add("cardSala");

        card.setPrefWidth(280);
        card.setPrefHeight(180);

        Label lblNombre = new Label(sala.getNombreSala());
        lblNombre.getStyleClass().add("tituloSala");

        Label lblPreguntas = new Label(
                "Preguntas: " + sala.getListaPreguntas().size());
        lblPreguntas.getStyleClass().add("infoSala");

        Label lblCreador = new Label(
                "Creador: " + sala.getPropietario().getNombreUsuario());
        lblCreador.getStyleClass().add("infoSala");

        Button btnPresentar = new Button("▶ Presentar");
        btnPresentar.getStyleClass().add("btnPresent");

        btnPresentar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                sala.setEstado(true);

                App.salaActual = sala; // GUARDAR LA SALA SELECCIONADA

                String trama = "PRESENTAR|" + sala.getCodigoSala();

                App.escritor.println(trama);

                System.out.println("Enviado: " + trama);

                try {
                    App.setRoot("VistaPresentarSalas");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        card.getChildren().addAll(
                lblNombre,
                lblPreguntas,
                lblCreador,
                btnPresentar
        );

        return card;
    }

    public void irAVistaDeCodigo() throws IOException {
        App.setRoot("VistaPresentarSalas");
    }
}
