/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package microphonevisualizer;

import java.awt.Color;

/**
 * class for representing wave's points
 * @author maryan
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
