/*
    Jose E. Rodriguez
    CS 478 Spring 2019
    Project 4 - Gopher Hunting
    University of Illinois at Chicago

    This class represents a single gopher hole.

    The only information needed is whether this hole
    was guessed already or not.
*/
package jrodr41.cs.uic.edu.gopherhuntingv2;

import android.graphics.Color;

public class GopherHole {

    private boolean guessed;
    private int position;
    private int color;

    // Default constructor
    public GopherHole(int position) {
        this.guessed = false;
        this.position = position;
        this.color = Color.GREEN;
    }

    public void setAsGuessed() { this.guessed = true; }
    public void setColor(int color) { this.color = color; }
    public boolean wasGuessed() { return this.guessed; }
    public int getPosition() { return this.position; }
    public int getColor() { return this.color; }
}
