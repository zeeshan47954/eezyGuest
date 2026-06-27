package com.example.bookandpostroom;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private EditText et;
    private EditText email;
    private EditText password;
    private long l;
    private long m;
    private long kk;
    private long ps;
    private int x;
    private AtomicInteger a = new AtomicInteger(0);
    private Button signin;
    private boolean allexist = false;
    private ProgressBar pb;

    private ConstraintLayout lottielayout;
    private CardView mainlinearlayout;
    private static final int RC_SIGN_IN = 9001;  // Request code for sign-in
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth firebaseAuth;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        lottielayout = findViewById(R.id.lottielayout);
        mainlinearlayout = findViewById(R.id.cv1);


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            // Get user's email
            String name = account.getDisplayName();
            String id = account.getId();




            mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);

            DatabaseReference rootref1 = FirebaseDatabase.getInstance().getReference("google").child(name).child(id);


            rootref1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    if (snapshot.hasChild("owner") && !snapshot.hasChild("student")) {

                        getFCMToken(rootref1,"owner");




                    } else if (!snapshot.hasChild("owner") && snapshot.hasChild("student")) {
                        getFCMToken(rootref1,"student");



                    } else if (!snapshot.hasChild("owner") && !snapshot.hasChild("student")) {

                        mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                Intent i = new Intent(MainActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {


                            }
                        });

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {


            lottielayout.setVisibility(View.GONE);
            mainlinearlayout.setVisibility(View.VISIBLE);
            EdgeToEdge.enable(this);
            // et = findViewById(R.id.Email);  // Move this line here
            signin = findViewById(R.id.btnSignInusinggoogle);
            pb = findViewById(R.id.pb);


            TextView tvSignUp = findViewById(R.id.tvSignUp);

            // Create a SpannableString
            SpannableString spannableString = new SpannableString("new user? Sign up");

            // Create a clickable span for the "Sign up" part
            ClickableSpan signUpClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // Handle the sign-up click action
                    Intent i = new Intent(MainActivity.this, Activity1.class);
                    startActivity(i);

                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false); // Optional: Remove underline
                    ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.teal)); // Change color if needed
                }
            };

            // Set the clickable span only for the "Sign up" text
            spannableString.setSpan(signUpClickableSpan, 10, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the spannable string to the TextView
            tvSignUp.setText(spannableString);
            tvSignUp.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.black));

            tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());


            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()  // Request user's email ID
                    .build();

            // Build a GoogleSignInClient with the options specified by gso
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        }
    }









    boolean signinstarted = false;

    public void signinusinggoogle(View view) {

         tv=findViewById(R.id.tvSignUp);
        tv.setVisibility(View.GONE);
        signin.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        signinstarted = true;

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);  // Handle the result of the sign-in attempt
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI
            updateUI(account);
        } catch (ApiException e) {

            if (e.getStatusCode() == com.google.android.gms.common.api.CommonStatusCodes.CANCELED) {
                Toast.makeText(this, "Sign-in canceled", Toast.LENGTH_SHORT).show();
            } else {
                Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show();
            }

            // Restore UI
            tv.setVisibility(View.VISIBLE);
            signin.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            signinstarted = false;
        }
    }


    private void updateUI(GoogleSignInAccount account) {
        if (account != null) {

            String name = account.getDisplayName();
            String id = account.getId();

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("google").child(name).child(id);
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        if (snapshot.hasChild("owner") && snapshot.hasChild("student")) {

                        } else if (snapshot.hasChild("owner") && !snapshot.hasChild("student")) {
                            getFCMToken(rootRef,"owner");






                        } else if (!snapshot.hasChild("owner") && snapshot.hasChild("student")) {
                            getFCMToken(rootRef,"student");

                        } else if (!snapshot.hasChild("owner") && !snapshot.hasChild("student")) {

                            mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Intent i = new Intent(MainActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {


                                }
                            });

                        }


                    } else {

                        Toast.makeText(MainActivity.this, "no account found please sign up", Toast.LENGTH_SHORT).show();

                        mGoogleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                tv.setVisibility(View.VISIBLE);
                                signin.setVisibility(View.VISIBLE);
                                pb.setVisibility(View.GONE);
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    void getFCMToken(DatabaseReference rootref, String type) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
    rootref.child(type).child("fcmtoken").setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        if(type.equals("owner"))
                        {Intent i = new Intent(MainActivity.this, Activity5.class);
                            startActivity(i);
                            finish();


                        }
                        else if(type.equals("student"))
                        {Intent i = new Intent(MainActivity.this, studentinfoandrooms.class);
                            startActivity(i);
                            finish();


                        }

                    }
                });

            }
        });


    }
}