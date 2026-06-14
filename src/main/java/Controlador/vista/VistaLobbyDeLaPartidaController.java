/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import Modelo.Sala;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import proyectofinaldesarrolloIII.App;

public class VistaLobbyDeLaPartidaController implements Initializable {

    /*Seccion de SceneBuilder*/
    @FXML
    private Button btnIniciarJuego;
    @FXML
    private Button btnRegresar;
    @FXML
    private Label lblPinSala;
    @FXML
    private Label lblTotalJugadores;
    @FXML
    private FlowPane flowJugadores;//Encargado de generar los nombre de los usuarios conectados a la sala

    Sala sala;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.sala = App.salaActual;//Se iguala la instancia a la global del App

        if (sala != null) {
            lblPinSala.setText("PIN: " + sala.getCodigoSala());//Traemos el codigo de la sala
        }

        escucharServidor();
        if (App.jugadoresLobby != null) {

            String datos = App.jugadoresLobby.replace("JUGADORES|", "");
            String[] jugadores = datos.split(",");
            actualizarJugadores(Arrays.asList(jugadores));
            lblTotalJugadores.setText("👤 " + jugadores.length + " participantes");

        }

    }

    /*Este metodo actualiza el tag para cada jugador*/
    public void actualizarJugadores(List<String> nombres) {
        flowJugadores.getChildren().clear();

        for (String nombre : nombres) {
            Label jugador = new Label(nombre);
            jugador.getStyleClass().add("player-tag");
            flowJugadores.getChildren().add(jugador);
        }
    }

    /*Con esto generamos el hilo para escuchar al servidor (traemos la informacion del servidor)*/
    private void escucharServidor() {
        new Thread(() -> {

            try {
                System.out.println("ESCUCHANDO SERVIDOR...");
                while (true) {
                    String mensaje = App.lector.readLine();
                    System.out.println("MENSAJE RECIBIDO: " + mensaje);
                    if (mensaje == null) {
                        break;
                    }

                    if (mensaje.startsWith("JUGADORES")) {
                        App.jugadoresLobby = mensaje;
                        String[] partes = mensaje.split("\\|");

                        if (partes.length > 1) {

                            String[] nombres = partes[1].split(",");

                            Platform.runLater(() -> {
                                actualizarJugadores(Arrays.asList(nombres));
                                lblTotalJugadores.setText("👤 " + nombres.length + " participantes");
                            });
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }

    @FXML
    public void ListoVamos() throws IOException {

        String trama = "OBTENER_PREGUNTAS|" + sala.getCodigoSala();

        App.escritor.println(trama);

        String respuesta = App.lector.readLine();

        System.out.println(respuesta);

        App.setRoot("VistaPreguntaMultiple");
    }

    @FXML
    public void regresar() throws IOException {
        App.setRoot("VistaGestorSalas");
    }
}
