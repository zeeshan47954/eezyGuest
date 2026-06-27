package com.example.bookandpostroom;


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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class Requestfragmentforowner extends Fragment implements RefreshListener {
    interface RequestListener{
        void functionforrequest(int id,String s);
    }

    List<Uri> imageurilist = new ArrayList<>();
    List<String> roomidslist = new ArrayList<>();
    List<String> roomnameslist = new ArrayList<>();
    Uri[] imageuriarray;
    String[] roomidarray;
    String[] roomnamearray;
    long index = 0;
    long index1 = 0;
    boolean done = false;
    long count;
    RequestListener requestListener;

    String name;
    String id;
    RecyclerView rv;
    ProgressBar pb;

    private DatabaseReference ownerRef;
    private ValueEventListener requestsListener; // Store listener reference

    @Override
    public void onRefresh() {
        imageurilist.clear();
        roomidslist.clear();
        roomnameslist.clear();

        index1 = 0;
        index = 0;
        done = false;
        count = 0;
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged();
        }
        pb.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);

        load();
    }
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requestfragmentforowner, container, false);
        rv = view.findViewById(R.id.rv);
        pb = view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        name = account.getDisplayName();
        id = account.getUid();
        load();

        return view;
    }

    public void load() {
        // Remove previous listener if exists
        if (ownerRef != null && requestsListener != null) {
            ownerRef.removeEventListener(requestsListener);
        }

        ownerRef = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");

        // Create real-time listener
        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear previous data
                imageurilist.clear();
                roomidslist.clear();
                roomnameslist.clear();
                index1 = 0;

                if (snapshot.child("actualrequestsrecieved").getChildrenCount() > 0) {
                    count = 0; // Reset count

                    // First pass: collect all valid room IDs
                    for (DataSnapshot ds : snapshot.child("actualrequestsrecieved").getChildren()) {
                        if (ds.getKey().contains("idendsfortenantforroomwithsid")) {
                            if (!ds.child("requestapproved").exists() || ds.child("requestapproved").getValue(Long.class) == 0) {
                                String value2 = ds.getKey();
                                String input = value2.split(":")[1];
                                roomidslist.add(input);

                                // Parse room name
                                String roomid = input.split("idendsfortenantforroomwithsid")[1];
                                String roomname = snapshot.child("rooms").child("roomId:" + roomid).child("roomname").getValue(String.class);
                                roomnameslist.add(roomname);

                                count++;
                            }
                        }
                    }

                    // If we have requests, load images
                    if (count > 0) {
                        for (int i = 0; i < roomidslist.size(); i++) {
                            String input = roomidslist.get(i);
                            final String usernametenant = input.split("idstartsfortenant")[0];
                            final String tenantid = input.split("idstartsfortenant")[1].split("idendsfortenantforroomwithsid")[0];

                            StorageReference ddd = FirebaseStorage.getInstance().getReference("tenantPhotos")
                                    .child(usernametenant).child(tenantid).child("pfp");

                            ddd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageurilist.add(uri);
                                    index1++;

                                    if (index1 == count) {
                                        updateAdapter();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Add a placeholder or null for failed images
                                    imageurilist.add(null);
                                    index1++;

                                    if (index1 == count) {
                                        updateAdapter();
                                    }
                                }
                            });
                        }
                    } else {
                        // No pending requests
                        showEmptyView();
                    }
                } else {
                    // No requests at all
                    showEmptyView();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getActivity() != null) {

                }
                pb.setVisibility(View.GONE);
            }
        };
        ownerRef.addValueEventListener(requestsListener);
        // Attach real-time listener

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if(ownerRef!=null && requestListener!=null)
        {ownerRef.removeEventListener(requestsListener);}
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(ownerRef!=null && requestListener!=null)
        {ownerRef.addValueEventListener(requestsListener);}
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(ownerRef!=null && requestListener!=null)
        {ownerRef.removeEventListener(requestsListener);}
    }
    private void updateAdapter() {
        if (getActivity() == null) return; // Check if fragment is still attached

        roomidarray = roomidslist.toArray(new String[0]);
        roomnamearray = roomnameslist.toArray(new String[0]);
        imageuriarray = imageurilist.toArray(new Uri[0]);

        adapterforrequestowner adapter = new adapterforrequestowner(roomidarray, roomnamearray, imageuriarray);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
        rv.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);

        adapter.setListener(new adapterforrequestowner.RequestListeneradapter() {
            @Override
            public void functionforrequest1(int id, String s) {
                if (requestListener != null) {
                    requestListener.functionforrequest(id, s);
                }
            }
        });
    }

    private void showEmptyView() {
        if (getActivity() == null) return;

        pb.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        AdapterForemptyvalue adapter = new AdapterForemptyvalue(10);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.requestListener = (RequestListener) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Remove listener when fragment view is destroyed to prevent memory leaks
        if (ownerRef != null && requestsListener != null) {
            ownerRef.removeEventListener(requestsListener);
        }
    }
}
/*package com.example.bookandpostroom;


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


public class Requestfragmentforowner extends Fragment  implements RefreshListener {
    interface RequestListener{
        void functionforrequest(int id,String s);

    }
    List<Uri> imageurilist=new ArrayList<>();
    List<String> roomidslist=new ArrayList<>();
    List<String> roomnameslist=new ArrayList<>();
    Uri [] imageuriarray;
    String [] roomidarray;
    String [] roomnamearray;
    long index=0;
    long index1=0;
    boolean done=false;
    long count;
    RequestListener requestListener;

    @Override
    public void onRefresh() {
        imageurilist.clear();
        roomidslist.clear();
        roomnameslist.clear();

        index1=0;
        index=0;
        done=false;
        count=0;
        if (rv.getAdapter() != null) {
            rv.getAdapter().notifyDataSetChanged(); // Notify adapter
        }
        pb.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);

        load();
    }
    String name;
    String id;
    RecyclerView rv;
    ProgressBar pb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requestfragmentforowner, container, false);
        rv=view.findViewById(R.id.rv);
        pb=view.findViewById(R.id.pb);
        pb.setVisibility(View.VISIBLE);
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getActivity());
        name=account.getDisplayName();
        id=account.getId();
        load();




        return view;

    }

    public void load() {
        DatabaseReference dd = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");
        dd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("actualrequestsrecieved").getChildrenCount() > 0) {
                    count = snapshot.child("actualrequestsrecieved").getChildrenCount();


                    for (DataSnapshot ds : snapshot.child("actualrequestsrecieved").getChildren()) {
                        String input = "";
                        if (ds.getKey().contains("idendsfortenantforroomwithsid")) {
                            String value2 = ds.getKey();
                            if (!ds.child("requestapproved").exists() || ds.child("requestapproved").getValue(Long.class) == 0  ) {
                                input = value2.split(":")[1];
                                roomidslist.add(input);
                            }
                        }

                        // Parse the input BEFORE the async call
                        if(roomidslist.size()>0){ final String usernametenant = input.split("idstartsfortenant")[0];
                        final String tenantid = input.split("idstartsfortenant")[1].split("idendsfortenantforroomwithsid")[0];
                        final String roomid = input.split("idendsfortenantforroomwithsid")[1];


                        String roomname = snapshot.child("rooms").child("roomId:" + roomid).child("roomname").getValue(String.class);
                        roomnameslist.add(roomname);

                        StorageReference ddd = FirebaseStorage.getInstance().getReference("tenantPhotos").child(usernametenant).child(tenantid).child("pfp");
                        ddd.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageurilist.add(uri);
                                index1++;

                                if (index1 == count) {
                                    // Convert lists to arrays
                                    roomidarray = roomidslist.toArray(new String[0]);
                                    roomnamearray = roomnameslist.toArray(new String[0]);
                                    imageuriarray = imageurilist.toArray(new Uri[0]);

                                    // Debug: Check if arrays are not null


                                    adapterforrequestowner adapter = new adapterforrequestowner(roomidarray, roomnamearray, imageuriarray);
                                    rv.setAdapter(adapter);
                                    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                                    rv.setLayoutManager(glm);
                                    rv.setVisibility(View.VISIBLE);
                                    pb.setVisibility(View.GONE);
                                    adapter.setListener(new adapterforrequestowner.RequestListeneradapter() {
                                        @Override
                                        public void functionforrequest1(int id, String s) {
                                            requestListener.functionforrequest(id, s);
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to load image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                // Handle failure case - you might want to increment index1 here too
                                // depending on your requirements
                            }
                        });
                    }
                        else{ pb.setVisibility(View.GONE);
                            rv.setVisibility(View.VISIBLE);
                            AdapterForemptyvalue adapter = new AdapterForemptyvalue(10);
                            rv.setAdapter(adapter);
                            GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                            rv.setLayoutManager(glm);

                        }
                    }
                } else {
                    pb.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    AdapterForemptyvalue adapter = new AdapterForemptyvalue(10);
                    rv.setAdapter(adapter);
                    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                    rv.setLayoutManager(glm);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.requestListener = (RequestListener) context;

    }


}*/