package com.example.agricircle.project.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Util.ActivityAdaptor;
import com.example.agricircle.R;

import java.util.List;

public class ActivitiesList extends Fragment implements AdapterView.OnItemClickListener{
    private ListView results;

    private List<Activity> activities;
    View myView;

    public ActivitiesList(){
        initialize();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_list, container, false);
        results = (ListView) myView.findViewById(R.id.activitylist);
        results.setAdapter(new ActivityAdaptor(getContext(),activities));
        results.setOnItemClickListener(this);



        return myView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void initialize(){
        activities = MainScreenActivity.getInstance().controller.getActivities();





    }

}
