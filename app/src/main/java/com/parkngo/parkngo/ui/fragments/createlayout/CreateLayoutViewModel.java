package com.parkngo.parkngo.ui.fragments.createlayout;

import androidx.lifecycle.ViewModel;

import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.repository.ParkRepository;

public class CreateLayoutViewModel extends ViewModel {
    private ParkRepository mRepo;
    public CreateLayoutViewModel() {
        mRepo = ParkRepository.getInstance();
    }


    public void createLayout(String layoutTitle, int rows, int columns, LoadData<String> loadData){
        mRepo.createLayout(rows,columns,layoutTitle,loadData);
    }
}
