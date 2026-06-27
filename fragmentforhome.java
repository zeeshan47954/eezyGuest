package com.example.bookandpostroom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class fragmentforhome extends Fragment implements RefreshListener {
    private ExecutorService executorService;
    private Handler mainHandler;

    private ProgressBar progressBar;
    private List<Long> chilledcapacity=new ArrayList<>();

    private String[] captions;
    private Long[] capacity;
    private String[] realcaptions;
    private Dialog progressDialog;
    private AdapterForOwnerpart1 adapter;
    private Query query;
    private AtomicInteger a=new AtomicInteger(0);
    private ProgressDialog pd;
    private int x;
    private String lastkey;
    private int start = 0;
    private int end = 4;
    private Uri[]images;
    private List<String> chilledcaptions = new ArrayList<>();
    private List<String> realchilledcaptions=new ArrayList<>();
    private List<Uri> chilledimages=new ArrayList<>();
private boolean isLoading = false;
    RecyclerView rv;
    ProgressBar pb;
    interface Listenerhome {
        void itemclicked(int position, String s);
    }

    interface Listenerhome2 {
        void itemdeleted(int position, String s);
    }

    private Listenerhome listenerhome;
    private Listenerhome2 listenerhome2;

    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Refreshing Home Fragment!", Toast.LENGTH_SHORT).show();

        // Clear previous data
        chilledcaptions.clear();
        chilledcapacity.clear();
        chilledimages.clear();
        lastkey = null;
        a.set(0);
        start = 0;
        end = 4;
        isLoading = false;
pb.setVisibility(View.VISIBLE);
        // Reset adapter with empty data (optional, for visual clarity)
        adapter.captions = new String[0];
        adapter.imageuris = new Uri[0];
        adapter.roomcapacity = new Long[0];
        adapter.notifyDataSetChanged();

        // Load fresh data
        loadMore();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragmentforhome, container, false);

 rv=view.findViewById(R.id.rv);
 pb=view.findViewById(R.id.pb);
adapter=new AdapterForOwnerpart1(new String[0],new Uri[0],new Long[0]);
        rv.setAdapter(adapter);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);
        loadMore();

        // Add scroll listener
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                // Check if the user has scrolled near the bottom
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null &&
                        layoutManager.findLastVisibleItemPosition() >= chilledcaptions.size() - 10 && !isLoading) {
                    loadMore();
                }
            }
        });




        return view;
    }

    private void loadMore()
    {
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getContext());
        String name= account.getDisplayName();
        String id= account.getId();
        isLoading = true;
DatabaseReference dss=FirebaseDatabase.getInstance().getReference("google")
        .child(name).child(id).child("owner").child("rooms");
        if (lastkey == null) {
            query = dss.orderByKey().limitToFirst(5);
        } else {
            query = dss.orderByKey().startAfter(lastkey).limitToFirst(4);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()) {
                    for (DataSnapshot d : snapshot.getChildren()) {
                        String key = d.getKey();
                        String roomValue = key.substring("roomId:".length());
                        chilledcaptions.add(roomValue);




                        lastkey = d.getKey();

                    }
                    // Update captions and adapter
                    captions = chilledcaptions.toArray(new String[0]);


                    if (captions.length > 0)
                        fetchdata(name, id);
                    else {


                        showEmptyState();


                    }
                }
                else{


                }
                // Loading complete
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false; // Reset loading state on error
            }
        });

    }

    public void fetchdata(String name,String id)
    {


        for(String jj:captions)
        {



            DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google")
                    .child(name).child(id).child("owner").child("rooms").child("roomId:"+jj);

            ds.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {



                    long dd=snapshot.child("roomcapacityreached").getValue(long.class);

                    chilledcapacity.add(dd);
                    x=a.incrementAndGet();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }





        loadimages(name,id);



    }

    public void loadimages(String name,String id)
    {

        chilledimages.clear(); // Clear previous images
        AtomicInteger taskCounter = new AtomicInteger(0); // Track completed tasks
        int totalTasks = captions.length;


        for(String jj:captions)
        {String number=jj.split("idendsroom")[1];
            StorageReference sr = FirebaseStorage.getInstance().getReference("ownerPhotos")
                    .child(name).child(id).child("images" + number);
            sr.list(1) // Limit to 1 item
                    .addOnSuccessListener(listResult -> {
                        if (!listResult.getItems().isEmpty()) {
                            StorageReference firstImageRef = listResult.getItems().get(0);
                            firstImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                chilledimages.add(uri); // Add URI to the list
                                checkTaskCompletion(taskCounter.incrementAndGet(), totalTasks);
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    checkTaskCompletion(taskCounter.incrementAndGet(), totalTasks);
                                }
                            });
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to list images", e));

          /*  sr.getDownloadUrl().addOnSuccessListener(uri -> {
                chilledimages.add(uri); // Add URI to the list
                checkTaskCompletion(taskCounter.incrementAndGet(), totalTasks); // Check if all tasks are done
            }).addOnFailureListener(e -> {
                // Handle failure (optional logging/toast)
                checkTaskCompletion(taskCounter.incrementAndGet(), totalTasks); // Still mark as completed
            });*/

        }



    }
    private void checkTaskCompletion(int completed, int total) {
        if (completed == total) {
            updateUI(); // Update the UI after all tasks are completed
        }
    }
public void updateUI()
{
    if(rv.getVisibility()==View.GONE)
    {
        pb.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
    }
    if(pb.getVisibility()==View.VISIBLE)
    {
        pb.setVisibility(View.GONE);
    }
    capacity=chilledcapacity.toArray(new Long[0]);

    images = chilledimages.toArray(new Uri[0]);
    adapter.captions = captions;
    adapter.imageuris=images;

    adapter.roomcapacity=capacity;

    adapter.notifyItemRangeInserted(start, chilledcaptions.size());

    adapter.setListener((position, s) -> {
        if (listenerhome != null) {
            listenerhome.itemclicked(position, s);
        }
    },(position,s)->{
     if(listenerhome2!=null)
     {
         listenerhome2.itemdeleted(position,s);
     }

    });

    start = chilledcaptions.size();
    end += 4;
    isLoading = false;
}

    private void showEmptyState() {
       pb.setVisibility(View.GONE);
        rv.setVisibility(View.VISIBLE);
        AdapterForemptyvalue adapter = new AdapterForemptyvalue(1);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
        pd.dismiss();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listenerhome = (Listenerhome) context;
        this.listenerhome2 = (Listenerhome2) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}
