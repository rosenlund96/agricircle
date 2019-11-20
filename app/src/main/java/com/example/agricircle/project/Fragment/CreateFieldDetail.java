package com.example.agricircle.project.Fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.R;
import com.example.agricircle.project.Activities.DrawNewFieldActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class CreateFieldDetail extends Fragment implements OnMapReadyCallback {
    private View myView;
    private DrawNewFieldActivity main;
    private Polygon polygon;
    private TextView toptext;

    private GoogleMap mMap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.create_new_field_detail, container, false);
        main = DrawNewFieldActivity.getInstance();
        polygon = main.polygon;
        toptext = myView.findViewById(R.id.surfacetext);
        final SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        return myView;
    }

    private void init(){
        if(polygon != null){
            toptext.setText("Area: "+ getPolygonArea(polygon));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(getPolygonCenterPoint(polygon), 15));
            drawPolygon(polygon);

        }
    }

    private String  getPolygonArea(Polygon polygon){
        DecimalFormat df2 = new DecimalFormat("#.##");
        double area = SphericalUtil.computeArea(polygon.getPoints());
        return df2.format(area/10000)+"ha";
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
