/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Partida;
import Modelo.Usuario;
import MySQL.ConexionBaseDeDatos;
import Utilidades.AlertaParaUsar;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import proyectofinaldesarrolloIII.App;

/**
 *
 * @author sronn
 */
public class VistaInicioController {

    public Connection conexion;

    Partida partida;

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
        this.partida = App.partida;
        conexion = ConexionBaseDeDatos.conectar();

    }

    @FXML
    void btnIniciarSesion(ActionEvent event) throws IOException {
       String usuarioInicioSecion=txtUsuarioIniciarSesion.getText().trim();
       String contraInicioSesion=txtContrasenaIniciarSesion.getText().trim();
       
       
        
        App.setRoot("VIstaPantallaDeIngreso");
    }

    @FXML
    void btnRegistrarUsuario(ActionEvent event) throws SQLException {
        String nombre = txtRegistarUsuario.getText().trim();
        String correo = txtRegistrarCorreo.getText().trim();
        String contra = txtRegistrarContrasena.getText().trim();
        String contraConfirmacion = txtConfirmarContrasena.getText().trim();

        if (!contra.equalsIgnoreCase(contraConfirmacion)) {
            AlertaParaUsar.mostrar("Erorr", "Contraseñas diferentes", Alert.AlertType.WARNING);
            return;

        }

        if (nombre.isEmpty() || contra.isEmpty() || contraConfirmacion.isEmpty() || correo.isEmpty()) {
            AlertaParaUsar.mostrar("Error", "Complete todos los campos", Alert.AlertType.WARNING);
            return;
        }

        String sql = "INSERT INTO usuarios(nombreUsuario, correo,contraseña) VALUES (?,?,?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, contra);

            int filas = ps.executeUpdate();

            AlertaParaUsar.mostrar("Exito", "Cliente registrado", Alert.AlertType.INFORMATION);
            AlertaParaUsar.mostrar("Exito", "Filas Afectadas " + filas, Alert.AlertType.INFORMATION);

        }

//        if (partida.getGestor().usuarioExiste(nombre)) {
//            AlertaParaUsar.mostrar("Error", "El usuario ya existe", Alert.AlertType.WARNING);
//            return;
//        }
    }

}
