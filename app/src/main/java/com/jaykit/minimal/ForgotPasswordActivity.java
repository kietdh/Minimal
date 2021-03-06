package com.jaykit.minimal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * ForgotPasswordActivity class
     * This class process events of Forgot Password
     * @author: Kiet Duong Hung
     * @writeDate: Arp 29, 2020
     * @version: v1.0.0
     *
     * */

    //Declare Firebase Auth.
    private FirebaseAuth mAuth;

    EditText edtEmail;
    ImageView btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_password);

        //Initialization Firebase Auth.
        mAuth = FirebaseAuth.getInstance();

        edtEmail = findViewById(R.id.edtForgotEmail);
        btnForgotPassword = findViewById(R.id.imageViewForgetPassword);

        btnForgotPassword.setOnClickListener(this);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        String email = edtEmail.getText().toString().trim();

        if ( email.isEmpty() ) {
            edtEmail.setError("Email is required.");
            edtEmail.requestFocus();
            return;
        }

        if( !Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
            edtEmail.setError("Please provide valid email.");
            edtEmail.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(edtEmail.getText().toString()).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()) {
                Toast.makeText(ForgotPasswordActivity.this, "Reset email has been sent to your email!", Toast.LENGTH_LONG).show();
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }
            else {
                Toast.makeText(ForgotPasswordActivity.this, "Failed to reset password!", Toast.LENGTH_LONG).show();
            }
        });
    }
}