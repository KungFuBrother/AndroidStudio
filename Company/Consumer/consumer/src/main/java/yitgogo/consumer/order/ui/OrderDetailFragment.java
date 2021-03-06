package yitgogo.consumer.order.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrder;
import yitgogo.consumer.order.model.ModelProductOrder;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.NormalAskDialog;

public class OrderDetailFragment extends BaseNetworkFragment {

    TextView orderNumberText, orderStateText, orderDateText, senderTextView,
            orderWuliuText, userNameText, userPhoneText, userAddressText,
            moneyText, discountText, payMoneyText;
    InnerListView productList;
    LinearLayout wuliuButton;
    SwipeRefreshLayout refreshLayout;
    ModelOrder order;
    List<ModelProductOrder> products;
    OrderProductAdapter orderProductAdapter;

    TextView payButton, receiveButton;
    // LinearLayout actionBarLayout, actionBar;
    // LinearLayout.LayoutParams actionButtonLayoutParams;

    String orderNumber = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_detail);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(OrderDetailFragment.class.getName());
        getOrderDetail();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(OrderDetailFragment.class.getName());
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("orderNumber")) {
                orderNumber = bundle.getString("orderNumber");
            }
        }
        products = new ArrayList<ModelProductOrder>();
        orderProductAdapter = new OrderProductAdapter();
        // actionButtonLayoutParams = new LinearLayout.LayoutParams(0,
        // LinearLayout.LayoutParams.MATCH_PARENT);
        // actionButtonLayoutParams.weight = 1;
    }

    @Override
    protected void findViews() {
        orderNumberText = (TextView) contentView
                .findViewById(R.id.order_detail_number);
        orderStateText = (TextView) contentView
                .findViewById(R.id.order_detail_state);
        orderDateText = (TextView) contentView
                .findViewById(R.id.order_detail_date);
        senderTextView = (TextView) contentView
                .findViewById(R.id.order_detail_sender);
        orderWuliuText = (TextView) contentView
                .findViewById(R.id.order_detail_wuliu);
        userNameText = (TextView) contentView
                .findViewById(R.id.order_detail_user_name);
        userPhoneText = (TextView) contentView
                .findViewById(R.id.order_detail_user_phone);
        userAddressText = (TextView) contentView
                .findViewById(R.id.order_detail_user_address);
        moneyText = (TextView) contentView
                .findViewById(R.id.order_detail_total_money);
        discountText = (TextView) contentView
                .findViewById(R.id.order_detail_discount);
        payMoneyText = (TextView) contentView
                .findViewById(R.id.order_detail_real_money);
        productList = (InnerListView) contentView
                .findViewById(R.id.order_detail_product);
        wuliuButton = (LinearLayout) contentView
                .findViewById(R.id.order_detail_wuliu_button);
        refreshLayout = (SwipeRefreshLayout) contentView
                .findViewById(R.id.order_detail_refresh);

        // actionBar = (LinearLayout) view
        // .findViewById(R.id.order_detail_action_bar);
        // actionBarLayout = (LinearLayout) view
        // .findViewById(R.id.order_detail_action_layout);
        payButton = (TextView) contentView
                .findViewById(R.id.order_detail_action_pay);
        receiveButton = (TextView) contentView
                .findViewById(R.id.order_detail_action_receive);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        productList.setAdapter(orderProductAdapter);
    }

    @Override
    protected void registerViews() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                getOrderDetail();
            }
        });
        wuliuButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("orderNumber", order.getOnlyOne());
                jump(OrderWuliuFragment.class.getName(), "物流信息", bundle);
            }
        });
        payButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                payMoney(order.getOrderNumber(), order.getTotalSellPrice(),
                        PayFragment.ORDER_TYPE_YY);
            }
        });
        receiveButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                NormalAskDialog askDialog = new NormalAskDialog(
                        "确认已经收到此订单中的货物了吗？", "收到了", "没收到") {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (makeSure) {
                            receiveOrder();
                        }
                        super.onDismiss(dialog);
                    }
                };
                askDialog.show(getFragmentManager(), null);
            }
        });
    }

    private void showInfo() {
        products = order.getProducts();
        orderProductAdapter.notifyDataSetChanged();
        orderNumberText.setText("订单号：" + order.getOrderNumber());
        orderStateText.setText(order.getOrderState().getOrderStatusName());
        orderDateText.setText(order.getSellTime());
        senderTextView.setText(Html.fromHtml(order.getHuoyuan()));
        userNameText.setText(order.getCustomerName());
        userPhoneText.setText(getSecretPhone(order.getPhone()));
        userAddressText.setText(order.getShippingaddress());
        // moneyText.setText(Parameters.CONSTANT_RMB
        // + decimalFormat.format(order.getTotalMoney()));
        // discountText.setText(Parameters.CONSTANT_RMB
        // + decimalFormat.format(order.getTotalDiscount()));
        payMoneyText.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(order.getTotalSellPrice()));
        initAciotnBar();
    }

    private void initAciotnBar() {
        if (order.getOrderState().getOrderStatusName().equalsIgnoreCase("新订单")) {
            payButton.setVisibility(View.VISIBLE);
            return;
        }
        if (order.getOrderState().getOrderStatusName().equalsIgnoreCase("已发货")) {
            receiveButton.setVisibility(View.VISIBLE);
            return;
        }
        payButton.setVisibility(View.GONE);
        receiveButton.setVisibility(View.GONE);
    }

    // private void addActionButton(String lable, OnClickListener
    // onClickListener) {
    // TextView button = new TextView(getActivity());
    // button.setLayoutParams(actionButtonLayoutParams);
    // button.setGravity(Gravity.CENTER);
    // button.setOnClickListener(onClickListener);
    // button.setPadding(ScreenUtil.dip2px(16), 0, ScreenUtil.dip2px(16), 0);
    // button.setText(lable);
    // button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
    // button.setTextColor(getResources().getColor(R.color.textColorSecond));
    // button.setBackgroundResource(R.drawable.button_rec_round);
    // actionBar.addView(button);
    // }

    class OrderProductAdapter extends BaseAdapter {

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
                convertView = layoutInflater.inflate(
                        R.layout.list_platform_order_product, null);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.list_product_img);
                holder.productNameText = (TextView) convertView
                        .findViewById(R.id.list_product_name);
                holder.productAttrText = (TextView) convertView
                        .findViewById(R.id.list_product_attr);
                holder.productPriceText = (TextView) convertView
                        .findViewById(R.id.list_product_price);
                holder.productCountText = (TextView) convertView
                        .findViewById(R.id.list_product_count);

                holder.returnButton = (Button) convertView.findViewById(R.id.list_platform_order_product_return);
                holder.operatedLayout = (LinearLayout) convertView.findViewById(R.id.list_platform_order_product_return_operated_layout);
                holder.operatedResultButton = (Button) convertView.findViewById(R.id.list_platform_order_product_return_operated_result);
                holder.successLayout = (LinearLayout) convertView.findViewById(R.id.list_platform_order_product_return_success_layout);
                holder.operatingTextView = (TextView) convertView.findViewById(R.id.list_platform_order_product_return_operating);
                holder.failLayout = (LinearLayout) convertView.findViewById(R.id.list_platform_order_product_return_failed_layout);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final ModelProductOrder product = products.get(position);
            holder.productNameText.setText(product.getProductName());
            holder.productAttrText.setText(product.getAttName());
            holder.productPriceText.setText("¥"
                    + decimalFormat.format(product.getUnitSellPrice()));
            holder.productCountText.setText(" × "
                    + product.getProductQuantity() + product.getProductUnit());
            ImageLoader.getInstance().displayImage(product.getImg(), holder.image);

            holder.returnButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("object", product.getJsonObject().toString());
                    jump(OrderPlatformReturnFragment.class.getName(), "申请退货", bundle);
                }
            });

            holder.operatedResultButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    jump(OrderPlatformReturnResultFragment.class.getName(), "退货结果");
                }
            });

            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView productNameText, productAttrText, productPriceText,
                    productCountText;
            Button returnButton;

            LinearLayout operatedLayout;
            Button operatedResultButton;

            LinearLayout successLayout;

            TextView operatingTextView;

            LinearLayout failLayout;

        }
    }

    private void getOrderDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("orderNumber", orderNumber));
        requestParams.add(new RequestParam("uerNumber", User.getUser().getUseraccount()));
        post(API.API_ORDER_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshLayout.setRefreshing(false);
                if (result.length() > 0) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            if (object.has("object")) {
                                if (!object.getString("object").equalsIgnoreCase(
                                        "null")) {
                                    order = new ModelOrder(
                                            object.getJSONObject("object"));
                                    showInfo();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void receiveOrder() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("orderNumber", orderNumber));
        requestParams.add(new RequestParam("stateId", "7"));
        requestParams.add(new RequestParam("onlyOne", order.getOnlyOne()));
        post(API.API_ORDER_RECEIVED, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            getOrderDetail();
                            return;
                        }
                        ApplicationTool.showToast(object.optString("message"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
