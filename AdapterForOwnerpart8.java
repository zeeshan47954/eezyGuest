package com.example.bookandpostroom;

import static java.security.AccessController.getContext;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class AdapterForOwnerpart8 extends RecyclerView.Adapter<AdapterForOwnerpart8.ViewHolder> {
    interface Listener1 {
        void iteminadapterforowneroccupiedforpaymentclicked(int id,long s,String transactionno);
    }
    interface Listener2{

        void clickforstatuscheck(int id,long s,String roomid,String transactionno,String date,String time,String paymentid,String transfer);
    }
    interface Listener3{

        void clickforrefund(int id,String refund);
    }
long tt;
    Listener1 listener1;
    Listener2 listener2;
    Listener3 listener3;
private String roomid;
private String transactionNo;


    String[] captions;
    String[] orders;
    String[] transactiondates;
    String[] transactiontimes;
    String[] payments;
    String[] transfer;
    String[] refund;


    public AdapterForOwnerpart8(String[] captions,String [] order,String[] transactiondates,String[] transactiontimes,String[] payments,String[] transfer,String[] refund) {
        this.captions = captions;
        this.orders=order;
        this.transactiondates=transactiondates;
        this.transactiontimes=transactiontimes;
        this.payments=payments;
        this.transfer=transfer;
        this.refund=refund;

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

    public void setListener(Listener1 listener1,Listener2 listener2,Listener3 listener3) {
        this.listener1 = listener1;
        this.listener2=listener2;
        this.listener3=listener3;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner6part10, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imv);
TextView tv=cardView.findViewById(R.id.progresstext);

Button progress=cardView.findViewById(R.id.progress);
Button details=cardView.findViewById(R.id.details);
ProgressBar pb=cardView.findViewById(R.id.pb);

Button refundprogress=cardView.findViewById(R.id.refundprogress);



        String s=captions[position];
        String order=orders[position];
        String date=transactiondates[position];
        String time=transactiontimes[position];
        String paymentid=payments[position];
        String tr=transfer[position];

String refundid=refund[position];

             roomid=s.split("transactionnumberstartshere")[0];
             transactionNo=s.split("transactionnumberstartshere")[1].split("status")[0];
             Long a=Long.parseLong(transactionNo);
              tt=a;
              String status=s.split("status")[1];
            String ownername = roomid.split("idstarts")[0];
            String id = roomid.split("idstarts")[1].split("idends")[0];
            if(status.equals("processing"))
            {
                tv.setText("Status:"+status);
                progress.setVisibility(View.GONE);
pb.setVisibility(View.GONE);


            }
            else if(status.equals("settled"))
            {
                tv.setText("Status:"+status);
                progress.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);
                refundprogress.setVisibility(View.GONE);

            }
           else if(status.equals("refund initiated") )
            {
iv.setImageResource(R.drawable.refunded);

                tv.setVisibility(View.VISIBLE);
                tv.setText("refund initiated,may take some time,you will be notified dont worry");
                progress.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);



            }
            else if(status.equals("refunded"))
            {
                iv.setImageResource(R.drawable.refunded);

                tv.setVisibility(View.VISIBLE);
                tv.setText("Status:"+status);
                progress.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);

            }


            refundprogress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
      if(listener3!=null)
      {
          listener3.clickforrefund(holder.getAdapterPosition(), refundid);
      }
                }
            });

progress.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(listener2!=null)
        {
          listener2.clickforstatuscheck(holder.getAdapterPosition(),tt,roomid,order,date,time,paymentid,tr);

        }
    }
});




        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.iteminadapterforowneroccupiedforpaymentclicked(holder.getAdapterPosition(),tt,roomid);
                }
            }
        });
    }
}
