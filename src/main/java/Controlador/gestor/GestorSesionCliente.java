/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.gestor;

/**
 *
 * @author sronn
 */
import red.ClienteSocket;

import java.io.IOException;

public class GestorSesionCliente {

    private final ClienteSocket cliente;

    public GestorSesionCliente(ClienteSocket cliente) {
        this.cliente = cliente;
    }

    public String registrar(String nombre,String correo, String contra) throws IOException {

        String trama
                = "REGISTRO|"
                + nombre + "|"
                + correo + "|"
                + contra;

        cliente.getEscritor().println(trama);// se manda el mensaje al servidor

        return cliente.getLector().readLine();// se lee lo que envia el servidor 
    }

    public String iniciarSesion(String usuario,String contrasena) throws IOException {

        String trama
                = "LOGIN|"
                + usuario + "|"
                + contrasena;

        cliente.getEscritor().println(trama);

        return cliente.getLector().readLine();
    }

}
