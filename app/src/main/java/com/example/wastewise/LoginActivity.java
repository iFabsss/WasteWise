package com.example.wastewise;

import android.content.Intent;
import android.credentials.GetCredentialRequest;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;

public class LoginActivity extends AppCompatActivity {
    private TextView createAcc, forgotPw;
    private EditText email, password;
    private FirebaseAuth mAuth;
    private Button loginBtn;
    private LinearLayout signinWithGoogle;

    private GoogleAuthCredential googleAuthCredential;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        createAcc = findViewById(R.id.create_acc);
        forgotPw = findViewById(R.id.forgot_pw);
        email = findViewById(R.id.email_et);
        password = findViewById(R.id.password_et);
        loginBtn = findViewById(R.id.login_btn);
        signinWithGoogle = findViewById(R.id.signin_with_google);
        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(v -> {
            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();

            if (emailStr.isEmpty()) {
                email.setError("Email is required");
                return;
            }

            if (passwordStr.isEmpty()) {
                password.setError("Password is required");
                return;
            }

            mAuth = FirebaseAuth.getInstance();

            mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Login successful
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class); // Replace with your actual home screen
                            startActivity(intent);
                            finish();
                        } else {
                            // Login failed
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login failed";
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        signinWithGoogle.setOnClickListener(v -> {


        });

        createAcc.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPw.setOnClickListener(v -> {
            // Handle forgot password logic here
        });


    }
}