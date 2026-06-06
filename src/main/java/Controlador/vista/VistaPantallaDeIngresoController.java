/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

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
 *
 * @author sronn
 */
public class VistaPantallaDeIngresoController {

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
    Juego juego;

    @FXML
    public void initialize() {
        this.juego = App.partida;
    }

    @FXML
    private void crearUnaSala() throws IOException {
        App.setRoot("VistaCreacionQuiz");
    }

    @FXML
    private void presentarSala() throws IOException {
        App.setRoot("VistaGestorSalas");
    }

    public void confirmacionDeCodigo() throws IOException {
        try {
            int codigoIngresado = Integer.parseInt(txtPinDelJuego.getText().trim());
            Sala sala = juego.buscarSala(codigoIngresado);
            if (codigoIngresado==0) {
                throw new IllegalStateException("Ingrese un codigo valido!");
            }

            if (sala != null) {
                Usuario jugador = App.usuarioActual;
                //TENGOQ QUE PONER EL NOMBRE EN CADA ETIQUETA jugador.setNombreUsuario("Jugador");
                sala.agregarJugador(jugador);
                App.salaActual = sala;
                ingresarPin();
            } else {
                AlertaParaUsar.mostrar("ERRO", "La partida no exits", Alert.AlertType.WARNING);

            }
        } catch (Exception e) {
            AlertaParaUsar.mostrar("Error", e.getMessage(), Alert.AlertType.WARNING);
        }

    }

    @FXML
    private void ingresarPin() throws IOException {
        App.setRoot("VistaLobbyDeCargaParaEntrar");
    }

}
