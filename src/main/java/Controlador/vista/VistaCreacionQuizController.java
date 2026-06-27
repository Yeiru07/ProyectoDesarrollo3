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
    private ComboBox<String> cmbTipoDePregunta;

    @FXML
    private ToggleGroup grupoRespuestas;

    @FXML
    private RadioButton rbRespuestaRojo;

    @FXML
    private RadioButton rbRespuestaAzul;

    @FXML
    private RadioButton rbRespuestaAmarillo;

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
    private Sala sala;

    private GestorSalaCliente gestorSala;
    private GestorPreguntasCliente gestorPregunta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        partida = App.partida;

        gestorSala = new GestorSalaCliente(App.cliente);
        gestorPregunta = new GestorPreguntasCliente(App.cliente);

        sala = gestorSala.crearSala(App.usuarioActual);

        partida.getArrayDeSalas().add(sala);

        labelDeNombreUsuario.setText("✔ Bienvenido "
                + App.usuarioActual.getNombreUsuario());

        cmbPuntosParaPregunta.getItems().addAll(
                10, 20, 30, 40, 50);

        cmbLimiteDeTiempo.getItems().addAll(
                15, 20, 30);

        cmbLimiteDeTiempo.setValue(20);
        cmbPuntosParaPregunta.setValue(10);

        cmbTipoDePregunta.getItems().addAll(
                "Quiz",
                "Verdadero O Falso");

        cmbTipoDePregunta.setValue("Quiz");

        grupoRespuestas = new ToggleGroup();

        rbRespuestaRojo.setToggleGroup(grupoRespuestas);
        rbRespuestaAzul.setToggleGroup(grupoRespuestas);
        rbRespuestaAmarillo.setToggleGroup(grupoRespuestas);
        rbRespuestaVerde.setToggleGroup(grupoRespuestas);

        crearNuevaPregunta();
    }

    @FXML
    public void crearNuevaPregunta() {

        String tipo = cmbTipoDePregunta.getValue();

        if (tipo == null) {

            AlertaParaUsar.mostrar(
                    "Error",
                    "Seleccione un tipo de pregunta",
                    Alert.AlertType.WARNING);

            return;
        }

        if (gestorPregunta.getNumeroDePreguntaActual() >= 0) {

            boolean guardado = guardarPreguntaSegunTipo();

            if (!guardado) {
                return;
            }

        }

        gestorPregunta.crearNuevaPregunta(sala, tipo);

        int indice = sala.getListaPreguntas().size() - 1;

        crearBotonPregunta(indice);

        limpiarCampos();
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

    public void cargarPregunta(int indice) {

        gestorPregunta.cargarPregunta(sala, indice);

        Preguntas pregunta = gestorPregunta.getPreguntaActual();

        txtTituloPregunta.setText(pregunta.getEnunciado());

        if (pregunta.getArregloDeRespuestasParaPreguntas() != null) {

            if (pregunta.getArregloDeRespuestasParaPreguntas().size() >= 1) {

                txtRespuestaRojo.setText(
                        pregunta.getArregloDeRespuestasParaPreguntas().get(0).getRespuestas());

            }

            if (pregunta.getArregloDeRespuestasParaPreguntas().size() >= 2) {

                txtRespuestaAzul.setText(
                        pregunta.getArregloDeRespuestasParaPreguntas().get(1).getRespuestas());

            }

            if (pregunta.getArregloDeRespuestasParaPreguntas().size() >= 3) {

                txtRespuestaAmarillo.setText(
                        pregunta.getArregloDeRespuestasParaPreguntas().get(2).getRespuestas());

            }

            if (pregunta.getArregloDeRespuestasParaPreguntas().size() >= 4) {

                txtRespuestaVerde.setText(
                        pregunta.getArregloDeRespuestasParaPreguntas().get(3).getRespuestas());

            }

        }

    }

    @FXML
    private void cambiarTipoPregunta(ActionEvent event) {

        boolean esVF = "Verdadero O Falso".equals(
                cmbTipoDePregunta.getValue());

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

    private boolean guardarPreguntaSegunTipo() {

        String tipo = cmbTipoDePregunta.getValue();

        if (tipo == null || gestorPregunta.getNumeroDePreguntaActual() < 0) {
            return true;
        }

        if (tipo.equals("Quiz")) {
            return guardarPreguntaActual();
        } else {
            return guardarPreguntaActualVerdaderoOFalso();
        }

    }

    public boolean guardarPreguntaActual() {

        try {

            return gestorPregunta.guardarPreguntaQuiz(
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
                    rbRespuestaVerde.isSelected());

        } catch (Exception e) {

            AlertaParaUsar.mostrar(
                    "Faltan datos",
                    e.getMessage(),
                    Alert.AlertType.WARNING);

            return false;

        }

    }

    public boolean guardarPreguntaActualVerdaderoOFalso() {

        try {

            return gestorPregunta.guardarPreguntaVerdaderoFalso(
                    sala,
                    txtTituloPregunta.getText().trim(),
                    txtRespuestaRojo.getText().trim(),
                    txtRespuestaAzul.getText().trim(),
                    cmbLimiteDeTiempo.getValue(),
                    cmbPuntosParaPregunta.getValue(),
                    cmbTipoDePregunta.getValue(),
                    rbRespuestaRojo.isSelected(),
                    rbRespuestaAzul.isSelected());

        } catch (Exception e) {

            AlertaParaUsar.mostrar(
                    "Faltan datos",
                    e.getMessage(),
                    Alert.AlertType.WARNING);

            return false;

        }

    }

    @FXML
    public void guardarPregunta(ActionEvent event) throws IOException {

        String tituloSala = txtTituloSala.getText().trim();

        if (tituloSala.isEmpty()) {

            AlertaParaUsar.mostrar(
                    "Error",
                    "Debe ingresar un titulo para la sala.",
                    Alert.AlertType.WARNING);

            return;

        }

        gestorSala.setNombreSala(tituloSala);

        if (!guardarPreguntaSegunTipo()) {
            return;
        }

        App.preguntasActuales.clear();
        App.jugadoresLobby = null;
        App.salaActual = sala;

        boolean salaGuardada = gestorSala.enviarSalaAlServidor();

        if (!salaGuardada) {

            AlertaParaUsar.mostrar(
                    "Error",
                    "No se pudo guardar la sala.",
                    Alert.AlertType.ERROR);

            return;

        }

        boolean preguntasGuardadas
                = gestorPregunta.enviarTodasLasPreguntas(sala);

        if (!preguntasGuardadas) {

            AlertaParaUsar.mostrar(
                    "Error",
                    "No se pudieron guardar las preguntas.",
                    Alert.AlertType.ERROR);

            return;

        }

        AlertaParaUsar.mostrar(
                "Éxito",
                "Sala y preguntas guardadas correctamente.",
                Alert.AlertType.INFORMATION);

        regresar();

    }

    @FXML
    private void eliminarPregunta(ActionEvent event) {

        if (!gestorPregunta.tienePreguntas(sala)) {

            AlertaParaUsar.mostrar(
                    "Error",
                    "No hay preguntas.",
                    Alert.AlertType.WARNING);

            return;

        }

        gestorPregunta.eliminarPregunta(
                sala,
                gestorPregunta.getNumeroDePreguntaActual());

        contenedorPreguntas.getChildren().clear();

        for (int i = 0; i < sala.getListaPreguntas().size(); i++) {
            crearBotonPregunta(i);
        }

        if (!gestorPregunta.tienePreguntas(sala)) {

            limpiarCampos();

            return;

        }

        cargarPregunta(gestorPregunta.getNumeroDePreguntaActual());

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
    public void regresar() throws IOException {

        App.setRoot("VistaPantallaDeIngreso");

    }
}
