package Controlador.vista;

import Controlador.gestor.GestorPreguntasCliente;
import Controlador.gestor.GestorSalaCliente;
import Modelo.Juego;
import Modelo.Preguntas;
import Modelo.Sala;
import Utilidades.AlertaParaUsar;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
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

    private Juego partida;
    private GestorSalaCliente gestorSala;
    private GestorPreguntasCliente gestorPreguntas;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.partida = App.partida;

        // Inicializar gestores con el cliente socket
        gestorSala = new GestorSalaCliente(App.cliente);
        gestorPreguntas = new GestorPreguntasCliente(App.cliente);

        // Crear sala automáticamente
        Sala sala = gestorSala.crearSala(App.usuarioActual);
        partida.getArrayDeSalas().add(sala);

        labelDeNombreUsuario.setText("✔ Bienvenido " + App.usuarioActual.getNombreUsuario());

        configurarCombos();
        configurarToggleGroup();

        // Crear primera pregunta
        crearNuevaPregunta();
    }

    private void configurarCombos() {
        cmbPuntosParaPregunta.getItems().addAll(10, 20, 30, 40, 50);
        cmbLimiteDeTiempo.getItems().addAll(15, 20, 30);
        cmbLimiteDeTiempo.setValue(20);
        cmbPuntosParaPregunta.setValue(10);
        cmbTipoDePregunta.getItems().addAll("Quiz", "Verdadero O Falso");
        cmbTipoDePregunta.setValue("Quiz");
    }

    private void configurarToggleGroup() {
        grupoRespuestas = new ToggleGroup();
        rbRespuestaRojo.setToggleGroup(grupoRespuestas);
        rbRespuestaAzul.setToggleGroup(grupoRespuestas);
        rbRespuestaAmarillo.setToggleGroup(grupoRespuestas);
        rbRespuestaVerde.setToggleGroup(grupoRespuestas);
    }

    @FXML
    public void crearBotonPregunta(int indice) {
        Button boton = new Button((indice + 1) + " Quiz");
        boton.setPrefWidth(180);
        boton.getStyleClass().add("question-card");
        boton.setUserData(indice);
        boton.setOnAction(this::seleccionarPregunta);
        contenedorPreguntas.getChildren().add(boton);
    }

    public void seleccionarPregunta(ActionEvent event) {
        Button boton = (Button) event.getSource();
        int indice = (Integer) boton.getUserData();
        Sala sala = gestorSala.getSalaActual();
        gestorPreguntas.cargarPregunta(sala, indice);
        cargarPreguntaEnUI(indice);
    }

    @FXML
    public void crearNuevaPregunta() {
        String tipoDeQuiz = cmbTipoDePregunta.getValue();

        if (tipoDeQuiz == null) {
            AlertaParaUsar.mostrar("Error", "Seleccione un tipo de pregunta", Alert.AlertType.WARNING);
            return;
        }

        // Guardar pregunta actual si existe
        if (gestorPreguntas.getNumeroDePreguntaActual() >= 0) {
            boolean seGuardo = guardarPreguntaSegunTipo();
            if (!seGuardo) {
                return;
            }
        }

        Sala sala = gestorSala.getSalaActual();
        gestorPreguntas.crearNuevaPregunta(sala, tipoDeQuiz);

        int indice = sala.getListaPreguntas().size() - 1;
        crearBotonPregunta(indice);
        limpiarCampos();
    }

    private boolean guardarPreguntaSegunTipo() {
        String tipo = cmbTipoDePregunta.getValue();
        if (tipo == null || gestorPreguntas.getNumeroDePreguntaActual() < 0) {
            return true;
        }

        try {
            if ("Quiz".equals(tipo)) {
                return guardarPreguntaQuiz();
            } else {
                return guardarPreguntaVerdaderoFalso();
            }
        } catch (IllegalArgumentException e) {
            AlertaParaUsar.mostrar("Faltan datos", e.getMessage(), Alert.AlertType.WARNING);
            return false;
        }
    }

    private boolean guardarPreguntaQuiz() {
        Sala sala = gestorSala.getSalaActual();
        return gestorPreguntas.guardarPreguntaQuiz(
                sala,
                txtTituloPregunta.getText().trim(),
                txtRespuestaRojo.getText().trim(),
                txtRespuestaAzul.getText().trim(),
                txtRespuestaAmarillo.getText().trim(),
                txtRespuestaVerde.getText().trim(),
                cmbLimiteDeTiempo.getValue(),
                cmbPuntosParaPregunta.getValue(),
                cmbTipoDePregunta.getValue(),
                rbRespuestaRojo.isSelected(),
                rbRespuestaAzul.isSelected(),
                rbRespuestaAmarillo.isSelected(),
                rbRespuestaVerde.isSelected()
        );
    }

    private boolean guardarPreguntaVerdaderoFalso() {
        Sala sala = gestorSala.getSalaActual();
        return gestorPreguntas.guardarPreguntaVerdaderoFalso(
                sala,
                txtTituloPregunta.getText().trim(),
                txtRespuestaRojo.getText().trim(),
                txtRespuestaAzul.getText().trim(),
                cmbLimiteDeTiempo.getValue(),
                cmbPuntosParaPregunta.getValue(),
                cmbTipoDePregunta.getValue(),
                rbRespuestaRojo.isSelected(),
                rbRespuestaAzul.isSelected()
        );
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

    public void cargarPreguntaEnUI(int indice) {
        Sala sala = gestorSala.getSalaActual();
        Preguntas pregunta = sala.getListaPreguntas().get(indice);

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

    @FXML
    public void guardarPregunta(ActionEvent event) throws IOException {
        String titulo = txtTituloSala.getText().trim();
        gestorSala.setNombreSala(titulo);

        boolean seGuardo = guardarPreguntaSegunTipo();
        if (!seGuardo) {
            return;
        }

        // Limpiar variables estáticas
        App.preguntasActuales.clear();
        App.jugadoresLobby = null;
        App.salaActual = gestorSala.getSalaActual();

        // Enviar sala usando el gestor
        boolean salaEnviada = gestorSala.enviarSalaAlServidor();
        if (!salaEnviada) {
            AlertaParaUsar.mostrar("Error", "Error al crear la sala en el servidor", Alert.AlertType.ERROR);
            return;
        }

        // Enviar preguntas usando el gestor
        Sala sala = gestorSala.getSalaActual();
        for (Preguntas pregunta : sala.getListaPreguntas()) {
            boolean preguntaEnviada = gestorPreguntas.enviarPreguntaAlServidor(pregunta);
            if (!preguntaEnviada) {
                AlertaParaUsar.mostrar("Error", "Error al guardar pregunta", Alert.AlertType.ERROR);
                return;
            }
        }

        AlertaParaUsar.mostrar("Éxito", "Guardado correctamente.", Alert.AlertType.INFORMATION);
        regresar();
    }

    @FXML
    private void eliminarPregunta(ActionEvent event) {
        int indiceActual = gestorPreguntas.getNumeroDePreguntaActual();
        Sala sala = gestorSala.getSalaActual();

        if (indiceActual < 0 || sala.getListaPreguntas().isEmpty()) {
            AlertaParaUsar.mostrar("Error", "No hay ninguna pregunta seleccionada", Alert.AlertType.WARNING);
            return;
        }

        gestorSala.eliminarPregunta(indiceActual);
        contenedorPreguntas.getChildren().clear();

        for (int i = 0; i < sala.getListaPreguntas().size(); i++) {
            crearBotonPregunta(i);
        }

        if (sala.getListaPreguntas().isEmpty()) {
            gestorPreguntas.limpiarPreguntaActual();
            limpiarCampos();
            return;
        }

        int nuevoIndice = Math.min(indiceActual, sala.getListaPreguntas().size() - 1);
        gestorPreguntas.setNumeroDePreguntaActual(nuevoIndice);
        cargarPreguntaEnUI(nuevoIndice);
    }

    @FXML
    public void regresar() throws IOException {
        App.setRoot("VistaPantallaDeIngreso");
    }
}
