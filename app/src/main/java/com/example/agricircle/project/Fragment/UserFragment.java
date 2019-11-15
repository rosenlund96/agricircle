package com.example.agricircle.project.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.agricircle.project.Activities.MainActivity;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.R;
import com.squareup.picasso.Picasso;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class UserFragment extends Fragment implements View.OnClickListener, Switch.OnCheckedChangeListener {
    View myView;
    Switch units,setting2,darkmode;
    Button logout;
    TextView profileName;
    ImageView profilePic;
    Dialog myDialog;
    MaterialBetterSpinner language, mapType;
    MainScreenActivity main;
    String profilePlaceholder = "https://core.agricircle.com/static/images/agricircle/profile_photo_placeholder.svg";


    public UserFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.usersettings, container, false);
        myDialog = new Dialog(getContext());
        darkmode = (Switch) myView.findViewById(R.id.darkmode);
        units = (Switch) myView.findViewById(R.id.Unit);
        units.setOnCheckedChangeListener(this);
        logout = (Button) myView.findViewById(R.id.logout);
        logout.setOnClickListener(this);
        String[] languageData = getResources().getStringArray(R.array.languages);
        List<String> mapTypes = Arrays.asList(getResources().getStringArray(R.array.maptypes));
        main = MainScreenActivity.getInstance();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, mapTypes);
        mapType = (MaterialBetterSpinner)
                myView.findViewById(R.id.mapstyle);
        mapType.setAdapter(arrayAdapter);

        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, languageData);
        language = (MaterialBetterSpinner)
                myView.findViewById(R.id.language);
        language.setAdapter(arrayAdapter2);


        mapType.setHintTextColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        language.setHintTextColor(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));


        mapType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SavePreferences("Map",mapType.getText().toString());

                Toast.makeText(getActivity(), "Korttype opdateret",
                        Toast.LENGTH_LONG).show();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        language.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SavePreferences("Language",language.getText().toString());
                UpdateLanguage();
                Toast.makeText(getActivity(), R.string.languageUpdated,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        profilePic = (ImageView) myView.findViewById(R.id.profilePic);
        profileName = (TextView) myView.findViewById(R.id.username);
        profileName.setText(MainScreenActivity.getInstance().controller.getUser().name);
        if(MainScreenActivity.getInstance().controller.getUser().avatarUrl != profilePlaceholder){

            Picasso.get().load(MainScreenActivity.getInstance().controller.getUser().avatarUrl).into(profilePic);
        }
        initialize();
        return myView;
    }

    public void initialize(){
        //Hent nuværende indstillinger ind og sæt profilfillede
        String unit = LoadPreferences("unit");
        if(unit.equals("Metric")){
            units.setChecked(true);
        }
        else if (unit.equals("Imperial")){
            units.setChecked(false);
        }
        mapType.setText(LoadPreferences("Map"));
        language.setText(LoadPreferences("Language"));

    }

    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    private String LoadPreferences(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        String result = sharedPreferences.getString(key, "");
        return result;

    }

    public void UpdateLanguage(){
        String languageString = LoadPreferences("Language");


        Locale locale = new Locale(main.controller.getLocale(languageString));
        Locale.setDefault(locale);
        Configuration config = this.getActivity().getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        this.getActivity().getBaseContext().getResources().updateConfiguration(config,
                this.getActivity().getBaseContext().getResources().getDisplayMetrics());
        System.out.println("Sprog hentet: " + getResources().getConfiguration().locale);
        logout.setText(R.string.logout);
        mapType.setHint(R.string.maptype);
        language.setHint(R.string.language);
        String[] languageData = getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, languageData);
        language.setAdapter(arrayAdapter2);
    }



public void logout(){

    Button okay,cancel;
    TextView overskrift, tekst;
    myDialog.setContentView(R.layout.popup_modal);
    overskrift = (TextView) myDialog.findViewById(R.id.modaloverskrift);
    tekst = (TextView) myDialog.findViewById(R.id.modaltekst);
    okay = (Button) myDialog.findViewById(R.id.alert_btn_okay);
    cancel = (Button) myDialog.findViewById(R.id.alert_btn_cancel);
    overskrift.setText(R.string.modalTitle);
    tekst.setText(R.string.modalText);
    okay.setText(R.string.buttonLogout);
    cancel.setText(R.string.buttonCancel);
    okay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myDialog.dismiss();
            getActivity().finish();
            startActivity(new Intent(getContext(), MainActivity.class));

        }
    });
    cancel.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            myDialog.dismiss();
        }
    });
    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    DisplayMetrics dm = new DisplayMetrics();
    getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
    int width = dm.widthPixels;
    int height = dm.heightPixels;
    myDialog.getWindow().setLayout((int)(width ),(int)(height * 0.45 ));
    myDialog.show();



}


    @Override
    public void onClick(View v) {
        if(v == logout){
        logout();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){
            SavePreferences("unit","Metric");
            Toast.makeText(getActivity(), "Metric",
                    Toast.LENGTH_LONG).show();
        }
        else{
            SavePreferences("unit", "Imperial");
            Toast.makeText(getActivity(), "Imperial",
                    Toast.LENGTH_LONG).show();
        }
    }
}
