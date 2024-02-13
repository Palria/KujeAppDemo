
package com.palria.kujeapp;

//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class SignInActivity extends AppCompatActivity {
//    private FirebaseAuth fireAuth;
//
//    private TextInputEditText emailTextInputEditText;
//    private TextInputEditText passwordTextInputEditText;
//    private String email;
//    private String password;
//    private Button logInButton, goToSignUpActivityButton, goToChangePasswordActivityButton;
//
//
//    ProgressDialog progressDialog;
//    AlertDialog warningDialog;
//    AlertDialog choicePageDialog;
//
//    ConstraintLayout dialogMainConstraintLayout;
//    ProgressBar dialogProgressBar;
//    TextView dialogTitleTextView,dialogMessageTextView;
//    Button dialogCancelButton,  dialogTryAgainButton;
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_in);
//        // FirebaseApp firebaseApp = FirebaseApp.getInstance("com.govanceinc.obidientera");
//        fireAuth=FirebaseAuth.getInstance();
//        emailTextInputEditText= findViewById(R.id.emailEditText);
//        passwordTextInputEditText=findViewById(R.id.passwordEditText);
//        goToSignUpActivityButton= findViewById(R.id.goToSignUpActivityButton);
//        logInButton= findViewById(R.id.logInButton);
//        logInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                email= String.valueOf(emailTextInputEditText.getText());
//                password= String.valueOf(passwordTextInputEditText.getText());
//
//                if(!(email.equals("")) && !(password.equals("")) ) {
//
//                    signInUser(email.trim(), password.trim());
//                }else{
//                    Toast.makeText(SignInActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//
////        signInAnonymouslyActionButton= findViewById(R.id.signInAnonymouslyActionButtonId);
////        signInAnonymouslyActionButton.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                email= "anonymous@gmail.com";
////                password= "AUNCHENRY";
////                progressDialog.show();
////
////                fireAuth.signInWithEmailAndPassword(email, password)
////
////                        .addOnFailureListener(new OnFailureListener() {
////                            @Override
////                            public void onFailure(@NonNull Exception e) {
////                                progressDialog.cancel();
////                                warningDialog.setMessage(e.getMessage());
////                                warningDialog.show();
////                                Toast.makeText(SignInActivity.this, "Account signIn failed! " + e.getMessage(), Toast.LENGTH_LONG).show();
////
////                            }
////                        })
////                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
////                            @Override
////                            public void onComplete(@NonNull Task<AuthResult> task) {
////                                if (task.isSuccessful()) {
////                                    progressDialog.cancel();
////                                    Intent intent = new Intent(getApplication(),MainActivity.class);
////                                    startActivity(intent);
////                                    SignInActivity.this.finish();
////
////                                } else {
////                                    Toast.makeText(SignInActivity.this, "Account signIn failed! " + task.getException(), Toast.LENGTH_LONG).show();
////                                    warningDialog.show();
////                                }
////                            }
////                        });
////
////
////            }
////        });
//        goToChangePasswordActivityButton=(Button) findViewById(R.id.goToChangePasswordActivityButtonId);
//        goToChangePasswordActivityButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(SignInActivity.this, ChangePasswordActivity.class);
//                startActivity(intent);
//
//            }
//        });
//
//
//        goToSignUpActivityButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SignInActivity.this.finish();
//                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
//                startActivity(intent);
//
//            }
//        });
//
//        createProgressDialogue();
//        createWarningDialog();
//        createChoicePageDialog();
//
//    }
//
//
//    @Override
//    public void onBackPressed() {
//
//
//    }
//
//
//    /**Signs in a user to the platform*/
//    public void signInUser(String email, String password) {
//        if (!(emailTextInputEditText.getText() == null && passwordTextInputEditText.getText() == null)) {
//            if (!(email.equals("") && password.equals(""))) {
//
//                progressDialog.show();
//
//
//                fireAuth.signInWithEmailAndPassword(email, password)
//
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.cancel();
//                                warningDialog.setMessage(e.getMessage());
//                                warningDialog.show();
////                                Toast.makeText(SignInActivity.this, "Account signIn failed! " + e.getMessage(), Toast.LENGTH_LONG).show();
//
//                            }
//                        })
//                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()) {
//                                    FirebaseUser fireUser = fireAuth.getCurrentUser();
////                                    Toast.makeText(SignInActivity.this, "Account successfully signed in " + fireUser.getEmail(), Toast.LENGTH_LONG).show();
//
//                                    // progressDialog.cancel();
////                            checkUserDetails();
////                            FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
////                            String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
////
////                            firebaseFirestore.collection("ALL_USERS").document(userId).get().addOnFailureListener(new OnFailureListener() {
////                                @Override
////                                public void onFailure(@NonNull Exception e) {
////
////                                }
////                            }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
////                                @Override
////                                public void onSuccess(DocumentSnapshot documentSnapshot) {
////                                    /*Check if the user has created his main profile*/
////                                    if(documentSnapshot.getBoolean("MAIN_PROFILE_CREATED")!=null){
////                                        /*If he has created his main profile, then check if he has created his business profile*/
////                                       if (documentSnapshot.getBoolean("MAIN_PROFILE_CREATED")) {
////                                            /*Check if he has created his business profile*/
////                                            if (documentSnapshot.getBoolean("BUSINESS_PROFILE_CREATED")!=null) {
////                                                /*If he has created his business profile, then check if he has created his portfolio profile*/
////                                                if (documentSnapshot.getBoolean("BUSINESS_PROFILE_CREATED")) {
////
////                                                    SignInActivity.this.finish();
////                                                    startActivity(new Intent(SignInActivity.this,HomeActivity.class));
////
////                                            }
////                                                /*check if he has created his portfolio profile*/
////                                                else if( documentSnapshot.getBoolean("PORTFOLIO_PROFILE_CREATED")!=null){
////                                                    /*If he has created his portfolio profile, then move home*/
////                                                    if( documentSnapshot.getBoolean("PORTFOLIO_PROFILE_CREATED")){
////                                                        SignInActivity.this.finish();
////                                                        startActivity(new Intent(SignInActivity.this,HomeActivity.class));
////
////                                                    }
////                                                    // if he has not created portfolio page, then show dialog to chose which page to create
////                                                    else{
////                                                            progressDialog.cancel();
////                                                            choicePageDialog.show();
////                                                    }
////                                                }
////                                        }else if( documentSnapshot.getBoolean("PORTFOLIO_PROFILE_CREATED")!=null){
////                                                if( documentSnapshot.getBoolean("PORTFOLIO_PROFILE_CREATED")){
////                                                    SignInActivity.this.finish();
////                                                    startActivity(new Intent(SignInActivity.this,HomeActivity.class));
////
////                                                }// if he has not created portfolio page, then show dialog to chose which page to create
////                                                else{
////                                                    progressDialog.cancel();
////                                                    choicePageDialog.show();
////                                                }
////                                            }//show dialog
////                                           else{
////                                                progressDialog.cancel();
////                                                choicePageDialog.show();
////                                            }
////
////                                    }
////                                }//if he has not created his main profile, then show screen for creating main profile
////                                    else{
////                                        SignInActivity.this.finish();
////                                        startActivity(new Intent(SignInActivity.this,OwnerEditMainProfileActivity.class));
////
////                                    }
////                                }
////                            });
//////
//////                            SignInActivity.this.finish();
//////                            startActivity(new Intent(SignInActivity.this,HomeActivity.class));
//
//                                    checkAndValidateUser();
//
//                                } else {
////                                    Toast.makeText(SignInActivity.this, "Account signIn failed! " + task.getException(), Toast.LENGTH_LONG).show();
//                                    warningDialog.show();
//                                }
//                            }
//                        });
//
//            }else{
//                Toast.makeText(SignInActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//
//            }
//        }else{
//            Toast.makeText(SignInActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//
//        }
//    }
//    //public void goToSignUpActivity(){
//    //   Intent signUpIntent = new Intent(this,SignUpActivity.class);
//    //   startActivity(signUpIntent);
//    //}
//
//
//    public void createProgressDialogue() {
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        progressDialog.setCancelable(false);
//        progressDialog.setCanceledOnTouchOutside(false);
////
////        progressDialog.setContentView(R.layout.account_sign_in_progress_dialog_layout);
////        dialogMainConstraintLayout = progressDialog.findViewById(R.id.dialogMainConstraintLayoutId);
////        dialogTitleTextView = progressDialog.findViewById(R.id.dialogTitleTextViewId);
////        dialogMessageTextView = progressDialog.findViewById(R.id.dialogMessageTextViewId);
//
//     /*
//        dialogCancelButton = requestDialog.findViewById(R.id.dialogCancelButtonId);
//        dialogTryAgainButton = requestDialog.findViewById(R.id.dialogTryAgainButtonId);
//
//        dialogCancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                requestDialog.cancel();
//            }
//        });
//
//        dialogTryAgainButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                email= String.valueOf(signUpEmailEditText.getText());
//                password= String.valueOf(signUpPasswordEditText.getText());
//
//                signUpNewUsers(email,password);
//            }
//        });
//*/
//
//    }
//    public void createWarningDialog(){
//        AlertDialog.Builder builder =new AlertDialog.Builder(this);
//
//        builder.setTitle("Error");
//        builder.setMessage("Account sign in failed!");
//        builder.setCancelable(false);
//        builder.setIcon(R.drawable.ic_baseline_error_high_24);
//        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                email= String.valueOf(emailTextInputEditText.getText());
//                password= String.valueOf(passwordTextInputEditText.getText());
//
//                signInUser(email,password);
//            }
//        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                warningDialog.cancel();
//            }
//        });
//        warningDialog=builder.create();
//
//
//    }
//
//    public void createChoicePageDialog(){
//        AlertDialog.Builder builder =new AlertDialog.Builder(this);
//
//        builder.setTitle("Successfully signed in");
//        builder.setMessage("Do you mind creating page for your self or your business?");    builder.setCancelable(false);
//        builder.setIcon(R.drawable.ic_baseline_person_24);
//        builder.setPositiveButton("Yes create", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
////                SignInActivity.this.finish();
////                startActivity(new Intent(SignInActivity.this,CreateNewPageActivity.class));
//
//            }
//        }).setNegativeButton("Remind me later", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                Intent intent =  new Intent(SignInActivity.this,MainActivity.class);
//                intent.putExtra("OPEN_PAGE","HOME");
//                startActivity(intent);
//                SignInActivity.this.finish();
//
//            }
//        });
//        choicePageDialog=builder.create();
//
//    }
//
//    //should be called only when a user signs in successfully else crashes the app;
//    private void checkAndValidateUser(){
//        DocumentReference userAccountDocumentReference = FirebaseFirestore.getInstance().collection(GlobalValue.ALL_PAGES).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection(GlobalValue.PAGE_PROFILE).document(FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        GlobalValue.checkIfDocumentExists(userAccountDocumentReference, new GlobalValue.OnDocumentExistStatusCallback() {
//            @Override
//            public void onExist() {
//                Intent intent = new Intent(getApplication(),MainActivity.class);
//                startActivity(intent);
//                SignInActivity.this.finish();
//                progressDialog.cancel();
//
//
//            }
//
//            @Override
//            public void onNotExist() {
////                Intent intent = new Intent(getApplicationContext(),EditPageProfileActivity.class);
////                intent.putExtra("IS_FROM_SIGN_IN_ACTIVITY",true);
////                intent.putExtra("IS_CREATE_NEW_ACCOUNT",true);
////                startActivity(intent);
////                SignInActivity.this.finish();
////                progressDialog.cancel();
//
//            }
//
//            @Override
//            public void onFailed(@NonNull String errorMessage) {
//                progressDialog.cancel();
//                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(intent);
//                SignInActivity.this.finish();
////                SignInActivity.this.finishAndRemoveTask();
//                Toast.makeText(getApplicationContext(), "failed", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//
//    }
//
//    public void checkUserDetails(){
//        FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
//        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        firebaseFirestore.collection(GlobalValue.ALL_PAGES).document(userId).get().addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//
//                Intent intent =  new Intent(SignInActivity.this,MainActivity.class);
//                intent.putExtra("OPEN_PAGE","HOME");
//                startActivity(intent);
//                SignInActivity.this.finish();
//
//            }
//        }).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                /*Check if the user has created his main profile*/
//                if(documentSnapshot.getBoolean("MAIN_PROFILE_IS_CREATED")!=null){
//                    /*If he has created his main profile, then check if he has created his business profile*/
//                    if (documentSnapshot.getBoolean("MAIN_PROFILE_IS_CREATED")) {
//                        /*Check if he has created his business profile*/
//                        if (documentSnapshot.getBoolean("BUSINESS_PROFILE_IS_CREATED")!=null) {
//                            /*If he has created his business profile, then check if he has created his portfolio profile*/
//                            if (documentSnapshot.getBoolean("BUSINESS_PROFILE_IS_CREATED")) {
//
//                                SignInActivity.this.finish();
//                                Intent intent =  new Intent(SignInActivity.this,MainActivity.class);
//                                intent.putExtra("OPEN_PAGE","HOME");
//                                startActivity(intent);
//
//                            }
//                            /*check if he has created his portfolio profile*/
//                            else if( documentSnapshot.getBoolean("PORTFOLIO_PROFILE_IS_CREATED")!=null){
//                                /*If he has created his portfolio profile, then move home*/
//                                if( documentSnapshot.getBoolean("PORTFOLIO_PROFILE_IS_CREATED")){
//                                    SignInActivity.this.finish();
//                                    Intent intent =  new Intent(SignInActivity.this,MainActivity.class);
//                                    intent.putExtra("OPEN_PAGE","HOME");
//                                    startActivity(intent);
//
//                                }
//                                // if he has not created portfolio page, then show dialog to chose which page to create
//                                else{
//                                    progressDialog.cancel();
//                                    choicePageDialog.show();
//                                }
//                            }
//                        }else if( documentSnapshot.getBoolean("PORTFOLIO_PROFILE_IS_CREATED")!=null){
//                            if( documentSnapshot.getBoolean("PORTFOLIO_PROFILE_IS_CREATED")){
//                                SignInActivity.this.finish();
//                                Intent intent =  new Intent(SignInActivity.this,MainActivity.class);
//                                intent.putExtra("OPEN_PAGE","HOME");
//                                startActivity(intent);
//
//                            }// if he has not created portfolio page, then show dialog to chose which page to create
//                            else{
//                                progressDialog.cancel();
//                                choicePageDialog.show();
//                            }
//                        }//show dialog
//                        else{
//                            progressDialog.cancel();
//                            choicePageDialog.show();
//                        }
//
//                    }
//                }//if he has not created his main profile, then show screen for creating main profile
//                else{
////                    SignInActivity.this.finish();
////                    startActivity(new Intent(SignInActivity.this,EditUserMainProfileActivity.class));
//
//                }
//            }
//        });
//
//    }
//


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.palria.kujeapp.SignUpActivity;

import org.w3c.dom.Text;

public class SignInActivity extends AppCompatActivity {
    private String email;
    private String password;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button signInActionButton;
    //register and forget password link
    private TextView register_link_view;
    private TextView forget_password_link;
    private TextView errorMessageTextView;

    AlertDialog alertDialog;

    /**
     * This is a flag indicating when the sign in process finishes
     * */
    boolean isInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_sign_in);
        //initializes this activity's views
        initUI();

        signInActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInProgress) {
                    isInProgress = true;
                    //show logging in progress
                    toggleProgress(true);
                    email = emailEditText.getText().toString();
                    password = passwordEditText.getText().toString();
//                    Toast.makeText(getApplicationContext(), "Sign in progress...!", Toast.LENGTH_SHORT).show();
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    errorMessageTextView.setText("Progress...");
                    GlobalValue.signInUserWithEmailAndPassword(SignInActivity.this, email, password, new GlobalValue.SignInListener() {
                        @Override
                        public void onSuccess(String email, String password) {
                            //user has successfully signed in

                            isInProgress = false;
                            //hide progress
                            toggleProgress(false);
                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                            Toast.makeText(getApplicationContext(), "You successfully signed in, go learn more, it is era of learning", Toast.LENGTH_LONG).show();
                            SignInActivity.this.finish();
                        }

                        @Override
                        public void onFailed(String errorMessage) {
                            // account sign in failed//
//                        Toast.makeText(getApplicationContext(), "Sign in failed: "+errorMessage+" please try again!", Toast.LENGTH_SHORT).show();
                            isInProgress = false;
                            //hide progress
                            toggleProgress(false);
                            errorMessageTextView.setText(errorMessage + "  Please try again!");
                            errorMessageTextView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onEmptyInput(boolean isEmailEmpty, boolean isPasswordEmpty) {
                            // Either email or password is empty
//                        Toast.makeText(getApplicationContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                            isInProgress = false;
                            //hide progress
                            toggleProgress(false);
                            errorMessageTextView.setText("All fields are required, fill the form and try again!");
                            errorMessageTextView.setVisibility(View.VISIBLE);
                        }
                    });
                }else{
//                    sign in is in progress
                    toggleProgress(true);
                }
            }
        });

        //register link button click listener
        register_link_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //register|sign up activity starts from here .
                Intent i = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

        forget_password_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //forget password activity intent starts from here .
                Intent i = new Intent(SignInActivity.this, ChangePasswordActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    public void onBackPressed() {

        if(!isInProgress){
            SignInActivity.super.onBackPressed();
        }else{
            //process running...


        }

    }

    /**
     * Initializes the views
     * Has to be called first before any other method
     * */
    private void initUI(){


        emailEditText = (EditText) findViewById(R.id.emailInput);
        passwordEditText = (EditText) findViewById(R.id.passwordInput);
        errorMessageTextView = (TextView) findViewById(R.id.errorMessage);

        signInActionButton = (Button) findViewById(R.id.loginButton);

        register_link_view = (TextView) findViewById(R.id.register_link);

        forget_password_link = (TextView) findViewById(R.id.forget_password_link);

        //init progress.
        alertDialog = new AlertDialog.Builder(SignInActivity.this)
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

}