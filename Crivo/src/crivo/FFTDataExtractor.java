/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package crivo;
import org.quifft.QuiFFT;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTStream;
import org.quifft.output.FrequencyBin;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class FFTDataExtractor {

    private File song;
    private FFTStream fftStream;

    public FFTDataExtractor(String filePath) {
        this.song = new File(filePath);
    }

    public void extractFFTData() {
        try {
            // Crear QuiFFT para analizar el archivo WAV
            QuiFFT quiFFT = new QuiFFT(song).windowSize(8192).windowOverlap(0.75);

            // Obtener el stream FFT del archivo de audio
            fftStream = quiFFT.fftStream();

            // Procesar cada frame del stream FFT
            while (fftStream.hasNext()) {
                FFTFrame frame = fftStream.next();
                FrequencyBin[] bins = frame.bins; // Array con datos de frecuencia/amplitud

                // Crear un array para almacenar las frecuencias y amplitudes
                double[] frequencies = new double[bins.length];
                double[] amplitudes = new double[bins.length];

                // Rellenar los arrays con datos de los bins
                for (int i = 0; i < bins.length; i++) {
                    frequencies[i] = bins[i].frequency;  // Frecuencia de cada bin
                    amplitudes[i] = bins[i].amplitude;   // Amplitud de cada bin
                }

                // Aquí puedes procesar los datos o guardarlos en un array general
                // Por ejemplo, imprimir los datos del frame
                System.out.println("Frame Start Time (ms): " + frame.frameStartMs);
                for (int i = 0; i < bins.length; i++) {
                    System.out.println("Frequency: " + frequencies[i] + " Hz, Amplitude: " + amplitudes[i] + " dB");
                }
            }

        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        FFTDataExtractor extractor = new FFTDataExtractor("E:\\Universidad\\Programacion\\Java\\Crivo\\grabacion.wav");
        extractor.extractFFTData();
    }
}
