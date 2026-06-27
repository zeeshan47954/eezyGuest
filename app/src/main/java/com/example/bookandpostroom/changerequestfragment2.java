package com.example.bookandpostroom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class changerequestfragment2 extends Fragment {
interface Listenerhome1{
 public void roomdetails(int id,String s);
}
interface Listenerhome2{

  public void done(int id,String s);
}
Listenerhome1 listenerhome1;
Listenerhome2 listenerhome2;

List<String>roomid=new ArrayList<>();
String []arrayroomid;
long start=0;
long end;
    private FirebaseAuth firebaseAuth;
    adapterforchangrequests2 adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Dialog pd=new Dialog(getContext());
        pd.setContentView(R.layout.progress_dialog_layout);
        pd.setCancelable(false);
        pd.show();

        RecyclerView rv=(RecyclerView) inflater.inflate(R.layout.fragment_changerequestfragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        String name=account.getDisplayName();
        String id=account.getUid();

        DatabaseReference fgg=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("requestforchangeofcapacity");
        fgg.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount()>0)
                {end=snapshot.getChildrenCount();
                    for(DataSnapshot dd:snapshot.getChildren())
                    {
                     String s=dd.getValue(String.class);
                     roomid.add(s);
                     start++;
                     if(start==end)
                     {

 adapter=new adapterforchangrequests2(roomid);
                         rv.setAdapter(adapter);
                         GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                         rv.setLayoutManager(glm);
                         pd.dismiss();
                         adapter.setListener(new adapterforchangrequests2.Listener1() {
                             @Override
                             public void roomdetailsinadapter(int id, String s) {
if(listenerhome1!=null)
{
    listenerhome1.roomdetails(id,s);

}
                             }
                         }, new adapterforchangrequests2.Listener2() {
                             @Override
                             public void doneinadapter(int id, String s) {
                                 if(listenerhome2!=null)
                                 {
                                     listenerhome2.done(id,s);
                                 }
                             }
                         });
                     }
                    }

                }
                else{
                    showEmptyState(rv,pd);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




   return rv;

    }
    public void removeItemFromAdapter(int position) {
        if (adapter != null) {
            adapter.removeItem(position);
        }
    }

    private void showEmptyState(RecyclerView rv, Dialog pb) {

        rv.setVisibility(View.VISIBLE);
        AdapterForemptyvalue adapter = new AdapterForemptyvalue(11);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
        pb.dismiss();
    }
}