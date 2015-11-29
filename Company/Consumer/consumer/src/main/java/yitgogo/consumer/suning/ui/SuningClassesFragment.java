package yitgogo.consumer.suning.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import yitgogo.consumer.suning.model.ModelProductClass;
import yitgogo.consumer.suning.model.SuningManager;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;

/**
 * Created by Tiger on 2015-10-21.
 */
public abstract class SuningClassesFragment extends BaseNetworkFragment {


    private SwipeRefreshLayout refreshLayout;
    private ListView listView;
    private List<ModelProductClass> productClasses;
    private ProductClassAdapter productClassAdapter;
    private ModelProductClass selectedProductClass = new ModelProductClass();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suning_classes);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SuningClassesFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SuningClassesFragment.class.getName());
        if (productClasses.isEmpty()) {
            if (!TextUtils.isEmpty(SuningManager.getSuningAreas().getTown().getCode())) {
                getClasses();
            } else {
                MainActivity.switchTab(0);
            }
        }
    }

    @Override
    protected void init() {
        productClasses = new ArrayList<>();
        productClassAdapter = new ProductClassAdapter();
    }

    @Override
    protected void findViews() {
        refreshLayout = (SwipeRefreshLayout) contentView.findViewById(R.id.suning_classes_refresh);
        listView = (ListView) contentView.findViewById(R.id.suning_classes);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        listView.setAdapter(productClassAdapter);
    }

    @Override
    protected void registerViews() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                select(productClasses.get(i));
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getClasses();
            }
        });
    }

    private void select(ModelProductClass productClass) {
        selectedProductClass = productClass;
        productClassAdapter.notifyDataSetChanged();
        onClassSelected(selectedProductClass);
    }

    public abstract void onClassSelected(ModelProductClass selectedProductClass);

    private void getClasses() {
        productClasses.clear();
        productClassAdapter.notifyDataSetChanged();
        JSONObject data = new JSONObject();
        try {
            data.put("accessToken", SuningManager.getSignature().getToken());
            data.put("appKey", SuningManager.appKey);
            data.put("v", SuningManager.version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("data", data.toString()));
        post(API.API_SUNING_PRODUCT_CALSSES, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshLayout.setRefreshing(false);
                if (SuningManager.isSignatureOutOfDate(result)) {
                    post(API.API_SUNING_SIGNATURE, new OnNetworkListener() {
                        @Override
                        public void onSuccess(String result) {
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                                        JSONObject dataMap = object.optJSONObject("dataMap");
                                        Content.saveStringContent(Parameters.CACHE_KEY_SUNING_SIGNATURE, dataMap.toString());
                                        getClasses();
                                        return;
                                    }
                                    ApplicationTool.showToast(object.optString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    return;
                }
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optBoolean("isSuccess")) {
                            JSONArray array = object.optJSONArray("result");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    productClasses.add(new ModelProductClass(array.optJSONObject(i)));
                                }
                                if (!productClasses.isEmpty()) {
                                    select(productClasses.get(0));
                                }
                            }
                            return;
                        }
                        ApplicationTool.showToast(object.optString("returnMsg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class ProductClassAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return productClasses.size();
        }

        @Override
        public Object getItem(int position) {
            return productClasses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.list_local_business_class, null);
                viewHolder.selector = convertView
                        .findViewById(R.id.local_business_class_selector);
                viewHolder.className = (TextView) convertView
                        .findViewById(R.id.local_business_class_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.className.setText(productClasses.get(position).getName());
            if (productClasses.get(position).getCategoryId().equals(selectedProductClass.getCategoryId())) {
                viewHolder.selector
                        .setBackgroundResource(R.color.textColorCompany);
                viewHolder.className.setTextColor(getResources()
                        .getColor(R.color.textColorCompany));
            } else {
                viewHolder.selector.setBackgroundResource(android.R.color.transparent);
                viewHolder.className.setTextColor(getResources()
                        .getColor(R.color.textColorSecond));
            }
            return convertView;
        }

        class ViewHolder {
            TextView className;
            View selector;
        }
    }

}
