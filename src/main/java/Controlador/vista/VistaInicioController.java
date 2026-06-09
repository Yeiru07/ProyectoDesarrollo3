package Controlador.vista;

import Modelo.Juego;
import Modelo.Usuario;
import Utilidades.AlertaParaUsar;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
            //Crear la trama para el Login
            String trama = "LOGIN|" + usuarioInicioSesion + "|" + contraInicioSesion;

            //Enviar la trama al servidor usando los flujos de comunicación globales
            App.escritor.println(trama);

            //Esperar la respuesta inmediata del servidor
            String respuesta = App.lector.readLine();

            if (respuesta != null && respuesta.startsWith("OK")) {
                AlertaParaUsar.mostrar("Éxito", "Sesión iniciada correctamente", Alert.AlertType.INFORMATION);

                //AGREGADO
                App.usuarioActual = new Usuario(0, usuarioInicioSesion, "", contraInicioSesion, 0);

                // Si el login es correcto, cambia de pantalla al Lobby de Salas
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

    /*Metodo para registar los usuarios en caso de no tener credenciales*/
    @FXML
    void btnRegistrarUsuario(ActionEvent event) {
        String nombre = txtRegistarUsuario.getText().trim();
        String correo = txtRegistrarCorreo.getText().trim();
        String contra = txtRegistrarContrasena.getText().trim();
        String contraConfirmacion = txtConfirmarContrasena.getText().trim();

        //Validaciones de Interfaz Grafica
        if (nombre.isEmpty() || contra.isEmpty() || contraConfirmacion.isEmpty() || correo.isEmpty()) {
            AlertaParaUsar.mostrar("Error", "Complete todos los campos de registro", Alert.AlertType.WARNING);
            return;
        }

        if (!contra.equals(contraConfirmacion)) {
            AlertaParaUsar.mostrar("Error", "Las contraseñas no coinciden", Alert.AlertType.WARNING);
            return;
        }

        try {
            //Armar la trama de datos formateada con pipes
            String trama = "REGISTRO|" + nombre + "|" + correo + "|" + contra;

            //Enviar la trama por el Socket de forma sincrona
            App.escritor.println(trama);

            //Quedarse esperando el veredicto que calcule el Gestor en el Servidor
            String respuesta = App.lector.readLine();

            //Analizar la respuesta del Servidor y mostrársela al usuario en la pantalla
            if (respuesta != null && respuesta.startsWith("OK")) {
                AlertaParaUsar.mostrar("Éxito", "Usuario registrado correctamente en el sistema", Alert.AlertType.INFORMATION);

                // Limpiar campos tras un registro exitoso
                limpiarEspacios();
            } else {
                // Si el servidor mandó "ERROR|El usuario ya existe", extraemos el mensaje informativo
                String[] partesRepuesta = respuesta.split("\\|");
                String mensajeError = partesRepuesta.length > 1 ? partesRepuesta[1] : "No se pudo completar el registro";
                AlertaParaUsar.mostrar("Error de Registro", mensajeError, Alert.AlertType.ERROR);
            }

        } catch (IOException e) {
            AlertaParaUsar.mostrar("Error de Red", "Hubo un problema al enviar los datos al servidor", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void limpiarEspacios() {
        txtRegistarUsuario.clear();
        txtRegistrarCorreo.clear();
        txtRegistrarContrasena.clear();
        txtConfirmarContrasena.clear();
    }
}
