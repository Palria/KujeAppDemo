package com.palria.kujeapp;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;

public class NotificationActivity extends AppCompatActivity {
    ImageButton backButton;
    TabLayout tabLayout;
    FrameLayout personalisedViewerFrameLayout;
    FrameLayout platformNotesFrameLayout;


    //boolean fragment open stats
    boolean ispersonalisedViewerFrameLayoutOpened=false;
    boolean isplatformNotesFrameLayoutOpened=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initUI();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationActivity.super.onBackPressed();
            }
        });
        createTabLayout();

    }

    void initUI(){
        backButton = findViewById(R.id.backButton);
        tabLayout = findViewById(R.id.tabLayoutId);
        personalisedViewerFrameLayout = findViewById(R.id.personalisedViewerFrameLayoutId);
        platformNotesFrameLayout = findViewById(R.id.platformNotesFrameLayoutId);

    }

    public void createTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                String tabTitle=tab.getText().toString().trim().toUpperCase();
                if(tabTitle.equalsIgnoreCase("PERSONALIZED")) {
                    if(ispersonalisedViewerFrameLayoutOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(personalisedViewerFrameLayout);
                    }else {
                        ispersonalisedViewerFrameLayoutOpened =true;
                        setFrameLayoutVisibility(personalisedViewerFrameLayout);
                        AllPersonalizedNotificationsFragment allPersonalizedNotificationsFragment = new AllPersonalizedNotificationsFragment();
                        Bundle bundle = new Bundle();
                        allPersonalizedNotificationsFragment.setArguments(bundle);
                        initFragment(allPersonalizedNotificationsFragment, personalisedViewerFrameLayout);
                    }
                }
                else if(tabTitle.equalsIgnoreCase("PLATFORM")){
                    if(isplatformNotesFrameLayoutOpened){
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(platformNotesFrameLayout);
                    }else {
                        isplatformNotesFrameLayoutOpened =true;
                        setFrameLayoutVisibility(platformNotesFrameLayout);

                        AllPlatformNotificationsFragment allPlatformNotificationsFragment = new AllPlatformNotificationsFragment();
                        Bundle bundle = new Bundle();
                        allPlatformNotificationsFragment.setArguments(bundle);
                        initFragment(allPlatformNotificationsFragment, platformNotesFrameLayout);
                    }


                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        TabLayout.Tab personalisedTabItem = tabLayout.newTab();
        personalisedTabItem.setText("Personalized");
        tabLayout.addTab(personalisedTabItem, 0,true);

        TabLayout.Tab platformTabItem = tabLayout.newTab();
        platformTabItem.setText("Platform");
        tabLayout.addTab(platformTabItem, 1);

    }


    private void initFragment(Fragment fragment, FrameLayout frameLayout){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), fragment)
                .commit();


    }

    private void setFrameLayoutVisibility(FrameLayout frameLayoutToSetVisible){
        personalisedViewerFrameLayout.setVisibility(View.GONE);
        platformNotesFrameLayout.setVisibility(View.GONE);
        frameLayoutToSetVisible.setVisibility(View.VISIBLE);
    }

}