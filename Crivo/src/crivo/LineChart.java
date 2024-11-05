
package crivo;

import java.awt.Dimension;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.quifft.output.FrequencyBin;

/**
 *
 * @author patri
 */
public class LineChart extends JFrame {
    XYSeries series;
    JFreeChart chart;

    public LineChart() {
        // Crear un conjunto de datos vacío para iniciar el gráfico
        series = new XYSeries("FFT Data");
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        // Crear un gráfico de líneas utilizando JFreeChart
        chart = ChartFactory.createXYLineChart(
                "FFT Spectrum Visualization", // Título del gráfico
                "Frequency (Hz)",             // Etiqueta del eje X
                "Amplitude (dB)",             // Etiqueta del eje Y
                dataset,                      // Conjunto de datos
                PlotOrientation.VERTICAL,
                true,                         // Mostrar leyenda
                true,                         // Mostrar tooltips
                false                         // Configuración de URLs
        );

        // Colocar el gráfico en un panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1280, 720));
        setContentPane(chartPanel);
    }

    // Método para actualizar los datos del gráfico
    public void updateChartData(FrequencyBin[] newBins, long timestamp) {
        series.clear(); // Limpiar datos antiguos
        
        // Agregar nuevos datos
        for (FrequencyBin bin : newBins) {
            double frequency = bin.frequency;
            double amplitude = bin.amplitude; // Asumo que tienes este campo en FrequencyBin
            series.add(frequency, amplitude);
        }
    }
}
