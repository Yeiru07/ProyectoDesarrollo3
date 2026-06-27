package Controlador.gestor;

import Modelo.Sala;
import Modelo.Usuario;
import red.ClienteSocket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class GestorSalaCliente {

    private Sala salaActual;
    private final ArrayList<Sala> salasTemporales = new ArrayList<>();
    private final ClienteSocket cliente;
    private final PrintWriter escritor;
    private final BufferedReader lector;

    public GestorSalaCliente(ClienteSocket cliente) {
        this.cliente = cliente;
        this.escritor = cliente.getEscritor();
        this.lector = cliente.getLector();
    }

    // ==================== METODOS LOCALES ====================
    public Sala crearSala(Usuario propietario) {
        int codigoSala = generarCodigoSala();
        Sala sala = new Sala(codigoSala, "", false, 0, propietario);
        sala.getListaDeCodigos().add(codigoSala);
        salasTemporales.add(sala);
        this.salaActual = sala;
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

    public Sala getSalaActual() {
        return salaActual;
    }

    public void setSalaActual(Sala sala) {
        this.salaActual = sala;
    }

    public boolean tienePreguntas() {
        return salaActual != null && !salaActual.getListaPreguntas().isEmpty();
    }

    public void eliminarPregunta(int indice) {
        if (salaActual != null && indice >= 0 && indice < salaActual.getListaPreguntas().size()) {
            salaActual.getListaPreguntas().remove(indice);
        }
    }

    // ==================== METODOS DE COMUNICACION CON SERVIDOR ====================
    /**
     * PREPARA LA TRAMA PARA ENVIAR UNA SALA AL SERVIDOR
     */
    public String prepararTramaSala() {
        if (salaActual == null || salaActual.getNombreSala().isEmpty()) {
            return null;
        }
        return "Sala|" + salaActual.getNombreSala() + "|"
                + salaActual.getCodigoSala() + "|"
                + salaActual.isEstado() + "|"
                + salaActual.getCantidadJugadores() + "|"
                + salaActual.getPropietario().getIdUsuario();
    }

    /**
     * ENVIA LA SALA ACTUAL AL SERVIDOR (METODO QUE NECESITAS)
     */
    public boolean enviarSalaAlServidor() {
        String trama = prepararTramaSala();
        if (trama == null) {
            return false;
        }

        try {
            escritor.println(trama);
            System.out.println("Trama de sala enviada: " + trama);

            String confirmacion = lector.readLine();
            if (confirmacion != null && confirmacion.equals("GUARDADO_OK")) {
                return true;
            } else {
                System.err.println("Error al enviar sala: " + confirmacion);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * GUARDA UNA SALA ESPECIFICA EN EL SERVIDOR
     */
    public boolean guardarSalaEnServidor(Sala sala, String nombreUsuario) {
        String trama = "Sala|" + sala.getNombreSala() + "|"
                + sala.getCodigoSala() + "|"
                + sala.isEstado() + "|"
                + sala.getCantidadJugadores() + "|"
                + nombreUsuario;

        try {
            escritor.println(trama);
            System.out.println("Trama de sala enviada: " + trama);

            String confirmacion = lector.readLine();
            if (confirmacion != null && confirmacion.equals("GUARDADO_OK")) {
                return true;
            } else {
                System.err.println("Error al enviar sala: " + confirmacion);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CONSULTA LAS SALAS DE UN USUARIO
     */
    public ArrayList<Sala> consultarSalasDeUsuario(String nombreUsuario) {
        ArrayList<Sala> salas = new ArrayList<>();
        String tramaEnvio = "CONSULTAR_SALAS|" + nombreUsuario;

        try {
            escritor.println(tramaEnvio);
            System.out.println("Solicitando salas al servidor: " + tramaEnvio);

            String respuesta = lector.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);

            if (respuesta != null && respuesta.startsWith("RESPUESTA_SALAS|")) {
                String[] partesRespuesta = respuesta.split("\\|");
                String contenido = partesRespuesta[1];

                if (!contenido.equals("VACIO")) {
                    String[] arregloSalas = contenido.split(";");

                    for (int i = 0; i < arregloSalas.length; i++) {
                        String[] datosSala = arregloSalas[i].split(",");
                        int codigo = Integer.parseInt(datosSala[0]);
                        String nombre = datosSala[1];
                        int jugadores = Integer.parseInt(datosSala[2]);

                        Sala sala = new Sala(codigo, nombre, true, jugadores);
                        salas.add(sala);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error de conexión al obtener salas: " + e.getMessage());
        }

        return salas;
    }

    /**
     * PRESENTA UNA SALA (LA MARCA COMO ACTIVA)
     */
    public boolean presentarSala(int codigoSala) {
        String trama = "PRESENTAR|" + codigoSala;

        try {
            escritor.println(trama);
            System.out.println("Enviado al servidor: " + trama);

            String respuesta = lector.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);

            if (respuesta != null && respuesta.startsWith("OK|Sala presentada")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * UNE UN JUGADOR A UNA SALA
     */
    public boolean unirASala(int codigoSala, String nombreJugador) {
        String trama = "UNIR_SALA|" + codigoSala + "|" + nombreJugador;

        try {
            escritor.println(trama);
            System.out.println("Uniendo a sala: " + trama);

            String respuesta = lector.readLine();
            System.out.println("Respuesta del servidor: " + respuesta);

            if (respuesta != null && !respuesta.startsWith("ERROR")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * CIERRA UNA SALA
     */
    public boolean cerrarSala(int codigoSala) {
        String trama = "CERRAR_SALA|" + codigoSala;

        try {
            escritor.println(trama);
            System.out.println("Cerrando sala: " + trama);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * OBTIENE LAS PREGUNTAS DE UNA SALA
     */
    public boolean obtenerPreguntasDeSala(int codigoSala) {
        String trama = "OBTENER_PREGUNTAS|" + codigoSala;

        try {
            escritor.println(trama);
            System.out.println("Solicitando preguntas: " + trama);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * INICIA EL JUEGO EN UNA SALA
     */
    public boolean iniciarJuegoEnSala(int codigoSala) {
        String trama = "INICIAR_JUEGO|" + codigoSala;

        try {
            escritor.println(trama);
            System.out.println("Iniciando juego: " + trama);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
