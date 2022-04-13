package com.parkngo.parkngo.ui.fragments.createlayout;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.parkngo.parkngo.R;
import com.parkngo.parkngo.databinding.FragmentCreateLayoutBinding;

public class CreateLayoutFragment extends Fragment {

    private FragmentCreateLayoutBinding binding;
    private CreateLayoutViewModel viewModel;
    private ProgressDialog dialog;

    public CreateLayoutFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding =  FragmentCreateLayoutBinding.inflate(LayoutInflater.from(requireContext()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CreateLayoutViewModel.class);
        dialog = new ProgressDialog(requireContext());
        dialog.setMessage("Creating Layout...");
        dialog.hide();
        binding.createLayout.setOnClickListener(v -> {
            if (TextUtils.isEmpty(binding.layoutTitle.getText().toString().trim()) ||
                    TextUtils.isEmpty(binding.rows.getText().toString().trim()) ||
                    TextUtils.isEmpty(binding.columns.getText().toString().trim())
            ){
                Toast.makeText(requireContext(), "Fill all details", Toast.LENGTH_SHORT).show();
            }else if (!isNumeric(binding.rows.getText().toString().trim())){
                Toast.makeText(requireContext(), "No. of rows is invalid", Toast.LENGTH_SHORT).show();
            }else if (!isNumeric(binding.columns.getText().toString().trim())){
                Toast.makeText(requireContext(), "No. of columns is invalid", Toast.LENGTH_SHORT).show();
            }else{
                dialog.show();
                viewModel.createLayout(binding.layoutTitle.getText().toString().trim(),Integer.parseInt(binding.rows.getText().toString().trim()),Integer.parseInt(binding.columns.getText().toString().trim()),layoutId->{
                    Toast.makeText(requireContext(), "Layout Created!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }
        });
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}