module proyectofinaldesarrolloIII {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens Controlador to javafx.fxml;
    opens Modelo to javafx.fxml;
    opens proyectofinaldesarrolloIII to javafx.fxml;
    exports proyectofinaldesarrolloIII;
    exports Controlador;
}
