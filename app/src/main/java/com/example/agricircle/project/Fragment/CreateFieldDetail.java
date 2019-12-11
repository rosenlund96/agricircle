package com.example.agricircle.project.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.R;
import com.example.agricircle.project.Activities.DrawNewFieldActivity;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Entities.Shape;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class CreateFieldDetail extends Fragment implements OnMapReadyCallback, View.OnClickListener {
    private View myView;
    private DrawNewFieldActivity main;
    private Polygon polygon;
    private EditText fieldName;
    private TextView toptext;
    private Button createField;
    private MainScreenActivity mainController;
    private GoogleMap mMap;
    AutoCompleteTextView cropSearch;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.create_new_field_detail, container, false);
        main = DrawNewFieldActivity.getInstance();
        polygon = main.polygon;
        fieldName = myView.findViewById(R.id.fieldNameCreate);
        toptext = myView.findViewById(R.id.surfacetext);
        createField = myView.findViewById(R.id.createfieldbutton);
        createField.setOnClickListener(this);
        mainController = MainScreenActivity.getInstance();

        cropSearch = (AutoCompleteTextView)
                myView.findViewById(R.id.autocompleteCrop);
        cropSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                View v = getActivity().getCurrentFocus();

                if (v != null) {

                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                }
            }
        });
        new getAllCrops().execute("");
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        return myView;
    }

    private void init(){
        if(polygon != null){
            toptext.setText("Area: "+ getPolygonArea(polygon)+"ha");

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getPolygonCenterPoint(polygon), 15));
            drawPolygon(polygon);

        }
    }

    @Override
    public void onClick(View v) {
        System.out.println("Størrelse: " + mainController.controller.getUser().fields.size());
        Crop crop = getCorrectCrop(cropSearch.getText().toString());
        System.out.println("Korrekt crop er: " + crop.getName());
        Shape newPolygon = new Shape(null,polygon.getPoints());
        List<LatLng> center = new ArrayList<>();
        center.add(getPolygonCenterPoint(polygon));
        Shape centerPoint = new Shape(null, center);
        String area = getPolygonArea(polygon);
        Double surface = Double.valueOf(area);
        Field newField = new Field(0,fieldName.getText().toString(),null,newPolygon,surface,fieldName.getText().toString(),false, centerPoint,crop.getCrop_id());
        newField.setCreatedLocally(true);
        mainController.controller.getUser().fields.add(newField);

        System.out.println("Størrelse: " + mainController.controller.getUser().fields.size());

        Gson gson = new Gson();

        String json = gson.toJson(mainController.controller.getUser());
        SavePreferences("User",json);



        Intent intent = new Intent(this.getContext(), MainScreenActivity.class);
        startActivity(intent);



    }
    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private boolean checkNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }



    public Crop getCorrectCrop(String name){
        Crop crop = null;
        for(int i= 0; i<mainController.controller.allCropsAsObject.size();i++){
            if(mainController.controller.allCropsAsObject.get(i).getName().equals(name)){
                crop = mainController.controller.allCropsAsObject.get(i);
            }
        }
        return crop;
    }


    private class getAllCrops extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            mainController.controller.getAllCrops(strings[0]);
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            while(true){
                if(mainController.controller.allCropsFinished){
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_dropdown_item_1line, mainController.controller.allCropsList);
                    System.out.println("Adaptor sættes med " + mainController.controller.allCropsList.size() + " elementer");
                    cropSearch.setAdapter(adapter);
                    break;
                }
            }
        }
    }

    private String  getPolygonArea(Polygon polygon){
        DecimalFormat df2 = new DecimalFormat("#.##");
        double area = SphericalUtil.computeArea(polygon.getPoints());
        return df2.format(area/10000);
    }

    private void drawPolygon(Polygon polygon){
        PolygonOptions rectOptions = new PolygonOptions();
        for(int i = 0; i<polygon.getPoints().size(); i++){
            rectOptions.add(polygon.getPoints().get(i));

        }

        rectOptions.strokeColor(Color.argb(255,0,128,255));
        rectOptions.fillColor(Color.argb(160,0,128,255));

        mMap.addPolygon(rectOptions);
        polygon.setStrokeColor(Color.argb(255,0,128,255));
        polygon.setFillColor(Color.argb(160,0,128,255));
        polygon.setClickable(false);
    }



    private LatLng getPolygonCenterPoint(Polygon polygon){
        LatLng centerLatLng = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i = 0 ; i < polygon.getPoints().size() ; i++)
        {
            builder.include(polygon.getPoints().get(i));
        }
        LatLngBounds bounds = builder.build();
        centerLatLng =  bounds.getCenter();

        return centerLatLng;
    }

    private String LoadPreferences(String key) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String result = sharedPreferences.getString(key, "");
        return result;

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
