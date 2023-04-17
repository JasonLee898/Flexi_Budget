//personal_profile_updatejava

package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Profile_Update extends AppCompatActivity {

    private TextView profile_header_update, Name_label_update,ph_label_update;
    private Button cancelButton_update,Save_update;
    private ImageView profile_pic_update;
    private EditText name_update,email_update,phonenumber_update;
    private FirebaseAuth firebase_auth;
    private DatabaseReference database_ref;
    private String onlineUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        profile_pic_update =findViewById(R.id.profile_pic_update);
        profile_header_update =findViewById(R.id.profile_header_update);
        Name_label_update =findViewById(R.id.Name_label_update);
        name_update =findViewById(R.id.name_update);

        ph_label_update =findViewById(R.id.ph_label_update);
        phonenumber_update =findViewById(R.id.phonenumber_update);
        cancelButton_update =findViewById(R.id.cancelButton_update);
        Save_update =findViewById(R.id.Save_update);

        //To find the data for specific user
        firebase_auth = FirebaseAuth.getInstance();
        onlineUserID = firebase_auth.getCurrentUser().getUid();
        database_ref = FirebaseDatabase.getInstance().getReference().child("users").child(onlineUserID);

        cancelButton_update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Update.this, Personal_Profile.class);
                startActivity(intent);
                finish();
            }
        });

        // save button click listener
        Save_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String update_name = name_update.getText().toString().trim();

                String update_ph = phonenumber_update.getText().toString().trim();
                if (!update_name.isEmpty()) {
                    try {
                        database_ref.child("name").setValue(update_name);
                    } catch (NumberFormatException e)
                    {
                        // handle invalid input
                        name_update.setError("Invalid input");
                        return;
                    }
                }
                if (!update_ph.isEmpty()) {
                    try {

                        database_ref.child("phone").setValue(update_ph);

                    } catch (NumberFormatException e) {
                        // handle invalid input
                        phonenumber_update.setError("Invalid input");
                        return;
                    }
                }
                Intent intent = new Intent(Profile_Update.this, Personal_Profile.class);
                startActivity(intent);
                finish();
            }
        });
    }
}