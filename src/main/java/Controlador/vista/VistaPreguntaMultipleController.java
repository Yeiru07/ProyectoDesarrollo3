package Controlador.vista;

import Modelo.Preguntas;
import Modelo.Respuestas;
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
import javafx.util.Duration;
import proyectofinaldesarrolloIII.App;

/**
 * Controlador de la vista de pregunta multiple.
 *
 * Maneja la visualizacion de preguntas, el temporizador de cuenta regresiva, la
 * seleccion de respuestas por el jugador y el envio de la respuesta al
 * servidor.
 */
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

    // Variables para el temporizador
    private Timeline temporizador;
    private int tiempoRestante;          // Tiempo restante en segundos
    private int tiempoTotal;             // Tiempo total de la pregunta
    private boolean preguntaRespondida;   // Para evitar multiples respuestas
    private int preguntaActualIndex;      // Indice de la pregunta actual

    // Variable para almacenar la respuesta seleccionada
    private Respuestas respuestaSeleccionada;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("PREGUNTAS CARGADAS = " + App.preguntasActuales.size());

        // Inicializamos el estado
        preguntaRespondida = false;
        preguntaActualIndex = 0;

        // Si no hay preguntas, salimos
        if (App.preguntasActuales.isEmpty()) {
            lblEnunciado.setText("No hay preguntas disponibles");
            return;
        }

        // Cargamos la primera pregunta
        cargarPreguntaActual();
    }

    /**
     * Carga la pregunta actual y configura el temporizador.
     */
    private void cargarPreguntaActual() {
        // Verificamos que haya preguntas disponibles
        if (preguntaActualIndex >= App.preguntasActuales.size()) {
            // No hay mas preguntas, notificamos fin del juego
            lblEnunciado.setText("Fin del juego");
            lblCronometro.setText("Todas las preguntas han terminado");
            deshabilitarBotones();
            return;
        }

        // Reiniciamos el estado de respuesta
        preguntaRespondida = false;
        respuestaSeleccionada = null;
        lblEstadoSeleccion.setText("Selecciona una opcion antes de que se agote el tiempo...");

        // Obtenemos la pregunta actual
        Preguntas pregunta = App.preguntasActuales.get(preguntaActualIndex);

        // Configuramos el enunciado
        lblEnunciado.setText(pregunta.getEnunciado());

        // Obtenemos el tiempo de la pregunta
        tiempoTotal = pregunta.getTiempoParaLasPreguntas();
        tiempoRestante = tiempoTotal;

        // Si no tiene tiempo definido, usamos 20 segundos por defecto
        if (tiempoTotal <= 0) {
            tiempoTotal = 20;
            tiempoRestante = 20;
        }

        // Configuramos las respuestas
        configurarBotonesRespuesta(pregunta.getArregloDeRespuestasParaPreguntas());

        // Iniciamos el temporizador
        iniciarTemporizador();
    }

    /**
     * Configura los botones con las respuestas de la pregunta. Muestra solo los
     * botones necesarios segun la cantidad de respuestas.
     *
     * @param respuestas Lista de respuestas de la pregunta
     */
    private void configurarBotonesRespuesta(ArrayList<Respuestas> respuestas) {
        // Primero ocultamos todos los botones
        btnOpcion1.setVisible(false);
        btnOpcion2.setVisible(false);
        btnOpcion3.setVisible(false);
        btnOpcion4.setVisible(false);

        // Habilitamos todos los botones (por si estaban deshabilitados)
        habilitarBotones();

        // Quitamos estilos anteriores
        resetearEstilosBotones();

        // Mostramos los botones segun la cantidad de respuestas
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

    /**
     * Inicia el temporizador de cuenta regresiva. Cada segundo actualiza el
     * cronometro y la barra de progreso. Cuando el tiempo se acaba, evalua la
     * respuesta como incorrecta.
     */
    private void iniciarTemporizador() {
        // Detenemos cualquier temporizador anterior
        if (temporizador != null) {
            temporizador.stop();
        }

        // Actualizamos la etiqueta y barra inicial
        actualizarCronometro();

        // Creamos un nuevo temporizador que se ejecuta cada segundo
        temporizador = new Timeline(
                new KeyFrame(Duration.seconds(1), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        tiempoRestante--;

                        // Actualizamos la interfaz en el hilo de JavaFX
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                actualizarCronometro();
                            }
                        });

                        // Si el tiempo se acabo
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

        // Configuramos el temporizador para que se repita (tiempoTotal veces)
        temporizador.setCycleCount(tiempoTotal);
        temporizador.play();
    }

    /**
     * Actualiza la etiqueta del cronometro y la barra de progreso.
     */
    private void actualizarCronometro() {
        lblCronometro.setText("Tiempo restante: " + tiempoRestante + "s");

        // Actualizamos la barra de progreso (1.0 = lleno, 0.0 = vacio)
        double progreso = (double) tiempoRestante / (double) tiempoTotal;
        progressTiempo.setProgress(progreso);

        // Cambiamos el color del texto segun el tiempo restante
        if (tiempoRestante <= 5) {
            lblCronometro.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else if (tiempoRestante <= 10) {
            lblCronometro.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
        } else {
            lblCronometro.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        }
    }

    /**
     * Se ejecuta cuando el tiempo se agota sin que el jugador haya respondido.
     * Marca la pregunta como incorrecta y envia la notificacion al servidor.
     */
    private void tiempoAgotado() {
        if (preguntaRespondida) {
            return; // Ya se respondio, no hacemos nada
        }

        preguntaRespondida = true;
        deshabilitarBotones();
        lblEstadoSeleccion.setText("Se acabo el tiempo! Respuesta incorrecta");

        // Enviamos respuesta vacia (tiempo agotado)
        enviarRespuestaAlServidor("TIEMPO_AGOTADO");

        // Programamos la carga de la siguiente pregunta
        programarSiguientePregunta();
    }

    /**
     * Maneja la seleccion de la opcion 1.
     */
    @FXML
    private void onResponderOpcion1() {
        procesarRespuesta(0);
    }

    /**
     * Maneja la seleccion de la opcion 2.
     */
    @FXML
    private void onResponderOpcion2() {
        procesarRespuesta(1);
    }

    /**
     * Maneja la seleccion de la opcion 3.
     */
    @FXML
    private void onResponderOpcion3() {
        procesarRespuesta(2);
    }

    /**
     * Maneja la seleccion de la opcion 4.
     */
    @FXML
    private void onResponderOpcion4() {
        procesarRespuesta(3);
    }

    /**
     * Procesa la respuesta seleccionada por el jugador.
     *
     * Flujo: 1. Verifica que no se haya respondido ya 2. Detiene el
     * temporizador 3. Obtiene la respuesta seleccionada 4. Verifica si es
     * correcta 5. Muestra feedback visual 6. Envia la respuesta al servidor 7.
     * Programa la siguiente pregunta
     *
     * @param indiceRespuesta Indice de la respuesta seleccionada (0-3)
     */
    private void procesarRespuesta(int indiceRespuesta) {
        // Evitamos multiples respuestas
        if (preguntaRespondida) {
            return;
        }

        // Verificamos que haya preguntas
        if (App.preguntasActuales.isEmpty()) {
            return;
        }

        // Detenemos el temporizador
        if (temporizador != null) {
            temporizador.stop();
        }

        // Marcamos como respondida
        preguntaRespondida = true;
        deshabilitarBotones();

        // Obtenemos la pregunta actual
        Preguntas pregunta = App.preguntasActuales.get(preguntaActualIndex);
        ArrayList<Respuestas> respuestas = pregunta.getArregloDeRespuestasParaPreguntas();

        // Verificamos que el indice sea valido
        if (respuestas == null || indiceRespuesta >= respuestas.size()) {
            lblEstadoSeleccion.setText("Error: Respuesta no valida");
            programarSiguientePregunta();
            return;
        }

        // Obtenemos la respuesta seleccionada
        respuestaSeleccionada = respuestas.get(indiceRespuesta);

        // Verificamos si es correcta
        boolean esCorrecta = respuestaSeleccionada.isCorrecta();

        // Mostramos el resultado
        if (esCorrecta) {
            lblEstadoSeleccion.setText("CORRECTO! +" + pregunta.getValorPuntosPreguntas() + " puntos");
            lblEstadoSeleccion.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            marcarBotonCorrecto(indiceRespuesta);
        } else {
            lblEstadoSeleccion.setText("INCORRECTO! La respuesta era incorrecta");
            lblEstadoSeleccion.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            marcarBotonIncorrecto(indiceRespuesta);
            // Marcamos cual era la correcta
            marcarRespuestaCorrecta(respuestas);
        }

        // Enviamos la respuesta al servidor
        enviarRespuestaAlServidor(respuestaSeleccionada.getRespuestas());

        // Programamos la siguiente pregunta despues de 2 segundos
        programarSiguientePregunta();
    }

    /**
     * Marca visualmente el boton de la respuesta correcta.
     *
     * @param indice Indice del boton correcto
     */
    private void marcarBotonCorrecto(int indice) {
        switch (indice) {
            case 0:
                btnOpcion1.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case 1:
                btnOpcion2.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case 2:
                btnOpcion3.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case 3:
                btnOpcion4.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
        }
    }

    /**
     * Marca visualmente el boton de la respuesta incorrecta seleccionada.
     *
     * @param indice Indice del boton incorrecto
     */
    private void marcarBotonIncorrecto(int indice) {
        switch (indice) {
            case 0:
                btnOpcion1.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case 1:
                btnOpcion2.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case 2:
                btnOpcion3.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
            case 3:
                btnOpcion4.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                break;
        }
    }

    /**
     * Marca visualmente cual era la respuesta correcta. Recorre todas las
     * respuestas y marca en verde la que es correcta.
     *
     * @param respuestas Lista de respuestas de la pregunta
     */
    private void marcarRespuestaCorrecta(ArrayList<Respuestas> respuestas) {
        for (int i = 0; i < respuestas.size(); i++) {
            if (respuestas.get(i).isCorrecta()) {
                switch (i) {
                    case 0:
                        btnOpcion1.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                        break;
                    case 1:
                        btnOpcion2.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                        break;
                    case 2:
                        btnOpcion3.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                        break;
                    case 3:
                        btnOpcion4.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                        break;
                }
                break; // Solo hay una correcta
            }
        }
    }

    /**
     * Resetea los estilos de todos los botones a su estado original.
     */
    private void resetearEstilosBotones() {
        btnOpcion1.setStyle("");
        btnOpcion2.setStyle("");
        btnOpcion3.setStyle("");
        btnOpcion4.setStyle("");
    }

    /**
     * Envia la respuesta del jugador al servidor.
     *
     * Formato de trama:
     * RESPUESTA|codigoSala|nombreUsuario|respuesta|tiempoRestante
     *
     * @param respuesta Texto de la respuesta seleccionada o "TIEMPO_AGOTADO"
     */
    private void enviarRespuestaAlServidor(String respuesta) {
        try {
            if (App.preguntasActuales.isEmpty()) {
                return;
            }

            Preguntas pregunta = App.preguntasActuales.get(preguntaActualIndex);
            String nombreUsuario = App.usuarioActual.getNombreUsuario();

            // Construimos la trama de respuesta
            String trama = "RESPUESTA|"
                    + pregunta.getCodigoSala() + "|"
                    + nombreUsuario + "|"
                    + respuesta + "|"
                    + tiempoRestante;

            // Enviamos al servidor
            App.escritor.println(trama);
            System.out.println("Respuesta enviada: " + trama);

        } catch (Exception e) {
            System.out.println("Error al enviar respuesta: " + e.getMessage());
        }
    }

    /**
     * Programa la carga de la siguiente pregunta despues de una pausa. Espera 2
     * segundos para que el jugador vea el resultado y luego carga la siguiente
     * pregunta o finaliza el juego.
     */
    private void programarSiguientePregunta() {
        // Creamos un timeline para esperar 2 segundos
        Timeline pausa = new Timeline(
                new KeyFrame(Duration.seconds(2), new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                // Avanzamos al siguiente indice
                                preguntaActualIndex++;

                                // Verificamos si hay mas preguntas
                                if (preguntaActualIndex < App.preguntasActuales.size()) {
                                    // Reseteamos estilos y cargamos la siguiente
                                    resetearEstilosBotones();
                                    cargarPreguntaActual();
                                } else {
                                    // No hay mas preguntas
                                    finalizarJuego();
                                }
                            }
                        });
                    }
                })
        );
        pausa.setCycleCount(1);
        pausa.play();
    }

    /**
     * Finaliza el juego cuando no hay mas preguntas. Muestra un mensaje y
     * deshabilita los botones.
     */
    private void finalizarJuego() {
        lblEnunciado.setText("Fin del juego!");
        lblCronometro.setText("Todas las preguntas han terminado");
        lblEstadoSeleccion.setText("Esperando resultados finales...");
        progressTiempo.setProgress(0);
        deshabilitarBotones();

        // Notificamos al servidor que el jugador termino
        try {
            String trama = "FIN_PREGUNTAS|" + App.usuarioActual.getNombreUsuario();
            App.escritor.println(trama);
        } catch (Exception e) {
            System.out.println("Error al notificar fin: " + e.getMessage());
        }
    }

    /**
     * Deshabilita todos los botones de respuesta.
     */
    private void deshabilitarBotones() {
        btnOpcion1.setDisable(true);
        btnOpcion2.setDisable(true);
        btnOpcion3.setDisable(true);
        btnOpcion4.setDisable(true);
    }

    /**
     * Habilita todos los botones de respuesta.
     */
    private void habilitarBotones() {
        btnOpcion1.setDisable(false);
        btnOpcion2.setDisable(false);
        btnOpcion3.setDisable(false);
        btnOpcion4.setDisable(false);
    }

    /**
     * Detiene el temporizador cuando se cierra la vista. Es llamado
     * automaticamente por JavaFX al cambiar de escena.
     */
    public void detenerTemporizador() {
        if (temporizador != null) {
            temporizador.stop();
        }
    }
}
