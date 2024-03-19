package com.palria.kujeapp;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class ApproveAdvertsActivity extends AppCompatActivity {
    TabLayout tabLayout ;
    FrameLayout allViewerFrameLayout;
    boolean isAllCustomAdsSearchOpen = false;
    boolean isAllProductsSearchOpen = false;
    boolean isAllBusinessPagesSearchOpen = false;
    boolean isAllPeopleSearchOpen = false;

    SearchView searchView;
    ImageButton searchActionButton;
    static String searchKeyword = "";

    MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_adverts);
        tabLayout = findViewById(R.id.tabLayoutId);

        searchView = findViewById(R.id.searchViewId);
        toolbar=findViewById(R.id.topBar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        EditText txtSearch = ((EditText) searchView.findViewById(androidx.appcompat.R.id.search_src_text));
        txtSearch.setHint("Search");
        txtSearch.setHintTextColor(Color.LTGRAY);
        txtSearch.setTextColor(Color.WHITE);

        searchView.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(1,0);

        //        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchKeyword = query;
                if(isAllProductsSearchOpen){
                    openFragment(new AllProductsFragment(),allViewerFrameLayout,true);

                }
                if(isAllCustomAdsSearchOpen){
                    openFragment(new AllCustomersFragment(),allViewerFrameLayout,true);

                }
                if(isAllProductsSearchOpen){
                    openFragment(new AllProductsFragment(),allViewerFrameLayout,true);

                }
                if(isAllBusinessPagesSearchOpen){
                    openFragment(new AllServicesFragment(),allViewerFrameLayout,true);

                }
                if(isAllPeopleSearchOpen){
                    openFragment(new AllCustomersFragment(),allViewerFrameLayout,true);

                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                searchKeyword = newText;

                return true;
            }
        });

//        searchActionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(isAllLibrariesSearchOpen){
//                    openFragment(new AllLibraryFragment(),allViewerFrameLayout);
//
//                }
//                if(isAllTutorialsSearchOpen){
//                    openFragment(new AllTutorialFragment(),allViewerFrameLayout);
//
//                }
//                if(isAllAuthorsSearchOpen){
//                    openFragment(new AllLibraryFragment(),allViewerFrameLayout);
//
//                }
//            }
//        });


        allViewerFrameLayout = findViewById(R.id.allViewerFrameLayoutId);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){

                    case 0:
                        openFragment(new AdvertsFragment(),allViewerFrameLayout,false);
//                        setLayoutVisibility();
                        setAllCustomAdsSearchOpen();
                        break;

                    case 1:
                        openFragment(new MarketFragment(),allViewerFrameLayout,false);
                        setAllProductsSearchOpen();
                        break;

                    case 2:
                        openFragment(new AllServicesFragment(),allViewerFrameLayout,false);
                        setAllBusinessPagesSearchOpen();
                        break;


                    case 3:
                        openFragment(new AllCustomersFragment(),allViewerFrameLayout,false);
                        setAllPeopleSearchOpen();
                        break;


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        manageTabLayout();
        searchView.requestFocus();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        searchKeyword = "";


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();  return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void manageTabLayout(){
        TabLayout.Tab customAdsTabItem = tabLayout.newTab();
        customAdsTabItem.setText("Custom Ads");
        tabLayout.addTab(customAdsTabItem,0,true);

        TabLayout.Tab productTabItem = tabLayout.newTab();
        productTabItem.setText("Products");
        tabLayout.addTab(productTabItem,1,true);


        TabLayout.Tab pagesTabItem=tabLayout.newTab();
        pagesTabItem.setText("Business pages");
        tabLayout.addTab(pagesTabItem,2);


        TabLayout.Tab peopleTabItem=tabLayout.newTab();
        peopleTabItem.setText("People");
        tabLayout.addTab(peopleTabItem,3);


    }

    public void openFragment(Fragment fragment, FrameLayout frameLayoutToBeReplaced, boolean isSearch){
//        String searchKeyword = searchFieldEditText.getText().toString();
        searchKeyword = searchKeyword.toLowerCase();
        Bundle bundle = new Bundle();
        if(isSearch){
            bundle.putString(GlobalValue.SEARCH_KEYWORD, searchKeyword);
            bundle.putBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,true);
        }
        bundle.putBoolean(GlobalValue.IS_FOR_APPROVAL,true);

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(frameLayoutToBeReplaced.getId(),fragment).commit();
    }


    void setAllCustomAdsSearchOpen(){
        isAllCustomAdsSearchOpen = true;
        isAllProductsSearchOpen = false;
        isAllBusinessPagesSearchOpen = false;
        isAllPeopleSearchOpen = false;


    }
    void setAllProductsSearchOpen(){
        isAllCustomAdsSearchOpen = false;
        isAllProductsSearchOpen = true;
        isAllBusinessPagesSearchOpen = false;
        isAllPeopleSearchOpen = false;


    }
    void setAllBusinessPagesSearchOpen(){
        isAllCustomAdsSearchOpen = false;
        isAllProductsSearchOpen = false;
        isAllBusinessPagesSearchOpen = true;
        isAllPeopleSearchOpen = false;

    }
    void setAllPeopleSearchOpen(){
        isAllCustomAdsSearchOpen = false;
        isAllProductsSearchOpen = false;
        isAllBusinessPagesSearchOpen = false;
        isAllPeopleSearchOpen = true;


    }


    interface  OnSearchTriggered{
        void  onSearch(String whichData);
    }
}