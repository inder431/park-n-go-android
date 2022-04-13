package com.parkngo.parkngo.ui.activities.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.repository.ParkRepository;

public class RegisterActivityViewModel extends ViewModel {

    private MutableLiveData<Boolean> Status = new MutableLiveData<>();
    private ParkRepository mRepo;

    public RegisterActivityViewModel() {
        mRepo = ParkRepository.getInstance();
    }

    public void signUpUserWithEmailAndPassword(String name, String email, String pass) {
        mRepo.signUpUserWithEmailAndPassword(name,email,pass,success->{
            Status.setValue(success);
        });
    }

    public void isAdmin(String uid, LoadData<Boolean> loadData){
        mRepo.isAdmin(uid,loadData);
    }

    public LiveData<Boolean> getStatus() {
        return Status;
    }
}
