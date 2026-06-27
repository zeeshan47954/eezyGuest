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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProfileFragment extends Fragment implements RefreshListener{

ImageView image;
CardView edit;
ImageButton back;
Uri imageuritemp;
Button choose;
private int finali;
private int count=1;
EditText ownername;
private  View view;
boolean picturechangemade=false;
EditText owneraddress;
private long profilecreated;
EditText ownercontact;
TextView errorsTextView;

Uri uri1;
Button submit;
    private Dialog progressDialog;
    private RecyclerView rv;
    private String name;
    private String id;
    private DatabaseReference dbrf;
   private StorageReference imageRefz;
    DatabaseReference dbrnn;
    private  StorageReference sr3;
    FirebaseUser account;
    @Override
    public void onRefresh() {
        Toast.makeText(getActivity(), "Refresh not required for profile", Toast.LENGTH_SHORT).show();



    }
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<Intent> pictureLauncher1;
    public void function_for_launcher() {
        // Launcher for rpic1
        pictureLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            picturechangemade=true;
                            uri1 = data.getData();
                            image.setImageURI(uri1);
                        }
                    }
                });}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         view = inflater.inflate(R.layout.fragment_profile, container, false);

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
        firebaseAuth = FirebaseAuth.getInstance();
         account = firebaseAuth.getCurrentUser();
        Button toggleButton = view.findViewById(R.id.toggleDetailsButton);
        LinearLayout detailsContainer = view.findViewById(R.id.detailsContainer);

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fixedDuration = 400; // Use a fixed, smooth duration

                if (detailsContainer.getVisibility() == View.GONE) {
                    expandWithDuration(detailsContainer, fixedDuration);
                    toggleButton.setText("Hide Details");
                    toggleButton.animate().rotation(180).setDuration(fixedDuration).start();
                } else {
                    collapseWithDuration(detailsContainer, fixedDuration);
                    toggleButton.setText("Show Details");
                    toggleButton.animate().rotation(0).setDuration(fixedDuration).start();
                }
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.options_recycler);

        String[] optionNames = { "Privacy Policy", "Settings", "Contact Us"};
        int[] optionIcons = {
                R.drawable.privacy,
                R.drawable.settings,
                R.drawable.contact
        };

// Convert to OptionItem list
        List<OptionItem> optionItems = new ArrayList<>();
        for (int i = 0; i < optionNames.length; i++) {
            optionItems.add(new OptionItem(optionNames[i], optionIcons[i]));
        }

// Set up RecyclerView
        OptionsAdapter adapter = new OptionsAdapter(optionItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            switch (position) {

                case 0:
                    // Privacy Policy clicked
                    Intent f=new Intent(getContext(), privacypolicy.class);
                    startActivity(f);
                    break;
                case 1:
                    // Settings clicked

                    Intent e=new Intent(getContext(), settingsadmin.class);
                    startActivity(e);
                    break;
                case 2:
                    // Contact Us clicked
                    Intent h=new Intent(getContext(), contactus.class);
                    startActivity(h);
                    break;

            }
        });
        if(account!=null) {
             name = account.getDisplayName();
             id = account.getUid();
             dbrf=FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");

            dbrf.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    profilecreated=snapshot.child("profilecreated").getValue(long.class);
                    if (profilecreated == 0) {

                        ImageView iv = view.findViewById(R.id.iv);
                        iv.setVisibility(View.VISIBLE);
                        CardView cv = view.findViewById(R.id.cv);
                        cv.setVisibility(View.GONE);


                    } else {
                         dbrnn = FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");
                        dbrnn.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override

                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.hasChild("ownername")) {

                                    ownername.setText(snapshot.child("ownername").getValue(String.class));
                                }
                                if (snapshot.hasChild("owneraddress")) {
                                    owneraddress.setText(snapshot.child("owneraddress").getValue(String.class));
                                }
                                if (snapshot.hasChild("ownernumber")) {
                                    ownercontact.setText(snapshot.child("ownernumber").getValue(String.class));
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


                         sr3 = FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id);
                         imageRefz = sr3.child("pfp");
                        imageRefz.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                imageuritemp = uri;
                                if (isAdded() && getView() != null) {
                                    Glide.with(getContext())
                                            .load(uri)
                                            .into(image);
                                }



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
            // Show the dialog


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
    private boolean isDetailsExpanded = false;

    private void expandWithDuration(final View v, int duration) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        v.startAnimation(animation);
    }

    private void collapseWithDuration(final View v, int duration) {
        final int initialHeight = v.getMeasuredHeight();

        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        animation.setDuration(duration);
        v.startAnimation(animation);
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
            ownercontact.getText().toString().isEmpty()||ownercontact.getText().toString().length()!=10)
    {
Handler h=new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                errorsTextView.setVisibility(View.GONE);
            }
        },2000);
        errorsTextView.setVisibility(View.VISIBLE);
        // Set drawable for the suggestion TextView
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.error);
        int width = 50; // set your desired width in pixels
        int height = 50; // set your desired height in pixels
        drawable.setBounds(0, 0, width, height);
        errorsTextView.setCompoundDrawables(drawable, null, null, null);

       if(ownername.getText().toString().isEmpty()||
               owneraddress.getText().toString().isEmpty()||
               ownercontact.getText().toString().isEmpty())
        errorsTextView.setText("fields cannot be empty");

       else if(ownercontact.getText().toString().length()!=10)
       {
           errorsTextView.setText("contact no. is less than 10 digits");
       }






    }
    else
    {
       Dialog dd=new Dialog(getContext());
       dd.setContentView(R.layout.progress_dialog_layout);
       dd.setCancelable(false);
       dd.show();

        String ownername1 = ownername.getText().toString();
        String owneraddress1 = owneraddress.getText().toString();
        String ownercontact1 = ownercontact.getText().toString();
        String name= account.getDisplayName();
        String id= account.getUid();
        DatabaseReference dbr1= FirebaseDatabase.getInstance().getReference("google").child(name).child(id).child("owner");



        Map<String,Object> ownerData = new HashMap<>();

        ownerData.put("ownername",ownername1);

        ownerData.put("owneraddress",owneraddress1);

        ownerData.put("ownernumber",ownercontact1);
        dbr1.updateChildren(ownerData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                StorageReference sr3 = FirebaseStorage.getInstance().getReference("ownerPhotos").child(name).child(id);
                StorageReference imageRefz = sr3.child("pfp");
                if (picturechangemade)
                {
                    if (uri1 == null && imageuritemp != null) {
                        uri1 = imageuritemp;

                    }
                UploadTask uploadTask = imageRefz.putFile(uri1);
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

                        dd.dismiss();

                    }
                });
            }
                else{
                    Toast.makeText(getContext(), "succesfully changed", Toast.LENGTH_SHORT).show();

                    edit.setVisibility(view.VISIBLE);

                    back.setVisibility(view.GONE);

                    choose.setVisibility(view.GONE);

                    ownername.setEnabled(false);

                    owneraddress.setEnabled(false);



                    submit.setVisibility(view.GONE);
                    ownercontact.setEnabled(false);
if(dd!=null)
                    dd.dismiss();
                }


            }
        });



    }








    }
}