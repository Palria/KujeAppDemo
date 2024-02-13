package com.palria.kujeapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;
import com.palria.kujeapp.GlobalValue;

import java.util.HashMap;

public class AddNewServiceActivity extends AppCompatActivity {

    TextInputEditText titleTextEditText;
    TextInputEditText descriptionEditText;
    String title;
    String description;
    String serviceId;
    Button addActionButton;
    AlertDialog alertProgressDialog;
    AlertDialog successDialog;
    AlertDialog confirmServiceAdditionDialog;
    ImageButton backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_service);
        titleTextEditText = findViewById(R.id.titleEditTextId);
        descriptionEditText = findViewById(R.id.descriptionEditTextId);
        backButton = findViewById(R.id.backButton);
        addActionButton = findViewById(R.id.addServiceActionButtonId);
        addActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmServiceAdditionDialog.show();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddNewServiceActivity.super.onBackPressed();
            }
        });
        alertProgressDialog = new AlertDialog.Builder(AddNewServiceActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();
        createSuccessDialog();
        initConfirmOrderDialog(false,"");
    }

    void addService() {

        title = titleTextEditText.getText() + "";
        description = descriptionEditText.getText() + "";
        serviceId = GlobalValue.getRandomString(60);
        if (!(description.trim().isEmpty())){
            if (!(title.trim().isEmpty())){
                toggleProgress(true);
                HashMap<String, Object> notDetails = new HashMap<>();
                notDetails.put(GlobalValue.SERVICE_ID, serviceId);
                notDetails.put(GlobalValue.SERVICE_TITLE, title);
//                notDetails.put(GlobalValue.CUSTOMER_ID, GlobalValue.getCurrentUserId());
                notDetails.put(GlobalValue.SERVICE_DESCRIPTION, description);
                notDetails.put(GlobalValue.SEARCH_VERBATIM_KEYWORD, GlobalValue.generateSearchVerbatimKeyWords(title));
                notDetails.put(GlobalValue.SEARCH_ANY_MATCH_KEYWORD,GlobalValue.generateSearchAnyMatchKeyWords(title));
                notDetails.put(GlobalValue.DATE_ADDED_TIME_STAMP, FieldValue.serverTimestamp());
                GlobalValue.getFirebaseFirestoreInstance()
                        .collection(GlobalValue.PLATFORM_SERVICES)
                        .document(serviceId)
                        .set(notDetails, SetOptions.merge())
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toggleProgress(false);
                                initConfirmOrderDialog(true,e.getMessage());
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        toggleProgress(false);
                        successDialog.show();

                    }
                });
            }else{
                titleTextEditText.setError("Provide title");
                GlobalValue.createSnackBar(AddNewServiceActivity.this,addActionButton,"Error: You need to provide service title in the text field", Snackbar.LENGTH_INDEFINITE);
            }
        }else{
            descriptionEditText.setError("Provide description");
            GlobalValue.createSnackBar(AddNewServiceActivity.this,addActionButton,"Error: You need to provide service description in the text field", Snackbar.LENGTH_INDEFINITE);
        }
    }

    private void toggleProgress(boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(show){
                    alertProgressDialog.show();
                }else{
                    alertProgressDialog.cancel();
                }
            }
        });

    }
    public void createSuccessDialog(){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);

        builder.setTitle("Successfully Added!");
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
                        AddNewServiceActivity.this.finish();
                    }
                }).setNeutralButton("Add new", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                titleTextEditText.setText("");
                descriptionEditText.setText("");
            }
        });
        successDialog =builder.create();

        // successDialog.show();

    }
    public void initConfirmOrderDialog(boolean isRetry, String errorMessage){

        AlertDialog.Builder builder =new AlertDialog.Builder(this);
        String button = "Confirm";
        String message = "Confirm to add your service";
        if(isRetry){
            button = "Retry";
            message = "Your new service failed to add with error: "+errorMessage+" Please try again";
        }

        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        addService();

                    }
                });
            }
        }).setNeutralButton("Not yet",null);
        confirmServiceAdditionDialog =builder.create();
        if(isRetry){
            confirmServiceAdditionDialog.show();
        }


    }


}