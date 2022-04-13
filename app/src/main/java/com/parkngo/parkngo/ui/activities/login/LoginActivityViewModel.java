package com.parkngo.parkngo.ui.activities.login;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.repository.ParkRepository;

public class LoginActivityViewModel extends ViewModel {

    private ParkRepository mRepo;
    private MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();

    public LoginActivityViewModel() {
        mRepo = ParkRepository.getInstance();
    }


    public void loginUserWithEmailAndPassword(String email, String password) {
        mRepo.loginUserWithEmailAndPassword(email,password,status->{
            loginStatus.setValue(status);
        });
    }

    public LiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }

    public void writeInitialDataIfNotExists(FirebaseUser user) {
        mRepo.writeInitialUserData(user.getUid(),user.getDisplayName(),user.getEmail(),success->{
            if (success){
                loginStatus.setValue(success);
            }
        });
    }

    public void sendPasswordResetEmail(String email,LoadData<Boolean> loadData){
        mRepo.sendPasswordResetRequest(email,loadData);
    }

    public void checkIsAdmin(String uid,LoadData<Boolean> loadData){
        mRepo.isAdmin(uid,loadData);
    }
}
