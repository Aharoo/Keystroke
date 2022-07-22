package ua.aharoo.keystroke.controller;

import com.google.common.base.Stopwatch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import ua.aharoo.keystroke.model.Analyze;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class MainController {

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private TextField loginTextField;
    @FXML
    private Button saveButton;
    @FXML
    private Button checkButton;
    @FXML
    private Button analyzeButton;
    @FXML
    private TextField resultTextField;
    @FXML
    private Text loginText;
    @FXML
    private Button resetButton;
    @FXML
    private TableView<Analyze> resultTableView;
    @FXML
    private TableColumn<Analyze, String> symbol;
    @FXML
    private TableColumn<Analyze, Double> pressTime;
    @FXML
    private TableColumn<Analyze, Double> timeBetweenPress;
    @FXML
    private Button statisticsButton;
    private int i, k; // Порядковий номер
    private double time_between, timeBuffer; // Час між натиском клавіш та попередього натиску клавіши
    private static String work, text_time_between, text_time_press; // змінна для створення ключа + порядковий номер
    private double startPush, endPush, lastTimePushed; // Час між натисканням
    private ObservableList<Analyze> analyzeList;
    private Stopwatch stopwatch;

    public MainController() {
        stopwatch = Stopwatch.createStarted();
        i = 0;
        k = 0;
        lastTimePushed = 0;
        time_between = 0;
        text_time_between = "";
        text_time_press = "";
        analyzeList = FXCollections.observableArrayList();
    }

    @FXML
    void onAnalyzeButtonClick(ActionEvent event) {
        symbol.setCellValueFactory(new PropertyValueFactory<>("Symbol"));
        pressTime.setCellValueFactory(new PropertyValueFactory<>("pressTime"));
        timeBetweenPress.setCellValueFactory(new PropertyValueFactory<>("timeBetweenPress"));

        resultTableView.setItems(analyzeList);
    }

    @FXML
    void onCheckButtonClick(ActionEvent event) {

    }

    @FXML
    void onMouseClicked(MouseEvent event) {

    }

    @FXML
    void onResetButtonClick(ActionEvent event) {
        analyzeList.clear();
        startPush = endPush = 0;
        i = 0;
        work = "";
        loginTextField.clear();
    }

    @FXML
    void onSaveButtonClick(ActionEvent event) {
        text_time_between += '\n';
        text_time_between += '\n';

        try(BufferedWriter writer = new BufferedWriter(new FileWriter("time_between.txt"));
        BufferedWriter writer1 = new BufferedWriter(new FileWriter("time_press.txt"))){
             writer.write(text_time_between);
             writer1.write(text_time_press);
        } catch (IOException e){
            e.printStackTrace();
        }
        k += 1;
        text_time_press = "";
        text_time_between = "";
//        try {
//            Files.deleteIfExists(Paths.get("time_between.txt"));
//            Files.deleteIfExists(Paths.get("time_press.txt"));
//        } catch (IOException e){
//            e.printStackTrace();
//        }

    }

    @FXML
    void onLoginTextFieldKeyPressed(KeyEvent event) {
        startPush = stopwatch.elapsed(TimeUnit.MILLISECONDS);
    }

    @FXML
    void onLoginTextFieldKeyReleased(KeyEvent event) {
        lastTimePushed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        endPush = lastTimePushed - startPush;

        if (i == 0) time_between = 0;
        else time_between = startPush - timeBuffer;

        timeBuffer = lastTimePushed;

        i++; // збільшення ітератора для унікальності ключа
        work = String.format(" %d %s ",i, event.getCode().getName()); // Створення унікального ключа

        text_time_between += time_between + '\t';
        text_time_press += endPush + '\t';

        analyzeList.add(new Analyze(work, endPush, time_between));
    }

    @FXML
    void onStatisticsButton(ActionEvent event) {

    }

    @FXML
    void initialize() {

    }

}
