package com.randeztrying.sil.Models.Weather.Models;

public class City {

    private int id;
    private String name;
    private Coord coord;
    private String country;
    private double sunrise;
    private double sunset;

    public City(int id, String name, Coord coord, String country, double sunrise, double sunset) {
        this.id = id;
        this.name = name;
        this.coord = coord;
        this.country = country;
        this.sunrise = sunrise;
        this.sunset = sunset;
    }

    public City() {
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

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getSunrise() {
        return sunrise;
    }

    public void setSunrise(double sunrise) {
        this.sunrise = sunrise;
    }

    public double getSunset() {
        return sunset;
    }

    public void setSunset(double sunset) {
        this.sunset = sunset;
    }
}
