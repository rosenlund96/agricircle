package com.example.agricircle.project.Entities;

import java.util.List;

public class Product {


    String name;
    int id;
    List<FertilizerElements> fertilizerElementsList;

    public Product(String name, int id, List<FertilizerElements> fertilizerElementsList) {
        this.name = name;
        this.id = id;
        this.fertilizerElementsList = fertilizerElementsList;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<FertilizerElements> getFertilizerElementsList() {
        return fertilizerElementsList;
    }

    public void setFertilizerElementsList(List<FertilizerElements> fertilizerElementsList) {
        this.fertilizerElementsList = fertilizerElementsList;
    }
}
