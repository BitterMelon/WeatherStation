package com.example.weatherstation.HeWeatherGson;


import com.google.gson.annotations.SerializedName;

public class HeWeatherBasic {

    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String weatherId;



}
