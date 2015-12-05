package yitgogo.consumer.store;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

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
import yitgogo.consumer.store.model.ModelArea;
import yitgogo.consumer.store.model.ModelStoreArea;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ScreenUtil;

public class SelectAreaFragment extends BaseNotifyFragment {

    HorizontalScrollView areasScrollView;
    LinearLayout selectedAreaLayout;
    ListView listView;
    /**
     * 定位区域
     */
    HashMap<Integer, ModelStoreArea> locatedAreaHashMap = new HashMap<>();
    /**
     * 各级区域
     */
    HashMap<Integer, ModelStoreArea> selectedAreaHashMap = new HashMap<>();
    /**
     * 所有区域
     */
    HashMap<String, List<ModelStoreArea>> allAreaHashMap = new HashMap<>();
    /**
     * 当前选择的区域
     */
    ModelStoreArea currentArea = new ModelStoreArea();
    AreaListAdapter areaListAdapter;

    public final static int TYPE_STORE_LOCATE = 1;
    public final static int TYPE_GET_AREA = 2;

    int type = TYPE_GET_AREA;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_select_area);
        init();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ModelStoreArea area = null;
        if (type == TYPE_STORE_LOCATE) {
            if (locatedAreaHashMap.containsKey(4)) {
                area = locatedAreaHashMap.get(4);
            } else if (locatedAreaHashMap.containsKey(3)) {
                area = locatedAreaHashMap.get(3);
            } else if (locatedAreaHashMap.containsKey(2)) {
                area = locatedAreaHashMap.get(2);
            } else if (locatedAreaHashMap.containsKey(1)) {
                area = locatedAreaHashMap.get(1);
            }
        }
        selectArea(area);
    }

    private void init() {
        measureScreen();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("type")) {
                type = bundle.getInt("type");
                if (type == TYPE_STORE_LOCATE) {
                    try {
                        JSONArray array = new JSONArray(bundle.getString("area"));
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                ModelStoreArea storeArea = new ModelStoreArea(array.optJSONObject(i));
                                locatedAreaHashMap.put(storeArea.getType(), storeArea);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void findViews() {
        areasScrollView = (HorizontalScrollView) contentView.findViewById(R.id.select_area_scroll);
        selectedAreaLayout = (LinearLayout) contentView.findViewById(R.id.select_area_selected_areas);
        listView = (ListView) contentView.findViewById(R.id.select_area_areas);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        listView.setAdapter(areaListAdapter);
    }

    @Override
    protected void registerViews() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ModelStoreArea area = allAreaHashMap.get(currentArea.getId()).get(i);
                selectArea(area);
            }
        });
    }

    private void selectArea(ModelStoreArea area) {
        if (area != null) {
            currentArea = area;
            switch (type) {
                case TYPE_STORE_LOCATE:
                    locatedAreaHashMap.put(currentArea.getType(), currentArea);
                    refreshLocatedArea();
                    break;
                case TYPE_GET_AREA:
                    selectedAreaHashMap.put(currentArea.getType(), currentArea);
                    refreshSelectedArea();
                    break;
            }
        }
        new GetArea().execute();
    }

    private void refreshLocatedArea() {
        selectedAreaLayout.removeAllViews();
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(locatedAreaHashMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, ModelStoreArea>>() {
            @Override
            public int compare(Map.Entry<Integer, ModelStoreArea> area1, Map.Entry<Integer, ModelStoreArea> area2) {
                return area1.getKey().compareTo(area2.getKey());
            }
        });
        for (int i = 0; i < entries.size(); i++) {
            View.OnClickListener onClickListener = null;
            final ModelStoreArea area = entries.get(i).getValue();
            if (area.getType() > 3) {
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeLocatedChildArea(area);
                    }
                };
            }
            selectedAreaLayout.addView(newTextView(entries.get(i).getValue().getName(), R.color.textColorPrimary, onClickListener));
        }
    }

    private void removeLocatedChildArea(ModelStoreArea area) {
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(locatedAreaHashMap.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getValue().getType() > area.getType()) {
                locatedAreaHashMap.remove(entries.get(i).getValue().getType());
            }
        }
        refreshLocatedArea();
        selectArea(area);
    }

    private String getLocatedAreaName() {
        StringBuilder builder = new StringBuilder();
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(locatedAreaHashMap.entrySet());
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

    private void refreshSelectedArea() {
        selectedAreaLayout.removeAllViews();
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(selectedAreaHashMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, ModelStoreArea>>() {
            @Override
            public int compare(Map.Entry<Integer, ModelStoreArea> area1, Map.Entry<Integer, ModelStoreArea> area2) {
                return area1.getKey().compareTo(area2.getKey());
            }
        });
        for (int i = 0; i < entries.size(); i++) {
            final ModelStoreArea area = entries.get(i).getValue();
            selectedAreaLayout.addView(newTextView(entries.get(i).getValue().getName(), R.color.textColorPrimary, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeSelectedChildArea(area);
                }
            }));
        }
    }

    private void removeSelectedChildArea(ModelStoreArea area) {
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(selectedAreaHashMap.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getValue().getType() > area.getType()) {
                selectedAreaHashMap.remove(entries.get(i).getValue().getType());
            }
        }
        refreshLocatedArea();
        selectArea(area);
    }

    private String getSelectedAreaName() {
        StringBuilder builder = new StringBuilder();
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(selectedAreaHashMap.entrySet());
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

    private TextView newTextView(String lable, int textColorResId, View.OnClickListener onClickListener) {
        TextView textView = new TextView(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ScreenUtil.dip2px(36));
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams);
        textView.setBackgroundResource(R.drawable.selector_trans_divider);
        textView.setPadding(ScreenUtil.dip2px(8), 0, ScreenUtil.dip2px(8), 0);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setText(lable + ">");
        textView.setTextColor(getResources().getColor(textColorResId));
        if (onClickListener != null) {
            textView.setOnClickListener(onClickListener);
        }
        return textView;
    }


    class AreaListAdapter extends BaseAdapter {

        List<ModelStoreArea> areas;

        public AreaListAdapter(List<ModelStoreArea> areas) {
            this.areas = areas;
        }

        @Override
        public int getCount() {
            return areas.size();
        }

        @Override
        public Object getItem(int position) {
            return areas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int index = position;
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_area_selected, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView
                        .findViewById(R.id.list_area_selected);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(areas.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    class GetArea extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            if (!TextUtils.isEmpty(currentArea.getId())) {
                nameValuePairs.add(new BasicNameValuePair("aid", currentArea.getId()));
            }
            return netUtil.postWithoutCookie(API.API_STORE_AREA, nameValuePairs, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            // {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{"id":3253,"valuename":"中国","valuetype":{"id":1,"typename":"国"},"onid":0,"onname":null,"brevitycode":null}],"totalCount":1,"dataMap":{},"object":null}
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            List<ModelStoreArea> areas = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                ModelArea area = new ModelArea(array.getJSONObject(i));
                                areas.add(new ModelStoreArea(area.getId(), area.getValuename(), area.getAreaType().getId()));
                            }
                            allAreaHashMap.put(currentArea.getId(), areas);
                            listView.setAdapter(new AreaListAdapter(areas));
                            if (areas.size() == 1) {
                                if (areas.get(0).getId().equals("3253")) {
                                    selectArea(areas.get(0));
                                }
                            }
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra("id", currentArea.getId());
                        switch (type) {
                            case TYPE_STORE_LOCATE:
                                intent.putExtra("name", getLocatedAreaName());
                                break;
                            case TYPE_GET_AREA:
                                intent.putExtra("name", getSelectedAreaName());
                                break;
                        }
                        getActivity().setResult(23, intent);
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
