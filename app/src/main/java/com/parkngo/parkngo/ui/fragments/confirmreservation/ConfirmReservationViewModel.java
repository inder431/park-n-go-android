package com.parkngo.parkngo.ui.fragments.confirmreservation;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.parkngo.parkngo.data.layout.Layout;
import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.repository.ParkRepository;

public class ConfirmReservationViewModel extends ViewModel {
    private ParkRepository mRepo;
    public ConfirmReservationViewModel() {
        mRepo = ParkRepository.getInstance();
    }

    public void reserveSlot(String layoutId, int index, long startTime, long endTime, String uid, LoadData<String> loadData){
        mRepo.getLayoutByLayoutId(layoutId,layout->{
            if (layout!=null){
                int row = index/layout.getColumns();
                int column = index%layout.getColumns();
                Log.e("reserveSlot", String.format("booking (%d,%d)",row,column));
                mRepo.reserveSlot(layout.layoutId,startTime,endTime,uid,row,column,layout.layoutTitle,loadData);
            }
        });
    }

    public void removeReservedSlot(String layoutId,String slotId,LoadData<Boolean> loadData){
        mRepo.removeReservedSlot(layoutId,slotId,loadData);
    }
}
