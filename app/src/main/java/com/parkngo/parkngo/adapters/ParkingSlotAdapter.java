package com.parkngo.parkngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.parkngo.parkngo.databinding.ParkingRowBinding;
import com.parkngo.parkngo.interfaces.LoadData;

import java.util.Locale;

public class ParkingSlotAdapter extends RecyclerView.Adapter<ParkingSlotAdapter.ParkingSlotViewHolder> {

    private boolean[] slots;
    private Context context;
    private int selectedPosition=-1;
    private int lastSelectedPosition=-1;
    private LoadData<Integer> loadData;
    private int columns;
    private String layoutTitle;

    public ParkingSlotAdapter(boolean[] slots, Context context,String layoutTitle,int columns,LoadData<Integer> loadData) {
        this.slots = slots;
        this.context = context;
        this.loadData = loadData;
        this.columns = columns;
        this.layoutTitle = layoutTitle;
    }

    @NonNull
    @Override
    public ParkingSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParkingSlotViewHolder(ParkingRowBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingSlotViewHolder holder, int position) {
        holder.bind(slots[position],position,layoutTitle,columns);
    }

    @Override
    public int getItemCount() {
        return slots.length;
    }

    public class ParkingSlotViewHolder extends RecyclerView.ViewHolder{
        private ParkingRowBinding binding;
        public ParkingSlotViewHolder(@NonNull ParkingRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(boolean slot,int position,String layoutTitle,int columns) {

            binding.seatCode.setText(String.format(Locale.getDefault(),"%s-%d%d",layoutTitle,position/columns,position%columns));

            if (selectedPosition == position){
                binding.selected.setVisibility(View.VISIBLE);
                binding.seatCode.setVisibility(View.GONE);
            }else{
                binding.seatCode.setVisibility(View.VISIBLE);
                binding.selected.setVisibility(View.GONE);
            }

            if (!slot){
                binding.carParked.setVisibility(View.INVISIBLE);
            }else{
                binding.carParked.setVisibility(View.VISIBLE);
                binding.seatCode.setVisibility(View.GONE);
            }

            binding.parkingLayout.setOnClickListener(v->{
                if (!slot){
                    selectedPosition = getAdapterPosition();
                    loadData.onDataLoaded(selectedPosition);
                    if (selectedPosition == lastSelectedPosition){
                        selectedPosition = -1;
                    }else if(lastSelectedPosition == -1)
                        lastSelectedPosition = selectedPosition;
                    else {
                        notifyItemChanged(lastSelectedPosition);
                        lastSelectedPosition = selectedPosition;
                    }
                    notifyItemChanged(selectedPosition);
                }
            });
        }
    }
}
