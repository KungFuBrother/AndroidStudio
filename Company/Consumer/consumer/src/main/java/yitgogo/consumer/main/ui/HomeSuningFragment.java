package yitgogo.consumer.main.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.suning.model.ModelProductClass;
import yitgogo.consumer.suning.model.ModelProductDetail;
import yitgogo.consumer.suning.model.ModelProductPool;
import yitgogo.consumer.suning.model.ModelProductPrice;
import yitgogo.consumer.suning.model.SuningManager;
import yitgogo.consumer.suning.ui.ProductDetailFragment;
import yitgogo.consumer.suning.ui.SuningAreaSelectFragment;
import yitgogo.consumer.suning.ui.SuningClassesFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerGridView;

public class HomeSuningFragment extends BaseNetworkFragment {

    ImageView classButton;
    TextView cityTextView;
    LinearLayout cityButton;
    FrameLayout classLayout;
    DrawerLayout drawerLayout;
    PullToRefreshScrollView refreshScrollView;
    InnerGridView productGridView;

    ModelProductClass productClass = new ModelProductClass();

    List<ModelProductPool> productPools = new ArrayList<>();
    List<ModelProductDetail> productDetails = new ArrayList<>();
    HashMap<String, ModelProductPrice> priceHashMap = new HashMap<>();

    ProductAdapter productAdapter;

    SuningClassesFragment classesFragment = new SuningClassesFragment() {

        @Override
        public void onClassSelected(ModelProductClass selectedProductClass) {
            if (productClass == selectedProductClass) return;
            productClass = selectedProductClass;
            drawerLayout.closeDrawers();
            getSuningProducts();
        }

    };

    int pageSize = 12, pageNo = 0;
    int totalPage = 0;

    public HomeSuningFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_suning);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(HomeSuningFragment.class.getName());
        cityTextView.setText(SuningManager.getSuningAreas().getCity().getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(HomeSuningFragment.class.getName());
    }

    @Override
    protected void init() {
        productAdapter = new ProductAdapter();
    }

    @Override
    protected void findViews() {
        classButton = (ImageView) contentView
                .findViewById(R.id.home_suning_class);
        cityTextView = (TextView) contentView
                .findViewById(R.id.home_suning_city);
        cityButton = (LinearLayout) contentView
                .findViewById(R.id.home_suning_city_select);
        classLayout = (FrameLayout) contentView
                .findViewById(R.id.home_suning_product_class);
        drawerLayout = (DrawerLayout) contentView
                .findViewById(R.id.home_suning_drawer);
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.home_suning_refresh);
        productGridView = (InnerGridView) contentView
                .findViewById(R.id.home_suning_product_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        refreshScrollView.setMode(Mode.BOTH);
        productGridView.setAdapter(productAdapter);
        getFragmentManager().beginTransaction().replace(R.id.home_suning_product_class, classesFragment).commit();
    }

    @Override
    protected void registerViews() {
        refreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getSuningProducts();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (pageNo >= totalPage) {
                    refreshScrollView.setMode(Mode.PULL_FROM_START);
                    refreshScrollView.onRefreshComplete();
                    return;
                }
                new GetSuningProductDetail().execute();
            }
        });
        productGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (priceHashMap.containsKey(productDetails.get(arg2).getSku())) {
                    if (priceHashMap.get(productDetails.get(arg2).getSku()).getPrice() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("product", productDetails.get(arg2).getJsonObject().toString());
                        bundle.putString("price", priceHashMap.get(productDetails.get(arg2).getSku()).getJsonObject().toString());
                        jump(ProductDetailFragment.class.getName(), productDetails.get(arg2).getName(), bundle);
                    } else {
                        ApplicationTool.showToast("此商品暂未设置价格");
                    }
                } else {
                    ApplicationTool.showToast("此商品暂未设置价格");
                }
            }
        });
        classButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(classLayout)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(classLayout);
                }
            }
        });
        cityButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(SuningAreaSelectFragment.class.getName(), "设置云商城收货区域");
            }
        });
    }

    private void getSuningProducts() {
        refreshScrollView.setMode(Mode.BOTH);
        productPools.clear();
        priceHashMap.clear();
        productDetails.clear();
        productAdapter.notifyDataSetChanged();
        pageNo = 0;
        totalPage = 0;
        JSONObject data = new JSONObject();
        try {
            data.put("accessToken", SuningManager.getSignature().getToken());
            data.put("appKey", SuningManager.appKey);
            data.put("v", SuningManager.version);
            data.put("categoryId", productClass.getCategoryId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("data", data.toString()));
        post(API.API_SUNING_PRODUCT_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshScrollView.onRefreshComplete();
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
                                        getSuningProducts();
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
                            JSONArray array = object.optJSONArray("prods");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    productPools.add(new ModelProductPool(array.optJSONObject(i)));
                                }
                                if (productPools.isEmpty()) {
                                    //无商品
                                    missionNodata();
                                } else {
                                    //有商品
                                    if (productPools.size() % pageSize == 0) {
                                        totalPage = productPools.size() / pageSize;
                                    } else {
                                        totalPage = productPools.size() / pageSize + 1;
                                    }
                                    new GetSuningProductDetail().execute();
                                }
                                return;
                            }
                        }
                        ApplicationTool.showToast(object.optString("returnMsg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                missionNodata();
            }
        });
    }

    class GetSuningProductDetail extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            missionStart();
            pageNo++;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            List<ModelProductPool> pools = new ArrayList<>();
            if (pageNo < totalPage) {
                pools = productPools.subList((pageNo - 1) * 12, pageNo * 12);
            } else {
                pools = productPools.subList((pageNo - 1) * 12, productPools.size());
            }
            for (int i = 0; i < pools.size(); i++) {
                JSONObject data = new JSONObject();
                try {
                    data.put("accessToken", SuningManager.getSignature().getToken());
                    data.put("appKey", SuningManager.appKey);
                    data.put("v", SuningManager.version);
                    data.put("sku", pools.get(i).getSku());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<RequestParam> requestParams = new ArrayList<>();
                requestParams.add(new RequestParam("data", data.toString()));
                String result = post(API.API_SUNING_PRODUCT_DETAIL, requestParams);
                System.out.println(result);
                //令牌过期
                if (SuningManager.isSignatureOutOfDate(result)) {
                    return 2;
                }
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optBoolean("isSuccess")) {
                            ModelProductDetail productDetail = new ModelProductDetail(object);
                            if (productDetail.getState() == 1) {
                                productDetails.add(productDetail);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return 1;
                    }
                }
            }
            //获取数据成功
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            System.out.println(result + "/" + productDetails.size() + "/" + productDetails);
            missionComplete();
            refreshScrollView.onRefreshComplete();
            switch (result) {

                case 0:
                    if (productDetails.isEmpty()) {
                        //无商品
                        missionNodata();
                    } else {
                        //有商品
                        productAdapter.notifyDataSetChanged();
                        getSuningProductPrice();
                    }
                    break;

                case 1:
                    missionNodata();
                    break;

                case 2:
                    post(API.API_SUNING_SIGNATURE, new OnNetworkListener() {
                        @Override
                        public void onSuccess(String result) {
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                                        JSONObject dataMap = object.optJSONObject("dataMap");
                                        Content.saveStringContent(Parameters.CACHE_KEY_SUNING_SIGNATURE, dataMap.toString());
                                        new GetSuningProductDetail().execute();
                                        return;
                                    }
                                    ApplicationTool.showToast(object.optString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    break;

                default:
                    missionNodata();
                    break;
            }
        }
    }

    private void getSuningProductPrice() {
        JSONArray dataArray = new JSONArray();
        List<ModelProductPool> pools = new ArrayList<>();
        if (pageNo < totalPage) {
            pools = productPools.subList((pageNo - 1) * 12, pageNo * 12);
        } else {
            pools = productPools.subList((pageNo - 1) * 12, productPools.size());
        }
        for (int i = 0; i < pools.size(); i++) {
            dataArray.put(pools.get(i).getSku());
        }
        JSONObject data = new JSONObject();
        try {
            data.put("accessToken", SuningManager.getSignature().getToken());
            data.put("appKey", SuningManager.appKey);
            data.put("v", SuningManager.version);
            data.put("cityId", SuningManager.getSuningAreas().getCity().getCode());
            data.put("sku", dataArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("data", data.toString()));
        post(API.API_SUNING_PRODUCT_PRICE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
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
                                        getSuningProductPrice();
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
                                for (int j = 0; j < array.length(); j++) {
                                    ModelProductPrice productPrice = new
                                            ModelProductPrice(array.optJSONObject(j));
                                    priceHashMap.put(productPrice.getSkuId(), productPrice);
                                }
                                productAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return productDetails.size();
        }

        @Override
        public Object getItem(int i) {
            return productDetails.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.list_product_suning, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) view.findViewById(R.id.list_product_suning_image);
                viewHolder.nameTextView = (TextView) view.findViewById(R.id.list_product_suning_name);
                viewHolder.priceTextView = (TextView) view.findViewById(R.id.list_product_suning_price);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth() / 2);
                viewHolder.imageView.setLayoutParams(layoutParams);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            ModelProductDetail productDetail = productDetails.get(i);
            ImageLoader.getInstance().displayImage(productDetail.getImage(), viewHolder.imageView);
            viewHolder.nameTextView.setText(productDetail.getName());
            if (priceHashMap.containsKey(productDetail.getSku())) {
                if (priceHashMap.get(productDetail.getSku()).getPrice() > 0) {
                    viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
                            + decimalFormat.format(priceHashMap.get(productDetail.getSku()).getPrice()));
                } else {
                    viewHolder.priceTextView.setHint("暂未设置价格");
                }
            } else {
                viewHolder.priceTextView.setHint("暂未设置价格");
            }
            return view;
        }

        class ViewHolder {
            ImageView imageView;
            TextView nameTextView;
            TextView priceTextView;
        }

    }

}
