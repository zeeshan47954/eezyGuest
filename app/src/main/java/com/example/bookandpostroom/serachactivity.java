package com.example.bookandpostroom;



import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;

import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class serachactivity extends AppCompatActivity implements fragmenttoshowsearchedelements.Favoritelistener,fragmenttoshowsearchedelements.Listenerhome2{
    String[] institutions;
    long start=0;
    long end;
    public FrameLayout fragmentcontainer;
    boolean permissionDeniedOnce = false;
    public Dialog ss;
    public LinearLayout ll1;
    String tenantlatitude;
    String tenantlongitude;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_REQUEST_CODE = 100;
    private FusedLocationProviderClient fusedLocationClient;
    String[] districts;
    String districtchosen;
    String institutechosen;
    String pricechosen;
    String roomtypechosen;
    String distancechosen;
    EditText roomcapacity;
    TextView searchTextView;
    TextView found;
    private LocationManager locationManager;
    AutoCompleteTextView institution;
    AppCompatSpinner districtSpinner;
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        NestedScrollView scrollView = findViewById(R.id.nested_scroll_view);

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
        setContentView(R.layout.activity_serachactivity);
        applySystemBarPadding();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

         districtSpinner = findViewById(R.id.district_spinner);
// Rotation animation (0 → 360)
        fragmentcontainer=findViewById(R.id.fragment_container);

searchTextView=findViewById(R.id.searchtextview);
found=findViewById(R.id.found);
        List<String> districtname=new ArrayList<>();
        List<String>institutename=new ArrayList<>();
        ll1=findViewById(R.id.ll1);
roomcapacity=findViewById(R.id.capacity);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

         institution = findViewById(R.id.institution);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkLocationPermission();

        DatabaseReference dj= FirebaseDatabase.getInstance().getReference("google").child("district");
        dj.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                end=snapshot.getChildrenCount();
             for(DataSnapshot ss:snapshot.getChildren())
             {districtname.add(ss.getKey());
start++;
                 String k="";
                 for(DataSnapshot hd:ss.child("institution").getChildren())
                 {
                     if(k==null)k=hd.getKey();
                     else {
                         k=k+"+"+hd.getKey();
                     }


                 }
                 institutename.add(k);

                 if(start==end)
                 {
 districts = districtname.toArray(new String[0]);
// Custom ArrayAdapter with teal text
                     ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(serachactivity.this, android.R.layout.simple_spinner_item, districts) {
                         @Override
                         public View getView(int position, View convertView, ViewGroup parent) {
                             TextView view = (TextView) super.getView(position, convertView, parent);
                             view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // selected text color
                             return view;
                         }

                         @Override
                         public View getDropDownView(int position, View convertView, ViewGroup parent) {
                             TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                             view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // dropdown text color
                             return view;
                         }
                     };
                     institutions=institutename.get(0).split("\\+");
                     adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                     districtSpinner.setAdapter(adapter4);
                     ArrayAdapter<String> institutionAdapter = new ArrayAdapter<String>(serachactivity.this, android.R.layout.simple_dropdown_item_1line, institutions) {
                         @Override
                         public View getView(int position, View convertView, ViewGroup parent) {
                             TextView view = (TextView) super.getView(position, convertView, parent);
                             view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // selected text color
                             return view;
                         }

                         @Override
                         public View getDropDownView(int position, View convertView, ViewGroup parent) {
                             TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                             view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // dropdown item text color
                             return view;
                         }
                     };
                     institution.setDropDownBackgroundResource(android.R.color.white); // optional
                     institution.setDropDownHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                     institution.setDropDownWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

                     institution.setDropDownAnchor(R.id.kl); // or parent view ID
                      // shows above
                     institution.setAdapter(institutionAdapter);
                     institution.setThreshold(1);
                     NestedScrollView scrollView = findViewById(R.id.nested_scroll_view);


                     break;
                 }



             }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
// Rotate forward (0 → 360)
        ObjectAnimator rotateForward = ObjectAnimator.ofFloat(searchTextView, "rotationY", 0f, 360f);
        rotateForward.setDuration(2000);

// Pause for 1 second
        ValueAnimator pause1 = ValueAnimator.ofInt(0, 0);
        pause1.setDuration(1000);

// Rotate backward (360 → 0)
        ObjectAnimator rotateBackward = ObjectAnimator.ofFloat(searchTextView, "rotationY", 360f, 0f);
        rotateBackward.setDuration(2000);

// Pause for 1 second
        ValueAnimator pause2 = ValueAnimator.ofInt(0, 0);
        pause2.setDuration(1000);

// Put them in a set
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(rotateForward, pause1, rotateBackward, pause2);

// Loop forever by restarting when finished
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatorSet.start(); // restart after finishing
            }
        });

// Start animation
        animatorSet.start();


        AppCompatSpinner spinner = findViewById(R.id.distance_spinner);




        String[] distances = {
                "Near me",
                "Within 1 km",
                "1 - 2 km from you",
                "2 - 3 km from you",
                "3 - 4 km from you",
                "4 - 5 km from you",
                "5 - 6 km from you",
                "6 - 7 km from you",
                "7 - 8 km from you",
                "8 - 9 km from you",
                "9 - 10 km from you",
                "any distance"

        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, distances) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // modern way
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal));
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

// Handle selection
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = distances[position];
                distancechosen=selected;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        AppCompatSpinner roomTypeSpinner = findViewById(R.id.roomtype_spinner);

        String[] roomTypes = {"PG", "Non-PG"};

// Custom ArrayAdapter with teal text
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, roomTypes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // selected text color
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // dropdown text color
                return view;
            }
        };

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomTypeSpinner.setAdapter(adapter2);

// Handle selection
        roomTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = roomTypes[position];
                roomtypechosen=selected;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        AppCompatSpinner priceSpinner = findViewById(R.id.price_spinner);

        String[] priceOptions = {
                "Below 4000",
                "4000-5000",
                "5000-6000",
                "6000-7000",
                "7000-8000",
                "8000-9000",
                "9000-10000",
                "Above 10000"
        };

// Custom ArrayAdapter with teal text
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priceOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // selected text color
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getDropDownView(position, convertView, parent);
                view.setTextColor(ContextCompat.getColor(getContext(), R.color.teal)); // dropdown text color
                return view;
            }
        };

        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priceSpinner.setAdapter(adapter3);

// Handle selection
        priceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = priceOptions[position];
                pricechosen=selected;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



// Handle selection
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = districts[position];
                institutions=institutename.get(position).split("\\+");
districtchosen=selected;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


// Example institutions list

// Custom ArrayAdapter with teal-colored dropdown items
       // start showing suggestions after typing 1 character

// Optional: show dropdown immediately on click
        institution.setOnClickListener(v -> institution.showDropDown());





    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ Permission granted
                fetchLocation();
            } else {
                // ❌ Permission denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // “Don’t ask again” selected — reopen settings instead
                    Toast.makeText(this, "Location permission is required. Please enable it in settings.", Toast.LENGTH_LONG).show();
                    showCustomAlertDialog();
                } else {
                    // User pressed cancel — go back to previous screen
                    Toast.makeText(this, "Permission denied. Permission required to serve you better.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 🔁 Always check again if user returned from settings or denied before
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fetchLocation();
        } else if (permissionDeniedOnce) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            Toast.makeText(this, "location permission is required", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);


        } else {
            fetchLocation();
        }
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

                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    tenantlatitude=String.valueOf(latitude);
                    tenantlongitude=String.valueOf(longitude);
                    
                    progressDialogg.dismiss();
                    // Stop location updates after getting the location
                    fusedLocationClient.removeLocationUpdates(this);
                }
                else{

                    Toast.makeText(serachactivity.this, "error fetching the coordinates,try again", Toast.LENGTH_SHORT).show();

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
    private void showCustomAlertDialog() {
        // Create the AlertDialog Builder
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);

        // Set the message with teal color text
        TextView message = new TextView(this);
        message.setText("This app requires location access to provide the best  experience.");
        message.setPadding(50, 40, 50, 10);
        message.setTextSize(16);
        message.setTextColor(ContextCompat.getColor(this, R.color.teal)); // 💠 teal text
        builder.setView(message);

        // Set title
        TextView title = new TextView(this);
        title.setText("Location Access Required");
        title.setPadding(50, 40, 50, 10);
        title.setTextSize(20);
        title.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark)); // 🔴 Red title
        builder.setCustomTitle(title);
        // Add buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            // Action for OK
           openAppSettings(); // example action
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            Toast.makeText(this, "Permission denied. permission is required.", Toast.LENGTH_SHORT).show();
            finish(); // exit activity
        });

        // Create and show the AlertDialog
        androidx.appcompat.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        // Customize button colors after showing the dialog
        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.teal)); // 💠 OK button teal
        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark)); // ❌ Cancel button red
    }

    long end1;
    long start1=0;
    long start2=0;
    long end2;
    Boolean found1=false;
    public void search(View view)
    {

        if(institution.getText().toString()==null||institution.getText().toString().isEmpty()||roomcapacity.getText().toString()==null||roomcapacity.getText().toString().isEmpty()||tenantlatitude==null ||tenantlongitude==null||tenantlatitude.isEmpty()||tenantlongitude.isEmpty())
        {
            Toast.makeText(this, "fields cannot be empty", Toast.LENGTH_SHORT).show();
            if(tenantlatitude==null ||tenantlongitude==null||tenantlatitude.isEmpty()||tenantlongitude.isEmpty())
            {
                Toast.makeText(this, "location access is required", Toast.LENGTH_SHORT).show();

                showCustomAlertDialog();


            }

        }


       else{
if(distancechosen==null)distancechosen="Near me";
if(districtchosen==null)districtchosen=districts[0];
        if(roomtypechosen==null)roomtypechosen="PG";
        if(pricechosen==null)pricechosen="Below 4000";


            long distanceValuemax; // in meters
long distanceValuemin;
            switch (distancechosen) {
                case "Near me":
                    distanceValuemax = 500; // 0 < variable < 500
                   distanceValuemin=0;
                    break;

                case "Within 1 km":
                    distanceValuemax= 1000;
                    distanceValuemin=500;
                    break;
                case "1 - 2 km from you":
                    distanceValuemax=2000;
                    distanceValuemin=1000;
                    break;
                case "2 - 3 km from you":
                    distanceValuemax=3000;
                    distanceValuemin=2000;
                    break;

                case "3 - 4 km from you":
                    distanceValuemax=4000;
                    distanceValuemin=3000;
                    break;

                case "4 - 5 km from you":
                    distanceValuemax=5000;
                    distanceValuemin=4000;
                    break;

                case "5 - 6 km from you":
                    distanceValuemax=6000;
                    distanceValuemin=5000;
                    break;

                case "6 - 7 km from you":
                    distanceValuemax=7000;
                    distanceValuemin=6000;
                    break;

                case "7 - 8 km from you":
                    distanceValuemax=8000;
                    distanceValuemin=7000;
                    break;

                case "8 - 9 km from you":
                    distanceValuemax=9000;
                    distanceValuemin=8000;
                    break;

                case "9 - 10 km from you":
                    distanceValuemax=10000;
                    distanceValuemin=9000;
                    break;

                case "any distance":
                default:
                    distanceValuemin=10000; // practically “no limit”
                    distanceValuemax=Long.MAX_VALUE;
                    break;
            }



            long priceValuemax;
            long priceValuemin;

            switch (pricechosen) {
                case "Below 4000":
                    priceValuemax=4000; // anything < 4000
                    priceValuemin=0;
                    break;

                case "4000-5000":
                    priceValuemax=5000; // anything < 4000
                    priceValuemin=4000;
                    break;

                case "5000-6000":
                    priceValuemax=6000; // anything < 4000
                    priceValuemin=5000;
                    break;

                case "6000-7000":
                    priceValuemax=7000; // anything < 4000
                    priceValuemin=6000;
                    break;

                case "7000-8000":
                    priceValuemax=8000; // anything < 4000
                    priceValuemin=7000;
                    break;

                case "8000-9000":
                    priceValuemax=9000; // anything < 4000
                    priceValuemin=8000;
                    break;

                case "9000-10000":
                    priceValuemax=10000; // anything < 4000
                    priceValuemin=9000;
                    break;

                case "Above 10000":
                default:
                    priceValuemin =10000; // no upper limit
                    priceValuemax=Long.MAX_VALUE;
                    break;
            }
            long roomTypeValue;

            switch (roomtypechosen) {
                case "PG":
                    roomTypeValue = 1; // let's say PG = 1
                    break;

                case "Non-PG":
                default:
                    roomTypeValue = 2; // Non-PG = 2
                    break;
            }



             ss=new Dialog(this);
           ss.setContentView(R.layout.progress_dialog_layout);
           ss.setCancelable(false);
           ss.show();
List<String>roomids=new ArrayList<>();
List<String>roomidspassed=new ArrayList<>();
List<String>roomidspassedbutweak=new ArrayList<>();
List<String>roomidspassedbutweaker=new ArrayList<>();
List<String>roomidspassedbutweakest=new ArrayList<>();
List<String>roomidsrejected=new ArrayList<>();
institutechosen=institution.getText().toString();



               DatabaseReference df=FirebaseDatabase.getInstance().getReference("google").child("district").child(districtchosen).child("institution").child(institutechosen);
           df.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(!snapshot.exists())
                   {
                       ss.dismiss();
                       Toast.makeText(serachactivity.this, "institution doesn't exist in database\n try something else", Toast.LENGTH_SHORT).show();
                   }
                   else{
                   end1 = snapshot.getChildrenCount();
                   for (DataSnapshot dj : snapshot.getChildren()) {
                       start1++;
                       String l = dj.getValue(String.class);


                       roomids.add(l);
                       if (start1 == end1) {

                           if (roomids.isEmpty()) {
                               ss.dismiss();

                               Toast.makeText(serachactivity.this, "couldn't find any room for this selection", Toast.LENGTH_SHORT).show();

                           } else {
                               end2 = roomids.size();
                               for (String jj : roomids) {
                                   start2++;
                                   String ownername = jj.split("idstarts")[0];
                                   String ownerid = jj.split("idstarts")[1].split("idends")[0];

                                   DatabaseReference ds = FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner").child("rooms").child("roomId:" + jj);
                                   ds.addListenerForSingleValueEvent(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           String latitude = snapshot.child("latitude").getValue(String.class);
                                           String longitude = snapshot.child("longitude").getValue(String.class);


                                           double lat1 = Double.parseDouble(tenantlatitude);
                                           double lon1 = Double.parseDouble(tenantlongitude);
                                           double lat2 = Double.parseDouble(latitude);
                                           double lon2 = Double.parseDouble(longitude);

                                           float[] results = new float[1];
                                           Location.distanceBetween(lat1, lon1, lat2, lon2, results);

                                           float distanceInMeters = results[0];


                                           int matchCount = 0;

// Check each condition and increment matchCount if it matches
                                           if (snapshot.child("roomcapacity").getValue(Long.class) != null &&
                                                   snapshot.child("roomcapacity").getValue(Long.class) >= Long.parseLong(roomcapacity.getText().toString())) {
                                               matchCount++;
                                           }

                                           if (snapshot.child("roomType").getValue(String.class) != null &&
                                                   snapshot.child("roomType").getValue(String.class).equals(roomtypechosen)) {
                                               matchCount++;
                                           }

                                           if (distanceValuemin < distanceInMeters && distanceInMeters <= distanceValuemax) {
                                               matchCount++;
                                           }

                                           if (priceValuemin < snapshot.child("roomprice").getValue(Long.class) && snapshot.child("roomprice").getValue(Long.class) <= priceValuemax) {
                                               matchCount++;
                                           }

// Add to the appropriate list based on matches
                                           switch (matchCount) {
                                               case 4:
                                                   roomidspassed.add(jj);
                                                   break;
                                               case 3:
                                                   roomidspassedbutweak.add(jj); // 3 or 4 matches
                                                   break;
                                               case 2:
                                                   roomidspassedbutweaker.add(jj); // 2 matches
                                                   break;
                                               case 1:
                                                   roomidspassedbutweakest.add(jj); // 1 match
                                                   break;
                                               default:
                                                   roomidsrejected.add(jj); // 0 matches
                                                   break;
                                           }

                                           if (start2 == end2) {

                                               if (!roomidsrejected.isEmpty()) {
                                                   Toast.makeText(serachactivity.this, "could not find the particular room..showing rooms you might like", Toast.LENGTH_SHORT).show();
                                                   ArrayList<String> combined = new ArrayList<>();
                                                   combined.addAll(roomidsrejected);
                                                   fragmenttoshowsearchedelements fragment = new fragmenttoshowsearchedelements();
                                                   Bundle bundle = new Bundle();
                                                   bundle.putStringArrayList("roomList", combined);

                                                   fragment.setArguments(bundle);

                                                   getSupportFragmentManager().beginTransaction()
                                                           .replace(R.id.fragment_container, fragment)
                                                           .commit();


                                               } else {


                                                   ArrayList<String> combined = new ArrayList<>();
                                                   combined.addAll(roomidspassed);
                                                   combined.addAll(roomidspassedbutweak);
                                                   combined.addAll(roomidspassedbutweaker);
                                                   combined.addAll(roomidspassedbutweakest);

                                                   fragmenttoshowsearchedelements fragment = new fragmenttoshowsearchedelements();
                                                   Bundle bundle = new Bundle();
                                                   bundle.putStringArrayList("roomList", combined);

                                                   fragment.setArguments(bundle);

                                                   getSupportFragmentManager().beginTransaction()
                                                           .replace(R.id.fragment_container, fragment)
                                                           .commit();


                                               }


                                           }

                                       }

                                       @Override
                                       public void onCancelled(@NonNull DatabaseError error) {

                                       }
                                   });


                               }


                           }


                       }


                   }

               }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
        }



    }
   public void itemclicked3(int position, String s)
   {
       Intent i = new Intent(serachactivity.this,roominfoandbookfinal2.class);
       i.putExtra(roominfoandbookfinal2.message,s);
       startActivity(i);


   }
   public void favoriteclick(int position,String s)
    { MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);

        // Show the progress dialog immediately
        Dialog pd=new Dialog(this);
        pd.setContentView(R.layout.progress_dialog_layout);
pd.setCancelable(false);
pd.show();// Check if already in SQLite favorites
        boolean alreadyFav = dbHelper.getFavoriteRooms().contains(s);

        if (!alreadyFav) {
            // ✅ Add to SQLite only
            dbHelper.insertRoom(1, s);
            Toast.makeText(serachactivity.this, "Added to favorites", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        } else {
            // ❌ Remove from SQLite only
            dbHelper.deleteRoom(s);
            Toast.makeText(serachactivity.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }


    }
}