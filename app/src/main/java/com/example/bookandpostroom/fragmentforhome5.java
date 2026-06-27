package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;



public class fragmentforhome5 extends BottomSheetDialogFragment {
    interface Listenerhome1 {
        void itemclicked5ok(int position,String s1,String s2,String s3);
    }
    interface Listenerhome2 {
        void itemclicked5paymentcancel(int position,String s1,String s2,String s3);
    }
    interface Listenerhome3 {

        void itemclickedforcardview1(int position,String s,String roomid);
    }
    interface Listenerhome4 {

        void itemclickedforcardview2(int position,String s);
    }
    boolean done=false;
    Uri uris;
    String values;
    String values2;
    Uri uris2;
    String values3;
    long countrequest;
    private long countroomoccupied;
   Listenerhome1 listenerhome1;
   Listenerhome2 listenerhome2;
   RecyclerView rv;
   Listenerhome3 listenerhome3;
String message;
   Listenerhome4 listenerhome4;
   ProgressBar pb;
    private FirebaseAuth firebaseAuth;
private int index2=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rv = (RecyclerView) inflater.inflate(R.layout.fragment_fragmentforhome4, container, false);



        if (getArguments() != null) {
            message = getArguments().getString("valuepassed");

        }
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if(account!=null)
        {

            String name=account.getDisplayName();
            String id=account.getUid();
            Dialog progressDialog = new Dialog(getContext());
            progressDialog.setCancelable(false); // Disable dismissal on touch outside

            // Inflate the custom layout
            View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog_layout, null);

            // Set the custom view to the dialog
            progressDialog.setContentView(view);

            // Show the dialog
            progressDialog.show();








//String lll="40 zeeshanidstartsfortenant112163198300768366314idendsfortenantforroomwithsid40 zeeshanidstarts112163198300768366314idendsroom1"
    String usernametenant = message.split("idstartsfortenant")[0];
    String tenantid = message.split("idstartsfortenant")[1].split("idendsfortenantforroomwithsid")[0];
    String roomid = message.split("idendsfortenantforroomwithsid")[1];
    String ownerusername = message.split("idendsfortenantforroomwithsid")[1].split("idstarts")[0];
    String ownerid = message.split("idstartsfortenant")[1].split("idstarts")[1].split("idends")[0];
    String roomNumber = message.split("idendsroom")[1];

    // Extract what is between "idstartsfortenant" and "idendsfortenantforroomwithsid"

    StorageReference srf = FirebaseStorage.getInstance().getReference("tenantPhotos")
            .child(usernametenant).child(tenantid).child("pfp");


srf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
    @Override
    public void onSuccess(Uri uri) {
values3=message;
        uris=uri;

values=usernametenant+"gapp"+tenantid;
StorageReference srf2=FirebaseStorage.getInstance().getReference("ownerPhotos").child(ownerusername).child(ownerid).child("images"+roomNumber);

        srf2.list(1) // Limit to 1 item
                .addOnSuccessListener(listResult -> {
                    if (!listResult.getItems().isEmpty()) {
                        StorageReference firstImageRef = listResult.getItems().get(0);
                        firstImageRef.getDownloadUrl().addOnSuccessListener(uri2 -> {
                            uris2=uri2;
                            values2=roomid;

                            AdapterForOwnerpart5 adapter = new AdapterForOwnerpart5(values,values2,values3,uris,uris2);
                            rv.setAdapter(adapter);
                            GridLayoutManager glm = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);
                            rv.setLayoutManager(glm);
                            progressDialog.dismiss();
                            adapter.setListener(new AdapterForOwnerpart5.Listener1() {
                                public void iteminadapterclickedforbookingfinalizeagree(int position, String s1,String s2,String s3) {
                                    if (listenerhome1 != null) {
                                        listenerhome1.itemclicked5ok(position, s1,s2,s3);
                                    }
                                }
                            }, new AdapterForOwnerpart5.Listener2() {
                                @Override
                                public void iteminadapterclickedforbookingfinalizepaymentcancel(int position, String s1,String s2,String s3) {
                                    if (listenerhome2 != null) {
                                        listenerhome2.itemclicked5paymentcancel(position, s1,s2,s3);

                                    }
                                }
                            }, new AdapterForOwnerpart5.Listener3() {
                                @Override
                                public void itemclickedcardviewfirstone(int id, String s,String s2) {
                                    if(listenerhome3!=null)
                                    {
                                        listenerhome3.itemclickedforcardview1(id,s,s2);
                                    }
                                }
                            }, new AdapterForOwnerpart5.Listener4() {
                                @Override
                                public void itemclickedsidebyside(int id, String s) {
                                    if(listenerhome4!=null)
                                    {
                                        listenerhome4.itemclickedforcardview2(id,s);

                                    }
                                }
                            });

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Failed to list images", e));


    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(getContext(), "sorry,an error occured while fetching the image", Toast.LENGTH_SHORT).show();
    }
});










    }

        return  rv;
}






    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.listenerhome1 = (Listenerhome1) context;
        this.listenerhome2=(Listenerhome2) context;
        this.listenerhome3=(Listenerhome3) context;
        this.listenerhome4=(Listenerhome4) context;

    }







    }


