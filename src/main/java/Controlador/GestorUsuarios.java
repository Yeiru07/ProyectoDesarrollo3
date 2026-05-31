/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author sronn
 */
import Modelo.Usuario;
import MySQL.ConexionBaseDeDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javafx.fxml.FXML;

public class GestorUsuarios {

    // Lista en memoria para saber quiénes están jugando/conectados actualmente
    private ArrayList<Usuario> usuariosConectados = new ArrayList<>();

    // 1. MÉTODO PARA INSERTAR EN LA BASE DE DATOS
    public boolean registrarUsuarioEnBD(String nombre, String correo, String contra) {
        String sql = "INSERT INTO usuarios (nombreUsuario, correo, contraseña, puntuajeAcumulado) VALUES (?, ?, ?, 0.0)";

        try (Connection conexion = ConexionBaseDeDatos.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, contra);

            int filas = ps.executeUpdate();
            return filas > 0; // Si afectó filas, el registro fue exitoso

        } catch (Exception e) {
            System.out.println("Error SQL al registrar: " + e.getMessage());
            return false; // Devuelve false si el usuario o correo ya existen (duplicados)
        }
    }

    // 2. MÉTODO PARA VERIFICAR LOGIN EN LA BASE DE DATOS
    public boolean verificarLoginEnBD(String nombre, String contra) {
        String sql = "SELECT * FROM usuarios WHERE nombreUsuario = ? AND contraseña = ?";

        try (Connection conexion = ConexionBaseDeDatos.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, contra);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // True si encontró al usuario con esa contraseña
            }

        } catch (Exception e) {
            System.out.println("Error SQL en login: " + e.getMessage());
            return false;
        }
    }

    // 3. MÉTODOS PARA CONTROLAR LOGUEADOS EN VIVO (Para las salas del juego)
    public void agregarUsuarioActivo(Usuario usuario) {
        usuariosConectados.add(usuario);
    }

    public void removerUsuarioActivo(Usuario usuario) {
        usuariosConectados.remove(usuario);
    }

//    @FXML
//    private void onVolver() throws IOException {
//        App.setRoot("VistaPantallaDeIngreso");
//    }
//
//    @FXML
//    private void onRegistrar() throws IOException {
//        App.setRoot("RegistroCreador");
//    }
}
