package com.example.mobilproje;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private Button loginButton, registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();

        // Set up button listeners
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            signInUser(email, password);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // If a user is already logged in with Firebase, go to SpotifyActivity
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            goToSpotifyActivity();
        }
    }

    private void signInUser(String email, String password) {
        // Check for admin credentials first
        if (email.equals("admin") && password.equals("admin")) {
            Toast.makeText(getApplicationContext(), "Admin girişi başarılı!", Toast.LENGTH_LONG).show();
            goToSpotifyActivity();
        } else {
            // Proceed with Firebase authentication for non-admin users
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                // User is verified, allow login
                                Toast.makeText(getApplicationContext(),
                                        "Giriş başarılı!", Toast.LENGTH_LONG).show();
                                goToSpotifyActivity();
                            } else {
                                // Email not verified, sign out and notify
                                mAuth.signOut();
                                Toast.makeText(getApplicationContext(),
                                        "Lütfen e-posta adresinizi doğrulayın ve tekrar giriş yapın.",
                                        Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Giriş başarısız: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void goToSpotifyActivity() {
        Intent intent = new Intent(LoginActivity.this, SpotifyActivity.class);
        startActivity(intent);
        finish(); // Close LoginActivity
    }
}