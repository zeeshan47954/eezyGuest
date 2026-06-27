package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

import java.util.ArrayList;
import java.util.List;

public class tenantdetails extends AppCompatActivity {
private ListView lv;
private static final String message="";
private String s;
private List<String>  ll=new ArrayList<>();
private List<String> info=new ArrayList<>();
private String[] info2;
private List<String>info3=new ArrayList<>();
int x;
int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tenantdetails);
   lv=findViewById(R.id.listView);
   s=getIntent().getStringExtra(message);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(tenantdetails.this);
        String name=account.getDisplayName();
        String id=account.getId();
        Dialog progressDialog = new Dialog(tenantdetails.this);
        progressDialog.setCancelable(false); // Disable dismissal on touch outside

        // Inflate the custom layout
        View view1 = LayoutInflater.from(tenantdetails.this).inflate(R.layout.progress_dialog_layout, null);

        // Set the custom view to the dialog
        progressDialog.setContentView(view1);

        // Show the dialog
        progressDialog.show();

        DatabaseReference dd= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner").child("rooms").child("roomId:"+s).child("grabbedby");
        dd.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds:snapshot.getChildren())
                {
String s=ds.getValue(String.class);

info.add(s);
                }
                info2=info.toArray( new String[0]);
x=info2.length;
              for(String s:info2)
              {index++;
                 String name=s.split("/")[0];
                  String id=s.split("/")[1];
                  DatabaseReference ss=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("name");
                  ss.addListenerForSingleValueEvent(new ValueEventListener() {
                      @Override
                      public void onDataChange(@NonNull DataSnapshot snapshot) {

                     String j=snapshot.getValue(String.class);
                       info3.add(j);
                       if(index==x)
                       {
progressDialog.dismiss();
                           ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                   tenantdetails.this,
                                   android.R.layout.simple_list_item_1,
                                   info3);

                           // Attach the adapter to the ListView
                           lv.setAdapter(adapter);

                       }


                      }

                      @Override
                      public void onCancelled(@NonNull DatabaseError error) {

                      }
                  });

              }
                



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked item's data
                String selectedTenant = info2[position];
String k=selectedTenant.split("/")[0]+"gapp"+selectedTenant.split("/")[1];


Intent i=new Intent(tenantdetails.this,tenantinfo.class);
i.putExtra(tenantinfo.message,k);
startActivity(i);


                // You can perform other actions here, such as navigating to another activity
            }
        });
    }
}