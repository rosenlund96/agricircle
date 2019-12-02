package com.example.agricircle.project.Fragment;


import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.agricircle.R;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Crop;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ActivityRecieptFragment extends Fragment implements View.OnClickListener{

    private View myView;
    private Button save;
    private Activity activity;
    private TextView fieldName, BBCHText, cropname, activityType, executor;
    private EditText commentBox;
    private ImageView BBCHImage, cropImage;
    private MainScreenActivity main;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Kvitteringsside vises");
        myView = inflater.inflate(R.layout.activity_reciept_view, container, false);
        main = MainScreenActivity.getInstance();
        save = myView.findViewById(R.id.logButton);
        save.setOnClickListener(this);
        fieldName = myView.findViewById(R.id.fieldNameReciept);
        BBCHText = myView.findViewById(R.id.BBCHText);
        BBCHImage = myView.findViewById(R.id.BBCHImage);
        cropname = myView.findViewById(R.id.cropNameReciept);
        cropImage = myView.findViewById(R.id.cropImageReciept);
        activityType = myView.findViewById(R.id.activityTypeReciept);
        executor = myView.findViewById(R.id.activityExecutor);
        commentBox = myView.findViewById(R.id.commentBox);

        Bundle bundle = getArguments();
        try{
            Activity obj = (Activity) bundle.getSerializable("Activity");
            if(obj != null){
                activity = obj;



            }
        }catch (Exception e){
            System.out.println("Intet felt sendt med");


        }
        if(activity != null){
            setInformations();
        }

        return myView;
    }

    public void setInformations(){
        System.out.println("Activityid: " + activity.getActivity_id());
        fieldName.setText(activity.getFieldname());

        BBCHText.setText("BBCH0");
        Picasso.get().load("https://core.agricircle.com/static/images/ac-vorsaat-nachsaat.png").into(BBCHImage);
        if(activity.getBBCHname() != null){
            BBCHText.setText("BBCH"+activity.getBBCHname());
            Picasso.get().load(activity.getBBCHImage()).into(BBCHImage);
        }
        if(!activity.getComment().isEmpty()){
            commentBox.setText(activity.getComment());
        }
        System.out.println("Executor sættes til: " + activity.getExecutor());
        executor.setText(activity.getExecutor());
        System.out.println("Sætter information med BBCH: " + activity.getBBCHname());

        cropname.setText(getCropName(activity.getCrop_id()));
        activityType.setText(activity.getActivityType());
    }


    private String getCropName(int id){
        String temp = "";
        String url = "";
        List<Crop> crops = MainScreenActivity.getInstance().controller.getUser().cropsList;
        for(int i = 0; i<crops.size(); i++){
            if(crops.get(i).getCrop_id() == id){
                temp = crops.get(i).getName();
                url = crops.get(i).getPhoto_url();
            }
        }

        Picasso.get().load(url).into(cropImage);
        return temp;
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
