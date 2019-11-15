package com.example.agricircle.project.Entities;

public class Activity {
    int field_id,crop_id;
    String ActivityType, fieldname, url;

    public Activity(int field_id, String activityType, int crop_id, String name, String url) {
        this.field_id = field_id;
        ActivityType = activityType;
        this.crop_id = crop_id;
        this.fieldname = name;
        this.url = url;
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
}
