package com.example.bookandpostroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
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

long k;
long l;
    private Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_roominfoandbookfinal2);
        s=getIntent().getStringExtra(message);
        String name=s.split("idstarts")[0];
        String id=s.split("idstarts")[1].split("idends")[0];
        String number=s.split("room")[1];
        name1=name;
        id1=id;


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String tenantname=account.getDisplayName();
        String tenantid=account.getId();

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
                    roomname=snapshot.child("roomname").getValue(String.class);
                    roomprice=String.valueOf(snapshot.child("roomprice").getValue(long.class));
occupants=snapshot.child("bookedbygender").getValue(String.class);
if(occupants.isEmpty())
{
    occupants="no current occupants";
}
                    if (snapshot.hasChild("roomType"))
                        roomtype = snapshot.child("roomType").getValue(String.class);
                    if (snapshot.hasChild("roomlocation"))
                        roomlocation = snapshot.child("roomlocation").getValue(String.class);
                    if (snapshot.hasChild("roomcapacity"))
                        capacity = String.valueOf(snapshot.child("roomcapacity").getValue(long.class));

                    remaining=snapshot.child("roomcapacity").getValue(long.class)-snapshot.child("roomcapacityreached").getValue(long.class);
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
                price.setText("₹"+roomprice);

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

            progressDialog.dismiss();

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





        //  decrementRoomCheck();
        super.onBackPressed();
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