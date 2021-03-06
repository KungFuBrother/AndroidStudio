package yitgogo.consumer.local.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.local.model.LocalCarController;
import yitgogo.consumer.local.model.ModelLocalGoodsDetail;
import yitgogo.consumer.product.ui.WebFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;

/**
 * @author Tiger
 * @description 本地商品详情
 */
public class LocalGoodsDetailFragment extends BaseNetworkFragment {

    // 商品详情部分控件
    ViewPager imagesPager;
    ImageView imageLastButton, imageNextButton;
    TextView imageIndexTextView, nameTextView, priceTextView, unitTextView,
            attrTextView, buyButton, addCarButton;

    LinearLayout attrButton, detailButton;

    String goodsId = "";

    ModelLocalGoodsDetail goodsDetail;

    ImageAdapter imageAdapter;
    RelationAdapter relationAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_lcaol_goods_detail);
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
        getGoodsDetail();
    }

    @Override
    protected void init() {
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
        attrTextView = (TextView) contentView
                .findViewById(R.id.fresh_attr_name);

        attrButton = (LinearLayout) contentView.findViewById(R.id.fresh_attr);

        buyButton = (TextView) contentView.findViewById(R.id.fresh_detail_buy);
        addCarButton = (TextView) contentView
                .findViewById(R.id.fresh_detail_car);

        detailButton = (LinearLayout) contentView
                .findViewById(R.id.fresh_detail);
        initViews();
        registerViews();
    }

    @SuppressLint("NewApi")
    @Override
    protected void initViews() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth());
        imagesPager.setLayoutParams(layoutParams);
        imagesPager.setAdapter(imageAdapter);
    }

    @Override
    protected void registerViews() {
        addImageButton(R.drawable.iconfont_cart, "购物车", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ShoppingCarLocalFragment.class.getName(), "本地商品购物车");
            }
        });
        attrButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!goodsDetail.getProductRelations().isEmpty()) {
                    new RelationDialog().show(getFragmentManager(), null);
                }
            }
        });
        buyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (User.getUser().isLogin()) {
                    if (goodsDetail != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("goods", goodsDetail.getLocalGoods()
                                .getJsonObject().toString());
                        jump(LocalGoodsBuyFragment.class.getName(), "确认订单",
                                bundle);
                    }
                } else {
                    Toast.makeText(getActivity(), "请先登录", Toast.LENGTH_SHORT)
                            .show();
                    jump(UserLoginFragment.class.getName(), "会员登录");
                    return;
                }
            }
        });
        addCarButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addToCar();
            }
        });
        detailButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("html", goodsDetail.getLocalGoods()
                        .getRetailProdDescribe());
                bundle.putInt("type", WebFragment.TYPE_HTML);
                jump(WebFragment.class.getName(), goodsDetail.getLocalGoods()
                        .getRetailProdManagerName(), bundle);
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

    private void showGoodsInfo() {
        if (goodsDetail != null) {
            imageAdapter.notifyDataSetChanged();
            nameTextView.setText(goodsDetail.getLocalGoods()
                    .getRetailProdManagerName());
            priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(goodsDetail.getLocalGoods()
                    .getRetailPrice()));
            unitTextView.setText("/" + goodsDetail.getLocalGoods().getUnit());
            attrTextView.setText(goodsDetail.getLocalGoods().getAttName());
            getFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fresh_detail_store_info,
                            new StorePartInfoFragment(goodsDetail
                                    .getLocalGoods().getProviderBean()))
                    .commit();
        }
    }

    /**
     * 添加到购物车
     */
    private void addToCar() {
        switch (LocalCarController.addGoods(goodsDetail.getLocalGoods())) {
            case 0:
                ApplicationTool.showToast("已添加到购物车");
                break;

            case 1:
                ApplicationTool.showToast("已添加过此商品");
                break;

            case 2:
                ApplicationTool.showToast("添加到购物车失败");
                break;

            default:
                break;
        }
    }

    private void getGoodsDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("retailProductManagerID", goodsId));
        post(API.API_LOCAL_BUSINESS_GOODS_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
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
        });
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
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth()));
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
            listView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    if (!goodsDetail.getProductRelations().get(arg2).getId()
                            .equals(goodsDetail.getLocalGoods().getId())) {
                        goodsId = goodsDetail.getProductRelations().get(arg2)
                                .getId();
                        getGoodsDetail();
                    }
                    dismiss();
                }
            });
        }
    }

}
