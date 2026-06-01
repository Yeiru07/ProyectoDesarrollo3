module proyectofinaldesarrolloIII {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.base;

    opens Controlador to javafx.base;
    opens Controlador.vista to javafx.fxml;
    opens Modelo to javafx.fxml;
    opens proyectofinaldesarrolloIII to javafx.fxml;
    exports proyectofinaldesarrolloIII;
    exports Controlador;
}
