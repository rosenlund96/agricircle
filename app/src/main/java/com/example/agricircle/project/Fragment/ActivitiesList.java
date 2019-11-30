package com.example.agricircle.project.Fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Util.ActivityAdaptor;
import com.example.agricircle.R;

import java.util.List;

public class ActivitiesList extends Fragment implements AdapterView.OnItemClickListener{
    private ListView results;
    private Button createActivity;
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
        createActivity = myView.findViewById(R.id.newactivity);
        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.article_fragment
                                , new CreateActivityFragment())
                        .commit();
            }
        });
        myView.setFocusableInTouchMode(true);
        myView.requestFocus();
        myView.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.article_fragment
                                    , new MapFragment())
                            .commit();
                    return true;
                }
                return false;
            }
        } );



        return myView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        System.out.println("Activity status: " + activities.get(position).getFinished());
        if(!activities.get(position).getFinished()){
            FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ActivityRun fragment2 = new ActivityRun();

            Bundle bundle = new Bundle();
            Activity obj = activities.get(position);
            bundle.putSerializable("Activity", obj);
            fragment2.setArguments(bundle);
            ft.replace(R.id.article_fragment, fragment2);
            ft.addToBackStack(null);
            ft.commit();
        }
        else{
            FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ActivityRecieptFragment fragment2 = new ActivityRecieptFragment();

            Bundle bundle = new Bundle();
            Activity obj = activities.get(position);
            bundle.putSerializable("Activity", obj);
            fragment2.setArguments(bundle);
            ft.replace(R.id.article_fragment, fragment2);
            ft.addToBackStack(null);
            ft.commit();
        }

    }

    public void initialize(){
        activities = MainScreenActivity.getInstance().controller.getActivities();





    }

}
