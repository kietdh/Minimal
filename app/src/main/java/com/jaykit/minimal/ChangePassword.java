package com.jaykit.minimal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {

    FirebaseUser user;

    private EditText edtOldPwd;
    private EditText edtNewPwd;
    private EditText edtVerifyNewPwd;
    private ImageView btnChangePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_change_password);

        edtOldPwd = findViewById(R.id.edtOldPwd);
        edtNewPwd = findViewById(R.id.edtNewPwd);
        edtVerifyNewPwd = findViewById(R.id.edtVerifyPwd);
        btnChangePwd = findViewById(R.id.btnChangePwd);

        btnChangePwd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnChangePwd) {
            changePassword();
        }
    }

    private void changePassword() {

        user = FirebaseAuth.getInstance().getCurrentUser();

        String oldPwd = edtOldPwd.getText().toString().trim();
        String newPwd = edtNewPwd.getText().toString().trim();
        String verifyPwd = edtVerifyNewPwd.getText().toString().trim();

        if (oldPwd.isEmpty()) {
            edtOldPwd.setError("Your old password is required");
            edtOldPwd.requestFocus();
            return;
        }
        if (newPwd.isEmpty()) {
            edtNewPwd.setError("Your new password is required");
            edtNewPwd.requestFocus();
            return;
        }
        if (verifyPwd.isEmpty()) {
            edtVerifyNewPwd.setError("Your verify new password is required");
            edtVerifyNewPwd.requestFocus();
            return;
        }
        if (!newPwd.equals(verifyPwd)) {
            edtVerifyNewPwd.setError("Your password does not match");
            edtVerifyNewPwd.requestFocus();
        }

        if (user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), edtVerifyNewPwd.getText().toString());

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                Toast.makeText(ChangePassword.this, "Change password successfully", Toast.LENGTH_LONG).show();
                finish();
            });
            user.updatePassword(edtVerifyNewPwd.getText().toString()).addOnCompleteListener(task -> Log.d("ChangePassword: ", "Successfully change password"));
        } else {
            Toast.makeText(ChangePassword.this, "Failed to change password!", Toast.LENGTH_LONG).show();
            edtOldPwd.setText("");
            edtNewPwd.setText("");
            edtVerifyNewPwd.setText("");
        }

    }
}