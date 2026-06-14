package proyectofinaldesarrolloIII;

import Modelo.Juego;
import Modelo.Preguntas;
import Modelo.Sala;
import Modelo.Usuario;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX App
 */
public class App extends Application {

    // Estas variables ahora se inicializarán en el metodo conectarServidor()
    public static PrintWriter escritor;
    public static BufferedReader lector;
    private static Socket socket;
    public static ArrayList<Preguntas> preguntasActuales = new ArrayList<>();

    private static Scene scene;
    public static Juego partida = new Juego();
    public static Usuario usuarioActual;
    public static Sala salaActual;
    public static String jugadoresLobby;

    @Override
    public void start(Stage stage) throws IOException {
        // Intentamos conectar con el servidor antes de mostrar la ventana
        conectarServidor();

        scene = new Scene(loadFXML("VistaLogging"), 1080, 720);
        stage.setScene(scene);
        stage.show();
    }

    // Método encargado de inicializar el Socket, el lector y el escritor
    private void conectarServidor() {
        try {
            // "localhost" si corres el servidor en la misma compu, o la IP si es externa.
            socket = new Socket("100.112.89.47", 5000);
            //socket = new Socket("localhost", 5000);

            // Inicializamos los flujos apuntando al socket
            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("¡Cliente conectado al servidor exitosamente!");

        } catch (IOException e) {
            System.err.println("ERROR: No se pudo conectar al servidor. ¿Está encendido?");
            e.printStackTrace();
        }
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

    // Es una buena práctica cerrar los flujos cuando se cierra la aplicación JavaFX
    @Override
    public void stop() {
        try {
            if (escritor != null) {
                escritor.close();
            }
            if (lector != null) {
                lector.close();
            }
            if (socket != null) {
                socket.close();
            }
            System.out.println("Conexiones cerradas correctamente al salir.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
