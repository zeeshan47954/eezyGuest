package com.example.bookandpostroom;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdapterForReview extends RecyclerView.Adapter<AdapterForReview.ViewHolder> {



String[] reviewer;
String [] review;
String [] dated;
Float[] rating;

    public AdapterForReview(String[] reviewer,String[] review,Float[]rating,String[]dated) {


this.reviewer=reviewer;
this.review=review;
this.rating=rating;
this.dated=dated;

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
        return reviewer.length;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapteforreview, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardView cv=holder.cardView;
        TextView tv=cv.findViewById(R.id.reviewer);
        TextView tv2=cv.findViewById(R.id.review);
        RatingBar rb=cv.findViewById(R.id.rating_bar);
        TextView tv3=cv.findViewById(R.id.review_date);
        tv3.setText(dated[position]);
        rb.setRating(rating[position]);
        tv.setText(reviewer[position]);
        tv2.setText(review[position]);




    }
}

