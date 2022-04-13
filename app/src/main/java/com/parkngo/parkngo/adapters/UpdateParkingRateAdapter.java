package com.parkngo.parkngo.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parkngo.parkngo.data.rates.Rate;
import com.parkngo.parkngo.databinding.FragmentUpdateParkingRatesBinding;
import com.parkngo.parkngo.databinding.ParkingRateRowBinding;
import com.parkngo.parkngo.databinding.UpdateParkingRateRowBinding;
import com.parkngo.parkngo.interfaces.LoadData;
import com.parkngo.parkngo.interfaces.UpdatePrice;

import java.util.List;

public class UpdateParkingRateAdapter extends RecyclerView.Adapter<UpdateParkingRateAdapter.UpdateParkingRateViewHolder> {

    private List<Rate> rateList;
    private Context context;
    private UpdatePrice updatePrice;
    private static final String TAG = UpdateParkingRateAdapter.class.getSimpleName();

    public UpdateParkingRateAdapter(List<Rate> rateList, Context context, UpdatePrice updatePrice) {
        this.rateList = rateList;
        this.context = context;
        this.updatePrice = updatePrice;
    }

    @NonNull
    @Override
    public UpdateParkingRateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UpdateParkingRateViewHolder(UpdateParkingRateRowBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateParkingRateViewHolder holder, int position) {
        holder.bind(rateList.get(position));

        holder.binding.updatePriceValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable.toString())){
                    Log.e(TAG, "afterTextChanged: price updated to :"+ editable);
                    updatePrice.onPriceUpdated(rateList.get(position),Double.parseDouble(editable.toString()),false);
                }else{
                    Log.e(TAG, "afterTextChanged: delete scenario"+ editable);
                    updatePrice.onPriceUpdated(rateList.get(position),0d,true);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rateList.size();
    }

    public class UpdateParkingRateViewHolder extends RecyclerView.ViewHolder{

        private UpdateParkingRateRowBinding binding;

        public UpdateParkingRateViewHolder(@NonNull UpdateParkingRateRowBinding binding) {
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
