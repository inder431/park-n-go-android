package com.parkngo.parkngo.ui.fragments.availableslots;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parkngo.parkngo.data.layout.Layout;
import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.repository.ParkRepository;

import java.util.List;

public class AvailableSlotsViewModel extends ViewModel {
    private ParkRepository mRepo;
    private MutableLiveData<List<Layout>> layouts = new MutableLiveData<>();
    private MutableLiveData<boolean[]> Slots = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public AvailableSlotsViewModel() {
        mRepo = ParkRepository.getInstance();
    }


    public void getAllLayout(LoadData<List<Layout>> loadData){
        isLoading.setValue(true);
        mRepo.getAllLayouts(layouts->{
            isLoading.setValue(false);
            loadData.onDataLoaded(layouts);
        });
    }

    public void loadLayout(String layoutId,long startTime,long endTime){
        isLoading.setValue(true);
        mRepo.getSlotList(layoutId,startTime,endTime,slots->{
            isLoading.setValue(false);
            Slots.setValue(slots);
        });
    }

    public void getLayout(String layoutId, LoadData<Layout> loadData){
        isLoading.setValue(true);
        mRepo.getLayoutByLayoutId(layoutId,layout->{
            isLoading.setValue(false);
            loadData.onDataLoaded(layout);
        });
    }

    public LiveData<boolean[]> getSlots() {
        return Slots;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
