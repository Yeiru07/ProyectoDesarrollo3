package red;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteSocket {

    private Socket socket;
    private PrintWriter escritor;
    private BufferedReader lector;

    public void conectar() {
        try {
            socket = new Socket("localhost", 5000);
            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Cliente conectado al servidor");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cerrarConexion() {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PrintWriter getEscritor() {
        return escritor;
    }

    public BufferedReader getLector() {
        return lector;
    }
}