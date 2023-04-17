package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseAppLifecycleListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginPage extends AppCompatActivity {

    private EditText login_email, login_password;
    private Button login_button;
    private TextView login_Registration, forgot_password_button;

    //Firebase
    private FirebaseAuth Auth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_button = findViewById(R.id.login_button);
        login_Registration = findViewById(R.id.login_Registration);
        forgot_password_button = findViewById(R.id.forgot_password_button);

        Auth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        // Check if user is already signed in
        FirebaseUser currentUser = Auth.getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, start the main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        login_Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, RegistrationPage.class);
                startActivity(intent);
            }
        });

        forgot_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginPage.this, ResetPasswordPage.class);
                startActivity(intent);
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = login_email.getText().toString();
                String Password = login_password.getText().toString();

                if(TextUtils.isEmpty(Email)){
                    login_email.setError("Required an email");
                }
                if(TextUtils.isEmpty(Password)){
                    login_password.setError("Required a Password");
                }
                else{
                    progressDialog.setMessage("Loading Progress...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    Auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // Remember change MainActivity to MainPage's Java Class Name !!!
                                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                                startActivity(intent);
                                Toast.makeText(LoginPage.this, "Successfully Login", Toast.LENGTH_SHORT).show();
                                finish();
                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(LoginPage.this,"Fail to Login", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}