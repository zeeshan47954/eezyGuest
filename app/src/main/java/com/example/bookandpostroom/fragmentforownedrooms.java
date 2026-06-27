package com.example.bookandpostroom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragmentforownedrooms extends Fragment {

    interface Listenerhome1 {
        void itemclicked2inownerrooms(int position, String s);
    }

    interface Listenerhome2 {
        void bookingcancellation(int position, String s, String tenanttranno, String transactionid, String amount, String transferid, String date, String time);
    }

    private Listenerhome1 listenerhome1;
    private Listenerhome2 listenerhome2;

    private List<String> madetoroominfo = new ArrayList<>();
    private List<Uri> imageuri1 = new ArrayList<>();
    private List<String> datelist = new ArrayList<>();
    private List<String> timelist = new ArrayList<>();
    private List<String> istenantlist = new ArrayList<>();
    private List<String> transferidlist = new ArrayList<>();
    private List<Long> tenanttransactionno = new ArrayList<>();
    private List<String> transactionidlist = new ArrayList<>();
    private List<String> amountlist = new ArrayList<>();
    private List<String> refundinitiatedlist = new ArrayList<>();

    private ProgressBar pb;
    private RecyclerView rv;
    private SwipeRefreshLayout swipeRefreshLayout;

    private AdapterForOwnerpart7 adapter;
    private FirebaseAuth firebaseAuth;

    // Keep track of listeners for proper cleanup
    private ValueEventListener requestListener;
    private ValueEventListener transactionListener;
    private ValueEventListener grabbedListener;
    private Map<String, ValueEventListener> grabbedListeners = new HashMap<>();
    DatabaseReference grabbedRef;
    private DatabaseReference requestRef;
    private DatabaseReference transactionRef;
    private Map<String, DatabaseReference> grabbedRefs = new HashMap<>();

    @Override
    public void onPause()
    {
        super.onPause();
        if(requestRef!=null && requestListener!=null)
        {requestRef.removeEventListener(requestListener);}
        if(grabbedRef!=null && grabbedListener!=null)
        {grabbedRef.removeEventListener(grabbedListener);}
        if(transactionRef!=null && transactionListener!=null)
        {transactionRef.removeEventListener(transactionListener);}
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(requestRef!=null && requestListener!=null)
        {requestRef.addValueEventListener(requestListener);}
        if(grabbedRef!=null && grabbedListener!=null)
        { grabbedRef.addValueEventListener(grabbedListener);}
        if(transactionRef!=null && transactionListener!=null)
        { transactionRef.addValueEventListener(transactionListener);}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmentforhome2, container, false);

        rv = view.findViewById(R.id.rv);
        pb = view.findViewById(R.id.pb);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        pb.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Show visual feedback of refresh
            rv.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);

            // Small delay to show the refresh animation
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                refreshData();
            }, 300);
        });

        loadData();

        return view;
    }

    private void refreshData() {
        // Remove all existing listeners first
        removeAllListeners();

        // Clear data
        clearData();

        // Reload data
        loadData();

        // Stop refresh animation
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void clearData() {
        madetoroominfo.clear();
        imageuri1.clear();
        datelist.clear();
        timelist.clear();
        transferidlist.clear();
        tenanttransactionno.clear();
        transactionidlist.clear();
        amountlist.clear();

        // Also reset adapter
        if (adapter != null) {
            adapter = null;
        }
    }

    private void removeAllListeners() {
        // Remove request listener
        if (requestRef != null && requestListener != null) {
            requestRef.removeEventListener(requestListener);
        }

        // Remove transaction listener
        if (transactionRef != null && transactionListener != null) {
            transactionRef.removeEventListener(transactionListener);
        }
        if(grabbedRef!=null && grabbedListener!=null)
        {grabbedRef.removeEventListener(grabbedListener);}

        // Remove all grabbed listeners
        for (Map.Entry<String, DatabaseReference> entry : grabbedRefs.entrySet()) {
            ValueEventListener listener = grabbedListeners.get(entry.getKey());
            if (listener != null) {
                entry.getValue().removeEventListener(listener);
            }
        }
        grabbedRefs.clear();
        grabbedListeners.clear();
    }

    private void loadData() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account == null) {
            showEmptyState();
            return;
        }

        String name = account.getDisplayName();
        String id = account.getUid();

        DatabaseReference dbrroom = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(name)
                .child(id)
                .child("student");

        requestRef = dbrroom.child("requestsenttotheownernumbers");
        requestListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() &&snapshot.getChildrenCount() > 0) {
                    loadTransactions(dbrroom, name, id);
                } else {
                    // Remove transaction listener if it exists
                    if (transactionRef != null && transactionListener != null) {
                        transactionRef.removeEventListener(transactionListener);
                    }
                    // Remove all grabbed listeners
                    for (Map.Entry<String, DatabaseReference> entry : grabbedRefs.entrySet()) {
                        ValueEventListener listener = grabbedListeners.get(entry.getKey());
                        if (listener != null) {
                            entry.getValue().removeEventListener(listener);
                        }
                    }
                    grabbedRefs.clear();
                    grabbedListeners.clear();

                    clearData();
                    showEmptyState();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showEmptyState();
            }
        };
        requestRef.addValueEventListener(requestListener);
    }
    String refinitiated;
    private void loadTransactions(DatabaseReference dbrroom, String name, String id) {
        transactionRef = dbrroom.child("transactionmadeno");
        transactionListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearData(); // Clear before loading new data

                if (!snapshot.exists()) {
                    showEmptyState();
                    return;
                }

                int totalTransactions = (int) snapshot.getChildrenCount();
                if (totalTransactions == 0) {
                    showEmptyState();
                    return;
                }

                // Track completion
                final int[] processedCount = {0};

                for (DataSnapshot chilled : snapshot.getChildren()) {
                    if(chilled.child("tenant").getValue(String.class).equals("yes")) {
                        String transactionInfo = chilled.child("info").getValue(String.class);
                        String date = chilled.child("transactionDate").getValue(String.class);
                        String time = chilled.child("transactionTime").getValue(String.class);
                        String transferid = chilled.child("transferid").getValue(String.class);
                        String istenant=chilled.child("tenant").getValue(String.class);
                        Long tenanttranono = chilled.child("transactionno").getValue(Long.class);
                        String transactionid = chilled.child("transactionid").getValue(String.class);
                        String amount = chilled.child("amount").getValue(String.class);
                        refinitiated = chilled.child("refundrequested").getValue(String.class);
                        if (refinitiated == null || refinitiated.isEmpty())
                            refinitiated = "no";

                        if (transactionInfo == null) {
                            processedCount[0]++;
                            if (processedCount[0] == totalTransactions) {
                                finalizeLoading();
                            }
                            continue;
                        }

                        String[] infoParts = transactionInfo.split("idstarts");
                        if (infoParts.length < 2) {
                            processedCount[0]++;
                            if (processedCount[0] == totalTransactions) {
                                finalizeLoading();
                            }
                            continue;
                        }

                        String ownername = infoParts[0];
                        String[] idParts = infoParts[1].split("idends");
                        if (idParts.length < 2) {
                            processedCount[0]++;
                            if (processedCount[0] == totalTransactions) {
                                finalizeLoading();
                            }
                            continue;
                        }

                        String ownerId = idParts[0];
                        String[] roomParts = transactionInfo.split("idendsroom");
                        if (roomParts.length < 2) {
                            processedCount[0]++;
                            if (processedCount[0] == totalTransactions) {
                                finalizeLoading();
                            }
                            continue;
                        }

                        String roomno = roomParts[1];

                         grabbedRef = FirebaseDatabase.getInstance()
                                .getReference("google")
                                .child(ownername)
                                .child(ownerId)
                                .child("owner")
                                .child("rooms")
                                .child("roomId:" + transactionInfo)
                                .child("grabbedby");

                        String refKey = transactionInfo; // Use transaction info as unique key

                         grabbedListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot grabSnap) {
                                boolean found = false;

                                if (grabSnap.exists()) {
                                    for (DataSnapshot dd : grabSnap.getChildren()) {
                                        String s = dd.getValue(String.class);
                                        if (s != null && s.contains(name + "idstarts" + id + "idends")) {
                                            found = true;
                                            // Load first image from storage
                                            StorageReference imgRef = FirebaseStorage.getInstance()
                                                    .getReference("ownerPhotos")
                                                    .child(ownername)
                                                    .child(ownerId)
                                                    .child("images" + roomno);

                                            imgRef.list(1).addOnSuccessListener(listResult -> {
                                                if (!listResult.getItems().isEmpty()) {
                                                    listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(uri -> {
                                                        // Check if already exists to avoid duplicates
                                                        if (!madetoroominfo.contains(transactionInfo) && istenant.equals("yes")) {
                                                            madetoroominfo.add(transactionInfo);
                                                            imageuri1.add(uri);
                                                            datelist.add(date);
                                                            timelist.add(time);
                                                            transferidlist.add(transferid);
                                                            tenanttransactionno.add(tenanttranono);
                                                            transactionidlist.add(transactionid);
                                                            amountlist.add(amount);
                                                            refundinitiatedlist.add(refinitiated);

                                                            updateAdapter();
                                                        }
                                                    }).addOnFailureListener(e -> {
                                                        Log.e("FirebaseImage", "Failed to get URL", e);
                                                        processedCount[0]++;
                                                        if (processedCount[0] == totalTransactions) {
                                                            finalizeLoading();
                                                        }
                                                    });
                                                } else {
                                                    processedCount[0]++;
                                                    if (processedCount[0] == totalTransactions) {
                                                        finalizeLoading();
                                                    }
                                                }
                                            }).addOnFailureListener(e -> {
                                                Log.e("FirebaseImage", "Failed to list images", e);
                                                processedCount[0]++;
                                                if (processedCount[0] == totalTransactions) {
                                                    finalizeLoading();
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }

                                if (!found) {
                                    processedCount[0]++;
                                    if (processedCount[0] == totalTransactions) {
                                        finalizeLoading();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                processedCount[0]++;
                                if (processedCount[0] == totalTransactions) {
                                    finalizeLoading();
                                }
                            }
                        };
                        grabbedRef.addValueEventListener(grabbedListener);
                        grabbedRefs.put(refKey, grabbedRef);
                        grabbedListeners.put(refKey, grabbedListener);

                    }
                    else{
                        processedCount[0]++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showEmptyState();
            }
        };
        transactionRef.addValueEventListener(transactionListener);
    }

    private void finalizeLoading() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (madetoroominfo.isEmpty()) {
                showEmptyState();
            } else {
                updateAdapter();
            }
        }, 500);
    }

    private void showEmptyState() {
        if (getActivity() == null) return;

        pb.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        AdapterForemptyvalue emptyAdapter = new AdapterForemptyvalue(7);
        rv.setAdapter(emptyAdapter);
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
    }

    private void updateAdapter() {
        if (getActivity() == null) return;

        if (madetoroominfo.isEmpty() || imageuri1.isEmpty() || datelist.isEmpty() ||
                timelist.isEmpty() || tenanttransactionno.isEmpty() ||
                transactionidlist.isEmpty() || amountlist.isEmpty() || transferidlist.isEmpty()|| refundinitiatedlist.isEmpty()) {
            showEmptyState();
            return;
        }

        if (adapter == null) {
            adapter = new AdapterForOwnerpart7(
                    madetoroominfo.toArray(new String[0]),
                    imageuri1.toArray(new Uri[0]),
                    datelist.toArray(new String[0]),
                    timelist.toArray(new String[0]),
                    tenanttransactionno.toArray(new Long[0]),
                    transactionidlist.toArray(new String[0]),
                    amountlist.toArray(new String[0]),
                    transferidlist.toArray(new String[0]),
                    refundinitiatedlist.toArray(new String[0])
            );

            rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            rv.setAdapter(adapter);

            adapter.setListener(new AdapterForOwnerpart7.Listener1() {
                @Override
                public void itemclickedforcheckingroomoccupied(int position, String s) {
                    if (listenerhome1 != null) {
                        listenerhome1.itemclicked2inownerrooms(position, s);
                    }
                }
            }, new AdapterForOwnerpart7.Listener2() {
                @Override
                public void cancelbookingfortenant(int id, String s, String tenanttrano, String transactionid, String amount, String transferid, String date, String time) {
                    if (listenerhome2 != null) {
                        listenerhome2.bookingcancellation(id, s, tenanttrano, transactionid, amount, transferid, date, time);
                    }
                }
            });
        } else {
            // Update adapter data
            adapter.updateData(
                    madetoroominfo.toArray(new String[0]),
                    imageuri1.toArray(new Uri[0]),
                    datelist.toArray(new String[0]),
                    timelist.toArray(new String[0]),
                    tenanttransactionno.toArray(new Long[0]),
                    transactionidlist.toArray(new String[0]),
                    amountlist.toArray(new String[0]),
                    transferidlist.toArray(new String[0]),
                    refundinitiatedlist.toArray(new String[0])
            );
        }

        pb.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listenerhome1 = (Listenerhome1) context;
        listenerhome2 = (Listenerhome2) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        removeAllListeners();
    }

}
//
//package com.example.bookandpostroom;
//
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
//public class fragmentforownedrooms extends Fragment {
//
//    interface Listenerhome1 {
//        void itemclicked2inownerrooms(int position, String s);
//    }
//
//    interface Listenerhome2 {
//        void bookingcancellation(int position, String s, String tenanttranno, String transactionid, String amount, String transferid,String date,String time);
//    }
//
//    private Listenerhome1 listenerhome1;
//    private Listenerhome2 listenerhome2;
//
//    private List<String> madetoroominfo = new ArrayList<>();
//    private List<Uri> imageuri1 = new ArrayList<>();
//    private List<String> datelist = new ArrayList<>();
//    private List<String> timelist = new ArrayList<>();
//    private List<String> transferidlist = new ArrayList<>();
//    private List<Long> tenanttransactionno = new ArrayList<>();
//    private List<String> transactionidlist = new ArrayList<>();
//    private List<String> amountlist = new ArrayList<>();
//
//    private ProgressBar pb;
//    private RecyclerView rv;
//    private SwipeRefreshLayout swipeRefreshLayout;
//
//    private AdapterForOwnerpart7 adapter;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_fragmentforhome2, container, false);
//
//        rv = view.findViewById(R.id.rv);
//        pb = view.findViewById(R.id.pb);
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
//
//        pb.setVisibility(View.VISIBLE);
//
//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            clearData();
//            loadData();
//
//        });
//
//        loadData();
//
//        return view;
//    }
//
//    private void clearData() {
//        madetoroominfo.clear();
//        imageuri1.clear();
//        datelist.clear();
//        timelist.clear();
//        transferidlist.clear();
//        tenanttransactionno.clear();
//        transactionidlist.clear();
//        amountlist.clear();
//    }
//    private FirebaseAuth firebaseAuth;
//    private void loadData() {
//        firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser account = firebaseAuth.getCurrentUser();
//        if (account == null) return;
//
//        String name = account.getDisplayName();
//        String id = account.getUid();
//
//        DatabaseReference dbrroom = FirebaseDatabase.getInstance()
//                .getReference("google")
//                .child(name)
//                .child(id)
//                .child("student");
//        dbrroom.child("requestsenttotheownernumbers").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.getChildrenCount() > 0)
//                {   dbrroom.child("transactionmadeno").addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                            clearData(); // Ensure no duplicate entries
//
//                            if (snapshot.exists()) {
//                                for (DataSnapshot chilled : snapshot.getChildren()) {
//                                    String transactionInfo = chilled.child("info").getValue(String.class);
//                                    String date = chilled.child("transactionDate").getValue(String.class);
//                                    String time = chilled.child("transactionTime").getValue(String.class);
//                                    String transferid = chilled.child("transferid").getValue(String.class);
//                                    Long tenanttranono = chilled.child("transactionno").getValue(Long.class);
//                                    String transactionid = chilled.child("transactionid").getValue(String.class);
//                                    String amount = chilled.child("amount").getValue(String.class);
//
//                                    if (transactionInfo == null) continue;
//
//                                    String ownername = transactionInfo.split("idstarts")[0];
//                                    String ownerId = transactionInfo.split("idstarts")[1].split("idends")[0];
//                                    String roomno = transactionInfo.split("idendsroom")[1];
//
//                                    DatabaseReference grabbedRef = FirebaseDatabase.getInstance()
//                                            .getReference("google")
//                                            .child(ownername)
//                                            .child(ownerId)
//                                            .child("owner")
//                                            .child("rooms")
//                                            .child("roomId:" + transactionInfo)
//                                            .child("grabbedby");
//
//                                    grabbedRef.addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot grabSnap) {
//
//                                            if (grabSnap.exists()) {
//                                                for (DataSnapshot dd : grabSnap.getChildren()) {
//                                                    String s = dd.getValue(String.class);
//                                                    if (s != null && s.contains(name + "idstarts" + id + "idends")) {
//                                                        // Load first image from storage
//                                                        StorageReference imgRef = FirebaseStorage.getInstance()
//                                                                .getReference("ownerPhotos")
//                                                                .child(ownername)
//                                                                .child(ownerId)
//                                                                .child("images" + roomno);
//
//                                                        imgRef.list(1).addOnSuccessListener(listResult -> {
//                                                            if (!listResult.getItems().isEmpty()) {
//                                                                listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(uri -> {
//                                                                    madetoroominfo.add(transactionInfo);
//                                                                    imageuri1.add(uri);
//                                                                    datelist.add(date);
//                                                                    timelist.add(time);
//                                                                    transferidlist.add(transferid);
//                                                                    tenanttransactionno.add(tenanttranono);
//                                                                    transactionidlist.add(transactionid);
//                                                                    amountlist.add(amount);
//
//                                                                    updateAdapter();
//                                                                }).addOnFailureListener(e -> Log.e("FirebaseImage", "Failed to get URL", e));
//                                                            }
//                                                        }).addOnFailureListener(e -> Log.e("FirebaseImage", "Failed to list images", e));
//                                                    }
//                                                }
//
//
//                                            } else {
//                                                pb.setVisibility(View.GONE);
//                                                rv.setVisibility(View.VISIBLE);
//                                                AdapterForemptyvalue emptyAdapter = new AdapterForemptyvalue(7);
//                                                rv.setAdapter(emptyAdapter);
//                                                rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//
//                                            }
//
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//                                        }
//                                    });
//                                }
//                            } else {
//                                pb.setVisibility(View.GONE);
//                                rv.setVisibility(View.VISIBLE);
//                                AdapterForemptyvalue emptyAdapter = new AdapterForemptyvalue(7);
//                                rv.setAdapter(emptyAdapter);
//                                rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                        }
//                    });
//            }else{
//                pb.setVisibility(View.GONE);
//                rv.setVisibility(View.VISIBLE);
//                AdapterForemptyvalue emptyAdapter = new AdapterForemptyvalue(7);
//                rv.setAdapter(emptyAdapter);
//                rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//            }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
//
//    private void updateAdapter() {
//
//        if(madetoroominfo.isEmpty()||imageuri1.isEmpty()||datelist.isEmpty()|| timelist.isEmpty()|| tenanttransactionno.isEmpty()
//        ||transactionidlist.isEmpty()|| amountlist.isEmpty()|| transferidlist.isEmpty())
//        {
//            pb.setVisibility(View.GONE);
//            rv.setVisibility(View.VISIBLE);
//            AdapterForemptyvalue emptyAdapter = new AdapterForemptyvalue(7);
//            rv.setAdapter(emptyAdapter);
//            rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//
//        }
//        if (adapter == null) {
//            adapter = new AdapterForOwnerpart7(
//                    madetoroominfo.toArray(new String[0]),
//                    imageuri1.toArray(new Uri[0]),
//                    datelist.toArray(new String[0]),
//                    timelist.toArray(new String[0]),
//                    tenanttransactionno.toArray(new Long[0]),
//                    transactionidlist.toArray(new String[0]),
//                    amountlist.toArray(new String[0]),
//                    transferidlist.toArray(new String[0])
//            );
//
//            rv.setAdapter(adapter);
//            rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//            pb.setVisibility(View.GONE);
//            rv.setVisibility(View.VISIBLE);
//
//            adapter.setListener(new AdapterForOwnerpart7.Listener1() {
//                @Override
//                public void itemclickedforcheckingroomoccupied(int position, String s) {
//                    if (listenerhome1 != null) {
//                        listenerhome1.itemclicked2inownerrooms(position, s);
//                    }
//                }
//            }, new AdapterForOwnerpart7.Listener2() {
//                @Override
//                public void cancelbookingfortenant(int id, String s, String tenanttrano, String transactionid, String amount, String transferid,String date,String time) {
//                    if (listenerhome2 != null) {
//                        listenerhome2.bookingcancellation(id, s, tenanttrano, transactionid, amount, transferid,date,time);
//                    }
//                }
//            });
//        } else {
//            adapter.notifyDataSetChanged();
//        }
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        listenerhome1 = (Listenerhome1) context;
//        listenerhome2 = (Listenerhome2) context;
//    }
//}


//package com.example.bookandpostroom;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.snackbar.Snackbar;
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
//public class fragmentforownedrooms extends Fragment {
//    interface Listenerhome1 {
//        void itemclicked2inownerrooms(int position, String s);
//    }
//    interface Listenerhome2{
//
//        void bookingcancellation(int position,String s,String tenanttranno,String transactionid,String amount,String transferid);
//    }
//List<String> madetoroominfo=new ArrayList<>();
//    List<Uri> imageuri1=new ArrayList<>();
//    List<String> datelist=new ArrayList<>();
//    List<String> timelist=new ArrayList<>();
//List<String> transferidlist=new ArrayList<>();
//    List<Long> tenanttransactionno=new ArrayList<>();
//    List<String> transactionidlist=new ArrayList<>();
//    List<String>amountlist=new ArrayList<>();
//
//    long x;
//    private long countroomoccupied;
//    private Listenerhome1 listenerhome1;
//    private Listenerhome2 listenerhome2;
//
//    private int index;
//    private String[] caption;
//    private Uri[] imageuri;
//    private String[]datearray;
//    private String[]timearray;
//    private long temp;
//    private Long[]ownertrannoarray;
//    private Long[]tenanttrannoarray;
//    private String[]transactionidarray;
//    private String[]amountarray;
//    private long finalIndex=0;
//    private long finalIndex2=0;
//    SwipeRefreshLayout swipeRefreshLayout;
//    ProgressBar pb;
//    RecyclerView rv;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View  view =  inflater.inflate(R.layout.fragment_fragmentforhome2, container, false);
//
// rv=view.findViewById(R.id.rv);
//
//
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
//        if (account != null) {
//            String name = account.getDisplayName();
//            String id = account.getId();
//
//             pb=view.findViewById(R.id.pb);
//            pb.setVisibility(View.VISIBLE);
//            swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
//            swipeRefreshLayout.setOnRefreshListener(() -> {
//
//
//                imageuri1.clear();
//                datelist.clear();
//                timelist.clear();
//                transferidlist.clear();
//                tenanttransactionno.clear();
//                transactionidlist.clear();
//                amountlist.clear();
//                finalIndex=0;
//                finalIndex2=0;
//                index=0;
//                rv.setAdapter(null);
//                loaddata(name, id);
//                new android.os.Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
//            });
//loaddata(name,id);
//        }
//
//        return view;
//    }
//    public void loaddata(String name,String id)
//    {DatabaseReference dbrroom = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("transactionmadeno");
//        dbrroom.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Long s=snapshot.getChildrenCount();
//
//                if ( s > 0) {
//
//                    List<DataSnapshot> childrenList = new ArrayList<>();  // Create a list to store children
//
//                    for (DataSnapshot chilld : snapshot.getChildren()) {
//                        childrenList.add(chilld);  // Add each child DataSnapshot to the list
//                    }
//                    for( DataSnapshot chilled : snapshot.getChildren())
//                    {
//
//
//
//                        // Preserve index for async callbacks
//                        String transactionInfo = chilled.child("info").getValue(String.class);
//                        String tenantinfo = name + "idstarts" + id + "idends";
//
//                        String date=chilled.child("transactionDate").getValue(String.class);
//                        String time=chilled.child("transactionTime").getValue(String.class);
//
//                        String transferid=chilled.child("transferid").getValue(String.class);
//                        long tenanttranono=chilled.child("transactionno").getValue(Long.class);
//                        String transactionid=chilled.child("transactionid").getValue(String.class);
//                        String amount=chilled.child("amount").getValue(String.class);
//                        String ownername = transactionInfo.split("idstarts")[0];
//                        String ownerId = transactionInfo.split("idstarts")[1].split("idends")[0];
//
//                        String roomno=transactionInfo.split("idendsroom")[1];
//
//
//                        DatabaseReference dbr=FirebaseDatabase.getInstance().getReference("google").child(ownername)
//                                .child(ownerId).child("owner").child("rooms").child("roomId:"+transactionInfo).child("grabbedby");
//                        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//
//                                long total=snapshot.getChildrenCount();
//                                for(DataSnapshot dd:snapshot.getChildren())
//                                {
//                                    String s=dd.getValue(String.class);
//                                    if(s!=null && s.contains(name+"idstarts"+id+"idends"))
//                                    {finalIndex2++;
//
//                                        StorageReference kk=FirebaseStorage.getInstance().getReference("ownerPhotos").child(ownername).child(ownerId).child("images"+roomno);
//                                            kk.list(1).addOnSuccessListener(listResult -> {
//                                                if (!listResult.getItems().isEmpty()) {
//                                                    StorageReference firstImageRef = listResult.getItems().get(0); // Get the first image
//
//                                                    firstImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                                                        imageuri1.add(uri);
//                                                        madetoroominfo.add(transactionInfo);
//                                                        datelist.add(date);
//                                                        timelist.add(time);
//transferidlist.add(transferid);
//                                                        tenanttransactionno.add(tenanttranono);
//                                                        transactionidlist.add(transactionid);
//                                                        amountlist.add(amount);
//                                                        if(finalIndex2==total )
//                                                        {
//
//                                                            Uri[] imageuri1Array = imageuri1.toArray(new Uri[0]);
//                                                            String[] madetoroominfoArray = madetoroominfo.toArray(new String[0]);
//                                                            datearray=datelist.toArray(new String[0]);
//                                                            timearray=timelist.toArray(new String[0]);
//                                                           String transferidlistarray[]=transferidlist.toArray(new String[0]);
//
//                                                            tenanttrannoarray=tenanttransactionno.toArray(new Long[0]);
//                                                            transactionidarray=transactionidlist.toArray(new String[0]);
//                                                            amountarray=amountlist.toArray(new String[0]);
//                                                            rv.setVisibility(View.VISIBLE);
//                                                            pb.setVisibility(View.GONE);
//
//
//                                                            AdapterForOwnerpart7 adapter = new AdapterForOwnerpart7(madetoroominfoArray, imageuri1Array,datearray,timearray,tenanttrannoarray,transactionidarray,amountarray,transferidlistarray);
//                                                            rv.setAdapter(adapter);
//                                                            rv.setLayoutManager(new GridLayoutManager(getActivity(), 1));
//                                                            adapter.setListener(new AdapterForOwnerpart7.Listener1() {
//                                                                @Override
//                                                                public void itemclickedforcheckingroomoccupied(int position, String s) {
//                                                                    if (listenerhome1 != null) {
//                                                                        listenerhome1.itemclicked2inownerrooms(position, s);
//                                                                    }
//                                                                }
//                                                            }, new AdapterForOwnerpart7.Listener2() {
//                                                                @Override
//                                                                public void cancelbookingfortenant(int id, String s,String tenanttrano,String transactionid,String amount,String transferid) {
//                                                                    if(listenerhome2!=null)
//                                                                    {
//                                                                        listenerhome2.bookingcancellation(id,s,tenanttrano,transactionid,amount,transferid);
//                                                                    }
//                                                                }
//                                                            });
//
//
//
//
//                                                        }
//
//
//                                                        // Use this URL to load the image with Glide or Picasso
//                                                    }).addOnFailureListener(e -> Log.e("FirebaseImage", "Failed to get URL", e));
//                                                } else {
//                                                    Log.d("FirebaseImage", "No images found");
//                                                }
//                                            }).addOnFailureListener(e -> Log.e("FirebaseImage", "Failed to list images", e));
//
//
//
//
//
//
//                                    }
//
//
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                            }
//                        });
//
//
//
//
//                    }
//                } else {
//
//                    rv.setVisibility(View.VISIBLE);
//                    pb.setVisibility(View.GONE);
//                    AdapterForemptyvalue adapter = new AdapterForemptyvalue(7);
//                    rv.setAdapter(adapter);
//                    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                    rv.setLayoutManager(glm);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//
//
//
//    }
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        this.listenerhome1 = (Listenerhome1) context;
//        this.listenerhome2=(Listenerhome2) context;
//    }
//}
