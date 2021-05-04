package com.jaykit.minimal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * LoginActivity class
     * This class process events of Login
     * @author: Kiet Duong Hung
     * @writeDate: Arp 29, 2020
     * @version: v1.0.0
     *
     * */

    //Declare Initialization Firebase Variables.
    private FirebaseAuth mAuth;

    //Declare SharedPreferences.
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    //Declare Palette.
    private TextView    forgotPasswordButton;
    private TextView    signUpButton;
    private EditText    emailField;
    private EditText    passwordField;
    private ImageView   loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialization SharedPreferences.
        sharedPref = getSharedPreferences("Login", Context.MODE_PRIVATE);
        if ( sharedPref.getBoolean("Login", false) ) {
            startActivity ( new Intent(this, MainActivity.class) );
            finish();
        }

        //Initialization Firebase Auth.
        mAuth = FirebaseAuth.getInstance();

        //SubFunction
        onView();

    }

    private void onView() {
        signUpButton = findViewById(R.id.textViewSignUp);
        forgotPasswordButton = findViewById(R.id.textViewForgotPassword);
        emailField = findViewById(R.id.edtLoginEmail);
        passwordField = findViewById(R.id.edtLoginPassword);
        loginButton = findViewById(R.id.imageViewLogin);

        signUpButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch ( view.getId() ) {
            case R.id.textViewSignUp:
                openSignUpActivity();
                break;
            case R.id.textViewForgotPassword:
                openForgotPasswordActivity();
                break;
            case R.id.imageViewLogin:
                openMainActivity();
                break;
        }
    }

    private void openSignUpActivity() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void openForgotPasswordActivity() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    private void openMainActivity() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if ( email.isEmpty() ) {
            emailField.setError("Email is required");
            emailField.requestFocus();
            return;
        }
        if ( password.isEmpty() ) {
            passwordField.setError("Password is required");
            passwordField.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( task.isSuccessful() ) {
                            startActivity( new Intent(LoginActivity.this, MainActivity.class) );
                            sharedPref.edit().putBoolean("Login", true).apply();
                            finish();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Failed to login, please check login information!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
    }
}