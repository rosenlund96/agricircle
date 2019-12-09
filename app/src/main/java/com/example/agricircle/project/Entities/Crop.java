package com.example.agricircle.project.Entities;

import java.io.Serializable;
import java.util.List;

public class Crop implements Serializable {
    private int crop_id;
    private List<Integer> field_ids;
    private String name;
    private String photo_url;
    private String typename;

    public Crop(int crop_id, List<Integer> field_ids, String name, String photo_url, String typename) {
        this.crop_id = crop_id;
        this.field_ids = field_ids;
        this.name = name;
        this.photo_url = photo_url;
        this.typename = typename;
    }

    public void setCrop_id(int crop_id) {
        this.crop_id = crop_id;
    }

    public void setField_ids(List<Integer> field_ids) {
        this.field_ids = field_ids;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public int getCrop_id() {
        return crop_id;
    }

    public List<Integer> getField_ids() {
        return field_ids;
    }

    public String getName() {
        return name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public String getTypename() {
        return typename;
    }
}
