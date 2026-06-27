package com.example.bookandpostroom;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class bookActivity extends AppCompatActivity {
    public static final String message = "hello";
    private EditText ownername;
    private EditText owneraddress;
    private StorageReference storageReff;
    private EditText ownernumber;
    private ActivityResultLauncher<Intent> pictureLauncher1;
    private ActivityResultLauncher<Intent> pictureLauncher2;
    private ActivityResultLauncher<Intent> pictureLauncher3;
    private Button rpic1;

    String sid;
    boolean capacityerror=false;
    private boolean isNavigatingWithinApp = false;
    private String gender;
    RadioGroup rg;
    RadioButton rbmale;
    RadioButton rbfemale;
    private boolean fifteensecondscomplete=false;
    boolean myturn=true;
    private long kk;
    private Button rpic2;
    private long countss;
    private boolean isPic1Selected = false;
    private boolean isPic2Selected = false;
    private boolean isPic3Selected = false;
    private String duration;
    private String beforekeyword111;
    private String id111;
    private String name1;
    Long roomcapacityassetbytenant;
    String roomcapp;
    long jj;
    String fcmtokenforowner;
    String fcmtokenfortenant;
    long request;
    private String id1;
    private boolean present=false;
    private CalendarView calendarViewEnter;
    private CalendarView calendarViewExit;
    private Button rpic3;
    List<String> queueList;
    private EditText rc;
    private EditText adhaarno;
    private TextView monthtime;
    private ImageView roompic1=null;
    private ImageView roompic2=null;
    private ImageView roompic3=null;
    public void function_for_launcher() {
        // Launcher for rpic1
        pictureLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            ImageView iv = findViewById(R.id.roompic1);
                            iv.setImageURI(imageUri);
                            loadAndCompressImage(imageUri, "one", R.id.roompic1, 1); // Use uri1 for roompic1
                        }
                    }
                });

        // Launcher for rpic2
        pictureLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            ImageView iv = findViewById(R.id.roompic2);

                            iv.setImageURI(imageUri);
                            loadAndCompressImage(imageUri, "two", R.id.roompic2, 2); // Use uri2 for roompic2
                        }
                    }
                });
        pictureLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            ImageView iv = findViewById(R.id.roompic3);

                            iv.setImageURI(imageUri);
                            loadAndCompressImage(imageUri, "three", R.id.roompic3, 3); // Use uri2 for roompic2
                        }
                    }
                });


        // Launcher for rpic3

    }

    private void loadAndCompressImage(Uri imageUri, String s, int imageid, int uriIndex) {


        Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        // Compress the bitmap
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 70, baos); // Compress to 20% quality
                        byte[] compressedData = baos.toByteArray();

                        // Create a new bitmap from compressed data

                        if (uriIndex == 3) {
                            storageReff = FirebaseStorage.getInstance().getReference("tenantPhotos").child(name1).child(id1).child("pfp");

                        } else {
                            // Set the compressed bitmap to the ImageView
                            storageReff = FirebaseStorage.getInstance().getReference("tenantPhotos").child(name1).child(id1).child("images").child(name1 + id1 + s);
                        }

                        Dialog progressDialog = new Dialog(bookActivity.this);
                        progressDialog.setCancelable(false); // Disable dismissal on touch outside

                        // Inflate the custom layout
                        View view = LayoutInflater.from(bookActivity.this).inflate(R.layout.progress_dialog_layout, null);

                        // Set the custom view to the dialog
                        progressDialog.setContentView(view);

                        // Show the dialog
                        progressDialog.show();
                        // Upload the image to Firebase Storage
                        UploadTask uploadTask = storageReff.putBytes(compressedData);
                        uploadTask.addOnSuccessListener(taskSnapshot -> {
                            // Get the download URL after the upload completes


                            // You now have the Firebase Storage URI
                            Toast.makeText(bookActivity.this, "image succesfully uploaded", Toast.LENGTH_SHORT).show();
                            // Do something with the URI (store it in Firestore, display it, etc.)
                            if (uriIndex == 3) {
                                isPic3Selected=true;


                            } else if(uriIndex==2){
                                // Set the compressed bitmap to the ImageView
                                isPic2Selected=true;
                            }
                            else if(uriIndex==1){
                                // Set the compressed bitmap to the ImageView
                                isPic1Selected=true;
                            }
                            progressDialog.dismiss();

                        }).addOnFailureListener(e -> {
                            // Handle failure
                            Toast.makeText(bookActivity.this, "sorry a failure occured", Toast.LENGTH_SHORT).show();

                            if (uriIndex == 3) {
                                isPic3Selected=false;


                            } else if(uriIndex==2){
                                // Set the compressed bitmap to the ImageView
                                isPic2Selected=false;
                            }
                            else if(uriIndex==1){
                                // Set the compressed bitmap to the ImageView
                                isPic1Selected=false;
                            }
                            progressDialog.dismiss();
                        });


                        // Dismiss the ProgressDialog

                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        // This method is called when the request is cleared
                    }
                });
    }
    Button submitbutton;
    String timeoftheday;
    String ownernameoriginal;
    String owneridoriginal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);
        sid=getIntent().getStringExtra(message);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        submitbutton=findViewById(R.id.submitbutton);
        if(account!=null)
        {
            name1=account.getDisplayName();
            id1=account.getId();
            String keyword = "idstarts";
            int startIndex = sid.indexOf(keyword);
            String beforeKeyword = sid.substring(0, startIndex);
            String keyword1 = "room";
            int startIndex1 = sid.indexOf(keyword1) + keyword1.length();
            String afterKeyword = sid.substring(startIndex1);
            String keywordStart = "idstarts";
            String keywordEnd = "idends";
            int startIndex2 = sid.indexOf(keywordStart) + keywordStart.length();
            int endIndex = sid.indexOf(keywordEnd);

            String idvalue = sid.substring(startIndex2, endIndex);
            TextView tv=findViewById(R.id.rctextview);
            beforekeyword111=beforeKeyword;
            id111=idvalue;
            DatabaseReference dd=FirebaseDatabase.getInstance().getReference("google").child(beforeKeyword).child(idvalue).child("owner").child("rooms").child("roomId:"+sid);
            dd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long s=snapshot.child("roomcapacityreached").getValue(long.class);
                    long d=snapshot.child("roomcapacity").getValue(long.class);
                    long k=d-s;
                    jj=k;


                    tv.setText("\uD83D\uDEA8 the room has a capacity of "+k+" people,enter the no. of spots you need for yourself?");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        function_for_launcher();
        Calendar calendar = Calendar.getInstance();

        monthtime=findViewById(R.id.monthtime);
        timeoftheday=name1+"+"+id1;
        ownernameoriginal=sid.split("idstarts")[0];
        owneridoriginal=sid.split("idstarts")[1].split("idends")[0];


        // Format the current date
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = formatter.format(calendar.getTime());
        System.out.println("Current Date: " + currentDate);

        // Add 32 days to the current date
        calendar.add(Calendar.DAY_OF_MONTH, 30);

        // Format the new date
        String newDate = formatter.format(calendar.getTime());
        duration=currentDate+"endsat"+newDate;


        monthtime.setText("Rent for 1 month:"+"("+currentDate+"to"+newDate+")");
        ownername=findViewById(R.id.ownername);
        owneraddress=findViewById(R.id.owneraddress);
        ownernumber=findViewById(R.id.ownernumber);
        adhaarno=findViewById(R.id.adhaarno);
        rg=findViewById(R.id.rggender);
        rbmale=findViewById(R.id.rbmale);
        rbfemale=findViewById(R.id.rbfemale);
        roompic1=findViewById(R.id.roompic1);
        roompic2=findViewById(R.id.roompic2);
        roompic3=findViewById(R.id.roompic3);
        rc=findViewById(R.id.rc);
        rpic1 = findViewById(R.id.roompic1button);
        rpic2 = findViewById(R.id.roompic2button);
        rpic3 = findViewById(R.id.roompic3button);





        rpic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickImageIntent.setType("image/*");

                // Start the activity to pick an image
                pictureLauncher1.launch(pickImageIntent);}

        });


        rpic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickImageIntent.setType("image/*");

                // Start the activity to pick an image
                pictureLauncher2.launch(pickImageIntent);

            }
        });
        rpic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pickImageIntent.setType("image/*");

                // Start the activity to pick an image
                pictureLauncher3.launch(pickImageIntent);


            }
        });

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }


    // Declare these as class fields
    private Dialog waitDialog;
    private View dialogView;



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (waitDialog != null && waitDialog.isShowing()) {

        }
    }
    ValueEventListener queueListener;
    Handler handler2=new Handler();
    public void submit(View view)
    {submitbutton.setVisibility(View.GONE);
        if(adhaarno.getText().toString().trim().isEmpty()||ownernumber.getText().toString().trim().isEmpty()||ownername.getText().toString().trim().isEmpty()||owneraddress.getText().toString().trim().isEmpty()
                ||rg.getCheckedRadioButtonId()==-1|| rc.getText().toString().trim().isEmpty()/* || !isPic3Selected || !isPic2Selected ||!isPic1Selected*/)
        {
            if(ownernumber.getText().toString().trim().isEmpty())
            {
                Toast.makeText(this, " number field is empty", Toast.LENGTH_SHORT).show();
            }
            if(ownername.getText().toString().trim().isEmpty())
            {
                Toast.makeText(this, " name field is empty", Toast.LENGTH_SHORT).show();
            }
            if(owneraddress.getText().toString().trim().isEmpty())
            {
                Toast.makeText(this, " address field is empty", Toast.LENGTH_SHORT).show();
            }


            if(rg.getCheckedRadioButtonId()==-1)
            {
                Toast.makeText(this, "gender field  is empty", Toast.LENGTH_SHORT).show();
            }

            if(ownernumber.getText().toString().length()<10)
            {
                Toast.makeText(this, "contact number should be 10 digits ", Toast.LENGTH_SHORT).show();




            }
            if(adhaarno.getText().toString().length()<12)
            {
                Toast.makeText(this, "adhaar no. should be 12 digits", Toast.LENGTH_SHORT).show();



            }
           /* if(!isPic1Selected) {
                Toast.makeText(this, "please upload adhaar front side is empty", Toast.LENGTH_SHORT).show();
            }
            if(!isPic2Selected) {
                Toast.makeText(this, "please upload adhaar front side is empty", Toast.LENGTH_SHORT).show();
            }
            if(!isPic3Selected) {
                Toast.makeText(this, "please upload profile picture is empty", Toast.LENGTH_SHORT).show();
            }*/

            if(!rc.getText().toString().isEmpty())
            {long roomcap=Long.parseLong(rc.getText().toString());
                if(roomcap>jj) {
                    Toast.makeText(this, "your set space exceeds available room capacity", Toast.LENGTH_SHORT).show();

                }
                else if(roomcap==0)
                {

                    Toast.makeText(this, "set space cannot be zero", Toast.LENGTH_SHORT).show();


                }
            }
            else if(rc.getText().toString().isEmpty())
            {
                Toast.makeText(this, "please enter the no. of spots you need for yourself", Toast.LENGTH_SHORT).show();

            }


            submitbutton.setVisibility(View.VISIBLE);
        }
        else{

            DatabaseReference queueRef = FirebaseDatabase.getInstance()
                    .getReference("google")
                    .child(ownernameoriginal)
                    .child(owneridoriginal)
                    .child("owner")
                    .child("rooms").child("roomId:" + sid).child("queue");
            DatabaseReference dd=FirebaseDatabase.getInstance().getReference("google").child(ownernameoriginal).child(owneridoriginal).child("owner").child("rooms").child("roomId:"+sid);
            dd.runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                    if(currentData.hasChild("queue"))
                    {
                        String c=currentData.child("queue").getValue(String.class);
                        if(c==null)
                        {
                            c="";

                        }
                        String j=c+","+timeoftheday;
                        if(c.isEmpty())
                        {
                            j=timeoftheday;
                        }
                        currentData.child("queue").setValue(j);
                        return Transaction.success(currentData);
                    }
                    else if(!currentData.hasChild("queue")){

                        currentData.child("queue").setValue(timeoftheday);
                        return Transaction.success(currentData);
                    }
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                    queueListener=new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String queue = snapshot.getValue(String.class);
                            if (queue != null && !queue.isEmpty()) {
                                String[] queueArray = queue.split(",");

                                queueList = new ArrayList<>(Arrays.asList(queueArray));
                                int position = queueList.indexOf(timeoftheday); // User's position in queue



                                if (position == -1) {
                                    if (waitDialog != null && waitDialog.isShowing()) {
                                        waitDialog.dismiss();
                                    }

                                    return;
                                }

                                if (position > 0) {
                                    int secondsToWait = position * 15;

                                    if (waitDialog == null || !waitDialog.isShowing()) {
                                        waitDialog = new Dialog(bookActivity.this);
                                        waitDialog.setCancelable(false);
                                        dialogView = LayoutInflater.from(bookActivity.this)
                                                .inflate(R.layout.wait_dialog_layout, null);
                                        waitDialog.setContentView(dialogView);
                                        waitDialog.show();
                                    }

                                    TextView waitMessage = dialogView.findViewById(R.id.wait_message);
                                    waitMessage.setText("You are #" + (position + 1) + " in line.\nEstimated wait: less than "
                                            + secondsToWait + " seconds. Do not close the app");
                                } else if (position == 0 ) {

                                    handler2.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (waitDialog != null && waitDialog.isShowing()) {
                                                waitDialog.dismiss();
                                            }

                                            processBooking(queueRef);
                                        }
                                    },10);


                                }
                            } else {

                                // Handle empty queue
                                if (waitDialog != null && waitDialog.isShowing()) {
                                    waitDialog.dismiss();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    queueRef.addValueEventListener(queueListener);




// Attach the continuous listener
                    //queueRef.child("queue").addValueEventListener(queueListener);

// Attach the continuous listener


                }
            });




        }
    }

String tenantnotification;
    Long no_of_people;

String tenantname,tenantid,rid;
    Long roomcap,roomcapreached;
    long currentTimeMillis1;

    Dialog progressDialog;
    private void processBooking(DatabaseReference queueRef) {
        if(waitDialog!=null && waitDialog.isShowing())
        {
            waitDialog.dismiss();
        }
        Handler handler=new Handler();



        progressDialog = new Dialog(bookActivity.this);
        progressDialog.setCancelable(false);
        View view1 = LayoutInflater.from(bookActivity.this).inflate(R.layout.progress_dialog_layout, null);
        progressDialog.setContentView(view1);
        progressDialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(progressDialog!=null && progressDialog.isShowing())
                {submitbutton.setVisibility(View.VISIBLE);
                    Toast.makeText(bookActivity.this, "error has occured,please try again ", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            }
        },22000);

        currentTimeMillis1 = System.currentTimeMillis();
        roomcapacityassetbytenant = Long.parseLong(rc.getText().toString());
        if (rg.getCheckedRadioButtonId() == R.id.rbmale) {
            gender = "male";
        } else if (rg.getCheckedRadioButtonId() == R.id.rbfemale) {
            gender = "female";
        }

        DatabaseReference dbrooo = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(ownernameoriginal)
                .child(owneridoriginal)
                .child("owner");

        // Add a lock check to prevent race conditions

        startBookingTransaction(dbrooo,progressDialog,queueRef);
    }

    private void startBookingTransaction(DatabaseReference dbrooo, Dialog progressDialog,DatabaseReference queueRef) {

        if (queueListener != null) {
            queueRef.removeEventListener(queueListener);
        }


        dbrooo.runTransaction(new Transaction.Handler() {

            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                try {
                    String queue = currentData
                            .child("rooms").child("roomId:" + sid).child("queue").getValue(String.class);
                    if (queue == null || queue.isEmpty()) {
                        queue=""; // No one in queue
                    }
                    queueList = new ArrayList<>(Arrays.asList(queue.split(",")));

                    currentData.child("rooms").child("roomId:" + sid).child("transactionLock").setValue(currentTimeMillis1+timeoftheday);











                    // Lock logic: only set if unset or expired


                    // Booking logic
                    roomcap = currentData.child("rooms")
                            .child("roomId:" + sid)
                            .child("roomcapacity")
                            .getValue(Long.class);

                    if (roomcap == null) {
                        // return success with no change → Firebase will retry with fresh data
                        return Transaction.success(currentData);
                    }

                    roomcapreached = currentData.child("rooms")
                            .child("roomId:" + sid)
                            .child("roomcapacityreached")
                            .getValue(Long.class);

                    if (roomcapreached == null) {
                        return Transaction.success(currentData);
                    }









                } catch (Exception e) {
                    // Log error for debugging

                    return Transaction.abort();
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                // Clear transaction lock

                long k = roomcap - roomcapreached;

                if (roomcapacityassetbytenant > k || roomcapacityassetbytenant == 0 ) {


                    dbrooo.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                            queueRef.removeEventListener(queueListener);
                            submitbutton.setVisibility(View.VISIBLE);
                            if (System.currentTimeMillis()-currentTimeMillis1<15000) {




                                String lock= currentData.child("rooms").child("roomId:" + sid).child("transactionLock").getValue(String.class);
                                if(lock==null)return Transaction.success(currentData);
                                String queue=currentData.child("rooms").child("roomId:"+sid).child("queue").getValue(String.class);
                                if (queue == null ) {
                                    // Force a retry instead of silently treating it as ""
                                    return  Transaction.success(currentData);
                                }
                                Log.d("queue",queue);
                                String[] queueArray = queue.split(",");

                                List <String>  queueListlocal = new ArrayList<>(Arrays.asList(queueArray));
                                int position = queueListlocal.indexOf(timeoftheday);
                                if(position==0) {
                                    if (queueList.size() > 0) {
                                        queueList.remove(0);
                                        currentData
                                                .child("rooms").child("roomId:" + sid).child("queue").setValue(queueList);

                                        if(lock.equals(currentTimeMillis1+timeoftheday))
                                        {  currentData.child("rooms").child("roomId:" + sid).child("transactionLock").setValue(null);
                                        }
                                    }
                                }
                                else{
                                    return  Transaction.abort();

                                }



                            }


                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {


                            TextView tv = findViewById(R.id.rctextview);
                            if (!committed) {
                                submitbutton.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                                Toast.makeText(bookActivity.this, "chigga nigga ", Toast.LENGTH_SHORT).show();

                            } else if (committed){
                                if (roomcap - roomcapreached > 0) {
                                    tv.setText("the room has a capacity of " + (roomcap - roomcapreached) + " people,enter the no. of spots you need for yourself?");
                                    Toast.makeText(bookActivity.this, "room capacity has changed,please enter the correct no. of spots you need", Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                } else {
                                    Toast.makeText(bookActivity.this, "no more space left,please choose another room", Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(bookActivity.this, studentinfoandrooms.class);
                                    startActivity(i);
                                }
                        }

                        }
                    });


                }



                else
                {queueRef.removeEventListener(queueListener);
                    DatabaseReference ad=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("student");


                    ad.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                            Long count1 = currentData.child("requestsenttotheownernumbers").getChildrenCount();
                            if (count1 == null) count1 = 0L;
                            currentData.child("requestsenttotheownernumbers").child("request" + count1).setValue(sid);

                            fcmtokenfortenant = currentData.child("fcmtoken").getValue(String.class);
                            if (fcmtokenfortenant == null) return  Transaction.success(currentData);
                            currentData.child("name").setValue(ownername.getText().toString());
                            currentData.child("address").setValue(owneraddress.getText().toString());
                            currentData.child("number").setValue(ownernumber.getText().toString());
                            currentData.child("adhaar").setValue(adhaarno.getText().toString());
                            Long x = currentData.child("no of requests sent").getValue(Long.class);
                            if (x == null) x = 0L;
                            currentData.child("no of requests sent").setValue(x + 1);

                            currentData.child("profilecreated").setValue(1);

                            currentData.child("gender").setValue(gender);
                            Long ddd = currentData.child("notificationforrequestsent").getValue(Long.class);
                            if (ddd == null) ddd = 0L;
                            currentData.child("notificationforrequestsent").setValue(ddd + 1);
                            currentData.child(sid).child("duration").setValue(duration);
                            currentData.child(sid).child("paid").setValue(0);
                            currentData.child(sid).child("requestapproved").setValue(0);





                            currentData.child(sid).child("no of people equal to").setValue(Long.parseLong(rc.getText().toString()));
                            if(System.currentTimeMillis()-currentTimeMillis1<15000)
                            {
                                return Transaction.success(currentData);
                            }
                            else if(System.currentTimeMillis()-currentTimeMillis1>=15000){
                                return Transaction.abort();
                            }

                            return Transaction.success(currentData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                            if(!committed)
                            {
                                submitbutton.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                                Toast.makeText(bookActivity.this, "destiny has arrived", Toast.LENGTH_SHORT).show();




                            }
                            if (committed){

                                if(System.currentTimeMillis()-currentTimeMillis1<15000)
                                {
                                    dbrooo.runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {


                                            currentData
                                                    .child("rooms").child("roomId:" + sid).child("roomcapacityreached").setValue(roomcapreached + roomcapacityassetbytenant);


                                            // Remove user from queue

                                            fcmtokenforowner = currentData.child("fcmtoken").getValue(String.class);
                                            if (fcmtokenforowner == null) fcmtokenforowner ="winnerchickendinner";



                                            Long ddd = currentData.child("notificationforrequestsrecieved").getValue(Long.class);
                                            if (ddd == null) ddd = 0L;
                                            currentData.child("notificationforrequestsrecieved").setValue(ddd + 1);
                                            Long count = currentData.child("actualrequestsrecieved").getChildrenCount();
                                            if (count == null) count = 0L;
                                            count++;
                                            Date date = new Date();
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                            String currentDateTime = sdf.format(date);


                                            currentData.child("actualrequestsrecieved").child("request" + count+":"+name1 + "idstartsfortenant" + id1 + "idendsfortenant" + "forroomwithsid" + sid).child("uploaded on and at").setValue(currentDateTime);;



                                            currentData.child("rooms").child("roomId:" + sid).child("transactionLock").setValue(null);


                                            String queue=currentData.child("rooms").child("roomId:"+sid).child("queue").getValue(String.class);
                                            if (queue == null ) {
                                                // Force a retry instead of silently treating it as ""
                                                return  Transaction.success(currentData);
                                            }
                                            Log.d("queue",queue);
                                            String[] queueArray = queue.split(",");

                                            List <String>  queueListlocal = new ArrayList<>(Arrays.asList(queueArray));
                                            int position = queueListlocal.indexOf(timeoftheday);
                                            if(position==0) {
                                                if (queueList.size() > 0) {
                                                    queueList.remove(0);
                                                    currentData
                                                            .child("rooms").child("roomId:" + sid).child("queue").setValue(queueList);

                                                }
                                            }
                                            else{
                                                return  Transaction.abort();

                                            }


                                            return Transaction.success(currentData);
                                        }

                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                                            if(!committed)
                                            {
                                                submitbutton.setVisibility(View.VISIBLE);
                                                progressDialog.dismiss();
                                                Toast.makeText(bookActivity.this, "sorry,an error occurred ,please try again", Toast.LENGTH_SHORT).show();

                                            }
                                            if(committed){

                                                if(System.currentTimeMillis()-currentTimeMillis1<15000)
                                                {

                                                    Sendnotification notificationsSender = new Sendnotification(
                                                            fcmtokenforowner,
                                                            "request",
                                                            "hello!a new request has been recieved",
                                                            bookActivity.this
                                                    );
                                                    notificationsSender.SendNotifications();
                                                    Sendnotification notificationsSender2 = new Sendnotification(
                                                            fcmtokenfortenant,
                                                            "success",
                                                            "request sent to the owner successfully",
                                                            bookActivity.this
                                                    );
                                                    notificationsSender2.SendNotifications();

                                                    progressDialog.dismiss();
                                                    Intent i = new Intent(bookActivity.this, studentinfoandrooms.class);
                                                    startActivity(i);


                                                }
                                                else{

                                                    submitbutton.setVisibility(View.VISIBLE);
                                                    progressDialog.dismiss();
                                                    Toast.makeText(bookActivity.this, "sorry,an error occurred ,please try again", Toast.LENGTH_SHORT).show();
                                                }



                                            }
                                        }
                                    });



                                }
                                else{

                                    submitbutton.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                    Toast.makeText(bookActivity.this, "sorry,an error occurred ,please try again", Toast.LENGTH_SHORT).show();
                                }






                            }
                        }
                    });




                }
            }
        });
    }



    @Override
    protected void onPause() {
        super.onPause();

    }



    @Override
    protected void onResume() {
        super.onResume();

    }




}


