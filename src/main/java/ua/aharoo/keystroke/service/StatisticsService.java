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
        double [] arrM = new double[arr[1].length];
        for (int i = 0; i < arr[1].length; i++){
            for (int j = 0; j < arr[0].length; i++){
                work += arr[j][i];
            }
            arrM[i] = work / arr[0].length;
            work = 0;
        }
        return arrM;
    }

    // Розрахунок дисперсії
    public static double[] dispersion(double[][] arr, double[] arrM, double work){
        double[] arrD = new double[arr[1].length];
        for (int i = 0; i < arr[1].length; i++){
            for (int j = 0; j < arr[0].length; j++){
                work += Math.pow(arr[j][i] - arrM[i],2);
            }
            arrD[i] = work / (arr[0].length - 1);
            work = 0;
        }
        return arrD;
    }

    // Розрахунок матриці кореляцій
    public static double[][] correlation(double[] arrM, int[][] arr, int n){
        double[][] arrKor = new double[arr[0].length][arr[1].length];

        for (int i = 0; i < arr[1].length; i++){
            for (int j = 0; j < arr[1].length; i++){
                arrKor[i][j] = upCor(i,j,arrM,arr) / downCor(i,j,arrM,arr);
            }
        }
        return arrKor;
    }

    // Розрахунок числівника в кореляції
    public static double upCor(int j, int h, double[] arrM, int[][] arr){
        double res = 0;
        for (int i = 0; i < arr[0].length; i++){
            res += (arr[i][j] - arrM[j]) * (arr[i][h] - arrM[h]);
        }
        return res;
    }

    // Розрахунок знаменника в кореляції
    public static double downCor(int j, int h, double[] arrM, int[][] arr){
        double res1 = 0, res2 = 0;

        for (int i = 0; i < arr[0].length; i++){
            res1 += Math.pow(arr[i][j] - arrM[j], 2);
            res2 += Math.pow(arr[i][h] - arrM[h], 2);
        }
        return Math.sqrt(res1) * Math.sqrt(res2);
    }

    // Розрахунок матриці коваріацій
    public static double[][] covariation(double[] arrM, int[][] arr, int n){
        double[][] arrCov = new double[arr[1].length][arr[1].length];
        for (int i = 0; i < arr[1].length; i++){
            for (int j = 0; j < arr[1].length; j++){
                arrCov[i][j] = upCor(i,j,arrM,arr) / arr[0].length;
            }
        }
        return arrCov;
    }

    // Розрахунок довірчих інтервалів
    public static double[][] confidenceInterval(double[] arrM, double[] arrD, int n ){
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
        int z = 0;
        try {
            list = Files.readAllLines(Paths.get(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < list.size(); i++){
            list.get(i).replace(" +"," ").trim();
            z++;
        }

        int m = list.size();
        int n = (int) Arrays.stream(list.get(0).split("\t")).count();
        double[][] arr = new double[m][n];
        int j = 0;

        for (int i = 0; i < m; i++){
            for(String s: (String[]) Arrays.stream(list.get(i).split("\t")).toArray()){
                arr[i][j] = Double.valueOf(s);
                j++;
            }
            j = 0;
        }
        return arr;
    }

    // Запис підрахунку в файл
    public static void Print(int[][] arr, double[] arm, double[] ard,
                             double[][] arrKor, double[][] arrCov,
                             double[][] arrDov, String file){
        String str = "Матриця", work = "";
        for (int i = 0; i < arr[0].length; i++){
            str += "\n\t";
            for (int j = 0; j < arr[1].length; j++){
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
            for (int j = 0; j < arr[0].length; j++){
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
