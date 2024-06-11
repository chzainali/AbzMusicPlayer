package com.example.abzmusicplayer.fragments;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.abzmusicplayer.R;
import com.example.abzmusicplayer.databinding.FragmentPlaySongBinding;
import com.example.abzmusicplayer.model.MusicModel;

import java.io.IOException;

public class PlaySongFragment extends Fragment implements View.OnClickListener {

    private FragmentPlaySongBinding binding;
    private MusicModel musicModel;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable runnable;

    public PlaySongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            musicModel = (MusicModel) getArguments().getSerializable("data");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPlaySongBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (musicModel != null) {
            binding.rlTop.tvLabel.setText(musicModel.getMusicName());
            binding.ivSong.setImageResource(musicModel.getImage());
        }
        binding.rlTop.ivBack.setVisibility(View.VISIBLE);
        binding.rlTop.ivBack.setOnClickListener(v -> {
            stopMediaPlayer();
            requireActivity().onBackPressed();
        });

        binding.ivPlayPause.setOnClickListener(this);
        binding.ivForward.setOnClickListener(this);
        binding.ivBackward.setOnClickListener(this);

        mediaPlayer = new MediaPlayer();
        handler = new Handler();
        initMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMediaPlayer();
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer.setDataSource(getResources().openRawResourceFd(musicModel.getResourceId()));
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        binding.seekBar.setMax(mediaPlayer.getDuration());

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            binding.ivPlayPause.setImageResource(R.drawable.ic_play);
            mediaPlayer.seekTo(0);
            binding.seekBar.setProgress(0);
            handler.removeCallbacks(runnable);
        });

        runnable = new Runnable() {
            @Override
            public void run() {
                binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 1000);
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivPlayPause) {
            if (isPlaying) {
                pauseMediaPlayer();
            } else {
                startMediaPlayer();
            }
        } else if (v.getId() == R.id.ivForward) {
            forwardMediaPlayer();
        } else if (v.getId() == R.id.ivBackward) {
            backwardMediaPlayer();
        }
    }

    private void startMediaPlayer() {
        mediaPlayer.start();
        isPlaying = true;
        binding.ivPlayPause.setImageResource(R.drawable.ic_pause);
        handler.postDelayed(runnable, 0);
    }

    private void pauseMediaPlayer() {
        mediaPlayer.pause();
        isPlaying = false;
        binding.ivPlayPause.setImageResource(R.drawable.ic_play);
        handler.removeCallbacks(runnable);
    }

    private void stopMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(runnable);
    }

    private void forwardMediaPlayer() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if (currentPosition + 5000 < duration) {
            mediaPlayer.seekTo(currentPosition + 5000);
        } else {
            mediaPlayer.seekTo(duration);
        }
    }

    private void backwardMediaPlayer() {
        int currentPosition = mediaPlayer.getCurrentPosition();
        if (currentPosition - 5000 > 0) {
            mediaPlayer.seekTo(currentPosition - 5000);
        } else {
            mediaPlayer.seekTo(0);
        }
    }
}
