package com.example.tack;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class singup extends AppCompatActivity {

    private FirebaseAuth mAuth;
    // 1. Declare FirebaseFirestore instance
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_singup);

        mAuth = FirebaseAuth.getInstance();
        // 2. Initialize Firestore
        db = FirebaseFirestore.getInstance();

        EditText usernameField = findViewById(R.id.usernameField);
        EditText passwordField = findViewById(R.id.passwordField);
        EditText repasswordField = findViewById(R.id.passwordField2);
        EditText emailField = findViewById(R.id.email);

        setHintBehavior(usernameField, "User Name");
        setHintBehavior(passwordField, "Enter Your Password");
        setHintBehavior(repasswordField, "Re Enter Your Password");
        setHintBehavior(emailField, "Enter Your Email");

        Button signUp = findViewById(R.id.registerButton);

        signUp.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String rePassword = repasswordField.getText().toString().trim();

            // Validation
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || rePassword.isEmpty()) {
                Toast.makeText(singup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(rePassword)) {
                Toast.makeText(singup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(singup.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create User in Firebase Auth
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // User Auth success! Now get the unique User ID (UID)
                            String userId = mAuth.getCurrentUser().getUid();

                            // 3. Prepare user data to save in Firestore
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", username);
                            userMap.put("email", email);

                            // 4. Push data to Firestore under 'users' collection with document ID as the Auth UID
                            db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(singup.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(singup.this, task_add.class);
                                        startActivity(intent);
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(singup.this, "Failed to save user data: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            Toast.makeText(singup.this, "Authentication Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
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