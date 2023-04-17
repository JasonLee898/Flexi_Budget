package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Insurance_Envelope_Review extends AppCompatActivity {

    private ImageButton insurance_backbutton;
    private Button insurance_budget_editbutton, insurance_goal_editbutton;
    private TextView insurance_budget_title, insurance_budget_amount, insurance_remaining_title, insurance_remaining_amount, insurance_Goal;
    private Context context;
    private ProgressBar insurance_remaining_progress;

    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance_envelope_review);

        //All the element in the this layout
        insurance_backbutton = findViewById(R.id.insurance_backbutton);
        insurance_budget_editbutton = findViewById(R.id.insurance_budget_editbutton);
        insurance_goal_editbutton = findViewById(R.id.insurance_goal_editbutton);
        insurance_budget_title = findViewById(R.id.insurance_budget_title);
        insurance_budget_amount = findViewById(R.id.insurance_budget_amount);
        insurance_remaining_title = findViewById(R.id.insurance_remaining_title);
        insurance_remaining_amount = findViewById(R.id.insurance_remaining_amount);
        insurance_Goal = findViewById(R.id.insurance_Goal);
        insurance_remaining_progress = findViewById(R.id.insurance_remaining_progress);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        //Function to read and show the envelope_data of current user
        preread_data();

        //Back button
        insurance_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent envelope_menu = new Intent(Insurance_Envelope_Review.this, Envelope_Management_Page.class);
                startActivity(envelope_menu);
            }
        });

        //Update Budget
        insurance_budget_editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Insurance_Envelope_Review.this, Insurance_Budget_Update.class);
                intent.putExtra("insurance_remaining_amount", insurance_remaining_amount.getText().toString());
                intent.putExtra("insurance_budget_amount", insurance_budget_amount.getText().toString());
                startActivity(intent);
            }
        });

        //Update Goal
        insurance_goal_editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Insurance_Envelope_Review.this, Insurance_Goal_Update.class);
                startActivity(intent);
            }
        });
    }

    private void preread_data() {
        database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check and retrieve insurance_budget_amount
                if (dataSnapshot.child("insurance_budget_amount").exists()) {
                    String budget_amount = dataSnapshot.child("insurance_budget_amount").getValue().toString();
                    insurance_budget_amount.setText(budget_amount);
                } else {
                    //If insurance_budget_amount does not exist, create it and set to 0
                    database_ref.child("insurance_budget_amount").setValue(0);
                    insurance_budget_amount.setText("0");
                }

                //Check and retrieve insurance_remaining_amount
                if (dataSnapshot.child("insurance_remaining_amount").exists()) {
                    String remaining_amount = dataSnapshot.child("insurance_remaining_amount").getValue().toString();
                    insurance_remaining_amount.setText(remaining_amount);
                } else {
                    database_ref.child("insurance_remaining_amount").setValue(0);
                    insurance_remaining_amount.setText("0");
                }

                //Check and retrieve insurance_Goal_Name
                if (dataSnapshot.child("insurance_Goal_Name").exists()) {
                    String goal_name = dataSnapshot.child("insurance_Goal_Name").getValue().toString();
                    insurance_Goal.setText(goal_name);
                } else {
                    //If insurance_Goal_Name does not exist, create it and set default name
                    database_ref.child("insurance_Goal_Name").setValue("DefaultName");
                    insurance_Goal.setText("DefaultName");
                }

                //Check and retrieve insurance_Goal_Amount
                if (dataSnapshot.child("insurance_Goal_Amount").exists()) {
                    String goal_amount = dataSnapshot.child("insurance_Goal_Amount").getValue().toString();
                    String remaining_amount = dataSnapshot.child("insurance_remaining_amount").getValue().toString();

                    double goal = Double.parseDouble(goal_amount);
                    double remaining = Double.parseDouble(remaining_amount);
                    double percentage = (remaining / goal) * 100;

                    insurance_remaining_progress.setProgress((int) percentage);
                } else {
                    //If insurance_Goal_Amount does not exist, create it and set default value
                    database_ref.child("insurance_Goal_Amount").setValue(0);
                    insurance_remaining_progress.setProgress(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Display error message if an error occurs
                Toast.makeText(Insurance_Envelope_Review.this, "Error Occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}