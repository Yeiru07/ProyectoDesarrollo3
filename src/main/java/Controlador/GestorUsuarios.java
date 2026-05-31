/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.Usuario;
import java.io.IOException;
import java.util.ArrayList;
import javafx.fxml.FXML;
import proyectofinaldesarrolloIII.App;

/**
 *
 * @author sronn
 */
public class GestorUsuarios {

    Usuario usuario;
    ArrayList<Usuario> usuarioLista = new ArrayList<>();

    public void agregarUsuario(Usuario usuario) {
        usuarioLista.add(usuario);

    }

    public boolean usuarioExiste(String nombre) {
        for (Usuario usuario1 : usuarioLista) {
            if (usuario.getNombreUsuario().equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void onVolver() throws IOException {
        App.setRoot("VistaPantallaDeIngreso");
    }

    @FXML
    private void onRegistrar() throws IOException {
        App.setRoot("RegistroCreador");
    }

}
