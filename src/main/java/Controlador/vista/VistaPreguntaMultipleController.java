package Controlador.vista;

import Modelo.Preguntas;
import Modelo.Respuestas;
import red.GestorJuegoVivoCliente;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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

    private GestorJuegoVivoCliente gestorJuegoVivo;
    private Timeline temporizador;
    private int tiempoRestante;
    private int tiempoTotal;
    private boolean preguntaRespondida;

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

        System.out.println("=== DEBUG ===");
        System.out.println("Usuario actual: " + App.usuarioActual.getNombreUsuario());

        System.out.println("Sala actual: " + App.salaActual.getCodigoSala());

        System.out.println("Propietario: "
                + (App.salaActual.getPropietario() == null
                ? "NULL"
                : App.salaActual.getPropietario().getNombreUsuario()));
        // Inicializar gestor de juego en vivo
        gestorJuegoVivo = new GestorJuegoVivoCliente(
                App.usuarioActual,
                App.salaActual,
                App.preguntasActuales
        );

        // Configurar callbacks
        configurarCallbacks();

        preguntaRespondida = false;
        boxBotonSiguiente.setVisible(false);
        boxBotonSiguiente.setManaged(false);

        if (App.preguntasActuales.isEmpty()) {
            lblEnunciado.setText("No hay preguntas disponibles");
            return;
        }

        // Iniciar partida
        gestorJuegoVivo.iniciarPartida();
    }

    private void configurarCallbacks() {
        gestorJuegoVivo.setOnPreguntaCargada(() -> {
            resetearEstilosBotones();
            cargarPreguntaActual();
        });

        gestorJuegoVivo.setOnPreguntaCambiada(() -> {
            detenerTemporizador();
            resetearEstilosBotones();
            cargarPreguntaActual();
        });

        gestorJuegoVivo.setOnTiempoAgotado(() -> {
            if (!preguntaRespondida) {
                tiempoAgotado();
            }
        });

        gestorJuegoVivo.setOnFinalizarJuego(this::finalizarJuego);

        gestorJuegoVivo.setOnErrorConexion(() -> {
            try {
                App.setRoot("VistaPantallaDeIngreso");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        gestorJuegoVivo.setOnRespuestaProcesada((esCorrecta, puntos, mensaje) -> {
            preguntaRespondida = true;
            lblEstadoSeleccion.getStyleClass().clear();

            if (esCorrecta) {
                lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_CORRECTO);
            } else {
                lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_INCORRECTO);
            }
            lblEstadoSeleccion.setText(mensaje);

            if (gestorJuegoVivo.isEsPresentador()) {
                mostrarBotonSiguiente();
            }
        });

        gestorJuegoVivo.setOnAbandonoExitoso(() -> {
            try {
                App.setRoot("VistaPantallaDeIngreso");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void cargarPreguntaActual() {
        Preguntas pregunta = gestorJuegoVivo.getPreguntaActual();
        int totalPreguntas = gestorJuegoVivo.getTotalPreguntas();
        int indiceActual = gestorJuegoVivo.getPreguntaActualIndex();

        if (pregunta == null) {
            finalizarJuego();
            return;
        }

        preguntaRespondida = false;
        boxBotonSiguiente.setVisible(false);
        boxBotonSiguiente.setManaged(false);

        lblEstadoSeleccion.getStyleClass().clear();
        lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_NORMAL);

        String textoEnunciado = "Pregunta " + (indiceActual + 1) + " de "
                + totalPreguntas + ":\n" + pregunta.getEnunciado();
        lblEnunciado.setText(textoEnunciado);

        tiempoTotal = pregunta.getTiempoParaLasPreguntas();
        tiempoRestante = tiempoTotal;

        if (tiempoTotal <= 0) {
            tiempoTotal = 20;
            tiempoRestante = 20;
        }

        if (gestorJuegoVivo.isEsPresentador()) {
            configurarVistaPresentador(pregunta);
        } else {
            configurarVistaJugador(pregunta);
        }

        iniciarTemporizador();
    }

    private void configurarVistaPresentador(Preguntas pregunta) {
        lblEstadoSeleccion.setText("Los jugadores están respondiendo...");
        ocultarBotonesOpciones();

        System.out.println("=== PREGUNTA " + (gestorJuegoVivo.getPreguntaActualIndex() + 1) + " (PRESENTADOR) ===");
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
        lblEstadoSeleccion.setText("Selecciona una opción antes de que se agote el tiempo...");
        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();

        System.out.println("=== PREGUNTA " + (gestorJuegoVivo.getPreguntaActualIndex() + 1) + " (JUGADOR) ===");
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
        ocultarBotonesOpciones();
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

    private void ocultarBotonesOpciones() {
        btnOpcion1.setVisible(false);
        btnOpcion2.setVisible(false);
        btnOpcion3.setVisible(false);
        btnOpcion4.setVisible(false);
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
                new KeyFrame(Duration.seconds(1), event -> {
                    tiempoRestante--;
                    Platform.runLater(this::actualizarCronometro);

                    if (tiempoRestante <= 0) {
                        temporizador.stop();
                        Platform.runLater(() -> {
                            if (!preguntaRespondida) {
                                gestorJuegoVivo.procesarTiempoAgotado();
                                tiempoAgotado();
                            }
                        });
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

        if (!gestorJuegoVivo.isEsPresentador()) {
            deshabilitarBotones();
            lblEstadoSeleccion.getStyleClass().clear();
            lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_INCORRECTO);
            lblEstadoSeleccion.setText("Se acabó el tiempo! Respuesta incorrecta");

            Preguntas pregunta = gestorJuegoVivo.getPreguntaActual();
            if (pregunta != null && pregunta.getArregloDeRespuestasParaPreguntas() != null) {
                marcarRespuestaCorrecta(pregunta.getArregloDeRespuestasParaPreguntas());
            }
        }

        if (gestorJuegoVivo.isEsPresentador()) {
            mostrarBotonSiguiente();
        }
    }

    @FXML
    private void onResponderOpcion1() {
        procesarRespuesta(0);
    }

    @FXML
    private void onResponderOpcion2() {
        procesarRespuesta(1);
    }

    @FXML
    private void onResponderOpcion3() {
        procesarRespuesta(2);
    }

    @FXML
    private void onResponderOpcion4() {
        procesarRespuesta(3);
    }

    private void procesarRespuesta(int indiceRespuesta) {
        if (gestorJuegoVivo.isEsPresentador() || preguntaRespondida) {
            return;
        }

        detenerTemporizador();
        deshabilitarBotones();

        // Procesar respuesta a través del gestor
        gestorJuegoVivo.procesarRespuesta(indiceRespuesta, tiempoRestante);

        // Marcar visualmente la respuesta seleccionada
        Preguntas pregunta = gestorJuegoVivo.getPreguntaActual();
        if (pregunta != null) {
            ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();
            if (respuestas != null && indiceRespuesta < respuestas.size()) {
                if (!respuestas.get(indiceRespuesta).isCorrecta()) {
                    marcarBotonIncorrecto(indiceRespuesta);
                    marcarRespuestaCorrecta(respuestas);
                } else {
                    marcarBotonCorrecto(indiceRespuesta);
                }
            }
        }
    }

    private void mostrarBotonSiguiente() {
        boxBotonSiguiente.setVisible(true);
        boxBotonSiguiente.setManaged(true);
        int totalPreguntas = gestorJuegoVivo.getTotalPreguntas();
        int indiceActual = gestorJuegoVivo.getPreguntaActualIndex();

        if (indiceActual >= totalPreguntas - 1) {
            btnSiguiente.setText("Finalizar ▶");
        } else {
            btnSiguiente.setText("Siguiente ▶");
        }
        System.out.println("Botón siguiente mostrado");
    }

    @FXML
    private void onSiguientePregunta() {
        gestorJuegoVivo.siguientePregunta();
        resetearEstilosBotones();
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
                System.out.println("Respuesta correcta en índice: " + i);
                marcarBotonCorrecto(i);
                return;
            }
        }
        System.out.println("ADVERTENCIA: No se encontró respuesta correcta!");
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

    private void finalizarJuego() {
        lblEnunciado.setText("¡Fin del juego!");
        lblCronometro.setText("Todas las preguntas han terminado");
        lblEstadoSeleccion.setText("Esperando resultados finales...");
        progressTiempo.setProgress(0);
        deshabilitarBotones();
        boxBotonSiguiente.setVisible(false);
        boxBotonSiguiente.setManaged(false);
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
        detenerTemporizador();
        gestorJuegoVivo.abandonarPartida();
    }
}
