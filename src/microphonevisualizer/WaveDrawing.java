/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microphonevisualizer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
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
        System.out.println("Created GUI on EDT? "+
                SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(250,250);
        MyPanel obj = new MyPanel();
        f.add(obj);
        
        f.pack();
        f.setVisible(true);


    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}


class MyPanel extends JPanel implements ActionListener {
    private short d;
    private int i =0 ;
    public TargetDataLine line;
    Timer timer;

    public MyPanel() {
        setBorder(BorderFactory.createLineBorder(Color.black));
            timer = new Timer(1, this);
//    timer.setInitialDelay(2290);
    timer.start();
        try {
          
            AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
            line = AudioSystem.getTargetDataLine(format);
            
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); 
            if (!AudioSystem.isLineSupported(info)) {

                System.out.println("error");
            }

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
            } catch (LineUnavailableException ex) {
                System.out.println("error");
            }
            

            line.start();
            
            
        } catch (LineUnavailableException ex) {
            System.out.println("error");
        }
    }
    
    public void readData() {
               
                int numBytesRead = 16;
                byte[] data = new byte[numBytesRead];
                    
            
                line.read(data, 0, numBytesRead);
                
                i++;
                
                ByteBuffer bb = ByteBuffer.wrap(data);
                d = bb.getShort();
                
                if (i == 200) {
                    i = 0;
                }
              System.out.println(d);
   
    }

    public Dimension getPreferredSize() {
        return new Dimension(500,500);
    }
    

    
    public void paint(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            g2.drawLine(14, this.i, this.d, 77);  
   
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
       readData();
        
        repaint();  
    }
}