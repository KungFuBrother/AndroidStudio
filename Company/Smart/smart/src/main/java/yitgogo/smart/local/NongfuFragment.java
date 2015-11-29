package yitgogo.smart.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.model.ModelLocalService;
import yitgogo.smart.local.model.ModelNongfu;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;

public class NongfuFragment extends BaseNotifyFragment {

    int pageNo = 0;
    int pageSize = 20;

    FrameLayout serviceLayout;
    PullToRefreshScrollView serviceScrollView;
    InnerGridView serviceProductGridView;

    List<ModelLocalService> localServices;
    ServiceAdapter serviceAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_local_nongfu);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(NongfuFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(NongfuFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refreshService();
    }

    private void init() {
        localServices = new ArrayList<ModelLocalService>();
        serviceAdapter = new ServiceAdapter();
    }

    protected void findViews() {
        serviceLayout = (FrameLayout) contentView
                .findViewById(R.id.store_service_layout);
        serviceScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.store_service_scroll);
        serviceProductGridView = (InnerGridView) contentView
                .findViewById(R.id.store_service_list);
        initViews();
        registerViews();
    }

    protected void initViews() {
        serviceProductGridView.setAdapter(serviceAdapter);
    }

    private void refreshService() {
        pageNo = 0;
        localServices.clear();
        serviceAdapter.notifyDataSetChanged();
        getNongFu();
    }

    protected void registerViews() {
        serviceProductGridView
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        Bundle bundle = new Bundle();
                        bundle.putString("productId", localServices.get(arg2)
                                .getId());
                        openWindow(LocalServiceDetailFragment.class.getName(),
                                localServices.get(arg2).getProductName(),
                                bundle);
                    }
                });

        serviceScrollView.setMode(Mode.BOTH);
        serviceScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        refreshService();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        getNongFu();
                    }
                });
    }

    class ServiceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localServices.size();
        }

        @Override
        public Object getItem(int position) {
            return localServices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_product_grid, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.list_product_image);
                viewHolder.priceTextView = (TextView) convertView
                        .findViewById(R.id.list_product_price);
                viewHolder.nameTextView = (TextView) convertView
                        .findViewById(R.id.list_product_name);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 256);
                viewHolder.imageView.setLayoutParams(layoutParams);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ModelLocalService localService = localServices.get(position);
            viewHolder.nameTextView.setText(localService.getProductName());
            viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(localService.getProductPrice()));
            imageLoader.displayImage(getSmallImageUrl(localService.getImg()),
                    viewHolder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView priceTextView, nameTextView;
        }
    }

    private void getNongFu() {
        pageNo++;
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_NONGFU);
        networkContent.addParameters("pagenum", pageNo + "");
        networkContent.addParameters("pagesize", pageSize + "");
        networkContent.addParameters("shebei", Device.getDeviceCode());
        MissionController.startNetworkMission(getActivity(), networkContent,
                new OnNetworkListener() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        serviceScrollView.onRefreshComplete();
                        hideLoading();
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        if (!TextUtils.isEmpty(message.getResult())) {
                            try {
                                JSONObject object = new JSONObject(message
                                        .getResult());
                                if (object.optString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
                                    JSONArray array = object
                                            .optJSONArray("dataList");
                                    if (array != null) {
                                        if (array.length() > 0) {
                                            if (array.length() < pageSize) {
                                                serviceScrollView
                                                        .setMode(Mode.PULL_FROM_START);
                                            }
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject jsonObject = array
                                                        .optJSONObject(i);
                                                if (jsonObject != null) {
                                                    ModelNongfu modelNongfu = new ModelNongfu(
                                                            jsonObject);
                                                    localServices.add(modelNongfu
                                                            .getLocalService());
                                                }
                                            }
                                            serviceAdapter
                                                    .notifyDataSetChanged();
                                            return;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        serviceScrollView.setMode(Mode.PULL_FROM_START);
                        if (localServices.isEmpty()) {
                            loadingEmpty();
                        }
                    }

                });
    }

}
