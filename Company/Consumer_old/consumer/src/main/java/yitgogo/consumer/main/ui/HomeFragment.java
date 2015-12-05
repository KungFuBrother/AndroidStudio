package yitgogo.consumer.main.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.home.model.ModelProduct;
import yitgogo.consumer.home.part.PartAdsFragment;
import yitgogo.consumer.home.part.PartBianminFragment;
import yitgogo.consumer.home.part.PartBrandFragment;
import yitgogo.consumer.home.part.PartFreshFragment;
import yitgogo.consumer.home.part.PartLocalBusinessFragment;
import yitgogo.consumer.home.part.PartMiaoshaFragment;
import yitgogo.consumer.home.part.PartSaleTimeFragment;
import yitgogo.consumer.home.part.PartScoreFragment;
import yitgogo.consumer.home.part.PartStoreFragment;
import yitgogo.consumer.home.part.PartTejiaFragment;
import yitgogo.consumer.home.part.PartThemeFragment;
import yitgogo.consumer.home.task.GetAds;
import yitgogo.consumer.home.task.GetBrand;
import yitgogo.consumer.home.task.GetLocalGoods;
import yitgogo.consumer.home.task.GetLocalService;
import yitgogo.consumer.home.task.GetLoveFresh;
import yitgogo.consumer.home.task.GetMiaoshaProduct;
import yitgogo.consumer.home.task.GetSaleTejia;
import yitgogo.consumer.home.task.GetSaleTheme;
import yitgogo.consumer.home.task.GetSaleTimes;
import yitgogo.consumer.home.task.GetScoreProduct;
import yitgogo.consumer.home.task.GetStore;
import yitgogo.consumer.local.ui.NongfuFragment;
import yitgogo.consumer.product.ui.ClassesFragment;
import yitgogo.consumer.product.ui.ProductSearchFragment;
import yitgogo.consumer.store.model.ModelStoreArea;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.ScreenUtil;
import yitgogo.consumer.view.InnerGridView;

public class HomeFragment extends BaseNotifyFragment implements OnClickListener {

    PullToRefreshScrollView refreshScrollView;
    InnerGridView productGridView;
    ImageView classButton, scanButton, nongfuButton;
    TextView searchTextView;

    LinearLayout timeLayout;
    TextView dayTextView, hourTextView, minuteTextView, secondsTextView;

    List<ModelProduct> products;
    HashMap<String, ModelListPrice> priceMap;
    ProductAdapter productAdapter;

    String currentStoreId = "";

    GetSaleTheme getSaleTheme;
    GetMiaoshaProduct getMiaoshaProduct;
    GetLoveFresh getLoveFresh;
    GetScoreProduct getScoreProduct;
    GetSaleTimes getSaleTimes;
    GetSaleTejia getSaleTejia;
    GetStore getStore;
    GetAds getAds;
    GetLocalGoods getLocalGoods;
    GetLocalService getLocalService;
    GetBrand getBrand;

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x12:
                    PartTejiaFragment.getTejiaFragment().initViews();
                    break;

                case 1:
                    if (timeLayout.getVisibility() == View.GONE) {
                        return;
                    }
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    long startTime = (long) 1449763200 * 1000;
                    if (startTime > currentTime) {
                        timeLayout.setVisibility(View.VISIBLE);
                        long time = startTime - currentTime;
                        long day = time / 86400000;
                        long hour = time % 86400000 / 3600000;
                        long minute = time % 3600000 / 60000;
                        long seconds = time % 60000 / 1000;
                        StringBuilder stringBuilder = new StringBuilder();
                        if (day < 10) {
                            stringBuilder.append("0");
                        }
                        stringBuilder.append(day);
                        dayTextView.setText(stringBuilder.toString());

                        stringBuilder = new StringBuilder();
                        if (hour < 10) {
                            stringBuilder.append("0");
                        }
                        stringBuilder.append(hour);
                        hourTextView.setText(stringBuilder.toString());

                        stringBuilder = new StringBuilder();
                        if (minute < 10) {
                            stringBuilder.append("0");
                        }
                        stringBuilder.append(minute);
                        minuteTextView.setText(stringBuilder.toString());

                        stringBuilder = new StringBuilder();
                        if (seconds < 10) {
                            stringBuilder.append("0");
                        }
                        stringBuilder.append(seconds);
                        secondsTextView.setText(stringBuilder.toString());

                        handler.sendEmptyMessageDelayed(1, 1000);
                    } else {
                        timeLayout.setVisibility(View.GONE);
                    }
                    break;

                default:
                    break;
            }
        }

        ;
    };
    boolean isAlive = false;

    private void runAnimateThread() {
        isAlive = true;
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (isAlive) {
                    try {
                        Thread.sleep(10000);
                        handler.sendEmptyMessage(0x12);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_main);
        init();
        findViews();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showDisconnectMargin();
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
            useCache = true;
            currentStoreId = Store.getStore().getStoreId();
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        isAlive = false;
        super.onDestroy();
    }

    private void init() {
        measureScreen();
        products = new ArrayList<>();
        priceMap = new HashMap<>();
        productAdapter = new ProductAdapter();
        getStoreAreas();
    }

    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.home_scroll);
        productGridView = (InnerGridView) contentView
                .findViewById(R.id.home_product_list);
        classButton = (ImageView) contentView
                .findViewById(R.id.home_title_class);
        scanButton = (ImageView) contentView.findViewById(R.id.home_title_scan);
        nongfuButton = (ImageView) contentView
                .findViewById(R.id.home_part_nongfu);
        searchTextView = (TextView) contentView
                .findViewById(R.id.home_title_edit);

        timeLayout = (LinearLayout) contentView.findViewById(R.id.ll_date_time);
        dayTextView = (TextView) contentView.findViewById(R.id.tv_timer_day);
        hourTextView = (TextView) contentView.findViewById(R.id.tv_timer_hour);
        secondsTextView = (TextView) contentView.findViewById(R.id.tv_timer_second);
        minuteTextView = (TextView) contentView.findViewById(R.id.tv_timer_minute);

        initViews();
        registerViews();
    }

    protected void initViews() {
        handler.sendEmptyMessage(1);
        refreshScrollView.setMode(Mode.BOTH);
        productGridView.setAdapter(productAdapter);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, screenWidth / 3);
        layoutParams.setMargins(0, 0, 0, ScreenUtil.dip2px(8));
        nongfuButton.setLayoutParams(layoutParams);
        getFragmentManager().beginTransaction()
                .replace(R.id.home_part_ads_layout,
                        PartAdsFragment.getAdsFragment())
                .replace(R.id.home_part_activity_layout,
                        PartBianminFragment.getBianminFragment())
                .replace(R.id.home_part_miaosha_layout,
                        PartMiaoshaFragment.getMiaoshaFragment())
                .replace(R.id.home_part_fresh_layout,
                        PartFreshFragment.getFreshFragment())
                .replace(R.id.home_part_score_layout,
                        PartScoreFragment.getScoreFragment())
                .replace(R.id.home_part_sale_time_layout,
                        PartSaleTimeFragment.getSaleTimeFragment())
                .replace(R.id.home_part_store_layout,
                        PartStoreFragment.getStoreFragment())
                .replace(R.id.home_part_theme_layout,
                        PartThemeFragment.getThemeFragment())
                .replace(R.id.home_part_tejia_layout,
                        PartTejiaFragment.getTejiaFragment())
                .replace(R.id.home_part_local_layout,
                        PartLocalBusinessFragment.getLocalBusinessFragment())
                .replace(R.id.home_part_brand_layout,
                        PartBrandFragment.getBrandFragment()).commit();
        handler.sendEmptyMessageDelayed(0x13, 5000);
    }

    @Override
    protected void registerViews() {
        refreshScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        useCache = false;
                        refresh();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        new GetProduct().execute();
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
        classButton.setOnClickListener(this);
        scanButton.setOnClickListener(this);
        searchTextView.setOnClickListener(this);
        nongfuButton.setOnClickListener(this);
    }

    private void refresh() {
        isAlive = false;
        pagenum = 0;
        refreshScrollView.setMode(Mode.BOTH);
        products.clear();
        productAdapter.notifyDataSetChanged();
        getSaleTheme();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.home_title_class:
                jump(ClassesFragment.class.getName(), "商品分类");
                break;

            case R.id.home_title_scan:
                startActivity(new Intent(getActivity(), CaptureActivity.class));
                break;

            case R.id.home_title_edit:
                jump(ProductSearchFragment.class.getName(), "商品搜索", true);
                break;

            case R.id.home_part_nongfu:
                jump(NongfuFragment.class.getName(), "农副产品");
                break;

            default:
                break;
        }
    }

    private void getSaleTheme() {
        if (getSaleTheme != null) {
            if (getSaleTheme.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getSaleTheme = new GetSaleTheme() {
            @Override
            protected void onPostExecute(String result) {
                PartThemeFragment.getThemeFragment().refresh(result);
                getMiaoshaProduct();
            }
        };
        getSaleTheme.execute(useCache);
    }

    private void getMiaoshaProduct() {
        if (getMiaoshaProduct != null) {
            if (getMiaoshaProduct.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getMiaoshaProduct = new GetMiaoshaProduct() {
            @Override
            protected void onPostExecute(String result) {
                PartMiaoshaFragment.getMiaoshaFragment().refresh(result);
                getLoveFresh();
            }
        };
        getMiaoshaProduct.execute(useCache);
    }

    private void getLoveFresh() {
        if (getLoveFresh != null) {
            if (getLoveFresh.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getLoveFresh = new GetLoveFresh() {
            @Override
            protected void onPostExecute(String result) {
                PartFreshFragment.getFreshFragment().refresh(result);
                getScoreProduct();
            }
        };
        getLoveFresh.execute(useCache);
    }

    private void getScoreProduct() {
        if (getScoreProduct != null) {
            if (getScoreProduct.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getScoreProduct = new GetScoreProduct() {
            @Override
            protected void onPostExecute(String result) {
                PartScoreFragment.getScoreFragment().refresh(result);
                getSaleTimes();
            }
        };
        getScoreProduct.execute(useCache);
    }

    private void getSaleTimes() {
        if (getSaleTimes != null) {
            if (getSaleTimes.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getSaleTimes = new GetSaleTimes() {
            @Override
            protected void onPostExecute(String result) {
                PartSaleTimeFragment.getSaleTimeFragment().refresh(result);
                getSaleTejia();
            }
        };
        getSaleTimes.execute(useCache);
    }

    private void getSaleTejia() {
        if (getSaleTejia != null) {
            if (getSaleTejia.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getSaleTejia = new GetSaleTejia() {
            @Override
            protected void onPostExecute(String result) {
                PartTejiaFragment.getTejiaFragment().refresh(result);
                getStore();
            }
        };
        getSaleTejia.execute(useCache);
    }

    private void getStore() {
        if (getStore != null) {
            if (getStore.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getStore = new GetStore() {
            @Override
            protected void onPostExecute(String result) {
                PartStoreFragment.getStoreFragment().refresh(result);
                getAds();
            }
        };
        getStore.execute(useCache);
    }

    private void getAds() {
        if (getAds != null) {
            if (getAds.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getAds = new GetAds() {
            @Override
            protected void onPostExecute(String result) {
                PartAdsFragment.getAdsFragment().refresh(result);
                runAnimateThread();
                getLocalGoods();
            }
        };
        getAds.execute(useCache);
    }

    private void getLocalGoods() {
        if (getLocalGoods != null) {
            if (getLocalGoods.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getLocalGoods = new GetLocalGoods() {
            @Override
            protected void onPostExecute(String result) {
                PartLocalBusinessFragment.getLocalBusinessFragment()
                        .refreshGoods(result);
                getLocalService();
            }
        };
        getLocalGoods.execute(useCache);
    }

    private void getLocalService() {
        if (getLocalService != null) {
            if (getLocalService.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getLocalService = new GetLocalService() {
            @Override
            protected void onPostExecute(String result) {
                PartLocalBusinessFragment.getLocalBusinessFragment()
                        .refreshService(result);
                getBrand();
            }
        };
        getLocalService.execute(useCache);
    }

    private void getBrand() {
        if (getBrand != null) {
            if (getBrand.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getBrand = new GetBrand() {
            @Override
            protected void onPostExecute(String result) {
                PartBrandFragment.getBrandFragment().refresh(result);
                new GetProduct().execute();
            }
        };
        getBrand.execute(useCache);
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
                        LayoutParams.MATCH_PARENT, screenWidth / 25 * 16);
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

    class GetProduct extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pagenum++;
        }

        @Override
        protected String doInBackground(Void... arg0) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("pageNo", pagenum + ""));
            params.add(new BasicNameValuePair("jmdId", Store.getStore()
                    .getStoreId()));
            params.add(new BasicNameValuePair("pageSize", pagesize + ""));
            return netUtil.postWithoutCookie(API.API_PRODUCT_LIST, params,
                    useCache, true);
        }

        @Override
        protected void onPostExecute(String result) {
            refreshScrollView.onRefreshComplete();
            if (result.length() > 0) {
                JSONObject info;
                try {
                    info = new JSONObject(result);
                    if (info.getString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray productArray = info.optJSONArray("dataList");
                        if (productArray != null) {
                            if (productArray.length() > 0) {
                                if (productArray.length() < pagesize) {
                                    refreshScrollView
                                            .setMode(Mode.PULL_FROM_START);
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
                                new GetPriceList().execute(stringBuilder
                                        .toString());
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            refreshScrollView.setMode(Mode.PULL_FROM_START);
        }
    }

    class GetPriceList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("jmdId", Store.getStore()
                    .getStoreId()));
            valuePairs.add(new BasicNameValuePair("productId", params[0]));
            return netUtil.postWithoutCookie(API.API_PRICE_LIST, valuePairs,
                    false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() > 0) {
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
    }

    private void getStoreAreas() {
        if (TextUtils.isEmpty(Content.getStringContent("product_detail_area_name", ""))) {
            new GetStoreArea().execute();
        }
    }

    class GetStoreArea extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("spid", Store.getStore().getStoreId()));
            return netUtil.postWithoutCookie(API.API_STORE_SELECTED_AREA, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            HashMap<Integer, ModelStoreArea> areaHashMap = new HashMap<>();
                            for (int i = 0; i < array.length(); i++) {
                                ModelStoreArea storeArea = new ModelStoreArea(array.optJSONObject(i));
                                areaHashMap.put(storeArea.getType(), storeArea);
                            }
                            Content.saveStringContent("product_detail_area_name", getAreaName(areaHashMap));
                            Content.saveStringContent("product_detail_area_id", getAreaId(areaHashMap));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private String getAreaName(HashMap<Integer, ModelStoreArea> areaHashMap) {
        StringBuilder builder = new StringBuilder();
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(areaHashMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, ModelStoreArea>>() {
            @Override
            public int compare(Map.Entry<Integer, ModelStoreArea> area1, Map.Entry<Integer, ModelStoreArea> area2) {
                return area1.getKey().compareTo(area2.getKey());
            }
        });
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) {
                builder.append(">");
            }
            builder.append(entries.get(i).getValue().getName());
        }
        return builder.toString();
    }

    private String getAreaId(HashMap<Integer, ModelStoreArea> areaHashMap) {
        List<Map.Entry<Integer, ModelStoreArea>> entries = new ArrayList<>(areaHashMap.entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<Integer, ModelStoreArea>>() {
            @Override
            public int compare(Map.Entry<Integer, ModelStoreArea> area1, Map.Entry<Integer, ModelStoreArea> area2) {
                return area1.getKey().compareTo(area2.getKey());
            }
        });
        return entries.get(entries.size() - 1).getValue().getId();
    }
}
