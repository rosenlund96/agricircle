package com.example.agricircle.project.Fragment;

import android.graphics.Color;
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
    MaterialBetterSpinner fieldChooser, cropChooser;
    TextView info;
    List<String> crops;
    Field temp;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.create_activity, container, false);
        fieldChooser = myView.findViewById(R.id.choosefield);
        cropChooser = myView.findViewById(R.id.choosecrop);
        cropChooser.setVisibility(View.INVISIBLE);
        main = MainScreenActivity.getInstance();
        info = myView.findViewById(R.id.infotextactivity);
        List<String> fields = new ArrayList<>();
        crops = new ArrayList<>();
        temp = null;
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


        fieldChooser.addTextChangedListener(new TextWatcher() {
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
        cropChooser.setVisibility(View.VISIBLE);

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
