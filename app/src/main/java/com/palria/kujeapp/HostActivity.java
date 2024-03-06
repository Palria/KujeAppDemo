package com.palria.kujeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.palria.kujeapp.ServiceRequestsFragment;
import com.palria.kujeapp.UserProfileFragment;

public class HostActivity extends AppCompatActivity {
    Intent intent;
    String FRAGMENT_TYPE = "";
    FrameLayout hostFrameLayout;
    String userId = "";
    String productId = "";
    String serviceId = "";

    MaterialToolbar materialToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        initUI();
        fetchIntentData();
        openIncomingFragment();
    }

    private void initUI(){
        materialToolbar=findViewById(R.id.topBar);
        hostFrameLayout = findViewById(R.id.hostFrameLayoutId);
        setSupportActionBar(materialToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }



    void fetchIntentData(){
        intent = getIntent();
        FRAGMENT_TYPE = intent.getStringExtra(GlobalValue.FRAGMENT_TYPE);
        userId = intent.getStringExtra(GlobalValue.USER_ID);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openIncomingFragment(){
        Bundle bundle = new Bundle();

        switch(FRAGMENT_TYPE){
            case GlobalValue.USER_PROFILE_FRAGMENT_TYPE:
                bundle = new Bundle();
                bundle.putString(GlobalValue.USER_ID,userId);
                materialToolbar.setTitle("Customer Profile");
                initFragment(bundle,new UserProfileFragment());
                break;
            case GlobalValue.ORDER_FRAGMENT_TYPE:
                bundle = new Bundle();
                bundle.putString(GlobalValue.USER_ID,userId);
                bundle.putBoolean(GlobalValue.IS_SINGLE_PRODUCT,false);
                bundle.putString(GlobalValue.PRODUCT_ID,productId);
                materialToolbar.setTitle("Orders");
                initFragment(bundle,new AllOrdersFragment());
                break;

            case GlobalValue.REQUEST_FRAGMENT_TYPE:
                bundle = new Bundle();
                bundle.putString(GlobalValue.USER_ID,userId);
                bundle.putBoolean(GlobalValue.IS_SINGLE_SERVICE,false);
                bundle.putString(GlobalValue.SERVICE_ID,serviceId);
                materialToolbar.setTitle("Requests");
                initFragment(bundle,new ServiceRequestsFragment());
                break;
            case GlobalValue.USERS_FRAGMENT_TYPE:
                bundle = new Bundle();
                bundle.putBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,false);
                materialToolbar.setTitle("Customers");
                initFragment(bundle,new AllCustomersFragment());
                break;
            case GlobalValue.USER_PRODUCT_FRAGMENT_TYPE:
                bundle = new Bundle();
                bundle.putBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,false);
                bundle.putString(GlobalValue.USER_ID,userId);
                bundle.putString(GlobalValue.PRODUCT_OWNER_USER_ID,userId);
                bundle.putBoolean(GlobalValue.IS_FROM_SINGLE_OWNER,true);
                bundle.putString(GlobalValue.CUSTOMER_ID,userId);
                materialToolbar.setTitle("Products");
                initFragment(bundle,new AllProductsFragment());
                break;
                case GlobalValue.APPROVE_ADVERTS_FRAGMENT_TYPE:
                bundle = new Bundle();
                bundle.putBoolean(GlobalValue.IS_FOR_APPROVAL,true);
                materialToolbar.setTitle("Approve Adverts");
                initFragment(bundle,new AdvertsFragment());
                break;
//            case GlobalValue.NOTES_FRAGMENT_TYPE:
//                bundle = new Bundle();
//                bundle.putString(GlobalValue.USER_ID,userId);
//                materialToolbar.setTitle("Notes");
//                initFragment(bundle,new AllNotesFragment());
//                break;

        }
    }

    void initFragment(Bundle bundle,Fragment fragment){
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(hostFrameLayout.getId(),fragment)
                .commit();
    }
}