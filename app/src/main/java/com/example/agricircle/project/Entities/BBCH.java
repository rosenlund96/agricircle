package com.example.agricircle.project.Entities;

public class BBCH {
    int id, bbchFrom,bbchTo;
    String image;

    public BBCH(int id, int bbchFrom, int bbchTo, String image) {
        this.id = id;
        this.bbchFrom = bbchFrom;
        this.bbchTo = bbchTo;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBbchFrom() {
        return bbchFrom;
    }

    public void setBbchFrom(int bbchFrom) {
        this.bbchFrom = bbchFrom;
    }

    public int getBbchTo() {
        return bbchTo;
    }

    public void setBbchTo(int bbchTo) {
        this.bbchTo = bbchTo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
