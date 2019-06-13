/*
    Jose E. Rodriguez
    CS 478 Spring 2019
    Project 4 - Gopher Hunting
    University of Illinois at Chicago

    This app is an implementation of the game Gopher
    Hunting.  Imagine you are in a field occupied by a
    gopher. The gopher could be hiding in one of any
    number of holes in the field’s ground. The goal of the
    game is to find the hole that contains the gopher

    This main activity asks the user to select a mode that
    the game will operate under before starting.

    Continuous means guesses will be made without stopping.
    In Guess By Guess the user presses a button to make each move.
*/
package jrodr41.cs.uic.edu.gopherhuntingv2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    protected TextView selectedModeTxt;
    protected Button guessByGuessBtn;
    protected Button continuousBtn;
    protected Button startGameBtn;
    protected final int GUESS_BY_GUESS_MODE = 1;
    protected final int CONTINUOUS_MODE = 2;
    protected final int NO_MODE_SELECTED = 0;
    protected int selectedMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedMode = NO_MODE_SELECTED;
        selectedModeTxt = findViewById(R.id.modeSelected);

        // Setup buttons
        guessByGuessBtn = findViewById(R.id.guessByGuessButton);
        continuousBtn = findViewById(R.id.continuousButton);
        startGameBtn = findViewById(R.id.startGameButton);
        startGameBtn.setEnabled(false);

        guessByGuessBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeButtonPressed(GUESS_BY_GUESS_MODE);
            }
        });

        continuousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeButtonPressed(CONTINUOUS_MODE);
            }
        });

        startGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GopherHunting.class);
                intent.putExtra("Mode", selectedMode);
                startActivity(intent);
            }
        });
    }


    // Change currently selected based on which button was pressed
    private void modeButtonPressed(int mode) {
        // Change text based on mode
        if(mode == GUESS_BY_GUESS_MODE) {
            selectedModeTxt.setText(R.string.selectedMode1);
        } else if (mode == CONTINUOUS_MODE) {
            selectedModeTxt.setText(R.string.selectedMode2);
        }
        selectedMode = mode;

        // Enable start game if not already enabled
        if(!startGameBtn.isEnabled()){
            startGameBtn.setEnabled(true);
        }
    }
}
