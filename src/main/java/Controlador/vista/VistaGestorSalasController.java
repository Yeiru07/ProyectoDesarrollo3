package Controlador.vista;

import Modelo.Juego;
import Modelo.Sala;
import Controlador.gestor.GestorSalaCliente;
import red.ClienteSocket;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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

public class VistaGestorSalasController implements Initializable {

    @FXML
    private FlowPane flowPaneSalas;
    @FXML
    private Label lblNoSalas;

    private Juego partida;
    private GestorSalaCliente gestorSala;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (App.cliente == null || !App.cliente.estaConectado()) {
            App.cliente = new ClienteSocket();
            App.cliente.conectar();
        }
        this.partida = App.partida;
        this.gestorSala = new GestorSalaCliente(App.cliente);
        verSalas();
    }

    public void verSalas() {
        flowPaneSalas.getChildren().clear();

        if (App.usuarioActual != null) {
            try {
                ArrayList<Sala> salas = gestorSala.consultarSalasDeUsuario(
                    App.usuarioActual.getNombreUsuario()
                );

                if (salas.isEmpty()) {
                    flowPaneSalas.getChildren().add(lblNoSalas);
                } else {
                    for (int i = 0; i < salas.size(); i++) {
                        Sala sala = salas.get(i);
                        VBox tarjeta = crearTarjetaSala(sala);
                        flowPaneSalas.getChildren().add(tarjeta);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al obtener salas: " + e.getMessage());
                flowPaneSalas.getChildren().add(lblNoSalas);
            }
        } else {
            flowPaneSalas.getChildren().add(lblNoSalas);
        }
    }

    @FXML
    public void regresar() throws IOException {
        App.setRoot("VistaPantallaDeIngreso");
    }

    private VBox crearTarjetaSala(final Sala sala) {
        VBox card = new VBox(12);
        card.getStyleClass().add("cardSala");
        card.setPrefWidth(280);
        card.setPrefHeight(180);

        Label lblNombre = new Label(sala.getNombreSala());
        lblNombre.getStyleClass().add("tituloSala");

        Label lblPreguntas = new Label("PIN de acceso: " + sala.getCodigoSala());
        lblPreguntas.getStyleClass().add("infoSala");

        Label lblCreador = new Label("Creador: " + App.usuarioActual.getNombreUsuario());
        lblCreador.getStyleClass().add("infoSala");

        Button btnPresentar = new Button("▶ Presentar");
        btnPresentar.getStyleClass().add("btnPresent");

        // Usamos clase interna anonima en lugar de lambda
        btnPresentar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean exito = gestorSala.presentarSala(sala.getCodigoSala());
                if (exito) {
                    sala.setPropietario(App.usuarioActual);
                    sala.getArrayDeUsuarios().clear();
                    App.salaActual = sala;
                    App.esPresentador = true;
                    App.jugadoresLobby = null;
                    App.respuestaLobby = null;
                    App.rankingActual = "";
                    App.preguntasActuales.clear();
                    try {
                        App.setRoot("VistaLobbyDeLaPartida");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        card.getChildren().addAll(lblNombre, lblPreguntas, lblCreador, btnPresentar);
        return card;
    }
}
