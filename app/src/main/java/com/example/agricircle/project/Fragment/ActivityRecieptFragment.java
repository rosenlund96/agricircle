package com.example.agricircle.project.Fragment;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.github.badoualy.datepicker.DatePickerTimeline;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ActivityRecieptFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback {

    private View myView;
    private Button save;
    private Activity activity;
    private TextView fieldName, BBCHText, cropname, activityType, executor, duedate;
    private EditText commentBox;
    private ImageView BBCHImage, cropImage;
    private MainScreenActivity main;
    private DatePickerTimeline date;
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Kvitteringsside vises");
        myView = inflater.inflate(R.layout.activity_reciept_view, container, false);
        main = MainScreenActivity.getInstance();
        save = myView.findViewById(R.id.logButton);
        save.setOnClickListener(this);
        fieldName = myView.findViewById(R.id.fieldNameReciept);
        BBCHText = myView.findViewById(R.id.BBCHText);
        BBCHImage = myView.findViewById(R.id.BBCHImage);
        cropname = myView.findViewById(R.id.cropNameReciept);
        cropImage = myView.findViewById(R.id.cropImageReciept);
        activityType = myView.findViewById(R.id.activityTypeReciept);
        executor = myView.findViewById(R.id.activityExecutor);
        commentBox = myView.findViewById(R.id.commentBox);
        duedate = myView.findViewById(R.id.duedatetext);
        date = myView.findViewById(R.id.datepicker);
        date.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                duedate.setText("Due date: " + day+"/"+(month+1)+"/"+year);
            }
        });

        Bundle bundle = getArguments();
        try{
            Activity obj = (Activity) bundle.getSerializable("Activity");
            if(obj != null){
                activity = obj;



            }
        }catch (Exception e){
            System.out.println("Intet felt sendt med");


        }
        if(activity != null){
            setInformations();
        }


        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.activityRecieptMap);
        mapFragment.getMapAsync(this);
        return myView;
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


    public void setInformations(){
        System.out.println("Activityid: " + activity.getActivity_id());
        fieldName.setText(activity.getFieldname());

        BBCHText.setText("BBCH0");
        Picasso.get().load("https://core.agricircle.com/static/images/ac-vorsaat-nachsaat.png").into(BBCHImage);
        if(activity.getBBCHname() != null){
            BBCHText.setText("BBCH"+activity.getBBCHname());
            Picasso.get().load(activity.getBBCHImage()).into(BBCHImage);
        }
        if(!activity.getComment().isEmpty()){
            commentBox.setText(activity.getComment());
        }
        System.out.println("Executor sættes til: " + activity.getExecutor());
        executor.setText(activity.getExecutor());
        System.out.println("Sætter information med BBCH: " + activity.getBBCHname());

        cropname.setText(getCropName(activity.getCrop_id()));
        activityType.setText(activity.getActivityType());
    }


    private String getCropName(int id){
        String temp = "";
        String url = "";
        List<Crop> crops = MainScreenActivity.getInstance().controller.getUser().cropsList;
        for(int i = 0; i<crops.size(); i++){
            if(crops.get(i).getCrop_id() == id){
                temp = crops.get(i).getName();
                url = crops.get(i).getPhoto_url();
            }
        }

        Picasso.get().load(url).into(cropImage);
        return temp;
    }

    public Field getRightField(int fieldID){
        List<Field> fields = MainScreenActivity.getInstance().controller.user.getFields();
        Field field = null;
        for(int i = 0; i<fields.size();i++){
            if(fields.get(i).getId() == fieldID){
                field = fields.get(i);
            }

        }
        return field;
    }





    @Override
    public void onClick(View v) {
        if(activity.getCameFromMap()){
            activity.setCameFromMap(false);
            main.controller.saveCurrentActivity(activity);
            FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            MapFragment fragment2 = new MapFragment();

            Bundle bundle = new Bundle();
            Field obj = getRightField(activity.getField_id());
            bundle.putSerializable("Field", obj);
            fragment2.setArguments(bundle);
            ft.replace(R.id.article_fragment, fragment2);
            ft.addToBackStack(null);
            ft.commit();
        }
        else{

            FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ListViewFragment fragment2 = new ListViewFragment();
            ft.replace(R.id.article_fragment, fragment2);
            ft.addToBackStack(null);
            ft.commit();
        }

    }

    private String LoadPreferences(String key) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String result = sharedPreferences.getString(key, "");
        return result;

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(field.getCenterpoint().getCoordinates().get(0), 15.3F));
        if(activity.getPath().size() >0){
            for(int i = 1; i<activity.getPath().size();i++){
                Polyline polyline = mMap.addPolyline(new PolylineOptions()
                        .clickable(true)
                        .width(2)
                        .color(Color.argb(255,255,0,0))
                        .add(activity.getPath().get(i-1),activity.getPath().get(i)

                        ));
            }

        }
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
}
