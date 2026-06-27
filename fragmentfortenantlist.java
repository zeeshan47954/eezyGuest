package com.example.bookandpostroom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class fragmentfortenantlist extends Fragment {
    interface RequestListener{
        void functionforrequest(int id,String s);

    }
  RequestListener requestListener;
interface RequestListener2{

    void functiondeletetenantafterbooking(int id,String s);
}
RequestListener2 requestListener2;


    private static final String ARG_PARAM = "param_key";
    private String myParam;
List<String> names=new ArrayList<>();
List <Uri> photos=new ArrayList<>();
List<String>addresses=new ArrayList<>();
String [] tenaninfo;
long start=0;
long end;
RecyclerView rv;
ProgressBar pb;
    public static fragmentfortenantlist newInstance(String param) {
        fragmentfortenantlist fragment = new fragmentfortenantlist();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            myParam = getArguments().getString(ARG_PARAM);
        }
        View view= inflater.inflate(R.layout.fragment_fragmentfortenantlist, container, false);

        rv=view.findViewById(R.id.rv);
        pb=view.findViewById(R.id.pb);

        tenaninfo=myParam.split("\\+");
end=tenaninfo.length;
for(String s:tenaninfo)
{start++;
    String name=s.split("idstarts")[0];
    String id=s.split("idstarts")[1].split("idends")[0];

    DatabaseReference ds= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
    ds.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            names.add(snapshot.child("name").getValue(String.class));
            addresses.add(snapshot.child("address").getValue(String.class));

            StorageReference ds= FirebaseStorage.getInstance().getReference("tenantPhotos").child(name).child(id).child("pfp");
            ds.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
               photos.add(uri);
               if(start==end)
               {

                   String [] namesarray=names.toArray(new String[0]);
                   String [] addressarray=addresses.toArray(new String[0]);
                   Uri [] photoarray=photos.toArray(new Uri[0]);

                   adapterfortenantlist adapter = new adapterfortenantlist(tenaninfo, namesarray, addressarray,photoarray);
                   rv.setAdapter(adapter);
                   GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                   rv.setLayoutManager(glm);
                   rv.setVisibility(View.VISIBLE);
                   pb.setVisibility(View.GONE);
                   adapter.setListener(new adapterfortenantlist.RequestListeneradapter() {
                       @Override
                       public void functionforrequest1(int id, String s) {
                           requestListener.functionforrequest(id, s);
                       }
                   }, new adapterfortenantlist.RequestListeneradapter2() {
                       @Override
                       public void deletetenantaftertenant(int id, String s) {
                           requestListener2.functiondeletetenantafterbooking(id,s);
                       }
                   });


               }
                }
            });
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
















}


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.requestListener = (RequestListener) context;
        this.requestListener2=(RequestListener2) context;

    }
}