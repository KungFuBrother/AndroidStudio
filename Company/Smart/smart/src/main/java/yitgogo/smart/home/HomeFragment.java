package yitgogo.smart.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.bianmin.BianminFragment;
import yitgogo.smart.home.model.HomeData;
import yitgogo.smart.home.model.ModelClass;
import yitgogo.smart.home.model.ModelHomeBrand;
import yitgogo.smart.home.model.ModelListPrice;
import yitgogo.smart.home.model.ModelLocalStore;
import yitgogo.smart.home.model.ModelProduct;
import yitgogo.smart.home.model.ModelSaleTheme;
import yitgogo.smart.home.model.ModelSaleTime;
import yitgogo.smart.home.part.PartAdsFragment;
import yitgogo.smart.home.part.PartBrandFragment;
import yitgogo.smart.home.part.PartClassFragment;
import yitgogo.smart.home.part.PartFreshFragment;
import yitgogo.smart.home.part.PartLocalBusinessFragment;
import yitgogo.smart.home.part.PartLocalMiaoshaFragment;
import yitgogo.smart.home.part.PartLocalTejiaFragment;
import yitgogo.smart.home.part.PartMiaoshaFragment;
import yitgogo.smart.home.part.PartSaleTimeFragment;
import yitgogo.smart.home.part.PartScoreFragment;
import yitgogo.smart.home.part.PartStoreFragment;
import yitgogo.smart.home.part.PartTejiaFragment;
import yitgogo.smart.home.part.PartThemeFragment;
import yitgogo.smart.local.NongfuFragment;
import yitgogo.smart.model.ModelMachineArea;
import yitgogo.smart.print.PrintService;
import yitgogo.smart.search.ProductSearchFragment;
import yitgogo.smart.suning.ui.SuningProductFragment;
import yitgogo.smart.task.HomeTask;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.PackageTool;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.tools.QrCodeTool;
import yitgogo.smart.view.InnerGridView;

public class HomeFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerGridView productGridView;
    ImageView nongfuImageView;
    LinearLayout bianmButton, suningButton, searchButton, qrcodeButton;

    FrameLayout timeLayout;
    TextView dayTextView, hourTextView, minuteTextView, secondsTextView;
    ImageView closeTimeButton;

    List<ModelProduct> products;
    HashMap<String, ModelListPrice> priceMap;
    ProductAdapter productAdapter;

    int pageSize = 12, pageNo = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
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
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        getActivity().startService(
                new Intent(getActivity(), PrintService.class));
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(HomeFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(HomeFragment.class.getName());
    }

    @Override
    public void onDestroy() {
        getActivity()
                .stopService(new Intent(getActivity(), PrintService.class));
        updateVersionInfo("OFFLINE");
        super.onDestroy();
    }

    private void init() {
        measureScreen();
        products = new ArrayList<ModelProduct>();
        priceMap = new HashMap<String, ModelListPrice>();
        productAdapter = new ProductAdapter();
        updateVersionInfo("ONLINE");
        getMachineArea();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.main_refresh);
        productGridView = (InnerGridView) contentView
                .findViewById(R.id.main_product);
        nongfuImageView = (ImageView) contentView
                .findViewById(R.id.home_part_nongfu_layout);
        bianmButton = (LinearLayout) contentView.findViewById(R.id.main_bianmin);
        suningButton = (LinearLayout) contentView.findViewById(R.id.main_suning);
        searchButton = (LinearLayout) contentView.findViewById(R.id.main_search);
        qrcodeButton = (LinearLayout) contentView.findViewById(R.id.main_qrcode);

        timeLayout = (FrameLayout) contentView.findViewById(R.id.home_1212_layout);
        dayTextView = (TextView) contentView.findViewById(R.id.home_1212_day);
        hourTextView = (TextView) contentView.findViewById(R.id.home_1212_hour);
        minuteTextView = (TextView) contentView.findViewById(R.id.home_1212_minute);
        secondsTextView = (TextView) contentView.findViewById(R.id.home_1212_second);
        closeTimeButton = (ImageView) contentView.findViewById(R.id.home_1212_close);

        initViews();
        registerViews();
    }

    protected void initViews() {
        handler.sendEmptyMessage(1);
        refreshScrollView.setMode(Mode.BOTH);
        productGridView.setAdapter(productAdapter);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.home_part_theme_layout,
                        PartThemeFragment.getThemeFragment())
                .replace(R.id.home_part_class_layout,
                        PartClassFragment.getClassFragment())
                .replace(R.id.home_part_miaosha_layout,
                        PartMiaoshaFragment.getMiaoshaFragment())
                .replace(R.id.home_part_fresh_layout,
                        PartFreshFragment.getFreshFragment())
                .replace(R.id.home_part_sale_time_layout,
                        PartSaleTimeFragment.getSaleTimeFragment())
                .replace(R.id.home_part_brand_layout,
                        PartBrandFragment.getBrandFragment())
                .replace(R.id.home_part_tejia_layout,
                        PartTejiaFragment.getTejiaFragment())
                .replace(R.id.home_part_score_layout,
                        PartScoreFragment.getScoreFragment())
                .replace(R.id.home_part_local_layout,
                        PartLocalBusinessFragment.getLocalBusinessFragment())
                .replace(R.id.home_part_local_sale_miaosha_layout,
                        PartLocalMiaoshaFragment.getLocalMiaoshaFragment())
                .replace(R.id.home_part_local_sale_tejia_layout,
                        PartLocalTejiaFragment.getLocalTejiaFragment())
                .replace(R.id.home_part_store_layout,
                        PartStoreFragment.getStoreFragment())
                .replace(R.id.home_part_ads_layout,
                        PartAdsFragment.getAdsFragment()).commit();
        getNewData();
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
                        getProduct();
                    }
                });
        productGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                showProductDetail(products.get(arg2).getId(),
                        QrCodeTool.SALE_TYPE_NONE);
            }
        });
        nongfuImageView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openWindow(NongfuFragment.class.getName(), "农副产品");
            }
        });
        bianmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openWindow(BianminFragment.class.getName(), "便民服务");
            }
        });
        suningButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openWindow(SuningProductFragment.class.getName(), "云商城");
            }
        });
        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openWindow(ProductSearchFragment.class.getName(), "搜索商品");
            }
        });
        qrcodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                new QrcodeDialog().show(getFragmentManager(), null);
            }
        });
        closeTimeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                timeLayout.setVisibility(View.GONE);
            }
        });
    }

    private void refresh() {
        pageNo = 0;
        refreshScrollView.setMode(Mode.BOTH);
        products.clear();
        productAdapter.notifyDataSetChanged();
        getNewData();
    }

    private void getNewData() {
        HomeTask.getSaleTheme(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                if (!TextUtils.isEmpty(message.getResult())) {
                    JSONObject object;
                    try {
                        object = new JSONObject(message.getResult());
                        if (object.optString("state").equalsIgnoreCase(
                                "SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                List<ModelSaleTheme> saleThemes = new ArrayList<ModelSaleTheme>();
                                for (int i = 0; i < array.length(); i++) {
                                    saleThemes.add(new ModelSaleTheme(array
                                            .optJSONObject(i)));
                                }
                                HomeData.getInstance()
                                        .setSaleThemes(saleThemes);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                PartThemeFragment.getThemeFragment().refresh();
            }
        });
        HomeTask.getMainClasses(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                HomeData.getInstance().getClasses().clear();
                if (!TextUtils.isEmpty(message.getResult())) {
                    JSONObject object;
                    try {
                        object = new JSONObject(message.getResult());
                        if (object.optString("state").equalsIgnoreCase(
                                "SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                List<ModelClass> classes = new ArrayList<ModelClass>();
                                for (int i = 0; i < array.length(); i++) {
                                    classes.add(new ModelClass(array
                                            .optJSONObject(i)));
                                }
                                HomeData.getInstance().setClasses(classes);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                PartClassFragment.getClassFragment().refresh();
            }
        });
        HomeTask.getMiaoshaProduct(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartMiaoshaFragment.getMiaoshaFragment().refresh(
                        message.getResult());
            }
        });
        HomeTask.getLoveFresh(getActivity(), 1, 8, new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartFreshFragment.getFreshFragment().refresh(
                        message.getResult());
            }
        });
        HomeTask.getSaleTimes(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                HomeData.getInstance().getSaleTimes().clear();
                if (!TextUtils.isEmpty(message.getResult())) {
                    JSONObject object;
                    try {
                        object = new JSONObject(message.getResult());
                        if (object.optString("state").equalsIgnoreCase(
                                "SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                List<ModelSaleTime> saleTimes = new ArrayList<ModelSaleTime>();
                                for (int i = 0; i < array.length(); i++) {
                                    saleTimes.add(new ModelSaleTime(array
                                            .optJSONObject(i)));
                                }
                                HomeData.getInstance().setSaleTimes(saleTimes);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                PartSaleTimeFragment.getSaleTimeFragment().refresh();
            }
        });
        HomeTask.getHomeBrand(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                HomeData.getInstance().getBrands().clear();
                if (!TextUtils.isEmpty(message.getResult())) {
                    JSONObject object;
                    try {
                        object = new JSONObject(message.getResult());
                        if (object.optString("state").equalsIgnoreCase(
                                "SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                List<ModelHomeBrand> homeBrands = new ArrayList<>();
                                for (int i = 0; i < array.length(); i++) {
                                    homeBrands.add(new ModelHomeBrand(array
                                            .optJSONObject(i)));
                                }
                                HomeData.getInstance().setBrands(homeBrands);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                PartBrandFragment.getBrandFragment().refresh();
            }
        });
        HomeTask.getSaleTejia(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartTejiaFragment.getTejiaFragment().refresh(
                        message.getResult());
            }
        });
        HomeTask.getScoreProduct(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartScoreFragment.getScoreFragment().refresh(
                        message.getResult());
            }
        });
        HomeTask.getLocalGoods(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartLocalBusinessFragment.getLocalBusinessFragment()
                        .refreshGoods(message.getResult());
            }
        });
        HomeTask.getLocalService(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartLocalBusinessFragment.getLocalBusinessFragment()
                        .refreshService(message.getResult());
            }
        });
        HomeTask.getLocalMiaosha(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartLocalMiaoshaFragment.getLocalMiaoshaFragment().refresh(
                        message.getResult());
            }
        });
        HomeTask.getLocalTejia(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartLocalTejiaFragment.getLocalTejiaFragment().refresh(
                        message.getResult());
            }
        });
        HomeTask.getLocalStore(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                HomeData.getInstance().getLocalStores().clear();
                if (!TextUtils.isEmpty(message.getResult())) {
                    JSONObject object;
                    try {
                        object = new JSONObject(message.getResult());
                        if (object.optString("state").equalsIgnoreCase(
                                "SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                List<ModelLocalStore> localStores = new ArrayList<ModelLocalStore>();
                                for (int i = 0; i < array.length(); i++) {
                                    localStores.add(new ModelLocalStore(array
                                            .optJSONObject(i)));
                                }
                                HomeData.getInstance().setLocalStores(
                                        localStores);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                PartStoreFragment.getStoreFragment().refresh();
            }
        });
        HomeTask.getAds(getActivity(), new OnNetworkListener() {

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                PartAdsFragment.getAdsFragment().refresh(message.getResult());
            }
        });
        getProduct();
    }

    private void getProduct() {
        pageNo++;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("pageNo", pageNo + ""));
        nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
        ProductTask.getProducts(getActivity(), nameValuePairs,
                new OnNetworkListener() {

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        refreshScrollView.onRefreshComplete();
                        if (!TextUtils.isEmpty(message.getResult())) {
                            JSONObject info;
                            try {
                                info = new JSONObject(message.getResult());
                                if (info.getString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
                                    JSONArray productArray = info
                                            .getJSONArray("dataList");
                                    if (productArray != null) {
                                        if (productArray.length() > 0) {
                                            if (productArray.length() < pageSize) {
                                                refreshScrollView
                                                        .setMode(Mode.PULL_FROM_START);
                                            }
                                            StringBuilder stringBuilder = new StringBuilder();
                                            for (int i = 0; i < productArray
                                                    .length(); i++) {
                                                ModelProduct product = new ModelProduct(
                                                        productArray
                                                                .getJSONObject(i));
                                                products.add(product);
                                                if (i > 0) {
                                                    stringBuilder.append(",");
                                                }
                                                stringBuilder.append(product
                                                        .getId());
                                            }
                                            productAdapter
                                                    .notifyDataSetChanged();
                                            getProductPrice(stringBuilder
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
                });
    }

    private void getProductPrice(String productIds) {
        ProductTask.getProductPrice(getActivity(), productIds,
                new OnNetworkListener() {

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        if (!TextUtils.isEmpty(message.getResult())) {
                            JSONObject object;
                            try {
                                object = new JSONObject(message.getResult());
                                if (object.getString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
                                    JSONArray priceArray = object
                                            .getJSONArray("dataList");
                                    if (priceArray.length() > 0) {
                                        for (int i = 0; i < priceArray.length(); i++) {
                                            ModelListPrice priceList = new ModelListPrice(
                                                    priceArray.getJSONObject(i));
                                            priceMap.put(
                                                    priceList.getProductId(),
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

    private void updateVersionInfo(String machineStatus) {
        NetworkContent networkContent = new NetworkContent(
                API.API_MACHINE_UPDATE_STATE);
        networkContent.addParameters("machine", Device.getDeviceCode());
        networkContent.addParameters("status", machineStatus);
        networkContent.addParameters("version", PackageTool.getVersionName() + "("
                + PackageTool.getVersionCode() + ")");
        MissionController.startNetworkMission(getActivity(), networkContent,
                new OnNetworkListener());
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
            ModelProduct product = products.get(position);
            imageLoader.displayImage(getSmallImageUrl(product.getImg()),
                    viewHolder.imageView);
            if (priceMap.containsKey(product.getId())) {
                viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
                        + decimalFormat.format(priceMap.get(product.getId())
                        .getPrice()));
            }
            viewHolder.nameTextView.setText(product.getProductName());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView priceTextView, nameTextView;
        }
    }


    private void getMachineArea() {
        ProductTask.getMachineArea(getActivity(), new OnNetworkListener() {

            @Override
            public void onStart() {
                super.onStart();
                showLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
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
                            JSONObject dataMap = object
                                    .optJSONObject("dataMap");
                            if (dataMap != null) {
                                ModelMachineArea machineArea = new ModelMachineArea(dataMap);
                                Device.setMachineArea(machineArea);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (isConnected()) {
                    if (TextUtils.isEmpty(Device.getMachineArea().getId())) {
                        getMachineArea();
                    }
                }
            }

        });
    }


}
