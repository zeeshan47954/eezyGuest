package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class AdapterForOwnerpart5 extends RecyclerView.Adapter<AdapterForOwnerpart5.ViewHolder> {

    interface Listener1 {
        void iteminadapterclickedforbookingfinalizeagree(int id, String s, String s2, String s3);
    }

    interface Listener2 {
        void iteminadapterclickedforbookingfinalizepaymentcancel(int id, String s, String s2, String s3);
    }

    interface Listener3 {
        void itemclickedcardviewfirstone(int id, String s, String s2);
    }

    interface Listener4 {
        void itemclickedsidebyside(int id, String s);
    }

    private Listener1 listener1;
    private Listener2 listener2;
    private Listener3 listener3;
    private Listener4 listener4;

    private String captions;
    private String captions2;
    private String captions3;
    private Uri imageuris;
    private Uri imageuris2;

    public AdapterForOwnerpart5(String captions, String captions2, String captions3, Uri imageuris, Uri imageuris2) {
        this.captions = captions;
        this.captions2 = captions2;
        this.captions3 = captions3;
        this.imageuris = imageuris;
        this.imageuris2 = imageuris2;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView1;
        private CardView cardView2;
        private Button okButton;
        private Button cancelButton;
        private ImageView imageView1;
        private ImageView imageView2;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView1 = itemView.findViewById(R.id.cv1);
            cardView2 = itemView.findViewById(R.id.cv2);
            okButton = itemView.findViewById(R.id.ok);
            cancelButton = itemView.findViewById(R.id.cancel);
            imageView1 = cardView1.findViewById(R.id.imv);
            imageView2 = cardView2.findViewById(R.id.imv2);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public void setListener(Listener1 listener1, Listener2 listener2, Listener3 listener3, Listener4 listener4) {
        this.listener1 = listener1;
        this.listener2 = listener2;
        this.listener3 = listener3;
        this.listener4 = listener4;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner5part5, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Get data
        String s = captions;
        String s2 = captions2;
        String s3 = captions3;
        Uri uri = imageuris;
        Uri uri2 = imageuris2;


        // Load images using Glide
        Glide.with(holder.itemView.getContext())
                .load(uri)
                .into(holder.imageView1);

        Glide.with(holder.itemView.getContext())
                .load(uri2)
                .into(holder.imageView2);

        // Set click listeners for buttons
        holder.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener1 != null) {
                    listener1.iteminadapterclickedforbookingfinalizeagree(holder.getAdapterPosition(), s, s2, s3);
                }
            }
        });

        holder.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener2 != null) {
                    listener2.iteminadapterclickedforbookingfinalizepaymentcancel(holder.getAdapterPosition(), s, s2, s3);
                }
            }
        });

        // Set click listeners for CardViews
        holder.cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener3 != null) {
                    listener3.itemclickedcardviewfirstone(holder.getAdapterPosition(),s,captions2);
                }
            }
        });

        holder.cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener4 != null) {
                    listener4.itemclickedsidebyside(holder.getAdapterPosition(),captions2);
                }
            }
        });
    }
}