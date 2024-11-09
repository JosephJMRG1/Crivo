package crivo;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.quifft.QuiFFT;
import org.quifft.output.FFTFrame;
import org.quifft.output.FFTStream;
import org.quifft.output.FrequencyBin;

public class FFTDataExtractor {
    double[] amplitudes;//datos importantes
    private FFTStream fftStream;

    public FFTDataExtractor() {
    
    }
    
    public void extractFFTData(QuiFFT quiFFT) {
        fftStream = quiFFT.fftStream();
        while (fftStream.hasNext()) {
            FFTFrame frame = fftStream.next();
            FrequencyBin[] bins = frame.bins;
            double[] frequencies = new double[bins.length];
            amplitudes = new double[bins.length];
            for (int i = 0; i < bins.length; i++) {
                frequencies[i] = Math.round(bins[i].frequency);
                amplitudes[i] = Math.round(bins[i].amplitude);
            }
          //processFrameData(frame, frequencies, amplitudes);
          //writeDataToFile(frequencies, amplitudes);
        }
    }

    private void processFrameData(FFTFrame frame, double[] frequencies, double[] amplitudes) {
        System.out.println("Frame Start Time (ms): " + frame.frameStartMs);
        for (int i = 0; i < frequencies.length; i++) {
            System.out.println("Frecuencia: " + frequencies[i] + " Hz, Amplitud: " + amplitudes[i] + " dB");
        }
        //writeDataToFile(frequencies, amplitudes);
    }

    private void writeDataToFile(double[] frequencies, double[] amplitudes) {
        try {
            File outputFile = new File(System.getProperty("user.dir"), "DatosFFT.txt");
            FileWriter writer = new FileWriter(outputFile);
            for (int i = 0; i < frequencies.length; i++) {
                writer.write("amp:"+amplitudes[i] + "\n");
            }
            writer.close();
            System.out.println("FFT data written to file: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error writing FFT data to file: " + e.getMessage());
        }
    }
    
    public double[] getAmplitudes(){
        return amplitudes;
    }
}