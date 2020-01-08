package com.example.weatherstation;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.weatherstation.db.City;
import com.example.weatherstation.db.County;
import com.example.weatherstation.db.Province;
import com.example.weatherstation.util.HttpUtil;
import com.example.weatherstation.util.Utility;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog mProgressDialog;
    private TextView mTitleText;
    private Button mBackButton;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> mDataList = new ArrayList<>();
    //private TextView mTestText;

    /**
     * 省列表
     */
    private List<Province> mProvinceList;

    /**
     * 市列表
     */
    private List<City> mCityList;

    /**
     * 县列表
     */
    private List<County> mCountyList;

    /**
     * 选中的省份
     */
    private Province mSelectedProvince;

    /**
     * 选中的城市
     */
    private City mSelectedCity;

    /**
     * 当前选中的级别
     */
    private int mCurrentLevel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.choose_area,container,false);
        mTitleText = (TextView) view.findViewById(R.id.title_text);
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,mDataList);
        mListView.setAdapter(mAdapter);
        //mTestText = (TextView) view.findViewById(R.id.test_text);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mCurrentLevel==LEVEL_PROVINCE){
                    mSelectedProvince = mProvinceList.get(position);
                    queryCities();
                }else if(mCurrentLevel==LEVEL_CITY){
                    mSelectedCity = mCityList.get(position);
                    queryCounties();
                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mCurrentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if(mCurrentLevel==LEVEL_CITY){
                    queryProvinces();
                }
            }
        });
        queryProvinces();
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://guolin.tech/api/china")
                            .build();
                    Response response = client.newCall(request).execute();
                    final String responseData = response.body().string();
                    Log.d("ChooseAreaFragment",responseData);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTestText.setText(responseData);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    /**
     * 查询全国所有的省，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryProvinces(){
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE);
        mProvinceList = LitePal.findAll(Province.class);
        Log.i("TAG", "queryProvinces, mProvinceList: " + (mProvinceList != null ? mProvinceList.size() + "" : "null"));
        if(mProvinceList.size()>0){
            mDataList.clear();
            for(Province province:mProvinceList){
                mDataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china";
            //String address = "http://www.baidu.com";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询选中省内所有的市，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCities(){
        mTitleText.setText(mSelectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCityList = LitePal.where("provinceid = ?",String.valueOf(mSelectedProvince.getId()))
                .find(City.class);
        if(mCityList.size()>0){
            mDataList.clear();
            for(City city:mCityList){
                mDataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
        }else {
            int provinceCode = mSelectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到再去服务器上查询
     */
    private void queryCounties(){
        mTitleText.setText(mSelectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = LitePal.where("cityid = ?",String.valueOf(mSelectedCity.getId()))
                .find(County.class);
        if(mCountyList.size()>0){
            mDataList.clear();
            for(County county:mCountyList){
                mDataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = mSelectedProvince.getProvinceCode();
            int cityCode = mSelectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address,final String type){
        Log.i("TAG", "queryFromServer, address: " + address);
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", "onFailure");
                //回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(),"加载失败",Toast.LENGTH_SHORT).show();
                        Log.d("ChooseAreaFragment","加载失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("TAG", "onResponse");
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText,mSelectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText,mSelectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if(mProgressDialog == null){
            mProgressDialog  = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    /**
     *  关闭进度对话框
     */
    private void closeProgressDialog(){
        if(mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }
}
