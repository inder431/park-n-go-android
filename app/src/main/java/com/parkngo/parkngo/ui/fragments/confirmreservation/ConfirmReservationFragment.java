package com.parkngo.parkngo.ui.fragments.confirmreservation;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.parkngo.parkngo.R;
import com.parkngo.parkngo.databinding.FragmentConfirmReservationBinding;
import com.parkngo.parkngo.repository.ParkRepository;

public class ConfirmReservationFragment extends Fragment {

    private FragmentConfirmReservationBinding binding;
    private CountDownTimer timer;
    private ConfirmReservationViewModel viewModel;
    private String slotId;
    private String layoutId;
    private int index;
    private long startTime;
    private long endTime;
    private String uid;

    public ConfirmReservationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConfirmReservationBinding.inflate(LayoutInflater.from(getContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ConfirmReservationViewModel.class);
        setUpTimerForConfirmation();
        if (getArguments()!=null) {
            layoutId = getArguments().getString("layoutId");
            index = getArguments().getInt("index");
            startTime = getArguments().getLong("fromDate");
            endTime = getArguments().getLong("toDate");
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        reserveSlot();

        ParkRepository.getInstance().getTotalParkingAmount(getArguments().getLong("fromDate"),getArguments().getLong("toDate"),total->{
            binding.amountBeforeTax.setText(total +" $");
            binding.gstAmount.setText(getGST(total) + " $");
            binding.qstAmount.setText(getQST(total) +" $");
            binding.totalAmount.setText(getTotalAmount(total) +" $");
        });


        binding.cancelButton.setOnClickListener(v->{
            viewModel.removeReservedSlot(layoutId,slotId,success->{
                if (success){
                    Navigation.findNavController(binding.getRoot()).navigate(R.id.action_confirmReservationFragment_to_dashboardFragment);
                }
            });
        });

        binding.confirmButton.setOnClickListener(v->{
            if (timer!=null){
                timer.cancel();
            }
            Bundle bundle = new Bundle();
            bundle.putString("layoutId",layoutId);
            bundle.putString("slotId",slotId);
            Navigation.findNavController(requireView()).navigate(R.id.action_confirmReservationFragment_to_confirmationFragment,bundle);
        });

    }

    private void reserveSlot() {
        viewModel.reserveSlot(layoutId,index,startTime,endTime,uid,slotId->{
            binding.confirmButton.setVisibility(View.VISIBLE);
            binding.cancelButton.setVisibility(View.VISIBLE);
            this.slotId = slotId;
        });
    }

    private void setUpTimerForConfirmation() {
        timer = new CountDownTimer(5 * 60 * 1000,1000){

            @Override
            public void onTick(long millisLeft) {
                long minutes = (millisLeft / 1000) / 60;
                long seconds = (millisLeft / 1000) % 60;
                binding.countDownTimer.setText(String.format("%s:%s Minutes",minutes,seconds));
            }

            @Override
            public void onFinish() {
                viewModel.removeReservedSlot(layoutId,slotId,success->{
                    if (success){
                        Navigation.findNavController(binding.getRoot()).navigate(R.id.action_confirmReservationFragment_to_dashboardFragment);
                    }
                });
            }
        }.start();
    }

    public double getGST(double total){
        return 0.05 * total;
    }

    public double getQST(double total){
        return 0.10 * total;
    }

    public double getTotalAmount(double total){
        return getGST(total)+getQST(total)+total;
    }
}