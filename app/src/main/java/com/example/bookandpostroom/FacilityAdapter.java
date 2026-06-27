package com.example.bookandpostroom;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {
    interface  Removelistener{

        public void delete(int position);
    }
    Removelistener removelistener;
    private List<String> facilities;

    public FacilityAdapter(List<String> facilities) {
        this.facilities = facilities;
    }

    @NonNull
    @Override
    public FacilityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.facility_item, parent, false);
        return new FacilityViewHolder(view);
    }
    public void setListener(Removelistener removelistener)
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
                    removelistener.delete(adapterPosition);
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
