package com.parkngo.parkngo.ui.fragments.updatelayoutstatus;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parkngo.parkngo.R;
import com.parkngo.parkngo.adapters.UpdateLayoutStatusAdapter;
import com.parkngo.parkngo.databinding.FragmentUpdateLayoutStatusBinding;

public class UpdateLayoutStatusFragment extends Fragment {

    private FragmentUpdateLayoutStatusBinding binding;
    private UpdateLayoutStatusViewModel viewModel;

    public UpdateLayoutStatusFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUpdateLayoutStatusBinding.inflate(LayoutInflater.from(requireContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(UpdateLayoutStatusViewModel.class);
        viewModel.getAllLayouts();
        viewModel.getLayouts().observe(getViewLifecycleOwner(),layouts->{
            binding.activeToggleRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
            UpdateLayoutStatusAdapter adapter = new UpdateLayoutStatusAdapter(layouts,requireContext());
            binding.activeToggleRecycler.setAdapter(adapter);
        });
    }
}