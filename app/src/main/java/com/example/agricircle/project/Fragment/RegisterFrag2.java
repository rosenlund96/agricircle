package com.example.agricircle.project.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.agricircle.project.Activities.MainActivity;
import com.example.agricircle.project.Activities.RegisterActivity;
import com.example.agricircle.project.Controller.UserController;
import com.example.agricircle.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

public class RegisterFrag2 extends Fragment implements View.OnClickListener {
    View myView;
    MaterialBetterSpinner state,language;
    Button next;
    CircularImageView farmer, contractor,agronomist;
    EditText companyName,companyAdress,companyCity, zip;
    String selected;
    RegisterActivity main;
    UserController UsrController;
    Dialog myDialog;





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.register_frag2, container, false);
        UsrController = new UserController(null);
        myDialog = new Dialog(getContext());
        next = (Button) myView.findViewById(R.id.registerbutton2);
        next.setFocusable(true);
        next.setFocusableInTouchMode(true);///add this line
        next.setOnClickListener(this);
        String[] titleData = {"Ms","Mr"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, titleData);
        state = (MaterialBetterSpinner)
                myView.findViewById(R.id.state);
        state.setAdapter(arrayAdapter);

        String[] languageData = getResources().getStringArray(R.array.languages);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this.getActivity(),
                android.R.layout.simple_dropdown_item_1line, languageData);
        language = (MaterialBetterSpinner)
                myView.findViewById(R.id.language);
        language.setAdapter(arrayAdapter2);
        selected = "";
        state.setBackground(getActivity().getDrawable(R.drawable.textedit_custom));
        language.setBackground(getActivity().getDrawable(R.drawable.textedit_custom));
        farmer = (CircularImageView) myView.findViewById(R.id.farmer);
        contractor = (CircularImageView) myView.findViewById(R.id.contractor);
        agronomist = (CircularImageView) myView.findViewById(R.id.agronomist);
        farmer.setOnClickListener(this);
        contractor.setOnClickListener(this);
        agronomist.setOnClickListener(this);
        companyName = (EditText) myView.findViewById(R.id.companyname);
        companyAdress = (EditText) myView.findViewById(R.id.companyadress);
        companyCity = (EditText) myView.findViewById(R.id.companycity);
        zip = (EditText) myView.findViewById(R.id.zipcode);
        main = RegisterActivity.getInstance();





        return myView;
    }
    public boolean checkInputs(){
        boolean returnvalue = false;
        if(companyAdress.getText().toString().matches("")){
            companyAdress.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(zip.getText().toString().matches("")){
            zip.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(companyCity.getText().toString().matches("")){
            companyCity.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(companyName.getText().toString().matches("")){
            companyName.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(language.getText().toString().matches("")){
            language.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(state.getText().toString().matches("")){
            state.setError("Felt skal udfyldes");
            returnvalue = true;
        }
        if(selected.matches("")){
            Toast.makeText(getActivity(), "Vælg venligst en rolle",
                    Toast.LENGTH_LONG).show();
            returnvalue = true;
        }

        return returnvalue;
    }


    public void toggleSubscription(View v){
        farmer.setBackgroundDrawable(getActivity().getDrawable(R.drawable.farmer));
        contractor.setBackgroundDrawable(getActivity().getDrawable(R.drawable.contractor));
        agronomist.setBackgroundDrawable(getActivity().getDrawable(R.drawable.agronomist));
        if(v == farmer){
            farmer.setBackgroundDrawable(getActivity().getDrawable(R.drawable.farmer_selected));
            selected = "farmer";
        }
        else if(v==contractor){
            contractor.setBackgroundDrawable(getActivity().getDrawable(R.drawable.contractor_selected));
            selected = "contractor";
        }
        else if(v == agronomist){
            agronomist.setBackgroundDrawable(getActivity().getDrawable(R.drawable.agronomist_selected));
            selected = "agronomist";
        }


    }
    public void createUser(String companyname,String companyadress,String companyzip, String language, String selected, String companycity){
        UsrController.createNewUser(main.getUserTitle(),main.getFornavn(),main.getEfternavn(),main.getTelefon(),main.getEmail(),companyname,companyadress,companyzip,companycity,language,selected);
        Button okay,cancel;
        TextView overskrift, tekst;
        myDialog.setContentView(R.layout.popup_modal);
        overskrift = (TextView) myDialog.findViewById(R.id.modaloverskrift);
        tekst = (TextView) myDialog.findViewById(R.id.modaltekst);
        okay = (Button) myDialog.findViewById(R.id.alert_btn_okay);
        cancel = (Button) myDialog.findViewById(R.id.alert_btn_cancel);
        overskrift.setText(R.string.usercreatedTitle);
        tekst.setText(R.string.usercreated);
        okay.setText(R.string.gotologon);
        cancel.setText(R.string.gotosite);
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
                String url = "https://core.agricircle.com/users/password/new?locale=en";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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
        if(v == farmer || v == contractor || v == agronomist ){

        toggleSubscription(v);
        }
        if(v == next){
            if(!checkInputs()){
               createUser(companyName.getText().toString(),companyAdress.getText().toString(),zip.getText().toString(),language.getText().toString(),selected,companyCity.getText().toString());
            }

        }

    }
}
