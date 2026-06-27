package com.example.bookandpostroom;

import static android.app.PendingIntent.getActivity;
import static java.lang.System.exit;
import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.appcompat.widget.Toolbar;
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
import java.util.HashMap;

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
private long index=0;
private long bruhh;
private String district;
private String institution;
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
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_5);
        Toolbar tb = findViewById(R.id.tbmain);
        ActivityCacheHelper dbHelper = new ActivityCacheHelper(this);
        dbHelper.saveLastActivity(Activity5.this.getClass().getName());

        setSupportActionBar(tb);







        int tealColor = ContextCompat.getColor(this, R.color.white);
        ColorStateList colorStateList = ColorStateList.valueOf(tealColor);




        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if (account != null) {
            String name = account.getDisplayName();
            String id = account.getId();
            name2 = name;

            DatabaseReference ss = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");
            ss.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    roomsposted = snapshot.child("roomsposted").getValue(long.class);
                    long accountsetup = snapshot.child("accountsetup").getValue(long.class);
                    if (accountsetup == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Activity5.this);
                        builder.setMessage("In order to recieve payments and request from the tenant/students,you need to set up your account");
                        builder.setCancelable(false);
                        builder.setPositiveButton("proceed", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(Activity5.this, AccountsetupActivity.class);
                                startActivity(i);
                                finish();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                    roomsposted--;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });


        }


        BottomNavigationView BNV = findViewById(R.id.bottom_navigation);
        BNV.setItemIconTintList(null);
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        if (account != null) {
            String name = account.getDisplayName();
            String id = account.getId();
            ownername = name;
            ownerid = id;

            DatabaseReference dbrroom = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");
            dbrroom.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long s = snapshot.child("roomsposted").getValue(long.class);
                    countroom = s;
                    bountroom = s;
                    countroom--;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            // Initialize fragments only once
            if (savedInstanceState == null) {
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
        DatabaseReference dss=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner");
        dss.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long s=snapshot.child("notificationforrequestsrecieved").getValue(Long.class);;

                if(s!=null)
                {

                    int menuItemId = R.id.request1;  // Replace with your menu item ID
                    BadgeDrawable badge = BNV.getOrCreateBadge(menuItemId);
                    if(s>0)
                    {
                        if(BNV.getSelectedItemId()==R.id.request1) {
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(requestfragment)
                                    .attach(requestfragment)
                                    .commitAllowingStateLoss();




                        }
                        else{

                            int ni =  s.intValue();
                            badge.setVisible(true);
                            badge.setBackgroundColor(Color.RED);   // Red color
                            badge.setNumber(ni);
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



                Long k=snapshot.child("notificationrecieptforowner").getValue(Long.class);

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
                                    .detach(paymentfragment)
                                    .attach(paymentfragment)
                                    .commitAllowingStateLoss();


                        }
                        else {int myInt =  k.intValue();
                            badge.setVisible(true);
                            badge.setBackgroundColor(Color.RED);   // Red color
                            badge.setNumber(myInt);
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(paymentfragment)
                                    .attach(paymentfragment)
                                    .commitAllowingStateLoss();

                        }
                    }
                    else{
                        badge.setVisible(false);
                    }
                }
                Long jj=snapshot.child("notificationforownerbooked").getValue(Long.class);

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
                                    .detach(roomoccupiedfragment)
                                    .attach(roomoccupiedfragment)
                                    .commitAllowingStateLoss();

                        }
                        else {int myInt3 =  jj.intValue();
                            badge.setVisible(true);
                            badge.setBackgroundColor(Color.RED);   // Red color
                            badge.setNumber(myInt3);
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                                    .detach(roomoccupiedfragment)
                                    .attach(roomoccupiedfragment)
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
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.home1) {
                    selectedFragment = fragmentHome;
                } else if (item.getItemId() == R.id.profile1) {
                    selectedFragment = fragmentProfile;
                } else if (item.getItemId() == R.id.booked1) {
                    dss.child("notificationforownerbooked").setValue(0);
                    selectedFragment = roomoccupiedfragment;
                } else if (item.getItemId() == R.id.request1) {
                    dss.child("notificationforrequestsrecieved").setValue(0);
                    selectedFragment = requestfragment;
                } else if (item.getItemId() == R.id.payment1) {
                    dss.child("notificationrecieptforowner").setValue(0);
                    selectedFragment = paymentfragment;
                } else {
                    return false;
                }

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
    }
    MenuItem refresh;
    View customView1;
    View customView2;
    View customView3;

    ImageView icon;

    MenuItem add;
    MenuItem logout;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        refresh=menu.findItem(R.id.refresh);
        add=menu.findItem(R.id.add);
        logout=menu.findItem(R.id.logout);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        customView1 = LayoutInflater.from(this).inflate(R.layout.menucutforrefresh, null);
        customView2 = LayoutInflater.from(this).inflate(R.layout.menucutforadd, null);
        customView3 = LayoutInflater.from(this).inflate(R.layout.menucutforlogout, null);

        refresh.setActionView(customView1);
        add.setActionView(customView2);
        logout.setActionView(customView3);
        // Use your own icon
customView1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
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
        Intent i=new Intent(Activity5.this,addActivity.class);
        startActivity(i);

    }
});
customView3.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        dialog=new Dialog(Activity5.this);
        dialog.setContentView(R.layout.dialogforlogoutconfirmation);

        dialog.setCancelable(false);
        Button cancel=dialog.findViewById(R.id.cancelButton);
        Button ok=dialog.findViewById(R.id.okButton);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signOut();


            }
        });


        dialog.show();
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
    @Override
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


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();

                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Activity5.this);
                if(account!=null)

                {

                    String[] parts = j.split("room");
                    String number = parts[1];


String name=account.getDisplayName();
String sid=account.getId();
                    ProgressDialog pd=new ProgressDialog(Activity5.this);
                    pd.setMessage("making changes please wait");
                    pd.setCancelable(false);
                    pd.show();

                    DatabaseReference drrr = FirebaseDatabase.getInstance().getReference().child("rooms");
                    drrr.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                           for(MutableData dd:currentData.getChildren())
                           {String key=dd.getKey();
                               if(dd.getValue(String.class).equals(j))
                               {
                                   currentData.child(key).setValue(null);

                               }


                           }

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        }
                    });

                    StorageReference sr1 = FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(sid).child("images"+number);
                    sr1.listAll().addOnSuccessListener(listResult -> {
                        bruhh=listResult.getItems().size();
                        // Loop through each file in the folder
                        for (StorageReference fileRef : listResult.getItems()) {
                            // Delete the file
                            fileRef.delete().addOnSuccessListener(aVoid -> {
                                index++;
                                if(index==bruhh)
                                {
                                    pd.dismiss();
                                    Intent i=new Intent(Activity5.this,Activity5.class);
                                    startActivity(i);
                                    finish();



// Retrieve the data from the "rooms" reference



                                }


                            }).addOnFailureListener(e -> {

                            });
                        }
                    }).addOnFailureListener(e -> {

                    });

                    DatabaseReference itemRef = FirebaseDatabase.getInstance().getReference("google").child(name).child(sid).child("owner").child("rooms").child("roomId:"+j);

                    itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            district=snapshot.child("district").getValue(String.class);
                            institution=snapshot.child("institution").getValue(String.class);
                            Toast.makeText(Activity5.this, "district:"+district+"/"+"institution:"+institution, Toast.LENGTH_SHORT).show();

                   snapshot.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void unused) {
                           DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child("district").child(district).child("institution").child(institution);

                           dj.addListenerForSingleValueEvent(new ValueEventListener() {
                               @Override
                               public void onDataChange(@NonNull DataSnapshot snapshot) {
                                   for(DataSnapshot df:snapshot.getChildren())
                                   {
                                        // Get the key (e.g., "room0", "room1", etc.)
                                       String roomValue = df.getValue(String.class);
                                       if(j.equals(roomValue))
                                       {
                                           snapshot.getRef().removeValue();
                                       }

                                   }
                               }

                               @Override
                               public void onCancelled(@NonNull DatabaseError error) {

                               }
                           });



                       }
                   });



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });




                    DatabaseReference gh=FirebaseDatabase.getInstance().getReference("google").child(name).child(sid).child("owner");
                    gh.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            long l=snapshot.child("roomsposted").getValue(Long.class);
                            if(l>0)
                            {    l--;}

                        snapshot.getRef().child("roomsposted").setValue(l);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }




            }
        });


        dialog2.show();

    }
    private void signOut() {
        mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                dialog.dismiss();
                Intent intent = new Intent(Activity5.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(Activity5.this, "sorry ,an error occured while sigining out", Toast.LENGTH_SHORT).show();
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
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed(); // This will exit the a
            finishAffinity(); // Closes all activities
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false; // reset the flag after 2 seconds
            }
        }, 2000);
    }


}
