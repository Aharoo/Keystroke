package ua.aharoo.keystroke.service;

public class KeyStrokeService {



    public void predict(){
        double[][] res_between = StatisticsService.mass("time_between.txt");
        double[][] res_press = StatisticsService.mass("time_press.txt");
        double[][] V = vector(res_between,res_press);
        double[] arrM = StatisticsService.mathematicalExpectation(0.0, V);
        double[] arrD = StatisticsService.dispersion(V, arrM, 0.0);
        double[][] arrCov = new double[V[1].length][V[1].length];

    }

    public static double[][] vector(double[][] between, double[][] press){
        int z = 0;
        double[] arrMint = mint(between);
        double[] arrAlf = arithm(between, arrMint);
        double[] si = speed(between,press);
        double[][] diff = difference(between);
        double[][] V = new double[between[0].length][between[1].length + 5];
        for (int t = 0; t < V[0].length; t++){
            for (z = 0; z < V[1].length - 5; z++){
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
        double[] arrM = new double[arr[1].length];
        for (int i = 0; i < arr[0].length; i++){
            for (int j = 0; j < arr[1].length; j++){
                work += arr[i][j] / max(arr,i);
                // Середні mint= ∑X[i,k]/max⁡(X[i,k]〗
            }
            arrM[i] = work / arr[1].length;
            work = 0;
        }
        return arrM;
    }

    // Аритмічність набору
    public static double[] arithm(double[][] arr, double[] arrM){
        double work = 0;
        double[] alf = new double[arr[0].length];
        for (int i = 0; i < arr[0].length; i++){
            for (int j = 0; j < arr[1].length; j++){
                work += Math.pow(arr[i][j] / max(arr,i) - arrM[i], 2);
                // ∑(X[i,k]/max()-mint[i])^2
            }
            alf[i] = Math.sqrt(work / (arr[1].length - 1));
            //√(1/(n-1)
        }
        return alf;
    }

    public static double[] speed(double[][] arrB, double[][] arrP){
        double work = 0;
        double[] arrS = new double[arrB[0].length];
        for (int i = 0; i < arrB[0].length; i++){
            for (int j = 0; i < arrP[1].length; j++)
                work += arrP[i][j]; // ∑X[i,k]

            for (int j = 0; i < arrB[1].length; j++)
                work += arrB[i][j]; // ∑X[i,k]

            arrS[i] = work * 0.015; // (900/60)*1/1000 (измиллисек в сек)
            work = 0;
        }
        return arrS;
    }

    public static double[][] difference(double[][] between){
        double[][] arrD = new double[between[0].length][3];
        double[] arr = new double[between[1].length - 1];
        double work = 0, plus = 0, exch = 0;

        for (int i = 0; i < between[0].length; i++){
            for (int j = 0; j < arr.length; j++)
                arr[j] = between[i][j + 1] - between[i][j];
            if (arr[0] >= 0) plus += 1;
            for (int j = 1; j < arr.length; j++){
                if (arr[j] * arr[j - 1] <= 0) exch += 1;
                if (arr[j] >= 0) plus +=1;
            }
            arrD[i][1] = plus / arr.length;
            arrD[i][2] = exch / arr.length - 1;
            work = plus = exch = 0;
        }
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
        double work = arr[h][0];
        for (int i = 1; i < arr[1].length; i++){
            if (arr[h][i] > work) work = arr[h][i];
        }
        return work;
    }
}
