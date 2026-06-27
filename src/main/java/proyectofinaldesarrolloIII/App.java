package proyectofinaldesarrolloIII;

import Modelo.Juego;
import Modelo.Preguntas;
import Modelo.Sala;
import Modelo.Usuario;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import red.ClienteSocket;

/**
 * JavaFX App
 */
public class App extends Application {

    public static ClienteSocket cliente;
    public static ArrayList<Preguntas> preguntasActuales = new ArrayList<>();

    private static Scene scene;
    public static Juego partida = new Juego();
    public static Usuario usuarioActual;
    public static Sala salaActual;
    public static String jugadoresLobby;
    public static String respuestaLobby;
    public static boolean esPresentador;
    public static String rankingActual = "";

    @Override
    public void start(Stage stage) throws IOException {
        cliente = new ClienteSocket();
        cliente.conectar();
        scene = new Scene(loadFXML("VistaLogging"), 1080, 720);
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/vista/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        if (cliente != null) {
            cliente.cerrarConexion();
        }
        super.stop();
    }

}
