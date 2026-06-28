package Controlador.gestor;

import Modelo.Usuario;
import Utilidades.AlertaParaUsar;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import proyectofinaldesarrolloIII.App;
import red.ClienteSocket;

public class GestorIngresoSalaCliente {

    private ClienteSocket clienteSocket;
    private Usuario usuarioActual;
    private String codigoSala;

    private Runnable onIngresoExitoso;
    private Runnable onIngresoFallido;
    private Runnable onErrorConexion;

    private String jugadoresLobby;
    private String respuestaInicialLobby;

    public GestorIngresoSalaCliente(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.clienteSocket = App.cliente;
    }

    /**
     * Intenta unir al usuario a una sala con el código proporcionado
     *
     * @param codigoSala Código de la sala a la que se quiere unir
     */
    public void unirASala(String codigoSala) {
        this.codigoSala = codigoSala;

        // Validar que el código sea numérico
        try {
            Integer.parseInt(codigoSala.trim());
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                AlertaParaUsar.mostrar("Error", "El código de sala debe ser numérico", Alert.AlertType.WARNING);
            });
            return;
        }

        // Conectar al servidor si no está conectado
        if (clienteSocket.getLector() == null) {
            //CPMENTADO PORQUE ESTA EN EL MAIN clienteSocket.conectar();
        }

        // Enviar solicitud en un hilo separado
        new Thread(() -> {
            try {
                PrintWriter escritor = clienteSocket.getEscritor();
                BufferedReader lector = clienteSocket.getLector();

                if (escritor == null || lector == null) {
                    Platform.runLater(() -> {
                        AlertaParaUsar.mostrar("Error", "No se pudo conectar al servidor", Alert.AlertType.ERROR);
                        if (onErrorConexion != null) {
                            onErrorConexion.run();
                        }
                    });
                    return;
                }

                // Enviar solicitud de unión
                String trama = "UNIR_SALA|" + codigoSala + "|" + usuarioActual.getNombreUsuario();
                escritor.println(trama);
                System.out.println("SOLICITUD ENVIADA: " + trama);

                // Esperar respuesta
                String respuesta = lector.readLine();
                System.out.println("RESPUESTA SERVIDOR: " + respuesta);

                if (respuesta == null) {
                    Platform.runLater(() -> {
                        AlertaParaUsar.mostrar("Error", "El servidor no respondió", Alert.AlertType.ERROR);
                        if (onErrorConexion != null) {
                            onErrorConexion.run();
                        }
                    });
                    return;
                }

                // Procesar respuesta
                if (respuesta.startsWith("JUGADORES|")) {

                    jugadoresLobby = respuesta;
                    respuestaInicialLobby = respuesta;

                    Platform.runLater(() -> {
                        if (onIngresoExitoso != null) {
                            onIngresoExitoso.run();
                        }
                    });
                } else if (respuesta.startsWith("ERROR")) {
                    Platform.runLater(() -> {
                        AlertaParaUsar.mostrar("Error", respuesta.replace("ERROR|", ""), Alert.AlertType.WARNING);
                        if (onIngresoFallido != null) {
                            onIngresoFallido.run();
                        }
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertaParaUsar.mostrar("Error", "Respuesta inesperada del servidor", Alert.AlertType.ERROR);
                        if (onIngresoFallido != null) {
                            onIngresoFallido.run();
                        }
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    AlertaParaUsar.mostrar("Error", "Error de comunicación con el servidor: " + e.getMessage(), Alert.AlertType.ERROR);
                    if (onErrorConexion != null) {
                        onErrorConexion.run();
                    }
                });
            }
        }).start();
    }

    /**
     * Intenta unir al usuario a una sala con el código proporcionado (versión
     * síncrona)
     *
     * @param codigoSala Código de la sala a la que se quiere unir
     * @return true si el ingreso fue exitoso, false en caso contrario
     */
    public boolean unirASalaSincrono(String codigoSala) {
        this.codigoSala = codigoSala;

        try {
            Integer.parseInt(codigoSala.trim());
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                AlertaParaUsar.mostrar("Error", "El código de sala debe ser numérico", Alert.AlertType.WARNING);
            });
            return false;
        }

        if (clienteSocket.getLector() == null) {
            clienteSocket.conectar();
        }

        try {
            PrintWriter escritor = clienteSocket.getEscritor();
            BufferedReader lector = clienteSocket.getLector();

            if (escritor == null || lector == null) {
                Platform.runLater(() -> {
                    AlertaParaUsar.mostrar("Error", "No se pudo conectar al servidor", Alert.AlertType.ERROR);
                });
                return false;
            }

            String trama = "UNIR_SALA|" + codigoSala + "|" + usuarioActual.getNombreUsuario();
            escritor.println(trama);
            System.out.println("SOLICITUD ENVIADA: " + trama);

            String respuesta = lector.readLine();
            System.out.println("RESPUESTA SERVIDOR: " + respuesta);

            if (respuesta == null) {
                Platform.runLater(() -> {
                    AlertaParaUsar.mostrar("Error", "El servidor no respondió", Alert.AlertType.ERROR);
                });
                return false;
            }

            if (respuesta.startsWith("JUGADORES|")) {

                jugadoresLobby = respuesta;
                respuestaInicialLobby = respuesta;

                Platform.runLater(() -> {
                    if (onIngresoExitoso != null) {
                        onIngresoExitoso.run();
                    }
                });

                return true;

            } else if (respuesta.startsWith("ERROR")) {
                Platform.runLater(() -> {
                    AlertaParaUsar.mostrar("Error", respuesta.replace("ERROR|", ""), Alert.AlertType.WARNING);
                });
                return false;
            } else {
                Platform.runLater(() -> {
                    AlertaParaUsar.mostrar("Error", "Respuesta inesperada del servidor", Alert.AlertType.ERROR);
                });
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                AlertaParaUsar.mostrar("Error", "Error de comunicación con el servidor: " + e.getMessage(), Alert.AlertType.ERROR);
            });
            return false;
        }
    }

    // Getters y Setters
    public String getJugadoresLobby() {
        return jugadoresLobby;
    }

    public void setUsuarioActual(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
    }

    public String getCodigoSala() {
        return codigoSala;
    }

    // Callbacks
    public void setOnIngresoExitoso(Runnable callback) {
        this.onIngresoExitoso = callback;
    }

    public void setOnIngresoFallido(Runnable callback) {
        this.onIngresoFallido = callback;
    }

    public void setOnErrorConexion(Runnable callback) {
        this.onErrorConexion = callback;
    }

    public void cerrarConexion() {
        // clienteSocket.cerrarConexion();
        System.out.println("Cambio de vista, se mantiene la conexión.");
    }

    public String getRespuestaInicialLobby() {
        return respuestaInicialLobby;
    }
}
