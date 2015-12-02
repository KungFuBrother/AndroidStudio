package yitgogo.consumer.store.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
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
import yitgogo.consumer.store.model.ModelArea;
import yitgogo.consumer.store.model.ModelStoreSelected;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.user.ui.UserAddressEditFragment;
import yitgogo.consumer.view.InnerGridView;

public class SelectAreaFragment extends BaseNotifyFragment {

    ListView listView;
    InnerGridView gridView;
    List<ModelArea> listAreas, gridAreas;
    List<ModelStoreSelected> stores;
    AreaListAdapter areaListAdapter;
    AreaGridAdapter areaGridAdapter;

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
        new GetArea().execute();
    }

    private void init() {
        measureScreen();
        listAreas = new ArrayList<>();
        gridAreas = new ArrayList<>();
        stores = new ArrayList<>();
        areaGridAdapter = new AreaGridAdapter();
        areaListAdapter = new AreaListAdapter();
    }

    @Override
    protected void findViews() {
        listView = (ListView) contentView.findViewById(R.id.store_select_selected);
        gridView = (InnerGridView) contentView.findViewById(R.id.store_select_selection);
        initViews();
    }

    @Override
    protected void initViews() {
        listView.setAdapter(areaListAdapter);
        gridView.setAdapter(areaGridAdapter);
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
            holder.textView.setOnClickListener(new View.OnClickListener() {

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
            holder.textView.setOnClickListener(new View.OnClickListener() {

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

    class GetArea extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            stores.clear();
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
                        String areaId = listAreas.get(listAreas.size() - 1).getId();
                        String areaName = "";
                        for (int i = 0; i < listAreas.size(); i++) {
                            if (i > 0) {
                                areaName += ">";
                            }
                            areaName += listAreas.get(i).getValuename();
                        }
                        Content.saveStringContent("product_detail_area_name", areaName);
                        Content.saveStringContent("product_detail_area_id", areaId);
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
