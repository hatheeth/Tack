package com.example.tack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {

    // 1. Declare FirebaseAuth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 2. Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Note: In your XML layout, make sure your login button has an ID assigned (e.g., loginButton)
        Button loginButton = findViewById(R.id.loginButton);
        EditText emailField = findViewById(R.id.usernameField); // Keeping your variable name, but Firebase requires the email string here
        EditText passwordField = findViewById(R.id.passwordField);
        TextView registerLink = findViewById(R.id.registerLink);

        // Hint behavior
        setHintBehavior(emailField, "Enter Your Email");
        setHintBehavior(passwordField, "Enter Your Password");

        // 🔑 Login click listener
        loginButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Basic Validation
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Authenticate user with Firebase
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Login success, navigate to the main application page
                            Toast.makeText(login.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(login.this, task_add.class); // Or whatever landing activity you want
                            startActivity(intent);
                            finish(); // Close the login screen so pressing 'Back' doesn't return here
                        } else {
                            // If authentication fails, display a helpful message to the user
                            Toast.makeText(login.this, "Login Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // 🔑 Register click listener
        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(login.this, singup.class);
            startActivity(intent);
        });
    }

    private void setHintBehavior(EditText editText, String hintText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                editText.setHint("");
            } else {
                if (editText.getText().toString().trim().isEmpty()) {
                    editText.setHint(hintText);
                }
            }
        });
    }
}