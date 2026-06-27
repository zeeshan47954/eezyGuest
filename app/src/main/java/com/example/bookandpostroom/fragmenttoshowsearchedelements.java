package com.example.bookandpostroom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


public class fragmenttoshowsearchedelements extends Fragment {


    interface Listenerhome2 {
        void itemclicked3(int position, String s);
    }
    interface Favoritelistener{
        void favoriteclick(int position,String s);
    }
    private DatabaseReference roomsRef;
    private StorageReference storageRef;
   Listenerhome2 listenerhome2;
    Favoritelistener favoritelistener;
    ArrayList<String> receivedList;
    RecyclerView rv;
    private final List<RoomData> roomDataList = new ArrayList<>();
    private AdapterForOwnerpart33 adapter;
    private static class RoomData {
        String caption;
        String roomname;
        String roomaddress;
        String gender;
        String roomprice;
        Uri imageUri;
        Float averagescore;
        Long reviewcount;
        Boolean occupied;

        RoomData(String caption) {
            this.caption = caption;
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        storageRef = FirebaseStorage.getInstance().getReference("ownerPhotos");
        if (getArguments() != null) {
            receivedList = getArguments().getStringArrayList("roomList");
        }
        View view=inflater.inflate(R.layout.fragment_fragmenttoshowsearchedelements, container, false);
        rv=view.findViewById(R.id.rv);
        adapter = new AdapterForOwnerpart33(new String[0], new String[0], new String[0],
                new String[0], new String[0], new String[0], new Uri[0],new Float[0],new Long[0],new Boolean[0]);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchBatchData(receivedList);
        // Inflate the layout for this fragment

    return view;
    }
    private void fetchBatchData(List<String> captions) {
        AtomicInteger pendingRequests = new AtomicInteger(captions.size());
        Map<String, RoomData> tempDataMap = new HashMap<>();

        for (String caption : captions) {
            RoomData roomData = new RoomData(caption);
            tempDataMap.put(caption, roomData);

            String[] parts = caption.split("idstarts");
            if (parts.length < 2) {
                checkCompletion(pendingRequests, tempDataMap, captions);
                continue;
            }

            String ownername = parts[0];
            String[] idParts = parts[1].split("idends");
            if (idParts.length < 2) {
                checkCompletion(pendingRequests, tempDataMap, captions);
                continue;
            }

            String ownerid = idParts[0];
            String roomId = "roomId:" + caption;

            DatabaseReference ownerRef = FirebaseDatabase.getInstance()
                    .getReference("google")
                    .child(ownername)
                    .child(ownerid)
                    .child("owner");

            ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        Long accountSetup = snapshot.child("accountsetup").getValue(Long.class);
                        Long accountVerified = snapshot.child("accountverified").getValue(Long.class);

                        if (accountSetup != null && accountSetup == 1 &&
                                accountVerified != null && accountVerified == 1) {

                            DataSnapshot roomSnapshot = snapshot.child("rooms").child(roomId);
if(roomSnapshot.child("grabbedby").exists())
{
    roomData.occupied=true;
}
else{
    roomData.occupied=false;
}
                            roomData.roomname = roomSnapshot.child("roomname").getValue(String.class);
                            String location = roomSnapshot.child("roomlocation").getValue(String.class);
                            String district = roomSnapshot.child("district").getValue(String.class);
                            roomData.roomaddress = (location != null ? location : "") + " " +
                                    (district != null ? district : "");
                            roomData.gender = roomSnapshot.child("bookedbygender").getValue(String.class);
                            Long price = roomSnapshot.child("roomprice").getValue(Long.class);
                            roomData.roomprice = price != null ? String.valueOf(price) : "0";
roomData.averagescore=roomSnapshot.child("reviews").child("averageScore").getValue(Float.class);
roomData.reviewcount=roomSnapshot.child("reviews").child("actualreview").getChildrenCount();
                            loadImage(caption, roomData, () ->
                                    checkCompletion(pendingRequests, tempDataMap, captions));
                        } else {
                            checkCompletion(pendingRequests, tempDataMap, captions);
                        }
                    } catch (Exception e) {
                        Log.e("Firebase", "Error parsing room data", e);
                        checkCompletion(pendingRequests, tempDataMap, captions);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    checkCompletion(pendingRequests, tempDataMap, captions);
                }
            });
        }
    }

    private void loadImage(String caption, RoomData roomData, Runnable onComplete) {
        String[] parts = caption.split("idstarts");
        String ownername = parts[0];
        String ownerid = parts[1].split("idends")[0];
        String number = caption.split("idendsroom")[1];

        StorageReference imageRef = storageRef.child(ownername)
                .child(ownerid)
                .child("images" + number);

        imageRef.list(1).addOnSuccessListener(listResult -> {
            if (!listResult.getItems().isEmpty()) {
                listResult.getItems().get(0).getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            roomData.imageUri = uri;
                            onComplete.run();
                        })
                        .addOnFailureListener(e -> onComplete.run());
            } else {
                onComplete.run();
            }
        }).addOnFailureListener(e -> onComplete.run());
    }

    private void checkCompletion(AtomicInteger counter, Map<String, RoomData> dataMap,
                                 List<String> captions) {
        if (counter.decrementAndGet() == 0) {
            updateUIWithBatch(dataMap, captions);
        }
    }

    private void updateUIWithBatch(Map<String,RoomData> dataMap, List<String> captions) {
        int startPos = roomDataList.size();

        for (String caption : captions) {
            RoomData data = dataMap.get(caption);
            if (data != null && data.roomname != null) {
                roomDataList.add(data);
            }
        }

        String[] captionArray = new String[roomDataList.size()];
        String[] roomnameArray = new String[roomDataList.size()];
        String[] addressArray = new String[roomDataList.size()];
        String[] genderArray = new String[roomDataList.size()];
        String[] priceArray = new String[roomDataList.size()];
        Boolean[] occupiedarray=new Boolean[roomDataList.size()];
        Uri[] imageArray = new Uri[roomDataList.size()];
        Float[] averagereviewarray=new Float[roomDataList.size()];
        Long[] reviewcountarray=new Long[roomDataList.size()];

        for (int i = 0; i < roomDataList.size(); i++) {
            RoomData data = roomDataList.get(i);
            captionArray[i] = data.caption;
            roomnameArray[i] = data.roomname;
            addressArray[i] = data.roomaddress;
            genderArray[i] = data.gender;
            priceArray[i] = data.roomprice;
            imageArray[i] = data.imageUri;
            occupiedarray[i]=data.occupied;
            averagereviewarray[i]=data.averagescore;
            reviewcountarray[i]=data.reviewcount;
        }

        adapter.captions = captionArray;
        adapter.roomname = roomnameArray;
        adapter.roomaddresscombined = addressArray;
        adapter.Genderr = genderArray;
        adapter.roomprice = priceArray;
        adapter.roomid = captionArray;
        adapter.occupied=occupiedarray;
        adapter.imageurilist = imageArray;
        adapter.averagescorearray=averagereviewarray;
        adapter.reviewcountarray=reviewcountarray;

        serachactivity activity = (serachactivity) getActivity();
        if (activity != null && activity.ss != null && activity.ss.isShowing()) {
            activity.ss.dismiss();
            activity.ll1.setVisibility(View.GONE);
            activity.fragmentcontainer.setVisibility(View.VISIBLE);
            activity.searchTextView.setVisibility(View.GONE);
            activity.found.setVisibility(View.VISIBLE);
        }

        rv.setVisibility(View.VISIBLE);


        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        adapter.setFavorites(new HashSet<>(dbHelper.getFavoriteRooms()));

        adapter.notifyItemRangeInserted(startPos, roomDataList.size() - startPos);
        adapter.setListener((position, s) -> {
            if (listenerhome2 != null) {
                listenerhome2.itemclicked3(position, s);
            }
        }, (position, s) -> {
            if (favoritelistener != null) {
                favoritelistener.favoriteclick(position, s);
            }
        });


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listenerhome2 = (Listenerhome2)context;
        this.favoritelistener = (Favoritelistener) context;
    }
}