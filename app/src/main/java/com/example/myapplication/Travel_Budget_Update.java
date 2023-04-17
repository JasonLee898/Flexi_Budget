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

public class Travel_Budget_Update extends AppCompatActivity {

    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    private TextView travel_budget_update;
    private EditText travel_budget_update_amount;
    private Button travel_budget_update_cancelbutton, travel_budget_update_savebutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_budget_update);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        //All the element in the this layout
        travel_budget_update = findViewById(R.id.travel_budget_update);
        travel_budget_update_amount = findViewById(R.id.travel_budget_update_amount);
        travel_budget_update_cancelbutton = findViewById(R.id.travel_budget_update_cancelbutton);
        travel_budget_update_savebutton = findViewById(R.id.travel_budget_update_savebutton);

        // cancel button click listener
        travel_budget_update_cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // save button click listener
        travel_budget_update_savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newAmount = travel_budget_update_amount.getText().toString().trim();
                if (!newAmount.isEmpty()) {
                    int newBudgetAmount = Integer.parseInt(newAmount);
                    int oldBudgetAmount = getIntent().getIntExtra("travel_budget_amount", 0);
                    int oldRemainingAmount = getIntent().getIntExtra("travel_remaining_amount", 0);
                    int newRemainingAmount = oldRemainingAmount + (newBudgetAmount - oldBudgetAmount);

                    database_ref.child("travel_budget_amount").setValue(newBudgetAmount);
                    database_ref.child("travel_remaining_amount").setValue(newRemainingAmount);

                    finish();
                }
            }
        });
    }
}