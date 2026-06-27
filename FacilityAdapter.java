package com.example.bookandpostroom;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class FacilityAdapter extends RecyclerView.Adapter<FacilityAdapter.FacilityViewHolder> {

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

    @Override
    public void onBindViewHolder(@NonNull FacilityViewHolder holder, int position) {
        holder.facilityText.setText(facilities.get(position));

        holder.removeButton.setOnClickListener(v -> {
            if (position >= 0 && position < facilities.size()) {
                facilities.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, facilities.size()); // Update remaining items
            }
        });
    }

    @Override
    public int getItemCount() {
        return facilities.size();
    }

    public static class FacilityViewHolder extends RecyclerView.ViewHolder {
        TextView facilityText;
        Button removeButton;

        public FacilityViewHolder(View itemView) {
            super(itemView);
            facilityText = itemView.findViewById(R.id.facility_text);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }
}
