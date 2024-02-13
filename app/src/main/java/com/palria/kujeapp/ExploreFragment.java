package com.palria.kujeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.chip.Chip;

public class ExploreFragment extends Fragment {


    public ExploreFragment() {
        // Required empty public constructor
    }
    public ExploreFragment(BottomAppBar bottomAppBar) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
  View parentView = inflater.inflate(R.layout.fragment_explore, container, false);
initUI(parentView);


        return parentView;
    }

    void initUI(View parentView){

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

         }
}

