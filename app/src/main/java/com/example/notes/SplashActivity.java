package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Inicia la siguiente actividad después del tiempo de espera
                Intent intent = new Intent(SplashActivity.this, Lobby_Notes.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DELAY);
    }
}