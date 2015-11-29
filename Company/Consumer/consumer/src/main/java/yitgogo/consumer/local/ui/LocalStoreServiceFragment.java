package yitgogo.consumer.local.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseFragment;
import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.local.model.ModelLocalService;
import yitgogo.consumer.local.model.ModelLocalServiceClass;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerGridView;

/**
 * @author Tiger
 * @Description 易商圈-本地服务
 */
public class LocalStoreServiceFragment extends BaseNetworkFragment implements
        OnClickListener {

    TextView selectorClasses, selectorSort;
    PullToRefreshScrollView refreshScrollView;
    InnerGridView serviceList;
    FrameLayout selectorFragmentLayout;
    LinearLayout selectorLayout;

    List<ModelLocalService> localServices;
    ServiceAdapter serviceAdapter;

    PriceSort priceSort;
    PriceSortAdapter priceSortAdapter;

    LocalServiceClass localServiceClass;
    ServiceClassAdapter serviceClassAdapter;

    String classId = "";

    String storeId = "";

    public LocalStoreServiceFragment(String storeId) {
        this.storeId = storeId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_local_business_service);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalStoreServiceFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LocalStoreServiceFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getServiceClasses();
        reload();
    }

    @Override
    protected void init() {
        localServices = new ArrayList<ModelLocalService>();
        serviceAdapter = new ServiceAdapter();

        localServiceClass = new LocalServiceClass();
        serviceClassAdapter = new ServiceClassAdapter();

        priceSort = new PriceSort();
        priceSortAdapter = new PriceSortAdapter();

    }

    @Override
    protected void findViews() {
        selectorClasses = (TextView) contentView
                .findViewById(R.id.local_business_selector_classes);
        selectorSort = (TextView) contentView
                .findViewById(R.id.local_business_selector_sort);
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.local_business_content_refresh);
        serviceList = (InnerGridView) contentView
                .findViewById(R.id.local_business_content_list);
        selectorFragmentLayout = (FrameLayout) contentView
                .findViewById(R.id.local_business_selector_fragment);
        selectorLayout = (LinearLayout) contentView
                .findViewById(R.id.local_business_selector_layout);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        refreshScrollView.setMode(Mode.BOTH);
        serviceList.setAdapter(serviceAdapter);
    }

    @Override
    protected void registerViews() {
        selectorLayout.setOnClickListener(this);
        selectorClasses.setOnClickListener(this);
        selectorSort.setOnClickListener(this);
        refreshScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        reload();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        pagenum++;
                        getService();
                    }
                });
        serviceList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> paramAdapterView,
                                    View paramView, int paramInt, long paramLong) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", localServices.get(paramInt)
                        .getId());
                jump(LocalServiceDetailFragment.class.getName(), localServices
                        .get(paramInt).getProductName(), bundle);

            }
        });
    }

    @Override
    protected void reload() {
        super.reload();
        hideSelector();
        refreshScrollView.setMode(Mode.BOTH);
        pagenum = 0;
        localServices.clear();
        serviceAdapter.notifyDataSetChanged();
        pagenum++;
        getService();
    }

    private void showSelector(Fragment fragment) {
        getFragmentManager().beginTransaction()
                .replace(R.id.local_service_selector_fragment, fragment)
                .commit();
        selectorLayout.setVisibility(View.VISIBLE);
    }

    public void hideSelector() {
        selectorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local_business_selector_layout:
                hideSelector();
                break;

            case R.id.local_business_selector_classes:
                showSelector(new ServiceClassSelector());
                break;

            case R.id.local_business_selector_sort:
                showSelector(new PriceSortSelector());
                break;

            default:
                break;
        }
    }

    class ServiceClassSelector extends BaseFragment {

        ListView listView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void init() {

        }

        @Override
        @Nullable
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(
                    R.layout.selector_local_business_service_class, null);
            findViews(view);
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            if (localServiceClass.getServiceClasses().isEmpty()) {
                getServiceClasses();
            }
        }

        @Override
        protected void findViews(View view) {
            listView = (ListView) view
                    .findViewById(R.id.selector_service_class);
            initViews();
            registerViews();
        }

        @Override
        protected void initViews() {
            listView.setAdapter(serviceClassAdapter);
        }

        @Override
        protected void registerViews() {
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    localServiceClass.setSelection(arg2);
                    classId = localServiceClass.getServiceClasses().get(arg2)
                            .getId();
                    reload();
                }
            });
        }
    }

    class PriceSortSelector extends BaseFragment {

        ListView listView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        protected void init() {

        }

        @Override
        @Nullable
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(
                    R.layout.selector_local_business_service_class, null);
            findViews(view);
            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        @Override
        protected void findViews(View view) {
            listView = (ListView) view
                    .findViewById(R.id.selector_service_class);
            initViews();
            registerViews();
        }

        @Override
        protected void initViews() {
            listView.setAdapter(priceSortAdapter);
        }

        @Override
        protected void registerViews() {
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    priceSort.setSelection(arg2);
                    reload();
                }
            });
        }
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
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.grid_product,
                        null);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.grid_product_image);
                holder.nameTextView = (TextView) convertView
                        .findViewById(R.id.grid_product_name);
                holder.priceTextView = (TextView) convertView
                        .findViewById(R.id.grid_product_price);
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth() / 3 * 2);
                convertView.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ModelLocalService localService = localServices.get(position);
            holder.nameTextView.setText(localService.getProductName());
            holder.priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(localService.getProductPrice()));
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(localService.getImg()), holder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView priceTextView, nameTextView;
        }
    }

    class PriceSortAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return priceSort.getPriceSort().length;
        }

        @Override
        public Object getItem(int position) {
            return priceSort.getPriceSort()[position];
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
                viewHolder.serviceClassName = (TextView) convertView
                        .findViewById(R.id.local_business_class_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.serviceClassName
                    .setText(priceSort.getPriceSort()[position]);
            if (position == priceSort.getSelection()) {
                viewHolder.selector
                        .setBackgroundResource(R.color.textColorCompany);
                viewHolder.serviceClassName.setTextColor(getResources()
                        .getColor(R.color.textColorCompany));
            } else {
                viewHolder.selector.setBackgroundResource(android.R.color.transparent);
                viewHolder.serviceClassName.setTextColor(getResources()
                        .getColor(R.color.textColorSecond));
            }
            return convertView;
        }

        class ViewHolder {
            TextView serviceClassName;
            View selector;
        }
    }

    class ServiceClassAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localServiceClass.getServiceClasses().size();
        }

        @Override
        public Object getItem(int position) {
            return localServiceClass.getServiceClasses().get(position);
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
                viewHolder.serviceClassName = (TextView) convertView
                        .findViewById(R.id.local_business_class_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.serviceClassName.setText(localServiceClass
                    .getServiceClasses().get(position).getClassValueName());
            if (position == localServiceClass.getSelection()) {
                viewHolder.selector
                        .setBackgroundResource(R.color.textColorCompany);
                viewHolder.serviceClassName.setTextColor(getResources()
                        .getColor(R.color.textColorCompany));
            } else {
                viewHolder.selector.setBackgroundResource(android.R.color.transparent);
                viewHolder.serviceClassName.setTextColor(getResources()
                        .getColor(R.color.textColorSecond));
            }
            return convertView;
        }

        class ViewHolder {
            TextView serviceClassName;
            View selector;
        }
    }

    class PriceSort {

        String[] priceSort = {"默认排序", "价格由低到高", "价格由高到低"};
        int selection = 0;

        public String[] getPriceSort() {
            return priceSort;
        }

        public int getSelection() {
            return selection;
        }

        public void setSelection(int selection) {
            this.selection = selection;
        }

    }

    class LocalServiceClass {

        List<ModelLocalServiceClass> serviceClasses = new ArrayList<ModelLocalServiceClass>();
        int selection = 0;

        public LocalServiceClass() {
        }

        public LocalServiceClass(String result) {
            if (result.length() > 0) {
                serviceClasses.add(new ModelLocalServiceClass());
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.optJSONObject(i);
                                if (jsonObject != null) {
                                    serviceClasses
                                            .add(new ModelLocalServiceClass(
                                                    jsonObject));
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        public int getSelection() {
            return selection;
        }

        public void setSelection(int selection) {
            this.selection = selection;
        }

        public List<ModelLocalServiceClass> getServiceClasses() {
            return serviceClasses;
        }

    }

    private void getServiceClasses() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("organizationId", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("providerId", storeId));
        post(API.API_LOCAL_BUSINESS_SERVICE_CLASS, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                localServiceClass = new LocalServiceClass(result);
                serviceClassAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getService() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("pageNo", pagenum + ""));
        requestParams.add(new RequestParam("pageSize", pagesize + ""));
        requestParams.add(new RequestParam("organizationId", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("providerId", storeId));
        if (classId.length() > 0) {
            requestParams.add(new RequestParam("classValueId", classId));
        }
        if (priceSort.getSelection() > 0) {
            requestParams.add(new RequestParam("pricePaixu", (priceSort.getSelection() - 1) + ""));
        }
        post(API.API_LOCAL_BUSINESS_SERVICE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshScrollView.onRefreshComplete();
                if (result.length() > 0) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                if (array.length() > 0) {
                                    if (array.length() < pagesize) {
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
                                    serviceAdapter.notifyDataSetChanged();
                                    return;
                                } else {
                                    refreshScrollView.setMode(Mode.PULL_FROM_START);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (localServices.size() == 0) {
                        missionNodata();
                    }
                } else {
                    missionFailed();
                }
            }
        });
    }

}
