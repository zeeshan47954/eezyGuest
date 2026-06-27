package com.example.bookandpostroom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class fragmentforreview extends Fragment {
    String name;
    String id;
    List<String> reviewer=new ArrayList<>();
    List<String> review=new ArrayList<>();
    List<String> dated=new ArrayList<>();
    List<Float> rating=new ArrayList<>();
    RecyclerView rv;
String receivedParam;
    AdapterForReview adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view=inflater.inflate(R.layout.fragment_fragmentforreview, container, false);
        rv=view.findViewById(R.id.rv);

        if (getArguments() != null) {
            receivedParam = getArguments().getString("key_param");
            Log.e("param",receivedParam);
            Log.e("hello","hello");
            Log.e("hello","hello");
            Log.e("hello","hello");
        }
String ownername=receivedParam.split("idstarts")[0];
        String ownerid=receivedParam.split("idstarts")[1].split("idends")[0];
        DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(ownername)
                .child(ownerid).child("owner").child("rooms").child("roomId:"+receivedParam).child("reviews").child("actualreview");
        ds.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
for(DataSnapshot ds:snapshot.getChildren())
{
   String key=ds.getKey();
   String name=key.split(":")[1];
   reviewer.add(name);

  for(DataSnapshot ds1:ds.getChildren())
  {if(ds1.getKey().equals("review"))
      review.add(ds1.getValue(String.class));
      else if(ds1.getKey().equals("rating"))
          rating.add(ds1.getValue(Float.class));
      else
          dated.add(ds1.getValue(String.class));


  }

}
                String reviewerArray[]=reviewer.toArray(new String[0]);
                String reviewArray[]=review.toArray(new String[0]);
                String datedArray[]=dated.toArray(new String[0]);
                Float ratingArray[]=rating.toArray(new Float[0]);



                AdapterForReview adapter = new AdapterForReview(reviewerArray,reviewArray,ratingArray,datedArray);
                rv.setAdapter(adapter);
                GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                rv.setLayoutManager(glm);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        return view;
    }
}