package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdapterForOwnerpart7 extends RecyclerView.Adapter<AdapterForOwnerpart7.ViewHolder> {

    interface Listener1 {
        void itemclickedforcheckingroomoccupied(int id, String s);
    }

    interface Listener2 {
        void cancelbookingfortenant(int id, String s, String tenanttrano, String transactionid, String amount, String transferid, String date, String time);
    }

    private Listener1 listener1;
    private Listener2 listener2;

    private String[] captions;
    private Uri[] imageuri;
    private String[] date;
    private String[] time;
    private Long[] tenanttransactionno;
    private String[] transactionid;
    private String[] amount;
    private String[] transferid;
    private String[] refundinitiated;

    public AdapterForOwnerpart7(String[] captions, Uri[] imageuri, String[] date, String[] time,
                                Long[] tenanttransactionno, String[] transactionid, String[] amount, String[] transferid,String[]refundinitiated) {
        this.captions = captions;
        this.imageuri = imageuri;
        this.date = date;
        this.time = time;
        this.tenanttransactionno = tenanttransactionno;
        this.transactionid = transactionid;
        this.amount = amount;
        this.transferid = transferid;
        this.refundinitiated=refundinitiated;
    }

    public void setListener(Listener1 listener1, Listener2 listener2) {
        this.listener1 = listener1;
        this.listener2 = listener2;
    }

    // Method to update data and refresh adapter
    public void updateData(String[] captions, Uri[] imageuri, String[] date, String[] time,
                           Long[] tenanttransactionno, String[] transactionid, String[] amount, String[] transferid,String [] refundinitiated) {
        this.captions = captions;
        this.imageuri = imageuri;
        this.date = date;
        this.time = time;
        this.tenanttransactionno = tenanttransactionno;
        this.transactionid = transactionid;
        this.amount = amount;
        this.transferid = transferid;
        this.refundinitiated=refundinitiated;

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iv;
        Button cancel;
TextView tv;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
            iv = v.findViewById(R.id.imv);
            cancel = v.findViewById(R.id.cancel);
            tv=v.findViewById(R.id.tv);
        }
    }

    @Override
    public int getItemCount() {
        return captions != null ? captions.length : 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_for_owner7part7, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (captions == null || position >= captions.length) return;

        String s = captions[position];
        String previousDateStr = date[position];
        String previousTimeStr = time[position];
        String transferidd = transferid[position];
        String tenanttrano = String.valueOf(tenanttransactionno[position]);
        String tranid = transactionid[position];
        String amt = amount[position];
String requested=refundinitiated[position];
        if (requested.equals("yes")) {
            holder.cancel.setVisibility(View.GONE);
            holder.iv.setVisibility(View.GONE);

            holder.cardView.setEnabled(false);
            FrameLayout fl1 = holder.cardView.findViewById(R.id.fl1);
            TextView tv2 = holder.cardView.findViewById(R.id.tv2);
            tv2.setText("refund initiated successfully");
            fl1.setVisibility(View.VISIBLE);

            FrameLayout fl2 = holder.cardView.findViewById(R.id.fl2);
            fl2.setVisibility(View.GONE); // ✅ IMPORTANT
        }

        // Load image with Glide
        Glide.with(holder.cardView.getContext())
                .load(imageuri[position])
                .into(holder.iv);

        // Check if cancellation is allowed (within 2 days)
        boolean canCancel = checkCancellationEligibility(previousDateStr, previousTimeStr);
        holder.cancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);

        // Set click listeners
        holder.cancel.setOnClickListener(v -> {
            if (listener2 != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener2.cancelbookingfortenant(adapterPosition, s, tenanttrano, tranid, amt, transferidd, previousDateStr, previousTimeStr);
                }
            }
        });

        holder.cardView.setOnClickListener(v -> {
            if (listener1 != null) {
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener1.itemclickedforcheckingroomoccupied(adapterPosition, s);
                }
            }
        });
    }

    private boolean checkCancellationEligibility(String dateStr, String timeStr) {
        if (dateStr == null || timeStr == null) {
            return false;
        }

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date transactionDateTime = dateTimeFormat.parse(dateStr + " " + timeStr);
            if (transactionDateTime == null) {
                return false;
            }

            Date currentDateTime = new Date();
            long diffInMillis = currentDateTime.getTime() - transactionDateTime.getTime();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            // Can cancel within 2 days
            return diffInDays <= 2;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }
}
//package com.example.bookandpostroom;
//
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class AdapterForOwnerpart7 extends RecyclerView.Adapter<AdapterForOwnerpart7.ViewHolder> {
//
//    interface Listener1 {
//        void itemclickedforcheckingroomoccupied(int id, String s);
//    }
//
//    interface Listener2 {
//        void cancelbookingfortenant(int id, String s, String tenanttrano, String transactionid, String amount, String transferid,String date,String time);
//    }
//
//    private Listener1 listener1;
//    private Listener2 listener2;
//
//    private String[] captions;
//    private Uri[] imageuri;
//    private String[] date;
//    private String[] time;
//    private Long[] tenanttransactionno;
//    private String[] transactionid;
//    private String[] amount;
//    private String[] transferid;
//
//    public AdapterForOwnerpart7(String[] captions, Uri[] imageuri, String[] date, String[] time,
//                                Long[] tenanttransactionno, String[] transactionid, String[] amount, String[] transferid) {
//        this.captions = captions;
//        this.imageuri = imageuri;
//        this.date = date;
//        this.time = time;
//        this.tenanttransactionno = tenanttransactionno;
//        this.transactionid = transactionid;
//        this.amount = amount;
//        this.transferid = transferid;
//    }
//
//    public void setListener(Listener1 listener1, Listener2 listener2) {
//        this.listener1 = listener1;
//        this.listener2 = listener2;
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        CardView cardView;
//
//        public ViewHolder(CardView v) {
//            super(v);
//            cardView = v;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return captions.length;
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
//        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.adapter_for_owner7part7, parent, false);
//        return new ViewHolder(cv);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, int position) {
//        CardView cardView = holder.cardView;
//        ImageView iv = cardView.findViewById(R.id.imv);
//        Button cancel = cardView.findViewById(R.id.cancel);
//
//        String s = captions[position];
//        String previousDateStr = date[position];
//        String previousTimeStr = time[position];
//        String transferidd = transferid[position];
//
//        Glide.with(cardView.getContext()).load(imageuri[position]).into(iv);
//
//        // Check if cancellation is allowed (within 2 days)
//        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date previousDateTime = dateTimeFormat.parse(previousDateStr + " " + previousTimeStr);
//            Date currentDateTime = new Date();
//            long diffInDays = (currentDateTime.getTime() - previousDateTime.getTime()) / (1000 * 60 * 60 * 24);
//
//            cancel.setVisibility(diffInDays <= 2 ? View.VISIBLE : View.GONE);
//        } catch (ParseException e) {
//            e.printStackTrace();
//            cancel.setVisibility(View.GONE);
//        }
//
//        String tenanttrano = String.valueOf(tenanttransactionno[position]);
//        String tranid = transactionid[position];
//        String amt = amount[position];
//
//        cancel.setOnClickListener(v -> {
//            if (listener2 != null) {
//                listener2.cancelbookingfortenant(holder.getAdapterPosition(), s, tenanttrano, tranid, amt, transferidd,previousDateStr,previousTimeStr);
//            }
//        });
//
//        cardView.setOnClickListener(v -> {
//            if (listener1 != null) {
//                listener1.itemclickedforcheckingroomoccupied(holder.getAdapterPosition(), s);
//            }
//        });
//    }
//}


//package com.example.bookandpostroom;

//
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class AdapterForOwnerpart7 extends RecyclerView.Adapter<AdapterForOwnerpart7.ViewHolder> {
//    interface Listener1 {
//        void itemclickedforcheckingroomoccupied(int id,String s);
//    }
//    interface Listener2{
//
//        void cancelbookingfortenant(int id,String s,String tenanttrano,String transactionid,String amount,String transferid);
//    }
//
//    Listener1 listener1;
//    Listener2 listener2;
//Uri[] imageuri;
//String [] date;
//String []time;
//
//Long[] tenanttransactionno;
//    String[] captions;
//String transactionid[];
//String amount[];
//String transferid[];
//
//
//    public AdapterForOwnerpart7(String[] captions, Uri[] imageuri,String[]date,String[]time,Long[] tenanttransactionno,String[]transactionid,String[]amount,String [] transferid) {
//        this.captions = captions;
//        this.imageuri=imageuri;
//        this.date=date;
//        this.time=time;
//        this.tenanttransactionno=tenanttransactionno;
//this.transferid=transferid;
//        this.transactionid=transactionid;
//        this.amount=amount;
//
//
//
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        private CardView cardView;
//
//        public ViewHolder(CardView v) {
//            super(v);
//            cardView = v;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return captions.length;
//    }
//
//    public void setListener(Listener1 listener1,Listener2 listener2) {
//        this.listener1 = listener1;
//        this.listener2=listener2;
//
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
//        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner7part7, parent, false);
//        return new ViewHolder(cv);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, final int position) {
//        CardView cardView = holder.cardView;
//        ImageView iv = cardView.findViewById(R.id.imv);
//Button cancel=cardView.findViewById(R.id.cancel);
//String s=captions[position];
//String previousdate=date[position];
//String previoustime=time[position];
//String transferidd=transferid[position];
//        String previousDateTimeStr = previousdate + " " + previoustime;
//
//        // Define date format
//        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//        try {
//            // Parse previous date and time
//            Date previousDateTime = dateTimeFormat.parse(previousDateTimeStr);
//
//            // Get current date and time
//            Date currentDateTime = new Date();
//
//            // Calculate time difference in milliseconds
//            long diffInMillis = currentDateTime.getTime() - previousDateTime.getTime();
//
//            // Convert milliseconds to days
//            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
//
//            // Check if the difference is less than or more than 2 days
//            if (diffInDays <= 2) {
//                cancel.setVisibility(View.VISIBLE);
//            } else {
//                cancel.setVisibility(View.GONE);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//            Glide.with(cardView.getContext()).load(imageuri[position]).into(iv);
//cardView.setEnabled(true);
//
//
//String tenanttrano=String.valueOf(tenanttransactionno[position]);
//String tranid=transactionid[position];
//String amt=amount[position];
//
//
//
//
//
//
//
//cancel.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        if(listener2!=null)
//        {
//            listener2.cancelbookingfortenant(holder.getAdapterPosition(),s,tenanttrano,tranid,amt,transferidd);
//        }
//    }
//});
//
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener1 != null) {
//                    listener1.itemclickedforcheckingroomoccupied(holder.getAdapterPosition(),s);
//                }
//            }
//        });
//    }
//}
