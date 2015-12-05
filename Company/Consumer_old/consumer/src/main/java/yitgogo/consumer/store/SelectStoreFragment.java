package yitgogo.consumer.store;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.store.model.ModelStoreArea;
import yitgogo.consumer.store.model.ModelStoreLocated;
import yitgogo.consumer.store.model.ModelStoreSelected;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;

public class SelectStoreFragment extends BaseNotifyFragment {

    LinearLayout areaLayout;
    TextView areaTextView;
    ListView listView;

    HashMap<Integer, ModelStoreArea> areaHashMap = new HashMap<>();

    LocationClient locationClient;
    int locateTime = 0;

    boolean firstTime = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_store);
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SelectStoreFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SelectStoreFragment.class.getName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 22) {
            if (resultCode == 23) {
                areaTextView.setText(data.getStringExtra("name"));
                String areaId = data.getStringExtra("id");
                new GetStore().execute(areaId);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Store.getStore() == null) {
            firstTime = true;
            Bundle bundle = new Bundle();
            bundle.putInt("type", SelectAreaFragment.TYPE_GET_AREA);
            jumpForResult(SelectAreaFragment.class.getName(), "选择区域", bundle, 22);
        } else {
            new GetStoreArea().execute();
        }
    }

    @Override
    protected void findViews() {
        areaLayout = (LinearLayout) contentView.findViewById(R.id.select_store_area_layout);
        areaTextView = (TextView) contentView.findViewById(R.id.select_store_area);
        listView = (ListView) contentView.findViewById(R.id.select_store_list);
        initViews();
        registerViews();
    }

    @Override
    protected void registerViews() {
        areaLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                if (Content.getBooleanContent(Parameters.CACHE_KEY_LOCATE_SUCCESS, false)) {
                    if (getAreaType() < 5) {
                        return;
                    } else {
                        bundle.putString("area", getAreaJsonArray());
                        bundle.putInt("type", SelectAreaFragment.TYPE_STORE_LOCATE);
                    }
                } else {
                    bundle.putInt("type", SelectAreaFragment.TYPE_GET_AREA);
                }
                jumpForResult(SelectAreaFragment.class.getName(), "选择区域", bundle, 22);
            }
        });
    }

    class SelectedStoreAdapter extends BaseAdapter {

        List<ModelStoreSelected> stores = new ArrayList<>();

        public SelectedStoreAdapter(List<ModelStoreSelected> stores) {
            this.stores = stores;
        }

        @Override
        public int getCount() {
            return stores.size();
        }

        @Override
        public Object getItem(int position) {
            return stores.get(position);
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
                holder.nameTextView = (TextView) convertView.findViewById(R.id.list_store_name);
                holder.addressTextView = (TextView) convertView.findViewById(R.id.list_store_address);
                holder.selectedImageView = (ImageView) convertView.findViewById(R.id.list_store_selected_image);
                holder.selectedTextView = (TextView) convertView.findViewById(R.id.list_store_selected_text);
                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.list_store_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ModelStoreSelected storeSelected = stores.get(position);
            holder.nameTextView.setText(storeSelected.getServicename());
            holder.addressTextView.setText(storeSelected.getServiceaddress());
            if (Store.getStore() != null) {
                if (Store.getStore().getStoreId().equals(storeSelected.getId())) {
                    holder.selectedTextView.setVisibility(View.VISIBLE);
                    holder.selectedImageView.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundResource(R.drawable.back_white_rec_border_orange);
                } else {
                    holder.selectedTextView.setVisibility(View.GONE);
                    holder.selectedImageView.setVisibility(View.GONE);
                    holder.linearLayout.setBackgroundResource(R.drawable.selector_white_rec_border);
                }
            } else {
                holder.selectedTextView.setVisibility(View.GONE);
                holder.selectedImageView.setVisibility(View.GONE);
                holder.linearLayout.setBackgroundResource(R.drawable.selector_white_rec_border);
            }
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Content.saveIntContent(Parameters.CACHE_KEY_STORE_TYPE, Parameters.CACHE_VALUE_STORE_TYPE_SELECTED);
                    Content.saveStringContent(Parameters.CACHE_KEY_STORE_JSONSTRING, storeSelected.getJsonObject().toString());
                    Store.init(getActivity());
                    if (firstTime) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                    getActivity().finish();
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView nameTextView, addressTextView, selectedTextView;
            ImageView selectedImageView;
            LinearLayout linearLayout;
        }
    }

    class LocatedStoreAdapter extends BaseAdapter {

        List<ModelStoreLocated> stores = new ArrayList<>();

        public LocatedStoreAdapter(List<ModelStoreLocated> stores) {
            this.stores = stores;
        }

        @Override
        public int getCount() {
            return stores.size();
        }

        @Override
        public Object getItem(int position) {
            return stores.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_store_selected, null);
                holder = new ViewHolder();
                holder.nameTextView = (TextView) convertView.findViewById(R.id.list_store_name);
                holder.addressTextView = (TextView) convertView.findViewById(R.id.list_store_address);
                holder.selectedImageView = (ImageView) convertView.findViewById(R.id.list_store_selected_image);
                holder.selectedTextView = (TextView) convertView.findViewById(R.id.list_store_selected_text);
                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.list_store_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ModelStoreLocated storeLocated = stores.get(position);
            holder.nameTextView.setText(storeLocated.getTitle());
            holder.addressTextView.setText(storeLocated.getAddress());
            if (Store.getStore() != null) {
                if (Store.getStore().getStoreId().equals(storeLocated.getJmdId())) {
                    holder.selectedTextView.setVisibility(View.VISIBLE);
                    holder.selectedImageView.setVisibility(View.VISIBLE);
                    holder.linearLayout.setBackgroundResource(R.drawable.back_white_rec_border_orange);
                } else {
                    holder.selectedTextView.setVisibility(View.GONE);
                    holder.selectedImageView.setVisibility(View.GONE);
                    holder.linearLayout.setBackgroundResource(R.drawable.selector_white_rec_border);
                }
            } else {
                holder.selectedTextView.setVisibility(View.GONE);
                holder.selectedImageView.setVisibility(View.GONE);
                holder.linearLayout.setBackgroundResource(R.drawable.selector_white_rec_border);
            }
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Content.saveIntContent(Parameters.CACHE_KEY_STORE_TYPE, Parameters.CACHE_VALUE_STORE_TYPE_LOCATED);
                    Content.saveStringContent(Parameters.CACHE_KEY_STORE_JSONSTRING, storeLocated.getJsonObject().toString());
                    Store.init(getActivity());
                    if (firstTime) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                    getActivity().finish();
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView nameTextView, addressTextView, selectedTextView;
            ImageView selectedImageView;
            LinearLayout linearLayout;
        }
    }

    private String getAreaJsonArray() {
        JSONArray array = new JSONArray();
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(areaHashMap.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getKey() < 5) {
                array.put(entries.get(i).getValue().getJsonObject());
            }
        }
        return array.toString();
    }

    private String getAreaName() {
        StringBuilder builder = new StringBuilder();
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(areaHashMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, ModelStoreArea>>() {
            @Override
            public int compare(Map.Entry<Integer, ModelStoreArea> area1, Map.Entry<Integer, ModelStoreArea> area2) {
                return area1.getKey().compareTo(area2.getKey());
            }
        });
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                builder.append(">");
            }
            builder.append(entries.get(i).getValue().getName());
        }
        return builder.toString();
    }

    private String getAreaId() {
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(areaHashMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, ModelStoreArea>>() {
            @Override
            public int compare(Map.Entry<Integer, ModelStoreArea> area1, Map.Entry<Integer, ModelStoreArea> area2) {
                return area1.getKey().compareTo(area2.getKey());
            }
        });
        return entries.get(entries.size() - 1).getValue().getId();
    }

    private int getAreaType() {
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(areaHashMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, ModelStoreArea>>() {
            @Override
            public int compare(Map.Entry<Integer, ModelStoreArea> area1, Map.Entry<Integer, ModelStoreArea> area2) {
                return area1.getKey().compareTo(area2.getKey());
            }
        });
        return entries.get(entries.size() - 1).getValue().getType();
    }

    class GetStoreArea extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("spid", Store.getStore().getStoreId()));
            return netUtil.postWithoutCookie(API.API_STORE_SELECTED_AREA, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            areaHashMap = new HashMap<>();
                            for (int i = 0; i < array.length(); i++) {
                                ModelStoreArea storeArea = new ModelStoreArea(array.optJSONObject(i));
                                areaHashMap.put(storeArea.getType(), storeArea);
                            }
                            areaTextView.setText(getAreaName());
                            if (Content.getIntContent(Parameters.CACHE_KEY_STORE_TYPE, Parameters.CACHE_VALUE_STORE_TYPE_LOCATED) == Parameters.CACHE_VALUE_STORE_TYPE_LOCATED) {
                                initLocationTool();
                                locate();
                            } else {
                                new GetStore().execute(getAreaId());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GetStore extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("areaId", params[0]));
            return netUtil.postWithoutCookie(API.API_STORE_LIST, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            List<ModelStoreSelected> storeSelecteds = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                storeSelecteds.add(new ModelStoreSelected(array.getJSONObject(i)));
                            }
                            if (storeSelecteds.size() > 0) {
                                listView.setAdapter(new SelectedStoreAdapter(storeSelecteds));
                            } else {
                                loadingEmpty("该区域暂无服务中心");
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 初始化定位工具
     */
    private void initLocationTool() {
        locationClient = new LocationClient(getActivity());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);// 设置定位模式
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
                    if (bdLocation.getLocType() == 61 || bdLocation.getLocType() == 65 || bdLocation.getLocType() == 161) {
                        getNearestStore(bdLocation);
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

    private void getNearestStore(BDLocation location) {
        RequestParams requestParams = new RequestParams();
        requestParams.add("ak", Parameters.CONSTANT_LBS_AK);
        requestParams.add("geotable_id", Parameters.CONSTANT_LBS_TABLE);
        requestParams.add("sortby", "distance:1");
        requestParams.add("radius", "30000");
        requestParams.add("page_index", "0");
        requestParams.add("page_size", "1");
        requestParams.add("location", location.getLongitude() + "," + location.getLatitude());
        AsyncHttpClient httpClient = new AsyncHttpClient();
        httpClient.get(API.API_LBS_NEARBY, requestParams,
                new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (statusCode == 200) {
                            if (response != null) {
                                try {
                                    JSONArray array = response.optJSONArray("contents");
                                    if (array != null) {
                                        List<ModelStoreLocated> storeLocateds = new ArrayList<>();
                                        for (int i = 0; i < array.length(); i++) {
                                            storeLocateds.add(new ModelStoreLocated(array.optJSONObject(i)));
                                        }
                                        listView.setAdapter(new LocatedStoreAdapter(storeLocateds));
                                        if (!storeLocateds.isEmpty()) {
                                            return;
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        // 执行到这里说明没有自动定位到到最近加盟店，需要手选
                        new GetStore().execute(getAreaId());
                    }
                });
    }


}
