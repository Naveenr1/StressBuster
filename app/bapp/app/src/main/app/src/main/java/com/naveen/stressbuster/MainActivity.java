package com.naveen.stressbuster;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private GameView gameView;
    private TextView tvTimer;
    private ImageButton btnStart;
    private CountDownTimer timer;
    private long sessionMs = 60000; // 60s

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Developer popup
        new AlertDialog.Builder(this)
                .setTitle("StressBuster")
                .setMessage("Developed by Naveen Rumoji")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();

        gameView = findViewById(R.id.gameView);
        tvTimer = findViewById(R.id.tvTimer);
        btnStart = findViewById(R.id.btnStart);

        updateTimerText(sessionMs);

        btnStart.setOnClickListener(v -> startSession());
    }

    private void startSession() {
        gameView.resetSession();
        if (timer != null) timer.cancel();

        timer = new CountDownTimer(sessionMs, 1000) {
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }
            public void onFinish() {
                updateTimerText(0);
                gameView.endSession();
            }
        };
        timer.start();
    }

    private void updateTimerText(long ms) {
        int seconds = (int) (ms / 1000);
        int mins = seconds / 60;
        int secs = seconds % 60;
        tvTimer.setText(String.format("%02d:%02d", mins, secs));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) timer.cancel();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }
}
