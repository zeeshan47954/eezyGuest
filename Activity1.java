package com.example.bookandpostroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class  Activity1 extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Button signin;
    private ProgressBar pb;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        signin=findViewById(R.id.btnSignInusinggoogle);
        pb=findViewById(R.id.pb);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    boolean signinstarted=false;
    public void signinusinggoogle(View view) {
        signin.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        signinstarted=true;
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String email = account.getEmail();
                String id = account.getId();
                String name = account.getDisplayName();
                checkUserAndProceed(name, id, email);
            }
        } catch (ApiException e) {

            if (e.getStatusCode() == com.google.android.gms.common.api.CommonStatusCodes.CANCELED) {
                Toast.makeText(this, "Sign-in canceled", Toast.LENGTH_SHORT).show();
            } else {
                Log.w("GoogleSignIn", "signInResult:failed code=" + e.getStatusCode());
                Toast.makeText(this, "Sign-in failed", Toast.LENGTH_SHORT).show();
            }

            // Restore UI
            signin.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            signinstarted = false;
        }
    }

    private void checkUserAndProceed(String name, String id, String email) {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("google");
        rootRef.child(name).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(Activity1.this, "Account already exists", Toast.LENGTH_SHORT).show();

                    mGoogleSignInClient.signOut();
                    signin.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                } else {
                    saveUserDataAndProceed(name, id, email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void saveUserDataAndProceed(String name, String id, String email) {

        startActivity(new Intent(Activity1.this, Activity2.class));
        finish();
    }



}

