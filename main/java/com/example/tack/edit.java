package com.example.tack;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class edit extends AppCompatActivity {

    private EditText editUserName;
    private Button btnSave;
    private Button btnCancel;  // 🔑 Added Cancel Button variable
    private ImageView navHome;  // 🔑 Added Home Icon variable
    private ImageView navUser;

    // Firebase Components
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        // Handle System Bars (Edge-to-Edge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Safety check to ensure user is logged in
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        } else {
            startActivity(new Intent(edit.this, login.class));
            finish();
            return;
        }

        // Initialize views from XML
        editUserName = findViewById(R.id.editUserName);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel); // 🔑 Map your XML Cancel button
        navHome = findViewById(R.id.navHome);       // 🔑 Map your XML Home bottom item
        navUser = findViewById(R.id.navUser);

        // Fetch and pre-fill the current username from Firestore
        loadCurrentUsername();

        // Set up the Save Button to update Firestore
        btnSave.setOnClickListener(v -> {
            String newName = editUserName.getText().toString().trim();
            if (!newName.isEmpty()) {
                updateUsernameInFirebase(newName);
            } else {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // 🔑 Set up the Cancel Button to drop back to Profile screen
        btnCancel.setOnClickListener(v -> {
            Intent intent = new Intent(edit.this, profile.class);
            startActivity(intent);
            finish(); // Closes the edit activity instance
        });

        // 🔑 Set up the Bottom Navigation Home Icon to jump directly to tasks page
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(edit.this, task_add.class);
            startActivity(intent);
            finish(); // Closes the edit activity instance
        });
    }

    // Method to fetch data from Firestore and drop it into the EditText
    private void loadCurrentUsername() {
        db.collection("users").document(currentUserId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String currentName = document.getString("username");
                            if (currentName != null) {
                                editUserName.setText(currentName);
                            }
                        }
                    }
                });
    }

    // Method to update the field on the cloud database server
    private void updateUsernameInFirebase(String newName) {
        db.collection("users").document(currentUserId)
                .update("username", newName)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(edit.this, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(edit.this, profile.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(edit.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}