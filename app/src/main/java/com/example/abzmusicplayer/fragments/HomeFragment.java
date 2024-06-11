package com.example.abzmusicplayer.fragments;
import static com.example.abzmusicplayer.MainActivity.musicList;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.abzmusicplayer.R;
import com.example.abzmusicplayer.databinding.FragmentHomeBinding;
import com.example.abzmusicplayer.model.MusicModel;
import com.example.abzmusicplayer.model.UserHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class HomeFragment extends Fragment implements View.OnClickListener {
    FragmentHomeBinding binding;
    ArrayList<MusicModel> filteredList = new ArrayList<>();
    private Random random = new Random();
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.rlTop.tvLabel.setText("Home");
        if (UserHelper.users != null){
            binding.tvWelcome.setText("Welcome Home, "+ UserHelper.users.getName());
        }

        filteredList = musicList;
        Collections.shuffle(filteredList);
        MusicModel randomMusic = filteredList.get(random.nextInt(filteredList.size()));
        binding.ivSong.setImageResource(randomMusic.getImage());
        binding.tvDetails.setText(randomMusic.getPromotion());
        binding.ivPlayPause.setImageResource(R.drawable.ic_play);
        binding.ivPlayPause.setTag(R.drawable.ic_play);
        binding.ivPlayPause.setOnClickListener(this);
        loadSong(randomMusic);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ivPlayPause) {
            int tag = (int) binding.ivPlayPause.getTag();
            if (tag == R.drawable.ic_play) {
                // Start playing the music
                binding.ivPlayPause.setImageResource(R.drawable.ic_pause);
                binding.ivPlayPause.setTag(R.drawable.ic_pause);
                startPlayback();
            } else {
                // Pause the music
                binding.ivPlayPause.setImageResource(R.drawable.ic_play);
                binding.ivPlayPause.setTag(R.drawable.ic_play);
                pausePlayback();
            }
        }
    }

    private void startPlayback() {
        if (!isPlaying && mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    private void pausePlayback() {
        if (isPlaying && mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    private void loadSong(MusicModel musicModel) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getResources().openRawResourceFd(musicModel.getResourceId()));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
