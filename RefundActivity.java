package com.example.bookandpostroom;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefundActivity extends AppCompatActivity {
    public static final String roomid = "hello";
    public static final String ownertrano = "hello";
    public static final String tenanttrano = "hello";
    public static final String transactionid = "hello";
    public static final String amount = "hello";
    String stringroomid;
    String stringownertrano;
    String stringtenanttrano;
    String stringtransactionid;
    String stringamount;
    String gender;
    String noofheads;
    String genderpresent;
    long realtenanttranno;
    long realownertranno;
    String name;
    String id ;
    String ownername ;
    String ownerid;

    private Button refundButton;
    private RequestQueue requestQueue;
    private long peoplepresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_refund);
        stringroomid = getIntent().getStringExtra(roomid);
        stringownertrano = getIntent().getStringExtra(ownertrano);
        realownertranno= Long.parseLong(stringownertrano);
        stringtenanttrano = getIntent().getStringExtra(tenanttrano);
        realtenanttranno= Long.parseLong(stringtenanttrano);
        stringtransactionid = getIntent().getStringExtra(transactionid);
        stringamount = getIntent().getStringExtra(amount);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(RefundActivity.this);

         name = account.getDisplayName();
         id = account.getId();
         ownername = stringroomid.split("idstarts")[0];
         ownerid = stringroomid.split("idstarts")[1].split("idends")[0];



    }
    ProgressDialog pd;

    public void refund(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to cancel your booking?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                pd = new ProgressDialog(RefundActivity.this);
                pd.setMessage("Please wait...");
                pd.show();
                DatabaseReference ss=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("roomId:"+stringroomid);
                ss.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        gender=snapshot.child("gender").getValue(String.class);
                        noofheads=snapshot.child("no of people equal to").getValue(String.class);
                        Map<String,Object> data=new HashMap<>();
                        data.put("roomid",stringroomid);
                        data.put("ownertransactionnumber",stringownertrano);
                        data.put("tenanttransactionnumber",stringtenanttrano);
                        data.put("transactionid",stringtransactionid);
                        data.put("amount",stringamount);


                        DatabaseReference djj=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("refundinfo");
                        djj.setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                            pd.dismiss();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
 /*   const functions = require("firebase-functions");
const admin = require("firebase-admin");
const crypto = require("crypto");

admin.initializeApp();

    // Function to send notifications
    async function sendNotification(token, title, body) {
        if (!token) {
            console.log("No FCM token provided, skipping notification");
            return;
        }

        // Log what we're sending for debugging
        console.log("Sending notification:", {
                token: token.substring(0, 10) + "...",
                title,
                message: body
  });

        try {
    const response = await fetch('https://us-central1-bookandpostroom-ecf7e.cloudfunctions.net/sendFcmMessage', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                    token: token,
                    title: title,
                    message: body
      })
    });

    const data = await response.json();

            if (!response.ok || !data.success) {
                throw new Error(data.error || 'Failed to send FCM message');
            }

            console.log("Successfully sent notification:", { title, token: token.substring(0, 10) + "..." });
            return data;
        } catch (error) {
            console.error("Error sending notification:", error);
        }
    }

    exports.razorpaySettlementWebhook = functions.https.onRequest(async (req, res) => {
        try {
    const secret = "YOUR_RAZORPAY_WEBHOOK_SECRET";

            // Verify Razorpay signature
    const shasum = crypto.createHmac("sha256", secret);
            shasum.update(JSON.stringify(req.body));
    const digest = shasum.digest("hex");

            if (digest !== req.headers["x-razorpay-signature"]) {
                console.error("Invalid signature ❌");
                return res.status(400).send("Invalid signature");
            }

            console.log("✅ Webhook verified");
            console.log("Event received:", req.body.event);

            let payments = [];
            let refunded = false;
            let settled = false;

            if (req.body.event === "settlement.processed") {
      const settlement = req.body.payload.settlement.entity;
                payments = settlement.payments || [];
                settled = true;
            } else if (req.body.event === "refund.processed") {
                // refund event → payment id is inside refund
      const refund = req.body.payload.refund.entity;
                payments = [{ id: refund.payment_id }];
                refunded = true;
            }

            for (const payment of payments) {
      const paymentId = payment.id;
                console.log("Checking for payment_id:", paymentId);

                // Traverse paymentprocesschecker in Realtime Database
      const snapshot = await admin.database().ref("paymentprocesschecker").once("value");

                if (snapshot.exists()) {
                    snapshot.forEach(async (child) => {
          const data = child.val();
          const entryKey = child.key; // Get the key to remove the entry later

                    if (data.transactionid === paymentId) {
                        console.log("✅ Match found:", data);

            const owner = data.owner;
            const tenant = data.tenant;

                        console.log("Owner:", owner, "Tenant:", tenant);

                        // Parse owner data
            const ownername = owner.split("idstarts")[0];
            const ownerid = owner.split("idstarts")[1].split("idends")[0];

                        // Parse tenant data
            const tenantname = tenant.split("idstarts")[0];
            const tenantid = tenant.split("idstarts")[1].split("idends")[0];
long noofpeople;
long tenant
               DatabaseReference dk=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");

               dk.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                       noofpeople=snapshot.child(owner).child("no of people equal to").getValue(long.class);
                       snapshot.getRef().child(owner).removeValue();
                       for(DataSnapshot ds:snapshot.child("requestsenttotheownernumbers").getChildren())
                       {
                           String key=ds.getKey();
                           if(key!=null && key.contains(owner))
                           {

                               snapshot.getRef().child("requestsenttotheownernumbers").child(key).removeValue();

                           }
                       }

                               for(DataSnapshot ds:snapshot.child("transactionmadeno").getChildren())
                               {
                                   if(ds.child("transactionid").getValue(String.class).equals(data.transactionid))
                                   {
                                       if(refunded) {
                                           ds.child("status").getRef().setValue("refunded");
                                       }
                                       else if(settled)
                                       {
                                           ds.child("status").getRef().setValue("settled");
                                       }
                                   }

                               }
                                for(DataSnapshot ds:snapshot.child("history").getChildren())
                               {
                                   if(ds.child("transactionid").getValue(String.class).equals(data.transactionid))
                                   {
                                       if(refunded) {
                                           ds.child("status").getRef().setValue("refunded");
                                       }
                                       else if(settled)
                                       {
                                           ds.child("status").getRef().setValue("settled");
                                       }
                                   }

                               }

                       DatabaseReference dd=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner");
                               dd.addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                                       long peoplepresent=snapshot.child("rooms").child("roomId:"+owner).child("roomcapacityreached");
                                       peoplepresent=peoplepresent-noofpeople;
                                       snapshot.child("rooms").child("roomId:"+owner).child("roomcapacityreached").setValue(peoplepresent);
                                       for(DataSnapshot dd:snapshot.child("actualrequestsrecieved").getChildren())
                                       {
                                           String key=dd.getKey();
                                           if(key!=null && key.contains(tenantname+"idstartsfortenant"+tenantid+"idendsfortenantforroomwithsid"+owner))
                                           {
                                               snapshot.child("actualrequestsrecieved").child(key).removeValue();

                                           }


                                       }

                                       for(DataSnapshot aj:snapshot.child("rooms").child("roomId:"+owner).child("grabbedby").getChildren())
                                       {
                                           String value=aj.getValue);
                                           String key=aj.getkey();
                                           if(value!=null && value.contains(tenant))
                                           {
                                               snapshot.child("rooms").child("roomId:"+owner).child("grabbedby").child(key).removeValue();

                                           }


                                       }
                                       for(DataSnapshot dd:snapshot.child("transactionsrecievedno").getChildren())
                                       {
                                           if(dd.child("transactionid").getValue(String.class).equals(data.transactionid))
                                           {
                                               if(refunded) {
                                                   dd.child("status").getRef().setValue("refunded");
                                               }
                                               else if(settled)
                                               {
                                                   dd.child("status").getRef().setValue("settled");
                                               }
                                           }


                                       }
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

                        try {
                            // Get owner data
              const ownerSnapshot = await admin.database()
                                    .ref("google")
                                    .child(ownername)
                                    .child(ownerid)
                                    .child("owner")
                                    .once("value");

                            if (ownerSnapshot.exists()) {
                const ownerData = ownerSnapshot.val();
                const ownerfcmtoken = ownerData.fcmtoken;

                                // Update owner's transaction status
                const ownerTransactions = ownerData.transactionsrecievedno || {};
                                for (const transactionKey in ownerTransactions) {
                  const transaction = ownerTransactions[transactionKey];
                                    if (transaction.transactionid === data.transactionid) {
                    const status = refunded ? "refunded" : (settled ? "settled" : "unknown");
                                        await admin.database()
                                                .ref("google")
                                                .child(ownername)
                                                .child(ownerid)
                                                .child("owner")
                                                .child("transactionsrecievedno")
                                                .child(transactionKey)
                                                .child("status")
                                                .set(status);
                                        break;
                                    }
                                }

                                // Get tenant data
                const tenantSnapshot = await admin.database()
                                        .ref("google")
                                        .child(tenantname)
                                        .child(tenantid)
                                        .child("student")
                                        .once("value");

                                if (tenantSnapshot.exists()) {
                  const tenantData = tenantSnapshot.val();
                  const tenantfcmtoken = tenantData.fcmtoken;
                  const tenantrealname = tenantData.name;

                                    // Update tenant's transaction status in transactionmadeno
                  const tenantTransactionsMade = tenantData.transactionmadeno || {};
                                    for (const transactionKey in tenantTransactionsMade) {
                    const transaction = tenantTransactionsMade[transactionKey];
                                        if (transaction.transactionid === data.transactionid) {
                      const status = refunded ? "refunded" : (settled ? "settled" : "unknown");
                                            await admin.database()
                                                    .ref("google")
                                                    .child(tenantname)
                                                    .child(tenantid)
                                                    .child("student")
                                                    .child("transactionmadeno")
                                                    .child(transactionKey)
                                                    .child("status")
                                                    .set(status);
                                            break;
                                        }
                                    }

                                    // Update tenant's transaction status in history
                  const tenantHistory = tenantData.history || {};
                                    for (const historyKey in tenantHistory) {
                    const historyItem = tenantHistory[historyKey];
                                        if (historyItem.transactionid === data.transactionid) {
                      const status = refunded ? "refunded" : (settled ? "settled" : "unknown");
                                            await admin.database()
                                                    .ref("google")
                                                    .child(tenantname)
                                                    .child(tenantid)
                                                    .child("student")
                                                    .child("history")
                                                    .child(historyKey)
                                                    .child("status")
                                                    .set(status);
                                            break;
                                        }
                                    }

                                    // Send notifications
                                    if (refunded) {
                                        // Send notification to tenant
                                        await sendNotification(
                                                tenantfcmtoken,
                                                "Refund Processed",
                                                "Amount has been refunded"
                    );

                                        // Send notification to owner
                                        await sendNotification(
                                                ownerfcmtoken,
                                                "Refund Processed",
                      `Payment from ${tenantrealname} was refunded`
                    );
                                    } else if (settled) {
                                        // Send notification to tenant
                                        await sendNotification(
                                                tenantfcmtoken,
                                                "Payment Settled",
                                                "Amount has been settled into the owner's bank account"
                    );

                                        // Send notification to owner
                                        await sendNotification(
                                                ownerfcmtoken,
                                                "Payment Settled",
                                                "Amount has been settled into your bank account"
                    );
                                    }

                                    console.log("✅ Transaction status updated and notifications sent");
                                }
                            }

                            // Remove the entry from paymentprocesschecker after processing
                            await admin.database()
                                    .ref("paymentprocesschecker")
                                    .child(entryKey)
                                    .remove();

                            console.log("✅ Entry removed from paymentprocesschecker:", entryKey);

                        } catch (processingError) {
                            console.error("Error processing transaction:", processingError);
                        }
                    }
        });
                }
            }

            return res.json({ status: "ok" });
        } catch (err) {
            console.error("Error handling webhook:", err);
            return res.status(500).send("Internal Server Error");
        }
    });*/

}