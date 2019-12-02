package com.example.agricircle.project.Fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Entities.SearchItem;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;

public class filterLayoutFragment extends Fragment {

    private View myView;
    private MaterialBetterSpinner fieldChooser, cropChooser;
    private List<String> fields;
    private List<String> crops;
    private MainScreenActivity main;
    private Button apply;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.filter_layout, container, false);
        fieldChooser = myView.findViewById(R.id.fieldChooser);
        cropChooser = myView.findViewById(R.id.cropChooserFilter);
        apply = myView.findViewById(R.id.applyButton);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchItem item = save();
                System.out.println("Field: " + item.field);
                System.out.println("Crop: " + item.crops);
                FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
                ActivitiesList fragment2 = new ActivitiesList();

                Bundle bundle = new Bundle();

                bundle.putSerializable("Search", item);
                fragment2.setArguments(bundle);
                ft.replace(R.id.listfragment, fragment2);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        main = MainScreenActivity.getInstance();
        getFieldsList();
        getCropsList();




        return myView;
    }

    private void getFieldsList(){
        List<Field> temp = main.controller.getUser().fields;
        List<String> temp2 = new ArrayList<>();
        temp2.add("All fields");
        for(int i = 0; i<temp.size();i++){
            temp2.add(temp.get(i).getName());
        }
        fields = temp2;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, fields);
        fieldChooser.setAdapter(arrayAdapter);
        fieldChooser.setText(fields.get(0));
    }

    private void getCropsList(){
        List<Crop> temp = main.controller.getUser().cropsList;
        List<String> temp2 = new ArrayList<>();
        temp2.add("All crops");
        for(int i = 0; i<temp.size();i++){
            temp2.add(temp.get(i).getName());
        }
        crops = temp2;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, crops);
        cropChooser.setAdapter(arrayAdapter);
        cropChooser.setText(crops.get(0));
    }

    private SearchItem save(){
        String field = "All fields";
        List<String> activities = new ArrayList<>();
        String crop = "All crops";
        if(!fieldChooser.getText().toString().equals(fields.get(0))){
            field = fieldChooser.getText().toString();
        }
        if(!cropChooser.getText().toString().equals(crops.get(0))){
            crop = cropChooser.getText().toString();
        }
        SearchItem item = new SearchItem(field,activities,crop);

        return item;

    }
}
