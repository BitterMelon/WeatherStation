package com.example.weatherstation.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.weatherstation.db.City;
import com.example.weatherstation.db.County;
import com.example.weatherstation.db.Province;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            Log.d("处理省查询回复","进入handleProvinceResponse函数");
            try {
                Gson gson = new Gson();
                List<Province> provinces = gson.fromJson(response,new TypeToken<List<Province>>(){}.getType());
                for (Province province:provinces){
                    province.setProvinceCode(province.getId());
                    province.setProvinceName(province.getName());
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
                Gson gson = new Gson();
                List<City> cities = gson.fromJson(response,new TypeToken<List<City>>(){}.getType());
                for(City city:cities){
                    city.setCityCode(city.getId());
                    city.setCityName(city.getName());
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
                    county.setCountyName(county.getName());
                    county.setWeatherId(county.getWeather_id());
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

}
