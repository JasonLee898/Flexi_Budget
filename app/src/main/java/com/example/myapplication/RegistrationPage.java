package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegistrationPage extends AppCompatActivity {

    private ImageButton registration_backbutton;
    private EditText registration_name, registration_email, registration_password,
            registration_confirm_password, registration_phone_number, enterOTPField;
    private Button Register_button, sendOtpButton, verifyOtpButton, resendOtpButton;
    private TextView registration_login_button;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mUsersRef;

    //OTP
    String userPhoneNumber, verificationID;
    //Firebase OTP
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_page);

        registration_backbutton = findViewById(R.id.registration_backbutton);
        registration_name = findViewById(R.id.registration_name);
        registration_email = findViewById(R.id.registration_email);
        registration_password = findViewById(R.id.registration_password);
        registration_confirm_password = findViewById(R.id.registration_confirm_password);
        registration_phone_number = findViewById(R.id.registration_phone_number);
        Register_button = findViewById(R.id.Register_button);
        registration_login_button = findViewById(R.id.registration_login_button);

        sendOtpButton = findViewById(R.id.sendOtpButton);
        enterOTPField = findViewById(R.id.enterOTPField);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);
        resendOtpButton = findViewById(R.id.resendOtpButton);
        resendOtpButton.setEnabled(false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mUsersRef = mDatabase.getReference("users");

        //Register button gone first. If user had verify the phone number, this Register Button will appear.
        Register_button.setVisibility(View.GONE);
        verifyOtpButton.setVisibility(View.GONE);
        resendOtpButton.setVisibility(View.GONE);

        registration_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        registration_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationPage.this, LoginPage.class);
                startActivity(intent);
                finish();
            }
        });

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(registration_phone_number.getText().toString().isEmpty()){
                    registration_phone_number.setError("Phone Number required!!!");
                    return;
                }
                userPhoneNumber = registration_phone_number.getText().toString();
                verifyPhoneNumber(userPhoneNumber);
                Toast.makeText(RegistrationPage.this,userPhoneNumber,Toast.LENGTH_SHORT).show();
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the otp
                if(enterOTPField.getText().toString().isEmpty()){
                    enterOTPField.setError("Enter OTP first");
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,enterOTPField.getText().toString());
                authenticateUser(credential);
            }
        });

        resendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyPhoneNumber(userPhoneNumber);
                resendOtpButton.setEnabled(false);
            }
        });

        Register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stringName = registration_name.getText().toString().trim();
                String stringEmail = registration_email.getText().toString().trim();
                String stringPassword = registration_password.getText().toString().trim();
                String stringConfirmPassword = registration_confirm_password.getText().toString().trim();
                String stringPhone = registration_phone_number.getText().toString().trim();

                if(TextUtils.isEmpty(stringName)){
                    registration_name.setError("Name required!!!");
                    registration_name.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(stringEmail)){
                    registration_email.setError("Valid Email required!!!");
                    registration_email.requestFocus();
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(stringEmail).matches()) {
                    registration_email.setError("Invalid email format");
                    registration_email.requestFocus();
                    return;
                }
                if(TextUtils.isEmpty(stringPhone) || registration_phone_number.length() < 9  ) {
                    registration_phone_number.setError(" Valid Phone Number required!!!");
                    registration_phone_number.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(stringPassword)){
                    registration_password.setError("Password required!!!");
                    registration_password.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(stringConfirmPassword)){
                    registration_confirm_password.setError("Password required!!!");
                    registration_confirm_password.requestFocus();
                    return;
                }

                if(!registration_confirm_password.getText().toString().equals(registration_password.getText().toString())){
                    registration_confirm_password.setError("Password must be same!!!");
                    registration_confirm_password.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(stringPhone)){
                    registration_phone_number.setError("Phone Number required!!!");
                    registration_phone_number.requestFocus();
                    return;
                }

                if(registration_phone_number.length() < 9 ){
                    registration_phone_number.setError("Invalid range of phone number!!!");
                    registration_phone_number.requestFocus();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(stringEmail, stringPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("name", stringName);
                            userData.put("email", user.getEmail());
                            userData.put("password", stringPassword);
                            userData.put("phone", stringPhone);
                            mUsersRef.child(user.getUid()).setValue(userData);
                            Toast.makeText(RegistrationPage.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationPage.this, LoginPage.class));
                            finish();
                        } else {
                            Toast.makeText(RegistrationPage.this, "Registration failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(RegistrationPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationID = s;
                token = forceResendingToken;

                verifyOtpButton.setVisibility(View.VISIBLE);
                resendOtpButton.setVisibility(View.VISIBLE);
                resendOtpButton.setEnabled(false);

                sendOtpButton.setVisibility(View.GONE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resendOtpButton.setEnabled(true);
            }
        };
    }

    public void verifyPhoneNumber(String phoneNum){
        //send OTP
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
            .setActivity(this)
            .setPhoneNumber(phoneNum)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void authenticateUser(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(RegistrationPage.this, "OTP Success", Toast.LENGTH_SHORT).show();
                verifyOtpButton.setVisibility(View.GONE);
                resendOtpButton.setVisibility(View.GONE);
                Register_button.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegistrationPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}