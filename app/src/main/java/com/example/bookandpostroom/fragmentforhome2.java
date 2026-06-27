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
import java.util.concurrent.atomic.AtomicInteger;


public class fragmentforhome2 extends Fragment implements RefreshListener {
    interface Listenerhome1 {
        void itemclicked2(int position,String s,String j);
    }



    private long countroomoccupied;
    private RecyclerView rv;
    private String name;
    private String id;
    ProgressBar pb;
    private DatabaseReference dbrroom;
    Listenerhome1 listenerhome1;
    private ValueEventListener roomsListener;

    private long x;
    private long index=0;

    // Add a timestamp to track current load operation
    private long currentLoadTimestamp = 0;

    @Override
    public void onRefresh() {
        countroomoccupied=0;
        index = 0;
        x=0;
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
        View view = inflater.inflate(R.layout.fragment_fragmentforhome2, container, false);
        rv=view.findViewById(R.id.rv);
        pb=view.findViewById(R.id.pb);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account != null) {
            name = account.getDisplayName();
            id = account.getUid();
            load();
        }

        return view;
    }

    long start=0;
    long end;
    Boolean data;
    @Override
    public void onResume() {
        super.onResume();

        if (dbrroom != null) {

            if (roomsListener != null)
                dbrroom.addValueEventListener(roomsListener);


        }
    }
    @Override
    public void onPause() {
        super.onPause();

        if (dbrroom != null) {

            if (roomsListener != null)
                dbrroom.removeEventListener(roomsListener);


        }
    }

    public void load(){
        // Remove previous listener if exists
        if (dbrroom != null && roomsListener != null) {
            dbrroom.removeEventListener(roomsListener);
        }

        dbrroom = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner").child("rooms");

        roomsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Increment timestamp for this load operation
                currentLoadTimestamp = System.currentTimeMillis();
                final long thisLoadTimestamp = currentLoadTimestamp;

                long totalRooms = snapshot.getChildrenCount();

                // If no rooms exist at all
                if (!snapshot.exists() ||totalRooms == 0) {
                    showEmptyState();
                    return;
                }

                // First pass: collect all occupied rooms data
                List<String> roomname = new ArrayList<>();
                List<String> tenantname = new ArrayList<>();
                List<String> roomNumbers = new ArrayList<>();

                for(DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.child("grabbedby").exists()) {
                        String key = ds.getKey();
                        roomname.add(key);

                        // Get tenant names
                        StringBuilder jj = new StringBuilder();
                        for(DataSnapshot df : ds.child("grabbedby").getChildren()) {
                            if(df.getKey().contains("tenant")) {
                                if(jj.length() == 0) {
                                    jj.append(df.getValue(String.class));
                                } else {
                                    jj.append("+").append(df.getValue(String.class));
                                }
                            }
                        }
                        tenantname.add(jj.toString());

                        // Extract room number
                        String roomno = key.split(":")[1].split("idendsroom")[1];
                        roomNumbers.add(roomno);
                    }
                }

                // If no occupied rooms, show empty state immediately
                if (roomname.isEmpty()) {
                    showEmptyState();
                    return;
                }

                // Now load images for occupied rooms
                List<Uri> images = new ArrayList<>();
                AtomicInteger imagesProcessed = new AtomicInteger(0);
                final int expectedImages = roomname.size();

                for(int i = 0; i < roomNumbers.size(); i++) {
                    final int index = i;
                    String roomno = roomNumbers.get(i);

                    StorageReference sr = FirebaseStorage.getInstance().getReference("ownerPhotos")
                            .child(name).child(id).child("images"+roomno);

                    // Initialize with null, we'll update when loaded
                    images.add(null);

                    sr.list(1).addOnSuccessListener(listResult -> {
                        if (!listResult.getItems().isEmpty()) {
                            listResult.getItems().get(0).getDownloadUrl()
                                    .addOnSuccessListener(uri -> {
                                        // Check if this is still the current load operation
                                        if (thisLoadTimestamp != currentLoadTimestamp) {
                                            return; // Discard outdated result
                                        }

                                        if (index < images.size()) {
                                            images.set(index, uri);
                                        }

                                        if(imagesProcessed.incrementAndGet() == expectedImages) {
                                            updateAdapter(roomname, tenantname, images);
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        if (thisLoadTimestamp != currentLoadTimestamp) {
                                            return;
                                        }

                                        if(imagesProcessed.incrementAndGet() == expectedImages) {
                                            updateAdapter(roomname, tenantname, images);
                                        }
                                    });
                        } else {
                            if (thisLoadTimestamp != currentLoadTimestamp) {
                                return;
                            }

                            if(imagesProcessed.incrementAndGet() == expectedImages) {
                                updateAdapter(roomname, tenantname, images);
                            }
                        }
                    }).addOnFailureListener(e -> {
                        if (thisLoadTimestamp != currentLoadTimestamp) {
                            return;
                        }

                        if(imagesProcessed.incrementAndGet() == expectedImages) {
                            updateAdapter(roomname, tenantname, images);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getActivity() != null) {

                }
                pb.setVisibility(View.GONE);
            }
        };
        dbrroom.addValueEventListener(roomsListener);
        // Attach real-time listener

    }

    private void showEmptyState() {
        if (getActivity() == null) return;

        pb.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        AdapterForemptyvalue adapter = new AdapterForemptyvalue(2);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
    }

    private void updateAdapter(List<String> roomname, List<String> tenantname, List<Uri> images) {
        if (getActivity() == null) return;

        String[] roomnamearray = roomname.toArray(new String[0]);
        String[] tenantnamearray = tenantname.toArray(new String[0]);
        Uri[] imagesarray = images.toArray(new Uri[0]);

        AdapterForOwnerpart2 adapter = new AdapterForOwnerpart2(roomnamearray, tenantnamearray, imagesarray);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
        pb.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);

        adapter.setListener(new AdapterForOwnerpart2.Listener1() {
            public void iteminadapterforowneroccupiedclicked(int position, String s, String j) {
                if (listenerhome1 != null) {
                    listenerhome1.itemclicked2(position, s, j);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listenerhome1 = (Listenerhome1) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (dbrroom != null && roomsListener != null) {
            dbrroom.removeEventListener(roomsListener);
        }
    }
}

//

//package com.example.bookandpostroom;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class fragmentforhome2 extends Fragment implements RefreshListener {
//    interface Listenerhome1 {
//        void itemclicked2(int position,String s,String j);
//    }
//
//
//    private long countroomoccupied;
//    private RecyclerView rv;
//    private String name;
//    private String id;
//    ProgressBar pb;
//    private DatabaseReference dbrroom;
//    Listenerhome1 listenerhome1;
//    private ValueEventListener roomsListener; // Store listener reference
//
//    private long x;
//    private long index=0;
//
//    @Override
//    public void onRefresh() {
//        countroomoccupied=0;
//        index = 0;
//        x=0;
//        // Reset the index counter
//        if (rv.getAdapter() != null) {
//            rv.getAdapter().notifyDataSetChanged(); // Notify adapter
//        }
//        pb.setVisibility(View.VISIBLE);
//        rv.setVisibility(View.GONE);
//        load();
//    }
//    private FirebaseAuth firebaseAuth;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_fragmentforhome2, container, false);
//        rv=view.findViewById(R.id.rv);
//        pb=view.findViewById(R.id.pb);
//        firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser account = firebaseAuth.getCurrentUser();
//        if (account != null) {
//            name = account.getDisplayName();
//            id = account.getUid();
//            load();
//        }
//
//        return view;
//    }
//
//    long start=0;
//    long end;
//    Boolean data;
//
//    public void load(){
//        // Remove previous listener if exists
//        if (dbrroom != null && roomsListener != null) {
//            dbrroom.removeEventListener(roomsListener);
//        }
//
//        dbrroom = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner").child("rooms");
//
//        // Create real-time listener
//        roomsListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                start = 0; // Reset counter
//                List<String> roomname = new ArrayList<>();
//                List<String> tenantname = new ArrayList<>();
//                List<Uri> images = new ArrayList<>();
//                end = snapshot.getChildrenCount();
//
//                if (end == 0) {
//                    pb.setVisibility(View.GONE);
//                    rv.setVisibility(View.VISIBLE);
//                    AdapterForemptyvalue adapter = new AdapterForemptyvalue(2);
//                    rv.setAdapter(adapter);
//                    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                    rv.setLayoutManager(glm);
//                    return;
//                }
//
//                for(DataSnapshot ds : snapshot.getChildren()) {
//                    start++;
//
//                    if(ds.child("grabbedby").exists()) {
//                        roomname.add(ds.getKey());
//                        String key = ds.getKey();
//                        String jj = "";
//
//                        for(DataSnapshot df : ds.child("grabbedby").getChildren()) {
//                            if(df.getKey().contains("tenant")) {
//                                if(jj == null || jj.isEmpty())
//                                    jj = df.getValue(String.class);
//                                else
//                                    jj = jj + "+" + df.getValue(String.class);
//                            }
//                        }
//
//                        tenantname.add(jj);
//                        String roomno = key.split(":")[1].split("idendsroom")[1];
//
//                        StorageReference sr = FirebaseStorage.getInstance().getReference("ownerPhotos")
//                                .child(name).child(id).child("images"+roomno);
//
//                        sr.list(1).addOnSuccessListener(listResult -> {
//                            if (!listResult.getItems().isEmpty()) {
//                                listResult.getItems().get(0).getDownloadUrl()
//                                        .addOnSuccessListener(uri -> {
//                                            images.add(uri);
//
//                                            if(images.size() == tenantname.size()) {
//                                                updateAdapter(roomname, tenantname, images);
//                                            }
//                                        });
//                            }
//                        }).addOnFailureListener(e -> {
//                            // Handle failure - add null or placeholder
//                            images.add(null);
//                            if(images.size() == tenantname.size()) {
//                                updateAdapter(roomname, tenantname, images);
//                            }
//                        });
//                    } else if (start == end && tenantname.isEmpty()) {
//                        pb.setVisibility(View.GONE);
//                        rv.setVisibility(View.VISIBLE);
//                        AdapterForemptyvalue adapter = new AdapterForemptyvalue(2);
//                        rv.setAdapter(adapter);
//                        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                        rv.setLayoutManager(glm);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                if (getActivity() != null) {
//
//                }
//                pb.setVisibility(View.GONE);
//            }
//        };
//
//        // Attach real-time listener instead of single value listener
//        dbrroom.addValueEventListener(roomsListener);
//    }
//
//    private void updateAdapter(List<String> roomname, List<String> tenantname, List<Uri> images) {
//        if (getActivity() == null) return; // Check if fragment is still attached
//
//        String[] roomnamearray = roomname.toArray(new String[0]);
//        String[] tenantnamearray = tenantname.toArray(new String[0]);
//        Uri[] imagesarray = images.toArray(new Uri[0]);
//
//        AdapterForOwnerpart2 adapter = new AdapterForOwnerpart2(roomnamearray, tenantnamearray, imagesarray);
//        rv.setAdapter(adapter);
//        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//        rv.setLayoutManager(glm);
//        pb.setVisibility(View.GONE);
//        rv.setVisibility(View.VISIBLE);
//
//        adapter.setListener(new AdapterForOwnerpart2.Listener1() {
//            public void iteminadapterforowneroccupiedclicked(int position, String s, String j) {
//                if (listenerhome1 != null) {
//                    listenerhome1.itemclicked2(position, s, j);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.listenerhome1 = (Listenerhome1) context;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // Remove listener when fragment view is destroyed to prevent memory leaks
//        if (dbrroom != null && roomsListener != null) {
//            dbrroom.removeEventListener(roomsListener);
//        }
//    }
//}

/*package com.example.bookandpostroom;

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
    Boolean data;
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
                    });
        }
    }).addOnFailureListener(e -> {




    });




} else if (start==end && tenantname.isEmpty()) {
    pb.setVisibility(View.GONE);
    rv.setVisibility(View.VISIBLE);
    AdapterForemptyvalue adapter = new AdapterForemptyvalue(2);
    rv.setAdapter(adapter);
    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
    rv.setLayoutManager(glm);
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
}*/