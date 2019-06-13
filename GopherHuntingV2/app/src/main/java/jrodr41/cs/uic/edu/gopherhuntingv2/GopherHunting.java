/*
    Jose E. Rodriguez
    CS 478 Spring 2019
    Project 4 - Gopher Hunting
    University of Illinois at Chicago

    This activity provides the display of the game. The user
    can watches as moves are made in continuous mode or
    advances the game at their own pace in guess by guess
    mode. Modes can be changed with the press of a button.

    Continuous means guesses will be made without stopping.
    In Guess By Guess the user presses a button to make each move.

    FIXME: Guess By Guess mode not implemented!
    FIXME: Change Mode button doesn't change mode (due to above)
    FIXME: Next Guess button also doesn't do anything (due to above)
    FIXME: Doesn't tell if guess was close, near, etc (only Success)
*/
package jrodr41.cs.uic.edu.gopherhuntingv2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GopherHunting extends AppCompatActivity {

    protected int winningHole;
    protected TextView currPlayer;
    protected TextView currMode;
    protected Button changeMode;
    protected Button playButton;
    protected Button nextGuessButton;
    protected GridView gopherGrid;
    protected GopherHuntingAdapter ghAdapter;
    protected Random random = new Random();
    protected PlayerHandlerThread p1;
    protected PlayerHandlerThread p2;

    protected int CURRENT_MODE;
    protected int currentTurn;
    protected final int GUESS_BY_GUESS_MODE = 1;
    protected final int CONTINUOUS_MODE = 2;

    // UI Handler cases
    protected final int FIRST_GUESS_MADE = 3;
    protected final int NEXT_MOVE = 4;
    protected final int UPDATE_GOPHER_HOLES = 7;

    // Player Handler cases
    protected final int MAKE_GUESS = 6;
    protected final int GAME_IS_OVER = 8;

    // UI Handler
    protected Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            GopherHole gh;
            Message newMSG;
            int guess;
            int what = msg.what;
            switch (what) {
                case FIRST_GUESS_MADE:
                    guess = msg.arg2;
                    gh = (GopherHole) ghAdapter.getItem(guess);
                    gh.setAsGuessed();
                    gh.setColor(Color.RED);
                    ghAdapter.notifyDataSetChanged();
                    Log.i("FIRST_GUESS_MADE","turn = " + currentTurn);
                    Log.i("FIRST_GUESS_MADE", "first guess = " + guess);

                    if(winningHole == guess) {
                        // Check turn for winner, stop threads, end game
                        Log.i("WINNER","Turn = " + currentTurn);
                        if(currentTurn%2 == 1)
                            currPlayer.setText(R.string.p1Wins);
                        else
                            currPlayer.setText(R.string.p2Wins);
                        //finish();
                        break;
                    }
                    currentTurn++;
                    break;
                case NEXT_MOVE:
                    if(currentTurn%2 == 1) {
                        Log.i("NEXT_MOVE: ", "Player 1's Turn = " + currentTurn);
                        changeCurrentPlayerTxt(currentTurn%2);
                        p1.addMessage(MAKE_GUESS);
                    } else {
                        Log.i("NEXT_MOVE: ", "Player 2's Turn = " + currentTurn);
                        changeCurrentPlayerTxt(currentTurn%2);
                        p2.addMessage(MAKE_GUESS);
                    }
                    break;
                case UPDATE_GOPHER_HOLES:
                    Log.i("UPDATE_GOPHER_HOLES: ", "updating");
                    // Check if guess ws already guessed before
                    guess = msg.arg2;
                    gh = (GopherHole) ghAdapter.getItem(guess);

                    if(gh.wasGuessed()) {
                        String response = "DISASTER: Hole already guessed!";
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    } else {
                        gh.setAsGuessed();
                        gh.setColor(Color.RED);
                        ghAdapter.notifyDataSetChanged();
                    }

                    if(winningHole == guess) {
                        // Check turn for winner, stop threads, end game
                        Log.i("WINNER","Turn = " + currentTurn);
                        if(currentTurn%2 == 1)
                            currPlayer.setText(R.string.p1Wins);
                        else
                            currPlayer.setText(R.string.p2Wins);
                        //finish();
                        break;
                    } else {
                        newMSG = uiHandler.obtainMessage(NEXT_MOVE);
                        uiHandler.sendMessage(newMSG);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gopher_hunting);

        // Create texts button and grid
        currPlayer = findViewById(R.id.gameCurrPlayerTxt);
        currMode = findViewById(R.id.gameCurrModeTxt);
        changeMode = findViewById(R.id.gameChangeMode);
        playButton = findViewById(R.id.playGameButton);
        nextGuessButton = findViewById(R.id.nextGuessBtn);
        gopherGrid = findViewById(R.id.gameGrid);
        ghAdapter = new GopherHuntingAdapter(this);
        gopherGrid.setAdapter(ghAdapter);

        // Randomly select a hole which contains gopher
        winningHole = random.nextInt(100);
        Log.i("Winning Hole: ", "" + winningHole);

        // Create and start threads
        p1 = new PlayerHandlerThread("Player1", uiHandler);
        p2 = new PlayerHandlerThread("Player2", uiHandler);
        p1.start();
        p2.start();

        // Get selected initial mode
        Intent intent = getIntent();
        int newMode = intent.getIntExtra("Mode", GUESS_BY_GUESS_MODE);
        changeModeText(newMode);
        changeMode.setEnabled(false);
        currentTurn = 1;

        // Set listener for button, switched current mode
        changeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapModeText();
                changeGameMode();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.GONE);
                startGame();
            }
        });

        nextGuessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Make the next move (Guess By Guess mode)
            }
        });
    }


    // FIXME: Currently only plays in Continuous mode
    public void startGame() {
        makeFirstGuesses();
        changeMode.setEnabled(true);
        if(CURRENT_MODE == GUESS_BY_GUESS_MODE)
            nextGuessButton.setEnabled(true);
        Log.i("startGame","First Guesses Made!");

        // Afterwards, p1 goes again (turn 3)
        Message msg = uiHandler.obtainMessage(NEXT_MOVE);
        uiHandler.sendMessage(msg);
    }


    // Create player threads and have them make their first guess
    private void makeFirstGuesses() {
        // Tell players to make first guess through a runnable
        p1.addRunnable(new Runnable() {
            @Override
            public void run() {
                // Make first guess randomly, change underlying data
                int guess = random.nextInt(50);

                // Tell UI handler we're done making our guess
                Message msg = uiHandler.obtainMessage(FIRST_GUESS_MADE);
                msg.arg2 = guess;
                uiHandler.sendMessage(msg);
            }
        });

        p2.addRunnable(new Runnable() {
            @Override
            public void run() {
                // Make first guess randomly, change underlying data
                int guess = random.nextInt(50) + 50;

                // Tell UI handler we're done making our guess
                Message msg = uiHandler.obtainMessage(FIRST_GUESS_MADE);
                msg.arg2 = guess;
                uiHandler.sendMessage(msg);
            }
        });
    }


    // TODO: Change the game mode in the player threads
    public void changeGameMode() {
        Log.i("ChangeGameMode:","Entered Function!");
    }


    /***************** NOTHING BEYOND HERE *******************/
    public void changeCurrentPlayerTxt(int mod) {
        if(mod == 1) {
            // Player 1's turn
            currPlayer.setText(R.string.gameTextP1);
        } else {
            // Player 2's turn
            currPlayer.setText(R.string.gameTextP2);
        }
    }


    // Change the mode text
    public void changeModeText(int newMode) {
        if (newMode == CONTINUOUS_MODE) {
            CURRENT_MODE = CONTINUOUS_MODE;
            currMode.setText(R.string.gameTextModeCNT);
        } else {
            CURRENT_MODE = GUESS_BY_GUESS_MODE;
            currMode.setText(R.string.gameTextModeGBG);
        }
    }


    // Swap the mode text
    public void swapModeText() {
        if(CURRENT_MODE == GUESS_BY_GUESS_MODE){
            CURRENT_MODE = CONTINUOUS_MODE;
            changeMode.setText(R.string.mainMode2Button);
        } else {
            CURRENT_MODE = GUESS_BY_GUESS_MODE;
            changeMode.setText(R.string.mainMode1Button);
        }
    }


    // Called at end of activity lifecycle
    // Destroy threads!
    @Override
    protected void onDestroy() {
        super.onDestroy();
        p1.quit();
        p2.quit();
        p1.interrupt();
        p2.interrupt();
    }


    /******** THREAD CLASS *******/
    public class PlayerHandlerThread extends HandlerThread {

        private PlayerHandler pHandler;
        private Handler uiHandler;
        private Random random;


        // Constructor
        public PlayerHandlerThread(String name, Handler uiHandler) {
            super(name);
            this.random = new Random();
            this.uiHandler = uiHandler;
            Log.i("Constructor: ", "initialized");
        }


        // Required, handles messages and runnables
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            pHandler = new PlayerHandler(getLooper());
        }


        // Used by UI thread to send message to worker thread
        public void addMessage(int message){
            if(pHandler != null) {
                Message msg = pHandler.obtainMessage(message);
                pHandler.sendMessageDelayed(msg, 1000);
            }
        }


        // Used by UI thread to send runnable to worker thread
        public void addRunnable(Runnable runnable) {
            if(pHandler != null) {
                pHandler.post(runnable);
            }
        }


        private class PlayerHandler extends Handler {

            public PlayerHandler(Looper looper) {
                super(looper);
            }


            @Override
            public void	handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                switch (what) {
                    case MAKE_GUESS:
                        Log.i("MAKE_GUESS: ", "Making move");
                        currentTurn++;
                        Message uiMSG = uiHandler.obtainMessage(UPDATE_GOPHER_HOLES);
                        uiMSG.arg2 = makeGuess();
                        uiHandler.sendMessage(uiMSG);
                        break;
                    case GAME_IS_OVER:
                        Log.i("GAME_IS_OVER: ", "game is over");
                        break;
                }
            }


            // Strategically guess a hole. Use adapter data to check
            // But don't change the data, let UI thread do that
            private int makeGuess() {
                Log.i("makeGuess: ", "Making Move");
                int guess = random.nextInt(100);
                //GopherHole gh;
                //boolean holeAlreadyGuessed = true;

                /*while(holeAlreadyGuessed) {
                    // Random number, get hole at position
                    guess = random.nextInt(100);
                    gh = (GopherHole) ghAdapter.getItem(guess);

                    // If hole wasn't guess yet, end loop, return guess
                    if(gh.wasGuessed() == false) {
                        Log.i("makeGuess: ", "Hole (" + guess + ") hasn't been guessed!");
                        holeAlreadyGuessed = false;
                    }
                }*/
                return guess;
            }
        }
    }
}
