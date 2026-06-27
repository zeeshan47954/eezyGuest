package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_requests_activityforowner);
String jj=getIntent().getStringExtra(message);

        Bundle bundle = new Bundle();
        bundle.putString("valuepassed", jj);

        fragmentforhome5 fragment = new fragmentforhome5();
        fragment.setArguments(bundle);

// Load the fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(this);
        String ownername=account.getDisplayName();
        String ownerid=account.getId();
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

    @Override
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




      /*DatabaseReference dr111 = FirebaseDatabase.getInstance().getReference("google")
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





*/







    }
    Boolean alreadydeletedbytenant=false;
    Long no_of_people;
    @Override
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
        dialog2.show();



     /*   DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(tenantname).child(tenantid).child("student");
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
        });*/


     /*   DatabaseReference deleterequests=FirebaseDatabase.getInstance().getReference("deleterequestfromowner");
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
        });*/
     /*   */









    }

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