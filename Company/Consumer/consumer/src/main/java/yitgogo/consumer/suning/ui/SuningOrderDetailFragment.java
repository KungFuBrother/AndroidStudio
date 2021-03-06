package yitgogo.consumer.suning.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import yitgogo.consumer.suning.model.ModelSuningOrder;
import yitgogo.consumer.suning.model.ModelSuningOrderProduct;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.NormalAskDialog;

public class SuningOrderDetailFragment extends BaseNetworkFragment {

    TextView orderNumberText, orderStateText, orderDateText, senderTextView,
            orderWuliuText, userNameText, userPhoneText, userAddressText,
            moneyText, moneyDetailTextView;
    InnerListView productList;
    LinearLayout wuliuButton;
    OrderProductAdapter orderProductAdapter;

    ModelSuningOrder suningOrder = new ModelSuningOrder();

    TextView payButton, receiveButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suning_order_detail);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SuningOrderDetailFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SuningOrderDetailFragment.class.getName());
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("object")) {
                try {
                    suningOrder = new ModelSuningOrder(new JSONObject(bundle.getString("object")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        orderProductAdapter = new OrderProductAdapter();
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
        moneyDetailTextView = (TextView) contentView
                .findViewById(R.id.order_detail_total_money_detail);
        productList = (InnerListView) contentView
                .findViewById(R.id.order_detail_product);
        wuliuButton = (LinearLayout) contentView
                .findViewById(R.id.order_detail_wuliu_button);

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
        showInfo();
    }

    @Override
    protected void registerViews() {
        wuliuButton.setVisibility(View.GONE);
        payButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                payMoney(suningOrder.getOrderNumber(), suningOrder.getAccount() + suningOrder.getFreight(),
                        PayFragment.ORDER_TYPE_SN);
                getActivity().finish();
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
        orderProductAdapter.notifyDataSetChanged();
        orderNumberText.setText("订单号：" + suningOrder.getOrderNumber());
        orderStateText.setText(suningOrder.getOrderType());
        orderDateText.setText(suningOrder.getSellTime());
        senderTextView.setText(suningOrder.getSpName() + "\n" + suningOrder.getSpPhone());
        userNameText.setText(suningOrder.getCustomerName());
        userPhoneText.setText(getSecretPhone(suningOrder.getCustomerPhone()));
        userAddressText.setText(suningOrder.getAddress());
        moneyText.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(suningOrder.getAccount() + suningOrder.getFreight()));
        moneyDetailTextView.setText("商品:" + Parameters.CONSTANT_RMB
                + decimalFormat.format(suningOrder.getAccount()) + ",运费:" + Parameters.CONSTANT_RMB
                + decimalFormat.format(suningOrder.getFreight()));
        initAciotnBar();
    }

    private void initAciotnBar() {
        if (suningOrder.getOrderType().equalsIgnoreCase("新订单")) {
            payButton.setVisibility(View.VISIBLE);
            return;
        }
        if (suningOrder.getOrderType().equalsIgnoreCase("已发货")) {
            receiveButton.setVisibility(View.VISIBLE);
            return;
        }
        payButton.setVisibility(View.GONE);
        receiveButton.setVisibility(View.GONE);
    }

    class OrderProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return suningOrder.getProducts().size();
        }

        @Override
        public Object getItem(int position) {
            return suningOrder.getProducts().get(position);
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
                        R.layout.list_suning_order_product, null);
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
                holder.wuliuButton = (TextView) convertView
                        .findViewById(R.id.list_product_wuliu);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ModelSuningOrderProduct product = suningOrder.getProducts().get(position);
            holder.productNameText.setText(product.getName());
            holder.productAttrText.setText(product.getAttr());
            holder.productPriceText.setText("¥"
                    + decimalFormat.format(product.getPrice()));
            holder.productCountText.setText(" × "
                    + product.getNo());
            if (!product.getImages().isEmpty()) {
                ImageLoader.getInstance().displayImage(product.getImages().get(1),
                        holder.image);
            }
            holder.wuliuButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putString("orderId", suningOrder.getSuNingOrderNumber());
                    bundle.putString("skuId", product.getNumber());
                    jump(SuningOrderWuliuFragment.class.getName(), "物流信息", bundle);
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView productNameText, productAttrText, productPriceText,
                    productCountText, wuliuButton;
        }
    }

    private void receiveOrder() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("tradeNo", suningOrder.getOrderNumber()));
        requestParams.add(new RequestParam("type", "2"));
        post(API.API_SUNING_ORDER_RECEIVE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            suningOrder.setOrderType("已收货");
                            showInfo();
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
