package com.example.bookandpostroom;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class roominfoandbookfinal33 extends AppCompatActivity implements fragmentfortenantlist.RequestListener,fragmentfortenantlist.RequestListener2{

    public static final String message="hello";
    public static final String tenantinformation="jack";
    String s;
    private String district;
    private String facilities;
    private String latitude;
    private String longitude;
    private String roomtype;
    private String roomlocation;
    private String rules;
    private TextView tv1;
    private  TextView tv2;
    String roomidd;
    private String name1;
    private String id1;
    private TextView tv3;
    private long capacity;
    TabLayout dotsIndicator;
    ViewPager2 imageSlider;
    private boolean isNavigatingWithinApp = false;
    private boolean alreadyincremented=false;
    private boolean alreadydecremented=false;
    private long max;
    private List<Uri> images=new ArrayList<>();
    private Uri [] imagesfinal;

String notificationowner1;
    long k;
    long l;
    private Dialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_roominfoandbookfinal33);
        s=getIntent().getStringExtra(message);
        roomidd=s;
        String name=s.split("idstarts")[0];
        String id=s.split("idstarts")[1].split("idends")[0];
        String number=s.split("room")[1];
        name1=name;
        id1=id;

        String info=getIntent().getStringExtra(tenantinformation);
        fragmentfortenantlist fragment = fragmentfortenantlist.newInstance(info);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
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

        DatabaseReference gg=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner");
        gg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationowner1=snapshot.child("fcmtoken").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference gh= FirebaseDatabase.getInstance().getReference("google")
                .child(name).child(id).child("owner").child("rooms").child("roomId"+":"+s);
        gh.keepSynced(true);
        gh.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {







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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        StorageReference dkk= FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id).child("images"+number);
        dkk.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (listResult.getItems().isEmpty()) {
                    Toast.makeText(roominfoandbookfinal33.this, "No images found", Toast.LENGTH_SHORT).show();
                    Toast.makeText(roominfoandbookfinal33.this, "No images found", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(roominfoandbookfinal33.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
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

public void functionforrequest(int id,String s)
{

    String name=s.split("idstarts")[0];
    String idd=s.split("idstarts")[1].split("idends")[0];
    String ss=name+"gapp"+idd;
    Intent i=new Intent(roominfoandbookfinal33.this, tenantinfo.class);
    i.putExtra(tenantinfo.message,ss);
    i.putExtra(tenantinfo.message2,roomidd);
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
    boolean alreadyleftbytenant=false;
    String notificationtenant;
    String notificationowner;
    public void functiondeletetenantafterbooking(int id,String s)
    {
        Dialog warning = new Dialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialogfordeleteafterbookingconfirmation, null);

        Button cancel=view.findViewById(R.id.cancelButton);
        Button ok=view.findViewById(R.id.okButton);
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
                Dialog dd=new Dialog(roominfoandbookfinal33.this);
                dd.setContentView(R.layout.progress_dialog_layout);
                dd.setCancelable(false);
                dd.show();
String tenant=s;
String owner=roomidd;
          String tenantname=s.split("idstarts")[0];
          String tenantid=s.split("idstarts")[1].split("idends")[0];
String ownername=name1;
String ownerid=id1;


                DatabaseReference ds=FirebaseDatabase.getInstance().getReference("deletebookingafterbooking");
                ds.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                        Long count=currentData.getChildrenCount();
                        if(count==null)count=0L;
                        count++;
                        Map<String,Object> data=new HashMap<>();
                        data.put("tenantname",tenantname);
                        data.put("tenantid",tenantid);
                        data.put("tenant",tenantname+"idstarts"+tenantid+"idends");
                        data.put("owner",roomidd);
                        data.put("ownername",name1);
                        data.put("ownerid",id1);
                        data.put("alreadynotification",notificationowner1);



                        currentData.child("request"+count).setValue(data);

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        Toast.makeText(roominfoandbookfinal33.this, "removed  request uploaded successfully", Toast.LENGTH_SHORT).show();
                        Intent i=new Intent(roominfoandbookfinal33.this,Activity5.class);
                        startActivity(i);
                    }
                });



         /*  DatabaseReference dk = FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");

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
                                Toast.makeText(roominfoandbookfinal33.this, "already dealted by user", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(roominfoandbookfinal33.this, "already dealted by user", Toast.LENGTH_SHORT).show();
                                            //     notification("tenant has already left");

                                        }

                                    }
                                }
                            });


                        }
                    }
                });
*/


            }
        });

        warning.setContentView(view);
        warning.setCancelable(false);
        warning.show();



    }

    @Override
    public void onBackPressed() {





        //  decrementRoomCheck();
        super.onBackPressed();
    }


}