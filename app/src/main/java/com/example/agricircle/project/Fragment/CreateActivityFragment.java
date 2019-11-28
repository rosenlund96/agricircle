package com.example.agricircle.project.Fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.Activities.type.CropActivityProductType;
import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Entities.Worker;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateActivityFragment extends Fragment implements View.OnClickListener {
    MainScreenActivity main;
    int currentBBCH;
    private View myView;
    MaterialBetterSpinner fieldChooser, cropChooser, activityChooser, categoryChooser, productChooser, executorChooser;
    Button prev,next;
    TextView info, BBCHText;
    ImageView cropImg;
    List<String> crops, executors;
    List<Worker> workers;
    Field temp;
    LinearLayout cropImgLayout;
    public final String getColleaguesURL= "https://core.agricircle.com/api/v1/filters/options?months=false&colleagues=true&logbook=false&workspace=false";
    int id;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.create_activity, container, false);
        workers = new ArrayList<>();
        executors = new ArrayList<>();
        cropImgLayout = myView.findViewById(R.id.cropimglayout);
        cropImg = myView.findViewById(R.id.cropStageImage);
        BBCHText = myView.findViewById(R.id.BBCHText);
        BBCHText.setText("");
        next = myView.findViewById(R.id.nextButton);
        prev = myView.findViewById(R.id.prevButton);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        fieldChooser = myView.findViewById(R.id.choosefield);
        cropChooser = myView.findViewById(R.id.choosecrop);
        activityChooser = myView.findViewById(R.id.chooseactivitytype);
        categoryChooser = myView.findViewById(R.id.chooseactivitycategory);
        productChooser = myView.findViewById(R.id.chooseactivityproduct);
        executorChooser = myView.findViewById(R.id.chooseExecutor);

        progressDialog = new ProgressDialog(getContext(),
                R.style.AppCompatAlertDialogStyle);
        main = MainScreenActivity.getInstance();
        new JsonTask().execute(getColleaguesURL);
        info = myView.findViewById(R.id.infotextactivity);
        List<String> fields = new ArrayList<>();
        crops = new ArrayList<>();
        temp = null;
        cropChooser.setFocusable(false);
        activityChooser.setFocusable(false);
        categoryChooser.setFocusable(false);
        productChooser.setFocusable(false);
        cropChooser.setVisibility(View.GONE);
        activityChooser.setVisibility(View.GONE);
        categoryChooser.setVisibility(View.GONE);
        cropImgLayout.setVisibility(View.GONE);
        productChooser.setVisibility(View.GONE);

        for(int i = 0; i<main.controller.getUser().fields.size();i++){
            fields.add(main.controller.getUser().fields.get(i).getDisplay_name());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, fields);
        fieldChooser.setAdapter(arrayAdapter);
        fieldChooser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                cropChooser.setText("");
                activityChooser.setText("");
                categoryChooser.setText("");
                productChooser.setText("");
                cropChooser.setFocusable(false);
                activityChooser.setFocusable(false);
                categoryChooser.setFocusable(false);
                productChooser.setFocusable(false);
                executorChooser.setFocusable(false);
                activityChooser.setVisibility(View.GONE);
                categoryChooser.setVisibility(View.GONE);
                cropImgLayout.setVisibility(View.GONE);
                productChooser.setVisibility(View.GONE);


                if(!checkFieldActiveCrop(fieldChooser.getText().toString())){
                    fieldChooser.setError("Field doesnt have an active crop");
                    info.setText(getContext().getString(R.string.fieldhasnocrop));
                    info.setTextColor(Color.RED);

                }
                else{
                    info.setText(getContext().getString(R.string.createActivity));
                    info.setTextColor(Color.DKGRAY);
                    buildCropList();
                    cropChooser.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });


        cropChooser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activityChooser.setText("");
                categoryChooser.setText("");
                productChooser.setText("");
                activityChooser.setFocusable(false);
                categoryChooser.setFocusable(false);
                productChooser.setFocusable(false);

                categoryChooser.setVisibility(View.GONE);

                productChooser.setVisibility(View.GONE);
                if(!cropChooser.getText().toString().equals("")){


                id = getCropID(cropChooser.getText().toString());
                new getCropBBCHStages().execute(id);
                new getCropActivityTypes().execute(id);
                    cropImgLayout.setVisibility(View.VISIBLE);
                    activityChooser.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        activityChooser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryChooser.setText("");
                productChooser.setText("");
                categoryChooser.setFocusable(false);
                productChooser.setFocusable(false);
                if(!activityChooser.getText().toString().equals("")) {
                    if(categoryChooser.getText().toString().equals("harvest")){
                        System.out.println("Harvest");
                        buildProductList();
                    }
                    new getActivityCategories().execute(activityChooser.getText().toString());
                    if(!activityChooser.getText().toString().equals("irrigation")){
                        categoryChooser.setVisibility(View.VISIBLE);
                        productChooser.setVisibility(View.VISIBLE);
                    }


                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        categoryChooser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productChooser.setText("");
                productChooser.setFocusable(false);
                if(!categoryChooser.getText().toString().equals("")){

                   if(!activityChooser.getText().toString().equals("harvest")){
                       new getActivityProducts().execute(activityChooser.getText().toString(),categoryChooser.getText().toString(), cropChooser.getText().toString() );
                   }



                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        productChooser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return myView;
    }

    @Override
    public void onClick(View v) {
        if(v == next){
            setBBCH(currentBBCH,0);
        }
        else if(v == prev){
            setBBCH(currentBBCH,1);

        }
    }


    private class JsonTask extends AsyncTask<String, String, String> {

        HttpResponse<String> Httpresponse = null;


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            JsonParser parser = new JsonParser();
            JsonObject activityObject = (JsonObject) parser.parse(Httpresponse.getBody());
            JsonArray workerslist = activityObject.getAsJsonArray("workers");
            JsonArray colleagueslist = activityObject.getAsJsonArray("colleagues");

            System.out.println(workerslist.getAsJsonArray().get(0).getAsJsonObject().get("name"));
            System.out.println(workerslist.getAsJsonArray().get(0).getAsJsonObject().get("id"));
            System.out.println(workerslist.getAsJsonArray().get(0).getAsJsonObject().get("photo_url"));
            System.out.println(workerslist.getAsJsonArray().size());
            for(int i= 0; i<workerslist.getAsJsonArray().size();i++){
                String photo = workerslist.getAsJsonArray().get(i).getAsJsonObject().get("photo_url").toString();
                executors.add(workerslist.getAsJsonArray().get(i).getAsJsonObject().get("name").getAsString());
               workers.add(new Worker(workerslist.getAsJsonArray().get(i).getAsJsonObject().get("id").getAsInt(),workerslist.getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString(),photo));

            }
            for(int x= 0; x<colleagueslist.getAsJsonArray().size();x++){
                String photo = colleagueslist.getAsJsonArray().get(x).getAsJsonObject().get("photo_url").toString();
                executors.add(colleagueslist.getAsJsonArray().get(x).getAsJsonObject().get("name").getAsString());
                workers.add(new Worker(colleagueslist.getAsJsonArray().get(x).getAsJsonObject().get("id").getAsInt(),colleagueslist.getAsJsonArray().get(x).getAsJsonObject().get("name").getAsString(),photo));
            }

            buildExecutorList();



        }

        protected String doInBackground(String... params) {
            String cookie = "_ga=GA1.2.1650363209.1570101259; km_ai=1jFFflnLZzj%2FTeCwqgrcXFOwwzA%3D; km_lv=x; signed_in=1; _gid=GA1.2.34756113.1574855966; kvcd=1574856407466; token=eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoyNDQ1LCJ0b2tlbiI6bnVsbCwibG9jYWxlIjoiZW4iLCJleHAiOjE1NzQ4ODAyNzd9.VgrefxVTFqzdTJvAX9KOgpOKCTJ4FwyFqSmAKwlhzJk; _AgriCircle_subdomain_session=RURNVS9YTFE0VHowL1FWNEdZRldSdmZRcGNyQ2NBNEdKaEYzcnFmbXFOUnRqdUt1ajJQajArTDIxd3BjQnFZQ1BzUEU4YmhTWmZkQ09qeEptZjd6NkkrOGdvOE94T3FKOUdpcGxreXZGWnlXODUzVHZxam1aYXoxcFJ0RENSVmNNOFZwc3lSTkxUMDUxRHNMZzdCSWZhVEsxYlNQUmVKTHVzQ05OSXJ1a0dXRUU1Z2lKVU9IU0QwNGQ4MUttcTEyWVNFREY1eW1yWnlaYnZZenVlYmR2UjNjeFlYT1U5ZzhZdjJWSERwQnJnMD0tLTZwR2NhVmNzKzhoN3lzdXB6czBXdGc9PQ%3D%3D--1172c69ac85cbe748040c8e280a47a800967afac";

            try {
                HttpResponse<String> response = Unirest.get(getColleaguesURL)
                        .header("cookie", cookie)
                        .header("content-type", "application/json")
                        .asString();
                Httpresponse = response;
            } catch (UnirestException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

public void buildExecutorList(){
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
            android.R.layout.simple_dropdown_item_1line, executors );
    executorChooser.setAdapter(arrayAdapter);
    executorChooser.setFocusable(true);
}





    private class getCropBBCHStages extends AsyncTask<Integer, Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            main.controller.getBBCHStages(integers[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            while(true){
                if(!main.controller.BBCHList.isEmpty()){
                    Picasso.get().load(main.controller.BBCHList.get(0).getImage()).into(cropImg);
                    BBCHText.setText(getContext().getString(R.string.bbch01));
                    currentBBCH = main.controller.BBCHList.get(0).getBbchFrom();
                    break;
                }
            }

            progressDialog.dismiss();
        }
    }

    public void setBBCH(int current, int direction){
        int index = 0;
        int low = 0;
        int max = main.controller.BBCHList.size();
        for(int i= 0; i< main.controller.BBCHList.size();i++){
            if(main.controller.BBCHList.get(i).getBbchFrom() == current){
                index = i;
            }
        }


        if(direction == 0 && (index+1<=max )){
            System.out.println("Test: " + index+1 + " " + max);
            currentBBCH = main.controller.BBCHList.get(index+1).getBbchFrom();
            Picasso.get().load(main.controller.BBCHList.get(index +1).getImage()).into(cropImg);
            BBCHText.setText("BBCH "+main.controller.BBCHList.get(index+1).getBbchFrom());
        }
        else if (direction == 1 && (index-1>=low)){
            currentBBCH = main.controller.BBCHList.get(index-1).getBbchFrom();
            Picasso.get().load(main.controller.BBCHList.get(index-1).getImage()).into(cropImg);
            BBCHText.setText("BBCH "+main.controller.BBCHList.get(index-1).getBbchFrom());
        }
    }


    private class getActivityProducts extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {

            System.out.println("BBCH: " + currentBBCH);
            System.out.println("Kategori: " + strings[1]);
            System.out.println("Aktivitet: " + strings[0]);
            main.controller.getCropActivityProducts(CropActivityProductType.safeValueOf(strings[0]),getCategoryNumber(strings[1]),getCropID(strings[2]),temp.fieldStrategyIDS, currentBBCH);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            buildProductList();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }
    }

    private String getCategoryNumber(String name){
        String returnval = "";
        for(int i = 0; i<main.controller.activityCategories.size();i++){
            if(main.controller.activityCategories.get(i).getName().equals(name)){
                returnval = String.valueOf(main.controller.activityCategories.get(i).getId());
            }
        }
        return returnval;
    }


    private class getActivityCategories extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            buildCategoryList();
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(String... strings) {
            main.controller.getActivityCategories(strings[0], id);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }
    }



    private class getCropActivityTypes extends AsyncTask<Integer,Void,Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            buildActivityList();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Loading");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            main.controller.getCropActivityTypes(integers[0]);

            return null;
        }
    }





    public int getCropID(String cropname){
        int id = 9999;
        for(int i = 0; i<main.controller.getUser().cropsList.size();i++){
            if(main.controller.getUser().cropsList.get(i).getName().equals(cropname)){
                id = main.controller.getUser().cropsList.get(i).getCrop_id();
            }
        }
        return id;
    }

    public void buildCategoryList(){

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, main.controller.activityCategoriesString);
        categoryChooser.setAdapter(arrayAdapter);
        categoryChooser.setFocusable(true);
    }

    public void buildProductList(){
        ArrayAdapter<String> arrayAdapter;
        System.out.println(activityChooser.getText().toString());
        if(activityChooser.getText().toString().equals("harvest")){
            System.out.println("Activity er harvest");
            arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_dropdown_item_1line, main.controller.harvestTypesString);
            productChooser.setAdapter(arrayAdapter);
            productChooser.setFocusable(true);
        }
        else{
            arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_dropdown_item_1line, main.controller.activityProductListString);
            productChooser.setAdapter(arrayAdapter);
            productChooser.setFocusable(true);
        }



    }




    public void buildCropList(){
        crops.clear();
        for(int i = 0; i<temp.cropIDS.size();i++){
            for(int s = 0; s<main.controller.getUser().cropsList.size();s++){
                if(temp.cropIDS.get(i).equals(main.controller.getUser().cropsList.get(s).getCrop_id())){
                    crops.add(main.controller.getUser().cropsList.get(s).getName());
                }
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, crops);
        cropChooser.setAdapter(arrayAdapter);
        cropChooser.setFocusable(true);

    }

    public void buildActivityList(){
        ArrayAdapter<String> arrayAdapter;


            arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_dropdown_item_1line, main.controller.activitytypes2);



        activityChooser.setAdapter(arrayAdapter);
        activityChooser.setFocusable(true);

    }

    public boolean checkFieldActiveCrop(String Fieldname){
        boolean returnval = false;
        for(int i = 0; i<main.controller.getUser().fields.size();i++){
            if(Fieldname.equals(main.controller.getUser().fields.get(i).getDisplay_name())){
                temp = main.controller.getUser().fields.get(i);
                System.out.println("Field fundet");
            }
        }
        try {
            if(!temp.getCropsList().isEmpty()) {
                returnval = true;
            }
        }catch (Exception e){
            returnval = false;
        }


        System.out.println("Field har aktiv crop: " + returnval);
        return returnval;
    }

}
