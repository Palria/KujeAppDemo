package com.palria.kujeapp;



import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class PostNewAdvertActivity extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    String userId;
    AlertDialog alertDialog;
    Spinner categorySpinner;
    String category = "Autos";
    ArrayList<String> imageDownloadUrlList = new ArrayList<>();
    ArrayList<Uri> imageGalleryUriList = new ArrayList<>();
    int numberOfImagesUploaded=0;
    int numberOfImages = 0;
    int numberOfImagesFailed=0;
    OnProductUploadListener onProductUploadListener;

    ImageButton popUpImageButton;

    ImageButton backButton;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 2002;
    private final int GALLERY_PERMISSION_REQUEST_CODE = 23;
    private final int VIDEO_PERMISSION_REQUEST_CODE = 20;


//    ProgressBar  progressIndicator;

//    ProgressBar  topPostProgressIndicator;

    ExtendedFloatingActionButton cameraFloatingButton;
//    LinearLayout  chooseImageLinearLayout;
//    Button openVideoGalleryButton;

    StorageReference appStorageReference;
    UploadTask productUploadTask;
    boolean isTotalNumberOfPostsIncremented = false;
    private  int successCounter=0;
    boolean isVisible=false;
//    TextView textHeaderTextView;

    String pagePosterId;
    boolean isPageThePoster = true;
    String postId;
    Button postActionButton;
    TextInputEditText postTitleEditText,postDescriptionEditText;
    String postTitle;
    Spinner viewLimitSpinner;
    int selectedViewLimit = 0;
    String postDescription;
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


    //    ImageView deleteVideoButton;
//    MaterialButton replaceVideoActionButton;
    boolean isUploadVideo = false;
    boolean isVideoUploaded = false;
    boolean isImageUploading = false;
    boolean isPostCountIncremented = false;



    //    FloatingActionButton startVideoUploadImageView = null;
//    FloatingActionButton successVideoImageView =  null;
//    FloatingActionButton pauseVideoUploadImageView =null;
//    FloatingActionButton resumeVideoUploadImageView =null;
//    ProgressBar progressBarVideo = null;
    StorageReference postVideoStorageReference = null;
    StorageMetadata imageStorageMetaData ;
    StorageMetadata videoStorageMetaData ;




    Uri videoInputUri;
    File trimmedVideoOutputFile;
    Uri trimmedVideoOutputUri;
    String videoInputUriPath;
    File videoOutPutFile;
    //    RelativeLayout videoUploadRelativeLayout;
//    StyledPlayerView postPreviewStyledVideoView;
//    LinearLayout videoProcessingIndicatorLinearLayout;
    String videoId = String.valueOf(new Random().nextInt(1000000));

//    ProgressBar videoUploadIndicator;
//    ProgressBar videoProcessingIndicator;

    ActivityResultLauncher<Intent> openGalleryLauncher;
    ActivityResultLauncher<Intent> openVideoGalleryLauncher;
    ActivityResultLauncher<Intent> openCameraLauncher;

  /*
    ArrayList<AddProductCustomRecyclerViewAdapter.ImageData>imageDataArrayList=new ArrayList<>();
    AddProductCustomRecyclerViewAdapter addProductCustomRecyclerViewAdapter;
    RecyclerView productContentRecyclerView;
*/


    LinearLayout containerLinearLayout;
//    LinearLayout addNewVideoLinearLayout;

    ArrayList<Uri>uriArrayList = new ArrayList<>();
    //    ArrayList<String> searchKeywordArrayList = new ArrayList<>();
    ArrayList<String> searchAnyMatchKeywordArrayList = new ArrayList<>();
    ArrayList<String> searchVerbatimKeywordArrayList = new ArrayList<>();
    ArrayList<String> hashTagKeywordArrayList = new ArrayList<>();
    Uri uri;
    private int totalNumberOfImages;
    ArrayList<ExoPlayer> activeExoPlayerArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_new_advert);

        firebaseFirestore=FirebaseFirestore.getInstance();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

        popUpImageButton =findViewById(R.id.popUpImageButtonId);
        categorySpinner =findViewById(R.id.categorySpinnerId);
        backButton =findViewById(R.id.backButton);
        popUpImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(PostNewAdvertActivity.this,R.menu.post_new_activity_pop_up_menu , popUpImageButton, new GlobalValue.OnMenuItemClickListener() {
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
                PostNewAdvertActivity.super.onBackPressed();
            }
        });
        //be careful to remove this at production Time
//         userId="testUID";
        manageIntentData();

        onProductUploadListener = new OnProductUploadListener() {
            @Override
            public void onSuccess() {
                //TODO : SEND NOTIFICATION TO THE PLATFORM ACCOUNT
                //carries the info about the quiz
                ArrayList<String> modelInfo = new ArrayList<>();
                modelInfo.add(postId);

                ArrayList<String> recipientIds = new ArrayList<>();
                recipientIds.add(GlobalValue.getPlatformId());

                //fires out the notification
                GlobalValue.sendNotificationToUsers(GlobalValue.NOTIFICATION_TYPE_ADVERT_SUBMITTED,GlobalValue.getRandomString(60),recipientIds,modelInfo,postTitle,"New advert has been submitted",null);

                Toast.makeText(PostNewAdvertActivity.this, "Advert submitted!", Toast.LENGTH_SHORT).show();
                toggleProgress(false);
//                GlobalValue.showAlertMessage("success",getApplicationContext(),"Product Added","Your product was successfully added");

                progressDialog.cancel();
                successDialog.show();

            }

            @Override
            public void onFailed(String errorMessage) {
                Toast.makeText(PostNewAdvertActivity.this, "Product failed to upload!", Toast.LENGTH_SHORT).show();
                toggleProgress(false);
                progressDialog.cancel();
//                GlobalValue.showAlertMessage("error",getApplicationContext(),"Product failed to Added","Your product failed to add, please try again");
                initConfirmOrderDialog(true,errorMessage);
            }
        };

//        textHeaderTextView = findViewById(R.id.textHeaderTextViewId);
//        progressIndicator = findViewById(R.id.progressIndicatorId);
//        progressBarVideo = findViewById(R.id.videoUploadIndicatorId);
//        topPostProgressIndicator = findViewById(R.id.topPostProgressIndicatorId);
//        topPostProgressIndicator.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int i) {
//if(topPostProgressIndicator.getVisibility() == View.INVISIBLE || topPostProgressIndicator.getVisibility() == View.GONE){
//    progressDialog.cancel();
//}
//            }
//        });
        cameraFloatingButton = findViewById(R.id.cameraFloatingButtonId);
//        chooseImageLinearLayout = findViewById(R.id.chooseImageLinearLayoutId);
//        openVideoGalleryButton = findViewById(R.id.openVideoGalleryButtonId);

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
        alertDialog = new AlertDialog.Builder(PostNewAdvertActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();

        appStorageReference= FirebaseStorage.getInstance().getReference();
        postId=GlobalValue.getRandomString(60);

        postActionButton = findViewById(R.id.postNewActionButtonId);
        postActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                uploadInBackground();
//                GlobalValue.hideKeyboard(getApplicationContext());
                if(!postTitleEditText.getText().toString().isEmpty() && !imageGalleryUriList.isEmpty()) {
                    confirmProductAdditionDialog.show();

                }else{
                    GlobalValue.createSnackBar(PostNewAdvertActivity.this,postActionButton,"Photos and or text are needed when adding product", Snackbar.LENGTH_INDEFINITE);
                }
            }
        });

        viewLimitSpinner=findViewById(R.id.viewLimitSpinnerId);
        postTitleEditText=findViewById(R.id.postTitleEditTextId);
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
//        addNewVideoLinearLayout = findViewById(R.id.addNewVideoLinearLayoutId);
////        totalNumberOfItems = containerLinearLayout.getChildCount();
//
//        deleteVideoButton = findViewById(R.id.deleteItemVideoImageViewId);
//        deleteVideoButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                videoUploadRelativeLayout.setVisibility(View.GONE);
//
//                videoClearDialog.show();
//            }
//        });
//
//        replaceVideoActionButton = findViewById(R.id.replaceVideoActionButtonId);
//        replaceVideoActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                requestForPermission(VIDEO_PERMISSION_REQUEST_CODE);
//            }
//        });

        openGalleryLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data=result.getData();
                    ClipData clipData=data.getClipData();
                    for(int i = 0;i<clipData.getItemCount();i++){
                        uri=clipData.getItemAt(i).getUri();
                   /*
                        AddProductCustomRecyclerViewAdapter.ImageData imageData=new AddProductCustomRecyclerViewAdapter.ImageData(uri);
                        imageDataArrayList.add(imageData);
*/
                        uriArrayList.add(uri);
                        imageGalleryUriList.add(uri);
                        displayPostMedia(PostNewAdvertActivity.this,containerLinearLayout,uri, null, false, imageGalleryUriList.indexOf(uri));
                        numberOfImages++;

                    }


                    //addProductCustomRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
        openVideoGalleryLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data=result.getData();
                    videoInputUri =  data.getData();
//                   FileDescriptor file = new FileDescriptor();


//                    File file = File.createTempFile(,,);
//                    fil
//
//                    videoInputUri = Uri.fromFile(file);

                    videoInputUriPath =  data.getData().getEncodedPath();
//                    doFFMPEGStuff();
                    isUploadVideo = true;
//                    videoUploadRelativeLayout.setVisibility(View.VISIBLE);


//                    postPreviewStyledVideoView = new StyledPlayerView((getApplicationContext()));
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
//                    postPreviewStyledVideoView.setLayoutParams(layoutParams);
//                    addNewVideoLinearLayout.removeAllViews();
//                    addNewVideoLinearLayout.addView(postPreviewStyledVideoView);
                    if(activeExoPlayerArrayList.size() != 0) {
                        for (int i = 0; i < activeExoPlayerArrayList.size(); i++) {
                            if (activeExoPlayerArrayList.get(i) != null) {
                                activeExoPlayerArrayList.get(i).release();
                            }
                        }
                    }
//                    GlobalValue.displayExoplayerVideo(getApplicationContext(), postPreviewStyledVideoView, activeExoPlayerArrayList, videoInputUri);


//                    openVideoGalleryButton.setEnabled(false);
                    //Uri.parse("/storage/emulated/0/Android/media/com.whatsApp/WhatsApp/Media/WhatsApp Video/VID-20220329-WA0002.mp4"));

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
                            Uri cameraUri = GlobalValue.getImageUri(PostNewAdvertActivity.this,bitmapFromCamera,10);

                            imageGalleryUriList.add(cameraUri);
                            displayPostMedia(PostNewAdvertActivity.this, containerLinearLayout, cameraUri, bitmapFromCamera, false,  imageGalleryUriList.indexOf(cameraUri));
                            numberOfImages++;
                        }

                    }
                    // }
                }else{
                    Toast.makeText(getApplicationContext(), "No image captured!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        videoOutPutFile = new File( new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"Business Era"),GlobalValue.getDate());
//        videoOutputUri = Uri.fromFile(videoOutPutFile);

        trimmedVideoOutputFile = new File( getCacheDir(),"PLATFORM_TRIMMED_VIDEO");
        trimmedVideoOutputUri = Uri.fromFile(trimmedVideoOutputFile);

//        videoUploadRelativeLayout = findViewById(R.id.videoUploadRelativeLayoutId);
//        postPreviewStyledVideoView = findViewById(R.id.postPreviewStyledVideoViewId);
//        videoUploadIndicator = findViewById(R.id.videoUploadIndicatorId);
//        videoProcessingIndicatorLinearLayout = findViewById(R.id.videoProcessingIndicatorLinearLayoutId);
//        videoProcessingIndicator = findViewById(R.id.videoProcessingIndicatorId);

        imageStorageMetaData = new StorageMetadata.Builder().setContentType("image/png").build();
        videoStorageMetaData = new StorageMetadata.Builder().setContentType("video/mp4").build();

//        GlobalValue.displayExoplayerVideo(getApplicationContext(), postPreviewStyledVideoView, Uri.parse("file:///data/user/0/com.govanceinc.obidientera/cache/AUNXs7mDJyB4pCJD"));
        initConfirmOrderDialog(false,"");
        createSuccessDialog();
        createProgressDialog();
        createVideoTrimDialog();
        createVideoClearDialog();
//        createPostUploadConfirmationDialog();
        createVideoTrimFailedDialog();
        createConfirmExitDialog();
        createConfirmMakeNewPostDialog();
        prepareCategorySpinner();
        prepareViewLimitSpinner();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(trimmedVideoOutputFile.exists()) {
            trimmedVideoOutputFile.delete();
        }
        if(activeExoPlayerArrayList.size() != 0){
            for (int i = 0; i < activeExoPlayerArrayList.size(); i++) {
                activeExoPlayerArrayList.get(i).release();
            }

        }
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
            if (requestCode == VIDEO_PERMISSION_REQUEST_CODE) {
                fireVideoIntent();
            }
        }
    }


    public void uploadInBackground() {
        postTitle = postTitleEditText.getText()+"";

        postDescription = postDescriptionEditText.getText()+"";

        //  if(!(postTitle.isEmpty() || postDescription.isEmpty())) {
        if (!postTitle.isEmpty()) {
            searchAnyMatchKeywordArrayList = GlobalValue.generateSearchAnyMatchKeyWords(postTitle);
            searchVerbatimKeywordArrayList = GlobalValue.generateSearchVerbatimKeyWords(postTitle);


            hashTagKeywordArrayList = GlobalValue.generateHashTagKeyWords(postTitle);

        }

        if (!postDescription.isEmpty()) {
            ArrayList<String> postDescriptionSearchKeywordArray = GlobalValue.generateSearchKeyWords(postDescription, 20);

           /* if (postDescriptionSearchKeywordArray.size() != 0) {
                for (int i = 0; i < postDescriptionSearchKeywordArray.size(); i++) {
                    if (!searchKeywordArrayList.contains(postDescriptionSearchKeywordArray.get(i))) {
                        searchKeywordArrayList.add(postDescriptionSearchKeywordArray.get(i));
                    }
                }
            } */

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

//        }else{
//            searchKeywordArrayList.add(" ");
//        }

//               uploadProductData(getApplicationContext());


        // TextView testTextView = (TextView) productContentRecyclerView.getChildAt(3);
        //AddProductCustomRecyclerViewAdapter.CustomViewHolder  viewHolder = (AddProductCustomRecyclerViewAdapter.CustomViewHolder) productContentRecyclerView.findViewHolderForLayoutPosition(3);
//    productContentRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//        @Override
//        public void onGlobalLayout() {
//            productContentRecyclerView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    AddProductCustomRecyclerViewAdapter.CustomViewHolder viewHolder2 = (AddProductCustomRecyclerViewAdapter.CustomViewHolder) productContentRecyclerView.findViewHolderForLayoutPosition(3);
//                    if (viewHolder2 != null) {
//                        viewHolder2.testTextView.setText("God is wonderful and must be wonderful to Govance");
//                        Toast.makeText(getApplicationContext(), "it is working", Toast.LENGTH_SHORT).show();
//
//                    }else {
//                        Toast.makeText(getApplicationContext(), "it is null", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            },1000L);
//            productContentRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//        }
        //   });
        if(GlobalValue.isConnectedOnline(this)) {

            postActionButton.setEnabled(false);

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
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//        isVisible = false;
        requestForPermission(GALLERY_PERMISSION_REQUEST_CODE);

    }
    public void openVideoGallery(View view){
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//        isVisible = false;
        requestForPermission(VIDEO_PERMISSION_REQUEST_CODE);

    }
    public void openCamera(View view){
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//        isVisible = false;
        requestForPermission(CAMERA_PERMISSION_REQUEST_CODE);
    }

    public void fireGalleryIntent(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        galleryIntent.setType("image/*");
        openGalleryLauncher.launch(galleryIntent);
    }
    public void fireVideoIntent(){
        Intent videoGalleryIntent = new Intent();
//        videoGalleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        videoGalleryIntent.setAction(Intent.ACTION_PICK);
        videoGalleryIntent.setType("video/*");
        openVideoGalleryLauncher.launch(videoGalleryIntent);
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


            uriArrayList.add(Uri.parse(uriFromCamera));
            displayPostMedia(PostNewAdvertActivity.this,containerLinearLayout,Uri.parse(uriFromCamera), null, false, 2);

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
            } if(requestCode == VIDEO_PERMISSION_REQUEST_CODE) {
                fireVideoIntent();
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


    public void manageIntentData(){
        Intent intent=getIntent();
        pagePosterId = intent.getStringExtra("PAGE_POSTER_ID");
        isPageThePoster = intent.getBooleanExtra("IS_PAGE_THE_POSTER",true);

//        else{
        //  productCollectionName="UNSPECIFIED";
        //}

    }




    void createProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait => Adding product...");
//        progressDialog.setButton("Hide", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                progressDialog.dismiss();
//
//            }
//        });
        progressDialog.create();

    }

    public void createSuccessDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setTitle("Update posted!");
        builder.setMessage("what next?");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_success_circle_outline_24);
        builder
//                .setPositiveButton("My page", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                PostNewUpdateActivity.this.finish();
//                startActivity(new Intent(getApplicationContext(),OwnerSinglePageActivity.class));
//            }
//        })
                .setNegativeButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  successDialog.cancel();
                        //openGallery(/*unnecessary view*/productCollectionNameEditText);
                        PostNewAdvertActivity.this.finish();
                    }
                }).setNeutralButton("Post New", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //productCollectionNameEditText,

//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
////                           ArrayList<AddProductCustomRecyclerViewAdapter.ImageData>imageDataArrayList=new ArrayList<>();
////                           AddProductCustomRecyclerViewAdapter addProductCustomRecyclerViewAdapter=new AddProductCustomRecyclerViewAdapter(getApplicationContext(),imageDataArrayList,firebaseFirestore, userId, appStorageReference);
////addProductCustomRecyclerViewAdapter.notifyDataSetChanged();
//                        uriArrayList.clear();
//                        containerLinearLayout.removeAllViews();
//                        postActionButton.setEnabled(true);
//                        postActionButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                               uploadInBackground();
//
//                         postUploadConfirmationDialog.show();
//                            }
//                        });
//                    }
//                });
//                postId=GlobalValue.getRandomString(60);
//                postTitleEditText.setText("");
//                postDescriptionEditText.setText("");
//                isPostCountIncremented = false;
//                threeImageUploadCounter=0;
//                numberOfImagesUploadCounter=0L;
//                postActionButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                uploadInBackground();
//
//                        postUploadConfirmationDialog.show();
//                    }
//                });

                confirmMakeNewPostDialog.show();
            }
        });
        successDialog =builder.create();

        // successDialog.show();

    }
    public void createVideoTrimDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setTitle("Please wait!");
        builder.setMessage("We only accept video clip of 30s, which means if your video exceeds 30s we trim and process it to match our accepted video duration,  This may even take some time to finish the work in background.");
        builder.setNegativeButton("No, cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Video upload canceled", Toast.LENGTH_SHORT).show();
                        videoTrimDialog.cancel();

                    }
                });
            }
        }).setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                videoTrimDialog.cancel();
            }
        });
        videoTrimDialog =builder.create();

        // successDialog.show();

    }
    public void createVideoTrimFailedDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setTitle("Sorry trimming failed!");
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                videoTrimFailedDialog.cancel();
            }
        });
        videoTrimFailedDialog =builder.create();

        // successDialog.show();

    }
    public void createVideoClearDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setMessage("Clear video");

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        videoClearDialog.cancel();

                    }
                });
            }
        }).setNeutralButton("Clear", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isUploadVideo = false;
//                videoUploadRelativeLayout.setVisibility(View.GONE);
//                openVideoGalleryButton.setEnabled(true);
            }
        });
        videoClearDialog =builder.create();

        // successDialog.show();

    }
    //    public void createPostUploadConfirmationDialog(){
//
//        AlertDialog.Builder builder =new AlertDialog.Builder(this);
//
//        builder.setMessage("Confirm to proceed");
//
//        builder.setNegativeButton("No, edit", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        postUploadConfirmationDialog.cancel();
//
//                    }
//                });
//            }
//        })
//                .setNeutralButton("Confirm", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
////                postUploadConfirmationDialog.show();
//                        uploadInBackground();
//
//                    }
//                });
//        postUploadConfirmationDialog =builder.create();
//
//        // successDialog.show();
//
//    }
    public void initConfirmOrderDialog(boolean isRetry, String errorMessage){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String button = "Confirm";
        String message = "Confirm to post";
        if(isRetry){
            button = "Retry";
            message = "Your post Failed,"+errorMessage+" Please try again";
        }

        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                        toggleProgress(true);
                        uploadInBackground();

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
                PostNewAdvertActivity.super.onBackPressed();

            }
        });
        confirmExitDialog =builder.create();


    }
    public void createConfirmMakeNewPostDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setMessage("Do you want to make new post?");

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        confirmMakeNewPostDialog.cancel();

                    }
                });
            }
        }).setNeutralButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                createNewPost();
            }
        });
        confirmMakeNewPostDialog =builder.create();


    }

    void createNewPost(){
        Intent intent = new Intent(PostNewAdvertActivity.this, PostNewAdvertActivity.class);
        intent.putExtra("PAGE_POSTER_ID",pagePosterId);
        intent.putExtra("IS_PAGE_THE_POSTER",isPageThePoster);
        startActivity(intent);
        PostNewAdvertActivity.this.finish();
    }

    void prepareCategorySpinner(){
        String[] categories = {"Autos","Motorcycles","Houses","Lands","Kitchen Utensils","Building Materials","Lady's Wear","Men's Wear","Computers","Mobiles"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,categories);
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
                imageGalleryUriList.remove(viewGroup.indexOfChild(holder));
                viewGroup.removeView(holder);
                numberOfImages--;

//                if(containerLinearLayout.getChildCount()==0){
//                    postActionButton.setEnabled(false);
//
//                }
//                  viewGroup.removeViewAt(Integer.parseInt(String.valueOf(deleteItemImageView.getTag())));

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
    private void uploadUpdate(ArrayList<String> imageUrlList){
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference productDocumentReference =  firebaseFirestore.collection(GlobalValue.PLATFORM_ADVERTS).document(postId);
        HashMap<String, Object> postDetails = new HashMap<>();
        postDetails.put(GlobalValue.ADVERT_ID, postId);
        postDetails.put(GlobalValue.PRODUCT_OWNER_USER_ID, GlobalValue.getCurrentUserId());
        postDetails.put(GlobalValue.ADVERT_TITLE, postTitle);
        postDetails.put(GlobalValue.ADVERT_VIEW_LIMIT, selectedViewLimit);
        postDetails.put(GlobalValue.IS_VIEW_EXCEEDED, false);
        postDetails.put(GlobalValue.SEARCH_ANY_MATCH_KEYWORD, searchAnyMatchKeywordArrayList);
        postDetails.put(GlobalValue.SEARCH_VERBATIM_KEYWORD, searchVerbatimKeywordArrayList);
        postDetails.put(GlobalValue.IS_PRIVATE, false);
        postDetails.put(GlobalValue.IS_BLOCKED, false);
        postDetails.put(GlobalValue.IS_APPROVED, false);
        postDetails.put(GlobalValue.IS_NEW, true);
        postDetails.put(GlobalValue.ADVERT_DESCRIPTION, postDescription);
        postDetails.put(GlobalValue.ADVERT_IMAGE_DOWNLOAD_URL_ARRAY_LIST,imageUrlList);
//    postDetails.put("PRODUCT_IMAGE_DOWNLOAD_URL_" + numberOfImagesUploadCounter, "EMPTY");
//    postDetails.put("PRODUCT_IMAGE_STORAGE_REFERENCE_" + numberOfImagesUploadCounter, "EMPTY");
        postDetails.put(GlobalValue.TOTAL_NUMBER_OF_IMAGES_UPLOADED, 0);

        postDetails.put(GlobalValue.TOTAL_NUMBER_OF_VIEWS, 0);
        postDetails.put(GlobalValue.DATE_POSTED_TIME_STAMP, FieldValue.serverTimestamp());
        postDetails.put(GlobalValue.TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.set(productDocumentReference,postDetails,SetOptions.merge());

        DocumentReference newProductDocumentReference =  firebaseFirestore.collection(GlobalValue.ALL_USERS).document(userId);
        HashMap<String, Object> newProductDetails = new HashMap<>();
        postDetails.put(GlobalValue.TOTAL_NUMBER_OF_ADVERTS, 1L);
        postDetails.put(GlobalValue.LAST_PRODUCT_ID, postId);
        writeBatch.update(newProductDocumentReference,newProductDetails);

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
                totalNumberOfImages = imageGalleryUriList.size();
                for (int i = 0; i < totalNumberOfImages; i++) {
                    View holder = containerLinearLayout.getChildAt(i);

                    // ImageView postImageView = holder.findViewById(R.id.postImageViewId);
                    ImageView deleteItemButton = holder.findViewById(R.id.deleteItemImageViewId);

//                FloatingActionButton startUploadImageView = holder.findViewById(R.id.startUploadButtonId);
//                FloatingActionButton successImageView = holder.findViewById(R.id.successImageViewId);
//
//                FloatingActionButton pauseUploadImageView = holder.findViewById(R.id.pauseUploadButtonId);
//
//                FloatingActionButton resumeUploadImageView = holder.findViewById(R.id.resumeUploadButtonId);

                    ImageView deleteItemImageView = holder.findViewById(R.id.deleteItemImageViewId);

//                ProgressBar progressBar = holder.findViewById(R.id.progressIndicatorId);

                    Uri fileUri = imageGalleryUriList.get(i);
                    String imageId = String.valueOf(new Random().nextInt(1000000));
                    StorageReference postImageStorageReference = appStorageReference.child(GlobalValue.ALL_USERS+"/" +GlobalValue.getCurrentUserId()+"/"+GlobalValue.IMAGES + "/" + imageId + ".PNG");
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            deleteItemImageView.setVisibility(View.GONE);

                        }
                    });


//                postImageView.setDrawingCacheEnabled(true);
//                postImageView.buildDrawingCache();
//                Bitmap bitmap = ((BitmapDrawable) postImageView.getDrawable()).getBitmap();
//
//
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();


//                bitmap.compress(Bitmap.CompressFormat.PNG, 5, byteArrayOutputStream);
//                bytes = byteArrayOutputStream.toByteArray();
                    productUploadTask = postImageStorageReference.putFile(fileUri, imageStorageMetaData);


                    productUploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
//                                Toast.makeText(context, "failed to upload", Toast.LENGTH_SHORT).show();
                                    numberOfImagesFailed++;
                                    if(numberOfImagesFailed == numberOfImages){

                                        onProductUploadListener.onFailed(e.getMessage());

                                    }else if(numberOfImagesFailed+numberOfImagesUploaded == numberOfImages){
                                        uploadUpdate(imageDownloadUrlList);
                                    }

                                }
                            });

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
                                    String postImageDownloadUrl = String.valueOf(task.getResult());
                                    imageDownloadUrlList.add(postImageDownloadUrl);
                                    if(numberOfImagesUploaded == numberOfImages || (numberOfImagesUploaded+numberOfImagesFailed == numberOfImages)){
                                        //success

                                        uploadUpdate(imageDownloadUrlList);
                                    }
                                }
                            });

                        }
                    });
                }

            }
        }).start();


    }
//

    void prepareViewLimitSpinner(){

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,R.layout.support_simple_spinner_dropdown_item,GlobalValue.getVviewLimitList());
        viewLimitSpinner.setAdapter(adapter);
        viewLimitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedViewLimit = Integer.parseInt(adapterView.getSelectedItem()+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

}
