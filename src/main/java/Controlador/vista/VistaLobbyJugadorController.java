package Controlador.vista;

import Controlador.gestor.GestorLobbyJugadorCliente;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import proyectofinaldesarrolloIII.App;

public class VistaLobbyJugadorController implements Initializable {

    @FXML
    private Label lblEstadoSala;

    @FXML
    private Label lblNombreJugador;

    @FXML
    private ProgressIndicator progressLobby;

    private GestorLobbyJugadorCliente gestorLobbyJugador;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Obtener el nombre del jugador desde App
        String nombreJugador = App.usuarioActual != null
                ? App.usuarioActual.getNombreUsuario() : "Jugador";

        // Inicializar el gestor de lobby para jugador
        gestorLobbyJugador = new GestorLobbyJugadorCliente(
                nombreJugador,
                lblEstadoSala,
                lblNombreJugador,
                progressLobby
        );

        // Configurar callbacks
        gestorLobbyJugador.setOnPreguntasRecibidas(() -> {
            try {
                App.setRoot("VistaPreguntaMultiple");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        gestorLobbyJugador.setOnInicioPartida(() -> {
            try {
                App.setRoot("VistaPreguntaMultiple");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        gestorLobbyJugador.setOnErrorConexion(() -> {
            // Manejar error de conexión - volver al menú principal o login
            try {
                App.setRoot("VistaInicioSesion");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Iniciar el lobby del jugador
        gestorLobbyJugador.iniciarLobby();
    }

    /**
     * Método llamado cuando se cierra la ventana o se navega fuera Se puede
     * llamar desde el método de cierre de la ventana
     */
    public void cerrarConexion() {
        if (gestorLobbyJugador != null) {
            gestorLobbyJugador.cerrarConexion();
        }
    }

    /**
     * Método para actualizar el estado de la sala desde la UI Por ejemplo,
     * cuando el jugador ve que se une a una sala
     */
    public void actualizarEstadoSala(String estado) {
        if (lblEstadoSala != null) {
            lblEstadoSala.setText(estado);
        }
    }

    // Getters y setters opcionales
    public GestorLobbyJugadorCliente getGestorLobbyJugador() {
        return gestorLobbyJugador;
    }
}
