package com.example.agricircle.project.Entities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.agricircle.R;

public class PopUp extends Activity implements View.OnClickListener {

    public Button okay,cancel;
    public TextView Overskrift, Brødtekst;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_modal);
        Overskrift = (TextView) findViewById(R.id.modaloverskrift);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        Intent PromptIntent = getIntent();
        final int antal = PromptIntent.getIntExtra("antal",1);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        System.out.println(antal);

        if (antal == 2){
            getWindow().setLayout((int) (width * .9), (int) (height * .60));
            System.out.println("Højden bør være .55");
        }
        else if (antal == 1) {
            getWindow().setLayout((int) (width * .9), (int) (height * .7));
            System.out.println("Højden bør være .7");
        }
        okay = (Button) findViewById(R.id.alert_btn_okay);
        cancel = (Button) findViewById(R.id.alert_btn_cancel);
        okay.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    public void setTekst(String overskrift, String brødtekst){
        Brødtekst.setText(brødtekst);
        Overskrift.setText(overskrift);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == okay.getId()){
            finish();
        }
    }
}

