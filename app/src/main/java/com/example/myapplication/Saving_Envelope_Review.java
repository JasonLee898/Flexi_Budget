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

public class Saving_Envelope_Review extends AppCompatActivity {

    private ImageButton saving_backbutton;
    private Button saving_budget_editbutton, saving_goal_editbutton;
    private TextView saving_budget_title, saving_budget_amount, saving_remaining_title, saving_remaining_amount, saving_Goal;
    private Context context;
    private ProgressBar saving_remaining_progress;

    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_envelope_review);

        //All the element in the this layout
        saving_backbutton = findViewById(R.id.saving_backbutton);
        saving_budget_editbutton = findViewById(R.id.saving_budget_editbutton);
        saving_goal_editbutton = findViewById(R.id.saving_goal_editbutton);
        saving_budget_title = findViewById(R.id.saving_budget_title);
        saving_budget_amount = findViewById(R.id.saving_budget_amount);
        saving_remaining_title = findViewById(R.id.saving_remaining_title);
        saving_remaining_amount = findViewById(R.id.saving_remaining_amount);
        saving_Goal = findViewById(R.id.saving_Goal);
        saving_remaining_progress = findViewById(R.id.saving_remaining_progress);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        //Function to read and show the envelope_data of current user
        preread_data();

        //Back button
        saving_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent envelope_menu = new Intent(Saving_Envelope_Review.this, Envelope_Management_Page.class);
                startActivity(envelope_menu);
            }
        });

        //Update Budget
        saving_budget_editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Saving_Envelope_Review.this, Saving_Budget_Update.class);
                intent.putExtra("saving_remaining_amount", saving_remaining_amount.getText().toString());
                intent.putExtra("saving_budget_amount", saving_budget_amount.getText().toString());
                startActivity(intent);
            }
        });

        //Update Goal
        saving_goal_editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Saving_Envelope_Review.this, Saving_Goal_Update.class);
                startActivity(intent);
            }
        });
    }

    private void preread_data() {
        database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check and retrieve saving_budget_amount
                if (dataSnapshot.child("saving_budget_amount").exists()) {
                    String budget_amount = dataSnapshot.child("saving_budget_amount").getValue().toString();
                    saving_budget_amount.setText(budget_amount);
                } else {
                    //If saving_budget_amount does not exist, create it and set to 0
                    database_ref.child("saving_budget_amount").setValue(0);
                    saving_budget_amount.setText("0");
                }

                //Check and retrieve saving_remaining_amount
                if (dataSnapshot.child("saving_remaining_amount").exists()) {
                    String remaining_amount = dataSnapshot.child("saving_remaining_amount").getValue().toString();
                    saving_remaining_amount.setText(remaining_amount);
                } else {
                    database_ref.child("saving_remaining_amount").setValue(0);
                    saving_remaining_amount.setText("0");
                }

                //Check and retrieve saving_Goal_Name
                if (dataSnapshot.child("saving_Goal_Name").exists()) {
                    String goal_name = dataSnapshot.child("saving_Goal_Name").getValue().toString();
                    saving_Goal.setText(goal_name);
                } else {
                    //If saving_Goal_Name does not exist, create it and set default name
                    database_ref.child("saving_Goal_Name").setValue("DefaultName");
                    saving_Goal.setText("DefaultName");
                }

                //Check and retrieve saving_Goal_Amount
                if (dataSnapshot.child("saving_Goal_Amount").exists()) {
                    String goal_amount = dataSnapshot.child("saving_Goal_Amount").getValue().toString();
                    String remaining_amount = dataSnapshot.child("saving_remaining_amount").getValue().toString();

                    double goal = Double.parseDouble(goal_amount);
                    double remaining = Double.parseDouble(remaining_amount);
                    double percentage = (remaining / goal) * 100;

                    saving_remaining_progress.setProgress((int) percentage);
                } else {
                    //If saving_Goal_Amount does not exist, create it and set default value
                    database_ref.child("saving_Goal_Amount").setValue(0);
                    saving_remaining_progress.setProgress(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Display error message if an error occurs
                Toast.makeText(Saving_Envelope_Review.this, "Error Occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}