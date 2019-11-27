package com.example.agricircle.project.Entities;

public class FertilizerElements {

    Double amount;
    String symbol;

    public FertilizerElements(Double amount, String symbol) {
        this.amount = amount;
        this.symbol = symbol;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
