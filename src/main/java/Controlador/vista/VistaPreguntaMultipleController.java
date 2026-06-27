package Controlador.vista;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
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

    // Variables para el temporizador
    private Timeline temporizador;
    private int tiempoRestante;
    private int tiempoTotal;
    private boolean preguntaRespondida;
    private int preguntaActualIndex;
    private boolean esPresentador;  // NUEVO: indica si este usuario es el presentador

    private Respuestas respuestaSeleccionada;

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

        // Determinamos si este usuario es el presentador (dueño de la sala)
        esPresentador = esUsuarioPresentador();
        System.out.println("Es presentador: " + esPresentador);

        preguntaRespondida = false;
        preguntaActualIndex = 0;

        // Ocultamos el boton siguiente al inicio
        boxBotonSiguiente.setVisible(false);
        boxBotonSiguiente.setManaged(false);

        if (App.preguntasActuales.isEmpty()) {
            lblEnunciado.setText("No hay preguntas disponibles");
            return;
        }

        cargarPreguntaActual();
    }

    /**
     * Determina si el usuario actual es el presentador (dueño de la sala).
     * Compara el nombre del usuario actual con el propietario de la sala.
     */
    private boolean esUsuarioPresentador() {
        if (App.salaActual == null || App.usuarioActual == null) {
            return false;
        }

        Sala sala = App.salaActual;

        // Verificar si la sala tiene propietario
        if (sala.getPropietario() != null) {
            String nombrePropietario = sala.getPropietario().getNombreUsuario();
            String nombreActual = App.usuarioActual.getNombreUsuario();
            return nombrePropietario != null && nombrePropietario.equals(nombreActual);
        }

        return false;
    }

    /**
     * Carga la pregunta actual y configura el temporizador.
     */
    private void cargarPreguntaActual() {
        if (preguntaActualIndex >= App.preguntasActuales.size()) {
            finalizarJuego();
            return;
        }

        preguntaRespondida = false;
        respuestaSeleccionada = null;
        lblEstadoSeleccion.setText("Selecciona una opcion antes de que se agote el tiempo...");

        // Ocultamos el boton siguiente
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

        // Imprimir las respuestas para depurar
        System.out.println("=== PREGUNTA " + (preguntaActualIndex + 1) + " ===");
        System.out.println("Enunciado: " + pregunta.getEnunciado());
        System.out.println("Tipo: " + pregunta.getTipoDePregunta());
        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();
        if (respuestas != null) {
            for (int i = 0; i < respuestas.size(); i++) {
                Respuestas r = respuestas.get(i);
                System.out.println("  Respuesta " + (i + 1) + ": " + r.getRespuestas() + " | Correcta: " + r.isCorrecta());
            }
        }

        configurarBotonesRespuesta(respuestas);
        iniciarTemporizador();
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
        deshabilitarBotones();

        lblEstadoSeleccion.getStyleClass().clear();
        lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_INCORRECTO);
        lblEstadoSeleccion.setText("Se acabo el tiempo! Respuesta incorrecta");

        Preguntas pregunta = App.preguntasActuales.get(preguntaActualIndex);
        if (pregunta.getArregloDeRespuestasParaPreguntas() != null) {
            marcarRespuestaCorrecta(pregunta.getArregloDeRespuestasParaPreguntas());
        }

        enviarRespuestaAlServidor("TIEMPO_AGOTADO");

        // Solo mostrar boton siguiente al presentador
        if (esPresentador) {
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
        if (preguntaRespondida) {
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
            if (esPresentador) {
                mostrarBotonSiguiente();
            }
            return;
        }

        respuestaSeleccionada = respuestas.get(indiceRespuesta);
        boolean esCorrecta = respuestaSeleccionada.isCorrecta();

        System.out.println("Respuesta seleccionada: " + respuestaSeleccionada.getRespuestas());
        System.out.println("Es correcta: " + esCorrecta);

        if (esCorrecta) {
            lblEstadoSeleccion.getStyleClass().clear();
            lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_CORRECTO);
            lblEstadoSeleccion.setText("CORRECTO! +" + pregunta.getValorPuntosPreguntas() + " puntos");
            marcarBotonCorrecto(indiceRespuesta);
        } else {
            lblEstadoSeleccion.getStyleClass().clear();
            lblEstadoSeleccion.getStyleClass().add(CSS_STATUS_INCORRECTO);
            lblEstadoSeleccion.setText("INCORRECTO! La respuesta era incorrecta");
            marcarBotonIncorrecto(indiceRespuesta);
            marcarRespuestaCorrecta(respuestas);
        }

        enviarRespuestaAlServidor(respuestaSeleccionada.getRespuestas());

        // Solo mostrar boton siguiente al presentador
        if (esPresentador) {
            mostrarBotonSiguiente();
        }
    }

    private void mostrarBotonSiguiente() {
        // Solo se muestra si es el presentador
        if (!esPresentador) {
            return;
        }

        boxBotonSiguiente.setVisible(true);
        boxBotonSiguiente.setManaged(true);

        if (preguntaActualIndex >= App.preguntasActuales.size() - 1) {
            btnSiguiente.setText("Finalizar ▶");
        } else {
            btnSiguiente.setText("Siguiente ▶");
        }
    }

    @FXML
    private void onSiguientePregunta() {
        preguntaActualIndex++;

        if (preguntaActualIndex < App.preguntasActuales.size()) {
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
                System.out.println("Respuesta correcta encontrada en indice: " + i);
                marcarBotonCorrecto(i);
                return; // Solo hay una correcta
            }
        }
        System.out.println("ADVERTENCIA: No se encontro ninguna respuesta marcada como correcta!");
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
            String nombreUsuario = App.usuarioActual.getNombreUsuario();

            String trama = "RESPUESTA|"
                    + pregunta.getCodigoSala() + "|"
                    + nombreUsuario + "|"
                    + respuesta + "|"
                    + tiempoRestante;

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

        try {
            String trama = "FIN_PREGUNTAS|" + App.usuarioActual.getNombreUsuario();
            App.escritor.println(trama);
        } catch (Exception e) {
            System.out.println("Error al notificar fin: " + e.getMessage());
        }
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
}
