package com.example.agricircle.project.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.example.agricircle.project.Controller.UserController;
import com.example.agricircle.project.Entities.User;
import com.example.agricircle.project.GraphQL.ClientBackEnd;
import com.example.agricircle.R;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public Button login, register;
    public EditText mail,pass;
    public TextView createUser;

    String cookie = null;
    boolean permission;
    private static final String BASE_URL = "https://graphql.agricircle.com/graphql";
    ClientBackEnd graphql;
    UserController UsrController;
    Locale currentLocale;
    private FirebaseAnalytics mFirebaseAnalytics;





    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginview);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        permission = false;

        createUser = findViewById(R.id.createUserText);
        createUser.setOnClickListener(this);
        createUser.setFocusable(false);
        login = findViewById(R.id.button2);
        login.setOnClickListener(this);
        mail = findViewById(R.id.email);
        pass =  findViewById(R.id.pass);


        currentLocale = getResources().getConfiguration().locale;


        String language = LoadPreferences("Language");
        graphql = new ClientBackEnd();
        UsrController = new UserController(null);

        System.out.println("Sprog hentet: " + language);
        login.setText(R.string.login);
        login.setFocusable(false);
        mail.setHint(R.string.email);
        pass.setHint(R.string.password);


        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);


        if(!UsrController.getCookie().equals("")){
            Toast.makeText(this, "Cookie eksisterer",
                    Toast.LENGTH_LONG).show();
        }






    }

    private boolean checkNetwork(){
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(checkNetwork()){


        if(!LoadPreferences("Cookie").equals("")){
            cookie = LoadPreferences("Cookie");
            UsrController.setCookie(cookie);
            User user = UsrController.getUser();
            Intent i = new Intent(MainActivity.this, MainScreenActivity.class);
            //ryd backstack
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra("Cookie", cookie);
            i.putExtra("UserObject", user);

            startActivity(i);
            finish();
        }
        }
    }

    private String LoadPreferences(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String result = sharedPreferences.getString(key, "");
        return result;

    }






public void init(){
    if(!checkNetwork()){
        new AlertDialog.Builder(this)
                .setTitle("No Internet")
                .setMessage(getResources().getString(R.string.permissiontext))
                .setPositiveButton(getResources().getText(R.string.buttonOK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                MY_PERMISSIONS_REQUEST_LOCATION );
                    }
                })
                .create()
                .show();
    }

    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //Location Permission already granted
            permission = true;

        } else {
            permission = false;
            //Request Location Permission

            checkLocationPermission();
        }
    }
}

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    //Tilføj flyt kamera efter accept
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.permissiontitle))
                        .setMessage(getResources().getString(R.string.permissiontext))
                        .setPositiveButton(getResources().getText(R.string.buttonOK), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }


    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }




    @Override
    public void onClick(View v) {

        if (v == login){
            init();

            if(permission){


            //new Login().execute();
            //graphql.login(this.mail.getText().toString(),this.pass.getText().toString(), this);
            //System.out.println("Login: " + graphql.checkLogin());

            UsrController.login(this.mail.getText().toString(),this.pass.getText().toString(),currentLocale.getCountry(), this);
            while(true){
                String cookie = UsrController.getCookie();
                SavePreferences("Cookie", cookie);
                User user = UsrController.user;
                int status = UsrController.getLoginstatus();
                String error = UsrController.getLoginError();
                //System.out.println("Status: " + status);
                if (cookie != null && user.getName() != null){
                    //System.out.println("Cookie blev hentet!");

                    Intent i = new Intent(MainActivity.this, MainScreenActivity.class);
                    //ryd backstack
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("Cookie", cookie);
                    i.putExtra("User", user);
                    System.out.println("Bruger sendt med: "+ user.getName());
                    startActivity(i);
                    finish();
                    break;
                }
                else if( status == 1){
                    //System.out.println("Fejl registreret");
                    //Toast.makeText(this, error,
                     //       Toast.LENGTH_LONG).show();
                    this.mail.setError(getResources().getString(R.string.wronglogin));
                    this.pass.setError(getResources().getString(R.string.wronglogin));
                    break;
                    //System.out.println("Cookie endnu ikke hentet");

                }



            }



            }
        }
        else if (v == createUser){
            Intent i = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(i);
        }
    }

}
