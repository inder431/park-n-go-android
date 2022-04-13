package com.parkngo.parkngo.ui.fragments.availableslots;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.parkngo.parkngo.R;
import com.parkngo.parkngo.adapters.ParkingSlotAdapter;
import com.parkngo.parkngo.data.layout.Layout;
import com.parkngo.parkngo.data.layout.SlotsOccupied;
import com.parkngo.parkngo.databinding.FragmentAvailableSlotsBinding;
import com.parkngo.parkngo.repository.ParkRepository;
import com.parkngo.parkngo.ui.fragments.bookslot.BookSlotViewModel;

public class AvailableSlotsFragment extends Fragment {

    private FragmentAvailableSlotsBinding binding;
    private AvailableSlotsViewModel viewModel;
    private long fromDate;
    private long toDate;
    private String layoutId;
    private int selectedIndex;

    public AvailableSlotsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAvailableSlotsBinding.inflate(LayoutInflater.from(getContext()), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(AvailableSlotsViewModel.class);
        if (getArguments() != null) {
            fromDate = getArguments().getLong("fromDate");
            toDate = getArguments().getLong("toDate");
        }

        viewModel.getAllLayout(layouts -> {
            for (Layout layout : layouts) {
                binding.tab.addTab(binding.tab.newTab().setText(layout.layoutTitle).setTag(layout.layoutId));
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.parkingSlotRecycler.setVisibility(View.GONE);
                binding.underMaintenance.setVisibility(View.GONE);

            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.parkingSlotRecycler.setVisibility(View.VISIBLE);
            }
        });

        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadLayout((String) tab.getTag());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.bookSlotButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("fromDate", fromDate);
            bundle.putLong("toDate", toDate);
            bundle.putString("layoutId", layoutId);
            bundle.putInt("index", selectedIndex);
            Navigation.findNavController(requireView()).navigate(R.id.action_availableSlotsFragment_to_confirmReservationFragment, bundle);
        });

        viewModel.getSlots().observe(getViewLifecycleOwner(), slots -> {
            viewModel.getLayout(layoutId, layout -> {
                if (layout.active) {
                    binding.parkingSlotRecycler.setLayoutManager(new GridLayoutManager(requireContext(), layout.columns));
                    binding.parkingSlotRecycler.setAdapter(new ParkingSlotAdapter(slots, requireContext(), layout.layoutTitle, layout.columns, selectedIndex -> {
                        this.selectedIndex = selectedIndex;
                        Log.e("selected index", String.format("(%d,%d)", selectedIndex / layout.columns, selectedIndex % layout.columns));
                        binding.bookSlotButton.setVisibility(View.VISIBLE);

                    }));
                } else {
                    binding.parkingSlotRecycler.setVisibility(View.GONE);
                    binding.underMaintenance.setVisibility(View.VISIBLE);
                    binding.bookSlotButton.setVisibility(View.GONE);
                }
            });
        });
    }

    private void loadLayout(String layoutId) {
        this.layoutId = layoutId;
        binding.bookSlotButton.setVisibility(View.GONE);
        viewModel.loadLayout(layoutId, fromDate, toDate);
    }
}