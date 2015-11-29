package yitgogo.consumer.bianmin.traffic.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import yitgogo.consumer.bianmin.traffic.model.ModelCarType;
import yitgogo.consumer.bianmin.traffic.model.ModelCity;
import yitgogo.consumer.bianmin.traffic.model.ModelProvince;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;

public class TraffictSearchFragment extends BaseNetworkFragment {

    TextView areaTextView, carTypeTextView;
    EditText plateNumberEditText, frameNumberEditText, engineNumberEditText;
    Button searchButton;

    List<ModelProvince> provinces;
    ProvinceAdapetr provinceAdapetr;
    List<ModelCity> cities;
    CityAdapetr cityAdapetr;
    List<ModelCarType> carTypes;
    CarTypeAdapetr carTypeAdapetr;

    ModelProvince province = new ModelProvince();
    ModelCity city = new ModelCity();
    ModelCarType carType = new ModelCarType();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bianmin_traffic_search);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TraffictSearchFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TraffictSearchFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void init() {
        provinces = new ArrayList<>();
        provinceAdapetr = new ProvinceAdapetr();
        cities = new ArrayList<>();
        cityAdapetr = new CityAdapetr();
        carTypes = new ArrayList<>();
        carTypeAdapetr = new CarTypeAdapetr();
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void findViews() {
        areaTextView = (TextView) contentView
                .findViewById(R.id.traffic_search_area);
        carTypeTextView = (TextView) contentView
                .findViewById(R.id.traffic_search_cartype);
        plateNumberEditText = (EditText) contentView
                .findViewById(R.id.traffic_search_plate_number);
        frameNumberEditText = (EditText) contentView
                .findViewById(R.id.traffic_search_frame_number);
        engineNumberEditText = (EditText) contentView
                .findViewById(R.id.traffic_search_engine_number);
        searchButton = (Button) contentView
                .findViewById(R.id.traffic_search_search);
        registerViews();
    }

    @Override
    protected void registerViews() {
        areaTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (provinces.size() <= 0) {
                    getProvince();
                } else {
                    new ProvinceDialog().show(getFragmentManager(), null);
                }
            }
        });
        carTypeTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (carTypes.size() <= 0) {
                    getCarType();
                } else {
                    new CarTypeDialog().show(getFragmentManager(), null);
                }
            }
        });
        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    private void search() {
        if (TextUtils.isEmpty(province.getName())) {
            ApplicationTool.showToast("请选择所在城市");
        } else if (TextUtils.isEmpty(city.getName())) {
            ApplicationTool.showToast("请选择所在城市");
        } else if (TextUtils.isEmpty(carType.getId())) {
            ApplicationTool.showToast("请选择车型");
        } else if (TextUtils.isEmpty(plateNumberEditText.getText().toString()
                .trim())) {
            ApplicationTool.showToast("请输入车牌号");
        } else if (TextUtils.isEmpty(frameNumberEditText.getText().toString()
                .trim())) {
            ApplicationTool.showToast("请输入车架号");
        } else if (TextUtils.isEmpty(engineNumberEditText.getText().toString()
                .trim())) {
            ApplicationTool.showToast("请输入发动机号");
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("provName", province.getName());
            bundle.putString("cityName", city.getName());
            bundle.putString("plateNumber", plateNumberEditText.getText()
                    .toString().trim());
            bundle.putString("frameNumber", frameNumberEditText.getText()
                    .toString().trim());
            bundle.putString("engineNumber", engineNumberEditText.getText()
                    .toString().trim());
            bundle.putString("vehicleTypeId", carType.getId());
            jump(TraffictHistoryFragment.class.getName(), "违章查询", bundle);
        }
    }

    class ProvinceDialog extends DialogFragment {

        View dialogView;
        ListView listView;
        TextView titleTextView, button;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            findViews();
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth()));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
            titleTextView = (TextView) dialogView
                    .findViewById(R.id.dialog_title);
            button = (TextView) dialogView.findViewById(R.id.dialog_button);
            listView = (ListView) dialogView.findViewById(R.id.dialog_list);
            initViews();
        }

        private void initViews() {
            titleTextView.setText("选择所在省");
            button.setText("取消");
            listView.setAdapter(provinceAdapetr);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    province = provinces.get(arg2);
                    getCities();
                    dismiss();
                }
            });
        }
    }

    class CityDialog extends DialogFragment {

        View dialogView;
        ListView listView;
        TextView titleTextView, button;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            findViews();
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth()));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
            titleTextView = (TextView) dialogView
                    .findViewById(R.id.dialog_title);
            button = (TextView) dialogView.findViewById(R.id.dialog_button);
            listView = (ListView) dialogView.findViewById(R.id.dialog_list);
            initViews();
        }

        private void initViews() {
            titleTextView.setText("选择所在城市");
            button.setText("取消");
            listView.setAdapter(cityAdapetr);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    city = cities.get(arg2);
                    areaTextView.setText(province.getName() + ""
                            + city.getName());
                    dismiss();
                }
            });
        }
    }

    class CarTypeDialog extends DialogFragment {

        View dialogView;
        ListView listView;
        TextView titleTextView, button;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            findViews();
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth()));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
            titleTextView = (TextView) dialogView
                    .findViewById(R.id.dialog_title);
            button = (TextView) dialogView.findViewById(R.id.dialog_button);
            listView = (ListView) dialogView.findViewById(R.id.dialog_list);
            initViews();
        }

        private void initViews() {
            titleTextView.setText("选择车型");
            button.setText("取消");
            listView.setAdapter(carTypeAdapetr);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    carType = carTypes.get(arg2);
                    carTypeTextView.setText(carType.getName());
                    dismiss();
                }
            });
        }
    }

    class ProvinceAdapetr extends BaseAdapter {

        @Override
        public int getCount() {
            return provinces.size();
        }

        @Override
        public Object getItem(int position) {
            return provinces.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_class_main,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_main_name);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                holder.textView.setGravity(Gravity.CENTER_VERTICAL);
                holder.textView.setPadding(ApplicationTool.dip2px(24), 0,
                        ApplicationTool.dip2px(24), 0);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ApplicationTool.dip2px(48));
                holder.textView.setLayoutParams(layoutParams);
                convertView
                        .setBackgroundResource(R.drawable.selector_trans_divider);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(provinces.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    private void getProvince() {
        provinces.clear();
        provinceAdapetr.notifyDataSetChanged();
        post(API.API_BIANMIN_TRAFFIC_PROVINCE, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    provinces.add(new ModelProvince(array.optJSONObject(i)));
                                }
                                if (provinces.size() > 0) {
                                    provinceAdapetr.notifyDataSetChanged();
                                    new ProvinceDialog().show(getFragmentManager(),
                                            null);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getCities() {
        cities.clear();
        cityAdapetr.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("provId", province.getId()));
        post(API.API_BIANMIN_TRAFFIC_CITY, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    cities.add(new ModelCity(array.optJSONObject(i)));
                                }
                                if (cities.size() > 0) {
                                    cityAdapetr.notifyDataSetChanged();
                                    new CityDialog().show(getFragmentManager(),
                                            null);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class CityAdapetr extends BaseAdapter {

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
                convertView = layoutInflater.inflate(R.layout.list_class_main,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_main_name);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                holder.textView.setGravity(Gravity.CENTER_VERTICAL);
                holder.textView.setPadding(ApplicationTool.dip2px(24), 0,
                        ApplicationTool.dip2px(24), 0);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ApplicationTool.dip2px(48));
                holder.textView.setLayoutParams(layoutParams);
                convertView
                        .setBackgroundResource(R.drawable.selector_trans_divider);
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

    private void getCarType() {
        carTypes.clear();
        carTypeAdapetr.notifyDataSetChanged();
        post(API.API_BIANMIN_TRAFFIC_CARTYPE, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    carTypes.add(new ModelCarType(array
                                            .optJSONObject(i)));
                                }
                                if (carTypes.size() > 0) {
                                    carTypeAdapetr.notifyDataSetChanged();
                                    new CarTypeDialog().show(getFragmentManager(),
                                            null);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class CarTypeAdapetr extends BaseAdapter {

        @Override
        public int getCount() {
            return carTypes.size();
        }

        @Override
        public Object getItem(int position) {
            return carTypes.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_class_main,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.class_main_name);
                holder.textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
                holder.textView.setGravity(Gravity.CENTER_VERTICAL);
                holder.textView.setPadding(ApplicationTool.dip2px(24), 0,
                        ApplicationTool.dip2px(24), 0);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        ApplicationTool.dip2px(48));
                holder.textView.setLayoutParams(layoutParams);
                convertView
                        .setBackgroundResource(R.drawable.selector_trans_divider);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(carTypes.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }
}
