package yitgogo.smart.suning.ui;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.smart.suning.model.API_SUNING;
import yitgogo.smart.suning.model.GetNewSignature;
import yitgogo.smart.suning.model.ModelSuningArea;
import yitgogo.smart.suning.model.ModelSuningAreas;
import yitgogo.smart.suning.model.SuningManager;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.view.Notify;

public class SuningAreaDialog extends DialogFragment {

    View dialogView;

    ImageView closeButton;

    TextView provinceTextView, cityTextView, districtTextView, townTextView, okButton;

    GridView areaGridView;

    LinearLayout loadingLayout;

    LayoutInflater layoutInflater;

    HashMap<String, List<ModelSuningArea>> suningAreaHashMap = new HashMap<>();

    ModelSuningArea province = new ModelSuningArea();
    ModelSuningArea city = new ModelSuningArea();
    ModelSuningArea district = new ModelSuningArea();
    ModelSuningArea town = new ModelSuningArea();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        layoutInflater = LayoutInflater.from(getActivity());
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SuningAreaDialog.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SuningAreaDialog.class.getName());
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView, new ViewGroup.LayoutParams(720, 960));
        selectProvince();
        return dialog;
    }

    private void findViews() {
        dialogView = layoutInflater.inflate(R.layout.dialog_suning_area,
                null);
        closeButton = (ImageView) dialogView
                .findViewById(R.id.dialog_close);

        provinceTextView = (TextView) dialogView
                .findViewById(R.id.suning_area_province);
        cityTextView = (TextView) dialogView
                .findViewById(R.id.suning_area_city);
        districtTextView = (TextView) dialogView
                .findViewById(R.id.suning_area_district);
        townTextView = (TextView) dialogView
                .findViewById(R.id.suning_area_town);
        okButton = (TextView) dialogView
                .findViewById(R.id.suning_area_ok);
        areaGridView = (GridView) dialogView
                .findViewById(R.id.suning_area_selections);

        loadingLayout = (LinearLayout) dialogView
                .findViewById(R.id.suning_area_loading);

        initViews();
    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingLayout.setVisibility(View.GONE);
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

    private void initViews() {
        closeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                    dismiss();
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
            return MissionController.post(API_SUNING.API_AREA_PROVINCE, nameValuePairs);
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
                    }
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
            return MissionController.post(API_SUNING.API_AREA_CITY, nameValuePairs);
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
                        }
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
            return MissionController.post(API_SUNING.API_AREA_DISTRICT, nameValuePairs);
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
                        }
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
            return MissionController.post(API_SUNING.API_AREA_TOWN, nameValuePairs);
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
                        }
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