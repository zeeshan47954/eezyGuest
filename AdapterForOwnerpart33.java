package com.example.bookandpostroom;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.HashSet;
import java.util.Set;

public class AdapterForOwnerpart33 extends RecyclerView.Adapter<AdapterForOwnerpart33.ViewHolder> {
    interface Listener1 {
        void iteminadapterclickedforbooking(int position, String s);
    }
    interface Listener2{

        void favoriteclicked(int position, String s);
    }

    private final Object lock = new Object();
    private Listener1 listener1;
    private Listener2 listener2;
    public String[] captions;
    public String[] roomname;
    public String[] roomaddresscombined;
    public String[] Genderr;
    public String[] roomprice;
    public String[] roomid;
    public Uri[] imageurilist;
    private Set<String> favoriteSet = new HashSet<>();

    public AdapterForOwnerpart33(String[] captions,String [] roomname,String [] roomid,String [] roomaddresscombined,String [] Genderr,String [] roomprice,Uri [] imageurilist) {

        this.captions = captions;
        this.roomname=roomname;
        this.roomid=roomid;
        this.roomaddresscombined=roomaddresscombined;
        this.Genderr=Genderr;
        this.roomprice=roomprice;
this.imageurilist=imageurilist;

    }
    public void setFavorites(Set<String> favorites) {
        this.favoriteSet = favorites;
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

            return captions != null ? captions.length : 0;

    }

    public void setListener(Listener1 listener1,Listener2 listener2) {
        this.listener1 = listener1;
        this.listener2=listener2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter33, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        TextView tv = cardView.findViewById(R.id.tv);
        TextView tv2 = cardView.findViewById(R.id.tv2);
        TextView gender = cardView.findViewById(R.id.genders);
        TextView rp = cardView.findViewById(R.id.price);
        ImageView iv = cardView.findViewById(R.id.imv);
        CardView cv = cardView.findViewById(R.id.favoritecard);
        Button details = cardView.findViewById(R.id.details);
        ImageView ivv = cv.findViewById(R.id.icondd);

        // Check if all arrays have data for this position
        if (position < roomname.length &&
                position < roomaddresscombined.length &&
                position < Genderr.length &&
                position < roomprice.length &&
                position < imageurilist.length &&
                position < captions.length) {

            String caption = captions[position];

            // ✅ SET INITIAL FAVORITE ICON STATE BASED ON DATABASE
            boolean isFavorite = favoriteSet.contains(caption);
            if (isFavorite) {
                Glide.with(cv.getContext())
                        .load(R.drawable.redheart)
                        .into(ivv);
            } else {
                Glide.with(cv.getContext())
                        .load(R.drawable.favorite)
                        .into(ivv);
            }

            // Set click listener for favorite button
            cv.setOnClickListener(v -> {
                boolean isFav = favoriteSet.contains(caption);

                if (listener2 != null) {
                    if (!isFav) {
                        // Change image to red heart immediately
                        Glide.with(cv.getContext())
                                .load(R.drawable.redheart)
                                .into(ivv);

                        favoriteSet.add(caption);
                        listener2.favoriteclicked(position, caption);

                    } else {
                        // Change image back to normal favorite
                        Glide.with(cv.getContext())
                                .load(R.drawable.favorite)
                                .into(ivv);

                        favoriteSet.remove(caption);
                        listener2.favoriteclicked(position, caption);
                    }
                }
            });

            // Load image
            Uri uri = imageurilist[position];
            Glide.with(cardView.getContext())
                    .load(uri)
                    .override(720, 720)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .thumbnail(0.1f)
                    .into(iv);

            // Set text data
            String roomname1 = roomname[position];
            String roomaddresscombined1 = roomaddresscombined[position];
            String gendersss = Genderr[position];
            String rpp = roomprice[position];

            tv.setText(roomname1);
            tv2.setText(roomaddresscombined1);
            rp.setText("₹" + rpp);
            gender.setText(gendersss != null && !gendersss.isEmpty() ? gendersss : "No current occupants");

            // Set click listener for main card
            cardView.setOnClickListener(view -> {
                if (listener1 != null) {
                    listener1.iteminadapterclickedforbooking(position, caption);
                }
            });

        } else {
            // Handle case where data is not yet loaded
            // Show placeholder or loading state
            tv.setText("Loading...");
            tv2.setText("");
            rp.setText("");
            gender.setText("");

            // Load placeholder image or clear previous image
            iv.setImageResource(R.drawable.ic_launcher_background);

            // Set default favorite icon for loading state
            Glide.with(cv.getContext())
                    .load(R.drawable.favorite)
                    .into(ivv);

            // Disable click listeners for incomplete items
            cv.setOnClickListener(null);
            cardView.setOnClickListener(null);
        }
    }


}