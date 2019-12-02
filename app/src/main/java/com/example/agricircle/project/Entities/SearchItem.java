package com.example.agricircle.project.Entities;

import java.io.Serializable;
import java.util.List;

public class SearchItem implements Serializable {
    public String field;
    public List<String> activities;
    public String crops;

    public SearchItem(String field, List<String> activities, String crops) {
        this.field = field;
        this.activities = activities;
        this.crops = crops;
    }


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public String getCrops() {
        return crops;
    }

    public void setCrops(String crops) {
        this.crops = crops;
    }
}
