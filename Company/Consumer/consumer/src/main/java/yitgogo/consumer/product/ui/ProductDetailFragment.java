package yitgogo.consumer.product.ui;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dtr.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.order.ui.SaleProductOrderConfirmFragment;
import yitgogo.consumer.product.model.ModelCar;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.product.model.ModelSaleDetailMiaosha;
import yitgogo.consumer.product.model.ModelSaleDetailTejia;
import yitgogo.consumer.product.model.ModelSaleDetailTime;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.InnerGridView;

public class ProductDetailFragment extends BaseNetworkFragment implements
        OnClickListener {

    ViewPager imagePager;
    LinearLayout htmlButton, attrButton, activityLayout;
    TextView nameTextView, brandTextView, attrTextView, priceTextView,
            originalPriceTextView, stateTextView;
    ImageView lastImageButton, nextImageButton;
    TextView imageIndexText;
    TextView carButton, buyButton;
    TextView activityNameTextView, activityDetailTextView;

    LinearLayout relationLayout;
    TextView hideRelationButton, noRelationTextView;
    InnerGridView relationList;

    ImageAdapter imageAdapter;
    String productId = "";
    ModelProduct productDetail;
    RelationAdapter relationAdapter;

    int saleType = CaptureActivity.SALE_TYPE_NONE;
    ModelSaleDetailTime saleDetailTime = new ModelSaleDetailTime();
    ModelSaleDetailMiaosha saleDetailMiaosha = new ModelSaleDetailMiaosha();
    ModelSaleDetailTejia saleDetailTejia = new ModelSaleDetailTejia();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_product_detail);
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
    }

    @Override
    protected void init() {
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
        imageAdapter = new ImageAdapter();
        relationAdapter = new RelationAdapter();
    }

    protected void findViews() {
        imagePager = (ViewPager) contentView
                .findViewById(R.id.product_detail_images);
        htmlButton = (LinearLayout) contentView
                .findViewById(R.id.product_detail_html);
        attrButton = (LinearLayout) contentView
                .findViewById(R.id.product_detail_attr);
        activityLayout = (LinearLayout) contentView
                .findViewById(R.id.product_detail_activity);
        nameTextView = (TextView) contentView
                .findViewById(R.id.product_detail_name);
        brandTextView = (TextView) contentView
                .findViewById(R.id.product_detail_brand);
        attrTextView = (TextView) contentView
                .findViewById(R.id.product_detail_attr_name);
        priceTextView = (TextView) contentView
                .findViewById(R.id.product_detail_price);
        originalPriceTextView = (TextView) contentView
                .findViewById(R.id.product_detail_price_original);
        stateTextView = (TextView) contentView
                .findViewById(R.id.product_detail_state);
        lastImageButton = (ImageView) contentView
                .findViewById(R.id.product_detail_image_last);
        nextImageButton = (ImageView) contentView
                .findViewById(R.id.product_detail_image_next);
        imageIndexText = (TextView) contentView
                .findViewById(R.id.product_detail_image_index);
        carButton = (TextView) contentView
                .findViewById(R.id.product_detail_car);
        buyButton = (TextView) contentView
                .findViewById(R.id.product_detail_buy);
        activityNameTextView = (TextView) contentView
                .findViewById(R.id.product_detail_activity_name);
        activityDetailTextView = (TextView) contentView
                .findViewById(R.id.product_detail_activity_detail);

        relationLayout = (LinearLayout) contentView
                .findViewById(R.id.product_detail_relation_layout);
        hideRelationButton = (TextView) contentView
                .findViewById(R.id.product_detail_relation_hide);
        relationList = (InnerGridView) contentView
                .findViewById(R.id.product_detail_relation_list);
        noRelationTextView = (TextView) contentView
                .findViewById(R.id.product_detail_relation_none);

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
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth());
        imagePager.setLayoutParams(layoutParams);
        imagePager.setAdapter(imageAdapter);
        relationList.setAdapter(relationAdapter);
        originalPriceTextView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void registerViews() {
        htmlButton.setOnClickListener(this);
        attrButton.setOnClickListener(this);
        lastImageButton.setOnClickListener(this);
        nextImageButton.setOnClickListener(this);
        carButton.setOnClickListener(this);
        buyButton.setOnClickListener(this);
        relationLayout.setOnClickListener(this);
        hideRelationButton.setOnClickListener(this);
        relationList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (!productDetail.getProductRelations().get(arg2).getId()
                        .equals(productDetail.getId())) {
                    productId = productDetail.getProductRelations().get(arg2)
                            .getId();
                    relationLayout.setVisibility(View.GONE);
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
        carButton.setText("加入购物车");
        carButton.setOnClickListener(this);
        relationAdapter.notifyDataSetChanged();
        if (productDetail.getProductRelations().size() > 0) {
            attrButton.setClickable(true);
        } else {
            attrButton.setClickable(false);
        }
        imageAdapter.notifyDataSetChanged();
        nameTextView.setText(productDetail.getProductName());
        priceTextView.setText("¥"
                + decimalFormat.format(productDetail.getPrice()));
        brandTextView.setText(productDetail.getBrandName());
        attrTextView.setText(productDetail.getAttName());
        if (productDetail.getNum() > 0) {
            if (productDetail.getNum() < 5) {
                stateTextView.setText("仅剩" + productDetail.getNum()
                        + productDetail.getUnit());
            } else {
                stateTextView.setText("有货");
            }
        } else {
            stateTextView.setText("无货");
        }
        if (imageAdapter.getCount() > 0) {
            imageIndexText.setText(1 + "/" + imageAdapter.getCount());
        }
        switch (saleType) {

            case CaptureActivity.SALE_TYPE_TIME:
                getTimeSaleDetail();
                break;

            case CaptureActivity.SALE_TYPE_MIAOSHA:
                getMiaoshaSaleDetail();
                break;

            case CaptureActivity.SALE_TYPE_TEJIA:
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
        imageIndexText.setText((imagePosition + 1) + "/"
                + imageAdapter.getCount());
    }

    /**
     * 添加到购物车
     */
    private void addToCar() {
        if (productDetail.getNum() > 0) {
            try {
                JSONArray carArray = new JSONArray(Content.getStringContent(
                        Parameters.CACHE_KEY_CAR, "[]"));
                for (int i = 0; i < carArray.length(); i++) {
                    ModelCar modelCar = new ModelCar(carArray.getJSONObject(i));
                    if (modelCar.getProduct().getId()
                            .equals(productDetail.getId())) {
                        ApplicationTool.showToast("已添加过此商品");
                        return;
                    }
                }
                JSONObject object = new JSONObject();
                object.put("productCount", 1);
                object.put("isSelected", true);
                object.put("product", productDetail.getJsonObject());
                carArray.put(object);
                Content.saveStringContent(Parameters.CACHE_KEY_CAR,
                        carArray.toString());
                ApplicationTool.showToast("已添加到购物车");
            } catch (JSONException e) {
                ApplicationTool.showToast("添加到购物车失败");
                e.printStackTrace();
            }
        } else {
            ApplicationTool.showToast("此商品无货，无法添加到购物车");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.product_detail_html:
                Bundle bundle = new Bundle();
                bundle.putString("html", productDetail.getXiangqing());
                bundle.putInt("type", WebFragment.TYPE_HTML);
                jump(WebFragment.class.getName(), productDetail.getProductName(),
                        bundle);
                break;
            case R.id.product_detail_attr:
                if (productDetail.getProductRelations().size() == 0) {
                    noRelationTextView.setVisibility(View.VISIBLE);
                } else {
                    noRelationTextView.setVisibility(View.GONE);
                }
                relationLayout.setVisibility(View.VISIBLE);
                break;

            case R.id.product_detail_relation_layout:
                relationLayout.setVisibility(View.GONE);
                break;

            case R.id.product_detail_relation_hide:
                relationLayout.setVisibility(View.GONE);
                break;

            case R.id.product_detail_image_last:
                if (imageAdapter.getCount() > 0) {
                    if (imagePager.getCurrentItem() == 0) {
                        setImagePosition(imageAdapter.getCount() - 1);
                    } else {
                        setImagePosition(imagePager.getCurrentItem() - 1);
                    }
                }
                break;

            case R.id.product_detail_image_next:
                if (imageAdapter.getCount() > 0) {
                    if (imagePager.getCurrentItem() == imageAdapter.getCount() - 1) {
                        setImagePosition(0);
                    } else {
                        setImagePosition(imagePager.getCurrentItem() + 1);
                    }
                }
                break;

            case R.id.product_detail_car:
                addToCar();
                break;

            case R.id.product_detail_buy:
                buySaleProduct();
                break;

            default:
                break;

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
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_attr_value,
                        null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.attr_value);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(productDetail.getProductRelations()
                    .get(position).getAttName());
            if (productDetail.getProductRelations().get(position).getId()
                    .equals(productDetail.getId())) {
                holder.textView
                        .setBackgroundResource(R.drawable.back_white_rec_border_orange);
            } else {
                holder.textView
                        .setBackgroundResource(R.drawable.selector_white_rec_border);
            }
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    private void getProductDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("jmdId", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_PRODUCT_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject detailObject = object
                                    .optJSONObject("dataMap");
                            if (detailObject != null) {
                                productDetail = new ModelProduct(detailObject);
                                showDetail();
                            }
                        } else {
                            htmlButton.setClickable(false);
                            attrButton.setClickable(false);
                            carButton.setClickable(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    htmlButton.setClickable(false);
                    attrButton.setClickable(false);
                    carButton.setClickable(false);
                }
            }
        });
    }

    private void getMiaoshaSaleDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productId", productDetail.getId()));
        post(API.API_SALE_MIAOSHA_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    saleDetailMiaosha = new ModelSaleDetailMiaosha(result);
                    if (saleDetailMiaosha != null) {
                        if (saleDetailMiaosha.getSeckillPrice() > 0) {
                            activityLayout.setVisibility(View.VISIBLE);
                            activityNameTextView.setText(saleDetailMiaosha
                                    .getSeckillName());
                            // 开始时间<=当前时间，活动已开始
                            if (saleDetailMiaosha.getStartTime() <= Calendar
                                    .getInstance().getTime().getTime()) {
                                // 剩余秒杀数量>0，显示秒杀信息
                                if (saleDetailMiaosha.getSeckillNUmber() > 0) {
                                    activityDetailTextView.setText("秒杀已开始，每个账号限购"
                                            + saleDetailMiaosha.getMemberNumber()
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
                                            + saleDetailMiaosha.getSeckillNUmber()
                                            + "件");
                                    carButton.setText("立即抢购");
                                    carButton
                                            .setOnClickListener(new OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    buySaleProduct();
                                                }
                                            });
                                }
                            } else {
                                // 开始时间>当前时间，活动未开始，显示预告
                                activityDetailTextView.setText("开始时间:\n"
                                        + simpleDateFormat.format(new Date(
                                        saleDetailMiaosha.getStartTime()))
                                        + "\n原价："
                                        + Parameters.CONSTANT_RMB
                                        + decimalFormat.format(saleDetailMiaosha
                                        .getPrice())
                                        + ","
                                        + "秒杀价："
                                        + Parameters.CONSTANT_RMB
                                        + decimalFormat.format(saleDetailMiaosha
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

    private void getTimeSaleDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productId", productDetail.getId()));
        post(API.API_SALE_TIME_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    saleDetailTime = new ModelSaleDetailTime(result);
                    if (saleDetailTime != null) {
                        if (saleDetailTime.getPromotionPrice() > 0) {
                            // 开始时间>当前时间，未开始，显示活动预告
                            if (saleDetailTime.getStartTime() > Calendar
                                    .getInstance().getTime().getTime()) {
                                activityLayout.setVisibility(View.VISIBLE);
                                activityNameTextView.setText(saleDetailTime
                                        .getPromotionName());
                                activityDetailTextView.setText("活动时间:\n"
                                        + simpleDateFormat.format(new Date(
                                        saleDetailTime.getStartTime()))
                                        + " 至\n"
                                        + simpleDateFormat.format(new Date(
                                        saleDetailTime.getEndTime())));
                            } else if (saleDetailTime.getEndTime() > Calendar
                                    .getInstance().getTime().getTime()) {
                                // 开始时间<=当前时间，结束时间>当前时间，已开始未结束，活动进行时
                                activityLayout.setVisibility(View.VISIBLE);
                                activityNameTextView.setText(saleDetailTime
                                        .getPromotionName());
                                activityDetailTextView.setText("活动时间:\n"
                                        + simpleDateFormat.format(new Date(
                                        saleDetailTime.getStartTime()))
                                        + " 至\n"
                                        + simpleDateFormat.format(new Date(
                                        saleDetailTime.getEndTime())));
                                priceTextView.setText("¥"
                                        + decimalFormat.format(saleDetailTime
                                        .getPromotionPrice()));
                                originalPriceTextView.setText("¥"
                                        + decimalFormat.format(saleDetailTime
                                        .getPrice()));
                                carButton.setText("立即抢购");
                                carButton.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        buySaleProduct();
                                    }
                                });
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

    private void getTejiaSaleDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productId", productDetail
                .getId()));
        post(API.API_SALE_TEJIA_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    saleDetailTejia = new ModelSaleDetailTejia(result);
                    if (saleDetailTejia != null) {
                        if (saleDetailTejia.getSalePrice() > 0) {
                            if (saleDetailTejia.getNumbers() > 0) {
                                activityLayout.setVisibility(View.VISIBLE);
                                activityNameTextView.setText(saleDetailTejia
                                        .getType());
                                activityDetailTextView.setText(saleDetailTejia
                                        .getSalePromotionName());
                                priceTextView.setText("¥"
                                        + decimalFormat.format(saleDetailTejia
                                        .getSalePrice()));
                                originalPriceTextView.setText("¥"
                                        + decimalFormat.format(saleDetailTejia
                                        .getPrice()));
                                carButton.setText("立即抢购");
                                carButton.setOnClickListener(new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        buySaleProduct();
                                    }
                                });
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void buySaleProduct() {
        if (User.getUser().isLogin()) {
            Bundle bundle = new Bundle();
            bundle.putString("productId", productDetail.getId());
            bundle.putInt("saleType", saleType);
            jump(SaleProductOrderConfirmFragment.class.getName(), "确认订单",
                    bundle);
        } else {
            Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT).show();
            jump(UserLoginFragment.class.getName(), "会员登录");
        }
    }
}
