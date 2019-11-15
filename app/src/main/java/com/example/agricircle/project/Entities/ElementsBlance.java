package com.example.agricircle.project.Entities;

public class ElementsBlance {
    private double N;
    private double S;
    private double CaO;
    private double K2O;
    private double MgO;
    private double P2O5;


    public ElementsBlance(double n, double s, double caO, double k2O, double mgO, double p2O5) {
        N = n;
        S = s;
        CaO = caO;
        K2O = k2O;
        MgO = mgO;
        P2O5 = p2O5;
    }

    public double getN() {
        return N;
    }

    public void setN(double n) {
        N = n;
    }

    public double getS() {
        return S;
    }

    public void setS(double s) {
        S = s;
    }

    public double getCaO() {
        return CaO;
    }

    public void setCaO(double caO) {
        CaO = caO;
    }

    public double getK2O() {
        return K2O;
    }

    public void setK2O(double k2O) {
        K2O = k2O;
    }

    public double getMgO() {
        return MgO;
    }

    public void setMgO(double mgO) {
        MgO = mgO;
    }

    public double getP2O5() {
        return P2O5;
    }

    public void setP2O5(double p2O5) {
        P2O5 = p2O5;
    }
}
