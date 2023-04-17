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

public class Food_Envelope_Review extends AppCompatActivity {

    private ImageButton food_backbutton;
    private Button food_budget_editbutton, food_goal_editbutton;
    private TextView food_budget_title, food_budget_amount, food_remaining_title, food_remaining_amount, food_Goal;
    private Context context;
    private ProgressBar food_remaining_progress;

    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_envelope_review);

        //All the element in the this layout
        food_backbutton = findViewById(R.id.food_backbutton);
        food_budget_editbutton = findViewById(R.id.food_budget_editbutton);
        food_goal_editbutton = findViewById(R.id.food_goal_editbutton);
        food_budget_title = findViewById(R.id.food_budget_title);
        food_budget_amount = findViewById(R.id.food_budget_amount);
        food_remaining_title = findViewById(R.id.food_remaining_title);
        food_remaining_amount = findViewById(R.id.food_remaining_amount);
        food_Goal = findViewById(R.id.food_Goal);
        food_remaining_progress = findViewById(R.id.food_remaining_progress);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        //Function to read and show the envelope_data of current user
        preread_data();

        //Back button
        food_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent envelope_menu = new Intent(Food_Envelope_Review.this, Envelope_Management_Page.class);
                startActivity(envelope_menu);
            }
        });

        //Update Budget
        food_budget_editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Food_Envelope_Review.this, Food_Budget_Update.class);
                intent.putExtra("food_remaining_amount", food_remaining_amount.getText().toString());
                intent.putExtra("food_budget_amount", food_budget_amount.getText().toString());
                startActivity(intent);
            }
        });

        //Update Goal
        food_goal_editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Food_Envelope_Review.this, Food_Goal_Update.class);
                startActivity(intent);
            }
        });
    }

    private void preread_data() {
        database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check and retrieve food_budget_amount
                if (dataSnapshot.child("food_budget_amount").exists()) {
                    String budget_amount = dataSnapshot.child("food_budget_amount").getValue().toString();
                    food_budget_amount.setText(budget_amount);
                } else {
                    //If food_budget_amount does not exist, create it and set to 0
                    database_ref.child("food_budget_amount").setValue(0);
                    food_budget_amount.setText("0");
                }

                //Check and retrieve food_remaining_amount
                if (dataSnapshot.child("food_remaining_amount").exists()) {
                    String remaining_amount = dataSnapshot.child("food_remaining_amount").getValue().toString();
                    food_remaining_amount.setText(remaining_amount);
                } else {
                    database_ref.child("food_remaining_amount").setValue(0);
                    food_remaining_amount.setText("0");
                }

                //Check and retrieve food_Goal_Name
                if (dataSnapshot.child("food_Goal_Name").exists()) {
                    String goal_name = dataSnapshot.child("food_Goal_Name").getValue().toString();
                    food_Goal.setText(goal_name);
                } else {
                    //If food_Goal_Name does not exist, create it and set default name
                    database_ref.child("food_Goal_Name").setValue("DefaultName");
                    food_Goal.setText("DefaultName");
                }

                //Check and retrieve food_Goal_Amount
                if (dataSnapshot.child("food_Goal_Amount").exists()) {
                    String goal_amount = dataSnapshot.child("food_Goal_Amount").getValue().toString();
                    String remaining_amount = dataSnapshot.child("food_remaining_amount").getValue().toString();

                    double goal = Double.parseDouble(goal_amount);
                    double remaining = Double.parseDouble(remaining_amount);
                    double percentage = (remaining / goal) * 100;

                    food_remaining_progress.setProgress((int) percentage);
                } else {
                    //If food_Goal_Amount does not exist, create it and set default value
                    database_ref.child("food_Goal_Amount").setValue(0);
                    food_remaining_progress.setProgress(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Display error message if an error occurs
                Toast.makeText(Food_Envelope_Review.this, "Error Occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}