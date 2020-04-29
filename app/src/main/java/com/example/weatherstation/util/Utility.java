package com.example.weatherstation.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.weatherstation.HeWeatherGson.HeWeatherAirQuality;
import com.example.weatherstation.HeWeatherGson.HeWeatherForecast;
import com.example.weatherstation.HeWeatherGson.HeWeatherLifeStyle;
import com.example.weatherstation.HeWeatherGson.HeWeatherNow;
import com.example.weatherstation.db.City;
import com.example.weatherstation.db.County;
import com.example.weatherstation.db.Province;
import com.example.weatherstation.gson.Weather;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            Log.d("处理省查询回复","进入handleProvinceResponse函数");
            Log.d("省查询回复的内容",response);
            try {
                Gson gson = new Gson();
                List<Province> provinces = gson.fromJson(response,new TypeToken<List<Province>>(){}.getType());
                Log.d("handleProvinceResponse","省的Json数据已解析");
                for (Province province:provinces){
                    province.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!response.isEmpty()){
            try {
                Log.d("handleCityResponse",response);
                Gson gson = new Gson();
                List<City> cities = gson.fromJson(response,new TypeToken<List<City>>(){}.getType());
                for(City city:cities){
                    Log.d("handleCityResponse",city.getId()+"---"+city.getName());
                    city.setCityCode(city.getId());
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!response.isEmpty()){
            try {
                Gson gson = new Gson();
                List<County> counties = gson.fromJson(response,new TypeToken<List<County>>(){}.getType());
                for (County county:counties){
                    Log.d("handleCountyResponse",county.getId()+"---"+county.getName()
                            +"---"+county.getWeather_id());
                    county.setCountyCode(county.getId());
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的Json数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 将返回的Json数据解析成HeWeather实体类
     */
    public static HeWeatherNow handleHeWeatherNowResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, HeWeatherNow.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static HeWeatherForecast handleHeWeatherForecastResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, HeWeatherForecast.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static HeWeatherLifeStyle handleHeWeatherLifeStyleResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, HeWeatherLifeStyle.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static HeWeatherAirQuality handleHeWeatherAirQualityResponse(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,HeWeatherAirQuality.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
