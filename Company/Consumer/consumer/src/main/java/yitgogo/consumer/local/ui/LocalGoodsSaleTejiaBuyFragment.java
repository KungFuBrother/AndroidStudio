package yitgogo.consumer.local.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.local.model.ModelLocalSaleTejiaDetail;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelDiliver;
import yitgogo.consumer.order.model.ModelLocalGoodsOrderResult;
import yitgogo.consumer.order.model.ModelPayment;
import yitgogo.consumer.order.model.ModelStorePostInfo;
import yitgogo.consumer.order.ui.OrderConfirmPartAddressFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerGridView;

public class LocalGoodsSaleTejiaBuyFragment extends BaseNetworkFragment {

    ImageView goodsImageView;
    TextView goodsNameTextView, goodsAttrTextView, goodsPriceTextView,
            storeInfoTextView, goodsCountTextView, payDiliverTextView,
            goodsMoneyTextView, postFeeTextView, totalPayTextView,
            confirmButton;
    LinearLayout diliverPayButton;
    FrameLayout countDeleteButton, countAddButton;

    double totalMoney = 0;
    int goodsCount = 1;

    ModelStorePostInfo storePostInfo = new ModelStorePostInfo();

    List<ModelDiliver> dilivers;
    List<ModelPayment> payments;
    ModelDiliver diliver;
    ModelPayment payment;
    DiliverAdapter diliverAdapter;
    PaymentAdapter paymentAdapter;

    ModelLocalSaleTejiaDetail tejiaDetail = new ModelLocalSaleTejiaDetail();
    OrderConfirmPartAddressFragment addressFragment = new OrderConfirmPartAddressFragment();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getStoreInfo();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm_order_local_goods);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LocalGoodsSaleTejiaBuyFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalGoodsSaleTejiaBuyFragment.class.getName());
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("object")) {
                try {
                    tejiaDetail = new ModelLocalSaleTejiaDetail(new JSONObject(
                            bundle.getString("object")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        dilivers = new ArrayList<ModelDiliver>();
        payments = new ArrayList<ModelPayment>();
        diliver = new ModelDiliver();
        payment = new ModelPayment();
        diliverAdapter = new DiliverAdapter();
        paymentAdapter = new PaymentAdapter();

    }

    @Override
    protected void findViews() {
        goodsImageView = (ImageView) contentView
                .findViewById(R.id.order_confirm_goods_image);
        goodsNameTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_goods_name);
        goodsAttrTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_goods_attr);
        goodsPriceTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_goods_price);
        storeInfoTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_store_info);
        goodsCountTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_goods_count);
        payDiliverTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_goods_pay_diliver_type);
        goodsMoneyTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_goods_money);
        postFeeTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_post_fee);
        totalPayTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_total_pay);
        confirmButton = (TextView) contentView
                .findViewById(R.id.order_confirm_confirm);
        diliverPayButton = (LinearLayout) contentView
                .findViewById(R.id.order_confirm_goods_pay_diliver);
        countDeleteButton = (FrameLayout) contentView
                .findViewById(R.id.order_confirm_goods_count_delete);
        countAddButton = (FrameLayout) contentView
                .findViewById(R.id.order_confirm_goods_count_add);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        getFragmentManager().beginTransaction()
                .replace(R.id.order_confirm_address, addressFragment).commit();
        ImageLoader.getInstance()
                .displayImage(getSmallImageUrl(tejiaDetail.getPromotionImg()),
                        goodsImageView);
        goodsNameTextView.setText(tejiaDetail.getProductName());
        goodsAttrTextView.setText(tejiaDetail.getAttribute());
        goodsPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(tejiaDetail.getSalePrice()));
        setDiliverPayType();
    }

    @Override
    protected void registerViews() {
        countAddButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                goodsCount++;
                countTotalMoney();
            }
        });
        countDeleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                if (goodsCount > 1) {
                    goodsCount--;
                }
                countTotalMoney();
            }
        });
        diliverPayButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                new DiliverPaymentDialog().show(getFragmentManager(), null);
            }
        });
        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                confirmOrder();
            }
        });
    }

    private void setDiliverPayType() {
        payDiliverTextView.setText(diliver.getName() + "、" + payment.getName());
    }

    private void setStorePostInfo() {
        dilivers.clear();
        payments.clear();
        dilivers.add(new ModelDiliver(ModelDiliver.TYPE_SELF,
                ModelDiliver.NAME_SELF));
        dilivers.add(new ModelDiliver(ModelDiliver.TYPE_HOME,
                ModelDiliver.NAME_HOME));
        payments.add(new ModelPayment(ModelPayment.TYPE_ONLINE,
                ModelPayment.NAME_ONLINE));
        if (storePostInfo.isSupportForDelivery()) {
            payments.add(new ModelPayment(ModelPayment.TYPE_RECEIVED,
                    ModelPayment.NAME_RECEIVED));
        }
        diliverAdapter.notifyDataSetChanged();
        paymentAdapter.notifyDataSetChanged();
    }

    private void countTotalMoney() {
        totalMoney = 0;
        double goodsMoney = goodsCount * tejiaDetail.getSalePrice();
        double postFee = 0;
        if (diliver.getType() != ModelDiliver.TYPE_SELF & goodsMoney > 0
                & goodsMoney < storePostInfo.getHawManyPackages()) {
            postFee = storePostInfo.getPostage();
        }
        totalMoney = goodsMoney + postFee;
        goodsCountTextView.setText(goodsCount + "");
        goodsMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(goodsMoney));
        postFeeTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(postFee));
        totalPayTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalMoney));
    }

    private void confirmOrder() {
        if (totalMoney <= 0) {
            ApplicationTool.showToast("商品信息有误");
        } else if (addressFragment.getAddress() == null) {
            ApplicationTool.showToast("收货人地址有误");
        } else {
            addLocalGoodsOrder();
        }
    }

    class DiliverPaymentDialog extends DialogFragment {

        View dialogView;
        InnerGridView diliverGridView, paymentGridView;
        TextView okButton;

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
            dialogView = layoutInflater.inflate(
                    R.layout.dialog_diliver_payment, null);
            okButton = (TextView) dialogView.findViewById(R.id.dialog_ok);
            diliverGridView = (InnerGridView) dialogView
                    .findViewById(R.id.diliver_types);
            paymentGridView = (InnerGridView) dialogView
                    .findViewById(R.id.payment_types);
            initViews();
        }

        private void initViews() {
            diliverGridView.setAdapter(diliverAdapter);
            paymentGridView.setAdapter(paymentAdapter);
            okButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            diliverGridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    if (diliver.getType() != dilivers.get(arg2).getType()) {
                        diliver = dilivers.get(arg2);
                        diliverAdapter.notifyDataSetChanged();
                        setDiliverPayType();
                        countTotalMoney();
                    }
                }
            });
            paymentGridView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    if (payment.getType() != payments.get(arg2).getType()) {
                        payment = payments.get(arg2);
                        paymentAdapter.notifyDataSetChanged();
                        setDiliverPayType();
                        countTotalMoney();
                    }
                }
            });
        }

    }

    class DiliverAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dilivers.size();
        }

        @Override
        public Object getItem(int position) {
            return dilivers.get(position);
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
                viewHolder.textView = (TextView) convertView
                        .findViewById(R.id.diliver_payment_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (diliver.getType() == dilivers.get(position).getType()) {
                viewHolder.textView.setTextColor(getResources().getColor(
                        R.color.blue));
                viewHolder.textView
                        .setBackgroundResource(R.drawable.back_trans_rec_border_blue);
            } else {
                viewHolder.textView.setTextColor(getResources().getColor(
                        R.color.textColorSecond));
                viewHolder.textView
                        .setBackgroundResource(R.drawable.back_trans_rec_border);
            }
            viewHolder.textView.setText(dilivers.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    class PaymentAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return payments.size();
        }

        @Override
        public Object getItem(int position) {
            return payments.get(position);
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
                viewHolder.textView = (TextView) convertView
                        .findViewById(R.id.diliver_payment_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (payment.getType() == payments.get(position).getType()) {
                viewHolder.textView.setTextColor(getResources().getColor(
                        R.color.blue));
                viewHolder.textView
                        .setBackgroundResource(R.drawable.back_trans_rec_border_blue);
            } else {
                viewHolder.textView.setTextColor(getResources().getColor(
                        R.color.textColorSecond));
                viewHolder.textView
                        .setBackgroundResource(R.drawable.back_trans_rec_border);
            }
            viewHolder.textView.setText(payments.get(position).getName());
            return convertView;
        }

        class ViewHolder {
            TextView textView;
        }
    }

    private void getStoreInfo() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("no", tejiaDetail.getSpNo()));
        post(API.API_STORE_SEND_FEE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            storePostInfo = new ModelStorePostInfo(
                                    object.optJSONObject("dataMap"));
                            storeInfoTextView
                                    .setText(getStorePostInfoString(storePostInfo));
                            setStorePostInfo();
                            countTotalMoney();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void addLocalGoodsOrder() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("serviceProvidID", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("customerName", addressFragment.getAddress().getPersonName()));
        requestParams.add(new RequestParam("customerPhone", addressFragment.getAddress().getPhone()));
        requestParams.add(new RequestParam("retailOrderPrice", totalMoney + ""));

        JSONArray data = new JSONArray();
        JSONArray deliveryInfo = new JSONArray();
        try {
            JSONObject deliveryInfoObject = new JSONObject();
            deliveryInfoObject.put("supplyId", tejiaDetail.getSpId());
            deliveryInfoObject.put("deliveryType", diliver.getName());
            switch (diliver.getType()) {
                case ModelDiliver.TYPE_HOME:
                    deliveryInfoObject
                            .put("address", addressFragment.getAddress()
                                    .getAreaAddress()
                                    + addressFragment.getAddress()
                                    .getDetailedAddress());
                    break;
                case ModelDiliver.TYPE_SELF:
                    deliveryInfoObject.put("address", Store.getStore()
                            .getStoreAddess());
                    break;
                default:
                    break;
            }
            deliveryInfoObject.put("paymentType", payment.getType());
            deliveryInfo.put(deliveryInfoObject);

            JSONObject dataObject = new JSONObject();
            dataObject.put("retailProductManagerID",
                    tejiaDetail.getProductId());
            dataObject.put("orderType", "1");
            dataObject.put("shopNum", goodsCount);
            dataObject.put("productPrice", tejiaDetail.getSalePrice());
            data.put(dataObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.add(new RequestParam("data", data.toString()));
        requestParams.add(new RequestParam("deliveryInfo", deliveryInfo.toString()));

        post(API.API_LOCAL_BUSINESS_GOODS_ORDER_ADD, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("下单成功");
                            JSONArray orderArray = object.optJSONArray("dataList");
                            if (orderArray != null) {
                                double payPrice = 0;
                                ArrayList<String> orderNumbers = new ArrayList<String>();
                                for (int i = 0; i < orderArray.length(); i++) {
                                    ModelLocalGoodsOrderResult orderResult = new ModelLocalGoodsOrderResult(
                                            orderArray.optJSONObject(i));
                                    if (orderResult.getPaymentType() == ModelPayment.TYPE_ONLINE) {
                                        orderNumbers.add(orderResult
                                                .getOrdernumber());
                                        payPrice += orderResult.getOrderPrice();
                                    }
                                }
                                if (orderNumbers.size() > 0) {
                                    if (payPrice > 0) {
                                        payMoney(orderNumbers, payPrice,
                                                PayFragment.ORDER_TYPE_LP);
                                        getActivity().finish();
                                        return;
                                    }
                                }
                            }
                            showOrder(PayFragment.ORDER_TYPE_LP);
                            getActivity().finish();
                            return;
                        } else {
                            ApplicationTool.showToast(object.optString("message"));
                            return;
                        }
                    } catch (JSONException e) {
                        ApplicationTool.showToast("下单失败");
                        e.printStackTrace();
                        return;
                    }
                }
                ApplicationTool.showToast("下单失败");
            }
        });
    }

}
