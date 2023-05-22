package com.govance.businessprojectconfiguration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.govance.businessprojectconfiguration.adapters.MoreActionsAdapter;
import com.govance.businessprojectconfiguration.models.MoreActionsModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

DrawerLayout drawerLayout;
BottomNavigationView bottomNavigationView;
BottomSheetDialog bottomSheetDialog;
TabLayout tabLayout;
FrameLayout productsFrameLayout;
FrameLayout allServicesFrameLayout;
FrameLayout userProfileFrameLayout;
FrameLayout allMessagesFrameLayout;
FrameLayout allCollectionsFrameLayout;
FrameLayout allUpdatesFrameLayout;
ExtendedFloatingActionButton messageActionButton;
    NavigationView mainNavigationView;
//FrameLayout allCustomersFrameLayout;
    ImageButton drawerOpenerActionButton;
    ImageButton popUpMenuActionButton;
    ImageButton searchActionButton;
    int productsFrameLayoutOpenCounter = 0;
    int allServicesFrameLayoutOpenCounter = 0;
    int userProfileFrameLayoutOpenCounter = 0;
    int allMessagesFrameLayoutOpenCounter = 0;
    int allCollectionsFrameLayoutOpenCounter = 0;
    int allUpdatesFrameLayoutOpenCounter = 0;
//    int allCustomersFrameLayoutOpenCounter =0;
    Menu mainNavigationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!GlobalValue.isUserLoggedIn()){
            Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }
        setContentView(R.layout.activity_main);
        initUI();
//        GlobalValue.setCurrentBusinessId();
       ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout/*,mainToolbar*/, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        initMoreActions();
//        manageTabLayout();
        manageBottomNavView();
        initNavigationView();
        drawerOpenerActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawerLayout();
            }
        });
        if(!GlobalValue.isBusinessOwner()) {
            popUpMenuActionButton.setVisibility(View.GONE);
        }
//        startService(new Intent(this,NotificationHandlerService.class));
        popUpMenuActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this,PostNewProductActivity.class));
////                startActivity(new Intent(MainActivity.this,EditUserProfileActivity.class));
                bottomSheetDialog.show();

            }
        });
        searchActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,SearchActivity.class));

            }
        });
        messageActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (GlobalValue.isBusinessOwner()) {
                    if (allMessagesFrameLayoutOpenCounter < 1) {
                        openFragment(new AllMessagesFragment(), allMessagesFrameLayout);
                        setLayoutVisibility(allMessagesFrameLayout);
                        allMessagesFrameLayoutOpenCounter++;
                    } else {

                        allMessagesFrameLayoutOpenCounter++;
                        setLayoutVisibility(allMessagesFrameLayout);

                    }
                    messageActionButton.setVisibility(View.GONE);

//                } else {
//
//                }
            }
        });
//        postNewActionButton.setOnContextClickListener(new View.OnContextClickListener() {
//            @Override
//            public boolean onContextClick(View view) {
//                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
//                return false;
//            }
//        });
//        postNewActionButton.setOnHoverListener(new View.OnHoverListener() {
//            @Override
//            public boolean onHover(View view, MotionEvent motionEvent) {
//                return false;
//            }
//        });
        GlobalValue.setOnChatsFragmentRefreshTriggeredListener(new GlobalValue.OnRefreshChatsTriggeredListener() {
            @Override
            public void onRefreshTriggered() {
                refreshChatsFragment();
            }
        });

        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        });
//
//
//        mainNavigationMenu.removeItem(R.id.feedbackId);
//        mainNavigationMenu.getItem(2).setTitle("Item Changed");

    }

    private void initUI(){
        drawerLayout = findViewById(R.id.drawerLayoutId);
        mainNavigationView = findViewById(R.id.mainNavigationViewId);
        drawerOpenerActionButton = findViewById(R.id.drawerOpenerActionButtonId);
        popUpMenuActionButton = findViewById(R.id.popUpMenuActionButtonId);
        searchActionButton = findViewById(R.id.searchActionButtonId);
        bottomNavigationView = findViewById(R.id.bottomNavigationViewId);
        mainNavigationMenu = mainNavigationView.getMenu();
        tabLayout = findViewById(R.id.tabLayoutId);
        productsFrameLayout = findViewById(R.id.homeFrameLayoutId);
        allServicesFrameLayout = findViewById(R.id.allServicesFrameLayoutId);
        userProfileFrameLayout = findViewById(R.id.userProfileFrameLayoutId);
        allMessagesFrameLayout = findViewById(R.id.allMessagesFrameLayoutId);
        allCollectionsFrameLayout = findViewById(R.id.allCollectionsFrameLayoutId);
        allUpdatesFrameLayout = findViewById(R.id.allUpdatesFrameLayoutId);
        messageActionButton = findViewById(R.id.messageActionButtonId);
//        allCustomersFrameLayout = findViewById(R.id.allCustomersFrameLayoutId);


    }
    public void openDrawerLayout(){
//        drawerLayout = findViewById(R.id.mainDrawerLayoutId);
//        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawerLayout.addDrawerListener(actionBarDrawerToggle);
//        actionBarDrawerToggle.syncState();
        drawerLayout.openDrawer(GravityCompat.START);

    }
    private void initNavigationView(){

        mainNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
/*
                if (item.getItemId() == R.id.allOrdersId) {
                    startActivity( GlobalValue.getHostActivityIntent(getApplicationContext(),null,GlobalValue.ORDER_FRAGMENT_TYPE,null));
                }
                if (item.getItemId() == R.id.allRequestsId) {
                    startActivity( GlobalValue.getHostActivityIntent(getApplicationContext(),null,GlobalValue.REQUEST_FRAGMENT_TYPE,null));
                }
                if (item.getItemId() == R.id.allCustomersId) {
                    startActivity( GlobalValue.getHostActivityIntent(getApplicationContext(),null,GlobalValue.CUSTOMERS_FRAGMENT_TYPE,null));
                }
                if (item.getItemId() == R.id.allNotesId) {
                Intent intent = new Intent(MainActivity.this,NotesActivity.class);
                startActivity(intent);

                }
                */
                if (item.getItemId() == R.id.feedbackId) {
                    startActivity(new Intent(getApplicationContext(), SendFeedbackActivity.class));
                }
                if (item.getItemId() == R.id.signOutId) {
                    signOut();
                    MainActivity.this.finish();
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
                    Toast.makeText(getApplicationContext(), "Signed out", Toast.LENGTH_SHORT).show();

                }
               else if (item.getItemId() == R.id.notificationsId) {
                    startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                }
                else if (item.getItemId() == R.id.aboutId) {
                    startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
                }
                else if (item.getItemId() == R.id.messageUsId) {
                    startActivity(GlobalValue.goToChatRoom(getApplicationContext(), GlobalValue.getCurrentBusinessId(), getString(R.string.app_name), new ImageView(getApplicationContext())));
                }
                else if (item.getItemId() == R.id.submitProductId) {
                    Intent intent = new Intent(getApplicationContext(), PostNewProductActivity.class);
                    intent.putExtra(GlobalValue.IS_PRODUCT_SUBMISSION, true);
                    startActivity(intent);
//                    Toast.makeText(getApplicationContext(), "post started//////////////////", Toast.LENGTH_SHORT).show();
                }
               /*
                if (item.getItemId() == R.id.sendNotificationId) {
                    startActivity(new Intent(getApplicationContext(), CreateNewNotificationActivity.class));
                }
                if (item.getItemId() == R.id.addNewServiceId) {
                    startActivity(new Intent(getApplicationContext(), AddNewServiceActivity.class));
                }
                */
                else if (item.getItemId() == R.id.updateAppId) {

                    try {
                        Intent updateAppIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                        updateAppIntent.setPackage("com.android.vending");
                        updateAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        updateAppIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                        updateAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(updateAppIntent);
                    } catch (android.content.ActivityNotFoundException activityNotFoundException) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));

                    }
                }
//                    if (item.getItemId() == R.id.aboutId) {
////                    startActivity(new Intent(getApplicationContext(),AboutAppActivity.class));
//
                else if (item.getItemId() == R.id.shareAppId) {
                try {
                    Intent shareAppIntent = new Intent(Intent.ACTION_SEND);
                    shareAppIntent.putExtra(Intent.EXTRA_TEXT, "Download " + getString(R.string.app_name) + " App from Play store:  \r\n" + "https://play.google.com/store/apps/details?id=" + getPackageName() + "  \r\ncurrently available on ANDROID PLATFORM \r\n");

                    shareAppIntent.setType("text/plain");
                    startActivity(shareAppIntent);



                }catch(Exception ignored){}
                }
//                if (item.getItemId() == R.id.signOutId) {
//                    accountSignOutConfirmationDialog.show();
//
//                }
//                if (item.getItemId() == R.id.deleteAccountId) {
//                    accountDeletionConfirmationDialog.show();
//
//                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }
    private void manageTabLayout(){

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch(tab.getPosition()){
                        case 1:
                            if(productsFrameLayoutOpenCounter <1) {
                                openFragment(new AllProductsFragment(), productsFrameLayout);
                                setLayoutVisibility(productsFrameLayout);
                                productsFrameLayoutOpenCounter++;
                            }else{

                                productsFrameLayoutOpenCounter++;

                            }


                        case 2:
                            if(allServicesFrameLayoutOpenCounter <1) {
                                openFragment(new AllOrdersFragment(), allServicesFrameLayout);
                                setLayoutVisibility(allServicesFrameLayout);
                                allServicesFrameLayoutOpenCounter++;
                            }else{
                                setLayoutVisibility(allServicesFrameLayout);
                                allServicesFrameLayoutOpenCounter++;

                            }

                           
                            break;
                            case 3:
                            if(allMessagesFrameLayoutOpenCounter <1) {
                                openFragment(new AllOrdersFragment(), allMessagesFrameLayout);
                                setLayoutVisibility(allMessagesFrameLayout);
                                allMessagesFrameLayoutOpenCounter++;
                            }else{
                                setLayoutVisibility(allMessagesFrameLayout);
                                allMessagesFrameLayoutOpenCounter++;

                            }


                            break;
                         /*
                            case 4:
                            if(allCustomersFrameLayoutOpenCounter <1) {
                                openFragment(new AllOrdersFragment(), allCustomersFrameLayout);
                                setLayoutVisibility(allCustomersFrameLayout);
                                allCustomersFrameLayoutOpenCounter++;
                            }else{
                                setLayoutVisibility(allCustomersFrameLayout);
                                allCustomersFrameLayoutOpenCounter++;

                            }
                            break;
                            */
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        TabLayout.Tab productsTabItem= tabLayout.newTab();
        productsTabItem.setText("Products");
        tabLayout.addTab(productsTabItem,0,true);

//        TabLayout.Tab collectionTabItem= tabLayout.newTab();
//        collectionTabItem.setText("Chats");
//        tabLayout.addTab(collectionTabItem,1);
/*
        TabLayout.Tab activityTabItem=tabLayout.newTab();
        activityTabItem.setText("Activity");
    tabLayout.addTab(activityTabItem,1);
*/
        TabLayout.Tab ordersTabItem=tabLayout.newTab();
        ordersTabItem.setText("Orders");
        tabLayout.addTab(ordersTabItem,1);

        TabLayout.Tab messagesTabItem=tabLayout.newTab();
        messagesTabItem.setText("Messages");
        tabLayout.addTab(messagesTabItem,2);

        TabLayout.Tab customersTabItem=tabLayout.newTab();
        customersTabItem.setText("Customers");
        tabLayout.addTab(customersTabItem,3);

//
//        TabLayout.Tab myPages=tabLayout.newTab();
//        myPages.setText("My pages");
//        tabLayout.addTab(myPages,4);
//
//        TabLayout.Tab messageTabItem=tabLayout.newTab();
//        messageTabItem.setText("Your feed");
//        tabLayout.addTab(messageTabItem,4);



    }
    void initMoreActions(){
        ArrayList<MoreActionsModel> moreActionsList = new ArrayList<>();
        moreActionsList.add(new MoreActionsModel("Add product",R.drawable.ic_baseline_store_mall_directory_24));
        moreActionsList.add(new MoreActionsModel("Create note",R.drawable.ic_baseline_note_add_24));
        moreActionsList.add(new MoreActionsModel("Notify",R.drawable.ic_baseline_add_alert_24));
        moreActionsList.add(new MoreActionsModel("New service",R.drawable.ic_baseline_miscellaneous_services_24));
        moreActionsList.add(new MoreActionsModel("Notes",R.drawable.ic_baseline_notes_24));
        moreActionsList.add(new MoreActionsModel("Records",R.drawable.ic_baseline_note_add_24));
        moreActionsList.add(new MoreActionsModel("Orders",R.drawable.ic_baseline_local_grocery_store_24));
        moreActionsList.add(new MoreActionsModel("Requests",R.drawable.ic_baseline_help_center_24));
        moreActionsList.add(new MoreActionsModel("Customers",R.drawable.ic_baseline_people_24));
        moreActionsList.add(new MoreActionsModel("New update",R.drawable.ic_baseline_local_fire_department_24));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        MoreActionsAdapter moreActionsAdapter = new MoreActionsAdapter(this,moreActionsList,bottomSheetDialog);

        View sheet =  getLayoutInflater().inflate(R.layout.business_owner_more_actions_layout,drawerLayout,false);
        RecyclerView recyclerView  = sheet.findViewById(R.id.moreActionsRecyclerViewId);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(moreActionsAdapter);
        recyclerView.setHasFixedSize(true);
        bottomSheetDialog.setContentView(sheet);
    }
    private void manageBottomNavView(){
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
messageActionButton.setVisibility(View.GONE);
                switch(item.getItemId()){
                    case R.id.goodsId:
                        if(productsFrameLayoutOpenCounter <1) {
                            openFragment(new AllProductsFragment(), productsFrameLayout);
                            setLayoutVisibility(productsFrameLayout);
                            productsFrameLayoutOpenCounter++;
                        }else{

                            productsFrameLayoutOpenCounter++;
                            setLayoutVisibility(productsFrameLayout);

                        }
                        messageActionButton.setVisibility(View.VISIBLE);

                        break;

                    case  R.id.servicesId:
                        if(allServicesFrameLayoutOpenCounter <1) {
                            openFragment(new AllServicesFragment(), allServicesFrameLayout);
                            setLayoutVisibility(allServicesFrameLayout);
                            allServicesFrameLayoutOpenCounter++;
                        }else{
                            setLayoutVisibility(allServicesFrameLayout);
                            allServicesFrameLayoutOpenCounter++;

                        }


                        break;

                    case  R.id.profileId:
                        if(userProfileFrameLayoutOpenCounter <1) {
                            Fragment profile = new UserProfileFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(GlobalValue.USER_ID,GlobalValue.getCurrentUserId());
                            profile.setArguments(bundle);
                            openFragment(profile, userProfileFrameLayout);
                            setLayoutVisibility(userProfileFrameLayout);
                            userProfileFrameLayoutOpenCounter++;
                        }else{
                            setLayoutVisibility(userProfileFrameLayout);
                            userProfileFrameLayoutOpenCounter++;

                        }
                        messageActionButton.setVisibility(View.VISIBLE);

                        break;

                   /* case R.id.messagesId:
                        if(allMessagesFrameLayoutOpenCounter <1) {
                            openFragment(new AllMessagesFragment(), allMessagesFrameLayout);
                            setLayoutVisibility(allMessagesFrameLayout);
                            allMessagesFrameLayoutOpenCounter++;
                        }else{
                            setLayoutVisibility(allMessagesFrameLayout);
                            allMessagesFrameLayoutOpenCounter++;

                        }


                        break;
                        */

                    /*    case R.id.allCollectionsId:
                        if(allCollectionsFrameLayoutOpenCounter <1) {
                            openFragment(new AllCollectionsFragment(), allCollectionsFrameLayout);
                            setLayoutVisibility(allCollectionsFrameLayout);
                            allCollectionsFrameLayoutOpenCounter++;
                        }else{
                            setLayoutVisibility(allCollectionsFrameLayout);
                            allCollectionsFrameLayoutOpenCounter++;

                        }
*/

                     //   break;
                /*    case 4:
                        if(allCustomersFrameLayoutOpenCounter <1) {
                            openFragment(new AllOrdersFragment(), allCustomersFrameLayout);
                            setLayoutVisibility(allCustomersFrameLayout);
                            allCustomersFrameLayoutOpenCounter++;
                        }else{
                            setLayoutVisibility(allCustomersFrameLayout);
                            allCustomersFrameLayoutOpenCounter++;

                        }


                        break;
                        */
                    case R.id.allUpdatesId:
                        if(allUpdatesFrameLayoutOpenCounter <1) {
                            openFragment(new AllUpdatesFragment(), allUpdatesFrameLayout);
                            setLayoutVisibility(allUpdatesFrameLayout);
                            allUpdatesFrameLayoutOpenCounter++;
                        }else{
                            setLayoutVisibility(allUpdatesFrameLayout);
                            allUpdatesFrameLayoutOpenCounter++;

                        }


                        break;

                }


                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.goodsId);

    }

    public void openFragment(Fragment fragment, FrameLayout frameLayoutToBeReplaced){
        Bundle bundle = new Bundle();
        bundle.putBoolean(GlobalValue.IS_FROM_SEARCH_CONTEXT, false);
        bundle.putString(GlobalValue.USER_ID,GlobalValue.getCurrentUserId());
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(frameLayoutToBeReplaced.getId(),fragment).commit();
    }

    public void setLayoutVisibility(FrameLayout layoutToBeVisible){

        productsFrameLayout.setVisibility(View.GONE);
        allServicesFrameLayout.setVisibility(View.GONE);
        allMessagesFrameLayout.setVisibility(View.GONE);
        userProfileFrameLayout.setVisibility(View.GONE);
        allCollectionsFrameLayout.setVisibility(View.GONE);
        allUpdatesFrameLayout.setVisibility(View.GONE);
//        allCustomersFrameLayout.setVisibility(View.GONE);
        layoutToBeVisible.setVisibility(View.VISIBLE);
    }
    void refreshChatsFragment(){
        Bundle bundle = new Bundle();

        allMessagesFrameLayoutOpenCounter = 1;
        openFragment(new AllMessagesFragment(), allMessagesFrameLayout);
        setLayoutVisibility(allMessagesFrameLayout);
    }
    private void signOut(){
        FirebaseAuth.getInstance().signOut();

    }

    void paste(){
//         new ModelClasses.UserProfileModel("Nnamdi Henry");
//        "package_name": "com.anchorallianceglobal.aaghomesproperty"

    }
}