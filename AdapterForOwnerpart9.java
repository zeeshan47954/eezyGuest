package com.example.bookandpostroom;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdapterForOwnerpart9 extends RecyclerView.Adapter<AdapterForOwnerpart9.ViewHolder> {
    interface Listener1 {
        void whencardisclickedforpaymentrequests(int id,String transactionid,String transactionmadeby,String transactionmadeto,String ownername,String ownernumber,String tenantname,String tenantnumber,String amount,String time,String date);
    }


    Listener1 listener1;
    interface Listener2{
        void whencardisclickedforpaymentrequestsbuttonisclicked(int id,String transactionid,String transactionmadeby,String transactionmadeto,String ownername,String ownernumber,String tenantname,String tenantnumber,String amount,String time,String date,Long ownertranno,Long tenanttrano);

    }
    Listener2 listener2;
private String roomid;
private String transactionNo;


    private String[] transactionmadeby;

    private String[] transactionmadeto;
    private String[] ownername;
    private String[] ownernumber;
    private String[] tenantname;
    private String[] tenantnumber;
    private String[] amount;
private String[] transactionid;
private  String[] time;
private String [] date;
private Long[] ownertranno;
private Long[]tenanttranno;
    public AdapterForOwnerpart9(String []transactionid,String[]transactionmadeby,String[]transactionmadeto,String[]ownername,String[]ownernumber,String[]tenantname,String[]tenantnumber,String[]amount,String [] time,String [] date,Long [] ownertranno,Long[] tenanttranno) {


        this.transactionmadeby=transactionmadeby;
        this.transactionmadeto=transactionmadeto;
        this.ownername=ownername;
        this.ownernumber=ownernumber;
        this.tenantname=tenantname;
        this.tenantnumber=tenantnumber;
        this.amount=amount;
this.transactionid=transactionid;
this.time=time;
this.date=date;
this.ownertranno=ownertranno;
this.tenanttranno=tenanttranno;

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
        return transactionmadeby.length;
    }

    public void setListener(Listener1 listener1,Listener2 listener2) {
        this.listener1 = listener1;
this.listener2=listener2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner9part9, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        Button accept = cardView.findViewById(R.id.accept);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(listener2!=null)
                   listener2.whencardisclickedforpaymentrequestsbuttonisclicked(holder.getAdapterPosition(),transactionid[position],transactionmadeby[position],transactionmadeto[position],ownername[position],ownernumber[position],tenantname[position],tenantnumber[position],amount[position],time[position],date[position],ownertranno[position],tenanttranno[position]);
            }
        });









        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.whencardisclickedforpaymentrequests(holder.getAdapterPosition(),transactionid[position],transactionmadeby[position],transactionmadeto[position],ownername[position],ownernumber[position],tenantname[position],tenantnumber[position],amount[position],time[position],date[position]);
                }
            }
        });
    }
}
