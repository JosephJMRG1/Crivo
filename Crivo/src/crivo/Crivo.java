
package crivo;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.quifft.QuiFFT;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTStream;
import org.quifft.output.FrequencyBin;

import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;

import javax.swing.*;
import java.awt.*;


public class Crivo {
    Grabadora gr = new Grabadora();

    private File song;

    // FFTStream used to compute FFT frames
    private static FFTStream fftStream;

    // Next frame to graph
    private static FFTFrame nextFrame;

    // Wrapper for JFreeChart line graph
    private static FFTGrapher grapher = new FFTGrapher();

    /**
     * Initialize audio file here
     */
    public Crivo() {  // El nombre del constructor debe coincidir con el nombre de la clase
        ClassLoader classLoader = getClass().getClassLoader();
        //song = new File(classLoader.getResource("E:\\Universidad\\Programacion\\Java\\Crivo\\grabacion.wav").getFile());  // Cambiado a WAV
        song = new File("grabacion.wav");

    }

    public static void main(String[] args) {        
        Crivo visualizer = new Crivo();  // Cambié el nombre a Crivo
        FFTDataExtractor extractor = new FFTDataExtractor("E:\\Universidad\\Programacion\\Java\\Crivo\\grabacion.wav");
        grapher.initializeGraph();
        visualizer.visualizeSpectrum();
        extractor.extractFFTData();
    }

    public void visualizeSpectrum() {
        // Obtain FFTStream for song from QuiFFT
        QuiFFT quiFFT = null;
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
            // Usar Java Sound API para reproducir el archivo WAV
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(song);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();  // Reproducir audio
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }

        // Calculate time between consecutive FFT frames
        double msBetweenFFTs = fftStream.windowDurationMs * (1 - fftStream.fftParameters.windowOverlap);
        long nanoTimeBetweenFFTs = Math.round(msBetweenFFTs * Math.pow(10, 6));

        // Begin visualization cycle
        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(Crivo::graphThenComputeNextFrame, 0, nanoTimeBetweenFFTs, TimeUnit.NANOSECONDS);  // Cambié a Crivo
    }

    public static void graphThenComputeNextFrame() {
        // Graph currently stored frame
        FrequencyBin[] bins = nextFrame.bins;
        long timestamp = (long) nextFrame.frameStartMs / 1000;
        grapher.updateFFTData(bins, timestamp);

        // If next frame exists, compute it
        if (fftStream.hasNext()) {
            nextFrame = fftStream.next();
        } else { // otherwise song has ended, so end program
            System.exit(0);
        }
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
        XYPlot plot = (XYPlot) chart.chart.getPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.getRenderer().setSeriesPaint(0, new Color(0, 128, 0));

        NumberAxis domainAxis = new LogarithmicAxis("Frequency (Hz)");
        domainAxis.setRange(5, 22000);
        plot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(-80, 0);
    }

    void updateFFTData(FrequencyBin[] newBins, long timestamp) {
        chart.updateChartData(newBins, timestamp);
    }
}

