//personal_profilejava

package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Personal_Profile extends AppCompatActivity {

    private TextView personal_profile_text, personal_profile_email,name_label,personal_profile_name,email_label
            ,  ph_label,personal_profile_phonenumber;
    private Button personal_profile_editbutton;
    private ImageButton hamburger;
    private ImageView personal_profil_pic;

    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    //@SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_profile);


        personal_profile_text =findViewById(R.id.personal_profile_text);
        personal_profil_pic =findViewById(R.id.personal_profil_pic);
        name_label = findViewById(R.id.name_label);
        personal_profile_name = findViewById(R.id.personal_profile_name);
        email_label = findViewById(R.id.email_label);
        personal_profile_email =findViewById(R.id.personal_profile_email);
        ph_label =findViewById(R.id.ph_label);
        personal_profile_phonenumber =findViewById(R.id.personal_profile_phonenumber);
        personal_profile_editbutton =findViewById(R.id.personal_profile_editbutton);
        hamburger=findViewById(R.id.hamburger);
        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        //Function to read and show the envelope_data of current user
        preread_data();

        personal_profile_editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Personal_Profile.this, Profile_Update.class);
                startActivity(intent);
                finish();

            }
        });
        hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(Personal_Profile.this, menupage.class);
                startActivity(intent);
                finish();

            }
        });
    }

    private void preread_data() {
        database_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Check and retrieve user credentials
                if (dataSnapshot.child("name").exists()) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    personal_profile_name.setText(name);
                }
                if (dataSnapshot.child("phone").exists()) {
                    String ph = dataSnapshot.child("phone").getValue().toString();
                    personal_profile_phonenumber.setText(ph);
                }
                if (dataSnapshot.child("email").exists()) {
                    String emaill = dataSnapshot.child("email").getValue().toString();
                    personal_profile_email.setText(emaill);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Display error message if an error occurs
                Toast.makeText(Personal_Profile.this, "Error Occurred: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}