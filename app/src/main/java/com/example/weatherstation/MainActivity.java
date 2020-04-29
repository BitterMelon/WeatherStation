package com.example.weatherstation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.weatherstation.gson.Weather;
import com.example.weatherstation.util.Utility;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //String weatherString = prefs.getString("weather",null);
        String heWeatherNowString = prefs.getString("HeWeatherNow",null);
        String heWeatherForecastString = prefs.getString("HeWeatherForecast",null);
        String heWeatherLifeStyleString = prefs.getString("HeWeatherLifeStyle",null);
        String heWeatherAirQualityString = prefs.getString("HeWeatherAirQuality",null);
        if(heWeatherNowString != null && heWeatherForecastString != null
                && heWeatherLifeStyleString != null && heWeatherAirQualityString != null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }
/*        if(weatherString != null){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
        }*/
    }
}
