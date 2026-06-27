package com.example.bookandpostroom;

import static android.app.PendingIntent.getActivity;
import static java.lang.System.exit;
import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class Activity5 extends AppCompatActivity implements fragmentforhome.Listenerhome,fragmentforhome.Listenerhome2,fragmentforhome2.Listenerhome1,fragmentforpayment.Listenerhomepayment,fragmentforpayment.Listenerhomepayment2,Requestfragmentforowner.RequestListener{
public static final String notificationMessage="";
    private Fragment fragmentHome;
    private Fragment fragmentProfile;
private Fragment roomoccupiedfragment;
    private Fragment activeFragment;
    private Fragment paymentfragment;
    private Fragment requestfragment;
    private Dialog dialog;
    private Dialog dialog2;
    private long roomsposted;
    ValueEventListener v1;
    ValueEventListener v2;
private long index=0;
private long bruhh;
private String district;
private String institution;
    private boolean doubleBackToExitPressedOnce = false;
    private OnBackPressedCallback onBackPressedCallback;
    private long x;
private String key1;
private boolean found1=false;
private boolean found2=false;

private String value1;
    private String key2;
    private String value2;

    private long transactionsrecieved;
    ImageView iv;
    private MenuItem requestItem;
    Fragment selectedFragment;
    private long countroom;
    private String name2;
    private String id9;
    private long bountroom;
    private long roomsoccupied;
    private long requestcount;
    private int id=0;
    private long profilecreated;
    private String ownername,ownerid;
    DatabaseReference ownerRef1;
    DatabaseReference ownerRef2;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onResume() {
        super.onResume();

        if (ownerRef1 != null) {

            if (v1 != null)
                ownerRef1.addValueEventListener(v1);

            if (v2 != null)
                ownerRef2.addValueEventListener(v2);

        }
        if(deleted!=null && ds!=null)
        {
            deleted.addValueEventListener(ds);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (ownerRef1 != null && ownerRef2!=null) {

            if (v1 != null)
                ownerRef1.removeEventListener(v1);

            if (v2 != null)
                ownerRef2.removeEventListener(v2);

        }
        if(deleted!=null && ds!=null)
        {
            deleted.removeEventListener(ds);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ownerRef1 != null && ownerRef2!=null) {

            if (v1 != null)
                ownerRef1.removeEventListener(v1);

            if (v2 != null)
                ownerRef2.removeEventListener(v2);

        }
        if(deleted!=null && ds!=null)
        {
            deleted.removeEventListener(ds);
        }
    }

    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        CoordinatorLayout scrollView = findViewById(R.id.main);

        // Apply padding to avoid system bars
        scrollView.setPadding(
                0,
                statusBarHeight,  // Top padding for status bar
                0,
                0      // Bottom padding for navigation bar
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
        setContentView(R.layout.activity_5);
applySystemBarPadding();
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        // Initialize toolbar
        Toolbar tb = findViewById(R.id.tbmain);
        setSupportActionBar(tb);

        // Save last activity
        ActivityCacheHelper dbHelper = new ActivityCacheHelper(this);
        dbHelper.saveLastActivity(Activity5.this.getClass().getName());


        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account == null) {
            // Handle non-signed-in state
            return;
        }

        // Extract account info
        ownername = account.getDisplayName();
        ownerid = account.getUid();
        name2 = ownername;

        // Initialize database reference
         ownerRef1 = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(ownername)
                .child(ownerid)
                .child("owner");
        ownerRef2 = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(ownername)
                .child(ownerid)
                .child("owner");

        // Update last activity in Firebase


        // Check account setup and room count
        checkAccountSetupAndRoomCount();

        // Initialize Bottom Navigation
        BottomNavigationView BNV = findViewById(R.id.bottom_navigation);
        BNV.setItemIconTintList(null);

        // Initialize fragments
        initializeFragments(savedInstanceState);

        // Setup notification badges and listeners
        setupNotificationListeners(BNV);

        // Setup bottom navigation listener
        setupBottomNavigation(BNV);
        setupBackPressHandler();

    }

    private void checkAccountSetupAndRoomCount() {
        v1=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long roomsPostedLong = snapshot.child("roomsexplored").getValue(Long.class);
                Long roomsss = snapshot.child("roomsposted").getValue(Long.class);

                if(!snapshot.child("roomsexplored").exists()|| roomsPostedLong==0)
                {
                    Intent i=new Intent(Activity5.this, Activity4.class);
                    startActivity(i);
                    finishAffinity();

                }
               else if(snapshot.child("roomsexplored").exists() && roomsPostedLong>0 && roomsss==0)
                {
                    Intent i=new Intent(Activity5.this, addActivity.class);
                    startActivity(i);
                    finishAffinity();
                }
                if (roomsPostedLong != null) {
                    countroom = roomsPostedLong;
                    bountroom = roomsPostedLong;
                    roomsposted = roomsPostedLong;
                    countroom--;
                    roomsposted--;
                }

                // Check account setup
                Long accountSetupLong = snapshot.child("accountsetup").getValue(Long.class);
                if (accountSetupLong != null && accountSetupLong == 0) {
                    showAccountSetupDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        ownerRef1.addValueEventListener(v1);

    }

    private void showAccountSetupDialog() {
        if (!isFinishing() && !isDestroyed()) {
            new AlertDialog.Builder(Activity5.this)
                    .setMessage("In order to receive payments and requests from the tenant/students, you need to set up your account")
                    .setCancelable(false)
                    .setPositiveButton("proceed", (dialog, which) -> {
                        Intent i = new Intent(Activity5.this, AccountsetupActivity.class);
                        startActivity(i);
                        finish();
                    })
                    .create()
                    .show();
        }


    }

    private void initializeFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            // Initialize fragments for first time
            fragmentHome = new fragmentforhome();
            paymentfragment = new fragmentforpayment();
            roomoccupiedfragment = new fragmentforhome2();
            fragmentProfile = new ProfileFragment();
            requestfragment = new Requestfragmentforowner();

            // Add all fragments but hide them initially
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragmentHome, "home");
            transaction.add(R.id.fragment_container, fragmentProfile, "profile");
            transaction.add(R.id.fragment_container, roomoccupiedfragment, "roomsbooked");
            transaction.add(R.id.fragment_container, paymentfragment, "payment");
            transaction.add(R.id.fragment_container, requestfragment, "request");

            // Hide all fragments except home
            transaction.hide(fragmentProfile);
            transaction.hide(roomoccupiedfragment);
            transaction.hide(paymentfragment);
            transaction.hide(requestfragment);

            transaction.commit();
            activeFragment = fragmentHome;
        } else {
            // Restore fragments from FragmentManager
            fragmentHome = (fragmentforhome) getSupportFragmentManager().findFragmentByTag("home");
            fragmentProfile = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("profile");
            roomoccupiedfragment = (fragmentforhome2) getSupportFragmentManager().findFragmentByTag("roomsbooked");
            paymentfragment = (fragmentforpayment) getSupportFragmentManager().findFragmentByTag("payment");
            requestfragment = (Requestfragmentforowner) getSupportFragmentManager().findFragmentByTag("request");

            // Find which fragment is currently active
            activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }
    }

    private void setupNotificationListeners( BottomNavigationView BNV) {
       v2=new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               handleRequestNotifications(snapshot, BNV);

               // Handle payment notifications
               handlePaymentNotifications(snapshot, BNV);

               // Handle booking notifications
               handleBookingNotifications(snapshot, BNV);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       };
        ownerRef2.addValueEventListener(v2);
    }

    private void handleRequestNotifications(DataSnapshot snapshot, BottomNavigationView BNV) {
        Long notificationCount = snapshot.child("notificationforrequestsrecieved").getValue(Long.class);

        if (notificationCount == null) return;

        BadgeDrawable badge = BNV.getOrCreateBadge(R.id.request1);

        if (notificationCount > 0) {
            refreshFragment(requestfragment);

            if (BNV.getSelectedItemId() != R.id.request1) {
                badge.setVisible(true);
                badge.setBackgroundColor(Color.RED);
                badge.setNumber(notificationCount.intValue());
            }
        } else {
            badge.setVisible(false);
        }
    }

    private void handlePaymentNotifications(DataSnapshot snapshot, BottomNavigationView BNV) {
        Long notificationCount = snapshot.child("notificationrecieptforowner").getValue(Long.class);

        if (notificationCount == null) return;

        BadgeDrawable badge = BNV.getOrCreateBadge(R.id.payment1);

        if (notificationCount > 0) {
            refreshFragment(paymentfragment);

            if (BNV.getSelectedItemId() != R.id.payment1) {
                badge.setVisible(true);
                badge.setBackgroundColor(Color.RED);
                badge.setNumber(notificationCount.intValue());
            }
        } else {
            badge.setVisible(false);
        }
    }

    private void handleBookingNotifications(DataSnapshot snapshot, BottomNavigationView BNV) {
        Long notificationCount = snapshot.child("notificationforownerbooked").getValue(Long.class);

        if (notificationCount == null) return;

        BadgeDrawable badge = BNV.getOrCreateBadge(R.id.booked1);

        if (notificationCount > 0) {
            refreshFragment(roomoccupiedfragment);

            if (BNV.getSelectedItemId() != R.id.booked1) {
                badge.setVisible(true);
                badge.setBackgroundColor(Color.RED);
                badge.setNumber(notificationCount.intValue());
            }
        } else {
            badge.setVisible(false);
        }
    }

    private void refreshFragment(Fragment fragment) {
        if (fragment != null && fragment.isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .detach(fragment)
                    .attach(fragment)
                    .commitAllowingStateLoss();
        }
    }

    private void setupBottomNavigation(BottomNavigationView BNV) {
        BNV.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.home1) {
                selectedFragment = fragmentHome;
            } else if (itemId == R.id.profile1) {
                selectedFragment = fragmentProfile;
            } else if (itemId == R.id.booked1) {
                ownerRef1.child("notificationforownerbooked").setValue(0);
                selectedFragment = roomoccupiedfragment;
            } else if (itemId == R.id.request1) {
                ownerRef1.child("notificationforrequestsrecieved").setValue(0);
                selectedFragment = requestfragment;
            } else if (itemId == R.id.payment1) {
                ownerRef1.child("notificationrecieptforowner").setValue(0);
                selectedFragment = paymentfragment;
            }

            if (selectedFragment != null && selectedFragment != activeFragment) {
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .hide(activeFragment)
                        .show(selectedFragment)
                        .commit();

                activeFragment = selectedFragment;
            }

            return selectedFragment != null;
        });
    }
    private void setupBackPressHandler() {
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (doubleBackToExitPressedOnce) {
                    // Exit the app
                    finishAffinity();
                    System.exit(0);
                    return;
                }

                doubleBackToExitPressedOnce = true;
                Toast.makeText(Activity5.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    doubleBackToExitPressedOnce = false;
                }, 2000);
            }
        };

        getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    MenuItem refresh;
    View customView1;
    View customView2;


    ImageView icon;

    MenuItem add;
    MenuItem logout;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        refresh=menu.findItem(R.id.refresh);
        add=menu.findItem(R.id.add);



        customView1 = LayoutInflater.from(this).inflate(R.layout.menucutforrefresh, null);
        customView2 = LayoutInflater.from(this).inflate(R.layout.menucutforadd, null);


        refresh.setActionView(customView1);
        add.setActionView(customView2);

        // Use your own icon
customView1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        customView1.setVisibility(View.GONE);
        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                customView1.setVisibility(View.VISIBLE);
            }
        },2000);
        if (activeFragment instanceof RefreshListener) {
            ((RefreshListener) activeFragment).onRefresh();


        } else {
            Toast.makeText(Activity5.this, "No active fragment to refresh!", Toast.LENGTH_SHORT).show();
        }

    }
});
customView2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        customView2.setVisibility(View.GONE);
        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                customView2.setVisibility(View.VISIBLE);
            }
        },2000);
        Intent i=new Intent(Activity5.this,addActivity.class);
        startActivity(i);

    }
});





        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



            return super.onOptionsItemSelected(item);
    }
    // Implementation of the interface method
    @Override
    public void itemclicked(int position,String j) {
        // Handle item click events from the fragmentforhome
        // You can perform actions based on the item position
        Intent i = new Intent(this, Activity6.class);

        i.putExtra(Activity6.message, j);

        startActivity(i);
    }
    @Override
    public void itemclicked2(int position,String m,String tenantinfo) {

       Intent i = new Intent(this, roominfoandbookfinal33.class);
i.putExtra(roominfoandbookfinal33.tenantinformation,tenantinfo);
        i.putExtra(roominfoandbookfinal33.message, m.split(":")[1]);
        startActivity(i);
    }
    public void functionforrequest(int id,String s)
    {
        Intent i=new Intent(this,requestsActivityforowner.class);
        i.putExtra(requestsActivityforowner.message,s);
        startActivity(i);

    }

    Dialog progressdialog;
    // Helper method to call cloud function
    public void statuschecking(int position, long s, String k, String orderid, String date, String time, String paymentid, String transferid) {
        Log.d("Payment", "Starting status check for payment: [" + paymentid + "], transfer: [" + transferid + "], order: [" + orderid + "], date: [" + date + "]");

        progressdialog=new Dialog(this);

        progressdialog=new Dialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setTitle("checking details please standby");
        View view = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressdialog.setContentView(view);

        // Show the dialog
        progressdialog.show();
        // Validate at least one ID
        if ((paymentid == null || paymentid.trim().isEmpty()) && (transferid == null || transferid.trim().isEmpty()) && (orderid == null || orderid.trim().isEmpty())) {
            Log.e("Payment", "Invalid IDs: All empty");
            runOnUiThread(() -> {
                dialog.dismiss();
                Toast.makeText(Activity5.this, "Invalid IDs: At least one of paymentId, transferId, or orderId is required", Toast.LENGTH_LONG).show();
            });
            return;
        }

        String cleanPaymentId = paymentid != null ? paymentid.trim() : "";
        String cleanTransferId = transferid != null ? transferid.trim() : "";
        String cleanOrderId = orderid != null ? orderid.trim() : "";
        String cleanDate = date != null ? date.trim() : "";
        String cleanTime = time != null ? time.trim() : "";

        Log.d("Payment", "Checking status for clean IDs - Payment: [" + cleanPaymentId + "], Transfer: [" + cleanTransferId + "], Order: [" + cleanOrderId + "], Date: [" + cleanDate + "]");

        // Call the HTTP function with all relevant params
        checkRazorpayPaymentStatusHttp(cleanOrderId, cleanDate, cleanTime, cleanPaymentId, cleanTransferId, new studentinfoandrooms.PaymentStatusCallback() {
            @Override
            public void onSuccess(studentinfoandrooms.PaymentStatusResponse response) {
                Log.d("Payment", "Success: " + response.toString());
                runOnUiThread(() -> {
                    if (response.success) {
                        progressdialog.dismiss();

                        // Check if match was found first
                        if (response.matchFound && response.settlementId != null && !response.settlementId.isEmpty()) {
                            String message = "Payment settled!\nSettlement ID: " + response.settlementId;
                            if (response.settlementUtr != null && !response.settlementUtr.isEmpty()) {
                                message += "\n UTR: " + response.settlementUtr+"\n";
                            }
                            if (response.settledAt != null && !response.settledAt.isEmpty()) {
                                message += "\nSettled At: " + response.settledAt;
                            }
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_message, null);

// Set message text
                            TextView textView = dialogView.findViewById(R.id.dialog_message);
                            textView.setText(message);
                            new AlertDialog.Builder(Activity5.this)
                                    .setView(dialogView)   // set custom layout
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();
                        } else {
                            // Use the message from the response, or fallback message
                            String message = response.message != null && !response.message.isEmpty()
                                    ? response.message
                                    : "payment has successfully been captured but not settled yet into the owner's bank account";

                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_message, null);

// Set message text
                            TextView textView = dialogView.findViewById(R.id.dialog_message);
                            textView.setText(message);
                            new AlertDialog.Builder(Activity5.this)
                                    .setView(dialogView)   // set custom layout
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();

                        }
                    } else {
                        progressdialog.dismiss();
                        Toast.makeText(Activity5.this, "Error: " + response.error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("Payment", "Error: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(Activity5.this, "Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void checkRazorpayPaymentStatusHttp(String orderid, String date, String time, String paymentid, String transferid, studentinfoandrooms.PaymentStatusCallback callback) {
        new Thread(() -> {
            try {
                String functionUrl = "https://us-central1-bookandpostroom-ecf7e.cloudfunctions.net/checkRazorpayPaymentStatus";

                // Create JSON payload with all fields
                JSONObject jsonPayload = new JSONObject();
                jsonPayload.put("orderid", orderid);
                jsonPayload.put("transactionDate", date);
                jsonPayload.put("transactionTime", time);
                jsonPayload.put("paymentid", paymentid);
                jsonPayload.put("transferid", transferid);

                Log.d("Payment", "Sending request to: " + functionUrl);
                Log.d("Payment", "Payload: " + jsonPayload.toString());

                // Create HTTP connection
                URL url = new URL(functionUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.setDoOutput(true);
                connection.setConnectTimeout(30000); // 30 seconds
                connection.setReadTimeout(30000);    // 30 seconds

                // Send request
                OutputStream os = connection.getOutputStream();
                os.write(jsonPayload.toString().getBytes("UTF-8"));
                os.close();

                // Get response
                int responseCode = connection.getResponseCode();
                Log.d("Payment", "Response code: " + responseCode);

                InputStream inputStream;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                } else {
                    inputStream = connection.getErrorStream();
                }

                // Read response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                connection.disconnect();

                Log.d("Payment", "Raw response: " + response.toString());

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());

                studentinfoandrooms.PaymentStatusResponse paymentResponse = new studentinfoandrooms.PaymentStatusResponse();
                paymentResponse.success = jsonResponse.optBoolean("success", false);
                paymentResponse.matchFound = jsonResponse.optBoolean("match_found", false);
                paymentResponse.settlementId = jsonResponse.optString("settlement_id", null);
                paymentResponse.settlementUtr = jsonResponse.optString("settlement_utr", null);
                paymentResponse.settledAt = jsonResponse.optString("settled_at", null);
                paymentResponse.isSettled = jsonResponse.optBoolean("is_settled", false);
                paymentResponse.error = jsonResponse.optString("error", "");
                paymentResponse.message = jsonResponse.optString("message", "");

                // Log the settlement information specifically
                Log.d("Payment", "Match Found: " + paymentResponse.matchFound);
                Log.d("Payment", "Settlement ID: " + paymentResponse.settlementId);
                Log.d("Payment", "Settlement UTR: " + paymentResponse.settlementUtr);
                Log.d("Payment", "Settled At: " + paymentResponse.settledAt);
                Log.d("Payment", "Is Settled: " + paymentResponse.isSettled);
                Log.d("Payment", "Message: " + paymentResponse.message);

                callback.onSuccess(paymentResponse);

            } catch (Exception e) {
                Log.e("Payment", "HTTP request failed", e);
                callback.onError("Network error: " + e.getMessage());
            }
        }).start();
    }

    // Updated Response class with new fields
    public static class PaymentStatusResponse {
        public boolean success = false;
        public boolean matchFound = false;  // New field to indicate if match was found
        public String settlementId = null;
        public String settlementUtr = null;
        public String settledAt = null;
        public boolean isSettled = false;
        public String error = "";
        public String message = "";  // New field for custom messages

        @Override
        public String toString() {
            return "PaymentStatusResponse{" +
                    "success=" + success +
                    ", matchFound=" + matchFound +
                    ", settlementId='" + settlementId + '\'' +
                    ", settlementUtr='" + settlementUtr + '\'' +
                    ", settledAt='" + settledAt + '\'' +
                    ", isSettled=" + isSettled +
                    ", error='" + error + '\'' +
                    ", message='" + message + '\'' +
                    '}';
        }
    }
   /* @Override
    public void itemdeleted(int position,String j)
    { String[] parts = j.split("room");
        String number = parts[1];

        Toast.makeText(this, "hello"+number, Toast.LENGTH_SHORT).show();
        dialog2=new Dialog(this);
        dialog2.setContentView(R.layout.dialogfordeleteconfirmation);
        dialog2.setCancelable(false);
        
        Button cancel=dialog2.findViewById(R.id.cancelButton);
        Button ok=dialog2.findViewById(R.id.okButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog2.dismiss();
            }
        });

String ownername=j.split("idstarts")[0];
String tenantname=j.split("idstarts")[1].split("idends")[0];

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                Dialog ss=new Dialog(Activity5.this);
ss.setContentView(R.layout.progress_dialog_layout);
ss.setCancelable(false);
ss.show();
DatabaseReference ds=FirebaseDatabase.getInstance().getReference("deleteroomfromowner");
ds.runTransaction(new Transaction.Handler() {
    @NonNull
    @Override
    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

       Long l=currentData.getChildrenCount();
       if(l==null)l=0L;
       l++;
       Map<String,Object> data=new HashMap<>();
       data.put("roomid",j);
       currentData.child("request"+l).setValue(data);

        return Transaction.success(currentData);
    }

    @Override
    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

        Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                ss.dismiss();
                Intent i=new Intent(Activity5.this,Activity5.class);
                startActivity(i);
            }
        },4000);

    }
});






            }
        });


        dialog2.show();

    }
*/
   // OWNER SIDE - Delete room with absolute priority
   @Override
   public void itemdeleted(int position, String j) {
       String[] parts = j.split("room");
       String number = parts[1];



       dialog2 = new Dialog(this);
       dialog2.setContentView(R.layout.dialogfordeleteconfirmation);
       dialog2.setCancelable(false);

       Button cancel = dialog2.findViewById(R.id.cancelButton);
       Button ok = dialog2.findViewById(R.id.okButton);

       cancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog2.dismiss();
           }
       });

       String ownername = j.split("idstarts")[0];
       String tenantname = j.split("idstarts")[1].split("idends")[0];

       ok.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               dialog2.dismiss();

               Dialog progressDialog = new Dialog(Activity5.this);
               progressDialog.setContentView(R.layout.progress_dialog_layout);
               progressDialog.setCancelable(false);
               progressDialog.show();
               SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
               sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST timezone
               String currentTime = sdf.format(new Date());
               String ownerQueueId = ownername + "+" + getOwnerIdFromString(j)+"timestamp"+currentTime;


               DatabaseReference dref = FirebaseDatabase.getInstance()
                       .getReference("google")
                       .child(ownername)
                       .child(getOwnerIdFromString(j))
                       .child("owner")
                       .child("rooms")
                       .child("roomId:" + j).child("paymentbeingmade");
               dref.runTransaction(new Transaction.Handler() {
                   @NonNull
                   @Override
                   public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                       if(currentData.hasChildren())
                       {
                           return Transaction.abort();
                       }
                       return Transaction.success(currentData);
                   }

                   @Override
                   public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
if(!committed)
{progressDialog.dismiss();

    Toast.makeText(Activity5.this, "the room cannot be deleted right now,please comeback later", Toast.LENGTH_SHORT).show();

}
else{
    DatabaseReference queueRef = FirebaseDatabase.getInstance()
            .getReference("google")
            .child(ownername)
            .child(getOwnerIdFromString(j))
            .child("owner")
            .child("rooms")
            .child("roomId:" + j)
            .child("priorityqueue");

    // STEP 1: Owner ALWAYS adds to queue (has absolute priority)
    queueRef.runTransaction(new Transaction.Handler() {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
            String existingQueue = currentData.getValue(String.class);

            // Owner always gets added to queue, no checks needed
            if (existingQueue == null || existingQueue.isEmpty()) {
                currentData.setValue(ownerQueueId);
            } else {
                // Avoid duplicate owner entries
                if (!existingQueue.contains(ownerQueueId)) {
                    currentData.setValue(existingQueue + "," + ownerQueueId);
                } else {
                    // Owner already in queue, keep as is
                    currentData.setValue(existingQueue);
                }
            }

            return Transaction.success(currentData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError error, boolean committed,
                               @Nullable DataSnapshot currentData) {
            if (error != null || !committed) {

                Toast.makeText(Activity5.this,
                        "Error acquiring lock: " + (error != null ? error.getMessage() : "Unknown error"),
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // STEP 2: Owner is now in queue with priority
            // Small delay to ensure any tenant transactions see the owner in queue
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    proceedWithRoomDeletion(progressDialog, queueRef, ownerQueueId, j);
                }
            }, 100); // 100ms delay to ensure queue propagation
        }
    });
}
                   }
               });

           }
       });

       dialog2.show();
   }
ValueEventListener ds;
   DatabaseReference deleted;
    // Proceed with deleting the room
    private void proceedWithRoomDeletion(Dialog progressDialog, DatabaseReference queueRef,
                                         String ownerQueueId, String roomId) {
        // STEP 3: Create delete room request
        DatabaseReference deleteRoomRef = FirebaseDatabase.getInstance()
                .getReference("deleteroomfromowner");

    deleted=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner").child("roomdeleted");
    ds=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if(snapshot.exists() && snapshot.getValue(String.class).equals("yes"))
            {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        snapshot.getRef().removeValue();
                        if(progressDialog!=null)
                        {  progressDialog.dismiss();}
                        Intent i = new Intent(Activity5.this, Activity5.class);
                        startActivity(i);
                    }
                }, 1500);

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    };
    deleted.addValueEventListener(ds);
        deleteRoomRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long l = currentData.getChildrenCount();
                if (l == null) l = 0L;
                l++;

                Map<String, Object> data = new HashMap<>();
                data.put("roomid", roomId);
                data.put("status", "processing");
                data.put("timestamp", System.currentTimeMillis());

                currentData.child("request" +id+l).setValue(data);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                // STEP 4: Remove owner from queue after completion
                removeFromQueue(queueRef, ownerQueueId, new Runnable() {
                    @Override
                    public void run() {


                        if (committed && error == null) {
                            progressDialog.dismiss();
                            Toast.makeText(Activity5.this,
                                    "Room deletion request submitted successfully",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(Activity5.this,
                                    "Error deleting room: " +
                                            (error != null ? error.getMessage() : "Unknown"),
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });
    }

    // Helper method to extract owner ID from the composite string
    private String getOwnerIdFromString(String j) {
        // Parse the owner ID from your string format
        // Adjust this based on your actual string structure
        try {
            String[] parts = j.split("idstarts");
            if (parts.length > 1) {
                String[] idParts = parts[1].split("idends");
                if (idParts.length > 1) {
                    // If there's more parsing needed, adjust here
                    return idParts[0];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ""; // Return empty string if parsing fails
    }

    // Helper method to remove entry from queue with callback
    private void removeFromQueue(DatabaseReference queueRef, String queueId, Runnable onComplete) {
        queueRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String queue = currentData.getValue(String.class);
                if (queue != null && !queue.isEmpty()) {
                    // Remove the owner entry from queue
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
                // Queue cleanup complete, execute callback
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        });
    }

   public void itemclicked2inpayment(int position, long s, String k)
    {

        Intent i=new Intent(this,ownerrecieptinfo.class);
        String j=k+"transactionnostartshere"+s;
        i.putExtra(ownerrecieptinfo.message,j);
        startActivity(i);


    }




}
