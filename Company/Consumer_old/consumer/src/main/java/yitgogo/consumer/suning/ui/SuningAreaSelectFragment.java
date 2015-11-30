package yitgogo.consumer.suning.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.suning.model.GetNewSignature;
import yitgogo.consumer.suning.model.ModelSuningArea;
import yitgogo.consumer.suning.model.ModelSuningAreas;
import yitgogo.consumer.suning.model.SuningManager;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.view.Notify;


public class SuningAreaSelectFragment extends BaseNotifyFragment {

    TextView provinceTextView, cityTextView, districtTextView, townTextView;

    GridView areaGridView;

    HashMap<String, List<ModelSuningArea>> suningAreaHashMap = new HashMap<>();

    ModelSuningArea province = new ModelSuningArea();
    ModelSuningArea city = new ModelSuningArea();
    ModelSuningArea district = new ModelSuningArea();
    ModelSuningArea town = new ModelSuningArea();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suning_area);
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SuningAreaSelectFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SuningAreaSelectFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectProvince();
    }

    protected void findViews() {

        provinceTextView = (TextView) contentView
                .findViewById(R.id.suning_area_province);
        cityTextView = (TextView) contentView
                .findViewById(R.id.suning_area_city);
        districtTextView = (TextView) contentView
                .findViewById(R.id.suning_area_district);
        townTextView = (TextView) contentView
                .findViewById(R.id.suning_area_town);

        areaGridView = (GridView) contentView
                .findViewById(R.id.suning_area_selection);

        initViews();
    }

    private void selectProvince() {
        provinceTextView.setBackgroundResource(R.drawable.back_white_rec_border_orange);
        cityTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        districtTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        townTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        areaGridView.setAdapter(new AreaAdapter(new ArrayList<ModelSuningArea>(), 0));
        if (suningAreaHashMap.containsKey("province")) {
            areaGridView.setAdapter(new AreaAdapter(suningAreaHashMap.get("province"), 0));
        } else {
            new GetSuningProvince().execute();
        }
    }

    private void selectCity() {
        provinceTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        cityTextView.setBackgroundResource(R.drawable.back_white_rec_border_orange);
        districtTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        townTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        areaGridView.setAdapter(new AreaAdapter(new ArrayList<ModelSuningArea>(), 0));
        if (suningAreaHashMap.containsKey(province.getCode())) {
            areaGridView.setAdapter(new AreaAdapter(suningAreaHashMap.get(province.getCode()), 1));
        } else {
            new GetSuningCity().execute();
        }
    }

    private void selectDistrict() {
        provinceTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        cityTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        districtTextView.setBackgroundResource(R.drawable.back_white_rec_border_orange);
        townTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        areaGridView.setAdapter(new AreaAdapter(new ArrayList<ModelSuningArea>(), 0));
        if (suningAreaHashMap.containsKey(city.getCode())) {
            areaGridView.setAdapter(new AreaAdapter(suningAreaHashMap.get(city.getCode()), 2));
        } else {
            new GetSuningDistrict().execute();
        }
    }

    private void selectTown() {
        provinceTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        cityTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        districtTextView.setBackgroundResource(R.drawable.back_white_rec_border);
        townTextView.setBackgroundResource(R.drawable.back_white_rec_border_orange);
        areaGridView.setAdapter(new AreaAdapter(new ArrayList<ModelSuningArea>(), 0));
        if (suningAreaHashMap.containsKey(district.getCode())) {
            areaGridView.setAdapter(new AreaAdapter(suningAreaHashMap.get(district.getCode()), 3));
        } else {
            new GetSuningTown().execute();
        }
    }

    protected void initViews() {
        addTextButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(province.getCode())) {
                    Notify.show("请选择所在省");
                } else if (TextUtils.isEmpty(city.getCode())) {
                    Notify.show("请选择所在市");
                } else if (TextUtils.isEmpty(district.getCode())) {
                    Notify.show("请选择所在区县");
                } else if (TextUtils.isEmpty(town.getCode())) {
                    Notify.show("请选择所在乡镇或街道");
                } else {
                    ModelSuningAreas suningAreas = SuningManager.getSuningAreas();
                    suningAreas.setProvince(province);
                    suningAreas.setCity(city);
                    suningAreas.setDistrict(district);
                    suningAreas.setTown(town);
                    suningAreas.save();
                    getActivity().finish();
                }
            }
        });
        provinceTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectProvince();
            }
        });
        cityTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(province.getCode())) {
                    Notify.show("请选择所在省");
                    return;
                }
                selectCity();
            }
        });
        districtTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(city.getCode())) {
                    Notify.show("请选择所在市");
                    return;
                }
                selectDistrict();
            }
        });
        townTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(district.getCode())) {
                    Notify.show("请选择所在区县");
                    return;
                }
                selectTown();
            }
        });
    }

    class GetSuningProvince extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            JSONObject data = new JSONObject();
            try {
                data.put("accessToken", SuningManager.getSignature().getToken());
                data.put("appKey", SuningManager.appKey);
                data.put("v", SuningManager.version);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
            return netUtil.postWithoutCookie(API.API_SUNING_AREA_PROVINCE, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String s) {
            hideLoading();
            if (SuningManager.isSignatureOutOfDate(s)) {
                GetNewSignature getNewSignature = new GetNewSignature() {
                    @Override
                    protected void onPreExecute() {
                        showLoading();
                    }

                    @Override
                    protected void onPostExecute(Boolean isSuccess) {
                        hideLoading();
                        if (isSuccess) {
                            new GetSuningProvince().execute();
                        }
                    }
                };
                getNewSignature.execute();
                return;
            }
            if (!TextUtils.isEmpty(s)) {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optBoolean("isSuccess")) {
                        JSONArray array = object.optJSONArray("province");
                        if (array != null) {
                            List<ModelSuningArea> provinceAreas = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                provinceAreas.add(new ModelSuningArea(array.optJSONObject(i)));
                            }
                            suningAreaHashMap.put("province", provinceAreas);
                            areaGridView.setAdapter(new AreaAdapter(provinceAreas, 0));
                        }
                        return;
                    }
                    Notify.show(object.optString("returnMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class GetSuningCity extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            showSuningAreas();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            JSONObject data = new JSONObject();
            try {
                data.put("accessToken", SuningManager.getSignature().getToken());
                data.put("appKey", SuningManager.appKey);
                data.put("v", SuningManager.version);
                data.put("provinceId", province.getCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
            return netUtil.postWithoutCookie(API.API_SUNING_AREA_CITY, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String s) {
            hideLoading();
            if (SuningManager.isSignatureOutOfDate(s)) {
                GetNewSignature getNewSignature = new GetNewSignature() {
                    @Override
                    protected void onPreExecute() {
                        showLoading();
                    }

                    @Override
                    protected void onPostExecute(Boolean isSuccess) {
                        hideLoading();
                        if (isSuccess) {
                            new GetSuningCity().execute();
                        }
                    }
                };
                getNewSignature.execute();
                return;
            }
            if (!TextUtils.isEmpty(s)) {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optBoolean("isSuccess")) {
                        JSONArray array = object.optJSONArray("city");
                        if (array != null) {
                            List<ModelSuningArea> cityAreas = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                cityAreas.add(new ModelSuningArea(array.optJSONObject(i)));
                            }
                            suningAreaHashMap.put(province.getCode(), cityAreas);
                            areaGridView.setAdapter(new AreaAdapter(cityAreas, 1));
                            return;
                        }
                        Notify.show(object.optString("returnMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    class GetSuningDistrict extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            showSuningAreas();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            JSONObject data = new JSONObject();
            try {
                data.put("accessToken", SuningManager.getSignature().getToken());
                data.put("appKey", SuningManager.appKey);
                data.put("v", SuningManager.version);
                data.put("cityId", city.getCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
            return netUtil.postWithoutCookie(API.API_SUNING_AREA_DISTRICT, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String s) {
            hideLoading();
            if (SuningManager.isSignatureOutOfDate(s)) {
                GetNewSignature getNewSignature = new GetNewSignature() {
                    @Override
                    protected void onPreExecute() {
                        showLoading();
                    }

                    @Override
                    protected void onPostExecute(Boolean isSuccess) {
                        hideLoading();
                        if (isSuccess) {
                            new GetSuningDistrict().execute();
                        }
                    }
                };
                getNewSignature.execute();
                return;
            }
            if (!TextUtils.isEmpty(s)) {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optBoolean("isSuccess")) {
                        JSONArray array = object.optJSONArray("district");
                        if (array != null) {
                            List<ModelSuningArea> districtAreas = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                districtAreas.add(new ModelSuningArea(array.optJSONObject(i)));
                            }
                            suningAreaHashMap.put(city.getCode(), districtAreas);
                            areaGridView.setAdapter(new AreaAdapter(districtAreas, 2));
                            return;
                        }
                        Notify.show(object.optString("returnMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    class GetSuningTown extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            showSuningAreas();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            JSONObject data = new JSONObject();
            try {
                data.put("accessToken", SuningManager.getSignature().getToken());
                data.put("appKey", SuningManager.appKey);
                data.put("v", SuningManager.version);
                data.put("cityId", city.getCode());
                data.put("countyId", district.getCode());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
            return netUtil.postWithoutCookie(API.API_SUNING_AREA_TOWN, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String s) {
            hideLoading();
            if (SuningManager.isSignatureOutOfDate(s)) {
                GetNewSignature getNewSignature = new GetNewSignature() {
                    @Override
                    protected void onPreExecute() {
                        showLoading();
                    }

                    @Override
                    protected void onPostExecute(Boolean isSuccess) {
                        hideLoading();
                        if (isSuccess) {
                            new GetSuningTown().execute();
                        }
                    }
                };
                getNewSignature.execute();
                return;
            }
            if (!TextUtils.isEmpty(s)) {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optBoolean("isSuccess")) {
                        JSONArray array = object.optJSONArray("town");
                        if (array != null) {
                            List<ModelSuningArea> townAreas = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                townAreas.add(new ModelSuningArea(array.optJSONObject(i)));
                            }
                            suningAreaHashMap.put(district.getCode(), townAreas);
                            areaGridView.setAdapter(new AreaAdapter(townAreas, 3));
                            return;
                        }
                        Notify.show(object.optString("returnMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showSuningAreas() {
        if (!TextUtils.isEmpty(province.getName())) {
            provinceTextView.setText(province.getName());
            if (!TextUtils.isEmpty(city.getName())) {
                cityTextView.setText(city.getName());
                if (!TextUtils.isEmpty(district.getName())) {
                    districtTextView.setText(district.getName());
                    if (!TextUtils.isEmpty(town.getName())) {
                        townTextView.setText(town.getName());
                    }
                }
            }
        }
    }

    private class AreaAdapter extends BaseAdapter {

        List<ModelSuningArea> areas = new ArrayList<>();
        int level = 0;

        public AreaAdapter(List<ModelSuningArea> areas, int level) {
            this.areas = areas;
            this.level = level;
        }

        @Override
        public int getCount() {
            return areas.size();
        }

        @Override
        public Object getItem(int i) {
            return areas.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            final int position = i;
            ViewHolder viewHolder;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.list_suning_area, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) view.findViewById(R.id.suning_area);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            viewHolder.textView.setText(areas.get(i).getName());
            viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (level) {
                        case 0:
                            province = areas.get(position);
                            city = new ModelSuningArea();
                            district = new ModelSuningArea();
                            town = new ModelSuningArea();
                            provinceTextView.setText(province.getName());
                            cityTextView.setText("");
                            districtTextView.setText("");
                            townTextView.setText("");
                            selectCity();
                            break;

                        case 1:
                            city = areas.get(position);
                            district = new ModelSuningArea();
                            town = new ModelSuningArea();
                            cityTextView.setText(city.getName());
                            districtTextView.setText("");
                            townTextView.setText("");
                            selectDistrict();
                            break;

                        case 2:
                            district = areas.get(position);
                            town = new ModelSuningArea();
                            districtTextView.setText(district.getName());
                            townTextView.setText("");
                            selectTown();
                            break;

                        case 3:
                            town = areas.get(position);
                            townTextView.setText(town.getName());
                            break;

                        default:
                            break;
                    }
                }
            });
            return view;
        }

    }

    private class ViewHolder {
        TextView textView;
    }

}