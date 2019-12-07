package com.example.agricircle.project.Fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.R;
import com.google.android.material.tabs.TabLayout;

public class ListViewFragment extends Fragment implements TabLayout.OnTabSelectedListener {
    TabLayout tabs;
    TextView filterButton;
    View myView;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.listviewlayout, container, false);
        filterButton = myView.findViewById(R.id.filterText);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.listfragment
                                , new filterLayoutFragment())
                        .commit();
            }
        });
        tabs = (TabLayout) myView.findViewById(R.id.ListViewTab);
        tabs.setOnTabSelectedListener(this);

        tabs.addTab(tabs.newTab().setText(R.string.activities));
        tabs.addTab(tabs.newTab().setText(R.string.fieldlist));






        return myView;
    }

    private boolean checkNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        if(tab.getText().toString().equals(getResources().getString(R.string.activities))){
            filterButton.setVisibility(View.VISIBLE);
            this.getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.listfragment
                            , new ActivitiesList())
                    .commit();
        }
        if(tab.getText().toString().equals(getResources().getString(R.string.fieldlist))){
            filterButton.setVisibility(View.INVISIBLE);
            this.getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.listfragment
                            , new FieldListFragment())
                    .commit();
        }


    }



    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
