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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.storage.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class fragmentforhome extends Fragment implements RefreshListener {
    // Optimized thread pool - uses optimal core count
    private static final int THREAD_POOL_SIZE = Math.max(4, Runtime.getRuntime().availableProcessors());
    private ExecutorService executorService;
    private Handler mainHandler;
    private ProgressBar progressBar;
    private String[] captions;
    private Long[] capacity;
    private String[] realcaptions;
    private Dialog progressDialog;
    private AdapterForOwnerpart1 adapter;
    private Query query;
    private ProgressDialog pd;
    private String lastkey;
    private int start = 0;
    private int end = 4;

    // Use ArrayList with initial capacity to reduce resizing
    private List<String> chilledcaptions = new ArrayList<>(20);
    private List<Uri> chilledimages = new ArrayList<>(20);
    private List<Boolean> occupied = new ArrayList<>(20);
    private boolean isLoading = false;

    // Cache user info to avoid repeated calls
    private String cachedName;
    private String cachedId;

    RecyclerView rv;
    ProgressBar pb;
    private FirebaseAuth firebaseAuth;
    interface Listenerhome {
        void itemclicked(int position, String s);
    }

    interface Listenerhome2 {
        void itemdeleted(int position, String s);
    }
ValueEventListener v1;
    private Listenerhome listenerhome;
    private Listenerhome2 listenerhome2;
    DatabaseReference dd;
    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Refreshing Home Fragment!", Toast.LENGTH_SHORT).show();

        chilledcaptions.clear();
        occupied.clear();
        chilledimages.clear();
        lastkey = null;
        start = 0;
        end = 4;
        isLoading = false;
        pb.setVisibility(View.VISIBLE);

        adapter.captions = new String[0];
        adapter.imageuris = new Uri[0];
        adapter.occupied = new Boolean[0];
        adapter.notifyDataSetChanged();

        loadMore();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentforhome, container, false);

        rv = view.findViewById(R.id.rv);
        pb = view.findViewById(R.id.pb);
        adapter = new AdapterForOwnerpart1(new String[0], new Uri[0], new Boolean[0]);
        rv.setAdapter(adapter);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(getContext());
        rv.setLayoutManager(lm);

        // Enable RecyclerView optimizations
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);
        rv.setDrawingCacheEnabled(true);
        rv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Optimized thread pool with cached threads for faster response
        executorService = new ThreadPoolExecutor(
                THREAD_POOL_SIZE,
                THREAD_POOL_SIZE * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        mainHandler = new Handler(Looper.getMainLooper());

        // Cache user credentials once
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account != null) {
            cachedName = account.getDisplayName();
            cachedId = account.getUid();
        }
 dd=FirebaseDatabase.getInstance().getReference("google").child(cachedName).child(cachedId).child("owner").child("roomsposted");
       v1=new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(!snapshot.exists() || snapshot.getValue(Long.class)==0)
               {
                   showEmptyState();
               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               showEmptyState();
           }
       };
        dd.addValueEventListener(v1);

        loadMore();
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null &&
                        layoutManager.findLastVisibleItemPosition() >= chilledcaptions.size() - 10 && !isLoading) {
                    loadMore();
                }
            }
        });

        return view;
    }

    private void loadMore() {
        // Use cached credentials
        if (cachedName == null || cachedId == null) {
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser account = firebaseAuth.getCurrentUser();
            if (account != null) {
                cachedName = account.getDisplayName();
                cachedId = account.getUid();
            }
        }

        isLoading = true;

        DatabaseReference dss = FirebaseDatabase.getInstance().getReference("google")
                .child(cachedName).child(cachedId).child("owner").child("rooms");

        if (lastkey == null) {
            query = dss.orderByKey().limitToFirst(5);
        } else {
            query = dss.orderByKey().startAfter(lastkey).limitToFirst(4);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<String> newCaptions = new ArrayList<>();
                    for (DataSnapshot d : snapshot.getChildren()) {
                        String key = d.getKey();
                        String roomValue = key.substring("roomId:".length());
                        newCaptions.add(roomValue);
                        lastkey = d.getKey();
                    }

                    if (!newCaptions.isEmpty()) {
                        fetchAllDataParallel(cachedName, cachedId, newCaptions);
                    } else {
                        isLoading = false;
                        if (chilledcaptions.isEmpty()) {
                            showEmptyState();
                        }
                    }
                } else {
                    isLoading = false;
                    if (chilledcaptions.isEmpty()) {
                        showEmptyState();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isLoading = false;

            }
        });
    }

    private void fetchAllDataParallel(String name, String id, List<String> newCaptions) {
        executorService.execute(() -> {
            try {
                // Use invokeAll for better performance - submits all at once
                List<Callable<RoomData>> tasks = new ArrayList<>(newCaptions.size());

                for (String caption : newCaptions) {
                    tasks.add(() -> fetchSingleRoomData(name, id, caption));
                }

                // invokeAll is more efficient than individual submits
                List<Future<RoomData>> futures = executorService.invokeAll(tasks, 15, TimeUnit.SECONDS);

                // Preallocate list with exact size
                List<RoomData> roomDataList = new ArrayList<>(futures.size());

                for (Future<RoomData> future : futures) {
                    try {
                        RoomData data = future.get(1, TimeUnit.MILLISECONDS); // Already waited in invokeAll
                        if (data != null) {
                            roomDataList.add(data);
                        }
                    } catch (TimeoutException e) {
                        // Already timed out in invokeAll
                        Log.w("Firebase", "Task already timed out");
                    } catch (Exception e) {
                        Log.e("Firebase", "Error fetching room data", e);
                    }
                }

                // Single UI update on main thread
                mainHandler.post(() -> {
                    // Batch add for better performance
                    int startPos = chilledcaptions.size();
                    for (RoomData data : roomDataList) {
                        chilledcaptions.add(data.caption);
                        occupied.add(data.occupied);
                        chilledimages.add(data.imageUri);
                    }
                    updateUI();
                });

            } catch (Exception e) {
                Log.e("Firebase", "Error in parallel fetch", e);
                mainHandler.post(() -> {
                    isLoading = false;

                });
            }
        });
    }

    private RoomData fetchSingleRoomData(String name, String id, String caption) {
        try {
            RoomData data = new RoomData();
            data.caption = caption;

            // Optimized: Fetch both tasks in parallel using CompletableFuture-like pattern
            CountDownLatch latch = new CountDownLatch(2);

            // Fetch occupation status
            DatabaseReference ds = FirebaseDatabase.getInstance().getReference("google")
                    .child(name).child(id).child("owner").child("rooms").child("roomId:" + caption);

            final boolean[] occupiedResult = {false};
            ds.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    occupiedResult[0] = task.getResult().child("grabbedby").exists();
                }
                latch.countDown();
            });

            // Fetch image in parallel
            String number = caption.split("idendsroom")[1];
            StorageReference sr = FirebaseStorage.getInstance().getReference("ownerPhotos")
                    .child(name).child(id).child("images" + number);

            final Uri[] imageResult = {null};
            sr.list(1).addOnCompleteListener(task -> {
                try {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().getItems().isEmpty()) {
                        StorageReference firstImageRef = task.getResult().getItems().get(0);
                        firstImageRef.getDownloadUrl().addOnCompleteListener(uriTask -> {
                            if (uriTask.isSuccessful()) {
                                imageResult[0] = uriTask.getResult();
                            }
                            latch.countDown();
                        }).addOnFailureListener(e -> latch.countDown());
                    } else {
                        latch.countDown();
                    }
                } catch (Exception e) {
                    latch.countDown();
                }
            }).addOnFailureListener(e -> latch.countDown());

            // Wait for both operations with timeout
            boolean completed = latch.await(5, TimeUnit.SECONDS);

            data.occupied = occupiedResult[0];
            data.imageUri = imageResult[0];

            return data;
        } catch (Exception e) {
            Log.e("Firebase", "Error fetching single room data", e);
            return null;
        }
    }

    private static class RoomData {
        String caption;
        Boolean occupied;
        Uri imageUri;
    }

    public void updateUI() {
        if (rv.getVisibility() == View.GONE) {
            pb.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
        if (pb.getVisibility() == View.VISIBLE) {
            pb.setVisibility(View.GONE);
        }

        Boolean[] occupiedarray = occupied.toArray(new Boolean[0]);
        Uri[] images = chilledimages.toArray(new Uri[0]);
        String[] captionsArray = chilledcaptions.toArray(new String[0]);

        int oldSize = adapter.captions.length;
        adapter.captions = captionsArray;
        adapter.imageuris = images;
        adapter.occupied = occupiedarray;

        int newItemCount = captionsArray.length - oldSize;
        if (newItemCount > 0) {
            adapter.notifyItemRangeInserted(oldSize, newItemCount);
        }

        adapter.setListener((position, s) -> {
            if (listenerhome != null) {
                listenerhome.itemclicked(position, s);
            }
        }, (position, s) -> {
            if (listenerhome2 != null) {
                listenerhome2.itemdeleted(position, s);
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
        if (pd != null) {
            pd.dismiss();
        }
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
        if(dd!=null)
        {
            if(v1!=null)
            {
                dd.removeEventListener(v1);
            }
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();

      if(dd!=null)
      {
          if(v1!=null)
          {
              dd.addValueEventListener(v1);
          }
      }
    }
    @Override
   public  void onPause() {
        super.onPause();

        if(dd!=null)
        {
            if(v1!=null)
            {
                dd.removeEventListener(v1);
            }
        }
    }

}
/*package com.example.bookandpostroom;

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
    private List<Boolean> occupied=new ArrayList<>();
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
        occupied.clear();
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
        adapter.occupied = new Boolean[0];
        adapter.notifyDataSetChanged();

        // Load fresh data
        loadMore();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragmentforhome, container, false);

 rv=view.findViewById(R.id.rv);
 pb=view.findViewById(R.id.pb);
adapter=new AdapterForOwnerpart1(new String[0],new Uri[0],new Boolean[0]);
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
        }
        else {
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



                    Boolean occup;
                    if(snapshot.child("grabbedby").exists())
                    {
                        occup=true;
                    }
                    else
                    {
                        occup=false;
                    }

                    occupied.add(occup);
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
    Boolean[] occupiedarray=occupied.toArray(new Boolean[0]);

    images = chilledimages.toArray(new Uri[0]);
    adapter.captions = captions;
    adapter.imageuris=images;

    adapter.occupied=occupiedarray;

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
*/