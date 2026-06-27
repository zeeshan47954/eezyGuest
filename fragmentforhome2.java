package com.example.bookandpostroom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class fragmentforhome2 extends Fragment implements RefreshListener {
    interface Listenerhome1 {
        void itemclicked2(int position,String s,String j);
    }


    private long countroomoccupied;
    private RecyclerView rv;
    private String name;
    private String id;
    ProgressBar pb;
    private  DatabaseReference dbrroom;
    Listenerhome1 listenerhome1;

private long x;
private long index=0;
    @Override
    public void onRefresh() {
        countroomoccupied=0;
        index = 0;
        x=0;
        // Reset the index counter
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged(); // Notify adapter
        }
        pb.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
        load();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view = inflater.inflate(R.layout.fragment_fragmentforhome2, container, false);
       rv=view.findViewById(R.id.rv);
       pb=view.findViewById(R.id.pb);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
             name = account.getDisplayName();
                    id = account.getId();
                    load();

        }

        return  view;
    }
    long start=0;
    long end;
public void load(){
    dbrroom = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner").child("rooms");
    dbrroom.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            List<String> roomname=new ArrayList<>();
            List<String>tenantname=new ArrayList<>();
            List<Uri> images=new ArrayList<>();
            end=snapshot.getChildrenCount();

            for(DataSnapshot ds:snapshot.getChildren())
            {start++;
if(ds.child("grabbedby").exists())
{
roomname.add(ds.getKey());
String key=ds.getKey();
String jj="";
for(DataSnapshot df:ds.child("grabbedby").getChildren())
{
    if(df.getKey().contains("tenant"))
    {if(jj==null || jj.isEmpty())jj=df.getValue(String.class);
        else
    {
        jj=jj+"+"+df.getValue(String.class);
    }




    }
}
tenantname.add(jj);
    String roomno=key.split(":")[1].split("idendsroom")[1];

    StorageReference sr=FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id).child("images"+roomno);
    sr.list(1).addOnSuccessListener(listResult -> {
        if (!listResult.getItems().isEmpty()) {
            listResult.getItems().get(0).getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        images.add(uri);


                        if(start==end)
                        {
                            String [] roomnamearray=roomname.toArray(new String[0]);
                            String [] tenantnamearray=tenantname.toArray(new String[0]);
                            Uri [] imagesarray=images.toArray(new Uri[0]);


                            AdapterForOwnerpart2 adapter = new AdapterForOwnerpart2(roomnamearray,tenantnamearray,imagesarray);
                            rv.setAdapter(adapter);
                            GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                            rv.setLayoutManager(glm);
                            pb.setVisibility(View.GONE);
                            rv.setVisibility(View.VISIBLE);

                            adapter.setListener(new AdapterForOwnerpart2.Listener1() {
                                public void iteminadapterforowneroccupiedclicked(int position,String s,String j) {
                                    if (listenerhome1 != null) {
                                        listenerhome1.itemclicked2(position,s,j);
                                    }
                                }
                            });



                        }
                    })
            ;
        }
    }).addOnFailureListener(e -> {

    });




}
            }









        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });









}
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listenerhome1 = (Listenerhome1) context;

    }
}