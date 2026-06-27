package com.example.bookandpostroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterForOwnerpart88 extends RecyclerView.Adapter<AdapterForOwnerpart88.ViewHolder> {
    interface Listener1 {
        void iteminadapterforowneroccupiedforpaymentclicked(int id, long s, String transactionno);
    }

    interface Listener2 {
        void clickforstatuscheck(int id, long s, String roomid, String transactionno, String date, String time, String paymentid, String transfer);
    }

    long tt;
    Listener1 listener1;
    Listener2 listener2;

    private String roomid;
    private String transactionNo;

    String[] captions;
    String[] orders;
    String[] transactiondates;
    String[] transactiontimes;
    String[] payments;
    String[] transfer;

    public AdapterForOwnerpart88(String[] captions, String[] order, String[] transactiondates, String[] transactiontimes, String[] payments, String[] transfer) {
        this.captions = captions;
        this.orders = order;
        this.transactiondates = transactiondates;
        this.transactiontimes = transactiontimes;
        this.payments = payments;
        this.transfer = transfer;
    }

    /**
     * Method to update adapter data in real-time
     * Called when Firebase data changes
     */
    public void updateData(String[] newCaptions, String[] newOrders,
                           String[] newTransactiondates, String[] newTransactiontimes,
                           String[] newPayments, String[] newTransfer) {
        this.captions = newCaptions;
        this.orders = newOrders;
        this.transactiondates = newTransactiondates;
        this.transactiontimes = newTransactiontimes;
        this.payments = newPayments;
        this.transfer = newTransfer;

        // Notify RecyclerView that data has changed
        notifyDataSetChanged();
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
        return captions != null ? captions.length : 0;
    }

    public void setListener(Listener1 listener1, Listener2 listener2) {
        this.listener1 = listener1;
        this.listener2 = listener2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner6part6, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imv);
        TextView tv = cardView.findViewById(R.id.progresstext);
        TextView tv2 = cardView.findViewById(R.id.progresstext2);
        TextView datetv;
        TextView timetv;
        datetv=cardView.findViewById(R.id.date);
        timetv=cardView.findViewById(R.id.time);
        Button progress = cardView.findViewById(R.id.progress);
        Button details = cardView.findViewById(R.id.details);
        ProgressBar pb = cardView.findViewById(R.id.pb);

        String s = captions[position];
        String order = orders[position];
        String date = transactiondates[position];
        String time = transactiontimes[position];
        String paymentid = payments[position];
        String tr = transfer[position];
        datetv.setText("Dated: "+date);
        timetv.setText("time: "+time);
        roomid = s.split("transactionnumberstartshere")[0];
        transactionNo = s.split("transactionnumberstartshere")[1].split("status")[0];
        Long a = Long.parseLong(transactionNo);
        tt = a;
        String status = s.split("status")[1];
        String ownername = roomid.split("idstarts")[0];
        String id = roomid.split("idstarts")[1].split("idends")[0];

        if (status.equals("processing")) {
            tv.setText("Status:" + "{"+status+"}"+"it will take 3 days to credit amount to your bank account" + "\n" + "you will be notified when the amount is credited");
            progress.setVisibility(View.GONE);

        } else if (status.equals("settled")) {
            tv.setVisibility(View.GONE);
            tv2.setVisibility(View.VISIBLE);
            tv2.setText("settled into your bank account");

            progress.setVisibility(View.GONE);

            pb.setVisibility(View.GONE);
        } else {
            pb.setVisibility(View.GONE);
            iv.setImageResource(R.drawable.refunded);
            tv.setVisibility(View.GONE);
            tv2.setVisibility(View.VISIBLE);
            tv2.setText("refunded");

            progress.setVisibility(View.GONE);

        }

        progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener2 != null) {
                    listener2.clickforstatuscheck(holder.getAdapterPosition(), tt, roomid, order, date, time, paymentid, tr);
                }
            }
        });

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.iteminadapterforowneroccupiedforpaymentclicked(holder.getAdapterPosition(), tt, roomid);
                }
            }
        });
    }
}
/*public class AdapterForOwnerpart88 extends RecyclerView.Adapter<AdapterForOwnerpart88.ViewHolder> {
    interface Listener1 {
        void iteminadapterforowneroccupiedforpaymentclicked(int id,long s,String transactionno);
    }
    interface Listener2{

        void clickforstatuscheck(int id,long s,String roomid,String transactionno,String date,String time,String paymentid,String transfer);
    }

long tt;
    Listener1 listener1;
    Listener2 listener2;

private String roomid;
private String transactionNo;


    String[] captions;
    String[] orders;
    String[] transactiondates;
    String[] transactiontimes;
    String[] payments;
    String[] transfer;



    public AdapterForOwnerpart88(String[] captions, String [] order, String[] transactiondates, String[] transactiontimes, String[] payments, String[] transfer) {
        this.captions = captions;
        this.orders=order;
        this.transactiondates=transactiondates;
        this.transactiontimes=transactiontimes;
        this.payments=payments;
        this.transfer=transfer;


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
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner6part6, parent, false);
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





        String s=captions[position];
        String order=orders[position];
        String date=transactiondates[position];
        String time=transactiontimes[position];
        String paymentid=payments[position];
        String tr=transfer[position];



             roomid=s.split("transactionnumberstartshere")[0];
             transactionNo=s.split("transactionnumberstartshere")[1].split("status")[0];
             Long a=Long.parseLong(transactionNo);
              tt=a;
              String status=s.split("status")[1];
            String ownername = roomid.split("idstarts")[0];
            String id = roomid.split("idstarts")[1].split("idends")[0];
            if(status.equals("processing"))
            {
                tv.setText("Status:"+status+"\n"+"you will be notified when the amount is credited");
                progress.setVisibility(View.GONE);
pb.setVisibility(View.GONE);


            }
            else if(status.equals("settled"))
            {
                tv.setText("Status:"+status);
                progress.setVisibility(View.GONE);
                pb.setVisibility(View.GONE);


            }
           else
            {
iv.setImageResource(R.drawable.refunded);

                tv.setVisibility(View.VISIBLE);
                tv.setText("refunded");
                progress.setVisibility(View.GONE);




            }




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
}*/
