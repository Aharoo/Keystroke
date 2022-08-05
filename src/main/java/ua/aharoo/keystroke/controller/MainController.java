package ua.aharoo.keystroke.controller;

import com.google.common.base.Stopwatch;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
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
import ua.aharoo.keystroke.service.KeyStrokeService;
import ua.aharoo.keystroke.service.StatisticsService;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
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
    private Text loginText;
    @FXML
    private Text authorText;
    @FXML
    private Text nonAuthorText;
    @FXML
    private Text resultText;
    @FXML
    private Button resetButton;
    @FXML
    private Button globalResetButton;
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
    @FXML
    private Button globalSaveButton;
    private int i; // Порядковий номер
    private double time_between, timeBuffer; // Час між натиском клавіш та попередього натиску клавіши
    private static String work, text_time_between, text_time_press; // змінна для створення ключа + порядковий номер
    private double startPush, endPush, lastTimePushed; // Час між натисканням
    private ObservableList<Analyze> analyzeList;
    private Stopwatch stopwatch;

    public MainController() {
        stopwatch = Stopwatch.createStarted();
        i = 0;
        lastTimePushed = 0;
        time_between = 0;
        text_time_between = "";
        text_time_press = "";
        analyzeList = FXCollections.observableArrayList();
    }

    @FXML
    void onAnalyzeButtonClick(ActionEvent event) { // кнопка "Аналіз"
        symbol.setCellValueFactory(new PropertyValueFactory<>("Symbol"));
        pressTime.setCellValueFactory(new PropertyValueFactory<>("pressTime"));
        timeBetweenPress.setCellValueFactory(new PropertyValueFactory<>("timeBetweenPress"));

        resultTableView.setItems(analyzeList);
    }

    @FXML
    void onCheckButtonClick(ActionEvent event) { // Кнопка "Перевірити"
        KeyStrokeService.predict(resultText,authorText,nonAuthorText);
    }

    @FXML
    void onMouseClicked(MouseEvent event) {

    }

    @FXML
    void onResetButtonClick(ActionEvent event) { // кнопка "Скинути"
        analyzeList.clear();
        startPush = endPush = 0;
        i = 0;
        work = "";
        loginTextField.clear();
        KeyStrokeService.n1 = 0;
        KeyStrokeService.a1 = 0;
        authorText.setText("");
        nonAuthorText.setText("");
        resultText.setText("");
        text_time_press = "";
        text_time_between = "";
    }

    @FXML
    void onGlobalResetButton(ActionEvent event) {
        onResetButtonClick(event);
        try {
            new PrintWriter("time_between_global.txt").close();
            new PrintWriter("time_press_global.txt").close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void onSaveButtonClick(ActionEvent event) { // кнопка "Зберегти"
        text_time_between += "\n";
        text_time_press += "\n";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter("time_between.txt",true));
        BufferedWriter writer1 = new BufferedWriter(new FileWriter("time_press.txt",true))){
             writer.write(text_time_between);
             writer1.write(text_time_press);
        } catch (IOException e){
            e.printStackTrace();
        }
        text_time_press = "";
        text_time_between = "";
        onResetButtonClick(event);
    }

    @FXML
    void onGlobalSaveButtonClick(ActionEvent event) {
        try(BufferedReader reader = new BufferedReader(new FileReader("time_between.txt"));
        BufferedReader reader1 = new BufferedReader(new FileReader("time_press.txt"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("time_between_global.txt",true));
        BufferedWriter writer1 = new BufferedWriter(new FileWriter("time_press_global.txt",true))){
            String s;
            while((s=reader.readLine())!=null) {
                writer.write(s);
                writer.write("\n");
            }

            s = "";
            while((s = reader1.readLine())!= null) {
                writer1.write(s);
                writer1.write("\n");
            }

        } catch (IOException e){
            e.printStackTrace();
        }

        try {
            new PrintWriter("time_between.txt").close();
            new PrintWriter("time_press.txt").close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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

        text_time_between += time_between + "\t";
        text_time_press += endPush + "\t";

        analyzeList.add(new Analyze(work, endPush, time_between));
    }

    @FXML
    void onStatisticsButton(ActionEvent event) { // кнопка "Статистика"
        read();
    }

    @FXML
    void initialize() {

    }

    private void read(){
        List<String> list_press;
        List<String> list_between;
        try {
            list_between = Files.readAllLines(Paths.get("time_between_global.txt"));
            list_press = Files.readAllLines(Paths.get("time_press_global.txt"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < list_press.size(); i++)
            list_between.get(i).replace(" +"," ").trim();

        for (int i = 0; i < list_between.size(); i++)
            list_press.get(i).replace(" +"," ").trim();

        int n = (int) Arrays.stream(list_between.get(0).split("\t")).count();
        int l = (int) Arrays.stream(list_press.get(0).split("\t")).count();

        double[][] res_between = new double[list_between.size()][n];
        double[][] res_press = new double[list_press.size()][l];

        for (int i = 0; i < list_between.size(); i++){
            String[] array_between = Arrays.stream(list_between.get(i).split("\t")).toArray(String[]::new);

            for (int j = 0; j < n; j++)
                res_between[i][j] = Double.valueOf(array_between[j]);

        }

        for (int i = 0; i < list_press.size(); i++){
            String[] array_press = Arrays.stream(list_press.get(i).split("\t")).toArray(String[]::new);

            for (int j = 0; j < l; j++)
                res_press[i][j] = Double.valueOf(array_press[j]);
        }

        double[] arrM = StatisticsService.mathematicalExpectation(0.0,res_between);
        double[] arrD = StatisticsService.dispersion(res_between, arrM, 0.0);
        double[][] arrKor = StatisticsService.correlation(arrM, res_between);
        double[][] arrCov = StatisticsService.covariation(arrM, res_between);
        double[][] arrDov = StatisticsService.confidenceInterval(arrM,arrD,res_between.length);
        StatisticsService.print(res_between,arrM,arrD,arrKor,arrCov,arrDov,"time_between_global_results.txt");
        arrM = StatisticsService.mathematicalExpectation(0.0,res_press);
        arrD = StatisticsService.dispersion(res_press, arrM, 0.0);
        arrKor = StatisticsService.correlation(arrM, res_press);
        arrCov = StatisticsService.covariation(arrM, res_press);
        arrDov = StatisticsService.confidenceInterval(arrM,arrD,res_press.length);
        StatisticsService.print(res_press,arrM,arrD,arrKor,arrCov,arrDov,"time_press_global_results.txt");
    }

}
