package com.example.agricircle.project.Entities;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Activity implements Serializable {
    int field_id,crop_id, activity_id;
    String ActivityType, fieldname, url;
    boolean finished;
    String currentTimestamp;
    Date duedate;
    String comment;
    String BBCHname;
    String BBCHImage;
    String Executor;
    List<LatLng> PATH;



    public Activity(int field_id, String activityType, int crop_id, String name, String url, int activity_id, String executor) {
        this.field_id = field_id;
        ActivityType = activityType;
        this.crop_id = crop_id;
        this.fieldname = name;
        this.url = url;
        this.activity_id = activity_id;
        this.BBCHname = null;
        this.BBCHImage = null;
        this.Executor = executor;
        this.comment = "";
        PATH = new ArrayList<>();
    }

    public void setPath(List<LatLng> path){
        this.PATH = path;
    }
    public List<LatLng> getPath(){
        return this.PATH;
    }

    public int getField_id() {
        return field_id;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }

    public void setField_id(int field_id) {
        this.field_id = field_id;
    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public int getCrop_id() {
        return crop_id;
    }

    public void setCrop_id(int crop_id) {
        this.crop_id = crop_id;
    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public void setCurrentTimestamp(String timestamp){
        this.currentTimestamp = timestamp;
    }
    public String getCurrentTimestamp(){
        return this.currentTimestamp;
    }
    public void setFinished(boolean status){
        this.finished = status;
    }
    public boolean getFinished(){
        return this.finished;
    }
    public int getActivity_id(){
        return this.activity_id;
    }
    public void setBBCHname(String BBCHname){
        this.BBCHname = BBCHname;
    }
    public String getBBCHname(){
        return this.BBCHname;
    }
    public void setBBCHImage(String image){
        this.BBCHImage = image;
    }
    public String getBBCHImage(){
        return this.BBCHImage;
    }
    public String getExecutor(){
        return this.Executor;
    }
    public String getComment(){
        return this.comment;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
}
