package com.example.agricircle.project.Entities;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    public String language;
    public String avatarUrl;
    public String name;
    public String slug;
    public boolean loggedIn;
    public List<Field> fields;
    public List<Company> companies;
    public int primaryCompany;

    public String getLanguage() {
        return language;
    }

    public void addCompany(Company company){
        companies.add(company);
    }
    public List<Company> getCompanies(){
        return this.companies;
    }



    public int getPrimaryCompany(){
        return this.primaryCompany;
    }
    public void setPrimaryCompany(int id){
        this.primaryCompany = id;
    }
    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public User() {
        this.fields = new ArrayList<>();
        this.companies = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "User{" +
                "language='" + language + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", name='" + name + '\'' +
                ", slug='" + slug + '\'' +
                '}';
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        if(this.fields == null){
            fields = new ArrayList<Field>();
        }
        this.fields = fields;
    }

    public void addField(Field field){
        this.fields.add(field);
    }
}
