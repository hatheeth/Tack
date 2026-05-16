package com.example.tack;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog; // 🔑 Correct AlertDialog import
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class profile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize UI Elements from your XML layout
        ImageView home = findViewById(R.id.home);
        ImageView edit = findViewById(R.id.edit1);
        ImageView logoutButton = findViewById(R.id.Log); // 🔑 Target your "Log" ID here

        TextView usernameTextView = findViewById(R.id.profileUsername);
        TextView emailTextView = findViewById(R.id.profileEmail);

        // Navigation actions
        home.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, task_add.class);
            startActivity(intent);
        });

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, edit.class);
            startActivity(intent);
        });

        // 🔑 Trigger your custom layout dialog box when Log is touched
        logoutButton.setOnClickListener(v -> showLogoutDialog());

        // Fetch and display profile information
        if (mAuth.getCurrentUser() != null) {
            String currentUserId = mAuth.getCurrentUser().getUid();
            String email = mAuth.getCurrentUser().getEmail();

            emailTextView.setText(email);

            db.collection("users").document(currentUserId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");
                                usernameTextView.setText(username);
                            } else {
                                usernameTextView.setText("No Username Found");
                            }
                        } else {
                            Toast.makeText(profile.this, "Failed to load username", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            startActivity(new Intent(profile.this, login.class));
            finish();
        }
    }

    // 🔑 Dynamic Custom Layout Dialog Method
    private void showLogoutDialog() {

        View dialogView = LayoutInflater.from(profile.this).inflate(R.layout.dialog_logout, null);


        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnCancelLogout = dialogView.findViewById(R.id.btnCancelLogout);

        // 3. Build and containerize the dialog
        AlertDialog dialog = new AlertDialog.Builder(profile.this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        // 4. Action when "Yes" is clicked
        btnYes.setOnClickListener(v -> {
            mAuth.signOut(); // End active firebase session

            Intent intent = new Intent(profile.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Wipe navigation history
            startActivity(intent);

            dialog.dismiss();
            finish(); // Destroys profile view instance
        });

        // 5. Action when "Cancel" is clicked
        btnCancelLogout.setOnClickListener(v -> dialog.dismiss());

        // 6. Project it on the phone screen
        dialog.show();
    }
}