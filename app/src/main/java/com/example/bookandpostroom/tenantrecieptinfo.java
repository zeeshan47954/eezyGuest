package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.database.ValueEventListener;

public class tenantrecieptinfo extends AppCompatActivity {
public static final String message="nigga";
private String roomid;
private String transactionno;
    private TextView transactionIdValue;
    private String tid;
    private TextView paidByValue;
    private String ptv;
    private TextView datedValue;
    String Date;
    private TextView timeValue;
    String time;
    private TextView amountValue;
    String amount;
    TextView status;
    TextView refundtime;
    TextView refunddate;
    TextView refunddateheader;
    TextView refundtimeheader;
long transactionno1;
    private FirebaseAuth firebaseAuth;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenantrecieptinfo);
        applySystemBarPadding();
String s=getIntent().getStringExtra(message);
Log.d("chigga",s);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        roomid=s.split("transactionnostartshere")[0];
        transactionno=s.split("transactionnostartshere")[1];

        Long l=Long.parseLong(transactionno);
        transactionno1=l;


        Dialog progressDialog = new Dialog(tenantrecieptinfo.this);
        progressDialog.setCancelable(false); // Disable dismissal on touch outside

        // Inflate the custom layout
        View view1 = LayoutInflater.from(tenantrecieptinfo.this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressDialog.setContentView(view1);

        // Show the dialog
        progressDialog.show();
       String name=account.getDisplayName();
       String id=account.getUid();
        transactionIdValue = findViewById(R.id.transactionIdValue);
        paidByValue = findViewById(R.id.paidtoValue);
        datedValue = findViewById(R.id.Dated);
        timeValue = findViewById(R.id.Time);
        amountValue = findViewById(R.id.amountValue);
        status=findViewById(R.id.status);
        refundtime=findViewById(R.id.refundtime);
        refunddate=findViewById(R.id.refunddate);
        refundtimeheader=findViewById(R.id.refundtimeheader);
        refunddateheader=findViewById(R.id.refunddateheader);
        DatabaseReference dbrr= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("transactionmadeno").child("transaction"+transactionno1);

        dbrr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tid=snapshot.child("transactionid").getValue(String.class);
                transactionIdValue.setText(tid);
                amount=snapshot.child("totalamount").getValue(String.class);
                String amount = snapshot.child("totalamount").getValue(String.class);

                if (amount != null && amount.length() > 2) {
                    amount = "₹"+amount.substring(0, amount.length() - 2);
                }

                amountValue.setText(amount);
                Date=snapshot.child("transactionDate").getValue(String.class);
                datedValue.setText(Date);
                time=snapshot.child("transactionTime").getValue(String.class);
                timeValue.setText(time);
                String refundddate="a";
                String refundttime="b";
                String statuss=snapshot.child("status").getValue(String.class);
                if(snapshot.child("cancellationdate").exists()) {
                     refundddate = snapshot.child("cancellationdate").getValue(String.class);
                }
                if(snapshot.child("cancellationtime").exists()) {
                     refundttime = snapshot.child("cancellationtime").getValue(String.class);
                }
                if(statuss.equals("refunded")||statuss.equals("refund initiated"))
                { LinearLayout ll1=findViewById(R.id.ll1);
                    LinearLayout ll2=findViewById(R.id.ll2);
                    ll1.setVisibility(View.VISIBLE);
                    ll2.setVisibility(View.VISIBLE);
                    status.setText(statuss);
                    refundtime.setText(refundttime);
                    refunddate.setText(refundddate);

                }
                else{
                    LinearLayout ll1=findViewById(R.id.ll1);
                    LinearLayout ll2=findViewById(R.id.ll2);
                    ll1.setVisibility(View.GONE);
                    ll2.setVisibility(View.GONE);
                    status.setText(statuss);
                    refunddateheader.setVisibility(View.GONE);
                    refundtimeheader.setVisibility(View.GONE);
                    refundtime.setVisibility(View.GONE);
                    refunddate.setVisibility(View.GONE);
                }


                String ownername=roomid.split("idstarts")[0];
                String idowner=roomid.split("idstarts")[1].split("idends")[0];
                DatabaseReference dbff=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(idowner).child("owner");
                dbff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

progressDialog.dismiss();
                        String ownername=snapshot.child("ownername").getValue(String.class);
                        paidByValue.setText(ownername);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });






    }
}