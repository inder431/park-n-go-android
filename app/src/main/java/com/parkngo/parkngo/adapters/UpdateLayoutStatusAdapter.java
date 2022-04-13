package com.parkngo.parkngo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nex3z.togglebuttongroup.button.OnCheckedChangeListener;
import com.parkngo.parkngo.data.layout.Layout;
import com.parkngo.parkngo.databinding.UpdateStatusLayoutRowBinding;
import com.parkngo.parkngo.repository.ParkRepository;

import java.util.List;

public class UpdateLayoutStatusAdapter extends RecyclerView.Adapter<UpdateLayoutStatusAdapter.UpdateLayoutStatusViewHolder> {

    private List<Layout> layouts;
    private Context context;
    private boolean[] status;


    public UpdateLayoutStatusAdapter(List<Layout> layouts, Context context) {
        this.layouts = layouts;
        this.context = context;
        status = new boolean[layouts.size()];
    }

    @NonNull
    @Override
    public UpdateLayoutStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UpdateLayoutStatusViewHolder(UpdateStatusLayoutRowBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UpdateLayoutStatusViewHolder holder, int position) {
        holder.bind(layouts.get(position),position);
    }

    @Override
    public int getItemCount() {
        return layouts.size();
    }

    public class UpdateLayoutStatusViewHolder extends RecyclerView.ViewHolder{
        private UpdateStatusLayoutRowBinding binding;
        public UpdateLayoutStatusViewHolder(@NonNull UpdateStatusLayoutRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Layout layout,int position) {
            binding.layoutTitle.setText(layout.layoutTitle);
            if (layout.active){
                status[position] = true;
            }else{
                status[position] = false;
            }

            if (status[position]){
                binding.activeToggle.setChecked(true);
                binding.activeToggle.setText("OK");
            }else{
                binding.activeToggle.setChecked(false);
                binding.activeToggle.setText("X");
            }

            binding.activeToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public <T extends View & Checkable> void onCheckedChanged(T view, boolean isChecked) {
                    ParkRepository.getInstance().updateStatusOfLayout(layout.layoutId,isChecked,success->{
                        if (success){
                            if (isChecked){
                                binding.activeToggle.setText("OK");
                            }else{
                                binding.activeToggle.setText("X");
                            }
                            status[position] = true;
                        }else{
                            status[position] = false;
                        }
                    });
                }
            });
        }
    }
}
