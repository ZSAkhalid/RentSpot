package com.example.rentspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
    private EditText emailEditText;
    private EditText passwordEditText;
    private String email;
    private String password;
    private FirebaseAuth mAuth;
    private Intent registerIntent;
    private Intent nextPageIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        emailEditText = findViewById(R.id.email_ID);
        passwordEditText = findViewById(R.id.password_ID);
        mAuth = FirebaseAuth.getInstance();
        registerIntent = new Intent(this, registerPage.class);
        nextPageIntent = new Intent(this, home.class);
    }

    public void createAccountHandler(View view) {
        startActivity(registerIntent);
    }

    public void loginHandler(View view) {
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        if (isInputValid()) {
            signIn();
        }
    }

    private boolean isInputValid() {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Please enter email and password!");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Your email is invalid!");
            emailEditText.requestFocus();
            return false;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showToast("Please insert a proper password it should contain special character " +
                    "(@#$%^&+=) and at least a letters and a number.");
            passwordEditText.requestFocus();
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void signIn() {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showToast("Done successfully!");
                            startActivity(nextPageIntent);
                        } else {
                            showToast(task.getException().toString());
                        }
                    }
                });
    }
}
