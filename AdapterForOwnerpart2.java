package com.example.bookandpostroom;

import static java.security.AccessController.getContext;

import android.net.Uri;
import android.util.Log;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdapterForOwnerpart2 extends RecyclerView.Adapter<AdapterForOwnerpart2.ViewHolder> {
    interface Listener1 {
        void iteminadapterforowneroccupiedclicked(int id,String s,String y);
    }

    Listener1 listener1;

    String[] captions;
    Uri[] photos;
    String[] tenantname;



    public AdapterForOwnerpart2(String [] captions,String []tenantname,Uri [] photos) {
        this.captions = captions;
        this.tenantname=tenantname;
        this.photos=photos;

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

    public void setListener(Listener1 listener1) {
        this.listener1 = listener1;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner2part2, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imv);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(cardView.getContext());
        String name= account.getDisplayName();
        String id=account.getId();

        Uri aa=photos[position];
        String tenantinfo=tenantname[position];


        Glide.with(cardView.getContext())
                .load(aa)
                .into(iv);




        // Use Glide to load the image

        String s=captions[position];
        TextView tv = cardView.findViewById(R.id.tv);
        if(s!=null)
        {
String last10Digits;
            last10Digits = s.substring(s.length() - 10);
            tv.setText(last10Digits);

        }
        else {
            tv.setText("room" + position);
        }




        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.iteminadapterforowneroccupiedclicked(holder.getAdapterPosition(),s,tenantinfo);
                }
            }
        });
    }
}
