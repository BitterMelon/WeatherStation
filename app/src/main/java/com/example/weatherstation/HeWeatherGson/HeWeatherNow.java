package com.example.weatherstation.HeWeatherGson;

import com.google.gson.annotations.SerializedName;

public class HeWeatherNow {

    public HeWeatherBasic basic;

    public HeWeatherUpdate update;

    public String status;

    public Now now;

    public class Now{
        @SerializedName("tmp")
        public String temperature;//温度

        @SerializedName("cond_txt")
        public String weatherCondition;//天气情况

        @SerializedName("wind_dir")
        public String windDirection;//风向

        @SerializedName("wind_sc")
        public String windLevel;//风力
    }

}
