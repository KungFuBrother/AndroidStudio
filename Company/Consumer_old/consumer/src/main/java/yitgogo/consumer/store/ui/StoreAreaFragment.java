package yitgogo.consumer.store.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.GetLocalBusinessState;
import yitgogo.consumer.main.ui.MainActivity;
import yitgogo.consumer.store.model.ModelArea;
import yitgogo.consumer.store.model.ModelStoreSelected;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.ui.UserAddressEditFragment;
import yitgogo.consumer.view.InnerGridView;

public class StoreAreaFragment extends BaseNotifyFragment {

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
        new GetArea().execute();
    }

    private void init() {
        measureScreen();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("getArea")) {
                getArea = bundle.getBoolean("getArea");
            }
        }
        listAreas = new ArrayList<ModelArea>();
        gridAreas = new ArrayList<ModelArea>();
        stores = new ArrayList<ModelStoreSelected>();
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
        registerViews();
    }

    @Override
    protected void initViews() {
        listView.setAdapter(areaListAdapter);
        gridView.setAdapter(areaGridAdapter);
    }

    @Override
    protected void registerViews() {
        onBackButtonClick(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getArea) {
                    if (Store.getStore() == null) {
                        getActivity().finish();
                    } else {
                        getLocalBusinessState();
                    }
                }
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
                    new GetArea().execute(listAreas.get(index).getId());
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
                    new GetArea().execute(modelArea.getId());
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

    class GetArea extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            stores.clear();
            storeAdapter.notifyDataSetChanged();
            gridAreas.clear();
            areaGridAdapter.notifyDataSetChanged();
            gridView.setNumColumns(3);
            gridView.setAdapter(areaGridAdapter);
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (params.length > 0) {
                nameValuePairs.add(new BasicNameValuePair("aid", params[0]));
            }
            return netUtil.postWithoutCookie(API.API_STORE_AREA,
                    nameValuePairs, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            // {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{"id":3253,"valuename":"中国","valuetype":{"id":1,"typename":"国"},"onid":0,"onname":null,"brevitycode":null}],"totalCount":1,"dataMap":{},"object":null}
            hideLoading();
            if (result.length() > 0) {
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
                        new GetStore().execute(areaId);
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
            stores.clear();
            storeAdapter.notifyDataSetChanged();
            gridAreas.clear();
            areaGridAdapter.notifyDataSetChanged();
            gridView.setNumColumns(1);
            gridView.setAdapter(storeAdapter);
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("areaId", params[0]));
            return netUtil.postWithoutCookie(API.API_STORE_LIST,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            /**
             * {"message":"ok","state":"SUCCESS"
             * ,"cacheKey":null,"dataList":[{"id"
             * :848,"no":"YT371674317287","brevitycode"
             * :null,"servicename":"默认加盟商"
             * ,"businessno":"VB11122220000","contacts"
             * :"易田","cardnumber":null,"serviceaddress"
             * :"四川省成都市金牛区","contactphone"
             * :null,"contacttelephone":null,"email":null
             * ,"reva":null,"contractno"
             * :null,"contractannex":null,"onservice":null
             * ,"state":null,"addtime"
             * :null,"starttime":null,"sptype":null,"endtime"
             * :null,"supply":false
             * ,"imghead":"","longitude":"104.06137695451","latitude"
             * :"30.735622100763"}],"totalCount":1,"dataMap":{},"object":null}
             */
            hideLoading();
            if (result.length() > 0) {
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

}
