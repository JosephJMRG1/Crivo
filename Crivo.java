package crivo;

import java.awt.*;
import java.io.File;

import java.io.IOException;
import java.util.ArrayList;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.*;
import javax.swing.*;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.quifft.QuiFFT;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTStream;
import org.quifft.output.FrequencyBin;



public class Crivo {
    
    // Song to play
    private File song;
    private QuiFFT quiFFT;  // ahora es una variable de instancia
    // FFTStream used to compute FFT frames
    private FFTStream fftStream;  // También cambiamos a instancia
    // Next frame to graph
    private FFTFrame nextFrame;  // También cambiamos a instancia
    
    // Wrapper for JFreeChart line graph
    private static FFTGrapher grapher = new FFTGrapher();
    
    private double[] amplitudes;
    private double[] amplitudes2;
    private double[] amplitudes3;
    /**
     * Initialize audio file here
     */
    private Crivo() {
        // Inicializar el archivo de audio
        song = new File("E:\\Universidad\\Programacion\\Java\\Crivo\\grabacion.wav");
    }

    public static void main(String[] args) {
        
        Grabadora gr = new Grabadora();
        Crivo visualizer = new Crivo();  // Crear una instancia de Crivo
        gr.Grabadora();
        grapher.initializeGraph();
        visualizer.visualizeSpectrum();  // Llamar al método de instancia visualizeSpectrum()
        visualizer.actualizaAmplitud();
        
        gr.Grabadora();
        grapher.initializeGraph();
        visualizer.visualizeSpectrum();
        visualizer.actualizaAmplitud2();
        
        gr.Grabadora();
        grapher.initializeGraph();
        visualizer.visualizeSpectrum();
        visualizer.actualizaAmplitud3();
        
        visualizer.generateSquareMatrix(32);
        // Pasar la instancia de quiFFT que está dentro del objeto visualizer
        //ext.extractFFTData(visualizer.getQuiFFT());
    }
    private void visualizeSpectrum() {
        // Obtener FFTStream para la canción desde QuiFFT
        
        try {
            quiFFT = new QuiFFT(song).windowSize(8192).windowOverlap(0.75);
        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
        fftStream = quiFFT.fftStream();
        System.out.println(fftStream);

        // Compute first frame
        nextFrame = fftStream.next();

        // Start playing audio
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(song);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

            // Agregar un listener para liberar el clip al terminar la reproducción
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    clip.close();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Calculate time between consecutive FFT frames
        double msBetweenFFTs = fftStream.windowDurationMs * (1 - fftStream.fftParameters.windowOverlap);
        long nanoTimeBetweenFFTs = Math.round(msBetweenFFTs * Math.pow(10, 6));

        // Begin visualization cycle
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::graphThenComputeNextFrame, 0, nanoTimeBetweenFFTs, TimeUnit.NANOSECONDS);
        
        
        
    }
    private void actualizaAmplitud(){
        FFTDataExtractor ext = new FFTDataExtractor();
        ext.extractFFTData(quiFFT);
        amplitudes = ext.getAmplitudes();
    }
    private void actualizaAmplitud2(){
        FFTDataExtractor ext = new FFTDataExtractor();
        ext.extractFFTData(quiFFT);
        amplitudes2 = ext.getAmplitudes();
    }
    private void actualizaAmplitud3(){
        FFTDataExtractor ext = new FFTDataExtractor();
        ext.extractFFTData(quiFFT);
        amplitudes3 = ext.getAmplitudes();
    }
    public double[] getAmplitudes() {
            return amplitudes;
        }

    public double[] getAmplitudes2() {
        return amplitudes2;
    }

    public double[] getAmplitudes3() {
        return amplitudes3;
    }
    
    private void graphThenComputeNextFrame() {
        // Graph currently stored frame
        FrequencyBin[] bins = nextFrame.bins;
        long timestamp = (long) nextFrame.frameStartMs / 1000;
        grapher.updateFFTData(bins, timestamp);

        // If next frame exists, compute it
        if(fftStream.hasNext()) {
            nextFrame = fftStream.next();
        } else { // otherwise song has ended, so end program
            try{
            Thread.sleep(2*1000);
            }catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
        }
    }
  
    
    //-----------
    
    public int[][] generateSquareMatrix(int n) {
        int totalAmplitudes = n * n; // Necesitamos al menos n^2 valores para la matriz cuadrada
        java.util.List<Double> allAmplitudes = new ArrayList<>();  // Cambia List<double> a List<Double>

        // Mezclar los valores de las tres grabaciones
        for (int i = 0; i < totalAmplitudes; i++) {
            if (i < amplitudes.length) allAmplitudes.add(amplitudes[i]);
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
                System.out.print(matrix[i][j] + "-");
            }
            System.out.print("\n");
        }

        return matrix;
    }

    // Función para normalizar las amplitudes (convertirlas en enteros para la clave de Hill)
    private int normalizeAmplitude(double amplitude) {
        // Puedes ajustar esta normalización según sea necesario. Aquí simplemente redondeamos.
        return (int) Math.round(amplitude % 26); // Para que esté dentro de [0-25] como en Hill Cipher
    }
    
    
}


class FFTGrapher {

    private LineChart chart;

    void initializeGraph() {
        // Initialize chart
        chart = new LineChart();
        chart.setSize(1280, 720);
        chart.setLocationRelativeTo(null);
        chart.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        chart.setVisible(true);

        // Set chart color and axis scales
        XYPlot plot = (XYPlot)chart.chart.getPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, new Color(0, 128, 0));

        NumberAxis domainAxis = new LogarithmicAxis("Frequency (Hz)");
        domainAxis.setRange(5, 18500);
        plot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setRange(-80, 0);
    }

    void updateFFTData(FrequencyBin[] newBins, long timestamp) {
        chart.updateChartData(newBins, timestamp);
    }
}
