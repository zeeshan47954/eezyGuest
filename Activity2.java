package com.example.bookandpostroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity2 extends AppCompatActivity {
Intent i;
private long j=0;
List<String>captions=new ArrayList<>();
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        setContentView(R.layout.activity_2);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        String name=account.getDisplayName();
        String id=account.getId();





    }

 public void roomsystem(View view)
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);



        if(account!=null) {
            String name=account.getDisplayName();
            String id=account.getId();

            DatabaseReference dbr = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");





            Map<String, Object> ownerData = new HashMap<>();




            ownerData.put("profilecreated",j);

            ownerData.put("transactionsmade",j);
            ownerData.put("transactionsrecieved",j);


            dbr.updateChildren(ownerData).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                      public void onSuccess(Void unused) {
                    getFCMToken(name,id,"student");
                    i=new Intent(Activity2.this,studentinfoandrooms.class);
                    startActivity(i);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Activity2.this, "sorry ,network problem,try logging in again", Toast.LENGTH_SHORT).show();
                }
            });















        }


    }

    public void logout(View view)
    {
        mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Intent intent = new Intent(Activity2.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(Activity2.this, "sorry ,an error occured while sigining out", Toast.LENGTH_SHORT).show();
            }
        });

    }
    void getFCMToken(String name,String id,String division)
    {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                DatabaseReference dbr=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child(division).child("fcmtoken");
                dbr.setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });

            }
        })  ;





    }

    public void ownersystem(View view)
    {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);



        if(account!=null) {
            String name=account.getDisplayName();
            String id=account.getId();

            DatabaseReference fd=FirebaseDatabase.getInstance().getReference("google").child(name).child(id);

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
           dbr.updateChildren(ownerData).addOnSuccessListener(new OnSuccessListener<Void>() {
               @Override
               public void onSuccess(Void unused) {
                   getFCMToken(name,id,"owner");
                   i = new Intent(Activity2.this, Activity4.class);
                   startActivity(i);
                   finish();
               }
           }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   Toast.makeText(Activity2.this, "sorry ,network problem,try logging in again", Toast.LENGTH_SHORT).show();
               }
           });















        }

    }
    @Override
    public void onBackPressed() {
        // Custom behavior
        mGoogleSignInClient.signOut();
        super.onBackPressed();
    }


}