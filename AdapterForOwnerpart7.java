package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AdapterForOwnerpart7 extends RecyclerView.Adapter<AdapterForOwnerpart7.ViewHolder> {
    interface Listener1 {
        void itemclickedforcheckingroomoccupied(int id,String s);
    }
    interface Listener2{

        void cancelbookingfortenant(int id,String s,String tenanttrano,String transactionid,String amount,String transferid);
    }

    Listener1 listener1;
    Listener2 listener2;
Uri[] imageuri;
String [] date;
String []time;

Long[] tenanttransactionno;
    String[] captions;
String transactionid[];
String amount[];
String transferid[];


    public AdapterForOwnerpart7(String[] captions, Uri[] imageuri,String[]date,String[]time,Long[] tenanttransactionno,String[]transactionid,String[]amount,String [] transferid) {
        this.captions = captions;
        this.imageuri=imageuri;
        this.date=date;
        this.time=time;
        this.tenanttransactionno=tenanttransactionno;
this.transferid=transferid;
        this.transactionid=transactionid;
        this.amount=amount;



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
        return captions.length;
    }

    public void setListener(Listener1 listener1,Listener2 listener2) {
        this.listener1 = listener1;
        this.listener2=listener2;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner7part7, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imv);
Button cancel=cardView.findViewById(R.id.cancel);
String s=captions[position];
String previousdate=date[position];
String previoustime=time[position];
String transferidd=transferid[position];
        String previousDateTimeStr = previousdate + " " + previoustime;

        // Define date format
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            // Parse previous date and time
            Date previousDateTime = dateTimeFormat.parse(previousDateTimeStr);

            // Get current date and time
            Date currentDateTime = new Date();

            // Calculate time difference in milliseconds
            long diffInMillis = currentDateTime.getTime() - previousDateTime.getTime();

            // Convert milliseconds to days
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            // Check if the difference is less than or more than 2 days
            if (diffInDays <= 2) {
                cancel.setVisibility(View.VISIBLE);
            } else {
                cancel.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

            Glide.with(cardView.getContext()).load(imageuri[position]).into(iv);
cardView.setEnabled(true);


String tenanttrano=String.valueOf(tenanttransactionno[position]);
String tranid=transactionid[position];
String amt=amount[position];







cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(listener2!=null)
        {
            listener2.cancelbookingfortenant(holder.getAdapterPosition(),s,tenanttrano,tranid,amt,transferidd);
        }
    }
});

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.itemclickedforcheckingroomoccupied(holder.getAdapterPosition(),s);
                }
            }
        });
    }
}
