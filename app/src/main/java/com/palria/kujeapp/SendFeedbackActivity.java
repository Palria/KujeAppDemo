package com.palria.kujeapp;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class SendFeedbackActivity extends AppCompatActivity {
    TextInputEditText feedbackTextEditText;
    TextInputEditText contactEmailEditText;
    String feedbackText;
    String contactEmail;
    Button sendFeedbackActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feedback);
        feedbackTextEditText = findViewById(R.id.feedbackTextEditTextId);
        contactEmailEditText = findViewById(R.id.contactEmailEditTextId);
        sendFeedbackActionButton = findViewById(R.id.sendFeedbackActionButtonId);
        sendFeedbackActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEmailAppForFeedback();
            }
        });
    }


    private void openEmailAppForFeedback(){
        feedbackText = ""+feedbackTextEditText.getText();
        contactEmail = ""+contactEmailEditText.getText();
        String[] feedBackEmail  =  new String[]{
                "anchorallianceglobal@gmail.com"
        };
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, feedBackEmail);
        intent.putExtra(Intent.EXTRA_SUBJECT,"My feedback");
        intent.putExtra(Intent.EXTRA_TEXT,feedbackText+"\r\n\r\n"+"\r\n My contact email : "+contactEmail);
        intent.setDataAndType(Uri.parse("email"),"message/rfc822");
        startActivity(Intent.createChooser(intent,"Send feedback"));

    }
}