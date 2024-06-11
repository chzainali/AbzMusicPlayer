package com.example.abzmusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.view.View;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.abzmusicplayer.databinding.ActivityMainBinding;
import com.example.abzmusicplayer.model.MusicModel;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    NavHostFragment navHostFragment;
    NavController navController;
    public static ArrayList<MusicModel> musicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get the NavHostFragment and NavController
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        // Set up MeowBottomNavigation
        handleBottomNavigation();
        musicList.clear();
        musicList.add(new MusicModel("Solitude", "Lucafrancini", getString(R.string.details1), R.drawable.ic_solitude, R.raw.solitude));
        musicList.add(new MusicModel("Better Day", "Oleksandr Stepanov", getString(R.string.details2), R.drawable.better_day, R.raw.better_day));
        musicList.add(new MusicModel("Relaxing", "Maksym Dudchyk", getString(R.string.details3), R.drawable.ic_relaxing, R.raw.relaxing));
        musicList.add(new MusicModel("Lofi Chill", "BoDleasons", getString(R.string.details4), R.drawable.lofi_chill, R.raw.lofi_chill));
        musicList.add(new MusicModel("Ambient Classical Guitar", "William King", getString(R.string.details5), R.drawable.ic_ambient, R.raw.ambient));
        musicList.add(new MusicModel("Weeknds", "DayFox", getString(R.string.details6), R.drawable.weeknds, R.raw.weeknds));
        musicList.add(new MusicModel("Stomping Rock", "AlexGrohi", getString(R.string.details7), R.drawable.stomping, R.raw.stomping));
        musicList.add(new MusicModel("Coffee", "Prazkhanal", getString(R.string.details9), R.drawable.ic_coffee, R.raw.coffee));
        musicList.add(new MusicModel("Sunshine Jaunt", "Top-Flow", getString(R.string.details9), R.drawable.sunshine, R.raw.sunshine));
        musicList.add(new MusicModel("Unlimited Motivation", "Top-Flow", getString(R.string.details9), R.drawable.motivation, R.raw.motivation));
    }

    private void handleBottomNavigation() {

        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_baseline_home_24));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_playlist));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_search));
        binding.bottomNav.bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_baseline_person_24));

        navController.navigate(R.id.homeFragment);
        binding.bottomNav.bottomNavigation.show(1, true);

        binding.bottomNav.bottomNavigation.setOnClickMenuListener(model -> {
            switch (model.getId()) {
                case 1:
                    navController.navigate(R.id.homeFragment);
                    break;

                case 2:
                    navController.navigate(R.id.playlistFragment);
                    break;

                case 3:
                    navController.navigate(R.id.searchFragment);
                    break;
                case 4:
                    navController.navigate(R.id.accountFragment);
                    break;
            }
            return null;
        });

        binding.bottomNav.home.setOnClickListener(v -> {
            navController.navigate(R.id.homeFragment);
            binding.bottomNav.bottomNavigation.show(1, true);
        });

        binding.bottomNav.playlists.setOnClickListener(v -> {
            binding.bottomNav.bottomNavigation.show(2, true);
            navController.navigate(R.id.playlistFragment);
        });

        binding.bottomNav.orders.setOnClickListener(v -> {
            navController.navigate(R.id.searchFragment);
            binding.bottomNav.bottomNavigation.show(3, true);
        });

        binding.bottomNav.account.setOnClickListener(v -> {
            navController.navigate(R.id.accountFragment);
            binding.bottomNav.bottomNavigation.show(4, true);
        });
    }
}