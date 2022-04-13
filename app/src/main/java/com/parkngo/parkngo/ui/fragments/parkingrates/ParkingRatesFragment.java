package com.parkngo.parkngo.ui.fragments.parkingrates;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parkngo.parkngo.R;
import com.parkngo.parkngo.adapters.ParkingRateAdapter;
import com.parkngo.parkngo.data.rates.Rate;
import com.parkngo.parkngo.databinding.FragmentParkingRatesBinding;

import java.util.List;

public class ParkingRatesFragment extends Fragment {
    private FragmentParkingRatesBinding binding;
    private ParkingRatesViewModel viewModel;

    public ParkingRatesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentParkingRatesBinding.inflate(LayoutInflater.from(getContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(ParkingRatesFragment.this).get(ParkingRatesViewModel.class);
        viewModel.getParkingRates(this::setUpParkingRecycler);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(),isLoading->{
            Log.e("getIsLoading()", "value:"+isLoading);
            if (isLoading){
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.parkingRatesRecycler.setVisibility(View.GONE);
                binding.rateHeader.setVisibility(View.GONE);
                binding.note.setVisibility(View.GONE);
                binding.reserveSlotButton.setVisibility(View.GONE);

            }else{
                binding.progressBar.setVisibility(View.GONE);
                binding.parkingRatesRecycler.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setUpParkingRecycler(List<Rate> rateList) {
        binding.parkingRatesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.parkingRatesRecycler.setAdapter(new ParkingRateAdapter(rateList,getContext()));
        binding.rateHeader.setVisibility(View.VISIBLE);
        binding.note.setVisibility(View.VISIBLE);
        binding.reserveSlotButton.setVisibility(View.VISIBLE);
    }
}