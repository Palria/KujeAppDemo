package com.palria.kujeapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.palria.kujeapp.models.QuestionDataModel;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;


public class MakeNewInquiryActivity extends AppCompatActivity {
String question;
String questionId;
String questionPhotoDownloadUrl = "";
EditText questionEditText;
ImageView questionPhoto;
FloatingActionButton askQuestionActionButton;
AlertDialog alertDialog;
TextView mLoginLink;
View imageContainerLayout;
ImageView removePhotoImageView;
private TextView errorMessageTextView;
boolean isPublic = false;
boolean isClosed = false;
boolean isEdition = false;
boolean isNewPhoto = true;
boolean isPhotoIncluded = false;
boolean isPhotoDeleted = false;
Switch visibilityIndicatorSwitch;
Switch closedIndicatorSwitch;
String category = "";
Spinner categorySelector;
    QuestionDataModel questionDatamodel;
    Uri galleryImageUri;
    /**
     * A  variable for launching the gallery {@link Intent}
     * */
    ActivityResultLauncher<Intent> openGalleryLauncher;

    /**
     * A  variable for launching the camera {@link Intent}
     * */
    ActivityResultLauncher<Intent> openCameraLauncher;
        Button pickImageActionButton;
    int CAMERA_PERMISSION_REQUEST_CODE = 2002;
    int GALLERY_PERMISSION_REQUEST_CODE = 23;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_make_new_inquiry);
        fetchIntentData();
        initUI();

        openGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data=result.getData();
                    galleryImageUri = data.getData();
//                        Picasso.get().load(galleryImageUri)
//                                .centerCrop()
//                                .into(pickImageActionButton);
                    Glide.with(MakeNewInquiryActivity.this)
                            .load(galleryImageUri)
                            .centerCrop()
                            .into(questionPhoto);
                    imageContainerLayout.setVisibility(View.VISIBLE);

                    isPhotoIncluded = true;
                    isNewPhoto = true;


                }
            }
        });
        openCameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK) {

                    if (result.getData() != null) {
                        Intent data = result.getData();
                        Bitmap bitmapFromCamera =(Bitmap) data.getExtras().get("data");

                        if(bitmapFromCamera != null) {
                         Bitmap   cameraImageBitmap = bitmapFromCamera;
                            //coverPhotoImageView.setImageBitmap(cameraImageBitmap);
                            Glide.with(MakeNewInquiryActivity.this)
                                    .load(cameraImageBitmap)
                                    .centerCrop()
                                    .into(questionPhoto);
                            imageContainerLayout.setVisibility(View.VISIBLE);
                            isPhotoIncluded = true;
                            isNewPhoto = true;
                        }

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "No image captured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        pickImageActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(MakeNewInquiryActivity.this,R.menu.pick_image_menu , pickImageActionButton, new GlobalValue.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClicked(MenuItem item) {

                        if(item.getItemId() == R.id.openImageGalleryId){
                            openGallery();
                        }
                        else if(item.getItemId() == R.id.openCameraId){
                            openCamera();
                        }
                        return true;
                    }
                });
            }
        });

        askQuestionActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                question = questionEditText.getText() +"";
              //check question not empty and valid?
                if(question.trim().equals("")){


                    errorMessageTextView.setText("Enter a valid question!");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    return;
                }
                showConfirmUploadDialog();
            }
        });
        removePhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              isPhotoIncluded = false;
                imageContainerLayout.setVisibility(View.GONE);
              isPhotoDeleted = true;
            }
        });
        initCategorySpinner(categorySelector);
        if(isEdition){
            renderQuestionDataBeforeEdition();
        }
    }
//1234567890qid


    private void fetchIntentData(){
        Intent intent = getIntent();
        isEdition = intent.getBooleanExtra(GlobalValue.IS_EDITION,false);

        isPhotoIncluded = intent.getBooleanExtra(GlobalValue.IS_PHOTO_INCLUDED,false);
        if(isEdition){
            questionId = intent.getStringExtra(GlobalValue.QUESTION_ID);
            questionDatamodel = (QuestionDataModel) intent.getSerializableExtra(GlobalValue.QUESTION_DATA_MODEL);


        }else{
            questionId = GlobalValue.getRandomString(100);


        }
    }


    @Override
    public void onBackPressed(){
        createConfirmExitDialog();
    }


    /**
     * Initializes the Activity's widgets
     * */
    private void initUI(){

        imageContainerLayout= findViewById(R.id.imageContainerLayoutId);
    questionEditText= findViewById(R.id.questionInput);
    closedIndicatorSwitch= findViewById(R.id.closedIndicatorSwitchId);
    visibilityIndicatorSwitch= findViewById(R.id.visibilityIndicatorSwitchId);
    categorySelector= findViewById(R.id.categorySelectorSpinnerId);
        pickImageActionButton= findViewById(R.id.selectImageActionButtonId);
        removePhotoImageView= findViewById(R.id.removePhotoImageViewId);
        questionPhoto= findViewById(R.id.questionPhotoImageViewId);
        askQuestionActionButton = findViewById(R.id.askQuestionActionButtonId);
    mLoginLink = findViewById(R.id.login_link);
    errorMessageTextView = findViewById(R.id.errorMessage);

    alertDialog = new AlertDialog.Builder(MakeNewInquiryActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();

    }


    private void toggleProgress(boolean show){
        if(show){
            alertDialog.show();
        }else{
            alertDialog.hide();
        }
    }
    private void createConfirmExitDialog(){
        AlertDialog confirmExitDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Exit");
        builder.setMessage("Click exit button to exit the screen");
        builder.setCancelable(false);
        builder.setIcon(R.drawable.ic_baseline_error_outline_24);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MakeNewInquiryActivity.super.onBackPressed();
            }
        })
                .setNegativeButton("Stay back", null);
        confirmExitDialog = builder.create();
        confirmExitDialog.show();

    }
    private void showConfirmUploadDialog(){
        AlertDialog confirmExitDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm before posting");
        builder.setMessage("Click confirm button to proceed");
        builder.setCancelable(true);
        builder.setIcon(R.drawable.ic_baseline_error_outline_24);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toggleProgress(true);
                if(isPhotoIncluded && isNewPhoto) {

    uploadQuestionPhoto(new QuestionPhotoUploadListener() {
        @Override
        public void onSuccess(String downloadUrl) {
            askQuestion(new QuestionListener() {

                @Override
                public void onSuccess() {
                    /*
                     *Email reset link sent to user's provided email
                     *address, inform him to open his inbox and click on the link
                     * to proceed resetting his password
                     */
                    toggleProgress(false);
                    //show success

                    GlobalHelpers.showAlertMessage("success",
                            MakeNewInquiryActivity.this,
                            "Question posted",
                            "Your question is successfully posted and waiting for answers from community contributors");



                }

                @Override
                public void onFailed(String errorMessage) {
                    toggleProgress(false);
                    //Take an action when the question post process fails
                    errorMessageTextView.setText(errorMessage + "  Please try again!");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                }


            });
        }

        @Override
        public void onFailed(String errorMessage) {
            toggleProgress(false);
            //Take an action when the question post process fails
            errorMessageTextView.setText(errorMessage + "  Please try again!");
            errorMessageTextView.setVisibility(View.VISIBLE);

        }
    });
}else{
    askQuestion(new QuestionListener() {

        @Override
        public void onSuccess() {
            /*
             *Email reset link sent to user's provided email
             *address, inform him to open his inbox and click on the link
             * to proceed resetting his password
             */
            toggleProgress(false);
            //show success

            GlobalHelpers.showAlertMessage("success",
                    MakeNewInquiryActivity.this,
                    "Question posted",
                    "Your question is successfully posted and waiting for answers from community contributors");



        }

        @Override
        public void onFailed(String errorMessage) {
            toggleProgress(false);
            //Take an action when the question post process fails
            errorMessageTextView.setText(errorMessage + "  Please try again!");
            errorMessageTextView.setVisibility(View.VISIBLE);
        }


    });

    }

            }
        })
                .setNegativeButton("Edit more", null);
        confirmExitDialog = builder.create();
        confirmExitDialog.show();

    }

    private void askQuestion(QuestionListener questionListener) {
        if (question != null && !question.isEmpty()) {
            isPublic = visibilityIndicatorSwitch.isChecked();
            isClosed = closedIndicatorSwitch.isChecked();
        if(isPhotoDeleted){
            try {
                GlobalValue.getFirebaseStorageInstance().getReferenceFromUrl(questionPhotoDownloadUrl).delete();
                questionPhotoDownloadUrl = "";
            } catch (Exception e) {
            }
        }

            WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();
            DocumentReference documentReference1 = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS).document(questionId);
            HashMap<String,Object> quizDetails = new HashMap<>();
            quizDetails.put(GlobalValue.QUESTION_ID,questionId);
            quizDetails.put(GlobalValue.AUTHOR_ID,GlobalValue.getCurrentUserId());
            quizDetails.put(GlobalValue.QUESTION_BODY,question);
            quizDetails.put(GlobalValue.QUESTION_PHOTO_DOWNLOAD_URL,questionPhotoDownloadUrl);
            quizDetails.put(GlobalValue.CATEGORY,category);
            quizDetails.put(GlobalValue.IS_PUBLIC,isPublic);
            quizDetails.put(GlobalValue.IS_CLOSED,isClosed);
            quizDetails.put(GlobalValue.IS_PHOTO_INCLUDED,isPhotoIncluded);
            if(isEdition){
                quizDetails.put(GlobalValue.DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());

            }else {
                quizDetails.put(GlobalValue.DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());
                quizDetails.put(GlobalValue.DATE_CREATED_TIME_STAMP, FieldValue.serverTimestamp());
            }
            writeBatch.set(documentReference1,quizDetails, SetOptions.merge());

            if (!isEdition) {
                DocumentReference userReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId());
                HashMap<String, Object> userDetails = new HashMap<>();
                userDetails.put(GlobalValue.TOTAL_QUESTIONS, FieldValue.increment(1L));
                writeBatch.set(userReference, userDetails, SetOptions.merge());
            }


            writeBatch.commit()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            questionListener.onFailed(e.getMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            questionListener.onSuccess();
                        }
                    });
        }else{

            //Tell the user to input his question
        }
    }

    private void renderQuestionDataBeforeEdition(){

        questionEditText.setText(questionDatamodel.getQuestionBody());
        visibilityIndicatorSwitch.setChecked(questionDatamodel.isPublic());
        closedIndicatorSwitch.setChecked(questionDatamodel.isClosed());
        Glide.with(MakeNewInquiryActivity.this)
                .load(questionDatamodel.getPhotoDownloadUrl())
                .centerCrop()
                .into(questionPhoto);


    }

    //not efficient for now
    private void fetchQuestionDataBeforeEdition(){
        GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_QUESTIONS)
                .document(questionId)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                    }
                });
    }

    private void initCategorySpinner(Spinner categorySelectorSpinner){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,GlobalValue.getProductCategoryList());
        categorySelectorSpinner.setAdapter(arrayAdapter);

        categorySelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                isCategorySelected = true;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(getApplicationContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

            if (requestCode == CAMERA_PERMISSION_REQUEST_CODE){
                fireCameraIntent();
            }

            if (requestCode == GALLERY_PERMISSION_REQUEST_CODE){
                fireGalleryIntent();
            }

        }
    }

    public void openGallery(){
        requestForPermissionAndPickImage(GALLERY_PERMISSION_REQUEST_CODE);

    }
    public void openCamera(){
        requestForPermissionAndPickImage(CAMERA_PERMISSION_REQUEST_CODE);
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
    public void requestForPermissionAndPickImage(int requestCode){
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

    private void uploadQuestionPhoto(QuestionPhotoUploadListener questionPhotoUploadListener){

        StorageReference coverPhotoStorageReference  = GlobalValue.getFirebaseStorageInstance().getReference().child(GlobalValue.ALL_QUESTIONS+"/"+questionId+"/"+GlobalValue.PHOTOS+".PNG");
        questionPhoto.setDrawingCacheEnabled(true);
        Bitmap coverPhotoBitmap = questionPhoto.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        coverPhotoBitmap.compress(Bitmap.CompressFormat.PNG,20,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        UploadTask coverPhotoUploadTask = coverPhotoStorageReference.putBytes(bytes);

        coverPhotoUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                questionPhotoUploadListener.onFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                coverPhotoUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return coverPhotoStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String coverPhotoDownloadUrl = String.valueOf(task.getResult());
                        questionPhotoUploadListener.onSuccess(coverPhotoDownloadUrl);
                    }
                });
            }
        });

    }


    /**
     * A callback triggered either if the QUESTION is successfully set or failed to send
     * */
    interface QuestionListener{
        /**
         * Triggered when the link is successfully sent to the given email address
         * */
        void onSuccess();
        /**
         * Triggered when the link fails to send to the email address
         * @param errorMessage the error message indicating the cause of the failure
         * */
        void onFailed(String errorMessage);
    }
    interface QuestionPhotoUploadListener{

        void onSuccess(String downloadUrl);

        void onFailed(String errorMessage);
    }


//
//c static final String TOTAL_NUMBER_OF_VIEWS_KEY = "TOTAL_NUMBER_OF_VIEWS";

//    public static final String QUESTION_ID_KEY = "QUESTION_ID";
//    public static final String QUESTION_DATA_MODEL_KEY = "QUESTION_DATA_MODEL";
//    public static final String QUESTION_BODY_KEY = "QUESTION_BODY";
//    public static final String QUESTION_PHOTO_DOWNLOAD_URL_KEY = "QUESTION_PHOTO_DOWNLOAD_URL";
//    public static final String ALL_QUESTIONS_KEY = "ALL_QUESTIONS";
//    public static final String IS_PHOTO_INCLUDED_KEY = "IS_PHOTO_INCLUDED";
//    public static final String PHOTOS_KEY = "PHOTOS";
//    public static final String QUESTION_SEARCH_ANY_MATCH_KEYWORD_KEY = "QUESTION_SEARCH_ANY_MATCH_KEYWORD";
//    public static final String TOTAL_NUMBER_OF_ANSWER_KEY = "TOTAL_NUMBER_OF_ANSWER";
//    publi
}