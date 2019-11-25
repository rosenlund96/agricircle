package com.example.agricircle.project.Fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.R;
import com.example.agricircle.project.Entities.User;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback,

        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap mGoogleMap;
    private SupportMapFragment mapFrag;
    private LocationRequest mLocationRequest;
    private boolean request;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private MainScreenActivity main;
    private Button userprofile,fields, weather,locationbutton;
    private ImageView weatherImage, imgx,imgy;
    private View myView;
    private List<Polygon> polygons;
    private List<Marker> mapMarkers;
    private List<Activity> activitites;
    private String activePolygon;
    private LinearLayout infoLayout, locationlayout;
    private TextView infoFieldName, infoFieldSurface, productx,producty, activitytypex,activitytypey, locationtext;
    private List<String> parameters;
    private boolean firstUpdate, fieldPresent;
    private Field field;
    private Display display;
    private LatLng latLng;
    private int width,height;
    private double heightconstant, widthconstant;
    public MapFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("On Resume kaldt");


        Bundle bundle = getArguments();
        try{
            Field obj= (Field) bundle.getSerializable("Field");
            if(obj != null){
                System.out.println(obj.getDisplay_name() + " blev sendt med");
                fieldPresent = true;
                field = obj;


            }
        }catch (Exception e){
            System.out.println("Intet felt sendt med");
            field = null;
            fieldPresent = false;
        }




        weather.setText(R.string.loadingWeather);
        //opdaterVejr(latLng);
        if(main.controller.getUser().fields.isEmpty()){
            Gson gson = new Gson();
            String json = LoadPreferences("User");
            User user = gson.fromJson(json, User.class);
            main.controller.setUser(user);
        }
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

        }



        clearPolygonColors();





    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activePolygon = "";
        widthconstant = 0.4;
        heightconstant = 0.0147;
        request = false;
        firstUpdate = false;
        fieldPresent = false;
        field = null;
        myView = inflater.inflate(R.layout.mainview, container, false);
        locationbutton = myView.findViewById(R.id.enablelocation);
        locationbutton.setText(getResources().getString(R.string.myLocation));
        locationbutton.setOnClickListener(this);
        locationtext =  myView.findViewById(R.id.locationtext);
        locationtext.setText(getResources().getString(R.string.locationtext));
        locationlayout =  myView.findViewById(R.id.locationlayout);
        locationlayout.setVisibility(View.INVISIBLE);
        //locationbutton.setVisibility(View.INVISIBLE);
        fields =  myView.findViewById(R.id.googlemaps_list);
        userprofile =  myView.findViewById(R.id.googlemaps_settings);
        infoLayout =  myView.findViewById(R.id.infolayout);
        infoFieldName =  myView.findViewById(R.id.mapFieldName);
        infoFieldSurface =  myView.findViewById(R.id.mapSurface);
        productx =  myView.findViewById(R.id.productx);
        producty =  myView.findViewById(R.id.producty);
        activitytypex =  myView.findViewById(R.id.activitytypex);
        activitytypey =  myView.findViewById(R.id.activitytypey);
        imgx =  myView.findViewById(R.id.imgx);
        imgy =  myView.findViewById(R.id.imgy);
        fields.setOnClickListener(this);
        weatherImage =  myView.findViewById(R.id.weatherimage);
        weather =  myView.findViewById(R.id.weatherbutton);
        weather.setOnClickListener(this);
        display = getActivity().getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        height = display.getHeight();
        weather.setWidth((int) (width * widthconstant));
        weather.setHeight((int) (height * heightconstant));
        locationbutton.setWidth((int)(height * 0.5));
        //LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int)(height*0.04),(int)(height * 0.04));
        //weatherImage.setLayoutParams(parms);
        userprofile.setOnClickListener(this);
        polygons= new ArrayList<>();
        mapMarkers = new ArrayList<>();
        mapFrag = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        mapFrag.setRetainInstance(true);
        initialize();
        return myView;
    }



    private void initialize(){
    main = MainScreenActivity.getInstance();
    activitites = main.controller.getActivities();
    String units = LoadPreferences("unit");
    if(units.equals("")){
        SavePreferences("unit","Metric");
    }
    //2109 fahrenheit 2103 celcius
    weather.setText(R.string.loadingWeather);



    //weather.setText("10" + " \u2103");








    }

    @Override
    public void onPause() {
        super.onPause();

        removeMarkersFromMap();
        removePolygonsFromMap();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        //SavePreferences("CurrentCameraPosition",mGoogleMap.getCameraPosition().target.toString());
    }

    @Override
    public void onClick(View v) {
        if (v == fields){

            //new testsoilsampling().execute();
            this.getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.article_fragment
                            , new ListViewFragment())
                    .commit();




        }
        else if(v == userprofile){

            this.getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.article_fragment
                            , new UserFragment())
                    .commit();
        }
        else if(v == weather){

            this.getFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.article_fragment
                            , new WeatherFragment())
                    .commit();
        }
        else if(v == locationbutton){
            mGoogleMap.setMyLocationEnabled(true);
            removeMarkersFromMap();
            DrawPolygonsOnMap();
            locationlayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("OnConnected kaldt");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latLng = new LatLng(location.getLatitude(), location.getLongitude());


        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

    }


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mGoogleMap=googleMap;
        String map = LoadPreferences("Map");
        if(map.equals("Normal")){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(map.equals("Sattelite")){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if(map.equals("Hybrid")){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }

        polygons.clear();
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                DrawPolygonsOnMap();
                if(!fieldPresent){
                    mGoogleMap.setMyLocationEnabled(true);
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
                else{

                        //Hvis mapfragment kaldes fra fieldList
                    System.out.println("Størrelse " + polygons.size());
                        for(int i = 0; i<polygons.size();i++){
                            if(polygons.get(i).getPoints().get(0).equals(field.getCoordinates().getCoordinates().get(0))){
                                polygons.get(i).setStrokeColor(Color.argb(255,255,51,51));
                                polygons.get(i).setFillColor(Color.argb(160,255,51,51));
                                System.out.println("Polygon fundet");
                            }
                        }

                        LatLng temp = new LatLng(field.getCenterpoint().getCoordinates().get(0).latitude-0.0020,field.getCenterpoint().getCoordinates().get(0).longitude);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp,16));
                        List<Activity> tempList = sortActivities(field);
                        infoFieldName.setText(field.getDisplay_name());
                        infoFieldSurface.setText(field.getSurface() +"ha");
                        if(!tempList.get(0).getUrl().equals("")){
                            Picasso.get().load(tempList.get(0).getUrl()).into(imgx);
                        }
                        else {
                            imgx.setImageDrawable(getContext().getDrawable(R.drawable.questionmark));
                        }
                        if(!tempList.get(1).getUrl().equals("")){
                            Picasso.get().load(tempList.get(1).getUrl()).into(imgy);
                        }
                        else{
                            imgy.setImageDrawable(getContext().getDrawable(R.drawable.questionmark));
                        }
                        productx.setText(getCropName(tempList.get(0).getCrop_id()));
                        producty.setText(getCropName(tempList.get(1).getCrop_id()));
                        activitytypex.setText(tempList.get(0).getActivityType());
                        activitytypey.setText(tempList.get(1).getActivityType());
                        infoLayout.setVisibility(View.VISIBLE);




                }


            } else {
                //Request Location Permission

                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            mGoogleMap.setMyLocationEnabled(false);






        }


            //opdaterVejr(mGoogleMap.getCameraPosition().target);
        //new UpdateWeather().execute(mGoogleMap.getCameraPosition().target);




        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
               clearPolygonColors();
               infoLayout.setVisibility(View.INVISIBLE);
               locationlayout.setVisibility(View.VISIBLE);


            }
        });
        //Hvis brugeren rykker kortet rundt
        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(i == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){

                    mGoogleMap.setMyLocationEnabled(false);
                    infoLayout.setVisibility(View.INVISIBLE);
                    locationlayout.setVisibility(View.VISIBLE);
                    clearPolygonColors();
                }

            }
        });
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                new UpdateWeather().execute(mGoogleMap.getCameraPosition().target);

                if(mGoogleMap.getCameraPosition().zoom < 12.5){

                    if(mapMarkers.isEmpty()){
                        DrawMarkersOnMap();


                    }

                }
                else if(mGoogleMap.getCameraPosition().zoom >12.6){
                    if(mapMarkers.size()>0){
                        removeMarkersFromMap();
                    }

                }
            }
        });



        mGoogleMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                Field field = getRightField(polygon.getPoints());
                List<Activity> tempList = sortActivities(field);
                clearPolygonColors();
                locationlayout.setVisibility(View.INVISIBLE);
                polygon.setStrokeColor(Color.argb(255,255,51,51));
                polygon.setFillColor(Color.argb(160,255,51,51));
                mGoogleMap.setMyLocationEnabled(false);
                LatLng temp = new LatLng(field.getCenterpoint().getCoordinates().get(0).latitude-0.0020,field.getCenterpoint().getCoordinates().get(0).longitude);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(temp,16));
                infoFieldName.setText(field.getDisplay_name());
                infoFieldSurface.setText(field.getSurface() +"ha");
                if(!tempList.get(0).getUrl().equals("")){
                    Picasso.get().load(tempList.get(0).getUrl()).into(imgx);
                }
                else {
                    imgx.setImageDrawable(getContext().getDrawable(R.drawable.questionmark));
                }
                if(!tempList.get(1).getUrl().equals("")){
                    Picasso.get().load(tempList.get(1).getUrl()).into(imgy);
                }
                else{
                    imgy.setImageDrawable(getContext().getDrawable(R.drawable.questionmark));
                }
                productx.setText(getCropName(tempList.get(0).getCrop_id()));
                producty.setText(getCropName(tempList.get(1).getCrop_id()));
                activitytypex.setText(tempList.get(0).getActivityType());
                activitytypey.setText(tempList.get(1).getActivityType());
                infoLayout.setVisibility(View.VISIBLE);




                //Toast.makeText(getActivity(), "Polygon: " + field.getDisplay_name(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private List<Activity> sortActivities(Field field){
        List<Activity> temp = new ArrayList<>();
        for(int i = 0; i<activitites.size();i++){
            if(activitites.get(i).getField_id() == field.getId()){
                temp.add(activitites.get(i));
            }
        }
        return temp;
    }

    private String getCropName(int id){
        for (int i = 0; i<main.controller.cropsList.size();i++){
            if(main.controller.cropsList.get(i).getCrop_id() == id){
                return main.controller.cropsList.get(i).getName();
            }
        }
        return "N/A";
    }

    private void DrawPolygonsOnMap(){
        System.out.println("DrawPolygons Kaldt, tegner " + main.controller.getUser().fields.size());
        for(int i = 0; i<main.controller.getUser().getFields().size();i++){
            Polygon polygon = mGoogleMap.addPolygon(DrawPolygon(main.controller.getUser().getFields().get(i)));

            polygon.setStrokeColor(Color.argb(255,0,128,255));
            polygon.setFillColor(Color.argb(160,0,128,255));
            polygon.setClickable(true);
            polygons.add(polygon);

            //System.out.println("Polygon "+polygon.getPoints());

        }
    }

    public void DrawMarkersOnMap()

    {

if(main.controller.getFields().size()> mapMarkers.size()){
    for(int i = 0; i<main.controller.getUser().getFields().size();i++){
        Field field = main.controller.getUser().getFields().get(i);
        MarkerOptions markeroptions = new MarkerOptions()
                .position(field.getCenterpoint().getCoordinates().get(0))
                .title(field.getDisplay_name());

        Marker marker = mGoogleMap.addMarker(markeroptions);
        mapMarkers.add(marker);




    }
}



    }

    private class testsoilsampling extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                main.controller.createSoilSampling();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnirestException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class UpdateWeather extends AsyncTask<LatLng, Void, Void> {

        String unit = LoadPreferences("unit");

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

                while(true) {
                    if (main.controller.weatherList.size() > 0) {

                        if (unit.equals("Metric")) {
                            weather.setText(main.controller.weatherList.get(0).t_2m_C + "\u2103");
                            weatherImage.setImageDrawable(main.controller.weatherList.get(0).getWeatherSymbol(getContext()));
                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int)(height*0.035),(int)(height * 0.035));
                            weatherImage.setLayoutParams(parms);
                        } else if (unit.equals("Imperial")) {
                            weather.setText(main.controller.weatherList.get(0).t_2m_C + "\u2109");
                            weatherImage.setImageDrawable(main.controller.weatherList.get(0).getWeatherSymbol(getContext()));
                            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams((int)(height*0.035),(int)(height * 0.035));
                            weatherImage.setLayoutParams(parms);
                        }
                        firstUpdate = true;
                        break;
                    }
                }



        }

        @Override
        protected Void doInBackground(LatLng... latLngs) {
            LatLng location = latLngs[0];





            if(unit.equals("Metric")){
                main.controller.getWeather(location,"mix",getCurrentTime("yyyy-MM-ddTHH:mm"),0,1, Arrays.asList(getResources().getStringArray(R.array.Metric)));
            }
            else if(unit.equals("Imperial")){
                main.controller.getWeather(location,"mix",getCurrentTime("yyyy-MM-ddTHH:mm"),0,1, Arrays.asList(getResources().getStringArray(R.array.Imperial)));
            }



            return null;
        }
    }


    public void removeMarkersFromMap(){
        System.out.println("MapMarkers størrelse: " + mapMarkers.size());
        for(int i = 0; i<mapMarkers.size(); i++){
            mapMarkers.get(i).remove();
        }
        mapMarkers.clear();
        System.out.println("MapMarkers størrelse: " + mapMarkers.size());
    }

    private void removePolygonsFromMap(){
        for (int i = 0; i < polygons.size(); i++){
            //System.out.println("Polygon fjernet: " + polygons.get(i).getId());
            polygons.get(i).remove();




        }
        polygons.clear();
    }

    private String getCurrentTime(String format){


        String date = android.text.format.DateFormat.format(format, new java.util.Date()).toString();
        return date;
    }


    private void clearPolygonColors(){
        for (int i = 0; i < polygons.size(); i++){

            polygons.get(i).setStrokeColor(Color.argb(255,0,128,255));
            polygons.get(i).setFillColor(Color.argb(160,0,128,255));


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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("OnRequestPermissionResult kaldt");

        buildGoogleApiClient();
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        request = true;
    }

    private String LoadPreferences(String key) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String result = sharedPreferences.getString(key, "");
        return result;

    }

    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //Tilføj flyt kamera efter accept
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getActivity())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                requestPermissions(
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );

            }
        }


    }


    private Field getRightField(List<LatLng> points){
        Field field = null;
        List<LatLng> newList = points;
        newList.remove(points.size()-1);

        for(int i = 0; i<main.controller.getUser().getFields().size();i++){
            if(newList.equals(main.controller.getUser().getFields().get(i).getCoordinates().getCoordinates())){
                System.out.println("Korrekt field fundet!");
                field = main.controller.getUser().getFields().get(i);
                return field;
            }
            else{
                System.out.println("Field ikke fundet på bruger");

            }
        }

        return null;
    }

    private PolygonOptions DrawPolygon(Field field){
        PolygonOptions rectOptions = new PolygonOptions();
        for(int i = 0; i<field.getCoordinates().getNumOfCoordinates(); i++){
            rectOptions.add(field.getCoordinates().getCoordinates().get(i));

        }
        rectOptions.add(field.getCoordinates().getCoordinates().get(0));
        rectOptions.strokeColor(Color.argb(255,0,128,255));
        rectOptions.fillColor(Color.argb(160,0,128,255));

        return rectOptions;
    }

}
