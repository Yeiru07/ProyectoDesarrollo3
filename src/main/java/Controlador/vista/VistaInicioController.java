package Controlador.vista;

import Controlador.gestor.GestorSesionCliente;
import Modelo.Juego;
import Modelo.Usuario;
import Utilidades.AlertaParaUsar;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import proyectofinaldesarrolloIII.App;

/**
 * @author sronn
 */
public class VistaInicioController {

    public static final GestorSesionCliente gestorSesion = new GestorSesionCliente(App.cliente);

    Juego partida;//Instancia del juego

    /*Esta es la seccion del sceneBuilder*/
    @FXML
    private Button btnIniciarSesion;
    @FXML
    private Button btnRegistrarUsuario;
    @FXML
    private PasswordField txtConfirmarContrasena;
    @FXML
    private PasswordField txtContrasenaIniciarSesion;
    @FXML
    private TextField txtRegistarUsuario;
    @FXML
    private PasswordField txtRegistrarContrasena;
    @FXML
    private TextField txtRegistrarCorreo;
    @FXML
    private TextField txtUsuarioIniciarSesion;

    @FXML
    public void initialize() {
        this.partida = App.partida;//Se iguala la instancia
    }

    /*Metodo para iniciar sesion*/
    @FXML
    void btnIniciarSesion(ActionEvent event) {
        String usuarioInicioSesion = txtUsuarioIniciarSesion.getText().trim();
        String contraInicioSesion = txtContrasenaIniciarSesion.getText().trim();

        if (usuarioInicioSesion.isEmpty() || contraInicioSesion.isEmpty()) {
            AlertaParaUsar.mostrar("Error", "Complete todos los campos de inicio de sesión", Alert.AlertType.WARNING);
            return;
        }

        try {

            String respuesta = gestorSesion.iniciarSesion(usuarioInicioSesion, contraInicioSesion);
            if (respuesta == null) {
                AlertaParaUsar.mostrar(
                        "Error",
                        "El servidor no respondió",
                        Alert.AlertType.ERROR
                );
                return;
            }
            if (respuesta != null && respuesta.startsWith("OK")) {
                AlertaParaUsar.mostrar("Éxito", "Sesión iniciada correctamente", Alert.AlertType.INFORMATION);

                App.usuarioActual = new Usuario(0, usuarioInicioSesion, "", contraInicioSesion, 0);
                App.setRoot("VistaPantallaDeIngreso");
            } else {
                String[] partesRepuesta = respuesta.split("\\|");
                String mensajeError = partesRepuesta.length > 1 ? partesRepuesta[1] : "Credenciales incorrectas";
                AlertaParaUsar.mostrar("Error", mensajeError, Alert.AlertType.ERROR);
            }

        } catch (IOException e) {
            AlertaParaUsar.mostrar("Error de Red", "No se pudo comunicar con el servidor", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    private void btnRegistrarUsuario(ActionEvent event) {
        String nombre = txtRegistarUsuario.getText().trim();
        String correo = txtRegistrarCorreo.getText().trim();
        String contra = txtRegistrarContrasena.getText().trim();
        String contraConfirmacion = txtConfirmarContrasena.getText().trim();

        if (nombre.isEmpty() || contra.isEmpty() || contraConfirmacion.isEmpty() || correo.isEmpty()) {
            AlertaParaUsar.mostrar("Error", "Complete todos los campos de registro", Alert.AlertType.WARNING);
            return;
        }

        if (!contra.equals(contraConfirmacion)) {
            AlertaParaUsar.mostrar("Error", "Las contraseñas no coinciden", Alert.AlertType.WARNING);
            return;
        }

        try {

            String respuesta = gestorSesion.registrar(nombre, correo, contra);// se envia  a la trama lo que se obtuvo de los txt

            if (respuesta != null && respuesta.startsWith("OK")) {
                limpiarRegistro();
                AlertaParaUsar.mostrar("Éxito", "Usuario registrado correctamente en el sistema", Alert.AlertType.INFORMATION);

            } else {
                String[] partesRepuesta = respuesta.split("\\|");
                String mensajeError = partesRepuesta.length > 1 ? partesRepuesta[1] : "No se pudo completar el registro";
                AlertaParaUsar.mostrar("Error de Registro", mensajeError, Alert.AlertType.ERROR);
            }

        } catch (IOException e) {
            AlertaParaUsar.mostrar("Error de Red", "Hubo un problema al enviar los datos al servidor", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void limpiarRegistro() {

        txtRegistarUsuario.clear();
        txtRegistrarCorreo.clear();
        txtRegistrarContrasena.clear();
        txtConfirmarContrasena.clear();
    }
}
