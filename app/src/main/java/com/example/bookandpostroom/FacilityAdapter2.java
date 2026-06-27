package com.example.bookandpostroom;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FacilityAdapter2 extends RecyclerView.Adapter<FacilityAdapter2.FacilityViewHolder> {
    interface  Removelistener2{

        public void delete2(int position);
    }
    Removelistener2 removelistener;
    private List<String> facilities;

    public FacilityAdapter2(List<String> facilities) {
        this.facilities = facilities;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_item, parent, false);
        return new FacilityViewHolder(view);
    }
    public void setListener(Removelistener2 removelistener)
    {this.removelistener=removelistener;
    }

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        holder.facilityText.setText(facilities.get(position));

        holder.removeButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                // Remove from adapter's list
                facilities.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                notifyItemRangeChanged(adapterPosition, facilities.size());

                // Notify activity about the change (just decrement counter)
                if (removelistener != null) {
                    removelistener.delete2(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView facilityText;
        ImageButton removeButton;

        public FacilityViewHolder(View itemView) {
            super(itemView);
            facilityText = itemView.findViewById(R.id.facility_text);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }

}
