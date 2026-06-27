package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity2 extends AppCompatActivity {
Intent i;
private long j=0;
    String name;
    String id;

List<String>captions=new ArrayList<>();
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_2);
        applySystemBarPadding();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        name=account.getDisplayName();
        id=account.getUid();





    }
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
 public void roomsystem(View view)
    {


        Dialog dd=new Dialog(this);
        dd.setContentView(R.layout.progress_dialog_layout);
        dd.setCancelable(false);
        dd.show();




            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");





            Map<String, Object> ownerData = new HashMap<>();




            ownerData.put("profilecreated",j);

            ownerData.put("transactionsmade",j);
            ownerData.put("transactionsrecieved",j);
dbr.runTransaction(new Transaction.Handler() {
    @NonNull
    @Override
    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

      currentData.setValue(ownerData);
        return Transaction.success(currentData);
    }

    @Override
    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
        getFCMToken("student",dd);
    }
});




















    }

    public void logout(View view)
    {
        firebaseAuth.signOut();
        Intent intent = new Intent(Activity2.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    void getFCMToken(String division,Dialog dd)
    {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {


                DatabaseReference dbr=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child(division).child("fcmtoken");
               dbr.runTransaction(new Transaction.Handler() {
                   @NonNull
                   @Override
                   public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                     currentData.setValue(s);
                       return Transaction.success(currentData);
                   }

                   @Override
                   public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                      dd.dismiss();
                       if(division.equals("student"))
                       {
                           i=new Intent(Activity2.this,studentinfoandrooms.class);
                           startActivity(i);
                           finish();
                       }
                       else{

                           i = new Intent(Activity2.this, Activity4.class);
                           startActivity(i);
                           finish();
                       }
                   }
               });

            }
        })  ;





    }

    public void ownersystem(View view)
    {



        Dialog dd=new Dialog(this);
        dd.setContentView(R.layout.progress_dialog_layout);
        dd.setCancelable(false);
        dd.show();






            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");

            Map<String, Object> ownerData = new HashMap<>();


            ownerData.put("roomsexplored", j);


            ownerData.put("notificationowner", j);
            ownerData.put("notificationtenant",j);
            ownerData.put("roomsposted",j);
            ownerData.put("profilecreated",j);
            ownerData.put("accountsetup",j);
            ownerData.put("accountverified",j);


            ownerData.put("transactionsrecieved",j);
            ownerData.put("transactionsmade",j);
        dbr.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                currentData.setValue(ownerData);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                getFCMToken("owner",dd);
            }
        });




    }



}