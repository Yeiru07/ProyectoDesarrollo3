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

    /*Seccion del SceneBuilder*/
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

    Juego juego;//instancia del juego

    @FXML
    public void initialize() {
        this.juego = App.partida;//Se iguala a la global
    }

    @FXML
    private void crearUnaSala() throws IOException {
        App.setRoot("VistaCreacionQuiz");
    }

    @FXML
    private void presentarSala() throws IOException {
        App.setRoot("VistaGestorSalas");
    }

    /*Se confirma el codigo de ingreso a la sala*/
    @FXML
    public void confirmacionDeCodigo() {

        try {
            int codigoIngresado = Integer.parseInt(txtPinDelJuego.getText().trim());

            App.escritor.println("UNIR_SALA|" + codigoIngresado + "|" + App.usuarioActual.getNombreUsuario());

            String respuesta = App.lector.readLine();

            System.out.println("RESPUESTA SERVIDOR: " + respuesta);

            if (respuesta.startsWith("JUGADORES|")) {
                App.jugadoresLobby = respuesta;//Los jugadores actuales que se unieron
                ingresarPin();

            } else if (respuesta.equals("ERROR")) {
                AlertaParaUsar.mostrar("Error", "La sala no existe", Alert.AlertType.WARNING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ingresarPin() throws IOException {
        App.setRoot("VistaLobbyDeCargaParaEntrar");
    }

}
