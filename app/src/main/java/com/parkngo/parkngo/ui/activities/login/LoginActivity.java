package com.parkngo.parkngo.ui.activities.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.parkngo.parkngo.databinding.ActivityLoginBinding;
import com.parkngo.parkngo.ui.activities.main.MainActivity;
import com.parkngo.parkngo.utils.Constants;

import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private ProgressDialog loginProgressDialog;
    private LoginActivityViewModel viewModel;
    private static final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);

        initializeProgressDialog();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        // setting google sign in button size
        binding.googleSignIn.setSize(SignInButton.SIZE_WIDE);

        // back button
        binding.backArrow.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.googleSignIn.setOnClickListener(view -> {
            startGoogleSignIn();
        });

        binding.resetPassword.setOnClickListener(view -> {
            if (TextUtils.isEmpty(binding.emailEditText.getText().toString().trim())){
                Toast.makeText(this, "Enter email to send password reset request", Toast.LENGTH_SHORT).show();
            }else{
                viewModel.sendPasswordResetEmail(binding.emailEditText.getText().toString().trim(),success->{
                    if (success){
                        Toast.makeText(this, "Email sent Successfully", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "Failed  to send email", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.signIn.setOnClickListener(view -> {
            showLoginProgressDialog();

            binding.signIn.setText("Signing In ...");
            binding.signIn.setClickable(false);

            if (!TextUtils.isEmpty(binding.emailEditText.getText().toString().trim()) && !TextUtils.isEmpty(binding.passwordEditText.getText().toString().trim())) {
                viewModel.loginUserWithEmailAndPassword(binding.emailEditText.getText().toString().trim(),binding.passwordEditText.getText().toString().trim());
            } else {
                binding.signIn.setText("sign in");
                binding.signIn.setClickable(true);
                Toast.makeText(this, "Fill All Details", Toast.LENGTH_SHORT).show();
                dismissLoginProgressDialog();
            }
        });

        viewModel.getLoginStatus().observe(this,success->{
            Log.e(TAG, "onCreate: success:"+success);
            if (success){
                viewModel.checkIsAdmin(FirebaseAuth.getInstance().getCurrentUser().getUid(),isAdmin->{
                    Log.e(TAG, "checkIsAdmin: "+isAdmin);
                    updateAdminValue(isAdmin);
                    navigateToMainActivity();
                });
            }else{
                Toast.makeText(this, "Incorrect Details", Toast.LENGTH_SHORT).show();
                binding.signIn.setText("Sign In");
                binding.signIn.setClickable(true);
                dismissLoginProgressDialog();
            }
        });

    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        dismissLoginProgressDialog();
        startActivity(intent);
        finish();
    }

    private void startGoogleSignIn() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            viewModel.writeInitialDataIfNotExists(user);
        } else {
            //Log.e(TAG, "onSignInResult: "+response.getError().getMessage());
            Toast.makeText(this, "Failed to login with Google", Toast.LENGTH_SHORT).show();
        }
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    private void initializeProgressDialog() {
        loginProgressDialog = new ProgressDialog(this);
        loginProgressDialog.setMessage("Please Wait ...");
    }

    public void updateAdminValue(boolean value){
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FirebaseAuth.getInstance().getCurrentUser().getUid(),value);
        editor.apply();
    }

    public void showLoginProgressDialog(){
        loginProgressDialog.show();
    }

    public void hideLoginProgressDialog(){
        loginProgressDialog.hide();
    }

    public void dismissLoginProgressDialog(){
        loginProgressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}