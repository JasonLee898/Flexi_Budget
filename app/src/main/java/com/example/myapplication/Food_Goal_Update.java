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

public class Food_Goal_Update extends AppCompatActivity {

    private TextView food_goal_update;
    private EditText food_goal_update_name, food_goal_update_amount;
    private Button food_goal_update_cancelbutton, food_goal_update_createbutton;

    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_goal_update);

        //All the element in the this layout
        food_goal_update = findViewById(R.id.food_goal_update);
        food_goal_update_name = findViewById(R.id.food_goal_update_name);
        food_goal_update_amount = findViewById(R.id.food_goal_update_amount);
        food_goal_update_cancelbutton = findViewById(R.id.food_goal_update_cancelbutton);
        food_goal_update_createbutton = findViewById(R.id.food_goal_update_createbutton);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        // cancel button click listener
        food_goal_update_cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // save button click listener
        food_goal_update_createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goal_Name = food_goal_update_name.getText().toString().trim();
                String goal_Amount = food_goal_update_amount.getText().toString().trim();
                if (!goal_Name.isEmpty()) {
                    try {
                        database_ref.child("food_Goal_Name").setValue(goal_Name);
                    } catch (NumberFormatException e) {
                        // handle invalid input
                        food_goal_update_name.setError("Invalid input");
                        return;
                    }
                }
                if (!goal_Amount.isEmpty()) {
                    try {
                        int new_Goal_Amount = Integer.parseInt(goal_Amount);
                        database_ref.child("food_Goal_Amount").setValue(goal_Amount);
                        finish();
                    } catch (NumberFormatException e) {
                        // handle invalid input
                        food_goal_update_amount.setError("Invalid input");
                        return;
                    }
                }
            }
        });
    }
}