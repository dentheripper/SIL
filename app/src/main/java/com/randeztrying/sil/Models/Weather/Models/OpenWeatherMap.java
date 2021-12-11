package com.randeztrying.sil.Models.Weather.Models;

import java.util.List;

public class OpenWeatherMap {

    private int cod;
    private int message;
    private int cnt;
    private List<WeatherOneDay> list;
    private City city;

    public OpenWeatherMap(int cod, int message, int cnt, List<WeatherOneDay> list, City city) {
        this.cod = cod;
        this.message = message;
        this.cnt = cnt;
        this.list = list;
        this.city = city;
    }

    public OpenWeatherMap() {
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public List<WeatherOneDay> getList() {
        return list;
    }

    public void setList(List<WeatherOneDay> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
