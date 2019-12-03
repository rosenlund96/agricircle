package com.example.agricircle.project.Fragment;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Field;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ActivityRun extends Fragment implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private View myView;
    private boolean locationEnabled;
    private Chronometer timer;
    private Activity activity;
    private GoogleMap mMap;
    private TextView fieldName, activityType, yourSpeed;
    private MainScreenActivity main;
    private Button finish, pause, location;
    Field field = null;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_run, container, false);
        timer = myView.findViewById(R.id.timer);
        locationEnabled = false;
        finish = myView.findViewById(R.id.finishButton);
        pause = myView.findViewById(R.id.pauseButton);
        location = myView.findViewById(R.id.locationButtonActivityRun);
        finish.setOnClickListener(this);
        pause.setOnClickListener(this);
        location.setOnClickListener(this);
        main = MainScreenActivity.getInstance();
        fieldName = myView.findViewById(R.id.fieldnameRun);
        activityType = myView.findViewById(R.id.RunType);
        yourSpeed = myView.findViewById(R.id.yourSpeed);
        yourSpeed.setText("0.0 KM/H");
        Bundle bundle = getArguments();
        try{
            Activity obj= (Activity) bundle.getSerializable("Activity");
            if(obj != null){
                activity = obj;
                activityType.setText(activity.getActivityType());
                System.out.println("Activityid: " + activity.getActivity_id());
                System.out.println("Activity BBCH: " + activity.getBBCHname());

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
        buildGoogleApiClient();

        mMap.getUiSettings().setAllGesturesEnabled(false);
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
        System.out.println("Aktivitet med id sendes: " + activity.getActivity_id());
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
        else if(v == location){
            if(!locationEnabled){
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                locationEnabled = true;
            }
            else{
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                locationEnabled = false;
            }
        }
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("OnConnected kaldt");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Spørg efter lokalitetsopdteringer");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if(field != null && location != null){
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(field.getCenterpoint().getCoordinates().get(0), 16.5F));
        }
        NumberFormat formatter = new DecimalFormat("#0.00");
        System.out.println("Locationchange kaldt");
        if (location==null){

            // if you can't get speed because reasons :)
            yourSpeed.setText("0,00 km/h");
        }
        else{
            //int speed=(int) ((location.getSpeed()) is the standard which returns meters per second.

            double speed=(double) ((location.getSpeed()*3600)/1000);

            yourSpeed.setText(formatter.format(speed)+" km/h");
        }
    }
}
