package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {
    String roomname;
    String name;
    String id;
    private FirebaseAuth firebaseAuth;
    String realname;
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
       ScrollView scrollView = findViewById(R.id.main);

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
        setContentView(R.layout.activity_review);
        applySystemBarPadding();
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account != null) {
            name = account.getDisplayName();
            id = account.getUid();
        }

        DatabaseReference ds= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                realname=snapshot.child("name").getValue(String.class);
                for(DataSnapshot ds:snapshot.child("requestsenttotheownernumbers").getChildren())
                {
                   roomname=ds.getValue(String.class);
                   break;
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        RatingBar ratingBar = findViewById(R.id.review_rating_bar);
        EditText reviewInput = findViewById(R.id.review_input);
        Button submitBtn = findViewById(R.id.submit_review_btn);

        submitBtn.setOnClickListener(v -> {
            float rating = ratingBar.getRating();
            String review = reviewInput.getText().toString().trim();

            if (review.isEmpty()) {
                Toast.makeText(this, "Please write a review", Toast.LENGTH_SHORT).show();
            } else {

                Dialog dd=new Dialog(this);
                dd.setContentView(R.layout.progress_dialog_layout);
                dd.setCancelable(false);
                dd.show();
          String ownername=roomname.split("idstarts")[0];
          String ownerid=roomname.split("idstarts")[1].split("idends")[0];
          DatabaseReference sd=FirebaseDatabase.getInstance().getReference("google").child(ownername)
                  .child(ownerid).child("owner").child("rooms").child("roomId:"+roomname).child("reviews");
          sd.runTransaction(new Transaction.Handler() {
              @NonNull
              @Override
              public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                  Float totalScore = currentData.child("totalScore").getValue(Float.class);
                  Long ratingCount = currentData.child("ratingCount").getValue(Long.class);


                  if (totalScore == null) totalScore = 0f;
                  if (ratingCount == null) ratingCount = 0L;

// Add the new rating
                  totalScore += rating;
                  ratingCount += 1;

// Compute new average
                  float average = Math.min(5f, Math.max(0f, totalScore / ratingCount));


// Save all three values back to the database
                  currentData.child("totalScore").setValue(totalScore);
                  currentData.child("ratingCount").setValue(ratingCount);
                  currentData.child("averageScore").setValue(average);

           Long kk=   currentData.child("actualreview").getChildrenCount();
           if(kk==null)kk=0L;
           currentData.child("actualreview").child("review"+kk+":"+realname).child("review").setValue(review);
                  currentData.child("actualreview").child("review"+kk+":"+realname).child("rating").setValue(rating);
                  Date currentTime = Calendar.getInstance().getTime();
                  SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                  String formattedDate = sdf.format(currentTime);
                  currentData.child("actualreview").child("review"+kk+":"+realname).child("dated").setValue(formattedDate);





                  return Transaction.success(currentData);
              }

              @Override
              public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                  Toast.makeText(ReviewActivity.this, "review added successfully", Toast.LENGTH_SHORT).show();
                  Intent i=new Intent(ReviewActivity.this,studentinfoandrooms.class);
                  startActivity(i);
                  finish();
              }
          });


            }
        });




    }


}