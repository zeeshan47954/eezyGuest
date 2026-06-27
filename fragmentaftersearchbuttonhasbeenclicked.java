package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class fragmentaftersearchbuttonhasbeenclicked extends BottomSheetDialogFragment {
    int end;

    interface Listenerhome2searchbasedbuttonclicked {
        void itemclicked3searchbasedbuttonclicked(int position, String s);
    }

    private String place;
private String District;
    // Use a static method to pass arguments to the fragment
    public static fragmentaftersearchbuttonhasbeenclicked newInstance(String place,String District) {
        fragmentaftersearchbuttonhasbeenclicked fragment = new fragmentaftersearchbuttonhasbeenclicked();
        Bundle args = new Bundle();
        args.putString("place", place);  // Add the argument to the Bundle
        args.putString("District",District);
        fragment.setArguments(args);  // Set the arguments on the fragment
        return fragment;
    }
    String roomname[];
    String roomaddresscombined[];
    String pass[];
    int finalindex;
    int startindex=0;
    private long value;
    private long countroomoccupied;
    Listenerhome2searchbasedbuttonclicked listenerhome2;
    int chiggacounter = 0;
    int currentPage = 0;
    int pageSize = 10;
    boolean isLoading = false;
    private boolean isInitialLoad = true;
 private int bend;
    RecyclerView rv;
    private View emptyStateView;
    AdapterForOwnerpart3 adapter;
    Uri[] photoids;
    ArrayList<String> photonamestemp = new ArrayList<>();
    ArrayList<Uri> photoidstemp = new ArrayList<>();
    ArrayList<String> roomnametemp = new ArrayList<>();
    ArrayList<String> roomaddresscombinedtemp = new ArrayList<>();
    ArrayList<String> roomidtemp=new ArrayList<>();
String [] Roomid;
    ArrayList<String> pricetemp = new ArrayList<>();
    String[] photonames;
    Dialog progressDialog;
    int roomCount;
    ArrayList<String> roomprice = new ArrayList<>();
    String Genderr[];
    String Price[];
    ArrayList<String>Gender=new ArrayList<>();
    ProgressBar bottomLoadingBar;
    ArrayList<String>roomid = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragmentforhome3searchbased, container, false);
        rv = rootView.findViewById(R.id.recyclerView);


        if (getArguments() != null) {
            place = getArguments().getString("place");  // Retrieve the argument from the Bundle
            District=getArguments().getString("District");
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        if (account != null) {
            DatabaseReference dbrroom = FirebaseDatabase.getInstance().getReference().child("roomCount");
            progressDialog = new Dialog(getContext());
            progressDialog.setCancelable(false);

            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog_layout, null);
            progressDialog.setContentView(dialogView);

            dbrroom.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    value = snapshot.getValue(Long.class);
                    if (value > 0) {
                        DatabaseReference dbr1 = FirebaseDatabase.getInstance().getReference().child("rooms");
                        dbr1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {


                                    roomCount = (int) snapshot.getChildrenCount();

                                    int index = 0;
                                    String[] ids = new String[roomCount];

                                    for (DataSnapshot roomSnapshot : snapshot.getChildren()) {
                                        ids[index] = roomSnapshot.getValue(String.class);
                                        index++;
                                    }



                                    loadImages(ids, currentPage, pageSize);
                                    setupScrollListener(ids);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle error
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }

        return rootView;
    }


    private void loadImages(String[] ids, int page, int size) {
        if (isLoading) return;
        isLoading = true;

        if (isInitialLoad && progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        } else {
            bottomLoadingBar.setVisibility(View.VISIBLE);
        }

        int start = page * size;
        end = Math.min(start + size, ids.length);
        bend=end;

        for(String kk:ids)
        {
            String ownername=kk.split("idstarts")[0];
            String ownerid=kk.split("idstarts")[1].split("idends")[0];
            String number=kk.split("idendsroom")[1];
            DatabaseReference dbrr = FirebaseDatabase.getInstance().getReference("google")
                    .child(ownername).child(ownerid).child("owner").child("rooms").child("roomId:" + kk);
            dbrr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String district=snapshot.child("district").getValue(String.class);
                    long roombeingchecked=snapshot.child("roombeingchecked").getValue(long.class);
                    long roombeingcheckedmax=snapshot.child("roombeingcheckedmax").getValue(long.class);
                    String place1=snapshot.child("roomlocation").getValue(String.class);
                    if(district.equals(District)&& roombeingchecked<roombeingcheckedmax && (place1.contains(place)||place.contains(place1)))
                    {

                        String kkk="";
                        roomid.add(kk);
                        if(snapshot.hasChild("roomname"))
                            roomnametemp.add(snapshot.child("roomname").getValue(String.class));
                        if(snapshot.hasChild("roomlocation"))
                            kkk=snapshot.child("roomlocation").getValue(String.class);


                        if(snapshot.hasChild("district"))
                            roomaddresscombinedtemp.add(kkk+" "+snapshot.child("district").getValue(String.class));
                        if(snapshot.hasChild("roomprice"))
                        {
                            roomprice.add(snapshot.child("roomprice").getValue(String.class));
                        }
                        if(snapshot.hasChild("bookedbygender"))
                        {
                            Gender.add(snapshot.child("bookedbygender").getValue(String.class));

                        }


                        photonamestemp.add(kk);
                        chiggacounter++;
                        if(chiggacounter==bend)
                        {
                            roomname = roomnametemp.toArray(new String[0]);
                            roomaddresscombined = roomaddresscombinedtemp.toArray(new String[0]);
                            photonames = photonamestemp.toArray(new String[0]);

                            Genderr=Gender.toArray(new String[0]);
                            Price=roomprice.toArray(new String[0]);
                            Roomid=roomid.toArray(new String[0]);
                            loadimagesfurther();

                        }

                    }
                    else{
                        chiggacounter++;
                        if(chiggacounter==bend)
                        {
                            roomname = roomnametemp.toArray(new String[0]);
                            if(roomname!=null &&roomname.length>0)
                            {roomname = roomnametemp.toArray(new String[0]);
                                roomaddresscombined = roomaddresscombinedtemp.toArray(new String[0]);
                                photonames = photonamestemp.toArray(new String[0]);

                                Genderr=Gender.toArray(new String[0]);
                                Price=roomprice.toArray(new String[0]);
                                Roomid=roomid.toArray(new String[0]);
                                loadimagesfurther();
                            }
                            else{

                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }

                                AdapterForemptyvalue adapter = new AdapterForemptyvalue(-1);
                                rv.setAdapter(adapter);
                                GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                                rv.setLayoutManager(glm);
                                bottomLoadingBar.setVisibility(View.GONE);
                                isLoading = false;

                            }


                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });





        }



        isInitialLoad = false;
    }

    public  void loadimagesfurther()
    {finalindex=photonames.length;
        for(String hhh:photonames)
        {
            String ownername=hhh.split("idstarts")[0];
            String ownerid=hhh.split("idstarts")[1].split("idends")[0];
            String number=hhh.split("idendsroom")[1];

            StorageReference srf = FirebaseStorage.getInstance().getReference("ownerPhotos")
                    .child(ownername).child(ownerid).child("images" + number).child(ownername + ownerid+ "zero");
            srf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    photoidstemp.add(uri);
                    startindex++;
                    if(startindex==finalindex)
                    {
                        photoids = photoidstemp.toArray(new Uri[0]);

                        updateUI();


                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (getContext() != null) {

                    }
                    isLoading = false;
                    bottomLoadingBar.setVisibility(View.GONE);
                }
            });


        }



    }

    private void updateUI() {
        if (getActivity() != null) {
            if (adapter == null) {

                adapter = new AdapterForOwnerpart3(requireContext(),photonames, photoids,roomname,roomaddresscombined,Genderr,Price,Roomid);
                rv.setAdapter(adapter);
                GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                rv.setLayoutManager(glm);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } else {
                adapter.notifyDataSetChanged();
            }

            adapter.setListener((position, s) -> {
                if (listenerhome2 != null) {
                    listenerhome2.itemclicked3searchbasedbuttonclicked(position, s);
                }
            });

            isLoading = false;
            bottomLoadingBar.setVisibility(View.GONE);
        }
    }

    private void setupScrollListener(String[] ids) {
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                GridLayoutManager layoutManager = (GridLayoutManager) rv.getLayoutManager();

                if (layoutManager != null && layoutManager.findLastVisibleItemPosition() == chiggacounter - 1 && !isLoading) {
                    if (chiggacounter < roomCount) {
                        currentPage++;
                        loadImages(ids, currentPage, pageSize);
                    }
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listenerhome2 = (Listenerhome2searchbasedbuttonclicked) context;
    }
}