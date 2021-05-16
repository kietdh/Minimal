package com.jaykit.minimal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.jaykit.minimal.model.User;

import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * SignUpActivity class
     * This class process events of SignUp
     * @author: Kiet Duong Hung
     * @writeDate: Arp 29, 2020
     * @version: v1.0.0
     *
     * */

    //Declare Firebase Auth
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;

    EditText edtName;
    EditText edtEmail;
    EditText edtPassword;
    EditText edtRePassword;
    ImageView btnSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        //Initialization Firebase Auth.
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //SubFunction.
        onView();
    }

    private void onView() {
        //Declare Palette.
        edtName = findViewById(R.id.edtSignUpName);
        edtEmail = findViewById(R.id.edtSignUpEmail);
        edtPassword = findViewById(R.id.edtSignUpPassword);
        edtRePassword = findViewById(R.id.edtConfirmSignUpPassword);
        btnSignUp = findViewById(R.id.imageViewSignUpButton);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageViewSignUpButton) {
            if (isValidSignupInfo()) {
                signUp();
            }
        }
    }

    private void signUp() {
        String usr = edtEmail.getText().toString();
        String pwd = edtRePassword.getText().toString();
        String name = edtName.getText().toString();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mAuth.createUserWithEmailAndPassword(usr, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if ( task.isSuccessful() ) {
                    User user = new User(name, usr);
                    FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void> () {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if ( task.isSuccessful() ) {
                                        Toast.makeText(SignUpActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(SignUpActivity.this, "Failed to sign up. Try again!", Toast.LENGTH_LONG).show();
                                        edtPassword.setText("");
                                        edtRePassword.setText("");
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Failed to sign up. Try again!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }



    private boolean isValidSignupInfo() {
        //Check empty fields.
        if (edtName.getText().toString().isEmpty()) {
            edtName.setError("Name is required");
            edtName.requestFocus();
        }
        if (edtEmail.getText().toString().isEmpty()) {
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
        }
        if (edtPassword.getText().toString().isEmpty()) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
        }
        if (edtRePassword.getText().toString().isEmpty()) {
            edtRePassword.setError("Confirm password is required");
            edtRePassword.requestFocus();
        }
        //Check valid contents.
        if (!Patterns.EMAIL_ADDRESS.matcher(edtEmail.getText()).matches()) {
            edtEmail.setError("Please provide valid email.");
            edtEmail.requestFocus();
        }
        if (!edtRePassword.getText().toString().equals(edtPassword.getText().toString())) {
            edtRePassword.setError("Your confirm password does not match.");
            edtRePassword.requestFocus();
        }
        return true;
        }
}