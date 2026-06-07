package Controlador.vista;

import Controlador.GestorUsuarios; // 1. IMPORTAMOS TU GESTOR
import Modelo.Juego;
import Modelo.Sala;
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

    Juego partida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.partida = App.partida;

        // Llamamos al método para pintar las salas apenas se abre la pantalla
        verSalas();
    }

    public void verSalas() {
        // Limpiamos el catálogo visual antes de cargar
        flowPaneSalas.getChildren().clear();

        if (App.usuarioActual != null) {
            try {
                // 1. Le pedimos las salas al SERVIDOR en lugar de usar la Base de Datos directo
                String nombreDeUsuario = App.usuarioActual.getNombreUsuario();
                String tramaEnvio = "CONSULTAR_SALAS|" + nombreDeUsuario;

                App.escritor.println(tramaEnvio);
                System.out.println("Solicitando salas al servidor: " + tramaEnvio);

                // 2. Esperamos y leemos la respuesta del SERVIDOR
                String respuesta = App.lector.readLine();
                System.out.println("Respuesta del servidor: " + respuesta);

                // 3. Verificamos que la respuesta sea la correcta
                if (respuesta != null && respuesta.startsWith("RESPUESTA_SALAS|")) {

                    // Extraemos lo que viene después de la barra "|"
                    String[] partesRespuesta = respuesta.split("\\|");
                    String contenido = partesRespuesta[1];

                    if (contenido.equals("VACIO")) {
                        flowPaneSalas.getChildren().add(lblNoSalas);
                    } else {
                        // Separamos cada sala (el servidor las envía separadas por ';')
                        String[] arregloSalas = contenido.split(";");

                        // Ciclo for tradicional, sin lambdas
                        for (int i = 0; i < arregloSalas.length; i++) {

                            // Separamos los atributos de esta sala específica (separados por ',')
                            String[] datosSala = arregloSalas[i].split(",");

                            int codigo = Integer.parseInt(datosSala[0]);
                            String nombre = datosSala[1];
                            int jugadores = Integer.parseInt(datosSala[2]);

                            // Creamos el objeto Sala en memoria
                            Sala sala = new Sala(codigo, nombre, true, jugadores);

                            // Creamos la tarjeta visual y la agregamos a la pantalla
                            VBox tarjeta = crearTarjetaSala(sala);
                            flowPaneSalas.getChildren().add(tarjeta);
                        }
                    }
                } else {
                    flowPaneSalas.getChildren().add(lblNoSalas);
                }

            } catch (Exception e) {
                System.out.println("Error de conexión al obtener salas: " + e.getMessage());
                flowPaneSalas.getChildren().add(lblNoSalas);
            }
        } else {
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

        // Evento clásico para el botón de presentar
        btnPresentar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                sala.setEstado(true);
                App.salaActual = sala;

                String trama = "PRESENTAR|" + sala.getCodigoSala();
                App.escritor.println(trama);
                System.out.println("Enviado al servidor: " + trama);

                try {
                    App.setRoot("VistaLobbyDeLaPartida");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        card.getChildren().addAll(lblNombre, lblPreguntas, lblCreador, btnPresentar);
        return card;
    }
}
