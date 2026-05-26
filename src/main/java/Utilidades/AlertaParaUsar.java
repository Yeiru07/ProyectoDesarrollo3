package Utilidades;

public class AlertaParaUsar {

    public static void mostrar(String titulo, String mensaje, javafx.scene.control.Alert.AlertType tipo) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
