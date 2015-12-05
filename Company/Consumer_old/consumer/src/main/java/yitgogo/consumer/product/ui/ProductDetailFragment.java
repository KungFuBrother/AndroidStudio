package yitgogo.consumer.product.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.controller.shoppingcart.DataBaseHelper;
import com.smartown.controller.shoppingcart.ShoppingCartController;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.order.ui.PlatformProductBuyFragment;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.product.model.ModelSaleDetailMiaosha;
import yitgogo.consumer.product.model.ModelSaleDetailTejia;
import yitgogo.consumer.product.model.ModelSaleDetailTime;
import yitgogo.consumer.store.SelectAreaFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.Notify;

public class ProductDetailFragment extends BaseNotifyFragment {

    CirclePageIndicator pageIndicator;
    FrameLayout imageLayout;
    ViewPager imagePager;

    TextView nameTextView;

    TextView priceTextView;

    LinearLayout saleLayout;
    TextView saleTextView;

    LinearLayout attrLayout;
    TextView attrTextView;

    LinearLayout areaLayout;
    TextView areaTextView;
    TextView freightTextView;

    FrameLayout countDeleteLayout;
    FrameLayout countAddLayout;
    TextView countTextView;

    LinearLayout htmlLayout;

    TextView totalMoneyTextView;
    Button buyButton;
    Button carButton;

    ImageAdapter imageAdapter;
    String productId = "";
    ModelProduct productDetail;
    RelationAdapter relationAdapter;

    int saleType = CaptureActivity.SALE_TYPE_NONE;
    ModelSaleDetailTime saleDetailTime = new ModelSaleDetailTime();
    ModelSaleDetailMiaosha saleDetailMiaosha = new ModelSaleDetailMiaosha();
    ModelSaleDetailTejia saleDetailTejia = new ModelSaleDetailTejia();

    int buyCount = 1;

    boolean isSaleEnable = false;
    HashMap<String, Double> freightMap;

    String areaName = "", areaId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_product_detail_new);
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
        initArea();
        new GetProductDetail().execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 22) {
            if (resultCode == 23) {
                Content.saveStringContent("product_detail_area_name", data.getStringExtra("name"));
                Content.saveStringContent("product_detail_area_id", data.getStringExtra("id"));
                initArea();
            }
        }
    }

    private void init() {
        measureScreen();
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

    private void initArea() {
        areaName = Content.getStringContent("product_detail_area_name", "四川省>成都市>锦江区");
        areaId = Content.getStringContent("product_detail_area_id", "2419");
        areaTextView.setText(areaName);
        if (!TextUtils.isEmpty(productDetail.getNumber())) {
            new GetFreight().execute();
        }
    }

    protected void findViews() {
        pageIndicator = (CirclePageIndicator) contentView.findViewById(R.id.platform_product_detail_image_indicator);
        imageLayout = (FrameLayout) contentView.findViewById(R.id.platform_product_detail_image_layout);
        imagePager = (ViewPager) contentView.findViewById(R.id.platform_product_detail_image_pager);

        nameTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_name);

        priceTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_price);

        saleLayout = (LinearLayout) contentView.findViewById(R.id.platform_product_detail_sale_layout);
        saleTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_sale_info);

        attrLayout = (LinearLayout) contentView.findViewById(R.id.platform_product_detail_attr_layout);
        attrTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_attr);

        areaLayout = (LinearLayout) contentView.findViewById(R.id.platform_product_detail_area_layout);
        areaTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_area);
        freightTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_freight);

        countDeleteLayout = (FrameLayout) contentView.findViewById(R.id.platform_product_detail_count_delete);
        countAddLayout = (FrameLayout) contentView.findViewById(R.id.platform_product_detail_count_add);
        countTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_count);
        freightTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_freight);

        htmlLayout = (LinearLayout) contentView.findViewById(R.id.platform_product_detail_html);

        totalMoneyTextView = (TextView) contentView.findViewById(R.id.platform_product_detail_total_money);
        buyButton = (Button) contentView.findViewById(R.id.platform_product_detail_buy);
        carButton = (Button) contentView.findViewById(R.id.platform_product_detail_add_car);


        addImageButton(R.drawable.iconfont_cart, "购物车", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ShoppingCarFragment.class.getName(), "易商城购物车");
            }
        });
        initViews();
        registerViews();
    }

    protected void initViews() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, screenWidth);
        imagePager.setLayoutParams(layoutParams);
        imagePager.setAdapter(imageAdapter);
        pageIndicator.setViewPager(imagePager);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void registerViews() {
        attrLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!productDetail.getProductRelations().isEmpty()) {
                    new RelationDialog().show(getFragmentManager(), null);
                }
            }
        });
        areaLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putInt("type", SelectAreaFragment.TYPE_GET_AREA);
                jumpForResult(SelectAreaFragment.class.getName(), "选择区域", bundle, 22);
            }
        });
        htmlLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("html", productDetail.getXiangqing());
                bundle.putInt("type", WebFragment.TYPE_HTML);
                jump(WebFragment.class.getName(), productDetail.getProductName(), bundle);
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
        countAddLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSaleEnable) {
                    if (saleType == CaptureActivity.SALE_TYPE_MIAOSHA) {
                        Notify.show("秒杀产品一次只能购买一件");
                        return;
                    }
                }
                if (buyCount < productDetail.getNum()) {
                    buyCount++;
                    new GetFreight().execute();
                } else {
                    Notify.show("库存不足");
                }
            }
        });
        countDeleteLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buyCount > 1) {
                    buyCount--;
                    new GetFreight().execute();
                }
            }
        });
    }

    /**
     * 显示商品详情
     */
    private void showDetail() {
        imageAdapter.notifyDataSetChanged();
        nameTextView.setText(productDetail.getProductName());
        priceTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(productDetail.getPrice()));
        attrTextView.setText(productDetail.getAttName());
        switch (saleType) {

            case CaptureActivity.SALE_TYPE_TIME:
                new GetTimeSaleDetail().execute();
                break;

            case CaptureActivity.SALE_TYPE_MIAOSHA:
                new GetMiaoshaSaleDetail().execute();
                break;

            case CaptureActivity.SALE_TYPE_TEJIA:
                new GetTejiaSaleDetail().execute();
                break;

            default:
                new GetFreight().execute();
                break;
        }
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

    class RelationDialog extends DialogFragment {

        View dialogView;
        ListView listView;
        TextView titleTextView, button;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            findViews();
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, screenWidth));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_list, null);
            titleTextView = (TextView) dialogView
                    .findViewById(R.id.dialog_title);
            button = (TextView) dialogView.findViewById(R.id.dialog_button);
            listView = (ListView) dialogView.findViewById(R.id.dialog_list);
            initViews();
        }

        private void initViews() {
            titleTextView.setText("选择商品属性");
            button.setText("取消");
            listView.setAdapter(relationAdapter);
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    if (!productDetail.getProductRelations().get(arg2).getId()
                            .equals(productDetail.getId())) {
                        productId = productDetail.getProductRelations().get(arg2).getId();
                        new GetProductDetail().execute();
                    }
                    dismiss();
                }
            });
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
                        R.layout.list_goods_relation, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.list_relation_check);
                viewHolder.textView = (TextView) convertView
                        .findViewById(R.id.list_relation_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (productDetail.getProductRelations().get(position).getId().equals(productDetail.getId())) {
                viewHolder.imageView.setImageResource(R.drawable.iconfont_check_checked);
            } else {
                viewHolder.imageView.setImageResource(R.drawable.iconfont_check_normal);
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

    /**
     * 获取商品详情
     *
     * @author Tiger
     */
    class GetProductDetail extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("jmdId", Store.getStore().getStoreId()));
            nameValuePairs.add(new BasicNameValuePair("productId", productId));
            String result = netUtil.postWithoutCookie(API.API_PRODUCT_DETAIL, nameValuePairs, false, false);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
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
        }
    }

    /**
     * 秒杀商品详情
     *
     * @author Tiger
     */
    class GetMiaoshaSaleDetail extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("productId", productDetail.getId()));
            return netUtil.postWithCookie(API.API_SALE_MIAOSHA_DETAIL, valuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            try {
                saleDetailMiaosha = new ModelSaleDetailMiaosha(result);
                if (saleDetailMiaosha != null) {
                    if (saleDetailMiaosha.getSeckillPrice() > 0) {
                        // 开始时间<=当前时间，活动已开始
                        if (saleDetailMiaosha.getStartTime() <= Calendar
                                .getInstance().getTime().getTime()) {
                            // 剩余秒杀数量>0，显示秒杀信息
                            if (saleDetailMiaosha.getSeckillNUmber() > 0) {
                                isSaleEnable = true;
                                saleLayout.setVisibility(View.VISIBLE);
                                StringBuilder saleInfo = new StringBuilder();
                                saleInfo.append(saleDetailMiaosha.getSeckillName());
                                saleInfo.append("\n");
                                priceTextView.setText("¥" + decimalFormat.format(saleDetailMiaosha.getSeckillPrice()));
                                saleInfo.append("秒杀已开始，每个账号限购" + saleDetailMiaosha.getMemberNumber() + "件。");
                                saleInfo.append("\n");
                                saleInfo.append("剩余" + saleDetailMiaosha.getSeckillNUmber() + "件");
                                saleInfo.append("\n");
                                saleInfo.append("原件:" + "¥" + decimalFormat.format(saleDetailMiaosha.getPrice()));
                                carButton.setVisibility(View.GONE);
                                saleTextView.setText(saleInfo.toString());
                            }
                        } else {
                            // 开始时间>当前时间，活动未开始，显示预告
                            saleLayout.setVisibility(View.VISIBLE);
                            StringBuilder saleInfo = new StringBuilder();
                            saleInfo.append(saleDetailMiaosha.getSeckillName());
                            saleInfo.append("开始时间:\n" + simpleDateFormat.format(new Date(saleDetailMiaosha.getStartTime()))
                                    + "\n原价：" + Parameters.CONSTANT_RMB + decimalFormat.format(saleDetailMiaosha.getPrice()) + ","
                                    + "秒杀价：" + Parameters.CONSTANT_RMB + decimalFormat.format(saleDetailMiaosha.getSeckillPrice()));
                            saleTextView.setText(saleInfo.toString());
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new GetFreight().execute();
        }
    }

    /**
     * 限时促销商品详情
     *
     * @author Tiger
     */
    class GetTimeSaleDetail extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("productId", productDetail
                    .getId()));
            return netUtil.postWithCookie(API.API_SALE_TIME_DETAIL, valuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            try {
                saleDetailTime = new ModelSaleDetailTime(result);
                if (saleDetailTime != null) {
                    if (saleDetailTime.getPromotionPrice() > 0) {
                        // 开始时间>当前时间，未开始，显示活动预告
                        if (saleDetailTime.getStartTime() > Calendar.getInstance().getTime().getTime()) {
                            StringBuilder saleInfo = new StringBuilder();
                            saleLayout.setVisibility(View.VISIBLE);
                            saleInfo.append(saleDetailTime.getPromotionName());
                            saleInfo.append("活动时间:\n"
                                    + simpleDateFormat.format(new Date(saleDetailTime.getStartTime()))
                                    + " 至\n" + simpleDateFormat.format(new Date(saleDetailTime.getEndTime())));
                            saleTextView.setText(saleInfo.toString());
                        } else if (saleDetailTime.getEndTime() > Calendar.getInstance().getTime().getTime()) {
                            // 开始时间<=当前时间，结束时间>当前时间，已开始未结束，活动进行时
                            isSaleEnable = true;
                            priceTextView.setText("¥" + decimalFormat.format(saleDetailTime.getPromotionPrice()));
                            StringBuilder saleInfo = new StringBuilder();
                            saleLayout.setVisibility(View.VISIBLE);
                            saleInfo.append(saleDetailTime.getPromotionName());
                            saleInfo.append("活动时间:\n"
                                    + simpleDateFormat.format(new Date(saleDetailTime.getStartTime()))
                                    + " 至\n" + simpleDateFormat.format(new Date(saleDetailTime.getEndTime())));
                            saleInfo.append("原价:¥" + decimalFormat.format(saleDetailTime.getPrice()));
                            carButton.setVisibility(View.GONE);
                        } else {
                            // 活动结束
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new GetFreight().execute();
        }
    }

    /**
     * 特价促销商品详情
     *
     * @author Tiger
     */
    class GetTejiaSaleDetail extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("productId", productDetail.getId()));
            return netUtil
                    .postWithCookie(API.API_SALE_TEJIA_DETAIL, valuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            try {
                saleDetailTejia = new ModelSaleDetailTejia(result);
                if (saleDetailTejia != null) {
                    if (saleDetailTejia.getSalePrice() > 0) {
                        if (saleDetailTejia.getNumbers() > 0) {
                            StringBuilder saleInfo = new StringBuilder();
                            isSaleEnable = true;
                            saleLayout.setVisibility(View.VISIBLE);
                            priceTextView.setText("¥" + decimalFormat.format(saleDetailTejia.getSalePrice()));
                            saleInfo.append(saleDetailTejia.getType());
                            saleInfo.append(saleDetailTejia.getSalePromotionName());
                            saleInfo.append("原价:¥" + decimalFormat.format(saleDetailTejia.getPrice()));
                            carButton.setText("立即抢购");
                            carButton.setVisibility(View.GONE);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new GetFreight().execute();
        }
    }

    class GetFreight extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            showLoading();
            freightMap.clear();
            countTextView.setText(String.valueOf(buyCount));
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new BasicNameValuePair("productNumber", productDetail.getNumber() + "-" + buyCount));
            valuePairs.add(new BasicNameValuePair("areaid", areaId));
            valuePairs.add(new BasicNameValuePair("spid", Store.getStore().getStoreId()));
            return netUtil.postWithCookie(API.API_PRODUCT_FREIGHT, valuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
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
                                countTotalMoney();
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
    }

    private void countTotalMoney() {
        double price = productDetail.getPrice();
        if (isSaleEnable) {
            switch (saleType) {

                case CaptureActivity.SALE_TYPE_TIME:
                    price = saleDetailTime.getPromotionPrice();
                    break;

                case CaptureActivity.SALE_TYPE_MIAOSHA:
                    price = saleDetailMiaosha.getSeckillPrice();
                    break;

                case CaptureActivity.SALE_TYPE_TEJIA:
                    price = saleDetailTejia.getSalePrice();
                    break;

                default:
                    price = productDetail.getPrice();
                    break;
            }
        }
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(buyCount * price + freightMap.get(productDetail.getSupplierId())));
    }

    private void buyProduct() {
        if (User.getUser().isLogin()) {
            if (productDetail.getNum() > 0) {
                int isIntegralMall = 0;
                double price = productDetail.getPrice();
                if (isSaleEnable) {
                    switch (saleType) {

                        case CaptureActivity.SALE_TYPE_TIME:
                            price = saleDetailTime.getPromotionPrice();
                            break;

                        case CaptureActivity.SALE_TYPE_MIAOSHA:
                            isIntegralMall = 2;
                            price = saleDetailMiaosha.getSeckillPrice();
                            break;

                        case CaptureActivity.SALE_TYPE_TEJIA:
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
                        bundle.putString("productNumber", productDetail.getNumber());
                        bundle.putString("name", productDetail.getProductName());
                        bundle.putString("productAttr", productDetail.getAttName());
                        bundle.putString("image", productDetail.getImg());
                        bundle.putInt("isIntegralMall", isIntegralMall);
                        bundle.putInt("buyCount", buyCount);
                        bundle.putDouble("price", price);
                        jump(PlatformProductBuyFragment.class.getName(), "确认订单", bundle);
                    } else {
                        Notify.show("查询运费失败，暂不能购买");
                    }
                } else {
                    Notify.show("查询价格失败，暂不能购买");
                }
            } else {
                Notify.show("此商品无货，暂不能购买");
            }
        } else {
            Notify.show("请先登录");
            jump(UserLoginFragment.class.getName(), "会员登录");
        }
    }

}
