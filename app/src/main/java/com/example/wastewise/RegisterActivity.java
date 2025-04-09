package com.example.wastewise;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.GoogleAuthProvider;



public class RegisterActivity extends AppCompatActivity {
    private EditText email, password, confirmPassword, username;
    private Button createPlayer, signupBtn;
    private LinearLayout signinWithGoogle;
    private RelativeLayout usernameModal;
    private FirebaseAuth mAuth;
    private TextView loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.email_et);
        password = findViewById(R.id.password_et);
        confirmPassword = findViewById(R.id.confirmPassword_et);
        signupBtn = findViewById(R.id.signup_btn);
        createPlayer = findViewById(R.id.createPlayer_btn);
        signinWithGoogle = findViewById(R.id.signin_with_google);
        usernameModal = findViewById(R.id.username_modal);
        username = findViewById(R.id.username_et);
        loginLink = findViewById(R.id.login_tv);

        loginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        signupBtn.setOnClickListener(v -> handleSignup());


        signinWithGoogle.setOnClickListener(v -> {
            Toast.makeText(this, "Google Sign Up coming soon!", Toast.LENGTH_SHORT).show();
        });

        createPlayer.setOnClickListener(v -> registerUser());
    }

    private void handleSignup(){
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();
        String confirmPasswordStr = confirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(emailStr)) {
            email.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(passwordStr)) {
            password.setError("Password is required.");
            return;
        }

        if (TextUtils.isEmpty(confirmPasswordStr)) {
            confirmPassword.setError("Confirm Password is required.");
            return;
        }

        if (!passwordStr.equals(confirmPasswordStr)) {
            confirmPassword.setError("Passwords do not match.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration successful
                            Log.d("RegisterActivity", "createUserWithEmail:success");
                            createPlayer(); // Show username modal
                        } else {
                            // Registration failed
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, "Email already exists.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    private void createPlayer() {
        usernameModal.setVisibility(View.VISIBLE);
    }

    private void registerUser() {
        String usernameStr = username.getText().toString().trim();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && !TextUtils.isEmpty(usernameStr)) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();


            db.collection("users")
                    .whereEqualTo("username", usernameStr)
                    .get()
                    .addOnCompleteListener(task-> {
                        if (task.isSuccessful()){
                             if (task.getResult() != null && !task.getResult().isEmpty()){
                                 Toast.makeText(this, "Username already taken. Choose another.", Toast.LENGTH_SHORT).show();
                             } else{
                                 Map<String, Object> userMap = new HashMap<>();
                                 userMap.put("username", usernameStr);
                                 userMap.put("email", user.getEmail());
                                 userMap.put("points", 0);

                                 db.collection("users").document(uid).set(userMap)
                                         .addOnSuccessListener(aVoid -> {
                                             Toast.makeText(this, "User successfully registered!", Toast.LENGTH_SHORT).show();

                                             Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                             startActivity(intent);
                                             finish();
                                         })
                                         .addOnFailureListener(e -> {
                                             Toast.makeText(this, "Error saving user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                             Log.e("RegisterActivity", "Firestore write failed", e);
                                         });
                             }
                        } else {
                            Toast.makeText(this, "Error checking username: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
        }
    }
}