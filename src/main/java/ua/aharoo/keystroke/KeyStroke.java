package ua.aharoo.keystroke;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KeyStroke extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(KeyStroke.class.getResource("password.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 757, 600);
        stage.setTitle("KeystrokeTime");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}