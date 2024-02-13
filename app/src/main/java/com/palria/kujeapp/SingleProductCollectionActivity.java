package com.palria.kujeapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SingleProductCollectionActivity extends AppCompatActivity {
    String PRODUCT_COLLECTION_NAME;
TextView headerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_collection);
        initUI();
        manageIntentData();
        headerTextView.setText(PRODUCT_COLLECTION_NAME);

    }

private void initUI(){
    headerTextView = findViewById(R.id.headerTextViewId);
}
    private void manageIntentData(){
        Intent intent =  getIntent();
        PRODUCT_COLLECTION_NAME = intent.getStringExtra(GlobalValue.PRODUCT_COLLECTION_NAME);
    }
}