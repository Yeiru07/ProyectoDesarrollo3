package servidor_cliente;

import Controlador.GestorUsuarios; // Importamos el gestor que creaste
import MySQL.ConexionBaseDeDatos;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.PreparedStatement;

public class ManejadorDeUsuarios extends Thread {

    private Socket socketCliente;
    // Creamos una instancia del gestor para delegarle el peso de la base de datos
    private GestorUsuarios gestor = new GestorUsuarios();

    public ManejadorDeUsuarios(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try {
            BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            PrintWriter escritor = new PrintWriter(socketCliente.getOutputStream(), true);

            String datosRecibidos;
            while ((datosRecibidos = lector.readLine()) != null) {
                System.out.println("TRAMA RECIBIDA EN EL SERVIDOR: " + datosRecibidos);

                String[] partes = datosRecibidos.split("\\|", -1);
                String comando = partes[0]; // Primer elemento antes del primer "|"

                // Evaluamos qué quiere hacer el cliente usando un bloque switch básico
                switch (comando) {

                    case "REGISTRO":
                        // Formato esperado desde la UI: REGISTRO|nombreUsuario|correo|contraseña
                        String nombreReg = partes[1];
                        String correoReg = partes[2];
                        String contraReg = partes[3];

                        // Le quitamos el peso al hilo y se lo delegamos al GestorUsuarios
                        boolean registrado = gestor.registrarUsuarioEnBD(nombreReg, correoReg, contraReg);

                        if (registrado) {
                            escritor.println("OK|Usuario registrado con exito");
                        } else {
                            escritor.println("ERROR|El usuario o correo ya existe");
                        }
                        break;

                    case "LOGIN":
                        // Formato esperado desde la UI: LOGIN|nombreUsuario|contraseña
                        String nombreLog = partes[1];
                        String contraLog = partes[2];

                        // Le delegamos la consulta de validación al Gestor
                        boolean loginCorrecto = gestor.verificarLoginEnBD(nombreLog, contraLog);

                        if (loginCorrecto) {
                            escritor.println("OK|Login correcto");
                        } else {
                            escritor.println("ERROR|Usuario o contraseña incorrectos");
                        }   
                        break;

                    case "Pregunta":
                        try {
                            // Si el arreglo no tiene los 6 componentes esperados, rechaza la operación de forma segura
                            if (partes.length < 6) {
                                System.out.println("Servidor rechazó trama inválida: Tamaño del arreglo es " + partes.length);
                                escritor.println("ERROR|Formato de pregunta incompleto");
                                break;
                            }

                            guardarPreguntaNuevobd(partes);
                            escritor.println("OK: Pregunta guardada");
                        } catch (Exception e) {
                            System.out.println("Error al guardar en BD: " + e.getMessage());
                            escritor.println("Error: No se pudo guardar la pregunta");
                        }
                        break;

                    default:
                        escritor.println("ERROR|Comando no reconocido por el servidor");
                        break;
                }
            }

        } catch (Exception e) {
            System.out.println("El cliente se desconectó o hubo un error: " + e.getMessage());
        } finally {
            // Nos aseguramos de liberar el socket si el ciclo termina
            try {
                if (socketCliente != null) {
                    socketCliente.close();
                }
            } catch (Exception ex) {
                System.out.println("Error al cerrar el socket: " + ex.getMessage());
            }
        }
    }

    // Tu método original para insertar preguntas de examen
    private void guardarPreguntaNuevobd(String[] partes) throws Exception {
        java.sql.Connection conexion = ConexionBaseDeDatos.conectar();
        String sql = "INSERT INTO preguntas (enunciado, respuesta1, respuesta2, respuesta3, respuesta4) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, partes[1]);
            ps.setString(2, partes[2]);
            ps.setString(3, partes[3]);
            ps.setString(4, partes[4]);
            ps.setString(5, partes[5]);
            ps.executeUpdate();
            System.out.println("Pregunta guardada correctamente");
        } finally {
            conexion.close();
        }
    }
}
