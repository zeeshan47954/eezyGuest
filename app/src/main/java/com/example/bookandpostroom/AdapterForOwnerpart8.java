package com.example.bookandpostroom;

import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

public class AdapterForOwnerpart8 extends RecyclerView.Adapter<AdapterForOwnerpart8.ViewHolder> {

    interface Listener1 {
        void itemClickedForDetails(int id, long s, String transactionNo);
    }

    interface Listener2 {
        void clickForStatusCheck(int id, long s, String roomId, String transactionNo, String date, String time, String paymentId, String transfer);
    }

    interface Listener3 {
        void clickForRefund(int id, String refund);
    }

    private Listener1 listener1;
    private Listener2 listener2;
    private Listener3 listener3;

    private final List<String> captions;
    private final List<String> orders;
    private final List<String> transactionDates;
    private final List<String> transactionTimes;
    private final List<String> payments;
    private final List<String> transfers;
    private final List<String> refunds;

    public AdapterForOwnerpart8(String[] captions, String[] orders, String[] dates, String[] times,
                                String[] payments, String[] transfers, String[] refunds) {
        this.captions = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.transactionDates = new ArrayList<>();
        this.transactionTimes = new ArrayList<>();
        this.payments = new ArrayList<>();
        this.transfers = new ArrayList<>();
        this.refunds = new ArrayList<>();

        for (String s : captions) this.captions.add(s != null ? s : "");
        for (String s : orders) this.orders.add(s != null ? s : "");
        for (String s : dates) this.transactionDates.add(s != null ? s : "");
        for (String s : times) this.transactionTimes.add(s != null ? s : "");
        for (String s : payments) this.payments.add(s != null ? s : "");
        for (String s : transfers) this.transfers.add(s != null ? s : "");
        for (String s : refunds) this.refunds.add(s != null ? s : "");
    }

    public void updateTransaction(int position, String status, String refundId) {
        if (position >= 0 && position < captions.size()) {
            String oldCaption = captions.get(position);
            String updatedCaption = oldCaption.split("status")[0] + "status" + status;
            captions.set(position, updatedCaption);
            refunds.set(position, refundId != null ? refundId : "");
            notifyItemChanged(position);
        }
    }

    public void updateData(String[] captions, String[] orders, String[] dates, String[] times,
                           String[] payments, String[] transfers, String[] refunds) {
        this.captions.clear();
        this.orders.clear();
        this.transactionDates.clear();
        this.transactionTimes.clear();
        this.payments.clear();
        this.transfers.clear();
        this.refunds.clear();

        for (String s : captions) this.captions.add(s != null ? s : "");
        for (String s : orders) this.orders.add(s != null ? s : "");
        for (String s : dates) this.transactionDates.add(s != null ? s : "");
        for (String s : times) this.transactionTimes.add(s != null ? s : "");
        for (String s : payments) this.payments.add(s != null ? s : "");
        for (String s : transfers) this.transfers.add(s != null ? s : "");
        for (String s : refunds) this.refunds.add(s != null ? s : "");

        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView iv;
        TextView tvStatus;
        ProgressBar pb;
        Button progress, details, refundProgress;
TextView datetv;
TextView timetv;
        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
            iv = v.findViewById(R.id.imv);
            tvStatus = v.findViewById(R.id.progresstext);
            pb = v.findViewById(R.id.pb);
            progress = v.findViewById(R.id.progress);
            details = v.findViewById(R.id.details);
            refundProgress = v.findViewById(R.id.refundprogress);
            datetv=v.findViewById(R.id.date);
            timetv=v.findViewById(R.id.time);
        }
    }

    @Override
    public int getItemCount() {
        return captions.size();
    }

    public void setListener(Listener1 listener1, Listener2 listener2, Listener3 listener3) {
        this.listener1 = listener1;
        this.listener2 = listener2;
        this.listener3 = listener3;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_for_owner6part10, parent, false);
        return new ViewHolder(cv);
    }
    long transactionNo;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String caption = captions.get(position);
        String order = orders.get(position);
        String date = transactionDates.get(position);
        String time = transactionTimes.get(position);
        String paymentId = payments.get(position);
        String transferId = transfers.get(position);
        String refundId = refunds.get(position);

        String roomId = caption.split("transactionnumberstartshere")[0];

        try {
            transactionNo = Long.parseLong(caption.split("transactionnumberstartshere")[1].split("status")[0]);
        } catch (NumberFormatException e) {
            transactionNo = 0;
            Log.e("Adapter", "Error parsing transaction number", e);
        }
        String status = caption.split("status")[1];

        holder.tvStatus.setVisibility(View.VISIBLE);
        holder.pb.setVisibility(View.GONE);
        holder.progress.setVisibility(View.VISIBLE);
        holder.refundProgress.setVisibility(View.GONE);
        holder.iv.setImageResource(R.drawable.paymentapproved);
        holder.datetv.setText("Dated: "+date);
        holder.timetv.setText("time: "+time);

        switch (status) {
            case "processing":
                holder.tvStatus.setText("Status: " +"{"+status+"}"+"\npayment has been captured,may take 3 days to settle into the owner's bank account");
                holder.progress.setVisibility(View.VISIBLE);
                holder.pb.setVisibility(View.VISIBLE);
                break;
            case "settled":
                holder.tvStatus.setText("Status: " + status);
                holder.progress.setVisibility(View.GONE);
                holder.pb.setVisibility(View.GONE);
                break;
            case "refund initiated":
                holder.iv.setImageResource(R.drawable.refunded);
                holder.tvStatus.setText("Refund initiated, you will be notified soon.");
                holder.progress.setVisibility(View.GONE);
                holder.pb.setVisibility(View.VISIBLE);
                break;
            case "refunded":
                holder.iv.setImageResource(R.drawable.refunded);
                holder.tvStatus.setText("Status: " + status);
                holder.progress.setVisibility(View.GONE);
                holder.pb.setVisibility(View.GONE);
                break;
        }

        holder.details.setOnClickListener(v -> {
            if (listener1 != null)
                listener1.itemClickedForDetails(holder.getAdapterPosition(), transactionNo, roomId);
        });

        holder.progress.setOnClickListener(v -> {
            if (listener2 != null)
                listener2.clickForStatusCheck(holder.getAdapterPosition(), transactionNo, roomId, order, date, time, paymentId, transferId);
        });

        holder.refundProgress.setOnClickListener(v -> {
            if (listener3 != null)
                listener3.clickForRefund(holder.getAdapterPosition(), refundId);
        });
    }
}

//package com.example.bookandpostroom;
//
//import static java.security.AccessController.getContext;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//public class AdapterForOwnerpart8 extends RecyclerView.Adapter<AdapterForOwnerpart8.ViewHolder> {
//    interface Listener1 {
//        void iteminadapterforowneroccupiedforpaymentclicked(int id,long s,String transactionno);
//    }
//    interface Listener2{
//
//        void clickforstatuscheck(int id,long s,String roomid,String transactionno,String date,String time,String paymentid,String transfer);
//    }
//    interface Listener3{
//
//        void clickforrefund(int id,String refund);
//    }
//long tt;
//    Listener1 listener1;
//    Listener2 listener2;
//    Listener3 listener3;
//private String roomid;
//private String transactionNo;
//
//
//    String[] captions;
//    String[] orders;
//    String[] transactiondates;
//    String[] transactiontimes;
//    String[] payments;
//    String[] transfer;
//    String[] refund;
//
//
//    public AdapterForOwnerpart8(String[] captions,String [] order,String[] transactiondates,String[] transactiontimes,String[] payments,String[] transfer,String[] refund) {
//        this.captions = captions;
//        this.orders=order;
//        this.transactiondates=transactiondates;
//        this.transactiontimes=transactiontimes;
//        this.payments=payments;
//        this.transfer=transfer;
//        this.refund=refund;
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
//    public void setListener(Listener1 listener1,Listener2 listener2,Listener3 listener3) {
//        this.listener1 = listener1;
//        this.listener2=listener2;
//        this.listener3=listener3;
//
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
//        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner6part10, parent, false);
//        return new ViewHolder(cv);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, final int position) {
//        CardView cardView = holder.cardView;
//        ImageView iv = cardView.findViewById(R.id.imv);
//TextView tv=cardView.findViewById(R.id.progresstext);
//
//Button progress=cardView.findViewById(R.id.progress);
//Button details=cardView.findViewById(R.id.details);
//ProgressBar pb=cardView.findViewById(R.id.pb);
//
//Button refundprogress=cardView.findViewById(R.id.refundprogress);
//
//
//
//        String s=captions[position];
//        String order=orders[position];
//        String date=transactiondates[position];
//        String time=transactiontimes[position];
//        String paymentid=payments[position];
//        String tr=transfer[position];
//
//String refundid=refund[position];
//
//             roomid=s.split("transactionnumberstartshere")[0];
//             transactionNo=s.split("transactionnumberstartshere")[1].split("status")[0];
//             Long a=Long.parseLong(transactionNo);
//              tt=a;
//              String status=s.split("status")[1];
//            String ownername = roomid.split("idstarts")[0];
//            String id = roomid.split("idstarts")[1].split("idends")[0];
//            if(status.equals("processing"))
//            {
//                tv.setText("Status:"+status);
//                progress.setVisibility(View.GONE);
//pb.setVisibility(View.GONE);
//
//
//            }
//            else if(status.equals("settled"))
//            {
//                tv.setText("Status:"+status);
//                progress.setVisibility(View.GONE);
//                pb.setVisibility(View.GONE);
//                refundprogress.setVisibility(View.GONE);
//
//            }
//           else if(status.equals("refund initiated") )
//            {
//iv.setImageResource(R.drawable.refunded);
//
//                tv.setVisibility(View.VISIBLE);
//                tv.setText("refund initiated,may take some time,you will be notified dont worry");
//                progress.setVisibility(View.GONE);
//                pb.setVisibility(View.VISIBLE);
//
//
//
//            }
//            else if(status.equals("refunded"))
//            {
//                iv.setImageResource(R.drawable.refunded);
//
//                tv.setVisibility(View.VISIBLE);
//                tv.setText("Status:"+status);
//                progress.setVisibility(View.GONE);
//                pb.setVisibility(View.GONE);
//
//            }
//
//
//            refundprogress.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//      if(listener3!=null)
//      {
//          listener3.clickforrefund(holder.getAdapterPosition(), refundid);
//      }
//                }
//            });
//
//progress.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        if(listener2!=null)
//        {
//          listener2.clickforstatuscheck(holder.getAdapterPosition(),tt,roomid,order,date,time,paymentid,tr);
//
//        }
//    }
//});
//
//
//
//
//        details.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener1 != null) {
//                    listener1.iteminadapterforowneroccupiedforpaymentclicked(holder.getAdapterPosition(),tt,roomid);
//                }
//            }
//        });
//    }
//}
