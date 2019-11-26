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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.Activities.type.CropActivityProductType;
import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateActivityFragment extends Fragment {
    MainScreenActivity main;
    private View myView;
    MaterialBetterSpinner fieldChooser, cropChooser, activityChooser, categoryChooser;
    TextView info;
    List<String> crops;
    Field temp;
    int id;
    ProgressDialog progressDialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.create_activity, container, false);
        fieldChooser = myView.findViewById(R.id.choosefield);
        cropChooser = myView.findViewById(R.id.choosecrop);
        activityChooser = myView.findViewById(R.id.chooseactivitytype);
        categoryChooser = myView.findViewById(R.id.chooseactivitycategory);
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
                cropChooser.setFocusable(false);
                activityChooser.setFocusable(false);
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
                activityChooser.setFocusable(false);
                categoryChooser.setFocusable(false);
                if(!cropChooser.getText().toString().equals("")){


                id = getCropID(cropChooser.getText().toString());
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
                categoryChooser.setFocusable(false);
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
                    new getActivityProducts().execute();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return myView;
    }

    private class getActivityProducts extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            List<Integer> ints = new ArrayList<>();
            ints.add(10366);
            main.controller.getCropActivityProducts(CropActivityProductType.safeValueOf("fertilization"),"32",9,ints);
            return null;
        }
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
                android.R.layout.simple_dropdown_item_1line, main.controller.activityCategories);
        categoryChooser.setAdapter(arrayAdapter);
        categoryChooser.setFocusable(true);
    }


    public void buildCropList(){
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
