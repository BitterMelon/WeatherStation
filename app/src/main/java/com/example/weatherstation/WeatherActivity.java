package com.example.weatherstation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherstation.HeWeatherGson.HeWeatherAirQuality;
import com.example.weatherstation.HeWeatherGson.HeWeatherForecast;
import com.example.weatherstation.HeWeatherGson.HeWeatherLifeStyle;
import com.example.weatherstation.HeWeatherGson.HeWeatherNow;
import com.example.weatherstation.gson.Forecast;
import com.example.weatherstation.gson.Weather;
import com.example.weatherstation.util.HttpUtil;
import com.example.weatherstation.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView windDirection;
    private TextView windLevel;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    //private TextView pm25Text;
    private TextView airQuality;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private TextView travelText;
    private TextView ultravioletText;
    private TextView fluText;
    private TextView dressingText;

    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    private Button navButton;
    public String weatherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT>=21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        //初始化各控件
        weatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        titleCity = (TextView) findViewById(R.id.title_city);
        titleUpdateTime = (TextView) findViewById(R.id.title_update_time);
        degreeText = (TextView) findViewById(R.id.degree_text);
        weatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        windDirection = (TextView) findViewById(R.id.wind_direction);
        windLevel = (TextView) findViewById(R.id.wind_level);
        forecastLayout = (LinearLayout) findViewById(R.id.forecast_layout);
        aqiText = (TextView) findViewById(R.id.aqi_text);
        //pm25Text = (TextView) findViewById(R.id.pm25_text);
        airQuality = (TextView) findViewById(R.id.pm25_text);
        comfortText = (TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        travelText = (TextView) findViewById(R.id.travel_text);
        ultravioletText = (TextView) findViewById(R.id.ultraviolet_text);
        fluText = (TextView) findViewById(R.id.flu_text);
        dressingText = (TextView) findViewById(R.id.dressing_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navButton = (Button) findViewById(R.id.nav_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic",null);
        if(bingPic != null){
            Glide.with(this).load(bingPic).into(bingPicImg);
        }else {
            loadBingPic();
        }
        String heWeatherNowString = prefs.getString("HeWeatherNow",null);
        String heWeatherForecastString = prefs.getString("HeWeatherForecast",null);
        String heWeatherLifeStyleString = prefs.getString("HeWeatherLifeStyle",null);
        String heWeatherAirQualityString = prefs.getString("HeWeatherAirQuality",null);
        if(heWeatherNowString != null && heWeatherForecastString != null
            && heWeatherLifeStyleString != null && heWeatherAirQualityString != null){
            //有缓存时直接解析天气数据
            //Weather weather = Utility.handleWeatherResponse(weatherString);
            HeWeatherNow heWeatherNow = Utility.handleHeWeatherNowResponse(heWeatherNowString);
            HeWeatherForecast heWeatherForecast = Utility.handleHeWeatherForecastResponse(heWeatherForecastString);
            HeWeatherLifeStyle heWeatherLifeStyle = Utility.handleHeWeatherLifeStyleResponse(heWeatherLifeStyleString);
            HeWeatherAirQuality heWeatherAirQuality = Utility.handleHeWeatherAirQualityResponse(heWeatherAirQualityString);
            weatherId = heWeatherNow.basic.weatherId;
            //当前天气情况
            titleCity.setText(heWeatherNow.basic.cityName);
            titleUpdateTime.setText(heWeatherNow.update.updateTime.split(" ")[1]);
            degreeText.setText(heWeatherNow.now.temperature+"℃");
            weatherInfoText.setText(heWeatherNow.now.weatherCondition);
            windDirection.setText(heWeatherNow.now.windDirection);
            windLevel.setText(heWeatherNow.now.windLevel+"级");
            //天气预报
            forecastLayout.removeAllViews();
            for(HeWeatherForecast.DailyForecast forecast:heWeatherForecast.dailyForecastList){
                View view = LayoutInflater.from(WeatherActivity.this).
                        inflate(R.layout.forecast_item,forecastLayout,false);
                TextView dateText = view.findViewById(R.id.data_text);
                TextView infoText = view.findViewById(R.id.info_text);
                TextView maxText = view.findViewById(R.id.max_text);
                TextView minText = view.findViewById(R.id.min_text);
                dateText.setText(forecast.forecastData);
                infoText.setText(forecast.conditionDay+"->"+forecast.conditionNight);
                maxText.setText(forecast.maxTemperature);
                minText.setText(forecast.minTemperature);
                forecastLayout.addView(view);
            }
            //空气质量
            aqiText.setText(heWeatherAirQuality.airNowCity.aqi);
            airQuality.setText(heWeatherAirQuality.airNowCity.quality);
            //生活指数
            for(HeWeatherLifeStyle.LifeStyle lifeStyle:heWeatherLifeStyle.lifeStyleList){
                if(lifeStyle.type.equals("comf")){
                    //Log.d("和风天气舒适度",lifeStyle.lifeIndexText);
                    comfortText.setText("舒适度："+lifeStyle.lifeIndexText);
                }else if(lifeStyle.type.equals("cw")){
                    //Log.d("和风天气洗车指数",lifeStyle.lifeIndexText);
                    carWashText.setText("洗车指数："+lifeStyle.lifeIndexText);
                }else if(lifeStyle.type.equals("sport")){
                    //Log.d("和风天气运动指数",lifeStyle.lifeIndexText);
                    sportText.setText("运动指数："+lifeStyle.lifeIndexText);
                }else if(lifeStyle.type.equals("drsg")){
                    dressingText.setText("穿衣指数："+lifeStyle.lifeIndexText);
                }else if(lifeStyle.type.equals("uv")){
                    ultravioletText.setText("紫外线指数："+lifeStyle.lifeIndexText);
                }else if(lifeStyle.type.equals("flu")){
                    fluText.setText("感冒指数："+lifeStyle.lifeIndexText);
                }else if(lifeStyle.type.equals("trav")){
                    travelText.setText("旅游指数："+lifeStyle.lifeIndexText);
                }
            }
        } else {
            //无缓存时去服务器查询天气
            weatherId = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            //requestWeather(weatherId);
            requestHeWeather(weatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //requestWeather(weatherId);
                requestHeWeather(weatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * 根据天气id向和风天气请求天气信息
     */
    public void requestHeWeather(String weatherId){
        requestHeWeatherByType(weatherId,"now");
        requestHeWeatherByType(weatherId,"forecast");
        requestHeWeatherByType(weatherId,"lifestyle");
        requestHeWeatherAirQuality(weatherId);
        loadBingPic();
    }


    private void requestHeWeatherByType(String weatherId, final String weatherType){
        String weatherUrl = "https://free-api.heweather.net/s6/weather/"+weatherType+
                "?location="+weatherId+"&key=f707fed750f34f3989f56ecaf21f61a8";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息"+weatherType+"失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                if(weatherType.equals("now")){
                    final HeWeatherNow heWeatherNow = Utility.handleHeWeatherNowResponse(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(heWeatherNow != null && "ok".equals(heWeatherNow.status)){
                                SharedPreferences.Editor editor = PreferenceManager.
                                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("HeWeatherNow",responseText);
                                editor.apply();
                                titleCity.setText(heWeatherNow.basic.cityName);
                                titleUpdateTime.setText(heWeatherNow.update.updateTime.split(" ")[1]);
                                degreeText.setText(heWeatherNow.now.temperature+"℃");
                                weatherInfoText.setText(heWeatherNow.now.weatherCondition);
                                windDirection.setText(heWeatherNow.now.windDirection);
                                windLevel.setText(heWeatherNow.now.windLevel+"级");
                            }else {
                                Toast.makeText(WeatherActivity.this,"获取天气信息"+weatherType+"失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                            swipeRefresh.setRefreshing(false);
                            weatherLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    //Log.d("和风天气",responseText);
/*                    Log.d("和风天气basic",heWeatherNow.basic.cityName+"---"+heWeatherNow.basic.weatherId);
                    Log.d("和风天气update",heWeatherNow.update.updateTime);
                    Log.d("和风天气status",heWeatherNow.status);
                    Log.d("气温",heWeatherNow.now.temperature);
                    Log.d("天气情况",heWeatherNow.now.weatherCondition);
                    Log.d("风力",heWeatherNow.now.windLevel);
                    Log.d("风向",heWeatherNow.now.windDirection);*/
                }else if(weatherType.equals("forecast")){
                    //Log.d("和风天气预报",responseText);
                    final HeWeatherForecast heWeatherForecast = Utility.handleHeWeatherForecastResponse(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(heWeatherForecast != null && heWeatherForecast.status.equals("ok")){
                                SharedPreferences.Editor editor = PreferenceManager.
                                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("HeWeatherForecast",responseText);
                                editor.apply();
                                forecastLayout.removeAllViews();
                                for(HeWeatherForecast.DailyForecast forecast:heWeatherForecast.dailyForecastList){
/*                                    Log.d("预报时间",forecast.forecastData);
                                    Log.d("白天天气情况",forecast.conditionDay);
                                    Log.d("晚上天气情况",forecast.conditionNight);
                                    Log.d("最高温度",forecast.maxTemperature);
                                    Log.d("最低温度",forecast.minTemperature);*/
                                    View view = LayoutInflater.from(WeatherActivity.this).
                                            inflate(R.layout.forecast_item,forecastLayout,false);
                                    TextView dateText = view.findViewById(R.id.data_text);
                                    TextView infoText = view.findViewById(R.id.info_text);
                                    TextView maxText = view.findViewById(R.id.max_text);
                                    TextView minText = view.findViewById(R.id.min_text);
                                    dateText.setText(forecast.forecastData);
                                    infoText.setText(forecast.conditionDay+"->"+forecast.conditionNight);
                                    maxText.setText(forecast.maxTemperature);
                                    minText.setText(forecast.minTemperature);
                                    forecastLayout.addView(view);
                                }
                            }else {
                                Toast.makeText(WeatherActivity.this,"获取天气信息"+weatherType+"失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                            swipeRefresh.setRefreshing(false);
                            weatherLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }else if(weatherType.equals("lifestyle")){
                    //Log.d("和风天气生活建议",responseText);
                    final HeWeatherLifeStyle heWeatherLifeStyle = Utility.handleHeWeatherLifeStyleResponse(responseText);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(heWeatherLifeStyle != null && heWeatherLifeStyle.status.equals("ok")){
                                SharedPreferences.Editor editor = PreferenceManager
                                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                                editor.putString("HeWeatherLifeStyle",responseText);
                                editor.apply();
                                for(HeWeatherLifeStyle.LifeStyle lifeStyle:heWeatherLifeStyle.lifeStyleList){
                                    if(lifeStyle.type.equals("comf")){
                                        //Log.d("和风天气舒适度",lifeStyle.lifeIndexText);
                                        comfortText.setText("舒适度："+lifeStyle.lifeIndexText);
                                    }else if(lifeStyle.type.equals("cw")){
                                        //Log.d("和风天气洗车指数",lifeStyle.lifeIndexText);
                                        carWashText.setText("洗车指数："+lifeStyle.lifeIndexText);
                                    }else if(lifeStyle.type.equals("sport")){
                                        //Log.d("和风天气运动指数",lifeStyle.lifeIndexText);
                                        sportText.setText("运动指数："+lifeStyle.lifeIndexText);
                                    }else if(lifeStyle.type.equals("drsg")){
                                        dressingText.setText("穿衣指数："+lifeStyle.lifeIndexText);
                                    }else if(lifeStyle.type.equals("uv")){
                                        ultravioletText.setText("紫外线指数："+lifeStyle.lifeIndexText);
                                    }else if(lifeStyle.type.equals("flu")){
                                        fluText.setText("感冒指数："+lifeStyle.lifeIndexText);
                                    }else if(lifeStyle.type.equals("trav")){
                                        travelText.setText("旅游指数："+lifeStyle.lifeIndexText);
                                    }
                                }
                            }else {
                                Toast.makeText(WeatherActivity.this,"获取天气信息"+weatherType+"失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                            swipeRefresh.setRefreshing(false);
                            weatherLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void requestHeWeatherAirQuality(String weatherId){
        String weatherUrl = "https://free-api.heweather.net/s6/air/now?"+
                "location="+weatherId+"&key=f707fed750f34f3989f56ecaf21f61a8";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取空气质量信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.d("和风天气空气质量",responseText);
                final HeWeatherAirQuality heWeatherAirQuality = Utility.handleHeWeatherAirQualityResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(heWeatherAirQuality != null && heWeatherAirQuality.status.equals("ok")){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("HeWeatherAirQuality",responseText);
                            editor.apply();
                            aqiText.setText(heWeatherAirQuality.airNowCity.aqi);
                            airQuality.setText(heWeatherAirQuality.airNowCity.quality);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取空气质量信息失败",
                                    Toast.LENGTH_SHORT).show();
                            aqiText.setText("");
                            airQuality.setText("");
                        }
                        swipeRefresh.setRefreshing(false);
                        weatherLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }


/*    private void requestHeWeatherNow(String weatherId){
        String weatherUrl = "https://free-api.heweather.net/s6/weather/now?"+
                "location="+weatherId+"&key=f707fed750f34f3989f56ecaf21f61a8";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final HeWeatherNow heWeatherNow = Utility.handleHeWeatherNowResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(heWeatherNow != null && "ok".equals(heWeatherNow.status)){
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("HeWeather",responseText);
                            editor.apply();
                            String cityName = heWeatherNow.basic.cityName;
                            String updateTime = heWeatherNow.update.updateTime.split(" ")[1];
                            String degree = heWeatherNow.now.temperature+"℃";
                            String condition = heWeatherNow.now.weatherCondition;
                            titleCity.setText(cityName);
                            titleUpdateTime.setText(updateTime);
                            degreeText.setText(degree);
                            weatherInfoText.setText(condition);
                            //showHeWeatherInfo(heWeatherNow);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
                //Log.d("和风天气",responseText);
                Log.d("和风天气basic",heWeatherNow.basic.cityName+"---"+heWeatherNow.basic.weatherId);
                Log.d("和风天气update",heWeatherNow.update.updateTime);
                Log.d("和风天气status",heWeatherNow.status);
                Log.d("气温",heWeatherNow.now.temperature);
                Log.d("天气情况",heWeatherNow.now.weatherCondition);
                Log.d("风力",heWeatherNow.now.windLevel);
                Log.d("风向",heWeatherNow.now.windDirection);
            }
        });
    }*/




    /**
     * 根据天气id请求城市天气信息
     */
   /* public void requestWeather(final String weatherId){
        String weatherUrl = "http://guolin.tech/api/weather?cityid="+weatherId+
                "&key=bc0418b57b2d4918819d3974ac1285d9";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager.
                                    getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }*/

    /**
     * 加载必应每日一图
     */
    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.
                        getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });

    }

    /**
     * 处理并展示Weather实体类中的数据
     */
    /*private void showWeatherInfo(Weather weather){
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature+"℃";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecastsList){
            View view = LayoutInflater.from(this).
                    inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText = view.findViewById(R.id.data_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            //pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数：" + weather.suggestion.carWash.info;
        String sport = "运动建议：" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }*/


}
