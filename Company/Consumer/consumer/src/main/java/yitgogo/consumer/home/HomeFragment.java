package yitgogo.consumer.home;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
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
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.home.model.ModelProduct;
import yitgogo.consumer.home.model.ModelSaleMiaosha;
import yitgogo.consumer.home.model.ModelSaleTheme;
import yitgogo.consumer.home.model.ModelSaleTime;
import yitgogo.consumer.product.ui.SaleTimeListFragment;
import yitgogo.consumer.product.ui.WebFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.AutoScrollViewPager;
import yitgogo.consumer.view.InnerGridView;

/**
 * Created by Tiger on 2015-11-24.
 */
public class HomeFragment extends BaseNetworkFragment {

    PullToRefreshScrollView refreshScrollView;

    ImageView classButton, scanButton;
    TextView searchTextView;

    AutoScrollViewPager saleThemePager;
    List<ModelSaleTheme> saleThemes;
    SaleThemeAdapter saleThemeAdapter;

    GridView entranceGridView;

    LinearLayout saleKillMoreButton;
    GridView saleKillGridView;
    List<ModelSaleMiaosha> saleMiaoshas;
    //    MiaoshaAdapter miaoshaAdapter;
    HashMap<String, Double> prices;

    ImageView nongfuButton;

    AutoScrollViewPager saleTimePager;
    List<ModelSaleTime> saleTimes;
    SaleTimeAdapter saleTimeAdapter;

    //特价

    AutoScrollViewPager adsPager;

    //品牌

    InnerGridView productGridView;

    List<ModelProduct> products;
    HashMap<String, ModelListPrice> priceMap;
//    ProductAdapter productAdapter;

    String currentStoreId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_main);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(HomeFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(HomeFragment.class.getName());
        if (!currentStoreId.equals(Store.getStore().getStoreId())) {
            currentStoreId = Store.getStore().getStoreId();
//            refresh();
        }
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView.findViewById(R.id.home_scroll);

        classButton = (ImageView) contentView.findViewById(R.id.home_title_class);
        scanButton = (ImageView) contentView.findViewById(R.id.home_title_scan);
        searchTextView = (TextView) contentView.findViewById(R.id.home_title_edit);

        saleThemePager = (AutoScrollViewPager) contentView.findViewById(R.id.home_sale_theme);

        entranceGridView = (GridView) contentView.findViewById(R.id.home_entrance);

        saleKillMoreButton = (LinearLayout) contentView.findViewById(R.id.home_sale_kill_more);
        saleKillGridView = (GridView) contentView.findViewById(R.id.home_sale_kill);

        saleTimePager = (AutoScrollViewPager) contentView.findViewById(R.id.home_sale_time);

        adsPager = (AutoScrollViewPager) contentView.findViewById(R.id.home_sale_ads);

        nongfuButton = (ImageView) contentView.findViewById(R.id.home_part_nongfu);

        productGridView = (InnerGridView) contentView.findViewById(R.id.home_product_list);

        initViews();
        registerViews();
    }

    @Override
    protected void init() {
        products = new ArrayList<>();
        priceMap = new HashMap<>();
//        productAdapter = new ProductAdapter();
        saleThemes = new ArrayList<>();
        saleThemeAdapter = new SaleThemeAdapter();
    }

    @Override
    protected void initViews() {
        saleThemePager.setAdapter(saleThemeAdapter);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth() / 3);
        saleThemePager.setLayoutParams(layoutParams);
        saleThemePager.setInterval(6000);
        saleThemePager.setAutoScrollDurationFactor(5);
        saleThemePager.startAutoScroll();
    }

    @Override
    protected void registerViews() {

    }

    private void getSaleTheme() {
        saleThemes.clear();
        saleThemeAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("strno", Store.getStore().getStoreNumber()));
        post(API.API_SALE_ACTIVITY, requestParams, new OnNetworkListener() {
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
                                    saleThemes.add(new ModelSaleTheme(array
                                            .optJSONObject(i)));
                                }
                                saleThemeAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getSaleKill() {
        saleMiaoshas.clear();
//        miaoshaAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("strno", Store.getStore().getStoreNumber()));
        post(API.API_SALE_MIAOSHA, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                if (array.length() > 4) {
                                    for (int i = 0; i < 4; i++) {
                                        saleMiaoshas.add(new ModelSaleMiaosha(array
                                                .optJSONObject(i)));
                                    }
                                } else {
                                    for (int i = 0; i < array.length(); i++) {
                                        saleMiaoshas.add(new ModelSaleMiaosha(array
                                                .optJSONObject(i)));
                                    }
                                }
//                                miaoshaAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getSaleTime() {
        saleTimes.clear();
        saleTimeAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("strno", Store.getStore().getStoreNumber()));
        requestParams.add(new RequestParam("flag", "1"));
        post(API.API_SALE_CLASS, requestParams, new OnNetworkListener() {
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
                                    saleTimes.add(new ModelSaleTime(array
                                            .optJSONObject(i)));
                                }
                                saleTimeAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getSaleTejia() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("strno", Store.getStore().getStoreNumber()));
        post(API.API_SALE_TEJIA, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
//                            saleTejia = new ModelSaleTejia(dataMap);
                            initViews();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getAds() {
//        ads.clear();
//        adsAdapter.notifyDataSetChanged();
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
//                                    ads.add(new ModelAds(array.getJSONObject(i)));
                                }
//                                adsAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getBrand() {
//        brands.clear();
//        brandAdapter.notifyDataSetChanged();
        post(API.API_HOME_BRAND, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (result.length() > 0) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
//                            if (array != null) {
//                                for (int i = 0; i < array.length(); i++) {
//                                    brands.add(new ModelHomeBrand(array
//                                            .optJSONObject(i)));
//                                }
//                                if (brands.size() > 0) {
//                                    getView().setVisibility(View.VISIBLE);
//                                    int colums = 0;
//                                    if (brands.size() < 8) {
//                                        colums = 4;
//                                    } else {
//                                        if (brands.size() % 2 == 0) {
//                                            colums = brands.size() / 2;
//                                        } else {
//                                            colums = brands.size() / 2 + 1;
//                                        }
//                                    }
//                                    brandList
//                                            .setLayoutParams(new LinearLayout.LayoutParams(
//                                                    colums * (screenWidth / 4),
//                                                    LinearLayout.LayoutParams.MATCH_PARENT));
//                                    brandList.setColumnWidth(screenWidth / 4);
//                                    brandList.setStretchMode(GridView.NO_STRETCH);
//                                    brandList.setNumColumns(colums);
//                                    brandAdapter.notifyDataSetChanged();
//                                }
//                            }
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
//                                    productAdapter.notifyDataSetChanged();
//                                    new GetPriceList().execute(stringBuilder
//                                            .toString());
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
//                                productAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class SaleThemeAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return saleThemes.size();
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
            ImageLoader.getInstance().displayImage(
                    saleThemes.get(position).getMoblieImg(), imageView);
            imageLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", WebFragment.TYPE_URL);
                    bundle.putString("url", saleThemes.get(index)
                            .getMoblieUrl());
                    jump(WebFragment.class.getName(), saleThemes.get(index)
                            .getThemeName(), bundle);
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

    class SaleTimeAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return saleTimes.size();
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

            ImageLoader.getInstance().displayImage(
                    saleTimes.get(position).getSaleClassImage(), imageView);
            imageLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("saleClassId", saleTimes.get(index)
                            .getSaleClassId());
                    jump(SaleTimeListFragment.class.getName(),
                            saleTimes.get(index).getSaleClassName(), bundle);
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
