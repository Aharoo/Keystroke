package ua.aharoo.keystroke.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class StatisticsService {

    // Розрахунок мат.очікування
    public static double[] mathematicalExpectation(double work, double[][] arr){
        double[] arrM = new double[arr[1].length];
        for (int i = 0; i < arr[1].length; i++){
            for (int j = 0; j < arr.length; j++){
                work += arr[j][i];
            }
            arrM[i] = work / arr.length;
            work = 0;
        }
        return arrM;
    }

    // Розрахунок дисперсії
    public static double[] dispersion(double[][] arr, double[] arrM, double work){
        double[] arrD = new double[arr[1].length];
        for (int i = 0; i < arr[1].length; i++){
            for (int j = 0; j < arr.length; j++){
                work += Math.pow(arr[j][i] - arrM[i],2);
            }
            arrD[i] = work / (arr.length - 1);
            work = 0;
        }
        return arrD;
    }

    // Розрахунок матриці кореляцій
    public static double[][] correlation(double[] arrM, double[][] arr){
        double[][] arrKor = new double[arr[1].length][arr[1].length];

        for (int i = 0; i < arr[1].length; i++){
            for (int j = 0; j < arr[1].length; j++){
                arrKor[i][j] = upCor(i,j,arrM,arr) / downCor(i,j,arrM,arr);
            }
        }
        return arrKor;
    }

    // Розрахунок числівника в кореляції
    public static double upCor(int j, int h, double[] arrM, double[][] arr){
        double res = 0;
        for (int i = 0; i < arr.length; i++){
            res += (arr[i][j] - arrM[j]) * (arr[i][h] - arrM[h]);
        }
        return res;
    }

    // Розрахунок знаменника в кореляції
    public static double downCor(int j, int h, double[] arrM, double[][] arr){
        double res1 = 0, res2 = 0;

        for (int i = 0; i < arr.length; i++){
            res1 += Math.pow(arr[i][j] - arrM[j], 2);
            res2 += Math.pow(arr[i][h] - arrM[h], 2);
        }
        return Math.sqrt(res1) * Math.sqrt(res2);
    }

    // Розрахунок матриці коваріацій
    public static double[][] covariation(double[] arrM, double[][] arr){
        double[][] arrCov = new double[arr[1].length][arr[1].length];
        for (int i = 0; i < arr[1].length; i++){
            for (int j = 0; j < arr[1].length; j++){
                arrCov[i][j] = upCor(i,j,arrM,arr) / arr.length;
            }
        }
        return arrCov;
    }

    // Розрахунок довірчих інтервалів
    public static double[][] confidenceInterval(double[] arrM, double[] arrD, int n){
        double[][] arrDov = new double[2][arrM.length];
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < arrM.length; j++){
                if (i == 0) arrDov[i][j] = arrM[j] - 1.9719 * Math.sqrt(arrD[j]) / Math.sqrt(n);
                else arrDov[i][j] = arrM[j] + 1.9719 * Math.sqrt(arrD[j]) / Math.sqrt(n);
            }
        }
        return arrDov;
    }

    // Текст в масив
    public static double[][] mass(String file){
        List<String> list;
        try {
            list = Files.readAllLines(Paths.get(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < list.size(); i++)
            list.get(i).replace(" +"," ").trim();

        int m = list.size();
        int n = (int) Arrays.stream(list.get(0).split("\t")).count();
        double[][] arr = new double[m][n];

        for (int i = 0; i < m; i++){
            String[] array = Arrays.stream(list.get(i).split("\t")).toArray(String[]::new);

            for (int j = 0; j < array.length; j++)
                arr[i][j] = Double.parseDouble(array[j]);

        }

        return arr;
    }

    // Запис підрахунку в файл
    public static void print(double[][] arr, double[] arm, double[] ard,
                             double[][] arrKor, double[][] arrCov,
                             double[][] arrDov, String file){
        String str = "Матриця";
        for (int i = 0; i < arr.length; i++){
            str += "\n\t";
            for (int j = 0; j < arr[i].length; j++){
                str += arr[i][j] + "\t";
            }
        }
        str += "\n\nMx\t";
        for (int i = 0; i < arm.length; i++)
            str += Math.round(arm[i]) + "\t";
        str += "\n\nDx\t";
        for (int i = 0; i < ard.length; i++)
            str += Math.round(ard[i]) + "\t";
        str += "\n\nsigma\t";
        for (int i = 0; i < ard.length; i++)
            str += Math.sqrt(ard[i]) + "\t";
        str += "\n\nКореляція:\t";
        for (int i = 0; i < arr[1].length; i++){
            str += "\n\t";
            for (int j = 0; j < arr[1].length; j++){
                str += Math.round(arrKor[i][j]) + "\t";
            }
        }
        str += "\n\nКоваріація:\t";
        for (int i = 0; i < arr[1].length; i++){
            str += "\n\t";
            for (int j = 0; j < arr[1].length; j++){
                str += Math.round(arrCov[i][j]) + "\t";
            }
        }
        str += "\n\nДовірчі інтервали:\t";
        for (int i = 0; i < 2; i++){
            str += "\n\t";
            for (int j = 0; j < arm.length; j++){
                str += Math.round(arrDov[i][j]) + "\t";
            }
        }

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
            writer.write(str);
        } catch (IOException e){
            e.printStackTrace();
        }
    }


}
