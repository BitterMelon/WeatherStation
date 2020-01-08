package com.example.weatherstation.db;


import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {
    private int id;
    private String name;
    private String provinceName;
    private int provinceCode;


    public String getName() {
        //provinceName = name;
        return name;
    }

    public void setName(String name) {
        this.name = name;
        //setProvinceName(name);
        //this.provinceName = name;
    }

    public int getId() {
        //provinceCode = id;
        return id;
    }

    public void setId(int id) {
        this.id = id;
        //setProvinceCode(id);
        //this.provinceCode = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
