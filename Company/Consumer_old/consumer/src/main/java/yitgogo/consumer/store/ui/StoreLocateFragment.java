package yitgogo.consumer.store.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.GetLocalBusinessState;
import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.store.model.ModelStoreLocated;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.LogUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerListView;

public class StoreLocateFragment extends BaseNotifyFragment {

    InnerListView storeListView;
    ImageView refreshButton;
    TextView locationText;
    LocationClient locationClient;
    List<ModelStoreLocated> storeLocateds;
    StoreAdapter storeAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_store_select_locate);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(StoreLocateFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(StoreLocateFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locate();
    }

    private void init() {
        measureScreen();
        initLocationTool();
        storeLocateds = new ArrayList<ModelStoreLocated>();
        storeAdapter = new StoreAdapter();
    }

    @Override
    protected void findViews() {
        storeListView = (InnerListView) contentView
                .findViewById(R.id.locate_stores);
        refreshButton = (ImageView) contentView
                .findViewById(R.id.locate_refresh);
        locationText = (TextView) contentView
                .findViewById(R.id.locate_location);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        storeListView.setAdapter(storeAdapter);
    }

    @Override
    protected void registerViews() {
        refreshButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                locate();
            }
        });
    }

    private void getLocalBusinessState() {
        GetLocalBusinessState localBusinessState = new GetLocalBusinessState() {

            @Override
            protected void onPreExecute() {
                showLoading();
            }

            @Override
            protected void onPostExecute(Boolean showLocalBusiness) {
                hideLoading();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("showLocalBusiness", showLocalBusiness);
                startActivity(intent);
                getActivity().finish();
            }
        };
        localBusinessState.execute();
    }

    class StoreAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return storeLocateds.size();
        }

        @Override
        public Object getItem(int position) {
            return storeLocateds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_store_selected, null);
                holder = new ViewHolder();
                holder.nameTextView = (TextView) convertView
                        .findViewById(R.id.list_store_name);
                holder.addressTextView = (TextView) convertView
                        .findViewById(R.id.list_store_address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ModelStoreLocated storeLocated = storeLocateds.get(position);
            holder.nameTextView.setText(storeLocated.getTitle());
            holder.addressTextView.setText(storeLocated.getAddress());
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Content.saveIntContent(Parameters.CACHE_KEY_STORE_TYPE,
                            Parameters.CACHE_VALUE_STORE_TYPE_LOCATED);
                    Content.saveStringContent(
                            Parameters.CACHE_KEY_STORE_JSONSTRING, storeLocated
                                    .getJsonObject().toString());
                    Store.init(getActivity());
                    getLocalBusinessState();
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView nameTextView, addressTextView;
        }
    }

    private void initLocationTool() {
        locationClient = new LocationClient(getActivity());
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
                // locateTime++;
                // // 防止重复定位，locateTime>1 表示已经定位过，无需再定位
                // if (locateTime > 1) {
                // return;
                // }
                if (bdLocation != null) {
                    if (bdLocation.getLocType() == 61
                            || bdLocation.getLocType() == 65
                            || bdLocation.getLocType() == 161) {
                        locationText.setText(bdLocation.getAddrStr());
                        getNearestStore(bdLocation);
                    } else {
                        locationText.setText("定位失败");
                    }
                } else {
                    locationText.setText("定位失败");
                }
                locationClient.stop();
            }
        });
    }

    private void locate() {
        locationClient.start();
        locationClient.requestLocation();
    }

    private void getNearestStore(BDLocation location) {
        storeLocateds.clear();
        storeAdapter.notifyDataSetChanged();
        RequestParams requestParams = new RequestParams();
        requestParams.add("ak", Parameters.CONSTANT_LBS_AK);
        requestParams.add("geotable_id", Parameters.CONSTANT_LBS_TABLE);
        requestParams.add("sortby", "distance:1");
        requestParams.add("radius", "100000");
        requestParams.add("page_index", "0");
        requestParams.add("page_size", "100");
        requestParams.add("location",
                location.getLongitude() + "," + location.getLatitude());
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(API.API_LBS_NEARBY, requestParams,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONObject response) {
                        // {"total":102,"contents":[{"uid":723032242,"tags":"","coord_type":3,"jmdNo":"YT445572733429","phone":"15123568974","weight":0,"location":[104.0866308,30.68404769],"jmdId":"7","type":0,"city":"成都市","distance":49,"title":"易田测试加盟店五","geotable_id":96314,"address":"解放路二段六号凤凰大厦","province":"四川省","create_time":1427430737,"icon_style_id":"sid1","bossName":"张帅的","district":"金牛区"}],"status":0,"size":1}
                        LogUtil.logInfo("API_LBS_NEARBY", response.toString());
                        if (statusCode == 200) {
                            if (response != null) {
                                JSONArray array;
                                try {
                                    array = response.getJSONArray("contents");
                                    if (array.length() > 0) {
                                        for (int i = 0; i < array.length(); i++) {
                                            storeLocateds.add(new ModelStoreLocated(
                                                    array.optJSONObject(i)));
                                        }
                                        storeAdapter.notifyDataSetChanged();
                                        // 自动定位到到最近加盟店，跳转到主页
                                        return;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // 执行到这里说明没有自动定位到到最近加盟店，需要手选
                    }
                });
    }

}
