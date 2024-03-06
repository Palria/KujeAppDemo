


package com.palria.kujeapp;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Html;
//import android.text.method.LinkMovementMethod;
//import android.view.View;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.CheckBox;
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
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//public class SignUpActivity extends AppCompatActivity {
//    Button signUpButton,goToSignInActivityButton,goToChangePasswordActivityButton;
//    private FirebaseAuth fireAuth;
//    TextInputEditText signUpEmailEditText, signUpPasswordEditText,signUpDisplayNameEditText,signUpPhoneNumberEditText;
//    private String email,password;
//    private FirebaseFirestore firebaseFirestore;
//
//    ProgressDialog progressDialog;
//    AlertDialog warningDialog;
//    ConstraintLayout dialogMainConstraintLayout;
//    ProgressBar dialogProgressBar;
//    TextView dialogTitleTextView,dialogMessageTextView;
//    Button dialogCancelButton,  dialogTryAgainButton;
////    CheckBox agreementCheckBox;
////    TextView termsTextView;
////    TextView privacyPolicyTextView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_sign_up);
//        // FirebaseApp firebaseApp = FirebaseApp.getInstance("com.govanceinc.obidientera");
//        fireAuth=FirebaseAuth.getInstance();
//        //firebaseFirestore=FirebaseFirestore.getInstance();
//
//        signUpEmailEditText=(TextInputEditText)  findViewById(R.id.signUpEmailEditText);
//        signUpPasswordEditText=(TextInputEditText)  findViewById(R.id.signUpPasswordEditText);
//        //signUpDisplayNameEditText=(TextInputEditText) findViewById(R.id.signUpDisplayNameEditTextId);
//        //signUpPhoneNumberEditText=(TextInputEditText) findViewById(R.id.signUpPhoneNumberEditTextId);
//
//
////        agreementCheckBox = findViewById(R.id.agreementCheckBoxId);
//
//
////        termsTextView = findViewById(R.id.termsTextViewId);
////        termsTextView.setMovementMethod(LinkMovementMethod.getInstance() );
////        termsTextView.setText(Html.fromHtml(getString(R.string.terms),Html.FROM_HTML_MODE_LEGACY));
////        privacyPolicyTextView = findViewById(R.id.privacyPolicyTextViewId);
////        privacyPolicyTextView.setMovementMethod(LinkMovementMethod.getInstance() );
////        privacyPolicyTextView.setText(Html.fromHtml(getString(R.string.privacy_policy),Html.FROM_HTML_MODE_LEGACY));
//
//        signUpButton=(Button) findViewById(R.id.signUpButton);
//        signUpButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                email= String.valueOf(signUpEmailEditText.getText()).trim();
//                password= String.valueOf(signUpPasswordEditText.getText()).trim();
//
//
//                signUpNewUsers(email.trim(), password.trim());
//
//
//            }
//        });
//
//        goToSignInActivityButton=(Button) findViewById(R.id.goToSignInActivityButton);
//        goToChangePasswordActivityButton=(Button) findViewById(R.id.goToChangePasswordActivityButtonId);
//        goToSignInActivityButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SignUpActivity.this.finish();
//                Intent intent=new Intent(SignUpActivity.this, SignInActivity.class);
//                startActivity(intent);
//
//            }
//        });
//        goToChangePasswordActivityButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(SignUpActivity.this, ChangePasswordActivity.class);
//                startActivity(intent);
//
//            }
//        });
//
//
//        createProgressDialogue();
//        createWarningDialog();
//
//    }
//    public void signUpNewUsers(String email, String password) {
////        if (agreementCheckBox.isChecked()) {
//            if (!(signUpEmailEditText.getText() == null && signUpPasswordEditText.getText() == null)) {
//                if (!(email.equals("") && password.equals(""))) {
//
//                    progressDialog.show();
//
//                    fireAuth.createUserWithEmailAndPassword(email, password)
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    progressDialog.cancel();
//                                    warningDialog.setMessage(e.getMessage());
//                                    warningDialog.show();
////                                    Toast.makeText(SignUpActivity.this, "Account creation failed: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//
//                                }
//                            })
//                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        FirebaseUser fireUser = fireAuth.getCurrentUser();
//                                        //String userId=fireUser.getUid();
//                                        //firebaseFirestore.collection("allusers").document();
//                                        Toast.makeText(SignUpActivity.this, "Account successfully created! " + fireUser.getEmail(), Toast.LENGTH_SHORT).show();
//                                        progressDialog.cancel();
//
//                                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
//                                        SignUpActivity.this.finish();
//                                    } else {
////                                        Toast.makeText(SignUpActivity.this, "Account creation failed!", Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            });
//                } else {
//                    Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//
//                }
//            } else {
//                Toast.makeText(SignUpActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
//
//            }
//
////        }else{
////            Toast.makeText(this, "Check the box and agree to continue", Toast.LENGTH_LONG).show();
////        }
//    }
//
//
//    public void createProgressDialogue() {
//
//        progressDialog = new ProgressDialog(this);
//        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        progressDialog.setCancelable(false);
//        progressDialog.setCanceledOnTouchOutside(false);
////
////        progressDialog.setContentView(R.layout.account_creation_progress_dialog_layout);
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
//        builder.setMessage("Account creation failed!");
//        builder.setCancelable(false);
//        builder.setIcon( R.drawable.ic_baseline_error_high_24);
//        builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                email= String.valueOf(signUpEmailEditText.getText());
////                password= String.valueOf(signUpPasswordEditText.getText());
//
//                signUpNewUsers(email,password);
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


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {

    private String userDisplayName;
    private String userCountryOfResidence;
    private String email;
    private String password;
    private String confirmPassword;
    private String genderType;
    private EditText userDisplayNameEditText;
    private EditText userCountryOfResidenceEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText genderTypeEditText;
    private Button signUpActionButton;
    private Spinner genderTypeSpinner;
    private Spinner countrySpinner;
    private EditText passwordConfirmEditText;
    AlertDialog alertDialog;
    //register and forget password link
    private TextView login_link_view;
    private TextView forget_password_link;
    private TextView errorMessageTextView;

    TextInputLayout passwordInputLayout;
    /**
     * This is a flag indicating when the sign up process finishes
     * */
    boolean isInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar()!=null)getSupportActionBar().hide();
        setContentView(R.layout.activity_sign_up);
        initUI();



        signUpActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userDisplayName = userDisplayNameEditText.getText().toString();
                //userCountryOfResidence = userCountryOfResidenceEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                confirmPassword = passwordConfirmEditText.getText().toString();

                Log.w("message", userDisplayName + " : " + email + " : " + genderType + " : " + userCountryOfResidence);

                //validate confirm password
                if(!password.equals(confirmPassword)){
                    Toast.makeText(SignUpActivity.this, password+"="+confirmPassword, Toast.LENGTH_SHORT).show();
                    errorMessageTextView.setText("Confirm password does not match the password!");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    return;
                }
                if(!isInProgress) {
                    isInProgress = true;
                    toggleProgress(true);
                    errorMessageTextView.setText("Progress...");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    if (!userDisplayName.isEmpty()) {
                        GlobalValue.signUpUserWithEmailAndPassword(SignUpActivity.this, email, password, new GlobalValue.SignUpListener() {
                            @Override
                            public void onSuccess(String email, String password) {
                                //user has successfully signed up
//                                toggleProgress(false);
//
//                                GlobalHelpers.showAlertMessage("success",
//                                        SignUpActivity.this,
//                                        "Account Created Successfully.",
//                                        "we have successfully sent a confirmation email. please check your email now and verify before login.");
//
//                                emailEditText.setText("");
//                                passwordEditText.setText("");
//                                passwordConfirmEditText.setText("");
//                                userDisplayNameEditText.setText("");
//                                isInProgress=false;
//                                errorMessageTextView.setText("Success");

                                //--do not login user directly we need to confirm email before login--//.
//                                Toast.makeText(SignUpActivity.this, "up sign success", Toast.LENGTH_SHORT).show();

                                GlobalValue.signInUserWithEmailAndPassword(SignUpActivity.this, email, password, new GlobalValue.SignInListener() {
                                    @Override
                                    public void onSuccess(String email, String password) {
                                        String uid = FirebaseAuth.getInstance().getCurrentUser()!=null?FirebaseAuth.getInstance().getCurrentUser().getUid():"0";
                                        //user has signed in so can now write to the database, now create his first profile
//                                        Toast.makeText(SignUpActivity.this, "sign in success", Toast.LENGTH_SHORT).show();
                                        createUserProfileInDatabase(new ProfileCreationListener() {
                                            @Override
                                            public void onSuccess(String userName) {
//                                                Toast.makeText(SignUpActivity.this, "profile success", Toast.LENGTH_SHORT).show();

                                                isInProgress = false;
                                                toggleProgress(false);
                                                FirebaseAuth.getInstance().signOut();
                                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                startActivity(intent);
                                                SignUpActivity.this.finish();
                                            }

                                            @Override
                                            public void onFailed(String errorMessage) {
                                                isInProgress = false;
                                                toggleProgress(false);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailed(String errorMessage) {
                                        isInProgress = false;
                                        toggleProgress(false);
                                        errorMessageTextView.setText(errorMessage + "  Please try again!");
                                        errorMessageTextView.setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onEmptyInput(boolean isEmailEmpty, boolean isPasswordEmpty) {
                                        isInProgress = false;
                                        toggleProgress(false);

                                    }
                                });



                            }

                            @Override
                            public void onFailed(String errorMessage) {
                                // account sign in failed
                                isInProgress = false;
                                toggleProgress(false);
                                errorMessageTextView.setText(errorMessage + "  Please try again!");
                                errorMessageTextView.setVisibility(View.VISIBLE);

                            }

                            @Override
                            public void onEmptyInput(boolean isEmailEmpty, boolean isPasswordEmpty) {
                                // Either email or password is empty
                                isInProgress = false;
                                toggleProgress(false);
                                errorMessageTextView.setText("All fields are required, fill the form and try again!");
                                errorMessageTextView.setVisibility(View.VISIBLE);

                            }
                        });
                    } else {
                        /*Take an action when a user has not filled in their user name and gender
                         *It is mandatory to fill in their user name and gender
                         */
                        isInProgress=false;
                        toggleProgress(false);
                        errorMessageTextView.setText("All fields are required, fill the form and try again!");
                        errorMessageTextView.setVisibility(View.VISIBLE);
                    }
                }
                else{
                    //Sign up in progress...
                }
            }
        });


        //register link button click listener
        login_link_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //register|sign up activity starts from here .
                Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
                startActivity(i);
            }
        });

        forget_password_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //forget password activity intent starts from here .
                Intent i = new Intent(SignUpActivity.this, ChangePasswordActivity.class);
                startActivity(i);
            }
        });
    }



    /**
     * Creates the user's profile in the database
     * @param profileCreationListener the callback triggered when the profile is created in the database or if the creation fails
     * */
    private void createUserProfileInDatabase(ProfileCreationListener profileCreationListener){
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();

        DocumentReference userProfileDocumentReference = FirebaseFirestore.getInstance().collection(GlobalValue.ALL_USERS).document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String,Object>userProfileDetails = new HashMap<>();
        userProfileDetails.put(GlobalValue.USER_DISPLAY_NAME,userDisplayName);
        userProfileDetails.put(GlobalValue.USER_COUNTRY_OF_RESIDENCE,userCountryOfResidence);
        userProfileDetails.put(GlobalValue.USER_GENDER_TYPE,genderType);
        userProfileDetails.put(GlobalValue.USER_EMAIL_ADDRESS,email);
        userProfileDetails.put(GlobalValue.IS_USER_BLOCKED,false);
        userProfileDetails.put(GlobalValue.USER_PROFILE_DATE_CREATED_TIME_STAMP, FieldValue.serverTimestamp());
        userProfileDetails.put(GlobalValue.USER_PROFILE_DATE_EDITED_TIME_STAMP, FieldValue.serverTimestamp());
        userProfileDetails.put(GlobalValue.USER_TOKEN_ID,"");
        userProfileDetails.put(GlobalValue.USER_SEARCH_VERBATIM_KEYWORD, GlobalValue.generateSearchVerbatimKeyWords(userDisplayName));
        userProfileDetails.put(GlobalValue.USER_SEARCH_ANY_MATCH_KEYWORD,GlobalValue.generateSearchAnyMatchKeyWords(userDisplayName));

        writeBatch.set(userProfileDocumentReference,userProfileDetails, SetOptions.merge());


        DocumentReference userDocumentReference = GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.PLATFORM_CONFIGURATION_FILE).document(GlobalValue.PLATFORM_CONFIGURATION_FILE);
        HashMap<String,Object>userDetails = new HashMap<>();
        userDetails.put(GlobalValue.LAST_DATE_USER_REGISTERED_TIME_STAMP,FieldValue.serverTimestamp());
        userDetails.put(GlobalValue.TOTAL_NUMBER_OF_USERS, FieldValue.increment(1L));
        userDetails.put(userCountryOfResidence, FieldValue.increment(1L));
        userDetails.put(GlobalValue.USER_EMAIL_ADDRESS_LIST, FieldValue.arrayUnion(email));

        switch(genderType){
            case GlobalValue.MALE:
            userDetails.put(GlobalValue.TOTAL_NUMBER_OF_MALE_USERS, FieldValue.increment(1L));
            break;
            case GlobalValue.FEMALE:
            userDetails.put(GlobalValue.TOTAL_NUMBER_OF_FEMALE_USERS, FieldValue.increment(1L));
            break;
            case GlobalValue.OTHER:
            userDetails.put(GlobalValue.TOTAL_NUMBER_OF_OTHER_USERS, FieldValue.increment(1L));
            break;
        }

        writeBatch.set(userDocumentReference,userDetails, SetOptions.merge());

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

    @Override
    public void onBackPressed() {

        if(!isInProgress){
            SignUpActivity.super.onBackPressed();
        }else{
            //process running...


        }

    }

    /**Initializes the activity's views*/
    private void initUI(){
        userDisplayNameEditText = findViewById(R.id.nameInput);
        emailEditText = (EditText) findViewById(R.id.emailInput);
        passwordEditText = (EditText) findViewById(R.id.passwordInput);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmInput);
        errorMessageTextView = (TextView) findViewById(R.id.errorMessage);
        signUpActionButton = (Button) findViewById(R.id.startButton);
        login_link_view = (TextView) findViewById(R.id.login_link);
        forget_password_link = (TextView) findViewById(R.id.forget_password_link);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);

        genderTypeSpinner = findViewById(R.id.genderSpinner);
        countrySpinner = findViewById(R.id.countrySpinner);
        //init progress.
        alertDialog = new AlertDialog.Builder(SignUpActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();
        initGenderSpinner();
        initCountrySpinner();
    }


    private void toggleProgress(boolean show) {
        if(show){
            alertDialog.show();
        }else{
            alertDialog.cancel();
        }
    }

    /**
     * Initializes the gender spinner for selection
     * */
    private void initGenderSpinner(){
        String[] genderArray = {getResources().getString(R.string.male),getResources().getString(R.string.female)};
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

    /**
     * A callback of the user's profile creation
     * <p>{@link ProfileCreationListener#onSuccess(String)} triggered when the process finishes successfully</p>
     * <p>{@link ProfileCreationListener#onFailed(String)} triggered when the process fails </p>
     * */
    interface ProfileCreationListener{

        /**
         * triggered when the process finishes successfully
         * @param userName the user's name
         * */
        void onSuccess(String userName);

        /**
         * triggered when the process fails
         * @param errorMessage the error message stating the cause of the failure
         * */
        void onFailed(String errorMessage);
    }
}
