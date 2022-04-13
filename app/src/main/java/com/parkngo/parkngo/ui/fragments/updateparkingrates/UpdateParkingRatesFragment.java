package com.parkngo.parkngo.ui.fragments.updateparkingrates;

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
import android.widget.Toast;

import com.parkngo.parkngo.R;
import com.parkngo.parkngo.adapters.ParkingRateAdapter;
import com.parkngo.parkngo.adapters.UpdateParkingRateAdapter;
import com.parkngo.parkngo.data.rates.Rate;
import com.parkngo.parkngo.databinding.FragmentUpdateParkingRatesBinding;
import com.parkngo.parkngo.interfaces.UpdatePrice;
import com.parkngo.parkngo.repository.ParkRepository;
import com.parkngo.parkngo.ui.fragments.parkingrates.ParkingRatesFragment;
import com.parkngo.parkngo.ui.fragments.parkingrates.ParkingRatesViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateParkingRatesFragment extends Fragment {

    private FragmentUpdateParkingRatesBinding binding;
    private UpdateParkingRatesViewModel viewModel;
    private List<Rate> rateList = new ArrayList<>();
    public UpdateParkingRatesFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateParkingRatesBinding.inflate(LayoutInflater.from(requireContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(UpdateParkingRatesViewModel.class);
        viewModel.getParkingRates(this::setUpParkingRecycler);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(),isLoading->{
            Log.e("getIsLoading()", "value:"+isLoading);
            if (isLoading){
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.updateParkingRatesRecycler.setVisibility(View.GONE);
                binding.rateHeader.setVisibility(View.GONE);
                binding.note.setVisibility(View.GONE);

            }else{
                binding.progressBar.setVisibility(View.GONE);
                binding.updateParkingRatesRecycler.setVisibility(View.VISIBLE);
            }
        });

        binding.updatePrices.setOnClickListener(v -> {
            if (rateList.isEmpty()){
                Toast.makeText(requireContext(), "Enter update price atleast for one!", Toast.LENGTH_SHORT).show();
            }else {
                ParkRepository.getInstance().updatePriceOfRate(this.rateList, success -> {
                    Toast.makeText(requireContext(), "Price Update Successful", Toast.LENGTH_SHORT).show();
                    viewModel.getParkingRates(this::setUpParkingRecycler);
                    Log.e("UpdatePrice", "updatePriceOfRate: " + success);
                });
            }
        });
    }

    private void setUpParkingRecycler(List<Rate> rateList) {
        this.rateList.clear();
        binding.updateParkingRatesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.updateParkingRatesRecycler.setAdapter(new UpdateParkingRateAdapter(rateList, requireContext(), (rate, updated, delete) -> {
            if (delete){
                if (this.rateList.size()>0) {
                    for (int i = this.rateList.size() - 1; i >= 0; --i) {
                        if (rate.rateId.equals(this.rateList.get(i).rateId)) {
                            this.rateList.remove(i);
                        }
                    }
                }
            }else{
                boolean exists=false;
                for (int i=0;i<this.rateList.size();++i){
                    if (rate.rateId.equals(this.rateList.get(i).rateId)){
                        this.rateList.get(i).setPrice(updated);
                        exists=true;
                    }
                }

                if (!exists){
                    rate.setPrice(updated);
                    this.rateList.add(rate);
                }
            }

            showRateListData();
        }));
        binding.rateHeader.setVisibility(View.VISIBLE);
        binding.note.setVisibility(View.VISIBLE);
    }

    private void showRateListData() {
        Log.e("rateList", String.format("size():%d",this.rateList.size()));
        for (Rate rate: this.rateList){
            Log.e("rateList", String.format("showRateListData(): rateId:%s price:%f",rate.rateId,rate.price));
        }
    }
}