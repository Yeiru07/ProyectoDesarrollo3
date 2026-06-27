package Controlador.vista;

import Modelo.Sala;
import red.GestorLobbyCliente;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import proyectofinaldesarrolloIII.App;

public class VistaLobbyDeLaPartidaController implements Initializable {

    @FXML
    private Button btnIniciarJuego;
    @FXML
    private Button btnRegresar;
    @FXML
    private Label lblPinSala;
    @FXML
    private Label lblTotalJugadores;
    @FXML
    private FlowPane flowJugadores;

    private GestorLobbyCliente gestorLobby;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Sala sala = App.salaActual;

        // Inicializar el gestor de lobby
        gestorLobby = new GestorLobbyCliente(sala, flowJugadores, lblPinSala, lblTotalJugadores);

        // Configurar callbacks
        gestorLobby.setOnPreguntasRecibidas(() -> {
            try {
                App.setRoot("VistaPreguntaMultiple");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        gestorLobby.setOnErrorConexion(() -> {
            // Manejar error de conexión - por ejemplo, volver al gestor de salas
            try {
                App.setRoot("VistaGestorSalas");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Iniciar el lobby
        gestorLobby.iniciarLobby();
    }

    @FXML
    public void ListoVamos() {
        gestorLobby.solicitarPreguntas();
    }

    @FXML
    public void regresar() throws IOException {
        gestorLobby.cerrarConexion();
        App.setRoot("VistaGestorSalas");
    }
}