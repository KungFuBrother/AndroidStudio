package yitgogo.consumer.order.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrderResult;
import yitgogo.consumer.product.model.ModelCar;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerListView;

public class PlatformOrderConfirmFragment extends BaseNetworkFragment {

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

    @Override
    protected void init() {
        modelCars = new ArrayList<>();
        priceMap = new HashMap<>();
        productAdapter = new ProductAdapter();
        orderResults = new ArrayList<>();
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
                getPriceList(stringBuilder.toString());
            } else {
                countTotalPrice();
                productAdapter.notifyDataSetChanged();
            }
        } else {
            missionNodata();
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
                        ApplicationTool.showToast("商品库存不足，无法下单");
                        return;
                    }
                } else {
                    ApplicationTool.showToast("有商品信息错误，无法下单");
                    return;
                }
            } else {
                ApplicationTool.showToast("有商品信息错误，无法下单");
                return;
            }
        }
        if (addressFragment.getAddress() == null) {
            ApplicationTool.showToast("收货人信息有误");
        } else {
            buy();
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

    private void buy() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("userNumber", User
                .getUser().getUseraccount()));
        requestParams.add(new RequestParam("customerName",
                addressFragment.getAddress().getPersonName()));
        requestParams.add(new RequestParam("phone", addressFragment
                .getAddress().getPhone()));
        requestParams
                .add(new RequestParam("shippingaddress",
                        addressFragment.getAddress().getAreaAddress()
                                + addressFragment.getAddress()
                                .getDetailedAddress()));
        requestParams.add(new RequestParam("totalMoney", totalPrice
                + ""));
        requestParams.add(new RequestParam("sex", User.getUser()
                .getSex()));
        requestParams.add(new RequestParam("age", User.getUser()
                .getAge()));
        requestParams.add(new RequestParam("address", Store
                .getStore().getStoreArea()));
        requestParams.add(new RequestParam("jmdId", Store.getStore()
                .getStoreId()));
        requestParams.add(new RequestParam("orderType", "0"));
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
        requestParams.add(new RequestParam("data", orderArray
                .toString()));
        post(API.API_ORDER_ADD_CENTER, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            ApplicationTool.showToast("下单成功");
                            if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
                                payMoney(object.optJSONArray("object"));
                                getActivity().finish();
                                return;
                            }
                            showOrder(PayFragment.ORDER_TYPE_YY);
                            getActivity().finish();
                            return;
                        }
                        ApplicationTool.showToast(object.optString("message"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("下单失败");
            }
        });
    }

    private void getPriceList(String productId) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("jmdId", Store.getStore()
                .getStoreId()));
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_PRICE_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
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
        });
    }

}
