package com.example.bookandpostroom;
import static java.security.AccessController.getContext;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class gpayActivity extends AppCompatActivity implements PaymentResultListener {
public static final String message="";
private String tenantname;
private String ownername;
private String tenantmobile;
private String ownermobile;
private long ownertranno;
private String totalamount;
private long tenanttranno;

private Long amount;
private Long noofheads;
private String phoneno;
private String info;
private String upiid;
private long transactionsmade;
private long number=0;
private String gender;
private boolean found=false;
private TextView paytext;
private TextView payinfo;

private String gendertobeadded;


    String email;
    String name;
    String id;
    FirebaseUser account;
    Button paybutton;
    private String orderIdFinal;
    private RequestQueue requestQueue;
    private String linkedAccountId;
private String ownernameaccount;
private String ownerid;
private String tenantnameaccount;
private String tenantid;
private String fulltenantname;
Dialog progressdialog;
    String acc;
    String ben;
    String ifs;
    String owneramount;
    private FirebaseAuth firebaseAuth;
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
        setContentView(R.layout.activity_gpay);
        applySystemBarPadding();
 paybutton=findViewById(R.id.pay);
paytext=findViewById(R.id.paytext);
info=getIntent().getStringExtra(message);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }



        progressdialog=new Dialog(this);
        progressdialog.setCancelable(false);
        progressdialog.setTitle("loading");
        View view = LayoutInflater.from(this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressdialog.setContentView(view);

        // Show the dialog
        progressdialog.show();


String headsup="1.As already mentioned,the app helps you to pay rent for the first month" +
        "\n 2. Payment goes directly to the owner." +
        "\n 3. Cancel within 2 days for a refund." +
        "\n 4. 18%  as gst will be deducted upon cancellation.";
TextView as=findViewById(R.id.headsup1);
as.setText(headsup);

        Checkout.preload(getApplicationContext());
        String keyword = "idstarts";
        int startIndex = info.indexOf(keyword);
        String beforeKeyword = info.substring(0, startIndex);
        ownername=beforeKeyword;
        String keyword1 = "room";
        int startIndex1 = info.indexOf(keyword1) + keyword1.length();
        String afterKeyword = info.substring(startIndex1);
        String keywordStart = "idstarts";
        String keywordEnd = "idends";
ownernameaccount=beforeKeyword;

        int startIndex2 = info.indexOf(keywordStart) + keywordStart.length();
        int endIndex = info.indexOf(keywordEnd);

        String idvalue = info.substring(startIndex2, endIndex);
        ownerid=idvalue;

        firebaseAuth = FirebaseAuth.getInstance();
         account = firebaseAuth.getCurrentUser();

        String name = account.getDisplayName();
        String id=account.getUid();
        tenantnameaccount=name;
        tenantid=id;
        String email=account.getEmail();

        DatabaseReference dss= FirebaseDatabase.getInstance().getReference("google").child(beforeKeyword).child(idvalue).child("owner");
        dss.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = account.getDisplayName();
                String id = account.getUid();
                linkedAccountId = snapshot.child("accountdetails").child("linkedaccountid").getValue(String.class);

                String keylist = "";
                for (DataSnapshot ss : snapshot.child("actualrequestsrecieved").getChildren()) {

                    String key = ss.getKey();
                    if (key != null && key.contains(name + "idstartsfortenant" + id + "idendsfortenantforroomwithsid" + info)) {
                        keylist = key;
                        break;


                    }

                }
                if (!keylist.isEmpty()) {
                    String date = snapshot.child("actualrequestsrecieved").child(keylist).child("approvedDateTime").getValue(String.class);

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                    try {

                        // Convert Firebase string to Date object
                        Date oldDate = sdf.parse(date);

                        // Get current date and time
                        Date currentDate = new Date();
                        String date2 = sdf.format(currentDate);  // just for logging / storing

                        // Compare difference in millis
                        long diffInMillis = currentDate.getTime() - oldDate.getTime();

                        // Convert to hours
                        long diffInHours = diffInMillis / (1000 * 60 * 60);

                        if (diffInHours >= 24) {
                            // 24 hours have passed

                            AlertDialog.Builder builder = new AlertDialog.Builder(gpayActivity.this);
                            builder.setTitle("Time up");
                            builder.setMessage("payment cannot be made as you failed to pay within 24  hours");

// Only one button
                            builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent i = new Intent(gpayActivity.this, studentinfoandrooms.class);
                                    startActivity(i);
                                    finish(); // closes the activity
                                }
                            });

// Show the dialog
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            progressdialog.dismiss();
                            // Less than 24 hours

                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

                else{

                    AlertDialog.Builder builder = new AlertDialog.Builder(gpayActivity.this);
                    builder.setTitle("Time up");
                    builder.setMessage("payment cannot be made as you failed to pay within 24  hours");

// Only one button
                    builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Intent i=new Intent(gpayActivity.this, studentinfoandrooms.class);
                            startActivity(i);
                            finish(); // closes the activity
                        }
                    });

// Show the dialog
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

            }






            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


DatabaseReference df=FirebaseDatabase.getInstance().getReference("google").child(beforeKeyword).child(idvalue).child("owner").child("rooms").child("roomId:"+info).child("roomprice");
df.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        amount=snapshot.getValue(long.class);
        String name = account.getDisplayName();
        String id = account.getUid();

        DatabaseReference ss=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");

        ss.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

if(snapshot.child(info).child("no of people equal to").exists())
{noofheads = snapshot.child(info).child("no of people equal to").getValue(long.class);
    fulltenantname = snapshot.child("name").getValue(String.class);
Log.d("amount",amount.toString());
Log.d("amount",noofheads.toString());


// Calculate total amount as double first
    double totalamount2 = amount * noofheads + (amount * noofheads * 0.18);

    long totalamountInPaise = (long)(totalamount2 * 100);
    totalamount = String.valueOf(totalamountInPaise);

// Calculate owner amount as long to avoid decimal issues
    long owneramountInPaise = (long)(totalamountInPaise * 0.85);
    owneramount = String.valueOf(owneramountInPaise);

    gender = snapshot.child("fcmtoken").getValue(String.class);
    paytext.setText("The room price per head is: " + amount +
            "\n your set spots is: " + noofheads +
            "\n Therefore, the total amount that you will pay is: room price * no.of spots + (18% gst)" +
            "\n total amount = " + totalamount2);

    paybutton.setText("pay  ₹" + totalamount2);
    phoneno = snapshot.child("number").getValue(String.class);

}
else{
    Intent i=new Intent(gpayActivity.this,studentinfoandrooms.class);
    Toast.makeText(gpayActivity.this, "the owner has deleted the room", Toast.LENGTH_LONG).show();
    startActivity(i);
}



// Convert to long (in paise) before converting to String

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});
         // Preload Razorpay Checkout
paybutton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        paybutton.setVisibility(View.GONE);
        DatabaseReference dss= FirebaseDatabase.getInstance().getReference("google").child(beforeKeyword).child(idvalue).child("owner").child("rooms").child("roomId:"+info);
     dss.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
             if(snapshot.exists())
             {
                 dss.runTransaction(new Transaction.Handler() {
                     @NonNull
                     @Override
                     public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                         Date now = new Date();
                         String ownerQueueId = ownername + "+" + ownerid;
                         String existingQueue = currentData.child("priorityqueue").getValue(String.class);

                         // CRITICAL CHECK: If owner is in queue, ABORT immediately
                         if (existingQueue != null && existingQueue.contains(ownerQueueId)) {

                             return Transaction.abort();

                         }
                         // Set up formatter for IST time zone
                         SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                         sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // IST Time Zone

                         // Format the current time in IST
                         String istTime = sdf.format(now);

                         // Example: storing it in your object
                         String approvedDateTime = istTime;
                         currentData.child("paymentbeingmade").child(name+id).setValue(approvedDateTime);


                         return Transaction.success(currentData);
                     }

                     @Override
                     public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                         if (!committed)
                         {
                             Toast.makeText(gpayActivity.this, "cannot make payment at this time", Toast.LENGTH_SHORT).show();
                             Intent i=new Intent(gpayActivity.this,studentinfoandrooms.class);
                             startActivity(i);
                         }
                         else if(committed){
                             progressdialog.show();

                             createOrderOnServer();
                         }
                     }
                 });


             }
             else{
                 Toast.makeText(gpayActivity.this, "cannot make payment at this time", Toast.LENGTH_SHORT).show();
                 Intent i=new Intent(gpayActivity.this,studentinfoandrooms.class);
                 startActivity(i);
             }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
     });


    }
});
        // Call startPayment() when user initiates the payment

    }


    String transferId;
   /* private void createOrderWithSplit() {
        progressBar.setVisibility(View.VISIBLE);
        paybutton.setVisibility(View.GONE);
        requestQueue = Volley.newRequestQueue(this);



        JSONObject jsonBody = new JSONObject();
        try {
            long totalAmountLong = Long.parseLong(totalamount); // In paise
            long ownerAmountLong = (long) (amount * noofheads *100); // 85% in paise

            jsonBody.put("amount", totalAmountLong);
            jsonBody.put("currency", "INR");
            jsonBody.put("receipt", "receipt#" + System.currentTimeMillis());
            jsonBody.put("payment_capture", 1);

            JSONObject transfer = new JSONObject();
            transfer.put("account", linkedAccountId);
            transfer.put("amount", ownerAmountLong);
            transfer.put("currency", "INR");



            JSONArray transfers = new JSONArray().put(transfer);
            jsonBody.put("transfers", transfers);
        } catch (JSONException e) {
            e.printStackTrace();
            resetUI();
            return;
        }

        String credentials = BuildConfig.RAZORPAY_KEY_ID + ":" + BuildConfig.RAZORPAY_KEY_SECRET;
        String authHeader = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                "https://api.razorpay.com/v1/orders", jsonBody,
                response -> {
                    try {
                        orderIdFinal = response.getString("id");
                        // Store transfer ID from response
                        JSONArray transfers = response.getJSONArray("transfers");
                         transferId = transfers.getJSONObject(0).getString("id");

                        startPayment(orderIdFinal);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing order response", Toast.LENGTH_SHORT).show();
                        resetUI();
                    }
                },
                error -> {
                    Log.e("OrderError", "Failed: " + error.toString() + ", Response: " + new String(error.networkResponse.data));
                    Toast.makeText(this, "Failed to create order: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    resetUI();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", authHeader);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        requestQueue.add(request);

    }*/
   private void createOrderOnServer() {

       if (linkedAccountId == null)
       {
           Toast.makeText(this, "no linked account id found", Toast.LENGTH_SHORT).show();
       }
      else
      {paybutton.setVisibility(View.GONE);

       new Thread(() -> {
           try {
               String functionUrl = "https://us-central1-bookandpostroom-ecf7e.cloudfunctions.net/createRazorpayOrder";

               // Build JSON payload
               JSONObject jsonPayload = new JSONObject();
               jsonPayload.put("totalamount", totalamount);
               jsonPayload.put("amount", amount);
               jsonPayload.put("noofheads", noofheads);
               jsonPayload.put("linkedAccountId", linkedAccountId);

               // Setup connection
               URL url = new URL(functionUrl);
               HttpURLConnection connection = (HttpURLConnection) url.openConnection();
               connection.setRequestMethod("POST");
               connection.setRequestProperty("Content-Type", "application/json");
               connection.setRequestProperty("Accept", "application/json");
               connection.setDoOutput(true);

               // Send request
               OutputStream os = connection.getOutputStream();
               os.write(jsonPayload.toString().getBytes("UTF-8"));
               os.close();

               // Get response
               int responseCode = connection.getResponseCode();
               InputStream inputStream = (responseCode == HttpURLConnection.HTTP_OK)
                       ? connection.getInputStream()
                       : connection.getErrorStream();

               BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
               StringBuilder response = new StringBuilder();
               String line;
               while ((line = reader.readLine()) != null) {
                   response.append(line);
               }
               reader.close();
               connection.disconnect();

               Log.d("Order", "Raw response: " + response.toString());

               JSONObject jsonResponse = new JSONObject(response.toString());
               String orderId = jsonResponse.optString("orderId", "");
               String transferId = jsonResponse.optString("transferId", "");

               runOnUiThread(() -> {

                   paybutton.setVisibility(View.VISIBLE);

                   if (!orderId.isEmpty()) {
                       orderIdFinal = orderId;
                       this.transferId = transferId;


                       // ✅ Launch Razorpay checkout
                       startPayment(orderIdFinal);
                   } else {
                       progressdialog.dismiss();
                       paybutton.setVisibility(View.VISIBLE);
                       Toast.makeText(this,
                               "Error creating order: " + jsonResponse.optString("error", "Unknown error"),
                               Toast.LENGTH_LONG).show();
                   }
               });

           } catch (Exception e) {
               Log.e("Order", "Error: ", e);
               runOnUiThread(() -> {

                   paybutton.setVisibility(View.VISIBLE);
                   Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
               });
           }
       }).start();
   }
   }


    String notificationtenant;
    String notificationowner;
    String orderidfinal;
    private void startPayment(String orderid) {

orderidfinal=orderid;




        Checkout checkout = new Checkout();
        checkout.setKeyID(BuildConfig.RAZORPAY_KEY_ID);


        // Set payment details
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Booking app");
            options.put("description", "Payment for the room");
            options.put("order_id", orderid);
            options.put("currency", "INR");
            options.put("amount", totalamount);  // Amount in paise (e.g., 50000 = ₹500)

            // Optional settings

            options.put("prefill.contact", phoneno);

            // Open Razorpay Checkout Activity
            checkout.open(this, options);

        } catch (Exception e) {
            Log.e("PaymentActivity", "Error in starting Razorpay Checkout", e);
        }
    }
    Boolean found2=false;
    // TENANT SIDE - Payment success with owner priority check
    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
        try {
            DatabaseReference gg = FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner").child("rooms").child("roomId:" + info).child("paymentbeingmade");
           gg.runTransaction(new Transaction.Handler() {
               @NonNull
               @Override
               public Transaction.Result doTransaction(@NonNull MutableData currentData) {


                   for(MutableData dd:currentData.getChildren())
                   {
                       String key=dd.getKey();
                       String value=dd.getValue(String.class);
                       Log.d("value",key+":"+value+tenantnameaccount+tenantid);

                       if(key.equals(tenantnameaccount+tenantid))
                       {found2=true;
                           currentData.child(key).setValue(null);
                           break;
                       }

                   }


                   return Transaction.success(currentData);
               }

               @Override
               public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                   if (!found2) {
                       DatabaseReference dk = FirebaseDatabase.getInstance().getReference("pendingrefunds");
                       dk.runTransaction(new Transaction.Handler() {
                           @NonNull
                           @Override
                           public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                               Map<String, Object> mp = new HashMap<>();
                               String name = account.getDisplayName();
                               String id = account.getUid();
                               String idss = name + "idstarts" + id + "idends";
                               mp.put("amount", totalamount.substring(0, totalamount.length() - 2));
                               mp.put("paymentid", razorpayPaymentId);
                               mp.put("transferid", transferId);
                               mp.put("tenantinfo", idss);
                               mp.put("roominfo", info);

                               Long l = currentData.getChildrenCount();
                               if (l == null) l = 0L;
                               l++;

                               currentData.child("count"+id+ l).setValue(mp);
                               return Transaction.success(currentData);


                           }

                           @Override
                           public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                               String name = account.getDisplayName();
                               String id = account.getUid();


                               sendRefundNotification(name, id);

                               // Show toast and navigate
                               Toast.makeText(gpayActivity.this,
                                       "Room is not available anymore. Refund initiated.",
                                       Toast.LENGTH_LONG).show();
progressdialog.dismiss();
                               Intent i = new Intent(gpayActivity.this, studentinfoandrooms.class);
                               startActivity(i);
                               finish();
                           }
                       });


                   } else if (found2)
                   {   String iddd = account.getUid();
                   DatabaseReference pendingPaymentRef = FirebaseDatabase.getInstance()
                           .getReference("pendingpayments")
                           .child(iddd);

                   String name = account.getDisplayName();
                   String id = account.getUid();


                   String idss = name + "idstarts" + id + "idends";
                   pendingPaymentRef.runTransaction(new Transaction.Handler() {
                       @NonNull
                       @Override
                       public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                           Map<String, Object> data = new HashMap<>();
                           data.put("orderid", orderidfinal);
                           data.put("transactionid", razorpayPaymentId);
                           data.put("transferid", transferId);
                           data.put("tenantname", name);
                           data.put("tenantid", id);
                           data.put("idss", idss);
                           data.put("info", info);
                           data.put("totalamount", totalamount);
                           data.put("fulltenantname", fulltenantname);
                           data.put("amount", String.valueOf(amount * noofheads * 100));
                           data.put("gender", gender);
                           data.put("status", "completed");
                           data.put("timestamp", System.currentTimeMillis());

                           currentData.setValue(data);
                           return Transaction.success(currentData);
                       }

                       @Override
                       public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                           if (committed) {
                               String bookingId = "booking_" + System.currentTimeMillis();
                               // Or use: String bookingId = currentData.getKey(); // if you have a Firebase key

                               // Pass the booking ID to the worker
                               Data inputData = new Data.Builder()
                                       .putString("booking_id", bookingId)
                                       .build();

                               OneTimeWorkRequest reviewWork = new OneTimeWorkRequest.Builder(ReviewWorker.class)
                                       .setInitialDelay(20, TimeUnit.DAYS)
                                       .setInputData(inputData)
                                       .build();

                               WorkManager.getInstance(gpayActivity.this).enqueue(reviewWork);

                               Toast.makeText(gpayActivity.this,
                                       "Payment processed successfully!",
                                       Toast.LENGTH_SHORT).show();
progressdialog.dismiss();
                               Intent i = new Intent(gpayActivity.this, studentinfoandrooms.class);
                               startActivity(i);
                               finish();

                           }
                       }
                   });
               }               }
           });


        }
        catch (Exception d)
        {
            Toast.makeText(gpayActivity.this, d.getMessage(), Toast.LENGTH_LONG).show();
        }
    }



    private void sendRefundNotification(String tenantName, String tenantId) {
        // Create notification channel (for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "refund_channel",
                    "Refund Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for payment refunds");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Build and show notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "refund_channel")
                .setSmallIcon(R.drawable.notify) // Replace with your icon
                .setContentTitle("Refund Initiated")
                .setContentText("Full refund of ₹" +totalamount.substring(0, totalamount.length() - 2) + " has been initiated. " +
                        "You will receive the amount within 5-7 business days.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("The room you attempted to book is no longer available. " +
                                "A full refund of ₹" + totalamount.substring(0, totalamount.length() - 2) + " has been initiated to your account. " +
                                "You will receive the amount within 5-7 business days."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Check for notification permission (Android 13+)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1001, builder.build());
        }
    }





    @Override
    public void onPaymentError(int code, String description) {
        Log.e("PaymentActivity", "Payment failed: " + description);
        // Handle payment failure (e.g., retry or display error message)
   resetUI();
    }
    private void resetUI() {
progressdialog.dismiss();
        paybutton.setVisibility(View.VISIBLE);
    }

}
