package com.example.weatherstation.HeWeatherGson;

import android.widget.LinearLayout;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HeWeatherLifeStyle {
    public HeWeatherBasic basic;

    public HeWeatherUpdate update;

    public String status;

    @SerializedName("lifestyle")
    public List<LifeStyle> lifeStyleList;

    public class LifeStyle{
        @SerializedName("brf")
        public String lifeIndexIntroduction;//生活指数介绍

        @SerializedName("txt")
        public String lifeIndexText;//生活指数详细描述

        public String type;//生活指数类型
    }
}
