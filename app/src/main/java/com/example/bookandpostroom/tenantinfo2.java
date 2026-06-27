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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mapbox.maps.MapView;

import org.w3c.dom.Text;


public class tenantinfo2 extends AppCompatActivity {
    public static final String message = "ligga";
    public static final String message2 = "hello";
    private MapView mapView;
private String address;
private String checkindate;
private String checkoutdate;

private String tenantname;

private TextView tv;
Uri uri1;
Uri uri2;
Uri uri3;
private  WebView  webView;
String s;
String roomid;
String duration;
String adhno;
String number;
Long no_of_spots;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    Uri[] images = new Uri[3];
    private Dialog progressDialog;
    private MenuItem requestItem;
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        NestedScrollView scrollView = findViewById(R.id.main);

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenantinfo2);
        applySystemBarPadding();
 s=getIntent().getStringExtra(message);
roomid=getIntent().getStringExtra(message2);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
       progressDialog = new Dialog(this);
        progressDialog.setCancelable(false); // Disable dismissal on touch outside
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Inflate the custom layout
        View view = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressDialog.setContentView(view);

        // Show the dialog
        progressDialog.show();
         viewPager = findViewById(R.id.viewPager);
         tabLayout = findViewById(R.id.tabLayout);

        // Set up the TabLayout with ViewPager2


        // Style the TabLayout indicators




       String name=s.split("gapp")[0];
String id=s.split("gapp")[1];
        DatabaseReference dbr0= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");

        dbr0.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                address = snapshot.child("address").getValue(String.class);

                duration = snapshot.child(roomid).child("duration").getValue(String.class);
                  no_of_spots= snapshot.child(roomid).child("no of people equal to").getValue(Long.class);
                checkindate = duration.split("endsat")[0];

                checkoutdate = duration.split("endsat")[1];
number=snapshot.child("number").getValue(String.class);
                adhno = snapshot.child("adhaar").getValue(String.class);
                TextView tv = findViewById(R.id.adhaarno);
                TextView ad = findViewById(R.id.address);
                TextView na = findViewById(R.id.name);
                TextView checkin = findViewById(R.id.checkin);
                TextView checkout = findViewById(R.id.checkout);
                TextView ph = findViewById(R.id.phoneno);
                    TextView spots=findViewById(R.id.spots);
                    spots.setText(no_of_spots.toString());
                tv.setText(adhno);


                if (snapshot.hasChild("name"))
                    tenantname = snapshot.child("name").getValue(String.class);
            ad.setText(address);
            na.setText(name);
            checkin.setText(checkindate);
            checkout.setText(checkoutdate);
            ph.setText(number);

                }
else{
    Toast.makeText(tenantinfo2.this, "data doesnt exist", Toast.LENGTH_SHORT).show();
    finish();
}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        StorageReference strf0= FirebaseStorage.getInstance().getReference("tenantPhotos").child(name).child(id).child("images");
        StorageReference strf1= strf0.child(name+id+"one");
        StorageReference strf3=FirebaseStorage.getInstance().getReference("tenantPhotos").child(name).child(id).child("pfp");
       strf1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
           @Override
           public void onSuccess(Uri uri) {
               images[0]=uri;
               StorageReference strf2= strf0.child(name+id+"two");
               strf2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       images[1]=uri;

                       strf3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                           @Override
                           public void onSuccess(Uri uri) {
                           images[2]=uri;

                               ImageAdapter adapter = new ImageAdapter(tenantinfo2.this, images);
                               viewPager.setAdapter(adapter);
                               runOnUiThread(()->{
                                   new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                                       // This space intentionally left blank
                                   }).attach();
                                   viewPager.setCurrentItem(1, false);
                                   progressDialog.dismiss();

                                   for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                       TabLayout.Tab tab = tabLayout.getTabAt(i);
                                       if (tab != null) {
                                           tab.view.setBackgroundTintList(i == 1 ? // Middle tab (position 1)
                                                   ColorStateList.valueOf(ContextCompat.getColor(tenantinfo2.this, R.color.teal)) :
                                                   ColorStateList.valueOf(ContextCompat.getColor(tenantinfo2.this, R.color.white)));
                                       }
                                   }

                                   // Set the initial selected tab
                                   tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(tenantinfo2.this, R.color.teal));
                                   tabLayout.setTabRippleColor(null);

                                   // Change indicator color when page changes
                                   viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                                       @Override
                                       public void onPageSelected(int position) {
                                           tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(tenantinfo2.this, R.color.teal));
                                           for (int i = 0; i < tabLayout.getTabCount(); i++) {
                                               TabLayout.Tab tab = tabLayout.getTabAt(i);
                                               if (tab != null) {
                                                   tab.view.setBackgroundTintList(i == position ?
                                                           ColorStateList.valueOf(ContextCompat.getColor(tenantinfo2.this, R.color.teal)) :
                                                           ColorStateList.valueOf(ContextCompat.getColor(tenantinfo2.this, R.color.white)));
                                               }
                                           }
                                       }
                                   });


                               });



                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(tenantinfo2.this, "chigga1", Toast.LENGTH_SHORT).show();
                           }
                       });
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(tenantinfo2.this, "chigga2", Toast.LENGTH_SHORT).show();
                   }
               });


           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(tenantinfo2.this, "chigga3", Toast.LENGTH_SHORT).show();
           }
       });


        // Example image resources





    }


    // ImageAdapter nested class for managing images in ViewPager2
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




        }

        @Override
        public int getItemCount() {
            return images2.length;
        }


        public static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    public void booknow(View view)
    {
        Intent i= new Intent(this,bookActivity.class);
        i.putExtra(bookActivity.message,s);
        startActivity(i);



    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }
    public void verifypage(View view) {

        String url = "https://myaadhaar.uidai.gov.in/check-aadhaar-validity/en";

        // Create an Intent with ACTION_VIEW action
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Set the URL in the intent data as a Uri
        intent.setData(Uri.parse(url));

        // Start the intent to open the URL in the user's default browser
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void copy(View view) {

        ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        // Create a ClipData with the text to copy
        ClipData clip = ClipData.newPlainText("adhaarno", adhno);

        // Set the ClipData to the Clipboard
        clipboard.setPrimaryClip(clip);
        ImageButton ib=findViewById(R.id.copy);
        ib.setImageResource(R.drawable.check);
        ib.setBackground(null);
        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ib.setImageResource(R.drawable.copy2);
                ib.setBackgroundResource(R.drawable.copy2);
            }
        },2000);
        // Optional: Show a message to indicate text is copied
        Toast.makeText(view.getContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();

    }
    public void copy2(View view) {

        ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);

        // Create a ClipData with the text to copy
        ClipData clip = ClipData.newPlainText("adhaarno", adhno);

        // Set the ClipData to the Clipboard
        clipboard.setPrimaryClip(clip);
        ImageButton ib=findViewById(R.id.copy2);
        ib.setImageResource(R.drawable.check);
        ib.setBackground(null);
        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ib.setImageResource(R.drawable.copy2);
                ib.setBackgroundResource(R.drawable.copy2);
            }
        },2000);

        // Optional: Show a message to indicate text is copied
        Toast.makeText(view.getContext(), "Phone no. copied to clipboard", Toast.LENGTH_SHORT).show();

    }
    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
    }



}