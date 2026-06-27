package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AdapterForemptyvalue extends RecyclerView.Adapter<AdapterForemptyvalue.ViewHolder> {

    private int  a;

    public AdapterForemptyvalue(int a) {
this.a=a;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }



    @Override
    public AdapterForemptyvalue.ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_adapterforemptyvalues, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        CardView cardView = holder.cardView;
        TextView tv=cardView.findViewById(R.id.textView);
        if(a==1)
        {
            tv.setText("Here ,All the rooms that u have uploaded will appear");
        }
        else if(a==2)
        {
          tv.setText("Here,All the rooms that u have rented out will appear");
        }
        else if(a==3)
        {
         tv.setText("Here,All the transactions that have been made will appear");

        }
        else if(a==4)
        {
            tv.setText("here,All the rooms available will appear");

        }
        else if(a==5)
        {

            tv.setText("here,All the rooms that You have booked will appear");
        }
        else if(a==6)
        {
            tv.setText("here,All the transaction that have been made will appear");

        }
       else if(a==-1)
        {

            tv.setText("empty");
        }
       else if(a==7)
        {
            tv.setText("here,All the rooms that you have booked will appear");

        }
       else if(a==8)
        {
           tv.setText("here,All the transactions that u have made will appear");

        }
       else if(a==9)
        {
            tv.setText("here all the rooms that you requested for but have not been approved will appear");
        }
       else if(a==10)
        {

            tv.setText("here,All the requests that you have recieved will appear");
        }
       else if(a==11)
        {

            tv.setText("Here,All the requests and other messages from owners will appear");
        }
        }

    }

