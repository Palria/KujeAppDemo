


package com.palria.kujeapp;



import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegKitConfig;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback;
import com.arthenica.ffmpegkit.ReturnCode;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class AddNewServiceActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    String userId;
    AlertDialog alertDialog;
    Spinner categorySpinner;
    ArrayList<String> categoryList = new ArrayList<>();
    String category = "PROVISION SHOP";
    ArrayList<String> imageDownloadUrlList = new ArrayList<>();
    ArrayList<Uri> imageGalleryUriList = new ArrayList<>();
    int numberOfImagesUploaded=0;
    int numberOfImagesEligibleForUpload = 0;
    int numberOfImagesFailed=0;
    OnProductUploadListener onProductUploadListener;
    boolean isEditPage = false;
    boolean isPrivate = false;
    boolean isApproved = true;
    boolean isProductSubmission = false;
    boolean isProductApproval = false;
    ImageButton popUpImageButton;
    TextView headerTextView;
    ImageButton backButton;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 2002;
    private final int GALLERY_PERMISSION_REQUEST_CODE = 23;
    private final int VIDEO_PERMISSION_REQUEST_CODE = 20;
    Switch visibilitySwitch;
//    ProgressBar  progressIndicator;

    //    ProgressBar  topPostProgressIndicator;
    ExtendedFloatingActionButton cameraFloatingButton;
    //    LinearLayout  chooseImageLinearLayout;
//    Button openVideoGalleryButton;
    ShimmerFrameLayout shimmerLayout;
    StorageReference appStorageReference;
    UploadTask productUploadTask;
    boolean isTotalNumberOfPostsIncremented = false;
    private  int successCounter=0;
    boolean isVisible=false;
//    TextView textHeaderTextView;


    boolean isUploadNewImage = false;
    String pageId;
    String productOwnerUserId;
    String pagePosterId;
    boolean isPageThePoster = true;
    //    String postId;
    Button postActionButton;
    TextInputEditText postTitleEditText,productLocationEditText,phoneEditText,emailEditText,residentialAddressEditText,postDescriptionEditText;
    String postTitle;
    String location;
    String phone;
    String email;
    String postDescription;
    String productPrice;
    boolean isFirstImage=true;
    int threeImageUploadCounter=0;
    long numberOfImagesUploadCounter=0L;
    int totalNumberOfItems=0;
    int itemsTagCounter=0;
    ProgressDialog progressDialog;
    AlertDialog successDialog;
    AlertDialog videoTrimDialog;
    AlertDialog videoTrimFailedDialog;
    AlertDialog videoClearDialog;
    AlertDialog confirmProductAdditionDialog;
    AlertDialog confirmExitDialog;
    AlertDialog confirmMakeNewPostDialog;
    byte[] bytes;


    StorageReference postVideoStorageReference = null;
    StorageMetadata imageStorageMetaData ;
    StorageMetadata videoStorageMetaData ;


String imageDownloadUrl = "";

    ActivityResultLauncher<Intent> openGalleryLauncher;
    ActivityResultLauncher<Intent> openVideoGalleryLauncher;
    ActivityResultLauncher<Intent> openCameraLauncher;


    LinearLayout containerLinearLayout;
//    LinearLayout addNewVideoLinearLayout;

    //    ArrayList<String> searchKeywordArrayList = new ArrayList<>();
    ArrayList<String> searchAnyMatchKeywordArrayList = new ArrayList<>();
    ArrayList<String> searchVerbatimKeywordArrayList = new ArrayList<>();
    ArrayList<String> hashTagKeywordArrayList = new ArrayList<>();
    Uri uri;
    //    private int totalNumberOfImages;
    ArrayList<ExoPlayer> activeExoPlayerArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_service);

        firebaseFirestore=FirebaseFirestore.getInstance();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        productOwnerUserId = userId;

        popUpImageButton =findViewById(R.id.popUpImageButtonId);
        categorySpinner =findViewById(R.id.categorySpinnerId);
        backButton =findViewById(R.id.backButton);
        popUpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(AddNewServiceActivity.this,R.menu.post_new_activity_pop_up_menu , popUpImageButton, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {

                        if(item.getItemId() == R.id.newPostMenuId){
                            confirmMakeNewPostDialog.show();
                        }

                        return true;
                    }
                });
                GlobalValue.hideKeyboard(getApplicationContext());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewServiceActivity.super.onBackPressed();
            }
        });
        //be careful to remove this at production Time
//         userId="testUID";
        manageIntentData();

        onProductUploadListener = new OnProductUploadListener() {
            @Override
            public void onSuccess() {
                //TODO : SEND NOTIFICATION TO THE PRODUCT NOTIFICATION LISTENERS
                //carries the info about the quiz
//                ArrayList<String> modelInfo = new ArrayList<>();
//                modelInfo.add(pageId);
//
//                ArrayList<String> recipientIds = GlobalValue.getProductNotificationListenersIdList();
//
//                //fires out the notification
//                GlobalValue.sendNotificationToUsers(GlobalValue.NOTIFICATION_TYPE_PRODUCT_POSTED,GlobalValue.getRandomString(60),recipientIds,modelInfo,postTitle,"New Product has been posted",null);


                Toast.makeText(AddNewServiceActivity.this, "Product uploaded!", Toast.LENGTH_SHORT).show();
                toggleProgress(false);
//                GlobalValue.showAlertMessage("success",getApplicationContext(),"Product Added","Your product was successfully added");

                progressDialog.cancel();
                successDialog.show();

            }

            @Override
            public void onFailed(String errorMessage) {
                Toast.makeText(AddNewServiceActivity.this, "Product failed to upload!", Toast.LENGTH_SHORT).show();
                toggleProgress(false);
                progressDialog.cancel();
//                GlobalValue.showAlertMessage("error",getApplicationContext(),"Product failed to Added","Your product failed to add, please try again");
                initConfirmAddProductDialog(true,errorMessage);
            }
        };

        cameraFloatingButton = findViewById(R.id.cameraFloatingButtonId);
        cameraFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(getApplicationContext(), R.menu.pick_image_menu, cameraFloatingButton, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {
                        if(item.getItemId() == R.id.openImageGalleryId){
                            openGallery(cameraFloatingButton);
                        }
                        else if(item.getItemId() == R.id.openCameraId){
                            openCamera(cameraFloatingButton);
                        }
                        return true;
                    }
                });
            }
        });
        alertDialog = new AlertDialog.Builder(AddNewServiceActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();

        appStorageReference= FirebaseStorage.getInstance().getReference();
        if(!isEditPage) {
            pageId = GlobalValue.getRandomString(60);
        }

        postActionButton = findViewById(R.id.postNewActionButtonId);
        postActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    confirmProductAdditionDialog.show();


            }
        });


        headerTextView=findViewById(R.id.headerTextViewId);
        visibilitySwitch=findViewById(R.id.visibilitySwitchId);
        postTitleEditText=findViewById(R.id.postTitleEditTextId);
        productLocationEditText=findViewById(R.id.productLocationEditTextId);
        phoneEditText=findViewById(R.id.phoneEditTextId);
        emailEditText=findViewById(R.id.emailEditTextId);
        residentialAddressEditText=findViewById(R.id.residentialAddressEditTextId);
        postDescriptionEditText=findViewById(R.id.postDescriptionEditTextId);
        postDescriptionEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch(motionEvent.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        return false;

                }


                return false;
            }
        });
/*
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        productContentRecyclerView=findViewById(R.id.productContentRecyclerViewId);
        addProductCustomRecyclerViewAdapter=new AddProductCustomRecyclerViewAdapter(OwnerBusinessAddProductActivity.this, imageDataArrayList, firebaseFirestore, userId, appStorageReference);
        productContentRecyclerView.setLayoutManager(linearLayoutManager);
        productContentRecyclerView.setAdapter(addProductCustomRecyclerViewAdapter);
*/

        containerLinearLayout = findViewById(R.id.containerLinearLayoutId);
        shimmerLayout = findViewById(R.id.shimmerLayout);

        openGalleryLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data=result.getData();
                   uri = data.getData();
                        displayPostMedia(AddNewServiceActivity.this,containerLinearLayout,uri, null, false, 0);
isUploadNewImage = true;


                    //addProductCustomRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
        openCameraLauncher=  registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK) {

//                    if(android.os.Build.VERSION.SDK_INT >= 29) {
//                        handleCameraResult();
//                    }else {
                    if (result.getData() != null) {
                        Intent data = result.getData();
                        Bitmap  bitmapFromCamera =(Bitmap) data.getExtras().get("data");

//                            uriArrayList.add(uriFromCamera);
                        if(bitmapFromCamera != null) {
                            Uri cameraUri = GlobalValue.getImageUri(AddNewServiceActivity.this,bitmapFromCamera,10);

                            displayPostMedia(AddNewServiceActivity.this, containerLinearLayout, cameraUri, bitmapFromCamera, false,  0);
                        }
                        isUploadNewImage = true;

                    }
                    // }
                }else{
                    Toast.makeText(getApplicationContext(), "No image captured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        visibilitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isPrivate = !b;
            }
        });

        imageStorageMetaData = new StorageMetadata.Builder().setContentType("image/png").build();

        if(isEditPage){
            fetchProductDataForEdition();
            headerTextView.setText("Edit business");
            postActionButton.setText("Save changes");
        }else{
            if(isProductSubmission){
                headerTextView.setText("Submit your business for approval");
                postActionButton.setText("Submit");
                isApproved = false;
                stopLoader();

            }else if(isProductApproval) {
                fetchProductDataForEdition();
                headerTextView.setText("Approve this business");
                postActionButton.setText("Approve");
            }else{
                stopLoader();

            }
        }
        initConfirmAddProductDialog(false,"");
        createSuccessDialog();
        createProgressDialog();
        createConfirmExitDialog();
        prepareCategorySpinner();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    @Override
    public void onBackPressed(){
        confirmExitDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                fireCameraIntent();
            }
            if (requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
                fireGalleryIntent();
            }
        }
    }

    public void manageIntentData(){
        Intent intent=getIntent();
        pageId = intent.getStringExtra(GlobalValue.PAGE_ID);
        isEditPage = intent.getBooleanExtra(GlobalValue.IS_EDITION,false);

    }




    public void uploadInBackground() {
        postTitle = postTitleEditText.getText().toString();
        location = productLocationEditText.getText().toString();
        phone = phoneEditText.getText().toString();
        email = emailEditText.getText().toString();
        postDescription = postDescriptionEditText.getText().toString();
//        isApproved = true;

        //  if(!(postTitle.isEmpty() || postDescription.isEmpty())) {
        if (!postTitle.isEmpty()) {
            searchAnyMatchKeywordArrayList = GlobalValue.generateSearchAnyMatchKeyWords(postTitle);
            searchVerbatimKeywordArrayList = GlobalValue.generateSearchVerbatimKeyWords(postTitle);
            hashTagKeywordArrayList = GlobalValue.generateHashTagKeyWords(postTitle);

        }

        if (!postDescription.isEmpty()) {
            ArrayList<String> postDescriptionSearchKeywordArray = GlobalValue.generateSearchKeyWords(postDescription, 20);


            ArrayList<String> postDescriptionHashTagKeywordArray = GlobalValue.generateHashTagKeyWords(postDescription);
//                hashTagKeywordArrayList = GlobalValue.generateHashTagKeyWords(postTitle);

            if (postDescriptionHashTagKeywordArray.size() != 0) {
                for (int i = 0; i < postDescriptionHashTagKeywordArray.size(); i++) {
                    if (!hashTagKeywordArrayList.contains(postDescriptionHashTagKeywordArray.get(i))) {
                        hashTagKeywordArrayList.add(postDescriptionHashTagKeywordArray.get(i));
                    }
                }
            }


        }

        if(GlobalValue.isConnectedOnline(this)) {


            Thread backgroundThread = new Thread(new Runnable() {
                @Override
                public void run() {
//                    if (isPageThePoster) {
                    if (true) {
                        //user is making post using his page
//                        postToPageDirectory(getApplicationContext());
                        toggleProgress(true);
                        uploadImages();
                    } else {
                        //user is making post using his main profile account
//                        postToMainUserDirectory(getApplicationContext());

                    }

                }
            });

            backgroundThread.start();

        }else{
            Toast.makeText(this, "No network! please try to connect", Toast.LENGTH_SHORT).show();
        }
    }

    public void setSomeViewsVisible(View view){
        switch(view.getId()){
            case R.id.cameraFloatingButtonId:
                if(!isVisible) {
//                    findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.VISIBLE);
                    isVisible = true;
                }else{
//                    findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
                    isVisible = false;
                }
                break;
        }
    }

    public void openGallery(View view){
        requestForPermission(GALLERY_PERMISSION_REQUEST_CODE);

    }
    public void openCamera(View view){
        requestForPermission(CAMERA_PERMISSION_REQUEST_CODE);
    }

    public void fireGalleryIntent(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        openGalleryLauncher.launch(galleryIntent);
    }
    public void fireCameraIntent(){
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraLauncher.launch(cameraIntent);
    }
    public void handleCameraResult(){
        ContentResolver contentResolver=getApplicationContext().getContentResolver();
        String[] path =new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
        };
        Cursor cursor=contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,path,null,null,path[1]+" DESC");
        if(cursor.moveToFirst()) {
            String uriFromCamera = "content://media/external/images/media/" + cursor.getInt(0);


            displayPostMedia(AddNewServiceActivity.this,containerLinearLayout,Uri.parse(uriFromCamera), null, false, 2);

        }
        cursor.close();
    }
    public void requestForPermission(int requestCode){
        if(getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},requestCode);
        }else{
            if(requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                fireCameraIntent();
            }
            if(requestCode == GALLERY_PERMISSION_REQUEST_CODE) {
                fireGalleryIntent();
            }
        }



    }


    private void toggleProgress(boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    alertDialog.show();
                }else{
                    alertDialog.cancel();
                }
            }
        });

    }



    void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait => Creating business...");

        progressDialog.create();

    }

    public void createSuccessDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String title = "Business Successfully Created!";
        if(isEditPage){
            title = "Business Successfully Edited!";
        }

        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_baseline_success_circle_outline_24);

            builder.setMessage("Done!");
            builder.setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //  successDialog.cancel();
                    //openGallery(/*unnecessary view*/productCollectionNameEditText);
                    AddNewServiceActivity.this.finish();
                }
            });
        successDialog =builder.create();

        // successDialog.show();

    }

    public void initConfirmAddProductDialog(boolean isRetry, String errorMessage){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String button = "Confirm";
        String message = "Confirm to create the business";
        if(isEditPage){
            message = "Confirm to edit the business";
        }
        if(isProductApproval){
            message = "Confirm to approve the business";
        }
        if(isProductSubmission){
            message = "Confirm to submit the business";
        }

        if(isRetry){
            button = "Retry";
            message = "Your business Failed,"+errorMessage+" Please try again";
        }

        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                            toggleProgress(true);
                            uploadInBackground();
                            postActionButton.setEnabled(false);


                    }
                });
            }
        }).setNeutralButton("Not yet",null);
        confirmProductAdditionDialog =builder.create();
        if(isRetry){
            confirmProductAdditionDialog.show();
        }


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
                AddNewServiceActivity.super.onBackPressed();

            }
        });
        confirmExitDialog =builder.create();


    }


    void displayPostMedia(Context context, ViewGroup viewGroup,Uri uri, Bitmap bitmap, boolean isBitmap,int position){

        View holder = LayoutInflater.from(context).inflate(R.layout.product_view_holder_layout,viewGroup, false);

        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run(){
                postActionButton.setEnabled(true);

            }
        });

        ImageView deleteItemImageView = holder.findViewById(R.id.deleteItemImageViewId);
//        deleteItemImageView.setTag(""+itemsTagCounter);

        ImageView postImageView = holder.findViewById(R.id.postImageViewId);
//        postImageView.setTag(""+itemsTagCounter);

        FloatingActionButton startUploadImageView = holder.findViewById(R.id.startUploadButtonId);
//        startUploadImageView.setTag(""+itemsTagCounter);

        FloatingActionButton pauseUploadImageView = holder.findViewById(R.id.pauseUploadButtonId);
//        pauseUploadImageView.setTag(""+itemsTagCounter);

        FloatingActionButton resumeUploadImageView = holder.findViewById(R.id.resumeUploadButtonId);
//        resumeUploadImageView.setTag(""+itemsTagCounter);

        ProgressBar progressBar = holder.findViewById(R.id.progressIndicatorId);
//        progressBar.setTag(""+itemsTagCounter);

        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run() {
                if(!isBitmap) {
                    if(uri != null) {
                        Glide.with(getApplicationContext())
                                .load(uri).into(postImageView);
                    }
                }else{
                    if(bitmap != null){
                        postImageView.setImageBitmap(bitmap);
                    }
//                postImageView.setImageURI(uri)
                };
            }
        });

        pauseUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productUploadTask.pause();
                progressBar.setVisibility(View.GONE);
                pauseUploadImageView.setVisibility(View.GONE);
                resumeUploadImageView.setVisibility(View.VISIBLE);
            }
        });

        startUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productUploadTask.resume();
                progressBar.setVisibility(View.VISIBLE);
                resumeUploadImageView.setVisibility(View.GONE);
                pauseUploadImageView.setVisibility(View.VISIBLE);
            }
        });
        resumeUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productUploadTask.resume();
                progressBar.setVisibility(View.VISIBLE);
                startUploadImageView.setVisibility(View.GONE);
                resumeUploadImageView.setVisibility(View.GONE);
                pauseUploadImageView.setVisibility(View.VISIBLE);
            }
        });
        deleteItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((isEditPage || isProductApproval)){
                    FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(imageDownloadUrl)).delete();
                }
                viewGroup.removeView(holder);


            }
        });

        new Handler(Looper.getMainLooper()).post(new Runnable(){
            @Override
            public void run(){
                containerLinearLayout.addView(holder,position);

            }
        });

        // }
        itemsTagCounter++;
    }

    interface OnProductUploadListener{
        void onSuccess();
        void onFailed(String errorMessage);
    }

    //
    private void uploadProduct(){
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference productDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PAGES)
                .document(pageId);
        HashMap<String, Object> postDetails = new HashMap<>();

        postDetails.put(GlobalValue.PAGE_ID, pageId);
        postDetails.put(GlobalValue.PAGE_OWNER_USER_ID, GlobalValue.getCurrentUserId());
        postDetails.put(GlobalValue.PAGE_TITLE, postTitle);
        postDetails.put(GlobalValue.CATEGORY,category);
        postDetails.put(GlobalValue.PAGE_DESCRIPTION, postDescription);
        postDetails.put(GlobalValue.SEARCH_VERBATIM_KEYWORD, GlobalValue.generateSearchVerbatimKeyWords(postTitle));
        postDetails.put(GlobalValue.SEARCH_ANY_MATCH_KEYWORD,GlobalValue.generateSearchAnyMatchKeyWords(postTitle));
        postDetails.put(GlobalValue.DATE_ADDED_TIME_STAMP, FieldValue.serverTimestamp());
        postDetails.put(GlobalValue.LOCATION, location);
        postDetails.put(GlobalValue.CONTACT_PHONE_NUMBER, phone);
        postDetails.put(GlobalValue.CONTACT_EMAIL, email);
        postDetails.put(GlobalValue.IMAGE_DOWNLOAD_URL,imageDownloadUrl);
        postDetails.put(GlobalValue.DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());
        postDetails.put(GlobalValue.IS_PRIVATE, isPrivate);


        if(!isEditPage){

            postDetails.put(GlobalValue.IS_NEW, true);
            postDetails.put(GlobalValue.IS_BLOCKED, false);
            postDetails.put(GlobalValue.DATE_CREATED_TIME_STAMP, FieldValue.serverTimestamp());

        }else{

            postDetails.put(GlobalValue.IS_NEW, false);
        }
        postDetails.put(GlobalValue.TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.set(productDocumentReference,postDetails,SetOptions.merge());
        if(!isEditPage) {
            DocumentReference newProductDocumentReference = firebaseFirestore.collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId());
            HashMap<String, Object> newProductDetails = new HashMap<>();
            newProductDetails.put(GlobalValue.TOTAL_NUMBER_OF_PAGES, FieldValue.increment(1L));
            newProductDetails.put(GlobalValue.LAST_PAGE_ID, pageId);

            writeBatch.set(newProductDocumentReference, newProductDetails,SetOptions.merge());


        }
        writeBatch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onProductUploadListener.onFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onProductUploadListener.onSuccess();

            }
        });
    }

    private void uploadImages(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                numberOfImagesEligibleForUpload = imageGalleryUriList.size();
                if(isUploadNewImage) {
                        View holder = containerLinearLayout.getChildAt(0);

                        // ImageView postImageView = holder.findViewById(R.id.postImageViewId);
                        ImageView deleteItemButton = holder.findViewById(R.id.deleteItemImageViewId);


                        ImageView deleteItemImageView = holder.findViewById(R.id.deleteItemImageViewId);


                        Uri fileUri = uri;
                        String imageId = GlobalValue.getRandomString(10);
                        StorageReference postImageStorageReference = appStorageReference.child(GlobalValue.ALL_USERS + "/" + GlobalValue.getCurrentUserId() + "/" + imageId + ".PNG");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                deleteItemImageView.setVisibility(View.GONE);

                            }
                        });


                        productUploadTask = postImageStorageReference.putFile(fileUri, imageStorageMetaData);


                        productUploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onProductUploadListener.onFailed(e.getMessage());

                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteItemButton.setVisibility(View.GONE);

                                    }
                                });

                                double uploadSize = snapshot.getTotalByteCount();
                                double uploadedSize = snapshot.getBytesTransferred();
                                double remainingSize = uploadSize - uploadedSize;
                                int uploadProgress = (int) ((100 * uploadedSize) / uploadSize);
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                });


                                // Toast.makeText(context, "progressing..." + uploadProgress, Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                //Toast.makeText(context, "image uploaded", Toast.LENGTH_SHORT).show();

                                productUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!(task.isSuccessful())) {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @Override
                                                public void run() {
//                                            Toast.makeText(context, (CharSequence) task.getException(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }

                                        return postImageStorageReference.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        numberOfImagesUploaded++;
                                        imageDownloadUrl = String.valueOf(task.getResult());

                                            uploadProduct();
                                        }
                                });

                            }
                        });
                    }
                else{
                    uploadProduct();
                }

            }
        }).start();


    }

    void fetchProductDataForEdition(){
//            toggleProgress(true);
        firebaseFirestore.collection(GlobalValue.ALL_PRODUCTS).document(pageId).get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                    productFetchListener.onFailed(e.getMessage());
            }
        })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        pageId = documentSnapshot.getId();
                        productOwnerUserId = ""+ documentSnapshot.get(GlobalValue.PAGE_OWNER_USER_ID);
                        String productTitle =""+ documentSnapshot.get(GlobalValue.PAGE_TITLE);
                        String location =""+ documentSnapshot.get(GlobalValue.LOCATION);
                        String phone = documentSnapshot.get(GlobalValue.CONTACT_PHONE_NUMBER) +"";
                        String email = documentSnapshot.get(GlobalValue.CONTACT_EMAIL) +"";
                        String residentialAddress = documentSnapshot.get(GlobalValue.CONTACT_ADDRESS) +"";
                        String productDescription =""+ documentSnapshot.get(GlobalValue.PAGE_DESCRIPTION);
                        String productCategory =""+ documentSnapshot.get(GlobalValue.CATEGORY);
                        imageDownloadUrl = documentSnapshot.get(GlobalValue.IMAGE_DOWNLOAD_URL)+"";
                        boolean isPrivate = documentSnapshot.get(GlobalValue.IS_PRIVATE)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_PRIVATE) : false;
                        boolean isBlocked = documentSnapshot.get(GlobalValue.IS_BLOCKED)!=null ? documentSnapshot.getBoolean(GlobalValue.IS_BLOCKED) : false;

                        visibilitySwitch.setChecked(!isPrivate);
                        postTitleEditText.setText(productTitle);
                        productLocationEditText.setText(location);
                        phoneEditText.setText(phone);
                        emailEditText.setText(email);
                        residentialAddressEditText.setText(residentialAddress);
                        categorySpinner.setSelection(categoryList.indexOf(productCategory));
                        postDescriptionEditText.setText(productDescription);

                            displayPostMedia(AddNewServiceActivity.this, containerLinearLayout, Uri.parse(imageDownloadUrl), null, false,0);


                        stopLoader();

                    }
                });
    }


    void prepareCategorySpinner(){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,GlobalValue.getPageCategory());
        categorySpinner.setAdapter(adapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getSelectedItem()+"";
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    void stopLoader(){
        shimmerLayout.stopShimmer();
        shimmerLayout.setVisibility(View.GONE);
        cameraFloatingButton.setVisibility(View.VISIBLE);

    }
    //
    interface ProductFetchListener{
        void onFailed(String errorMessage);
        void onSuccess(ProductDataModel productDataModel);
    }




}


