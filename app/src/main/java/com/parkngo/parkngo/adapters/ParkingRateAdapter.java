package com.parkngo.parkngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parkngo.parkngo.data.rates.Rate;
import com.parkngo.parkngo.databinding.ParkingRateRowBinding;

import java.util.List;

public class ParkingRateAdapter extends RecyclerView.Adapter<ParkingRateAdapter.ParkingRateViewHolder> {

    private List<Rate> rateList;
    private Context context;

    public ParkingRateAdapter(List<Rate> rateList, Context context) {
        this.rateList = rateList;
        this.context = context;
    }

    @NonNull
    @Override
    public ParkingRateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParkingRateViewHolder(ParkingRateRowBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingRateViewHolder holder, int position) {
        holder.bind(rateList.get(position));
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public class ParkingRateViewHolder extends RecyclerView.ViewHolder{

        private ParkingRateRowBinding binding;

        public ParkingRateViewHolder(@NonNull ParkingRateRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Rate rate) {
            binding.timeValue.setText(rate.getTime());
            if (rate.getShowPerHour()){
                binding.priceValue.setText(String.format("%s%s",rate.getPrice(),"$ /Hr"));
            }else{
                binding.priceValue.setText(String.format("%s $",rate.getPrice()));
            }
        }
    }
}
