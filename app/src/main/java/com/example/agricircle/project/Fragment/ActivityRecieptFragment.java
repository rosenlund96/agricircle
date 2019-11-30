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
import com.squareup.picasso.Picasso;

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
        new getAllCrops().execute("");
        Bundle bundle = getArguments();
        try{
            com.example.agricircle.project.Entities.Activity obj= (com.example.agricircle.project.Entities.Activity) bundle.getSerializable("Activity");
            if(obj != null){
                activity = obj;
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
                executor.setText(activity.getExecutor());
                System.out.println("Sætter information med BBCH: " + activity.getBBCHname());
                setCropInfo(activity.getCrop_id());
                activityType.setText(activity.getActivityType());


            }
        }catch (Exception e){
            System.out.println("Intet felt sendt med");
            activity = null;

        }

        return myView;
    }

    public void setCropInfo(int id){

        while (true){
            if(main.controller.allCropsFinished){
                for(int i = 0; i<main.controller.allCropsList.size();i++){
                    if(main.controller.allCropsAsObject.get(i).getCrop_id() == id){
                        cropname.setText(main.controller.allCropsAsObject.get(i).getName());
                        Picasso.get().load(main.controller.allCropsAsObject.get(i).getPhoto_url()).into(cropImage);
                        System.out.println("Crop: " + main.controller.allCropsAsObject.get(i).getName() + " Img: " +main.controller.allCropsAsObject.get(i).getPhoto_url() );
                    }
                }
                break;
            }
        }
    }


    private class getAllCrops extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... strings) {
            main.controller.getAllCrops(strings[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


        }
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
