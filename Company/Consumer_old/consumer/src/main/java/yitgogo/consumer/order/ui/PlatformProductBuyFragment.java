package yitgogo.consumer.order.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;

public class PlatformProductBuyFragment extends BaseNotifyFragment {

    ImageView imageView;
    TextView providerTextView, nameTextView, attrTextView, countTextView, priceTextView, freightTextView;

    FrameLayout addressLayout, paymentLayout;
    TextView totalMoneyTextView, confirmButton;

    String supplierId = "";
    String supplierName = "";
    String productId = "";
    String productNumber = "";
    String productAttr = "";
    String name = "";
    String image = "";
    int isIntegralMall = 0;
    double price = 0;
    int buyCount = 0;

    HashMap<String, Double> freightMap;

    OrderConfirmPartAddressFragment addressFragment;
    OrderConfirmPartPaymentFragment paymentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm_order_sale);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PlatformProductBuyFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PlatformProductBuyFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(R.id.platform_product_buy_address, addressFragment)
                .replace(R.id.platform_product_buy_payment, paymentFragment)
                .commit();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("supplierId")) {
                supplierId = bundle.getString("supplierId");
            }
            if (bundle.containsKey("supplierName")) {
                supplierName = bundle.getString("supplierName");
            }
            if (bundle.containsKey("productId")) {
                productId = bundle.getString("productId");
            }
            if (bundle.containsKey("productNumber")) {
                productNumber = bundle.getString("productNumber");
            }
            if (bundle.containsKey("name")) {
                name = bundle.getString("name");
            }
            if (bundle.containsKey("productAttr")) {
                productAttr = bundle.getString("productAttr");
            }
            if (bundle.containsKey("image")) {
                image = bundle.getString("image");
            }
            if (bundle.containsKey("isIntegralMall")) {
                isIntegralMall = bundle.getInt("isIntegralMall");
            }
            if (bundle.containsKey("price")) {
                price = bundle.getDouble("price");
            }
            if (bundle.containsKey("buyCount")) {
                buyCount = bundle.getInt("buyCount");
            }
        }
        freightMap = new HashMap<>();
        addressFragment = new OrderConfirmPartAddressFragment();
        paymentFragment = new OrderConfirmPartPaymentFragment(true, false);
        addressFragment.setOnSetAddressListener(new OrderConfirmPartAddressFragment.OnSetAddressListener() {
            @Override
            public void onSetAddress() {
                new GetFreight().execute();
            }
        });
    }

    protected void findViews() {
        imageView = (ImageView) contentView.findViewById(R.id.platform_product_buy_image);
        providerTextView = (TextView) contentView.findViewById(R.id.platform_product_buy_provider);
        nameTextView = (TextView) contentView.findViewById(R.id.platform_product_buy_name);
        attrTextView = (TextView) contentView.findViewById(R.id.platform_product_buy_attr);
        countTextView = (TextView) contentView.findViewById(R.id.platform_product_buy_count);
        priceTextView = (TextView) contentView.findViewById(R.id.platform_product_buy_price);
        freightTextView = (TextView) contentView.findViewById(R.id.platform_product_buy_freight);
        addressLayout = (FrameLayout) contentView.findViewById(R.id.platform_product_buy_address);
        paymentLayout = (FrameLayout) contentView.findViewById(R.id.platform_product_buy_payment);
        totalMoneyTextView = (TextView) contentView.findViewById(R.id.platform_product_buy_money);
        confirmButton = (TextView) contentView.findViewById(R.id.platform_product_buy_ok);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        ImageLoader.getInstance().displayImage(getSmallImageUrl(image), imageView);
        providerTextView.setText(supplierName);
        nameTextView.setText(name);
        attrTextView.setText(productAttr);
        priceTextView.setText("单价:" + Parameters.CONSTANT_RMB + decimalFormat.format(price));
        countTextView.setText("数量:" + String.valueOf(buyCount));
    }

    @Override
    protected void registerViews() {
        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addOrder();
            }
        });
    }

    private void addOrder() {
        if (buyCount * price > 0) {
            if (freightMap.containsKey(supplierId)) {
                if (addressFragment.getAddress() == null) {
                    Notify.show("收货人信息有误");
                } else {
                    new AddOrder().execute();
                }
            } else {
                Notify.show("查询运费失败，不能购买");
            }
        } else {
            Notify.show("商品信息有误，不能购买");
        }
    }

    /**
     * 促销产品下单
     *
     * @author Tiger
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[{
     * "zhekouhou"
     * :"34.0","zongzhekou":"0.0","fuwuZuoji":"028-2356895623"
     * ,"zongjine":"34.0","productInfo":
     * "[{\"spname\":\"韵思家具 法式田园抽屉储物六斗柜 欧式复古白色五斗柜 4斗柜 KSDG01\",\"price\":\"34.0\",\"Amount\":\"34.0\",\"num\":\"1\"}]"
     * ,"ordernumber":"YT5431669380","totalIntegral":"0","fuwushang":
     * "测试运营中心一"
     * ,"shijian":"2015-07-31","fuwuPhone":"15878978945"}],"totalCount"
     * :1,"dataMap":{},"object":null}
     */
    class AddOrder extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("userNumber", User.getUser().getUseraccount()));
            nameValuePairs.add(new BasicNameValuePair("customerName", addressFragment.getAddress().getPersonName()));
            nameValuePairs.add(new BasicNameValuePair("phone", addressFragment.getAddress().getPhone()));
            nameValuePairs.add(new BasicNameValuePair("shippingaddress", addressFragment.getAddress().getAreaAddress() + addressFragment.getAddress().getDetailedAddress()));
            nameValuePairs.add(new BasicNameValuePair("totalMoney", decimalFormat.format(buyCount * price)));
            nameValuePairs.add(new BasicNameValuePair("sex", User.getUser().getSex()));
            nameValuePairs.add(new BasicNameValuePair("age", User.getUser().getAge()));
            nameValuePairs.add(new BasicNameValuePair("address", Store.getStore().getStoreArea()));
            nameValuePairs.add(new BasicNameValuePair("jmdId", Store.getStore().getStoreId()));
            nameValuePairs.add(new BasicNameValuePair("orderType", "0"));
            try {
                JSONArray dataArray = new JSONArray();
                JSONObject object = new JSONObject();
                object.put("productIds", productId);
                object.put("shopNum", buyCount);
                object.put("price", price);
                object.put("isIntegralMall", isIntegralMall);
                dataArray.put(object);
                nameValuePairs.add(new BasicNameValuePair("data", dataArray.toString()));

                JSONArray freightArray = new JSONArray();
                JSONObject freightObject = new JSONObject();
                freightObject.put("supplyId", supplierId);
                freightObject.put("freight", freightMap.get(supplierId));
                freightArray.put(freightObject);
                nameValuePairs.add(new BasicNameValuePair("freights", freightArray.toString()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return netUtil.postWithoutCookie(API.API_ORDER_ADD_CENTER,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        Toast.makeText(getActivity(), "下单成功",
                                Toast.LENGTH_SHORT).show();
                        if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
                            payMoney(object.optJSONArray("object"));
                            getActivity().finish();
                            return;
                        }
                        showOrder(PayFragment.ORDER_TYPE_YY);
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

    class GetFreight extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            freightMap.clear();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new BasicNameValuePair("productNumber", productNumber + "-" + buyCount));
            valuePairs.add(new BasicNameValuePair("areaid", addressFragment.getAddress().getAreaId()));
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
                            if (freightMap.containsKey(supplierId)) {
                                freightTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(freightMap.get(supplierId)));
                                totalMoneyTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format((buyCount * price) + freightMap.get(supplierId)));
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

}
