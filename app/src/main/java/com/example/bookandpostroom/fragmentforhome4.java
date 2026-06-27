package com.example.bookandpostroom;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class fragmentforhome4 extends Fragment {

    interface Listenerhome1 {
        void itemclicked4(int position, String s);
    }

    interface Listenerhome2 {
        void itemclicked4payment(int position, String s);
    }

    private Listenerhome1 listenerhome1;
    private Listenerhome2 listenerhome2;

    private List<String> roomnameList = new ArrayList<>();
    private List<Long> roomtakenList = new ArrayList<>();
    private List<Uri> roompicList = new ArrayList<>();
    private String[] roomnamearray;
    private Long[] roomtakenarray;
    private Uri[] roompicarray;
    private int index2 = 0;
    private long countrequest = 0;

    private RecyclerView rv;
    private ProgressBar pb;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseAuth firebaseAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmentforhom344, container, false);

        rv = view.findViewById(R.id.rv);
        pb = view.findViewById(R.id.pb);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefresh);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account != null) {
            String name = account.getDisplayName();
            String id = account.getUid();

            loaddata(name, id);

            swipeRefreshLayout.setOnRefreshListener(() -> {
                roomnameList.clear();
                roomtakenList.clear();
                roompicList.clear();
                index2 = 0;
                countrequest = 0;
                rv.setAdapter(null);
                loaddata(name, id);
                new android.os.Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
            });
        }

        return view;
    }
    String roomname="";
    @Override
    public void onResume() {
        super.onResume();

        if(dbr!=null)
        {
            if(v1!=null)
            {
                dbr.addValueEventListener(v1);
            }
        }
        if(dbr.child("requestsenttotheownernumbers")!=null)
        {

            if(v2!=null)
            {
                dbr.child("requestsenttotheownernumbers").addValueEventListener(v2);
            }
        }
        if(dbr.child(roomname)!=null)
        {
            if(v3!=null)
            {
                dbr.child(roomname).addValueEventListener(v3);
            }
        }


    }
    @Override
    public void onPause() {
        super.onPause();


    }

    DatabaseReference dbr;
    ValueEventListener v1;
    ValueEventListener v2;
    ValueEventListener v3;

    public void loaddata(String name, String id) {
         dbr = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
v1=new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

            roomnameList.clear();
            roomtakenList.clear();
            roompicList.clear();
            index2 = 0;

            if (snapshot.child("no of requests sent").exists() && snapshot.child("no of requests sent").getValue(Long.class) > 0) {
                countrequest = snapshot.child("no of requests sent").getValue(Long.class);

              v2=new ValueEventListener() {
                  @Override
                  public void onDataChange(@NonNull DataSnapshot snapshot) {

                          roomnameList.clear();
                          roomtakenList.clear();
                          roompicList.clear();
                          index2 = 0;

                          for (DataSnapshot ds : snapshot.getChildren()) {
                               roomname = ds.getValue(String.class);
                              if (roomname == null) continue;

                              String ownername = roomname.split("idstarts")[0];
                              String roomid = roomname.split("idstarts")[1].split("idends")[0];
                              String roomno = roomname.split("room")[1];

                              v3=new ValueEventListener() {
                                  @Override
                                  public void onDataChange(@NonNull DataSnapshot snapshot) {

                                          Long requestStatus = snapshot.child("requestapproved").getValue(Long.class);
                                          if (requestStatus == null) requestStatus = 0L;

                                          if (!roomnameList.contains(roomname)) {
                                              roomtakenList.add(requestStatus);
                                              roomnameList.add(roomname);
                                          }

                                  }

                                  @Override
                                  public void onCancelled(@NonNull DatabaseError error) {

                                  }
                              };
                              dbr.child(roomname).addValueEventListener(v3);
                              StorageReference sr = FirebaseStorage.getInstance().getReference("ownerPhotos")
                                      .child(ownername).child(roomid).child("images" + roomno);

                              sr.list(1).addOnSuccessListener(listResult -> {
                                  if (!listResult.getItems().isEmpty()) {
                                      StorageReference firstImageRef = listResult.getItems().get(0);
                                      firstImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                          roompicList.add(uri);
                                          index2++;

                                          if (index2 == countrequest) {
                                              updateRecycler();
                                          }
                                      }).addOnFailureListener(e -> {
                                          index2++;
                                          if (index2 == countrequest) {
                                              updateRecycler();
                                          }
                                      });
                                  }
                              });
                          }

                  }

                  @Override
                  public void onCancelled(@NonNull DatabaseError error) {

                  }
              };
                dbr.child("requestsenttotheownernumbers").addValueEventListener(v2);
            } else {
                rv.setVisibility(View.VISIBLE);
                pb.setVisibility(View.GONE);
                AdapterForemptyvalue adapter = new AdapterForemptyvalue(9);
                rv.setAdapter(adapter);
                GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                rv.setLayoutManager(glm);
            }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
};
        dbr.addValueEventListener(v1);
    }

    private void updateRecycler() {
        roomtakenarray = roomtakenList.toArray(new Long[0]);
        roomnamearray = roomnameList.toArray(new String[0]);
        roompicarray = roompicList.toArray(new Uri[0]);

        AdapterForOwnerpart4 adapter = new AdapterForOwnerpart4(roomnamearray, roompicarray, roomtakenarray);
        rv.setAdapter(adapter);
        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(glm);
        rv.setVisibility(View.VISIBLE);
        pb.setVisibility(View.GONE);

        adapter.setListener((position, s) -> {
            if (listenerhome1 != null) {
                listenerhome1.itemclicked4(position, s);
            }
        }, (position, s) -> {
            if (listenerhome2 != null) {
                listenerhome2.itemclicked4payment(position, s);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listenerhome1 = (Listenerhome1) context;
        listenerhome2 = (Listenerhome2) context;
    }
}



//package com.example.bookandpostroom;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.GridLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//
//import com.google.android.gms.auth.api.signin.GoogleSignIn;
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class fragmentforhome4 extends BottomSheetDialogFragment {
//    interface Listenerhome1 {
//        void itemclicked4(int position,String s);
//    }
//    interface Listenerhome2 {
//        void itemclicked4payment(int position,String s);
//    }
//    long countrequest;
//
//   Listenerhome1 listenerhome1;
//   Listenerhome2 listenerhome2;
//   private List<String> roomnameList=new ArrayList<>();
//   private List<Long> roomtakenList=new ArrayList<>();
//   private List<Uri> roompicList=new ArrayList<>();
//   private String roomnamearray[];
//   private Long roomtakenarray[];
//   private Uri roompicarray[];
//private int index2=0;
//RecyclerView rv;
//ProgressBar pb;
//    SwipeRefreshLayout swipeRefreshLayout;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view =  inflater.inflate(R.layout.fragmentforhom344, container, false);
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());
//        if(account!=null)
//        {String name=account.getDisplayName();
//String id= account.getId();
//
//rv=view.findViewById(R.id.rv);
//pb=view.findViewById(R.id.pb);
//loaddata(name,id);
//            swipeRefreshLayout=view.findViewById(R.id.swipeRefresh);
//            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//
//                    roomnameList.clear();
//                    roomtakenList.clear();
//                    roompicList.clear();
//                    index2=0;
//                    countrequest=0;
//rv.setAdapter(null);
//                    loaddata(name,id);
//                    new android.os.Handler().postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 2000);
//                }
//            });
//
//
//
//
//
//
//
//
//
//
//        }
//
//        return  view;
//    }
//    public void loaddata(String name,String id)
//    {DatabaseReference dbr=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
//        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.child("no of requests sent").exists() && snapshot.child("no of requests sent").getValue(long.class)>0)
//                {
//                    countrequest=snapshot.child("no of requests sent").getValue(Long.class);
//
//                    dbr.child("requestsenttotheownernumbers").addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for(DataSnapshot ds:snapshot.getChildren())
//                            {
//                                String roomname=ds.getValue(String.class);
//
//                                String ownername=roomname.split("idstarts")[0];
//                                String roomid=roomname.split("idstarts")[1].split("idends")[0];
//                                String roomno=roomname.split("room")[1];
//
//
//                                dbr.child(roomname).addListenerForSingleValueEvent(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        roomtakenList.add(snapshot.child("requestapproved").getValue(long.class));
//                                        roomnameList.add(roomname);
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//                                StorageReference sr = FirebaseStorage.getInstance().getReference("ownerPhotos")
//                                        .child(ownername).child(roomid).child("images" + roomno);
//
//                                sr.list(1) // Limit to 1 item
//                                        .addOnSuccessListener(listResult -> {
//                                            if (!listResult.getItems().isEmpty()) {
//                                                StorageReference firstImageRef = listResult.getItems().get(0);
//                                                firstImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                                                    roompicList.add(uri); // Add URI to the list
//                                                    index2++;
//
//                                                    if(index2==countrequest)
//                                                    {roomtakenarray=roomtakenList.toArray(new Long[0]);
//                                                        roomnamearray=roomnameList.toArray(new String[0]);
//                                                        roompicarray=roompicList.toArray(new Uri[0]);
//
//                                                        AdapterForOwnerpart4 adapter = new AdapterForOwnerpart4(roomnamearray,roompicarray,roomtakenarray);
//                                                        rv.setAdapter(adapter);
//                                                        GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                                                        rv.setLayoutManager(glm);
//                                                        rv.setVisibility(View.VISIBLE);
//                                                        pb.setVisibility(View.GONE);
//                                                        adapter.setListener(new AdapterForOwnerpart4.Listener1() {
//                                                            public void iteminadapterclickedforbookingfinalize(int position, String s) {
//                                                                if (listenerhome1 != null) {
//                                                                    listenerhome1.itemclicked4(position, s);
//                                                                }
//                                                            }
//                                                        }, new AdapterForOwnerpart4.Listener2() {
//                                                            @Override
//                                                            public void iteminadapterclickedforbookingfinalizepayment(int position, String s) {
//                                                                if(listenerhome2!=null)
//                                                                {
//                                                                    listenerhome2.itemclicked4payment(position,s);
//
//                                                                }
//                                                            }
//                                                        });
//
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        index2++;
//                                                        if(index2==countrequest)
//                                                        {roomtakenarray=roomtakenList.toArray(new Long[0]);
//                                                            roomnamearray=roomnameList.toArray(new String[0]);
//                                                            roompicarray=roompicList.toArray(new Uri[0]);
//                                                            AdapterForOwnerpart4 adapter = new AdapterForOwnerpart4(roomnamearray,roompicarray,roomtakenarray);
//                                                            rv.setAdapter(adapter);
//                                                            GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                                                            rv.setLayoutManager(glm);
//                                                            rv.setVisibility(View.VISIBLE);
//                                                            pb.setVisibility(View.GONE);
//                                                            adapter.setListener(new AdapterForOwnerpart4.Listener1() {
//                                                                public void iteminadapterclickedforbookingfinalize(int position, String s) {
//                                                                    if (listenerhome1 != null) {
//                                                                        listenerhome1.itemclicked4(position, s);
//                                                                    }
//                                                                }
//                                                            }, new AdapterForOwnerpart4.Listener2() {
//                                                                @Override
//                                                                public void iteminadapterclickedforbookingfinalizepayment(int position, String s) {
//                                                                    if(listenerhome2!=null)
//                                                                    {
//                                                                        listenerhome2.itemclicked4payment(position,s);
//
//                                                                    }
//                                                                }
//                                                            });
//                                                        }
//                                                    }
//                                                });
//                                            }
//                                        });
//
//
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//
//                }
//                else{
//
//                    rv.setVisibility(View.VISIBLE);
//                    pb.setVisibility(View.GONE);
//                    AdapterForemptyvalue adapter = new AdapterForemptyvalue(9);
//                    rv.setAdapter(adapter);
//                    GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
//                    rv.setLayoutManager(glm);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.listenerhome1 = (Listenerhome1) context;
//this.listenerhome2=(Listenerhome2) context;
//    }
//}