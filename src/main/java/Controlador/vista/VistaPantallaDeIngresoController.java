package Controlador.vista;

import Controlador.gestor.GestorIngresoSalaCliente;
import Modelo.Juego;
import Modelo.Sala;
import Modelo.Usuario;
import Utilidades.AlertaParaUsar;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import proyectofinaldesarrolloIII.App;

/**
 * Controlador para la pantalla de ingreso a una sala de juego
 *
 * @author sronn
 */
public class VistaPantallaDeIngresoController {

    /* Sección del SceneBuilder */
    @FXML
    private Button btnCrearSala;
    @FXML
    private Button btnIngresar;
    @FXML
    private Button btnPresentarJuego;
    @FXML
    private Button btnRegistrarse;
    @FXML
    private TextField txtPinDelJuego;

    private Juego juego;
    private GestorIngresoSalaCliente gestorIngresoSala;

    @FXML
    public void initialize() {
        this.juego = App.partida;

        // Inicializar el gestor de ingreso a sala
        Usuario usuarioActual = App.usuarioActual;
        if (usuarioActual != null) {
            gestorIngresoSala = new GestorIngresoSalaCliente(usuarioActual);

            // Configurar callbacks
            gestorIngresoSala.setOnIngresoExitoso(() -> {
                try {

                    // Guardar los jugadores del lobby
                    App.jugadoresLobby = gestorIngresoSala.getJugadoresLobby();

                    // Guardar la primera respuesta del servidor
                    App.respuestaLobby = gestorIngresoSala.getRespuestaInicialLobby();

                    ingresarPin();

                } catch (IOException e) {
                    e.printStackTrace();
                    AlertaParaUsar.mostrar(
                            "Error",
                            "No se pudo cargar el lobby: " + e.getMessage(),
                            Alert.AlertType.ERROR
                    );
                }
            });

            gestorIngresoSala.setOnIngresoFallido(() -> {
                // Limpiar el campo de texto para que el usuario pueda intentar nuevamente
                txtPinDelJuego.clear();
                txtPinDelJuego.requestFocus();
            });

            gestorIngresoSala.setOnErrorConexion(() -> {
                try {
                    // Volver a la pantalla anterior en caso de error de conexión
                    App.setRoot("VistaLogging");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.err.println("Usuario actual no disponible en VistaPantallaDeIngresoController");
        }
    }

    @FXML
    private void crearUnaSala() throws IOException {
        // Cerrar conexión anterior si existe
        if (gestorIngresoSala != null) {
            gestorIngresoSala.cerrarConexion();
        }
        App.setRoot("VistaCreacionQuiz");
    }

    @FXML
    private void presentarSala() throws IOException {
        // Cerrar conexión anterior si existe
        if (gestorIngresoSala != null) {
            gestorIngresoSala.cerrarConexion();
        }
        App.setRoot("VistaGestorSalas");
    }

    /**
     * Confirma el código de ingreso a la sala
     */
    @FXML
    public void confirmacionDeCodigo() {

        String codigoIngresado = txtPinDelJuego.getText().trim();

        if (codigoIngresado.isEmpty()) {
            AlertaParaUsar.mostrar(
                    "Atención",
                    "Por favor ingrese un código de sala",
                    Alert.AlertType.WARNING);
            txtPinDelJuego.requestFocus();
            return;
        }

        if (gestorIngresoSala == null) {
            AlertaParaUsar.mostrar(
                    "Error",
                    "No se pudo conectar al servidor",
                    Alert.AlertType.ERROR);
            return;
        }

        // Guardar la sala actual para que esté disponible en todo el juego
        App.salaActual = new Sala(
                Integer.parseInt(codigoIngresado),
                "",
                false,
                0
        );
        App.esPresentador = false;

        // Deshabilitar el botón mientras se procesa la solicitud
        btnIngresar.setDisable(true);

        // Intentar unirse a la sala
        gestorIngresoSala.unirASala(codigoIngresado);
    }

    @FXML
    private void ingresarPin() throws IOException {
        App.setRoot("VistaLobbyDeCargaParaEntrar");
    }

    /**
     * Método llamado cuando se cierra la ventana o se navega fuera
     */
    public void cerrarConexion() {
        if (gestorIngresoSala != null) {
            gestorIngresoSala.cerrarConexion();
        }
    }

    // Getters y Setters
    public GestorIngresoSalaCliente getGestorIngresoSala() {
        return gestorIngresoSala;
    }

    public void setGestorIngresoSala(GestorIngresoSalaCliente gestorIngresoSala) {
        this.gestorIngresoSala = gestorIngresoSala;
    }
}
