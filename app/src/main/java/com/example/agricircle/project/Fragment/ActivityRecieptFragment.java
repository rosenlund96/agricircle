package com.example.agricircle.project.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;

public class ActivityRecieptFragment extends Fragment implements View.OnClickListener{

    private View myView;
    private Button save;
    private Activity activity;
    private TextView fieldName;
    private MainScreenActivity main;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.activity_reciept_view, container, false);
        main = MainScreenActivity.getInstance();
        save = myView.findViewById(R.id.logButton);
        save.setOnClickListener(this);
        fieldName = myView.findViewById(R.id.fieldNameReciept);
        Bundle bundle = getArguments();
        try{
            com.example.agricircle.project.Entities.Activity obj= (com.example.agricircle.project.Entities.Activity) bundle.getSerializable("Activity");
            if(obj != null){
                activity = obj;
                fieldName.setText(activity.getFieldname());


            }
        }catch (Exception e){
            System.out.println("Intet felt sendt med");
            activity = null;

        }

        return myView;
    }

    @Override
    public void onClick(View v) {
        main.controller.saveCurrentActivity(activity);
        FragmentTransaction ft =  getActivity().getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ListViewFragment fragment2 = new ListViewFragment();
        ft.replace(R.id.article_fragment, fragment2);
        ft.addToBackStack(null);
        ft.commit();
    }
}
