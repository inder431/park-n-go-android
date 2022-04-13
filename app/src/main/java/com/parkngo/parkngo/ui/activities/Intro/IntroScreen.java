package com.parkngo.parkngo.ui.activities.Intro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.google.firebase.auth.FirebaseAuth;
import com.parkngo.parkngo.databinding.ActivityIntroScreenBinding;
import com.parkngo.parkngo.ui.activities.login.LoginActivity;
import com.parkngo.parkngo.ui.activities.main.MainActivity;
import com.parkngo.parkngo.ui.activities.register.RegisterActivity;

public class IntroScreen extends AppCompatActivity {

    private ActivityIntroScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroScreenBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser()!=null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.signInButton.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.registerButton.setOnClickListener(view -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

    }
}