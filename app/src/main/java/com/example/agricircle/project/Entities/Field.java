package com.example.agricircle.project.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Field implements Serializable {

    //ToDo: Add soil_data, rotations Add crops from GraphQL
    private int id;
    private String name;
    private String typename;
    private Shape coordinates;
    private double surface;
    private String display_name;
    private boolean active;
    public int cropid;
    public List<Integer> cropIDS;
    public List<Integer> fieldStrategyIDS;

    private boolean fertilizer_enabled;
    private Shape centerpoint;


    public int getId() {
        return id;
    }

    public int getCropid(){
        return this.cropid;
    }

    public void setCropid(int cropid){
        this.cropid = cropid;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public Shape getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Shape coordinates) {
        this.coordinates = coordinates;
    }

    public double getSurface() {
        return surface;
    }

    public void setSurface(double surface) {
        this.surface = surface;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public boolean isFertilizer_enabled() {
        return fertilizer_enabled;
    }

    public void setFertilizer_enabled(boolean fertilizer_enabled) {
        this.fertilizer_enabled = fertilizer_enabled;
    }

    public Shape getCenterpoint() {
        return centerpoint;
    }

    public void setCenterpoint(Shape centerpoint) {
        this.centerpoint = centerpoint;
    }


    public Field(int id, String name, String typename, Shape coordinates, double surface, String display_name, boolean fertilizer_enabled, Shape centerpoint, int cropid) {
        this.id = id;
        this.name = name;
        this.typename = typename;
        this.coordinates = coordinates;
        this.surface = surface;
        this.display_name = display_name;
        this.fertilizer_enabled = fertilizer_enabled;
        this.centerpoint = centerpoint;
        this.cropid = cropid;
        this.cropIDS = new ArrayList<>();
    }

    public void setCropList(List<Integer> list){
        this.cropIDS = list;
    }
    public List<Integer> getCropsList(){
        return this.cropIDS;
    }
    public void setFieldStrategyList(List<Integer> list){
        this.fieldStrategyIDS = list;
    }
    public List<Integer> getFieldStrategyList(){
        return this.fieldStrategyIDS;
    }
}
