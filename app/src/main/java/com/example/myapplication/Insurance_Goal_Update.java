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

public class Insurance_Goal_Update extends AppCompatActivity {

    private TextView insurance_goal_update;
    private EditText insurance_goal_update_name, insurance_goal_update_amount;
    private Button insurance_goal_update_cancelbutton, insurance_goal_update_createbutton;

    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_goal_update);

        //All the element in the this layout
        insurance_goal_update = findViewById(R.id.insurance_goal_update);
        insurance_goal_update_name = findViewById(R.id.insurance_goal_update_name);
        insurance_goal_update_amount = findViewById(R.id.insurance_goal_update_amount);
        insurance_goal_update_cancelbutton = findViewById(R.id.insurance_goal_update_cancelbutton);
        insurance_goal_update_createbutton = findViewById(R.id.insurance_goal_update_createbutton);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        // cancel button click listener
        insurance_goal_update_cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // save button click listener
        insurance_goal_update_createbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String goal_Name = insurance_goal_update_name.getText().toString().trim();
                String goal_Amount = insurance_goal_update_amount.getText().toString().trim();
                if (!goal_Name.isEmpty()) {
                    try {
                        database_ref.child("insurance_Goal_Name").setValue(goal_Name);
                    } catch (NumberFormatException e) {
                        // handle invalid input
                        insurance_goal_update_name.setError("Invalid input");
                        return;
                    }
                }
                if (!goal_Amount.isEmpty()) {
                    try {
                        int new_Goal_Amount = Integer.parseInt(goal_Amount);
                        database_ref.child("insurance_Goal_Amount").setValue(goal_Amount);
                        finish();
                    } catch (NumberFormatException e) {
                        // handle invalid input
                        insurance_goal_update_amount.setError("Invalid input");
                        return;
                    }
                }
            }
        });
    }
}