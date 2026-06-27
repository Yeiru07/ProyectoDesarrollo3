package Controlador.gestor;

import Modelo.Sala;
import Modelo.Usuario;
import red.ClienteSocket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GestorSalaCliente {

    private Sala salaActual;
    private final ArrayList<Sala> salasTemporales;

    private final ClienteSocket cliente;
    private final PrintWriter escritor;
    private final BufferedReader lector;

    public GestorSalaCliente(ClienteSocket cliente) {
        this.cliente = cliente;
        this.escritor = cliente.getEscritor();
        this.lector = cliente.getLector();
        this.salasTemporales = new ArrayList<>();
    }

    // =====================================================
    // CREACION Y MANEJO LOCAL DE SALAS
    // =====================================================
    public Sala crearSala(Usuario propietario) {

        int codigoSala = generarCodigoSala();

        Sala sala = new Sala(
                codigoSala,
                "",
                false,
                0,
                propietario
        );

        sala.getListaDeCodigos().add(codigoSala);

        salasTemporales.add(sala);

        salaActual = sala;

        return sala;
    }

    private int generarCodigoSala() {
        return (int) (Math.random() * 900000) + 100000;
    }

    public void setNombreSala(String nombre) {

        if (salaActual != null) {
            salaActual.setNombreSala(nombre);
        }

    }

    public boolean salaTieneNombre() {

        return salaActual != null
                && salaActual.getNombreSala() != null
                && !salaActual.getNombreSala().trim().isEmpty();

    }

    public Sala getSalaActual() {
        return salaActual;
    }

    public void setSalaActual(Sala sala) {
        this.salaActual = sala;
    }

    public void limpiarSalaActual() {
        salaActual = null;
    }

    public ArrayList<Sala> getSalasTemporales() {
        return salasTemporales;
    }

    public boolean tienePreguntas() {

        return salaActual != null
                && !salaActual.getListaPreguntas().isEmpty();

    }

    // =====================================================
    // ELIMINAR PREGUNTA
    // =====================================================
    public boolean eliminarPregunta(int indice) {

        if (salaActual == null) {
            return false;
        }

        if (indice < 0
                || indice >= salaActual.getListaPreguntas().size()) {
            return false;
        }

        salaActual.getListaPreguntas().remove(indice);

        return true;
    }

    // =====================================================
    // TRAMA SALA
    // =====================================================
    public String prepararTramaSala() {

        if (salaActual == null) {
            return null;
        }

        if (salaActual.getNombreSala() == null
                || salaActual.getNombreSala().trim().isEmpty()) {
            return null;
        }

        String propietario = "";

        if (salaActual.getPropietario() != null) {
            propietario = salaActual
                    .getPropietario()
                    .getNombreUsuario();
        }

        return "Sala|"
                + salaActual.getNombreSala() + "|"
                + salaActual.getCodigoSala() + "|"
                + salaActual.isEstado() + "|"
                + salaActual.getCantidadJugadores() + "|"
                + propietario;
    }

    // =====================================================
    // ENVIAR SALA ACTUAL
    // =====================================================
    public boolean enviarSalaAlServidor() {

        String trama = prepararTramaSala();

        if (trama == null) {
            return false;
        }

        try {

            escritor.println(trama);

            System.out.println("Sala enviada:");
            System.out.println(trama);

            String respuesta = lector.readLine();

            return respuesta != null
                    && respuesta.contains("GUARDADO_OK");

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    // =====================================================
    // GUARDAR SALA ESPECIFICA
    // =====================================================
    public boolean guardarSalaEnServidor(
            Sala sala,
            String nombreUsuario) {

        try {

            String trama = "Sala|"
                    + sala.getNombreSala() + "|"
                    + sala.getCodigoSala() + "|"
                    + sala.isEstado() + "|"
                    + sala.getCantidadJugadores() + "|"
                    + nombreUsuario;

            escritor.println(trama);

            System.out.println("Sala enviada:");
            System.out.println(trama);

            String respuesta = lector.readLine();

            return respuesta != null
                    && respuesta.contains("GUARDADO_OK");

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    // =====================================================
    // GUARDAR SALA COMPLETA
    // =====================================================
    public boolean guardarSalaCompleta() {

        if (salaActual == null) {
            return false;
        }

        if (salaActual.getPropietario() == null) {
            return false;
        }

        return guardarSalaEnServidor(
                salaActual,
                salaActual
                        .getPropietario()
                        .getNombreUsuario()
        );

    }

    // =====================================================
    // CONSULTAR SALAS
    // =====================================================
    public ArrayList<Sala> consultarSalasDeUsuario(
            String nombreUsuario) {

        ArrayList<Sala> salas = new ArrayList<>();

        try {

            String trama
                    = "CONSULTAR_SALAS|" + nombreUsuario;

            escritor.println(trama);

            String respuesta = lector.readLine();

            if (respuesta == null) {
                return salas;
            }

            if (!respuesta.startsWith("RESPUESTA_SALAS|")) {
                return salas;
            }

            String[] partes = respuesta.split("\\|");

            if (partes.length < 2) {
                return salas;
            }

            String contenido = partes[1];

            if (contenido.equals("VACIO")) {
                return salas;
            }

            String[] arregloSalas
                    = contenido.split(";");

            for (String s : arregloSalas) {

                String[] datos = s.split(",");

                int codigo
                        = Integer.parseInt(datos[0]);

                String nombre
                        = datos[1];

                int jugadores
                        = Integer.parseInt(datos[2]);

                Sala sala
                        = new Sala(
                                codigo,
                                nombre,
                                true,
                                jugadores
                        );

                salas.add(sala);
            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return salas;
    }

    // =====================================================
    // PRESENTAR SALA
    // =====================================================
    public boolean presentarSala(int codigoSala) {

        try {

            String trama
                    = "PRESENTAR|" + codigoSala;

            escritor.println(trama);

            String respuesta = lector.readLine();

            return respuesta != null
                    && respuesta.startsWith("OK");

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    // =====================================================
    // UNIRSE A SALA
    // =====================================================
    public boolean unirASala(
            int codigoSala,
            String nombreJugador) {

        try {

            String trama
                    = "UNIR_SALA|"
                    + codigoSala
                    + "|"
                    + nombreJugador;

            escritor.println(trama);

            String respuesta = lector.readLine();

            return respuesta != null
                    && !respuesta.startsWith("ERROR");

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    // =====================================================
    // CERRAR SALA
    // =====================================================
    public boolean cerrarSala(int codigoSala) {

        try {

            escritor.println(
                    "CERRAR_SALA|" + codigoSala
            );

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    // =====================================================
    // OBTENER PREGUNTAS
    // =====================================================
    public boolean obtenerPreguntasDeSala(
            int codigoSala) {

        try {

            escritor.println(
                    "OBTENER_PREGUNTAS|"
                    + codigoSala
            );

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }

    // =====================================================
    // INICIAR JUEGO
    // =====================================================
    public boolean iniciarJuegoEnSala(
            int codigoSala) {

        try {

            escritor.println(
                    "INICIAR_JUEGO|"
                    + codigoSala
            );

            return true;

        } catch (Exception e) {

            e.printStackTrace();
            return false;

        }

    }
}
