package com.example.rentspot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class registerPage extends AppCompatActivity {
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z]+" +
            "(?:-[a-zA-Z-]+" +
            ")*$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile(("^" +
            "[a-zA-Z]" +
            "[a-zA-Z0-9_]*" +
            "$"));

    private EditText fNameEditText;
    private EditText lNameEditText;
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText phoneNumberEditText;
    private EditText addressEditText;
    private CheckBox agreementCheckBox;
    private FirebaseAuth firebaseAuth;
    private Intent loginIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        fNameEditText = findViewById(R.id.f_nameID);
        lNameEditText = findViewById(R.id.l_nameID);
        usernameEditText = findViewById(R.id.usernameID);
        emailEditText = findViewById(R.id.e_mailID);
        passwordEditText = findViewById(R.id.passwordID);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordID);
        phoneNumberEditText = findViewById(R.id.phoneNumberID);
        agreementCheckBox = findViewById(R.id.agreementID);
        addressEditText = findViewById(R.id.addressID);

        loginIntent = new Intent(this, MainActivity.class);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void goBackButton(View view) {
        startActivity(loginIntent);
    }

    public void nextHandler(View view) {
        String firstName = fNameEditText.getText().toString().trim();
        String lastName = lNameEditText.getText().toString().trim();
        String username = usernameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String phoneNumber = phoneNumberEditText.getText().toString().trim();

        if (!validateName(firstName, "first")) {
            fNameEditText.requestFocus();
            return;
        }

        if (!validateName(lastName, "last")) {
            lNameEditText.requestFocus();
            return;
        }

        if (!validateUsername(username)) {
            usernameEditText.requestFocus();
            return;
        }

        if (!validateAddress(address)) {
            addressEditText.requestFocus();
            return;
        }

        if (!validateEmail(email)) {
            emailEditText.requestFocus();
            return;
        }

        if (!validatePassword(password)) {
            passwordEditText.requestFocus();
            return;
        }

        if (!validateConfirmPassword(password, confirmPassword)) {
            confirmPasswordEditText.requestFocus();
            return;
        }

        if (!validatePhoneNumber(phoneNumber)) {
            phoneNumberEditText.requestFocus();
            return;
        } else if (!agreementCheckBox.isChecked()) {
            agreementCheckBox.requestFocus();
            Toast.makeText(this, "You should check Agree to our terms and services.", Toast.LENGTH_SHORT).show();
            return;
        }

        registerUser();
    }

    private boolean validateName(String name, String type) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter your " + type + " name.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            Toast.makeText(this, "Please enter a valid " + type + " name.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateUsername(String username) {
        if (username.isEmpty()) {
            Toast.makeText(this, "Please enter a username.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            Toast.makeText(this, "Username should start with a letter and only contain letters, numbers, and underscores.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateAddress(String address) {
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter an address.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (address.length() > 30) {
            Toast.makeText(this, "Address should be less than 30 letters.", Toast.LENGTH_SHORT).show();
        } else {
            return true;
        }
        return false;
    }

    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter an email.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(this, "Password must be at least 8 characters and contain at least one letter, one number, and one special character.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validateConfirmPassword(String password, String confirmPassword) {
        if (confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm your password.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!confirmPassword.equals(password)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void registerUser() {
        String firstName, lastName, username, address, email, password, phoneNumber, property1, property2, property3;
        firstName = fNameEditText.getText().toString();
        lastName = lNameEditText.getText().toString();
        username = usernameEditText.getText().toString();
        address = addressEditText.getText().toString();
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();
        phoneNumber = phoneNumberEditText.getText().toString();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(firstName, lastName, username, address, email,
                                    password, phoneNumber);
                            String userId = firebaseAuth.getCurrentUser().getUid();
                            FirebaseDatabase.getInstance().getReference("Users").child(userId).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                                startActivity(loginIntent);
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Registration failed: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Registration failed: " + task.getException().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void termsAndConditionsHandler(View view) {
    }
}