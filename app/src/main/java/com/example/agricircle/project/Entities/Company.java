package com.example.agricircle.project.Entities;

public class Company {
    int id;
    String name;
    String access;
    String owner_photo_url;
    boolean Default;

    public Company(int id, String name, String access, String owner_photo_url, boolean aDefault) {
        this.id = id;
        this.name = name;
        this.access = access;
        this.owner_photo_url = owner_photo_url;
        Default = aDefault;
    }

    public int getId() {
        return id;
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

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getOwner_photo_url() {
        return owner_photo_url;
    }

    public void setOwner_photo_url(String owner_photo_url) {
        this.owner_photo_url = owner_photo_url;
    }

    public boolean isDefault() {
        return Default;
    }

    public void setDefault(boolean aDefault) {
        Default = aDefault;
    }
}
