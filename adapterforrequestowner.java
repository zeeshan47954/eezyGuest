package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class adapterforrequestowner extends RecyclerView.Adapter<adapterforrequestowner.ViewHolder>{
interface  RequestListeneradapter{

 public void functionforrequest1(int id,String s);
}
private String[] captions;
RequestListeneradapter listener1;
String[] roomids;
String []roomnames;
Uri [] imageuri;

public adapterforrequestowner(String []roomids,String []roomnames,Uri[] imageuri) {
this.roomids=roomids;
this.roomnames=roomnames;
this.imageuri=imageuri;


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
        return roomids.length;
    }

    public void setListener(RequestListeneradapter listener1) {
        this.listener1 = listener1;


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutforadapterrequest, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imv);
        TextView tv=cardView.findViewById(R.id.roomname);
        String s=roomids[position];
        String s1=roomnames[position];
        tv.setText(s1);
        Glide.with(cardView.getContext()).load(imageuri[position]).into(iv);



        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.functionforrequest1(holder.getAdapterPosition(),s);
                }
            }
        });
    }
}
