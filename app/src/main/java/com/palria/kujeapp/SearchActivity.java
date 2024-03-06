package com.palria.kujeapp;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class SearchActivity extends AppCompatActivity {
    TabLayout tabLayout ;
    FrameLayout allViewerFrameLayout;
    boolean isAllProductsSearchOpen = false;
    boolean isAllUsersSearchOpen = false;
    boolean isAllServicesSearchOpen = false;
    boolean isAllUpdatesSearchOpen = false;

    SearchView searchView;
    ImageButton searchActionButton;
    static String searchKeyword = "";

    MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
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
                    openFragment(new AllProductsFragment(),allViewerFrameLayout);

                }
                if(isAllUsersSearchOpen){
                    openFragment(new AllCustomersFragment(),allViewerFrameLayout);

                }
                if(isAllServicesSearchOpen){
                    openFragment(new AllServicesFragment(),allViewerFrameLayout);

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
                        openFragment(new AllProductsFragment(),allViewerFrameLayout);
//                        setLayoutVisibility();
                        setAllProductsSearchOpen();
                        break;

                    case 1:
                        openFragment(new AllCustomersFragment(),allViewerFrameLayout);
                        setAllUsersSearchOpen();
                        break;

                    case 2:
                        openFragment(new AllServicesFragment(),allViewerFrameLayout);
                        setAllServicesSearchOpen();
                        break;


                    case 3:
                        openFragment(new AllUpdatesFragment(),allViewerFrameLayout);
                        setAllUpdatesSearchOpen();
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
        TabLayout.Tab productTabItem = tabLayout.newTab();
        productTabItem.setText("Products");
        tabLayout.addTab(productTabItem,0,true);


        TabLayout.Tab customersTabItem=tabLayout.newTab();
        customersTabItem.setText("People");
        tabLayout.addTab(customersTabItem,1);


        TabLayout.Tab servicesTabItem=tabLayout.newTab();
        servicesTabItem.setText("Jobs");
        tabLayout.addTab(servicesTabItem,2);

        TabLayout.Tab updatesTabItem=tabLayout.newTab();
        updatesTabItem.setText("Updates");
        tabLayout.addTab(updatesTabItem,3);

        TabLayout.Tab newsTabItem=tabLayout.newTab();
        newsTabItem.setText("News");
        tabLayout.addTab(newsTabItem,4);


    }

    public void openFragment(Fragment fragment, FrameLayout frameLayoutToBeReplaced){
//        String searchKeyword = searchFieldEditText.getText().toString();
        searchKeyword = searchKeyword.toLowerCase();
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValue.SEARCH_KEYWORD, searchKeyword);
        bundle.putBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT,true);
//        bundle.putString(GlobalValue.FRAGMENT_OPENING_PURPOSE,GlobalValue.FRAGMENT_OPENING_PURPOSE_DEFAULT);

        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(frameLayoutToBeReplaced.getId(),fragment).commit();
    }


    void setAllProductsSearchOpen(){
        isAllProductsSearchOpen = true;
        isAllUsersSearchOpen = false;
        isAllUpdatesSearchOpen = false;
        isAllServicesSearchOpen = false;


    }
    void setAllUsersSearchOpen(){
        isAllProductsSearchOpen = false;
        isAllUsersSearchOpen = true;
        isAllUpdatesSearchOpen = false;
        isAllServicesSearchOpen = false;

    }
    void setAllServicesSearchOpen(){
        isAllProductsSearchOpen = false;
        isAllUsersSearchOpen = false;
        isAllUpdatesSearchOpen = false;
        isAllServicesSearchOpen = true;


    }
    void setAllUpdatesSearchOpen(){
        isAllProductsSearchOpen = false;
        isAllUsersSearchOpen = false;
        isAllServicesSearchOpen = false;
        isAllUpdatesSearchOpen = true;


    }

    interface  OnSearchTriggered{
        void  onSearch(String whichData);
    }
}