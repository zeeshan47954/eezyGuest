//
//package com.example.bookandpostroom;
//
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class AdapterForOwnerpart33 extends RecyclerView.Adapter<AdapterForOwnerpart33.ViewHolder> {
//
//    interface Listener1 {
//        void iteminadapterclickedforbooking(int position, String s);
//    }
//
//    interface Listener2 {
//        void favoriteclicked(int position, String s);
//    }
//
//    private Listener1 listener1;
//    private Listener2 listener2;
//    public String[] captions;
//    public String[] roomname;
//    public String[] roomaddresscombined;
//    public String[] Genderr;
//    public String[] roomprice;
//    public String[] roomid;
//    public Uri[] imageurilist;
//    private Set<String> favoriteSet = new HashSet<>();
//
//    public AdapterForOwnerpart33(String[] captions, String[] roomname, String[] roomid,
//                                 String[] roomaddresscombined, String[] Genderr,
//                                 String[] roomprice, Uri[] imageurilist) {
//        this.captions = captions;
//        this.roomname = roomname;
//        this.roomid = roomid;
//        this.roomaddresscombined = roomaddresscombined;
//        this.Genderr = Genderr;
//        this.roomprice = roomprice;
//        this.imageurilist = imageurilist;
//    }
//
//    public void setFavorites(Set<String> favorites) {
//        this.favoriteSet = favorites != null ? favorites : new HashSet<>();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        final CardView cardView;
//        final TextView tv;
//        final TextView tv2;
//        final TextView gender;
//        final TextView rp;
//        final ImageView iv;
//        final CardView favoriteCard;
//        final ImageView favoriteIcon;
//        final Button details;
//
//        public ViewHolder(CardView v) {
//            super(v);
//            cardView = v;
//            tv = v.findViewById(R.id.tv);
//            tv2 = v.findViewById(R.id.tv2);
//            gender = v.findViewById(R.id.genders);
//            rp = v.findViewById(R.id.price);
//            iv = v.findViewById(R.id.imv);
//            favoriteCard = v.findViewById(R.id.favoritecard);
//            details = v.findViewById(R.id.details);
//            favoriteIcon = favoriteCard.findViewById(R.id.icondd);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return captions != null ? captions.length : 0;
//    }
//
//    public void setListener(Listener1 listener1, Listener2 listener2) {
//        this.listener1 = listener1;
//        this.listener2 = listener2;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.adapter33, parent, false);
//        return new ViewHolder(cv);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        if (position >= captions.length || position >= roomname.length ||
//                position >= roomaddresscombined.length || position >= Genderr.length ||
//                position >= roomprice.length || position >= imageurilist.length) {
//
//            // Loading state
//            holder.tv.setText("Loading...");
//            holder.tv2.setText("");
//            holder.rp.setText("");
//            holder.gender.setText("");
//            holder.iv.setImageResource(R.drawable.ic_launcher_background);
//            holder.favoriteIcon.setImageResource(R.drawable.favorite);
//            holder.favoriteCard.setOnClickListener(null);
//            holder.cardView.setOnClickListener(null);
//            return;
//        }
//
//        final String caption = captions[position];
//        final boolean isFavorite = favoriteSet.contains(caption);
//
//        // Set favorite icon
//        holder.favoriteIcon.setImageResource(isFavorite ? R.drawable.redheart : R.drawable.favorite);
//
//        // Favorite click listener
//        holder.favoriteCard.setOnClickListener(v -> {
//            boolean currentlyFavorite = favoriteSet.contains(caption);
//
//            if (listener2 != null) {
//                if (currentlyFavorite) {
//                    holder.favoriteIcon.setImageResource(R.drawable.favorite);
//                    favoriteSet.remove(caption);
//                } else {
//                    holder.favoriteIcon.setImageResource(R.drawable.redheart);
//                    favoriteSet.add(caption);
//                }
//                listener2.favoriteclicked(position, caption);
//            }
//        });
//
//        // Load image with optimizations
//        Uri uri = imageurilist[position];
//        if (uri != null) {
//            Glide.with(holder.iv.getContext())
//                    .load(uri)
//                    .override(720, 720)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .transition(DrawableTransitionOptions.withCrossFade(200))
//                    .thumbnail(0.1f)
//                    .into(holder.iv);
//        } else {
//            holder.iv.setImageResource(R.drawable.ic_launcher_background);
//        }
//
//        // Set text data
//        holder.tv.setText(roomname[position] != null ? roomname[position] : "");
//        holder.tv2.setText(roomaddresscombined[position] != null ? roomaddresscombined[position] : "");
//        holder.rp.setText(roomprice[position] != null ? "₹" + roomprice[position] : "₹0");
//
//        String genderText = Genderr[position];
//        holder.gender.setText(genderText != null && !genderText.isEmpty() ?
//                genderText : "No current occupants");
//
//        // Card click listener
//        holder.cardView.setOnClickListener(view -> {
//            if (listener1 != null) {
//                listener1.iteminadapterclickedforbooking(position, caption);
//            }
//        });
//    }
//
//    @Override
//    public void onViewRecycled(@NonNull ViewHolder holder) {
//        super.onViewRecycled(holder);
//        // Clear Glide requests to prevent memory leaks
//        Glide.with(holder.iv.getContext()).clear(holder.iv);
//        Glide.with(holder.favoriteIcon.getContext()).clear(holder.favoriteIcon);
//    }
//}
package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.HashSet;
import java.util.Set;

public class AdapterForOwnerpart33 extends RecyclerView.Adapter<AdapterForOwnerpart33.ViewHolder> {

    interface Listener1 {
        void iteminadapterclickedforbooking(int position, String s);
    }

    interface Listener2 {
        void favoriteclicked(int position, String s);
    }

    private Listener1 listener1;
    private Listener2 listener2;
    public String[] captions;
    public String[] roomname;
    public String[] roomaddresscombined;
    public String[] Genderr;
    public String[] roomprice;
    public Float[] averagescorearray;
    public Long[] reviewcountarray;
    public String[] roomid;
    public Uri[] imageurilist;

    public Boolean[] occupied;
    private Set<String> favoriteSet = new HashSet<>();

    public AdapterForOwnerpart33(String[] captions, String[] roomname, String[] roomid,
                                 String[] roomaddresscombined, String[] Genderr,
                                 String[] roomprice, Uri[] imageurilist,Float[] averagescorearray,Long[]reviewcount,Boolean [] occupied) {
        this.captions = captions;
        this.roomname = roomname;
        this.roomid = roomid;
        this.roomaddresscombined = roomaddresscombined;
        this.Genderr = Genderr;
        this.roomprice = roomprice;
        this.imageurilist = imageurilist;
        this.averagescorearray=averagescorearray;
        this.reviewcountarray=reviewcount;
        this.occupied=occupied;
    }

    public void setFavorites(Set<String> favorites) {
        this.favoriteSet = favorites != null ? favorites : new HashSet<>();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;
        final TextView tv;
        final TextView tv2;
        final TextView gender;
        final TextView rp;
        final ImageView iv;
        final ShimmerFrameLayout shimmerLayout;
        final CardView favoriteCard;
        final ImageView favoriteIcon;
        final Button details;
final RatingBar ratingBar;
final TextView reviewcount;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
            tv = v.findViewById(R.id.tv);
            tv2 = v.findViewById(R.id.tv2);
            gender = v.findViewById(R.id.genders);
            rp = v.findViewById(R.id.price);
            iv = v.findViewById(R.id.imv);
            shimmerLayout = v.findViewById(R.id.shimmer_layout);
            favoriteCard = v.findViewById(R.id.favoritecard);
            details = v.findViewById(R.id.details);
            favoriteIcon = favoriteCard.findViewById(R.id.icondd);
            ratingBar=v.findViewById(R.id.rating_bar);
            reviewcount=v.findViewById(R.id.review_count);
        }
    }

    @Override
    public int getItemCount() {
        return captions != null ? captions.length : 0;
    }

    public void setListener(Listener1 listener1, Listener2 listener2) {
        this.listener1 = listener1;
        this.listener2 = listener2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter33, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position >= captions.length || position >= roomname.length ||
                position >= roomaddresscombined.length || position >= Genderr.length ||
                position >= roomprice.length || position >= imageurilist.length) {

            // Loading state
            holder.tv.setText("Loading...");
            holder.tv2.setText("");
            holder.rp.setText("");
            holder.gender.setText("");
            holder.iv.setImageResource(R.drawable.ic_launcher_background);
            holder.shimmerLayout.startShimmer();
            holder.shimmerLayout.setVisibility(View.VISIBLE);
            holder.iv.setVisibility(View.GONE);
            holder.favoriteIcon.setImageResource(R.drawable.favorite);
            holder.favoriteCard.setOnClickListener(null);
            holder.cardView.setOnClickListener(null);
            return;
        }

        final String caption = captions[position];
        final boolean isFavorite = favoriteSet.contains(caption);

        // Set favorite icon
        holder.favoriteIcon.setImageResource(isFavorite ? R.drawable.redheart : R.drawable.favorite);

        // Favorite click listener
        holder.favoriteCard.setOnClickListener(v -> {
            boolean currentlyFavorite = favoriteSet.contains(caption);

            if (listener2 != null) {
                if (currentlyFavorite) {
                    holder.favoriteIcon.setImageResource(R.drawable.favorite);
                    favoriteSet.remove(caption);
                } else {
                    holder.favoriteIcon.setImageResource(R.drawable.redheart);
                    favoriteSet.add(caption);
                }
                listener2.favoriteclicked(position, caption);
            }
        });

        // Load image with shimmer effect
        Uri uri = imageurilist[position];
        if (uri != null) {
            // Image is available - hide shimmer, show image
            holder.shimmerLayout.stopShimmer();
            holder.shimmerLayout.setVisibility(View.GONE);
            holder.iv.setVisibility(View.VISIBLE);

            Glide.with(holder.iv.getContext())
                    .load(uri)
                    .override(720, 720)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(DrawableTransitionOptions.withCrossFade(200))
                    .thumbnail(0.1f)
                    .into(holder.iv);
        } else {
            // Image not yet loaded - show shimmer
            holder.iv.setVisibility(View.GONE);
            holder.shimmerLayout.setVisibility(View.VISIBLE);
            holder.shimmerLayout.startShimmer();
        }

        // Set text data
        holder.tv.setText(roomname[position] != null ? roomname[position] : "");
        holder.tv2.setText(roomaddresscombined[position] != null ? roomaddresscombined[position] : "");
        holder.rp.setText(roomprice[position] != null ? "₹" + roomprice[position] : "₹0");
Float score=averagescorearray[position];
if(score!=null)
{

    holder.ratingBar.setRating(score);
}
else{
holder.ratingBar.setRating(5f);

}
holder.reviewcount.setText(reviewcountarray[position]+" reviews");
        String genderText = Genderr[position];
        Boolean occ=occupied[position];
        if(occ) {
            holder.gender.setText(genderText);
        }
        else{
            holder.gender.setText("No current occupants");
        }
        // Card click listener
        holder.cardView.setOnClickListener(view -> {
            if (listener1 != null) {
                listener1.iteminadapterclickedforbooking(position, caption);
            }
        });
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        // Stop shimmer to prevent memory leaks
        if (holder.shimmerLayout != null) {
            holder.shimmerLayout.stopShimmer();
        }
        // Clear Glide requests to prevent memory leaks
        Glide.with(holder.iv.getContext()).clear(holder.iv);
        Glide.with(holder.favoriteIcon.getContext()).clear(holder.favoriteIcon);
    }
}