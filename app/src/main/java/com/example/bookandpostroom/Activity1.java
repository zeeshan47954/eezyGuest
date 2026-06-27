package com.example.bookandpostroom;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Activity1 extends AppCompatActivity {
    private static final String TAG = "Activity1";
    private FirebaseAuth mAuth;
    private Button signin;
    private ProgressBar pb;
    private CredentialManager credentialManager;
    private boolean signinstarted = false;
    private Executor executor;
TextView tvv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Activity started");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_1);
        applySystemBarPadding();
        // Initialize views
        tvv=findViewById(R.id.tvv);
        signin = findViewById(R.id.btnSignInusinggoogle);
        pb = findViewById(R.id.pb);
        Log.d(TAG, "onCreate: Views initialized");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "onCreate: FirebaseAuth initialized");

        // Initialize Credential Manager
        credentialManager = CredentialManager.create(this);
        executor = Executors.newSingleThreadExecutor();
        Log.d(TAG, "onCreate: CredentialManager initialized");

        // Lock orientation to portrait
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            Log.d(TAG, "onCreate: Orientation locked (already portrait)");
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            Log.d(TAG, "onCreate: Orientation forced to portrait");
        }
    }
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        ConstraintLayout scrollView = findViewById(R.id.main);

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
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Checking current user");

        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "onStart: User already signed in: " + currentUser.getEmail());
            // Optionally navigate to main activity
            // updateUI(currentUser);
        } else {
            Log.d(TAG, "onStart: No user currently signed in");
        }
    }

    public void signupusinggoogle(View view) {
        Log.d(TAG, "signupusinggoogle: Button clicked");
tvv.setText("Signing you up stand by...");
        if (signinstarted) {
            Log.w(TAG, "signupusinggoogle: Sign-in already in progress, ignoring click");
            return;
        }

        signin.setVisibility(View.GONE);
        pb.setVisibility(View.VISIBLE);
        signinstarted = true;
        Log.d(TAG, "signupusinggoogle: UI updated, starting sign-in");

        signInWithGoogle();
    }

    private void signInWithGoogle() {
        Log.d(TAG, "signInWithGoogle: Preparing Google Sign-In request");

        // Get Web Client ID from resources
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

        // Configure Google ID option with better settings
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Show all accounts
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false) // Let user choose account
                .setNonce(null) // Remove nonce if you're not using it
                .build();

        Log.d(TAG, "signInWithGoogle: Google ID option configured");

        // Build credential request
        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .setPreferImmediatelyAvailableCredentials(false) // Important: allow account selection
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
                                tvv.setText("signup success\uD83C\uDF89");
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
                                tvv.setText("signup failed \uD83D\uDE22");
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

                    // Get proper user information
                    String id = googleIdTokenCredential.getId();
                    String displayName = googleIdTokenCredential.getDisplayName();

                    // Extract email properly
                    String email = extractEmail(googleIdTokenCredential, id);

                    Log.d(TAG, "handleSignInResult: Google Sign-In successful");
                    Log.d(TAG, "handleSignInResult: ID=" + id);
                    Log.d(TAG, "handleSignInResult: Display Name=" + displayName);
                    Log.d(TAG, "handleSignInResult: Email=" + email);
                    Log.d(TAG, "handleSignInResult: ID Token present=" + (idToken != null));

                    if (idToken == null) {
                        Log.e(TAG, "handleSignInResult: ID Token is null!");
                        Toast.makeText(this, "Sign-in failed: No ID token received", Toast.LENGTH_SHORT).show();
                        restoreUI();
                        return;
                    }

                    // Authenticate with Firebase
                    firebaseAuthWithGoogle(idToken, displayName, id, email);


            } else {
                Log.e(TAG, "handleSignInResult: Unexpected credential type: " + credential.getType());
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
        // Try to get email using reflection since the method might not be available in all versions
        try {
            // Try to call getEmail() method if it exists
            java.lang.reflect.Method getEmailMethod = googleIdTokenCredential.getClass().getMethod("getEmail");
            String email = (String) getEmailMethod.invoke(googleIdTokenCredential);
            if (email != null && !email.isEmpty()) {
                return email;
            }
        } catch (Exception e) {
            Log.d(TAG, "getEmail method not available, using fallback");
        }

        // Fallback: use ID as email or try to extract from display name
        if (fallbackId != null && fallbackId.contains("@")) {
            return fallbackId;
        }

        // If ID is not an email, create a placeholder
        return fallbackId + "@gmail.com";
    }

    private void handleSignInError(GetCredentialException e) {
        Log.e(TAG, "handleSignInError: Exception occurred", e);
        Log.e(TAG, "handleSignInError: Exception type: " + e.getClass().getSimpleName());
        Log.e(TAG, "handleSignInError: Exception message: " + e.getMessage());

        String errorMessage;
        String exceptionType = e.getClass().getSimpleName();

        // Use instanceof for more reliable exception type checking
        if (e instanceof androidx.credentials.exceptions.GetCredentialCancellationException) {
            Log.d(TAG, "handleSignInError: User cancelled sign-in");
            // Don't show Toast for cancellation - it's a normal user action
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
            Log.e(TAG, "handleSignInError: Unhandled exception type: " + exceptionType);
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        restoreUI();
    }

    private void firebaseAuthWithGoogle(String idToken, String name, String id, String email) {
        Log.d(TAG, "firebaseAuthWithGoogle: Starting Firebase authentication");

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Log.d(TAG, "firebaseAuthWithGoogle: Credential created");

        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "firebaseAuthWithGoogle: Authentication successful");
                    FirebaseUser user = mAuth.getCurrentUser();

                    if (user != null) {
                        Log.d(TAG, "firebaseAuthWithGoogle: Firebase user=" + user.getEmail());
                        Log.d(TAG, "firebaseAuthWithGoogle: Firebase UID=" + user.getUid());

                        // Use Firebase user data which is more reliable
                        String firebaseName = user.getDisplayName() != null ? user.getDisplayName() : name;
                        String firebaseEmail = user.getEmail() != null ? user.getEmail() : email;
                        String firebaseId = user.getUid();

                        // Check if user exists in database
                        checkUserAndProceed(firebaseName, firebaseId, firebaseEmail);
                    } else {
                        Log.e(TAG, "firebaseAuthWithGoogle: User is null after auth");
                        Toast.makeText(Activity1.this, "Authentication failed: User is null",
                                Toast.LENGTH_SHORT).show();
                        restoreUI();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "firebaseAuthWithGoogle: Authentication failed", e);
                    Toast.makeText(Activity1.this, "Firebase authentication failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    restoreUI();
                });
    }

    private void checkUserAndProceed(String name, String id, String email) {
        Log.d(TAG, "checkUserAndProceed: Checking if user exists in database");
        Log.d(TAG, "checkUserAndProceed: Path=google/" + name + "/" + id);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("google");
        rootRef.child(name).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "checkUserAndProceed.onDataChange: Snapshot received");

                if (snapshot.exists()) {
                    Log.d(TAG, "checkUserAndProceed.onDataChange: User already exists in database");
                    Toast.makeText(Activity1.this, "Account already exists", Toast.LENGTH_SHORT).show();

                    // Sign out from Firebase
                    mAuth.signOut();
                    Log.d(TAG, "checkUserAndProceed.onDataChange: Signed out from Firebase");

                    restoreUI();
                } else {
                    Log.d(TAG, "checkUserAndProceed.onDataChange: User does not exist, saving data");
                    saveUserDataAndProceed(name, id, email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "checkUserAndProceed.onCancelled: Database error", error.toException());
                Log.e(TAG, "checkUserAndProceed.onCancelled: Error code=" + error.getCode());
                Log.e(TAG, "checkUserAndProceed.onCancelled: Error message=" + error.getMessage());

                Toast.makeText(Activity1.this, "Database error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
                restoreUI();
            }
        });
    }

    private void saveUserDataAndProceed(String name, String id, String email) {
        Log.d(TAG, "saveUserDataAndProceed: Saving user data to database");
        Log.d(TAG, "saveUserDataAndProceed: Name=" + name + ", ID=" + id + ", Email=" + email);
        Intent intent = new Intent(Activity1.this, Activity2.class);
        startActivity(intent);
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(name)
                .child(id);

        // Create user data object
        UserData userData = new UserData(name, email, id);

//        userRef.setValue(userData)
//                .addOnSuccessListener(aVoid -> {
//                    Log.d(TAG, "saveUserDataAndProceed: User data saved successfully");
//                    Log.d(TAG, "saveUserDataAndProceed: Navigating to Activity2");
//                    Toast.makeText(this, "logged in successfullly", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(Activity1.this, Activity2.class);
//                    intent.putExtra("USER_NAME", name);
//                    intent.putExtra("USER_ID", id);
//                    intent.putExtra("USER_EMAIL", email);
//
//                    startActivity(intent);
//                    finish();
//                    Log.d(TAG, "saveUserDataAndProceed: Activity1 finished");
//                })
//                .addOnFailureListener(e -> {
//                    Log.e(TAG, "saveUserDataAndProceed: Failed to save user data", e);
//                    Toast.makeText(Activity1.this, "Failed to save user data: " + e.getMessage(),
//                            Toast.LENGTH_SHORT).show();
//                    restoreUI();
//                });
    }

    private void restoreUI() {
        Log.d(TAG, "restoreUI: Restoring UI to initial state");
        runOnUiThread(() -> {
            signin.setVisibility(View.VISIBLE);
            pb.setVisibility(View.GONE);
            signinstarted = false;
        });
    }

    // Helper class for user data
    public static class UserData {
        public String name;
        public String email;
        public String id;

        public UserData() {
            // Default constructor required for Firebase
        }

        public UserData(String name, String email, String id) {
            this.name = name;
            this.email = email;
            this.id = id;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity being destroyed");
    }
}
/*package com.example.bookandpostroom;

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
*/
