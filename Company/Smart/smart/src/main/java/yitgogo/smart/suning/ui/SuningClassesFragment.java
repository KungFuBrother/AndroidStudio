package yitgogo.smart.suning.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.suning.model.GetNewSignature;
import yitgogo.smart.suning.model.ModelProductClass;
import yitgogo.smart.suning.model.SuningManager;
import yitgogo.smart.task.SuningTask;
import yitgogo.smart.tools.MissionMessage;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.view.InnerListView;
import yitgogo.smart.view.Notify;

public abstract class SuningClassesFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerListView listView;
    List<ModelProductClass> productClasses;
    ProductClassAdapter productClassAdapter;
    ModelProductClass selectedProductClass = new ModelProductClass();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suning_product_class);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SuningClassesFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SuningClassesFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProductClass();
    }

    private void init() {
        productClasses = new ArrayList<>();
        productClassAdapter = new ProductClassAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView.findViewById(R.id.suning_product_class_refresh);
        listView = (InnerListView) contentView.findViewById(R.id.suning_product_class_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        listView.setAdapter(productClassAdapter);
    }

    @Override
    protected void registerViews() {
        refreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        refreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getProductClass();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                select(productClasses.get(i));
            }
        });
    }

    private void select(ModelProductClass productClass) {
        selectedProductClass = productClass;
        productClassAdapter.notifyDataSetChanged();
        onClassSelected(selectedProductClass);
    }

    public abstract void onClassSelected(ModelProductClass selectedProductClass);

    private void getProductClass() {
        productClasses.clear();
        productClassAdapter.notifyDataSetChanged();
        SuningTask.getProductClass(getActivity(), new OnNetworkListener() {

            @Override
            public void onStart() {
                super.onStart();
                showLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                refreshScrollView.onRefreshComplete();
                hideLoading();
            }

            @Override
            protected void onFail(MissionMessage missionMessage) {
                super.onFail(missionMessage);
                loadingEmpty();
            }

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                hideLoading();
                if (SuningManager.isSignatureOutOfDate(message.getResult())) {
                    GetNewSignature getNewSignature = new GetNewSignature() {
                        @Override
                        protected void onPreExecute() {
                            showLoading();
                        }

                        @Override
                        protected void onPostExecute(Boolean isSuccess) {
                            hideLoading();
                            if (isSuccess) {
                                getProductClass();
                            }
                        }
                    };
                    getNewSignature.execute();
                    return;
                }
                if (!TextUtils.isEmpty(message.getResult())) {
                    try {
                        JSONObject object = new JSONObject(message.getResult());
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
                        Notify.show(object.optString("returnMsg"));
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
