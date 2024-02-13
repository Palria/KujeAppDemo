
package com.palria.kujeapp;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.palria.kujeapp.SingleProductActivity;
import com.palria.kujeapp.SingleServiceActivity;
import com.palria.kujeapp.adapters.CatalogAdapter;
import com.palria.kujeapp.adapters.ProductOrderRcvAdapter;
import com.palria.kujeapp.adapters.RequestAdapter;
import com.palria.kujeapp.adapters.RequestAdapter;
import com.palria.kujeapp.models.ProductOrderDataModel;
import com.palria.kujeapp.models.RequestDataModel;
import com.palria.kujeapp.models.SalesRecordDataModel;
import com.palria.kujeapp.models.ServiceDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class ServiceCatalogFragment extends Fragment {


    CatalogCallback catalogCallback;
    CatalogAdapter catalogRcvAdapter;
    ArrayList<CatalogDataModel> catalogDataModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;
    String userId;
    LinearLayout containerLinearLayout;
    //    boolean isSingleService = false;
//    boolean isSingleCustomer = false;
    AlertDialog alertDialog;

    String serviceId = "";
    //    String customerId = "";
    ImageView catalogImageView;
    Uri catalogUri;
    private final int CAMERA_PERMISSION_REQUEST_CODE = 2002;
    private final int GALLERY_PERMISSION_REQUEST_CODE = 23;
    private final int VIDEO_PERMISSION_REQUEST_CODE = 20;
    CatalogUploadListener catalogUploadListener;

    ActivityResultLauncher<Intent> openGalleryLauncher;
    ActivityResultLauncher<Intent> openCameraLauncher;


    public ServiceCatalogFragment() {
        // Required empty public constructor
    }
    public ServiceCatalogFragment(SingleServiceActivity.OnAddNewCatalogButtonClickListener onAddNewCatalogButtonClickListener) {
        SingleServiceActivity.onAddNewCatalogButtonClickListener = new SingleServiceActivity.OnAddNewCatalogButtonClickListener() {
            @Override
            public void onClick(View button) {

                        final String[] recordId = {""};

                        BottomSheetCatalogFormBuilderWidget bottomSheetCatalogFormBuilderWidget =  new BottomSheetCatalogFormBuilderWidget(getContext());
                bottomSheetCatalogFormBuilderWidget.setOnSubmit(new BottomSheetFormBuilderWidget.OnSubmitHandler(){
                    @Override
                    public void onSubmit(String title, String body) {
                        super.onSubmit(title, body);
                        uploadCatalogPhoto(title,body);
                    }
                });
                catalogImageView = new ImageView(getContext());
                catalogImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,200));
                catalogImageView.setImageResource(R.drawable.ic_outline_camera_alt_24);
                catalogImageView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                               GlobalValue.createPopUpMenu(getContext(), R.menu.pick_image_menu, catalogImageView, new GlobalValue.OnMenuItemClickListener() {
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
                bottomSheetCatalogFormBuilderWidget.setTitle("Feed your customers with your service samples with photos")
                                .setPositiveTitle("Add");
                bottomSheetCatalogFormBuilderWidget.addImage(catalogImageView);
                        BottomSheetFormBuilderWidget.EditTextInput titleEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(getContext());
                        titleEditTextInput.setHint("Enter title")
                                .autoFocus();
                bottomSheetCatalogFormBuilderWidget.addInputField(titleEditTextInput);

                        BottomSheetFormBuilderWidget.EditTextInput captionEditTextInput = new BottomSheetFormBuilderWidget.EditTextInput(getContext());
                        captionEditTextInput.setHint("Enter description");
                bottomSheetCatalogFormBuilderWidget.addInputField(captionEditTextInput);

                bottomSheetCatalogFormBuilderWidget
                                .render("Add")
                                .show();
            }
        };
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(getArguments() != null){
            serviceId = getArguments().getString(GlobalValue.SERVICE_ID,"");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_service_catalog, container, false);
        initUI(parentView);

        alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();

        catalogUploadListener = new CatalogUploadListener() {
            @Override
            public void onSuccess(String catalogId,String title,String description,String coverPhotoDownloadUrl) {

                toggleProgress(false);
                catalogDataModelArrayList.add(new CatalogDataModel(catalogId,serviceId,title,"now",description,"coverPhotoDownloadUrl"));
                catalogRcvAdapter.notifyItemChanged(catalogDataModelArrayList.size());

                GlobalValue.showAlertMessage("success",
                        getContext(),
                        "Catalog successfully added",
                        "Your catalog has been successfully added: ");

            }

            @Override
            public void onFailed(String errorMessage) {
                GlobalValue.showAlertMessage("error",
                        getContext(),
                        "Catalog failed to add",
                        "Your catalog failed to add with error: "+errorMessage);

            }
        };
        initRecyclerView();

        openGalleryLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {

            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getData() != null) {
                    catalogImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    Intent data=result.getData();

//                    ClipData clipData=data.getClipData();
//                    for(int i = 0;i<clipData.getItemCount();i++){
                        catalogUri =data.getData();
                        catalogImageView.setImageURI(catalogUri);
                        catalogImageView.setDrawingCacheEnabled(true);
                        catalogUri = GlobalValue.getImageUri(getContext(),catalogImageView.getDrawingCache(),10);

//                    }


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
                        catalogImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        Intent data = result.getData();
                        Bitmap bitmapFromCamera =(Bitmap) data.getExtras().get("data");

//                            uriArrayList.add(uriFromCamera);
                        if(bitmapFromCamera != null) {
                             catalogUri = GlobalValue.getImageUri(getContext(),bitmapFromCamera,10);
                            catalogImageView.setImageURI(catalogUri);
                        }

                    }
                    // }
                }else{
                    Toast.makeText(getContext(), "No image captured!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        catalogCallback = new CatalogCallback() {
            @Override
            public void onSuccess(CatalogDataModel catalogDataModel) {
                catalogDataModelArrayList.add(catalogDataModel);
                catalogRcvAdapter.notifyItemChanged(catalogDataModelArrayList.size());
            }

            @Override
            public void onFailed(String errorMessage) {

            }
        };
        getCatalog();

        if(getContext().getClass().equals(SingleProductActivity.class)){
            recyclerView.setPadding(2,2,2,200);
        }
        return parentView;
    }
    private  void initUI(View parentView){
        recyclerView = parentView.findViewById(R.id.catalogRecyclerViewId);
//        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);

    }
    private void initRecyclerView(){
        catalogRcvAdapter = new CatalogAdapter(catalogDataModelArrayList,getContext(),recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(catalogRcvAdapter);

        /*requestDataModelArrayList.add(new RequestDataModel(
                serviceId,
                "serviceName",
                "requestId",
                "customerId",
                "dateRequest",
                "requestDescription",
                "customerContactPhoneNumber",
                "customerContactEmail",
                "customerContactAddress",
                "customerContactLocation",
                false
        ));
        */

        catalogRcvAdapter.notifyItemChanged(catalogDataModelArrayList.size());
    }
    private void getCatalog(){
        Query catalogQuery = firebaseFirestore.collection(GlobalValue.PLATFORM_SERVICES).document(serviceId).collection(GlobalValue.SERVICE_CATALOG);

        catalogQuery.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        }).addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    String catalogId = documentSnapshot.getId();

                    String catalogTitle  = ""+documentSnapshot.get(GlobalValue.CATALOG_TITLE);
                    String catalogDescription  = ""+documentSnapshot.get(GlobalValue.CATALOG_DESCRIPTION);
                    String catalogCoverPhotoDownloadUrl  = ""+documentSnapshot.get(GlobalValue.CATALOG_COVER_PHOTO_DOWNLOAD_URL);

                    String dateAdded  = documentSnapshot.get(GlobalValue.DATE_ADDED_TIME_STAMP)!=null?documentSnapshot.getTimestamp(GlobalValue.DATE_ADDED_TIME_STAMP).toDate()+"":"Moments ago";
                    if(dateAdded.length()>10){
                        dateAdded = dateAdded.substring(0,10);
                    }

                    catalogCallback.onSuccess(new CatalogDataModel(
                            catalogId,
                            serviceId,
                            catalogTitle,
                            dateAdded,
                            catalogDescription,
                            catalogCoverPhotoDownloadUrl
                    ));



//                    firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(GlobalValue.getBusinessAdminId()).collection(GlobalValue.PAGES).document(GlobalValue.getCurrentBusinessId()).collection(GlobalValue.PRODUCTS).document(productId).collection(GlobalValue.PRODUCT_PROFILE).document(productId).get().addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                        }
//                    }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                            String productImageImageDownloadUrl = ""+documentSnapshot.get(GlobalValue.IMAGE_DOWNLOAD_URL);
//                            String productName  = ""+documentSnapshot.get(GlobalValue.PRODUCT_TITLE);
//                            String productDescription = ""+documentSnapshot.get(GlobalValue.PRODUCT_DESCRIPTION);
//
////                            displayOrderView(getContext(),
////                                    containerLinearLayout,
////                                     productImageImageDownloadUrl,
////                                     productName,
////                                     productId,
////                                     dateOrdered2,
////                                     productDescription,
////                                     customerName,
////                                     customerContactPhoneNumber,
////                                     customerContactEmail,
////                                     customerContactAddress,
////                                     customerContactLocation,
////                                     orderDescription
////                            );
//                        }
//                    });
                }
            }
        });
    }


    public void openGallery(){
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//        isVisible = false;
        requestForPermission(GALLERY_PERMISSION_REQUEST_CODE);

    }
    public void openCamera(){
//        findViewById(R.id.chooseImageLinearLayoutId).setVisibility(View.GONE);
//        isVisible = false;
        requestForPermission(CAMERA_PERMISSION_REQUEST_CODE);
    }

    public void fireGalleryIntent(){
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_PICK);
//        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        galleryIntent.setType("image/*");
        openGalleryLauncher.launch(galleryIntent);
    }
    public void fireCameraIntent(){
        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraLauncher.launch(cameraIntent);
    }
    public void requestForPermission(int requestCode){
        if(getContext().checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED || getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
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
        if(show){
            alertDialog.show();
        }else{
            alertDialog.cancel();
        }
    }

    private void uploadCatalogPhoto(String title,String description){
        toggleProgress(true);
        String catalogId = GlobalValue.getRandomString(60);
        StorageReference coverPhotoStorageReference  = GlobalValue.getFirebaseStorageInstance().getReference().child(GlobalValue.ALL_USERS+"/"+GlobalValue.getCurrentUserId()+"/"+GlobalValue.PLATFORM_SERVICES+"/"+serviceId+"/"+GlobalValue.SERVICE_CATALOG+"/"+catalogId+".PNG");
//        pickImageActionButton.setDrawingCacheEnabled(true);
//        Bitmap coverPhotoBitmap = pickImageActionButton.getDrawingCache();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        coverPhotoBitmap.compress(Bitmap.CompressFormat.PNG,20,byteArrayOutputStream);
//        byte[] bytes = byteArrayOutputStream.toByteArray();
        UploadTask coverPhotoUploadTask = coverPhotoStorageReference.putFile(catalogUri);

        coverPhotoUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                catalogUploadListener.onFailed(e.getMessage());
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
//                        String coverPhotoStorageReference_2 = coverPhotoStorageReference.getPath();
                        addCatalog(catalogId,title,description,coverPhotoDownloadUrl);

                    }
                });
            }
        });

    }

    private void addCatalog(String catalogId,String catalogTitle,String catalogDescription,String catalogPhotoDownloadUrl){
        WriteBatch writeBatch = GlobalValue.getFirebaseFirestoreInstance().batch();
        DocumentReference documentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).document(serviceId).collection(GlobalValue.SERVICE_CATALOG).document(catalogId);
        HashMap<String,Object> details = new HashMap<>();
        details.put(GlobalValue.CATALOG_ID,catalogId);
        details.put(GlobalValue.CATALOG_TITLE,catalogTitle);
        details.put(GlobalValue.CATALOG_DESCRIPTION,catalogDescription);
        details.put(GlobalValue.CATALOG_COVER_PHOTO_DOWNLOAD_URL,catalogPhotoDownloadUrl);
        details.put(GlobalValue.DATE_ADDED_TIME_STAMP, FieldValue.serverTimestamp());
        writeBatch.set(documentReference,details, SetOptions.merge());

         DocumentReference serviceReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_SERVICES).document(serviceId);
        HashMap<String,Object> details2 = new HashMap<>();
        details.put(GlobalValue.TOTAL_NUMBER_OF_CATALOG,FieldValue.increment(1L));
        writeBatch.update(serviceReference,details2);

        writeBatch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        toggleProgress(false);
                        GlobalValue.showAlertMessage("error",
                                getContext(),
                                "Catalog failed to add",
                                "Your catalog failed to add with error: "+e.getMessage());

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        catalogUploadListener.onSuccess(catalogId,catalogTitle, catalogDescription,catalogPhotoDownloadUrl);

                    }
                });

    }

    interface CatalogCallback{
        void onSuccess(CatalogDataModel catalogDataModel);
        void onFailed(String errorMessage);
    }
    interface CatalogUploadListener{
        void onSuccess(String catalogId,String title,String description , String coverPhotoDownloadUrl);
        void onFailed(String errorMessage);

    }

}