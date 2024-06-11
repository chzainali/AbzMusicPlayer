package com.example.abzmusicplayer.fragments;

import static com.example.abzmusicplayer.MainActivity.musicList;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.abzmusicplayer.R;
import com.example.abzmusicplayer.adapter.SongsAdapter;
import com.example.abzmusicplayer.databinding.FragmentSongsBinding;
import com.example.abzmusicplayer.model.MusicModel;
import com.example.abzmusicplayer.model.PlaylistModel;
import com.example.abzmusicplayer.model.SongsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class SongsFragment extends Fragment implements View.OnClickListener {
    FragmentSongsBinding binding;
    ProgressDialog progressDialog;
    FirebaseAuth auth;
    DatabaseReference dbRefSongsList;
    ArrayList<SongsModel> firebaseList = new ArrayList<>();
    ArrayList<MusicModel> songsList = new ArrayList<>();
    PlaylistModel playlistModel;
    MediaPlayer mediaPlayer;
    Handler handler;
    Runnable runnable;
    int currentSongIndex = 0;
    SongsAdapter songsAdapter;

    public SongsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playlistModel = (PlaylistModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSongsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (playlistModel != null){
            binding.rlTop.tvLabel.setText(playlistModel.getPlayListName());
        }
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(getString(R.string.app_name));
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        dbRefSongsList = FirebaseDatabase.getInstance().getReference("Songs");

        binding.ivPlayPause.setOnClickListener(this);
        binding.ivForward.setOnClickListener(this);
        binding.ivBackward.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        handler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPlaylists();
    }

    private void getPlaylists(){
        progressDialog.show();
        dbRefSongsList.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    firebaseList.clear();
                    progressDialog.dismiss();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        try {
                            SongsModel model = ds.getValue(SongsModel.class);
                            if (model.getUserId().equals(auth.getCurrentUser().getUid()) && model.getPlayListId().equals(playlistModel.getPlayListId())){
                                firebaseList.add(model);
                            }
                        } catch (DatabaseException e) {
                            e.printStackTrace();
                        }
                    }

                    if (firebaseList.isEmpty()) {
                        binding.noDataFound.setVisibility(View.VISIBLE);
                        binding.rvSongs.setVisibility(View.GONE);
                    } else {
                        setAdapter();
                        binding.noDataFound.setVisibility(View.GONE);
                        binding.rvSongs.setVisibility(View.VISIBLE);
                        binding.llSong.setVisibility(View.VISIBLE);
                        playSong(currentSongIndex);
                    }
                } else {
                    progressDialog.dismiss();
                    binding.noDataFound.setVisibility(View.VISIBLE);
                    binding.rvSongs.setVisibility(View.GONE);
                    binding.llSong.setVisibility(View.GONE);
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
        songsList.clear();
        for (SongsModel model : firebaseList) {
            MusicModel musicModel = null;
            for (MusicModel music : musicList) {
                if (music.getMusicName().equals(model.getSongName())) {
                    musicModel = music;
                    break;
                }
            }
            if (musicModel != null) {
                songsList.add(musicModel);
            }
        }
        songsAdapter = new SongsAdapter(requireContext(), songsList, "playlist", pos -> {

        });
        binding.rvSongs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvSongs.setAdapter(songsAdapter);
    }

    private void playSong(int index) {
        if (index >= 0 && index < songsList.size()) {
            MusicModel musicModel = songsList.get(index);
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getResources().openRawResourceFd(musicModel.getResourceId()));
                mediaPlayer.prepare();
                mediaPlayer.start();
                updateSongUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateSongUI() {
        binding.ivImage.setImageResource(songsList.get(currentSongIndex).getImage());
        binding.ivPlayPause.setImageResource(R.drawable.ic_pause);
        binding.seekBar.setProgress(0);
        binding.seekBar.setMax(mediaPlayer.getDuration());

        handler.removeCallbacks(runnable);
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    handler.postDelayed(this, 1000);
                }catch (Exception ignored){

                }
            }
        };
        handler.postDelayed(runnable, 0);

        mediaPlayer.setOnCompletionListener(mp -> {
            if (currentSongIndex < songsList.size() - 1) {
                currentSongIndex++;
                playSong(currentSongIndex);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivPlayPause) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                binding.ivPlayPause.setImageResource(R.drawable.ic_play);
            } else {
                mediaPlayer.start();
                binding.ivPlayPause.setImageResource(R.drawable.ic_pause);
            }
        } else if (id == R.id.ivForward) {
            if (currentSongIndex < songsList.size() - 1) {
                currentSongIndex++;
                playSong(currentSongIndex);
            }
        } else if (id == R.id.ivBackward) {
            if (currentSongIndex > 0) {
                currentSongIndex--;
                playSong(currentSongIndex);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }
}
