package com.example.bookandpostroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Editphotosandaddphotos extends AppCompatActivity {
    public static final String message="message";
    int counter=0;
    String currenttag;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ActivityResultLauncher<Intent> pictureLauncher;
    private ImageView currentSelectedImageView;
    private int currentImageIndex;
    private String name1;

    private String id1;

    Button addMoreButton;
    private List<Uri> images=new ArrayList<>();
    private Uri [] imagesfinal;
    private List<String> deletedimages=new ArrayList<>();
    private String ss;
    private List<String> addimages=new ArrayList<>();
    private List<String> imagenames=new ArrayList<>();
    private String[] names;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 101;
    private String sss;
    private int imagecount=0;
    private List<Long> sortednames=new ArrayList<>();
    private int max;


    private Long j;

    private LinearLayout ll;
    private void applySystemBarPadding() {
        SystemBarsUtil barsUtil = new SystemBarsUtil(this);

        int statusBarHeight = barsUtil.getStatusBarHeight();
        int navBarHeight = barsUtil.getActualNavigationBarHeight();

        // Find your NestedScrollView
        NestedScrollView scrollView = findViewById(R.id.main);

        // Apply padding to avoid system bars
        scrollView.setPadding(
                0,
                0,  // Top padding for status bar
                0,
                navBarHeight      // Bottom padding for navigation bar
        );

        // Optional: Log the values for debugging

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.teal));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.teal));
        }
        setContentView(R.layout.activity_editphotosandaddphotos);
        applySystemBarPadding();
        ss=getIntent().getStringExtra(message);
        sss=ss.split("room")[1];
        initializeViews();
        setupImagePicker();
        getpictures();
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        } else {
            // Optional: if user opens the app already in landscape,
            // you can still force portrait if you want:
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }



         addMoreButton = findViewById(R.id.addMoreButton);
        addMoreButton.setOnClickListener(v -> addmoreimages());


    }
    public void addmoreimages()
    {
        addImageView2();


    }
    ImageButton deleteButton2;
    public void addImageView2()
    {

        int frameId = View.generateViewId();
        int imageId = View.generateViewId();
        int deleteButtonId = View.generateViewId();
        int chooseButtonId = View.generateViewId();

        float scale = getResources().getDisplayMetrics().density;
        int heightInPixels = (int) (300 * scale + 0.5f);
        int forbutton=(int)(40*scale+0.5f);
        int margin5=(int)(5*scale+0.5f);
        int margin0=(int)(0*scale+0.5f);
        // Parent layout where new views will be added
        LinearLayout parentLayout = findViewById(R.id.ll);

        // Create FrameLayout
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        frameLayout.setId(frameId);
        String d= String.valueOf(counter);
        // Create ImageView
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(heightInPixels, heightInPixels));
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setId(imageId);
        imageView.setTag(d);
        imageView.setImageResource(R.drawable.roompic);
        // Create Delete Button
         deleteButton2 = new ImageButton(this);
        FrameLayout.LayoutParams deleteParams = new FrameLayout.LayoutParams(forbutton, forbutton);
        deleteParams.gravity = Gravity.TOP | Gravity.END;
        deleteParams.setMargins(margin0, margin5, margin5, margin0);
        deleteButton2.setLayoutParams(deleteParams);
        deleteButton2.setImageResource(R.drawable.delete);

        deleteButton2.setBackground(null);
        deleteButton2.setId(deleteButtonId);

        deleteButton2.setTag(d);
        deleteButton2.setVisibility(View.GONE);
        addMoreButton.setVisibility(View.GONE);

        // Create Choose Button
        Button chooseButton = new Button(this);
        LinearLayout.LayoutParams chooseParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        chooseParams.gravity = Gravity.CENTER; // Center horizontally in parent
        chooseParams.topMargin = 10;

        chooseButton.setText("Choose");
        chooseButton.setTag(d);


        chooseButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        chooseButton.setBackground(ContextCompat.getDrawable(this, R.drawable.fancy_button_background));
        chooseButton.setId(chooseButtonId);

        // **Set click listeners**
        deleteButton2.setOnClickListener(this::deleteimage);  // Calls existing delete method
        chooseButton.setOnClickListener(this::showImagePickerDialog);
        // Calls existing choose method

        // Add views to FrameLayout
        frameLayout.addView(imageView);
        frameLayout.addView(deleteButton2);

        // Add FrameLayout and Choose button to parent layout
        parentLayout.addView(frameLayout);
        parentLayout.addView(chooseButton);

        counter++;


    }

    public void getpictures()
    {
        String name=ss.split("idstarts")[0];
        String id=ss.split("idstarts")[1].split("idends")[0];
        name1=name;
        id1=id;

        String number=ss.split("room")[1];
        StorageReference dss=FirebaseStorage.getInstance().getReference("ownerPhotos")
                .child(name).child(id).child("images"+number);
        dss.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                if (listResult.getItems().isEmpty()) {
                    Toast.makeText(Editphotosandaddphotos.this, "No images found", Toast.LENGTH_SHORT).show();
                    return;
                }

                AtomicInteger counter = new AtomicInteger(0); // Track progress
                int totalItems = listResult.getItems().size();

                for (StorageReference fileRef : listResult.getItems()) {
                    String imageName = fileRef.getName();
                    imagenames.add(imageName);
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            images.add(uri); // Add URI to the list

                            // Check if all URIs are fetched
                            if (counter.incrementAndGet() == totalItems) {
                                onAllUrisFetched(); // Call method after fetching all URLs
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("FirebaseStorage", "Failed to get URL", e);
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Editphotosandaddphotos.this, "Sorry, an error occurred while fetching the images", Toast.LENGTH_SHORT).show();
            }
        });






    }
    private void onAllUrisFetched() {

        imagesfinal=images.toArray(new Uri[0]);
        names=imagenames.toArray(new String[0]);


        for (int i =0; i < imagesfinal.length; i++) {

            addImageView(i);  // Calls function to add new views dynamically
        }

        for(int i=0;i<names.length;i++)
        {
            String s=names[i].split(name1+id1)[1];
            long number = Long.parseLong(s);
            sortednames.add(number);


        }
        long maxNumber = Collections.max(sortednames);
        counter=(int)maxNumber+1;



    }
    private void addImageView(final int index) {
        // Generate dynamic IDs for new views
        int frameId = View.generateViewId();
        int imageId = View.generateViewId();
        int deleteButtonId = View.generateViewId();
        int chooseButtonId = View.generateViewId();

        // Parent layout where new views will be added
        LinearLayout parentLayout = findViewById(R.id.ll);
        float scale = getResources().getDisplayMetrics().density;
        int heightInPixels = (int) (300 * scale + 0.5f);
        int margin5=(int)(5*scale+0.5f);
        int margin0=(int)(0*scale+0.5f);
        int forbutton=(int)(70*scale+0.5f);
        // Create FrameLayout
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        frameLayout.setId(frameId);

        // Create ImageView
        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(heightInPixels, heightInPixels));
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setId(imageId);
        String jj=names[index].split(name1+id1)[1];

        imageView.setTag(jj);
        Glide.with(this)
                .load(imagesfinal[index])
                .into(imageView);
        // Create Delete Button
        // Create CardView to wrap the delete button
        CardView deleteCard = new CardView(this);
        FrameLayout.LayoutParams cardParams = new FrameLayout.LayoutParams(forbutton, forbutton);
        cardParams.gravity = Gravity.TOP | Gravity.END;
        cardParams.setMargins(margin0, margin5, margin5, margin0);
        deleteCard.setLayoutParams(cardParams);
        deleteCard.setCardBackgroundColor(Color.WHITE);
        deleteCard.setRadius(100f); // Make it fully circular
        deleteCard.setCardElevation(8 * scale); // Elevation for pop-up effect
        deleteCard.setPreventCornerOverlap(true);
        deleteCard.setUseCompatPadding(true);

// Create ImageButton inside CardView
        ImageButton deleteButton = new ImageButton(this);
        deleteButton.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        deleteButton.setImageResource(R.drawable.delete);
        deleteButton.setBackground(null);
        deleteButton.setPadding((int)(8*scale), (int)(8*scale), (int)(8*scale), (int)(8*scale));
        deleteButton.setId(deleteButtonId);
        deleteButton.setTag(jj);

// Add click listener
        deleteButton.setOnClickListener(this::deleteimage);

// Assemble
        deleteCard.addView(deleteButton);


        // Create Choose Button
        Button chooseButton = new Button(this);
        LinearLayout.LayoutParams chooseParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        chooseParams.gravity = Gravity.CENTER; // Center horizontally in parent
        chooseParams.topMargin = 10;
        chooseButton.setText("Choose");

        chooseButton.setTag(jj);


        chooseButton.setTextColor(ContextCompat.getColor(this, R.color.white));
        chooseButton.setBackground(ContextCompat.getDrawable(this, R.drawable.fancy_button_background));
        chooseButton.setId(chooseButtonId);


        // **Set click listeners**
        deleteButton.setOnClickListener(this::deleteimage);  // Calls existing delete method
        chooseButton.setOnClickListener(this::showImagePickerDialog);

        // Add views to FrameLayout
        frameLayout.addView(imageView);
        frameLayout.addView(deleteCard);

        // Add FrameLayout and Choose button to parent layout
        parentLayout.addView(frameLayout);
        parentLayout.addView(chooseButton);


    }
    public void deleteimage(View view) {
        String tag = (String) view.getTag();
        deletedimages.add(tag);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to continue?");

        // Yes Button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Action for "Yes"
                ProgressDialog pd=new ProgressDialog(Editphotosandaddphotos.this);
                pd.setMessage("please wait");
                pd.setCancelable(false);
                pd.show();
                StorageReference srf=FirebaseStorage.getInstance().getReference("ownerPhotos")
                        .child(name1).child(id1).child("images"+sss);
                srf.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int itemCount = listResult.getItems().size(); // Count of files
                        if(itemCount>6)
                        {

                            StorageReference srff=FirebaseStorage.getInstance().getReference("ownerPhotos")
                                    .child(name1).child(id1).child("images"+sss).child(name1+id1+tag);
                            srff.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    LinearLayout parentLayout = findViewById(R.id.ll);
                                    List<View> viewsToRemove = new ArrayList<>();

                                    for (int i = 0; i < parentLayout.getChildCount(); i++) {
                                        View child = parentLayout.getChildAt(i);

                                        // Check for FrameLayout containing ImageView and Delete Button
                                        if (child instanceof FrameLayout) {
                                            FrameLayout frameLayout = (FrameLayout) child;

                                            // Check if any child inside the FrameLayout has the matching tag
                                            for (int j = 0; j < frameLayout.getChildCount(); j++) {
                                                View insideChild = frameLayout.getChildAt(j);
                                                if (tag.equals(insideChild.getTag())) {
                                                    viewsToRemove.add(frameLayout); // Mark FrameLayout for removal
                                                    break; // No need to check further
                                                }
                                            }
                                        }
                                        // Check for Choose Button
                                        else if (child instanceof Button && tag.equals(child.getTag())) {
                                            viewsToRemove.add(child); // Mark Choose button for removal
                                        }
                                    }

                                    // Remove all marked views
                                    for (View v : viewsToRemove) {
                                        parentLayout.removeView(v);
                                    }
                                    pd.dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                }
                            });



                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(Editphotosandaddphotos.this, "cannot delete atleast six images are required to be there,if u want to delete previous ones,add more images below", Toast.LENGTH_LONG).show();

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("FirebaseStorage", "Error retrieving items: " + e.getMessage());
                    }
                });

            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();
    }





    private void initializeViews() {
        ll = findViewById(R.id.ll);


    }


    private void setupImagePicker() {
        pictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null ) {
                            // Show progress dialog here for gallery picks
                            Dialog progressDialog = new Dialog(Editphotosandaddphotos.this);
                            View view = LayoutInflater.from(Editphotosandaddphotos.this)
                                    .inflate(R.layout.progress_dialog_layout, null);
                            progressDialog.setContentView(view);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            ImageView imageView = findViewById(android.R.id.content).findViewWithTag(currenttag);

                            imageView.setImageURI(imageUri);
                            loadAndCompressImage(imageUri, progressDialog);
                        }
                    }
                }
        );
    }




    private void showImagePickerDialog(View view) {
        String[] options = {"Pick from files", "Capture with Camera"};
        currenttag=(String) view.getTag();
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
            if (currentPhotoPath != null && currenttag != null) {
                // Show progress dialog immediately when processing camera image
                Dialog progressDialog = new Dialog(Editphotosandaddphotos.this);
                View view = LayoutInflater.from(Editphotosandaddphotos.this).inflate(R.layout.progress_dialog_layout, null);
                progressDialog.setContentView(view);
                progressDialog.setCancelable(false);
                progressDialog.show();

                File f = new File(currentPhotoPath);
                Uri contentUri = Uri.fromFile(f);
                ImageView imageView = findViewById(android.R.id.content).findViewWithTag(currenttag);
                imageView.setImageURI(contentUri);

                // Modified loadAndCompressImage to accept the progress dialog
                loadAndCompressImage(contentUri, progressDialog);
            }
        }
    }


    private void loadAndCompressImage(Uri imageUri, Dialog existingDialog) {
        // Use existing dialog if provided, otherwise create new one
        Toast.makeText(this, ""+currenttag, Toast.LENGTH_SHORT).show();
        Dialog progressDialog1 = existingDialog != null ? existingDialog : new Dialog(Editphotosandaddphotos.this);
        if (existingDialog == null) {
            View view1 = LayoutInflater.from(Editphotosandaddphotos.this).inflate(R.layout.progress_dialog_layout, null);
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
                            Toast.makeText(Editphotosandaddphotos.this, "Image size is too small, try larger picture", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        StorageReference ds = FirebaseStorage.getInstance()
                                .getReference("ownerPhotos")
                                .child(name1)
                                .child(id1)
                                .child("images" + sss)
                                .child(name1 + id1 + currenttag);

                        ds.putBytes(compressedData)
                                .addOnSuccessListener(taskSnapshot -> {
                                    imagecount = imagecount + 1;
                                    progressDialog1.dismiss();
                                    deleteButton2.setVisibility(View.VISIBLE);
                                    addMoreButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(Editphotosandaddphotos.this, "Image successfully uploaded", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog1.dismiss();
                                    Toast.makeText(Editphotosandaddphotos.this, "Couldn't upload image, please try again", Toast.LENGTH_SHORT).show();
                                });
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        progressDialog1.dismiss();
                    }
                });
    }
    public void submit(View view) {



    }

}