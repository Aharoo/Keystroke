package ua.aharoo.keystroke.service;

import javafx.scene.control.Alert;

public class ErrorsHandler {

    public static void emptyLoginFieldError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("EmptyLoginFieldError");
        alert.setHeaderText(null);
        alert.setContentText("Введіть логін!");

        alert.showAndWait();
    }

    public static void equalLoginError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("EqualLoginError");
        alert.setHeaderText(null);
        alert.setContentText("Потрібно вводити лише один і той самий логін");

        alert.showAndWait();
    }

}
