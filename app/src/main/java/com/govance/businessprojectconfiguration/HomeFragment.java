package com.govance.businessprojectconfiguration;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {


    GridView mainActionsGridView;
    CustomActionsGridViewAdapter customActionsGridViewAdapter;
    public HomeFragment() {
        // Required empty public constructor
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

        openAllProductsFragment();

        return parentView;
    }

    private void initUI(View view){
        mainActionsGridView = view.findViewById(R.id.mainActionsGridViewId);

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

    private void openAllProductsFragment(){
        getChildFragmentManager().beginTransaction().replace(R.id.allProductsFrameLayoutId,new AllProductsFragment()).commit();

    }
}