package com.palria.kujeapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.palria.kujeapp.GlobalValue;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateBusinessPageActivity extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    String userId;
    String pageId;
    String actionToTake;
    TextView textHeaderTextView;

    StorageReference appStorageReference;

    ActivityResultLauncher<Intent> openGalleryLauncher;
    ActivityResultLauncher<Intent> openCameraLauncher;
    Bitmap bitmap;
    Uri uri;

    int galleryRequestCode = 1;
    int cameraRequestCode = 2;

    boolean isVisible = false;
    boolean isInProgress = false;
    boolean isCreatePage = false;
    boolean isPageCoverPhotoIncluded = false;
    boolean isImageReplaced = false;

    String pageCategory = "";

    String retrievedCoverPhotoStorageReference;
    String retrievedCoverPhotoDownloadUrl;

    String pageProfileImageStorageReference;
    String pageImageDownloadUrl;

    MaterialButton createPageActionButton;
    FloatingActionButton pickMediaActionButton;

    ImageView pageProfileImageView;
    EditText pageDisplayNameEditText;
    //EditText pageCountryEditText;
    EditText pageLocationAddressLine1EditText;
    EditText pageMobileNumber1EditText;
    EditText pageEmailAddressEditText;
    EditText pageWebsiteAddressEditText;
    EditText pageDescriptionEditText;
    Spinner countryPickerSpinner;

    ProgressDialog progressDialog;
    ProgressDialog loadInfoProgressDialog;
    AlertDialog initDialog;
    AlertDialog confirmationDialog;
    AlertDialog successDialog;
    AlertDialog failureDialog;
    AlertDialog confirmExitDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_business_page_acivity);


        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = "FirebaseAuth.getInstance().getCurrentUser().getUid()";
        appStorageReference = FirebaseStorage.getInstance().getReference();
        textHeaderTextView = findViewById(R.id.textHeaderTextViewId);
        createPageActionButton = findViewById(R.id.createPageActionButtonId);
        pickMediaActionButton = findViewById(R.id.pickMediaActionButtonId);
        pickMediaActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(getApplicationContext(),R.menu.pick_image_menu , pickMediaActionButton, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId()==R.id.openImageGalleryId){
                            requestForPermissionAndPickImage(galleryRequestCode);
                        }else if(item.getItemId()==R.id.openCameraId){
                            requestForPermissionAndPickImage(cameraRequestCode);

                        }

                        return false;
                    }
                });
            }
        });

        createLoadInfoProgressDialog();
        manageIntentData();
        createConfirmationDialog();
        createProgressDialog();
        createSuccessDialog();
        createFailureDialog();
        createConfirmExitDialog();

        pageProfileImageView = findViewById(R.id.pageProfileImageViewId);
        pageDisplayNameEditText = findViewById(R.id.pageDisplayNameEditTextId);
        countryPickerSpinner = findViewById(R.id.countryPickerSpinnerId);
        pageLocationAddressLine1EditText = findViewById(R.id.locationAddressLine1EditTextViewId);
        pageMobileNumber1EditText = findViewById(R.id.mobileNumber1EditTextId);
        pageEmailAddressEditText = findViewById(R.id.emailAddressEditTextId);
        pageWebsiteAddressEditText = findViewById(R.id.websiteAddressEditTextId);
        pageDescriptionEditText = findViewById(R.id.pageDescriptionEditTextId);
        createPageActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmationDialog.show();

            }
        });
        initDialog  = new AlertDialog.Builder(CreateBusinessPageActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();


        openGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data = result.getData();
                    uri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        isPageCoverPhotoIncluded = true;
                        isImageReplaced = true;
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    pageProfileImageView.setImageURI(uri);


                }
            }
        });
        openCameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    isPageCoverPhotoIncluded = true;
                    isImageReplaced = true;

//                    if(android.os.Build.VERSION.SDK_INT >= 29) {
//                        handleCameraResult();
//                    }else {
                    if (result.getData() != null) {
                        Intent data = result.getData();
                        Uri uriFromCamera = data.getData();
//                            pageProfileImageView.setImageURI(uriFromCamera);
                        Bitmap bt = (Bitmap) data.getExtras().get("data");
                        if(bt != null){
                            pageProfileImageView.setImageBitmap(bt);
                        }
                    }
//                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No image captured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        prepareCountryPickerSpinner();
    }
    @Override
    public void onBackPressed() {

        confirmExitDialog.show();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == cameraRequestCode) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                fireCameraIntent();
            }
        }else if(requestCode == galleryRequestCode) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                openGallery();
            }

        }

    }
    void manageIntentData() {
        Intent intent = getIntent();
        isCreatePage = intent.getBooleanExtra("IS_CREATE_NEW_PAGE",true);
        if (!isCreatePage) {
            //The user action is page edition
            textHeaderTextView.setText("Edit page profile");
            pageId = intent.getStringExtra("PAGE_ID");
            createPageActionButton.setText("Update");
            loadInfoProgressDialog.show();
            loadPageInfoForEdition();
        }
        if (isCreatePage) {
            //The user action is page creation
            pageId = GlobalValue.getRandomString(60);
            createPageActionButton.setText("Create the page");

        }
    }

    public void setSomeViewsVisible(View view) {

        if (!isVisible) {
//            findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.VISIBLE);
            isVisible = true;
        } else {
//            findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
            isVisible = false;

        }
    }

    public void openGallery() {

        //findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);


        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        openGalleryLauncher.launch(galleryIntent);
    }
//
//    public void openCamera() {
//        isVisible = false;
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//    }

    public void fireCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraLauncher.launch(cameraIntent);
    }

    public void handleCameraResult() {
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        String[] path = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
        };
        Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, path, null, null, path[1] + " DESC");
        if (cursor.moveToFirst()) {
            String uriFromCamera = "content://media/external/images/media/" + cursor.getInt(0);
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(uriFromCamera));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            pageProfileImageView.setImageURI(Uri.parse(uriFromCamera));


        }
        cursor.close();
    }

    public void requestForPermissionAndPickImage(int requestCode) {
//        isVisible = false;
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);

        if (getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
        } else {
            if(requestCode == cameraRequestCode) {
                fireCameraIntent();
            }else if(requestCode == galleryRequestCode){
                openGallery();
            }


        }

    }

    void createProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.create();
    }

    void createLoadInfoProgressDialog() {
        loadInfoProgressDialog = new ProgressDialog(this);
        loadInfoProgressDialog.setCancelable(true);
//        loadInfoProgressDialog.setCancelable(true);
        loadInfoProgressDialog.create();
    }


    public void createConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm your action");
        builder.setMessage("Are you sure?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_error_high_24);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (pageDisplayNameEditText.getText() != null && !String.valueOf(pageDisplayNameEditText.getText()).equals("") ) {

                    validatePageAndPost();

                    progressDialog.show();
                }else{
                    pageDisplayNameEditText.setHint("You must enter page name");
                    pageDisplayNameEditText.setHintTextColor(Color.RED);
                    Toast.makeText(CreateBusinessPageActivity.this, "Fill the page name", Toast.LENGTH_SHORT).show();

                }

            }
        })
                .setNegativeButton("Edit more", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        confirmationDialog = builder.create();

        // successDialog.show();

    }

    private void toggleProgress(boolean show)
    {
        if(show){
            initDialog.show();
        }else{
            initDialog.cancel();
        }
    }
    public void createSuccessDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Action succeeded!");
        builder.setMessage("what next?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_success_circle_outline_24);
        builder.setPositiveButton("View page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                CreateNewPageActivity.this.finish();
//                Intent intent =  new Intent(getApplicationContext(), OwnerSinglePageActivity.class);
//                intent.putExtra("PAGE_ID",pageId);
//                startActivity(intent);

            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                CreateBusinessPageActivity.this.finish();
            }
        });
        successDialog = builder.create();

        // successDialog.show();

    }

    public void createFailureDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Action failed!");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_error_high_24);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               validatePageAndPost();
            }
        }).setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                CreateBusinessPageActivity.this.finish();
            }
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        failureDialog = builder.create();

        // successDialog.show();

    }
    public void createConfirmExitDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setMessage("Do you want to exit?");

        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        confirmExitDialog.cancel();

                    }
                });
            }
        }).setNeutralButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CreateBusinessPageActivity.super.onBackPressed();

            }
        });
        confirmExitDialog =builder.create();


    }

     private void createNewPage(String pageCategory, String pageCoverPhotoDownloadUrl, String pageCoverPhotoStorageReference, OnPageEditionListener onPageEditionListener){

        String pageDisplayName = pageDisplayNameEditText.getText().toString();
        String country =countryPickerSpinner.getSelectedItem().toString();
        String locationAddressLine1 = pageLocationAddressLine1EditText.getText().toString();
        String mobileNumber1 = pageMobileNumber1EditText.getText().toString();
        String emailAddress = pageEmailAddressEditText.getText().toString();
        String websiteAddress = pageWebsiteAddressEditText.getText().toString();
        String businessDescription = pageDescriptionEditText.getText().toString();
        if (!(pageDisplayName.equals(""))){

          /*  if (bitmap != null) {
                //CREATE PAGE WITH PROFILE IMAGE

                new Thread(new  Runnable(){


                    @Override
                    public void run() {

                        StorageReference pageProfileImageStorageReference = appStorageReference.child("ALL__PAGES/PAGE_PROFILE_IMAGES/" + userId +"/"+pageId+ "/.PNG");

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 2, byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        UploadTask pageProfileImageUploadTask = pageProfileImageStorageReference.putBytes(bytes);

                        pageProfileImageUploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        isInProgress = false;
                                        failureDialog.setMessage(String.valueOf(e.getMessage()));
                                        failureDialog.show();
                                        progressDialog.cancel();

                                    }
                                });

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        isInProgress = true;

                                        progressDialog.show();

                                    }
                                });


                                double uploadSize = snapshot.getTotalByteCount();
                                double uploadedSize = snapshot.getBytesTransferred();
                                double remainingSize = uploadSize - uploadedSize;
                                int uploadProgress = (int) ((100 * uploadedSize) / uploadSize);
                                // holder.progressBar.setProgress(uploadProgress);
//                                Toast.makeText(getApplicationContext(), "progressing..." + uploadProgress, Toast.LENGTH_SHORT).show();
//
//                    if (uploadProgress > 1) {
//                        dialogProgressBar.setIndeterminate(false);
//                        dialogProgressBar.setProgress(uploadProgress);
//                    }
//                    if (uploadProgress == 100) {
//                        dialogProgressBar.setIndeterminate(true);
//                    }
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                                pageProfileImageUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!(task.isSuccessful())) {
//          there is and= error here
//
//                                Toast.makeText(getApplicationContext(), String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
//                                isInProgress = false;
//                                failureDialog.setMessage(String.valueOf(task.getException()));
//                                failureDialog.show();
//                                progressDialog.cancel();

                                        }

                                        return pageProfileImageStorageReference.getDownloadUrl();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                isInProgress = false;
                                                failureDialog.setMessage(String.valueOf(e.getMessage()));
                                                failureDialog.show();
                                                progressDialog.cancel();

                                            }
                                        });
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {

//                            Toast.makeText(getApplicationContext(), "Task completed", Toast.LENGTH_SHORT).show();

                                        String pageImageDownloadUrl = String.valueOf(task.getResult());

                                        HashMap<String, Object> newPageDetails = new HashMap<>();
                                        newPageDetails.put("PAGE_DISPLAY_NAME", pageDisplayName);
                                        newPageDetails.put("PAGE_ID", pageId);
                                        newPageDetails.put("PAGE_OWNER_USER_ID", userId);
                                        newPageDetails.put("COUNTRY", country);
                                        newPageDetails.put("PAGE_CATEGORY", pageCategory);
                                        newPageDetails.put("PAGE_LOCATION_ADDRESS_LINE_1", locationAddressLine1);
                                        newPageDetails.put("PAGE_MOBILE_NUMBER_1", mobileNumber1);
                                        newPageDetails.put("PAGE_EMAIL_ADDRESS", emailAddress);
                                        newPageDetails.put("PAGE_WEBSITE_ADDRESS", websiteAddress);
                                        newPageDetails.put("PAGE_DESCRIPTION", businessDescription);
                                        newPageDetails.put("PROFILE_IMAGE_DOWNLOAD_URL", pageImageDownloadUrl);
                                        newPageDetails.put("PROFILE_IMAGE_STORAGE_REFERENCE", String.valueOf(pageProfileImageStorageReference.getPath()));
                                        newPageDetails.put("TOTAL_NUMBER_OF_POSTS", 0L);
                                        newPageDetails.put("IS_BLOCKED", false);
                                        newPageDetails.put("IS_PRIVATE", false);
                                        newPageDetails.put("DATE_CREATED", GlobalValue.getDate());
                                        newPageDetails.put("LAST_EDITED", GlobalValue.getDate());
                                        newPageDetails.put("DATE_CREATED_TIME_STAMP", FieldValue.serverTimestamp());
                                        newPageDetails.put("LAST_EDITED_TIME_STAMP", FieldValue.serverTimestamp());
                                        newPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).collection(GlobalValue.PAGES).document(pageId).collection(GlobalValue.PAGE_PROFILE).document(pageId).set(newPageDetails).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {

                                                        isInProgress = false;
                                                        failureDialog.setMessage(String.valueOf(e.getMessage()));
                                                        failureDialog.show();
                                                        progressDialog.cancel();
                                                    }
                                                });

                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {


                                                HashMap<String,Object>pageDetails = new HashMap<>();
                                                pageDetails.put("PAGE_ID",pageId);
                                                pageDetails.put("PAGE_OWNER_USER_ID",userId);
                                                pageDetails.put("WORKERS_ID_ARRAY",FieldValue.arrayUnion(GlobalValue.BUSINESS_ADMIN_ID));
                                                pageDetails.put("PAGE_DISPLAY_NAME",pageDisplayName);
                                                pageDetails.put("SEARCH_KEYWORD_ARRAY",  GlobalValue.generateSearchKeyWords(pageDisplayName,pageDisplayName.length()));
                                                pageDetails.put("LAST_EDITED_TIME_STAMP",FieldValue.serverTimestamp());
                                                pageDetails.put("DATE_CREATED_TIME_STAMP",FieldValue.serverTimestamp());
                                                pageDetails.put("LAST_EDITED",GlobalValue.getDate());
                                                pageDetails.put("DATE_CREATED",GlobalValue.getDate());
                                                firebaseFirestore.collection(GlobalValue.ALL_AVAILABLE_PAGES).document(pageId).set(pageDetails,SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {

                                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                isInProgress = false;
                                                                failureDialog.setMessage(String.valueOf(e.getMessage()));
                                                                failureDialog.show();
                                                                progressDialog.cancel();

                                                            }
                                                        });


                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        //increment total number of pages an owner has created so far .\
                                                        HashMap<String, Object> profileDetails = new HashMap<>();
                                                        profileDetails.put("TOTAL_NUMBER_OF_VIEWS",0L);
                                                        profileDetails.put("TOTAL_NUMBER_OF_FOLLOWERS",0L);
                                                        profileDetails.put("TOTAL_NUMBER_OF_SHARES", 0L);
                                                        firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).collection(GlobalValue.PAGES).document(pageId).set(profileDetails,SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                HashMap<String, Object> newPageDetails = new HashMap<>();
                                                                newPageDetails.put("LAST_PAGE_CREATED_DATE",GlobalValue.getDate());
                                                                newPageDetails.put("LAST_PAGE_CREATED_ID", pageId);
                                                                newPageDetails.put("LAST_PAGE_CREATED_TIME_STAMP", FieldValue.serverTimestamp());
                                                                newPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());
                                                                newPageDetails.put("TOTAL_NUMBER_OF_PAGES", FieldValue.increment(1L));


                                                                firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).set(newPageDetails, SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                isInProgress = false;
                                                                                failureDialog.setMessage(String.valueOf(e.getMessage()));
                                                                                failureDialog.show();
                                                                                progressDialog.cancel();

                                                                            }
                                                                        });

                                                                    }
                                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                isInProgress = false;
                                                                                progressDialog.cancel();
                                                                                successDialog.show();

                                                                            }
                                                                        });

                                                                    }
                                                                });
                                                            }
                                                        });

                                                    }
                                                });

                                            }
                                        });


                                    }
                                });


                            }


                        });

                    }
                }).start();
            }*/
//            else {

//create page without image

                WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
                DocumentReference pageDocumentReference = firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(pageId);
                HashMap<String, Object> newPageDetails = new HashMap<>();
                newPageDetails.put("PAGE_DISPLAY_NAME", pageDisplayName);
                newPageDetails.put("PAGE_ID", pageId);
                newPageDetails.put("PAGE_OWNER_USER_ID", userId);
                newPageDetails.put("COUNTRY", country);
                newPageDetails.put("PAGE_LOCATION_ADDRESS_LINE_1", locationAddressLine1);
                newPageDetails.put("PAGE_MOBILE_NUMBER_1", mobileNumber1);
                newPageDetails.put("PAGE_EMAIL_ADDRESS", emailAddress);
                newPageDetails.put("PAGE_CATEGORY", pageCategory);
                newPageDetails.put("PAGE_WEBSITE_ADDRESS", websiteAddress);
                newPageDetails.put("PAGE_DESCRIPTION", businessDescription);
                newPageDetails.put("PROFILE_IMAGE_DOWNLOAD_URL", pageCoverPhotoDownloadUrl);
                newPageDetails.put("PROFILE_IMAGE_STORAGE_REFERENCE", pageCoverPhotoStorageReference);
                newPageDetails.put("TOTAL_NUMBER_OF_POSTS", 0L);
                newPageDetails.put("TOTAL_NUMBER_OF_VIEWS",0L);
                newPageDetails.put("TOTAL_NUMBER_OF_FOLLOWERS",0L);
                newPageDetails.put("TOTAL_NUMBER_OF_SHARES", 0L);
                newPageDetails.put("IS_BLOCKED", false);
                newPageDetails.put("IS_PRIVATE", false);
                newPageDetails.put("SEARCH_KEYWORD_ARRAY",  GlobalValue.generateSearchKeyWords(pageDisplayName,pageDisplayName.length()));
//                newPageDetails.put("DATE_CREATED", GlobalValue.getDate());
//                newPageDetails.put("LAST_EDITED", GlobalValue.getDate());
                newPageDetails.put("DATE_CREATED_TIME_STAMP", FieldValue.serverTimestamp());
                newPageDetails.put("LAST_EDITED_TIME_STAMP", FieldValue.serverTimestamp());
                newPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());
                writeBatch.set(pageDocumentReference,newPageDetails,SetOptions.merge());


                DocumentReference ownerDocumentReference = firebaseFirestore.collection(GlobalValue.ALL_USERS).document(userId);
                HashMap<String, Object> pageDetails = new HashMap<>();
//                newPageDetails.put("LAST_PAGE_CREATED_DATE",GlobalValue.getDate());
                newPageDetails.put("LAST_PAGE_CREATED_ID", pageId);
                newPageDetails.put("LAST_PAGE_CREATED_TIME_STAMP", FieldValue.serverTimestamp());
                newPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());
                newPageDetails.put("TOTAL_NUMBER_OF_PAGES", FieldValue.increment(1L));
                writeBatch.update(ownerDocumentReference,pageDetails);


writeBatch.commit().addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        isInProgress = false;
        failureDialog.setMessage(String.valueOf(e.getMessage()));
        failureDialog.show();
        progressDialog.cancel();
    }
}).addOnSuccessListener(new OnSuccessListener<Void>() {
    @Override
    public void onSuccess(Void unused) {
        isInProgress = false;
        progressDialog.cancel();
        successDialog.show();
    }
});


/*
                firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).collection(GlobalValue.PAGES).document(pageId).collection(GlobalValue.PAGE_PROFILE).document(pageId).set(newPageDetails).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                isInProgress = false;
                                failureDialog.setMessage(String.valueOf(e.getMessage()));
                                failureDialog.show();
                                progressDialog.cancel();
                            }
                        });

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {


                        HashMap<String,Object>pageDetails = new HashMap<>();
                        pageDetails.put("PAGE_ID",pageId);
                        pageDetails.put("PAGE_OWNER_USER_ID",userId);
                        pageDetails.put("WORKERS_ID_ARRAY",FieldValue.arrayUnion(GlobalValue.BUSINESS_ADMIN_ID));
                        pageDetails.put("PAGE_DISPLAY_NAME",pageDisplayName);
                        pageDetails.put("SEARCH_KEYWORD_ARRAY",  GlobalValue.generateSearchKeyWords(pageDisplayName,pageDisplayName.length()));
                        pageDetails.put("LAST_EDITED_TIME_STAMP",FieldValue.serverTimestamp());
                        pageDetails.put("DATE_CREATED_TIME_STAMP",FieldValue.serverTimestamp());
                        pageDetails.put("LAST_EDITED",GlobalValue.getDate());
                        pageDetails.put("DATE_CREATED",GlobalValue.getDate());
                        firebaseFirestore.collection(GlobalValue.ALL_AVAILABLE_PAGES).document(pageId).set(pageDetails,SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        isInProgress = false;
                                        failureDialog.setMessage(String.valueOf(e.getMessage()));
                                        failureDialog.show();
                                        progressDialog.cancel();

                                    }
                                });


                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                //increment total number of pages an owner has created so far .\
                                HashMap<String, Object> profileDetails = new HashMap<>();
                                profileDetails.put("TOTAL_NUMBER_OF_VIEWS",0L);
                                profileDetails.put("TOTAL_NUMBER_OF_FOLLOWERS",0L);
                                profileDetails.put("TOTAL_NUMBER_OF_SHARES", 0L);
                                firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).collection(GlobalValue.PAGES).document(pageId).set(profileDetails,SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        HashMap<String, Object> newPageDetails = new HashMap<>();
                                        newPageDetails.put("LAST_PAGE_CREATED_DATE",GlobalValue.getDate());
                                        newPageDetails.put("LAST_PAGE_CREATED_ID", pageId);
                                        newPageDetails.put("LAST_PAGE_CREATED_TIME_STAMP", FieldValue.serverTimestamp());
                                        newPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());
                                        newPageDetails.put("TOTAL_NUMBER_OF_PAGES", FieldValue.increment(1L));


                                        firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).set(newPageDetails, SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        isInProgress = false;
                                                        failureDialog.setMessage(String.valueOf(e.getMessage()));
                                                        failureDialog.show();
                                                        progressDialog.cancel();

                                                    }
                                                });

                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        isInProgress = false;
                                                        progressDialog.cancel();
                                                        successDialog.show();

                                                    }
                                                });

                                            }
                                        });
                                    }
                                });

                            }
                        });

                    }
                });
*/

//            }

        } else {
            Toast.makeText(CreateBusinessPageActivity.this, "Fill the page name", Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
        }

    }

    private void loadPageInfoForEdition() {

        firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(pageId).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadInfoProgressDialog.cancel();
            }
        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    String pageDisplayName = String.valueOf(documentSnapshot.get("PAGE_DISPLAY_NAME"));
                    String pageProfileImageDownloadUrl = String.valueOf(documentSnapshot.get("PROFILE_IMAGE_DOWNLOAD_URL"));
                    String pageProfileImageStorageReference = String.valueOf(documentSnapshot.get("PROFILE_IMAGE_STORAGE_REFERENCE"));
                    String dateCreated = String.valueOf(documentSnapshot.get("DATE_CREATED"));
                    String lastEdited = String.valueOf(documentSnapshot.get("LAST_EDITED"));
                    String totalNumberOfPosts = String.valueOf(documentSnapshot.get("TOTAL_NUMBER_OF_POSTS"));
                    String country = String.valueOf(documentSnapshot.get("COUNTRY"));
                    String locationAddressLine1 = String.valueOf(documentSnapshot.get("PAGE_LOCATION_ADDRESS_LINE_1"));
                    String mobileNumber1 = String.valueOf(documentSnapshot.get("PAGE_MOBILE_NUMBER_1"));
                    String emailAddress = String.valueOf(documentSnapshot.get("PAGE_EMAIL_ADDRESS"));
                    String websiteAddress = String.valueOf(documentSnapshot.get("PAGE_WEBSITE_ADDRESS"));
                    String businessDescription = String.valueOf(documentSnapshot.get("PAGE_DESCRIPTION"));

                    if (pageProfileImageDownloadUrl != null) {
                        Picasso.get().load(pageProfileImageDownloadUrl).error(R.drawable.ic_baseline_person_24).into(pageProfileImageView);
                    }
                    pageDisplayNameEditText.setText(pageDisplayName);
//                    pageCountryEditText.setText(country);
                    pageMobileNumber1EditText.setText(mobileNumber1);
                    pageEmailAddressEditText.setText(emailAddress);
                    pageLocationAddressLine1EditText.setText(locationAddressLine1);
                    pageWebsiteAddressEditText.setText(websiteAddress);
                    pageDescriptionEditText.setText(businessDescription);
                    loadInfoProgressDialog.cancel();
                    retrievedCoverPhotoDownloadUrl = pageProfileImageDownloadUrl;
                    retrievedCoverPhotoStorageReference = pageProfileImageStorageReference;

                    if(!country.equals("null")){
                        ArrayList<String> countryArrayList = new ArrayList<>();
                        countryArrayList.add(0,country);
                        ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<String>(CreateBusinessPageActivity.this,R.layout.support_simple_spinner_dropdown_item,GlobalValue.getCountryArrayList(countryArrayList));
                        countryPickerSpinner.setAdapter(countryArrayAdapter);
                    }

                }else{
                    Toast.makeText(getApplicationContext(), "No record found", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void editPage(String pageCategory, String pageCoverPhotoDownloadUrl, String pageCoverPhotoStorageReference, OnPageEditionListener onPageEditionListener){

        String pageDisplayName = pageDisplayNameEditText.getText().toString();
        String country =countryPickerSpinner.getSelectedItem().toString();
        String locationAddressLine1 = pageLocationAddressLine1EditText.getText().toString();
        String mobileNumber1 = pageMobileNumber1EditText.getText().toString();
        String emailAddress = pageEmailAddressEditText.getText().toString();
        String websiteAddress = pageWebsiteAddressEditText.getText().toString();
        String businessDescription = pageDescriptionEditText.getText().toString();

        if (pageDisplayNameEditText.getText() != null && !pageDisplayName.equals("")) {

  /*          if (bitmap != null) {
                //EDIT PAGE WITH PROFILE IMAGE
                if (isImageReplaced) {

                    StorageReference pageProfileImageStorageReference = appStorageReference.child("ALL__PAGES/PAGE_PROFILE_IMAGES/" + userId +"/"+pageId+ "/.PNG");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 2, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    UploadTask pageProfileImageUploadTask = pageProfileImageStorageReference.putBytes(bytes);

                    pageProfileImageUploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isInProgress = false;
                            failureDialog.setMessage(String.valueOf(e.getMessage()));
                            failureDialog.show();
                            progressDialog.cancel();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            isInProgress = true;

                            progressDialog.show();


                            double uploadSize = snapshot.getTotalByteCount();
                            double uploadedSize = snapshot.getBytesTransferred();
                            double remainingSize = uploadSize - uploadedSize;
                            int uploadProgress = (int) ((100 * uploadedSize) / uploadSize);
                            // holder.progressBar.setProgress(uploadProgress);
                            Toast.makeText(getApplicationContext(), "progressing..." + uploadProgress, Toast.LENGTH_SHORT).show();
//
//                    if (uploadProgress > 1) {
//                        dialogProgressBar.setIndeterminate(false);
//                        dialogProgressBar.setProgress(uploadProgress);
//                    }
//                    if (uploadProgress == 100) {
//                        dialogProgressBar.setIndeterminate(true);
//                    }
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            pageProfileImageUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!(task.isSuccessful())) {
//          there is and= error here
//
//                                Toast.makeText(getApplicationContext(), String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
//                                isInProgress = false;
//                                failureDialog.setMessage(String.valueOf(task.getException()));
//                                failureDialog.show();
//                                progressDialog.cancel();

                                    }

                                    return pageProfileImageStorageReference.getDownloadUrl();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isInProgress = false;
                                    failureDialog.setMessage(String.valueOf(e.getMessage()));
                                    failureDialog.show();
                                    progressDialog.cancel();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
//                            Toast.makeText(getApplicationContext(), "Task completed", Toast.LENGTH_SHORT).show();
                                    if (task.isSuccessful()) {
                                        String pageImageDownloadUrl = String.valueOf(task.getResult());

                                        HashMap<String, Object> editPageDetails = new HashMap<>();
                                        editPageDetails.put("PAGE_DISPLAY_NAME", pageDisplayName);
                                        editPageDetails.put("PAGE_ID", pageId);
                                        editPageDetails.put("PAGE_OWNER_USER_ID", userId);
                                        editPageDetails.put("COUNTRY", country);
                                        editPageDetails.put("PAGE_LOCATION_ADDRESS_LINE_1", locationAddressLine1);
                                        editPageDetails.put("PAGE_MOBILE_NUMBER_1", mobileNumber1);
                                        editPageDetails.put("PAGE_EMAIL_ADDRESS", emailAddress);
                                        editPageDetails.put("PAGE_WEBSITE_ADDRESS", websiteAddress);
                                        editPageDetails.put("PAGE_DESCRIPTION", businessDescription);
                                        editPageDetails.put("PROFILE_IMAGE_DOWNLOAD_URL", pageImageDownloadUrl);
                                        editPageDetails.put("PROFILE_IMAGE_STORAGE_REFERENCE", String.valueOf(pageProfileImageStorageReference.getPath()));
                                        editPageDetails.put("LAST_EDITED", GlobalValue.getDate());
                                        editPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());

                                        firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).collection(GlobalValue.PAGES).document(pageId).collection(GlobalValue.PAGE_PROFILE).document(pageId).set(editPageDetails).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                isInProgress = false;
                                                failureDialog.setMessage(String.valueOf(e.getMessage()));
                                                failureDialog.show();
                                                progressDialog.cancel();

                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                HashMap<String,Object>pageDetails = new HashMap<>();
                                                pageDetails.put("PAGE_ID",pageId);
                                                pageDetails.put("PAGE_OWNER_USER_ID",userId);
                                                pageDetails.put("PAGE_DISPLAY_NAME",pageDisplayName);
                                                pageDetails.put("SEARCH_KEYWORD_ARRAY",  GlobalValue.generateSearchKeyWords(pageDisplayName,pageDisplayName.length()));
                                                pageDetails.put("LAST_EDITED_TIME_STAMP",FieldValue.serverTimestamp());
                                                pageDetails.put("LAST_EDITED",GlobalValue.getDate());
                                                firebaseFirestore.collection(GlobalValue.ALL_AVAILABLE_PAGES).document(pageId).set(pageDetails,SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        isInProgress = false;
                                                        failureDialog.setMessage(String.valueOf(e.getMessage()));
                                                        failureDialog.show();
                                                        progressDialog.cancel();


                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                        isInProgress = false;
                                                        progressDialog.cancel();
                                                        successDialog.show();

                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        isInProgress = false;
                                        failureDialog.setMessage(String.valueOf(task.getException()));
                                        failureDialog.show();
                                        progressDialog.cancel();

                                    }

                                }
                            });


                        }


                    });
                }
                else {
                    //when editing and the image is untouched, then the original profileImageDownloadUrl & profileImageStorageReference should be maintained.

                    HashMap<String, Object> editPageDetails = new HashMap<>();
                    editPageDetails.put("PAGE_DISPLAY_NAME", pageDisplayName);
                    editPageDetails.put("PAGE_ID", pageId);
                    editPageDetails.put("PAGE_OWNER_USER_ID", userId);
                    editPageDetails.put("COUNTRY", country);
                    editPageDetails.put("PAGE_LOCATION_ADDRESS_LINE_1", locationAddressLine1);
                    editPageDetails.put("PAGE_MOBILE_NUMBER_1", mobileNumber1);
                    editPageDetails.put("PAGE_EMAIL_ADDRESS", emailAddress);
                    editPageDetails.put("PAGE_WEBSITE_ADDRESS", websiteAddress);
                    editPageDetails.put("PAGE_DESCRIPTION", businessDescription);
                    editPageDetails.put("PROFILE_IMAGE_DOWNLOAD_URL", pageImageDownloadUrl);
                    editPageDetails.put("PROFILE_IMAGE_STORAGE_REFERENCE", String.valueOf(pageProfileImageStorageReference));
                    editPageDetails.put("LAST_EDITED", GlobalValue.getDate());
                    editPageDetails.put("LAST_EDITED_TIME_STAMP", FieldValue.serverTimestamp());
                    editPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());

                    firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).collection(GlobalValue.PAGES).document(pageId).collection(GlobalValue.PAGE_PROFILE).document(pageId).set(editPageDetails).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isInProgress = false;
                            failureDialog.setMessage(String.valueOf(e.getMessage()));
                            failureDialog.show();
                            progressDialog.cancel();

                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                            HashMap<String,Object>pageDetails = new HashMap<>();
                            pageDetails.put("PAGE_ID",pageId);
                            pageDetails.put("PAGE_OWNER_USER_ID",userId);
                            pageDetails.put("PAGE_DISPLAY_NAME",pageDisplayName);
                            pageDetails.put("SEARCH_KEYWORD_ARRAY",  GlobalValue.generateSearchKeyWords(pageDisplayName,pageDisplayName.length()));
                            pageDetails.put("LAST_EDITED_TIME_STAMP",FieldValue.serverTimestamp());
                            pageDetails.put("LAST_EDITED",GlobalValue.getDate());
                            firebaseFirestore.collection(GlobalValue.ALL_AVAILABLE_PAGES).document(pageId).set(pageDetails,SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    isInProgress = false;
                                    failureDialog.setMessage(String.valueOf(e.getMessage()));
                                    failureDialog.show();
                                    progressDialog.cancel();

                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    isInProgress = false;
                                    progressDialog.cancel();
                                    successDialog.show();
                                }
                            });

                        }
                    });
                }


            }
            else {

                //EDIT PAGE WITHOUT PROFILE IMAGE

                if (isImageReplaced) {
                    pageImageDownloadUrl = null;
                    pageProfileImageStorageReference = null;
                }
                HashMap<String, Object> editPageDetails = new HashMap<>();
                editPageDetails.put("PAGE_DISPLAY_NAME", pageDisplayName);
                editPageDetails.put("COUNTRY", country);
                editPageDetails.put("PAGE_LOCATION_ADDRESS_LINE_1", locationAddressLine1);
                editPageDetails.put("PAGE_MOBILE_NUMBER_1", mobileNumber1);
                editPageDetails.put("PAGE_EMAIL_ADDRESS", emailAddress);
                editPageDetails.put("PAGE_WEBSITE_ADDRESS", websiteAddress);
                editPageDetails.put("PAGE_DESCRIPTION", businessDescription);
                editPageDetails.put("PROFILE_IMAGE_DOWNLOAD_URL", null);
                editPageDetails.put("PROFILE_IMAGE_STORAGE_REFERENCE", null);
//                editPageDetails.put("LAST_EDITED", GlobalValue.getDate());
                editPageDetails.put("LAST_EDITED_TIME_STAMP", FieldValue.serverTimestamp());
                editPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());

                firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.BUSINESS_ADMIN_ID).collection(GlobalValue.PAGES).document(pageId).collection(GlobalValue.PAGE_PROFILE).document(pageId).set(editPageDetails).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        isInProgress = false;
                        failureDialog.setMessage(String.valueOf(e.getMessage()));
                        failureDialog.show();
                        progressDialog.cancel();

                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        HashMap<String,Object>pageDetails = new HashMap<>();
                        pageDetails.put("PAGE_ID",pageId);
                        pageDetails.put("PAGE_OWNER_USER_ID",userId);
                        pageDetails.put("PAGE_DISPLAY_NAME",pageDisplayName);
                        pageDetails.put("SEARCH_KEYWORD_ARRAY",  GlobalValue.generateSearchKeyWords(pageDisplayName,pageDisplayName.length()));
                        pageDetails.put("LAST_EDITED_TIME_STAMP",FieldValue.serverTimestamp());
                        pageDetails.put("LAST_EDITED",GlobalValue.getDate());
                        firebaseFirestore.collection(GlobalValue.ALL_AVAILABLE_PAGES).document(pageId).set(pageDetails,SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                isInProgress = false;
                                failureDialog.setMessage(String.valueOf(e.getMessage()));
                                failureDialog.show();
                                progressDialog.cancel();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                isInProgress = false;
                                progressDialog.cancel();
                                successDialog.show();

                            }
                        });
                    }
                });


            }
*/

            WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
            DocumentReference pageDocumentReference = firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(pageId);
            HashMap<String, Object> newPageDetails = new HashMap<>();
            newPageDetails.put("PAGE_DISPLAY_NAME", pageDisplayName);
//            newPageDetails.put("PAGE_ID", pageId);
//            newPageDetails.put("PAGE_OWNER_USER_ID", userId);
            newPageDetails.put("COUNTRY", country);
            newPageDetails.put("PAGE_LOCATION_ADDRESS_LINE_1", locationAddressLine1);
            newPageDetails.put("PAGE_MOBILE_NUMBER_1", mobileNumber1);
            newPageDetails.put("PAGE_EMAIL_ADDRESS", emailAddress);
            newPageDetails.put("PAGE_CATEGORY", pageCategory);
            newPageDetails.put("PAGE_WEBSITE_ADDRESS", websiteAddress);
            newPageDetails.put("PAGE_DESCRIPTION", businessDescription);
            newPageDetails.put("PROFILE_IMAGE_DOWNLOAD_URL", pageCoverPhotoDownloadUrl);
            newPageDetails.put("PROFILE_IMAGE_STORAGE_REFERENCE", pageCoverPhotoStorageReference);
            newPageDetails.put("SEARCH_KEYWORD_ARRAY",  GlobalValue.generateSearchKeyWords(pageDisplayName,pageDisplayName.length()));
//                newPageDetails.put("LAST_EDITED", GlobalValue.getDate());
            newPageDetails.put("LAST_EDITED_TIME_STAMP", FieldValue.serverTimestamp());
            newPageDetails.put("TIME_STAMP", FieldValue.serverTimestamp());
            writeBatch.update(pageDocumentReference,newPageDetails);

            writeBatch.commit().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    isInProgress = false;
                    failureDialog.setMessage(String.valueOf(e.getMessage()));
                    failureDialog.show();
                    progressDialog.cancel();
                }
            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    isInProgress = false;
                    progressDialog.cancel();
                    successDialog.show();
                }
            });

        }else{
            Toast.makeText(CreateBusinessPageActivity.this, "Fill the page name", Toast.LENGTH_SHORT).show();
            progressDialog.cancel();
        }

    }

    private void uploadPageCoverPhoto(CoverPhotoUploadListener coverPhotoUploadListener){
        StorageReference pageProfileImageStorageReference = appStorageReference.child("ALL_PAGES"+"/"+pageId+ "/.PNG");

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 2, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        UploadTask pageProfileImageUploadTask = pageProfileImageStorageReference.putBytes(bytes);

        pageProfileImageUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                coverPhotoUploadListener.onFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pageProfileImageUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return pageProfileImageStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String coverPhotoDownloadUrl = String.valueOf(task.getResult());
                        String coverPhotoStorageReference_2 = pageProfileImageStorageReference.getPath();
                        coverPhotoUploadListener.onSuccess(coverPhotoDownloadUrl,coverPhotoStorageReference_2);
                    }
                });
            }
        });

    }

    private void validatePageAndPost(){

        if(isPageCoverPhotoIncluded){
            if(isCreatePage){
                uploadPageCoverPhoto(new CoverPhotoUploadListener() {
                    @Override
                    public void onSuccess(String coverPhotoDownloadUrl,String coverPhotoStorageReference) {
                        createNewPage(pageCategory,coverPhotoDownloadUrl, coverPhotoStorageReference,new OnPageEditionListener() {
                            @Override
                            public void onSuccess() {

                                toggleProgress(false);

                                GlobalValue.showAlertMessage("success",
                                        CreateBusinessPageActivity.this,
                                        "Page Created Successfully.",
                                        "You have successfully created your page,thanks go ahead and add manage your page ");

                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                toggleProgress(false);

                                GlobalValue.showAlertMessage("error",
                                        CreateBusinessPageActivity.this,
                                        "Page Creation Failed.",
                                        errorMessage);
                            }
                        });
                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        toggleProgress(false);

                        GlobalValue.showAlertMessage("error",
                                CreateBusinessPageActivity.this,
                                "Page Creation Failed.",
                                errorMessage);

                    }
                });
            }
            else{
                if(isPageCoverPhotoIncluded){
                    uploadPageCoverPhoto(new CoverPhotoUploadListener() {
                        @Override
                        public void onSuccess(String coverPhotoDownloadUrl,String coverPhotoStorageReference) {
                            editPage(pageCategory,coverPhotoDownloadUrl, coverPhotoStorageReference,new OnPageEditionListener() {
                                @Override
                                public void onSuccess() {
                                    toggleProgress(false);

                                    GlobalValue.showAlertMessage("success",
                                            CreateBusinessPageActivity.this,
                                            "Page Edited Successfully.",
                                            "You have successfully edited your page,thanks go ahead and add manage your page ");


                                }

                                @Override
                                public void onFailed(String errorMessage) {
                                    toggleProgress(false);

                                    GlobalValue.showAlertMessage("error",
                                            CreateBusinessPageActivity.this,
                                            "Page Edition Failed.",
                                            errorMessage);


                                }
                            });
                        }

                        @Override
                        public void onFailed(String errorMessage) {


                        }
                    });
                }
                else{
                    editPage(pageCategory,retrievedCoverPhotoDownloadUrl,retrievedCoverPhotoStorageReference, new OnPageEditionListener() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onFailed(String errorMessage) {

                        }
                    });
                }
            }
        }
        else{
            if(isCreatePage){
                createNewPage(pageCategory,"","", new OnPageEditionListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailed(String errorMessage) {

                    }
                });
            }
            else{
                editPage(pageCategory,"","", new OnPageEditionListener() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailed(String errorMessage) {


                    }
                });
            }
        }
    }

    void prepareCountryPickerSpinner(){

        ArrayAdapter<String> countryArrayAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,GlobalValue.getCountryArrayList(null));
        countryPickerSpinner.setAdapter(countryArrayAdapter);
    }



    void testThis(){

        HashMap<String,Object>pageDetails = new HashMap<>();
        pageDetails.put("PAGE_ID",pageId);
        pageDetails.put("PAGE_OWNER_USER_ID",userId);
        pageDetails.put("LAST_EDITED_TIME_STAMP",FieldValue.serverTimestamp());
        pageDetails.put("DATE_CREATED_TIME_STAMP",FieldValue.serverTimestamp());
        pageDetails.put("LAST_EDITED",GlobalValue.getDate());
        pageDetails.put("DATE_CREATED",GlobalValue.getDate());
        firebaseFirestore.collection("ALL_AVAILABLE_PAGES").document(pageId).set(pageDetails,SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    interface CoverPhotoUploadListener{
        void onSuccess(String coverPhotoDownloadUrl,String coverPhotoStorageReference);
        void onFailed(String errorMessage);

    }
    interface OnPageEditionListener{
        void onSuccess();
        void onFailed(String errorMessage);

    }


}