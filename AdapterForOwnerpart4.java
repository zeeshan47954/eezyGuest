package com.example.bookandpostroom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class AdapterForOwnerpart4 extends RecyclerView.Adapter<AdapterForOwnerpart4.ViewHolder> {
    interface Listener1 {
        void iteminadapterclickedforbookingfinalize(int id,String s);
    }

    Listener1 listener1;
interface  Listener2{

    void iteminadapterclickedforbookingfinalizepayment(int id,String s);

}
Listener2 listener2;
    String[] captions;
    Uri[] imageuris;
Long [] k;
    public AdapterForOwnerpart4(String[] captions, Uri[] imageuris,Long []k) {
        this.captions = captions;
        this.imageuris = imageuris;
        this.k=k;
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
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner4part4, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imv);
        Uri uri = imageuris[position];
        ImageButton ib=cardView.findViewById(R.id.paymentbutton);
        ProgressBar pb1=cardView.findViewById(R.id.pb);
        long a=k[position];
        TextView tv = cardView.findViewById(R.id.tv);
        String s=captions[position];
        if(a==0)
        {
ib.setVisibility(View.GONE);

            pb1.setVisibility(View.VISIBLE);
        }
        else if(a==1){
tv.setVisibility(View.GONE);
        ib.setVisibility(View.VISIBLE);
        pb1.setVisibility(View.GONE);
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener1 != null) {
                    listener2.iteminadapterclickedforbookingfinalizepayment(holder.getAdapterPosition(),s);
                }
            }
        });
        }

        // Use Glide to load the image
        Glide.with(cardView.getContext())
                .load(uri)
                .into(iv);




        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.iteminadapterclickedforbookingfinalize(holder.getAdapterPosition(),s);
                }
            }
        });
    }
}
