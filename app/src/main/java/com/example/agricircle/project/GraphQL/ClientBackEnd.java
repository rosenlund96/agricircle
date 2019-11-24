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

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ClientBackEnd {

    private static final String BASE_URL = "https://graphql.agricircle.com/graphql";

    String cookie;

    public ClientBackEnd(String cookie) {
        this.cookie = cookie;
    }

    public void createSoilSamplingPath() throws JSONException, UnirestException {
        JSONArray res = getGraphQl("CreateSamplingPath",
                "[{\"query\":\"mutation createSoilPath{createSoilSamplingPath(soilSamplingJobId:153 soilSamplingInput: {minZoneSize:1 samplesPerZone:50 numZonesToSample:5}){errors{message}soilSampling}}\\n\",\"variables\":null,\"operationName\":\"createSoilPath\"}]");
    }

    /**
     * Henter et antal GraphQL objekter fra serveren
     *
     * @param beskrivelse Tekst
     * @param query
     * @return JSON-objekt med data
     */
    private JSONArray getGraphQl(String beskrivelse, String query) throws UnirestException, JSONException {

        HttpResponse<String> response = Unirest.post("https://core.agricircle.com/graphql")
                .header("x-cookie", cookie)
                .header("content-type", "application/json")
                .body(query)
                .asString();

        System.out.println("\n\nAction: " + beskrivelse);
        //System.out.println(response.getHeaders());
        System.out.println("Response code: " + response.getCode() );
        System.out.println("-----------------------------------");
        //System.out.println(response.getBody());
        if (response.getCode() != 200) throw new IllegalArgumentException("Fik fejl i svar");

        JSONArray spmjsonarr = new JSONArray(query);
        JSONArray svarjsonarr = new JSONArray(response.getBody());
        for (int i = 0; i < spmjsonarr.length(); i++) {
            JSONObject qjson = spmjsonarr.getJSONObject(i);
            String gqlq = qjson.getString("query");
            System.out.println("Spørger efter:\n" + qjson.toString(2));
            System.out.println(gqlq.replaceAll("\\n", "\n"));

            System.out.println(svarjsonarr.getJSONObject(i).toString(4));
        }
        return svarjsonarr;
    }















}




