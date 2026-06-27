package com.example.bookandpostroom;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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
String key;

String accountnumber,ifsccode,beneficiaryname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountsetup);
keys k=new keys();
key=k.key3;
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        ownername = account.getDisplayName();
        ownerid = account.getId();
        email = account.getEmail();
        Toast.makeText(this, "the email is"+email, Toast.LENGTH_SHORT).show();
        accountnotextview = findViewById(R.id.accountno);
        beneficiarytextview= findViewById(R.id.beneficiary);

        ifsctextview = findViewById(R.id.ifsc);






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
       ProgressDialog pd1=new ProgressDialog(this);
        pd1.setCancelable(false);
        pd1.setMessage("proceeding");
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
                DatabaseReference df=FirebaseDatabase.getInstance().getReference("google").child("pendingaccounts");
                df.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                        Long c=currentData.getChildrenCount();
                        if(c==null)c=0L;
                        c++;
                        data.put("index",c);
                        data.put("email",email);
                        currentData.child("account"+c).setValue(data);
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