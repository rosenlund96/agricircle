package com.example.agricircle.project.Entities;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Shape {
    private String type;
    private List<LatLng> coordinates;

    public Shape(String type, List<LatLng> coordinates) {
        this.type = type;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }
    public int getNumOfCoordinates(){
        return this.coordinates.size();
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<LatLng> coordinates) {
        this.coordinates = coordinates;
    }
}
