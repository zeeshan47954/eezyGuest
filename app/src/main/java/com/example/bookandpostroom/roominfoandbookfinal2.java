package com.example.bookandpostroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class roominfoandbookfinal2 extends AppCompatActivity {
    public static final String message="hello";
    String s;

    private String facilities;
    private String latitude;
    private String longitude;
    private String roomtype;
    private String roomlocation;
    private String rules;
    private TextView tv1;
    private  TextView tv2;
    private String name1;
    private String id1;
    Long reviewcount;
    TextView roomName, roomLocation, roomType, roomoccupants, roomcapacity, price,roomdistrict;
    private TextView tv3;
    private String capacity;
    TabLayout dotsIndicator;
    ViewPager2 imageSlider;
    TextView remainingtextview;
    private boolean isNavigatingWithinApp = false;
    private boolean alreadyincremented=false;
    private boolean alreadydecremented=false;
    private long max;
    private List<Uri> images=new ArrayList<>();
    private Uri [] imagesfinal;
    String districtstring;
    private FirebaseAuth firebaseAuth;
long k;
long l;
    private Dialog progressDialog;
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        barsUtil.initialize(new SystemBarsUtil.OnInsetsAvailableListener() {
            @Override
            public void onInsetsAvailable() {
                int statusBarHeight = barsUtil.getStatusBarHeight();
                int navBarHeight = barsUtil.getActualNavigationBarHeight();

                // Find your CoordinatorLayout
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
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_roominfoandbookfinal2);
        applySystemBarPadding();
        s=getIntent().getStringExtra(message);
        String name=s.split("idstarts")[0];
        String id=s.split("idstarts")[1].split("idends")[0];
        String number=s.split("room")[1];
        name1=name;
        id1=id;
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        fragmentforreview fragment = new fragmentforreview();

// Create a Bundle and add your parameter
        Bundle bundle = new Bundle();
        bundle.putString("key_param", s);

// Set the arguments before adding the fragment
        fragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        String tenantname=account.getDisplayName();
        String tenantid=account.getUid();

        DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(s).exists())
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(roominfoandbookfinal2.this);

// Inflate custom layout
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.custom_dialog, null);
                    builder.setView(dialogView);

// Create dialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();

// Bind views
                    Button backButton = dialogView.findViewById(R.id.backButton);

// Back button action
                    backButton.setOnClickListener(v -> {
                       finish();
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tv2=findViewById(R.id.textView2);
        tv3=findViewById(R.id.textView3);
        progressDialog = new Dialog(this);
        progressDialog.setCancelable(false); // Disable dismissal on touch outside
remainingtextview=findViewById(R.id.capacityremaining);
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

        DatabaseReference gh= FirebaseDatabase.getInstance().getReference("google")
                .child(name).child(id).child("owner").child("rooms").child("roomId"+":"+s);
        gh.keepSynced(true);
        gh.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                if (!snapshot.child("reviews").exists()) {

                    TextView tv1 = findViewById(R.id.noreview);
                    FrameLayout f1 = findViewById(R.id.fragment_container);
                    tv1.setVisibility(View.VISIBLE);
                    f1.setVisibility(View.GONE);
                }
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
                        Toast.makeText(roominfoandbookfinal2.this, "data doesn't exist", Toast.LENGTH_SHORT).show();
                 finish();
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        StorageReference dkk= FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id).child("images"+number);
        dkk.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (listResult.getItems().isEmpty()) {
                    Toast.makeText(roominfoandbookfinal2.this, "No images found", Toast.LENGTH_SHORT).show();
                    Toast.makeText(roominfoandbookfinal2.this, "No images found", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(roominfoandbookfinal2.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
            }
        });




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


            if (!isFinishing() && !isDestroyed()) {
                progressDialog.dismiss();
            }

            // Adjust tab layout margins
            for (int j = 0; j < dotsIndicator.getTabCount(); j++) {
                View tabView = ((ViewGroup) dotsIndicator.getChildAt(0)).getChildAt(j);
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) tabView.getLayoutParams();
                params.setMargins(8, 0, 8, 0);
                tabView.requestLayout();
            }

            dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbookfinal2.this, R.color.teal));
            dotsIndicator.setTabRippleColor(null);

            imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbookfinal2.this, R.color.teal));
                    for (int k = 0; k < dotsIndicator.getTabCount(); k++) {
                        TabLayout.Tab tab = dotsIndicator.getTabAt(k);
                        if (tab != null) {
                            tab.view.setBackgroundTintList(k == position
                                    ? ColorStateList.valueOf(ContextCompat.getColor(roominfoandbookfinal2.this, R.color.teal))
                                    : ColorStateList.valueOf(ContextCompat.getColor(roominfoandbookfinal2.this, R.color.white)));
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
   /* public void booknow(View view) {
        isNavigatingWithinApp = true;


        Intent i = new Intent(this, bookActivity.class);
        i.putExtra(bookActivity.message, s);
        startActivity(i);
    }
*/
   public void booknow(View view) {
       isNavigatingWithinApp = true;

       // Check notification permission for Android 13+
       if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
           String notifPermission = "android.permission.POST_NOTIFICATIONS";

           if (ContextCompat.checkSelfPermission(this, notifPermission)
                   != PackageManager.PERMISSION_GRANTED) {

               // Show rationale if needed
               if (ActivityCompat.shouldShowRequestPermissionRationale(this, notifPermission)) {
                   Toast.makeText(this, "Notification permission is required to receive booking updates.", Toast.LENGTH_LONG).show();
               }

               // Request permission
               ActivityCompat.requestPermissions(this, new String[]{notifPermission}, 101);
               return; // wait for permission result
           }
       }

       // Permission already granted or not needed → trigger Worker and start booking
       proceedToBooking();
   }

    // Extracted to avoid duplication

    private void proceedToBooking() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        // Create a custom TextView for teal-colored message
        TextView message = new TextView(this);
        message.setText("Once a request is sent successfully,No changes can be made to the request.If you still want to do so,delete the previous and send a new one. This is done to ensure fairness and reduce traffic.");
        message.setTextColor(Color.parseColor("#008080")); // Teal
        message.setTextSize(16);
        message.setPadding(50, 40, 50, 10);

        builder.setView(message);

        builder.setPositiveButton("OK, I understand", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Proceed to booking activity
                Intent i = new Intent(roominfoandbookfinal2.this, bookActivity.class);
                i.putExtra(bookActivity.message, s);
                startActivity(i);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Leave", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Just close dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        // Change button colors after showing dialog
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#008080")); // Teal
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED); // Red
    }

    // Permission result callback
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedToBooking();
            } else {
                // Check if user selected "Don't ask again"
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        "android.permission.POST_NOTIFICATIONS")) {
                    // User denied with "Don't ask again" - guide them to settings
                    new AlertDialog.Builder(this)
                            .setTitle("Permission Required")
                            .setMessage("Notification permission is required to receive booking updates. Please enable it in app settings.")
                            .setPositiveButton("Go to Settings", (dialog, which) -> {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    // User just denied - show explanation
                    Toast.makeText(this, "You won't receive booking notifications without this permission.", Toast.LENGTH_LONG).show();
                    finish();
                }
                // Don't call requestPermissions again!
            }
        }
    }



    public void showmap(View view) {
        isNavigatingWithinApp = true;

        Intent i = new Intent(this, activitymap.class);
        i.putExtra(activitymap.latitude, latitude);
        i.putExtra(activitymap.longitude, longitude);
        startActivity(i);
    }


    @Override
    protected void onPause() {
        super.onPause();


    }



    @Override
    protected void onResume() {
        super.onResume();



    }


}