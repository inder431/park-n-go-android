package com.parkngo.parkngo.ui.fragments.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.parkngo.parkngo.R;
import com.parkngo.parkngo.databinding.FragmentProfileBinding;
import com.parkngo.parkngo.repository.ParkRepository;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public ProfileFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(LayoutInflater.from(requireContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ParkRepository.getInstance().getUserByUid(FirebaseAuth.getInstance().getCurrentUser().getUid(), user -> {
            binding.name.setText(user.name);
            binding.email.setText(user.email);
        });


        ParkRepository.getInstance().getProfilePicOfUser(uri->{
            if (uri!=null) {
                Picasso.get().load(uri).into(binding.profilePic);
            }
        });

        ParkRepository.getInstance().checkGoogleSignIn(isGoogleLogin -> {
            if (isGoogleLogin) {
                binding.passwordBlock.setVisibility(View.GONE);
                binding.confirmPasswordBlock.setVisibility(View.GONE);
                binding.updateProfile.setVisibility(View.GONE);
            } else {
                binding.passwordBlock.setVisibility(View.VISIBLE);
                binding.confirmPasswordBlock.setVisibility(View.VISIBLE);
                binding.updateProfile.setVisibility(View.VISIBLE);
            }
        });


        binding.updateProfile.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.password.getText().toString().trim())) {
                Toast.makeText(requireContext(), "Password is empty", Toast.LENGTH_SHORT).show();
            }else if (TextUtils.isEmpty(binding.password.getText().toString().trim())){
                Toast.makeText(requireContext(), "New Password is empty", Toast.LENGTH_SHORT).show();
            } else {
                ParkRepository.getInstance().updateUserPassword(binding.password.getText().toString().trim(),binding.newPassword.getText().toString().trim(), code -> {
                    switch (code) {
                        case 0:
                            Toast.makeText(requireContext(), "Password Updated!", Toast.LENGTH_SHORT).show();
                            resetPasswordFields();
                            break;
                        case 1:
                            Toast.makeText(requireContext(), "Failed to update Password!", Toast.LENGTH_SHORT).show();
                            resetPasswordFields();
                            break;
                        case 2:
                            Toast.makeText(requireContext(), "Authentication Failed", Toast.LENGTH_SHORT).show();
                            resetPasswordFields();
                            break;
                    }
                });
            }
        });
    }

    private void resetPasswordFields() {
        binding.password.getText().clear();
        binding.newPassword.getText().clear();
    }
}