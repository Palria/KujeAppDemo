package com.palria.kujeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.palria.kujeapp.models.QuestionDataModel;
import com.palria.kujeapp.widgets.BottomSheetFormBuilderWidget;

public class SingleQuestionActivity extends AppCompatActivity {



    String authorId;
    String question;
    String questionId;
    String questionPhotoDownloadUrl = "";
    TextView questionBodyText;
    ImageView questionPhoto;
    TextView ansCountTextView;
    TextView viewCountTextView;
    TextView dateAskedTextView;
    TextView askerNameTextView;
    ImageView verificationFlagImageView;
    ImageButton backButton;
    ImageView askerPhoto;
    FloatingActionButton answerQuestionActionButton;
    AlertDialog alertDialog;
    QuestionDataModel questionDataModel;
    BottomSheetFormBuilderWidget bottomSheetCatalogFormBuilderWidget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_single_question);
        initUI();
        fetchIntentData();
        renderQuestion();
        initAnswerFragment();


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            SingleQuestionActivity.super.onBackPressed();

            }
        });
        answerQuestionActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AllAnswersFragment.addAnswerListener!=null) {
                    AllAnswersFragment.addAnswerListener.onAddAnswerTriggered(null, "",false,true);
                }
//                bottomSheetCatalogFormBuilderWidget.show();
            }
        });
    }
//

    /**
     * Initializes the Activity's widgets
     * */
    private void initUI(){
        backButton= findViewById(R.id.backButtonId);
        questionBodyText= findViewById(R.id.questionBodyTextViewId);
        answerQuestionActionButton = findViewById(R.id.addAnswerFloatingButtonId);
        questionPhoto = findViewById(R.id.questionPhotoImageViewId);
        ansCountTextView = findViewById(R.id.ansCountTextViewId);
        viewCountTextView = findViewById(R.id.viewCountTextViewId);
        dateAskedTextView = findViewById(R.id.dateAskedTextViewId);
        askerNameTextView = findViewById(R.id.posterNameTextViewId);
        verificationFlagImageView = findViewById(R.id.verificationFlagImageViewId);
        askerPhoto = findViewById(R.id.askerProfilePhotoId);
        alertDialog = new AlertDialog.Builder(SingleQuestionActivity.this)
                .setCancelable(false)
                .setView(getLayoutInflater().inflate(R.layout.default_loading_layout,null))
                .create();


        askerNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(GlobalValue.getHostActivityIntent(SingleQuestionActivity.this,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE,questionDataModel.getAuthorId()));

            }
        });
       askerPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(GlobalValue.getHostActivityIntent(SingleQuestionActivity.this,null,GlobalValue.USER_PROFILE_FRAGMENT_TYPE, questionDataModel.getAuthorId()));

            }
        });

    }

    private void fetchIntentData(){
        Intent intent = getIntent();
        questionDataModel = (QuestionDataModel) intent.getSerializableExtra(GlobalValue.QUESTION_DATA_MODEL);

    }


    private void toggleProgress(boolean show)
    {
        if(show){
            alertDialog.show();
        }else{
            alertDialog.hide();
        }
    }
    private void renderQuestion(){


    authorId = questionDataModel.getAuthorId();
    question = questionDataModel.getQuestionBody();
    questionId = questionDataModel.getQuestionId();
    questionPhotoDownloadUrl = questionDataModel.getPhotoDownloadUrl();
    questionBodyText.setText(question);

        Glide.with(this)
                .load(questionPhotoDownloadUrl)
                .centerCrop()
                .into(questionPhoto);
        ansCountTextView.setText(questionDataModel.getNumOfAnswers()+" ans");
        viewCountTextView.setText(questionDataModel.getNumOfViews()+"");
        dateAskedTextView.setText(questionDataModel.getDateAsked()+"");

        GlobalValue.getFirebaseFirestoreInstance().collection(GlobalValue.ALL_USERS)
                .document(questionDataModel.getAuthorId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final String userName = ""+ documentSnapshot.get(GlobalValue.USER_DISPLAY_NAME);
                        final String userProfilePhotoDownloadUrl = ""+ documentSnapshot.get(GlobalValue.USER_PROFILE_PHOTO_DOWNLOAD_URL);

                        askerNameTextView.setText(userName);
                        try {
                            Glide.with(SingleQuestionActivity.this)
                                    .load(userProfilePhotoDownloadUrl)
                                    .centerCrop()
                                    .placeholder(R.drawable.default_profile)
                                    .into(askerPhoto);
                        } catch (Exception ignored) {
                        }

                        boolean isVerified = documentSnapshot.get(GlobalValue.IS_ACCOUNT_VERIFIED) != null ? documentSnapshot.getBoolean(GlobalValue.IS_ACCOUNT_VERIFIED) : false;
                        if (isVerified) {
                           verificationFlagImageView.setVisibility(View.VISIBLE);
                        } else {
                           verificationFlagImageView.setVisibility(View.INVISIBLE);

                        }
                    }
                });


    }

    private void initAnswerFragment(){
        AllAnswersFragment allAnswersFragment = new AllAnswersFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putBoolean(GlobalValue.IS_FOR_PREVIEW,false);
        bundle.putBoolean(GlobalValue.IS_FROM_QUESTION_CONTEXT,true);
        bundle.putBoolean(GlobalValue.IS_VIEW_ALL_ANSWERS_FOR_SINGLE_QUESTION,true);
        bundle.putBoolean(GlobalValue.IS_VIEW_SINGLE_ANSWER_REPLY,false);
        bundle.putString(GlobalValue.AUTHOR_ID,authorId);
        bundle.putString(GlobalValue.ANSWER_ID,"");
        bundle.putString(GlobalValue.QUESTION_ID,questionId);
        allAnswersFragment.setArguments(bundle);
        try {
            fragmentManager.beginTransaction()
                    .replace(R.id.answersFrameLayoutId,allAnswersFragment )
                    .commit();
        }catch(Exception e){}

    }



    /**
     * A callback triggered either if the answer is successfully added or failed to add
     * */
    interface AnswerListener{
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
    interface AnswerPhotoUploadListener{
        /**
         * Triggered when the answer photo is uploaded
         * */
        void onSuccess(String answer,boolean isPhotoIncluded,boolean isEdition,String answerPhotoDownloadUrl);
        /**
         * Triggered when the photo fails to upload
         * */
        void onFailed(String errorMessage);
    }



}