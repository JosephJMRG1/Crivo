package crivo;
import java.io.File;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
/**
 *
 * @author patri
 */
public class Grabadora {
    AudioFileFormat.Type tipo_formato = AudioFileFormat.Type.WAVE;
    AudioFormat Formato = new AudioFormat(8000.0f,16,1,true,false);
    TargetDataLine tD;
    File archivo = new File("grabacion.wav");
    
    public Grabadora(){
        try{
            DataLine.Info dLI = new DataLine.Info(TargetDataLine.class,Formato);
            tD = (TargetDataLine) AudioSystem.getLine(dLI);
            new CapThread().start();
            System.out.println("Grabando durante 5s...");
            Thread.sleep(5000);//5 segundos de grabacion
            tD.close();
            
        }catch(Exception e){}
    }
    class CapThread extends Thread {
        @Override
        public void run() {
            try {
                tD.open(Formato);
                tD.start();
                AudioSystem.write(new AudioInputStream(tD), tipo_formato, archivo);
            } catch (Exception e) { }
        }
    }
}
