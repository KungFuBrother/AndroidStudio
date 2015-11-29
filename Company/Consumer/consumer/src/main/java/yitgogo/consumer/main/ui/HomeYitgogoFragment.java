package yitgogo.consumer.main.ui;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
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
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.home.model.ModelAds;
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.home.model.ModelProduct;
import yitgogo.consumer.product.ui.ClassesFragment;
import yitgogo.consumer.product.ui.ProductSearchFragment;
import yitgogo.consumer.product.ui.SaleTimeListFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerGridView;

public class HomeYitgogoFragment extends BaseNetworkFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerGridView productGridView;
    List<ModelProduct> products;
    HashMap<String, ModelListPrice> priceMap;
    ProductAdapter productAdapter;

    ImageView classButton, searchButton;

    String currentStoreId = "";

    List<ModelAds> ads;
    AdsAdapter adsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_yitgogo);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(HomeYitgogoFragment.class.getName());
        if (!currentStoreId.equals(Store.getStore().getStoreId())) {
            currentStoreId = Store.getStore().getStoreId();
            reload();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(HomeYitgogoFragment.class.getName());
    }

    @Override
    protected void init() {
        ads = new ArrayList<>();
        adsAdapter = new AdsAdapter();
        products = new ArrayList<>();
        priceMap = new HashMap<>();
        productAdapter = new ProductAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.home_yitgogo_refresh);
        productGridView = (InnerGridView) contentView
                .findViewById(R.id.home_yitgogo_product_list);
        classButton = (ImageView) contentView
                .findViewById(R.id.home_yitgogo_class);
        searchButton = (ImageView) contentView
                .findViewById(R.id.home_yitgogo_search);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        refreshScrollView.setMode(Mode.BOTH);
        productGridView.setAdapter(productAdapter);
    }

    @Override
    protected void registerViews() {
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
                        getProduct();
                    }
                });
        productGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                showProductDetail(products.get(arg2).getId(), products
                                .get(arg2).getProductName(),
                        CaptureActivity.SALE_TYPE_NONE);
            }
        });
        classButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ClassesFragment.class.getName(), "商品分类");
            }
        });
        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ProductSearchFragment.class.getName(), "商品搜索");
            }
        });
    }

    @Override
    protected void reload() {
        super.reload();
        getAds();
        refreshScrollView.setMode(Mode.BOTH);
        pagenum = 0;
        products.clear();
        productAdapter.notifyDataSetChanged();
        pagenum++;
        getProduct();
    }

    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position);
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
                        LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth() / 25 * 16);
                convertView.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelProduct product = products.get(position);
            holder.nameTextView.setText(product.getProductName());
            if (priceMap.containsKey(product.getId())) {
                holder.priceTextView.setText(Parameters.CONSTANT_RMB
                        + decimalFormat.format(priceMap.get(product.getId())
                        .getPrice()));
            }
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(product.getImg()), holder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView priceTextView, nameTextView;
        }
    }

    private void getAds() {
        ads.clear();
        adsAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("number", Store.getStore().getStoreNumber()));
        post(API.API_ADS, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (result.length() > 0) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    ads.add(new ModelAds(array.getJSONObject(i)));
                                }
                                adsAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getProduct() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("pageNo", pagenum + ""));
        requestParams.add(new RequestParam("jmdId", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("pageSize", pagesize + ""));
        post(API.API_PRODUCT_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshScrollView.onRefreshComplete();
                if (!TextUtils.isEmpty(result)) {
                    JSONObject info;
                    try {
                        info = new JSONObject(result);
                        if (info.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray productArray = info.optJSONArray("dataList");
                            if (productArray != null) {
                                if (productArray.length() > 0) {
                                    if (productArray.length() < pagesize) {
                                        refreshScrollView
                                                .setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    }
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < productArray.length(); i++) {
                                        ModelProduct product = new ModelProduct(
                                                productArray.getJSONObject(i));
                                        products.add(product);
                                        if (i > 0) {
                                            stringBuilder.append(",");
                                        }
                                        stringBuilder.append(product.getId());
                                    }
                                    productAdapter.notifyDataSetChanged();
                                    getProductPrice(stringBuilder.toString());
                                    return;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                refreshScrollView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
        });
    }

    private void getProductPrice(String productId) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("jmdId", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_PRICE_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray priceArray = object.getJSONArray("dataList");
                            if (priceArray.length() > 0) {
                                for (int i = 0; i < priceArray.length(); i++) {
                                    ModelListPrice priceList = new ModelListPrice(
                                            priceArray.getJSONObject(i));
                                    priceMap.put(priceList.getProductId(),
                                            priceList);
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

    class AdsAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return ads.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            final int index = position;
            View imageLayout = layoutInflater.inflate(
                    R.layout.adapter_viewpager, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.view_pager_img);
            ProgressBar spinner = (ProgressBar) imageLayout
                    .findViewById(R.id.view_pager_loading);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            spinner.setVisibility(View.GONE);
            if (ads.get(position).getAdverImg().length() > 0) {
                ImageLoader.getInstance().displayImage(
                        ads.get(position).getAdverImg(), imageView);
            } else {
                ImageLoader.getInstance().displayImage(
                        ads.get(position).getDefaultImg(), imageView);
            }
            imageLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 产品广告，跳转到产品详情界面
                    if (ads.get(index).getType().contains("产品")) {
                        String productId = "";
                        if (ads.get(index).getAdverUrl().length() > 0) {
                            productId = ads.get(index).getAdverUrl();
                        } else {
                            productId = ads.get(index).getDefaultUrl();
                        }
                        showProductDetail(productId, ads.get(index)
                                .getAdvername(), CaptureActivity.SALE_TYPE_NONE);
                    } else {
                        // 主题广告，跳转到活动
                        String saleClassId = "";
                        if (ads.get(index).getAdverUrl().length() > 0) {
                            saleClassId = ads.get(index).getAdverUrl();
                        } else {
                            saleClassId = ads.get(index).getDefaultUrl();
                        }
                        Bundle bundle = new Bundle();
                        bundle.putString("saleClassId", saleClassId);
                        jump(SaleTimeListFragment.class.getName(),
                                ads.get(index).getAdvername(), bundle);
                    }
                }
            });
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }

}
