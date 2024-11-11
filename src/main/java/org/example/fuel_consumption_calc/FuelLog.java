package org.example.fuel_consumption_calc;

public class FuelLog {
    private int id;
    private double distance;
    private double fuelUsed;
    private double consumptionPer100Km;
    private String cost;
    private String language;

    public FuelLog(int id, double distance, double fuelUsed, double consumptionPer100Km, String cost, String language) {
        this.id = id;
        this.distance = distance;
        this.fuelUsed = fuelUsed;
        this.consumptionPer100Km = consumptionPer100Km;
        this.cost = cost;
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public double getDistance() {
        return distance;
    }

    public double getFuelUsed() {
        return fuelUsed;
    }

    public double getConsumptionPer100Km() {
        return consumptionPer100Km;
    }

    public String getCost() {
        return cost;
    }

    public String getLanguage() {
        return language;
    }
}
