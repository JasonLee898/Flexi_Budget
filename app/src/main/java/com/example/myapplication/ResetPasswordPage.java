package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordPage extends AppCompatActivity {

    private EditText resetEmail;
    private Button resetPasswordButton;
    private ProgressBar progressBar;
    private ImageButton registration_backbutton;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_page);

        resetEmail = findViewById(R.id.resetEmail);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        progressBar = findViewById(R.id.progressBar);
        registration_backbutton = findViewById(R.id.registration_backbutton);

        auth = FirebaseAuth.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

        registration_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPasswordPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void resetPassword(){
        String email = resetEmail.getText().toString().trim();

        if(email.isEmpty()){
            resetEmail.setError("Email is required!");
            resetEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            resetEmail.setError("Please provide a valid email!");
            resetEmail.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {

            if(task.isSuccessful()){
                Toast.makeText(ResetPasswordPage.this, "Please check your email inbox to reset your password!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ResetPasswordPage.this, LoginPage.class));
                finish();
            }else{
                Toast.makeText(ResetPasswordPage.this,"Invalid Email! Try again!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}