package com.example.abzmusicplayer.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.abzmusicplayer.CardDetailsActivity;
import com.example.abzmusicplayer.R;
import com.example.abzmusicplayer.RegisterActivity;
import com.example.abzmusicplayer.adapter.PlaylistAdapter;
import com.example.abzmusicplayer.databinding.FragmentHomeBinding;
import com.example.abzmusicplayer.databinding.FragmentPlaylistBinding;
import com.example.abzmusicplayer.model.MusicModel;
import com.example.abzmusicplayer.model.OnClick;
import com.example.abzmusicplayer.model.PlaylistModel;
import com.example.abzmusicplayer.model.UserHelper;
import com.example.abzmusicplayer.model.UserModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PlaylistFragment extends Fragment {
    FragmentPlaylistBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefPlaylist;
    PlaylistAdapter playlistAdapter;
    ArrayList<PlaylistModel> playList = new ArrayList<>();
    public PlaylistFragment() {
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
        binding = FragmentPlaylistBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rlTop.tvLabel.setText("PlayLists");
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefPlaylist = FirebaseDatabase.getInstance().getReference("Playlists");

        binding.addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    playlistAdapter.setList(playList    );
                } else {
                    filter(newText);
                }
                return false;
            }
        });

    }

    @SuppressLint("SetTextI18n")
    public void showDialog() {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_playlist_dialog);

        TextInputEditText nameEt = dialog.findViewById(R.id.nameEt);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String name = nameEt.getText().toString();

            if (name.isEmpty()) {
                showMessage("Please enter name");
            }  else {
                String pushId = dbRefPlaylist.push().getKey();
                PlaylistModel model = new PlaylistModel(pushId, auth.getCurrentUser().getUid(), name);
                dbRefPlaylist.child(pushId).setValue(model).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        dialog.dismiss();
                        getPlaylists();
                    }else{
                        progressDialog.dismiss();
                        showMessage(task.getException().getMessage());
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getLocalizedMessage());
                });
            }
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPlaylists();
    }

    private void getPlaylists(){
        progressDialog.show();
        dbRefPlaylist.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    playList.clear();
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            PlaylistModel model = ds.getValue(PlaylistModel.class);
                            if (model.getUserId().equals(auth.getCurrentUser().getUid())){
                                playList.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    setAdapter();

                    if (playList.isEmpty()) {
                        binding.noDataFound.setVisibility(View.VISIBLE);
                        binding.rvPlaylist.setVisibility(View.GONE);
                    } else {
                        binding.noDataFound.setVisibility(View.GONE);
                        binding.rvPlaylist.setVisibility(View.VISIBLE);
                    }
                } else {
                    progressDialog.dismiss();
                    binding.noDataFound.setVisibility(View.VISIBLE);
                    binding.rvPlaylist.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setAdapter() {
        playlistAdapter = new PlaylistAdapter(requireContext(), playList, pos -> {
            PlaylistModel playlistModel = playList.get(pos);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", playlistModel);
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_playlistFragment_to_songsFragment, bundle);
        });
        binding.rvPlaylist.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvPlaylist.setAdapter(playlistAdapter);
    }

    public void filter(String text) {
        List<PlaylistModel> filteredList = new ArrayList<>();
        for (PlaylistModel model : playList) {
            if (model.getPlayListName().toLowerCase(Locale.ROOT).contains(text.toLowerCase())) {
                filteredList.add(model);
            }
        }
        playlistAdapter.setList(filteredList);
    }

    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}