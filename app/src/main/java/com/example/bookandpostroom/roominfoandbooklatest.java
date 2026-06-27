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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Firebase;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class roominfoandbooklatest extends AppCompatActivity {
    public static final String message = "hello";
    private String s;
private long l;
private long k;
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
    private long capacity;
    TabLayout dotsIndicator;
    ViewPager2 imageSlider;
    private boolean isNavigatingWithinApp = false;

    private List<Uri> images=new ArrayList<>();
    private Uri [] imagesfinal;



    private Dialog progressDialog;
    private MenuItem requestItem;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_roominfoandbookfinal);
        applySystemBarPadding();
s=getIntent().getStringExtra(message);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        tv1=findViewById(R.id.textView1);
        tv2=findViewById(R.id.textView2);
        tv3=findViewById(R.id.textView3);
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

        DatabaseReference dss= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner")
                .child("roomId:"+s);
        dss.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {




                        if (snapshot.hasChild("district"))
                            district = snapshot.child("district").getValue(String.class);
                        if (snapshot.hasChild("facilities")) {
                            facilities = snapshot.child("facilities").getValue(String.class);

                            List<String> rulesList = Arrays.asList(facilities.replace("[", "").replace("]", "").split(", "));
                            StringBuilder starList = new StringBuilder();
                            for (String rule : rulesList) {
                                starList.append("★ ").append(rule).append("\n");  // Unicode star
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
                                starList.append("★ ").append(rule).append("\n");  // Unicode star
                            }
                            tv3.setText(starList.toString());


                        }


                        tv1.setText("room type: " + roomtype + "\n" + "\n" + "room location: " + roomlocation + "\n" + "district: " + district + "\n" + "room capacity: " + capacity);

                }
                else{
                    Toast.makeText(roominfoandbooklatest.this, "data doesnt exist", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(roominfoandbooklatest.this, "No images found", Toast.LENGTH_SHORT).show();
                    Toast.makeText(roominfoandbooklatest.this, "No images found", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(roominfoandbooklatest.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
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

            dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbooklatest.this, R.color.teal));
            dotsIndicator.setTabRippleColor(null);

            imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(roominfoandbooklatest.this, R.color.teal));
                    for (int k = 0; k < dotsIndicator.getTabCount(); k++) {
                        TabLayout.Tab tab = dotsIndicator.getTabAt(k);
                        if (tab != null) {
                            tab.view.setBackgroundTintList(k == position
                                    ? ColorStateList.valueOf(ContextCompat.getColor(roominfoandbooklatest.this, R.color.teal))
                                    : ColorStateList.valueOf(ContextCompat.getColor(roominfoandbooklatest.this, R.color.white)));
                        }
                    }
                }
            });
        });

        Log.d("FirebaseStorage", "All images fetched: " + images.size());
    }

    // ImageAdapter nested class for managing images in ViewPager2




    public void booknow(View view) {
        isNavigatingWithinApp = true;
        Intent i = new Intent(this, bookActivity.class);
        i.putExtra(bookActivity.message, s);
        startActivity(i);
    }

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




}