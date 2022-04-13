package com.parkngo.parkngo.ui.fragments.parkingrates;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.parkngo.parkngo.data.rates.Rate;
import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.repository.ParkRepository;

import java.util.List;

public class ParkingRatesViewModel extends ViewModel {

    private ParkRepository mRepo;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ParkingRatesViewModel(){
        mRepo = ParkRepository.getInstance();
    }

    public void getParkingRates(LoadData<List<Rate>> loadData){
        isLoading.setValue(true);
        mRepo.getParkingRates(rateList->{
            loadData.onDataLoaded(rateList);
            isLoading.setValue(false);
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
