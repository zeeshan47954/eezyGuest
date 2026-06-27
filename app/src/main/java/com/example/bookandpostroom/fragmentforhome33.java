
package com.example.bookandpostroom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;



public class fragmentforhome33 extends Fragment {

    interface Listenerhome2 {
        void itemclicked3(int position, String s);
    }
    interface Favoritelistener{
        void favoriteclick(int position,String s);
    }
    Listenerhome2 listenerhome2;
    Favoritelistener favoritelistener;
    private List<String> chilledcaptions = new ArrayList<>();
    private List<String> chilledroomname = new ArrayList<>();
private List<Boolean> occupied=new ArrayList<>();
    private List<String> chilledroomaddresscombined = new ArrayList<>();
    private List<String> chilledGenderr = new ArrayList<>();
    private List<String> chilledroomprice = new ArrayList<>();
    private List<Uri> chilledimages=new ArrayList<>();
    private List<Float> averageScores = new ArrayList<>();
    private List<Long> reviewcount= new ArrayList<>();

    private Map<String, Uri> imageCache = new HashMap<>(); // Cache for images by caption
    private String[] captions;
    private String[] roomname;
    private String[] roomaddresscombined;
    private String[] Genderr;
    private String[] roomprice;
    private Uri []images;
    private int start = 0;
    private int end = 4;
    private String lastkey;
    private Query query;
    private int x;
    private AtomicInteger a=new AtomicInteger(0);
    private AdapterForOwnerpart33 adapter;
    private boolean isLoading = false;
    SwipeRefreshLayout swipeRefreshLayout;

    List<Task<Uri>> downloadTasks = new ArrayList<>();

    RecyclerView rv;
    ProgressBar pb;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragmentforhome33, container, false);
        rv=view.findViewById(R.id.rv);

        pb=view.findViewById(R.id.pb);



        adapter = new AdapterForOwnerpart33(new String[0],new String[0],new String[0],new String[0],new String[0],new String[0],new Uri[0],new Float[0],new Long[0],new Boolean[0]); // Initially empty
        rv.setAdapter(adapter);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);

        // Load initial data
        loadMore(rv);

        // Add scroll listener
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Check if the user has scrolled near the bottom
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null &&
                        layoutManager.findLastVisibleItemPosition() >= chilledcaptions.size() - 10 && !isLoading) {
                    loadMore(rv);
                }
            }
        });


        swipeRefreshLayout=view.findViewById(R.id.swipeRefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Reset all data
            chilledcaptions.clear();
            chilledroomname.clear();
            chilledroomaddresscombined.clear();
            chilledGenderr.clear();
            chilledroomprice.clear();
            chilledimages.clear();
            averageScores.clear();
            imageCache.clear();
            occupied.clear();
            lastkey = null;
            start = 0;
            end = 4;
            isLoading = false;

            adapter = new AdapterForOwnerpart33(new String[0],new String[0],new String[0],new String[0],new String[0],new String[0],new Uri[0],new Float[0],new Long[0],new Boolean[0]);
            rv.setAdapter(adapter);
            loadMore(rv);

            // Stop the refresh indicator after short delay in case data load is fast
            new android.os.Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh favorites when returning from favorites activity
        refreshFavorites();
    }

    private void refreshFavorites() {
        if (adapter != null && getContext() != null) {
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
            Set<String> favorites = new HashSet<>(dbHelper.getFavoriteRooms());
            adapter.setFavorites(favorites);
            adapter.notifyDataSetChanged();
        }
    }

    public void loadMore(RecyclerView rv) {
        isLoading = true; // Mark as loading to prevent duplicate calls


        DatabaseReference dss = FirebaseDatabase.getInstance().getReference("rooms");

        // Set up the query based on whether it's the first load or subsequent loads
        if (lastkey == null) {
            query = dss.orderByKey().limitToFirst(4);
        } else {
            query = dss.orderByKey().startAfter(lastkey).limitToFirst(4);
        }

        // Execute query
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    chilledcaptions.add(d.getValue(String.class)); // Add data to list
                    lastkey = d.getKey(); // Update lastkey

                }

                // Update captions and adapter
                captions = chilledcaptions.toArray(new String[0]);

                fetchdata();


                // Loading complete
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false; // Reset loading state on error
            }
        });
    }
    public void fetchdata()
    {
        AtomicInteger dataCounter = new AtomicInteger(0);
        int totalItems = captions.length;

        for(String jj:captions)
        {

            String ownername=jj.split("idstarts")[0];
            String ownerid=jj.split("idstarts")[1].split("idends")[0];

            DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google")
                    .child(ownername).child(ownerid).child("owner");

            ds.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child("accountsetup").exists() && snapshot.child("accountsetup").getValue(long.class)==1 && snapshot.child("accountverified").exists() &&snapshot.child("accountverified").getValue(long.class)==1 )

                    {
if(snapshot.child("grabbedby").exists())
{
    occupied.add(true);
}
else
{
    occupied.add(false);
}
                        String location = snapshot.child("rooms").child("roomId:"+jj).child("roomlocation").getValue(String.class);
averageScores.add(snapshot.child("rooms").child("roomId:"+jj).child("reviews").child("averageScore").getValue(Float.class));
reviewcount.add(snapshot.child("rooms").child("roomId:"+jj).child("reviews").child("actualreview").getChildrenCount());
                        String district =
                                snapshot.child("rooms").child("roomId:"+jj).child("district").getValue(String.class);

                        String roompice=String.valueOf(snapshot.child("rooms").child("roomId:"+jj).child("roomprice").getValue(Long.class));


                        chilledroomname.add(snapshot.child("rooms").child("roomId:"+jj).child("roomname").getValue(String.class));


                        chilledroomprice.add(roompice);

                        chilledroomaddresscombined.add(location + " " + district);
                        chilledGenderr.add(snapshot.child("rooms").child("roomId:"+jj).child("bookedbygender").getValue(String.class));
                        x=a.incrementAndGet();
                    }

                    // Check if all data fetched
                    if (dataCounter.incrementAndGet() == totalItems) {
                        updateUIWithData();
                        // Start loading images after data is displayed
                        loadimages();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (dataCounter.incrementAndGet() == totalItems) {
                        updateUIWithData();
                        loadimages();
                    }
                }
            });


        }



    }

    private void updateUIWithData() {
        roomname = chilledroomname.toArray(new String[0]);
        roomaddresscombined = chilledroomaddresscombined.toArray(new String[0]);
        roomprice = chilledroomprice.toArray(new String[0]);
        Genderr = chilledGenderr.toArray(new String[0]);
      Boolean occupiedarray[]=occupied.toArray(new Boolean[0]);
Float[] averagescorearray=averageScores.toArray(new Float[0]);
Long [] reviewcountarray=reviewcount.toArray(new Long[0]);
        // Initialize images array with nulls (will show shimmer)
        images = new Uri[captions.length];
        for (int i = 0; i < chilledimages.size() && i < images.length; i++) {
            images[i] = chilledimages.get(i);
        }

        if (adapter != null) {
            adapter.captions = captions;
            adapter.roomname = roomname;
            adapter.roomaddresscombined = roomaddresscombined;
            adapter.Genderr = Genderr;
            adapter.roomprice = roomprice;
            adapter.roomid = captions;
            adapter.occupied=occupiedarray;
            adapter.imageurilist = images;
            adapter.averagescorearray=averagescorearray;
            adapter.reviewcountarray=reviewcountarray;

            rv.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);

            MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
            Set<String> favorites = new HashSet<>(dbHelper.getFavoriteRooms());
            adapter.setFavorites(favorites);

            adapter.notifyItemRangeInserted(start, chilledcaptions.size());
            adapter.setListener((position, s) -> {
                if (listenerhome2 != null) {
                    listenerhome2.itemclicked3(position, s);
                }
            },(position,s)->{

                if(favoritelistener!=null)
                {
                    favoritelistener.favoriteclick(position,s);
                }
            });

        }

        // Update pagination counters
        start = chilledcaptions.size();
        end += 4;
    }

    private void loadimages() {
        for (int index = 0; index < captions.length; index++) {
            final int position = index;
            String kk = captions[index];

            // Check if image is already cached
            if (imageCache.containsKey(kk)) {
                updateImageAtPosition(position, imageCache.get(kk));
                continue;
            }

            // Extract ownername, ownerid, and number
            String ownername = kk.split("idstarts")[0];
            String ownerid = kk.split("idstarts")[1].split("idends")[0];
            String number = kk.split("idendsroom")[1];

            // Create a reference to the image
            StorageReference sr = FirebaseStorage.getInstance().getReference("ownerPhotos")
                    .child(ownername).child(ownerid).child("images" + number);

            // Get the download URL
            sr.list(1) // Limit to 1 item
                    .addOnSuccessListener(listResult -> {
                        if (!listResult.getItems().isEmpty()) {
                            StorageReference firstImageRef = listResult.getItems().get(0);
                            firstImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // Cache the image
                                imageCache.put(kk, uri);
                                // Update UI immediately
                                updateImageAtPosition(position, uri);
                            }).addOnFailureListener(e -> {
                                Log.e("Firebase", "Failed to get download URL", e);
                            });
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to list images", e));
        }

        isLoading = false; // Reset loading flag
    }

    private void updateImageAtPosition(int position, Uri uri) {
        if (adapter != null && position < adapter.imageurilist.length) {
            adapter.imageurilist[position] = uri;
            // Notify only this specific item changed
            adapter.notifyItemChanged(position);
        }
    }









    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listenerhome2 = (Listenerhome2) context;
        this.favoritelistener=(Favoritelistener) context;
    }
}