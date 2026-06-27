


package com.example.bookandpostroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Locale;
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
    private Boolean errorincapacity=false;
    private Boolean uploadmismatch=false;
    
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
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        CoordinatorLayout scrollView = findViewById(R.id.main);

        // Apply padding to avoid system bars
        scrollView.setPadding(
                0,
                0,  // Top padding for status bar
                0,
                navBarHeight      // Bottom padding for navigation bar
        );

        // Optional: Log the values for debugging

    }
    Button submitbutton;
    String timeoftheday;
    String ownernameoriginal;
    String owneridoriginal;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_book);
         applySystemBarPadding();
        sid=getIntent().getStringExtra(message);
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        submitbutton=findViewById(R.id.submitbutton);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if(account!=null)
        {
            name1=account.getDisplayName();
            id1=account.getUid();
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
            StorageReference df=FirebaseStorage.getInstance().getReference("tenantPhotos").child(name1).child(id1).child("pfp");
            df.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(uri!=null)
                    {
                        ImageView iv=findViewById(R.id.roompic3);
                        Glide.with(bookActivity.this)
                                .load(uri)
                                .into(iv);
isPic1Selected=true;
                    }
                }
            });
            StorageReference dm=FirebaseStorage.getInstance().getReference("tenantPhotos").child(name1).child(id1).child("images").child(name1+id1+"one");
            dm.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(uri!=null)
                    {
                        ImageView iv=findViewById(R.id.roompic2);
                        Glide.with(bookActivity.this)
                                .load(uri)
                                .into(iv);
isPic2Selected=true;
                    }
                }
            });
            StorageReference dl=FirebaseStorage.getInstance().getReference("tenantPhotos").child(name1).child(id1).child("images").child(name1+id1+"two");
            dl.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    if(uri!=null)
                    {
                        ImageView iv=findViewById(R.id.roompic1);
                        Glide.with(bookActivity.this)
                                .load(uri)
                                .into(iv);
isPic3Selected=true;
                    }
                }
            });


            DatabaseReference dd=FirebaseDatabase.getInstance().getReference("google").child(beforeKeyword).child(idvalue).child("owner").child("rooms").child("roomId:"+sid);
            dd.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        long s = snapshot.child("roomcapacityreached").getValue(long.class);
                        long d = snapshot.child("roomcapacity").getValue(long.class);
                        long k = d - s;
                        jj = k;

if(jj>0)
                        tv.setText("\uD83D\uDEA8 the room has a capacity of " + k + " people,enter the no. of spots you need for yourself?");

                 else if(jj==0){

    SpannableString title = new SpannableString("No Space");
    title.setSpan(new ForegroundColorSpan(Color.RED), 0, title.length(), 0);
    SpannableString ok = new SpannableString("ok");
    title.setSpan(new ForegroundColorSpan(Color.RED), 0, title.length(), 0);
    // Message in red
    SpannableString message = new SpannableString("The room has been fully booked and has no available space");
    message.setSpan(new ForegroundColorSpan(Color.RED), 0, message.length(), 0);
    new AlertDialog.Builder(bookActivity.this)
            .setTitle(title)
            .setMessage(message).setCancelable(false)
            .setPositiveButton(ok, (dialog, which) -> {
                // Delete user data from Firebase Database
                finish();

            })

            .show();
}
                    }
                    else{
                        Toast.makeText(bookActivity.this, "the data doesn't exist", Toast.LENGTH_SHORT).show();
                        finish();

                    }
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

    // Declare these as class fields
    private Dialog waitDialog;
    private View dialogView;





    String c;

    Handler handler2=new Handler();
    ValueEventListener a;
    ValueEventListener capacityErrorListener;
    ValueEventListener doneListener;
    ValueEventListener queueListener;
    ValueEventListener capacitylistener;
    DatabaseReference studentRef;
    DatabaseReference queueRef;
    Dialog progressDialog;
    Boolean zerocapacity=false;
    DatabaseReference dd;
    private long currentTimeMillis1;
    long currentremainingcapacity;
    public void submit(View view) {
        Log.d("QUEUE_DEBUG", "submit: Starting submit method");
        submitbutton.setVisibility(View.GONE);
         studentRef = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(name1)
                .child(id1)
                .child("student");
        // Validate input fields
        if (adhaarno.getText().toString().trim().isEmpty() ||
                ownername.getText().toString().trim().isEmpty() ||
                owneraddress.getText().toString().trim().isEmpty() ||
                rg.getCheckedRadioButtonId() == -1 ||
                rc.getText().toString().trim().isEmpty() ||
                !isPic3Selected || !isPic2Selected || !isPic1Selected) {
            Log.w("QUEUE_DEBUG", "submit: Validation failed - empty fields or unselected images");
            Toast.makeText(bookActivity.this, "Please fill all fields and select all images", Toast.LENGTH_SHORT).show();
            submitbutton.setVisibility(View.VISIBLE);
            return;
        }
        Log.d("QUEUE_DEBUG", "submit: Input validation passed");

        // Initialize dialogs
        waitDialog = new Dialog(bookActivity.this);
        waitDialog.setCancelable(false);
        dialogView = LayoutInflater.from(bookActivity.this).inflate(R.layout.wait_dialog_layout, null);
        waitDialog.setContentView(dialogView);
        progressDialog = new Dialog(bookActivity.this);
        progressDialog.setCancelable(false);
        View view1 = LayoutInflater.from(bookActivity.this).inflate(R.layout.progress_dialog_layout, null);
        progressDialog.setContentView(view1);
        Log.d("QUEUE_DEBUG", "submit: Dialogs initialized");

        // Reference to the queue
         queueRef = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(ownernameoriginal)
                .child(owneridoriginal)
                .child("owner")
                .child("rooms")
                .child("roomId:" + sid)
                .child("queue");
        Log.d("QUEUE_DEBUG", "submit: Queue reference set: " + queueRef.toString());

        // Add request to queue
        queueRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String currentQueue = currentData.getValue(String.class);
                Log.d("QUEUE_DEBUG", "submit: Queue transaction - currentQueue=" + (currentQueue != null ? currentQueue : "null"));
                if (currentQueue == null || currentQueue.isEmpty()) {
                    currentData.setValue(timeoftheday);
                    Log.d("QUEUE_DEBUG", "submit: Queue empty, setting queue to: " + timeoftheday);
                } else {
                    currentData.setValue(currentQueue + "," + timeoftheday);
                    Log.d("QUEUE_DEBUG", "submit: Appending to queue, new value: " + currentQueue + "," + timeoftheday);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null || !committed) {
                    Log.e("QUEUE_DEBUG", "submit: Queue transaction failed, error=" + (error != null ? error.getMessage() : "commit failed"));
                    Toast.makeText(bookActivity.this, "Failed to join queue: " + (error != null ? error.getMessage() : "Transaction failed"), Toast.LENGTH_LONG).show();
                    submitbutton.setVisibility(View.VISIBLE);
                    if (waitDialog != null && waitDialog.isShowing()) {
                        waitDialog.dismiss();
                    }
                    return;
                }
                Log.d("QUEUE_DEBUG", "submit: Queue transaction committed, queue=" + (currentData != null ? currentData.getValue(String.class) : "null"));

                // Reference to student data
                 studentRef = FirebaseDatabase.getInstance()
                        .getReference("google")
                        .child(name1)
                        .child(id1)
                        .child("student");
                Log.d("QUEUE_DEBUG", "submit: Student reference set: " + studentRef.toString());

                // Set up done listener
                doneListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("QUEUE_DEBUG", "submit: Done listener triggered, snapshot exists=" + snapshot.exists() + ", value=" + (snapshot.exists() ? snapshot.getValue() : "null"));
                        if (snapshot.exists() && snapshot.getValue(Integer.class) == 1) {
                            Log.d("QUEUE_DEBUG", "submit: Done flag set to 1, request processed successfully");
                           if(doneListener!=null)
                           {studentRef.child("done").removeEventListener(doneListener);}
                           if(queueListener!=null)
                           { queueRef.removeEventListener(queueListener);}
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            if (waitDialog != null && waitDialog.isShowing()) {
                                waitDialog.dismiss();
                            }
                            Intent i = new Intent(bookActivity.this, studentinfoandrooms.class);
                            startActivity(i);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("QUEUE_DEBUG", "submit: Done listener cancelled, error=" + error.getMessage());
                        Toast.makeText(bookActivity.this, "Error checking done status: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        if(doneListener!=null)
                        {studentRef.child("done").removeEventListener(doneListener);}
                        if(queueListener!=null)
                        {queueRef.removeEventListener(queueListener);}
                        if (waitDialog != null && waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                        submitbutton.setVisibility(View.VISIBLE);
                    }
                };
                studentRef.child("done").addValueEventListener(doneListener);
                 dd=FirebaseDatabase.getInstance().getReference("google").child(ownernameoriginal).child(owneridoriginal).child("owner").child("rooms").child("roomId:"+sid).child("capacityremaining");
capacitylistener=new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {

        currentremainingcapacity=snapshot.getValue(Long.class);
        if(!snapshot.exists()||currentremainingcapacity==0)
        {
            Toast.makeText(bookActivity.this, "no more space available", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(bookActivity.this,studentinfoandrooms.class);
            startActivity(i);
        }
        TextView tv=findViewById(R.id.rctextview);
        tv.setText("\uD83D\uDEA8 the room has a capacity of " + currentremainingcapacity + " people,enter the no. of spots you need for yourself?");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
};
                dd.addValueEventListener(capacitylistener);

                Log.d("QUEUE_DEBUG", "submit: Done listener attached");

                // Set up queue listener
                queueListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String queue = snapshot.getValue(String.class);
                        Log.d("QUEUE_DEBUG", "submit: Queue listener triggered, queue=" + (queue != null ? queue : "null"));
                        if (queue != null && !queue.isEmpty()) {
                            String[] queueArray = queue.split(",");
                            queueList = new ArrayList<>(Arrays.asList(queueArray));
                            int position = queueList.indexOf(timeoftheday);
                            Log.d("QUEUE_DEBUG", "submit: Queue position=" + position + ", timeoftheday=" + timeoftheday);

                            if (position == -1) {
                                Log.w("QUEUE_DEBUG", "submit: Request not found in queue, timeoftheday=" + timeoftheday);
                                if (waitDialog != null && waitDialog.isShowing()) {
                                    waitDialog.dismiss();
                                }
                                Toast.makeText(bookActivity.this, "Your request was removed from the queue. Please try again.", Toast.LENGTH_LONG).show();
                               if(queueListener!=null)
                               {queueRef.removeEventListener(queueListener);}
                               if(doneListener!=null)
                               {studentRef.child("done").removeEventListener(doneListener);}
                                submitbutton.setVisibility(View.VISIBLE);
                                return;
                            }

                            if (position > 0) {
                                int secondsToWait = position * 15;
                                Log.d("QUEUE_DEBUG", "submit: Waiting at position " + (position + 1) + ", estimated wait=" + secondsToWait + " seconds");
                                if (!waitDialog.isShowing()) {
                                    waitDialog.show();
                                    Log.d("QUEUE_DEBUG", "submit: Showing wait dialog");
                                }
                                TextView waitMessage = dialogView.findViewById(R.id.wait_message);
                                waitMessage.setText("You are #" + (position + 1) + " in line.\nEstimated wait: < " + secondsToWait + " seconds.\nDo not close the app");
                            } else if (position == 0) {
                                Log.d("QUEUE_DEBUG", "submit: Reached position 0, calling processBooking");
                                if (waitDialog != null && waitDialog.isShowing()) {
                                    waitDialog.dismiss();
                                    Log.d("QUEUE_DEBUG", "submit: Wait dialog dismissed");
                                }
                                // Use handler to avoid reentrancy issues
                                handler2.post(() -> processBooking(queueRef));
                            }
                        } else {
                            Log.w("QUEUE_DEBUG", "submit: Queue is empty or null");
                            if (waitDialog != null && waitDialog.isShowing()) {
                                waitDialog.dismiss();
                            }
                            Toast.makeText(bookActivity.this, "Queue is empty. Please try again.", Toast.LENGTH_LONG).show();
                           if(queueListener!=null)
                           {queueRef.removeEventListener(queueListener);}
                           if(doneListener!=null)
                           { studentRef.child("done").removeEventListener(doneListener);}
                            submitbutton.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("QUEUE_DEBUG", "submit: Queue listener cancelled, error=" + error.getMessage());
                        if (waitDialog != null && waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                        Toast.makeText(bookActivity.this, "Error reading queue: " + error.getMessage(), Toast.LENGTH_LONG).show();
                       if(queueListener!=null)
                       {queueRef.removeEventListener(queueListener);}
                       if(doneListener!=null)
                       {  studentRef.child("done").removeEventListener(doneListener);}
                        submitbutton.setVisibility(View.VISIBLE);
                    }
                };
                queueRef.addValueEventListener(queueListener);
                Log.d("QUEUE_DEBUG", "submit: Queue listener attached");
            }
        });
    }

    private void processBooking(DatabaseReference queueRef) {
        Log.d("QUEUE_DEBUG", "processBooking: Starting processBooking");
        if (!bookActivity.this.isFinishing()) {
            progressDialog.show();
            Log.d("QUEUE_DEBUG", "processBooking: Showing progress dialog");
        }

        currentTimeMillis1 = System.currentTimeMillis();
        try {
            roomcapacityassetbytenant = Long.parseLong(rc.getText().toString());
        } catch (NumberFormatException e) {
            Log.e("QUEUE_DEBUG", "processBooking: Invalid room capacity, error=" + e.getMessage());
            Toast.makeText(bookActivity.this, "Invalid room capacity value", Toast.LENGTH_LONG).show();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            submitbutton.setVisibility(View.VISIBLE);
            if(queueListener!=null)
            { queueRef.removeEventListener(queueListener);}
            if(doneListener!=null)
            {  studentRef.child("done").removeEventListener(doneListener);}
            return;
        }
        Log.d("QUEUE_DEBUG", "processBooking: roomcapacityassetbytenant=" + roomcapacityassetbytenant);

        if (rg.getCheckedRadioButtonId() == R.id.rbmale) {
            gender = "male";
        } else if (rg.getCheckedRadioButtonId() == R.id.rbfemale) {
            gender = "female";
        } else {
            Log.w("QUEUE_DEBUG", "processBooking: No gender selected");
            Toast.makeText(bookActivity.this, "Please select a gender", Toast.LENGTH_LONG).show();
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            submitbutton.setVisibility(View.VISIBLE);
            if(queueListener!=null)
            { queueRef.removeEventListener(queueListener);}
            if(doneListener!=null)
            {  studentRef.child("done").removeEventListener(doneListener);}
            return;
        }
        Log.d("QUEUE_DEBUG", "processBooking: gender=" + gender);

        String tenantname = ownername.getText().toString();
        String tenantaddress = owneraddress.getText().toString();
        String tenantnumber = ownernumber.getText().toString();
        String tenantadhaar = adhaarno.getText().toString();
        Log.d("QUEUE_DEBUG", "processBooking: tenantname=" + tenantname + ", tenantid=" + id1 + ", address=" + tenantaddress + ", number=" + tenantnumber + ", adhaar=" + tenantadhaar);

        DatabaseReference ds = FirebaseDatabase.getInstance()
                .getReference("google")
                .child(ownernameoriginal)
                .child(owneridoriginal)
                .child("owner")
                .child("rooms")
                .child("roomId:" + sid);
        Log.d("QUEUE_DEBUG", "processBooking: Booking reference set: " + ds.toString());

        ds.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                String currentQueue = currentData.child("queue").getValue(String.class);
                Log.d("QUEUE_DEBUG", "processBooking: Booking transaction - queue=" + (currentQueue != null ? currentQueue : "null"));

                if (currentQueue == null || currentQueue.isEmpty()) {
                    Log.w("QUEUE_DEBUG", "processBooking: Booking transaction - queue is null or empty");
                    uploadmismatch = true;
                    return Transaction.abort();
                }

                String[] queueArray = currentQueue.split(",");
                Long currentRemainingCapacity = currentData.child("capacityremaining").getValue(Long.class);
                Log.d("QUEUE_DEBUG", "processBooking: Booking transaction - capacityremaining=" + (currentRemainingCapacity != null ? currentRemainingCapacity : "null"));

                String expectedQueueEntry = name1 + "+" + id1;
                if (!queueArray[0].equals(expectedQueueEntry)) {
                    Log.w("QUEUE_DEBUG", "processBooking: Booking transaction - queue mismatch, expected=" + expectedQueueEntry + ", actual=" + queueArray[0]);
                    uploadmismatch = true;
                    return Transaction.abort();
                }

                if (currentRemainingCapacity == null) {
                    Log.w("QUEUE_DEBUG", "processBooking: Booking transaction - capacityremaining is null");
                    errorincapacity = true;
                    return Transaction.abort();
                }

                if (roomcapacityassetbytenant > currentRemainingCapacity) {
                    Log.w("QUEUE_DEBUG", "processBooking: Booking transaction - capacity exceeded, requested=" + roomcapacityassetbytenant + ", remaining=" + currentRemainingCapacity);
                    errorincapacity = true;
                    return Transaction.abort();
                }


                Map<String, Object> data = new HashMap<>();
                data.put("ownername", ownernameoriginal);
                data.put("ownerid", owneridoriginal);
                data.put("tenantname", name1);
                data.put("tenantid", id1);
                data.put("roomcapacityassetbytenant", roomcapacityassetbytenant);
                data.put("name", tenantname);
                data.put("address", tenantaddress);
                data.put("number", tenantnumber);
                data.put("adhaar", tenantadhaar);
                data.put("gender", gender);
                data.put("duration", duration);
                currentData.child("requestsforbooking").setValue(data);
                Log.d("QUEUE_DEBUG", "processBooking: Booking transaction - wrote requestsforbooking=" + data);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                Log.d("QUEUE_DEBUG", "processBooking: Booking transaction completed, committed=" + committed + ", error=" + (error != null ? error.getMessage() : "null"));
                if (!committed) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    if (uploadmismatch) {
                        Log.w("QUEUE_DEBUG", "processBooking: Booking transaction failed - queue mismatch");
                        Toast.makeText(bookActivity.this, "Please wait for your turn", Toast.LENGTH_SHORT).show();
                        uploadmismatch = false;
                    } else if (errorincapacity) {
                        Log.w("QUEUE_DEBUG", "processBooking: Booking transaction failed - capacity exceeded");
                        Toast.makeText(bookActivity.this, "Your set spots exceed the available capacity, please choose fewer spots", Toast.LENGTH_SHORT).show();
                        errorincapacity = false;
                    }  else {
                        Log.e("QUEUE_DEBUG", "processBooking: Booking transaction failed - error=" + (error != null ? error.getMessage() : "unknown error"));
                        Toast.makeText(bookActivity.this, "Error processing request: " + (error != null ? error.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
                    }
                    submitbutton.setVisibility(View.VISIBLE);
                    if(queueListener!=null)
                    { queueRef.removeEventListener(queueListener);}
                    if(doneListener!=null)
                    { studentRef.child("done").removeEventListener(doneListener);}
                } else {
                    Log.d("QUEUE_DEBUG", "processBooking: Booking transaction succeeded");
                    // Success case handled by doneListener
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (queueRef != null) {
            if (queueListener != null) {
                queueRef.removeEventListener(queueListener);
            }
        }
        if (dd != null) {
            if (capacitylistener != null) {
                dd.removeEventListener(capacitylistener);
            }
        }
        if (studentRef != null) {
            if (doneListener != null) {
                studentRef.child("done").removeEventListener(doneListener);
            }

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (queueRef != null) {
            if (queueListener != null) {
                queueRef.removeEventListener(queueListener);
            }
        }
        if (dd != null) {
            if (capacitylistener != null) {
                dd.removeEventListener(capacitylistener);
            }
        }
        if (studentRef != null) {
            if (doneListener != null) {
                studentRef.child("done").removeEventListener(doneListener);
            }

        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (queueRef != null) {
            if (queueListener != null) {
                queueRef.addValueEventListener(queueListener);
            }
        }
        if (dd != null) {
            if (capacitylistener != null) {
                dd.addValueEventListener(capacitylistener);
            }
        }
        if (studentRef != null) {
            if (doneListener != null) {
                studentRef.child("done").addValueEventListener(doneListener);
            }

        }
    }
//    public void submit(View view)
//    {submitbutton.setVisibility(View.GONE);
//        if(adhaarno.getText().toString().trim().isEmpty()||ownernumber.getText().toString().trim().isEmpty()||ownername.getText().toString().trim().isEmpty()||owneraddress.getText().toString().trim().isEmpty()
//                ||rg.getCheckedRadioButtonId()==-1|| rc.getText().toString().trim().isEmpty() || !isPic3Selected || !isPic2Selected ||!isPic1Selected)
//        {
//            if(ownernumber.getText().toString().trim().isEmpty())
//            {
//                Toast.makeText(this, " number field is empty", Toast.LENGTH_SHORT).show();
//            }
//            if(ownername.getText().toString().trim().isEmpty())
//            {
//                Toast.makeText(this, " name field is empty", Toast.LENGTH_SHORT).show();
//            }
//            if(owneraddress.getText().toString().trim().isEmpty())
//            {
//                Toast.makeText(this, " address field is empty", Toast.LENGTH_SHORT).show();
//            }
//
//
//            if(rg.getCheckedRadioButtonId()==-1)
//            {
//                Toast.makeText(this, "gender field  is empty", Toast.LENGTH_SHORT).show();
//            }
//
//            if(ownernumber.getText().toString().length()<10)
//            {
//                Toast.makeText(this, "contact number should be 10 digits ", Toast.LENGTH_SHORT).show();
//
//
//
//
//            }
//            if(adhaarno.getText().toString().length()<12)
//            {
//                Toast.makeText(this, "adhaar no. should be 12 digits", Toast.LENGTH_SHORT).show();
//
//
//
//            }
//            if(!isPic1Selected) {
//                Toast.makeText(this, "please upload adhaar front side ", Toast.LENGTH_SHORT).show();
//            }
//            if(!isPic2Selected) {
//                Toast.makeText(this, "please upload adhaar backside side ", Toast.LENGTH_SHORT).show();
//            }
//            if(!isPic3Selected) {
//                Toast.makeText(this, "please upload profile picture is empty", Toast.LENGTH_SHORT).show();
//            }
//
//            if(!rc.getText().toString().isEmpty())
//            {long roomcap=Long.parseLong(rc.getText().toString());
//                if(roomcap>jj) {
//                    Toast.makeText(this, "your set space exceeds available room capacity", Toast.LENGTH_SHORT).show();
//
//                }
//                else if(roomcap==0)
//                {
//
//                    Toast.makeText(this, "set space cannot be zero", Toast.LENGTH_SHORT).show();
//
//
//                }
//            }
//            else if(rc.getText().toString().isEmpty())
//            {
//                Toast.makeText(this, "please enter the no. of spots you need for yourself", Toast.LENGTH_SHORT).show();
//
//            }
//
//
//            submitbutton.setVisibility(View.VISIBLE);
//        }
//        else{
//
//
//            waitDialog = new Dialog(bookActivity.this);
//            waitDialog.setCancelable(false);
//            dialogView = LayoutInflater.from(bookActivity.this)
//                    .inflate(R.layout.wait_dialog_layout, null);
//            waitDialog.setContentView(dialogView);
//            progressDialog = new Dialog(bookActivity.this);
//            progressDialog.setCancelable(false);
//            View view1 = LayoutInflater.from(bookActivity.this).inflate(R.layout.progress_dialog_layout, null);
//            progressDialog.setContentView(view1);
//
//            DatabaseReference queueRef = FirebaseDatabase.getInstance()
//                    .getReference("google")
//                    .child(ownernameoriginal)
//                    .child(owneridoriginal)
//                    .child("owner")
//                    .child("rooms").child("roomId:" + sid).child("queue");
//
//            queueRef.runTransaction(new Transaction.Handler() {
//                @NonNull
//                @Override
//                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//
//                     c=currentData.getValue(String.class);
//                    if(c==null ||c.isEmpty())
//                    {
//                        c=timeoftheday;
//                        currentData.setValue(c);
//                    }
//
//                  else
//                    {String j=c+","+timeoftheday;
//                        currentData.setValue(j);
//
//                    }
//                    return Transaction.success(currentData);                }
//
//                @Override
//                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//
//
//                    DatabaseReference dj = FirebaseDatabase.getInstance()
//                            .getReference("google")
//                            .child(name1)
//                            .child(id1)
//                            .child("student");
//
//
//// =================== CAPACITY LISTENER ===================
//               /*     DatabaseReference dd=FirebaseDatabase.getInstance().getReference("google").child(ownernameoriginal).child(owneridoriginal).child("owner").child("rooms").child("roomId:"+sid).child("capacityremaining");
//capacitylistener=new ValueEventListener() {
//    @Override
//    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//        currentremainingcapacity=snapshot.getValue(Long.class);
//        TextView tv=findViewById(R.id.rctextview);
//        tv.setText("\uD83D\uDEA8 the room has a capacity of " + currentremainingcapacity + " people,enter the no. of spots you need for yourself?");
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError error) {
//
//    }
//};
//
//dd.addValueEventListener(capacitylistener);*/
//// =================== DONE LISTENER ===================
//                    doneListener = new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            if (snapshot.exists()) {
//
//                                // ✅ Proper listener removal
//
//                                dj.child("done").removeEventListener(doneListener);
//
//                                queueRef.removeEventListener(queueListener);
//                               // dd.removeEventListener(capacitylistener);
//
//                                if(progressDialog != null && progressDialog.isShowing())
//                                    progressDialog.dismiss();
//
//                                Intent i = new Intent(bookActivity.this, studentinfoandrooms.class);
//                                startActivity(i);
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) { }
//                    };
//                    dj.child("done").addValueEventListener(doneListener);
//
//
//// =================== QUEUE LISTENER ===================
//                    queueListener = new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            String queue = snapshot.getValue(String.class);
//                            if (queue != null && !queue.isEmpty()) {
//
//                                String[] queueArray = queue.split(",");
//                                queueList = new ArrayList<>(Arrays.asList(queueArray));
//
//                                int position = queueList.indexOf(timeoftheday);
//
//                                if (position == -1) {
//                                    if (waitDialog != null && waitDialog.isShowing())
//                                        waitDialog.dismiss();
//                                    return;
//                                }
//
//                                if (position > 0) {
//                                    int secondsToWait = position * 15;
//
//                                    if(!waitDialog.isShowing())
//                                        waitDialog.show();
//
//                                    TextView waitMessage = dialogView.findViewById(R.id.wait_message);
//                                    waitMessage.setText("You are #" + (position + 1)
//                                            + " in line.\nEstimated wait: < "
//                                            + secondsToWait + " seconds.\nDo not close the app");
//
//                                } else if (position == 0) {
//                                    handler2.postDelayed(() -> {
//                                        if (waitDialog.isShowing())
//                                            waitDialog.dismiss();
//
//                                        processBooking(queueRef);
//
//                                    }, 10);
//                                }
//
//                            } else {
//                                if(waitDialog.isShowing())
//                                    waitDialog.dismiss();
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) { }
//                    };
//                    queueRef.addValueEventListener(queueListener);
//
//
//                }
//            });
//
//
//
//
//        }
//    }
//
//    String tenantnotification;
//    Long no_of_people;
//
//    String tenantname,tenantid,rid;
//    Long roomcap,roomcapreached;
//    long currentTimeMillis1;
//    long capacityremaining;
//    String ownerfcm;
//    String tenantfcm;
//
//
//    Boolean worst=false;
//    private void processBooking(DatabaseReference queueRef) {
//        Handler h=new Handler();
//        h.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(progressDialog!=null &&progressDialog.isShowing())
//                {
//                    progressDialog.dismiss();
//                    submitbutton.setVisibility(View.VISIBLE);
//                }
//            }
//        },10000);
//        if(waitDialog!=null && waitDialog.isShowing())
//        {
//            waitDialog.dismiss();
//        }
//
//
//        progressDialog = new Dialog(bookActivity.this);
//        progressDialog.setCancelable(false);
//        View view1 = LayoutInflater.from(bookActivity.this).inflate(R.layout.progress_dialog_layout, null);
//        progressDialog.setContentView(view1);
//        if (!bookActivity.this.isFinishing()) {
//            progressDialog.show();
//        }
//
//
//
//        currentTimeMillis1 = System.currentTimeMillis();
//        roomcapacityassetbytenant = Long.parseLong(rc.getText().toString());
//        if (rg.getCheckedRadioButtonId() == R.id.rbmale) {
//            gender = "male";
//        } else if (rg.getCheckedRadioButtonId() == R.id.rbfemale) {
//            gender = "female";
//        }
//        String tenantname=ownername.getText().toString();
//        String tenantaddress=owneraddress.getText().toString();
//        String tenantnumber=ownernumber.getText().toString();
//        String tenantadhaar=adhaarno.getText().toString();
//
//      DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(ownernameoriginal).child(owneridoriginal).child("owner").child("rooms").child("roomId:"+sid);
//
//
//        ds.runTransaction(new Transaction.Handler() {
//            @NonNull
//            @Override
//            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//
//                String currentqueue=currentData.child("queue").getValue(String.class);
//                if(currentqueue==null)return Transaction.success(currentData);
//                String[] queuearray=currentqueue.split(",");
//
//
//               Long currentremainingcapacity1=currentData.child("capacityremaining").getValue(Long.class);
//               if(currentremainingcapacity1==null)return Transaction.success(currentData);
//
//                if(!queuearray[0].equals(name1+"+"+id1) &&roomcapacityassetbytenant>currentremainingcapacity1)
//                {
//                    worst=true;
//                    return Transaction.abort();
//                }
//               if(!queuearray[0].equals(name1+"+"+id1))
//               {
//                   uploadmismatch=true;
//                   return Transaction.abort();
//               }
//                   if(roomcapacityassetbytenant>currentremainingcapacity1 &&currentremainingcapacity1!=0) {
//                       capacityerror = true;
//
//                       return Transaction.abort();
//                   }
//                if(roomcapacityassetbytenant>currentremainingcapacity1 &&currentremainingcapacity1==0) {
//                    zerocapacity = true;
//
//                    return Transaction.abort();
//                }
//
//
//
//
//
//
//
//
//                return Transaction.success(currentData);
//            }
//
//            @Override
//            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//                if(!committed)
//                {Log.d("in !comitted","in ! comutted");
//                    if(uploadmismatch)
//                    {Log.d("in upload mismatch","nvrnurnu");
//                        progressDialog.dismiss();
//                        Toast.makeText(bookActivity.this, "please wait for your turn", Toast.LENGTH_SHORT).show();
//
//
//
//                   uploadmismatch=false ;}
//
//                    if(errorincapacity)
//                    {Log.d("in error capacity","in error capacity");
//                        Toast.makeText(bookActivity.this, "your set spots exceeds the available capacity,please choose fewer spots", Toast.LENGTH_SHORT).show();
//errorincapacity=false;
//                    }
//                    if(zerocapacity)
//                    {Log.d("in zero","in zero");
//                        progressDialog.dismiss();
//                        Toast.makeText(bookActivity.this,"no more space available in this room",Toast.LENGTH_SHORT).show();
//                        Intent i=new Intent(bookActivity.this,studentinfoandrooms.class);
//                        startActivity(i);                    }
//                    if(worst)
//                    {progressDialog.dismiss();
//                        Toast.makeText(bookActivity.this, "error has occured,please try again", Toast.LENGTH_SHORT).show();
//                      Intent i=new Intent(bookActivity.this,studentinfoandrooms.class);
//                      startActivity(i);
//
//                    }
//
//                } else if (committed) {
//                    Log.d("in committed","in comitted");
//                    DatabaseReference ds=FirebaseDatabase.getInstance().getReference("google").child(ownernameoriginal).child(owneridoriginal).child("owner").child("rooms").child("roomId:"+sid).child("requestsforbooking");
//                    ds.runTransaction(new Transaction.Handler() {
//                        @NonNull
//                        @Override
//                        public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//                            Map<String,Object>data=new HashMap<>();
//                            data.put("ownername",ownernameoriginal);
//                            data.put("ownerid",owneridoriginal);
//                            data.put("tenantname",name1);
//                            data.put("tenantid",id1);
//                            data.put("roomcapacityassetbytenant",roomcapacityassetbytenant);
//                            data.put("name",tenantname);
//                            data.put("address",tenantaddress);
//                            data.put("number",tenantnumber);
//                            data.put("adhaar",tenantadhaar);
//                            data.put("gender",gender);
//                            data.put("duration",duration);
//                            currentData.setValue(data);
//
//                            return Transaction.success(currentData);
//                        }
//
//                        @Override
//                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//
//                        }
//                    });
//
//
//
//                }
//            }
//        });
//
//
//
//
//    }









}



