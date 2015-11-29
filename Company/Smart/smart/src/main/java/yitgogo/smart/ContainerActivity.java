package yitgogo.smart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import yitgogo.smart.print.PrintData;
import yitgogo.smart.print.Printer;
import yitgogo.smart.suning.model.API_SUNING;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.weather.model.ModelWeatherInfo;

public class ContainerActivity extends BaseActivity {

    TextView printTest, timeTextView, weatherTextView, printerStateTextView;
    TextView ipTextView;
    TextView disconnectTextView;
    FrameLayout topLayout;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
            "yyyy年MM月dd日 HH点mm分");

    String fragmentName = "";
    Fragment fragment;
    Bundle bundle;

    LocationClient locationClient;
    ModelWeatherInfo weatherInfo = new ModelWeatherInfo();

    BroadcastReceiver broadcastReceiver;

    GetWeatherInfo getWeatherInfo;
    CheckPrinterTask checkPrinterTask;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_container);
        init();
        findViews();
    }

    @Override
    protected void onResume() {
        topLayout.setVisibility(View.GONE);
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        topLayout.setVisibility(View.VISIBLE);
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        if (checkPrinterTask != null) {
            if (checkPrinterTask.getStatus() == Status.RUNNING) {
                checkPrinterTask.cancel(true);
            }
        }
        if (getWeatherInfo != null) {
            if (getWeatherInfo.getStatus() == Status.RUNNING) {
                getWeatherInfo.cancel(true);
            }
        }
        super.onDestroy();
    }

    private void init() {
        bundle = getIntent().getExtras();
        if (bundle.containsKey("fragmentName")) {
            fragmentName = bundle.getString("fragmentName");
        }
        if (fragmentName.length() > 0) {
            try {
                fragment = (Fragment) Class.forName(fragmentName).newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        initReceiver();
        initLocationTool();
    }

    private void initReceiver() {
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {
                    checkConnection();
                } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                    refreshStatusBar(Calendar.getInstance());
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void findViews() {
        printTest = (TextView) findViewById(R.id.container_print);
        timeTextView = (TextView) findViewById(R.id.container_time);
        weatherTextView = (TextView) findViewById(R.id.container_weather);
        printerStateTextView = (TextView) findViewById(R.id.container_printer);
        topLayout = (FrameLayout) findViewById(R.id.container_top);
        disconnectTextView = (TextView) findViewById(R.id.container_disconnect);

        ipTextView = (TextView) findViewById(R.id.container_ip);
        ipTextView.setText(API.API_IP + "\n" + API_SUNING.IP_SUNING);

        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment, fragment).commit();
        }
        refreshStatusBar(Calendar.getInstance());
    }

    private void refreshStatusBar(Calendar calendar) {
        if (weatherTextView.length() > 0) {
            if (calendar.get(Calendar.MINUTE) == 0) {
                getWeather();
            }
        } else {
            getWeather();
        }
        timeTextView.setText(simpleDateFormat.format(calendar.getTime()));
        checkPrinter();
    }

    @Override
    protected void registerViews() {
        disconnectTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        printTest.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // printTest();
            }
        });
    }

    /**
     * 判断是否连接网络
     *
     * @return
     */
    protected boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            if (connectivityManager.getActiveNetworkInfo().isAvailable()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void checkConnection() {
        if (isConnected()) {
            disconnectTextView.setVisibility(View.GONE);
        } else {
            disconnectTextView.setVisibility(View.VISIBLE);
        }
    }

    private void printTest() {
        try {
            JSONObject object = new JSONObject();
            object.put("machineCode", "machineCode");
            object.put("shopname", "打印测试");
            object.put("date", simpleDateFormat.format(new Date()));
            object.put("id", "YT0123456789");
            object.put("consignee", "测试客户");
            object.put("telphone", "0123456789");
            object.put("address", "测试地址");
            object.put("total", 10000);
            object.put("zhekou", 1000);
            object.put("shifu", 9000);
            object.put("servicephone1", "测试手机");
            object.put("servicephone2", "测试座机");
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < 2; i++) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("spname", "测试产品" + (i + 1));
                jsonObject.put("price", 2500);
                jsonObject.put("num", 2);
                jsonObject.put("Amount", 5000);
                jsonArray.put(jsonObject);
            }
            object.put("goodsArr", jsonArray);
            connectPrinter(new PrintData(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void connectPrinter(PrintData printData) {
        final PrintData data = printData;
        new Thread(new Runnable() {

            @Override
            public void run() {
                Printer printer = new Printer(data);
                try {
                    printer.connect();
                    printer.print();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 初始化定位工具
     */
    private void initLocationTool() {
        locationClient = new LocationClient(getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
        option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
        option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
        // option.setScanSpan(5000);//设置发起定位请求的间隔时间为5000ms
        // option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                if (bdLocation != null) {
                    if (bdLocation.getLocType() == 61
                            || bdLocation.getLocType() == 65
                            || bdLocation.getLocType() == 161) {
                        Device.setLocation(bdLocation);
                        getWeatherInfo();
                    }
                }
                locationClient.stop();
            }
        });
    }

    private void locate() {
        locationClient.start();
        locationClient.requestLocation();
    }

    private void showWeatherInfo() {
        if (weatherInfo.getShowapi_res_code() == 0) {
            weatherTextView.setText(weatherInfo.getWeatherCityInfo()
                    .getDistrict()
                    + "天气："
                    + weatherInfo.getWeatherData().getWeather()
                    + "/"
                    + weatherInfo.getWeatherData().getTemperature() + "℃");
        }
    }

    private void getWeather() {
        if (Device.getLocation() == null) {
            locate();
        } else {
            getWeatherInfo();
        }
    }

    private void getWeatherInfo() {
        if (getWeatherInfo != null) {
            if (getWeatherInfo.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getWeatherInfo = new GetWeatherInfo();
        getWeatherInfo.execute();
    }

    // 检查打印机连接状态
    private void checkPrinter() {
        if (checkPrinterTask != null) {
            if (checkPrinterTask.getStatus() == Status.RUNNING) {
                return;
            }
        }
        checkPrinterTask = new CheckPrinterTask() {

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    printerStateTextView.setText("打印机连接正常");
                } else {
                    printerStateTextView.setText("打印机连接异常");
                }
            }
        };
        checkPrinterTask.execute();
    }

    class CheckPrinterTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... paramVarArgs) {
            Printer printer = new Printer(new PrintData());
            try {
                printer.connect(1000);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

    }

    class GetWeatherInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String httpUrl = "http://apis.baidu.com/showapi_open_bus/weather_showapi/point";
            String httpArg = "lng=" + Device.getLocation().getLongitude()
                    + "&lat=" + Device.getLocation().getLatitude()
                    + "&from=5&needMoreDay=0&needIndex=0";
            return request(httpUrl, httpArg);
        }

        @Override
        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    weatherInfo = new ModelWeatherInfo(new JSONObject(result));
                    if (weatherInfo != null) {
                        showWeatherInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @param httpUrl :请求接口
     * @param httpArg :参数
     * @return 返回结果
     */
    public static String request(String httpUrl, String httpArg) {
        BufferedReader reader = null;
        String result = "";
        StringBuffer sbf = new StringBuffer();
        httpUrl = httpUrl + "?" + httpArg;
        try {
            URL url = new URL(httpUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setConnectTimeout(1000);
            connection.setRequestMethod("GET");
            // 填入apikey到HTTP header
            connection.setRequestProperty("apikey",
                    "236f240ba38004776744c8fe36726a53");
            connection.connect();
            InputStream is = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String strRead = null;
            while ((strRead = reader.readLine()) != null) {
                sbf.append(strRead);
                sbf.append("\r\n");
            }
            reader.close();
            result = sbf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
