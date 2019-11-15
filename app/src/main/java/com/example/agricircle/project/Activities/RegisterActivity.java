package com.example.agricircle.project.Activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.agricircle.project.Fragment.RegisterFrag1;
import com.example.agricircle.R;

public class RegisterActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    String fornavn, efternavn, telefon, email, title;
    private static RegisterActivity sRegisterActivity;

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public String getEfternavn() {
        return efternavn;
    }

    public void setEfternavn(String efternavn) {
        this.efternavn = efternavn;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);
        this.fornavn = "";
        this.efternavn = "";
        this.telefon = "";
        this.email = "";
        this.title = "";
        fragmentManager = getSupportFragmentManager();
        sRegisterActivity = this;
        init();








    }

    public static RegisterActivity getInstance(){
        return sRegisterActivity;
    }
    public void init(){
        fragmentManager.beginTransaction()
                //.addToBackStack(null)
                .replace(R.id.register_frag
                        , new RegisterFrag1())
                .commit();
    }
}
