package yitgogo.consumer.local.ui;

import android.app.Dialog;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.model.ModelLocalSaleMiaoshaDetail;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelDiliver;
import yitgogo.consumer.order.model.ModelLocalGoodsOrderResult;
import yitgogo.consumer.order.model.ModelPayment;
import yitgogo.consumer.order.model.ModelStorePostInfo;
import yitgogo.consumer.order.ui.OrderConfirmPartAddressFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerGridView;
import yitgogo.consumer.view.Notify;

public class LocalGoodsSaleMiaoshaBuyFragment extends BaseNotifyFragment {

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

    ModelLocalSaleMiaoshaDetail miaoshaDetail = new ModelLocalSaleMiaoshaDetail();
    OrderConfirmPartAddressFragment addressFragment = new OrderConfirmPartAddressFragment();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetStoreInfo().execute();
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
        MobclickAgent.onPageEnd(LocalGoodsSaleMiaoshaBuyFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalGoodsSaleMiaoshaBuyFragment.class.getName());
    }

    private void init() {
        measureScreen();
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("object")) {
                try {
                    miaoshaDetail = new ModelLocalSaleMiaoshaDetail(new JSONObject(bundle.getString("object")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        dilivers = new ArrayList<>();
        payments = new ArrayList<>();
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
        String imageUrl = "";
        if (miaoshaDetail.getImages().size() > 0) {
            imageUrl = miaoshaDetail.getImages().get(0).getImgName();
        }
        ImageLoader.getInstance().displayImage(getSmallImageUrl(imageUrl),
                goodsImageView);
        goodsNameTextView.setText(miaoshaDetail.getProductName());
        goodsAttrTextView.setText(miaoshaDetail.getAttribute());
        goodsPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(miaoshaDetail.getSeckillPrice()));
        setDiliverPayType();
    }

    @Override
    protected void registerViews() {
        countAddButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                Notify.show("秒杀产品一次只能购买一件");
            }
        });
        countDeleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View paramView) {
                if (goodsCount > 1) {
                    goodsCount--;
                }
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
        double goodsMoney = goodsCount * miaoshaDetail.getSeckillPrice();
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
            Notify.show("商品信息有误");
        } else if (addressFragment.getAddress() == null) {
            Notify.show("收货人地址有误");
        } else {
            new AddLocalGoodsOrder().execute();
        }
    }

    /**
     * @author Tiger
     * @Json {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
     * "totalCount"
     * :1,"dataMap":{"whetherToUseStockSystem":false,"hawManyPackages"
     * :50.0
     * ,"autoPurchase":false,"supportForDelivery":true,"postage":10.0}
     * ,"object":null}
     */
    class GetStoreInfo extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("no", miaoshaDetail
                    .getSpNo()));
            return netUtil.postWithoutCookie(API.API_STORE_SEND_FEE,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
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
                    LayoutParams.MATCH_PARENT, screenWidth));
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

    /**
     * 添加本地产品订单
     *
     * @author Tiger
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{
     * "servicetelephone"
     * :"028-12345678","deliveryType":"送货上门","paymentType"
     * :"1","orderDate"
     * :"2015-10-16","servicename":"易田测试加盟店四","productInfo":
     * "[{\"spname\":\"产品组-测试产品\",\"price\":\"33.0\",\"Amount\":\"66.0\",\"num\":\"2\"}]"
     * ,"ordernumber":"YT5966059962","postagePrice":"满50.0包邮",
     * "orderPrice"
     * :"66.0","servicePhone":"13228116626"}],"totalCount":1,
     * "dataMap":{},"object":null}
     */
    class AddLocalGoodsOrder extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading("下单中,请稍候...");
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("serviceProvidID", Store
                    .getStore().getStoreId()));
            valuePairs.add(new BasicNameValuePair("memberAccount", User
                    .getUser().getUseraccount()));
            valuePairs.add(new BasicNameValuePair("customerName",
                    addressFragment.getAddress().getPersonName()));
            valuePairs.add(new BasicNameValuePair("customerPhone",
                    addressFragment.getAddress().getPhone()));
            valuePairs.add(new BasicNameValuePair("retailOrderPrice",
                    totalMoney + ""));

            JSONArray data = new JSONArray();
            JSONArray deliveryInfo = new JSONArray();
            try {
                JSONObject deliveryInfoObject = new JSONObject();
                deliveryInfoObject.put("supplyId", miaoshaDetail.getSpId());
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
                dataObject.put("retailProductManagerID", miaoshaDetail.getId());
                dataObject.put("orderType", "1");
                dataObject.put("shopNum", goodsCount);
                dataObject.put("productPrice", miaoshaDetail.getSeckillPrice());
                data.put(dataObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            valuePairs.add(new BasicNameValuePair("data", data.toString()));
            valuePairs.add(new BasicNameValuePair("deliveryInfo", deliveryInfo
                    .toString()));

            return netUtil.postWithoutCookie(
                    API.API_LOCAL_BUSINESS_GOODS_ORDER_ADD, valuePairs, false,
                    false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        Notify.show("下单成功");
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
                        hideLoading();
                        Notify.show(object.optString("message"));
                        return;
                    }
                } catch (JSONException e) {
                    hideLoading();
                    Notify.show("下单失败");
                    e.printStackTrace();
                    return;
                }
            }
            hideLoading();
            Notify.show("下单失败");
        }
    }

}
