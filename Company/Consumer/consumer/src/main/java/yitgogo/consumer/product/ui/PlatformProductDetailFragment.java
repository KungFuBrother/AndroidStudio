package yitgogo.consumer.product.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.product.model.ModelCar;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerGridView;

import static com.smartown.yitian.gogo.R.id.platform_product_detail_add_car;

public class PlatformProductDetailFragment extends BaseNetworkFragment {

    CirclePageIndicator pageIndicator;
    FrameLayout imageLayout;
    ViewPager imagePager;

    TextView nameTextView;

    TextView priceTextView;

    LinearLayout attrLayout;
    TextView attrTextView;

    LinearLayout areaLayout;
    TextView areaTextView;

    FrameLayout countDeleteLayout;
    FrameLayout countAddLayout;
    TextView countEditText;

    LinearLayout htmlLayout;

    TextView totalMoneyTextView;
    Button buyButton;
    Button carButton;

    LinearLayout relationLayout;
    TextView hideRelationButton, noRelationTextView;
    InnerGridView relationList;

    ImageAdapter imageAdapter;
    String productId = "";
    ModelProduct productDetail;
    RelationAdapter relationAdapter;

    int buyCount = 1;
    double totalMoney = 0;

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
        MobclickAgent.onPageStart(PlatformProductDetailFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PlatformProductDetailFragment.class.getName());
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
        }
        productDetail = new ModelProduct();
        imageAdapter = new ImageAdapter();
        relationAdapter = new RelationAdapter();
    }

    protected void findViews() {
        pageIndicator = (CirclePageIndicator) findViewById(R.id.platform_product_detail_image_indicator);
        imageLayout = (FrameLayout) findViewById(R.id.platform_product_detail_image_layout);
        imagePager = (ViewPager) findViewById(R.id.platform_product_detail_image_pager);

        nameTextView = (TextView) findViewById(R.id.platform_product_detail_name);

        priceTextView = (TextView) findViewById(R.id.platform_product_detail_price);

        attrLayout = (LinearLayout) findViewById(R.id.platform_product_detail_attr_layout);
        attrTextView = (TextView) findViewById(R.id.platform_product_detail_attr);

        areaLayout = (LinearLayout) findViewById(R.id.platform_product_detail_area_layout);
        areaTextView = (TextView) findViewById(R.id.platform_product_detail_area);

        countDeleteLayout = (FrameLayout) findViewById(R.id.platform_product_detail_count_delete);
        countAddLayout = (FrameLayout) findViewById(R.id.platform_product_detail_count_add);
        countEditText = (TextView) findViewById(R.id.platform_product_detail_count);

        htmlLayout = (LinearLayout) findViewById(R.id.platform_product_detail_html);

        totalMoneyTextView = (TextView) findViewById(R.id.platform_product_detail_total_money);
        buyButton = (Button) findViewById(R.id.platform_product_detail_buy);
        carButton = (Button) findViewById(platform_product_detail_add_car);

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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth());
        imageLayout.setLayoutParams(layoutParams);
        imagePager.setAdapter(imageAdapter);
        pageIndicator.setViewPager(imagePager);
//        relationList.setAdapter(relationAdapter);
    }

    @Override
    protected void registerViews() {
        attrLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        areaLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

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

            }
        });
        carButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        countAddLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                buyCount++;
                countTotalMoney();
            }
        });
        countDeleteLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buyCount > 1) {
                    buyCount--;
                    countTotalMoney();
                }
            }
        });
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

    private void showProductDetail() {
        imageAdapter.notifyDataSetChanged();
        nameTextView.setText(productDetail.getProductName());
        priceTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(productDetail.getPrice()));
        attrTextView.setText(productDetail.getAttName());
        countTotalMoney();
    }

    private void countTotalMoney() {
        countEditText.setText(String.valueOf(buyCount));
        totalMoney = buyCount * productDetail.getPrice();
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(totalMoney));
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
                                showProductDetail();
                            }
                        } else {
                            carButton.setClickable(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    carButton.setClickable(false);
                }
            }
        });
    }

    private void getFreight() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productNumber", productDetail.getNumber() + "-" + buyCount));
        requestParams.add(new RequestParam("areaid", Store.getStore().getStoreId()));
    }

}
