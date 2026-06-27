package com.example.bookandpostroom;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Activity6 extends AppCompatActivity {
public static final String message="message";
private int count=0;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
   private Uri[] photouri= new Uri[6];
    private Button manualButton;
    private Button automaticButton;
   Uri uri1;

    private String longitude1;
    private String latitude1;
    int totalImageSize = 0;
    private static final int LOCATION_REQUEST_CODE = 100;
   Uri uri2;
   List<String>facilities=new ArrayList<>();
   List<String>rules=new ArrayList<>();

   TextView facttv;
   TextView facttv2;
   Button request;
   int i=1;
   private TextView warning;
    private Button addFacilityButton;
    private Button addRulesButton;
    private RecyclerView facilityList;
    private RecyclerView rulesList;
    private FacilityAdapter facilityAdapter;
    private FacilityAdapter rulesAdapter;
   Dialog progressDialog2;
    private static final long LOCATION_TIMEOUT = 30000; // 30 seconds
    private Handler timeoutHandler = new Handler();

    private LocationManager locationManager;
    private boolean isManualMode = true;
    EditText ownerNameField ;
    EditText ownerAddressField ;
    EditText roomLocationField;
    String name1;
    String id1;
EditText facilityinput;
EditText rulesinput;
private List<Uri> images=new ArrayList<>();
private Uri [] imagesfinal;
    TabLayout dotsIndicator;
    EditText roomname;
    EditText roomcapacity;
    EditText roompricing;
    Spinner districtSpinner ;
    EditText institution;
    EditText ownerNumberField;
    EditText latitudeField;

    EditText longitudeField;

    private String ss;
    RadioGroup radioGroup;
    RadioButton rdPG;
    String number;
    RadioButton rdnonPG;
    CardView cv;

    FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;
    private String svalue;
    Button submit;
    ViewPager2 imageSlider;
    Dialog progressDialog;
    TextView tvsuggestion;
    Dialog progressDialog1;
    private FusedLocationProviderClient fusedLocationClient;
List<String> fcmtokens=new ArrayList<>();
Long roomcapacityoriginal;
Long roomcapacityreachedoriginal;
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
        setContentView(R.layout.activity_6);
        applySystemBarPadding();
facilityList=findViewById(R.id.facility_list);
rulesList=findViewById(R.id.rules_list);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

         roomLocationField = findViewById(R.id.roomlocation);
         districtSpinner = findViewById(R.id.spinner);
         ownerNumberField = findViewById(R.id.ownernumber);
         latitudeField = findViewById(R.id.latitude);
         longitudeField = findViewById(R.id.longitude);
         facttv=findViewById(R.id.factv);
        facttv2=findViewById(R.id.factv2);
         manualButton = findViewById(R.id.manual);
         automaticButton = findViewById(R.id.automatic);
         imageSlider = findViewById(R.id.imageSlider);
         dotsIndicator = findViewById(R.id.dotsIndicator);


        Toolbar tb=findViewById(R.id.tb);
        setSupportActionBar(tb);
        AppBarLayout appBarLayout = findViewById(R.id.abl);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.ct);

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

         radioGroup = findViewById(R.id.radioGroup);
         facilityinput=findViewById(R.id.facility_input);
         rulesinput=findViewById(R.id.rules_input);
         addFacilityButton=findViewById(R.id.add_facility_button);
         addRulesButton=findViewById(R.id.add_rules_button);
request=findViewById(R.id.request);
        fab=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit();
            }
        });
         rdPG=findViewById(R.id.radioButton1PG);
         rdnonPG=findViewById(R.id.radioButton2NONPG);
        roomname=findViewById(R.id.roomname);
        roomcapacity=findViewById(R.id.roomcapacity);
        roompricing=findViewById(R.id.roompricing);



        submit=findViewById(R.id.submitbutton);
        institution=findViewById(R.id.institution);
districtSpinner.setAlpha(0.5f);
        rdPG.setAlpha(0.5f);
        rdnonPG.setAlpha(0.5f);
         tvsuggestion=findViewById(R.id.suggestion);
        addFacilityButton.setOnClickListener(v -> addFacility());
        addRulesButton.setOnClickListener(v->addRules());
       // TextView errorsTextView = findViewById(R.id.errors);

         progressDialog = new Dialog(Activity6.this);
        progressDialog.setCancelable(false); // Disable dismissal on touch outside

        // Inflate the custom layout
        View view = LayoutInflater.from(Activity6.this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressDialog.setContentView(view);

        // Show the dialog
        progressDialog.show();

         ss=getIntent().getStringExtra(message);

        String[] parts = ss.split("room");
         number = parts[1];


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Request location permission if not granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }



        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();



        if(account!=null) {
String name=account.getDisplayName();
String id= account.getUid();
name1=name;
id1=id;

            DatabaseReference df=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner").child("rooms").child("roomId:"+ss);


            df.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    end=snapshot.getChildrenCount();


                        for(DataSnapshot ds:snapshot.child("grabbedby").getChildren())
                    {
                        String name=ds.getValue(String.class).split("idstarts")[0];
                        String id=ds.getValue(String.class).split("idstarts")[1].split("idends")[0];




                        DatabaseReference fgg=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");

                        fgg.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String token=snapshot.child("fcmtoken").getValue(String.class);
                                fcmtokens.add(token);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });





            DatabaseReference dbr= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner").child("rooms").child("roomId:"+ss);



            dbr.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

        if(snapshot.exists()){
        if (snapshot.hasChild("district")) {
// Assuming 'districtSpinner' is your Spinner and 'snapshot' is your DataSnapshot
            String districtValue = snapshot.child("district").getValue(String.class);

            List<String> districtList = new ArrayList<>();
            districtList.add(districtValue);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(Activity6.this, android.R.layout.simple_spinner_item, districtList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            districtSpinner.setAdapter(adapter);

        }
        if (snapshot.hasChild("institution")) {
            String institution1 = snapshot.child("institution").getValue(String.class);
            institution.setText(institution1);

        }
        if (snapshot.hasChild("facilities")) {

            String facilities1 = snapshot.child("facilities").getValue(String.class);

            String facilitiesString = facilities1.replace("[", "").replace("]", "");
            String[] items = facilitiesString.split(",");

            facttv.setText(facilitiesString);
            // Clear existing facilities and add new ones
            if (facilities == null) {
                facilities = new ArrayList<>();
            } else {
                facilities.clear();
            }
            for (String item : items) {
                facilities.add(item.trim()); // Trim any whitespace
            }

            facilityAdapter = new FacilityAdapter(facilities);
            facilityList.setLayoutManager(new LinearLayoutManager(Activity6.this));
            facilityList.setAdapter(facilityAdapter);

        }
        if (snapshot.hasChild("rules")) {
            String rules1 = snapshot.child("rules").getValue(String.class);

            String rulesString = rules1.replace("[", "").replace("]", "");
            String[] ruleitems = rulesString.split(",");

            facttv2.setText(rulesString);
            // Clear existing facilities and add new ones
            if (rules == null) {
                rules = new ArrayList<>();
            } else {
                rules.clear();
            }
            for (String item : ruleitems) {
                rules.add(item.trim()); // Trim any whitespace
            }

            rulesAdapter = new FacilityAdapter(rules);
            rulesList.setLayoutManager(new LinearLayoutManager(Activity6.this));
            rulesList.setAdapter(rulesAdapter);


        }
        if (snapshot.hasChild("latitude")) {
            latitudeField.setText(snapshot.child("latitude").getValue(String.class));
        }
        if (snapshot.hasChild("longitude")) {
            longitudeField.setText(snapshot.child("longitude").getValue(String.class));
        }

        if (snapshot.hasChild("roomType")) {
            String s = snapshot.child("roomType").getValue(String.class);
            if (s.equals("PG")) {
                rdPG.setChecked(true);
            } else {
                rdPG.setChecked(true);

            }

        }
        if (snapshot.hasChild("roomname")) {
            roomname.setText(snapshot.child("roomname").getValue(String.class));


        }
        if (snapshot.hasChild("roomprice")) {
            roompricing.setText(String.valueOf(snapshot.child("roomprice").getValue(Long.class)));


        }
        roomcapacityreachedoriginal=snapshot.child("roomcapacityreached").getValue(Long.class);
        if (snapshot.hasChild("roomcapacity")) {
            long d = snapshot.child("roomcapacity").getValue(long.class);
            String str = String.valueOf(d);

            roomcapacity.setText(str);
            roomcapacityoriginal=d;

        }

        if (snapshot.hasChild("roomlocation")) {
            roomLocationField.setText(snapshot.child("roomlocation").getValue(String.class));
        }
    }
        else{
            Toast.makeText(Activity6.this, "the room doesnt exist", Toast.LENGTH_SHORT).show();
    finish();

        }
    }


    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});



StorageReference dss=FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id).child("images"+number);
            dss.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    if (listResult.getItems().isEmpty()) {
                        Toast.makeText(Activity6.this, "No images found", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(Activity6.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
                }
            });


        }

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

    private void onAllUrisFetched() {
        imagesfinal=images.toArray(new Uri[0]);
        // Use the images list (e.g., update RecyclerView)
        runOnUiThread(() -> {
            ImageAdapter adapter = new ImageAdapter(Activity6.this, imagesfinal);
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

            dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(Activity6.this, R.color.teal));
            dotsIndicator.setTabRippleColor(null);

            imageSlider.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    dotsIndicator.setSelectedTabIndicatorColor(ContextCompat.getColor(Activity6.this, R.color.teal));
                    for (int k = 0; k < dotsIndicator.getTabCount(); k++) {
                        TabLayout.Tab tab = dotsIndicator.getTabAt(k);
                        if (tab != null) {
                            tab.view.setBackgroundTintList(k == position
                                    ? ColorStateList.valueOf(ContextCompat.getColor(Activity6.this, R.color.teal))
                                    : ColorStateList.valueOf(ContextCompat.getColor(Activity6.this, R.color.white)));
                        }
                    }
                }
            });
        });

        Log.d("FirebaseStorage", "All images fetched: " + images.size());
    }
    private void addFacility() {
        String newFacility = facilityinput.getText().toString().trim();
        if (!newFacility.isEmpty()) {
            facilities.add(newFacility);
            facilityAdapter.notifyDataSetChanged();
            facilityinput.setText("");  // Clear input field
        }
    }
    private void addRules() {
        String newFacility = rulesinput.getText().toString().trim();
        if (!newFacility.isEmpty()) {
           rules.add(newFacility);
            rulesAdapter.notifyDataSetChanged();
            rulesinput.setText("");  // Clear input field
        }
    }


    public void showcoordinatesformanual(View view) {
        isManualMode = true;  // Switch to manual mode

        cv=findViewById(R.id.mapcv);
        cv.setVisibility(View.VISIBLE);
        // Show manual input fields, hide automatic button, reset input fields
        manualButton.setVisibility(View.GONE);
        automaticButton.setVisibility(View.VISIBLE);
        latitudeField.setEnabled(true);
        longitudeField.setEnabled(true);
        latitudeField.setVisibility(View.VISIBLE);
        longitudeField.setVisibility(View.VISIBLE);

        // Clear any previously filled coordinates from automatic mode
        latitudeField.setText("");
        longitudeField.setText("");

        // Stop automatic location updates

    }
    public void showmap(View view)
    {
        if( !latitudeField.getText().toString().isEmpty() && !longitudeField.getText().toString().isEmpty())
        { Intent i = new Intent(this, activitymap2.class);
            i.putExtra(activitymap2.latitude, latitudeField.getText().toString());
            i.putExtra(activitymap2.longitude, longitudeField.getText().toString());
            startActivity(i);}
        else{

            Toast.makeText(this, "please enter the coordinates", Toast.LENGTH_SHORT).show();
        }

    }

    public void showcoordinatesforapp(View view) {

        cv=findViewById(R.id.mapcv);
        cv.setVisibility(View.VISIBLE);
        // Check if GPS is enabled

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {

            fetchLocation();
        }

        // Show automatic input, hide manual button



        // Request location updates

    }



    private void fetchLocation() {
        // Check permissions first
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Dialog progressDialogg = new Dialog(this);
        progressDialogg.setCancelable(false); // Disable dismissal on touch outside

        // Inflate the custom layout
        View view1 = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressDialogg.setContentView(view1);

        // Show the dialog
        progressDialogg.show();
        // Create location request
//        LocationRequest locationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(10000)
//                .setFastestInterval(5000);
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, // priority
                10000 // interval in milliseconds
        )
                .setMinUpdateIntervalMillis(5000) // fastest interval
                .build();

        // Use an anonymous inner class for LocationCallback
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    automaticButton.setVisibility(View.GONE);
                    manualButton.setVisibility(View.VISIBLE);
                    latitudeField.setEnabled(false);
                    longitudeField.setEnabled(false);
                    latitudeField.setVisibility(View.VISIBLE);
                    longitudeField.setVisibility(View.VISIBLE);
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    latitude1=String.valueOf(latitude);
                    longitude1=String.valueOf(longitude);
                    progressDialogg.dismiss();
                    latitudeField.setText( latitude1);
                    longitudeField.setText(longitude1);

                    // Stop location updates after getting the location
                    fusedLocationClient.removeLocationUpdates(this);
                }
                else{
                    automaticButton.setVisibility(View.VISIBLE);
                    manualButton.setVisibility(View.VISIBLE);
                    latitudeField.setEnabled(false);
                    longitudeField.setEnabled(false);
                    latitudeField.setVisibility(View.GONE);
                    longitudeField.setVisibility(View.GONE);
                    Toast.makeText(Activity6.this, "error fetching the coordinates,try again", Toast.LENGTH_SHORT).show();

                }
            }
        };

        // Request location updates
        fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop requesting location updates when activity is paused
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop requesting location updates when activity is destroyed
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }




    public void edit()
    {

        View fadeOverlay = findViewById(R.id.fadeOverlay);

// To fade in (show dim effect)
        fadeOverlay.setVisibility(View.VISIBLE);
        fadeOverlay.animate().alpha(1f).setDuration(300).start();

// To fade out (remove dim effect)
        fadeOverlay.animate().alpha(0f).setDuration(300)
                .withEndAction(() -> fadeOverlay.setVisibility(View.GONE))
                .start();
        facttv.setVisibility(View.GONE);
        facilityList.setVisibility(View.VISIBLE);

        roomLocationField.setEnabled(true);
        roomname.setEnabled(true);

        districtSpinner.setEnabled(true);
        roomcapacity.setEnabled(true);

        roompricing.setEnabled(true);

        latitudeField.setEnabled(true);
        longitudeField.setEnabled(true);
        institution.setEnabled(true);
        latitudeField.setVisibility(View.GONE);
        longitudeField.setVisibility(View.GONE);
        radioGroup.setEnabled(true);
        rdPG.setEnabled(true);
        rdnonPG.setEnabled(true);
        rulesList.setVisibility(View.VISIBLE);

roomcapacity.setVisibility(View.VISIBLE);
roompricing.setVisibility(View.VISIBLE);
        districtSpinner.setAlpha(1f);
        rdPG.setAlpha(1f);
        rdnonPG.setAlpha(1f);


        submit.setVisibility(View.VISIBLE);

        manualButton.setVisibility(View.VISIBLE);
        automaticButton.setVisibility(View.VISIBLE);
        facilityinput.setEnabled(true);
        facilityinput.setVisibility(View.VISIBLE);
        addFacilityButton.setEnabled(true);
        addFacilityButton.setVisibility(View.VISIBLE);
        rulesinput.setEnabled(true);
        rulesinput.setVisibility(View.VISIBLE);
        addRulesButton.setEnabled(true);
        addRulesButton.setVisibility(View.VISIBLE);
        roomLocationField.setFocusable(true); roomLocationField.setFocusableInTouchMode(true);
        roomname.setFocusable(true); roomname.setFocusableInTouchMode(true);
        districtSpinner.setFocusable(true); districtSpinner.setFocusableInTouchMode(true);
        roomcapacity.setFocusable(true); roomcapacity.setFocusableInTouchMode(true);
        roompricing.setFocusable(true); roompricing.setFocusableInTouchMode(true);
        latitudeField.setFocusable(true); latitudeField.setFocusableInTouchMode(true);
        longitudeField.setFocusable(true); longitudeField.setFocusableInTouchMode(true);
        institution.setFocusable(true); institution.setFocusableInTouchMode(true);
        facilityinput.setFocusable(true); facilityinput.setFocusableInTouchMode(true);
        rulesinput.setFocusable(true); rulesinput.setFocusableInTouchMode(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Activity6.this,
                R.array.predefined_values, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        districtSpinner.setAdapter(adapter);
        progressDialog1 = new Dialog(Activity6.this);
        progressDialog1.setCancelable(false); // Disable dismissal on touch outside

        // Inflate the custom layout


        fab.setVisibility(View.GONE);



    /*   DatabaseReference dss= FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner").child("rooms").child("roomId:"+ss).child("listforchangeapproved");

        dss.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists())
                {

roomLocationField.setEnabled(true);
                    roomname.setEnabled(true);
                    facttv.setVisibility(View.GONE);
                    facilityList.setVisibility(View.VISIBLE);
                    rulesList.setVisibility(View.VISIBLE);
                    roomcapacity.setEnabled(true);
                    roompricing.setEnabled(true);
                    districtSpinner.setEnabled(true);
                    ownerNumberField.setEnabled(true);
                    latitudeField.setEnabled(true);


                    institution.setEnabled(true);
                    districtSpinner.setAlpha(1f);
                    rdPG.setAlpha(1f);
                    rdnonPG.setAlpha(1f);
                    longitudeField.setEnabled(true);
                    latitudeField.setVisibility(View.GONE);
                    longitudeField.setVisibility(View.GONE);
                    radioGroup.setEnabled(true);
                    rdPG.setEnabled(true);
                    rdnonPG.setEnabled(true);
                    facilityList.setOnTouchListener(null);
                    facilityList.setAlpha(1.0f);
                    rulesList.setOnTouchListener(null);
                    rulesList.setAlpha(1.0f);
                    submit.setVisibility(View.VISIBLE);

                    manualButton.setVisibility(View.VISIBLE);
                    automaticButton.setVisibility(View.VISIBLE);
                    facilityinput.setEnabled(true);
                    facilityinput.setVisibility(View.VISIBLE);
                    addFacilityButton.setEnabled(true);
                    addFacilityButton.setVisibility(View.VISIBLE);

                    rulesinput.setEnabled(true);
                    rulesinput.setVisibility(View.VISIBLE);
                    addRulesButton.setEnabled(true);
                    addRulesButton.setVisibility(View.VISIBLE);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Activity6.this,
                            R.array.predefined_values, R.layout.spinner_item);
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    districtSpinner.setAdapter(adapter);
                    progressDialog1.dismiss();

                }

               else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        long s = ds.getValue(long.class);
                        if (s == 0) {
                            i = 0;


                        }


                    }

                    if (i == 1) {


                        ownerNameField.setEnabled(true);
                        facttv.setVisibility(View.GONE);
                        facilityList.setVisibility(View.VISIBLE);
                        ownerAddressField.setEnabled(true);
                        roomLocationField.setEnabled(true);
                        roomname.setEnabled(true);
                        roomcapacity.setEnabled(true);
                        roompricing.setEnabled(true);
                        districtSpinner.setEnabled(true);
                        ownerNumberField.setEnabled(true);
                        latitudeField.setEnabled(true);
                        longitudeField.setEnabled(true);
                        institution.setEnabled(true);
                        latitudeField.setVisibility(View.GONE);
                        longitudeField.setVisibility(View.GONE);
                        radioGroup.setEnabled(true);
                        rdPG.setEnabled(true);
                        rdnonPG.setEnabled(true);
rulesList.setVisibility(View.VISIBLE);


                        districtSpinner.setAlpha(1f);
                        rdPG.setAlpha(1f);
                        rdnonPG.setAlpha(1f);


                        submit.setVisibility(View.VISIBLE);

                        manualButton.setVisibility(View.VISIBLE);
                        automaticButton.setVisibility(View.VISIBLE);
                        facilityinput.setEnabled(true);
                        facilityinput.setVisibility(View.VISIBLE);
                        addFacilityButton.setEnabled(true);
                        addFacilityButton.setVisibility(View.VISIBLE);
                        rulesinput.setEnabled(true);
                        rulesinput.setVisibility(View.VISIBLE);
                        addRulesButton.setEnabled(true);
                        addRulesButton.setVisibility(View.VISIBLE);

                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Activity6.this,
                                R.array.predefined_values, R.layout.spinner_item);
                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        districtSpinner.setAdapter(adapter);
                        progressDialog1.dismiss();
                    }
                    else{
progressDialog1.dismiss();
warning.setVisibility(View.VISIBLE);
request.setVisibility(View.VISIBLE);
                        facttv.setVisibility(View.VISIBLE);
                        facttv2.setVisibility(View.VISIBLE);

                        rulesList.setVisibility(View.GONE);

                        facilityList.setVisibility(View.GONE);



                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/








    }
    long start=0;
    long end;

  /*  public void back(View view)
    {facttv.setVisibility(View.VISIBLE);
        facilityList.setVisibility(View.GONE);
        facttv2.setVisibility(View.VISIBLE);
        rulesList.setVisibility(View.GONE);

        back.setVisibility(view.GONE);
        edit.setVisibility(view.VISIBLE);
        districtSpinner.setAlpha(0.5f);
        rdPG.setAlpha(0.5f);
        cv=findViewById(R.id.mapcv);
        cv.setVisibility(View.GONE);
        rdnonPG.setAlpha(0.5f);
        rulesinput.setEnabled(false);
        rulesinput.setVisibility(View.GONE);
        addRulesButton.setEnabled(false);
        addRulesButton.setVisibility(View.GONE);
        facilityinput.setEnabled(false);
        facilityinput.setVisibility(View.GONE);
        addFacilityButton.setEnabled(false);
        addFacilityButton.setVisibility(View.GONE);
        districtSpinner.setEnabled(false);
        roomcapacity.setEnabled(false);
        roomname.setEnabled(false);


        roompricing.setEnabled(false);
        latitudeField.setEnabled(false);
        longitudeField.setEnabled(false);
        institution.setEnabled(false);
        latitudeField.setVisibility(view.VISIBLE);
        longitudeField.setVisibility(view.VISIBLE);
        radioGroup.setEnabled(false);
        rdPG.setEnabled(false);
        rdnonPG.setEnabled(false);

        submit.setVisibility(view.GONE);

        manualButton.setVisibility(View.GONE);
        automaticButton.setVisibility(View.GONE);





    }
   */
  public void editphotos(View view)
  {
      Intent i=new Intent(this,Editphotosandaddphotos.class);
      i.putExtra(Editphotosandaddphotos.message,ss);
      startActivity(i);


  }
    public void submit(View view) {
        TextView errorsTextView = findViewById(R.id.errors);

        // Check if any of the fields are empty
        if (facilities.toString().isEmpty()||rules.toString().isEmpty()||institution.getText().toString().isEmpty()||institution.getText().toString().contains("+")||roompricing.getText().toString().isEmpty() || roomcapacity.getText().toString().isEmpty() ||
                roomname.getText().toString().isEmpty() ||
                roomLocationField.getText().toString().isEmpty() ||
                districtSpinner.getSelectedItem() == null ||
                latitudeField.getText().toString().isEmpty() ||
                longitudeField.getText().toString().isEmpty() ||
                radioGroup.getCheckedRadioButtonId() == -1) {
            errorsTextView.setVisibility(View.VISIBLE);
            // Set drawable for the suggestion TextView
            Drawable drawable = getResources().getDrawable(R.drawable.error);
            int width = 50; // set your desired width in pixels
            int height = 50; // set your desired height in pixels
            drawable.setBounds(0, 0, width, height);
            errorsTextView.setCompoundDrawables(drawable, null, null, null);
            if(institution.getText().toString().contains("+")){
                errorsTextView.setText("Institution field  cannot contain symbols such as +#$%@ etc");
            }
                else{
            errorsTextView.setText("fields cannot be empty");}
        } else {

            String price = roompricing.getText().toString();
            Long roomprice=Long.parseLong(price);
            String capacity = roomcapacity.getText().toString();
            Long roomcap=Long.parseLong(capacity);
            String nameroom = roomname.getText().toString();

            String roomlocation = roomLocationField.getText().toString();
            String district = districtSpinner.getSelectedItem().toString();

            String latitude = latitudeField.getText().toString();
            String longitude = longitudeField.getText().toString();
            String institution1=institution.getText().toString();


            Dialog progressDialog = new Dialog(Activity6.this);
            progressDialog.setCancelable(false);
            View view1 = LayoutInflater.from(Activity6.this).inflate(R.layout.progress_dialog_layout, null);
            progressDialog.setContentView(view1);



            progressDialog.show();



                String roomType = radioGroup.getCheckedRadioButtonId() == rdPG.getId() ? "PG" : "Non-PG";
            DatabaseReference dk=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner")
                    ;
            dk.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if (snapshot.child("rooms").child("roomId:"+ss).child("grabbedby").exists()||snapshot.child("actualrequestsrecieved").exists())
                   {
if(Long.parseLong(capacity)<roomcapacityoriginal)
{progressDialog.dismiss();
    Toast.makeText(Activity6.this, "failed to change:in order to decrease the capacity,current requests and occupants need to be removed", Toast.LENGTH_LONG).show();
Intent i=new Intent(Activity6.this,Activity5.class);
startActivity(i);
}
else{
    DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner")
            .child("rooms").child("roomId:"+ss);
    ds.runTransaction(new Transaction.Handler() {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
            String jj=currentData.child("queue").getValue(String.class);
            if(jj==null ||jj.isEmpty())

            {
                currentData.child("queue").setValue(name1+id1);
                currentData.child("roomlocation").setValue(roomlocation);
                currentData.child("district").setValue(district);
                currentData.child("roomname").setValue(nameroom);
                currentData.child("roomprice").setValue(Long.parseLong(roompricing.getText().toString()));
                currentData.child("roomcapacity").setValue(Long.parseLong(capacity));
                currentData.child("latitude").setValue(latitude);
                currentData.child("longitude").setValue(longitude);
                currentData.child("roomType").setValue(roomType);
                currentData.child("capacityremaining").setValue(Long.parseLong(capacity)-roomcapacityreachedoriginal);
                currentData.child("institution").setValue(institution1);
                currentData.child("rules").setValue(rules.toString());
                currentData.child("facilities").setValue(facilities.toString());

            }
            else{
                return Transaction.abort();
            }

            return Transaction.success(currentData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
            if(!committed)
            {

                Toast.makeText(Activity6.this, "can't make changes rightnow,please comeback later", Toast.LENGTH_SHORT).show();
            }
            else if(committed){
                if(fcmtokens.size()>0) {
                    for (String s : fcmtokens) {
                        Sendnotification notificationsSender = new Sendnotification(s, "changed", "changes have been made to the room by room owner",
                                Activity6.this);
                        notificationsSender.SendNotifications();

                    }
                }
                Intent i=new Intent(Activity6.this,Activity6.class);
                i.putExtra(Activity6.message,ss);
                startActivity(i);
                finish();
            }
        }
    });

}
                   }
                   else{
                       DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner")
                               .child("rooms").child("roomId:"+ss);
                       ds.runTransaction(new Transaction.Handler() {
                           @NonNull
                           @Override
                           public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                               String jj=currentData.child("queue").getValue(String.class);
                               if(jj==null ||jj.isEmpty())

                               {
                                   currentData.child("queue").setValue(name1+id1);
                                   currentData.child("roomlocation").setValue(roomlocation);
                                   currentData.child("district").setValue(district);
                                   currentData.child("roomname").setValue(nameroom);
                                   currentData.child("roomprice").setValue(Long.parseLong(roompricing.getText().toString()));
                                   currentData.child("roomcapacity").setValue(Long.parseLong(capacity));
                                   currentData.child("latitude").setValue(latitude);
                                   currentData.child("longitude").setValue(longitude);
                                   currentData.child("capacityremaining").setValue(Long.parseLong(capacity)-roomcapacityreachedoriginal);
                                   currentData.child("roomType").setValue(roomType);
                                   currentData.child("institution").setValue(institution1);
                                   currentData.child("rules").setValue(rules.toString());
                                   currentData.child("facilities").setValue(facilities.toString());

                               }
                               else{
                                   return Transaction.abort();
                               }

                               return Transaction.success(currentData);
                           }

                           @Override
                           public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                               if(!committed)
                               {

                                   Toast.makeText(Activity6.this, "can't make changes rightnow,please comeback later", Toast.LENGTH_SHORT).show();
                               }
                               else if(committed){
                                   if(fcmtokens.size()>0) {
                                       for (String s : fcmtokens) {
                                           Sendnotification notificationsSender = new Sendnotification(s, "changed", "changes have been made to the room by room owner",
                                                   Activity6.this);
                                           notificationsSender.SendNotifications();

                                       }
                                   }
                                   Intent i=new Intent(Activity6.this,Activity6.class);
                                   i.putExtra(Activity6.message,ss);
                                   startActivity(i);
                                   finish();
                               }
                           }
                       });


                   }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });








        }
    }




}