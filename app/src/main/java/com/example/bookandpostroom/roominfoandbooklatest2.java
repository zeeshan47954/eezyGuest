package com.example.bookandpostroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.mapbox.maps.MapView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class roominfoandbooklatest2 extends AppCompatActivity {
    public static final String message = "hello";
    private String s;
    private long l;
    private long k;
    private String roomid;
    private MapView mapView;
    private String district;
    private String facilities;
    private String latitude;
    private String longitude;
    private String roomtype;
    private String roomlocation;
    private String rules;
    private TextView tv1;
    private  TextView tv2;
    private TextView tv3;

    TabLayout dotsIndicator;
    ViewPager2 imageSlider;
    String name1;
    String id1;
    String districtstring;
    private String capacity;

    TextView roomName, roomLocation, roomType, roomoccupants, roomcapacity, price,roomdistrict;




    TextView remainingtextview;
    private boolean isNavigatingWithinApp = false;

    private List<Uri> images=new ArrayList<>();
    private Uri [] imagesfinal;
    private FirebaseAuth firebaseAuth;


    private Dialog progressDialog;
    private MenuItem requestItem;
String ownername;
String ownerid;
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
        Toast.makeText(this, "Top: " + statusBarHeight + "px, Bottom: " + navBarHeight + "px",
                Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_roominfoandbooklatest2);
        applySystemBarPadding();
        s=getIntent().getStringExtra(message);
roomid=getIntent().getStringExtra("roomid");
        tv2=findViewById(R.id.textView2);
        tv3=findViewById(R.id.textView3);
        // Disable dismissal on touch outside
        remainingtextview=findViewById(R.id.capacityremaining);
        progressDialog = new Dialog(this);
        progressDialog.setCancelable(false); // Disable dismissal on touch outside

        // Inflate the custom layout
        View view = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressDialog.setContentView(view);

        // Show the dialog
        progressDialog.show();
        imageSlider = findViewById(R.id.imageSlider);
        dotsIndicator = findViewById(R.id.dotsIndicator);

        // Set up the TabLayout with ViewPager2

        Toolbar tb=findViewById(R.id.tb);
        setSupportActionBar(tb);
        AppBarLayout appBarLayout = findViewById(R.id.abl);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.ct);
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.teal,getTheme()));
        // Style the TabLayout indicators
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    // Fully collapsed
                    collapsingToolbar.setTitle("Details");
                } else if (verticalOffset == 0) {
                    // Fully expanded
                    collapsingToolbar.setTitle("");
                } else {
                    // In between
                    collapsingToolbar.setTitle(""); // Optional: Hide text while scrolling
                }
            }
        });

        String name = s.split("idstarts")[0];
        String id = s.split("idstarts")[1].split("idends")[0];
        String number=s.split("room")[1];
        ownername=name;
        ownerid=id;
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        name1=account.getDisplayName();
        id1=account.getUid();



        DatabaseReference gh= FirebaseDatabase.getInstance().getReference("google")
                .child(name).child(id).child("owner").child("rooms").child("roomId"+":"+s);

        gh.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                if (snapshot.hasChild("district"))
                    districtstring = snapshot.child("district").getValue(String.class);
                if (snapshot.hasChild("facilities")) {
                    facilities = snapshot.child("facilities").getValue(String.class);

                    List<String> rulesList = Arrays.asList(facilities.replace("[", "").replace("]", "").split(", "));
                    StringBuilder starList = new StringBuilder();
                    for (String rule : rulesList) {
                        starList.append("★ ").append(rule).append("\n");  // Unicode star
                    }
                    tv2.setText(starList.toString());

                }
                String roomname;
                String roomprice;
                String occupants;
                long remaining;
                roomName = findViewById(R.id.roomname);
                roomLocation = findViewById(R.id.roomlocation);
                roomType = findViewById(R.id.roomtype);
                roomdistrict = findViewById(R.id.roomdistrict);
                roomoccupants = findViewById(R.id.occupants);
                roomcapacity = findViewById(R.id.capacity);
                price = findViewById(R.id.price);
                if (snapshot.hasChild("latitude"))
                    latitude = snapshot.child("latitude").getValue(String.class);
                if (snapshot.hasChild("longitude"))
                    longitude = snapshot.child("longitude").getValue(String.class);
                roomname = snapshot.child("roomname").getValue(String.class);
                roomprice = String.valueOf(snapshot.child("roomprice").getValue(long.class));
                occupants = snapshot.child("bookedbygender").getValue(String.class);
                if (occupants.isEmpty()) {
                    occupants = "no current occupants";
                }
                if (snapshot.hasChild("roomType"))
                    roomtype = snapshot.child("roomType").getValue(String.class);
                if (snapshot.hasChild("roomlocation"))
                    roomlocation = snapshot.child("roomlocation").getValue(String.class);
                if (snapshot.hasChild("roomcapacity"))
                    capacity = String.valueOf(snapshot.child("roomcapacity").getValue(long.class));

                remaining = snapshot.child("roomcapacity").getValue(long.class) - snapshot.child("roomcapacityreached").getValue(long.class);
                remainingtextview.setText(String.valueOf(remaining));
                roomName = findViewById(R.id.roomname);
                roomLocation = findViewById(R.id.roomlocation);
                roomType = findViewById(R.id.roomtype);
                roomdistrict = findViewById(R.id.roomdistrict);
                roomoccupants = findViewById(R.id.occupants);
                roomcapacity = findViewById(R.id.capacity);
                price = findViewById(R.id.price);
                roomName.setText(roomname);
                roomLocation.setText(roomlocation);
                roomType.setText(roomtype);
                roomdistrict.setText(districtstring);
                roomoccupants.setText(occupants);
                roomcapacity.setText(capacity);
                price.setText("₹" + roomprice);

                if (snapshot.hasChild("rules")) {
                    String rulesStr = snapshot.child("rules").getValue(String.class);
                    List<String> rulesList = Arrays.asList(rulesStr.replace("[", "").replace("]", "").split(", "));
                    StringBuilder starList = new StringBuilder();
                    for (String rule : rulesList) {
                        starList.append("★ ").append(rule).append("\n");  // Unicode star
                    }
                    tv3.setText(starList.toString());


                }


            }

else{
                    Toast.makeText(roominfoandbooklatest2.this, "data doesnt exist", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        StorageReference dkk= FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id).child("images"+number);
        // Example image resources
        dkk.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (listResult.getItems().isEmpty()) {
                    Toast.makeText(roominfoandbooklatest2.this, "No images found", Toast.LENGTH_SHORT).show();
                    Toast.makeText(roominfoandbooklatest2.this, "No images found", Toast.LENGTH_SHORT).show();
                    return;
                }

                AtomicInteger counter = new AtomicInteger(0); // Track progress
                int totalItems = listResult.getItems().size();

                for (StorageReference fileRef : listResult.getItems()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            images.add(uri); // Add URI to the list

                            // Check if all URIs are fetched
                            if (counter.incrementAndGet() == totalItems) {
                                onAllUrisFetched(); // Call method after fetching all URLs
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
                Toast.makeText(roominfoandbooklatest2.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
            }
        });

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
        public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
            return new ImageAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
            if (images2[position] != null) {
                Glide.with(context)
                        .load(images2[position])

                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(R.drawable.error_placeholder);
            }


            holder.imageView.setOnClickListener(v -> {
                // Open enlarged image in dialog
                showImageDialog(images2[position]);
            });
        }

        @Override
        public int getItemCount() {
            return images2.length;
        }

        private void showImageDialog(Uri imageResId) {
            // Create a dialog to display the enlarged image
            final Dialog dialog = new Dialog(context);

            dialog.setContentView(R.layout.dialog_image);

            ImageView enlargedImageView = dialog.findViewById(R.id.enlargedImageView);
            enlargedImageView.setImageURI(imageResId);

            // Dismiss dialog when image is clicked
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

    private void onAllUrisFetched(){
        imagesfinal=images.toArray(new Uri[0]);
        // Use the images list (e.g., update RecyclerView)
        runOnUiThread(() -> {
            ImageAdapter adapter = new ImageAdapter(this, imagesfinal);
            imageSlider.setAdapter(adapter);
            new TabLayoutMediator(dotsIndicator, imageSlider, (tab, position) -> {
                // Optional: Add tab labels
            }).attach();

            progressDialog.dismiss();

            // Adjust tab layout margins
            for (int j = 0; j < dotsIndicator.getTabCount(); j++) {
                View tabView = ((ViewGroup) dotsIndicator.getChildAt(0)).getChildAt(j);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                params.setMargins(8, 0, 8, 0);
                tabView.requestLayout();
            }

            dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbooklatest2.this, R.color.teal));
            dotsIndicator.setTabRippleColor(null);

            imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbooklatest2.this, R.color.teal));
                    for (int k = 0; k < dotsIndicator.getTabCount(); k++) {
                        TabLayout.Tab tab = dotsIndicator.getTabAt(k);
                        if (tab != null) {
                            tab.view.setBackgroundTintList(k == position
                                    ? ColorStateList.valueOf(ContextCompat.getColor(roominfoandbooklatest2.this, R.color.teal))
                                    : ColorStateList.valueOf(ContextCompat.getColor(roominfoandbooklatest2.this, R.color.white)));
                        }
                    }
                }
            });
        });

        Log.d("FirebaseStorage", "All images fetched: " + images.size());
    }

    // ImageAdapter nested class for managing images in ViewPager2

String notificationvaluetenant;
String notificationvalueowner;

Boolean approved=false;

Boolean alreadydeletedbyowner=false;
Long no_of_people;
String c;
List<String> Queue=new ArrayList<>();

    // TENANT SIDE - Only proceeds if owner is NOT in queue
    public void deleterequest(View view) {
        Dialog dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.dialogfordeleteconfirmationrequest);
        dialog2.setCancelable(false);

        Button cancel = dialog2.findViewById(R.id.cancelButton);
        Button ok = dialog2.findViewById(R.id.okButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();

                Dialog progressDialog = new Dialog(roominfoandbooklatest2.this);
                progressDialog.setCancelable(false);
                View view2 = LayoutInflater.from(roominfoandbooklatest2.this)
                        .inflate(R.layout.progress_dialog_layout, null);
                progressDialog.setContentView(view2);
                progressDialog.show();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST timezone
                String currentTime = sdf.format(new Date());
                String tenantQueueId = name1 + "+" + id1+"timestamp"+currentTime;
                String ownerQueueId = ownername + "+" + ownerid;

                DatabaseReference queueRef = FirebaseDatabase.getInstance()
                        .getReference("google")
                        .child(ownername)
                        .child(ownerid)
                        .child("owner")
                        .child("rooms")
                        .child("roomId:" + s)
                        .child("priorityqueue");

                // STEP 1: Try to acquire lock by adding tenant to queue
                queueRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        String existingQueue = currentData.getValue(String.class);

                        // CRITICAL CHECK: If owner is in queue at ANY point, ABORT immediately
                        if (existingQueue != null && existingQueue.contains(ownerQueueId)) {
                            return Transaction.abort();
                        }

                        // Owner not in queue, add tenant
                        if (existingQueue == null || existingQueue.isEmpty()) {
                            currentData.setValue(tenantQueueId);
                        } else {
                            currentData.setValue(existingQueue + "," + tenantQueueId);
                        }

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed,
                                           @Nullable DataSnapshot currentData) {
                        progressDialog.dismiss();

                        if (!committed || error != null) {
                            // Owner is in queue OR transaction failed
                            Toast.makeText(roominfoandbooklatest2.this,
                                    "Cannot delete request right now. Owner may be processing it. Try again later.",
                                    Toast.LENGTH_LONG).show();
                            Intent i = new Intent(roominfoandbooklatest2.this, studentinfoandrooms.class);
                            startActivity(i);
                            return;
                        }

                        // STEP 2: Successfully added tenant to queue
                        // Now do a SECOND check to ensure owner didn't jump in
                        queueRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String currentQueue = task.getResult().getValue(String.class);

                                // Double-check: if owner appeared in queue, ABORT
                                if (currentQueue != null && currentQueue.contains(ownerQueueId)) {
                                    // Owner jumped in! Remove tenant and abort
                                    removeFromQueueAndAbort(queueRef, tenantQueueId);
                                    Toast.makeText(roominfoandbooklatest2.this,
                                            "Owner is processing this request. Cannot delete.",
                                            Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(roominfoandbooklatest2.this,
                                            studentinfoandrooms.class);
                                    startActivity(i);
                                    return;
                                }

                                // STEP 3: Safe to proceed - owner is NOT in queue
                                proceedWithDeleteRequest(queueRef, tenantQueueId);
                            } else {
                                // Error reading queue, abort safely
                                removeFromQueueAndAbort(queueRef, tenantQueueId);
                                Toast.makeText(roominfoandbooklatest2.this,
                                        "Error occurred. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(roominfoandbooklatest2.this,
                                        studentinfoandrooms.class);
                                startActivity(i);
                            }
                        });
                    }
                });
            }
        });

        dialog2.show();
    }

    // Helper method to proceed with delete request
    private void proceedWithDeleteRequest(DatabaseReference queueRef, String tenantQueueId) {
        DatabaseReference deleterequests = FirebaseDatabase.getInstance()
                .getReference("deleterequest");

        deleterequests.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long count = currentData.getChildrenCount();
                if (count == null) count = 0L;
                count++;

                Map<String, Object> mp = new HashMap<>();
                mp.put("name1", name1);
                mp.put("id1", id1);
                mp.put("roomid", s);
                mp.put("status", "pending");

                currentData.child("request" +id1+count).setValue(mp);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                // Remove tenant from queue after request is submitted
                removeFromQueue(queueRef, tenantQueueId);

                if (committed) {
                    Toast.makeText(roominfoandbooklatest2.this,
                            "Delete request submitted successfully",
                            Toast.LENGTH_SHORT).show();
                }

                Intent i = new Intent(roominfoandbooklatest2.this, studentinfoandrooms.class);
                startActivity(i);
            }
        });
    }

    // Helper method to remove from queue and abort
    private void removeFromQueueAndAbort(DatabaseReference queueRef, String queueId) {
        removeFromQueue(queueRef, queueId);
    }

    // Helper method to remove entry from queue
    private void removeFromQueue(DatabaseReference queueRef, String queueId) {
        queueRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String queue = currentData.getValue(String.class);
                if (queue != null && !queue.isEmpty()) {
                    // Remove the specific entry
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
                // Cleanup complete
            }
        });
    }
/* public void deleterequest(View view) {


        Dialog dialog2=new Dialog(this);
        dialog2.setContentView(R.layout.dialogfordeleteconfirmationrequest);
        dialog2.setCancelable(false);

        Button cancel=dialog2.findViewById(R.id.cancelButton);
        Button ok=dialog2.findViewById(R.id.okButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog2.dismiss();
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();

               Dialog progressDialog = new Dialog(roominfoandbooklatest2.this);
                progressDialog.setCancelable(false); // Disable dismissal on touch outside

                // Inflate the custom layout
                View view2 = LayoutInflater.from(roominfoandbooklatest2.this).inflate(R.layout.progress_dialog_layout, null);

                // Set the custom view to the dialog
                progressDialog.setContentView(view2);

                // Show the dialog
                progressDialog.show();
String timeoftheday=name1+id1;
                DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner").child("rooms").child("roomId:"+s).child("priorityqueue");
                dj.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                        c = currentData.getValue(String.class);
                        if (c == null || c.isEmpty()) {
                            c = timeoftheday;
                            currentData.setValue(c);
                            Queue.add(c);
                        } else {
                            String j = c + "," + timeoftheday;
                            currentData.setValue(j);
                            Queue.add(j);

                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        if(Queue.contains(ownername+"+"+ownerid))
                        {

                            Toast.makeText(roominfoandbooklatest2.this, "cannot make changes right now,try again later", Toast.LENGTH_SHORT).show();
                       Intent i=new Intent(roominfoandbooklatest2.this,studentinfoandrooms.class);
                       startActivity(i);
                        }
                     else{
                            DatabaseReference deleterequests = FirebaseDatabase.getInstance().getReference("deleterequest");

                            deleterequests.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                    Long count = currentData.getChildrenCount();
                                    if (count == null) count = 0L;
                                    count++;
                                    Map<String, Object> mp = new HashMap<>();
                                    mp.put("name1", name1);
                                    mp.put("id1", id1);
                                    mp.put("roomid", s);


                                    currentData.child("request" + count).setValue(mp);


                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                                    Intent i = new Intent(roominfoandbooklatest2.this, studentinfoandrooms.class);
                                    startActivity(i);
                                }
                            });

                        }
                    }

                    });


            }




    });
        dialog2.show();
    }*/
    public void showmap(View view) {
        isNavigatingWithinApp = true;
        Intent i = new Intent(this, activitymap.class);
        i.putExtra(activitymap.latitude, latitude);
        i.putExtra(activitymap.longitude, longitude);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        isNavigatingWithinApp = true;

        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (!isNavigatingWithinApp) {

        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (isNavigatingWithinApp) {
            isNavigatingWithinApp = false; // Reset the flag
            return;
        }

    }

 /*
 DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("student");

                dj.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        notificationvaluetenant=currentData.child("fcmtoken").getValue(String.class);
                        if(notificationvaluetenant==null)return Transaction.success(currentData);

                        if(currentData.hasChild(s)) {

                            Long a = currentData.child(s).child("requestapproved").getValue(Long.class);
                            if (a == null) return Transaction.success(currentData);
                            if (a == 1) {
                                approved = true;
                                return Transaction.abort();

                            } else {
                                no_of_people = currentData.child(s).child("no of people equal to").getValue(Long.class);
                                if (no_of_people == null)
                                    return Transaction.success(currentData);

                                currentData.child(s).setValue(null);
                                List<String> keysToDelete = new ArrayList<>();
                                for (MutableData ss : currentData.child("requestsenttotheownernumbers").getChildren()) {
                                    String key = ss.getKey();
                                    String value = ss.getValue(String.class);
                                    if (value == null) return Transaction.success(currentData);
                                    if (value != null && value.contains(s)) {
                                        keysToDelete.add(key);
                                    }

                                }
                                if (!keysToDelete.isEmpty()) {
                                    for (String key : keysToDelete) {
                                        currentData.child("requestsenttotheownernumbers").child(key).setValue(null);
                                    }
                                    Long d = currentData.child("no of requests sent").getValue(Long.class);
                                    if (d == null) return Transaction.success(currentData);
                                    d--;

                                    currentData.child("no of requests sent").setValue(d);
                                   return Transaction.success(currentData);

                                } else {
                                    alreadydeletedbyowner = true;
                                    return Transaction.abort();

                                }


                            }
                        }
                        else{

                            alreadydeletedbyowner = true;
                            return Transaction.abort();
                        }


                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        if(!committed) {
                            {
                                if (approved) {

                                    Sendnotification notificationsSender = new Sendnotification(
                                            notificationvaluetenant,
                                            "accepted",
                                            "the request was approved by the owner,however,you can choose not to pay",
                                            roominfoandbooklatest2.this
                                    );
                                    notificationsSender.SendNotifications();

                                }

                            }
                            if (alreadydeletedbyowner) {

                                Sendnotification notificationsSender = new Sendnotification(
                                        notificationvaluetenant,
                                        "Rejected",
                                        "the request was already rejected by owner",
                                        roominfoandbooklatest2.this
                                );
                                notificationsSender.SendNotifications();


                            }
                        }
                        if(committed)
                        {
                            Toast.makeText(roominfoandbooklatest2.this, "here in the comitted", Toast.LENGTH_SHORT).show();
                            DatabaseReference df=FirebaseDatabase.getInstance().getReference("google").child(s.split("idstarts")[0]).child(s.split("idstarts")[1].split("idends")[0]).child("owner") ;

                            df.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                    notificationvalueowner=currentData.child("fcmtoken").getValue(String.class);
                                    if(notificationvalueowner==null)return Transaction.success(currentData);
                                    List<String> keysToDelete = new ArrayList<>();
                                    for(MutableData ss:currentData.child("actualrequestsrecieved").getChildren())
                                    {

                                        String key = ss.getKey();
                                        if(key==null)return Transaction.success(currentData);
                                        if (key != null && key.contains(name1+"idstartsfortenant"+id1+"idendsfortenantforroomwithsid"+s)) {
                                            keysToDelete.add(key);
                                        }


                                    }
                                    if(!keysToDelete.isEmpty()) {
                                        for (String key : keysToDelete) {
                                            currentData.child("actualrequestsrecieved").child(key).setValue(null);
                                        }
                                        Long dd=currentData.child("rooms").child("roomId:"+s).child("roomcapacityreached").getValue(Long.class);
                                        if(dd==null) return Transaction.success(currentData);
                                        currentData.child("rooms").child("roomId:"+s).child("roomcapacityreached").setValue(dd-no_of_people);





                                    }
                                    else{
                                        alreadydeletedbyowner=true;
                                        return Transaction.abort();

                                    }




                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                                    if(!committed)
                                    {
                                        if(alreadydeletedbyowner)
                                        {


                                            Sendnotification notificationsSender = new Sendnotification(
                                                    notificationvaluetenant,
                                                    "Rejected",
                                                    "the request was already rejected by owner",
                                                    roominfoandbooklatest2.this
                                            );
                                            notificationsSender.SendNotifications();


                                        }


                                    }
                                    if(committed)
                                    {
                                        Sendnotification notificationsSender1 = new Sendnotification(
                                                notificationvaluetenant,
                                                "deleted",
                                                "request successfully deleted",
                                                roominfoandbooklatest2.this
                                        );
                                        notificationsSender1.SendNotifications();
                                        Sendnotification notificationsSender2 = new Sendnotification(
                                                notificationvalueowner,
                                                "deleted",
                                                "one of the room requests was deleted by the user",
                                                roominfoandbooklatest2.this
                                        );
                                        notificationsSender2.SendNotifications();



                                    }
                                }
                            });




                        }
                    }
                });
 */

}
