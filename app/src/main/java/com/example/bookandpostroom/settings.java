package com.example.bookandpostroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class settings extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    String name;
    String id;
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
        setContentView(R.layout.activity_settings);
        applySystemBarPadding();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser acct = firebaseAuth.getCurrentUser();
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

name=acct.getDisplayName();
id=acct.getUid();


    }


    public void logout(View view)
    {firebaseAuth.signOut();
        Intent intent = new Intent(settings.this, MainActivity.class);
        startActivity(intent);
        finish();


    }
    public void deleteData(android.view.View view) {
        Dialog dd=new Dialog(this);
        dd.setContentView(R.layout.progress_dialog_layout);
        dd.setCancelable(false);
        dd.show();
        DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("requestsenttotheownernumbers");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChildren())
                {
                    dd.dismiss();
                    Snackbar.make(findViewById(android.R.id.content),
                            "Deletion not possible right now. Please wait for the previous request or leave the current room",
                            Snackbar.LENGTH_LONG).show();

                }
                else{

                    dd.dismiss();
                    SpannableString title = new SpannableString("Delete Data");
                    title.setSpan(new ForegroundColorSpan(Color.RED), 0, title.length(), 0);
                    SpannableString ok = new SpannableString("Yes");
                    title.setSpan(new ForegroundColorSpan(Color.RED), 0, title.length(), 0);
                    // Message in red
                    SpannableString message = new SpannableString("Are you sure you want to delete your data? This action cannot be undone.");
                    message.setSpan(new ForegroundColorSpan(Color.RED), 0, message.length(), 0);
                    new AlertDialog.Builder(settings.this)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(ok, (dialog, which) -> {
                                // Delete user data from Firebase Database
                                DatabaseReference ds= FirebaseDatabase.getInstance().getReference("google").child(name).child(id);
                                ds.removeValue();
                                firebaseAuth.signOut();
                                Toast.makeText(settings.this, "Deleted all the info", Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(settings.this,MainActivity.class);
                                startActivity(i);

                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // ------------------- RATE US -------------------
    public void rateUs(android.view.View view) {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            // Fallback if Play Store is not installed
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

}