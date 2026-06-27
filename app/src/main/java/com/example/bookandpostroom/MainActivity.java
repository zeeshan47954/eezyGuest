
package com.example.bookandpostroom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

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
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
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
    private FirebaseAuth firebaseAuth;
    private CredentialManager credentialManager;
    private Executor executor;
    TextView tv;
    TextView tvv;
    private boolean signinstarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity started");
        setTheme(R.style.Theme_YourApp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        lottielayout = findViewById(R.id.lottielayout);
        mainlinearlayout = findViewById(R.id.cv1);
tvv=findViewById(R.id.tvv);
        // Lock orientation to portrait
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            Log.d(TAG, "onCreate: Orientation locked (already portrait)");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.d(TAG, "onCreate: Orientation forced to portrait");
        }

        firebaseAuth = FirebaseAuth.getInstance();
        credentialManager = CredentialManager.create(this);
        executor = Executors.newSingleThreadExecutor();
        Log.d(TAG, "onCreate: Firebase and CredentialManager initialized");

        FirebaseUser account = firebaseAuth.getCurrentUser();

        if (account != null) {
            Log.d(TAG, "onCreate: User already signed in");
            // Get user's info
            String name = account.getDisplayName();
            String id = account.getUid();

            DatabaseReference rootref1 = FirebaseDatabase.getInstance().getReference("google").child(name).child(id);

            rootref1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "onCreate.onDataChange: Checking user data");

                    if (snapshot.hasChild("owner") && !snapshot.hasChild("student")) {
                        Log.d(TAG, "onCreate.onDataChange: User is owner");
                        getFCMToken(rootref1, "owner");
                    } else if (!snapshot.hasChild("owner") && snapshot.hasChild("student")) {
                        Log.d(TAG, "onCreate.onDataChange: User is student");
                        getFCMToken(rootref1, "student");
                    } else if (!snapshot.hasChild("owner") && !snapshot.hasChild("student")) {
                        Log.d(TAG, "onCreate.onDataChange: User has no role,please sign up with a different account, signing out");
                        firebaseAuth.signOut();
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCreate.onCancelled: Database error", error.toException());
                }
            });
        } else {
            Log.d(TAG, "onCreate: No user signed in, showing UI");
            lottielayout.setVisibility(View.GONE);
            mainlinearlayout.setVisibility(View.VISIBLE);
            EdgeToEdge.enable(this);

            signin = findViewById(R.id.btnSignInusinggoogle);
            signin.setBackgroundResource(R.drawable.fancy_button_background);
            pb = findViewById(R.id.pb);
            TextView tvSignUp = findViewById(R.id.tvSignUp);

            // Create a SpannableString
            SpannableString spannableString = new SpannableString("new user? Sign up");

            // Create a clickable span for the "Sign up" part
            ClickableSpan signUpClickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Log.d(TAG, "SignUp clicked");
                    Intent i = new Intent(MainActivity.this, Activity1.class);
                    startActivity(i);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                    ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.teal));
                }
            };

            // Set the clickable span only for the "Sign up" text
            spannableString.setSpan(signUpClickableSpan, 10, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set the spannable string to the TextView
            tvSignUp.setText(spannableString);
            tvSignUp.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.black));
            tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void signinusinggoogle(View view) {
        Log.d(TAG, "signinusinggoogle: Button clicked");
tvv.setText("signing you in stand by...");
        if (signinstarted) {
            Log.w(TAG, "signinusinggoogle: Sign-in already in progress");
            return;
        }

        tv = findViewById(R.id.tvSignUp);
        tv.setVisibility(View.GONE);
        signin.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        signinstarted = true;
        Log.d(TAG, "signinusinggoogle: UI updated, starting sign-in");

        signInWithGoogle();
    }

    private void signInWithGoogle() {
        Log.d(TAG, "signInWithGoogle: Preparing Google Sign-In request");

        // Get Web Client ID
        String webClientId;
        try {
            webClientId = "199022863046-1nal6r857ps7ferkhckpj8p6f6r7t36d.apps.googleusercontent.com";
            Log.d(TAG, "signInWithGoogle: Web Client ID loaded");
        } catch (Exception e) {
            Log.e(TAG, "signInWithGoogle: Failed to load Web Client ID", e);
            Toast.makeText(this, "Configuration error: Web Client ID not found", Toast.LENGTH_LONG).show();
            restoreUI();
            return;
        }

        // Configure Google ID option
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .setNonce(null)
                .build();

        Log.d(TAG, "signInWithGoogle: Google ID option configured");

        // Build credential request
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .setPreferImmediatelyAvailableCredentials(false)
                .build();

        Log.d(TAG, "signInWithGoogle: Credential request built");

        // Request credentials
        credentialManager.getCredentialAsync(
                this,
                request,
                null,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                    @Override
                    public void onResult(GetCredentialResponse result) {
                        Log.d(TAG, "onResult: Credential received");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvv.setText("signin success\uD83C\uDF89");
                            }
                        });

                        runOnUiThread(() -> handleSignInResult(result));
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e(TAG, "onError: Credential request failed", e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvv.setText("sigin failed \uD83D\uDE22");
                            }
                        });

                        runOnUiThread(() -> handleSignInError(e));
                    }
                }
        );
    }

    private void handleSignInResult(GetCredentialResponse result) {
        Log.d(TAG, "handleSignInResult: Processing credential response");

        Credential credential = result.getCredential();
        Log.d(TAG, "handleSignInResult: Credential type: " + credential.getType());

        if (credential instanceof CustomCredential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                // Extract Google ID Token credential
                GoogleIdTokenCredential googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());

                String idToken = googleIdTokenCredential.getIdToken();
                String id = googleIdTokenCredential.getId();
                String displayName = googleIdTokenCredential.getDisplayName();
                String email = extractEmail(googleIdTokenCredential, id);

                Log.d(TAG, "handleSignInResult: Google Sign-In successful");
                Log.d(TAG, "handleSignInResult: ID=" + id);
                Log.d(TAG, "handleSignInResult: Display Name=" + displayName);
                Log.d(TAG, "handleSignInResult: Email=" + email);

                if (idToken == null) {
                    Log.e(TAG, "handleSignInResult: ID Token is null!");
                    Toast.makeText(this, "Sign-in failed: No ID token received", Toast.LENGTH_SHORT).show();
                    restoreUI();
                    return;
                }

                // Authenticate with Firebase
                firebaseAuthWithGoogle(idToken, displayName, id, email);
            } else {
                Log.e(TAG, "handleSignInResult: Unexpected credential type");
                Toast.makeText(this, "Unexpected credential type", Toast.LENGTH_SHORT).show();
                restoreUI();
            }
        } else {
            Log.e(TAG, "handleSignInResult: Credential is not CustomCredential");
            Toast.makeText(this, "Invalid credential format", Toast.LENGTH_SHORT).show();
            restoreUI();
        }
    }

    private String extractEmail(GoogleIdTokenCredential googleIdTokenCredential, String fallbackId) {
        try {
            java.lang.reflect.Method getEmailMethod = googleIdTokenCredential.getClass().getMethod("getEmail");
            String email = (String) getEmailMethod.invoke(googleIdTokenCredential);
            if (email != null && !email.isEmpty()) {
                return email;
            }
        } catch (Exception e) {
            Log.d(TAG, "getEmail method not available, using fallback");
        }

        if (fallbackId != null && fallbackId.contains("@")) {
            return fallbackId;
        }

        return fallbackId + "@gmail.com";
    }

    private void handleSignInError(GetCredentialException e) {
        Log.e(TAG, "handleSignInError: Exception occurred", e);
        Log.e(TAG, "handleSignInError: Exception type: " + e.getClass().getSimpleName());
        Log.e(TAG, "handleSignInError: Exception message: " + e.getMessage());

        String errorMessage;

        if (e instanceof androidx.credentials.exceptions.GetCredentialCancellationException) {
            Log.d(TAG, "handleSignInError: User cancelled sign-in");
            restoreUI();
            return;
        } else if (e instanceof androidx.credentials.exceptions.NoCredentialException) {
            errorMessage = "No Google accounts found on device";
            Log.e(TAG, "handleSignInError: No credentials available");
        } else if (e instanceof androidx.credentials.exceptions.GetCredentialInterruptedException) {
            errorMessage = "Sign-in interrupted";
            Log.e(TAG, "handleSignInError: Sign-in interrupted");
        } else if (e instanceof androidx.credentials.exceptions.GetCredentialUnknownException) {
            errorMessage = "Unknown error occurred";
            Log.e(TAG, "handleSignInError: Unknown exception");
        } else {
            errorMessage = "Sign-in failed: " + e.getMessage();
            Log.e(TAG, "handleSignInError: Unhandled exception type");
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        restoreUI();
    }

    private void firebaseAuthWithGoogle(String idToken, String name, String id, String email) {
        Log.d(TAG, "firebaseAuthWithGoogle: Starting Firebase authentication");

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Log.d(TAG, "firebaseAuthWithGoogle: Credential created");

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "firebaseAuthWithGoogle: Authentication successful");
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user != null) {
                        Log.d(TAG, "firebaseAuthWithGoogle: Firebase user=" + user.getEmail());
                        Log.d(TAG, "firebaseAuthWithGoogle: Firebase UID=" + user.getUid());

                        String firebaseName = user.getDisplayName() != null ? user.getDisplayName() : name;
                        String firebaseEmail = user.getEmail() != null ? user.getEmail() : email;
                        String firebaseId = user.getUid();

                        updateUI(user);
                    } else {
                        Log.e(TAG, "firebaseAuthWithGoogle: User is null after auth");
                        Toast.makeText(MainActivity.this, "Authentication failed: User is null",
                                Toast.LENGTH_SHORT).show();
                        restoreUI();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "firebaseAuthWithGoogle: Authentication failed", e);
                    Toast.makeText(MainActivity.this, "Firebase authentication failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    restoreUI();
                });
    }

    private void updateUI(FirebaseUser account) {
        if (account != null) {
            Log.d(TAG, "updateUI: Updating UI for user");
            String name = account.getDisplayName();
            String id = account.getUid();

            Log.d("id",id);

            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("google").child(name).child(id);
            rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d(TAG, "updateUI.onDataChange: Checking user data");

                    if (snapshot.exists()) {
                        if (snapshot.hasChild("owner") && snapshot.hasChild("student")) {
                            Log.d(TAG, "updateUI.onDataChange: User has both roles");
                            // Handle both roles scenario
                        } else if (snapshot.hasChild("owner") && !snapshot.hasChild("student")) {
                            Log.d(TAG, "updateUI.onDataChange: User is owner");
                            getFCMToken(rootRef, "owner");
                        } else if (!snapshot.hasChild("owner") && snapshot.hasChild("student")) {
                            Log.d(TAG, "updateUI.onDataChange: User is student");
                            getFCMToken(rootRef, "student");
                        } else if (!snapshot.hasChild("owner") && !snapshot.hasChild("student")) {
                            Log.d(TAG, "updateUI.onDataChange: User has no role");
                            firebaseAuth.signOut();
                            Intent i = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Log.d(TAG, "updateUI.onDataChange: User not found in database");
                        Toast.makeText(MainActivity.this, "no account found please sign up", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        restoreUI();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "updateUI.onCancelled: Database error", error.toException());
                }
            });
        }
    }

    void getFCMToken(DatabaseReference rootref, String type) {
        Log.d(TAG, "getFCMToken: Getting FCM token for type=" + type);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "getFCMToken.onSuccess: Token received");

                rootref.child(type).child("fcmtoken").setValue(s).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "getFCMToken.onSuccess: Token saved, navigating");

                        if (type.equals("owner")) {
                            Intent i = new Intent(MainActivity.this, Activity5.class);
                            startActivity(i);
                            finish();
                        } else if (type.equals("student")) {
                            Intent i = new Intent(MainActivity.this, studentinfoandrooms.class);
                            startActivity(i);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getFCMToken.onFailure: Failed to save token", e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "getFCMToken.onFailure: Failed to get token", e);
            }
        });
    }

    private void restoreUI() {
        Log.d(TAG, "restoreUI: Restoring UI to initial state");
        runOnUiThread(() -> {
            tv.setVisibility(View.VISIBLE);
            signin.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            signinstarted = false;
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity being destroyed");
    }
}

