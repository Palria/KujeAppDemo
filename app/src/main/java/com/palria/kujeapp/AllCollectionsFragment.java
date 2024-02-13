








package com.palria.kujeapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.palria.kujeapp.CreateNewCollection;
import com.palria.kujeapp.GlobalValue;
import com.palria.kujeapp.PostNewProductActivity;
import com.palria.kujeapp.SingleProductCollectionActivity;


public class AllCollectionsFragment extends Fragment {


    public AllCollectionsFragment() {
        // Required empty public constructor
    }


    LinearLayout containerLinearLayout;
    MaterialButton createNewCollectionActionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.fragment_all_collections, container, false);
        initUI(parentView);

        createNewCollectionActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateNewCollection.class);
                startActivity(intent);
            }
        });

        return parentView;
    }



    private void initUI(View parentView){
        containerLinearLayout = parentView.findViewById(R.id.containerLinearLayoutId);
        createNewCollectionActionButton = parentView.findViewById(R.id.createNewCollectionActionButtonId);
    }

    private void displayCollectionItem(String collectionName, String totalNumberOfProducts){
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.collection_item_layout_holder,containerLinearLayout,false);
        FloatingActionButton addNewProductAction = itemView.findViewById(R.id.addNewProductActionButtonId);

        addNewProductAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PostNewProductActivity.class);
                intent.putExtra(GlobalValue.PRODUCT_COLLECTION_NAME,collectionName);
                startActivity(intent);
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SingleProductCollectionActivity.class);
                intent.putExtra(GlobalValue.PRODUCT_COLLECTION_NAME,collectionName);
                startActivity(intent);
            }
        });
        containerLinearLayout.addView(itemView);

    }

}