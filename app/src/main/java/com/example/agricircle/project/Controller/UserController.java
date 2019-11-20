package com.example.agricircle.project.Controller;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import com.example.agricircle.Activities.CheckEmailMutation;
import com.example.agricircle.project.CreateUserMutation;
import com.example.agricircle.project.Entities.Activity;
import com.example.agricircle.project.Entities.Company;
import com.example.agricircle.project.Entities.Crop;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Entities.Shape;
import com.example.agricircle.project.Entities.User;
import com.example.agricircle.project.Entities.Weather;
import com.example.agricircle.project.GetAllCropsQuery;
import com.example.agricircle.project.GetCompaniesQuery;
import com.example.agricircle.project.GetCropsQuery;
import com.example.agricircle.project.GetFieldsQuery;
import com.example.agricircle.project.GetUserQuery;
import com.example.agricircle.project.GetWeatherQuery;
import com.example.agricircle.project.LoginInputMutation;
import com.google.android.gms.maps.model.LatLng;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class UserController implements Serializable {
    public User user;
    public boolean fieldsLoaded;
    public List<Crop> cropsList = new ArrayList<>();
    public List<Field> fieldsList = new ArrayList<>();
    public List<Company> companiesList = new ArrayList<>();
    public List<Activity> activities = new ArrayList<>();
    public List<String> activitytypes = new ArrayList<>();
    private static final String BASE_URL = "https://graphql.agricircle.com/graphql";
    ApolloClient apolloClient;
    int loginStatus;
    String loginError;
    public int emailvalidate;
    public String cookie;

    public ArrayList<Weather> weatherList;




    public UserController(String cookie) {
        this.cookie = cookie;
        init();
        System.out.println("Ny usercontroller oprettet: " + cookie);

    }

    public void init(){
        weatherList = new ArrayList<>();
        OkHttpClient okHttpClient;
        if(cookie == null){
            cookie = "";
            okHttpClient = new OkHttpClient.Builder().build();
            createUser();
            apolloClient = ApolloClient.builder()
                    .serverUrl(BASE_URL)
                    .okHttpClient(okHttpClient)

                    .build();
        }
        else{
            okHttpClient = createOkHttpWithValidToken(cookie);
            apolloClient = ApolloClient.builder()
                    .serverUrl(BASE_URL)
                    .okHttpClient(okHttpClient)

                    .build();
            getUserDB();




        }








    }



    public List<Activity> getActivities(){
        return this.activities;
    }
    public void setActivities(List<Activity> activities){
        this.activities = activities;
    }

    public List<Crop> getCropsList(){
        return this.cropsList;
    }

    public int getLoginstatus(){
        return this.loginStatus;
    }
    public String getLoginError(){
        return this.loginError;
    }
    public void createUser(){
        user = new User();
        cropsList = new ArrayList<>();
    }

    private OkHttpClient createOkHttpWithValidToken(final String cookie) {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        return chain.proceed(chain.request().newBuilder().header("x-cookie", cookie).build());
                    }
                }).build();


    }
    public String getCookie(){
        return this.cookie;
    }
    public void setCookie(String cookie){
        this.cookie = cookie;
        init();
    }
    public boolean getFieldsLoaded() {return this.fieldsLoaded;}


    public List<Crop> getAllCrops(){
        cropsList.clear();
        GetAllCropsQuery query = GetAllCropsQuery.builder().build();

        apolloClient.query(query).enqueue(new ApolloCall.Callback<GetAllCropsQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetAllCropsQuery.Data> response) {
                List<GetAllCropsQuery.Collection> cropdata = response.data().allCrops().collection();
                System.out.println("Crops hentet fra DB: " + cropdata.size());
                for (int i = 0; i < cropdata.size();i++){
                    cropsList.add(new Crop(cropdata.get(i).id(),null,cropdata.get(i).name(),cropdata.get(i).image_url(),cropdata.get(i).__typename()));
                }
                System.out.println("Crops hentet:" + cropsList.size());

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                System.out.println("Fejl: " + e.getCause());
            }
        });





        return cropsList;
    }

    public void createDummyData(List<Field> fields, List<Crop> crops){

        activitytypes.add("Sowing");
        activitytypes.add("Spraying");
        activitytypes.add("Fertilization");



        System.out.println("Dummy: " + fields.size());
        Random rand = new Random();
        for(int i = 0; i<fields.size();i++){
            int randomNum = rand.nextInt((3 - 2) + 1) + 2;
            for(int x = 0; x < randomNum;x++){
                int randomNum2 = rand.nextInt((3 - 1) + 1) + 1;
                activities.add(new Activity(fields.get(i).getId(),activitytypes.get(randomNum2-1),0,fields.get(i).getDisplay_name(),""));
            }

        }

        for(int c = 0;c<crops.size();c++){
            int  id = crops.get(c).getField_ids().get(0);
            for (int f = 0; f<activities.size();f++){
                if(activities.get(f).getField_id() == id){
                    activities.get(f).setCrop_id(crops.get(c).getCrop_id());
                    activities.get(f).setUrl(crops.get(c).getPhoto_url());

                }
            }
        }
        System.out.println("" + activities.size() + " dummy aktiviteter oprettet");


    }





    public String getLocale(String language){
        String value = "";
        if(language.equals("English")){
            value = "en";
        }
        else if(language.equals("German")){
            value = "de";
        }
        else if(language.equals("Danish")){
            value = "da";
        }

        return value;
    }


    public List<Crop> getCrops(int companyid, int year){
            cropsList.clear();


            final GetCropsQuery crops = GetCropsQuery.builder()
                    .companyid(companyid)
                    .year(year)
                    .build();

            apolloClient.query(crops).enqueue(new ApolloCall.Callback<GetCropsQuery.Data>() {
                @Override
                public void onResponse(@NotNull Response<GetCropsQuery.Data> response) {
                    if(!response.hasErrors()){

                          for(int x = 0; x < response.data().crops().size(); x++){
                              GetCropsQuery.Crop data = response.data().crops().get(x);
                             //System.out.println(response.data().allCrops().collection().get(i).name());
                            cropsList.add(new Crop(data.crop_id(),data.field_ids().subList(0,data.field_ids().size()),data.name(),data.photo_url(),data.__typename()));

                    }

                }
                    user.setCropsList(cropsList);
                }

                @Override
                public void onFailure(@NotNull ApolloException e) {
                    System.out.println("Hentning af crops fejlede: " + e.getCause());
                }
            });




        return cropsList;
    }

    public List<Field> getFields(){
        fieldsLoaded = false;
        GetFieldsQuery fields = GetFieldsQuery.builder().build();

        apolloClient.query(fields).enqueue(new ApolloCall.Callback<GetFieldsQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetFieldsQuery.Data> response) {
                if(!response.hasErrors()){
                    List<GetFieldsQuery.Field> fields = response.data().fields();

                    for(int i = 0; i < fields.size(); i++){

                        JsonParser parser = new JsonParser();
                        JsonObject objectPolygon = (JsonObject) parser.parse(fields.get(i).shape());
                        JsonObject objectCenterpoint = (JsonObject) parser.parse(fields.get(i).center_point());
                        JsonArray objectRotations = (JsonArray) parser.parse(fields.get(i).rotations());

                        List<LatLng> coordinatePolygon = new ArrayList<LatLng>();
                        List<LatLng> coordinateCenterpoint = new ArrayList<>();
                        String typePolygon = objectPolygon.get("type").getAsString();
                        String typeCenterpoint = objectCenterpoint.get("type").getAsString();
                        //String cropid = objectRotations.getAs;
                        //System.out.println("CropID: " + objectRotations.getAsJsonArray().size());

                        for(int x = 0; x < objectPolygon.getAsJsonArray("coordinates").get(0).getAsJsonArray().size();x++){
                            //System.out.println("Coordinate " + x + " :" + object.getAsJsonArray("coordinates").get(0).getAsJsonArray().get(x).getAsJsonArray().get(1));
                            coordinatePolygon.add(new LatLng(Double.parseDouble(objectPolygon.getAsJsonArray("coordinates").get(0).getAsJsonArray().get(x).getAsJsonArray().get(1).toString()),Double.parseDouble(objectPolygon.getAsJsonArray("coordinates").get(0).getAsJsonArray().get(x).getAsJsonArray().get(0).toString())));
                        }


                        coordinateCenterpoint.add(new LatLng(Double.parseDouble(objectCenterpoint.getAsJsonArray("coordinates").get(1).toString()),Double.parseDouble(objectCenterpoint.getAsJsonArray("coordinates").get(0).toString())));


                        Shape centerpoint = new Shape(typeCenterpoint,coordinateCenterpoint);
                        Shape polygon = new Shape(typePolygon,coordinatePolygon);



                        fieldsList.add(new Field(fields.get(i).id(),fields.get(i).name(),fields.get(i).__typename(),polygon,fields.get(i).surface(),fields.get(i).name(),fields.get(i).fertilizer_enabled(),centerpoint));
                        //System.out.println("Felt hentet: " + fields.get(i));

                    }
                    user.setFields(fieldsList);
                    fieldsLoaded = true;

                }


            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                System.out.println("Hentning af fields fejlede: " + e.getCause());
            }
        });
        return fieldsList;


    }

    public int ValidateEmail(String email){
        emailvalidate = 0;
        CheckEmailMutation validate = CheckEmailMutation.builder()
                .email(email)
                .locale("EN")
                .build();

        apolloClient.mutate(validate).enqueue(new ApolloCall.Callback<CheckEmailMutation.Data>() {
            @Override
            public void onResponse(@NotNull Response<CheckEmailMutation.Data> response) {
                if(!response.hasErrors()){
                    System.out.println("Status for validering: " + response.data().validateEmail().code());
                    emailvalidate = 2;

                }
                else{
                    System.out.println("Fejl ved email validering: " +response.errors().get(0));
                    emailvalidate = 1;


                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                System.out.println("Fejl ved kald af validering");
            }
        });

        return emailvalidate;

    }

    public void getWeather(LatLng coordinate, String model, String startdate, int hoursrange, int hoursinterval, List<String> parameters){
        weatherList.clear();
        GetWeatherQuery weather = GetWeatherQuery.builder()
                .lat(coordinate.latitude)
                .lng(coordinate.longitude)
                .model(model)
                .startdate(startdate)
                .hoursrange(hoursrange)
                .hoursinterval(hoursinterval)
                .parameters(parameters)
                .build();
        apolloClient.query(weather).enqueue(new ApolloCall.Callback<GetWeatherQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetWeatherQuery.Data> response) {
                if(!response.hasErrors()){
                    ArrayList<Weather> temp = new ArrayList<>();
                    GetWeatherQuery.Weather weather = response.data().weather();
                    JsonParser parser = new JsonParser();
                    JsonArray weatherobj = (JsonArray) parser.parse(weather.data());
                    Weather weatherdata;
                    int size = weatherobj.getAsJsonArray().get(0).getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonObject().get("dates").getAsJsonArray().size();
                    for(int i = 0; i < size; i++){
                        double temp_nu =  Double.parseDouble(weatherobj.getAsJsonArray().get(0).getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonObject().get("dates").getAsJsonArray().get(i).getAsJsonObject().get("value").toString());
                        double max_temp = Double.parseDouble(weatherobj.getAsJsonArray().get(1).getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonObject().get("dates").getAsJsonArray().get(i).getAsJsonObject().get("value").toString());
                        double min_temp = Double.parseDouble(weatherobj.getAsJsonArray().get(2).getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonObject().get("dates").getAsJsonArray().get(i).getAsJsonObject().get("value").toString());
                        double rain =  Double.parseDouble(weatherobj.getAsJsonArray().get(3).getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonObject().get("dates").getAsJsonArray().get(i).getAsJsonObject().get("value").toString());
                        double windspeed =  Double.parseDouble(weatherobj.getAsJsonArray().get(4).getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonObject().get("dates").getAsJsonArray().get(i).getAsJsonObject().get("value").toString());
                        double winddirection = Double.parseDouble(weatherobj.getAsJsonArray().get(5).getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonObject().get("dates").getAsJsonArray().get(i).getAsJsonObject().get("value").toString());
                        String date = weatherobj.getAsJsonArray().get(5).getAsJsonObject().get("coordinates").getAsJsonArray().get(0).getAsJsonObject().get("dates").getAsJsonArray().get(i).getAsJsonObject().get("date").toString();
                        weatherdata = new Weather(temp_nu,max_temp,min_temp,rain,windspeed,winddirection,date);



                            temp.add(weatherdata);



                    }
                        weatherList = temp;
                        //System.out.println("Der er " + weatherList.size() + " vejrelemente(r)");




                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });

    }

    public void createNewUser(String title,String firstname, String lastname, String phone, String email, String companyname,String companyadress,String companyzip, String companycity, String language, String subscription){
        String sourcechannel = "app";
        String companyCountrycode = "";
        if(language.equals("German")){
            companyCountrycode = "DE";
        }
        if(language.equals("Danish")){
            companyCountrycode = "DK";
        }
        if(language.equals("English")){
            companyCountrycode = "US";
        }


        CreateUserMutation create = CreateUserMutation.builder()
                .title(title)
                .firstname(firstname)
                .lastname(lastname)
                .phonenumber(phone)
                .email(email)
                .companyname(companyname)
                .companyadress(companyadress)
                .companycity(companycity)
                .companystate("")
                .companypostalcode(companyzip)
                .sourcechannel(sourcechannel)
                .companycountrycode(companyCountrycode)
                .subscriptiontype(subscription)
                .language(language)

                .build();

        apolloClient.mutate(create).enqueue(new ApolloCall.Callback<CreateUserMutation.Data>() {

            @Override
            public void onResponse(@NotNull Response<CreateUserMutation.Data> response) {
                if(!response.hasErrors()){
                    System.out.println("Bruger oprettet");

                }
                System.out.println("Der var en fejl" + response.data());
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                System.out.println("Fejl ved brugeroprettelse: " + e.getMessage());

            }
        });

    }


    public void getCompanies(){
        companiesList.clear();

        GetCompaniesQuery companies = GetCompaniesQuery.builder().build();

        apolloClient.query(companies).enqueue(new ApolloCall.Callback<GetCompaniesQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetCompaniesQuery.Data> response) {
                if(!response.hasErrors()){
                    System.out.println(response.data().companies());
                   for(int i = 0; i < response.data().companies().size();i++){
                       GetCompaniesQuery.Company data = response.data().companies().get(i);
                       Company company = new Company(data.id(),data.name(),data.access(),data.owner_photo_url(),data.default_());
                       if(!checkObjectExists(company.getId())){
                           companiesList.add(company);
                       }

                   }

                    user.companies = companiesList;
                   user.setPrimaryCompany(companiesList.get(0).getId());
                    System.out.println("Antal virksomheder: " + user.getCompanies().size());
                }
                else {
                    System.out.println("Fejl ved hentning af virksomheder: "+response.errors());
                }
            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                System.out.println(e.getCause());
            }
        });
    }
    public boolean checkObjectExists(int key){
        boolean value = false;

        for(int i = 0; i<companiesList.size();i++){
            if(companiesList.get(i).getId() == key) {
                value = true;
            }
        }
        return value;
    }

    public boolean checkInternetConnection(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        System.out.println("Current conectivitystatus: " + isConnected);
        return isConnected;
    }



    public void login(String email, String password,String locale,final Context context) {
    loginError = "";
        LoginInputMutation login = LoginInputMutation.builder()
                .user(email)
                .pass(password)
                .locale(locale)
                .build();
        //System.out.println("Mail: " + login.variables().user() + " Pass: " + login.variables().pass() );

        apolloClient.mutate(login).enqueue(new ApolloCall.Callback<LoginInputMutation.Data>() {
            @Override
            public void onStatusEvent(@NotNull ApolloCall.StatusEvent event) {
                super.onStatusEvent(event);
                if(event == ApolloCall.StatusEvent.COMPLETED){
                    if(cookie == null){
                        System.out.println("Cookie er null");
                        getUserDB();
                    }

                    init();
                    //getUserDB();
                    //System.out.println("Bruger hentes");
                    Handler mainHandler = new Handler(Looper.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {



                        } // This is your code
                    };
                    mainHandler.post(myRunnable);
                }
            }

            @Override
            public void onResponse(@NotNull Response<LoginInputMutation.Data> response) {
                if(!response.hasErrors()){
                    cookie = response.data().login().cookie().get(2);
                    //cookie="_AgriCircle_subdomain_session=SmlEZzEzMlR0YitqSnVkMndHem9Ddlhmb0drR3g5Q0pwdGJVUnBMWGZtRzdiNFRtcE5vdXp2b2J2ZkFkRlVDVStHTlg1ZlZUdVM0cTZGOXRxKy9mMEdjMG04MkVUZGczK2dRK3JsOTdKYjRGVm1JNDlNVFRHTzVrcWRRcmtJc1J0NWR6bmdXM2VxcVVacVpCS1VrYWNZK2VXR2NidXpEbUdzNDNnamhnVnhVPS0tVStNbDFqblZ5U1EzYzkwSTN6UnFiZz09--15f97196700b3eeab76d6e1a18517790c3798152;";
                    loginStatus = 2;
                    System.out.println("Cookie: " + cookie);
                    //getUser();









                }
                else {

                    System.out.println("Fejl ved login: "+response.errors().get(0).message().contains("401"));
                    loginStatus = 1;
                    loginError = "Forkert brugernavn eller kodeord";
                }


            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
                System.out.println("En fejl er opstået ved login: " + e.getCause());
                loginStatus = 1;
                loginError = "Fejl ved forbindelse til server";

            }
        });

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void logout(){

    }

    public void getUserDB(){

        GetUserQuery query = GetUserQuery.builder()
                .build();


        apolloClient.query(query).enqueue(new ApolloCall.Callback<GetUserQuery.Data>() {


            @Override
            public void onStatusEvent(@NotNull ApolloCall.StatusEvent event) {
                super.onStatusEvent(event);
                if(event == ApolloCall.StatusEvent.COMPLETED){
                    //System.out.println("Bruger hentet, virksomheder hentes");
                    //getCompanies();
                }
            }

            @Override
            public void onResponse(@NotNull Response<GetUserQuery.Data> response) {
                if(response.hasErrors()){
                    System.out.println("Der opstod en fejl ved hentning af bruger: "+response.hasErrors());
                }
                else {
                    //System.out.println("Bruger hentet: "+response.data().user().name());
                    GetUserQuery.User user = response.data().user();
                    User temp = new User();
                    temp.setName(user.name());
                    temp.setAvatarUrl(user.avatarUrl());
                    temp.setLanguage(user.language());
                    temp.setSlug(user.slug());
                    setUser(temp);


                }

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });

    }


}
