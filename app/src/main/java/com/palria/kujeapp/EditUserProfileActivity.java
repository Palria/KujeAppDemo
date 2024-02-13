package com.palria.kujeapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.palria.kujeapp.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class EditUserProfileActivity extends AppCompatActivity {


    private String userDisplayName;
    private String description;
    private String userCountryOfResidence;
    private String contactEmail;
    private String contactPhoneNumber;
    private String webLink;
    private String genderType;
    private EditText userDisplayNameEditText;
    private EditText descriptionInput;
    private EditText contactEmailEditText;
    private EditText webLinkInput;
    private EditText genderTypeEditText;
    private EditText contactPhoneNumberEditText;
    private EditText userCountryOfResidenceEditText;

    private Spinner genderTypeSpinner;
    private Spinner countrySpinner;

    TextView display_name;
    TextView display_email;
    RoundedImageView profile_image_view;

    boolean isBirthDateSet = false;
    private String birthDate;
    private EditText birthDateInput;

    boolean isProfilePhotoChanged;
    boolean isProfilePhotoIncluded;
    boolean isUserBlocked = false;
    int CAMERA_PERMISSION_REQUEST_CODE = 2002;
    int GALLERY_PERMISSION_REQUEST_CODE = 23;

    ImageView profilePhotoImageView;
    /**
     * This field will be initialized from the database if there has been a profile photo before trying to edit
     * It is the user's profile photo url which will be converted to a {@link Uri} when retrieved from the database
     * When the user edits the profile but did not make change on his profile photo, this value is still retained for downloading his
     * profile photo else ifn he changes the photo then another value will fill it after changing his profile photo.
     * */
    String retrievedProfilePictureDownloadUrl;
    String retrievedProfilePhotoStorageReference;
    Bitmap cameraImageBitmap;
    Uri galleryImageUri;

    Button editProfileActionButton;
    Button cancelButton;
    ImageView pickImageActionButton;
    ActivityResultLauncher<Intent> openGalleryLauncher;
    ActivityResultLauncher<Intent> openCameraLauncher;
    AlertDialog alertDialog;
    DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        initUI();
        toggleProgress(true);
        initUserProfileValuesBeforeEdition(new ProfileValueInitListener() {
            @Override
            public void onSuccess(String userDisplayName,String description,String birthDate, String userCountryOfResidence, String contactEmail,String webLink, String contactPhoneNumber, String genderType, String userProfilePhotoDownloadUrl, String profilePhotoStorageReference, boolean isUserBlocked, boolean isUserProfilePhotoIncluded) {

                userDisplayNameEditText.setText(userDisplayName);
                descriptionInput.setText(description);
                birthDateInput.setText(birthDate);
                contactEmailEditText.setText(contactEmail);
                webLinkInput.setText(webLink);
                contactPhoneNumberEditText.setText(contactPhoneNumber);
//           genderTypeEditText.setText(genderType);
                retrievedProfilePictureDownloadUrl = userProfilePhotoDownloadUrl;
                retrievedProfilePhotoStorageReference = profilePhotoStorageReference;
                EditUserProfileActivity.this.isUserBlocked = isUserBlocked;
                EditUserProfileActivity.this.isProfilePhotoIncluded = isUserProfilePhotoIncluded;

                display_name.setText(userDisplayName);
                display_email.setText(contactEmail);

                ///gender auto select after info loaded
                switch (genderType.toLowerCase()){
                    case "male":
                        genderTypeSpinner.setSelection(0);
                        break;
                    case "female":
                        genderTypeSpinner.setSelection(1);
                        break;
                    case "other":
                        genderTypeSpinner.setSelection(2);
                        break;
                    default:break;
                }


                //country auto select
                ArrayList<String> countries = GlobalValue.getCountryArrayList(null);
                for(String country : countries){
                    if(country.toLowerCase().equals(userCountryOfResidence.toLowerCase())){
                        countrySpinner.setSelection(countries.indexOf(country));
                        break;
                    }
                }


                Log.w("success_tag",userCountryOfResidence);
                toggleProgress(false);


                Glide.with(EditUserProfileActivity.this)
                        .load(userProfilePhotoDownloadUrl)
                        .centerCrop()
                        .error(R.drawable.default_profile)
                        .into(profile_image_view);
            }

            @Override
            public void onFailed(String errorMessage) {
                toggleProgress(false);

            }
        });
        prepareDatePickerDialog();
        birthDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });
        openGalleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    Intent data=result.getData();
                    galleryImageUri = data.getData();
                    Picasso.get().load(galleryImageUri).into(profile_image_view);
//                    Glide.with(EditUserProfileActivity.this)
//                            .load(data.toUri(Intent.URI_ALLOW_UNSAFE))
//                            .load(galleryImageUri)
//                            .centerCrop()
//                            .into(profile_image_view);
                    isProfilePhotoIncluded = true;
                    isProfilePhotoChanged = true;


                }
            }
        });
        openCameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()==RESULT_OK) {

                    if (result.getData() != null) {
                        Intent data = result.getData();
                        Bitmap  bitmapFromCamera =(Bitmap) data.getExtras().get("data");

                        if(bitmapFromCamera != null) {
                            cameraImageBitmap = bitmapFromCamera;
                            //profilePhotoImageView.setImageBitmap(cameraImageBitmap);
                            Glide.with(EditUserProfileActivity.this)
                                    .asBitmap()
                                    .load(cameraImageBitmap)
                                    .centerCrop()
                                    .into(profile_image_view);
                            isProfilePhotoIncluded = true;
                            isProfilePhotoChanged = true;
                        }

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "No image captured!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditUserProfileActivity.this.onBackPressed();
            }
        });

        editProfileActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDisplayName = userDisplayNameEditText.getText()+"";
                description = descriptionInput.getText()+"";
                birthDate = birthDateInput.getText()+"";
                userCountryOfResidence = countrySpinner.getSelectedItem()+"";
                genderType = genderTypeSpinner.getSelectedItem()+"";
                contactEmail = contactEmailEditText.getText()+"";
                webLink = webLinkInput.getText()+"";
                contactPhoneNumber = contactPhoneNumberEditText.getText()+"";

                if(isBirthDateSet){
                    if(!userDisplayName.isEmpty()){
                        toggleProgress(true);

                        if(isProfilePhotoIncluded){
                            if(isProfilePhotoChanged){
                                uploadUserProfilePhoto(new ProfilePhotoUploadListener() {
                                    @Override
                                    public void onSuccess(String profilePhotoDownloadUrl,String profilePhotoStorageReference) {
                                        editUserProfile(profilePhotoDownloadUrl,profilePhotoStorageReference, new ProfileCreationListener() {
                                            @Override
                                            public void onSuccess(String userName) {
                                                //succeed in editing profile

                                                GlobalHelpers.showAlertMessage("success",
                                                        EditUserProfileActivity.this,
                                                        "Profile Edited Successfully.",
                                                        "You have successfully edited your profile, Learn Era has more knowledge to offer you, go ahead and learn more.");



                                            }

                                            @Override
                                            public void onFailed(String errorMessage) {

                                                //failed to post new changes of user's profile
                                                toggleProgress(false);

                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailed(String errorMessage) {
                                        //failed to upload user's profile photo
                                        toggleProgress(false);

                                    }
                                });
                            }else{
                                editUserProfile(retrievedProfilePictureDownloadUrl,retrievedProfilePhotoStorageReference, new ProfileCreationListener() {
                                    @Override
                                    public void onSuccess(String userName) {
                                        //succeed in editing profile
                                        GlobalHelpers.showAlertMessage("success",
                                                EditUserProfileActivity.this,
                                                "Profile Edited Successfully.",
                                                "You have successfully edited your profile, Learn Era has more knowledge to offer you, go ahead and learn more.");

                                        display_name.setText(userDisplayNameEditText.getText().toString());
                                        display_email.setText(contactEmailEditText.getText().toString());


                                    }

                                    @Override
                                    public void onFailed(String errorMessage) {
                                        //failed to post new changes of user's profile
                                        toggleProgress(false);

                                    }
                                });
                            }
                        }else {
                            editUserProfile("","", new ProfileCreationListener() {
                                @Override
                                public void onSuccess(String userName) {
                                    //succeed in editing profile
                                    GlobalHelpers.showAlertMessage("success",
                                            EditUserProfileActivity.this,
                                            "Profile Edited Successfully.",
                                            "You have successfully edited your profile, Learn Era has more knowledge to offer you, go ahead and learn more.");

                                    toggleProgress(false);
                                    display_name.setText(userDisplayNameEditText.getText().toString());
                                    display_email.setText(contactEmailEditText.getText().toString());
                                }

                                @Override
                                public void onFailed(String errorMessage) {
                                    //failed to post new changes of user's profile
                                    toggleProgress(false);

                                }
                            });
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Please enter your user name", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Please set your date of birth", Toast.LENGTH_SHORT).show();

                    birthDateInput.performClick();
                }

            }
        });

        pickImageActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GlobalValue.createPopUpMenu(EditUserProfileActivity.this,R.menu.pick_image_menu , pickImageActionButton, new GlobalValue.OnMenuItemClickListener() {
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
    }
    @Override
    public void onBackPressed(){
        createConfirmExitDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initUI(){

        Toolbar actionBar = (Toolbar)  findViewById(R.id.topBar);
        setSupportActionBar(actionBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         *  private EditText userDisplayNameEditText;
         private EditText contactEmailEditText;
         private EditText genderTypeEditText;
         private EditText contactPhoneNumberEditText;
         private EditText userCountryOfResidenceEditText;
         * */

        userDisplayNameEditText = findViewById(R.id.nameInput);
        descriptionInput = findViewById(R.id.descriptionInputId);
        birthDateInput = findViewById(R.id.birthDateInputId);
        contactEmailEditText = findViewById(R.id.emailInput);
        webLinkInput = findViewById(R.id.webLinkInputId);
        contactPhoneNumberEditText = findViewById(R.id.contactInput);

        //gender and country is spinner.
        genderTypeSpinner = findViewById(R.id.genderSpinner);
        countrySpinner = findViewById(R.id.countrySpinner);

        pickImageActionButton = findViewById(R.id.photoSelectorButton);
        editProfileActionButton = findViewById(R.id.update_button);
        display_name = findViewById(R.id.current_name);
        display_email = findViewById(R.id.current_email);
        profile_image_view = findViewById(R.id.imageView1);

        cancelButton=findViewById(R.id.cancelButton);


        initCountrySpinner();
        initGenderSpinner();


        //init progress.
        alertDialog = new AlertDialog.Builder(EditUserProfileActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();
    }


    private void toggleProgress(boolean show)
    {
        if(show){
            alertDialog.show();
        }else{
            alertDialog.cancel();
        }
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
                EditUserProfileActivity.super.onBackPressed();
            }
        })
                .setNegativeButton("Stay back", null);
        confirmExitDialog = builder.create();
        confirmExitDialog.show();

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


    private void uploadUserProfilePhoto(ProfilePhotoUploadListener profilePhotoUploadListener){
        StorageReference profilePhotoStorageReference  = GlobalValue.getFirebaseStorageInstance().getReference().child(GlobalValue.ALL_USERS+"/"+GlobalValue.getCurrentUserId()+"/"+GlobalValue.USER_IMAGES+"/"+GlobalValue.USER_PROFILE_PHOTO+".PNG");
        profile_image_view.setDrawingCacheEnabled(true);
        Bitmap profilePhotoBitmap = profile_image_view.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profilePhotoBitmap.compress(Bitmap.CompressFormat.PNG,20,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        UploadTask profilePhotoUploadTask = profilePhotoStorageReference.putBytes(bytes);

        profilePhotoUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                profilePhotoUploadListener.onFailed(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                profilePhotoUploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        return profilePhotoStorageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String profilePhotoDownloadUrl = String.valueOf(task.getResult());
                        String profilePhotoStorageReference_2 = profilePhotoStorageReference.getPath();

                        profilePhotoUploadListener.onSuccess(profilePhotoDownloadUrl,profilePhotoStorageReference_2);
                    }
                });
            }
        });

    }

    private void editUserProfile(String userProfilePhotoDownloadUrl,String profilePhotoStorageReference, ProfileCreationListener profileCreationListener){
        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();

//        DocumentReference userProfileDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId()).collection(GlobalValue.USER_PROFILE).document(GlobalValue.getCurrentUserId());
        DocumentReference userProfileDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId());

        HashMap<String,Object>userProfileDetails = new HashMap<>();
        userProfileDetails.put(GlobalValue.USER_DISPLAY_NAME,userDisplayName);
        userProfileDetails.put(GlobalValue.USER_DESCRIPTION,description);
        userProfileDetails.put(GlobalValue.USER_BIRTH_DATE,birthDate);
        userProfileDetails.put(GlobalValue.USER_ID,GlobalValue.getCurrentUserId());
        userProfileDetails.put(GlobalValue.USER_COUNTRY_OF_RESIDENCE,userCountryOfResidence);
        userProfileDetails.put(GlobalValue.USER_GENDER_TYPE,genderType);
        userProfileDetails.put(GlobalValue.IS_USER_BLOCKED,isUserBlocked);
        userProfileDetails.put(GlobalValue.USER_CONTACT_EMAIL_ADDRESS,contactEmail);
        userProfileDetails.put(GlobalValue.USER_PERSONAL_WEBSITE_LINK,webLink);
        userProfileDetails.put(GlobalValue.USER_PROFILE_PHOTO_DOWNLOAD_URL,userProfilePhotoDownloadUrl);
        userProfileDetails.put(GlobalValue.USER_PROFILE_PHOTO_STORAGE_REFERENCE,profilePhotoStorageReference);
        userProfileDetails.put(GlobalValue.IS_USER_PROFILE_PHOTO_INCLUDED,isProfilePhotoIncluded);
        userProfileDetails.put(GlobalValue.USER_CONTACT_PHONE_NUMBER,contactPhoneNumber);
        userProfileDetails.put(GlobalValue.USER_TOKEN_ID,GlobalValue.getCurrentUserTokenId());
        userProfileDetails.put(GlobalValue.USER_PROFILE_DATE_EDITED,GlobalValue.getDate());
        userProfileDetails.put(GlobalValue.USER_PROFILE_DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());
//        userProfileDetails.put(GlobalValue.DOCUMENT_CREATED, "DOCUMENT_CREATED");


        userProfileDetails.put(GlobalValue.USER_SEARCH_VERBATIM_KEYWORD, GlobalValue.generateSearchVerbatimKeyWords(userDisplayName));
        userProfileDetails.put(GlobalValue.USER_SEARCH_ANY_MATCH_KEYWORD,GlobalValue.generateSearchAnyMatchKeyWords(userDisplayName));

        writeBatch.set(userProfileDocumentReference,userProfileDetails,SetOptions.merge());


//        DocumentReference userDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId());
//        HashMap<String,Object>userDetails = new HashMap<>();
//        userDetails.put(GlobalValue.USER_PROFILE_DATE_EDITED,GlobalValue.getDate());
//        userDetails.put(GlobalValue.USER_PROFILE_DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());
//        writeBatch.set(userDocumentReference,userDetails, SetOptions.merge());

        writeBatch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        profileCreationListener.onSuccess(userDisplayName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileCreationListener.onFailed(e.getMessage());
                    }
                });
    }
    private void initUserProfileValuesBeforeEdition(ProfileValueInitListener profileValueInitListener){
        GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS).document(GlobalValue.getCurrentUserId())
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        profileValueInitListener.onFailed(e.getMessage());
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        String userDisplayName =""+ documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                        String description =""+ documentSnapshot.get(GlobalValue.USER_DESCRIPTION);
                        String birthDate =""+ documentSnapshot.get(GlobalValue.USER_BIRTH_DATE);
                        if(documentSnapshot.get(GlobalValue.USER_BIRTH_DATE) != null){
                            isBirthDateSet = true;
                        }
                        String userCountryOfResidence =""+ documentSnapshot.get(GlobalValue.USER_COUNTRY_OF_RESIDENCE);
                        String contactEmail =""+ documentSnapshot.get(GlobalValue.USER_CONTACT_EMAIL_ADDRESS);
                        String contactPhoneNumber =""+ documentSnapshot.get(GlobalValue.USER_CONTACT_PHONE_NUMBER);
                        String webLink =documentSnapshot.get(GlobalValue.USER_PERSONAL_WEBSITE_LINK)!=null? ""+ documentSnapshot.get(GlobalValue.USER_PERSONAL_WEBSITE_LINK):"";
                        String genderType =""+ documentSnapshot.get(GlobalValue.USER_GENDER_TYPE);
                        String userProfilePhotoDownloadUrl =""+ documentSnapshot.get(GlobalValue.USER_PROFILE_PHOTO_DOWNLOAD_URL);
                        String profilePhotoStorageReference =""+ documentSnapshot.get(GlobalValue.USER_PROFILE_PHOTO_STORAGE_REFERENCE);
                        boolean isUserBlocked = false;
                        boolean isUserProfilePhotoIncluded = false;
                        if(documentSnapshot.get(GlobalValue.IS_USER_BLOCKED) != null){
                            isUserBlocked =documentSnapshot.getBoolean(GlobalValue.IS_USER_BLOCKED);

                        }
                        if(documentSnapshot.get(GlobalValue.IS_USER_PROFILE_PHOTO_INCLUDED) != null){
                            isUserProfilePhotoIncluded = documentSnapshot.getBoolean(GlobalValue.IS_USER_PROFILE_PHOTO_INCLUDED);

                        }

                        profileValueInitListener.onSuccess( userDisplayName,description,birthDate, userCountryOfResidence, contactEmail,webLink, contactPhoneNumber, genderType, userProfilePhotoDownloadUrl,  profilePhotoStorageReference, isUserBlocked, isUserProfilePhotoIncluded);

                    }
                });
    }


    void prepareDatePickerDialog(){

        Calendar dateCalendar = Calendar.getInstance();
        final int YEAR = dateCalendar.get(Calendar.YEAR);
        final int MONTH = dateCalendar.get(Calendar.MONTH);
        final int DAY_OF_MONTH = dateCalendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int YEAR, int MONTH, int DAY_OF_MONTH) {
                MONTH += 1 ;
                birthDateInput.setText(DAY_OF_MONTH  + "/" + MONTH + "/" + YEAR);
                isBirthDateSet = true;
            }
        }, YEAR, MONTH, DAY_OF_MONTH);
        datePickerDialog.setCancelable(false);

    }

    /**
     * Initializes the gender spinner for selection
     * */
    private void initGenderSpinner(){
        String[] genderArray = {getResources().getString(R.string.male),getResources().getString(R.string.female),getResources().getString(R.string.other) };
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,genderArray);
        genderTypeSpinner.setAdapter(arrayAdapter);
        genderTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genderType = String.valueOf(genderTypeSpinner.getSelectedItem());
                //genderTypeEditText.setText(genderType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * Initializes the country spinner for selection
     * */
    private void initCountrySpinner(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,GlobalValue.getCountryArrayList(new ArrayList<>()));
        countrySpinner.setAdapter(arrayAdapter);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                userCountryOfResidence = String.valueOf(countrySpinner.getSelectedItem());
                //userCountryOfResidenceEditText.setText(userCountryOfResidence);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    interface ProfilePhotoUploadListener{
        void onSuccess(String profilePhotoDownloadUrl,String profilePhotoStorageReference);
        void onFailed(String errorMessage);

    }
    interface ProfileCreationListener{
        void onSuccess(String userName);
        void onFailed(String errorMessage);
    }
    interface ProfileValueInitListener{
        void onSuccess(String userDisplayName,String description,String birthDate,String userCountryOfResidence,String contactEmail,String webLink,String contactPhoneNumber,String genderType,String userProfilePhotoDownloadUrl,String profilePhotoStorageReference,boolean isUserBlocked,boolean isUserProfilePhotoIncluded);
        void onFailed(String errorMessage);
    }

}
