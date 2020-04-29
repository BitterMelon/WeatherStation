package com.example.weatherstation.HeWeatherGson;

import com.google.gson.annotations.SerializedName;

public class HeWeatherAirQuality {
    public HeWeatherBasic basic;

    public HeWeatherUpdate update;

    public String status;

    @SerializedName("air_now_city")
    public AirNowCity airNowCity;

    public class AirNowCity{
        public String aqi;

        @SerializedName("qlty")
        public String quality;
    }
}
