package com.example.abzmusicplayer.fragments;

import static com.example.abzmusicplayer.MainActivity.musicList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abzmusicplayer.R;
import com.example.abzmusicplayer.adapter.PlaylistAdapter;
import com.example.abzmusicplayer.adapter.SongsAdapter;
import com.example.abzmusicplayer.databinding.FragmentSearchBinding;
import com.example.abzmusicplayer.databinding.FragmentSongsBinding;
import com.example.abzmusicplayer.model.MusicModel;
import com.example.abzmusicplayer.model.OnClick;
import com.example.abzmusicplayer.model.PlaylistModel;
import com.example.abzmusicplayer.model.SongsModel;
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

public class SearchFragment extends Fragment {
    FragmentSearchBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefSongs;
    DatabaseReference dbRefPlaylist;
    SongsAdapter songsAdapter;
    ArrayList<PlaylistModel> playList = new ArrayList<>();
    PlaylistAdapter playlistAdapter;

    public SearchFragment() {
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
        binding = FragmentSearchBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rlTop.tvLabel.setText("Search");
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefSongs = FirebaseDatabase.getInstance().getReference("Songs");
        dbRefPlaylist = FirebaseDatabase.getInstance().getReference("Playlists");

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    songsAdapter.setList(musicList);
                } else {
                    filter(newText);
                }
                return false;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getPlaylists();
        setAdapter();
    }

    private void setAdapter() {
        songsAdapter = new SongsAdapter(requireContext(), musicList, "search", pos -> {
            MusicModel musicModel = musicList.get(pos);
            showDialog(musicModel);
        });
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSongs.setAdapter(songsAdapter);
    }

    @SuppressLint("SetTextI18n")
    public void showDialog(MusicModel musicModel) {
        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.item_playlist_recyclerview_dialog);

        TextView noDataFound = dialog.findViewById(R.id.noDataFound);
        RecyclerView rvPlaylist = dialog.findViewById(R.id.rvPlaylist);
        Button btnDismiss = dialog.findViewById(R.id.btnDismiss);

        playlistAdapter = new PlaylistAdapter(requireContext(), playList, new OnClick() {
            @Override
            public void clicked(int pos) {
                progressDialog.show();
                PlaylistModel playlistModel = playList.get(pos);
                String pushId = dbRefSongs.push().getKey();
                SongsModel model = new SongsModel(pushId, auth.getCurrentUser().getUid(), playlistModel.getPlayListId(), musicModel.getMusicName());
                dbRefSongs.child(pushId).setValue(model).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        dialog.dismiss();
                        showMessage("Added Successfully");
                    } else {
                        progressDialog.dismiss();
                        showMessage(task.getException().getMessage());
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    showMessage(e.getLocalizedMessage());
                });
            }
        });
        rvPlaylist.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPlaylist.setAdapter(playlistAdapter);

        if (playList.isEmpty()) {
            noDataFound.setVisibility(View.VISIBLE);
            rvPlaylist.setVisibility(View.GONE);
        } else {
            noDataFound.setVisibility(View.GONE);
            rvPlaylist.setVisibility(View.VISIBLE);
        }

        btnDismiss.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public void filter(String text) {
        List<MusicModel> filteredList = new ArrayList<>();
        for (MusicModel model : musicList) {
            if (model.getMusicName().toLowerCase(Locale.ROOT).contains(text.toLowerCase()) || model.getAuthorName().toLowerCase(Locale.ROOT).contains(text.toLowerCase())) {
                filteredList.add(model);
            }
        }
        songsAdapter.setList(filteredList);
    }

    private void getPlaylists() {
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
                            if (model.getUserId().equals(auth.getCurrentUser().getUid())) {
                                playList.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

}