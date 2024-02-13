package com.palria.kujeapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomappbar.BottomAppBar;

public class JobsFragment extends Fragment {

    public JobsFragment() {
        // Required empty public constructor
    }
    public JobsFragment(BottomAppBar bottomAppBar) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
  View parentView = inflater.inflate(R.layout.fragment_jobs, container, false);
initUI(parentView);


        return parentView;
    }

    void initUI(View parentView){

    }


}

