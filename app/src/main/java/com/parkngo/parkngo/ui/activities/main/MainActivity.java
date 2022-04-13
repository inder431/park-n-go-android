package com.parkngo.parkngo.ui.activities.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.parkngo.parkngo.R;
import com.parkngo.parkngo.databinding.ActivityMainBinding;
import com.parkngo.parkngo.ui.activities.Intro.IntroScreen;
import com.parkngo.parkngo.ui.activities.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBar.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).setOpenableLayout(binding.drawerLayout).build();
        NavigationUI.setupWithNavController(binding.appBar.toolbar,navController,appBarConfiguration);
        binding.navView.getMenu().findItem(R.id.logOut).setOnMenuItemClickListener(menuItem -> {
            FirebaseAuth.getInstance().signOut();
            AuthUI.getInstance()
                    .signOut(this);
            moveToLoginActivity();
            binding.drawerLayout.close();
            return false;
        });

        binding.navView.getMenu().findItem(R.id.profile).setOnMenuItemClickListener(menuItem -> {
            Navigation.findNavController(this,R.id.nav_host_fragment).navigate(R.id.action_dashboardFragment_to_profileFragment);
            binding.drawerLayout.close();
            return false;
        });
        binding.navView.getMenu().findItem(R.id.reserve_slot).setOnMenuItemClickListener(menuItem -> {
            Navigation.findNavController(this,R.id.nav_host_fragment).navigate(R.id.action_dashboardFragment_to_bookSlotFragment);
            binding.drawerLayout.close();
            return false;
        });

        binding.navView.getMenu().findItem(R.id.booking_history).setOnMenuItemClickListener(menuItem -> {
            Navigation.findNavController(this,R.id.nav_host_fragment).navigate(R.id.action_dashboardFragment_to_bookingHistoryFragment);
            binding.drawerLayout.close();
            return false;
        });
    }

    private void moveToLoginActivity() {
        Intent intent = new Intent(this, IntroScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}