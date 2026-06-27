package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

public class fulldetails extends AppCompatActivity {

    public static final String message = "hello";
    String s;
    private String district;
    private String facilities;
    private String latitude;
    private String longitude;
    private String roomtype;
    private ImageView profileImage;
    private TextView valueName;
    private TextView valueAddress;
    private TextView valuePhone;
    private String roomlocation;
    private TextView coordinates;
    private String rules;
    private TextView tv1;
    private TextView tv2;
    private String name1;
    private String id1;
    private TextView tv3;

    TabLayout dotsIndicator;
    ViewPager2 imageSlider;
    private boolean isNavigatingWithinApp = false;
    private boolean alreadyincremented = false;
    private boolean alreadydecremented = false;
    private long max;
    private List<Uri> images = new ArrayList<>();
    private Uri[] imagesfinal;
    TextView roomName, roomLocation, roomType, roomoccupants, roomcapacity, price, roomdistrict;
    String districtstring;
    private String capacity;
    long k;
    String gender;
    long l;
    private Dialog progressDialog;
Button leave;
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
    protected void onResume() {
        super.onResume();


        if(deleted!=null && ds!=null)
        {
            deleted.addValueEventListener(ds);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        if(deleted!=null && ds!=null)
        {
            deleted.removeEventListener(ds);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();


        if(deleted!=null && ds!=null)
        {
            deleted.removeEventListener(ds);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_roominfoandbookfinal44);
        applySystemBarPadding();
        s = getIntent().getStringExtra(message);
        String name = s.split("idstarts")[0];
        String id = s.split("idstarts")[1].split("idends")[0];
        String number = s.split("room")[1];
        name1 = name;
        id1 = id;
        leave=findViewById(R.id.leavebutton);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }



        tv2 = findViewById(R.id.textView2);
        tv3 = findViewById(R.id.textView3);
        profileImage = findViewById(R.id.profileImage);
        coordinates = findViewById(R.id.coordinates);
        valueName = findViewById(R.id.valueName);

        valueAddress = findViewById(R.id.valueAddress);

        valuePhone = findViewById(R.id.valuePhone);

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

        Toolbar tb = findViewById(R.id.tb);
        setSupportActionBar(tb);
        AppBarLayout appBarLayout = findViewById(R.id.abl);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.ct);
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
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();

        String tenantname=account.getDisplayName();
        String tenantid=account.getUid();

        DatabaseReference fh=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");
        fh.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
gender=snapshot.child("gender").getValue(String.class);

for(DataSnapshot ds:snapshot.child("transactionmadeno").getChildren())
{
    String ownerid=ds.child("info").getValue(String.class);
    if(ownerid.contains(id))
    {SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date previousDateTime = dateTimeFormat.parse(ds.child("transactionDate").getValue(String.class) + " " + ds.child("transactionTime").getValue(String.class));
            Date currentDateTime = new Date();
            long diffInDays = (currentDateTime.getTime() - previousDateTime.getTime()) / (1000 * 60 * 60 * 24);

            leave.setVisibility(diffInDays <= 2 ? View.GONE : View.VISIBLE);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

break;
    }




}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference ds = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                valueName.setText(snapshot.child("ownername").getValue(String.class));
                valueAddress.setText(snapshot.child("owneraddress").getValue(String.class));

                valuePhone.setText(snapshot.child("ownernumber").getValue(String.class));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        StorageReference dks = FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id).child("pfp");
        dks.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(fulldetails.this)
                        .load(uri)
                        .into(profileImage);
            }
        });
        DatabaseReference gh = FirebaseDatabase.getInstance().getReference("google")
                .child(name).child(id).child("owner").child("rooms").child("roomId" + ":" + s);

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

                latitude = snapshot.child("latitude").getValue(String.class);
                longitude = snapshot.child("longitude").getValue(String.class);
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
                coordinates.setText(latitude + "," + longitude);
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
                    Toast.makeText(fulldetails.this, "data not available", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(fulldetails.this, "No images found", Toast.LENGTH_SHORT).show();
                    Toast.makeText(fulldetails.this, "No images found", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(fulldetails.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void onAllUrisFetched() {
        imagesfinal = images.toArray(new Uri[0]);
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

            dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(fulldetails.this, R.color.teal));
            dotsIndicator.setTabRippleColor(null);

            imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(fulldetails.this, R.color.teal));
                    for (int k = 0; k < dotsIndicator.getTabCount(); k++) {
                        TabLayout.Tab tab = dotsIndicator.getTabAt(k);
                        if (tab != null) {
                            tab.view.setBackgroundTintList(k == position
                                    ? ColorStateList.valueOf(ContextCompat.getColor(fulldetails.this, R.color.teal))
                                    : ColorStateList.valueOf(ContextCompat.getColor(fulldetails.this, R.color.white)));
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
    String notificationtenant;
    String notificationowner;
    boolean alreadyleftbytenant;
    Long noofpeople;
    Dialog d;
    Dialog progressDialog2;
    private FirebaseAuth firebaseAuth;
    // TENANT SIDE - Leave room with owner priority check
    public void leaveroom(View view) {
        String ownername = name1;
        String ownerid = id1;
        String owner = s;
        String roomId = "roomId:" + s;

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();

        String tenantname = account.getDisplayName();
        String tenantid = account.getUid();
        String tenant = tenantname + "idstarts" + tenantid + "idends";

        String ownerQueueId = ownername + "+" + ownerid;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST timezone
        String currentTime = sdf.format(new Date());
        String tenantQueueId = tenantname + "+" + tenantid+"timestamp"+currentTime;


        d = new Dialog(this);
        View view1 = LayoutInflater.from(this).inflate(R.layout.dialogforleaveconfirmation, null, false);
        Button cancel = view1.findViewById(R.id.cancelButton);
        Button ok = view1.findViewById(R.id.okButton);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();

                progressDialog2 = new Dialog(fulldetails.this);
                progressDialog2.setCancelable(false);
                View view2 = LayoutInflater.from(fulldetails.this).inflate(R.layout.progress_dialog_layout, null);
                progressDialog2.setContentView(view2);
                progressDialog2.show();

                DatabaseReference queueRef = FirebaseDatabase.getInstance()
                        .getReference("google")
                        .child(ownername)
                        .child(ownerid)
                        .child("owner")
                        .child("rooms")
                        .child(roomId)
                        .child("priorityqueue");

                // STEP 1: Try to acquire lock by adding tenant to queue
                queueRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        String existingQueue = currentData.getValue(String.class);

                        // CRITICAL CHECK: If owner is in queue, ABORT immediately
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
                        if (!committed || error != null) {
                            // Owner is in queue OR transaction failed
                            progressDialog2.dismiss();
                            Toast.makeText(fulldetails.this,
                                    "Cannot leave room right now. Owner may be processing it. Try again later.",
                                    Toast.LENGTH_LONG).show();
                          Intent i=new Intent(fulldetails.this,studentinfoandrooms.class);
                          startActivity(i);
                          finish();
                            return;
                        }

                        // STEP 2: Successfully added tenant to queue
                        // Now do a SECOND check to ensure owner didn't jump in
                        queueRef.get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String currentQueue = task.getResult().getValue(String.class);

                                // Double-check: if owner appeared in queue, ABORT
                                if (currentQueue != null && currentQueue.contains(ownerQueueId)) {
                                    removeFromQueue(queueRef, tenantQueueId);
                                    progressDialog2.dismiss();
                                    Toast.makeText(fulldetails.this,
                                            "Owner is processing this room. Cannot leave right now.",
                                            Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // STEP 3: Safe to proceed - owner is NOT in queue
                                proceedWithLeaveRoom(queueRef, tenantQueueId, tenantname,
                                        tenantid, tenant, s, ownername, ownerid);
                            } else {
                                // Error reading queue, abort safely
                                removeFromQueue(queueRef, tenantQueueId);
                                progressDialog2.dismiss();
                                Toast.makeText(fulldetails.this,
                                        "Error occurred. Please try again.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.setContentView(view1);
        d.show();
    }
    ValueEventListener ds;
    DatabaseReference deleted;
    // Proceed with leave room request
    private void proceedWithLeaveRoom(DatabaseReference queueRef, String tenantQueueId,
                                      String tenantname, String tenantid, String tenant,
                                      String owner, String ownername, String ownerid) {

        deleted=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student").child("roomleft");
       ds=new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
if(snapshot.exists() && snapshot.getValue(String.class).equals("yes"))
{
    if(progressDialog2.isShowing())
    {
        progressDialog2.dismiss();
        showReviewDialog(owner, tenantname);
    }

}
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       };

       deleted.addValueEventListener(ds);
        DatabaseReference leaveRef = FirebaseDatabase.getInstance()
                .getReference("deletebookingafterbookingbytenant");

        leaveRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long count = currentData.getChildrenCount();
                if (count == null) count = 0L;
                count++;

                Map<String, Object> data = new HashMap<>();
                data.put("tenantname", tenantname);
                data.put("tenantid", tenantid);
                data.put("tenant", tenant);
                data.put("owner", owner);
                data.put("ownername", ownername);
                data.put("ownerid", ownerid);
                data.put("gender", gender);

                currentData.child("request" +tenantid+count).setValue(data);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                // Remove tenant from queue
                removeFromQueue(queueRef, tenantQueueId);



                if (committed && error == null) {
                    Toast.makeText(fulldetails.this,
                            "Leave request uploaded successfully",
                            Toast.LENGTH_SHORT).show();

                    // Show review dialog

                } else {
                    Toast.makeText(fulldetails.this,
                            "Error submitting leave request",
                            Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(fulldetails.this, studentinfoandrooms.class);
                    startActivity(i);
                }
            }
        });
    }

    // Show review dialog after leaving room
    private void showReviewDialog(String owner, String tenantname) {
        Dialog reviewDialog = new Dialog(fulldetails.this);

        View view = LayoutInflater.from(fulldetails.this).inflate(R.layout.dialogforreview, null, false);
        Button yesButton = view.findViewById(R.id.okButton);
        Button noButton = view.findViewById(R.id.cancelButton);

        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
                Intent i = new Intent(fulldetails.this, reviewActivity2.class);
                i.putExtra(reviewActivity2.message, owner);
                i.putExtra(reviewActivity2.realname, tenantname);
                startActivity(i);
            }
        });

        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
                Intent i = new Intent(fulldetails.this, studentinfoandrooms.class);
                startActivity(i);
            }
        });

        reviewDialog.setContentView(view);
        reviewDialog.setCancelable(false);
        reviewDialog.show();
    }

    // Helper method to remove entry from queue
    private void removeFromQueue(DatabaseReference queueRef, String queueId) {
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
                // Cleanup complete
            }
        });
    }
//    public void leaveroom(View view) {
//
//        String ownername=name1;
//        String ownerid=id1;
//        String owner=s;
//        String roomId="roomId:"+s;
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser account = firebaseAuth.getCurrentUser();
//
//        String tenantname=account.getDisplayName();
//        String tenantid=account.getUid();
//        String tenant=tenantname+"idstarts"+tenantid+"idends";
//
//
//
//         d = new Dialog(this);
//        View view1 = LayoutInflater.from(this).inflate(R.layout.dialogforleaveconfirmation, null, false);
//        Button cancel = view1.findViewById(R.id.cancelButton);
//        Button ok = view1.findViewById(R.id.okButton);
//        ok.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//
//                progressDialog2 = new Dialog(fulldetails.this);
//                progressDialog2.setCancelable(false); // Disable dismissal on touch outside
//
//                // Inflate the custom layout
//                View view2 = LayoutInflater.from(fulldetails.this).inflate(R.layout.progress_dialog_layout, null);
//
//                // Set the custom view to the dialog
//                progressDialog2.setContentView(view2);
//
//                // Show the dialog
//                progressDialog2.show();
//                DatabaseReference ds=FirebaseDatabase.getInstance().getReference("deletebookingafterbookingbytenant");
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
//                        data.put("tenant",tenant);
//                        data.put("owner",s);
//                        data.put("ownername",ownername);
//                        data.put("ownerid",ownerid);
//                        data.put("gender",gender);
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
//                        Toast.makeText(fulldetails.this, "leave  request uploaded successfully", Toast.LENGTH_SHORT).show();
//                        Dialog dd=new Dialog(fulldetails.this);
//                        View view=LayoutInflater.from(fulldetails.this).inflate(R.layout.dialogforreview,null,false);
//                        Button ss=view.findViewById(R.id.okButton);
//                        Button hj=view.findViewById(R.id.cancelButton);
//                        progressDialog2.dismiss();
//                        dd.setContentView(view);
//                        dd.show();
//                        ss.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent i=new Intent(fulldetails.this,reviewActivity2.class);
//                                i.putExtra(reviewActivity2.message,s);
//                                i.putExtra(reviewActivity2.realname,tenantname);
//                                startActivity(i);
//                            }
//                        });
//
//                        hj.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                Intent i=new Intent(fulldetails.this,studentinfoandrooms.class);
//                                startActivity(i);
//                            }
//                        });
//
//                    }
//                });
//
//
//            }
//        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                d.dismiss();
//            }
//        });
//        d.setContentView(view1);
//        d.show();
//
//
//
//
//    }

/*    d.dismiss();
              DatabaseReference dk = FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");

                dk.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        notificationtenant = currentData.child("fcmtoken").getValue(String.class);
                        if (notificationtenant == null) return Transaction.success(currentData);

                        noofpeople = currentData.child(owner).child("no of people equal to").getValue(Long.class);
                        if (noofpeople == null) return Transaction.success(currentData);
                        currentData.child(owner).setValue(null);
                        List<String> s = new ArrayList<>();
                        for (MutableData ds : currentData.child("requestsenttotheownernumbers").getChildren()) {
                            String key = ds.getKey();
                            if (key == null) return Transaction.success(currentData);
                            String value = ds.getValue(String.class);
                            if (value == null) return Transaction.success(currentData);
                            if (value != null && value.contains(owner)) {
                                s.add(key);


                            }
                        }
                        if (s.isEmpty()) {
                            alreadyleftbytenant = true;
                            return Transaction.abort();

                        } else if (!s.isEmpty()) {


                            currentData.child("requestsenttotheownernumbers").child(s.get(0)).setValue(null);
                        }

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        if (!committed) {
                            if (alreadyleftbytenant) {
                                Toast.makeText(fulldetails.this, "already dealted by user", Toast.LENGTH_SHORT).show();
                                //notification("tenant has already left");
                            }

                        }
                        if (committed) {
                            DatabaseReference dd = FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner");
                            dd.runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                    notificationowner = currentData.child("fcmtoken").getValue(String.class);
                                    if (notificationowner == null) return Transaction.success(currentData);
Log.d("notificationowner",notificationowner);
                                    List<String> ss = new ArrayList<>();

                                    long peoplepresent = currentData.child("rooms").child("roomId:" + owner).child("roomcapacityreached").getValue(Long.class);

                                    peoplepresent = peoplepresent - noofpeople;
                                    Log.d("peoplepresent",String.valueOf(peoplepresent));
                                    currentData.child("rooms").child("roomId:" + owner).child("roomcapacityreached").setValue(peoplepresent);

                                    Log.d("here","here");
                                    for (MutableData dd : currentData.child("actualrequestsrecieved").getChildren()) {

                                        String key = dd.getKey();
                                        if (key == null) return Transaction.success(currentData);
                                        if (key != null && key.contains(tenantname+"idstartsfortenant"+tenantid+"idendsfortenantforroomwithsid"+owner)) {
                                            ss.add(key);
                                            Log.d("key",key);
                                        }
                                    }
                                    if (ss.isEmpty()) {
                                        alreadyleftbytenant = true;
                                        return Transaction.abort();

                                    } else if (!ss.isEmpty()) {


                                        currentData.child("actualrequestsrecieved").child(ss.get(0)).setValue(null);
                                    }
                                    List<String> sss = new ArrayList<>();
                                    for (MutableData aj : currentData.child("rooms").child("roomId:" + owner).child("grabbedby").getChildren()) {
                                        String key2 = aj.getKey();
                                        if (key2 == null) return Transaction.success(currentData);
                                        String value2 = aj.getValue(String.class);
                                        if (value2 == null) return Transaction.success(currentData);
                                        Log.d("key2",key2);
                                        Log.d("value2",value2);

                                        if (value2 != null && value2.equals(tenant)) {
                                            sss.add(key2);


                                        }


                                    }
                                    if (sss.isEmpty()) {
                                        alreadyleftbytenant = true;
                                        return Transaction.abort();

                                    } else if (!sss.isEmpty()) {
                                        currentData.child("rooms").child("roomId:" + owner).child("grabbedby").child(sss.get(0)).setValue(null);
                                    }

                                    return Transaction.success(currentData);
                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                                    if (committed) {

                                        //   notification("tenant successfully removed");
                                    } else if (!committed) {
                                        if (alreadyleftbytenant) {
                                            Toast.makeText(fulldetails.this, "already dealted by user", Toast.LENGTH_SHORT).show();
                                            //     notification("tenant has already left");

                                        }

                                    }
                                }
                            });


                        }
                    }
                });*/


    public void showmap(View view) {

        if( !latitude.isEmpty() && !longitude.isEmpty())
        { Intent i = new Intent(this, activitymap2.class);
            i.putExtra(activitymap2.latitude, latitude);
            i.putExtra(activitymap2.longitude,longitude);
            startActivity(i);}
        else{

            Toast.makeText(this, "please enter the coordinates", Toast.LENGTH_SHORT).show();
        }
    }

public void copy(View view)
{

    ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);

    // Create a ClipData with the text to copy
    ClipData clip = ClipData.newPlainText("adhaarno", latitude+","+longitude);

    // Set the ClipData to the Clipboard
    clipboard.setPrimaryClip(clip);
    ImageButton ib=findViewById(R.id.copy);
    ib.setImageResource(R.drawable.check);
    ib.setBackground(null);

    // Optional: Show a message to indicate text is copied
    Toast.makeText(view.getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

}


public void review(View view)
{
    Intent i=new Intent(this,ReviewActivity.class);
    startActivity(i);
}



}