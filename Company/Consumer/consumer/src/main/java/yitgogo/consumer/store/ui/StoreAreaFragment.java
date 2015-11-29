package yitgogo.consumer.store.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.store.model.ModelArea;
import yitgogo.consumer.store.model.ModelStoreSelected;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.ui.UserAddressEditFragment;
import yitgogo.consumer.view.InnerGridView;

public class StoreAreaFragment extends BaseNetworkFragment {

    ListView listView;
    InnerGridView gridView;
    List<ModelArea> listAreas, gridAreas;
    List<ModelStoreSelected> stores;
    AreaListAdapter areaListAdapter;
    AreaGridAdapter areaGridAdapter;
    StoreAdapter storeAdapter;

    boolean getArea = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_store_select_area);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(StoreAreaFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(StoreAreaFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArea(null);
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("getArea")) {
                getArea = bundle.getBoolean("getArea");
            }
        }
        listAreas = new ArrayList<>();
        gridAreas = new ArrayList<>();
        stores = new ArrayList<>();
        areaGridAdapter = new AreaGridAdapter();
        areaListAdapter = new AreaListAdapter();
        storeAdapter = new StoreAdapter();
    }

    @Override
    protected void findViews() {
        listView = (ListView) contentView
                .findViewById(R.id.store_select_selected);
        gridView = (InnerGridView) contentView
                .findViewById(R.id.store_select_selection);
        initViews();
    }

    @Override
    protected void initViews() {
        listView.setAdapter(areaListAdapter);
        gridView.setAdapter(areaGridAdapter);
    }

    @Override
    protected void registerViews() {

    }

    private void getLocalBusinessState() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("organizationId", Store.getStore().getStoreId()));
        post(API.API_LOCAL_BUSINESS_STATE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                boolean showLocalBusiness = false;
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
                            if (dataMap != null) {
                                showLocalBusiness = dataMap.optInt("returnNum") != 0;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("showLocalBusiness", showLocalBusiness);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    class AreaListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listAreas.size();
        }

        @Override
        public Object getItem(int position) {
            return listAreas.get(position);
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
            holder.textView.setText(listAreas.get(position).getValuename());
            holder.textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getArea(listAreas.get(index).getId());
                    listAreas = listAreas.subList(0, index + 1);
                    areaListAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    class AreaGridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return gridAreas.size();
        }

        @Override
        public Object getItem(int position) {
            return gridAreas.get(position);
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
                        R.layout.list_area_selection, null);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView
                        .findViewById(R.id.list_area_selection);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ModelArea modelArea = gridAreas.get(position);
            holder.textView.setText(modelArea.getValuename());
            holder.textView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    listAreas.add(modelArea);
                    areaListAdapter.notifyDataSetChanged();
                    getArea(modelArea.getId());
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    class StoreAdapter extends BaseAdapter {

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
                holder.nameTextView = (TextView) convertView
                        .findViewById(R.id.list_store_name);
                holder.addressTextView = (TextView) convertView
                        .findViewById(R.id.list_store_address);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ModelStoreSelected storeSelected = stores.get(position);
            holder.nameTextView.setText(storeSelected.getServicename());
            holder.addressTextView.setText(storeSelected.getServiceaddress());
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Content.saveIntContent(Parameters.CACHE_KEY_STORE_TYPE,
                            Parameters.CACHE_VALUE_STORE_TYPE_SELECTED);
                    Content.saveStringContent(
                            Parameters.CACHE_KEY_STORE_JSONSTRING,
                            storeSelected.getJsonObject().toString());
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

    private void getArea(String aid) {
        stores.clear();
        storeAdapter.notifyDataSetChanged();
        gridAreas.clear();
        areaGridAdapter.notifyDataSetChanged();
        gridView.setNumColumns(3);
        gridView.setAdapter(areaGridAdapter);
        List<RequestParam> requestParams = new ArrayList<>();
        if (!TextUtils.isEmpty(aid)) {
            requestParams.add(new RequestParam("aid", aid));
        }
        post(API.API_STORE_AREA, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    gridAreas.add(new ModelArea(array
                                            .getJSONObject(i)));
                                }
                                if (gridAreas.size() > 0) {
                                    areaGridAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                            String areaId = listAreas.get(listAreas.size() - 1)
                                    .getId();
                            if (getArea) {
                                String areaName = "";
                                for (int i = 0; i < listAreas.size(); i++) {
                                    if (i > 0) {
                                        areaName += ">";
                                    }
                                    areaName += listAreas.get(i).getValuename();
                                }
                                UserAddressEditFragment.areaId = areaId;
                                UserAddressEditFragment.areaName = areaName;
                                getActivity().finish();
                                return;
                            }
                            getStore(areaId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getStore(String areaId) {
        stores.clear();
        storeAdapter.notifyDataSetChanged();
        gridAreas.clear();
        areaGridAdapter.notifyDataSetChanged();
        gridView.setNumColumns(1);
        gridView.setAdapter(storeAdapter);
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("areaId", areaId));
        post(API.API_STORE_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    stores.add(new ModelStoreSelected(array
                                            .getJSONObject(i)));
                                }
                                if (stores.size() > 0) {
                                    storeAdapter.notifyDataSetChanged();
                                } else {
                                    missionNodata();
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

}
