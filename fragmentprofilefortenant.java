package com.example.bookandpostroom;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class fragmentprofilefortenant extends Fragment {
    ImageView image;
    CardView edit;
    ImageButton back;
    Uri imageuritemp;
    Button choose;
    private int finali;
    private int count=1;
    EditText ownername;
    EditText owneraddress;
private long profilecreated;
    EditText ownercontact;
    TextView errorsTextView;
    private GoogleSignInAccount account;
    Uri uri1;
    Button submit;
    private ActivityResultLauncher<Intent> pictureLauncher1;
    public void function_for_launcher() {
        // Launcher for rpic1
        pictureLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            uri1 = data.getData();
                            image.setImageURI(uri1);
                        }
                    }
                });}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_fragmentprofilefortenant, container, false);

        edit=view.findViewById(R.id.edit);
        back=view.findViewById(R.id.back);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit(v);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back(v);
            }
        });
        choose=view.findViewById(R.id.choosephoto);
        ownername=view.findViewById(R.id.ownername);
        owneraddress=view.findViewById(R.id.owneraddress);
        image=view.findViewById(R.id.image);
        ownercontact=view.findViewById(R.id.ownernumber);
        errorsTextView=view.findViewById(R.id.errors);
        submit=view.findViewById(R.id.submitbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit(v);
            }
        });
        function_for_launcher();
        account = GoogleSignIn.getLastSignedInAccount(getContext());

        if(account!=null) {
            String name = account.getDisplayName();
            String id= account.getId();
         DatabaseReference dbrf=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student").child("profilecreated");


            Dialog progressDialog = new Dialog(getContext());
            progressDialog.setCancelable(false); // Disable dismissal on touch outside

            // Inflate the custom layout
            View view1 = LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog_layout, null);

            // Set the custom view to the dialog
            progressDialog.setContentView(view1);

            // Show the dialog

            dbrf.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    profilecreated=snapshot.getValue(long.class);
if(profilecreated==0)
{progressDialog.dismiss();
    ImageView iv=view.findViewById(R.id.iv);
    iv.setVisibility(View.VISIBLE);
    ImageView iv2=view.findViewById(R.id.iv2);
    iv2.setVisibility(View.VISIBLE);
TextView tv=view.findViewById(R.id.tvinfo);
tv.setVisibility(View.VISIBLE);
    CardView cv=view.findViewById(R.id.cv);
    cv.setVisibility(View.GONE);


}
else
{
    DatabaseReference dbrnn= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");
    dbrnn.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {

            if(snapshot.hasChild("name"))
            {

                ownername.setText(snapshot.child("name").getValue(String.class));
            }
            if(snapshot.hasChild("address"))
            {
                owneraddress.setText(snapshot.child("address").getValue(String.class));
            }
            if(snapshot.hasChild("number"))
            {
                ownercontact.setText(snapshot.child("number").getValue(String.class));
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });


    StorageReference sr3 = FirebaseStorage.getInstance().getReference("tenantPhotos").child(name).child(id);
    StorageReference imageRefz = sr3.child("pfp");
    imageRefz.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
        @Override
        public void onSuccess(Uri uri) {

            imageuritemp=uri;

            Glide.with(getContext())
                    .load(uri)
                    .into(image);


        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Toast.makeText(getContext(), "sorry an error occured while fetching the profile photo", Toast.LENGTH_SHORT).show();
            
        }
    });

}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickImageIntent.setType("image/*");

                // Start the activity to pick an image
                pictureLauncher1.launch(pickImageIntent);
            }
        });
        return view;
    }
    public void  edit(View view)
    {
        edit.setVisibility(view.GONE);

        back.setVisibility(view.VISIBLE);

        choose.setVisibility(view.VISIBLE);
        submit.setVisibility(view.VISIBLE);
        ownername.setEnabled(true);

        owneraddress.setEnabled(true);




        ownercontact.setEnabled(true);



    }

    public void back(View view)
    {
        edit.setVisibility(view.VISIBLE);

        back.setVisibility(view.GONE);

        choose.setVisibility(view.GONE);

        ownername.setEnabled(false);

        owneraddress.setEnabled(false);



        submit.setVisibility(view.GONE);
        ownercontact.setEnabled(false);







    }
    public void submit(View view)
    {
        if(ownername.getText().toString().isEmpty()||
                owneraddress.getText().toString().isEmpty()||
                ownercontact.getText().toString().isEmpty())
        {
            errorsTextView.setVisibility(View.VISIBLE);
            // Set drawable for the suggestion TextView
            Drawable drawable = getResources().getDrawable(R.drawable.error);
            int width = 50; // set your desired width in pixels
            int height = 50; // set your desired height in pixels
            drawable.setBounds(0, 0, width, height);
            errorsTextView.setCompoundDrawables(drawable, null, null, null);
            errorsTextView.setText("fields cannot be empty");




        }
        else
        {
            ProgressDialog pg2=new ProgressDialog(getContext());
            pg2.setMessage("making changes");
            pg2.setCancelable(false);
            pg2.show();
            String ownername1 = ownername.getText().toString();
            String owneraddress1 = owneraddress.getText().toString();
            String ownercontact1 = ownercontact.getText().toString();
            String name= account.getDisplayName();
            String id= account.getId();

                    DatabaseReference dbr1= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("student");



                    Map<String,Object> ownerData = new HashMap<>();

                    ownerData.put("name",ownername1);

                    ownerData.put("address",owneraddress1);

                    ownerData.put("number",ownercontact1);
                    dbr1.updateChildren(ownerData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            StorageReference sr3 = FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id);
                            StorageReference imageRefz = sr3.child("pfp");

                            if(uri1==null && imageuritemp!=null)
                            {
                                uri1=imageuritemp;

                            }
                            UploadTask uploadTask=imageRefz.putFile(uri1);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                                    Toast.makeText(getContext(), "succesfully changed", Toast.LENGTH_SHORT).show();

                                    edit.setVisibility(view.VISIBLE);

                                    back.setVisibility(view.GONE);

                                    choose.setVisibility(view.GONE);

                                    ownername.setEnabled(false);

                                    owneraddress.setEnabled(false);



                                    submit.setVisibility(view.GONE);
                                    ownercontact.setEnabled(false);

                                    pg2.dismiss();

                                }
                            });

                        }
                    });




                }




        }








    }
