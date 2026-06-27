package com.example.bookandpostroom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class fragmentforownedrooms extends Fragment {
    interface Listenerhome1 {
        void itemclicked2inownerrooms(int position, String s);
    }
    interface Listenerhome2{

        void bookingcancellation(int position,String s,String tenanttranno,String transactionid,String amount,String transferid);
    }
List<String> madetoroominfo=new ArrayList<>();
    List<Uri> imageuri1=new ArrayList<>();
    List<String> datelist=new ArrayList<>();
    List<String> timelist=new ArrayList<>();
List<String> transferidlist=new ArrayList<>();
    List<Long> tenanttransactionno=new ArrayList<>();
    List<String> transactionidlist=new ArrayList<>();
    List<String>amountlist=new ArrayList<>();

    long x;
    private long countroomoccupied;
    private Listenerhome1 listenerhome1;
    private Listenerhome2 listenerhome2;

    private int index;
    private String[] caption;
    private Uri[] imageuri;
    private String[]datearray;
    private String[]timearray;
    private long temp;
    private Long[]ownertrannoarray;
    private Long[]tenanttrannoarray;
    private String[]transactionidarray;
    private String[]amountarray;
    private long finalIndex=0;
    private long finalIndex2=0;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar pb;
    RecyclerView rv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  view =  inflater.inflate(R.layout.fragment_fragmentforhome2, container, false);

 rv=view.findViewById(R.id.rv);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
        if (account != null) {
            String name = account.getDisplayName();
            String id = account.getId();

             pb=view.findViewById(R.id.pb);
            pb.setVisibility(View.VISIBLE);

loaddata(name,id);
        }

        return view;
    }
    public void loaddata(String name,String id)
    {DatabaseReference dbrroom = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("transactionmadeno");
        dbrroom.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long s=snapshot.getChildrenCount();

                if ( s > 0) {

                    List<DataSnapshot> childrenList = new ArrayList<>();  // Create a list to store children

                    for (DataSnapshot chilld : snapshot.getChildren()) {
                        childrenList.add(chilld);  // Add each child DataSnapshot to the list
                    }
                    for( DataSnapshot chilled : snapshot.getChildren())
                    {



                        // Preserve index for async callbacks
                        String transactionInfo = chilled.child("info").getValue(String.class);
                        String tenantinfo = name + "idstarts" + id + "idends";

                        String date=chilled.child("transactionDate").getValue(String.class);
                        String time=chilled.child("transactionTime").getValue(String.class);

                        String transferid=chilled.child("transferid").getValue(String.class);
                        long tenanttranono=chilled.child("transactionno").getValue(Long.class);
                        String transactionid=chilled.child("transactionid").getValue(String.class);
                        String amount=chilled.child("amount").getValue(String.class);
                        String ownername = transactionInfo.split("idstarts")[0];
                        String ownerId = transactionInfo.split("idstarts")[1].split("idends")[0];

                        String roomno=transactionInfo.split("idendsroom")[1];


                        DatabaseReference dbr=FirebaseDatabase.getInstance().getReference("google").child(ownername)
                                .child(ownerId).child("owner").child("rooms").child("roomId:"+transactionInfo).child("grabbedby");
                        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                long total=snapshot.getChildrenCount();
                                for(DataSnapshot dd:snapshot.getChildren())
                                {
                                    String s=dd.getValue(String.class);
                                    if(s!=null && s.contains(name+"idstarts"+id+"idends"))
                                    {finalIndex2++;

                                        StorageReference kk=FirebaseStorage.getInstance().getReference("ownerPhotos").child(ownername).child(ownerId).child("images"+roomno);
                                            kk.list(1).addOnSuccessListener(listResult -> {
                                                if (!listResult.getItems().isEmpty()) {
                                                    StorageReference firstImageRef = listResult.getItems().get(0); // Get the first image

                                                    firstImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                        imageuri1.add(uri);
                                                        madetoroominfo.add(transactionInfo);
                                                        datelist.add(date);
                                                        timelist.add(time);
transferidlist.add(transferid);
                                                        tenanttransactionno.add(tenanttranono);
                                                        transactionidlist.add(transactionid);
                                                        amountlist.add(amount);
                                                        if(finalIndex2==total )
                                                        {

                                                            Uri[] imageuri1Array = imageuri1.toArray(new Uri[0]);
                                                            String[] madetoroominfoArray = madetoroominfo.toArray(new String[0]);
                                                            datearray=datelist.toArray(new String[0]);
                                                            timearray=timelist.toArray(new String[0]);
                                                           String transferidlistarray[]=transferidlist.toArray(new String[0]);

                                                            tenanttrannoarray=tenanttransactionno.toArray(new Long[0]);
                                                            transactionidarray=transactionidlist.toArray(new String[0]);
                                                            amountarray=amountlist.toArray(new String[0]);
                                                            rv.setVisibility(View.VISIBLE);
                                                            pb.setVisibility(View.GONE);


                                                            AdapterForOwnerpart7 adapter = new AdapterForOwnerpart7(madetoroominfoArray, imageuri1Array,datearray,timearray,tenanttrannoarray,transactionidarray,amountarray,transferidlistarray);
                                                            rv.setAdapter(adapter);
                                                            rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                                                            adapter.setListener(new AdapterForOwnerpart7.Listener1() {
                                                                @Override
                                                                public void itemclickedforcheckingroomoccupied(int position, String s) {
                                                                    if (listenerhome1 != null) {
                                                                        listenerhome1.itemclicked2inownerrooms(position, s);
                                                                    }
                                                                }
                                                            }, new AdapterForOwnerpart7.Listener2() {
                                                                @Override
                                                                public void cancelbookingfortenant(int id, String s,String tenanttrano,String transactionid,String amount,String transferid) {
                                                                    if(listenerhome2!=null)
                                                                    {
                                                                        listenerhome2.bookingcancellation(id,s,tenanttrano,transactionid,amount,transferid);
                                                                    }
                                                                }
                                                            });




                                                        }


                                                        // Use this URL to load the image with Glide or Picasso
                                                    }).addOnFailureListener(e -> Log.e("FirebaseImage", "Failed to get URL", e));
                                                } else {
                                                    Log.d("FirebaseImage", "No images found");
                                                }
                                            }).addOnFailureListener(e -> Log.e("FirebaseImage", "Failed to list images", e));






                                    }


                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });




                    }
                } else {

                    rv.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    AdapterForemptyvalue adapter = new AdapterForemptyvalue(7);
                    rv.setAdapter(adapter);
                    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                    rv.setLayoutManager(glm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });



    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listenerhome1 = (Listenerhome1) context;
        this.listenerhome2=(Listenerhome2) context;
    }
}
