package yitgogo.consumer.bianmin.traffic.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.bianmin.traffic.model.ModelCity;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;

public class TraffictHistoryFragment extends BaseNetworkFragment {

    ListView listView;
    String provName = "", cityName = "", plateNumber = "", frameNumber = "",
            engineNumber = "", vehicleTypeId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bianmin_traffic_area);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TraffictHistoryFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TraffictHistoryFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getTrafficHistory();
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("provName")) {
                provName = bundle.getString("provName");
            }
            if (bundle.containsKey("cityName")) {
                cityName = bundle.getString("cityName");
            }
            if (bundle.containsKey("plateNumber")) {
                plateNumber = bundle.getString("plateNumber");
            }
            if (bundle.containsKey("frameNumber")) {
                frameNumber = bundle.getString("frameNumber");
            }
            if (bundle.containsKey("engineNumber")) {
                engineNumber = bundle.getString("engineNumber");
            }
            if (bundle.containsKey("vehicleTypeId")) {
                vehicleTypeId = bundle.getString("vehicleTypeId");
            }
        }
    }

    @Override
    protected void findViews() {
        listView = (ListView) contentView
                .findViewById(R.id.traffic_history_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
    }

    @Override
    protected void registerViews() {

    }

    // class ProvinceAdapetr extends BaseAdapter {
    //
    // @Override
    // public int getCount() {
    // return provinces.size();
    // }
    //
    // @Override
    // public Object getItem(int position) {
    // return provinces.get(position);
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // return position;
    // }
    //
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // ViewHolder holder;
    // if (convertView == null) {
    // holder = new ViewHolder();
    // convertView = layoutInflater.inflate(
    // R.layout.list_bianmin_traffic_area, null);
    // holder.provinceTextView = (TextView) convertView
    // .findViewById(R.id.area_province);
    // holder.cityGridView = (InnerGridView) convertView
    // .findViewById(R.id.area_city);
    // convertView.setTag(holder);
    // } else {
    // holder = (ViewHolder) convertView.getTag();
    // }
    // holder.provinceTextView.setText(provinces.get(position).getName());
    // holder.cityGridView.setAdapter(new CityAdapetr(provinces.get(
    // position).getCities()));
    // return convertView;
    // }
    //
    // class ViewHolder {
    // TextView provinceTextView;
    // InnerGridView cityGridView;
    // }
    //
    // }

    class CityAdapetr extends BaseAdapter {

        List<ModelCity> cities = new ArrayList<ModelCity>();

        public CityAdapetr(List<ModelCity> cities) {
            this.cities = cities;
        }

        @Override
        public int getCount() {
            return cities.size();
        }

        @Override
        public Object getItem(int position) {
            return cities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_class_min,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_min_name);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ApplicationTool.dip2px(36));
                holder.textView.setLayoutParams(layoutParams);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(cities.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    private void getTrafficHistory() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("provName", provName));
        requestParams.add(new RequestParam("cityName", cityName));
        requestParams.add(new RequestParam("plateNumber", plateNumber));
        requestParams.add(new RequestParam("frameNumber", frameNumber));
        requestParams.add(new RequestParam("engineNumber", engineNumber));
        requestParams.add(new RequestParam("vehicleTypeId", vehicleTypeId));
        post(API.API_BIANMIN_TRAFFIC_HISTORY, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                // for (int i = 0; i < array.length(); i++) {
                                // provinces.add(new ModelProvince(array
                                // .optJSONObject(i)));
                                // }
                                // if (provinces.size() > 0) {
                                // new GetCities().execute();
                                // }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}
