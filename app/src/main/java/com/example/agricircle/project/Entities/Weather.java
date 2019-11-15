package com.example.agricircle.project.Entities;

public class Weather {
    public double t_2m_C, t_max_2m_3h_C, t_min_2m_3h_C, precip_6h_mm, wind_speed_10m_kmh, wind_dir_10m_d;
    public String date;


    public Weather(double t_2m_C, double t_max_2m_3h_C, double t_min_2m_3h_C, double precip_6h_mm, double wind_speed_10m_kmh, double wind_dir_10m_d, String date) {
        this.t_2m_C = t_2m_C;
        this.t_max_2m_3h_C = t_max_2m_3h_C;
        this.t_min_2m_3h_C = t_min_2m_3h_C;
        this.precip_6h_mm = precip_6h_mm;
        this.wind_speed_10m_kmh = wind_speed_10m_kmh;
        this.wind_dir_10m_d = wind_dir_10m_d;
        this.date = date;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public double getT_2m_C() {
        return t_2m_C;
    }

    public void setT_2m_C(int t_2m_C) {
        this.t_2m_C = t_2m_C;
    }

    public double getT_max_2m_3h_C() {
        return t_max_2m_3h_C;
    }

    public void setT_max_2m_3h_C(int t_max_2m_3h_C) {
        this.t_max_2m_3h_C = t_max_2m_3h_C;
    }

    public double getT_min_2m_3h_C() {
        return t_min_2m_3h_C;
    }

    public void setT_min_2m_3h_C(int t_min_2m_3h_C) {
        this.t_min_2m_3h_C = t_min_2m_3h_C;
    }

    public double getPrecip_6h_mm() {
        return precip_6h_mm;
    }

    public void setPrecip_6h_mm(int precip_6h_mm) {
        this.precip_6h_mm = precip_6h_mm;
    }

    public double getWind_speed_10m_kmh() {
        return wind_speed_10m_kmh;
    }

    public void setWind_speed_10m_kmh(int wind_speed_10m_kmh) {
        this.wind_speed_10m_kmh = wind_speed_10m_kmh;
    }

    public double getWind_dir_10m_d() {
        return wind_dir_10m_d;
    }

    public void setWind_dir_10m_d(int wind_dir_10m_d) {
        this.wind_dir_10m_d = wind_dir_10m_d;
    }
}
