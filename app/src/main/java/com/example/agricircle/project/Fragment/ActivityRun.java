package com.example.agricircle.project.Fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Field;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class ActivityRun extends Fragment implements OnMapReadyCallback, View.OnClickListener {


    private View myView;
    private Chronometer timer;
    private Activity activity;
    private GoogleMap mMap;
    private TextView fieldName, activityType;
    private MainScreenActivity main;
    private Button finish, pause;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_run, container, false);
        timer = myView.findViewById(R.id.timer);
        finish = myView.findViewById(R.id.finishButton);
        pause = myView.findViewById(R.id.pauseButton);
        finish.setOnClickListener(this);
        pause.setOnClickListener(this);
        main = MainScreenActivity.getInstance();
        fieldName = myView.findViewById(R.id.fieldnameRun);
        activityType = myView.findViewById(R.id.RunType);
        Bundle bundle = getArguments();
        try{
            Activity obj= (Activity) bundle.getSerializable("Activity");
            if(obj != null){
                activity = obj;
                activityType.setText(activity.getActivityType());

            }
        }catch (Exception e){
            System.out.println("Intet felt sendt med");
            activity = null;

        }

        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return myView;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        String map = LoadPreferences("Map");
        if(map.equals("Normal")){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(map.equals("Sattelite")){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if(map.equals("Hybrid")){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        init();

    }

    private String LoadPreferences(String key) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String result = sharedPreferences.getString(key, "");
        return result;

    }

    private void drawPolygon(List<LatLng> points){
        PolygonOptions rectOptions = new PolygonOptions();
        for(int i = 0; i<points.size(); i++){
            rectOptions.add(points.get(i));

        }
        rectOptions.add(points.get(0));

        rectOptions.strokeColor(Color.argb(255,0,128,255));
        rectOptions.fillColor(Color.argb(160,0,128,255));

        Polygon polygon = mMap.addPolygon(rectOptions);
        polygon.setClickable(false);
    }

    public void init(){
        Field field = null;
        List<Field> Fields = main.controller.user.getFields();
        for(int i= 0; i<Fields.size();i++ ){
            if(Fields.get(i).getId() == activity.getField_id()){
                field = Fields.get(i);
            }
        }
        drawPolygon(field.getCoordinates().getCoordinates());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(field.getCenterpoint().getCoordinates().get(0), 16.5F));
        fieldName.setText(field.getDisplay_name());
        //Hvis aktivitet ikke har været startet før
        System.out.println("Activity time: " + activity.getCurrentTimestamp());
        if(activity.getCurrentTimestamp() == null){
            timer.start();
        }
        else{
            System.out.println(activity.getCurrentTimestamp().length());
            int nr_of_min = 2;
            int nr_of_sec = 48;
            int nr_of_hr = 0;
            if(activity.getCurrentTimestamp().length() == 5){
                nr_of_sec = Integer.parseInt(activity.getCurrentTimestamp().substring(3,5));
                nr_of_min = Integer.parseInt(activity.getCurrentTimestamp().substring(0,2));
                System.out.println("Min: " + nr_of_min + " sec: " + nr_of_sec);
            }
            else if(activity.getCurrentTimestamp().length() == 8){
                nr_of_hr = Integer.parseInt(activity.getCurrentTimestamp().substring(0,2));
                nr_of_sec = Integer.parseInt(activity.getCurrentTimestamp().substring(6,8));
                nr_of_min = Integer.parseInt(activity.getCurrentTimestamp().substring(3,5));
            }


            timer.setBase(SystemClock.elapsedRealtime() - (nr_of_min * 60000 + nr_of_sec * 1000));
            timer.start();
        }
    }

    public void saveActivity(boolean status){
        activity.setCurrentTimestamp(timer.getText().toString());
        activity.setFinished(status);



        //main.controller.saveCurrentActivity(activity);
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ActivityRecieptFragment fragment2 = new ActivityRecieptFragment();

        Bundle bundle = new Bundle();
        Activity obj = activity;
        bundle.putSerializable("Activity", obj);
        fragment2.setArguments(bundle);


        ft.replace(R.id.article_fragment, fragment2);
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void onClick(View v) {
        if(v==finish){
            saveActivity(true);
        }
        else if(v==pause){
            //set time og gem nuværende activity
            saveActivity(false);

        }
    }
}
