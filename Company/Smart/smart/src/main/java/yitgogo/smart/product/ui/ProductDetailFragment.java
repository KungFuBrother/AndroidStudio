package yitgogo.smart.product.ui;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import smartown.controller.shoppingcart.DataBaseHelper;
import smartown.controller.shoppingcart.ShoppingCartController;
import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.model.ModelMachineArea;
import yitgogo.smart.order.ui.ProductPlatformBuyFragment;
import yitgogo.smart.product.model.ModelProduct;
import yitgogo.smart.sale.model.ModelSaleDetailMiaosha;
import yitgogo.smart.sale.model.ModelSaleDetailTejia;
import yitgogo.smart.sale.model.ModelSaleDetailTime;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.OnQrEncodeListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.tools.QrCodeTool;
import yitgogo.smart.tools.QrEncodeMissonMessage;
import yitgogo.smart.tools.ScreenUtil;
import yitgogo.smart.view.InnerListView;
import yitgogo.smart.view.Notify;

public class ProductDetailFragment extends BaseNotifyFragment {

    ViewPager imagePager;
    LinearLayout activityLayout;
    TextView nameTextView, brandTextView, priceTextView, originalPriceTextView, stateTextView, attrTextView;
    ImageView lastImageButton, nextImageButton;
    TextView imageIndexText;

    TextView buyButton, carButton;
    ImageView countDeleteButton, countAddButton;
    TextView countTextView, freightTextView;

    TextView activityNameTextView, activityDetailTextView;
    ImageView qrCodeImageView;

    InnerListView relationList;

    ImageAdapter imageAdapter;
    String productId = "";
    ModelProduct productDetail;
    RelationAdapter relationAdapter;

    WebView webView;
    ProgressBar progressBar;

    int buyCount = 1;

    boolean isSaleEnable = false;

    int saleType = 0;
    ModelSaleDetailTime saleDetailTime = new ModelSaleDetailTime();
    ModelSaleDetailMiaosha saleDetailMiaosha = new ModelSaleDetailMiaosha();
    ModelSaleDetailTejia saleDetailTejia = new ModelSaleDetailTejia();

    ModelMachineArea machineArea = new ModelMachineArea();

    HashMap<String, Double> freightMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_product_detail);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ProductDetailFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ProductDetailFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProductDetail();
        getArea();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("productId")) {
                productId = bundle.getString("productId");
            }
            if (bundle.containsKey("saleType")) {
                saleType = bundle.getInt("saleType");
            }
        }
        productDetail = new ModelProduct();
        freightMap = new HashMap<>();
        imageAdapter = new ImageAdapter();
        relationAdapter = new RelationAdapter();
    }

    protected void findViews() {
        imagePager = (ViewPager) contentView.findViewById(R.id.product_detail_images);
        activityLayout = (LinearLayout) contentView.findViewById(R.id.product_detail_activity);
        nameTextView = (TextView) contentView.findViewById(R.id.product_detail_name);
        brandTextView = (TextView) contentView.findViewById(R.id.product_detail_brand);
        priceTextView = (TextView) contentView.findViewById(R.id.product_detail_price);
        originalPriceTextView = (TextView) contentView.findViewById(R.id.product_detail_price_original);
        stateTextView = (TextView) contentView.findViewById(R.id.product_detail_state);
        lastImageButton = (ImageView) contentView.findViewById(R.id.product_detail_image_last);
        nextImageButton = (ImageView) contentView.findViewById(R.id.product_detail_image_next);
        imageIndexText = (TextView) contentView.findViewById(R.id.product_detail_image_index);

        buyButton = (TextView) contentView.findViewById(R.id.product_detail_buy);
        carButton = (TextView) contentView.findViewById(R.id.product_detail_car);

        countDeleteButton = (ImageView) contentView.findViewById(R.id.product_buy_count_delete);
        countAddButton = (ImageView) contentView.findViewById(R.id.product_buy_count_add);
        countTextView = (TextView) contentView.findViewById(R.id.product_buy_count);

        freightTextView = (TextView) contentView.findViewById(R.id.product_buy_freight);

        activityNameTextView = (TextView) contentView.findViewById(R.id.product_detail_activity_name);
        activityDetailTextView = (TextView) contentView.findViewById(R.id.product_detail_activity_detail);
        attrTextView = (TextView) contentView.findViewById(R.id.product_detail_attr);
        relationList = (InnerListView) contentView.findViewById(R.id.product_detail_relation_list);
        qrCodeImageView = (ImageView) contentView.findViewById(R.id.product_detail_qrcode);

        webView = (WebView) contentView.findViewById(R.id.web_webview);
        progressBar = (ProgressBar) contentView.findViewById(R.id.web_progress);

        initViews();
        registerViews();
    }

    protected void initViews() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenUtil.getScreenWidth() / 3);
        imagePager.setLayoutParams(layoutParams);
        imagePager.setAdapter(imageAdapter);
        relationList.setAdapter(relationAdapter);
        originalPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    if (progressBar.getVisibility() == View.GONE)
                        progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        settings.setLoadWithOverviewMode(true);

        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getActivity().getCacheDir().getPath());
        settings.setAppCacheEnabled(true);

    }

    @Override
    protected void registerViews() {
        lastImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageAdapter.getCount() > 0) {
                    if (imagePager.getCurrentItem() == 0) {
                        setImagePosition(imageAdapter.getCount() - 1);
                    } else {
                        setImagePosition(imagePager.getCurrentItem() - 1);
                    }
                }
            }
        });
        nextImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageAdapter.getCount() > 0) {
                    if (imagePager.getCurrentItem() == imageAdapter.getCount() - 1) {
                        setImagePosition(0);
                    } else {
                        setImagePosition(imagePager.getCurrentItem() + 1);
                    }
                }
            }
        });
        buyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                buyProduct();
            }
        });
        carButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCar();
            }
        });
        countAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                buyCount++;
                getFreight();
            }
        });
        countDeleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buyCount > 1) {
                    buyCount--;
                    getFreight();
                }
            }
        });
        relationList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (!productDetail.getProductRelations().get(arg2).getId().equals(productDetail.getId())) {
                    productId = productDetail.getProductRelations().get(arg2).getId();
                    getProductDetail();
                }
            }
        });
        imagePager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                imageIndexText.setText((imagePager.getCurrentItem() + 1) + "/"
                        + imageAdapter.getCount());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     * 显示商品详情
     */
    private void showDetail() {
        addBrowsHistory();
        qrEncodeProduct(productDetail.getId(), productDetail.getNumber(),
                productDetail.getProductName(),
                QrCodeTool.PRODUCT_TYPE_PLATFORM, saleType,
                new OnQrEncodeListener() {
                    @Override
                    public void onSuccess(QrEncodeMissonMessage message) {
                        super.onSuccess(message);
                        if (message.getBitmap() != null) {
                            qrCodeImageView.setImageBitmap(message.getBitmap());
                        }
                    }
                });
        getFreight();
        relationAdapter.notifyDataSetChanged();
        imageAdapter.notifyDataSetChanged();
        nameTextView.setText(productDetail.getProductName());
        priceTextView.setText("¥" + decimalFormat.format(productDetail.getPrice()));
        brandTextView.setText(productDetail.getBrandName());
        attrTextView.setText(productDetail.getAttName());
        if (productDetail.getNum() > 0) {
            if (productDetail.getNum() < 5) {
                stateTextView.setText("仅剩" + productDetail.getNum() + productDetail.getUnit());
            } else {
                stateTextView.setText("有货");
            }
        } else {
            stateTextView.setText("无货");
        }
        if (imageAdapter.getCount() > 0) {
            imageIndexText.setText(1 + "/" + imageAdapter.getCount());
        }
        webView.loadData(productDetail.getXiangqing(), "text/html; charset=utf-8", "utf-8");
        switch (saleType) {

            case QrCodeTool.SALE_TYPE_TIME:
                getTimeSaleDetail();
                break;

            case QrCodeTool.SALE_TYPE_MIAOSHA:
                getMiaoshaSaleDetail();
                break;

            case QrCodeTool.SALE_TYPE_TEJIA:
                getTejiaSaleDetail();
                break;

            default:
                break;
        }
    }

    /**
     * 点击左右导航按钮切换图片
     *
     * @param imagePosition
     */
    private void setImagePosition(int imagePosition) {
        imagePager.setCurrentItem(imagePosition, true);
        imageIndexText.setText((imagePosition + 1) + "/" + imageAdapter.getCount());
    }

    /**
     * 添加到购物车
     */
    private void addToCar() {
        if (productDetail.getNum() > 0) {
            if (ShoppingCartController.getInstance().hasProduct(DataBaseHelper.tableCarPlatform, productDetail.getId())) {
                Notify.show("购物车已存在此商品");
            } else {
                ShoppingCartController.getInstance().addProduct(DataBaseHelper.tableCarPlatform, true, buyCount, productDetail.getSupplierId(), productDetail.getSupplierName(), productDetail.getId(), productDetail.getJsonObject().toString());
                Notify.show("添加到购物车成功");
            }
        } else {
            Notify.show("此商品无货，无法添加到购物车");
        }
    }

    /**
     * viewpager适配器
     */
    private class ImageAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return productDetail.getImages().size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = layoutInflater.inflate(
                    R.layout.adapter_viewpager, view, false);
            assert imageLayout != null;
            ImageView imageView = (ImageView) imageLayout
                    .findViewById(R.id.view_pager_img);
            final ProgressBar spinner = (ProgressBar) imageLayout
                    .findViewById(R.id.view_pager_loading);
            ImageLoader.getInstance().displayImage(
                    getBigImageUrl(productDetail.getImages().get(position)),
                    imageView, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
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

    class RelationAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return productDetail.getProductRelations().size();
        }

        @Override
        public Object getItem(int position) {
            return productDetail.getProductRelations().get(position);
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
                        R.layout.list_diliver_payment, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.diliver_payment_check);
                viewHolder.textView = (TextView) convertView
                        .findViewById(R.id.diliver_payment_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (productDetail.getProductRelations().get(position).getId()
                    .equals(productDetail.getId())) {
                viewHolder.imageView
                        .setImageResource(R.drawable.iconfont_check_checked);
            } else {
                viewHolder.imageView
                        .setImageResource(R.drawable.iconfont_check_normal);
            }
            viewHolder.textView.setText(productDetail.getProductRelations()
                    .get(position).getAttName());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }

    }

    private void addBrowsHistory() {
        NetworkContent networkContent = new NetworkContent(API.API_PRODUCT_BROWSE_HISTORY);
        networkContent.addParameters("productType", "0");
        networkContent.addParameters("productNumber", productDetail.getNumber());
        networkContent.addParameters("equipNo", Device.getDeviceCode());
        MissionController.startNetworkMission(getActivity(), networkContent, new OnNetworkListener());
    }

    /**
     * 获取商品详情
     *
     * @author Tiger
     */
    private void getProductDetail() {
        ProductTask.getProductDetail(getActivity(), productId,
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
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        if (!TextUtils.isEmpty(message.getResult())) {
                            try {
                                JSONObject object = new JSONObject(message.getResult());
                                if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                                    JSONObject detailObject = object.optJSONObject("dataMap");
                                    if (detailObject != null) {
                                        productDetail = new ModelProduct(detailObject);
                                        showDetail();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    /**
     * 秒杀商品详情
     *
     * @author Tiger
     */
    private void getMiaoshaSaleDetail() {
        ProductTask.getProductSaleMiaoshaDetail(getActivity(), productId,
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
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        try {
                            saleDetailMiaosha = new ModelSaleDetailMiaosha(
                                    message.getResult());
                            if (saleDetailMiaosha != null) {
                                if (saleDetailMiaosha.getSeckillPrice() > 0) {
                                    activityLayout.setVisibility(View.VISIBLE);
                                    activityNameTextView
                                            .setText(saleDetailMiaosha
                                                    .getSeckillName());
                                    // 开始时间<=当前时间，活动已开始
                                    if (saleDetailMiaosha.getStartTime() <= Calendar
                                            .getInstance().getTime().getTime()) {
                                        // 剩余秒杀数量>0，显示秒杀信息
                                        if (saleDetailMiaosha
                                                .getSeckillNUmber() > 0) {
                                            isSaleEnable = true;
                                            activityDetailTextView.setText("秒杀已开始，每个账号限购"
                                                    + saleDetailMiaosha
                                                    .getMemberNumber()
                                                    + "件。");
                                            priceTextView.setText("¥"
                                                    + decimalFormat
                                                    .format(saleDetailMiaosha
                                                            .getSeckillPrice()));
                                            originalPriceTextView.setText("¥"
                                                    + decimalFormat
                                                    .format(saleDetailMiaosha
                                                            .getPrice()));
                                            stateTextView.setText("剩余"
                                                    + saleDetailMiaosha
                                                    .getSeckillNUmber()
                                                    + "件");
                                            carButton.setVisibility(View.GONE);
                                        }
                                    } else {
                                        // 开始时间>当前时间，活动未开始，显示预告
                                        activityDetailTextView.setText("开始时间:\n"
                                                + simpleDateFormat.format(new Date(
                                                saleDetailMiaosha
                                                        .getStartTime()))
                                                + "\n原价："
                                                + Parameters.CONSTANT_RMB
                                                + decimalFormat
                                                .format(saleDetailMiaosha
                                                        .getPrice())
                                                + ","
                                                + "秒杀价："
                                                + Parameters.CONSTANT_RMB
                                                + decimalFormat
                                                .format(saleDetailMiaosha
                                                        .getSeckillPrice()));
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 限时促销商品详情
     *
     * @author Tiger
     */
    private void getTimeSaleDetail() {
        ProductTask.getProductSaleTimeDetail(getActivity(), productId,
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
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        try {
                            saleDetailTime = new ModelSaleDetailTime(message
                                    .getResult());
                            if (saleDetailTime != null) {
                                if (saleDetailTime.getPromotionPrice() > 0) {
                                    // 开始时间>当前时间，未开始，显示活动预告
                                    if (saleDetailTime.getStartTime() > Calendar.getInstance().getTime().getTime()) {
                                        activityLayout.setVisibility(View.VISIBLE);
                                        activityNameTextView.setText(saleDetailTime.getPromotionName());
                                        activityDetailTextView.setText("活动时间:\n" + simpleDateFormat.format(new Date(saleDetailTime
                                                .getStartTime()))
                                                + " 至\n"
                                                + simpleDateFormat.format(new Date(
                                                saleDetailTime
                                                        .getEndTime())));
                                    } else if (saleDetailTime.getEndTime() > Calendar.getInstance().getTime().getTime()) {
                                        isSaleEnable = true;
                                        // 开始时间<=当前时间，结束时间>当前时间，已开始未结束，活动进行时
                                        activityLayout.setVisibility(View.VISIBLE);
                                        activityNameTextView.setText(saleDetailTime.getPromotionName());
                                        activityDetailTextView.setText("活动时间:\n" + simpleDateFormat.format(new Date(saleDetailTime
                                                .getStartTime()))
                                                + " 至\n"
                                                + simpleDateFormat.format(new Date(
                                                saleDetailTime
                                                        .getEndTime())));
                                        priceTextView.setText("¥"
                                                + decimalFormat.format(saleDetailTime
                                                .getPromotionPrice()));
                                        originalPriceTextView.setText("¥"
                                                + decimalFormat
                                                .format(saleDetailTime
                                                        .getPrice()));
                                        carButton.setVisibility(View.GONE);
                                    } else {
                                        // 活动结束
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 特价促销商品详情
     *
     * @author Tiger
     */
    private void getTejiaSaleDetail() {
        ProductTask.getProductSaleTejiaDetail(getActivity(), productId,
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
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        try {
                            saleDetailTejia = new ModelSaleDetailTejia(message
                                    .getResult());
                            if (saleDetailTejia != null) {
                                if (saleDetailTejia.getSalePrice() > 0) {
                                    if (saleDetailTejia.getNumbers() > 0) {
                                        isSaleEnable = true;
                                        activityLayout
                                                .setVisibility(View.VISIBLE);
                                        activityNameTextView
                                                .setText(saleDetailTejia
                                                        .getType());
                                        activityDetailTextView.setText(saleDetailTejia
                                                .getSalePromotionName());
                                        priceTextView.setText("¥"
                                                + decimalFormat.format(saleDetailTejia
                                                .getSalePrice()));
                                        originalPriceTextView.setText("¥"
                                                + decimalFormat
                                                .format(saleDetailTejia
                                                        .getPrice()));
                                        carButton.setVisibility(View.GONE);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void getArea() {
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
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
                            if (dataMap != null) {
                                machineArea = new ModelMachineArea(dataMap);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    getArea();
                }
            }

        });
    }

    private void getFreight() {
        countTextView.setText(String.valueOf(buyCount));
        ProductTask.getFreight(getActivity(), productDetail.getNumber() + "-" + buyCount, machineArea.getId(),
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
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        //{"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{"3046":0.0}],"totalCount":1,"dataMap":{},"object":null}
                        if (!TextUtils.isEmpty(message.getResult())) {
                            try {
                                JSONObject object = new JSONObject(message.getResult());
                                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                                    JSONArray jsonArray = object.optJSONArray("dataList");
                                    if (jsonArray != null) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.optJSONObject(i);
                                            if (jsonObject != null) {
                                                Iterator<String> keys = jsonObject.keys();
                                                while (keys.hasNext()) {
                                                    String key = keys.next();
                                                    freightMap.put(key, jsonObject.optDouble(key));
                                                }
                                            }
                                        }
                                        if (freightMap.containsKey(productDetail.getSupplierId())) {
                                            freightTextView.setText("运费:" + Parameters.CONSTANT_RMB + decimalFormat.format(freightMap.get(productDetail.getSupplierId())));
                                        }
                                    }
                                    return;
                                }
                                Notify.show(object.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void buyProduct() {
        if (productDetail.getNum() > 0) {
            int isIntegralMall = 0;
            double price = productDetail.getPrice();
            if (isSaleEnable) {
                switch (saleType) {

                    case QrCodeTool.SALE_TYPE_TIME:
                        price = saleDetailTime.getPromotionPrice();
                        break;

                    case QrCodeTool.SALE_TYPE_MIAOSHA:
                        isIntegralMall = 2;
                        price = saleDetailMiaosha.getSeckillPrice();
                        break;

                    case QrCodeTool.SALE_TYPE_TEJIA:
                        price = saleDetailTejia.getSalePrice();
                        break;

                    default:
                        price = productDetail.getPrice();
                        break;

                }
            }
            if (price > 0) {
                if (freightMap.containsKey(productDetail.getSupplierId())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("supplierId", productDetail.getSupplierId());
                    bundle.putString("supplierName", productDetail.getSupplierName());
                    bundle.putString("productId", productDetail.getId());
                    bundle.putString("name", productDetail.getProductName());
                    bundle.putString("image", productDetail.getImg());
                    bundle.putInt("isIntegralMall", isIntegralMall);
                    bundle.putInt("buyCount", buyCount);
                    bundle.putDouble("price", price);
                    bundle.putDouble("freight", freightMap.get(productDetail.getSupplierId()));
                    openWindow(ProductPlatformBuyFragment.class.getName(), "确认订单", bundle);
                } else {
                    Notify.show("查询运费失败，暂不能购买");
                }
            } else {
                Notify.show("查询价格失败，暂不能购买");
            }
        } else {
            Notify.show("此商品无货，暂不能购买");
        }
    }

}
