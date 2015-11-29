package yitgogo.consumer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.store.ui.StoreAreaFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.model.VersionInfo;
import yitgogo.consumer.view.DownloadDialog;
import yitgogo.consumer.view.NormalAskDialog;

public class EntranceActivity extends BaseActivity {

    LocationClient locationClient;
    BDLocation location;
    int locateTime = 0;
    boolean disConnect = false;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_entrance);
        initLocationTool();
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(EntranceActivity.class.getName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(EntranceActivity.class.getName());
        if (disConnect) {
            if (isConnected()) {
                disConnect = false;
                checkUpdate();
            } else {
                NormalAskDialog askDialog = new NormalAskDialog(
                        "无法连接网络，请检查网络设置！", "查看设置", "退出", false) {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        super.onDismiss(dialog);
                        if (makeSure) {
                            Intent intent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(intent);
                        } else {
                            finish();
                        }
                    }
                };
                askDialog.show(getSupportFragmentManager(), null);
            }
        }
    }

    /**
     * 检查网络连通性
     */
    private void checkConnection() {
        if (isConnected()) {
            //能访问网络，检查更新
            checkUpdate();
        } else {
            //不能访问网络
            disConnect = true;
        }
    }

    /**
     * 判断是否需要更新配送点
     */
    private void shouldUpdateStore() {
        if (Store.getStore() == null) {
            ApplicationTool.showToast("正在查找附近的服务中心");
            updateStore(true);
        } else {
            if (Content.getBooleanContent(Parameters.CACHE_KEY_AUTO_LOCATE,
                    true)) {
                updateStore(false);
            } else {
                getLocalBusinessState();
            }
        }
    }

    private void updateStore(boolean must) {
        if (location != null) {
            getNearestStore(must);
        } else {
            if (must) {
                selectJmd();
            } else {
                getLocalBusinessState();
            }
        }
    }

    private void selectJmd() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("firstTime", true);
        jump(StoreAreaFragment.class.getName(), "选择服务中心", bundle);
        finish();
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
                locateTime++;
                // 防止重复定位，locateTime>1 表示已经定位过，无需再定位
                if (locateTime > 1) {
                    return;
                }
                if (bdLocation != null) {
                    if (bdLocation.getLocType() == 61
                            || bdLocation.getLocType() == 65
                            || bdLocation.getLocType() == 161) {
                        location = bdLocation;
                    }
                }
                updateUserLocation();
                shouldUpdateStore();
                locationClient.stop();
            }

        });
    }

    private void locate() {
        locationClient.start();
        locationClient.requestLocation();
    }

    private void getNearestStore(boolean must) {
        final boolean mustGetStore = must;
        RequestParams requestParams = new RequestParams();
        requestParams.add("ak", Parameters.CONSTANT_LBS_AK);
        requestParams.add("geotable_id", Parameters.CONSTANT_LBS_TABLE);
        requestParams.add("sortby", "distance:1");
        requestParams.add("radius", "100000");
        requestParams.add("page_index", "0");
        requestParams.add("page_size", "1");
        requestParams.add("location",
                location.getLongitude() + "," + location.getLatitude());
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(API.API_LBS_NEARBY, requestParams,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        // {"total":102,"contents":[{"uid":723032242,"tags":"","coord_type":3,"jmdNo":"YT445572733429","phone":"15123568974","weight":0,"location":[104.0866308,30.68404769],"jmdId":"7","type":0,"city":"成都市","distance":49,"title":"易田测试加盟店五","geotable_id":96314,"address":"解放路二段六号凤凰大厦","province":"四川省","create_time":1427430737,"icon_style_id":"sid1","bossName":"张帅的","district":"金牛区"}],"status":0,"size":1}
                        ApplicationTool.log("API_LBS_NEARBY", response.toString());
                        if (statusCode == 200) {
                            if (response != null) {
                                JSONArray array;
                                try {
                                    array = response.getJSONArray("contents");
                                    if (array.length() > 0) {
                                        Content.saveIntContent(
                                                Parameters.CACHE_KEY_STORE_TYPE,
                                                Parameters.CACHE_VALUE_STORE_TYPE_LOCATED);
                                        Content.saveStringContent(
                                                Parameters.CACHE_KEY_STORE_JSONSTRING,
                                                array.getString(0));
                                        Store.init(getApplicationContext());
                                        // 自动定位到到最近加盟店，跳转到主页
                                        getLocalBusinessState();
                                        return;
                                    }
                                } catch (JSONException e) {
                                    // 执行到这里说明没有自动定位到到最近加盟店，需要手选
                                    if (mustGetStore) {
                                        selectJmd();
                                    } else {
                                        getLocalBusinessState();
                                    }
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        }
                        // 执行到这里说明没有自动定位到到最近加盟店，需要手选
                        if (mustGetStore) {
                            selectJmd();
                        } else {
                            getLocalBusinessState();
                        }
                    }
                });
    }

    private void checkUpdate() {
        post(API.API_UPDATE, null, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                final VersionInfo versionInfo = new VersionInfo(result);
                if (versionInfo.getVerCode() > ApplicationTool.getVersionCode()) {
                    NormalAskDialog askDialog = new NormalAskDialog(versionInfo) {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            if (makeSure) {
                                DownloadDialog downloadDialog = new DownloadDialog(
                                        versionInfo) {
                                    public void onDismiss(DialogInterface dialog) {
                                        locate();
                                        super.onDismiss(dialog);
                                    }
                                };
                                downloadDialog.show(getSupportFragmentManager(),
                                        null);
                            } else {
                                locate();
                            }
                            super.onDismiss(dialog);
                        }
                    };
                    askDialog.show(getSupportFragmentManager(), null);
                    return;
                }
                locate();
            }
        });
    }

    private void updateUserLocation() {
        if (User.getUser().isLogin()) {
            if (location != null) {
                List<RequestParam> requestParams = new ArrayList<>();
                requestParams.add(new RequestParam("member_account", User.getUser().getUseraccount()));
                requestParams.add(new RequestParam("store_id", Store.getStore().getStoreId()));
                requestParams.add(new RequestParam("location", location.getAddrStr()));
                requestParams.add(new RequestParam("coordinate", location.getLongitude() + "," + location.getLatitude()));
                post(API.API_USER_UPDATE_LOCATION, requestParams, new OnNetworkListener() {
                    @Override
                    public void onSuccess(String result) {

                    }
                });
            }
        }
    }

    private void getLocalBusinessState() {
        GetLocalBusinessState localBusinessState = new GetLocalBusinessState() {

            @Override
            protected void onPostExecute(Boolean showLocalBusiness) {
                Intent intent = new Intent(EntranceActivity.this, MainActivity.class);
                intent.putExtra("showLocalBusiness", showLocalBusiness);
                startActivity(intent);
                finish();
            }
        };
        localBusinessState.execute();
    }

}
