package com.example.agricircle.project.Util;

import com.example.agricircle.project.Entities.ElementsBlance;
import com.example.agricircle.project.Entities.Field;
import com.example.agricircle.project.Entities.Shape;
import com.google.android.gms.maps.model.LatLng;


import org.json.JSONArray;
import org.json.JSONException;


import java.util.ArrayList;
import java.util.List;

public class Parser {

    public Parser() {
    }


    public List<Field> parseFieldJSON(String json) throws JSONException {
        JSONArray jsonarray = new JSONArray(json);
        JSONArray fields = jsonarray.getJSONObject(0).getJSONObject("data").getJSONArray("fields");
        JSONArray coordinates = jsonarray.getJSONObject(1).getJSONObject("data").getJSONArray("fields");
        List<Field> fieldList = new ArrayList<>();
        System.out.println("Længde: " + fields.length());

        /***************************
         *Parse JSON for each field*
         ***************************/
        for (int i = 0; i < fields.length()-1; i++) {


            //Field
            int id = Integer.parseInt(fields.getJSONObject(i).getString("id"));
            String name = fields.getJSONObject(i).getString("name");
            double surface = coordinates.getJSONObject(i).getDouble("surface");
            boolean fertilizer_enabled = coordinates.getJSONObject(i).getBoolean("fertilizer_enabled");
            int numOfCoordinates = coordinates.getJSONObject(i).getJSONObject("shape").getJSONArray("coordinates").getJSONArray(0).length();



            //Centerpoint
            List<LatLng> coordinates_centerpoint = new ArrayList<>();
            coordinates_centerpoint.add(new LatLng(coordinates.getJSONObject(i).getJSONObject("center_point").getJSONArray("coordinates").getDouble(1),coordinates.getJSONObject(i).getJSONObject("center_point").getJSONArray("coordinates").getDouble(0)));
            String type_centerpoint = coordinates.getJSONObject(i).getJSONObject("center_point").getString("type");


            //Polygon
            List<LatLng> coordinate = new ArrayList<LatLng>();
            String type = coordinates.getJSONObject(i).getJSONObject("shape").getString("type");
            for(int x = 0; x<numOfCoordinates-1;x++){

                //System.out.println("Koordinat x: "+coordinates.getJSONObject(i).getJSONObject("shape").getJSONArray("coordinates").getJSONArray(0).getJSONArray(x).getDouble(0) + " Koordinat y: " +coordinates.getJSONObject(i).getJSONObject("shape").getJSONArray("coordinates").getJSONArray(0).getJSONArray(x).getDouble(1) );
                coordinate.add(new LatLng(coordinates.getJSONObject(i).getJSONObject("shape").getJSONArray("coordinates").getJSONArray(0).getJSONArray(x).getDouble(1),coordinates.getJSONObject(i).getJSONObject("shape").getJSONArray("coordinates").getJSONArray(0).getJSONArray(x).getDouble(0)));

            }




            //Elements balance


            System.out.println("ID: " +id + " - Name: " +name + " - shape: " + type + " - surface: " + surface + " - Fertilizer: " + fertilizer_enabled + " - Centertype: " + type_centerpoint + " - Coordinates Centerpoint: " + coordinates_centerpoint);


            /**************************
             *     Create objects     *
             **************************/

            //Elements_balance
            ElementsBlance elements_balance = new ElementsBlance(0.0,0.0,0.0,0.0,0.0,0.0);
            //Polygon
            Shape shape = new Shape(type,coordinate);
            //Centerpoint
            Shape centerpoint = new Shape(type_centerpoint, coordinates_centerpoint);
            //Feltobjekt med polygon
            //Field field = new Field(id, name, type, shape, surface, name, fertilizer_enabled, centerpoint);
            //Tilføj felt til liste
            //fieldList.add(field);



        }




        return fieldList;
    }
}

