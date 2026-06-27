package com.example.bookandpostroom;

import static java.security.AccessController.getContext;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class addActivity extends AppCompatActivity implements FacilityAdapter.Removelistener,FacilityAdapter2.Removelistener2{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int REQUEST_PERMISSION = 200;
    private static final int STORAGE_PERMISSION_CODE = 1001;
    private EditText facilityInput;
    private EditText rulesInput;
    private Button addFacilityButton;
    long counterforfacilities=0;
    long counterforrules=0;
    private Button addRulesButton;
    private RecyclerView facilityList;
    private RecyclerView ruleslist;
    private FacilityAdapter facilityAdapter;
    private FacilityAdapter2 rulesAdapter;
    private List<String> facilities = new ArrayList<>();
    private List<String> rules=new ArrayList<>();
    private TextView tvSuggestion;
    private String longitude1;
    private String latitude1;
    private long l;
    private String district;
    private CardView cv;

    private EditText latitudeField;

    private EditText longitudeField;

    private Dialog progressDialog1;


    private Button manualButton;
    private Button automaticButton;


    private LocationManager locationManager;
    private boolean isManualMode = true; // Track if in manual or automatic mode

    private String name1;


    private long sss;
    private String id1;
    private long occupied = 0;
    private FirebaseAuth firebaseAuth;
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
        setContentView(R.layout.activity_add);
        applySystemBarPadding();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // Initialize UI elements
        longitudeField = findViewById(R.id.longitude);
        latitudeField = findViewById(R.id.latitude);
        manualButton = findViewById(R.id.manual);
        automaticButton = findViewById(R.id.automatic);
        tvSuggestion = findViewById(R.id.suggestion);



        facilityInput = findViewById(R.id.facility_input);
        rulesInput=findViewById(R.id.rules_input);

        addFacilityButton = findViewById(R.id.add_facility_button);
        addRulesButton=findViewById(R.id.add_rules_button);
        facilityList = findViewById(R.id.facility_list);
        ruleslist=findViewById(R.id.rules_list);

        // Set up RecyclerView
        facilityAdapter = new FacilityAdapter(facilities);
        rulesAdapter=new FacilityAdapter2(rules);
        facilityAdapter.setListener(this);
        rulesAdapter.setListener(this);
        facilityList.setLayoutManager(new LinearLayoutManager(this));
        facilityList.setAdapter(facilityAdapter);
        ruleslist.setLayoutManager(new LinearLayoutManager(this));
        ruleslist.setAdapter(rulesAdapter);

        addFacilityButton.setOnClickListener(v -> addFacility());
        addRulesButton.setOnClickListener(v->addrules());
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account != null) {
            String name = account.getDisplayName();
            name1 = name;
            id1 = account.getUid();


            DatabaseReference dbrroom = FirebaseDatabase.getInstance().getReference("google").child(name).child(id1).child("owner");

            dbrroom.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (snapshot.hasChild("roomsexplored")) {
                        sss = snapshot.child("roomsexplored").getValue(long.class);
                        sss=sss+1;


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        // Set up spinner


        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.predefined_values, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);

        // Set drawable for the suggestion TextView
        Drawable drawable = ContextCompat.getDrawable(this,R.drawable.lightbulb);
        int width = 50; // set your desired width in pixels
        int height = 50; // set your desired height in pixels
        drawable.setBounds(0, 0, width, height);
        tvSuggestion.setCompoundDrawables(drawable, null, null, null);
        tvSuggestion.setText("Enter the room coordinates yourself or let the app do it");

        // Location Manager setup
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Request location permission if not granted




    }


    private void addFacility() {
        String newFacility = facilityInput.getText().toString().trim();
        if (!newFacility.isEmpty()) {
            facilities.add(newFacility);
            facilityAdapter.notifyDataSetChanged();
            facilityInput.setText("");  // Clear input field
       counterforfacilities++;
        }
    }
    private void addrules(){
        String newFacility = rulesInput.getText().toString().trim();
        if (!newFacility.isEmpty()) {
            rules.add(newFacility);
            rulesAdapter.notifyDataSetChanged();
            rulesInput.setText("");  // Clear input field
     counterforrules++;
        }



    }
    public void delete(int position) {
        // Don't remove from list here - adapter already did it
        // Just decrement the counter
        if (counterforfacilities > 0) {
            counterforfacilities--;
        }
    }

    public void delete2(int position) {
        // Don't remove from list here - adapter already did it
        // Just decrement the counter
        if (counterforrules > 0) {
            counterforrules--;
        }
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


    public void showcoordinatesformanual(View view) {
        isManualMode = true;  // Switch to manual mode
        cv=findViewById(R.id.mapcv);
        cv.setVisibility(View.VISIBLE);
        // Show manual input fields, hide automatic button, reset input fields
        manualButton.setVisibility(View.GONE);

        automaticButton.setVisibility(View.VISIBLE);
        latitudeField.setVisibility(View.VISIBLE);
        longitudeField.setVisibility(View.VISIBLE);

        // Clear any previously filled coordinates from automatic mode
        latitudeField.setText("");
        longitudeField.setText("");

        // Stop automatic location updates

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
                    manualButton.setText("enter manually");
                    manualButton.setPadding(0,0,0,5);
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
                    Toast.makeText(addActivity.this, "error fetching the coordinates,try again", Toast.LENGTH_SHORT).show();

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


    String roomType;
    public void submit(View view) {

        EditText roomLocationField = findViewById(R.id.roomlocation);
        Spinner districtSpinner = findViewById(R.id.spinner);
EditText institution=findViewById(R.id.institution);
        EditText latitudeField = findViewById(R.id.latitude);
        EditText roomname=findViewById(R.id.roomname);
        EditText longitudeField = findViewById(R.id.longitude);



        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        EditText roompricing=findViewById(R.id.roompricing);
        EditText roomcapacity=findViewById(R.id.roomcapacity);
        TextView errorsTextView = findViewById(R.id.errors);

        RadioButton rb1 = findViewById(R.id.radioButton1);
        RadioButton rb2 = findViewById(R.id.radioButton2);


        // Check if any of the fields are empty
        if (
             counterforrules==0||counterforfacilities==0||rules.toString().isEmpty()||facilities.toString().isEmpty()||  roompricing.getText().toString().isEmpty()||
                        roomcapacity.getText().toString().isEmpty() ||
                        roomname.getText().toString().isEmpty() ||
                        roomLocationField.getText().toString().isEmpty() ||
institution.getText().toString().isEmpty()||institution.getText().toString().contains("+")||

                        latitudeField.getText().toString().isEmpty() ||
                        longitudeField.getText().toString().isEmpty() ||
                        radioGroup.getCheckedRadioButtonId() == -1 ) {
            errorsTextView.setVisibility(View.VISIBLE);
            // Set drawable for the suggestion TextView
            Drawable drawable = ContextCompat.getDrawable(this,R.drawable.error);
            int width = 50; // set your desired width in pixels
            int height = 50; // set your desired height in pixels
            drawable.setBounds(0, 0, width, height);
            errorsTextView.setCompoundDrawables(drawable, null, null, null);
            Handler h=new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    errorsTextView.setVisibility(View.GONE);
                }
            },2000);
            if(institution.getText().toString().contains("+"))
            {
                errorsTextView.setText("institution field cannot be contain symbols");
            }
            else{
            errorsTextView.setText("fields cannot be empty");}
        } else {

           if(districtSpinner.getSelectedItem() == null)
           {
              district="Anantnag";
           }
           else{
               district = districtSpinner.getSelectedItem().toString();
           }

            String roomlocation = roomLocationField.getText().toString();

String institute=institution.getText().toString();
            String latitude = latitudeField.getText().toString();
            String longitude = longitudeField.getText().toString();
            String roomprice=roompricing.getText().toString();
            String roomcap=roomcapacity.getText().toString();
            // If all fields are filled, you can proceed with the submission
            // Add your submission logic here
            progressDialog1 = new Dialog(addActivity.this);
            progressDialog1.setCancelable(false); // Disable dismissal on touch outside

            // Inflate the custom layout
            View view1 = LayoutInflater.from(addActivity.this).inflate(R.layout.progress_dialog_layout2, null);

            // Set the custom view to the dialog
            progressDialog1.setContentView(view1);

            // Show the dialog
            progressDialog1.show();




                 roomType = "";
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == rb1.getId()) {
                    // PG is selected
                    roomType = "PG";
                } else if (selectedId == rb2.getId()) {
                    // Non-PG is selected
                    roomType = "Non-PG";
                }

                Intent i = new Intent(addActivity.this, addActivity2.class);
                String kk=name1+"idstarts"+id1+"idends"+"room"+sss;
                i.putExtra(addActivity2.message,kk);
                i.putExtra(addActivity2.district1,district);
                i.putExtra(addActivity2.institution1,institution.getText().toString());
                i.putExtra(addActivity2.roomname1,roomname.getText().toString());
                i.putExtra(addActivity2.roomlocation1, roomlocation);
                i.putExtra(addActivity2.roomcap1, roomcap); // as String, since parsed as Long earlier
                i.putExtra(addActivity2.latitude1, latitude);
                i.putExtra(addActivity2.longitude1, longitude);
                i.putExtra(addActivity2.consentsforchange1, 0L);
                i.putExtra(addActivity2.bookedbygender1, "");
                i.putExtra(addActivity2.roomprice1, roomprice); // in case different from roompricing
                i.putExtra(addActivity2.roomType1, roomType);
                i.putExtra(addActivity2.rules1, rules.toString());
                i.putExtra(addActivity2.facilities1, facilities.toString());
                i.putExtra(addActivity2.roomcapacityreached1, 0);
progressDialog1.dismiss();
                startActivity(i);





        }


    }





}



