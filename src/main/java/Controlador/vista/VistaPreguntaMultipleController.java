package Controlador.vista;

import Modelo.Preguntas;
import Modelo.Respuestas;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import proyectofinaldesarrolloIII.App;

public class VistaPreguntaMultipleController implements Initializable {

    @FXML
    private Label lblEnunciado;
    @FXML
    private Label lblCronometro;
    @FXML
    private Label lblEstadoSeleccion;
    @FXML
    private ProgressBar progressTiempo;
    @FXML
    private Button btnOpcion1;
    @FXML
    private Button btnOpcion2;
    @FXML
    private Button btnOpcion3;
    @FXML
    private Button btnOpcion4;
    @FXML
    private HBox boxBotonSiguiente;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnSalir;

    private Timeline temporizador;
    private int tiempoRestante;
    private int tiempoTotal;
    private boolean preguntaRespondida;
    private int preguntaActualIndex;
    private boolean esPresentador;
    private Respuestas respuestaSeleccionada;
    private boolean escuchandoServidor;

    private static final String CSS_TIMER_NORMAL = "timerLabel";
    private static final String CSS_TIMER_WARNING = "timerLabelWarning";
    private static final String CSS_TIMER_DANGER = "timerLabelDanger";
    private static final String CSS_BOTON_CORRECTO = "optionButtonCorrect";
    private static final String CSS_BOTON_INCORRECTO = "optionButtonIncorrect";
    private static final String CSS_STATUS_CORRECTO = "statusFooterLabelCorrect";
    private static final String CSS_STATUS_INCORRECTO = "statusFooterLabelIncorrect";
    private static final String CSS_STATUS_NORMAL = "statusFooterLabel";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("PREGUNTAS CARGADAS = " + App.preguntasActuales.size());

        esPresentador = App.salaActual != null;
        System.out.println("Es presentador: " + esPresentador);
        System.out.println("Usuario actual: " + App.usuarioActual.getNombreUsuario());

        preguntaRespondida = false;
        preguntaActualIndex = 0;
        escuchandoServidor = true;

        boxBotonSiguiente.setVisible(false);
        boxBotonSiguiente.setManaged(false);

        if (App.preguntasActuales.isEmpty()) {
            lblEnunciado.setText("No hay preguntas disponibles");
            return;
        }

        // Los jugadores deben escuchar cambios de pregunta del servidor
        if (!esPresentador) {
            iniciarEscuchaServidor();
        }

        cargarPreguntaActual();
    }

    /**
     * Hilo que escucha mensajes del servidor para cambiar de pregunta
     */
    private void iniciarEscuchaServidor() {
        Thread hiloEscucha = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (escuchandoServidor) {
                        String mensaje = App.lector.readLine();
                        if (mensaje == null) {
                            break;
                        }

                        System.out.println("Jugador recibio del servidor: " + mensaje);

                        if (mensaje.startsWith("CAMBIAR_PREGUNTA")) {
                            String[] partes = mensaje.split("\\|");
                            if (partes.length >= 2) {
                                final int nuevoIndice = Integer.parseInt(partes[1]);

                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        System.out.println("Jugador: cambiando a pregunta " + nuevoIndice);
                                        preguntaActualIndex = nuevoIndice;
                                        detenerTemporizador();
                                        resetearEstilosBotones();
                                        cargarPreguntaActual();
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error en escucha del jugador: " + e.getMessage());
                }
            }
        });
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

    private void cargarPreguntaActual() {
        if (preguntaActualIndex >= App.preguntasActuales.size()) {
            finalizarJuego();
            return;
        }

        preguntaRespondida = false;
        respuestaSeleccionada = null;

        boxBotonSiguiente.setVisible(false);
        boxBotonSiguiente.setManaged(false);

        lblEstadoSeleccion.getStyleClass().clear();
        lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_NORMAL);

        Preguntas pregunta = App.preguntasActuales.get(preguntaActualIndex);

        String textoEnunciado = "Pregunta " + (preguntaActualIndex + 1) + " de "
                + App.preguntasActuales.size() + ":\n" + pregunta.getEnunciado();
        lblEnunciado.setText(textoEnunciado);

        tiempoTotal = pregunta.getTiempoParaLasPreguntas();
        tiempoRestante = tiempoTotal;

        if (tiempoTotal <= 0) {
            tiempoTotal = 20;
            tiempoRestante = 20;
        }

        if (esPresentador) {
            configurarVistaPresentador(pregunta);
        } else {
            configurarVistaJugador(pregunta);
        }

        iniciarTemporizador();
    }

    private void configurarVistaPresentador(Preguntas pregunta) {
        lblEstadoSeleccion.setText("Los jugadores estan respondiendo...");
        btnOpcion1.setVisible(false);
        btnOpcion2.setVisible(false);
        btnOpcion3.setVisible(false);
        btnOpcion4.setVisible(false);

        System.out.println("=== PREGUNTA " + (preguntaActualIndex + 1) + " (PRESENTADOR) ===");
        System.out.println("Enunciado: " + pregunta.getEnunciado());
        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();
        if (respuestas != null) {
            for (int i = 0; i < respuestas.size(); i++) {
                Respuestas r = respuestas.get(i);
                System.out.println("  R" + (i + 1) + ": " + r.getRespuestas() + " | Correcta: " + r.isCorrecta());
            }
        }
    }

    private void configurarVistaJugador(Preguntas pregunta) {
        lblEstadoSeleccion.setText("Selecciona una opcion antes de que se agote el tiempo...");
        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();

        System.out.println("=== PREGUNTA " + (preguntaActualIndex + 1) + " (JUGADOR) ===");
        System.out.println("Enunciado: " + pregunta.getEnunciado());
        if (respuestas != null) {
            for (int i = 0; i < respuestas.size(); i++) {
                Respuestas r = respuestas.get(i);
                System.out.println("  R" + (i + 1) + ": " + r.getRespuestas() + " | Correcta: " + r.isCorrecta());
            }
        }

        configurarBotonesRespuesta(respuestas);
    }

    private void configurarBotonesRespuesta(ArrayList<Respuestas> respuestas) {
        btnOpcion1.setVisible(false);
        btnOpcion2.setVisible(false);
        btnOpcion3.setVisible(false);
        btnOpcion4.setVisible(false);

        restaurarClaseBoton(btnOpcion1, "kahootRed");
        restaurarClaseBoton(btnOpcion2, "kahootBlue");
        restaurarClaseBoton(btnOpcion3, "kahootGold");
        restaurarClaseBoton(btnOpcion4, "kahootGreen");

        habilitarBotones();

        if (respuestas != null) {
            if (respuestas.size() >= 1) {
                btnOpcion1.setVisible(true);
                btnOpcion1.setText(respuestas.get(0).getRespuestas());
            }
            if (respuestas.size() >= 2) {
                btnOpcion2.setVisible(true);
                btnOpcion2.setText(respuestas.get(1).getRespuestas());
            }
            if (respuestas.size() >= 3) {
                btnOpcion3.setVisible(true);
                btnOpcion3.setText(respuestas.get(2).getRespuestas());
            }
            if (respuestas.size() >= 4) {
                btnOpcion4.setVisible(true);
                btnOpcion4.setText(respuestas.get(3).getRespuestas());
            }
        }
    }

    private void restaurarClaseBoton(Button boton, String claseColor) {
        boton.getStyleClass().clear();
        boton.getStyleClass().add("optionButton");
        boton.getStyleClass().add(claseColor);
    }

    private void iniciarTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }
        actualizarCronometro();

        temporizador = new Timeline(
                new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tiempoRestante--;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                actualizarCronometro();
                            }
                        });
                        if (tiempoRestante <= 0) {
                            temporizador.stop();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    tiempoAgotado();
                                }
                            });
                        }
                    }
                })
        );
        temporizador.setCycleCount(tiempoTotal);
        temporizador.play();
    }

    private void actualizarCronometro() {
        lblCronometro.setText("Tiempo restante: " + tiempoRestante + "s");
        double progreso = (double) tiempoRestante / (double) tiempoTotal;
        progressTiempo.setProgress(progreso);

        lblCronometro.getStyleClass().clear();
        if (tiempoRestante <= 5) {
            lblCronometro.getStyleClass().add(CSS_TIMER_DANGER);
        } else if (tiempoRestante <= 10) {
            lblCronometro.getStyleClass().add(CSS_TIMER_WARNING);
        } else {
            lblCronometro.getStyleClass().add(CSS_TIMER_NORMAL);
        }
    }

    private void tiempoAgotado() {
        if (preguntaRespondida) {
            return;
        }
        preguntaRespondida = true;

        if (!esPresentador) {
            deshabilitarBotones();
            lblEstadoSeleccion.getStyleClass().clear();
            lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_INCORRECTO);
            lblEstadoSeleccion.setText("Se acabo el tiempo! Respuesta incorrecta");

            Preguntas pregunta = App.preguntasActuales.get(preguntaActualIndex);
            if (pregunta.getArregloDeRespuestasParaPreguntas() != null) {
                marcarRespuestaCorrecta(pregunta.getArregloDeRespuestasParaPreguntas());
            }
            enviarRespuestaAlServidor("TIEMPO_AGOTADO");
        }

        if (esPresentador) {
            mostrarBotonSiguiente();
        }
    }

    @FXML
    private void onResponderOpcion1() {
        if (!esPresentador) {
            procesarRespuesta(0);
        }
    }

    @FXML
    private void onResponderOpcion2() {
        if (!esPresentador) {
            procesarRespuesta(1);
        }
    }

    @FXML
    private void onResponderOpcion3() {
        if (!esPresentador) {
            procesarRespuesta(2);
        }
    }

    @FXML
    private void onResponderOpcion4() {
        if (!esPresentador) {
            procesarRespuesta(3);
        }
    }

    private void procesarRespuesta(int indiceRespuesta) {
        if (esPresentador || preguntaRespondida) {
            return;
        }
        if (App.preguntasActuales.isEmpty()) {
            return;
        }
        if (temporizador != null) {
            temporizador.stop();
        }

        preguntaRespondida = true;
        deshabilitarBotones();

        Preguntas pregunta = App.preguntasActuales.get(preguntaActualIndex);
        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();

        if (respuestas == null || indiceRespuesta >= respuestas.size()) {
            lblEstadoSeleccion.setText("Error: Respuesta no valida");
            return;
        }

        respuestaSeleccionada = respuestas.get(indiceRespuesta);
        boolean esCorrecta = respuestaSeleccionada.isCorrecta();

        System.out.println("Respuesta seleccionada: " + respuestaSeleccionada.getRespuestas() + " | Correcta: " + esCorrecta);

        if (esCorrecta) {
            lblEstadoSeleccion.getStyleClass().clear();
            lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_CORRECTO);
            lblEstadoSeleccion.setText("CORRECTO! +" + pregunta.getValorPuntosPreguntas() + " puntos");
            marcarBotonCorrecto(indiceRespuesta);
        } else {
            lblEstadoSeleccion.getStyleClass().clear();
            lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_INCORRECTO);
            lblEstadoSeleccion.setText("INCORRECTO!");
            marcarBotonIncorrecto(indiceRespuesta);
            marcarRespuestaCorrecta(respuestas);
        }

        enviarRespuestaAlServidor(respuestaSeleccionada.getRespuestas());
    }

    private void mostrarBotonSiguiente() {
        boxBotonSiguiente.setVisible(true);
        boxBotonSiguiente.setManaged(true);
        if (preguntaActualIndex >= App.preguntasActuales.size() - 1) {
            btnSiguiente.setText("Finalizar ▶");
        } else {
            btnSiguiente.setText("Siguiente ▶");
        }
        System.out.println("Boton siguiente mostrado");
    }

    @FXML
    private void onSiguientePregunta() {
        preguntaActualIndex++;

        if (preguntaActualIndex < App.preguntasActuales.size()) {
            // NOTIFICAR AL SERVIDOR para que avise a los jugadores
            if (App.salaActual != null) {
                String trama = "SIGUIENTE_PREGUNTA|" + App.salaActual.getCodigoSala() + "|" + preguntaActualIndex;
                App.escritor.println(trama);
                System.out.println("Presentador notifica: " + trama);
            }
            resetearEstilosBotones();
            cargarPreguntaActual();
        } else {
            finalizarJuego();
        }
    }

    private void marcarBotonCorrecto(int indice) {
        Button boton = obtenerBotonPorIndice(indice);
        if (boton != null) {
            boton.getStyleClass().clear();
            boton.getStyleClass().add(CSS_BOTON_CORRECTO);
        }
    }

    private void marcarBotonIncorrecto(int indice) {
        Button boton = obtenerBotonPorIndice(indice);
        if (boton != null) {
            boton.getStyleClass().clear();
            boton.getStyleClass().add(CSS_BOTON_INCORRECTO);
        }
    }

    private void marcarRespuestaCorrecta(ArrayList<Respuestas> respuestas) {
        for (int i = 0; i < respuestas.size(); i++) {
            if (respuestas.get(i).isCorrecta()) {
                System.out.println("Respuesta correcta en indice: " + i);
                marcarBotonCorrecto(i);
                return;
            }
        }
        System.out.println("ADVERTENCIA: No se encontro respuesta correcta!");
    }

    private Button obtenerBotonPorIndice(int indice) {
        switch (indice) {
            case 0:
                return btnOpcion1;
            case 1:
                return btnOpcion2;
            case 2:
                return btnOpcion3;
            case 3:
                return btnOpcion4;
            default:
                return null;
        }
    }

    private void resetearEstilosBotones() {
        restaurarClaseBoton(btnOpcion1, "kahootRed");
        restaurarClaseBoton(btnOpcion2, "kahootBlue");
        restaurarClaseBoton(btnOpcion3, "kahootGold");
        restaurarClaseBoton(btnOpcion4, "kahootGreen");
    }

    private void enviarRespuestaAlServidor(String respuesta) {
        try {
            if (App.preguntasActuales.isEmpty()) {
                return;
            }
            Preguntas pregunta = App.preguntasActuales.get(preguntaActualIndex);
            String trama = "RESPUESTA|" + pregunta.getCodigoSala() + "|"
                    + App.usuarioActual.getNombreUsuario() + "|" + respuesta + "|" + tiempoRestante;
            App.escritor.println(trama);
            System.out.println("Respuesta enviada: " + trama);
        } catch (Exception e) {
            System.out.println("Error al enviar respuesta: " + e.getMessage());
        }
    }

    private void finalizarJuego() {
        lblEnunciado.setText("Fin del juego!");
        lblCronometro.setText("Todas las preguntas han terminado");
        lblEstadoSeleccion.setText("Esperando resultados finales...");
        progressTiempo.setProgress(0);
        deshabilitarBotones();
        boxBotonSiguiente.setVisible(false);
        boxBotonSiguiente.setManaged(false);
        escuchandoServidor = false;
    }

    private void deshabilitarBotones() {
        btnOpcion1.setDisable(true);
        btnOpcion2.setDisable(true);
        btnOpcion3.setDisable(true);
        btnOpcion4.setDisable(true);
    }

    private void habilitarBotones() {
        btnOpcion1.setDisable(false);
        btnOpcion2.setDisable(false);
        btnOpcion3.setDisable(false);
        btnOpcion4.setDisable(false);
    }

    public void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }
    }

        @FXML
        private void onSalir() {
            // Detenemos el temporizador
            detenerTemporizador();

            // Detenemos la escucha del servidor
            escuchandoServidor = false;

            // Notificamos al servidor que el jugador abandona
            try {
                String trama = "ABANDONAR|" + App.usuarioActual.getNombreUsuario();
                App.escritor.println(trama);
            } catch (Exception e) {
                System.out.println("Error al notificar abandono: " + e.getMessage());
            }

            // Volvemos a la pantalla de ingreso
            try {
                App.setRoot("VistaPantallaDeIngreso");
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
