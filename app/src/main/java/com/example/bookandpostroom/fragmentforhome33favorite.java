package com.example.bookandpostroom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class fragmentforhome33favorite extends Fragment {

    interface Listenerhome2 {
        void itemclicked3(int position, String s);
    }

    interface Favoritelistener {
        void favoriteclick(int position, String s);
    }

    Listenerhome2 listenerhome2;
    Favoritelistener favoritelistener;

    // Full list of favorites from SQLite
    private List<String> favoriteRoomIds = new ArrayList<>();

    // Intersection of SQLite favorites & Firebase rooms
    private List<String> firebaseFavoriteRooms = new ArrayList<>();

    // Pagination
    private int batchSize = 4;
    private int currentIndex = 0;
    private boolean isLoading = false;

    // Data lists
    private List<String> chilledcaptions = new ArrayList<>();
    private List<String> chilledroomname = new ArrayList<>();
    private List<String> chilledroomaddresscombined = new ArrayList<>();
    private List<String> chilledGenderr = new ArrayList<>();
    private List<String> chilledroomprice = new ArrayList<>();
    private List<Uri> chilledimages = new ArrayList<>();

    private String[] captions;
    private String[] roomname;
    private String[] roomaddresscombined;
    private String[] Genderr;
    private String[] roomprice;
    private Uri[] images;

    private AdapterForOwnerpartfavorite adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView rv;
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmentforhome33, container, false);
        rv = view.findViewById(R.id.rv);
        pb = view.findViewById(R.id.pb);

        adapter = new AdapterForOwnerpartfavorite(new String[0], new String[0], new String[0],
                new String[0], new String[0], new String[0], new Uri[0]);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load initial data

        loadMore(rv);

        // Infinite scroll
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm != null && lm.findLastVisibleItemPosition() >= chilledcaptions.size() - 2 && !isLoading) {
                    loadMore(rv);
                }
            }
        });

        // Swipe-to-refresh
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            chilledcaptions.clear();
            chilledroomname.clear();
            chilledroomaddresscombined.clear();
            chilledGenderr.clear();
            chilledroomprice.clear();
            chilledimages.clear();
            firebaseFavoriteRooms.clear();
            currentIndex = 0;
            isLoading = false;

            adapter = new AdapterForOwnerpartfavorite(new String[0], new String[0], new String[0],
                    new String[0], new String[0], new String[0], new Uri[0]);
            rv.setAdapter(adapter);

            loadMore(rv);

            new android.os.Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 1000);
        });

        return view;
    }

    public void loadMore(RecyclerView rv) {
        if (isLoading) return;
        isLoading = true;
        pb.setVisibility(View.VISIBLE);

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        favoriteRoomIds = dbHelper.getFavoriteRooms(); // Favorite IDs only

        DatabaseReference dss = FirebaseDatabase.getInstance().getReference("rooms");

        // First time: fetch Firebase rooms and intersect with SQLite favorites
        if (firebaseFavoriteRooms.isEmpty()) {
            dss.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    firebaseFavoriteRooms.clear();
                    for (DataSnapshot d : snapshot.getChildren()) {
                        String roomId = d.getValue(String.class);
                        if (favoriteRoomIds.contains(roomId)) {
                            firebaseFavoriteRooms.add(roomId);
                        }
                    }
                    loadBatch();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    isLoading = false;
                    pb.setVisibility(View.GONE);
                }
            });
        } else {
            // Subsequent loads: just take next batch
            loadBatch();
        }
    }

    private void loadBatch() {
        if (currentIndex >= firebaseFavoriteRooms.size()) {
            pb.setVisibility(View.GONE);
            return;
        }

        int endIndex = Math.min(currentIndex + batchSize, firebaseFavoriteRooms.size());
        List<String> batch = firebaseFavoriteRooms.subList(currentIndex, endIndex);

        chilledcaptions.addAll(batch);
        captions = chilledcaptions.toArray(new String[0]);
        currentIndex = endIndex;

        fetchdata(batch);
    }

    public void fetchdata(List<String> batch) {
        AtomicInteger counter = new AtomicInteger(0);
        int total = batch.size();

        for (String jj : batch) {
            String ownername = jj.split("idstarts")[0];
            String ownerid = jj.split("idstarts")[1].split("idends")[0];

            DatabaseReference ds = FirebaseDatabase.getInstance()
                    .getReference("google")
                    .child(ownername).child(ownerid).child("owner");

            ds.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("accountsetup").getValue(Long.class) == 1 &&
                            snapshot.child("accountverified").getValue(Long.class) == 1) {

                        String location = snapshot.child("rooms").child("roomId:" + jj)
                                .child("roomlocation").getValue(String.class);

                        String district = snapshot.child("rooms").child("roomId:" + jj)
                                .child("district").getValue(String.class);

                        String roompice = String.valueOf(snapshot.child("rooms").child("roomId:" + jj)
                                .child("roomprice").getValue(Long.class));

                        chilledroomname.add(snapshot.child("rooms").child("roomId:" + jj)
                                .child("roomname").getValue(String.class));

                        chilledroomprice.add(roompice);
                        chilledroomaddresscombined.add(location + " " + district);
                        chilledGenderr.add(snapshot.child("rooms").child("roomId:" + jj)
                                .child("bookedbygender").getValue(String.class));
                    }

                    if (counter.incrementAndGet() == total) {
                        loadimages(batch);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (counter.incrementAndGet() == total) {
                        loadimages(batch);
                    }
                }
            });
        }
    }

    private void loadimages(List<String> batch) {
        AtomicInteger taskCounter = new AtomicInteger(0);
        int totalTasks = batch.size();

        for (String kk : batch) {
            String ownername = kk.split("idstarts")[0];
            String ownerid = kk.split("idstarts")[1].split("idends")[0];
            String number = kk.split("idendsroom")[1];

            StorageReference sr = FirebaseStorage.getInstance()
                    .getReference("ownerPhotos")
                    .child(ownername).child(ownerid).child("images" + number);

            sr.list(1).addOnSuccessListener(listResult -> {
                if (!listResult.getItems().isEmpty()) {
                    listResult.getItems().get(0).getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                chilledimages.add(uri);
                                checkTaskCompletion(taskCounter.incrementAndGet(), totalTasks);
                            })
                            .addOnFailureListener(e -> checkTaskCompletion(taskCounter.incrementAndGet(), totalTasks));
                } else {
                    checkTaskCompletion(taskCounter.incrementAndGet(), totalTasks);
                }
            }).addOnFailureListener(e -> {
                Log.e("Firebase", "Failed to list images", e);
                checkTaskCompletion(taskCounter.incrementAndGet(), totalTasks);
            });
        }
    }

    private void checkTaskCompletion(int completed, int total) {
        if (completed == total) {
            updateUI();
        }
    }
    public void removeItemFromAdapter(int position) {


        if (adapter != null && position >= 0 && position < chilledcaptions.size()) {

            String removedItem = chilledcaptions.get(position);


            // Remove from all data lists
            chilledcaptions.remove(position);
            chilledroomname.remove(position);
            chilledroomaddresscombined.remove(position);
            chilledGenderr.remove(position);
            chilledroomprice.remove(position);
            chilledimages.remove(position);

            // Remove from Firebase favorites list
            if (position < firebaseFavoriteRooms.size()) {
                firebaseFavoriteRooms.remove(position);
            }

            // Update adapter arrays
            adapter.captions = chilledcaptions.toArray(new String[0]);
            adapter.roomname = chilledroomname.toArray(new String[0]);
            adapter.roomaddresscombined = chilledroomaddresscombined.toArray(new String[0]);
            adapter.Genderr = chilledGenderr.toArray(new String[0]);
            adapter.roomprice = chilledroomprice.toArray(new String[0]);
            adapter.roomid = chilledcaptions.toArray(new String[0]);
            adapter.imageurilist = chilledimages.toArray(new Uri[0]);

            // Update favorites set in adapter
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
            Set<String> favorites = new HashSet<>(dbHelper.getFavoriteRooms());
            adapter.setFavorites(favorites);



            // Use notifyDataSetChanged for now to ensure it works
            adapter.notifyDataSetChanged();


            if (currentIndex > chilledcaptions.size()) {
                currentIndex = chilledcaptions.size();
            }

            android.util.Log.d("DEBUG", "=== removeItemFromAdapter END ===");

        } else {

        }
    }

    private void updateUI() {
        roomname = chilledroomname.toArray(new String[0]);
        roomaddresscombined = chilledroomaddresscombined.toArray(new String[0]);
        roomprice = chilledroomprice.toArray(new String[0]);
        Genderr = chilledGenderr.toArray(new String[0]);
        images = chilledimages.toArray(new Uri[0]);

        if (adapter != null) {
            adapter.captions = captions;
            adapter.roomname = roomname;
            adapter.roomaddresscombined = roomaddresscombined;
            adapter.Genderr = Genderr;
            adapter.roomprice = roomprice;
            adapter.roomid = captions;
            adapter.imageurilist = images;

            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);

            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
            Set<String> favorites = new HashSet<>(dbHelper.getFavoriteRooms());
            adapter.setFavorites(favorites);

            adapter.notifyItemRangeInserted(chilledcaptions.size() - batchSize, batchSize);
            adapter.setListener((position, s) -> {
                if (listenerhome2 != null) listenerhome2.itemclicked3(position, s);
            }, (position, s) -> {
                if (favoritelistener != null) favoritelistener.favoriteclick(position, s);
            });
        }

        isLoading = false;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listenerhome2 = (Listenerhome2) context;
        this.favoritelistener = (Favoritelistener) context;
    }
}
