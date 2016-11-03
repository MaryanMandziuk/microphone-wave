/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microphonevisualizer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author maryan
 */
public final class Util {
    
    public static void writeWAV(short[] data, String fileName, AudioFormat format) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length * 2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asShortBuffer().put(data);
        byte[] bytes = buffer.array();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        AudioInputStream audioInputStream = new AudioInputStream(
                bais,
                format,
                bytes.length);
        
        File fileOut = new File(fileName + ".wav");
        if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, 
            audioInputStream)) {
            try {
                AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);
            } catch (IOException ex) {
                System.out.println("Error: " + ex);
            }
        }
    }
    
}
