package Controlador.vista;

import Modelo.Juego;
import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import Utilidades.AlertaParaUsar;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import proyectofinaldesarrolloIII.App;

/**
 * @author sronn
 */
public class VistaCreacionQuizController implements Initializable {

    @FXML
    private Button btnAgregarPregunta;
    @FXML
    private HBox boxRespuestaAmarilla;
    @FXML
    private HBox boxRespuestaVerde;
    @FXML
    private ComboBox<Integer> cmbLimiteDeTiempo;
    @FXML
    private ComboBox<Integer> cmbPuntosParaPregunta;
    @FXML
    private ToggleGroup grupoRespuestas;
    @FXML
    private ComboBox<String> cmbTipoDePregunta;
    @FXML
    private RadioButton rbRespuestaAmarillo;
    @FXML
    private RadioButton rbRespuestaAzul;
    @FXML
    private RadioButton rbRespuestaRojo;
    @FXML
    private RadioButton rbRespuestaVerde;
    @FXML
    private VBox contenedorPreguntas;
    @FXML
    private TextField txtTituloPregunta;
    @FXML
    private TextField txtRespuestaRojo;
    @FXML
    private TextField txtRespuestaAzul;
    @FXML
    private TextField txtRespuestaAmarillo;
    @FXML
    private TextField txtRespuestaVerde;
    @FXML
    private TextField txtTituloSala;

    @FXML
    private Label labelDeNombreUsuario;

    private int numeroDePreguntaActual = -1;

    Juego partida;
    Sala sala;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.partida = App.partida;

        //Hay que refactorizar
        int codigoSala = (int) (Math.random() * 900000) + 100000;
        sala = new Sala(codigoSala, "", false, 0, App.usuarioActual);
        sala.getListaDeCodigos().add(codigoSala);
        partida.getArrayDeSalas().add(sala);

        ///////////////////////////////////////////////////////////////////////////////////
        labelDeNombreUsuario.setText(App.usuarioActual.getNombreUsuario());

        cmbPuntosParaPregunta.getItems().addAll(10, 20, 30, 40, 50);
        cmbLimiteDeTiempo.getItems().addAll(15, 20, 30);
        cmbLimiteDeTiempo.setValue(20);
        cmbPuntosParaPregunta.setValue(10);

        cmbTipoDePregunta.getItems().addAll("Quiz", "Verdadero O Falso");
        cmbTipoDePregunta.setValue("Quiz"); // Valor por defecto

        grupoRespuestas = new ToggleGroup();
        rbRespuestaRojo.setToggleGroup(grupoRespuestas);
        rbRespuestaAzul.setToggleGroup(grupoRespuestas);
        rbRespuestaAmarillo.setToggleGroup(grupoRespuestas);
        rbRespuestaVerde.setToggleGroup(grupoRespuestas);

        // Creamos la primera pregunta en blanco para que el usuario empiece a editar de inmediato
        crearNuevaPregunta();
    }

    @FXML
    public void crearBotonPregunta(int indice) {
        Button boton = new Button((indice + 1) + " Quiz");
        boton.setPrefWidth(180);
        boton.getStyleClass().add("question-card");
        boton.setUserData(indice);

        boton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                seleccionarPregunta(event);
            }
        });
        contenedorPreguntas.getChildren().add(boton);
    }

    public void seleccionarPregunta(ActionEvent event) {

        Button boton = (Button) event.getSource();
        int indice = (Integer) boton.getUserData();
        cargarPregunta(indice);
    }

    @FXML
    public void crearNuevaPregunta() {
        String tipoDeQuiz = cmbTipoDePregunta.getValue();

        if (tipoDeQuiz == null) {
            AlertaParaUsar.mostrar("Error", "Seleccione un tipo de pregunta", Alert.AlertType.WARNING);
            return;
        }

        if (numeroDePreguntaActual >= 0) {
            // Validar antes de crear una nueva
            boolean seGuardo = guardarPreguntaSegunTipo();
            if (!seGuardo) {
                return; // Si faltan datos en la actual, no te deja crear una nueva
            }
        }

        Preguntas pregunta = new Preguntas();
        pregunta.setTipoDePregunta(tipoDeQuiz);
        sala.getListaPreguntas().add(pregunta);

        int indice = sala.getListaPreguntas().size() - 1;
        crearBotonPregunta(indice);
        numeroDePreguntaActual = indice;

        limpiarCampos();
    }

    private boolean guardarPreguntaSegunTipo() {
        String tipo = cmbTipoDePregunta.getValue();

        if (tipo == null || numeroDePreguntaActual < 0) {
            return true;
        }

        if ("Quiz".equals(tipo)) {
            return guardarPreguntaActual();
        } else {
            return guardarPreguntaActualVerdaderoOFalso();
        }
    }

    @FXML
    private void cambiarTipoPregunta(ActionEvent event) {
        boolean esVF = "Verdadero O Falso".equals(cmbTipoDePregunta.getValue());

        boxRespuestaAmarilla.setVisible(!esVF);
        boxRespuestaAmarilla.setManaged(!esVF);

        boxRespuestaVerde.setVisible(!esVF);
        boxRespuestaVerde.setManaged(!esVF);

        if (esVF) {
            txtRespuestaRojo.setText("Verdadero");
            txtRespuestaAzul.setText("Falso");
            txtRespuestaAmarillo.clear();
            txtRespuestaVerde.clear();
        } else {
            txtRespuestaRojo.clear();
            txtRespuestaAzul.clear();
        }
    }

    // AHORA RETORNA BOOLEAN
    public boolean guardarPreguntaActual() {
        if (numeroDePreguntaActual < 0) {
            return true;
        }

        try {
            String tituloPregunta = txtTituloPregunta.getText().trim();
            String respuestaRojo = txtRespuestaRojo.getText().trim();
            String respuestaAzul = txtRespuestaAzul.getText().trim();
            String respuestaAmarillo = txtRespuestaAmarillo.getText().trim();
            String respuestaVerde = txtRespuestaVerde.getText().trim();
            int tiempoParaLasPreguntas = cmbLimiteDeTiempo.getValue();
            int puntosParaPreguntas = cmbPuntosParaPregunta.getValue();
            String tipoPregunta = cmbTipoDePregunta.getValue();

            if (tituloPregunta.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar un enunciado a la pregunta");
            }
            if (respuestaAmarillo.isEmpty() || respuestaAzul.isEmpty() || respuestaRojo.isEmpty() || respuestaVerde.isEmpty()) {
                throw new IllegalArgumentException("Debe de ingresar las 4 respuestas");
            }
            if (grupoRespuestas.getSelectedToggle() == null) {
                throw new IllegalArgumentException("Debe seleccionar una respuesta correcta (Radio Button)");
            }
            Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);
            pregunta.setEnunciado(tituloPregunta);
            pregunta.setTiempoParaLasPreguntas(tiempoParaLasPreguntas);
            pregunta.setValorPuntosPreguntas(puntosParaPreguntas);
            pregunta.setTipoDePregunta(tipoPregunta);
            pregunta.setCodigoSala(sala.getCodigoSala());

            if (pregunta.getArregloDeRespuestasParaPreguntas() == null) {
                pregunta.setArregloDeRespuestasParaPreguntas(new ArrayList<>());
            }
            pregunta.getArregloDeRespuestasParaPreguntas().clear();

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(1, respuestaRojo, rbRespuestaRojo.isSelected()));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(2, respuestaAzul, rbRespuestaAzul.isSelected()));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(3, respuestaAmarillo, rbRespuestaAmarillo.isSelected()));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(4, respuestaVerde, rbRespuestaVerde.isSelected()));

            return true; // Éxito

        } catch (Exception e) {
            AlertaParaUsar.mostrar("Faltan datos", e.getMessage(), Alert.AlertType.WARNING);
            return false; // Fallo
        }
    }

    // AHORA RETORNA BOOLEAN
    public boolean guardarPreguntaActualVerdaderoOFalso() {
        if (numeroDePreguntaActual < 0) {
            return true;
        }

        try {
            String tituloPregunta = txtTituloPregunta.getText().trim();
            String respuestaRojo = txtRespuestaRojo.getText().trim();
            String respuestaAzul = txtRespuestaAzul.getText().trim();
            int tiempoParaLasPreguntas = cmbLimiteDeTiempo.getValue();
            int puntosParaPreguntas = cmbPuntosParaPregunta.getValue();
            String tipoPregunta = cmbTipoDePregunta.getValue();

            if (tituloPregunta.isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar un enunciado a la pregunta");
            }
            if (respuestaAzul.isEmpty() || respuestaRojo.isEmpty()) {
                throw new IllegalArgumentException("Debe de ingresar las 2 respuestas");
            }
            if (grupoRespuestas.getSelectedToggle() == null) {
                throw new IllegalArgumentException("Debe seleccionar la opción correcta");
            }

            Preguntas pregunta = sala.getListaPreguntas().get(numeroDePreguntaActual);
            pregunta.setEnunciado(tituloPregunta);
            pregunta.setTiempoParaLasPreguntas(tiempoParaLasPreguntas);
            pregunta.setValorPuntosPreguntas(puntosParaPreguntas);
            pregunta.setTipoDePregunta(tipoPregunta);

            if (pregunta.getArregloDeRespuestasParaPreguntas() == null) {
                pregunta.setArregloDeRespuestasParaPreguntas(new ArrayList<>());
            }
            pregunta.getArregloDeRespuestasParaPreguntas().clear();

            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(1, respuestaRojo, rbRespuestaRojo.isSelected()));
            pregunta.getArregloDeRespuestasParaPreguntas().add(new Respuestas(2, respuestaAzul, rbRespuestaAzul.isSelected()));

            return true; // Éxito

        } catch (Exception e) {
            AlertaParaUsar.mostrar("Faltan datos", e.getMessage(), Alert.AlertType.WARNING);
            return false; // Fallo
        }
    }

    public void cargarPregunta(int indice) {
        Preguntas pregunta = sala.getListaPreguntas().get(indice);
        numeroDePreguntaActual = indice;
        txtTituloPregunta.setText(pregunta.getEnunciado());

        int cantidad = pregunta.getArregloDeRespuestasParaPreguntas().size();

        if (cantidad >= 2) {
            txtRespuestaRojo.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(0).getRespuestas());
            txtRespuestaAzul.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(1).getRespuestas());
        }

        if (cantidad >= 4) {
            txtRespuestaAmarillo.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(2).getRespuestas());
            txtRespuestaVerde.setText(pregunta.getArregloDeRespuestasParaPreguntas().get(3).getRespuestas());
        }

        // Aquí podrías agregar la lógica para marcar los RadioButtons al cargar la pregunta
    }

    public void limpiarCampos() {
        txtTituloPregunta.clear();
        txtRespuestaRojo.clear();
        txtRespuestaAzul.clear();
        txtRespuestaAmarillo.clear();
        txtRespuestaVerde.clear();
        if (grupoRespuestas.getSelectedToggle() != null) {
            grupoRespuestas.getSelectedToggle().setSelected(false);
        }
    }

    private void enviarPreguntaPorSocket(Preguntas p) {
        if (p.getEnunciado().isEmpty() || p.getArregloDeRespuestasParaPreguntas().isEmpty()) {
            return;
        }

        try {
            String trama = "Pregunta|" + p.getEnunciado();
            for (Respuestas r : p.getArregloDeRespuestasParaPreguntas()) {
                trama += "|" + r.getRespuestas();
            }
            trama += "|" + p.getCodigoSala();

            // 1. Enviamos la pregunta
            App.escritor.println(trama);
            System.out.println("Enviado exitosamente: " + trama);

            // 2. NUEVO: Leemos la respuesta del servidor ("OK: Pregunta guardada") para limpiar el tubo
//            String respuestaServidor = App.lector.readLine();
//            System.out.println("Servidor dice: " + respuestaServidor);

        } catch (Exception e) {
            e.printStackTrace();
            AlertaParaUsar.mostrar("Error", "Error al enviar: " + e.getMessage(), Alert.AlertType.WARNING);
        }
    }

    private void enviarSalaPorSocket(Sala s) {
        if (s.getNombreSala().isEmpty() || s.getCodigoSala() == 0) {
            return;
        }

        try {
            // 1. Obtenemos el nombre del usuario logueado actualmente
            String nombreUsuario = App.usuarioActual.getNombreUsuario();

            // 2. Agregamos el nombreUsuario al final de la trama, separado por "|"
            String trama = "Sala|" + s.getNombreSala() + "|" + s.getCodigoSala() + "|" + s.getCantidadJugadores() + "|" + nombreUsuario;

            proyectofinaldesarrolloIII.App.escritor.println(trama);
            System.out.println("Trama de sala enviada: " + trama); // Opcional, para que lo veas en consola

        } catch (Exception e) {
            AlertaParaUsar.mostrar("Error", "No se pudo conectar al servidor: " + e.getMessage(), Alert.AlertType.WARNING);
        }
    }

 @FXML
    public void guardarPregunta(ActionEvent event) throws IOException {
        String titulo = txtTituloSala.getText().trim();
        sala.setNombreSala(titulo);

        boolean seGuardo = guardarPreguntaSegunTipo();
        if (!seGuardo) return;

        // 1. Enviamos la sala
        enviarSalaPorSocket(sala);
        App.lector.readLine(); // Esperamos confirmación de Sala

        // 2. Enviamos las preguntas
        for (Preguntas pregunta : sala.getListaPreguntas()) {
            enviarPreguntaPorSocket(pregunta);
            App.lector.readLine(); // Esperamos confirmación de Pregunta
        }

        // 3. Solo cuando el servidor nos confirmó todo, cambiamos de pantalla
        AlertaParaUsar.mostrar("Éxito", "Guardado correctamente.", Alert.AlertType.INFORMATION);
        regresar(); 
    }

    @FXML
    private void eliminarPregunta(ActionEvent event) {

        if (numeroDePreguntaActual < 0 || sala.getListaPreguntas().isEmpty()) {
            AlertaParaUsar.mostrar("Error", "No hay ninguna pregunta seleccionada", Alert.AlertType.WARNING);
            return;
        }
        sala.getListaPreguntas().remove(numeroDePreguntaActual);
        contenedorPreguntas.getChildren().clear();

        for (int i = 0; i < sala.getListaPreguntas().size(); i++) {
            crearBotonPregunta(i);
        }

        if (sala.getListaPreguntas().isEmpty()) {
            numeroDePreguntaActual = -1;
            limpiarCampos();
            return;
        }
        if (numeroDePreguntaActual >= sala.getListaPreguntas().size()) {
            numeroDePreguntaActual = sala.getListaPreguntas().size() - 1;
        }
        cargarPregunta(numeroDePreguntaActual);
    }

    @FXML
    public void regresar() throws IOException {
        App.setRoot("VistaPantallaDeIngreso");
    }
}
