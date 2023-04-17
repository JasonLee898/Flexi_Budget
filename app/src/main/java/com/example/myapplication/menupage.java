//menupagejava

package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class menupage extends AppCompatActivity
{

    private TextView username_menu;
    private Button summary_menu,envelope_menu,transaction_menu, logout_menu,personal_profile_menu;
    private ImageView envelope_icon_1,transaction_icon,personal_profile_icon,logout_icon,summary_icon;
    private ImageView personal_pic_menu;
    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_page);

        username_menu =findViewById(R.id.username_menu);
        summary_menu =findViewById(R.id.summary_menu);
        envelope_menu =findViewById(R.id.envelope_menu);
        transaction_menu =findViewById(R.id.transaction_menu);
        logout_menu = findViewById(R.id.logout_menu);
        personal_profile_menu = findViewById(R.id.personal_profile_menu);
        envelope_icon_1 = findViewById(R.id.envelope_icon_1);
        transaction_icon =findViewById(R.id.transaction_icon);
        personal_profile_icon =findViewById(R.id.personal_profile_icon);
        logout_icon =findViewById(R.id.logout_icon);
        summary_icon =findViewById(R.id.summary_icon);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        //Function to read and show the envelope_data of current user
        preread_data();

        summary_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(menupage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        envelope_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(menupage.this, Envelope_Management_Page.class);
                startActivity(intent);
                finish();
            }
        });

        transaction_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(menupage.this, Transaction.class);
                startActivity(intent);
                finish();
            }
        });

        personal_profile_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(menupage.this, Personal_Profile.class);
                startActivity(intent);
                finish();
            }
        });

        logout_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(menupage.this);

                builder.setTitle("Flexi Budget").setMessage("Are you sure you want to exit?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        firebase_auth.signOut();
                        Intent intent = new Intent(menupage.this, LoginPage.class);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", null).show();
            }
        });
    }

    private void preread_data()
    {
        database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check and retrieve user credentials
                if (dataSnapshot.child("name").exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    personal_profile_menu.setText(name);
                    username_menu.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Display error message if an error occurs
                Toast.makeText(menupage.this, "Error Occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}