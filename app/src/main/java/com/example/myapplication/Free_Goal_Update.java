package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Free_Goal_Update extends AppCompatActivity {

    private TextView free_goal_update;
    private EditText free_goal_update_name, free_goal_update_amount;
    private Button free_goal_update_cancelbutton, free_goal_update_createbutton;

    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_goal_update);

        //All the element in the this layout
        free_goal_update = findViewById(R.id.free_goal_update);
        free_goal_update_name = findViewById(R.id.free_goal_update_name);
        free_goal_update_amount = findViewById(R.id.free_goal_update_amount);
        free_goal_update_cancelbutton = findViewById(R.id.free_goal_update_cancelbutton);
        free_goal_update_createbutton = findViewById(R.id.free_goal_update_createbutton);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        // cancel button click listener
        free_goal_update_cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // save button click listener
        free_goal_update_createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goal_Name = free_goal_update_name.getText().toString().trim();
                String goal_Amount = free_goal_update_amount.getText().toString().trim();
                if (!goal_Name.isEmpty()) {
                    try {
                        database_ref.child("free_Goal_Name").setValue(goal_Name);
                    } catch (NumberFormatException e) {
                        // handle invalid input
                        free_goal_update_name.setError("Invalid input");
                        return;
                    }
                }
                if (!goal_Amount.isEmpty()) {
                    try {
                        int new_Goal_Amount = Integer.parseInt(goal_Amount);
                        database_ref.child("free_Goal_Amount").setValue(goal_Amount);
                        finish();
                    } catch (NumberFormatException e) {
                        // handle invalid input
                        free_goal_update_amount.setError("Invalid input");
                        return;
                    }
                }
            }
        });
    }
}