package yitgogo.consumer.order.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.controller.shoppingcart.DataBaseHelper;
import com.smartown.controller.shoppingcart.ModelShoppingCart;
import com.smartown.controller.shoppingcart.ShoppingCartController;
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
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrderResult;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.Notify;

public class ShoppingCarPlatformBuyFragment extends BaseNotifyFragment {

    InnerListView carListView;
    FrameLayout addressLayout, paymentLayout;
    TextView totalPriceTextView, confirmButton;

    //购物车勾选的商品
    List<ModelShoppingCart> shoppingCarts;
    //商品价格
    HashMap<String, ModelListPrice> priceMap;
    //供货商
    List<String> providers;
    HashMap<String, Double> freightMap;
    //按供货商分组的商品
    HashMap<String, List<ModelShoppingCart>> shoppingCartByProvider;

    StringBuilder productNumbers = new StringBuilder();

    private CarAdapter carAdapter;
    double goodsMoney = 0;

    List<ModelOrderResult> orderResults;

    OrderConfirmPartAddressFragment addressFragment;
    OrderConfirmPartPaymentFragment paymentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm_order);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShoppingCarPlatformBuyFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ShoppingCarPlatformBuyFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initShoppingCart();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.platform_order_confirm_part_address, addressFragment)
                .replace(R.id.platform_order_confirm_part_payment, paymentFragment).commit();
    }

    private void init() {
        shoppingCarts = new ArrayList<>();
        priceMap = new HashMap<>();
        providers = new ArrayList<>();
        freightMap = new HashMap<>();
        shoppingCartByProvider = new HashMap<>();
        carAdapter = new CarAdapter();
        orderResults = new ArrayList<>();
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
        carListView = (InnerListView) contentView.findViewById(R.id.platform_order_confirm_products);
        addressLayout = (FrameLayout) contentView.findViewById(R.id.platform_order_confirm_part_address);
        paymentLayout = (FrameLayout) contentView.findViewById(R.id.platform_order_confirm_part_payment);
        totalPriceTextView = (TextView) contentView.findViewById(R.id.platform_order_confirm_total_money);
        confirmButton = (TextView) contentView.findViewById(R.id.platform_order_confirm);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        carListView.setAdapter(carAdapter);
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

    private void initShoppingCart() {
        shoppingCarts = ShoppingCartController.getInstance().getSelectedProducts(DataBaseHelper.tableCarPlatform);
        priceMap = new HashMap<>();
        providers = new ArrayList<>();
        freightMap = new HashMap<>();
        shoppingCartByProvider = new HashMap<>();
        carAdapter.notifyDataSetChanged();
        productNumbers = new StringBuilder();
        totalPriceTextView.setText("");
        if (shoppingCarts.size() > 0) {
            StringBuilder productIds = new StringBuilder();
            for (int i = 0; i < shoppingCarts.size(); i++) {
                try {
                    ModelProduct product = new ModelProduct(new JSONObject(shoppingCarts.get(i).getProductObject()));
                    if (i > 0) {
                        productIds.append(",");
                        productNumbers.append(",");
                    }
                    productIds.append(shoppingCarts.get(i).getProductId());
                    productNumbers.append(product.getNumber() + "-" + shoppingCarts.get(i).getBuyCount());

                    if (!providers.contains(shoppingCarts.get(i).getProviderId())) {
                        providers.add(shoppingCarts.get(i).getProviderId());
                    }

                    if (shoppingCartByProvider.containsKey(shoppingCarts.get(i).getProviderId())) {
                        shoppingCartByProvider.get(shoppingCarts.get(i).getProviderId()).add(shoppingCarts.get(i));
                    } else {
                        List<ModelShoppingCart> providerShoppingCarts = new ArrayList<>();
                        providerShoppingCarts.add(shoppingCarts.get(i));
                        shoppingCartByProvider.put(shoppingCarts.get(i).getProviderId(), providerShoppingCarts);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            new GetPriceList().execute(productIds.toString());
        } else {
            Notify.show("请勾选要购买的商品");
        }
    }

    private void countTotalMoney() {
        goodsMoney = 0;
        double freightMoney = 0;
        for (int i = 0; i < providers.size(); i++) {
            if (shoppingCartByProvider.containsKey(providers.get(i))) {
                List<ModelShoppingCart> shoppingCarts = shoppingCartByProvider.get(providers.get(i));
                for (int j = 0; j < shoppingCarts.size(); j++) {
                    if (priceMap.containsKey(shoppingCarts.get(j).getProductId())) {
                        double price = priceMap.get(shoppingCarts.get(j).getProductId()).getPrice();
                        int count = shoppingCarts.get(j).getBuyCount();
                        if (price > 0) {
                            goodsMoney += count * price;
                        }
                    }
                }
                if (freightMap.containsKey(providers.get(i))) {
                    freightMoney += freightMap.get(providers.get(i));
                }
            }
        }
        totalPriceTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(goodsMoney + freightMoney));
    }

    private void addOrder() {
        for (int i = 0; i < shoppingCarts.size(); i++) {
            if (priceMap.containsKey(shoppingCarts.get(i).getProductId())) {
                double price = priceMap.get(shoppingCarts.get(i).getProductId()).getPrice();
                if (price <= 0) {
                    errorProductInfo(shoppingCarts.get(i));
                    return;
                }
            } else {
                errorProductInfo(shoppingCarts.get(i));
                return;
            }
        }
        if (addressFragment.getAddress() == null) {
            Notify.show("收货人信息有误");
        } else {
            new AddOrder().execute();
        }
    }

    private void errorProductInfo(ModelShoppingCart shoppingCart) {
        try {
            ModelProduct product = new ModelProduct(new JSONObject(shoppingCart.getProductObject()));
            Notify.show("商品“" + product.getProductName() + "”信息有误，不能购买。");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class CarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return providers.size();
        }

        @Override
        public Object getItem(int position) {
            return providers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = new ViewHolder();
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_shopping_cart_platform_buy_provider, null);
                holder.providerNameTextView = (TextView) convertView.findViewById(R.id.cart_platform_provider_name);
                holder.freightTextView = (TextView) convertView.findViewById(R.id.cart_platform_provider_send);
                holder.productListView = (InnerListView) convertView.findViewById(R.id.cart_platform_provider_product);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (shoppingCartByProvider.containsKey(providers.get(position))) {
                if (!shoppingCartByProvider.get(providers.get(position)).isEmpty()) {
                    holder.providerNameTextView.setText(shoppingCartByProvider.get(providers.get(position)).get(0).getProviderName());
                } else {
                    holder.providerNameTextView.setText("");
                }
            } else {
                holder.providerNameTextView.setText("");
            }
            if (freightMap.containsKey(providers.get(position))) {
                holder.freightTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(freightMap.get(providers.get(position))));
            } else {
                holder.freightTextView.setText("");
            }
            holder.productListView.setAdapter(new CarProductAdapter(shoppingCartByProvider.get(providers.get(position))));
            return convertView;
        }

        class ViewHolder {
            TextView providerNameTextView, freightTextView;
            InnerListView productListView;
        }
    }

    class CarProductAdapter extends BaseAdapter {

        List<ModelShoppingCart> shoppingCarts = new ArrayList<>();

        public CarProductAdapter(List<ModelShoppingCart> shoppingCarts) {
            this.shoppingCarts = shoppingCarts;
        }

        @Override
        public int getCount() {
            return shoppingCarts.size();
        }

        @Override
        public Object getItem(int i) {
            return shoppingCarts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = new ViewHolder();
            if (view == null) {
                view = layoutInflater.inflate(R.layout.list_shopping_cart_platform_buy, null);
                holder.goodsImageView = (ImageView) view.findViewById(R.id.platform_car_buy_image);
                holder.goodsNameTextView = (TextView) view.findViewById(R.id.platform_car_buy_name);
                holder.goodsAttrTextView = (TextView) view.findViewById(R.id.platform_car_buy_attr);
                holder.goodsCountTextView = (TextView) view.findViewById(R.id.platform_car_buy_count);
                holder.goodsPriceTextView = (TextView) view.findViewById(R.id.platform_car_buy_price);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            try {
                ModelProduct product = new ModelProduct(new JSONObject(shoppingCarts.get(i).getProductObject()));
                ImageLoader.getInstance().displayImage(getSmallImageUrl(product.getImg()), holder.goodsImageView);
                holder.goodsNameTextView.setText(product.getProductName());
                holder.goodsAttrTextView.setText(product.getAttName());
                holder.goodsCountTextView.setText("数量:" + shoppingCarts.get(i).getBuyCount());
                if (priceMap.containsKey(shoppingCarts.get(i).getProductId())) {
                    double price = priceMap.get(shoppingCarts.get(i).getProductId()).getPrice();
                    if (price > 0) {
                        holder.goodsPriceTextView.setText("单价:" + Parameters.CONSTANT_RMB + decimalFormat.format(price));
                    } else {
                        holder.goodsPriceTextView.setText("商品价格异常");
                    }
                } else {
                    holder.goodsPriceTextView.setText("");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return view;
        }

        class ViewHolder {
            ImageView goodsImageView;
            TextView goodsNameTextView, goodsAttrTextView, goodsCountTextView, goodsPriceTextView;
        }

    }

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
            nameValuePairs.add(new BasicNameValuePair("totalMoney", decimalFormat.format(goodsMoney)));
            nameValuePairs.add(new BasicNameValuePair("sex", User.getUser().getSex()));
            nameValuePairs.add(new BasicNameValuePair("age", User.getUser().getAge()));
            nameValuePairs.add(new BasicNameValuePair("address", Store.getStore().getStoreArea()));
            nameValuePairs.add(new BasicNameValuePair("jmdId", Store.getStore().getStoreId()));
            nameValuePairs.add(new BasicNameValuePair("orderType", "0"));
            try {
                JSONArray dataArray = new JSONArray();
                for (int i = 0; i < shoppingCarts.size(); i++) {
                    ModelProduct product = new ModelProduct(new JSONObject(shoppingCarts.get(i).getProductObject()));
                    if (priceMap.containsKey(product.getId())) {
                        JSONObject object = new JSONObject();
                        object.put("productIds", product.getId());
                        object.put("shopNum", shoppingCarts.get(i).getBuyCount());
                        object.put("price", product.getPrice());
                        object.put("isIntegralMall", 0);
                        dataArray.put(object);
                    }
                }
                nameValuePairs.add(new BasicNameValuePair("data", dataArray.toString()));

                JSONArray freightArray = new JSONArray();
                for (int i = 0; i < providers.size(); i++) {
                    if (freightMap.containsKey(providers.get(i))) {
                        JSONObject freightObject = new JSONObject();
                        freightObject.put("supplyId", providers.get(i));
                        freightObject.put("freight", freightMap.get(providers.get(i)));
                        freightArray.put(freightObject);
                    }
                }
                nameValuePairs.add(new BasicNameValuePair("freights", freightArray.toString()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return netUtil.postWithoutCookie(API.API_ORDER_ADD_CENTER, nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        ShoppingCartController.getInstance().removeSelectedProducts(DataBaseHelper.tableCarPlatform);
                        Notify.show("下单成功");
                        if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
                            payMoney(object.optJSONArray("object"));
                            getActivity().finish();
                            return;
                        }
                        showOrder(PayFragment.ORDER_TYPE_YY);
                        getActivity().finish();
                        return;
                    }
                    Notify.show(object.optString("message"));
                    return;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Notify.show("下单失败");
        }
    }

    class GetPriceList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... value) {
            List<NameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new BasicNameValuePair("jmdId", Store.getStore().getStoreId()));
            valuePairs.add(new BasicNameValuePair("productId", value[0]));
            return netUtil.postWithoutCookie(API.API_PRICE_LIST, valuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray priceArray = object.optJSONArray("dataList");
                        if (priceArray != null) {
                            for (int i = 0; i < priceArray.length(); i++) {
                                ModelListPrice priceList = new ModelListPrice(priceArray.getJSONObject(i));
                                priceMap.put(priceList.getProductId(), priceList);
                            }
                            carAdapter.notifyDataSetChanged();
                            countTotalMoney();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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
            valuePairs.add(new BasicNameValuePair("productNumber", productNumbers.toString()));
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
                            carAdapter.notifyDataSetChanged();
                            countTotalMoney();
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
