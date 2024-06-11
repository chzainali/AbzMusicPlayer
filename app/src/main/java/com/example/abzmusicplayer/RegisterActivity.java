package com.example.abzmusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.abzmusicplayer.databinding.ActivityLoginBinding;
import com.example.abzmusicplayer.databinding.ActivityRegisterBinding;
import com.example.abzmusicplayer.model.UserHelper;
import com.example.abzmusicplayer.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding binding;
    String name, email, phone, dob, password, conPassword, gender = "";
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    Calendar calendar;
    SimpleDateFormat dateFormatter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rlTop.tvLabel.setText("Register (Step 1/2)");
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        binding.btnSubmit.setOnClickListener(view -> {
            if (isValidated()){
                registerUserAndSubmitData();
            }
        });

        binding.btnClear.setOnClickListener(v -> {
            binding.nameEt.setText("");
            binding.emailEt.setText("");
            binding.phoneEt.setText("");
            binding.dobEt.setText("");
            binding.passET.setText("");
            binding.conPassET.setText("");
            binding.radioGroup.clearCheck();
        });

        binding.dobEt.setOnClickListener(v -> showDatePickerDialog());

    }

    private void registerUserAndSubmitData() {
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authResult -> {
            if (authResult.isSuccessful()){
                UserModel model = new UserModel(name, email, phone, dob, password,gender,"","","", "","","free");
                dbRefUsers.child(auth.getCurrentUser().getUid()).setValue(model).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        UserHelper.users = model;
                        progressDialog.dismiss();
                        showMessage("Submitted Successfully");
                        Intent intent = new Intent(RegisterActivity.this, CardDetailsActivity.class);
                        intent.putExtra("from", "auth");
                        startActivity(intent);
                    }else{
                        progressDialog.dismiss();
                        showMessage(task.getException().getMessage());
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getLocalizedMessage());
                });
            }else{
                progressDialog.dismiss();
                showMessage(authResult.getException().getMessage());
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getLocalizedMessage());
        });
    }

    private Boolean isValidated() {
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        phone = binding.phoneEt.getText().toString().trim();
        dob = binding.dobEt.getText().toString().trim();
        password = binding.passET.getText().toString().trim();
        conPassword = binding.conPassET.getText().toString().trim();
        int selectedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            gender = selectedRadioButton.getText().toString();
        }

        if (name.isEmpty()) {
            showMessage("Please enter full name");
            return false;
        }
        if (email.isEmpty()) {
            showMessage("Please enter email");
            return false;
        }
        if (!(Patterns.EMAIL_ADDRESS).matcher(email).matches()) {
            showMessage("Please enter email in correct format");
            return false;
        }
        if (phone.isEmpty()) {
            showMessage("Please enter phone");
            return false;
        }
        if (dob.isEmpty()) {
            showMessage("Please select date of birth");
            return false;
        }
        if (password.isEmpty()) {
            showMessage("Please enter password");
            return false;
        }
        if (!password.contentEquals(conPassword)) {
            showMessage("Password and confirm password must be matched");
            return false;
        }
        if (gender.isEmpty()) {
            showMessage("Please select gender");
            return false;
        }

        return true;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(year, monthOfYear, dayOfMonth);
            String selectedDate = dateFormatter.format(calendar.getTime());
            binding.dobEt.setText(selectedDate);
        }
    };

}