package com.palria.kujeapp;

//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.firebase.auth.FirebaseAuth;
//
//public class ChangePasswordActivity extends AppCompatActivity {
//    Button sendEmailResetLinkActionButton;
//    TextInputEditText emailFieldEditText ;
//    String email;
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_change_password);
//
//
//        emailFieldEditText = findViewById(R.id.emailFieldEditTextId);
//        sendEmailResetLinkActionButton = findViewById(R.id.sendEmailResetLinkActionButtonId);
//        sendEmailResetLinkActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                email = String.valueOf(emailFieldEditText.getText());
//                if(!(emailFieldEditText.getText() == null || email.equals(""))) {
//
//                    resetPassword();
//                }else{
//                    Toast.makeText(ChangePasswordActivity.this, "Please enter your email to continue", Toast.LENGTH_SHORT).show();
//
//                }
//            }
//        });
//    }
//
//    private void resetPassword() {
//        if (!(emailFieldEditText.getText() == null || email.isEmpty())) {
//            String trimmedEmail = email.trim();
//            sendEmailResetLinkActionButton.setEnabled(false);
//            sendEmailResetLinkActionButton.setText("Sending email wait...");
//            FirebaseAuth.getInstance().sendPasswordResetEmail(trimmedEmail)
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(ChangePasswordActivity.this, "Email failed to send please kindly try again", Toast.LENGTH_SHORT).show();
//                            sendEmailResetLinkActionButton.setEnabled(true);
//                            sendEmailResetLinkActionButton.setText("Try again");
//
//                        }
//                    })
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void unused) {
//                            sendEmailResetLinkActionButton.setEnabled(true);
//                            sendEmailResetLinkActionButton.setText("Successfully sent");
//
//                            Toast.makeText(ChangePasswordActivity.this, "Email successfully sent", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//
//        } else {
//            Toast.makeText(ChangePasswordActivity.this, "Please enter your email to continue", Toast.LENGTH_SHORT).show();
//
//        }
//    }
//}
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.SignInActivity;

public class ChangePasswordActivity extends AppCompatActivity {
    String email;
    EditText emailEditText;
    Button sendEmailActionButton;
    AlertDialog alertDialog;
    TextView mLoginLink;
    private TextView errorMessageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_change_password);
        initUI();

        sendEmailActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailEditText.getText().toString();
                //check email not empty and valid?
                if(email==null || email.trim().equals("")){


                    errorMessageTextView.setText("Enter a valid email!");
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    return;
                }
                toggleProgress(true);
                sendEmailResetLink(new SendLinkListener() {
                    @Override
                    public void onSuccess() {
                        /*
                         *Email reset link sent to user's provided email
                         *address, inform him to open his inbox and click on the link
                         * to proceed resetting his password
                         */
                        toggleProgress(false);
                        //show success

                        GlobalValue.showAlertMessage("success",
                                ChangePasswordActivity.this,
                                "Password Reset Link Sent",
                                "we have successfully sent your password reset link. check your email now and click the link");



                    }

                    @Override
                    public void onFailed(String errorMessage) {
                        toggleProgress(false);
                        //Take an action when the password reset process fails
                        errorMessageTextView.setText(errorMessage + "  Please try again!");
                        errorMessageTextView.setVisibility(View.VISIBLE);
                    }


                });
            }
        });

        //login link click listener
        mLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ChangePasswordActivity.this, SignInActivity.class);
                startActivity(i);
            }
        });
    }
//

    /**
     * Initializes the Activity's widgets
     * */
    private void initUI(){

        emailEditText= findViewById(R.id.emailInput);
        sendEmailActionButton = findViewById(R.id.resetButton);
        mLoginLink = findViewById(R.id.login_link);
        errorMessageTextView = findViewById(R.id.errorMessage);

        alertDialog = new AlertDialog.Builder(ChangePasswordActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();

    }


    private void toggleProgress(boolean show)
    {
        if(show){
            alertDialog.show();
        }else{
            alertDialog.hide();
        }
    }


    private void sendEmailResetLink(SendLinkListener sendLinkListener) {
        if (email != null && !email.isEmpty()) {
            String trimmedEmail = email.trim();
            FirebaseAuth.getInstance().sendPasswordResetEmail(trimmedEmail)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            sendLinkListener.onFailed(e.getMessage());
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            sendLinkListener.onSuccess();
                        }
                    });
        }else{
            //Tell the user to input his email address
        }
    }

    /**
     * A callback triggered either if the link is successfully set or failed to send
     * */
    interface SendLinkListener{
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



}