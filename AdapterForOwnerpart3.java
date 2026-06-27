package com.example.bookandpostroom;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.util.Arrays;

public class AdapterForOwnerpart3 extends RecyclerView.Adapter<AdapterForOwnerpart3.ViewHolder> {
    interface Listener1 {
        void iteminadapterclickedforbooking(int position, String s);
    }

    private final Object lock = new Object();
    private Listener1 listener1;
    public Context context;
    public String[] captions;
    public Uri[] imageuris;
    public String[] roomname;
    public String[] roomaddresscombined;
    public String[] Genderr;
    public String[] roomprice;
    public String[] roomid;

    public AdapterForOwnerpart3(Context context, String[] captions, Uri[] imageuris,
                                String[] roomname, String[] roomaddresscombined,
                                String[] Genderr, String[] roomprice, String[] roomid) {
        this.context = context;
        this.captions = captions;
        this.imageuris = imageuris;
        this.roomname = roomname;
        this.roomaddresscombined = roomaddresscombined;
        this.Genderr = Genderr;
        this.roomprice = roomprice;
        this.roomid = roomid;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    @Override
    public int getItemCount() {

            return captions != null ? captions.length : 0;

    }

    public void setListener(Listener1 listener1) {
        this.listener1 = listener1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter33, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

            CardView cardView = holder.cardView;
            if (position >= getItemCount()) return;

            ImageView iv = cardView.findViewById(R.id.imv);
            TextView tv = cardView.findViewById(R.id.tv);
            TextView tv2 = cardView.findViewById(R.id.tv2);
            TextView gender = cardView.findViewById(R.id.genders);
            TextView rp = cardView.findViewById(R.id.price);

            Uri uri = imageuris[position];
            String roomname1 = roomname[position];
            String roomaddresscombined1 = roomaddresscombined[position];
            String gendersss = Genderr[position];
            String rpp = roomprice[position];

            tv.setText(roomname1);
            tv2.setText(roomaddresscombined1);
            rp.setText(rpp);
            gender.setText(gendersss.isEmpty() ? "No current occupants" : gendersss);

            Glide.with(cardView.getContext())
                    .load(uri)
                    .override(720, 720)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .thumbnail(0.1f)
                    .into(iv);

            cardView.setOnClickListener(view -> {
                if (listener1 != null && position < captions.length) {
                    listener1.iteminadapterclickedforbooking(position, captions[position]);
                }
            });

    }

    public void removeItem(int position) {

            if (position < 0 || position >= getItemCount()) return;

            String[] newCaptions = removeFromArray(captions, position);
            Uri[] newImageuris = removeFromArray(imageuris, position);
            String[] newRoomname = removeFromArray(roomname, position);
            String[] newRoomaddresscombined = removeFromArray(roomaddresscombined, position);
            String[] newGenderr = removeFromArray(Genderr, position);
            String[] newRoomprice = removeFromArray(roomprice, position);
            String[] newRoomid = removeFromArray(roomid, position);

            captions = newCaptions;
            imageuris = newImageuris;
            roomname = newRoomname;
            roomaddresscombined = newRoomaddresscombined;
            Genderr = newGenderr;
            roomprice = newRoomprice;
            roomid = newRoomid;

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
            notifyDataSetChanged();

    }

    private <T> T[] removeFromArray(T[] array, int position) {
        if (array == null || position < 0 || position >= array.length) return array;

        @SuppressWarnings("unchecked")
        T[] newArray = (T[]) java.lang.reflect.Array.newInstance(
                array.getClass().getComponentType(), array.length - 1);

        System.arraycopy(array, 0, newArray, 0, position);
        System.arraycopy(array, position + 1, newArray, position, array.length - position - 1);

        return newArray;
    }

    public int getItemIndexById(String id) {
        synchronized (lock) {
            if (roomid == null || id == null) return -1;

            for (int i = 0; i < roomid.length; i++) {
                if (id.equals(roomid[i])) return i;
            }
            return -1;
        }
    }
}