package com.application.workouttimer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.health.TimerStat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Time;
import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    // UI Elements
    private TextView phaseTextElement;
    private ProgressBar progressBarElement;
    private TextView progressTextElement;
    private EditText workoutDurationInputElement;
    private EditText restDurationInputElement;
    private Button startButtonElement;
    private Button stopButtonElement;

    // Since the app can be in two states/phases, I'll use an enum to represent this.
    private enum TimerState{
        Workout,
        Rest
    }

    private TimerState timerState;

    // Duration tracking
    long currentDuration;

    // Timer Handling
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetCoreElements();

        timerState = TimerState.Workout;

        HandleButtonLogic();
    }

    private void GetCoreElements(){

        phaseTextElement = findViewById(R.id.phaseText);
        progressBarElement = findViewById(R.id.durationProgressBar);
        progressTextElement = findViewById(R.id.durationText);
        workoutDurationInputElement = findViewById(R.id.workoutDurationInput);
        restDurationInputElement = findViewById(R.id.restDurationInput);
        startButtonElement = findViewById(R.id.startButton);
        stopButtonElement = findViewById(R.id.stopButton);
    }

    private void HandleButtonLogic(){

        startButtonElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                CreateCountDownTimer();
            }
        });

        stopButtonElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (countDownTimer != null) {

                    countDownTimer.cancel();

                    timerState = TimerState.Workout;
                    progressBarElement.setProgress(100);
                    phaseTextElement.setText(R.string.workout_phase_name);
                    progressTextElement.setText(workoutDurationInputElement.getText().toString());
                }
            }
        });
    }

    private void CreateCountDownTimer(){

        SetCurrentMaxDuration();

        // Create a new count down timer that will tick every 1 second for a total duration based on the input.
        // The input being used will be dependent on the state of the timer.
        countDownTimer = new CountDownTimer(currentDuration, 1000){

            @Override
            public void onTick(long l) {

                // Set the progress bar fill based on the percentage
                Integer timerAsPercentage = Math.round((float) l / (float) currentDuration * 100);
                progressBarElement.setProgress(timerAsPercentage);

                // Set the text to be the current duration
                String remainingDurationAsString = String.valueOf(Math.round(l * 0.001));
                progressTextElement.setText(remainingDurationAsString);

            }

            @Override
            public void onFinish() {

                progressBarElement.setProgress(0);

                // Keeping it simple for now, but would need a better way of handling these states.
                if (timerState == TimerState.Workout){
                    timerState = TimerState.Rest;
                    phaseTextElement.setText(R.string.rest_phase_name);
                } else if(timerState == TimerState.Rest){
                    timerState = TimerState.Workout;
                    phaseTextElement.setText(R.string.workout_phase_name);
                }

                CreateCountDownTimer();
            }
        }.start();
    }

    private void SetCurrentMaxDuration(){

        // Since this app only has two states, I'll just use switch statements - that way I can add on more if needed quite easily.
        switch (timerState){

            case Workout:

                currentDuration = Long.parseLong(workoutDurationInputElement.getText().toString()) * 1000;
                break;

            case Rest:

                currentDuration = Long.parseLong(restDurationInputElement.getText().toString()) * 1000;
                break;
        }
    }
}