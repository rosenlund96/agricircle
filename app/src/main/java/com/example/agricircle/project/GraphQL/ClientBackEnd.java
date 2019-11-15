package com.example.agricircle.project.GraphQL;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.agricircle.project.Activities.MainScreenActivity;
import com.example.agricircle.project.GetUserQuery;
import com.example.agricircle.project.LoginInputMutation;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import static com.google.android.gms.plus.PlusOneDummyView.TAG;

public class ClientBackEnd {

    private static final String BASE_URL = "https://graphql.agricircle.com/graphql";
    ApolloClient apolloClient;
    Boolean loginStatus;
    String cookie;





    public ClientBackEnd() {
init(null);

    }

    public void init(String cookie){
        OkHttpClient okHttpClient;
        if(cookie == null){
            okHttpClient = new OkHttpClient.Builder().build();
        }
        else{
            okHttpClient = createOkHttpWithValidToken(cookie);
        }




        apolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)

                .build();
        loginStatus = false;


    }
    public boolean checkLogin(){
        return loginStatus;
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

    public void login(String email, String password, final Context context){

        LoginInputMutation login = LoginInputMutation.builder()
                                    .user(email)
                                    .pass(password)
                                    .build();
        //System.out.println("Mail: " + login.variables().user() + " Pass: " + login.variables().pass() );

        apolloClient.mutate(login).enqueue(new ApolloCall.Callback<LoginInputMutation.Data>() {

            @Override
            public void onResponse(@NotNull Response<LoginInputMutation.Data> response) {
                if(!response.hasErrors()){
                    cookie = response.data().login().cookie().get(2);
                    //cookie="_AgriCircle_subdomain_session=SmlEZzEzMlR0YitqSnVkMndHem9Ddlhmb0drR3g5Q0pwdGJVUnBMWGZtRzdiNFRtcE5vdXp2b2J2ZkFkRlVDVStHTlg1ZlZUdVM0cTZGOXRxKy9mMEdjMG04MkVUZGczK2dRK3JsOTdKYjRGVm1JNDlNVFRHTzVrcWRRcmtJc1J0NWR6bmdXM2VxcVVacVpCS1VrYWNZK2VXR2NidXpEbUdzNDNnamhnVnhVPS0tVStNbDFqblZ5U1EzYzkwSTN6UnFiZz09--15f97196700b3eeab76d6e1a18517790c3798152;";
                    init(cookie);
                    System.out.println("Cookie: " + cookie);
                    //getUser();
                    loginStatus = true;


                    Handler mainHandler = new Handler(Looper.getMainLooper());

                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {

                            Intent i = new Intent(context, MainScreenActivity.class);
                            i.putExtra("cookie", cookie);

                            context.startActivity(i);

                        } // This is your code
                    };
                    mainHandler.post(myRunnable);


                }
                else {
                    System.out.println(response.errors());
                    loginStatus = false;
                }


            }

            @Override
            public void onFailure(@NotNull ApolloException e) {
                Log.e(TAG, e.getMessage(), e);
                System.out.println("En fejl er opstået ved login: " + e.getCause());
                loginStatus = false;

            }
        });

    }

    public void getUser(){

        GetUserQuery query = GetUserQuery.builder()
                            .build();


        apolloClient.query(query).enqueue(new ApolloCall.Callback<GetUserQuery.Data>() {
            @Override
            public void onResponse(@NotNull Response<GetUserQuery.Data> response) {
                if(response.hasErrors()){
                    System.out.println(response.errors());
                }
                else {
                    System.out.println(response.data().user().name());
                }

            }

            @Override
            public void onFailure(@NotNull ApolloException e) {

            }
        });

    }
}
