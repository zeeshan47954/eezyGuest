package com.example.bookandpostroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class addActivity2 extends AppCompatActivity {
    public static final String message = "hello";
    public static final String roomlocation1 = "roomlocation";
    public static final String district1 = "district";
    public static final String institution1 = "institution";
    public static final String roomname1 = "roomname";
    public static final String roomcap1 = "roomcap";
    public static final String latitude1 = "latitude";
    public static final String longitude1 = "longitude";
    public static final String consentsforchange1 = "consentsforchange";
    public static final String bookedbygender1 = "bookedbygender";
    public static final String roomprice1 = "roomprice";
    public static final String roomType1 = "roomType";
    public static final String rules1 = "rules";
    public static final String facilities1 = "facilities";
    public static final String roomcapacityreached1 = "roomcapacityreached";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> pictureLauncher;
    private ImageView currentSelectedImageView;
    private int currentImageIndex;
    private String name1;
    private String fcmtoken;
    private String id1;
    private long l;
    private long m;
private String ownername;
private String ownernumber;
private String owneraddress;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;
    private long sss;
    private int imagecount=0;
private long ddd;

    private int counter = 6; // Starting after the initial 6 static views
    private Long j;
    private ImageView roompic1, roompic2, roompic3, roompic4, roompic5, roompic6;
    private Button rpic1, rpic2, rpic3, rpic4, rpic5, rpic6;
    private GridLayout gridLayout;
    String roomlocation;
    String district;
    String institution;
    String roomname;
    String roomcap;

    String latitude;
    String longitude;
    long consentsforchange;
    String bookedbygender;
    String roomprice;
    String roomType;
    String rules;
    String facilities;
    String roomcapacityreached;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add2);

        initializeViews();
        setupImagePicker();
        setupClickListeners();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            name1 = account.getDisplayName();
            id1 = account.getId();
String jj=getIntent().getStringExtra(message);
            Intent intent = getIntent();
            roomlocation = intent.getStringExtra(addActivity2.roomlocation1);
            district = intent.getStringExtra(addActivity2.district1);
            institution = intent.getStringExtra(addActivity2.institution1);
            roomname = intent.getStringExtra(addActivity2.roomname1);

            roomcap =  intent.getStringExtra(addActivity2.roomcap1);

            latitude = intent.getStringExtra(addActivity2.latitude1);
            longitude = intent.getStringExtra(addActivity2.longitude1);
            consentsforchange = intent.getLongExtra(addActivity2.consentsforchange1, 0L);
            bookedbygender = intent.getStringExtra(addActivity2.bookedbygender1);
             roomprice=intent.getStringExtra(addActivity2.roomprice1);


            roomType = intent.getStringExtra(addActivity2.roomType1);
            rules = intent.getStringExtra(addActivity2.rules1);
            facilities = intent.getStringExtra(addActivity2.facilities1);
            roomcapacityreached = intent.getStringExtra(addActivity2.roomcapacityreached1);


sss=Long.parseLong(jj.split("room")[1]);



            DatabaseReference dj=FirebaseDatabase.getInstance().getReference("google").child(name1).child(id1).child("owner");
            dj.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
fcmtoken=snapshot.child("fcmtoken").getValue(String.class);
                    ddd=snapshot.child("roomsposted").getValue(Long.class);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        Button addMoreButton = findViewById(R.id.addMoreButton);
        addMoreButton.setOnClickListener(v -> addRowToGrid(gridLayout, name1, id1));
    }
    private void initializeViews() {
        gridLayout = findViewById(R.id.main);
        roompic1 = findViewById(R.id.roompic1);
        roompic2 = findViewById(R.id.roompic2);
        roompic3 = findViewById(R.id.roompic3);
        roompic4 = findViewById(R.id.roompic111);
        roompic5 = findViewById(R.id.roompic222);
        roompic6 = findViewById(R.id.roompic333);

        rpic1 = findViewById(R.id.roompic1button);
        rpic2 = findViewById(R.id.roompic2button);
        rpic3 = findViewById(R.id.roompic3button);
        rpic4 = findViewById(R.id.roompic111button);
        rpic5 = findViewById(R.id.roompic222button);
        rpic6 = findViewById(R.id.roompic333button);
    }
    private void addRowToGrid(GridLayout gridLayout, String name, String id) {
        int columnCount = gridLayout.getColumnCount();
        List<ImageView> newImageViews = new ArrayList<>();
        List<Button> newButtons = new ArrayList<>();
        float scale = getResources().getDisplayMetrics().density;
        int paddingInPixels = (int) (16 * scale + 0.5f); // 16dp to pixels

        // Set padding to the GridLayout
        gridLayout.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);
        float scale1= getResources().getDisplayMetrics().density;
        int heightInPixels = (int) (150 * scale + 0.5f); // 150dp to pixels

// Set the height in pixels

        // Add ImageViews
        for (int i = 0; i < columnCount; i++) {
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
            layoutParams.columnSpec = GridLayout.spec(i, 1f);
            layoutParams.width = 0;
            layoutParams.height = heightInPixels;
            layoutParams.setMargins(8, 8, 8, 8);
            layoutParams.setGravity(Gravity.FILL_HORIZONTAL);

            ImageView imageView = new ImageView(this);
            imageView.setId(View.generateViewId());
            imageView.setTag(counter + (i + 1));
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(R.drawable.roompic);
            imageView.setAdjustViewBounds(true);

            gridLayout.addView(imageView);
            newImageViews.add(imageView);
        }

        // Add Buttons
        for (int i = 0; i < columnCount; i++) {
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
            layoutParams.rowSpec = GridLayout.spec(GridLayout.UNDEFINED);
            layoutParams.columnSpec = GridLayout.spec(i, 1f);
            layoutParams.width = 0;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.setMargins(8, 8, 8, 8);
            layoutParams.setGravity(Gravity.FILL_HORIZONTAL);

            Button button = new Button(this);
            button.setId(View.generateViewId());
            button.setTag(counter + (i + 1));
            button.setLayoutParams(layoutParams);
            button.setText("Choose");
            button.setBackgroundResource(R.drawable.fancy_button_background);
            button.setTextColor(getResources().getColor(R.color.white));

            gridLayout.addView(button);
            newButtons.add(button);
        }

        // Set up click listeners for new buttons
        for (int i = 0; i < columnCount; i++) {
            final ImageView imageView = newImageViews.get(i);
            final Button button = newButtons.get(i);
            final int index = counter + (i + 1);

            button.setOnClickListener(v -> {
                currentSelectedImageView = imageView;
                currentImageIndex = index;
                showImagePickerDialog();
            });
        }

        counter += 3; // Increment counter as per your logic
    }

    private void setupImagePicker() {
        pictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null && currentSelectedImageView != null) {
                            // Show progress dialog here for gallery picks
                            Dialog progressDialog = new Dialog(addActivity2.this);
                            View view = LayoutInflater.from(addActivity2.this)
                                    .inflate(R.layout.progress_dialog_layout, null);
                            progressDialog.setContentView(view);
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            currentSelectedImageView.setImageURI(imageUri);
                            loadAndCompressImage(imageUri, currentImageIndex, progressDialog);
                        }
                    }
                }
        );
    }

    private void setupClickListeners() {
        setupImageButtonClickListener(rpic1, roompic1, 1);
        setupImageButtonClickListener(rpic2, roompic2, 2);
        setupImageButtonClickListener(rpic3, roompic3, 3);
        setupImageButtonClickListener(rpic4, roompic4, 4);
        setupImageButtonClickListener(rpic5, roompic5, 5);
        setupImageButtonClickListener(rpic6, roompic6, 6);
    }

    private void setupImageButtonClickListener(Button button, ImageView imageView, int index) {
        button.setOnClickListener(v -> {
            currentSelectedImageView = imageView;
            currentImageIndex = index;
            showImagePickerDialog();
        });
    }

    private void showImagePickerDialog() {
        String[] options = {"Pick from files", "Capture with Camera"};
        new AlertDialog.Builder(this)
                .setTitle("Choose an option")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        pickImageIntent.setType("image/*");
                        pictureLauncher.launch(pickImageIntent);
                    } else if (which == 1) {
                        if (checkPermissions()) {
                            openCamera();
                            // Permissions granted, continue with functionality
                        } else {
                            requestPermissions();
                        }


                    }
                })
                .show();
    }

    private boolean checkPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        return cameraPermission == PackageManager.PERMISSION_GRANTED && storagePermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        // Requesting both Camera and Storage permissions if they are not granted
        ActivityCompat.requestPermissions(this,
                new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                boolean cameraGranted = false;
                boolean storageGranted = false;

                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equals(android.Manifest.permission.CAMERA) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        cameraGranted = true;
                    }
                    if (permissions[i].equals(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        storageGranted = true;
                    }
                }

                if (cameraGranted && storageGranted) {
                    // Both permissions granted, open the camera
                    openCamera();
                } else {
                    // Permissions denied, show a toast or message
                    Toast.makeText(this, "Camera and Storage permissions are required to capture and save images", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            // Create the File where the photo should go
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.bookandpostroom.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } catch (IOException ex) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",        /* suffix */
                storageDir     /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private String currentPhotoPath;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (currentPhotoPath != null && currentSelectedImageView != null) {
                // Show progress dialog immediately when processing camera image
                Dialog progressDialog = new Dialog(addActivity2.this);
                View view = LayoutInflater.from(addActivity2.this).inflate(R.layout.progress_dialog_layout, null);
                progressDialog.setContentView(view);
                progressDialog.setCancelable(false);
                progressDialog.show();

                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                currentSelectedImageView.setImageURI(contentUri);

                // Modified loadAndCompressImage to accept the progress dialog
                loadAndCompressImage(contentUri, currentImageIndex, progressDialog);
            }
        }
    }


    private void loadAndCompressImage(Uri imageUri, int uriIndex, Dialog existingDialog) {
        // Use existing dialog if provided, otherwise create new one
        Dialog progressDialog1 = existingDialog != null ? existingDialog : new Dialog(addActivity2.this);
        if (existingDialog == null) {
            View view1 = LayoutInflater.from(addActivity2.this).inflate(R.layout.progress_dialog_layout, null);
            progressDialog1.setContentView(view1);
            progressDialog1.setCancelable(false);
            progressDialog1.show();
        }

        Glide.with(this)
                .asBitmap()
                .load(imageUri)
                .override(1024, 1024)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int quality = 100;
                        resource.compress(Bitmap.CompressFormat.JPEG, quality, baos);

                        while (baos.toByteArray().length / 1024 > 20 && quality > 20) {
                            baos.reset();
                            quality -= 10;
                            resource.compress(Bitmap.CompressFormat.JPEG, quality, baos);
                        }

                        byte[] compressedData = baos.toByteArray();
                        if (compressedData.length / 1024 < 5) {
                            progressDialog1.dismiss();
                            Toast.makeText(addActivity2.this, "Image size is too small, try larger picture", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        StorageReference ds = FirebaseStorage.getInstance()
                                .getReference("ownerPhotos")
                                .child(name1)
                                .child(id1)
                                .child("images" + sss)
                                .child(name1 + id1 + uriIndex);

                        ds.putBytes(compressedData)
                                .addOnSuccessListener(taskSnapshot -> {
                                    imagecount = imagecount + 1;
                                    progressDialog1.dismiss();
                                    Toast.makeText(addActivity2.this, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog1.dismiss();
                                    Toast.makeText(addActivity2.this, "Couldn't upload image, please try again", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        progressDialog1.dismiss();
                    }
                });
    }
    public void submit(View view)
    {

        if(imagecount>=6) {
            updateDatabase();

        }
        else{
            Toast.makeText(this, "no. of images uploaded is less than 6", Toast.LENGTH_LONG).show();
        }

    }

    private void updateDatabase() {
        // Show progress dialog
        Dialog progressDialog1 = new Dialog(addActivity2.this);
        View view1 = LayoutInflater.from(addActivity2.this).inflate(R.layout.progress_dialog_layout, null);
        progressDialog1.setContentView(view1);
        progressDialog1.setCancelable(false);
        progressDialog1.show();

        DatabaseReference fgg=FirebaseDatabase.getInstance().getReference("pendinguploadedrooms");
        fgg.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                Map<String, Object> ownerData = new HashMap<>();
Long d=currentData.getChildrenCount();
if(d==null)d=0l;
d++;
long roomcapacity=Long.parseLong(roomcap);
long priceofroom=Long.parseLong(roomprice);
long capacityofroomreached=Long.parseLong(roomcapacityreached);
                ownerData.put("entryno",d);
                ownerData.put("roomlocation", roomlocation);
                ownerData.put("district", district);
                ownerData.put("institution", institution);
                ownerData.put("roomname", roomname);
                ownerData.put("roomcap", roomcapacity);
                ownerData.put("latitude", latitude);
                ownerData.put("longitude", longitude);
                ownerData.put("consentsforchange", consentsforchange);
                ownerData.put("bookedbygender", bookedbygender);
                ownerData.put("roomprice", priceofroom);
                ownerData.put("roomType", roomType);
                ownerData.put("rules", rules);
                ownerData.put("facilities", facilities);
                ownerData.put("roomcapacityreached", capacityofroomreached);
                ownerData.put("ownername", name1);
                ownerData.put("ownerid", id1);
                ownerData.put("roomsexplored",sss);
                ownerData.put("roomsposted",ddd);

                ownerData.put("fcmtoken",fcmtoken);
                ownerData.put("completed","completed");

currentData.child("room"+d).setValue(ownerData);

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                progressDialog1.dismiss(); // Dismiss progress dialog after completion

                // Navigate to Activity5
                Intent i = new Intent(addActivity2.this, Activity5.class);
                startActivity(i);
            }
        });




        // Reference to user's data in Fire

        // Update roomsexplored and roomsposted c
    }

    // Function to handle room transaction after roomCount is updated

}