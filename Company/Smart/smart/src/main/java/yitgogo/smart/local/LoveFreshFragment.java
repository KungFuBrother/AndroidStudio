package yitgogo.smart.local;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.model.ModelLocalService;
import yitgogo.smart.task.HomeTask;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;

public class LoveFreshFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerGridView productGridView;

    List<ModelLocalService> localServices;
    ServiceAdapter serviceAdapter;

    int pageSize = 12, pageNo = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_products);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LoveFreshFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LoveFreshFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
    }

    private void init() {
        localServices = new ArrayList<>();
        serviceAdapter = new ServiceAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.product_search_scroll);
        productGridView = (InnerGridView) contentView
                .findViewById(R.id.product_search_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        productGridView.setAdapter(serviceAdapter);
        refreshScrollView.setMode(Mode.BOTH);
    }

    @Override
    protected void registerViews() {
        refreshScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        refresh();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        getService();
                    }
                });
        productGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", localServices.get(arg2).getId());
                openWindow(LocalServiceDetailFragment.class.getName(),
                        localServices.get(arg2).getProductName(), bundle);
            }
        });
    }

    private void refresh() {
        refreshScrollView.setMode(Mode.BOTH);
        pageNo = 0;
        localServices.clear();
        serviceAdapter.notifyDataSetChanged();
        getService();
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

    private void getService() {
        pageNo++;
        HomeTask.getLoveFresh(getActivity(), pageNo, pageSize,
                new OnNetworkListener() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideLoading();
                        refreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        if (!TextUtils.isEmpty(message.getResult())) {
                            JSONObject object;
                            try {
                                object = new JSONObject(message.getResult());
                                if (object.optString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
                                    JSONArray array = object
                                            .optJSONArray("dataList");
                                    if (array != null) {
                                        if (array.length() > 0) {
                                            if (array.length() < pageSize) {
                                                refreshScrollView
                                                        .setMode(Mode.PULL_FROM_START);
                                            }
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject jsonObject = array
                                                        .optJSONObject(i);
                                                if (jsonObject != null) {
                                                    localServices
                                                            .add(new ModelLocalService(
                                                                    jsonObject));
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
                        refreshScrollView.setMode(Mode.PULL_FROM_START);
                        if (localServices.size() == 0) {
                            loadingEmpty();
                        }
                    }
                });
    }
}
