package com.example.agricircle.project.Entities;

public class rotations {
    private int id;
    private int field_id;
    private int crop_id;
    private int year;
    private String created_at;
    private String updated_at;
    private boolean updated;
    private String name;
    private double intensity;
    private String description;
    private int creator_id;
    private int last_logged_crop_stage_id;
    private int current_crop_stage_id;
    private String current_bbch;
    private String yield;
    private String fertilizer_soil_type;
    private int fertilizer_pre_crop_group;
    private String trend;
    private boolean autumn_fertilization;
    private String fertilizer_delivery;
    private int cuts_count;
    private String harvest_type;
    private boolean reminder_left_on_field;
    private int position;
    private int last_added_crop_stage_id;
    private ElementsBlance elements_balance;

    public rotations(int id, int field_id, int crop_id, int year, String created_at, String updated_at, boolean updated, String name, double intensity, String description, int creator_id, int last_logged_crop_stage_id, int current_crop_stage_id, String current_bbch, String yield, String fertilizer_soil_type, int fertilizer_pre_crop_group, String trend, boolean autumn_fertilization, String fertilizer_delivery, int cuts_count, String harvest_type, boolean reminder_left_on_field, int position, int last_added_crop_stage_id, ElementsBlance elements_balance) {
        this.elements_balance = elements_balance;
        this.id = id;
        this.field_id = field_id;
        this.crop_id = crop_id;
        this.year = year;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.updated = updated;
        this.name = name;
        this.intensity = intensity;
        this.description = description;
        this.creator_id = creator_id;
        this.last_logged_crop_stage_id = last_logged_crop_stage_id;
        this.current_crop_stage_id = current_crop_stage_id;
        this.current_bbch = current_bbch;
        this.yield = yield;
        this.fertilizer_soil_type = fertilizer_soil_type;
        this.fertilizer_pre_crop_group = fertilizer_pre_crop_group;
        this.trend = trend;
        this.autumn_fertilization = autumn_fertilization;
        this.fertilizer_delivery = fertilizer_delivery;
        this.cuts_count = cuts_count;
        this.harvest_type = harvest_type;
        this.reminder_left_on_field = reminder_left_on_field;
        this.position = position;
        this.last_added_crop_stage_id = last_added_crop_stage_id;
    }

    public ElementsBlance getElements_balance() {
        return elements_balance;
    }

    public void setElements_balance(ElementsBlance elements_balance) {
        this.elements_balance = elements_balance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getField_id() {
        return field_id;
    }

    public void setField_id(int field_id) {
        this.field_id = field_id;
    }

    public int getCrop_id() {
        return crop_id;
    }

    public void setCrop_id(int crop_id) {
        this.crop_id = crop_id;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    public int getLast_logged_crop_stage_id() {
        return last_logged_crop_stage_id;
    }

    public void setLast_logged_crop_stage_id(int last_logged_crop_stage_id) {
        this.last_logged_crop_stage_id = last_logged_crop_stage_id;
    }

    public int getCurrent_crop_stage_id() {
        return current_crop_stage_id;
    }

    public void setCurrent_crop_stage_id(int current_crop_stage_id) {
        this.current_crop_stage_id = current_crop_stage_id;
    }

    public String getCurrent_bbch() {
        return current_bbch;
    }

    public void setCurrent_bbch(String current_bbch) {
        this.current_bbch = current_bbch;
    }

    public String getYield() {
        return yield;
    }

    public void setYield(String yield) {
        this.yield = yield;
    }

    public String getFertilizer_soil_type() {
        return fertilizer_soil_type;
    }

    public void setFertilizer_soil_type(String fertilizer_soil_type) {
        this.fertilizer_soil_type = fertilizer_soil_type;
    }

    public int getFertilizer_pre_crop_group() {
        return fertilizer_pre_crop_group;
    }

    public void setFertilizer_pre_crop_group(int fertilizer_pre_crop_group) {
        this.fertilizer_pre_crop_group = fertilizer_pre_crop_group;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }

    public boolean isAutumn_fertilization() {
        return autumn_fertilization;
    }

    public void setAutumn_fertilization(boolean autumn_fertilization) {
        this.autumn_fertilization = autumn_fertilization;
    }

    public String getFertilizer_delivery() {
        return fertilizer_delivery;
    }

    public void setFertilizer_delivery(String fertilizer_delivery) {
        this.fertilizer_delivery = fertilizer_delivery;
    }

    public int getCuts_count() {
        return cuts_count;
    }

    public void setCuts_count(int cuts_count) {
        this.cuts_count = cuts_count;
    }

    public String getHarvest_type() {
        return harvest_type;
    }

    public void setHarvest_type(String harvest_type) {
        this.harvest_type = harvest_type;
    }

    public boolean isReminder_left_on_field() {
        return reminder_left_on_field;
    }

    public void setReminder_left_on_field(boolean reminder_left_on_field) {
        this.reminder_left_on_field = reminder_left_on_field;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLast_added_crop_stage_id() {
        return last_added_crop_stage_id;
    }

    public void setLast_added_crop_stage_id(int last_added_crop_stage_id) {
        this.last_added_crop_stage_id = last_added_crop_stage_id;
    }
}
