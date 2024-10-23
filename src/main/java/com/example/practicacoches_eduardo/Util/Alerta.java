package com.example.practicacoches_eduardo.Util;

import javafx.scene.control.Alert;

public class Alerta {
    public static void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ALERTA! ERROR!");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    public static void mostrarInformacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("CONFIRMACIÓN!");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
