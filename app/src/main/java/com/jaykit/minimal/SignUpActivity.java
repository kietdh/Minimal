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

    //Declare Palette.
    private EditText    name;
    private EditText    email;
    private EditText    password;
    private EditText    re_password;
    private ImageView   buttonSignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        //Initialization Firebase Auth.
        mAuth = FirebaseAuth.getInstance();

        //SubFunction.
        onView();

    }

    private void onView() {
        name = findViewById(R.id.edtSignUpName);
        email = findViewById(R.id.edtSignUpEmail);
        password = findViewById(R.id.edtSignUpPassword);
        re_password = findViewById(R.id.edtConfirmSignUpPassword);
        buttonSignUp = findViewById(R.id.imageViewSignUpButton);

        buttonSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if ( view.getId() == R.id.imageViewSignUpButton ) {
            signUp();
        }
    }

    private void signUp() {
        String uname = name.getText().toString().trim();
        String uemail = email.getText().toString().trim();
        String upassword = password.getText().toString().trim();
        String urepassword = re_password.getText().toString().trim();

        //Check empty fields.
        if ( uname.isEmpty() ) {
            name.setError("Name is required");
            name.requestFocus();
            return;
        }

        if ( uemail.isEmpty() ) {
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if ( upassword.isEmpty() ) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if ( urepassword.isEmpty() ) {
            re_password.setError("Confirm password is required");
            re_password.requestFocus();
            return;
        }

        //Check valid contents.
        if( !Patterns.EMAIL_ADDRESS.matcher(uemail).matches() ) {
            email.setError("Please provide valid email.");
            email.requestFocus();
            return;
        }

        if ( !upassword.equals(urepassword) ) {
            re_password.setError("Your confirm password does not match.");
            re_password.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(uemail, urepassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ) {
                            User user = new User( uname, uemail, urepassword);

                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void> () {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if ( task.isSuccessful() ) {
                                        Toast.makeText(SignUpActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(SignUpActivity.this, "Failed to sign up. Try again!", Toast.LENGTH_LONG).show();
                                        password.setText("");
                                        re_password.setText("");
                                        return;
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(SignUpActivity.this, "Failed to sign up. Try again!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
    }
}