/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crivo;

import java.util.ArrayList;
import java.util.List;


public class matrizGeneral {

    // Genera una matriz cuadrada de tamaño nxn basada en los valores de amplitud
    public int[][] generateSquareMatrix(double[] amplitudes1, double[] amplitudes2, double[] amplitudes3, int n) {
        int totalAmplitudes = n * n; // Necesitamos al menos n^2 valores para la matriz cuadrada
        List<Double> allAmplitudes = new ArrayList<>();  // Cambia List<double> a List<Double>

        // Mezclar los valores de las tres grabaciones
        for (int i = 0; i < totalAmplitudes; i++) {
            if (i < amplitudes1.length) allAmplitudes.add(amplitudes1[i]);
            if (i < amplitudes2.length) allAmplitudes.add(amplitudes2[i]);
            if (i < amplitudes3.length) allAmplitudes.add(amplitudes3[i]);
        }

        // Si no tenemos suficientes amplitudes, tomamos los primeros n^2
        allAmplitudes = allAmplitudes.subList(0, Math.min(totalAmplitudes, allAmplitudes.size()));

        // Normalizar los valores y convertir a enteros (puedes ajustar esta parte según sea necesario)
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = normalizeAmplitude(allAmplitudes.get(i * n + j));
                System.out.println(matrix[i][j] + "-");
            }
            System.out.println("\n");
        }

        return matrix;
    }

    // Función para normalizar las amplitudes (convertirlas en enteros para la clave de Hill)
    private int normalizeAmplitude(double amplitude) {
        // Puedes ajustar esta normalización según sea necesario. Aquí simplemente redondeamos.
        return (int) Math.round(amplitude % 26); // Para que esté dentro de [0-25] como en Hill Cipher
    }
}
