package com.example.abzmusicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.abzmusicplayer.databinding.ActivityCardDetailsBinding;
import com.example.abzmusicplayer.databinding.ActivityForgotPasswordBinding;
import com.example.abzmusicplayer.model.UserHelper;
import com.example.abzmusicplayer.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    ProgressDialog progressDialog;
    String email;
    FirebaseAuth auth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rlTop.tvLabel.setText("Forgot Password");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        auth = FirebaseAuth.getInstance();

        binding.btnSend.setOnClickListener(v -> {
            if (isValidated()) {
                progressDialog.show();
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        showMessage("Please check your email, password reset link has been sent");
                        finish();
                    } else {
                        showMessage(String.valueOf(task.getException()));
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(e -> {
                    showMessage(e.getMessage());
                    progressDialog.dismiss();
                });
            }
        });

    }

    private Boolean isValidated() {
        email = binding.emailEt.getText().toString().trim();

        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}