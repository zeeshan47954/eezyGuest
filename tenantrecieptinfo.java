package com.example.bookandpostroom;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
long transactionno1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenantrecieptinfo);
String s=getIntent().getStringExtra(message);
Log.d("chigga",s);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

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
       String id=account.getId();
        transactionIdValue = findViewById(R.id.transactionIdValue);
        paidByValue = findViewById(R.id.paidtoValue);
        datedValue = findViewById(R.id.Dated);
        timeValue = findViewById(R.id.Time);
        amountValue = findViewById(R.id.amountValue);
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