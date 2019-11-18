package com.example.agricircle.project.Fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.project.Activities.MainScreenActivity;
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


import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import java.util.Random;

public class WeatherFragment extends Fragment implements TabLayout.OnTabSelectedListener{
   Button weather1, weather2, weather3;
   TabLayout tabs;
   TextView offlineText;
   View myView;
   CombinedChart mChart;
   Random r;
   ArrayList<Weather> vejrData;
   MainScreenActivity main;
    List<String> parameters;
    private final int count = 12;
    String selectedInterval = "";



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        myView = inflater.inflate(R.layout.weather_fragment, container, false);
        tabs = (TabLayout) myView.findViewById(R.id.tabLayout);
        tabs.setOnTabSelectedListener(this);
        mChart =(CombinedChart) myView.findViewById(R.id.weatherchart);
        offlineText = myView.findViewById(R.id.offlinetext);
        vejrData = new ArrayList<>();
        r = new Random();
        mChart.getDescription().setEnabled(false);
        main = MainScreenActivity.getInstance();
        selectedInterval = "";
        tabs.addTab(tabs.newTab().setText(R.string.today));
        tabs.addTab(tabs.newTab().setText(R.string.fivedays));
        tabs.addTab(tabs.newTab().setText(R.string.fourteendays));






        return myView;
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
                        if(selectedInterval.equals("time")){
                            String oldDate = vejrData.get((int) value).date;
                            String[] separated = oldDate.split("T");

                            return separated[1].substring(0,5);
                        }

                        String oldDate = vejrData.get((int) value).date;
                        String[] separated = oldDate.split("T");

                        String date = separated[0].substring(9,11);
                        String month = separated[0].substring(6,8);
                        String time = separated[1].substring(0,5);

                        return ""+date+"/"+month+"-"+time;
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
            mChart.setBackgroundColor(Color.WHITE);
            mChart.setDrawGridBackground(false);
            mChart.setDrawBarShadow(false);
            mChart.setHighlightFullBarEnabled(false);
            mChart.getAxisRight().setDrawLabels(false);
            mChart.animateX(500);
            XAxis xAxis = mChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

            LatLng temp = new LatLng(55.731659,12.363490);
            opdaterVejr(temp, hoursrange,hoursinterval, elementer);
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

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
