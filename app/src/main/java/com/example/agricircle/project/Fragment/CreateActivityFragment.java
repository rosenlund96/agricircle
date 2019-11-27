package com.example.agricircle.project.Fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.example.agricircle.Activities.type.CropActivityProductType;
import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateActivityFragment extends Fragment implements View.OnClickListener {
    MainScreenActivity main;
    int currentBBCH;
    private View myView;
    MaterialBetterSpinner fieldChooser, cropChooser, activityChooser, categoryChooser, productChooser;
    Button prev,next;
    TextView info, BBCHText;
    ImageView cropImg;
    List<String> crops;
    Field temp;
    int id;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.create_activity, container, false);
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
        progressDialog = new ProgressDialog(getContext(),
                R.style.AppCompatAlertDialogStyle);
        main = MainScreenActivity.getInstance();
        info = myView.findViewById(R.id.infotextactivity);
        List<String> fields = new ArrayList<>();
        crops = new ArrayList<>();
        temp = null;
        cropChooser.setFocusable(false);
        activityChooser.setFocusable(false);
        categoryChooser.setFocusable(false);
        productChooser.setFocusable(false);
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
                categoryChooser.setFocusable(false);

                if(!checkFieldActiveCrop(fieldChooser.getText().toString())){
                    fieldChooser.setError("Field doesnt have an active crop");
                    info.setText(getContext().getString(R.string.fieldhasnocrop));
                    info.setTextColor(Color.RED);

                }
                else{
                    info.setText(getContext().getString(R.string.createActivity));
                    info.setTextColor(Color.DKGRAY);
                    buildCropList();
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
                if(!cropChooser.getText().toString().equals("")){


                id = getCropID(cropChooser.getText().toString());
                new getCropBBCHStages().execute(id);
                new getCropActivityTypes().execute(id);

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
                    new getActivityCategories().execute(activityChooser.getText().toString());
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
                    new getActivityProducts().execute(activityChooser.getText().toString(),categoryChooser.getText().toString(), cropChooser.getText().toString() );
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


            main.controller.getCropActivityProducts(CropActivityProductType.safeValueOf(strings[0]),getCategoryNumber(strings[1]),getCropID(strings[2]),temp.fieldStrategyIDS);
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
        System.out.println("Der er elementer i listen: "+ main.controller.activityProductListString.size());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, main.controller.activityProductListString);
        productChooser.setAdapter(arrayAdapter);
        productChooser.setFocusable(true);
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

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
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
            if(temp.cropid != 99999) {
                returnval = true;
            }
        }catch (Exception e){
            returnval = false;
        }


        System.out.println("Field har aktiv crop: " + returnval);
        return returnval;
    }

}
