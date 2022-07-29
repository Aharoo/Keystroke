package ua.aharoo.keystroke.service;

import javafx.scene.control.TextField;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.Covariance;

public class KeyStrokeService {

    public static void predict(TextField resultTextField){
        double[][] res_between = StatisticsService.mass("time_between.txt");
        double[][] res_press = StatisticsService.mass("time_press.txt");
        double[][] V = vector(res_between,res_press);
        double[] arrM = StatisticsService.mathematicalExpectation(0.0, V);
        double[] arrD = StatisticsService.dispersion(V, arrM, 0.0);
        double[][] arrCov;
        arrCov = new Covariance(MatrixUtils.createRealMatrix(V)).getCovarianceMatrix().getData();
        int info, n1 = 0, a1 = 0;
        //RealMatrix inverse = MatrixUtils.inverse(MatrixUtils.createRealMatrix(arrCov));
        double[][] res_bet_test = StatisticsService.mass("time_between.txt");
        double[][] res_pres_test = StatisticsService.mass("time_press.txt");
        for (int i = 0; i < res_bet_test.length;i++){
            double[][] bet_test = new double[1][res_bet_test[i].length];
            double[][] pres_test = new double[1][res_pres_test[i].length];
            for (int j = 0; j < res_bet_test[i].length; j++)
                bet_test[0][j] = res_bet_test[i][j];
            for (int j = 0; j < res_pres_test[i].length; j++)
                pres_test[0][j] = res_pres_test[i][j];

            double[][] V_test = vector(bet_test,pres_test);
            double r = g(arrCov,arrM,V_test);
            resultTextField.clear();
            if (r < 0) {
                a1++;
                resultTextField.setText("Спроба вводу распізнана як авторська");
            }
            else {
                n1++;
                resultTextField.setText("Спроба вводу распізнана як не авторська");
            }
        }
    }

    public static double g(double[][] arrCov, double[] arrM, double[][] U){
        double work = 0.0;
        for (int j = 0; j < arrCov[0].length;j++)
            for (int k = 0; k < arrCov[0].length; k++)
                work += arrCov[j][k] * ((U[0][j] - arrM[j]) * (U[0][k]) - arrM[k]);

        return (0.5 * work - 3.3735 * 3.3735);
    }

    public static double[][] vector(double[][] between, double[][] press){
        int z = 0;
        double[] arrMint = mint(between);
        double[] arrAlf = arithm(between, arrMint);
        double[] si = speed(between,press);
        double[][] diff = difference(between);
        double[][] V = new double[between.length][between[0].length + 5];
        for (int t = 0; t < V.length; t++){
            for (z = 0; z < V[t].length - 5; z++){
                V[t][z] = between[t][z];
            }
            V[t][z] = arrMint[t];
            V[t][z + 1] = arrAlf[t];
            V[t][z + 2] = si[t];
            V[t][z + 3] = diff[t][1];
            V[t][z + 4] = diff[t][2];
        }
        return V;
    }

    // Нормоване мат.очікування
    public static double[] mint(double[][] arr){
        double work = 0;
        double[] arrM = new double[arr.length];
        for (int i = 0; i < arr.length; i++){
            for (int j = 0; j < arr[i].length; j++){
                work += arr[i][j] / max(arr,i);
                // Середні mint= ∑X[i,k]/max⁡(X[i,k]〗
            }
            arrM[i] = work / arr[i].length;
            work = 0;
        }
        return arrM;
    }

    // Аритмічність набору
    public static double[] arithm(double[][] arr, double[] arrM){
        double work = 0;
        double[] alf = new double[arr.length];
        for (int i = 0; i < arr.length; i++){
            for (int j = 0; j < arr[i].length; j++){
                work += Math.pow(arr[i][j] / max(arr,i) - arrM[i], 2);
                // ∑(X[i,k]/max()-mint[i])^2
            }
            alf[i] = Math.sqrt(work / (arr[i].length - 1));
            //√(1/(n-1)
        }
        return alf;
    }

    public static double[] speed(double[][] arrB, double[][] arrP){
        double work = 0;
        double[] arrS = new double[arrB.length];
        for (int i = 0; i < arrB.length; i++){
            for (int j = 0; j < arrB[i].length; j++)
                work += arrB[i][j]; // ∑X[i,k]

            for (int j = 0; j < arrP[i].length; j++)
                work += arrP[i][j]; // ∑X[i,k]


            arrS[i] = work * 0.015; // (900/60)*1/1000 (измиллисек в сек)
            work = 0;
        }
        return arrS;
    }

    public static double[][] difference(double[][] between){
        double[][] arrD = new double[between.length][3];
//        double[] arr = new double[between.length - 1];
//        double work = 0, plus = 0, exch = 0;
//
//        for (int i = 0; i < between.length; i++){
//            for (int j = 0; j < arr.length; j++)
//                arr[j] = between[i][j + 1] - between[i][j];
//            if (arr[i] >= 0) plus += 1;
//            for (int j = 1; j < arr.length; j++){
//                if (arr[j] * arr[j - 1] <= 0) exch += 1;
//                if (arr[j] >= 0) plus +=1;
//            }
//            arrD[i][1] = plus / arr.length;
//            arrD[i][2] = exch / arr.length - 1;
//            work = plus = exch = 0;
//        }
        return arrD;
    }

    // Число перекриттів
    public static double ns(double[][] res_between, int i){
        double ns = 0;
        for (int j = 0; j < res_between[1].length; j++)
            if (res_between[i][j] < 0) ns += 1;
        return ns;
    }

    public static double max(double[][] arr, int h){
        double work = arr[0][h];
        for (int i = 0; i < arr.length; i++){
            if (arr[i][h] > work) work = arr[i][h];
        }
        return work;
    }
}
