/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microphonevisualizer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.Timer;

/**
 *
 * @author maryan
 */
public class WaveDrawing extends JFrame {

    /**
     * Creates new form WaveDrawing
     */
    public WaveDrawing() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame("Swing Paint");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MyPanel obj = new MyPanel();
        f.add(obj);

        f.pack();
        f.setVisible(true);

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

class MyPanel extends JPanel implements ActionListener {

    private static final double THRESHOLD = 1000.0;
    private static final float SAMPLERATE = 8000.0f;
    private static final double SCALER = Short.MAX_VALUE / (double) 150;
    private static final int SECONDS = 1;
    public static int count = 0;
    private short d;
    public double rms;
    public double db;
    public TargetDataLine line;
    public int sumOfSquares;

//    Timer timer;
    List<Points> points;

    /**
     * Accessing audio
     */
    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
//        timer = new Timer(0, this);
        points = new ArrayList<>();
//        
//        timer.start();
        try {

            AudioFormat format = new AudioFormat(SAMPLERATE, 16, 1, true, true);
            line = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line doesn't supported. ");
            }

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();
            

        } catch (LineUnavailableException ex) {
            System.out.println("Line unavailable " + ex);
        }
    }

    /**
     * Reading sample
     */
    public void readData() {
        int numBytesRead = 16;
        byte[] data = new byte[numBytesRead];
        line.read(data, 0, numBytesRead);
        ByteBuffer bb = ByteBuffer.wrap(data);
        d = bb.getShort();
    }

    /**
     * Reading sample
     *
     * @return 16 bytes sample
     */
    private short getSample() {
        int numBytesRead = 16;
        byte[] data = new byte[numBytesRead];
        line.read(data, 0, numBytesRead);
        ByteBuffer bb = ByteBuffer.wrap(data);
        return bb.getShort();
    }

    /**
     * Capturing samples
     *
     * @return short array of samples
     */
    private short[] getShortArray() {
        int len = (int) (SAMPLERATE * SECONDS);
        short[] data = new short[len];
        for (int i = 0; i < len; i++) {
            data[i] = getSample();
        }
        writeWAV(data);
        System.out.println("close");
        line.stop();
        line.close();
        return data;
    }

    /**
     * Scaled data method: average value in chunk
     *
     * @return short array
     */
    public short[] scaledData1() {
        int windowLenght = 800;
        short[] data = getShortArray();
        int pointsScaler = (int) (SAMPLERATE / (windowLenght / SECONDS));
        short[] scaledData = new short[data.length / pointsScaler];
        int j = 0;
        int sum = 0;
        int negSum = 0;
        int c1 = 0;
        int c2 = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 0) {
                sum += data[i];
                c1++;
            } else {
                negSum += data[i];
                c2++;
            }
            if (c1 == pointsScaler) {
                scaledData[j] = (short) (sum / c1);
                sum = 0;
                c1 = 0;
                j++;
            }
            if (c2 == pointsScaler) {
                scaledData[j] = (short) (negSum / c2);
                negSum = 0;
                c2 = 0;
                j++;
            }
        }
        return scaledData;
    }

    /**
     * Scaled data method: highest value in chunk
     *
     * @return short array
     */
    private short[] scaledData2() {
        int windowLenght = 800;
        short[] data = getShortArray();
        int pointsScaler = (int) (SAMPLERATE / (windowLenght / SECONDS));
        short[] scaledData = new short[data.length / pointsScaler];
        int j = 0;
        int sum = 0;
        int negSum = 0;
        int c1 = 0;
        int c2 = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 0) {
                if (data[i] > sum) {
                    sum = data[i];
                }
                c1++;
            } else {
                if (data[i] < negSum) {
                    negSum = data[i];
                }
                c2++;
            }
            if (c1 == pointsScaler) {
                scaledData[j] = (short) sum;
                j++;
                sum = 0;
                c1 = 0;
            }
            if (c2 == pointsScaler) {
                scaledData[j] = (short) (negSum);
                j++;
                negSum = 0;
                c2 = 0;
            }
        }
        return scaledData;
    }

    /**
     * Scaled data method: last value in chunk
     *
     * @return short array
     */
    private short[] scaledData3() {
        int windowLenght = 800;
        short[] data = getShortArray();
        int pointsScaler = (int) (SAMPLERATE / (windowLenght / SECONDS));
        short[] scaledData = new short[data.length / pointsScaler];
        int j = 0;
        int c1 = 0;
        int c2 = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 0) {
                c1++;
            } else {
                c2++;
            }
            if (c1 == pointsScaler) {
                scaledData[j] = data[i];
                c1 = 0;
                j++;
            }
            if (c2 == pointsScaler) {
                scaledData[j] = data[i];
                c2 = 0;
                j++;
            }
        }
        return scaledData;
    }

    /**
     * Getting envelope from audio data
     *
     * @param arr
     * @return short array
     */
    public short[] getEnvelope(short[] arr) {
        short[] data = new short[arr.length];
        for (int i = 0; i < data.length; i++) {
            data[i] = (short) Math.abs(arr[i]);
        }
        return data;
    }

    /**
     * Applying basic filter for envelope
     */
    public void filter(short[] envelope) {
        int N = 2;
        for (int i = 0; i < envelope.length; i++) {
            envelope[i] = getAverage(envelope, i, N);
        }
    }

    /**
     * Getting average value from envelope N samples
     *
     * @param data
     * @param index
     * @param N
     * @return
     */
    public short getAverage(short[] data, int index, int N) {
        int sum = 0;
        int start = index - N;
        int end = index + N;
        if (start < 0) {
            start = 0;
        }
        if (end > data.length) {
            end = data.length;
        }
        for (int i = start; i < end; i++) {
            sum += data[i];
        }
        return (short) (sum / (N * 2 + 1));
    }

    /**
     * Calculating points for drawing waves
     */
    public void pointsGenerate() {
//        short[] scaledData = scaledData1();
        short[] scaledData = scaledData2();
//        short[] scaledData = scaledData3();
        short[] thresholdData = getEnvelope(scaledData);
        filter(thresholdData);
        Points p;
        Color col;
        for (int i = 0; i < 800; i++) {
            if (thresholdData[i] < THRESHOLD) {
                col = Color.RED;
            } else {
                col = Color.BLACK;
            }
            if (points.isEmpty()) {
                p = new Points(800, 200, 800, 200 - (int) (scaledData[i] / SCALER), col);
                points.add(p);
            } else {
                int i2 = points.get(points.size() - 1).j2;
                p = new Points(800 - i, i2, 800 - i, 200 - (int) (scaledData[i] / SCALER), col);
                points.add(p);
            }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 400);
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        pointsGenerate();
        for (Points p : points) {
            g2.setColor(p.col);
            g2.drawLine(p.i1, p.i2, p.j1, p.j2);
        }

    }

    @Override
    public void actionPerformed(ActionEvent ae) {
//        readData();
//        Points p;
//        Color c;
//
//        if (count < 100) {
//            count++;
//            sumOfSquares += Math.pow(Math.abs((int) (d / SCALER)), 2);
//            rms = Math.sqrt(sumOfSquares / count);
//        } else {
////            rms = Math.sqrt(sumOfSquares / count);
//            count = 0;
//            sumOfSquares = 0;
//        }
//
//        for (Points p1 : points) {
//            p1.i1 = p1.i1 - 1;
//            p1.j1 = p1.j1 - 1;
//        }
//        dbCalculation();
//        if (rms < 3.0) {
////        if (Math.abs(db)  > 36) {
//            c = Color.red;
//        } else {
//            c = Color.black;
//        }
//
//        if (points.isEmpty()) {
//            p = new Points(800, 200, 800, 200 - (int) (d / SCALER), c);
//            points.add(p);
//        } else {
//            int i2 = points.get(points.size() - 1).j2;
//            p = new Points(800, i2, 800, 200 - (int) (d / SCALER), c);
//            points.add(p);
//
//        }
//
//        if (points.size() > 801) {
//            points.remove(0);
//        }

        repaint();
    }

    /**
     * Calculation decibels
     */
    private void dbCalculation() {
        double amplitude;
        if (d > 0) {
            amplitude = d / (double) Short.MAX_VALUE;
        } else {
            amplitude = d / (double) Short.MIN_VALUE;
        }
        db = 20 * Math.log10((amplitude));
    }
    
    public void writeWAV(short[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(data.length * 2);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asShortBuffer().put(data);
        byte[] bytes = buffer.array();
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        AudioInputStream audioInputStream = new AudioInputStream(bais,new AudioFormat(SAMPLERATE, 16, 1, true, true),bytes.length);
        File fileOut = new File("test.wav");
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

/**
 * class for representing wave's points
 */
class Points {

    public int i1;
    public int i2;
    public int j1;
    public int j2;
    public Color col;

    Points(int i1, int i2, int j1, int j2, Color c) {
        this.i1 = i1;
        this.i2 = i2;
        this.j1 = j1;
        this.j2 = j2;
        this.col = c;
    }
}
