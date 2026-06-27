package com.example.bookandpostroom;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE;
import static java.lang.System.exit;
import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableString;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;

import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class studentinfoandrooms extends AppCompatActivity  implements fragmentforhome33.Listenerhome2,fragmentforhome33.Favoritelistener,fragmentforhome4.Listenerhome1,fragmentforhome4.Listenerhome2
,fragmentforownedrooms.Listenerhome1,fragmentforownedrooms.Listenerhome2,fragmentpaymenthistoryfortenantfinal.Listenerhomepayment,fragmentpaymenthistoryfortenantfinal.Listenerforstatus, fragmentpaymenthistoryfortenantfinal.Listenerforrefund {


    private static final String TAG_HOME = "home";
    private static final String TAG_PROFILE = "profile";
    private static final String TAG_ROOMS_BOOKED = "roomsbooked";
    private static final String TAG_PAYMENT = "payment";
    private static final String TAG_REQUEST = "request";

    // Firebase notification keys
    private static final String KEY_NOTIFICATION_REQUEST = "notificationforrequestsent";
    private static final String KEY_NOTIFICATION_RECEIPT = "notificationrecieptfortenant";
    private static final String KEY_NOTIFICATION_BOOKED = "notificationfortenantbooked";

    // Static fields
    public static final String notificationMessage = "";
    public static final String lastkey2 = "";

    // UI Components
    private BottomNavigationView BNV;

    // Fragments
    private Fragment fragmentHome;
    private Fragment fragmentprofiletenant;
    private Fragment fragmentpaymenthistoryfortenant;
    private Fragment ownedroomsfortenant;
    private Fragment requestfragment;
    private Fragment activeFragment;
    private Fragment selectedFragment = null;

    // User data
    private String name;
    private String id;

    // Google Sign-In


    // Unused legacy fields (kept for compatibility)
    private Dialog dialog;
    private MenuItem requestItem;
    private long profilecreated;
    private Fragment chigga;
    private ImageView iv;
    private String LastKey;
    private long rowcount;
    Toolbar tb;
    SearchView searchView;
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        CoordinatorLayout scrollView = findViewById(R.id.bbb);

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
        setContentView(R.layout.activity_studentinfoandrooms);
applySystemBarPadding();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();

        if (account != null) {
            name = account.getDisplayName();
            id = account.getUid();
        }
        DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("done");
        dj.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setupToolbar();
        setupGoogleSignIn();
         searchView = findViewById(R.id.search_view);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "review_channel";
            CharSequence name = "Review Notifications";
            String description = "Notifications to ask users for reviews";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

// Set hint color to red
        searchEditText.setHintTextColor(getResources().getColor(R.color.teal));
searchEditText.setFocusable(false);
searchEditText.setClickable(true);
// Optional: set the text color too

// Hints to rotate
        String[] hints = {
                "Search by district",
                "Search by institution",
                "Search by distance"
        };
searchEditText.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent i=new Intent(studentinfoandrooms.this, serachactivity.class);
        startActivity(i);
    }
});

        Handler handler = new Handler();
        final int[] hintIndex = {0};
        final int[] charIndex = {0};
        final boolean[] typing = {true}; // true = typing, false = deleting

        Runnable typeWriter = new Runnable() {
            @Override
            public void run() {
                String currentHint = hints[hintIndex[0]];

                if (typing[0]) { // Typing characters
                    if (charIndex[0] < currentHint.length()) {
                        searchView.setQueryHint(currentHint.substring(0, charIndex[0] + 1));
                        charIndex[0]++;
                        handler.postDelayed(this, 100); // typing speed
                    } else {
                        typing[0] = false;
                        handler.postDelayed(this, 1500); // wait before deleting
                    }
                } else { // Deleting characters
                    if (charIndex[0] >= 0) {
                        searchView.setQueryHint(currentHint.substring(0, charIndex[0]));
                        charIndex[0]--;
                        handler.postDelayed(this, 60); // deleting speed
                    } else {
                        typing[0] = true;
                        hintIndex[0] = (hintIndex[0] + 1) % hints.length; // next hint
                        handler.postDelayed(this, 300); // small delay before typing again
                    }
                }
            }
        };

// Start animation
        handler.post(typeWriter);

        setupBottomNavigation();
        setupFragments(savedInstanceState);
        setupBackPressHandler();
        observeNotifications();
    }

    private void setupToolbar() {
         tb = findViewById(R.id.tbmain);
        setSupportActionBar(tb);
    }
    private FirebaseAuth firebaseAuth;
    private void setupGoogleSignIn() {

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();

        if (account != null) {
            name = account.getDisplayName();
            id = account.getUid();
        }
    }

    private void setupBottomNavigation() {
        BNV = findViewById(R.id.bottom_navigation);
        BNV.setItemIconTintList(null);

        int tealColor = ContextCompat.getColor(this, R.color.white);
        ColorStateList colorStateList = ColorStateList.valueOf(tealColor);

        BNV.setOnItemSelectedListener(item -> {
            DatabaseReference dss = FirebaseDatabase.getInstance()
                    .getReference("google")
                    .child(name)
                    .child(id)
                    .child("student");

            selectedFragment = getFragmentForMenuItem(item.getItemId());

            if (selectedFragment == null) {
                return false;
            }

            // Reset notification counter for selected item
            resetNotificationForMenuItem(item.getItemId(), dss);

            // Switch fragments
            switchFragment(selectedFragment);

            return true;
        });
    }

    private void setupFragments(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            initializeFragments();
            addFragmentsToContainer();
        } else {
            restoreFragments();
        }
    }

    private void initializeFragments() {
        fragmentprofiletenant = new fragmentprofilefortenant();
        fragmentpaymenthistoryfortenant = new fragmentpaymenthistoryfortenantfinal();
        ownedroomsfortenant = new fragmentforownedrooms();
        fragmentHome = new fragmentforhome33();
        requestfragment = new fragmentforhome4();
    }

    private void addFragmentsToContainer() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, fragmentHome, TAG_HOME);
        transaction.add(R.id.fragment_container, fragmentprofiletenant, TAG_PROFILE);
        transaction.add(R.id.fragment_container, ownedroomsfortenant, TAG_ROOMS_BOOKED);
        transaction.add(R.id.fragment_container, fragmentpaymenthistoryfortenant, TAG_PAYMENT);
        transaction.add(R.id.fragment_container, requestfragment, TAG_REQUEST);

        // Hide all fragments except home
        transaction.hide(fragmentprofiletenant);
        transaction.hide(ownedroomsfortenant);
        transaction.hide(fragmentpaymenthistoryfortenant);
        transaction.hide(requestfragment);

        transaction.commit();
        activeFragment = fragmentHome;
    }

    private void restoreFragments() {
        fragmentHome = (fragmentforhome33) getSupportFragmentManager().findFragmentByTag(TAG_HOME);
        fragmentprofiletenant = (fragmentprofilefortenant) getSupportFragmentManager().findFragmentByTag(TAG_PROFILE);
        ownedroomsfortenant = (fragmentforownedrooms) getSupportFragmentManager().findFragmentByTag(TAG_ROOMS_BOOKED);
        fragmentpaymenthistoryfortenant = (fragmentpaymenthistoryfortenantfinal) getSupportFragmentManager().findFragmentByTag(TAG_PAYMENT);
        requestfragment = (fragmentforhome4) getSupportFragmentManager().findFragmentByTag(TAG_REQUEST);

        activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void setupBackPressHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        });
    }
    DatabaseReference dss;
    ValueEventListener vv;
    private void observeNotifications() {
         dss = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(name)
                .child(id)
                .child("student");
vv=new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        handleRequestNotification(snapshot);
        handleReceiptNotification(snapshot);
        handleBookedNotification(snapshot);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
};
        dss.addValueEventListener(vv);
    }
@Override
    public void onPause()
    {
        super.onPause();
        if(dss!=null && vv!=null)
        {
            dss.removeEventListener(vv);
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        if(dss!=null && vv!=null)
        {
            dss.addValueEventListener(vv);
        }
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(dss!=null && vv!=null)
        {
            dss.removeEventListener(vv);
        }
    }

    private void handleRequestNotification(DataSnapshot snapshot) {
        Long s = snapshot.child(KEY_NOTIFICATION_REQUEST).getValue(Long.class);
        if (s != null) {
            updateBadge(R.id.requested1, s, requestfragment);
        }
    }

    private void handleReceiptNotification(DataSnapshot snapshot) {
        Long k = snapshot.child(KEY_NOTIFICATION_RECEIPT).getValue(Long.class);
        if (k != null) {
            updateBadge(R.id.payment1, k, fragmentpaymenthistoryfortenant);
        }
    }

    private void handleBookedNotification(DataSnapshot snapshot) {
        Long jj = snapshot.child(KEY_NOTIFICATION_BOOKED).getValue(Long.class);
        if (jj != null) {
            updateBadge(R.id.booked1, jj, ownedroomsfortenant);
        }
    }

    private void updateBadge(int menuItemId, Long count, Fragment fragment) {
        BadgeDrawable badge = BNV.getOrCreateBadge(menuItemId);

        if (count > 0) {
            if (BNV.getSelectedItemId() == menuItemId) {
              //  refreshFragment(fragment);
            } else {
                int myInt = count.intValue();
                badge.setVisible(true);
                badge.setBackgroundColor(Color.RED);
                badge.setNumber(myInt);
              //  refreshFragment(fragment);
            }
        } else {
            badge.setVisible(false);
        }
    }

    private void refreshFragment(Fragment fragment) {
        // Only refresh if the fragment is currently visible
        // This prevents unnecessary recreation of fragment views
        if (fragment == activeFragment && fragment.isVisible()) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .detach(fragment)
                    .attach(fragment)
                    .commitAllowingStateLoss();
        }
    }

    private Fragment getFragmentForMenuItem(int menuItemId) {
        TextView tv=findViewById(R.id.tvtb);
        AppBarLayout appBar = findViewById(R.id.appbar); // give your AppBarLayout an ID in XML
        FrameLayout ff = findViewById(R.id.fragment_container);
     /*   appBar.animate()
                .translationY(appBar.getVisibility() == View.VISIBLE ? -appBar.getHeight() : 0)
                .setDuration(200)
                .withStartAction(() -> {
                    if (appBar.getVisibility() == View.GONE) appBar.setVisibility(View.VISIBLE);
                })
                .withEndAction(() -> {
                    if (appBar.getTranslationY() == -appBar.getHeight()) appBar.setVisibility(View.GONE);
                });
        if (menuItemId == R.id.home1) {
            appBar.setVisibility(View.VISIBLE);
        } else {
            appBar.setVisibility(View.GONE);
        }*/

        if (menuItemId == R.id.home1) {
            searchView.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);

            return fragmentHome;
        } else if (menuItemId == R.id.booked1) {
            searchView.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            tv.setText("Booked Rooms");
            return ownedroomsfortenant;
        } else if (menuItemId == R.id.profile1) {
            searchView.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            tv.setText("Profile");

            return fragmentprofiletenant;
        } else if (menuItemId == R.id.payment1) {
            searchView.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            tv.setText("Payment History");

            return fragmentpaymenthistoryfortenant;
        } else if (menuItemId == R.id.requested1) {
            searchView.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            tv.setText("Room Requests");

            return requestfragment;
        }
        return null;
    }

    private void resetNotificationForMenuItem(int menuItemId, DatabaseReference dss) {
        if (menuItemId == R.id.booked1) {
            dss.child(KEY_NOTIFICATION_BOOKED).setValue(0);
        } else if (menuItemId == R.id.payment1) {
            dss.child(KEY_NOTIFICATION_RECEIPT).setValue(0);
        } else if (menuItemId == R.id.requested1) {
            dss.child(KEY_NOTIFICATION_REQUEST).setValue(0);
        }
    }

    private void switchFragment(Fragment selectedFragment) {
        if (selectedFragment != null && selectedFragment != activeFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            transaction.hide(activeFragment);
            transaction.show(selectedFragment);
            transaction.commit();
            activeFragment = selectedFragment;
        }
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save any additional state if needed
    }


    View customView2;
    View customView3;

    ImageView icon;
    private TextView badge;
    MenuItem favorite;
    MenuItem setting;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
   /*     getMenuInflater().inflate(R.menu.menu4, menu);


        favorite = menu.findItem(R.id.favorite);
        setting = menu.findItem(R.id.setting);



        // Inflate the custom view

        customView2 = LayoutInflater.from(this).inflate(R.layout.menuforfavorites, null);
        customView3 = LayoutInflater.from(this).inflate(R.layout.menuforsettings, null);


        // Initially hide both text and badge



        favorite.setActionView(customView2);
        setting.setActionView(customView3);

        customView2.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
Intent i=new Intent(studentinfoandrooms.this,favorites.class);
startActivity(i);

    }
});
customView3.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent i=new Intent(studentinfoandrooms.this,settings.class);
        startActivity(i);
    }
});

*/



        return super.onCreateOptionsMenu(menu);
    }

    // Method to show badge with count (1-9)
    private void showBadge(int count) {
        icon.setVisibility(View.VISIBLE);
        badge.setVisibility(View.VISIBLE);
        badge.setText("\uD83D\uDD34");

    }

    // Method to show text count (10+)
    private void showTextCount(int count) {
        icon.setVisibility(View.GONE);
        badge.setVisibility(View.GONE);


        // Format large numbers (99+ shows as "99+")


    }

    // Method to hide all notifications indicators
    private void hideBadgeAndText() {
        icon.setVisibility(View.VISIBLE);
        badge.setVisibility(View.GONE);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {




            return super.onOptionsItemSelected(item);
    }


    @Override
   public void itemclicked3(int position,String s)
    {


        Intent i = new Intent(studentinfoandrooms.this,roominfoandbookfinal2.class);
        i.putExtra(roominfoandbookfinal2.message,s);
        startActivity(i);


    }

    @Override
    public void itemclicked4(int position,String s)
    {

        Intent i = new Intent(this,roominfoandbooklatest2.class);
        i.putExtra(roominfoandbooklatest2.message,s);
        startActivity(i);

    }
    @Override
    public void itemclicked4payment(int position,String s)
    {

        Intent i =new Intent(this,gpayActivity.class);
        i.putExtra(gpayActivity.message,s);
        startActivity(i);
//payments and all
    }

    public void itemclickedintenantpaymenthistory(int position,long transactionno,String roomid)
    {
        Intent i=new Intent(this,tenantrecieptinfo.class);
        String j=roomid+"transactionnostartshere"+transactionno;
        i.putExtra(tenantrecieptinfo.message,j);
        startActivity(i);


    }

   public void refundstatus(int position,String s){


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
                Toast.makeText(studentinfoandrooms.this, "Invalid IDs: At least one of paymentId, transferId, or orderId is required", Toast.LENGTH_LONG).show();
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
        checkRazorpayPaymentStatusHttp(cleanOrderId, cleanDate, cleanTime, cleanPaymentId, cleanTransferId, new PaymentStatusCallback() {
            @Override
            public void onSuccess(PaymentStatusResponse response) {
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
                            new AlertDialog.Builder(studentinfoandrooms.this)
                                    .setView(dialogView)   // set custom layout
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();
                        } else {
                            // Use the message from the response, or fallback message
                            String message = response.message != null && !response.message.isEmpty()
                                    ? response.message
                                    : "payment has successfully been captured not settled yet into the owner's bank account";

                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.dialog_message, null);

// Set message text
                            TextView textView = dialogView.findViewById(R.id.dialog_message);
                            textView.setText(message);
                            new AlertDialog.Builder(studentinfoandrooms.this)
                                    .setView(dialogView)   // set custom layout
                                    .setCancelable(false)
                                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                    .show();

                        }
                    } else {
                        progressdialog.dismiss();
                        Toast.makeText(studentinfoandrooms.this, "Error: " + response.error, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("Payment", "Error: " + error);
                runOnUiThread(() -> {
                    Toast.makeText(studentinfoandrooms.this, "Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void checkRazorpayPaymentStatusHttp(String orderid, String date, String time, String paymentid, String transferid, PaymentStatusCallback callback) {
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

                PaymentStatusResponse paymentResponse = new PaymentStatusResponse();
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

    // Interface for callback
    public interface PaymentStatusCallback {
        void onSuccess(PaymentStatusResponse response);
        void onError(String error);
    }

    // Callback interface remains the same
//method to call cloud function


    // Response class


    // Callback interface








    public void itemclicked2inownerrooms(int position,String s)
    {

        Intent i = new Intent(this, fulldetails.class);
        i.putExtra(fulldetails.message,s);
        startActivity(i);
    }
    Dialog progressdialog2;
    public  void bookingcancellation(int position,String s,String tenanttranno,String transactionid,String amount,String transferid,String previousDateStr,String previousTimeStr)
    {

Dialog warning=new Dialog(this);
View view=LayoutInflater.from(this).inflate(R.layout.dialogforleaveconfirmation2,null);

        warning.setContentView(view);

// Find views from the custom layout
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button okButton = view.findViewById(R.id.okButton);
        okButton.setBackgroundResource(R.drawable.backgroundshape90);
        okButton.setOnClickListener(v -> {
            // TODO: put your "delete/leave" logic here
            warning.dismiss();
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date previousDateTime = dateTimeFormat.parse(previousDateStr + " " + previousTimeStr);
                Date currentDateTime = new Date();
                long diffInDays = (currentDateTime.getTime() - previousDateTime.getTime()) / (1000 * 60 * 60 * 24);
                if(diffInDays<=2)
                {
                    long value = Long.parseLong(amount);

                    // Divide by 100 to drop last 2 digits
                    value = value / 100;

                    // Convert back to String
                    String result = String.valueOf(value);
                     progressdialog2=new Dialog(this);
                    progressdialog2.setContentView(R.layout.progress_dialog_layout);
                    progressdialog2.setCancelable(false);
                    progressdialog2.show();

                    DatabaseReference ds=FirebaseDatabase.getInstance().getReference("pendingrefunds");
                    ds.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                            Long k=currentData.getChildrenCount();
                            if(k==null)k=0L;
                            k++;
                            Map<String,Object> data=new HashMap<>();
                            data.put("amount",result);
                            data.put("paymentid",transactionid);
                            data.put("transferid",transferid);
                            data.put("tenantinfo",name+"idstarts"+id+"idends");
                            data.put("roominfo",s);
                            currentData.child("count"+id+k).setValue(data);


                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {



DatabaseReference ss=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("transactionmadeno").child("transaction"+tenanttranno);
ss.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        snapshot.child("refundrequested").getRef().setValue("yes");
        progressdialog2.dismiss();
        Toast.makeText(studentinfoandrooms.this, "refund initiated successfully", Toast.LENGTH_SHORT).show();


        Intent i=new Intent(studentinfoandrooms.this, studentinfoandrooms.class);
        startActivity(i);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
                        }
                    });

                }
                else{
progressdialog2.dismiss();
                    Toast.makeText(this, "booking cannot be cancelled for refund,as 2 days have already passed", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();

            }







        });

// Optional: make dialog background transparent if you want rounded corners to show

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning.dismiss();
            }
        });

// Show the dialog
        warning.show();

    }
    @Override
    public void favoriteclick(int position, String s) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);

        // Show the progress dialog immediately
        Dialog pd=new Dialog(this);
        pd.setContentView(R.layout.progress_dialog_layout);
        pd.setCancelable(false);
        pd.show();
        // Check if already in SQLite favorites
        boolean alreadyFav = dbHelper.getFavoriteRooms().contains(s);

        if (!alreadyFav) {
            // ✅ Add to SQLite only
            dbHelper.insertRoom(1, s);
            Toast.makeText(studentinfoandrooms.this, "Added to favorites", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        } else {
            // ❌ Remove from SQLite only
            dbHelper.deleteRoom(s);
            Toast.makeText(studentinfoandrooms.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }
    }




}