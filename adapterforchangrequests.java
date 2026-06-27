package com.example.bookandpostroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class adapterforchangrequests extends RecyclerView.Adapter<adapterforchangrequests.ViewHolder> {
    interface Listener1 {
        void roomdetailsinadapter(int id,String s);
    }

    interface Listener2{

        void doneinadapter(int id,String s);
    }
    Listener1 listener1;
Listener2 listener2;
    List<String> captions=new ArrayList<>();


    public adapterforchangrequests(List captions) {
       this.captions=captions;
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
        return captions.size();
    }

    public void setListener(Listener1 listener1,Listener2 listener2) {
        this.listener1 = listener1;
        this.listener2=listener2;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_for_owner10part10, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;
Button ok=cardView.findViewById(R.id.ok);
        Button details=cardView.findViewById(R.id.details);

        String s = captions.get(position);



        // Use Glide to load the image

ok.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(listener2!=null)
        {

            listener2.doneinadapter(holder.getAdapterPosition(),s);
        }
    }
});
details.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(listener1!=null)
        {

            listener1.roomdetailsinadapter(holder.getAdapterPosition(),s);
        }
    }
});



    }
    public void removeItem(int position) {
        if (position >= 0 && position < captions.size()) {
            captions.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, captions.size()); // Optional: keeps adapter positions correct
        }
    }


}
