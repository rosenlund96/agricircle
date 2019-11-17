package com.example.agricircle.project.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.agricircle.project.Controller.UserController;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Entities.User;
import com.example.agricircle.project.Fragment.MapFragment;
import com.example.agricircle.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity
         {


    public UserController controller;


    SharedPreferences mPrefs;
    User user;
    Dialog myDialog;
    String cookie;
    List<Crop> crops;
    Intent i;
    ProgressDialog progressDialog;
    Boolean polygonsDrawed;
    private static MainScreenActivity sMainScreenActivity;
    private FragmentManager fragmentManager;

    public enum STATE {LOADUSER,LOADCROPS, LOADFIELDS, SETOBJECTS, GETCOOKIE, LOADCOMPANIES};
    public STATE currentState;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        polygonsDrawed = false;
        fragmentManager = getSupportFragmentManager();
        sMainScreenActivity = this;




        i = getIntent();


        crops = new ArrayList<>();


        mPrefs = getPreferences(MODE_PRIVATE);
        currentState = STATE.GETCOOKIE;
        progressDialog = new ProgressDialog(MainScreenActivity.this,
                R.style.Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);

        progressDialog.show();
        loadAssets();








        //new GetFields().execute();
        myDialog = new Dialog(this);






    }

    public static MainScreenActivity getInstance() {
        return sMainScreenActivity;
    }

    public void loadAssets(){



        while(true){
            switch(currentState){
                case GETCOOKIE:
                    cookie = i.getStringExtra("Cookie");
                    progressDialog.setMessage("Henter Cookie");
                    while(true){
                        if(cookie != null){
                            controller = new UserController(cookie);
                            currentState = STATE.LOADUSER;
                            System.out.println("Cookie hentet");
                            break;
                        }
                    }

                case LOADUSER:
                    user = (User) i.getSerializableExtra("User");
                    progressDialog.setMessage("Henter Bruger");
                    while(true){
                        if(user != null){
                            controller.setUser(user);
                            //controller.getCompanies();
                            setUserSettings();
                            //System.out.println("Bruger hentet");
                            currentState = STATE.LOADCOMPANIES;
                            break;
                        }
                    }
                case LOADCOMPANIES:
                    Boolean getCompaniesCalled = false;
                    progressDialog.setMessage("Henter Virksomheder");

                    while(true){
                        if(controller.getUser().getCompanies().size() > 0){

                            System.out.println("Virksomheder hentet");
                            currentState = STATE.LOADCROPS;
                            break;
                        }
                        else if(controller.getUser().getCompanies().isEmpty() && !getCompaniesCalled){
                            controller.getCompanies();
                            getCompaniesCalled = true;
                        }
                    }


                case LOADCROPS:

                    for(int i = 0; i<controller.getUser().getCompanies().size();i++){
                        System.out.println("Virksomhed "+(i+1)+" "+controller.getUser().getCompanies().get(i).getName());
                    }
                    System.out.println("Henter crops for: " +controller.getUser().getCompanies().get(0).getName());
                    //controller.getCrops(controller.getUser().getCompanies().get(0).getId(),2019);
                    controller.getCrops(controller.user.getPrimaryCompany(),2019);
                    progressDialog.setMessage("Henter Crops");
                    while(true){
                        if(controller.cropsList.size() > 0){
                            System.out.println("Crops hentet: " + controller.cropsList.size());
                            currentState = STATE.LOADFIELDS;
                            break;
                        }
                    }

                case LOADFIELDS:
                    progressDialog.setMessage("Henter Fields");
                    controller.getFields();
                    while(true){

                        if(controller.getFieldsLoaded()){
                            System.out.println("Fields hentet" + "Antal: " + controller.getUser().getFields().size());
                            currentState = STATE.SETOBJECTS;
                            break;
                        }
                    }


                case SETOBJECTS:
                    controller.createDummyData(controller.getUser().getFields(),controller.getCropsList());

                    break;



            }
            progressDialog.dismiss();

            fragmentManager.beginTransaction()
                    //.addToBackStack(null)
                    .replace(R.id.article_fragment
                            , new MapFragment(),"MAP")

                    .commit();
            break;
        }

    }

    public void setUserSettings(){
        if(LoadPreferences("FirstLogin").equals("")){
            SavePreferences("FirstLogin","true");
            SavePreferences("Map","Normal");
            SavePreferences("Language","English");
            SavePreferences("unit","Metric");
        }
        else{
            SavePreferences("FirstLogin","false");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        //stop location updates when Activity is no longer active

    }


             private void SavePreferences(String key, String value) {
                 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                 SharedPreferences.Editor editor = sharedPreferences.edit();
                 editor.putString(key, value);
                 editor.commit();
             }

             private String LoadPreferences(String key) {
                 SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                 String result = sharedPreferences.getString(key, "");
                 return result;

             }












    public void ShowPopup(View v) {

        Button knap;
        myDialog.setContentView(R.layout.popup_modal);
        knap = (Button) myDialog.findViewById(R.id.alert_btn_okay);
        knap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        myDialog.getWindow().setLayout((int)(width ),(int)(height * 0.89 ));
        myDialog.show();
    }
    public PolygonOptions DrawPolygon(Field field){
        PolygonOptions rectOptions = new PolygonOptions();
        for(int i = 0; i<field.getCoordinates().getNumOfCoordinates(); i++){
            rectOptions.add(field.getCoordinates().getCoordinates().get(i));

        }
        rectOptions.add(field.getCoordinates().getCoordinates().get(0));
        rectOptions.strokeColor(Color.RED);
        rectOptions.fillColor(Color.GREEN);
        return rectOptions;
    }





}
