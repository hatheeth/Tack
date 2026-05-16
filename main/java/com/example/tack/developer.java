package com.example.tack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class developer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_developer);

        ImageView home = findViewById(R.id.gg);
        ImageView profile = findViewById(R.id.profileIcon);

        home.setOnClickListener(v -> {
            Intent intent = new Intent(developer.this, task_add.class);
            startActivity(intent);
        });



        profile.setOnClickListener(v -> {
            Intent intent = new Intent(developer.this, profile.class);
            startActivity(intent);
        });




    }
}