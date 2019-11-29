package com.example.agricircle.project.Fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Entities.Weather;
import com.example.agricircle.R;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.ValueFormatter;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import java.util.Random;

public class WeatherFragment extends Fragment implements TabLayout.OnTabSelectedListener{
   Button weather1, weather2, weather3;
   TabLayout tabs;
   TextView offlineText, currentTemp;
   ImageView weatherIcon;
   View myView;
   CombinedChart mChart;
   Random r;
   ArrayList<Weather> vejrData;
   MainScreenActivity main;
    List<String> parameters;
    private final int count = 12;
    String selectedInterval = "";
    MaterialBetterSpinner locationChooser;
    List<String> fields;
    LatLng location, currentLocation;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.weather_fragment, container, false);
        fields = new ArrayList<>();
        main = MainScreenActivity.getInstance();


        tabs = (TabLayout) myView.findViewById(R.id.tabLayout);
        tabs.setOnTabSelectedListener(this);
        mChart =(CombinedChart) myView.findViewById(R.id.weatherchart);
        offlineText = myView.findViewById(R.id.offlinetext);
        vejrData = new ArrayList<>();
        r = new Random();
        currentTemp = myView.findViewById(R.id.currentTemp);
        weatherIcon = myView.findViewById(R.id.currentWeatherImg);
        mChart.getDescription().setEnabled(false);
        mChart.setBackgroundColor(Color.TRANSPARENT);
        mChart.setDrawGridBackground(false);

        selectedInterval = "time";
        location = null;
        tabs.addTab(tabs.newTab().setText(R.string.today));
        tabs.addTab(tabs.newTab().setText(R.string.fivedays));
        tabs.addTab(tabs.newTab().setText(R.string.fourteendays));
        Bundle bundle = getArguments();
        try{
            String obj= (String) bundle.getSerializable("Location");
            if(obj != null){
                String[] latlong =  obj.split(",");
                double latitude = Double.parseDouble(latlong[0]);
                double longitude = Double.parseDouble(latlong[1]);
                currentLocation = new LatLng(latitude, longitude);
                location = currentLocation;
                System.out.println("Lokation hentet" + location);
                updateWeather(24,2, (24/2));

            }
        }catch (Exception e){

            System.out.println("Ingen lokation sendt med " + e);


        }







        locationChooser = myView.findViewById(R.id.locationChooser);

        fields.add(getContext().getString(R.string.myLocation));
        for(int i = 0; i<main.controller.getUser().fields.size();i++){
            fields.add(main.controller.getUser().fields.get(i).getDisplay_name());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, fields);
        locationChooser.setAdapter(arrayAdapter);
        locationChooser.setFocusable(true);
        locationChooser.setText(getContext().getString(R.string.myLocation));
        locationChooser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                location = getLocation(locationChooser.getText().toString());
                if(tabs.getSelectedTabPosition() == 0){
                    selectedInterval = "time";
                    updateWeather(24,2, (24/2));
                }
                if(tabs.getSelectedTabPosition() == 1){
                    selectedInterval = "date";
                    updateWeather(120, 8,(120/8));
                }
                if(tabs.getSelectedTabPosition() == 2){
                    selectedInterval = "date";
                    updateWeather(336,15,(336/15));
                }




            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        /*--------------------------------------*
         *--Status ID-er for vejrikoner       --*
         *--1 ->Clear sky                     --*
         *--2 ->Light clouds                  --*
         *--3 ->Partly cloudy                 --*
         *--4 ->Cloudy                        --*
         *--5 ->Rain                          --*
         *--6 ->Rain and snow / sleet         --*
         *--7 ->Snow                          --*
         *--8 ->Rain shower                   --*
         *--9 ->Snow shower                   --*
         *--10 ->Sleet shower                 --*
         *--11 ->Light Fog                    --*
         *--12 ->Dense fog                    --*
         *--13 ->Freezing rain                --*
         *--14 ->Thunderstorms                --*
         *--15 ->Drizzle                      --*
         *--16 ->Sandstorm                    --*
         *--101 ->Clear sky(Night)            --*
         *--102 ->Light clouds(Night)         --*
         *--103 ->Partly cloudy(Night)        --*
         *--104 ->Cloudy(Night)               --*
         *--104 ->Rain(Night)                 --*
         *--106 ->Rain and snow / sleet(Night)--*
         *--107 ->Snow(Night)                 --*
         *--108 ->Rain shower(Night)          --*
         *--109 ->Snow shower(Night)          --*
         *--110 ->Sleet shower(Night)         --*
         *--111 ->Light Fog(Night)            --*
         *--112 ->Dense fog(Night)            --*
         *--113 ->Freezing rain(Night)        --*
         *--114 ->Thunderstorms(Night)        --*
         *--115 ->Drizzle(Night)              --*
         *--116 ->Sandstorm(Night)            --*
         *--------------------------------------*/






        return myView;
    }

    private LatLng getLocation(String name){
        LatLng temp = null;
        if(!locationChooser.getText().toString().equals(getContext().getString(R.string.myLocation))){
            for(int i = 0; i<main.controller.user.fields.size();i++){
                if(main.controller.user.fields.get(i).getDisplay_name().equals(name)){
                    temp = main.controller.user.getFields().get(i).getCenterpoint().getCoordinates().get(0);
                }
            }
        }
        else{
            temp = currentLocation;
        }
        return temp;

    }
    private String LoadPreferences(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String result = sharedPreferences.getString(key, "");
        return result;

    }

    public void opdaterVejr(LatLng lokation, int hoursrange, int hoursinterval, int elementer){
        String unit = LoadPreferences("unit");
        System.out.println(unit);
        if(unit.equals("Metric")){
            main.controller.getWeather(lokation,"mix",getCurrentTime(),hoursrange,hoursinterval, Arrays.asList(getResources().getStringArray(R.array.Metric)));
        }
        else if(unit.equals("Imperial")){
            main.controller.getWeather(lokation,"mix",getCurrentTime(),hoursrange,hoursinterval, Arrays.asList(getResources().getStringArray(R.array.Imperial)));
        }

        while(true){
            if(main.controller.weatherList.size() > elementer){
                vejrData = main.controller.weatherList;
                if (unit.equals("Metric")) {
                    currentTemp.setText(vejrData.get(0).t_2m_C + "\u2103");
                }
                else if(unit.equals("Imperial")){
                    currentTemp.setText(vejrData.get(0).t_2m_C + "\u2109");
                }
                weatherIcon.setImageDrawable(vejrData.get(0).getWeatherSymbol(getContext()));
                System.out.println("Printer " + main.controller.weatherList.size() + " elementer");

                // draw bars behind lines
                mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                        CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
                });

                Legend l = mChart.getLegend();
                l.setWordWrapEnabled(true);
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(false);



                YAxis rightAxis = mChart.getAxisRight();
                rightAxis.setDrawGridLines(false);
                rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                YAxis leftAxis = mChart.getAxisLeft();
                leftAxis.setDrawGridLines(false);
                leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                XAxis xAxis = mChart.getXAxis();

                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        String oldDate = vejrData.get((int) value).date;
                        String[] separated = oldDate.split("T");
                        String returnval;
                        if(selectedInterval.equals("time")){


                            returnval = separated[1].substring(0,5);
                        }
                        else{
                            String date = separated[0].substring(9,11);
                            String month = separated[0].substring(6,8);
                            String time = separated[1].substring(0,5);
                            returnval = ""+date+"/"+month+"-"+time;
                        }





                        return returnval;
                    }
                });
                xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
                xAxis.setAxisMinimum(0f);
                xAxis.setGranularity(1f);
                CombinedData data = new CombinedData();

                LineData d = new LineData();
                d.addDataSet(generateCurrTempData());
                d.addDataSet(generateMinTempData());
                d.addDataSet(generateMaxTempData());
                data.setData(d);
                data.setData(generateRainData());


                //data.setData(generateCandleData());


                xAxis.setAxisMaximum(data.getXMax() + 0.25f);

                mChart.setData(data);
                mChart.invalidate();
                //System.out.println("Vejrdata hentet - Temp: " + main.controller.weatherItem.t_2m_C );
                //weather.setText(main.controller.weatherItem.t_2m_C+"\u2103");
                break;
            }
        }

    }



    public String getCurrentTime(){


        String date = android.text.format.DateFormat.format("yyyy-MM-ddTHH:mm", new java.util.Date()).toString();
        return date;
    }

    public void updateWeather(int hoursrange, int hoursinterval, int elementer){

        if(main.controller.checkInternetConnection(getContext())){
            mChart.setVisibility(View.VISIBLE);
            offlineText.setVisibility(View.INVISIBLE);
            mChart.clear();
            mChart.setTouchEnabled(true);

            mChart.getDescription().setEnabled(false);
            mChart.setDrawGridBackground(false);
            mChart.setDrawBarShadow(false);
            mChart.setHighlightFullBarEnabled(false);
            mChart.getAxisRight().setDrawLabels(false);
            mChart.animateX(500);
            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            //LatLng temp = new LatLng(55.731659,12.363490);
            opdaterVejr(location, hoursrange,hoursinterval, elementer);
        }
        else{
            mChart.setVisibility(View.INVISIBLE);
            offlineText.setVisibility(View.VISIBLE);
        }








    }
    private LineDataSet generateMinTempData() {



        ArrayList<Entry> entries = new ArrayList<>();


        for (int index = 0; index < vejrData.size(); index++){
            entries.add(new Entry(index + 0.0f , (float) (vejrData.get(index).t_min_2m_3h_C)));

        }


        LineDataSet set = new LineDataSet(entries, "Min");
        set.setColor(Color.rgb(0, 40, 240));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(0, 40, 240));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(0, 40, 240));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(0, 40, 240));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);


        return set;
    }
    private LineDataSet generateMaxTempData() {



        ArrayList<Entry> entries = new ArrayList<>();


        for (int index = 0; index < vejrData.size(); index++){
            entries.add(new Entry(index + 0.0f , (float) (vejrData.get(index).t_max_2m_3h_C)));

        }


        LineDataSet set = new LineDataSet(entries, "Max");
        set.setColor(Color.rgb(240, 40, 0));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 40, 0));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 40, 0));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 40, 0));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);


        return set;
    }
    private LineDataSet generateCurrTempData() {



        ArrayList<Entry> entries = new ArrayList<>();


        for (int index = 0; index < vejrData.size(); index++){
            entries.add(new Entry(index + 0.0f , (float) (vejrData.get(index).t_2m_C)));

        }


        LineDataSet set = new LineDataSet(entries, "Temp");
        set.setColor(Color.rgb(240, 238, 70));
        set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(240, 238, 70));
        set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(240, 238, 70));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(240, 238, 70));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);


        return set;
    }

    private BarData generateRainData() {

        ArrayList<BarEntry> entries1 = new ArrayList<>();


        for (int index = 0; index < vejrData.size(); index++) {
            entries1.add(new BarEntry(index, (float) vejrData.get(index).precip_6h_mm));


        }

        BarDataSet set1 = new BarDataSet(entries1, "Nedbør");
        set1.setColor(Color.rgb(60, 220, 78));
        set1.setValueTextColor(Color.rgb(60, 220, 78));
        set1.setValueTextSize(10f);
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);




        float barWidth = 0.45f; // x2 dataset
        // (0.45 + 0.02) * 2 + 0.06 = 1.00 -> interval per "group"

        BarData d = new BarData(set1);
        d.setBarWidth(barWidth);



        return d;
    }

    public void setActiveButton(View v){
        weather1.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        weather2.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        weather3.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        v.setBackgroundColor(getResources().getColor(R.color.colorAccent));


    }



    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        if(location != null){
            if(tab.getText().toString().equals(getResources().getString(R.string.today))){
                selectedInterval = "time";
                updateWeather(24,2, (24/2));
            }
            if(tab.getText().toString().equals(getResources().getString(R.string.fivedays))){
                selectedInterval = "date";
                updateWeather(120, 8,(120/8));
            }
            if(tab.getText().toString().equals(getResources().getString(R.string.fourteendays))){
                selectedInterval = "date";
                updateWeather(336,15,(336/15));
            }
        }






    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
