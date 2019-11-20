package com.example.agricircle.project.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.project.Activities.RegisterActivity;
import com.example.agricircle.R;
import com.google.android.gms.maps.model.LatLng;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class RegisterFrag1 extends Fragment implements View.OnClickListener {

    View myView;
    MaterialBetterSpinner title;
    Button next;
    EditText navn, efternavn, telefon, email;
    RegisterActivity main;
    boolean returnvalue;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.register_frag1, container, false);
        String[] titleData = {"Ms","Mr"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, titleData);
        title = (MaterialBetterSpinner)
                myView.findViewById(R.id.title);
        title.setAdapter(arrayAdapter);
        main = RegisterActivity.getInstance();
        title.setBackground(getActivity().getDrawable(R.drawable.textedit_custom));
        next = (Button) myView.findViewById(R.id.registerbutton);
        next.setFocusable(true);
        next.setFocusableInTouchMode(true);///add this line
        next.setOnClickListener(this);

        navn = (EditText) myView.findViewById(R.id.fornavn);
        efternavn = (EditText) myView.findViewById(R.id.efternavn);
        telefon = (EditText) myView.findViewById(R.id.telefon);
        email = (EditText) myView.findViewById(R.id.email);




        return myView;
    }

    public boolean checkInputs(){
       returnvalue = false;
        if(title.getText().toString().matches("")){
            title.setError(getResources().getString(R.string.required));
            returnvalue = true;
        }
        if(navn.getText().toString().matches("")){
            navn.setError(getResources().getString(R.string.required));
            returnvalue = true;
        }
        if(efternavn.getText().toString().matches("")){
            efternavn.setError(getResources().getString(R.string.required));
            returnvalue = true;
        }
        if(telefon.getText().toString().matches("")){
            telefon.setError(getResources().getString(R.string.required));
            returnvalue = true;
        }
        if(!isValidEmail(email.getText().toString())){
            returnvalue = true;
            email.setError(getResources().getString(R.string.emailregex));
        }


        return returnvalue;
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private class validateEmail extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            main.controller.ValidateEmail(strings[0]);
            return null;
        }


    }


    public void nextFrag(){
        main.setFornavn(navn.getText().toString());
        main.setEfternavn(efternavn.getText().toString());
        main.setTelefon(telefon.getText().toString());
        main.setEmail(email.getText().toString());
        main.setTitle(title.getText().toString());
        this.getFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.register_frag
                        , new RegisterFrag2())
                .commit();
    }
    @Override
    public void onClick(View v) {
        if(v == next){
            if(!checkInputs()){
                new validateEmail().execute(email.getText().toString());
                while(true){
                    if(main.controller.emailvalidate == 1){
                        System.out.println("Email eksisterer");
                        email.setError(getResources().getString(R.string.emailalreadypresent));
                        break;
                    }
                    else if(main.controller.emailvalidate == 2){

                        System.out.println("Email eksisterer ikke");
                        main.controller.emailvalidate = 0;
                        nextFrag();
                        break;

                    }
                }
            }

        }

    }
}
