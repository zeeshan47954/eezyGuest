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
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
private long noofheads;
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
    ProgressBar progressBar;

    String email;
    String name;
    String id;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpay);
 paybutton=findViewById(R.id.pay);
paytext=findViewById(R.id.paytext);
info=getIntent().getStringExtra(message);

progressBar=findViewById(R.id.pb);

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
        "\n 4. ₹200 will be deducted upon cancellation.";
TextView as=findViewById(R.id.headsup1);
as.setText(headsup);

        Checkout.preload(getApplicationContext());
        String keyword = "idstarts";
        int startIndex = info.indexOf(keyword);
        String beforeKeyword = info.substring(0, startIndex);
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

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(gpayActivity.this);

        String name = account.getDisplayName();
        String id=account.getId();
        tenantnameaccount=name;
        tenantid=id;
        String email=account.getEmail();

        DatabaseReference dss= FirebaseDatabase.getInstance().getReference("google").child(beforeKeyword).child(idvalue).child("owner");
        dss.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = account.getDisplayName();
                String id = account.getId();
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
                    String date = snapshot.child("actualrequestsrecieved").child(keylist).child("uploaded on and at").getValue(String.class);

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
        String id = account.getId();

        DatabaseReference ss=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");

        ss.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                noofheads = snapshot.child(info).child("no of people equal to").getValue(long.class);
                fulltenantname = snapshot.child("name").getValue(String.class);

// Calculate total amount as double first
                double totalamount2 = amount * noofheads + (amount * noofheads * 0.18);

// Convert to long (in paise) before converting to String
                long totalamountInPaise = (long)(totalamount2 * 100);
                totalamount = String.valueOf(totalamountInPaise);

// Calculate owner amount as long to avoid decimal issues
                long owneramountInPaise = (long)(totalamountInPaise * 0.85);
                owneramount = String.valueOf(owneramountInPaise);

                gender = snapshot.child("gender").getValue(String.class);
                paytext.setText("The room price per head is: " + amount +
                        "\n your set spots is: " + noofheads +
                        "\n Therefore, the total amount that you will pay is: room price * no.of spots + (18% gst)" +
                        "\n total amount = " + totalamount2);

                paybutton.setText("pay  ₹" + totalamount2);
                phoneno = snapshot.child("number").getValue(String.class);
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
       createOrderOnServer();

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

        String credentials = "YOUR_RAZORPAY_KEY_ID:YOUR_RAZORPAY_KEY_SECRET";
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

       progressBar.setVisibility(View.VISIBLE);
       paybutton.setVisibility(View.GONE);

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
                   progressBar.setVisibility(View.GONE);
                   paybutton.setVisibility(View.VISIBLE);

                   if (!orderId.isEmpty()) {
                       orderIdFinal = orderId;
                       this.transferId = transferId;


                       // ✅ Launch Razorpay checkout
                       startPayment(orderIdFinal);
                   } else {
                       paybutton.setVisibility(View.VISIBLE);
                       Toast.makeText(this,
                               "Error creating order: " + jsonResponse.optString("error", "Unknown error"),
                               Toast.LENGTH_LONG).show();
                   }
               });

           } catch (Exception e) {
               Log.e("Order", "Error: ", e);
               runOnUiThread(() -> {
                   progressBar.setVisibility(View.GONE);
                   paybutton.setVisibility(View.VISIBLE);
                   Toast.makeText(this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show();
               });
           }
       }).start();
   }


    String notificationtenant;
    String notificationowner;
    String orderidfinal;
    private void startPayment(String orderid) {

orderidfinal=orderid;




        Checkout checkout = new Checkout();
        checkout.setKeyID("YOUR_RAZORPAY_KEY_ID");


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

    @Override
    public void onPaymentSuccess(String razorpayPaymentId) {
try {
    // Handle successful payment (e.g., update server or display success message)
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(gpayActivity.this);

    if (account != null) {
        String name = account.getDisplayName();
        String id = account.getId();
        Button bt=findViewById(R.id.pay);
        bt.setVisibility(View.GONE);
         progressBar=findViewById(R.id.pb);
        progressBar.setVisibility(View.VISIBLE);

        String idss = name + "idstarts" + id + "idends";

        String keyword = "idstarts";
        int startIndex = info.indexOf(keyword);
        String beforeKeyword = info.substring(0, startIndex);
        String keyword1 = "room";
        int startIndex1 = info.indexOf(keyword1) + keyword1.length();
        String afterKeyword = info.substring(startIndex1);
        String keywordStart = "idstarts";
        String keywordEnd = "idends";

        int startIndex2 = info.indexOf(keywordStart) + keywordStart.length();
        int endIndex = info.indexOf(keywordEnd);

        String idvalue = info.substring(startIndex2, endIndex);


 DatabaseReference fff=FirebaseDatabase.getInstance().getReference("pendingpayments");
 fff.runTransaction(new Transaction.Handler() {
     @NonNull
     @Override
     public Transaction.Result doTransaction(@NonNull MutableData currentData) {
         Long a=currentData.getChildrenCount();
         if(a==null)a=0L;
         a++;
         Map<String, Object> data = new HashMap<>();
         data.put("orderid",orderidfinal);
         data.put("transactionid",razorpayPaymentId);
         data.put("transferid",transferId);
         data.put("tenantname",name);
         data.put("tenantid",id);
         data.put("idss",idss);
         data.put("info",info);
         data.put("totalamount",totalamount);
         data.put("fulltenantname",fulltenantname);
         data.put("amount",owneramount);
         data.put("gender",gender);
         data.put("paymentno",a);

         currentData.child("payment"+a).setValue(data);




         return Transaction.success(currentData);
     }

     @Override
     public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
Intent i=new Intent(gpayActivity.this, studentinfoandrooms.class);
startActivity(i);
     }
 });


     /*   String ownername=info.split("idstarts")[0];
        String ownerid=info.split("idstarts")[1].split("idends")[0];
        DatabaseReference rd1=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner");
        rd1.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String genderoriginal=currentData.child("rooms").child("roomId:"+info).child("bookedbygender").getValue(String.class);
                if(genderoriginal==null)genderoriginal=gender;
                else{
                    genderoriginal=genderoriginal+","+gender;
                }
                currentData.child("rooms").child("roomId:"+info).child("bookedbygender").setValue(genderoriginal);

                Long count=currentData.child("rooms").child("roomId:"+info).child("grabbedby").getChildrenCount();
                if(count==null)count=0L;
                count++;
                currentData.child("rooms").child("roomId:"+info).child("grabbedby").child("tenant"+count).setValue(idss);
                Long recieptcount=  currentData.child("notificationrecieptforowner").getValue(Long.class);
                if(recieptcount==null)return Transaction.success(currentData);
                recieptcount++;
                currentData.child("notificationrecieptforowner").setValue(recieptcount);
                Long bookedcount=  currentData.child("notificationforownerbooked").getValue(Long.class);
                if(recieptcount==null)return Transaction.success(currentData);
                bookedcount++;
                currentData.child("notificationforownerbooked").setValue(bookedcount);

                String realownername=currentData.child("ownername").getValue(String.class);
                if(realownername==null)return Transaction.success(currentData);
                String realownernumber=currentData.child("ownernumber").getValue(String.class);
                if(realownernumber==null)return Transaction.success(currentData);
                Long transactionsNo=currentData.child("transactionsrecievedno").getValue(Long.class);
                for(MutableData ss:currentData.child("actualrequestsrecieved").getChildren())
                {
                    String key=ss.getKey();
                    if(key==null)return Transaction.success(currentData);
                    if(key!=null && key.contains(name+"idstartsfortenant"+id+"idendsfortenantforroomwithsid"+info))
                    {

                        currentData.child("actualrequestsrecieved").child(key).child("paid").setValue(1);
                  break;
                    }


                }

                if(transactionsNo==null)transactionsNo=0L;
                transactionsNo++;

                HashMap<String, Object> ownerData = new HashMap<>();
                ownerData.put("transactionid", razorpayPaymentId);
                ownerData.put("idss", idss);
                ownerData.put("orderid", orderidfinal);
                ownerData.put("info", info);
                ownerData.put("amount", owneramount);
                ownerData.put("tenantname", fulltenantname);
                ownerData.put("totalamount", totalamount);
                ownerData.put("transactionno", transactionsNo);
                ownerData.put("transferid", transferId);
                ownerData.put("status", "processing");

// date in format yyyy-MM-dd
                String transactionDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                        .format(new java.util.Date());
                ownerData.put("transactionDate", transactionDate);

// time in format HH:mm:ss
                String transactionTime = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        .format(new java.util.Date());
                ownerData.put("transactionTime", transactionTime);
                currentData.child("transactionsrecievedno").child("transaction"+transactionsNo).setValue(ownerData);
                notificationowner=currentData.child("fcmtoken").getValue(String.class);
                if(notificationowner==null)return Transaction.success(currentData);
                return Transaction.success(currentData);

            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                DatabaseReference dr2=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
                dr2.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                        Long count1=currentData.child("notificationrecieptfortenant").getValue(Long.class);
                        if(count1==null)return Transaction.success(currentData);
                        count1++;
                        currentData.child("notificationrecieptfortenant").setValue(count1);
                        Long count2=currentData.child("notificationfortenantbooked").getValue(Long.class);
                        if(count2==null)return Transaction.success(currentData);
                        count2++;
                        currentData.child("notificationfortenantbooked").setValue(count2);

                        Long transactionsNo=currentData.child("transactionsmadeNo").getChildrenCount();
                        if(transactionsNo==null)transactionsNo=0L;
                        transactionsNo++;
                        currentData.child(info).child("paid").setValue(1);


                        HashMap<String, Object> ownerData = new HashMap<>();
                        ownerData.put("transactionid", razorpayPaymentId);
                        ownerData.put("idss", idss);
                        ownerData.put("orderid", orderidfinal);
                        ownerData.put("info", info);
                        ownerData.put("amount", owneramount);
                        ownerData.put("tenantname", fulltenantname);
                        ownerData.put("totalamount", totalamount);
                        ownerData.put("transactionno", transactionsNo);
                        ownerData.put("transferid", transferId);
                        ownerData.put("status", "processing");

// date in format yyyy-MM-dd
                        String transactionDate = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
                                .format(new java.util.Date());
                        ownerData.put("transactionDate", transactionDate);

// time in format HH:mm:ss
                        String transactionTime = new java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                                .format(new java.util.Date());
                        ownerData.put("transactionTime", transactionTime);
                        Long historycount=currentData.child("history").getChildrenCount();
                        if(historycount==null)historycount=0L;
                        historycount++;
                        currentData.child("history").child("payment"+historycount).setValue(ownerData);
                        currentData.child("transactionmadeno").child("transaction"+transactionsNo).setValue(ownerData);



                        notificationtenant=currentData.child("fcmtoken").getValue(String.class);
                        if(notificationtenant==null)return Transaction.success(currentData);

                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        DatabaseReference ds=FirebaseDatabase.getInstance().getReference("paymentprocesschecker");
                        ds.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                Long count=currentData.getChildrenCount();
                                if(count==null)count=0L;
                                count++;
                                HashMap<String, Object> ownerData = new HashMap<>();
                                ownerData.put("orderid",orderidfinal);
                                ownerData.put("owner",info);
                                ownerData.put("tenant",idss);
                                ownerData.put("transferid", transferId);
                                ownerData.put("transactionid", razorpayPaymentId);
                                currentData.child("paymentprocesschecker"+count).setValue(ownerData);


                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {


                                Sendnotification notificationsSender1 = new Sendnotification(
                                        notificationowner,
                                        "success",
                                        "tenant has made the payment",
                                        gpayActivity.this
                                );
                                notificationsSender1.SendNotifications();

                                Sendnotification notificationsSender2 = new Sendnotification(
                                        notificationtenant,
                                        "congrats",
                                        "you have successfully booked the room",
                                        gpayActivity.this
                                );
                                notificationsSender2.SendNotifications();

                                Intent i=new Intent(gpayActivity.this, studentinfoandrooms.class);
                                startActivity(i);
                                finish();
                            }
                        });


                    }
                });


            }
        });*/



    }
}
catch (Exception e)
{
    Toast.makeText(this, "sorry an error occured while processing the payment"+e.getMessage(), Toast.LENGTH_SHORT).show();

}


    }

    @Override
    public void onPaymentError(int code, String description) {
        Log.e("PaymentActivity", "Payment failed: " + description);
        // Handle payment failure (e.g., retry or display error message)
   resetUI();
    }
    private void resetUI() {
        progressBar.setVisibility(View.GONE);
        paybutton.setVisibility(View.VISIBLE);
    }

}
