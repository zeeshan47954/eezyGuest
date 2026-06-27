package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class roomrequests extends Fragment {

private List<String> ss=new ArrayList<>();
private long x;
private long index=0;

interface Listenerroom{
     void itemclickedforroominfo(int position,String s);
}
    private FirebaseAuth firebaseAuth;
Listenerroom listenerroom;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        RecyclerView rv=(RecyclerView) inflater.inflate(R.layout.fragment_roomrequests, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if(account!=null)
        {
            String name=account.getDisplayName();
            String id=account.getUid();
            Dialog progressDialog = new Dialog(getContext());
            progressDialog.setCancelable(false); // Disable dismissal on touch outside

            // Inflate the custom layout
            View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog_layout, null);

            // Set the custom view to the dialog
            progressDialog.setContentView(view);

            // Show the dialog
            progressDialog.show();
            DatabaseReference df= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("requests");
            df.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long s=snapshot.getValue(long.class);
                    x=s;

                    if(s>0)
                    {
                        DatabaseReference df= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("roomrequestnumbers");
                        df.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                             for(DataSnapshot children:snapshot.getChildren())
                             { index++;
                                 String s=children.getValue(String.class);
                                 ss.add(s);

                                 if(index==x)
                                 {
                                     progressDialog.dismiss();
                                     String []sss=ss.toArray(new String[0]);
                                     AdapterForOwnerpart10 adapter = new AdapterForOwnerpart10(sss);
                                     rv.setAdapter(adapter);
                                     GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                                     rv.setLayoutManager(glm);
                                     adapter.setListener(new AdapterForOwnerpart10.Listener1() {
                                         @Override
                                         public void itemclickedbyadmin(int id, String s) {
                                             if(listenerroom!=null)
                                             {
                                                 listenerroom.itemclickedforroominfo(id,s);
                                             }
                                         }
                                     });





                                 }




                             }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });






                    }
                    else{

                        progressDialog.dismiss();
                       /* AdapterForemptyvalue adapter = new AdapterForemptyvalue();
                        rv.setAdapter(adapter);
                        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                        rv.setLayoutManager(glm);*/

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });





        }



    return rv;}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listenerroom = (Listenerroom) context;
    }
}