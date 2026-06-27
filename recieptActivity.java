package com.example.bookandpostroom;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

public class recieptActivity extends AppCompatActivity {
public static final String message="";
    private TextView Phonenoview, transactionIdValue, paidToValue, paidByValue, datedValue, timeValue, amountValue;
   private String tid,ptv,pbv,dv,tv,number,name,roomid,amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reciept);

        String s=getIntent().getStringExtra(message);

        transactionIdValue = findViewById(R.id.transactionIdValue);

        paidByValue = findViewById(R.id.paidByValue);
        datedValue = findViewById(R.id.Dated);
        timeValue = findViewById(R.id.Time);
Phonenoview =findViewById(R.id.phonenumber);
amountValue=findViewById(R.id.amountValue);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(recieptActivity.this);
        if(account!=null)
        {
            DatabaseReference dbr= FirebaseDatabase.getInstance().getReference("google").child(account.getDisplayName()).child(account.getId()).child("owner").child("transactionsrecievedno").child("transaction"+s);

            dbr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("transactionid"))
                    {    tid=snapshot.child("transactionid").getValue(String.class);
                  transactionIdValue.setText(tid);}
                    if (snapshot.hasChild("madetoroom")) {
                        roomid = snapshot.child("madetoroom").getValue(String.class);
                    }
if(snapshot.hasChild("amount"))
{
    amount=snapshot.child("amount").getValue(String.class);
    amountValue.setText(amount);


}

                    if (snapshot.hasChild("madeby"))
                    {    pbv=snapshot.child("madeby").getValue(String.class);
                        String ownername=pbv.split("idstarts")[0];
                        String id=pbv.split("idstarts")[1].split("idends")[0];
DatabaseReference dbrf=FirebaseDatabase.getInstance().getReference("google").child(ownername).child(id);

dbrf.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
         number=snapshot.child("number").getValue(String.class);
        Phonenoview.setText(number);
        name=snapshot.child("name").getValue(String.class);
        paidByValue.setText(name);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});







                    }
                    if (snapshot.hasChild("transactionDate"))
                    {    dv=snapshot.child("transactionDate").getValue(String.class);
                        datedValue.setText(dv);}
                    if (snapshot.hasChild("transactionTime"))
                    {    tv=snapshot.child("transactionTime").getValue(String.class);
                        timeValue.setText(tv);}

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });









        }


    }
    public void details(View view)
    {
        Intent i= new Intent(this, roominfoandbookfinal3.class);
        i.putExtra(roominfoandbookfinal3.message,roomid);
        startActivity(i);



    }
}