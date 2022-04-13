package com.parkngo.parkngo.ui.activities.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.parkngo.parkngo.databinding.ActivityRegisterBinding;
import com.parkngo.parkngo.ui.activities.Intro.IntroScreen;
import com.parkngo.parkngo.ui.activities.main.MainActivity;
import com.parkngo.parkngo.utils.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private ProgressDialog signUpProgressDialog;
    private RegisterActivityViewModel viewModel;
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(RegisterActivityViewModel.class);

        initializeProgressDialog();
        hideSignUpProgressDialog();

        viewModel.getStatus().observe(this,success->{
            if (success){
                viewModel.isAdmin(FirebaseAuth.getInstance().getCurrentUser().getUid(),isAdmin->{
                    Log.e(TAG, "checkIsAdmin: "+isAdmin);
                    updateAdminValue(isAdmin);
                    Log.e(TAG, "getStatus: SignUp: Success");
                    FirebaseAuth.getInstance().signOut();
                    AuthUI.getInstance().signOut(this);
                    Intent intent = new Intent(this, IntroScreen.class);
                    startActivity(intent);
                    finish();
                });
            }else{
                Log.e(TAG, "getStatus: SignUp: Failed");
            }
            dismissSignUpProgressDialog();
        });

        binding.backArrow.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.signUp.setOnClickListener(view -> {
            initiateRegistration();
        });
    }

    private void initiateRegistration() {
        showSignUpProgressDialog();
        final String Name = binding.nameEditText.getText().toString().trim();
        final String Email = binding.emailEditText.getText().toString().trim();
        final String Pass = binding.passwordEditText.getText().toString().trim();
        final String ConfirmPass = binding.confirmPasswordEditText.getText().toString().trim();

        if (!TextUtils.isEmpty(binding.nameEditText.getText().toString().trim())
                && !TextUtils.isEmpty(binding.emailEditText.getText().toString())
                && !TextUtils.isEmpty(binding.passwordEditText.getText().toString().trim())
                && !TextUtils.isEmpty(binding.confirmPasswordEditText.getText().toString().trim())) {

            if (!validateUserFullName(Name)){
                dismissSignUpProgressDialog();
                Toast.makeText(this, "Name is not valid!", Toast.LENGTH_SHORT).show();
            }else if (!validateUserEmail(Email)){
                dismissSignUpProgressDialog();
                Toast.makeText(this, "Email address not valid!", Toast.LENGTH_SHORT).show();
            }else if (Pass.length() < 6){
                dismissSignUpProgressDialog();
                Toast.makeText(this, "Password Length less than 6", Toast.LENGTH_SHORT).show();
            }else if (!Pass.equals(ConfirmPass)){
                dismissSignUpProgressDialog();
                Toast.makeText(this, "Password and Confirm Password does not match", Toast.LENGTH_SHORT).show();
            }else{
                Log.e(TAG, "SignUpUser: valid details:");
                viewModel.signUpUserWithEmailAndPassword(Name,Email,Pass);
            }
        } else {
            Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show();
            dismissSignUpProgressDialog();
        }
    }

    private void initializeProgressDialog() {
        signUpProgressDialog = new ProgressDialog(this);
        signUpProgressDialog.setMessage("Please Wait ...");
    }

    public void showSignUpProgressDialog(){
        signUpProgressDialog.show();
    }

    public void hideSignUpProgressDialog(){
        signUpProgressDialog.hide();
    }

    public void dismissSignUpProgressDialog(){
        signUpProgressDialog.dismiss();
    }

    public static boolean validateUserFullName(String name) {
        String regex = "^[\\p{L} .'-]+$";
        Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(name);
        return matcher.find();
    }

    public static boolean validateUserEmail(String email) {
        return email.contains("@") && email.contains(".");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void updateAdminValue(boolean value){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FirebaseAuth.getInstance().getCurrentUser().getUid(),value);
        editor.apply();
    }
}