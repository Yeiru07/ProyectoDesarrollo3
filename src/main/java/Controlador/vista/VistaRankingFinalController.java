/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador.vista;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import proyectofinaldesarrolloIII.App;

/**
 *
 * @author sronn
 */
public class VistaRankingFinalController implements Initializable {

    @FXML
    private TableView<ResultadoRanking> tblRanking;
    @FXML
    private TableColumn<ResultadoRanking, String> colNombre;
    @FXML
    private TableColumn<ResultadoRanking, Integer> colCorrectas;
    @FXML
    private TableColumn<ResultadoRanking, Integer> colIncorrectas;
    @FXML
    private Button btnSalir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorrectas.setCellValueFactory(new PropertyValueFactory<>("correctas"));
        colIncorrectas.setCellValueFactory(new PropertyValueFactory<>("incorrectas"));
        tblRanking.setItems(parsearRanking(App.rankingActual));
    }

    private ObservableList<ResultadoRanking> parsearRanking(String ranking) {
        ObservableList<ResultadoRanking> resultados = FXCollections.observableArrayList();

        if (ranking == null || ranking.trim().isEmpty()) {
            return resultados;
        }

        String[] jugadores = ranking.split(";");
        for (String jugador : jugadores) {
            String[] datos = jugador.split(",", -1);
            if (datos.length < 3) {
                continue;
            }

            resultados.add(new ResultadoRanking(
                    datos[0],
                    parseEntero(datos[1]),
                    parseEntero(datos[2])
            ));
        }

        return resultados;
    }

    private int parseEntero(String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return 0;
        }
    }

    @FXML
    private void onSalir() {
        try {
            App.rankingActual = "";
            App.preguntasActuales.clear();
            App.setRoot("VistaPantallaDeIngreso");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ResultadoRanking {

        private final String nombre;
        private final int correctas;
        private final int incorrectas;

        public ResultadoRanking(String nombre, int correctas, int incorrectas) {
            this.nombre = nombre;
            this.correctas = correctas;
            this.incorrectas = incorrectas;
        }

        public String getNombre() {
            return nombre;
        }

        public int getCorrectas() {
            return correctas;
        }

        public int getIncorrectas() {
            return incorrectas;
        }
    }
}
