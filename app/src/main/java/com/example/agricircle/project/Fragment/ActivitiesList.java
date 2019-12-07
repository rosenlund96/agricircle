package com.example.agricircle.project.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.project.Activities.MainActivity;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Entities.SearchItem;
import com.example.agricircle.project.Util.ActivityAdaptor;
import com.example.agricircle.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.agricircle.project.Activities.MainActivity.MY_PERMISSIONS_REQUEST_LOCATION;

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
        Bundle bundle = getArguments();
        try{
            SearchItem obj= (SearchItem) bundle.getSerializable("Search");
            if(obj != null){
                List<Activity> temp = MainScreenActivity.getInstance().controller.getActivities();
                List<Activity> temp2 = new ArrayList<>();
                List<Activity> temp3 = new ArrayList<>();
                if(!obj.field.equals("All fields")){
                    for(int i = 0; i<temp.size();i++){
                        if(temp.get(i).getFieldname().equals(obj.field)){
                            temp2.add(temp.get(i));
                        }
                    }
                }
                else{
                    temp2 = temp;
                }
                if(!obj.crops.equals("All crops")){
                    for(int i = 0 ; i<temp2.size();i++){
                        if(getCropName(temp2.get(i).getCrop_id()).equals(obj.crops)){
                            temp3.add(temp2.get(i));
                        }
                    }
                }
                else{
                    temp3 = temp2;
                }


                activities = temp3;

            }
        }catch (Exception e){
            System.out.println("Ingen data sendt med");
            activities = MainScreenActivity.getInstance().controller.getActivities();

        }

        results.setAdapter(new ActivityAdaptor(getContext(),activities));
        results.setOnItemClickListener(this);
        createActivity = myView.findViewById(R.id.newactivity);
        createActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkNetwork()){
                    getFragmentManager().beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.article_fragment
                                    , new CreateActivityFragment())
                            .commit();
                }
                else{
                    new AlertDialog.Builder(getContext())
                            .setTitle(getContext().getResources().getString(R.string.offline))
                            .setMessage(getResources().getString(R.string.networkrequired))
                            .setPositiveButton(getResources().getText(R.string.buttonOK), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(getActivity(),
                                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION );
                                }
                            })
                            .create()
                            .show();
                }

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

    private boolean checkNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public void initialize(){











    }

    private String getCropName(int id){
        String temp = "";
        List<Crop> crops = MainScreenActivity.getInstance().controller.getUser().cropsList;
        for(int i = 0; i<crops.size(); i++){
            if(crops.get(i).getCrop_id() == id){
                temp = crops.get(i).getName();
            }
        }
        return temp;
    }

}
