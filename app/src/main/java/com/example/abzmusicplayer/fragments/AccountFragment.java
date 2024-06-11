package com.example.abzmusicplayer.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abzmusicplayer.CardDetailsActivity;
import com.example.abzmusicplayer.LoginActivity;
import com.example.abzmusicplayer.R;
import com.example.abzmusicplayer.databinding.FragmentAccountBinding;
import com.example.abzmusicplayer.databinding.FragmentHomeBinding;
import com.example.abzmusicplayer.model.UserHelper;

public class AccountFragment extends Fragment {
    FragmentAccountBinding binding;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAccountBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (UserHelper.users != null) {
            binding.rlTop.tvLabel.setText("Account");
        }

        binding.btnChangeMembership.setOnClickListener(view12 -> {
            Intent intent = new Intent(requireContext(), CardDetailsActivity.class);
            intent.putExtra("from", "account");
            startActivity(intent);
        });

        binding.btnLogout.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finishAffinity();
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        binding.tvName.setText(UserHelper.users.getName());
        binding.tvEmail.setText(UserHelper.users.getEmail());
        binding.tvPhone.setText(UserHelper.users.getPhone());
        binding.tvDob.setText(UserHelper.users.getDob());
        binding.tvGender.setText(UserHelper.users.getGender());
        if (UserHelper.users.getMembership().equals("free")){
            binding.tvMembership.setText("Free Membership");
        }else{
            binding.tvMembership.setText("Gold Membership");
        }

    }
}