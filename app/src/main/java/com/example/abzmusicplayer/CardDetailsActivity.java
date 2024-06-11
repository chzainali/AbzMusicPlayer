package com.example.abzmusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.abzmusicplayer.databinding.ActivityCardDetailsBinding;
import com.example.abzmusicplayer.databinding.ActivityRegisterBinding;
import com.example.abzmusicplayer.model.UserHelper;
import com.example.abzmusicplayer.model.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CardDetailsActivity extends AppCompatActivity {
    ActivityCardDetailsBinding binding;
    String cardName, cardNumber, expiryDate, securityCode, zipPostalCode, membership;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefUsers;
    String from;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCardDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getIntent().getExtras() != null){
            from = getIntent().getStringExtra("from");
            if (Objects.equals(from, "account")){
                binding.rlTop.tvLabel.setText("Update Details");
                binding.nameEt.setText(UserHelper.users.getCardName());
                binding.cardNumberEt.setText(UserHelper.users.getCardNumber());
                binding.expiryDateEt.setText(UserHelper.users.getExpiryDate());
                binding.securityEt.setText(UserHelper.users.getSecurityCode());
                binding.zipPostalCodeEt.setText(UserHelper.users.getZipPostalCode());
                if (Objects.equals(UserHelper.users.getMembership(), "free")){
                    binding.rbFree.setChecked(true);
                }else{
                    binding.rbGold.setChecked(true);
                }
            }else{
                binding.rlTop.tvLabel.setText("Register (Step 2/2)");
            }
        }

        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> finish());
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefUsers = FirebaseDatabase.getInstance().getReference("Users");

        binding.cardNumberEt.addTextChangedListener(new CardNumberTextWatcher(binding.cardNumberEt));
        binding.expiryDateEt.addTextChangedListener(new ExpiryDateTextWatcher(binding.expiryDateEt));

        binding.btnSave.setOnClickListener(view -> {
            if (isValidated()){
                saveData();
            }
        });

        binding.btnSkip.setOnClickListener(v -> {
            startActivity(new Intent(CardDetailsActivity.this, MainActivity.class));
            finishAffinity();
        });

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.rbFree) {
                    membership = "free";
                } else if (checkedId == R.id.rbGold) {
                    // Gold membership selected
                    // First, check if data is validated
                    if (isValidated()) {
                        membership = "gold";
                    } else {
                        // If validation fails, revert to Free membership
                        binding.rbFree.setChecked(true);
                        membership = "free";
                    }
                }
            }
        });
    }

    private void saveData() {
        progressDialog.show();
        Map<String, Object> update = new HashMap<>();
        update.put("cardName", cardName);
        update.put("cardNumber", cardNumber);
        update.put("expiryDate", expiryDate);
        update.put("securityCode", securityCode);
        update.put("zipPostalCode", zipPostalCode);
        update.put("membership", membership);
        dbRefUsers.child(auth.getCurrentUser().getUid()).updateChildren(update).addOnCompleteListener(task -> {
            UserHelper.users.setCardName(cardName);
            UserHelper.users.setCardNumber(cardNumber);
            UserHelper.users.setExpiryDate(expiryDate);
            UserHelper.users.setSecurityCode(securityCode);
            UserHelper.users.setZipPostalCode(zipPostalCode);
            UserHelper.users.setMembership(membership);
            progressDialog.dismiss();
            showMessage("Saved Successfully");
            if (Objects.equals(from, "account")){
                finish();
            }else{
                startActivity(new Intent(CardDetailsActivity.this, MainActivity.class));
                finishAffinity();
            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            showMessage(e.getMessage());
        });
    }

    private Boolean isValidated() {
        cardName = binding.nameEt.getText().toString().trim();
        cardNumber = binding.cardNumberEt.getText().toString().trim();
        expiryDate = binding.expiryDateEt.getText().toString().trim();
        securityCode = binding.securityEt.getText().toString().trim();
        zipPostalCode = binding.zipPostalCodeEt.getText().toString().trim();
        int selectedRadioButtonId = binding.radioGroup.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            String checkMember = selectedRadioButton.getText().toString();
            if (checkMember.contains("Free")){
                membership = "free";
            }else{
                membership = "gold";
            }
        }

        if (cardName.isEmpty() || !validateCardNumber(cardNumber) || expiryDate.length()<5 || securityCode.isEmpty() || zipPostalCode.isEmpty()) {
            showMessage("Please enter valid card details");
            return false;
        }

        return true;
    }

    private boolean validateCardNumber(String cardNumber) {
        // Perform card number validation logic
        return !TextUtils.isEmpty(cardNumber) && cardNumber.length() == 19;
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private class CardNumberTextWatcher implements TextWatcher {
        private TextInputEditText editText;

        CardNumberTextWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            String formattedText = formatCardNumber(text);
            editText.removeTextChangedListener(this);
            editText.setText(formattedText);
            editText.setSelection(formattedText.length());
            editText.addTextChangedListener(this);
        }
    }

    private String formatCardNumber(String cardNumber) {
        // Remove any non-digit characters
        String cleanedNumber = cardNumber.replaceAll("\\D", "");

        // Insert space after every 4 digits
        StringBuilder formattedNumber = new StringBuilder();
        for (int i = 0; i < cleanedNumber.length(); i++) {
            formattedNumber.append(cleanedNumber.charAt(i));
            if ((i + 1) % 4 == 0 && i + 1 < cleanedNumber.length()) {
                formattedNumber.append(" ");
            }
        }
        return formattedNumber.toString();
    }

    private class ExpiryDateTextWatcher implements TextWatcher {
        private TextInputEditText editText;

        ExpiryDateTextWatcher(TextInputEditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            // You can leave this method empty or add any specific logic you need after text changes
            String text = editable.toString();
            String formattedText = formatExpiryDate(text);
            editText.removeTextChangedListener(this);
            editText.setText(formattedText);
            editText.setSelection(formattedText.length());
            editText.addTextChangedListener(this);
        }
    }

    private String formatExpiryDate(String expiryDate) {
        // Remove any non-digit characters
        String cleanedDate = expiryDate.replaceAll("\\D", "");

        // Check if the cleaned string has a length of 4 and follows the desired format (MM/YY)
        if (!TextUtils.isEmpty(cleanedDate) && cleanedDate.length() == 4 && cleanedDate.matches("^\\d{4}$")) {
            // Extract month and year from the cleaned string
            int month = Integer.parseInt(cleanedDate.substring(0, 2));
            int year = Integer.parseInt(cleanedDate.substring(2));

            // Get the current month and last 2 digits of the current year
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int currentMonth = calendar.get(java.util.Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
            int last2DigitsOfYear = calendar.get(java.util.Calendar.YEAR) % 100;

            // Perform validations
            if (month < 1 || month > 12) {
                showMessage("Please enter a valid month (01-12)");
                cleanedDate = "";
            } else if (year < last2DigitsOfYear || (year == last2DigitsOfYear && month < currentMonth)) {
                showMessage("Please enter a valid expiry date (future date)");
                cleanedDate = "";
            } else {
                // Insert a "/" after the first two digits (month)
                return cleanedDate.substring(0, 2) + "/" + cleanedDate.substring(2);
            }
        }

        // If validations fail, return the original cleaned date
        return cleanedDate;
    }

}