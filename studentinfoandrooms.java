package com.example.bookandpostroom;

import static com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE;
import static java.lang.System.exit;
import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class studentinfoandrooms extends AppCompatActivity  implements fragmentforhome33.Listenerhome2,fragmentforhome33.Favoritelistener,fragmentforhome4.Listenerhome1,fragmentforhome4.Listenerhome2,fragmentaftersearchbuttonhasbeenclicked.Listenerhome2searchbasedbuttonclicked
,fragmentforownedrooms.Listenerhome1,fragmentforownedrooms.Listenerhome2,fragmentpaymenthistoryfortenantfinal.Listenerhomepayment,fragmentpaymenthistoryfortenantfinal.Listenerforstatus, fragmentpaymenthistoryfortenantfinal.Listenerforrefund {
    public static final String notificationMessage="";
    public static  final String lastkey2="";
    private Dialog dialog;
    private Fragment fragmentHome;
    private Fragment fragmentprofiletenant;
    private Fragment fragmentpaymenthistoryfortenant;
    private Fragment  ownedroomsfortenant;
    private Fragment activeFragment;
    private MenuItem requestItem;
    private Fragment requestfragment;
    private long profilecreated;
    private String name;
    private Fragment chigga;

private String id;
  private  Fragment selectedFragment = null;
private ImageView iv;
String LastKey;
private long rowcount;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_studentinfoandrooms);
        Toolbar tb = findViewById(R.id.tbmain);


        setSupportActionBar(tb);

        BottomNavigationView BNV = findViewById(R.id.bottom_navigation);
        BNV.setItemIconTintList(null);
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);


        int tealColor = ContextCompat.getColor(this, R.color.white);

// Create a ColorStateList for the text color
        ColorStateList colorStateList = ColorStateList.valueOf(tealColor);

// Set the item text color to teal

        name = account.getDisplayName();
        id = account.getId();


       /* StorageReference dbrf = FirebaseStorage.getInstance().getReference("tenantPhotos").child(name).child(id).child("pfp");
        dbrf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                if (uri != null) {
                    Glide.with(studentinfoandrooms.this)
                            .load(uri)
                            .into(iv);
                } else {
                    Toast.makeText(studentinfoandrooms.this, "the uri is null", Toast.LENGTH_SHORT).show();

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //  Toast.makeText(studentinfoandrooms.this, "profile not created", Toast.LENGTH_SHORT).show();
            }
        });*/


        fragmentprofiletenant = new fragmentprofilefortenant();
        fragmentpaymenthistoryfortenant = new fragmentpaymenthistoryfortenantfinal();
        ownedroomsfortenant = new fragmentforownedrooms();
        fragmentHome = new fragmentforhome33();
        requestfragment = new fragmentforhome4();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Your back press logic here
                // Example: finish activi
                finishAffinity();
            }
        });
        if (savedInstanceState == null) {

            fragmentprofiletenant = new fragmentprofilefortenant();
            fragmentpaymenthistoryfortenant = new fragmentpaymenthistoryfortenantfinal();
            ownedroomsfortenant = new fragmentforownedrooms();
            fragmentHome = new fragmentforhome33();
            requestfragment = new fragmentforhome4();
            // Add all fragments but hide them initially
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragment_container, fragmentHome, "home");
            transaction.add(R.id.fragment_container, fragmentprofiletenant, "profile");
            transaction.add(R.id.fragment_container, ownedroomsfortenant, "roomsbooked");
            transaction.add(R.id.fragment_container, fragmentpaymenthistoryfortenant, "payment");
            transaction.add(R.id.fragment_container, requestfragment, "request");

            // Hide all fragments except home
            transaction.hide(fragmentprofiletenant);
            transaction.hide(ownedroomsfortenant);
            transaction.hide(fragmentpaymenthistoryfortenant);
            transaction.hide(requestfragment);

            transaction.commit();
            activeFragment = fragmentHome;
        } else {
            // Restore fragments from FragmentManager
            fragmentHome = (fragmentforhome33) getSupportFragmentManager().findFragmentByTag("home");
            fragmentprofiletenant = (fragmentprofilefortenant) getSupportFragmentManager().findFragmentByTag("profile");
            ownedroomsfortenant = (fragmentforownedrooms) getSupportFragmentManager().findFragmentByTag("roomsbooked");
            fragmentpaymenthistoryfortenant = (fragmentpaymenthistoryfortenantfinal) getSupportFragmentManager().findFragmentByTag("payment");
            requestfragment = (fragmentforhome4) getSupportFragmentManager().findFragmentByTag("request");

            // Find which fragment is currently active
            activeFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        }



        DatabaseReference dss=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
        dss.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long s=snapshot.child("notificationforrequestsent").getValue(Long.class);

                if(s!=null)
                {

                    int menuItemId = R.id.requested1;  // Replace with your menu item ID
                    BadgeDrawable badge = BNV.getOrCreateBadge(menuItemId);
                    if(s>0)
                    {
                        if(BNV.getSelectedItemId()==R.id.requested1) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(requestfragment)
                                    .attach(requestfragment)
                                    .commitAllowingStateLoss();



                        }
                        else{int myInt =  s.intValue();
                            badge.setVisible(true);
                            badge.setBackgroundColor(Color.RED);   // Red color
                            badge.setNumber(myInt);
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(requestfragment)
                                    .attach(requestfragment)
                                    .commitAllowingStateLoss();



                        }
                    }
                    else{
                        badge.setVisible(false);
                    }
                }



                Long k=snapshot.child("notificationrecieptfortenant").getValue(Long.class);
                if(k!=null )
                {
                    int menuItemId = R.id.payment1;
                    // Replace with your menu item ID
                    BadgeDrawable badge = BNV.getOrCreateBadge(menuItemId);
                    if(k>0)
                    {
                        if(BNV.getSelectedItemId()==R.id.payment1)
                        {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(fragmentpaymenthistoryfortenant)
                                    .attach(fragmentpaymenthistoryfortenant)
                                    .commitAllowingStateLoss();


                        }
                        else {int myInt =  k.intValue();
                            badge.setVisible(true);
                            badge.setBackgroundColor(Color.RED);   // Red color
                            badge.setNumber(myInt);
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(fragmentpaymenthistoryfortenant)
                                    .attach(fragmentpaymenthistoryfortenant)
                                    .commitAllowingStateLoss();

                        }
                    }
                    else{
                        badge.setVisible(false);
                    }
                }
                Long jj=snapshot.child("notificationfortenantbooked").getValue(Long.class);

                if(jj!=null )
                {
                    int menuItemId = R.id.booked1;
                    // Replace with your menu item ID
                    BadgeDrawable badge = BNV.getOrCreateBadge(menuItemId);
                    if(jj>0)
                    {

                        if(BNV.getSelectedItemId()==R.id.booked1)
                        {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(ownedroomsfortenant)
                                    .attach(ownedroomsfortenant)
                                    .commitAllowingStateLoss();


                        }
                        else {int myInt3 =  jj.intValue();
                            badge.setVisible(true);
                            badge.setBackgroundColor(Color.RED);   // Red color
                            badge.setNumber(myInt3);
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(ownedroomsfortenant)
                                    .attach(ownedroomsfortenant)
                                    .commitAllowingStateLoss();

                        }
                    }
                    else{
                        badge.setVisible(false);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {


            }
        });
        BNV.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DatabaseReference dss=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");

                selectedFragment = null;

                if (item.getItemId() == R.id.home1) {


                    selectedFragment = fragmentHome;
                } else if (item.getItemId() == R.id.booked1) {
                    dss.child("notificationfortenantbooked").setValue(0);
                    selectedFragment = ownedroomsfortenant;
                } else if (item.getItemId() == R.id.profile1) {

                    selectedFragment = fragmentprofiletenant;
                } else if (item.getItemId() == R.id.payment1) {
                    dss.child("notificationrecieptfortenant").setValue(0);
                    selectedFragment = fragmentpaymenthistoryfortenant;
                } else if (item.getItemId()==R.id.requested1) {
                    dss.child("notificationforrequestsent").setValue(0);
                    selectedFragment=requestfragment;

                } else {
                    return false; // Return false if no valid item is selected
                }

                // Show the selected fragment and hide the active one
                if (selectedFragment != null && selectedFragment != activeFragment) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// Fade in the new fragment and fade out the old one
                    transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

                    transaction.hide(activeFragment);
                    transaction.show(selectedFragment);
                    transaction.commit();
                    activeFragment = selectedFragment;
                }

                return true;
            }
        });
        View rootView = findViewById(android.R.id.content);




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
        getMenuInflater().inflate(R.menu.menu4, menu);


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
    private void signOut() {
        mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dialog.dismiss();
                Toast.makeText(studentinfoandrooms.this, "successfully logged out", Toast.LENGTH_SHORT).show();
                // On successful sign out, update the UI or navigate to another activity
                Intent intent = new Intent(studentinfoandrooms.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(studentinfoandrooms.this, "sorry ,an error occured while sigining out", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
   public void itemclicked3(int position,String s)
    {


        Intent i = new Intent(studentinfoandrooms.this,roominfoandbookfinal2.class);
        i.putExtra(roominfoandbookfinal2.message,s);
        startActivity(i);


    }
    @Override
    public void itemclicked3searchbasedbuttonclicked(int position, String s)
    { Intent i = new Intent(studentinfoandrooms.this,roominfoandbooklatest.class);
        i.putExtra(roominfoandbooklatest.message,s);
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
                                    : "payment has been successfully captured but  not settled yet into the  owner's bankaccount";

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






    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    public void itemclicked2inownerrooms(int position,String s)
    {

        Intent i = new Intent(this, fulldetails.class);
        i.putExtra(fulldetails.message,s);
        startActivity(i);
    }
    public  void bookingcancellation(int position,String s,String tenanttranno,String transactionid,String amount,String transferid)
    {

Dialog warning=new Dialog(this);
View view=LayoutInflater.from(this).inflate(R.layout.dialogforleaveconfirmation,null);

        warning.setContentView(view);

// Find views from the custom layout
        Button cancelButton = view.findViewById(R.id.cancelButton);
        Button okButton = view.findViewById(R.id.okButton);
        okButton.setBackgroundResource(R.drawable.backgroundshape90);
        okButton.setOnClickListener(v -> {
            // TODO: put your "delete/leave" logic here
            warning.dismiss();

            long value = Long.parseLong(amount);

            // Divide by 100 to drop last 2 digits
            value = value / 100;

            // Convert back to String
            String result = String.valueOf(value);
       Dialog progressdialog=new Dialog(this);
        progressdialog.setContentView(R.layout.progress_dialog_layout);
        progressdialog.setCancelable(false);
        progressdialog.show();

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
                    currentData.child("count"+k).setValue(data);


                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                    progressdialog.dismiss();
                    Toast.makeText(studentinfoandrooms.this, "refund initiated successfully", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(studentinfoandrooms.this, studentinfoandrooms.class);
                    startActivity(i);
                }
            });





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
        ProgressDialog pd = new ProgressDialog(studentinfoandrooms.this);
        pd.setCancelable(false);
        pd.setMessage("Please wait...");
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