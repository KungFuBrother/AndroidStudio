package yitgogo.consumer.local.ui;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.CirclePageIndicator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.LocalCarController;
import yitgogo.consumer.local.model.ModelLocalGoodsDetail;
import yitgogo.consumer.product.ui.WebFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.Notify;

public class LocalGoodsDetailFragment extends BaseNotifyFragment {

    CirclePageIndicator pageIndicator;
    FrameLayout imageLayout;
    ViewPager imagePager;

    TextView nameTextView;

    TextView priceTextView;

    LinearLayout attrLayout;
    TextView attrTextView;

    FrameLayout countDeleteLayout;
    FrameLayout countAddLayout;
    TextView countTextView;

    LinearLayout htmlLayout;

    TextView totalMoneyTextView;
    Button buyButton;
    Button carButton;

    String goodsId = "";
    ModelLocalGoodsDetail goodsDetail;
    ImageAdapter imageAdapter;
    RelationAdapter relationAdapter;

    int buyCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_local_goods_detail);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalGoodsDetailFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LocalGoodsDetailFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetGoodsDetail().execute();
    }

    private void init() {
        measureScreen();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("id")) {
                goodsId = bundle.getString("id");
            }
        }
        goodsDetail = new ModelLocalGoodsDetail();
        imageAdapter = new ImageAdapter();
        relationAdapter = new RelationAdapter();
    }

    protected void findViews() {
        pageIndicator = (CirclePageIndicator) contentView.findViewById(R.id.local_goods_detail_image_indicator);
        imageLayout = (FrameLayout) contentView.findViewById(R.id.local_goods_detail_image_layout);
        imagePager = (ViewPager) contentView.findViewById(R.id.local_goods_detail_image_pager);

        nameTextView = (TextView) contentView.findViewById(R.id.local_goods_detail_name);

        priceTextView = (TextView) contentView.findViewById(R.id.local_goods_detail_price);

        attrLayout = (LinearLayout) contentView.findViewById(R.id.local_goods_detail_attr_layout);
        attrTextView = (TextView) contentView.findViewById(R.id.local_goods_detail_attr);

        countDeleteLayout = (FrameLayout) contentView.findViewById(R.id.local_goods_detail_count_delete);
        countAddLayout = (FrameLayout) contentView.findViewById(R.id.local_goods_detail_count_add);
        countTextView = (TextView) contentView.findViewById(R.id.local_goods_detail_count);

        htmlLayout = (LinearLayout) contentView.findViewById(R.id.local_goods_detail_html);

        totalMoneyTextView = (TextView) contentView.findViewById(R.id.local_goods_detail_total_money);
        buyButton = (Button) contentView.findViewById(R.id.local_goods_detail_buy);
        carButton = (Button) contentView.findViewById(R.id.local_goods_detail_add_car);

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
        addImageButton(R.drawable.iconfont_cart, "购物车", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ShoppingCarLocalFragment.class.getName(), "本地商品购物车");
            }
        });
        attrLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!goodsDetail.getProductRelations().isEmpty()) {
                    new RelationDialog().show(getFragmentManager(), null);
                }
            }
        });
        htmlLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("html", goodsDetail.getLocalGoods().getRetailProdDescribe());
                bundle.putInt("type", WebFragment.TYPE_HTML);
                jump(WebFragment.class.getName(), goodsDetail.getLocalGoods().getRetailProdManagerName(), bundle);
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

    private void showGoodsInfo() {
        if (goodsDetail != null) {
            imageAdapter.notifyDataSetChanged();
            nameTextView.setText(goodsDetail.getLocalGoods().getRetailProdManagerName());
            priceTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(goodsDetail.getLocalGoods().getRetailPrice()));
            attrTextView.setText(goodsDetail.getLocalGoods().getAttName());
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.local_goods_detail_store_info, new StorePartInfoFragment(goodsDetail.getLocalGoods().getProviderBean()))
                    .commit();
            countTotalMoney();
        }
    }

    private void countTotalMoney() {
        countTextView.setText(String.valueOf(buyCount));
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(buyCount * goodsDetail.getLocalGoods().getRetailPrice()));
    }

    private void buyProduct() {
        if (User.getUser().isLogin()) {
            if (goodsDetail != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("buyCount", buyCount);
                bundle.putString("goods", goodsDetail.getLocalGoods().getJsonObject().toString());
                jump(LocalGoodsBuyFragment.class.getName(), "确认订单", bundle);
            }
        } else {
            Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT)
                    .show();
            jump(UserLoginFragment.class.getName(), "会员登录");
            return;
        }
    }

    /**
     * 添加到购物车
     */
    private void addToCar() {
        switch (LocalCarController.addGoods(goodsDetail.getLocalGoods())) {
            case 0:
                Notify.show("已添加到购物车");
                break;

            case 1:
                Notify.show("已添加过此商品");
                break;

            case 2:
                Notify.show("添加到购物车失败");
                break;

            default:
                break;
        }
    }

    class GetGoodsDetail extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("retailProductManagerID",
                    goodsId));
            return netUtil.postWithoutCookie(
                    API.API_LOCAL_BUSINESS_GOODS_DETAIL, valuePairs, false,
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
                        JSONObject object2 = object.optJSONObject("dataMap");
                        if (object2 != null) {
                            goodsDetail = new ModelLocalGoodsDetail(object2);
                            showGoodsInfo();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ImageAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return goodsDetail.getLocalGoods().getImages().size();
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
                    + goodsDetail.getLocalGoods().getImages().size());
            ImageLoader.getInstance().displayImage(
                    getBigImageUrl(goodsDetail.getLocalGoods().getImages()
                            .get(position).getRetailProductImgUrl()),
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
            return goodsDetail.getProductRelations().size();
        }

        @Override
        public Object getItem(int position) {
            return goodsDetail.getProductRelations().get(position);
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
            if (goodsDetail.getProductRelations().get(position).getId()
                    .equals(goodsDetail.getLocalGoods().getId())) {
                viewHolder.imageView
                        .setImageResource(R.drawable.iconfont_check_checked);
            } else {
                viewHolder.imageView
                        .setImageResource(R.drawable.iconfont_check_normal);
            }
            viewHolder.textView.setText(goodsDetail.getProductRelations()
                    .get(position).getAttName());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
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
                    if (!goodsDetail.getProductRelations().get(arg2).getId()
                            .equals(goodsDetail.getLocalGoods().getId())) {
                        goodsId = goodsDetail.getProductRelations().get(arg2)
                                .getId();
                        new GetGoodsDetail().execute();
                    }
                    dismiss();
                }
            });
        }
    }

}
