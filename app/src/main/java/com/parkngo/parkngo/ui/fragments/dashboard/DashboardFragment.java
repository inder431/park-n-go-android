package com.parkngo.parkngo.ui.fragments.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.parkngo.parkngo.R;
import com.parkngo.parkngo.databinding.FragmentDashboardBinding;
import com.parkngo.parkngo.repository.ParkRepository;
import com.parkngo.parkngo.utils.Constants;

import java.util.Objects;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private static final String TAG = DashboardFragment.class.getSimpleName();

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(LayoutInflater.from(getContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.parkingRates.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_dashboardFragment_to_parkingRatesFragment);
        });

        binding.bookSlot.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_dashboardFragment_to_bookSlotFragment);
        });

        binding.parkingHistory.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_dashboardFragment_to_bookingHistoryFragment);
        });

        binding.blockLayout.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_dashboardFragment_to_updateLayoutStatusFragment);
        });

        binding.createLayout.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_dashboardFragment_to_createLayoutFragment);
        });

        binding.updatePrice.setOnClickListener(v->{
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_dashboardFragment_to_updateParkingRatesFragment);
        });


        Log.e(TAG, "onViewCreated: isAdmin: "+isAdmin());
        if (isAdmin()){
            showAdminCards();
        }


    }

    private void showAdminCards() {
        binding.blockLayout.setVisibility(View.VISIBLE);
        binding.createLayout.setVisibility(View.VISIBLE);
        binding.updatePrice.setVisibility(View.VISIBLE);
    }

    public boolean isAdmin(){
        return requireActivity().getSharedPreferences(Constants.SHARED_PF_NAME, Context.MODE_PRIVATE).getBoolean(FirebaseAuth.getInstance().getCurrentUser().getUid(),false);
    }

}