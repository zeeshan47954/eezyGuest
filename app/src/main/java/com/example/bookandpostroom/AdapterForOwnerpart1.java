package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class AdapterForOwnerpart1 extends RecyclerView.Adapter<AdapterForOwnerpart1.ViewHolder> {

    interface Listener1 {
        void iteminadapterforownerclicked(int id, String s);
    }

    interface Listener2 {
        void itemdeletedinadapterforowner(int id, String s);
    }

    public String[] captions;
    public Uri[] imageuris;
    public Boolean[] occupied;
    private Listener1 listener1;
    private Listener2 listener2;

    // Optimized Glide options - preload for better performance
    private static final RequestOptions glideOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .skipMemoryCache(false)
            .override(400, 400); // Reduce image size for faster loading

    public AdapterForOwnerpart1(String[] captions, Uri[] imageuris, Boolean[] occupied) {
        this.captions = captions;
        this.imageuris = imageuris;
        this.occupied = occupied;
        setHasStableIds(true); // Enable stable IDs for better performance
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iv;
        ImageButton ib;
        TextView tv;
        ProgressBar pb;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
            iv = v.findViewById(R.id.imv);
            ib = v.findViewById(R.id.delete);
            tv = v.findViewById(R.id.tv);
            pb = v.findViewById(R.id.pdd);
        }
    }

    @Override
    public int getItemCount() {
        return captions.length;
    }

    @Override
    public long getItemId(int position) {
        // Return stable ID based on caption hashcode
        return captions[position] != null ? captions[position].hashCode() : position;
    }

    public void setListener(Listener1 listener1, Listener2 listener2) {
        this.listener1 = listener1;
        this.listener2 = listener2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adpter_for_owner1part1, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String caption = captions[position];
        Uri uri = imageuris[position];
        boolean roomoccupied = occupied[position];

        if (caption != null) {
            String[] parts = caption.split("idstarts");
            if (parts.length >= 2) {
                // Show views
                holder.iv.setVisibility(View.VISIBLE);
                holder.pb.setVisibility(View.GONE);
                holder.cardView.setEnabled(true);
                holder.tv.setVisibility(View.VISIBLE);

                // Optimized image loading with thumbnail for faster perceived loading
                if (uri != null) {
                    Glide.with(holder.cardView.getContext())
                            .load(uri)
                            .thumbnail(0.1f) // Load 10% thumbnail first for speed
                            .apply(glideOptions)
                            .into(holder.iv);
                }

                // Show/hide delete button based on occupancy
                holder.ib.setVisibility(roomoccupied ? View.GONE : View.VISIBLE);

                // Optimized string extraction - avoid substring when possible
                String last10Digits;
                int len = caption.length();
                if (len <= 10) {
                    last10Digits = caption;
                } else {
                    last10Digits = caption.substring(len - 10);
                }
                holder.tv.setText(last10Digits);
            }
        } else {
            holder.tv.setText("room" + position);
        }

        // Optimized click listeners - avoid creating new objects
        holder.ib.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener2 != null) {
                listener2.itemdeletedinadapterforowner(pos, caption);
            }
        });

        holder.cardView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && listener1 != null) {
                listener1.iteminadapterforownerclicked(pos, caption);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        // Clear Glide request to prevent memory leaks
        Glide.with(holder.cardView.getContext()).clear(holder.iv);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        // Additional cleanup when view is detached
        holder.cardView.setOnClickListener(null);
        holder.ib.setOnClickListener(null);
    }
}

/*package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdapterForOwnerpart1 extends RecyclerView.Adapter<AdapterForOwnerpart1.ViewHolder> {

    interface Listener1 {
        void iteminadapterforownerclicked(int id, String s);
    }

    interface Listener2 {
        void itemdeletedinadapterforowner(int id, String s);
    }

    public  String[] captions;
    public  Uri[] imageuris;

    public Boolean[] occupied;
    private Listener1 listener1;
    private Listener2 listener2;
    private String last10Digits;

    public AdapterForOwnerpart1(String[] captions, Uri[] imageuris,Boolean[] occupied) {
        this.captions = captions;
        this.imageuris = imageuris;
        this.occupied=occupied;

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public int getItemCount() {
        return captions.length;
    }

    public void setListener(Listener1 listener1, Listener2 listener2) {
        this.listener1 = listener1;
        this.listener2 = listener2;
    }

    @NonNull
    @Override
    public AdapterForOwnerpart1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adpter_for_owner1part1, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imv);
        ImageButton ib = cardView.findViewById(R.id.delete);
        TextView tv = cardView.findViewById(R.id.tv);
        ProgressBar pb = cardView.findViewById(R.id.pdd);

        String caption = captions[position];
        Uri uri = imageuris[position];

        boolean roomoccupied=occupied[position];

        if (caption != null) {
            String[] parts = caption.split("idstarts");
            if (parts.length >= 2) {
                String ownername = parts[0];
                String id = parts[1].split("idends")[0];




                        iv.setVisibility(View.VISIBLE);
                        pb.setVisibility(View.GONE);
                        cardView.setEnabled(true);
                        tv.setVisibility(View.VISIBLE);
                        Glide.with(cardView.getContext())
                                .load(uri)
                                .thumbnail(0.1f) // Loads a lower-quality version first
                                .diskCacheStrategy(DiskCacheStrategy.ALL) // Caches all image versions
                                .into(iv);


                        if (roomoccupied) {
                            ib.setVisibility(View.GONE);
                        } else {
                            ib.setVisibility(View.VISIBLE);
                        }



                last10Digits = caption.substring(caption.length() - 10);
                tv.setText(last10Digits);
            }
        } else {
            tv.setText("room" + position);
        }

        ib.setOnClickListener(v -> {
            if (listener2 != null) {
                listener2.itemdeletedinadapterforowner(holder.getAdapterPosition(), caption);
            }
        });

        // Use Glide to load the image


        cardView.setOnClickListener(view -> {
            if (listener1 != null) {
                listener1.iteminadapterforownerclicked(holder.getAdapterPosition(), caption);
            }
        });
    }
}*/
