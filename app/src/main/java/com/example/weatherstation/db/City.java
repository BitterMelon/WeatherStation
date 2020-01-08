package com.example.weatherstation.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {
    private int id;
    private String name;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        cityName = name;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        cityCode = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
