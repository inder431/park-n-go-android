package com.parkngo.parkngo.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parkngo.parkngo.data.parkinghistory.ParkingHistory;
import com.parkngo.parkngo.databinding.ParkingHistoryRowBinding;
import com.parkngo.parkngo.repository.ParkRepository;
import com.parkngo.parkngo.utils.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ParkingHistoryAdapter extends RecyclerView.Adapter<ParkingHistoryAdapter.ParkingHistoryViewHolder> {

    private List<ParkingHistory> mList;
    private Context mContext;
    private static final String TAG = ParkingHistoryAdapter.class.getSimpleName();

    public ParkingHistoryAdapter(List<ParkingHistory> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ParkingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParkingHistoryViewHolder(ParkingHistoryRowBinding.inflate(LayoutInflater.from(mContext),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingHistoryViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ParkingHistoryViewHolder extends RecyclerView.ViewHolder{
        private ParkingHistoryRowBinding binding;
        public ParkingHistoryViewHolder(@NonNull ParkingHistoryRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ParkingHistory parkingHistory){

            binding.qrCode.setImageResource(Constants.qrCodes[new Random().nextInt(Constants.qrCodes.length)]);
            binding.bookingId.setText(parkingHistory.bookingId);
            binding.confirmedOn.setText(new SimpleDateFormat("dd MMMM yyyy hh:mm aaa").format(new Date(parkingHistory.confirmedOn)));
            if (new Date(parkingHistory.duration.getEndTime()).before(new Date(System.currentTimeMillis()))){
                binding.status.setTextColor(Color.parseColor("#E40404"));
                binding.status.setText("EXPIRED");
            }else{
                binding.status.setTextColor(Color.parseColor("#0BBB12"));
                binding.status.setText("CONFIRMED");
            }

            binding.getRoot().setOnClickListener(view -> {

            });

            ParkRepository.getInstance().getTotalParkingAmount(parkingHistory.duration.startTime,parkingHistory.duration.endTime,totalAmount->{
                binding.totalAmount.setText(String.format(Locale.getDefault(),"%.2f $",totalAmount));
            });

            DateFormat durationFormat = new SimpleDateFormat("hh:mm aaa");
            binding.duration.setText(String.format("%s to %s",
                    durationFormat.format(new Date(parkingHistory.duration.getStartTime()))
                    ,durationFormat.format(new Date(parkingHistory.duration.getEndTime()))));
            binding.slotCode.setText(parkingHistory.slotCode);

        }
    }
}
