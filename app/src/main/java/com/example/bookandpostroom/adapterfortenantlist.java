package com.example.bookandpostroom;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class adapterfortenantlist extends RecyclerView.Adapter<adapterfortenantlist.ViewHolder> {
    interface RequestListeneradapter {
        void functionforrequest1(int id, String s);
    }

    interface RequestListeneradapter2 {
        void deletetenantaftertenant(int id, String s);
    }

    private String[] captions;
    RequestListeneradapter listener1;
    RequestListeneradapter2 listener2;
    String[] roomids;
    String[] roomnames;
    String[] address;
    Uri[] imageuri;

    public adapterfortenantlist(String[] roomids, String[] names, String[] address, Uri[] imageuri) {
        this.roomids = roomids;
        this.roomnames = names;
        this.address = address;
        this.imageuri = imageuri;
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
        return roomids != null ? roomids.length : 0;
    }

    public void setListener(RequestListeneradapter listener1, RequestListeneradapter2 listener2) {
        this.listener1 = listener1;
        this.listener2 = listener2;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutfortenantdetailadapter, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (roomids == null || position < 0 || position >= roomids.length) {
            return;
        }

        CardView cardView = holder.cardView;
        ImageView iv = cardView.findViewById(R.id.imv);
        TextView tv = cardView.findViewById(R.id.name);
        ImageButton ib = cardView.findViewById(R.id.delete);

        String s1 = roomnames[position];
        tv.setText(s1);

        if (imageuri[position] != null) {
            Glide.with(cardView.getContext()).load(imageuri[position]).into(iv);
        } else {
            // Set placeholder image if URI is null
            iv.setImageResource(R.drawable.error_placeholder);
        }

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener2 != null) {
                    listener2.deletetenantaftertenant(holder.getAdapterPosition(), roomids[position]);
                }
            }
        });

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener1 != null) {
                    listener1.functionforrequest1(holder.getAdapterPosition(), roomids[position]);
                }
            }
        });
    }

    public void removeItem(int position) {
        if (position >= 0 && position < getItemCount() && roomids != null) {
            // Create new arrays without the removed item
            String[] newRoomIds = removeElement(roomids, position);
            String[] newRoomNames = removeElement(roomnames, position);
            String[] newAddress = removeElement(address, position);
            Uri[] newImageUri = removeElement(imageuri, position);

            // Update the arrays
            roomids = newRoomIds;
            roomnames = newRoomNames;
            address = newAddress;
            imageuri = newImageUri;

            // Notify adapter
            notifyItemRemoved(position);
        }
    }

    // Helper method to remove element from array
    private String[] removeElement(String[] array, int position) {
        if (array == null || position < 0 || position >= array.length) {
            return array;
        }

        String[] result = new String[array.length - 1];
        System.arraycopy(array, 0, result, 0, position);
        System.arraycopy(array, position + 1, result, position, array.length - position - 1);
        return result;
    }

    private Uri[] removeElement(Uri[] array, int position) {
        if (array == null || position < 0 || position >= array.length) {
            return array;
        }

        Uri[] result = new Uri[array.length - 1];
        System.arraycopy(array, 0, result, 0, position);
        System.arraycopy(array, position + 1, result, position, array.length - position - 1);
        return result;
    }
}
//package com.example.bookandpostroom;
//
//import android.net.Uri;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//
//public class adapterfortenantlist extends RecyclerView.Adapter<adapterfortenantlist.ViewHolder>{
//interface  RequestListeneradapter{
//
// public void functionforrequest1(int id,String s);
//}
//    interface  RequestListeneradapter2{
//
//        public void deletetenantaftertenant(int id,String s);
//    }
//
//private String[] captions;
//RequestListeneradapter listener1;
//RequestListeneradapter2 listener2;
//String[] roomids;
//String []roomnames;
//String[]address;
//Uri [] imageuri;
//
//public adapterfortenantlist(String []roomids,String []names, String []address, Uri[] imageuri) {
//this.roomids=roomids;
//this.roomnames=names;
//this.address=address;
//this.imageuri=imageuri;
//
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
//        return roomids.length;
//    }
//
//    public void setListener(RequestListeneradapter listener1,RequestListeneradapter2 listener2) {
//        this.listener1 = listener1;
//        this.listener2=listener2;
//
//
//    }
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewtype) {
//        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.layoutfortenantdetailadapter, parent, false);
//        return new ViewHolder(cv);
//    }
//
//    @Override
//    public void onBindViewHolder(ViewHolder holder, final int position) {
//
//
//        CardView cardView = holder.cardView;
//        ImageView iv = cardView.findViewById(R.id.imv);
//        TextView tv=cardView.findViewById(R.id.name);
//
//        ImageButton ib=cardView.findViewById(R.id.delete);
//
//        String s1=roomnames[position];
//        tv.setText(s1);
//        Glide.with(cardView.getContext()).load(imageuri[position]).into(iv);
//
//        ib.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(listener2!=null)
//                {
//
//                    listener2.deletetenantaftertenant(holder.getAdapterPosition(),roomids[position]);
//                }
//            }
//        });
//
//
//        cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (listener1 != null) {
//                    listener1.functionforrequest1(holder.getAdapterPosition(),roomids[position]);
//                }
//            }
//        });
//    }
//}
