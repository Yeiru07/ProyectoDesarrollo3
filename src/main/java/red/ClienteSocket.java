package red;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClienteSocket {

    private Socket socket;
    private PrintWriter escritor;
    private BufferedReader lector;
    private static final String SERVER_HOST = System.getProperty("server.host", "localhost");
    private static final int SERVER_PORT = 5000;

    public void conectar() {
        try {
            if (estaConectado()) {
                return;
            }
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Cliente conectado al servidor " + SERVER_HOST + ":" + SERVER_PORT);
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
            escritor = null;
            lector = null;
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean estaConectado() {
        return socket != null
                && socket.isConnected()
                && !socket.isClosed()
                && escritor != null
                && lector != null;
    }

    public PrintWriter getEscritor() {
        return escritor;
    }

    public BufferedReader getLector() {
        return lector;
    }
}
