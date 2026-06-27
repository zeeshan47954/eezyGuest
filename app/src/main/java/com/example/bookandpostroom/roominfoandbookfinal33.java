package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class roominfoandbookfinal33 extends AppCompatActivity implements fragmentfortenantlist.RequestListener, fragmentfortenantlist.RequestListener2 {

    public static final String message = "hello";
    public static final String tenantinformation = "jack";
    String s;
    private String district;
    private String facilities;
    private String latitude;
    private String longitude;
    private String roomtype;
    private String roomlocation;
    private String rules;
    private TextView tv1;
    private TextView tv2;
    String roomidd;
    private String name1;
    private String id1;
    private TextView tv3;
    private fragmentfortenantlist tenantFragment;
    private long capacity;
    TabLayout dotsIndicator;
    ViewPager2 imageSlider;
    private boolean isNavigatingWithinApp = false;
    private boolean alreadyincremented = false;
    private boolean alreadydecremented = false;
    private long max;
    private List<Uri> images = new ArrayList<>();
    private Uri[] imagesfinal;
    DatabaseReference gk;
    String notificationowner1;
    long k;
    long l;
    ValueEventListener es;
    private Dialog progressDialog;
    @Override
    protected void onPause() {
        super.onPause();
        if(gk!=null && es!=null)
        {
            gk.removeEventListener(es);
        }
        if(deleted!=null && ds!=null)
        {
            deleted.removeEventListener(ds);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        if(gk!=null && es!=null)
        {
            gk.addValueEventListener(es);
        }
        if(deleted!=null && ds!=null)
        {
            deleted.addValueEventListener(ds);
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(gk!=null && es!=null)
        {
            gk.removeEventListener(es);
        }
        if(deleted!=null && ds!=null)
        {
            deleted.removeEventListener(ds);
        }
    }
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        CoordinatorLayout scrollView = findViewById(R.id.main);

        // Apply padding to avoid system bars
        scrollView.setPadding(
                0,
                0,  // Top padding for status bar
                0,
                navBarHeight      // Bottom padding for navigation bar
        );

        // Optional: Log the values for debugging

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_roominfoandbookfinal33);
        applySystemBarPadding();
        s = getIntent().getStringExtra(message);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        roomidd = s;
        String name = s.split("idstarts")[0];
        String id = s.split("idstarts")[1].split("idends")[0];
        String number = s.split("room")[1];
        name1 = name;
        id1 = id;
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        String info = getIntent().getStringExtra(tenantinformation);

        // Create fragment only once
        tenantFragment = fragmentfortenantlist.newInstance(info);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, tenantFragment)
                .commit();

        tv1 = findViewById(R.id.textView1);
        tv2 = findViewById(R.id.textView2);
        tv3 = findViewById(R.id.textView3);
        progressDialog = new Dialog(this);
        progressDialog.setCancelable(false);

        View view = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null);
        progressDialog.setContentView(view);
        progressDialog.show();

        imageSlider = findViewById(R.id.imageSlider);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        AppBarLayout appBarLayout = findViewById(R.id.abl);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.ct);
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.teal,getTheme()));
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    collapsingToolbar.setTitle("Details");
                } else if (verticalOffset == 0) {
                    collapsingToolbar.setTitle("");
                } else {
                    collapsingToolbar.setTitle("");
                }
            }
        });

        DatabaseReference gg = FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner");
        gg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationowner1 = snapshot.child("fcmtoken").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
         gk = FirebaseDatabase.getInstance().getReference("google")
                .child(name).child(id).child("owner").child("rooms")
                .child("roomId" + ":" + s).child("grabbedby");

// Track initial state and tenant count
        final long[] previousTenantCount = {-1}; // -1 means not initialized
es=new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

            // Count current tenants
            long currentTenantCount = 0;
            for (DataSnapshot child : snapshot.getChildren()) {
                if (child.getKey().contains("tenant")) {
                    currentTenantCount++;
                }
            }

            // If this is the first load, just store the count
            if (previousTenantCount[0] == -1) {
                previousTenantCount[0] = currentTenantCount;
                return;
            }

            // Check if there was an addition or deletion
            if (currentTenantCount != previousTenantCount[0]) {
                boolean isAddition = currentTenantCount > previousTenantCount[0];

                // Start Intent on addition or deletion
                Intent intent = new Intent(roominfoandbookfinal33.this, Activity5.class);


                startActivity(intent);
                finish();
                // Update the previous count
                previousTenantCount[0] = currentTenantCount;
            }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(roominfoandbookfinal33.this,
                "Error: " + error.getMessage(),
                Toast.LENGTH_SHORT).show();
    }
};
        gk.addValueEventListener(es);
        DatabaseReference gh = FirebaseDatabase.getInstance().getReference("google")
                .child(name).child(id).child("owner").child("rooms").child("roomId" + ":" + s);
        gh.keepSynced(true);
        gh.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("district"))
                        district = snapshot.child("district").getValue(String.class);
                    if (snapshot.hasChild("facilities")) {
                        facilities = snapshot.child("facilities").getValue(String.class);
                        List<String> rulesList = Arrays.asList(facilities.replace("[", "").replace("]", "").split(", "));
                        StringBuilder starList = new StringBuilder();
                        for (String rule : rulesList) {
                            starList.append("★ ").append(rule).append("\n");
                        }
                        tv2.setText(starList.toString());
                    }
                    if (snapshot.hasChild("latitude"))
                        latitude = snapshot.child("latitude").getValue(String.class);
                    if (snapshot.hasChild("longitude"))
                        longitude = snapshot.child("longitude").getValue(String.class);
                    if (snapshot.hasChild("roomType"))
                        roomtype = snapshot.child("roomType").getValue(String.class);
                    if (snapshot.hasChild("roomlocation"))
                        roomlocation = snapshot.child("roomlocation").getValue(String.class);
                    if (snapshot.hasChild("roomcapacity"))
                        capacity = snapshot.child("roomcapacity").getValue(long.class);
                    if (snapshot.hasChild("rules")) {
                        String rulesStr = snapshot.child("rules").getValue(String.class);
                        List<String> rulesList = Arrays.asList(rulesStr.replace("[", "").replace("]", "").split(", "));
                        StringBuilder starList = new StringBuilder();
                        for (String rule : rulesList) {
                            starList.append("★ ").append(rule).append("\n");
                        }
                        tv3.setText(starList.toString());
                    }
                    tv1.setText("room type: " + roomtype + "\n" + "\n" + "room location: " + roomlocation + "\n" + "district: " + district + "\n" + "room capacity: " + capacity);
                } else {
                    Toast.makeText(roominfoandbookfinal33.this, "data doesnt exist", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        StorageReference dkk = FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id).child("images" + number);
        dkk.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (listResult.getItems().isEmpty()) {
                    Toast.makeText(roominfoandbookfinal33.this, "No images found", Toast.LENGTH_SHORT).show();
                    return;
                }

                AtomicInteger counter = new AtomicInteger(0);
                int totalItems = listResult.getItems().size();

                for (StorageReference fileRef : listResult.getItems()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            images.add(uri);
                            if (counter.incrementAndGet() == totalItems) {
                                onAllUrisFetched();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("FirebaseStorage", "Failed to get URL", e);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(roominfoandbookfinal33.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onAllUrisFetched() {
        imagesfinal = images.toArray(new Uri[0]);
        runOnUiThread(() -> {
            ImageAdapter adapter = new ImageAdapter(this, imagesfinal);
            imageSlider.setAdapter(adapter);
            new TabLayoutMediator(dotsIndicator, imageSlider, (tab, position) -> {
            }).attach();

            progressDialog.dismiss();

            for (int j = 0; j < dotsIndicator.getTabCount(); j++) {
                View tabView = ((ViewGroup) dotsIndicator.getChildAt(0)).getChildAt(j);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                params.setMargins(8, 0, 8, 0);
                tabView.requestLayout();
            }

            dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbookfinal33.this, R.color.teal));
            dotsIndicator.setTabRippleColor(null);

            imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbookfinal33.this, R.color.teal));
                    for (int k = 0; k < dotsIndicator.getTabCount(); k++) {
                        TabLayout.Tab tab = dotsIndicator.getTabAt(k);
                        if (tab != null) {
                            tab.view.setBackgroundTintList(k == position
                                    ? ColorStateList.valueOf(ContextCompat.getColor(roominfoandbookfinal33.this, R.color.teal))
                                    : ColorStateList.valueOf(ContextCompat.getColor(roominfoandbookfinal33.this, R.color.white)));
                        }
                    }
                }
            });
        });
        Log.d("FirebaseStorage", "All images fetched: " + images.size());
    }

    private static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        private final Context context;
        private final Uri[] images2;

        public ImageAdapter(Context context, Uri[] images1) {
            this.context = context;
            this.images2 = images1;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (images2[position] != null) {
                Glide.with(context)
                        .load(images2[position])
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.error_placeholder);
            }

            holder.imageView.setOnClickListener(v -> {
                showImageDialog(images2[position]);
            });
        }

        @Override
        public int getItemCount() {
            return images2.length;
        }

        private void showImageDialog(Uri imageResId) {
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialog_image);
            ImageView enlargedImageView = dialog.findViewById(R.id.enlargedImageView);
            enlargedImageView.setImageURI(imageResId);
            enlargedImageView.setOnClickListener(v -> dialog.dismiss());
            dialog.show();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }

    public void functionforrequest(int id, String s) {
        String name = s.split("idstarts")[0];
        String idd = s.split("idstarts")[1].split("idends")[0];
        String ss = name + "gapp" + idd;
        Intent i = new Intent(roominfoandbookfinal33.this, tenantinfo2.class);
        i.putExtra(tenantinfo.message, ss);
        i.putExtra(tenantinfo.message2, roomidd);
        startActivity(i);
    }

    public void showmap(View view) {
        isNavigatingWithinApp = true;
        Intent i = new Intent(this, activitymap.class);
        i.putExtra(activitymap.latitude, latitude);
        i.putExtra(activitymap.longitude, longitude);
        startActivity(i);
    }

    Long noofpeople;
    boolean alreadyleftbytenant = false;
    String notificationtenant;
    String notificationowner;

    // OWNER SIDE - Delete tenant after booking with absolute priority
    public void functiondeletetenantafterbooking(int id, String s) {

       Dialog dd=new Dialog(this);
       dd.setContentView(R.layout.progress_dialog_layout);
       dd.setCancelable(false);
       dd.show();
        DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1)
                .child("owner").child("transactionsrecievedno");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

               String time="";
               String date="";
                for(DataSnapshot dk:snapshot.getChildren())
                {

                  if(dk.child("idss").getValue(String.class).equals(s) && dk.child("tenant").getValue(String.class).equals("yes"))
                  { date = dk.child("transactionDate").getValue(String.class);
                       time = dk.child("transactionTime").getValue(String.class);
break;
                  }


                }
                String dateTimeString = date + " " + time;

                try {
                    // Parse the transaction datetime
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST timezone

                    Date transactionDateTime = sdf.parse(dateTimeString);

                    // Get current datetime in IST
                    Date currentDateTime = new Date();

                    // Calculate the difference in milliseconds
                    long diffInMillis = currentDateTime.getTime() - transactionDateTime.getTime();

                    // Convert to hours
                    long diffInHours = TimeUnit.MILLISECONDS.toHours(diffInMillis);

                    // Check if 48 hours have passed
                    if (diffInHours >= 48) {
                        dd.dismiss();
                        // More than or equal to 48 hours have passed
                        Dialog warning = new Dialog(roominfoandbookfinal33.this);
                        View view = LayoutInflater.from(roominfoandbookfinal33.this).inflate(R.layout.dialogfordeleteafterbookingconfirmation, null);

                        Button cancel = view.findViewById(R.id.cancelButton);
                        Button ok = view.findViewById(R.id.okButton);

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warning.dismiss();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                warning.dismiss();

                                Dialog progressDialog = new Dialog(roominfoandbookfinal33.this);
                                progressDialog.setContentView(R.layout.progress_dialog_layout);
                                progressDialog.setCancelable(false);
                                progressDialog.show();

                                String tenant = s;
                                String tenantname = s.split("idstarts")[0];
                                String tenantid = s.split("idstarts")[1].split("idends")[0];
                                String ownername = name1;
                                String ownerid = id1;
                                String roomid = "roomId:" + roomidd;
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST timezone
                                String currentTime = sdf.format(new Date());
                                String ownerQueueId = ownername + "+" + ownerid+"timestamp"+currentTime;
                                String tenantqueueid=tenantname+ "+" +tenantid;

                                DatabaseReference queueRef = FirebaseDatabase.getInstance()
                                        .getReference("google")
                                        .child(ownername)
                                        .child(ownerid)
                                        .child("owner")
                                        .child("rooms")
                                        .child(roomid)
                                        .child("priorityqueue");

                                // STEP 1: Owner ALWAYS adds to queue (has absolute priority)
                                queueRef.runTransaction(new Transaction.Handler() {
                                    @NonNull
                                    @Override
                                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                        String existingQueue = currentData.getValue(String.class);

                                        if (existingQueue == null || existingQueue.isEmpty()) {
                                            currentData.setValue(ownerQueueId);
                                        } else {
                                            if (existingQueue.contains(tenantqueueid)) {
                                                return Transaction.abort();
                                            }
                                        }

                                        return Transaction.success(currentData);
                                    }

                                    @Override
                                    public void onComplete(@Nullable DatabaseError error, boolean committed,
                                                           @Nullable DataSnapshot currentData) {
                                        if (error != null || !committed) {
                                            progressDialog.dismiss();
                                            Toast.makeText(roominfoandbookfinal33.this,
                                                    "Error acquiring lock: " + (error != null ? error.getMessage() : "Unknown error"),
                                                    Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(roominfoandbookfinal33.this, Activity5.class);
                                            startActivity(i);
                                            finish();
                                            return;
                                        } else if (committed)
                                            // STEP 2: Owner is now in queue, proceed after delay
                                        {    new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    proceedWithTenantDeletion(progressDialog, queueRef, ownerQueueId,
                                                            id, s, tenantname, tenantid, ownername, ownerid);
                                                }
                                            }, 100);
                                    }
                                    }
                                });
                            }
                        });

                        warning.setContentView(view);
                        warning.setCancelable(false);
                        warning.show();
                        // Your logic here
                    } else {
                        dd.dismiss();
                        // Less than 48 hours
                        Toast.makeText(roominfoandbookfinal33.this, "Tenant can only be removed 48 hours after the payment has been made", Toast.LENGTH_SHORT).show();
                        // Your logic here
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    ValueEventListener ds;
    DatabaseReference deleted;
    // Proceed with deleting tenant after booking
    private void proceedWithTenantDeletion(Dialog progressDialog, DatabaseReference queueRef,
                                           String ownerQueueId, int id, String s,
                                           String tenantname, String tenantid,
                                           String ownername, String ownerid) {
        // Get tenant gender first
        DatabaseReference genderRef = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(tenantname)
                .child(tenantid)
                .child("student")
                .child("gender");

        deleted=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner").child("tenantremoved");
        ds=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getValue(String.class).equals("yes"))
                {if(progressDialog.isShowing())
                { progressDialog.dismiss();}
snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        genderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String gender = snapshot.getValue(String.class);

                // Create delete booking request
                DatabaseReference deleteBookingRef = FirebaseDatabase.getInstance()
                        .getReference("deletebookingafterbooking");

                deleteBookingRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Long count = currentData.getChildrenCount();
                        if (count == null) count = 0L;
                        count++;

                        Map<String, Object> data = new HashMap<>();
                        data.put("tenantname", tenantname);
                        data.put("tenantid", tenantid);
                        data.put("tenant", tenantname + "idstarts" + tenantid + "idends");
                        data.put("owner", roomidd);
                        data.put("ownername", name1);
                        data.put("ownerid", id1);
                        data.put("gender", gender);
                        data.put("alreadynotification", notificationowner1);

                        currentData.child("request"+ownerid + count).setValue(data);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed,
                                           @Nullable DataSnapshot currentData) {
                        // Remove owner from queue
                        removeFromQueue(queueRef, ownerQueueId, new Runnable() {
                            @Override
                            public void run() {

                                if (committed && error == null) {
                                    Toast.makeText(roominfoandbookfinal33.this,
                                            "Remove request uploaded successfully",
                                            Toast.LENGTH_SHORT).show();

                                    // Remove item from adapter in real-time
                                    if (tenantFragment != null) {
                                        tenantFragment.removeTenant(id, s);

                                        // Check if this was the last item
                                        if (tenantFragment.wasLastItem()) {
                                            new Handler().postDelayed(() -> {
                                                Intent i = new Intent(roominfoandbookfinal33.this, Activity5.class);
                                                startActivity(i);
                                                finish();
                                            }, 500);
                                        }
                                    }
                                } else {
                                    Toast.makeText(roominfoandbookfinal33.this,
                                            "Error removing tenant: " +
                                                    (error != null ? error.getMessage() : "Unknown"),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                removeFromQueue(queueRef, ownerQueueId, new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(roominfoandbookfinal33.this,
                                "Error fetching tenant data",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Helper method to remove entry from queue with callback
    private void removeFromQueue(DatabaseReference queueRef, String queueId, Runnable onComplete) {
        queueRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String queue = currentData.getValue(String.class);
                if (queue != null && !queue.isEmpty()) {
                    String[] entries = queue.split(",");
                    StringBuilder newQueue = new StringBuilder();

                    for (String entry : entries) {
                        String trimmedEntry = entry.trim();
                        if (!trimmedEntry.equals(queueId)) {
                            if (newQueue.length() > 0) {
                                newQueue.append(",");
                            }
                            newQueue.append(trimmedEntry);
                        }
                    }

                    String finalQueue = newQueue.toString();
                    currentData.setValue(finalQueue.isEmpty() ? "" : finalQueue);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }
}
//    public void functiondeletetenantafterbooking(int id, String s) {
//        Dialog warning = new Dialog(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.dialogfordeleteafterbookingconfirmation, null);
//
//        Button cancel = view.findViewById(R.id.cancelButton);
//        Button ok = view.findViewById(R.id.okButton);
//
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                warning.dismiss();
//            }
//        });
//
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                warning.dismiss();
//                Dialog dd = new Dialog(roominfoandbookfinal33.this);
//                dd.setContentView(R.layout.progress_dialog_layout);
//                dd.setCancelable(false);
//                dd.show();
//
//                String tenant = s;
//                String tenantname = s.split("idstarts")[0];
//                String tenantid = s.split("idstarts")[1].split("idends")[0];
//                String ownername=name1;
//                String ownerid=id1;
//                String roomid="roomId:"+roomidd;
//                DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student").child("gender");
//                dj.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        DatabaseReference ds = FirebaseDatabase.getInstance().getReference("deletebookingafterbooking");
//                        String gender=snapshot.getValue(String.class);
//                        ds.runTransaction(new Transaction.Handler() {
//                            @NonNull
//                            @Override
//                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//                                Long count = currentData.getChildrenCount();
//                                if (count == null) count = 0L;
//                                count++;
//                                Map<String, Object> data = new HashMap<>();
//                                data.put("tenantname", tenantname);
//                                data.put("tenantid", tenantid);
//                                data.put("tenant", tenantname + "idstarts" + tenantid + "idends");
//                                data.put("owner", roomidd);
//                                data.put("ownername", name1);
//                                data.put("ownerid", id1);
//                                data.put("gender",gender);
//                                data.put("alreadynotification", notificationowner1);
//
//                                currentData.child("request" + count).setValue(data);
//                                return Transaction.success(currentData);
//                            }
//
//                            @Override
//                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//                                dd.dismiss();
//                                Toast.makeText(roominfoandbookfinal33.this, "Remove request uploaded successfully", Toast.LENGTH_SHORT).show();
//
//                                // Remove item from adapter in real-time
//                                if (tenantFragment != null) {
//                                    tenantFragment.removeTenant(id, s);
//
//                                    // Check if this was the last item
//                                    if (tenantFragment.wasLastItem()) {
//                                        // If it was the last item, navigate to Activity5
//                                        new Handler().postDelayed(() -> {
//                                            Intent i = new Intent(roominfoandbookfinal33.this, Activity5.class);
//                                            startActivity(i);
//                                            finish();
//                                        }, 500);
//                                    }
//                                }
//                            }
//                        });
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//
//            }
//        });
//
//        warning.setContentView(view);
//        warning.setCancelable(false);
//        warning.show();
//    }
//
//
//}

//package com.example.bookandpostroom;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.content.res.ColorStateList;
//import android.content.res.Configuration;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.viewpager2.widget.ViewPager2;
//
//import com.bumptech.glide.Glide;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.appbar.AppBarLayout;
//import com.google.android.material.appbar.CollapsingToolbarLayout;
//import com.google.android.material.tabs.TabLayout;
//import com.google.android.material.tabs.TabLayoutMediator;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.MutableData;
//import com.google.firebase.database.Transaction;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.ListResult;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
//public class roominfoandbookfinal33 extends AppCompatActivity implements fragmentfortenantlist.RequestListener,fragmentfortenantlist.RequestListener2{
//
//    public static final String message="hello";
//    public static final String tenantinformation="jack";
//    String s;
//    private String district;
//    private String facilities;
//    private String latitude;
//    private String longitude;
//    private String roomtype;
//    private String roomlocation;
//    private String rules;
//    private TextView tv1;
//    private  TextView tv2;
//    String roomidd;
//    private String name1;
//    private String id1;
//    private TextView tv3;
//    private long capacity;
//    TabLayout dotsIndicator;
//    ViewPager2 imageSlider;
//    private boolean isNavigatingWithinApp = false;
//    private boolean alreadyincremented=false;
//    private boolean alreadydecremented=false;
//    private long max;
//    private List<Uri> images=new ArrayList<>();
//    private Uri [] imagesfinal;
//
//String notificationowner1;
//    long k;
//    long l;
//    private Dialog progressDialog;
//    @Override
//    protected void onResume() {
//        super.onResume();
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);
//    }
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_roominfoandbookfinal33);
//        s=getIntent().getStringExtra(message);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
//                WindowManager.LayoutParams.FLAG_SECURE);
//        roomidd=s;
//        String name=s.split("idstarts")[0];
//        String id=s.split("idstarts")[1].split("idends")[0];
//        String number=s.split("room")[1];
//        name1=name;
//        id1=id;
//        int currentOrientation = getResources().getConfiguration().orientation;
//        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
//        } else {
//            // Optional: if user opens the app already in landscape,
//            // you can still force portrait if you want:
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
//
//        String info=getIntent().getStringExtra(tenantinformation);
//        fragmentfortenantlist fragment = fragmentfortenantlist.newInstance(info);
//
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .commit();
//tv1=findViewById(R.id.textView1);
//        tv2=findViewById(R.id.textView2);
//        tv3=findViewById(R.id.textView3);
//        progressDialog = new Dialog(this);
//        progressDialog.setCancelable(false); // Disable dismissal on touch outside
//
//        // Inflate the custom layout
//        View view = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null);
//
//        // Set the custom view to the dialog
//        progressDialog.setContentView(view);
//
//        // Show the dialog
//        progressDialog.show();
//        imageSlider = findViewById(R.id.imageSlider);
//        dotsIndicator = findViewById(R.id.dotsIndicator);
//
//        // Set up the TabLayout with ViewPager2
//
//        Toolbar tb=findViewById(R.id.tb);
//        setSupportActionBar(tb);
//        AppBarLayout appBarLayout = findViewById(R.id.abl);
//        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.ct);
//        // Style the TabLayout indicators
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
//                    // Fully collapsed
//                    collapsingToolbar.setTitle("Details");
//                } else if (verticalOffset == 0) {
//                    // Fully expanded
//                    collapsingToolbar.setTitle("");
//                } else {
//                    // In between
//                    collapsingToolbar.setTitle(""); // Optional: Hide text while scrolling
//                }
//            }
//        });
//
//        DatabaseReference gg=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner");
//        gg.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                notificationowner1=snapshot.child("fcmtoken").getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        DatabaseReference gh= FirebaseDatabase.getInstance().getReference("google")
//                .child(name).child(id).child("owner").child("rooms").child("roomId"+":"+s);
//        gh.keepSynced(true);
//        gh.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.exists()){
//                if (snapshot.hasChild("district"))
//                    district = snapshot.child("district").getValue(String.class);
//                if (snapshot.hasChild("facilities")) {
//                    facilities = snapshot.child("facilities").getValue(String.class);
//
//                    List<String> rulesList = Arrays.asList(facilities.replace("[", "").replace("]", "").split(", "));
//                    StringBuilder starList = new StringBuilder();
//                    for (String rule : rulesList) {
//                        starList.append("★ ").append(rule).append("\n");  // Unicode star
//                    }
//                    tv2.setText(starList.toString());
//
//                }
//                if (snapshot.hasChild("latitude"))
//                    latitude = snapshot.child("latitude").getValue(String.class);
//                if (snapshot.hasChild("longitude"))
//                    longitude = snapshot.child("longitude").getValue(String.class);
//                if (snapshot.hasChild("roomType"))
//                    roomtype = snapshot.child("roomType").getValue(String.class);
//                if (snapshot.hasChild("roomlocation"))
//                    roomlocation = snapshot.child("roomlocation").getValue(String.class);
//                if (snapshot.hasChild("roomcapacity"))
//                    capacity = snapshot.child("roomcapacity").getValue(long.class);
//                if (snapshot.hasChild("rules")) {
//                    String rulesStr = snapshot.child("rules").getValue(String.class);
//                    List<String> rulesList = Arrays.asList(rulesStr.replace("[", "").replace("]", "").split(", "));
//                    StringBuilder starList = new StringBuilder();
//                    for (String rule : rulesList) {
//                        starList.append("★ ").append(rule).append("\n");  // Unicode star
//                    }
//                    tv3.setText(starList.toString());
//
//
//                }
//
//
//                tv1.setText("room type: " + roomtype + "\n" + "\n" + "room location: " + roomlocation + "\n" + "district: " + district + "\n" + "room capacity: " + capacity);
//
//
//            }
//else{
//                    Toast.makeText(roominfoandbookfinal33.this, "data doesnt exist", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        StorageReference dkk= FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id).child("images"+number);
//        dkk.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
//            @Override
//            public void onSuccess(ListResult listResult) {
//                if (listResult.getItems().isEmpty()) {
//                    Toast.makeText(roominfoandbookfinal33.this, "No images found", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(roominfoandbookfinal33.this, "No images found", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                AtomicInteger counter = new AtomicInteger(0); // Track progress
//                int totalItems = listResult.getItems().size();
//
//                for (StorageReference fileRef : listResult.getItems()) {
//                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            images.add(uri); // Add URI to the list
//
//                            // Check if all URIs are fetched
//                            if (counter.incrementAndGet() == totalItems) {
//                                onAllUrisFetched(); // Call method after fetching all URLs
//                            }
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.e("FirebaseStorage", "Failed to get URL", e);
//                        }
//                    });
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(roominfoandbookfinal33.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//
//
//    }
//
//    private void onAllUrisFetched(){
//        imagesfinal=images.toArray(new Uri[0]);
//        // Use the images list (e.g., update RecyclerView)
//        runOnUiThread(() -> {
//        ImageAdapter adapter = new ImageAdapter(this, imagesfinal);
//            imageSlider.setAdapter(adapter);
//            new TabLayoutMediator(dotsIndicator, imageSlider, (tab, position) -> {
//                // Optional: Add tab labels
//            }).attach();
//
//            progressDialog.dismiss();
//
//            // Adjust tab layout margins
//            for (int j = 0; j < dotsIndicator.getTabCount(); j++) {
//                View tabView = ((ViewGroup) dotsIndicator.getChildAt(0)).getChildAt(j);
//                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
//                params.setMargins(8, 0, 8, 0);
//                tabView.requestLayout();
//            }
//
//            dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbookfinal33.this, R.color.teal));
//            dotsIndicator.setTabRippleColor(null);
//
//            imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//                @Override
//                public void onPageSelected(int position) {
//                    dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbookfinal33.this, R.color.teal));
//                    for (int k = 0; k < dotsIndicator.getTabCount(); k++) {
//                        TabLayout.Tab tab = dotsIndicator.getTabAt(k);
//                        if (tab != null) {
//                            tab.view.setBackgroundTintList(k == position
//                                    ? ColorStateList.valueOf(ContextCompat.getColor(roominfoandbookfinal33.this, R.color.teal))
//                                    : ColorStateList.valueOf(ContextCompat.getColor(roominfoandbookfinal33.this, R.color.white)));
//                        }
//                    }
//                }
//            });
//        });
//
//        Log.d("FirebaseStorage", "All images fetched: " + images.size());
//    }
//    private static class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
//        private final Context context;
//        private final Uri[] images2;
//
//        public ImageAdapter(Context context, Uri[] images1) {
//            this.context = context;
//            this.images2 = images1;
//        }
//
//        @NonNull
//        @Override
//        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
//            return new ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//            if (images2[position] != null) {
//                Glide.with(context)
//                        .load(images2[position])
//
//                        .into(holder.imageView);
//            } else {
//                holder.imageView.setImageResource(R.drawable.error_placeholder);
//            }
//
//
//            holder.imageView.setOnClickListener(v -> {
//                // Open enlarged image in dialog
//                showImageDialog(images2[position]);
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return images2.length;
//        }
//
//        private void showImageDialog(Uri imageResId) {
//            // Create a dialog to display the enlarged image
//            final Dialog dialog = new Dialog(context);
//
//            dialog.setContentView(R.layout.dialog_image);
//
//            ImageView enlargedImageView = dialog.findViewById(R.id.enlargedImageView);
//            enlargedImageView.setImageURI(imageResId);
//
//            // Dismiss dialog when image is clicked
//            enlargedImageView.setOnClickListener(v -> dialog.dismiss());
//
//            dialog.show();
//        }
//
//        public static class ViewHolder extends RecyclerView.ViewHolder {
//            ImageView imageView;
//
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                imageView = itemView.findViewById(R.id.imageView);
//            }
//        }
//    }
//
//public void functionforrequest(int id,String s)
//{
//
//    String name=s.split("idstarts")[0];
//    String idd=s.split("idstarts")[1].split("idends")[0];
//    String ss=name+"gapp"+idd;
//    Intent i=new Intent(roominfoandbookfinal33.this, tenantinfo.class);
//    i.putExtra(tenantinfo.message,ss);
//    i.putExtra(tenantinfo.message2,roomidd);
//    startActivity(i);
//
//
//}
//    public void showmap(View view) {
//        isNavigatingWithinApp = true;
//
//        Intent i = new Intent(this, activitymap.class);
//        i.putExtra(activitymap.latitude, latitude);
//        i.putExtra(activitymap.longitude, longitude);
//        startActivity(i);
//    }
//    Long noofpeople;
//    boolean alreadyleftbytenant=false;
//    String notificationtenant;
//    String notificationowner;
//    public void functiondeletetenantafterbooking(int id,String s)
//    {
//        Dialog warning = new Dialog(this);
//        View view = LayoutInflater.from(this).inflate(R.layout.dialogfordeleteafterbookingconfirmation, null);
//
//        Button cancel=view.findViewById(R.id.cancelButton);
//        Button ok=view.findViewById(R.id.okButton);
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                warning.dismiss();
//            }
//        });
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                warning.dismiss();
//                Dialog dd=new Dialog(roominfoandbookfinal33.this);
//                dd.setContentView(R.layout.progress_dialog_layout);
//                dd.setCancelable(false);
//                dd.show();
//String tenant=s;
//String owner=roomidd;
//          String tenantname=s.split("idstarts")[0];
//          String tenantid=s.split("idstarts")[1].split("idends")[0];
//String ownername=name1;
//String ownerid=id1;
//
//
//                DatabaseReference ds=FirebaseDatabase.getInstance().getReference("deletebookingafterbooking");
//                ds.runTransaction(new Transaction.Handler() {
//                    @NonNull
//                    @Override
//                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//                        Long count=currentData.getChildrenCount();
//                        if(count==null)count=0L;
//                        count++;
//                        Map<String,Object> data=new HashMap<>();
//                        data.put("tenantname",tenantname);
//                        data.put("tenantid",tenantid);
//                        data.put("tenant",tenantname+"idstarts"+tenantid+"idends");
//                        data.put("owner",roomidd);
//                        data.put("ownername",name1);
//                        data.put("ownerid",id1);
//                        data.put("alreadynotification",notificationowner1);
//
//
//
//                        currentData.child("request"+count).setValue(data);
//
//                        return Transaction.success(currentData);
//                    }
//
//                    @Override
//                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//                        Toast.makeText(roominfoandbookfinal33.this, "remove  request uploaded successfully", Toast.LENGTH_SHORT).show();
//                        Intent i=new Intent(roominfoandbookfinal33.this,Activity5.class);
//                        startActivity(i);
//                    }
//                });
//
//
//
//         /*  DatabaseReference dk = FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");
//
//                dk.runTransaction(new Transaction.Handler() {
//                    @NonNull
//                    @Override
//                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//                        notificationtenant = currentData.child("fcmtoken").getValue(String.class);
//                        if (notificationtenant == null) return Transaction.success(currentData);
//
//                        noofpeople = currentData.child(owner).child("no of people equal to").getValue(Long.class);
//                        if (noofpeople == null) return Transaction.success(currentData);
//                        currentData.child(owner).setValue(null);
//                        List<String> s = new ArrayList<>();
//                        for (MutableData ds : currentData.child("requestsenttotheownernumbers").getChildren()) {
//                            String key = ds.getKey();
//                            if (key == null) return Transaction.success(currentData);
//                            String value = ds.getValue(String.class);
//                            if (value == null) return Transaction.success(currentData);
//                            if (value != null && value.contains(owner)) {
//                                s.add(key);
//
//
//                            }
//                        }
//                        if (s.isEmpty()) {
//                            alreadyleftbytenant = true;
//                            return Transaction.abort();
//
//                        } else if (!s.isEmpty()) {
//
//
//                            currentData.child("requestsenttotheownernumbers").child(s.get(0)).setValue(null);
//                        }
//
//                        return Transaction.success(currentData);
//                    }
//
//                    @Override
//                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//                        if (!committed) {
//                            if (alreadyleftbytenant) {
//                                Toast.makeText(roominfoandbookfinal33.this, "already dealted by user", Toast.LENGTH_SHORT).show();
//                                //notification("tenant has already left");
//                            }
//
//                        }
//                        if (committed) {
//                            DatabaseReference dd = FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner");
//                            dd.runTransaction(new Transaction.Handler() {
//                                @NonNull
//                                @Override
//                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//                                    notificationowner = currentData.child("fcmtoken").getValue(String.class);
//                                    if (notificationowner == null) return Transaction.success(currentData);
//Log.d("notificationowner",notificationowner);
//                                    List<String> ss = new ArrayList<>();
//
//                                    long peoplepresent = currentData.child("rooms").child("roomId:" + owner).child("roomcapacityreached").getValue(Long.class);
//
//                                    peoplepresent = peoplepresent - noofpeople;
//                                    Log.d("peoplepresent",String.valueOf(peoplepresent));
//                                    currentData.child("rooms").child("roomId:" + owner).child("roomcapacityreached").setValue(peoplepresent);
//
//                                    Log.d("here","here");
//                                    for (MutableData dd : currentData.child("actualrequestsrecieved").getChildren()) {
//
//                                        String key = dd.getKey();
//                                        if (key == null) return Transaction.success(currentData);
//                                        if (key != null && key.contains(tenantname+"idstartsfortenant"+tenantid+"idendsfortenantforroomwithsid"+owner)) {
//                                            ss.add(key);
//                                            Log.d("key",key);
//                                        }
//                                    }
//                                    if (ss.isEmpty()) {
//                                        alreadyleftbytenant = true;
//                                        return Transaction.abort();
//
//                                    } else if (!ss.isEmpty()) {
//
//
//                                        currentData.child("actualrequestsrecieved").child(ss.get(0)).setValue(null);
//                                    }
//                                    List<String> sss = new ArrayList<>();
//                                    for (MutableData aj : currentData.child("rooms").child("roomId:" + owner).child("grabbedby").getChildren()) {
//                                        String key2 = aj.getKey();
//                                        if (key2 == null) return Transaction.success(currentData);
//                                        String value2 = aj.getValue(String.class);
//                                        if (value2 == null) return Transaction.success(currentData);
//                                        Log.d("key2",key2);
//                                        Log.d("value2",value2);
//
//                                        if (value2 != null && value2.equals(tenant)) {
//                                            sss.add(key2);
//
//
//                                        }
//
//
//                                    }
//                                    if (sss.isEmpty()) {
//                                        alreadyleftbytenant = true;
//                                        return Transaction.abort();
//
//                                    } else if (!sss.isEmpty()) {
//                                        currentData.child("rooms").child("roomId:" + owner).child("grabbedby").child(sss.get(0)).setValue(null);
//                                    }
//
//                                    return Transaction.success(currentData);
//                                }
//
//                                @Override
//                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//
//                                    if (committed) {
//
//                                        //   notification("tenant successfully removed");
//                                    } else if (!committed) {
//                                        if (alreadyleftbytenant) {
//                                            Toast.makeText(roominfoandbookfinal33.this, "already dealted by user", Toast.LENGTH_SHORT).show();
//                                            //     notification("tenant has already left");
//
//                                        }
//
//                                    }
//                                }
//                            });
//
//
//                        }
//                    }
//                });
//*/
//
//
//            }
//        });
//
//        warning.setContentView(view);
//        warning.setCancelable(false);
//        warning.show();
//
//
//
//    }
//
//
//
//}