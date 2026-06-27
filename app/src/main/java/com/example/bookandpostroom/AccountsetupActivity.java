package com.example.bookandpostroom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AccountsetupActivity extends AppCompatActivity {
    EditText accountnotextview, beneficiarytextview, ifsctextview;
    String email, ownername, ownerid;
    private FirebaseAuth firebaseAuth;

String accountnumber,ifsccode,beneficiaryname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetup);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        applySystemBarPadding();
        Button bb=findViewById(R.id.acb);
        bb.setBackgroundResource(R.drawable.backgroundshape1);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();

        ownername = account.getDisplayName();
        ownerid = account.getUid();
        email = account.getEmail();

        accountnotextview = findViewById(R.id.accountno);
        beneficiarytextview= findViewById(R.id.beneficiary);

        ifsctextview = findViewById(R.id.ifsc);






    }
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        NestedScrollView scrollView = findViewById(R.id.nestedScrollView);

        // Apply padding to avoid system bars
        scrollView.setPadding(
                0,
                0,  // Top padding for status bar
                0,
                navBarHeight      // Bottom padding for navigation bar
        );

        // Optional: Log the values for debugging

    }

    public void submit(View view) {

        accountnumber = accountnotextview.getText().toString();

        beneficiaryname = beneficiarytextview.getText().toString();

        ifsccode = ifsctextview.getText().toString();




        if (accountnumber.isEmpty() || beneficiaryname.isEmpty() || ifsccode.isEmpty()) {
            Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
        } else {
           saveToFirebase();
        }
    }


    HashMap<String, Object> data = new HashMap<>();


    private void saveToFirebase() {
       Dialog pd1=new Dialog(this);
        pd1.setCancelable(false);
        pd1.setContentView(R.layout.progress_dialog_layout);
        pd1.show();

        DatabaseReference dff1=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(ownerid).child("owner");

        dff1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                data.put("account", accountnumber);
                data.put("ifsc", ifsccode);
                data.put("beneficiary", beneficiaryname);

                data.put("name",ownername);
                data.put("id",ownerid);
                data.put("fcmToken",snapshot.child("fcmtoken").getValue(String.class));
                snapshot.getRef().child("accountdetails").updateChildren(data);
                snapshot.getRef().child("accountsetup").setValue(1);
                DatabaseReference df=FirebaseDatabase.getInstance().getReference("pendingaccounts");
                df.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                        Long c=currentData.getChildrenCount();
                        if(c==null)c=0L;
                        c++;
                        data.put("index",c);
                        data.put("email",email);

                        currentData.child("account"+ownerid+c).setValue(data);
                        return Transaction.success(currentData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                        pd1.dismiss();
                        Toast.makeText(AccountsetupActivity.this, "Account details saved! Verification in progress.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AccountsetupActivity.this, Activity5.class));
                        finish();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



         // Add initial status as "pending"


    }
}