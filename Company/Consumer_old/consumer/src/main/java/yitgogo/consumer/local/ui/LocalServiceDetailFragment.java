package yitgogo.consumer.local.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalService;
import yitgogo.consumer.product.ui.WebFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;

/**
 * @author Tiger
 * @description 本地服务详情
 */
public class LocalServiceDetailFragment extends BaseNotifyFragment {

    String productId = "";
    ModelLocalService serviceDetail;

    // 商品详情部分控件
    ViewPager imagesPager;
    ImageView imageLastButton, imageNextButton;
    TextView imageIndexTextView, nameTextView, priceTextView, unitTextView,
            buyButton, remarkTextView;
    LinearLayout detailButton;
    TextView diliverInfoTextView, paymentTextView, saleTextView;

    ImageAdapter imageAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lcaol_service_detail);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LocalServiceDetailFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalServiceDetailFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetServiceDetail().execute();
    }

    private void init() {
        measureScreen();
        Bundle bundle = getArguments();
        if (bundle.containsKey("productId")) {
            productId = bundle.getString("productId");
            serviceDetail = new ModelLocalService();
            imageAdapter = new ImageAdapter();
        }
    }

    @Override
    protected void findViews() {
        imagesPager = (ViewPager) contentView
                .findViewById(R.id.fresh_detail_images);
        imageLastButton = (ImageView) contentView
                .findViewById(R.id.fresh_detail_image_last);
        imageNextButton = (ImageView) contentView
                .findViewById(R.id.fresh_detail_image_next);
        imageIndexTextView = (TextView) contentView
                .findViewById(R.id.fresh_detail_image_index);
        nameTextView = (TextView) contentView
                .findViewById(R.id.fresh_detail_name);
        priceTextView = (TextView) contentView
                .findViewById(R.id.fresh_detail_price);
        unitTextView = (TextView) contentView
                .findViewById(R.id.fresh_detail_unit);
        buyButton = (TextView) contentView.findViewById(R.id.fresh_detail_buy);
        remarkTextView = (TextView) contentView
                .findViewById(R.id.local_business_detail_remark);
        diliverInfoTextView = (TextView) contentView
                .findViewById(R.id.local_business_detail_send);
        paymentTextView = (TextView) contentView
                .findViewById(R.id.local_business_detail_payment);
        saleTextView = (TextView) contentView
                .findViewById(R.id.local_business_detail_sale);
        detailButton = (LinearLayout) contentView
                .findViewById(R.id.fresh_detail);

        initViews();
        registerViews();
    }

    @SuppressLint("NewApi")
    @Override
    protected void initViews() {
        LayoutParams layoutParams = new LayoutParams(screenWidth, screenWidth);
        imagesPager.setLayoutParams(layoutParams);
        imagesPager.setAdapter(imageAdapter);
    }

    @Override
    protected void registerViews() {
        buyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!User.getUser().isLogin()) {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT)
                            .show();
                    jump(UserLoginFragment.class.getName(), "会员登录");
                    return;
                }
                if (serviceDetail != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("productId", serviceDetail.getId());
                    jump(LocalServiceOrderConfirmFragment.class.getName(),
                            "确认订单", bundle);
                }
            }
        });
        detailButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("html", serviceDetail.getProductDescribe());
                bundle.putInt("type", WebFragment.TYPE_HTML);
                jump(WebFragment.class.getName(),
                        serviceDetail.getProductName(), bundle);
            }
        });
        imageLastButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageAdapter.getCount() > 0) {
                    if (imagesPager.getCurrentItem() == 0) {
                        imagesPager.setCurrentItem(imageAdapter.getCount() - 1, true);
                    } else {
                        imagesPager.setCurrentItem(imagesPager.getCurrentItem() - 1, true);
                    }
                }
            }
        });
        imageNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageAdapter.getCount() > 0) {
                    if (imagesPager.getCurrentItem() == imageAdapter.getCount() - 1) {
                        imagesPager.setCurrentItem(0, true);
                    } else {
                        imagesPager.setCurrentItem(imagesPager.getCurrentItem() + 1, true);
                    }
                }
            }
        });
    }

    /**
     * 显示售卖方式(送货方式、支付方式、优惠信息)
     */
    private void showSaleMethod() {
        if (serviceDetail.isDeliverYN()) {
            diliverInfoTextView.setText("*支持送货上门，满"
                    + serviceDetail.getDeliverNum() + "件起送");
        } else {
            diliverInfoTextView.setText("*不支持送货上门");
        }
        if (serviceDetail.isDeliveredToPaidYN()) {
            paymentTextView.setText("*支持货到付款");
        } else {
            paymentTextView.setText("*不支持货到付款");
        }
        saleTextView.setText("*" + serviceDetail.getPrivilege());
    }

    /**
     * 显示本地服务详情
     */
    private void showServiceInfo() {
        if (serviceDetail != null) {
            imageAdapter.notifyDataSetChanged();
            nameTextView.setText(serviceDetail.getProductName());
            priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(serviceDetail.getProductPrice()));
            remarkTextView.setText(serviceDetail.getRemark());
            showSaleMethod();
            getFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fresh_detail_store_info,
                            new StorePartInfoFragment(serviceDetail
                                    .getProviderBean())).commit();
        }
    }

    class ImageAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return serviceDetail.getImages().size();
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
            TextView indexTextView = (TextView) imageLayout
                    .findViewById(R.id.view_pager_index);
            indexTextView.setText((position + 1) + "/"
                    + serviceDetail.getImages().size());
            ImageLoader.getInstance().displayImage(
                    getBigImageUrl(serviceDetail.getImages().get(position)
                            .getImgName()), imageView,
                    new SimpleImageLoadingListener() {
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

    class GetServiceDetail extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("productId", productId));
            return netUtil.postWithoutCookie(
                    API.API_LOCAL_BUSINESS_SERVICE_DETAIL, valuePairs, false,
                    false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONObject object2 = object.optJSONObject("object");
                        if (object2 != null) {
                            serviceDetail = new ModelLocalService(object2);
                            showServiceInfo();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
