package red;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import Modelo.Usuario;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GestorJuegoVivoCliente {

    private ClienteSocket clienteSocket;
    private Sala salaActual;
    private Usuario usuarioActual;
    private List<Preguntas> preguntasActuales;
    
    private int preguntaActualIndex = 0;
    private boolean esPresentador = false;
    private boolean escuchandoServidor = true;
    private String respuestaSeleccionada;

    public GestorJuegoVivoCliente(Usuario usuarioActual, Sala salaActual, List<Preguntas> preguntas) {
        this.usuarioActual = usuarioActual;
        this.salaActual = salaActual;
        this.preguntasActuales = new ArrayList<>(preguntas);
        this.esPresentador = salaActual != null;
        this.clienteSocket = new ClienteSocket();
    }

    public void iniciarPartida() {
        if (clienteSocket.getLector() == null) {
            clienteSocket.conectar();
        }

        if (!esPresentador) {
            iniciarEscuchaServidor();
        }
    }

    public void iniciarEscuchaServidor() {
        Thread hiloEscucha = new Thread(() -> {
            try {
                BufferedReader lector = clienteSocket.getLector();
                
                while (escuchandoServidor && lector != null) {
                    String mensaje = lector.readLine();
                    if (mensaje == null) {
                        break;
                    }

                    System.out.println("Jugador recibió del servidor: " + mensaje);

                    if (mensaje.startsWith("CAMBIAR_PREGUNTA")) {
                        String[] partes = mensaje.split("\\|");
                        if (partes.length >= 2) {
                            final int nuevoIndice = Integer.parseInt(partes[1]);
                            Platform.runLater(() -> {
                                preguntaActualIndex = nuevoIndice;
                            });
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error en escucha del jugador: " + e.getMessage());
            }
        });
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

    public void enviarRespuestaAlServidor(String respuesta, int tiempoRestante) {
        try {
            if (preguntaActualIndex >= preguntasActuales.size() || usuarioActual == null) {
                return;
            }
            
            Preguntas pregunta = preguntasActuales.get(preguntaActualIndex);
            String codigoSala = pregunta.getCodigoSala() != null ? 
                               String.valueOf(pregunta.getCodigoSala()) : 
                               (salaActual != null ? String.valueOf(salaActual.getCodigoSala()) : "");
            
            String trama = "RESPUESTA|" + codigoSala + "|" 
                         + usuarioActual.getNombreUsuario() + "|" 
                         + respuesta + "|" + tiempoRestante;
                         
            PrintWriter escritor = clienteSocket.getEscritor();
            if (escritor != null) {
                escritor.println(trama);
                System.out.println("Respuesta enviada: " + trama);
            }
        } catch (Exception e) {
            System.out.println("Error al enviar respuesta: " + e.getMessage());
        }
    }

    public void siguientePregunta() {
        preguntaActualIndex++;

        if (preguntaActualIndex < preguntasActuales.size()) {
            if (esPresentador && salaActual != null) {
                String trama = "SIGUIENTE_PREGUNTA|" + salaActual.getCodigoSala() + "|" + preguntaActualIndex;
                PrintWriter escritor = clienteSocket.getEscritor();
                if (escritor != null) {
                    escritor.println(trama);
                    System.out.println("Presentador notifica: " + trama);
                }
            }
        }
    }

    public void abandonarPartida() {
        escuchandoServidor = false;
        
        if (usuarioActual != null) {
            String trama = "ABANDONAR|" + usuarioActual.getNombreUsuario();
            PrintWriter escritor = clienteSocket.getEscritor();
            if (escritor != null) {
                escritor.println(trama);
                System.out.println("Abandono notificado: " + trama);
            }
        }
    }

    public void cerrarConexion() {
        escuchandoServidor = false;
        clienteSocket.cerrarConexion();
    }

    // Getters
    public List<Preguntas> getPreguntasActuales() {
        return preguntasActuales;
    }

    public int getPreguntaActualIndex() {
        return preguntaActualIndex;
    }

    public boolean isEsPresentador() {
        return esPresentador;
    }

    public String getRespuestaSeleccionada() {
        return respuestaSeleccionada;
    }

    public void setRespuestaSeleccionada(String respuestaSeleccionada) {
        this.respuestaSeleccionada = respuestaSeleccionada;
    }

    public boolean isEscuchandoServidor() {
        return escuchandoServidor;
    }

    public void setEscuchandoServidor(boolean escuchandoServidor) {
        this.escuchandoServidor = escuchandoServidor;
    }
}