package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class requestsActivityforowner extends AppCompatActivity implements fragmentforhome5.Listenerhome1,fragmentforhome5.Listenerhome2,fragmentforhome5.Listenerhome3,fragmentforhome5.Listenerhome4{
public static final String message="";
    String keytenant;
    String keyowner;
    String notificationtenant;
    String notifcationowner;
    Boolean alreadycancelledbytenant=false;
    private FirebaseAuth firebaseAuth;
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        LinearLayout scrollView = findViewById(R.id.main);

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
        setContentView(R.layout.activity_requests_activityforowner);
        applySystemBarPadding();
String jj=getIntent().getStringExtra(message);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Bundle bundle = new Bundle();
        bundle.putString("valuepassed", jj);

        fragmentforhome5 fragment = new fragmentforhome5();
        fragment.setArguments(bundle);

// Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();

        String ownername=account.getDisplayName();
        String ownerid=account.getUid();
DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner");
ds.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        notifcationowner=snapshot.child("fcmtoken").getValue(String.class);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

    }
String c;
    String timeoftheday;
    List<String>Queue=new ArrayList<>();
    // OWNER SIDE - Always gets priority, blocks tenant operations
    @Override
    public void itemclicked5ok(int position, String s, String j, String g) {
        Dialog progressDialog = new Dialog(requestsActivityforowner.this);
        progressDialog.setCancelable(false);
        View view2 = LayoutInflater.from(requestsActivityforowner.this)
                .inflate(R.layout.progress_dialog_layout, null);
        progressDialog.setContentView(view2);
        progressDialog.show();

        String tenantname = s.split("gapp")[0];
        String tenantid = s.split("gapp")[1];
        String ownername = j.split("idstarts")[0];
        String ownerid = j.split("idstarts")[1].split("idends")[0];

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();

        String ownerQueueId = ownername + "+" + ownerid;

        DatabaseReference queueRef = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(ownername)
                .child(ownerid)
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
                        // Owner already in queue, just keep it as is
                        currentData.setValue(existingQueue);
                    }
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                if (error != null || !committed) {
                    progressDialog.dismiss();
                    Toast.makeText(requestsActivityforowner.this,
                            "Error acquiring lock: " + (error != null ? error.getMessage() : "Unknown error"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // STEP 2: Owner is now in queue with priority
                // Small delay to ensure any tenant transactions see the owner in queue
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proceedWithAcceptance(progressDialog, queueRef, ownerQueueId,
                                tenantname, tenantid, j, ownername, ownerid);
                    }
                }, 100); // 100ms delay to ensure queue propagation
            }
        });
    }

    // Proceed with accepting the request
    private void proceedWithAcceptance(Dialog progressDialog, DatabaseReference queueRef,
                                       String ownerQueueId, String tenantname, String tenantid,
                                       String roomId, String ownername, String ownerid) {
        // STEP 3: Create acceptance request
        DatabaseReference accept = FirebaseDatabase.getInstance()
                .getReference("acceptrequest");

        accept.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long count = currentData.getChildrenCount();
                if (count == null) count = 0L;
                count++;

                Map<String, Object> mp = new HashMap<>();
                mp.put("name1", tenantname);
                mp.put("id1", tenantid);
                mp.put("roomid", roomId);
                mp.put("ownername", ownername);
                mp.put("ownerid", ownerid);
                mp.put("status", "accepted");
                mp.put("timestamp", System.currentTimeMillis());

                currentData.child("request" +ownerid+count).setValue(mp);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                // STEP 4: Remove owner from queue after completion
                removeFromQueue(queueRef, ownerQueueId, new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        if (committed && error == null) {
                            Toast.makeText(requestsActivityforowner.this,
                                    "Request accepted successfully",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requestsActivityforowner.this,
                                    "Error accepting request: " + (error != null ? error.getMessage() : "Unknown"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        Intent i = new Intent(requestsActivityforowner.this, Activity5.class);
                        startActivity(i);                        Handler h = new Handler();
                    }
                });
            }
        });
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

String gender;
    // OWNER SIDE - Cancel/Delete tenant payment request with absolute priority
    @Override
    public void itemclicked5paymentcancel(int position, String s, String j, String g) {
        Dialog dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.dialogfordeleteconfirmationrequest);
        dialog2.setCancelable(false);

        Button cancel = dialog2.findViewById(R.id.cancelButton);
        Button ok = dialog2.findViewById(R.id.okButton);

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

                Dialog progressDialog = new Dialog(requestsActivityforowner.this);
                progressDialog.setCancelable(false);
                View view2 = LayoutInflater.from(requestsActivityforowner.this)
                        .inflate(R.layout.progress_dialog_layout, null);
                progressDialog.setContentView(view2);
                progressDialog.show();

                String tenantname = s.split("gapp")[0];
                String tenantid = s.split("gapp")[1];
                String ownername = j.split("idstarts")[0];
                String ownerid = j.split("idstarts")[1].split("idends")[0];
                String roomnumber = j.split("idendsroom")[1];

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser account = firebaseAuth.getCurrentUser();

                String ownerQueueId = ownername + "+" + ownerid;
DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student").child("gender");
ds.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        gender=snapshot.getValue(String.class);
        DatabaseReference queueRef = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(ownername)
                .child(ownerid)
                .child("owner")
                .child("rooms")
                .child("roomId:" + j)
                .child("priorityqueue");
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
                    progressDialog.dismiss();
                    Toast.makeText(requestsActivityforowner.this,
                            "Error acquiring lock: " + (error != null ? error.getMessage() : "Unknown error"),
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // STEP 2: Owner is now in queue with priority
                // Small delay to ensure any tenant transactions see the owner in queue
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        proceedWithPaymentCancellation(progressDialog, queueRef, ownerQueueId,
                                tenantname, tenantid, j, ownername,
                                ownerid, notifcationowner);
                    }
                }, 100); // 100ms delay to ensure queue propagation
            }
        });

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});


                // STEP 1: Owner ALWAYS adds to queue (has absolute priority)
            }
        });

        dialog2.show();
    }

    // Proceed with cancelling/deleting the payment request
    private void proceedWithPaymentCancellation(Dialog progressDialog, DatabaseReference queueRef,
                                                String ownerQueueId, String tenantname, String tenantid,
                                                String roomId, String ownername, String ownerid,
                                                String notificationOwner) {
        // STEP 3: Create delete request
        DatabaseReference deleteRequestRef = FirebaseDatabase.getInstance()
                .getReference("deleterequestfromowner");

        deleteRequestRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long count = currentData.getChildrenCount();
                if (count == null) count = 0L;
                count++;

                Map<String, Object> data = new HashMap<>();
                data.put("tenantname", tenantname);
                data.put("tenantid", tenantid);
                data.put("j", roomId);
                data.put("gender",gender);
                data.put("ownername", ownername);
                data.put("ownerid", ownerid);
                data.put("notificationowner", notificationOwner);
                data.put("status", "processing");
                data.put("timestamp", System.currentTimeMillis());

                currentData.child("request" +ownerid+ count).setValue(data);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed,
                                   @Nullable DataSnapshot currentData) {
                // STEP 4: Remove owner from queue after completion
                removeFromQueue2(queueRef, ownerQueueId, new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();

                        if (committed && error == null) {
                            Toast.makeText(requestsActivityforowner.this,
                                    " request cancelled successfully",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requestsActivityforowner.this,
                                    "Error cancelling payment request: " +
                                            (error != null ? error.getMessage() : "Unknown"),
                                    Toast.LENGTH_SHORT).show();
                        }
                        Intent i = new Intent(requestsActivityforowner.this, Activity5.class);
                        startActivity(i);

                    }
                });
            }
        });
    }

    // Helper method to remove entry from queue with callback
    private void removeFromQueue2(DatabaseReference queueRef, String queueId, Runnable onComplete) {
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
    /*  @Override
    public void itemclicked5ok(int position,String s,String j,String g)
    {


        Dialog progressDialog = new Dialog(requestsActivityforowner.this);
        progressDialog.setCancelable(false); // Disable dismissal on touch outside

        // Inflate the custom layout
        View view2 = LayoutInflater.from(requestsActivityforowner.this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressDialog.setContentView(view2);

        // Show the dialog
        progressDialog.show();

        String tenantname=s.split("gapp")[0];
        String tenantid=s.split("gapp")[1];
        String ownername=j.split("idstarts")[0];
        String ownerid=j.split("idstarts")[1].split("idends")[0];

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        String id=account.getUid();
        timeoftheday=ownername+"+"+ownerid;
        DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner").child("rooms").child("roomId:"+j).child("priorityqueue");
        dj.runTransaction(new Transaction.Handler() {
                              @NonNull
                              @Override
                              public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                                  c = currentData.getValue(String.class);
                                  if (c == null || c.isEmpty()) {
                                      c = timeoftheday;
                                      currentData.setValue(c);
                                      Queue.add(c);
                                  } else {
                                      String j = c + "," + timeoftheday;
                                      currentData.setValue(j);
Queue.add(j);

                                  }
                                  return Transaction.success(currentData);
                              }

                              @Override
                              public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {




                                      DatabaseReference accept=FirebaseDatabase.getInstance().getReference("acceptrequest");
                                      accept.runTransaction(new Transaction.Handler() {
                                          @NonNull
                                          @Override
                                          public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                                              Long count=currentData.getChildrenCount();
                                              if(count==null)count=0L;
                                              count++;
                                              Map<String,Object> mp=new HashMap<>();
                                              mp.put("name1",tenantname);
                                              mp.put("id1",tenantid);
                                              mp.put("roomid",j);
                                              mp.put("ownername",ownername);
                                              mp.put("ownerid",ownerid);


                                              currentData.child("request"+count).setValue(mp);


                                              return Transaction.success(currentData);


                                          }

                                          @Override
                                          public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                             Queue.remove(timeoftheday);
                                             dj.getRef().setValue(Queue);
                                              Handler h=new Handler();
                                              h.postDelayed(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      Intent i=new Intent(requestsActivityforowner.this,Activity5.class);
                                                      startActivity(i);
                                                  }
                                              },2000);

                                          }
                                      });




                              }
                          });






     DatabaseReference dr111 = FirebaseDatabase.getInstance().getReference("google")
                .child(ownername).child(ownerid).child("owner");

        dr111.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                notifcationowner=currentData.child("fcmtoken").getValue(String.class);
                if(notifcationowner==null)return Transaction.success(currentData);
                List<String> keysToDelete = new ArrayList<>();
                for(MutableData ds:currentData.child("actualrequestsrecieved").getChildren())
                {
                    String key=ds.getKey();

                    if(key==null)return Transaction.success(currentData);
                    if(key!=null && key.contains(tenantname+"idstartsfortenant"+tenantid+"idendsfortenantforroomwithsid"+j))
                    {
                        keysToDelete.add(key);
                    }



                }
                if(keysToDelete!=null)
                {
                    for (String key : keysToDelete) {
                        currentData.child("actualrequestsrecieved").child(key).child("requestapproved").setValue(1);
                    }

                }
                else{
                    alreadycancelledbytenant=true;
                  return Transaction.abort();
                }


                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
if(!committed)
{
    if(alreadycancelledbytenant) {
        progressDialog.dismiss();
        Sendnotification notificationsSender1 = new Sendnotification(
                notifcationowner,
                "deleted",
                "request cannot be accepted as it was already deleted by the owner",
                requestsActivityforowner.this
        );
        notificationsSender1.SendNotifications();
    }
    }

if(committed)
{
    DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");

    dj.runTransaction(new Transaction.Handler() {
        @NonNull
        @Override
        public Transaction.Result doTransaction(@NonNull MutableData currentData) {

            notificationtenant=currentData.child("fcmtoken").getValue(String.class);
            if(notificationtenant==null)return Transaction.success(currentData);
            List<String> keysToDelete = new ArrayList<>();

            for(MutableData ds:currentData.child("requestsenttotheownernumbers").getChildren())
            {
                String key=ds.getKey();
                String value=ds.getValue(String.class);
                if(value==null)value="";
                if(value!=null && value.contains(j))
                {
                    keysToDelete.add(key);
                }

            }
            if(!keysToDelete.isEmpty()) {
                notificationtenant=currentData.child("fcmtoken").getValue(String.class);


                currentData.child(j).child("requestapproved").setValue(1);

                currentData.child("notificationforrequestsent").setValue(1);
            }
            else{
                alreadycancelledbytenant=true;
                return Transaction.abort();
            }





            return Transaction.success(currentData);
        }

        @Override
        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            if(!committed)
            {

                if(alreadycancelledbytenant)
                {        Sendnotification notificationsSender1 = new Sendnotification(
                        notifcationowner,
                        "deleted",
                        "request cannot be accepted as it was already deleted by the owner",
                        requestsActivityforowner.this
                );
                    notificationsSender1.SendNotifications();



                }

            }
            else if(committed) {
                Sendnotification notificationsSender = new Sendnotification(
                        notificationtenant,
                        "Accepted",
                        "Congrats!,requested accepted by owner click to pay",
                        requestsActivityforowner.this
                );



            }

        }
    });


}
            }
        });













    }*/
    Boolean alreadydeletedbytenant=false;
    Long no_of_people;
  /*  @Override
    public void itemclicked5paymentcancel(int position,String s,String j,String g) {

        Dialog dialog2=new Dialog(this);
        dialog2.setContentView(R.layout.dialogfordeleteconfirmationrequest);
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

                Dialog progressDialog = new Dialog(requestsActivityforowner.this);
                progressDialog.setCancelable(false); // Disable dismissal on touch outside

                // Inflate the custom layout
                View view2 = LayoutInflater.from(requestsActivityforowner.this).inflate(R.layout.progress_dialog_layout, null);

                // Set the custom view to the dialog
                progressDialog.setContentView(view2);

                // Show the dialog
                progressDialog.show();
                String tenantname = s.split("gapp")[0];
                String tenantid = s.split("gapp")[1];
                String ownername = j.split("idstarts")[0];
                String ownerid = j.split("idstarts")[1].split("idends")[0];
                String roomnumber = j.split("idendsroom")[1];
                String newg;

                firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser account = firebaseAuth.getCurrentUser();
                String id=account.getUid();
                timeoftheday=ownername+"+"+ownerid;
                DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner").child("rooms").child("roomId:"+j).child("priorityqueue");
                dj.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                        c = currentData.getValue(String.class);
                        if (c == null || c.isEmpty()) {
                            c = timeoftheday;
                            currentData.setValue(c);
                            Queue.add(c);
                        } else {
                            String j = c + "," + timeoftheday;
                            currentData.setValue(j);
                            Queue.add(j);

                        }
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        DatabaseReference ds=FirebaseDatabase.getInstance().getReference("deleterequestfromowner");
                        ds.runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                Long count=currentData.getChildrenCount();
                                if(count==null)count=0L;
                                count++;
                                Map<String,Object> data=new HashMap<>();
                                data.put("tenantname",tenantname);
                                data.put("tenantid",tenantid);
                                data.put("j",j);
                                data.put("ownername",ownername);
                                data.put("ownerid",ownerid);
                                data.put("notificationowner",notifcationowner);

                                currentData.child("request"+count).setValue(data);

                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                Queue.remove(timeoftheday);
                                dj.getRef().setValue(Queue);
                                Handler h=new Handler();
                                h.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(requestsActivityforowner.this, "deleted successfully", Toast.LENGTH_SHORT).show();
                                        Intent i=new Intent(requestsActivityforowner.this,Activity5.class);
                                        startActivity(i);
                                    }
                                },2000);

                            }
                        });


                    }
                });



            }




        });
        dialog2.show();



        DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");
        dj.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                notificationtenant=currentData.child("fcmtoken").getValue(String.class);
                if(notificationtenant==null)return Transaction.success(currentData);
                List<String> keysToDelete = new ArrayList<>();
                no_of_people = currentData.child(j).child("no of people equal to").getValue(Long.class);
                if (no_of_people == null)
                    return Transaction.success(currentData);

                for(MutableData ds:currentData.child("requestsenttotheownernumbers").getChildren())
                {
                    String key=ds.getKey();
                    String value=ds.getValue(String.class);
                    if(value==null)value="";
                    if(value!=null && value.contains(j))
                    {
                        keysToDelete.add(key);
                    }

                }
                if(!keysToDelete.isEmpty()) {
                    for (String key : keysToDelete) {
                        currentData.child("requestsenttotheownernumbers").child(key).setValue(null);
                    }
                    Long d = currentData.child("no of requests sent").getValue(Long.class);
                    if (d == null) return Transaction.success(currentData);
                    d--;

                    currentData.child("no of requests sent").setValue(d);
                    currentData.child(j).setValue(null);
                    return Transaction.success(currentData);
                }
                else{alreadydeletedbytenant = true;
                    return Transaction.abort();
                }







            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                if(!committed)
                {if(alreadydeletedbytenant) {


                    Sendnotification notificationsSender1 = new Sendnotification(
                            notifcationowner,
                            "deleted",
                            "already deleted by the user,so no need to delete",
                            requestsActivityforowner.this
                    );
                    notificationsSender1.SendNotifications();

                }


                }
                else if(committed)
                {        DatabaseReference dr111 = FirebaseDatabase.getInstance().getReference("google")
                        .child(ownername).child(ownerid).child("owner");

                    dr111.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                            List<String> keysToDelete = new ArrayList<>();
                            for(MutableData ds:currentData.child("actualrequestsrecieved").getChildren())
                            {
                                String key=ds.getKey();

                                if(key==null)return Transaction.success(currentData);
                                if(key!=null && key.contains(tenantname+"idstartsfortenant"+tenantid+"idendsfortenantforroomwithsid"+j))
                                {
                                    keysToDelete.add(key);
                                }



                            }
                            if(keysToDelete!=null)
                            {Log.d("no of people",String.valueOf(no_of_people));
                                for (String key : keysToDelete) {
                                    currentData.child("actualrequestsrecieved").child(key).setValue(null);
                                }
                                Long dd=currentData.child("rooms").child("roomId:"+j).child("roomcapacityreached").getValue(Long.class);
                                Log.d("no of people 2",String.valueOf(dd));
                                if(dd==null) return Transaction.success(currentData);


                                currentData.child("rooms").child("roomId:"+j).child("roomcapacityreached").setValue(dd-no_of_people);

                            }
                            else{alreadydeletedbytenant=true;
                                return Transaction.abort();
                            }

return Transaction.success(currentData);

                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            if(!committed)
                            {
                                if(alreadydeletedbytenant)
                                {

                                    Sendnotification notificationsSender1 = new Sendnotification(
                                            notifcationowner,
                                            "deleted",
                                            "already deleted by the user,so no need to delete",
                                            requestsActivityforowner.this
                                    );
                                    notificationsSender1.SendNotifications();

                                }



                            }
                            else if(committed)
                            {
                                Sendnotification notificationsSender1 = new Sendnotification(
                                        notificationtenant,
                                        "deleted",
                                        "sorry,but your request for room was rejected",
                                        requestsActivityforowner.this
                                );
                                notificationsSender1.SendNotifications();


                            }
                        }
                    });


                }








            }
        });


      DatabaseReference deleterequests=FirebaseDatabase.getInstance().getReference("deleterequestfromowner");
        deleterequests.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Long count=currentData.getChildrenCount();
                if(count==null)count=0L;
                count++;
                Map<String,Object> mp=new HashMap<>();
                mp.put("name1",tenantname);
                mp.put("id1",tenantid);
                mp.put("roomid",j);
                mp.put("madeby","owner");
                mp.put("notificationowner",notifcationowner);

                currentData.child("request"+count).setValue(mp);


                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Intent i=new Intent(requestsActivityforowner.this,Activity5.class);
startActivity(i);
            }
        });










    }*/

    @Override
    public void itemclickedforcardview1(int position,String s,String roomid)
    {
//        Toast.makeText(this, ""+"\n"+s+roomid, Toast.LENGTH_SHORT).show();

Intent i=new Intent(requestsActivityforowner.this, tenantinfo.class);
i.putExtra(tenantinfo.message,s);
i.putExtra(tenantinfo.message2,roomid);
startActivity(i);

    }
    @Override
    public void itemclickedforcardview2(int position,String s)
    {
       // Toast.makeText(this, ""+"\n"+s, Toast.LENGTH_SHORT).show();
       Intent i=new Intent(this, roominfoandbookfinal3.class);
        i.putExtra(roominfoandbookfinal3.message,s);
        startActivity(i);

    }
}