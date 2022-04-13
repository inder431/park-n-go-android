package com.parkngo.parkngo.ui.fragments.confirmation;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.zxing.WriterException;
import com.parkngo.parkngo.R;
import com.parkngo.parkngo.databinding.FragmentConfirmationBinding;
import com.parkngo.parkngo.repository.ParkRepository;

import net.glxn.qrgen.android.QRCode;

public class ConfirmationFragment extends Fragment {

    private FragmentConfirmationBinding binding;
    public ConfirmationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentConfirmationBinding.inflate(LayoutInflater.from(getContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        confirm();
    }

    private void confirm() {
        ParkRepository.getInstance().confirmSlot(getArguments().getString("layoutId"),getArguments().getString("slotId"), this::generateQrCode);
    }

    private void generateQrCode(String parkingId) {
        Bitmap qrBitmap = QRCode.from(parkingId).bitmap();
        binding.qrCode.setImageBitmap(qrBitmap);
        new Handler().postDelayed(()->{
            Navigation.findNavController(requireView()).navigate(R.id.action_confirmationFragment_to_dashboardFragment);
        },3000);
    }
}