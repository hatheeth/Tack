package com.example.tack;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View dot1 = findViewById(R.id.dot1);
        View dot2 = findViewById(R.id.dot2);
        View dot3 = findViewById(R.id.dot3);
        View dot4 = findViewById(R.id.dot4);

        Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        Handler handler = new Handler();

        // Animate each dot one after another
        handler.postDelayed(() -> dot1.startAnimation(scale), 0);
        handler.postDelayed(() -> dot2.startAnimation(scale), 400);
        handler.postDelayed(() -> dot3.startAnimation(scale), 800);
        handler.postDelayed(() -> dot4.startAnimation(scale), 1200);

        // Move to next screen after animation
        handler.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
}
