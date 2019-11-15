package com.example.agricircle.project.Fragment;

import android.os.Bundle;
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
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class RegisterFrag1 extends Fragment implements View.OnClickListener {

    View myView;
    MaterialBetterSpinner title;
    Button next;
    EditText navn, efternavn, telefon, email;
    RegisterActivity main;




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
       boolean returnvalue = false;
        if(title.getText().toString().matches("")){
            title.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(navn.getText().toString().matches("")){
            navn.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(efternavn.getText().toString().matches("")){
            efternavn.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(telefon.getText().toString().matches("")){
            telefon.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(email.getText().toString().matches("")){
            email.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        return returnvalue;
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
                nextFrag();
            }

        }

    }
}
