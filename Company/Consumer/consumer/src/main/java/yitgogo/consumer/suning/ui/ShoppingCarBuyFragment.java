package yitgogo.consumer.suning.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartAddressFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartPaymentFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.suning.model.ModelProductPrice;
import yitgogo.consumer.suning.model.ModelSuningCar;
import yitgogo.consumer.suning.model.ModelSuningOrderResult;
import yitgogo.consumer.suning.model.SuningCarController;
import yitgogo.consumer.suning.model.SuningManager;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerListView;


public class ShoppingCarBuyFragment extends BaseNetworkFragment {

    InnerListView productListView;
    TextView totalMoneyTextView, confirmButton;
    List<ModelSuningCar> suningCars = new ArrayList<>();
    HashMap<String, ModelProductPrice> priceHashMap = new HashMap<>();

    ProductAdapter productAdapter;
    double goodsMoney = 0;

    OrderConfirmPartAddressFragment addressFragment;
    OrderConfirmPartPaymentFragment paymentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suning_order_add);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShoppingCarBuyFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ShoppingCarBuyFragment.class.getName());
    }

    @Override
    protected void init() {
        initPrice();
        suningCars = SuningCarController.getSelectedCars();
        productAdapter = new ProductAdapter();
        addressFragment = new OrderConfirmPartAddressFragment();
        paymentFragment = new OrderConfirmPartPaymentFragment(true, true, false);
    }

    private void initPrice() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("price")) {
                String result = bundle.getString("price");
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optBoolean("isSuccess")) {
                            JSONArray array = object.optJSONArray("result");
                            if (array != null) {
                                for (int j = 0; j < array.length(); j++) {
                                    ModelProductPrice productPrice = new
                                            ModelProductPrice(array.optJSONObject(j));
                                    priceHashMap.put(productPrice.getSkuId(), productPrice);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void findViews() {
        productListView = (InnerListView) contentView.findViewById(R.id.order_add_products);
        totalMoneyTextView = (TextView) contentView.findViewById(R.id.order_add_total_money);
        confirmButton = (TextView) contentView.findViewById(R.id.order_add_confirm);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        productListView.setAdapter(productAdapter);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.order_add_address,
                        addressFragment)
                .replace(R.id.order_add_payment,
                        paymentFragment).commit();
        countTotalPrice();
    }

    @Override
    protected void registerViews() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrder();
            }
        });
    }

    private void addOrder() {
        if (addressFragment.getAddress() == null) {
            ApplicationTool.showToast("收货人信息有误");
        } else {
            if (goodsMoney > 0) {
                buy();
            } else {
                ApplicationTool.showToast("有商品信息有误，暂不能购买");
            }
        }
    }

    private void countTotalPrice() {
        goodsMoney = 0;
        double sendMoney = 0;
        for (int i = 0; i < suningCars.size(); i++) {
            if (suningCars.get(i).isSelected()) {
                long count = suningCars.get(i).getProductCount();
                if (priceHashMap.containsKey(suningCars.get(i).getProductDetail().getSku())) {
                    double price = priceHashMap.get(suningCars.get(i).getProductDetail().getSku()).getPrice();
                    if (price > 0) {
                        goodsMoney += count * price;
                    }
                }
            }
        }
        if (goodsMoney > 0 & goodsMoney < 69) {
            sendMoney = 5;
        }
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(goodsMoney + sendMoney));
    }

    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return suningCars.size();
        }

        @Override
        public Object getItem(int position) {
            return suningCars.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_car_buy,
                        null);
                holder = new ViewHolder();
                holder.goodNameText = (TextView) convertView
                        .findViewById(R.id.list_car_title);
                holder.goodsImageView = (ImageView) convertView
                        .findViewById(R.id.list_car_image);
                holder.goodsPriceText = (TextView) convertView
                        .findViewById(R.id.list_car_price);
                holder.guigeText = (TextView) convertView
                        .findViewById(R.id.list_car_guige);
                holder.stateText = (TextView) convertView
                        .findViewById(R.id.list_car_state);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(suningCars.get(position).getProductDetail().getImage(),
                    holder.goodsImageView);
            holder.goodNameText.setText(suningCars.get(position).getProductDetail().getName());
            if (priceHashMap.containsKey(suningCars.get(position).getProductDetail().getSku())) {
                holder.goodsPriceText.setText(Parameters.CONSTANT_RMB
                        + decimalFormat.format(priceHashMap.get(suningCars.get(position).getProductDetail().getSku()).getPrice()));
            }
            holder.stateText.setText("×" + suningCars.get(position).getProductCount());
            return convertView;
        }

        class ViewHolder {
            ImageView goodsImageView;
            TextView goodNameText, goodsPriceText, guigeText, stateText;
        }
    }

    private void buy() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("menberAccount", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("name", addressFragment.getAddress().getPersonName()));
        requestParams.add(new RequestParam("mobile", addressFragment.getAddress().getPhone()));
        requestParams.add(new RequestParam("address", addressFragment.getAddress().getDetailedAddress()));
        requestParams.add(new RequestParam("spId", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("amount", goodsMoney + ""));
        requestParams.add(new RequestParam("provinceId", SuningManager.getSuningAreas().getProvince().getCode()));
        requestParams.add(new RequestParam("cityId", SuningManager.getSuningAreas().getCity().getCode()));
        requestParams.add(new RequestParam("countyId", SuningManager.getSuningAreas().getDistrict().getCode()));
        requestParams.add(new RequestParam("townId", SuningManager.getSuningAreas().getTown().getCode()));
        JSONArray skuArray = new JSONArray();
        for (int i = 0; i < suningCars.size(); i++) {
            if (priceHashMap.containsKey(suningCars.get(i).getProductDetail().getSku())) {
                try {
                    JSONObject skuObject = new JSONObject();
                    skuObject.put("number", suningCars.get(i).getProductDetail().getSku());
                    skuObject.put("num", suningCars.get(i).getProductCount());
                    skuObject.put("price", priceHashMap.get(suningCars.get(i).getProductDetail().getSku()).getPrice());
                    skuObject.put("name", suningCars.get(i).getProductDetail().getName());
                    skuObject.put("attr", suningCars.get(i).getProductDetail().getModel());
                    skuArray.put(skuObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        requestParams.add(new RequestParam("sku", skuArray.toString()));
        post(API.API_SUNING_ORDER_ADD, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equals("SUCCESS")) {
                            ApplicationTool.showToast("下单成功");
                            SuningCarController.deleteSelectedCars();
                            ModelSuningOrderResult orderResult = new ModelSuningOrderResult(object.optJSONObject("dataMap"));
                            if (orderResult.getZongjine() > 0) {
                                if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
                                    payMoney(orderResult.getOrderNumber(), orderResult.getZongjine() + orderResult.getFreight(), PayFragment.ORDER_TYPE_SN);
                                    getActivity().finish();
                                    return;
                                }
                            }
                            showOrder(PayFragment.ORDER_TYPE_SN);
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

}
