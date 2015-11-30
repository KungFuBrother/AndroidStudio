package yitgogo.consumer.order.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrderResult;
import yitgogo.consumer.product.model.ModelCar;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.Notify;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

public class PlatformOrderConfirmFragment extends BaseNotifyFragment {

    InnerListView productListView;
    FrameLayout addressLayout, paymentLayout;
    TextView totalPriceTextView, confirmButton;

    List<ModelCar> modelCars;
    HashMap<String, ModelListPrice> priceMap;
    ProductAdapter productAdapter;
    double totalPrice = 0;

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
        MobclickAgent.onPageStart(PlatformOrderConfirmFragment.class.getName());
        getOrderProduct();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PlatformOrderConfirmFragment.class.getName());
    }

    private void init() {
        modelCars = new ArrayList<ModelCar>();
        priceMap = new HashMap<String, ModelListPrice>();
        productAdapter = new ProductAdapter();
        orderResults = new ArrayList<ModelOrderResult>();
        addressFragment = new OrderConfirmPartAddressFragment();
        paymentFragment = new OrderConfirmPartPaymentFragment(true, false);
    }

    protected void findViews() {
        productListView = (InnerListView) contentView
                .findViewById(R.id.platform_order_confirm_products);
        addressLayout = (FrameLayout) contentView
                .findViewById(R.id.platform_order_confirm_part_address);
        paymentLayout = (FrameLayout) contentView
                .findViewById(R.id.platform_order_confirm_part_payment);
        totalPriceTextView = (TextView) contentView
                .findViewById(R.id.platform_order_confirm_total_money);
        confirmButton = (TextView) contentView
                .findViewById(R.id.platform_order_confirm);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.platform_order_confirm_part_address,
                        addressFragment)
                .replace(R.id.platform_order_confirm_part_payment,
                        paymentFragment).commit();
        productListView.setAdapter(productAdapter);
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

    private void getOrderProduct() {
        modelCars.clear();
        productAdapter.notifyDataSetChanged();
        totalPriceTextView.setText("");
        try {
            JSONArray orderArray = new JSONArray(Content.getStringContent(
                    Parameters.CACHE_KEY_ORDER_PRODUCT, "[]"));
            for (int i = 0; i < orderArray.length(); i++) {
                modelCars.add(new ModelCar(orderArray.getJSONObject(i)));
            }
            productAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (modelCars.size() > 0) {
            if (priceMap.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < modelCars.size(); i++) {
                    if (i > 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(modelCars.get(i).getProduct().getId());
                }
                new GetPriceList().execute(stringBuilder.toString());
            } else {
                countTotalPrice();
                productAdapter.notifyDataSetChanged();
            }
        } else {
            loadingEmpty("购物车还没有添加商品");
        }
    }

    private void countTotalPrice() {
        totalPrice = 0;
        for (int i = 0; i < modelCars.size(); i++) {
            if (modelCars.get(i).isSelected()) {
                ModelProduct product = modelCars.get(i).getProduct();
                if (priceMap.containsKey(product.getId())) {
                    double price = priceMap.get(product.getId()).getPrice();
                    long count = modelCars.get(i).getProductCount();
                    if (price > 0) {
                        totalPrice += count * price;
                    }
                }
            }
        }
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalPrice));
    }

    private void addOrder() {
        for (int i = 0; i < modelCars.size(); i++) {
            ModelProduct product = modelCars.get(i).getProduct();
            if (priceMap.containsKey(product.getId())) {
                ModelListPrice price = priceMap.get(product.getId());
                if (price.getPrice() > 0) {
                    if (price.getNum() < modelCars.get(i).getProductCount()) {
                        Notify.show("商品库存不足，无法下单");
                        return;
                    }
                } else {
                    Notify.show("有商品信息错误，无法下单");
                    return;
                }
            } else {
                Notify.show("有商品信息错误，无法下单");
                return;
            }
        }
        if (addressFragment.getAddress() == null) {
            Notify.show("收货人信息有误");
        } else {
            new AddOrder().execute();
        }
    }

    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return modelCars.size();
        }

        @Override
        public Object getItem(int position) {
            return modelCars.get(position);
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
                        R.layout.list_order_product, null);
                viewHolder = new ViewHolder();
                viewHolder.attr = (TextView) convertView
                        .findViewById(R.id.list_product_attr);
                viewHolder.price = (TextView) convertView
                        .findViewById(R.id.list_product_price);
                viewHolder.count = (TextView) convertView
                        .findViewById(R.id.list_product_count);
                viewHolder.name = (TextView) convertView
                        .findViewById(R.id.list_product_name);
                viewHolder.img = (ImageView) convertView
                        .findViewById(R.id.list_product_img);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ModelCar modelCar = modelCars.get(position);
            ModelProduct product = modelCar.getProduct();
            ImageLoader.getInstance().displayImage(product.getImg(),
                    viewHolder.img);
            viewHolder.count.setText(" × " + modelCar.getProductCount());
            viewHolder.name.setText(product.getProductName());
            viewHolder.attr.setText(product.getAttName());
            if (priceMap.containsKey(product.getId())) {
                ModelListPrice price = priceMap.get(product.getId());
                viewHolder.price.setText("¥"
                        + decimalFormat.format(price.getPrice()));
                if (price.getNum() <= 0) {
                    viewHolder.price.setText("无货");
                }
            }
            return convertView;
        }

        class ViewHolder {
            ImageView img;
            TextView name, price, count, attr;
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
            nameValuePairs.add(new BasicNameValuePair("userNumber", User
                    .getUser().getUseraccount()));
            nameValuePairs.add(new BasicNameValuePair("customerName",
                    addressFragment.getAddress().getPersonName()));
            nameValuePairs.add(new BasicNameValuePair("phone", addressFragment
                    .getAddress().getPhone()));
            nameValuePairs
                    .add(new BasicNameValuePair("shippingaddress",
                            addressFragment.getAddress().getAreaAddress()
                                    + addressFragment.getAddress()
                                    .getDetailedAddress()));
            nameValuePairs.add(new BasicNameValuePair("totalMoney", totalPrice
                    + ""));
            nameValuePairs.add(new BasicNameValuePair("sex", User.getUser()
                    .getSex()));
            nameValuePairs.add(new BasicNameValuePair("age", User.getUser()
                    .getAge()));
            nameValuePairs.add(new BasicNameValuePair("address", Store
                    .getStore().getStoreArea()));
            nameValuePairs.add(new BasicNameValuePair("jmdId", Store.getStore()
                    .getStoreId()));
            nameValuePairs.add(new BasicNameValuePair("orderType", "0"));
            JSONArray orderArray = new JSONArray();
            try {
                for (int i = 0; i < modelCars.size(); i++) {
                    ModelProduct product = modelCars.get(i).getProduct();
                    if (priceMap.containsKey(product.getId())) {
                        JSONObject object = new JSONObject();
                        object.put("productIds", product.getId());
                        object.put("shopNum", modelCars.get(i)
                                .getProductCount());
                        object.put("price", product.getPrice());
                        object.put("isIntegralMall", 0);
                        orderArray.put(object);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameValuePairs.add(new BasicNameValuePair("data", orderArray
                    .toString()));
            return netUtil.postWithoutCookie(API.API_ORDER_ADD_CENTER,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
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
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("jmdId", Store.getStore()
                    .getStoreId()));
            valuePairs.add(new BasicNameValuePair("productId", value[0]));
            return netUtil.postWithoutCookie(API.API_PRICE_LIST, valuePairs,
                    false, false);
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
                                ModelListPrice priceList = new ModelListPrice(
                                        priceArray.getJSONObject(i));
                                priceMap.put(priceList.getProductId(),
                                        priceList);
                            }
                            countTotalPrice();
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
