package com.example.agricircle.project.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.agricircle.project.Controller.UserController;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.User;
import com.example.agricircle.project.Fragment.MapFragment;
import com.example.agricircle.R;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity implements Serializable
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
    public transient Context mContext;
    public PlacesClient placesClient;
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
        mContext = this;



        i = getIntent();


        crops = new ArrayList<>();


        mPrefs = getPreferences(MODE_PRIVATE);
        currentState = STATE.GETCOOKIE;
        progressDialog = new ProgressDialog(MainScreenActivity.this,
                R.style.AppCompatAlertDialogStyle);

        //loadAssets();











        //new GetFields().execute();
        myDialog = new Dialog(this);






    }

             @Override
             protected void onResume() {
                 super.onResume();
                 Gson gson = new Gson();
                 mContext = this;
                 String json = LoadPreferences("User");
                 User user = gson.fromJson(json, User.class);
                 String cookie = LoadPreferences("Cookie");
                 if((cookie != null) && (user != null)){
                     System.out.println("Offlinedata bruges");

                     this.controller = new UserController(cookie);
                     this.controller.setUser(user);
                     controller.createDummyData(user.fields,user.cropsList);

                     fragmentManager.beginTransaction()
                             //.addToBackStack(null)
                             .replace(R.id.article_fragment
                                     , new MapFragment(),"MAP")

                             .commit();
                 }

                if(controller == null){
                    System.out.println("Data hentes på ny");
                    new LoadAssetsAsync().execute();
                }

                 //Tjek at alt data eksisterer og om der er netværksforbindelse.
             }

             public static MainScreenActivity getInstance() {
        return sMainScreenActivity;
    }

             private class LoadAssetsAsync extends AsyncTask<Void, Void, Void>{

                 @Override
                 protected void onPostExecute(Void aVoid) {
                     super.onPostExecute(aVoid);
                     progressDialog.dismiss();

                     fragmentManager.beginTransaction()
                             //.addToBackStack(null)
                             .replace(R.id.article_fragment
                                     , new MapFragment(),"MAP")

                             .commit();
                 }

                 @Override
                 protected Void doInBackground(Void... voids) {
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
                                 Boolean usercalled = false;

                                 progressDialog.setMessage("Henter Bruger");
                                 while(true){
                                     user = controller.getUser();
                                     if(user != null){
                                         controller.setUser(user);
                                         //controller.getCompanies();
                                         setUserSettings();
                                         //System.out.println("Bruger hentet");
                                         currentState = STATE.LOADCOMPANIES;
                                         break;
                                     }
                                     else if(!usercalled){
                                         usercalled = true;
                                         controller.getUserDB();
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

                         break;
                     }




                     return null;
                 }

                 @Override
                 protected void onPreExecute() {
                     super.onPreExecute();
                     //progressDialog.setIndeterminate(true);
                     progressDialog.setMessage("Henter informationer fra server");

                     progressDialog.show();
                 }
             }




    public void setUserSettings(){
        if(LoadPreferences("FirstLogin").equals("")){
            SavePreferences("FirstLogin","true");
            SavePreferences("Map","Normal");
            //SavePreferences("Language","English");
            SavePreferences("unit","Metric");
            SavePreferences("darkmode", "OFF");
            SavePreferences("ferMode", "OFF");
        }
        else{
            SavePreferences("FirstLogin","false");
        }
    }








    @Override
    public void onPause() {
        super.onPause();

        Gson gson = new Gson();
        System.out.println("Onpause blev kaldt");
        String json = gson.toJson(controller.getUser());
        SavePreferences("User",json);
        SavePreferences("Cookie",controller.cookie);
        //SavePreferences("Controller",json);

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





}
