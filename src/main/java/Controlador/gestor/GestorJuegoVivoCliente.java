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
import proyectofinaldesarrolloIII.App;

public class GestorJuegoVivoCliente {

    private ClienteSocket clienteSocket;
    private Sala salaActual;
    private Usuario usuarioActual;
    private List<Preguntas> preguntasActuales;

    private int preguntaActualIndex = 0;
    private boolean esPresentador = false;
    private boolean escuchandoServidor = true;
    private String respuestaSeleccionada;
    private int totalPreguntas = 0;

    // Callbacks
    private Runnable onPreguntaCargada;
    private Runnable onPreguntaCambiada;
    private Runnable onTiempoAgotado;
    private Runnable onFinalizarJuego;
    private Runnable onErrorConexion;
    private TriConsumer<Boolean, Integer, String> onRespuestaProcesada;
    private Runnable onAbandonoExitoso;

    // Interfaz funcional para 3 parámetros
    @FunctionalInterface
    public interface TriConsumer<T, U, V> {

        void accept(T t, U u, V v);
    }

    public GestorJuegoVivoCliente(Usuario usuarioActual, Sala salaActual, List<Preguntas> preguntas) {
        this.usuarioActual = usuarioActual;
        this.salaActual = salaActual;
        this.preguntasActuales = new ArrayList<>(preguntas);
        this.totalPreguntas = preguntasActuales.size();
        this.clienteSocket = App.cliente;

        // Verificar si es presentador
        if (salaActual != null && usuarioActual != null) {
            String nombrePropietario = salaActual.getPropietario() != null
                    ? salaActual.getPropietario().getNombreUsuario() : "";
            this.esPresentador = nombrePropietario.equals(usuarioActual.getNombreUsuario());
        }

        System.out.println("GestorJuegoVivoCliente inicializado:");
        System.out.println("  Usuario: " + (usuarioActual != null ? usuarioActual.getNombreUsuario() : "null"));
        System.out.println("  Sala: " + (salaActual != null ? salaActual.getCodigoSala() : "null"));
        System.out.println("  Preguntas: " + preguntasActuales.size());
        System.out.println("  Es presentador: " + esPresentador
        );
    }

    public void iniciarPartida() {
        if (clienteSocket.getLector() == null) {
            clienteSocket.conectar();
        }

        if (!esPresentador) {
            iniciarEscuchaServidor();
        }

        // Cargar la primera pregunta
        if (!preguntasActuales.isEmpty()) {
            Platform.runLater(() -> {
                if (onPreguntaCargada != null) {
                    onPreguntaCargada.run();
                }
            });
        } else {
            Platform.runLater(() -> {
                if (onFinalizarJuego != null) {
                    onFinalizarJuego.run();
                }
            });
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

                    // Procesar mensajes del servidor
                    if (mensaje.startsWith("CAMBIAR_PREGUNTA")) {
                        String[] partes = mensaje.split("\\|");
                        if (partes.length >= 2) {
                            try {
                                int nuevoIndice = Integer.parseInt(partes[1]);
                                Platform.runLater(() -> {
                                    preguntaActualIndex = nuevoIndice;
                                    if (onPreguntaCambiada != null) {
                                        onPreguntaCambiada.run();
                                    }
                                });
                            } catch (NumberFormatException e) {
                                System.out.println("Error al parsear índice de pregunta: " + e.getMessage());
                            }
                        }
                    } else if (mensaje.startsWith("JUGADORES")) {
                        // Lista de jugadores actualizada
                        System.out.println("Lista de jugadores actualizada: " + mensaje);
                    } else if (mensaje.startsWith("FINALIZAR_JUEGO")) {
                        Platform.runLater(() -> {
                            if (onFinalizarJuego != null) {
                                onFinalizarJuego.run();
                            }
                        });
                        break;
                    } else if (mensaje.startsWith("RESPUESTA_PROCESADA")) {
                        // Formato: RESPUESTA_PROCESADA|correcta|puntos|mensaje
                        String[] partes = mensaje.split("\\|");
                        if (partes.length >= 4) {
                            boolean esCorrecta = Boolean.parseBoolean(partes[1]);
                            int puntos = Integer.parseInt(partes[2]);
                            String mensajeRespuesta = partes[3];
                            // Crear variables finales para el lambda
                            final boolean finalEsCorrecta = esCorrecta;
                            final int finalPuntos = puntos;
                            final String finalMensaje = mensajeRespuesta;
                            Platform.runLater(() -> {
                                if (onRespuestaProcesada != null) {
                                    onRespuestaProcesada.accept(finalEsCorrecta, finalPuntos, finalMensaje);
                                }
                            });
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error en escucha del jugador: " + e.getMessage());
                Platform.runLater(() -> {
                    if (onErrorConexion != null) {
                        onErrorConexion.run();
                    }
                });
            }
        });
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

    public void procesarRespuesta(int indiceRespuesta, int tiempoRestante) {
        if (preguntaActualIndex >= preguntasActuales.size() || usuarioActual == null) {
            return;
        }

        Preguntas pregunta = preguntasActuales.get(preguntaActualIndex);
        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();

        if (respuestas == null || indiceRespuesta >= respuestas.size()) {
            return;
        }

        Respuestas respuestaSeleccionada = respuestas.get(indiceRespuesta);
        boolean esCorrecta = respuestaSeleccionada.isCorrecta();

        // Calcular puntos basados en tiempo restante
        int puntos = 0;
        if (esCorrecta) {
            double proporcionTiempo = (double) tiempoRestante / (double) pregunta.getTiempoParaLasPreguntas();
            puntos = (int) (1000 * (0.5 + 0.5 * proporcionTiempo));
            puntos = Math.max(100, Math.min(1000, puntos));
        }

        String mensaje = esCorrecta
                ? "¡Correcto! +" + puntos + " puntos"
                : "Incorrecto. La respuesta correcta era otra.";

        // Enviar al servidor
        enviarRespuestaAlServidor(String.valueOf(indiceRespuesta), tiempoRestante);

        // Notificar a la vista (crear variables finales)
        final boolean finalEsCorrecta = esCorrecta;
        final int finalPuntos = puntos;
        final String finalMensaje = mensaje;

        Platform.runLater(() -> {
            if (onRespuestaProcesada != null) {
                onRespuestaProcesada.accept(finalEsCorrecta, finalPuntos, finalMensaje);
            }
        });
    }

    public void procesarTiempoAgotado() {
        Platform.runLater(() -> {
            if (onTiempoAgotado != null) {
                onTiempoAgotado.run();
            }
        });
    }

    public void enviarRespuestaAlServidor(String respuesta, int tiempoRestante) {
        try {
            if (preguntaActualIndex >= preguntasActuales.size() || usuarioActual == null) {
                return;
            }

            Preguntas pregunta = preguntasActuales.get(preguntaActualIndex);

            // 🔴 OBTENER CÓDIGO DE SALA CORRECTAMENTE
            String codigoSala = "";

            // Primero intentar con la salaActual del gestor
            if (salaActual != null) {
                codigoSala = String.valueOf(salaActual.getCodigoSala());
            } // Si no, intentar con la sala de App
            else if (App.salaActual != null) {
                codigoSala = String.valueOf(App.salaActual.getCodigoSala());
            } // Si no, intentar con el código de la pregunta
            else if (pregunta.getCodigoSala() != 0) {
                codigoSala = String.valueOf(pregunta.getCodigoSala());
            }

            // Si aún está vacío, usar el código de sala del cliente desde la conexión
            if (codigoSala == null || codigoSala.isEmpty()) {
                System.out.println("⚠️ ADVERTENCIA: Código de sala vacío, usando '0'");
                codigoSala = "0";
            }

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
/*asdf*/
    public void siguientePregunta() {
        int siguienteIndex = preguntaActualIndex + 1;

        if (siguienteIndex < preguntasActuales.size()) {
            preguntaActualIndex = siguienteIndex;

            if (esPresentador && salaActual != null) {
                String trama = "SIGUIENTE_PREGUNTA|" + salaActual.getCodigoSala() + "|" + preguntaActualIndex;
                PrintWriter escritor = clienteSocket.getEscritor();
                if (escritor != null) {
                    escritor.println(trama);
                    System.out.println("Presentador notifica: " + trama);
                }
            }

            Platform.runLater(() -> {
                if (onPreguntaCambiada != null) {
                    onPreguntaCambiada.run();
                }
            });
        } else {
            // No hay más preguntas, finalizar juego
            Platform.runLater(() -> {
                if (onFinalizarJuego != null) {
                    onFinalizarJuego.run();
                }
            });
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

        Platform.runLater(() -> {
            if (onAbandonoExitoso != null) {
                onAbandonoExitoso.run();
            }
        });
    }

    public void cerrarConexion() {
        escuchandoServidor = false;
        clienteSocket.cerrarConexion();
    }

    // ========== GETTERS ==========
    public Preguntas getPreguntaActual() {
        if (preguntaActualIndex < preguntasActuales.size()) {
            return preguntasActuales.get(preguntaActualIndex);
        }
        return null;
    }

    public int getPreguntaActualIndex() {
        return preguntaActualIndex;
    }

    public int getTotalPreguntas() {
        return totalPreguntas;
    }

    public boolean isEsPresentador() {
        return esPresentador;
    }

    public List<Preguntas> getPreguntasActuales() {
        return preguntasActuales;
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

    // ========== SETTERS DE CALLBACKS ==========
    public void setOnPreguntaCargada(Runnable onPreguntaCargada) {
        this.onPreguntaCargada = onPreguntaCargada;
    }

    public void setOnPreguntaCambiada(Runnable onPreguntaCambiada) {
        this.onPreguntaCambiada = onPreguntaCambiada;
    }

    public void setOnTiempoAgotado(Runnable onTiempoAgotado) {
        this.onTiempoAgotado = onTiempoAgotado;
    }

    public void setOnFinalizarJuego(Runnable onFinalizarJuego) {
        this.onFinalizarJuego = onFinalizarJuego;
    }

    public void setOnErrorConexion(Runnable onErrorConexion) {
        this.onErrorConexion = onErrorConexion;
    }

    public void setOnRespuestaProcesada(TriConsumer<Boolean, Integer, String> onRespuestaProcesada) {
        this.onRespuestaProcesada = onRespuestaProcesada;
    }

    public void setOnAbandonoExitoso(Runnable onAbandonoExitoso) {
        this.onAbandonoExitoso = onAbandonoExitoso;
    }
}
