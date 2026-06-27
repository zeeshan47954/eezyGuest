package com.example.bookandpostroom;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterForOwnerpart66 extends RecyclerView.Adapter<AdapterForOwnerpart66.ViewHolder> {
    interface Listener1 {
        void iteminadapterforowneroccupiedforpaymentclicked(int id,String s);
    }
    interface Listener2{
        void buttonclickedforstatuschecking(String transactionid,String transferid);
    }

    Listener1 listener1;
Listener2 listener2;

    String[] captions;
    String[] order;


    public AdapterForOwnerpart66(String[] captions) {
        this.captions = captions;

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
     /*   ImageView iv = cardView.findViewById(R.id.imv);
TextView madeby=cardView.findViewById(R.id.madeby);
TextView timedate=cardView.findViewById(R.id.timedate);
TextView status=cardView.findViewById(R.id.status);
Button checkstatus=cardView.findViewById(R.id.status);






        String s=captions[position];

        if(s!=null) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(cardView.getContext());
            String  ownername=account.getDisplayName();
            String id= account.getId();
            DatabaseReference dbr= FirebaseDatabase.getInstance().getReference("google").child(ownername).child(id).child("owner").child("transactionsrecievedno").child("transaction"+s);
            dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String status1=snapshot.child("status").getValue(String.class);
                   String transferid=snapshot.child("transferid").getValue(String.class);
                   String transactionid=snapshot.child("transactionid").getValue(String.class);
                    if(status1.equals("onhold"))
                    {
                       status.setVisibility(View.VISIBLE);
                       checkstatus.setVisibility(View.VISIBLE);

                       checkstatus.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View v) {
                               if(listener2!=null)
                               {
                                   listener2.buttonclickedforstatuschecking(transactionid,transferid);
                               }

                           }
                       });
                    }
                    else{
                        status.setVisibility(View.GONE);
                        checkstatus.setVisibility(View.GONE);


                    }

                    madeby.setText("made by:"+snapshot.child("tenantname").getValue(String.class));
                    timedate.setText("on:"+snapshot.child("transactionDate").getValue(String.class)+" at "+snapshot.child("transactionTime").getValue(String.class));



                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }



        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.iteminadapterforowneroccupiedforpaymentclicked(holder.getAdapterPosition(),s);
                }
            }
        });*/
    }
}
