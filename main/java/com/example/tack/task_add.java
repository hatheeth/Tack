package com.example.tack;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class task_add extends AppCompatActivity {

    private LinearLayout taskContainer;

    // 🔑 Declare Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        // 🔑 Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        } else {
            // Safety check: if user isn't logged in, send them back to login screen
            startActivity(new Intent(task_add.this, login.class));
            finish();
            return;
        }

        taskContainer = findViewById(R.id.taskContainer);
        ImageView addTaskIcon = findViewById(R.id.addTaskIcon);

        ImageView dev = findViewById(R.id.menuIcon);
        ImageView pro = findViewById(R.id.profileIcon);

        pro.setOnClickListener(v -> {
            Intent intent = new Intent(task_add.this, profile.class);
            startActivity(intent);
        });

        dev.setOnClickListener(v -> {
            Intent intent = new Intent(task_add.this, developer.class);
            startActivity(intent);
        });

        addTaskIcon.setOnClickListener(v -> showAddTaskDialog());

        // 🔑 Start listening to real-time updates from Firebase
        listenForTasks();
    }

    // 🔑 Save new task to Firestore
    private void saveTaskToFirebase(String taskText) {
        Map<String, Object> taskMap = new HashMap<>();
        taskMap.put("taskText", taskText);
        taskMap.put("userId", currentUserId);
        taskMap.put("timestamp", FieldValue.serverTimestamp()); // Used to order tasks chronologically

        db.collection("tasks")
                .add(taskMap)
                .addOnFailureListener(e ->
                        Toast.makeText(task_add.this, "Error saving task: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    // 🔑 Listen to Firestore changes in real-time
    private void listenForTasks() {
        db.collection("tasks")
                .whereEqualTo("userId", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                // 🔑 FIX: Tells Firestore how to handle temporary local null timestamps gracefully
                .addSnapshotListener(com.google.firebase.firestore.MetadataChanges.INCLUDE, (value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Error loading tasks: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        taskContainer.removeAllViews();

                        for (DocumentSnapshot doc : value.getDocuments()) {
                            String taskText = doc.getString("taskText");
                            String taskId = doc.getId();

                            if (taskText != null) {
                                renderTaskCard(taskId, taskText);
                            }
                        }
                    }
                });
    }

    private void showAddTaskDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText taskInput = dialogView.findViewById(R.id.taskInput);
        Button doneButton = dialogView.findViewById(R.id.doneButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        doneButton.setOnClickListener(v -> {
            String taskText = taskInput.getText().toString().trim();
            if (!taskText.isEmpty()) {
                // 🔑 Instead of injecting directly to UI, save to Firebase
                saveTaskToFirebase(taskText);
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Renamed from addTaskCard to make it clear this handles rendering from Firebase data
    private void renderTaskCard(String taskId, String taskText) {
        View taskView = LayoutInflater.from(this).inflate(R.layout.task_item, null);
        TextView taskTextView = taskView.findViewById(R.id.taskText);
        ImageView deleteIcon = taskView.findViewById(R.id.deleteIcon);
        ImageView editIcon = taskView.findViewById(R.id.editIcon);

        taskTextView.setText(taskText);

        // 🔑 Delete task from Firestore using its document ID
        deleteIcon.setOnClickListener(v ->
                db.collection("tasks").document(taskId).delete()
        );

        // 🔑 Edit task dialog trigger
        editIcon.setOnClickListener(v -> showEditTaskDialog(taskId, taskTextView));

        // Append to local view layout container
        taskContainer.addView(taskView);
    }

    private void showEditTaskDialog(String taskId, TextView taskTextView) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText taskInput = dialogView.findViewById(R.id.taskInput);
        Button doneButton = dialogView.findViewById(R.id.doneButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        taskInput.setText(taskTextView.getText().toString());

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        doneButton.setOnClickListener(v -> {
            String newText = taskInput.getText().toString().trim();
            if (!newText.isEmpty()) {
                // 🔑 Update the specific document entry inside Firestore
                db.collection("tasks").document(taskId)
                        .update("taskText", newText)
                        .addOnFailureListener(e ->
                                Toast.makeText(task_add.this, "Update failed", Toast.LENGTH_SHORT).show()
                        );
            }
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}