package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class fragmentforhome4 extends BottomSheetDialogFragment {
    interface Listenerhome1 {
        void itemclicked4(int position,String s);
    }
    interface Listenerhome2 {
        void itemclicked4payment(int position,String s);
    }
    long countrequest;

   Listenerhome1 listenerhome1;
   Listenerhome2 listenerhome2;
   private List<String> roomnameList=new ArrayList<>();
   private List<Long> roomtakenList=new ArrayList<>();
   private List<Uri> roompicList=new ArrayList<>();
   private String roomnamearray[];
   private Long roomtakenarray[];
   private Uri roompicarray[];
private int index2=0;
RecyclerView rv;
ProgressBar pb;
    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragmentforhom344, container, false);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if(account!=null)
        {String name=account.getDisplayName();
String id= account.getId();

rv=view.findViewById(R.id.rv);
pb=view.findViewById(R.id.pb);
loaddata(name,id);
            swipeRefreshLayout=view.findViewById(R.id.swipeRefresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    roomnameList.clear();
                    roomtakenList.clear();
                    roompicList.clear();
                    index2=0;
                    countrequest=0;
rv.setAdapter(null);
                    loaddata(name,id);
                    new android.os.Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
                }
            });










        }

        return  view;
    }
    public void loaddata(String name,String id)
    {DatabaseReference dbr=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child("no of requests sent").exists() && snapshot.child("no of requests sent").getValue(long.class)>0)
                {
                    countrequest=snapshot.child("no of requests sent").getValue(Long.class);

                    dbr.child("requestsenttotheownernumbers").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds:snapshot.getChildren())
                            {
                                String roomname=ds.getValue(String.class);

                                String ownername=roomname.split("idstarts")[0];
                                String roomid=roomname.split("idstarts")[1].split("idends")[0];
                                String roomno=roomname.split("room")[1];


                                dbr.child(roomname).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        roomtakenList.add(snapshot.child("requestapproved").getValue(long.class));
                                        roomnameList.add(roomname);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                StorageReference sr = FirebaseStorage.getInstance().getReference("ownerPhotos")
                                        .child(ownername).child(roomid).child("images" + roomno);

                                sr.list(1) // Limit to 1 item
                                        .addOnSuccessListener(listResult -> {
                                            if (!listResult.getItems().isEmpty()) {
                                                StorageReference firstImageRef = listResult.getItems().get(0);
                                                firstImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                    roompicList.add(uri); // Add URI to the list
                                                    index2++;

                                                    if(index2==countrequest)
                                                    {roomtakenarray=roomtakenList.toArray(new Long[0]);
                                                        roomnamearray=roomnameList.toArray(new String[0]);
                                                        roompicarray=roompicList.toArray(new Uri[0]);

                                                        AdapterForOwnerpart4 adapter = new AdapterForOwnerpart4(roomnamearray,roompicarray,roomtakenarray);
                                                        rv.setAdapter(adapter);
                                                        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                                                        rv.setLayoutManager(glm);
                                                        rv.setVisibility(View.VISIBLE);
                                                        pb.setVisibility(View.GONE);
                                                        adapter.setListener(new AdapterForOwnerpart4.Listener1() {
                                                            public void iteminadapterclickedforbookingfinalize(int position, String s) {
                                                                if (listenerhome1 != null) {
                                                                    listenerhome1.itemclicked4(position, s);
                                                                }
                                                            }
                                                        }, new AdapterForOwnerpart4.Listener2() {
                                                            @Override
                                                            public void iteminadapterclickedforbookingfinalizepayment(int position, String s) {
                                                                if(listenerhome2!=null)
                                                                {
                                                                    listenerhome2.itemclicked4payment(position,s);

                                                                }
                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        index2++;
                                                        if(index2==countrequest)
                                                        {roomtakenarray=roomtakenList.toArray(new Long[0]);
                                                            roomnamearray=roomnameList.toArray(new String[0]);
                                                            roompicarray=roompicList.toArray(new Uri[0]);
                                                            AdapterForOwnerpart4 adapter = new AdapterForOwnerpart4(roomnamearray,roompicarray,roomtakenarray);
                                                            rv.setAdapter(adapter);
                                                            GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                                                            rv.setLayoutManager(glm);
                                                            rv.setVisibility(View.VISIBLE);
                                                            pb.setVisibility(View.GONE);
                                                            adapter.setListener(new AdapterForOwnerpart4.Listener1() {
                                                                public void iteminadapterclickedforbookingfinalize(int position, String s) {
                                                                    if (listenerhome1 != null) {
                                                                        listenerhome1.itemclicked4(position, s);
                                                                    }
                                                                }
                                                            }, new AdapterForOwnerpart4.Listener2() {
                                                                @Override
                                                                public void iteminadapterclickedforbookingfinalizepayment(int position, String s) {
                                                                    if(listenerhome2!=null)
                                                                    {
                                                                        listenerhome2.itemclicked4payment(position,s);

                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            }
                                        });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                else{

                    rv.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    AdapterForemptyvalue adapter = new AdapterForemptyvalue(9);
                    rv.setAdapter(adapter);
                    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                    rv.setLayoutManager(glm);
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
this.listenerhome2=(Listenerhome2) context;
    }
}