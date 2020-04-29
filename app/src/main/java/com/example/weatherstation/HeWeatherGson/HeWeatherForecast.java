package com.example.weatherstation.HeWeatherGson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HeWeatherForecast {

    public HeWeatherBasic basic;

    public HeWeatherUpdate update;

    public String status;

    @SerializedName("daily_forecast")
    public List<DailyForecast> dailyForecastList;

    public class DailyForecast{
        @SerializedName("date")
        public String forecastData;//预报时间

        @SerializedName("cond_txt_d")
        public String conditionDay;//白天天气情况

        @SerializedName("cond_txt_n")
        public String conditionNight;//晚上天气情况

        @SerializedName("tmp_max")
        public String maxTemperature;//最高温度

        @SerializedName("tmp_min")
        public String minTemperature;//最低温度
    }
}
