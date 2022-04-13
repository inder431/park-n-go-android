package com.parkngo.parkngo.ui.fragments.updatelayoutstatus;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parkngo.parkngo.data.layout.Layout;
import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.repository.ParkRepository;

import java.util.List;

public class UpdateLayoutStatusViewModel extends ViewModel {

    private MutableLiveData<List<Layout>> layouts = new MutableLiveData<>();

    private ParkRepository mRepo;

    public UpdateLayoutStatusViewModel() {
        mRepo = ParkRepository.getInstance();
    }

    public void getAllLayouts(){
        mRepo.getAllLayouts(layouts->{
            this.layouts.setValue(layouts);
        });
    }

    public LiveData<List<Layout>> getLayouts() {
        return layouts;
    }
}
