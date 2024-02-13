package com.palria.kujeapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    Chip exploreChip;
    Chip updatesChip;
    Chip newsChip;
    Chip jobsChip;
    Chip inquiriesChip;
    GridView mainActionsGridView;
    CustomActionsGridViewAdapter customActionsGridViewAdapter;




    boolean isExploreFragmentOpen = false;
    boolean isUpdatesFragmentOpen = false;
    boolean isNewsFragmentOpen = false;
    boolean isJobFragmentOpen = false;
    boolean isInquiriesFragmentOpen = false;
    FrameLayout updatesFrameLayout;
    FrameLayout newsFrameLayout;
    FrameLayout exploreFrameLayout;
    FrameLayout jobsFrameLayout;
    FrameLayout inquiriesFrameLayout;

    public HomeFragment() {
        // Required empty public constructor
    }
    public HomeFragment(BottomAppBar bottomAppBar) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
    View parentView =     inflater.inflate(R.layout.fragment_home, container, false);
    initUI(parentView);
        manageMainActionGridView();
        manageChipSelection();


//        openAllProductsFragment();

        return parentView;
    }

    private void initUI(View view){
        mainActionsGridView = view.findViewById(R.id.mainActionsGridViewId);

        exploreChip = view.findViewById(R.id.exploreChipId);
        updatesChip = view.findViewById(R.id.updatesChipId);
        newsChip = view.findViewById(R.id.newsChipId);
        jobsChip = view.findViewById(R.id.jobsChipId);

        inquiriesChip = view.findViewById(R.id.inquiriesChipId);
        exploreFrameLayout = view.findViewById(R.id.exploreFrameLayoutId);
        updatesFrameLayout = view.findViewById(R.id.updatesFrameLayoutId);
        newsFrameLayout = view.findViewById(R.id.newsFrameLayoutId);
        jobsFrameLayout = view.findViewById(R.id.jobsFrameLayoutId);
        inquiriesFrameLayout = view.findViewById(R.id.inquiriesFrameLayoutId);

    }

private void manageMainActionGridView(){

    ArrayList<String> tagNameArrayList = new ArrayList<>();
    tagNameArrayList.add("Customers");
    ArrayList<Integer> imageButtonResInt = new ArrayList<>();
    imageButtonResInt.add(R.drawable.ic_baseline_group_24);

    customActionsGridViewAdapter = new CustomActionsGridViewAdapter(getContext(),tagNameArrayList, imageButtonResInt);
}

    class CustomActionsGridViewAdapter extends BaseAdapter {
        Context context;
        ArrayList<String> tagNameArrayList;
        ArrayList<Integer> imageButtonResInt;


            CustomActionsGridViewAdapter(Context context, ArrayList<String> tagNameArrayList,ArrayList<Integer> imageButtonResInt){

                this.context = context;
                this.tagNameArrayList = tagNameArrayList;
                this.imageButtonResInt  = imageButtonResInt;
        }
        @Override
        public int getCount() {
            return tagNameArrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
           View itemView = getLayoutInflater().inflate(R.layout.main_actions_grid_view_layout_holder,viewGroup,false);

            ImageButton imageButton = itemView.findViewById(R.id.imageButtonId);
            TextView tagNameTextView = itemView.findViewById(R.id.tagNameTextViewId);

            imageButton.setImageResource(imageButtonResInt.get(i));
            tagNameTextView.setText(tagNameArrayList.get(i));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            return itemView;
        }
    }

    void manageChipSelection(){
        exploreChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (isExploreFragmentOpen) {
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(exploreFrameLayout);

                    }
                    else {
                        isExploreFragmentOpen = true;

                        setFrameLayoutVisibility(exploreFrameLayout);
                        Bundle bundle = new Bundle();
//                        bundle.putBoolean(GlobalConfig.IS_OPEN_STARTED_QUIZ_KEY, false);
                        ExploreFragment exploreFragment = new ExploreFragment();
                        exploreFragment.setArguments(bundle);
                        initFragment(exploreFragment, exploreFrameLayout);
                    }
                }

            }
        });
        exploreChip.setChecked(true);

        updatesChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (isUpdatesFragmentOpen) {
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(updatesFrameLayout);

                    }
                    else {
                        isUpdatesFragmentOpen = true;

                        setFrameLayoutVisibility(updatesFrameLayout);
                        Bundle bundle = new Bundle();
//                        bundle.putBoolean(GlobalConfig.IS_OPEN_STARTED_QUIZ_KEY, false);
                        AllUpdatesFragment allUpdatesFragment = new AllUpdatesFragment();
                        allUpdatesFragment.setArguments(bundle);
                        initFragment(allUpdatesFragment, updatesFrameLayout);
                    }
                }

            }
        });
        newsChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (isNewsFragmentOpen) {
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(newsFrameLayout);

                    }
                    else {
                        isNewsFragmentOpen = true;

                        setFrameLayoutVisibility(newsFrameLayout);
                        Bundle bundle = new Bundle();
//                        bundle.putBoolean(GlobalConfig.IS_OPEN_STARTED_QUIZ_KEY, true);
                        AllUpdatesFragment allUpdatesFragment = new AllUpdatesFragment();
                        allUpdatesFragment.setArguments(bundle);
                        initFragment(allUpdatesFragment, newsFrameLayout);
                    }
                }

            }
        });

        jobsChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (isJobFragmentOpen) {
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(jobsFrameLayout);

                    }
                    else {
                        isJobFragmentOpen = true;

                        setFrameLayoutVisibility(jobsFrameLayout);
                        Bundle bundle = new Bundle();
//                        bundle.putBoolean(GlobalConfig.IS_OPEN_STARTED_QUIZ_KEY, true);
                        AllJobsFragment allJobsFragment = new AllJobsFragment();
                        allJobsFragment.setArguments(bundle);
                        initFragment(allJobsFragment, jobsFrameLayout);
                    }
                }

            }
        });
        inquiriesChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if (isInquiriesFragmentOpen) {
                        //Just set the frame layout visibility
                        setFrameLayoutVisibility(inquiriesFrameLayout);

                    }
                    else {
                        isInquiriesFragmentOpen = true;

                        setFrameLayoutVisibility(inquiriesFrameLayout);
                        Bundle bundle = new Bundle();
//                        bundle.putBoolean(GlobalConfig.IS_OPEN_COMPLETED_QUIZ_KEY, true);
                        AllInquiryFragment allInquiryFragment = new AllInquiryFragment();
                        allInquiryFragment.setArguments(bundle);
                        initFragment(allInquiryFragment, inquiriesFrameLayout);
                    }
                }

            }
        });



    }


    private void initFragment(Fragment fragment, FrameLayout frameLayout){
        FragmentManager fragmentManager = getChildFragmentManager();
        try {
            fragmentManager.beginTransaction()
                    .replace(frameLayout.getId(), fragment)
                    .commit();
        }catch(Exception e){}

    }

    private void setFrameLayoutVisibility(FrameLayout frameLayoutToSetVisible){
        exploreFrameLayout.setVisibility(View.GONE);
        updatesFrameLayout.setVisibility(View.GONE);
        newsFrameLayout.setVisibility(View.GONE);
        jobsFrameLayout.setVisibility(View.GONE);
        inquiriesFrameLayout.setVisibility(View.GONE);
        frameLayoutToSetVisible.setVisibility(View.VISIBLE);
    }

}