package com.jaykit.minimal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

    //Declare Palette.
    private EditText    email;
    private ImageView   buttonForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //Initialization Firebase Auth.
        mAuth = FirebaseAuth.getInstance();

        //SubFunction.
        onView();
    }

    private void onView() {
        email = findViewById(R.id.edtForgotEmail);
        buttonForgotPassword = findViewById(R.id.imageViewForgetPassword);
    }

    @Override
    public void onClick(View view) {
        String uemail = email.getText().toString().trim();

        if ( uemail.isEmpty() ) {
            email.setText("Email is required.");
            email.requestFocus();
            return;
        }

        if( !Patterns.EMAIL_ADDRESS.matcher(uemail).matches() ) {
            email.setError("Please provide valid email.");
            email.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(uemail).addOnCompleteListener(this, new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset email has been sent to your email!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                }
                else {
                    Toast.makeText(ForgotPasswordActivity.this, "Failed to reset password!", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
    }
}