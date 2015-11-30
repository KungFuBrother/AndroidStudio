package yitgogo.smart.product.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import smartown.controller.shoppingcart.DataBaseHelper;
import smartown.controller.shoppingcart.ModelShoppingCart;
import smartown.controller.shoppingcart.ShoppingCartController;
import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.ModelListPrice;
import yitgogo.smart.model.ModelMachineArea;
import yitgogo.smart.order.model.User;
import yitgogo.smart.product.model.ModelProduct;
import yitgogo.smart.task.OrderTask;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerListView;
import yitgogo.smart.view.Notify;

public class ShoppingCarPlatformBuyFragment extends BaseNotifyFragment {
    // 购物车部分
    ListView carList;

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

    CarAdapter carAdapter;

    EditText userPhoneEditText, userNameEditText, userAddressEditText;
    TextView userAreaTextView;

    TextView totalPriceTextView, buyButton;
    double goodsMoney = 0;
    User user = new User();
    ModelMachineArea machineArea = new ModelMachineArea();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_platform_car_buy);
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
        getArea();
    }

    private void init() {
        shoppingCarts = new ArrayList<>();
        priceMap = new HashMap<>();
        providers = new ArrayList<>();
        freightMap = new HashMap<>();
        shoppingCartByProvider = new HashMap<>();
        carAdapter = new CarAdapter();
    }

    protected void findViews() {
        carList = (ListView) contentView.findViewById(R.id.car_list);
        totalPriceTextView = (TextView) contentView.findViewById(R.id.car_total);

        userNameEditText = (EditText) contentView.findViewById(R.id.order_user_name);
        userPhoneEditText = (EditText) contentView.findViewById(R.id.order_user_phone);
        userAreaTextView = (TextView) contentView.findViewById(R.id.order_user_area);
        userAddressEditText = (EditText) contentView.findViewById(R.id.order_user_address);
        buyButton = (TextView) contentView.findViewById(R.id.order_user_confirm);

        initViews();
        registerViews();
    }

    protected void initViews() {
        carList.setAdapter(carAdapter);
    }

    @Override
    protected void registerViews() {
        buyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addOrder();
            }
        });
        userPhoneEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isPhoneNumber(userPhoneEditText.getText().toString().trim())) {
                    getUserInfo(true);
                } else {
                    user = new User();
                    showUserInfo();
                }
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
            getProductPrice(productIds.toString());
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
        if (goodsMoney > 0) {
            if (!isPhoneNumber(userPhoneEditText.getText().toString().trim())) {
                Notify.show("请填写正确的收货人电话");
            } else if (userNameEditText.length() == 0) {
                Notify.show("请填写收货人姓名");
            } else if (userAddressEditText.length() == 0) {
                Notify.show("请填写收货地址");
            } else {
                if (user.isLogin()) {
                    buyProduct();
                } else {
                    registerUser();
                }
            }
        }
    }

    private void errorProductInfo(ModelShoppingCart shoppingCart) {
        try {
            ModelProduct product = new ModelProduct(new JSONObject(shoppingCart.getProductObject()));
            Notify.show("商品“" + product.getProductName() + "”价格有误，不能购买。");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showUserInfo() {
        if (user != null) {
            userNameEditText.setText(user.getRealname());
            userAddressEditText.setText(user.getAddress());
        }
    }

    private void registerUser() {
        OrderTask.registerUser(getActivity(), userPhoneEditText.getText().toString(), userNameEditText.getText().toString(),
                userAddressEditText.getText().toString(),
                new OnNetworkListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideLoading();
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        if (!TextUtils.isEmpty(message.getResult())) {
                            JSONObject object;
                            try {
                                object = new JSONObject(message.getResult());
                                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                                    getUserInfo(false);
                                    return;
                                }
                                Notify.show("用户注册失败，下单失败，请重试！");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void getUserInfo(boolean show) {
        final boolean showUserInfo = show;
        OrderTask.getUserInfo(getActivity(), userPhoneEditText.getText()
                .toString(), new OnNetworkListener() {

            @Override
            public void onStart() {
                super.onStart();
                showLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
            }

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                if (!TextUtils.isEmpty(message.getResult())) {
                    JSONObject object;
                    try {
                        object = new JSONObject(message.getResult());
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject userJsonObject = object.optJSONObject("object");
                            if (userJsonObject != null) {
                                user = new User(userJsonObject);
                            } else {
                                user = new User();
                            }
                            if (showUserInfo) {
                                showUserInfo();
                            } else {
                                if (user.isLogin()) {
                                    buyProduct();
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

    private void getProductPrice(String productIds) {
        ProductTask.getProductPrice(getActivity(), productIds,
                new OnNetworkListener() {

                    @Override
                    public void onSuccess(NetworkMissionMessage requestMessage) {
                        super.onSuccess(requestMessage);
                        if (!TextUtils.isEmpty(requestMessage.getResult())) {
                            JSONObject object;
                            try {
                                object = new JSONObject(requestMessage.getResult());
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
                });
    }

    private void getFreight(String productNumber) {
        ProductTask.getFreight(getActivity(), productNumber, machineArea.getId(),
                new OnNetworkListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideLoading();
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        if (!TextUtils.isEmpty(message.getResult())) {
                            try {
                                JSONObject object = new JSONObject(message.getResult());
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
                });
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

    private void getArea() {
        ProductTask.getMachineArea(getActivity(), new OnNetworkListener() {

            @Override
            public void onStart() {
                super.onStart();
                showLoading();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                hideLoading();
            }

            @Override
            public void onSuccess(NetworkMissionMessage message) {
                super.onSuccess(message);
                if (!TextUtils.isEmpty(message.getResult())) {
                    JSONObject object;
                    try {
                        object = new JSONObject(message.getResult());
                        if (object.optString("state").equalsIgnoreCase(
                                "SUCCESS")) {
                            JSONObject dataMap = object
                                    .optJSONObject("dataMap");
                            if (dataMap != null) {
                                machineArea = new ModelMachineArea(dataMap);
                                userAreaTextView.setText(machineArea.getAreas());
                                getFreight(productNumbers.toString());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        });
    }

    private void buyProduct() {
        NetworkContent networkContent = new NetworkContent(API.API_ORDER_ADD);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters("userNumber", user.getUseraccount());
        networkContent.addParameters("customerName", userNameEditText.getText().toString());
        networkContent.addParameters("phone", userPhoneEditText.getText().toString());
        networkContent.addParameters("shippingaddress", userAddressEditText.getText().toString());
        networkContent.addParameters("totalMoney", goodsMoney + "");
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
            networkContent.addParameters("data", dataArray.toString());

            JSONArray freightArray = new JSONArray();
            for (int i = 0; i < providers.size(); i++) {
                if (freightMap.containsKey(providers.get(i))) {
                    JSONObject freightObject = new JSONObject();
                    freightObject.put("supplyId", providers.get(i));
                    freightObject.put("freight", freightMap.get(providers.get(i)));
                    freightArray.put(freightObject);
                }
            }
            networkContent.addParameters("freights", freightArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MissionController.startNetworkMission(getActivity(), networkContent,
                new OnNetworkListener() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        hideLoading();
                    }

                    @Override
                    public void onFail(NetworkMissionMessage message) {
                        super.onFail(message);
                        Notify.show("下单失败");
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage message) {
                        super.onSuccess(message);
                        if (!TextUtils.isEmpty(message.getResult())) {
                            JSONObject object;
                            try {
                                object = new JSONObject(message.getResult());
                                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                                    Notify.show("下单成功");
                                    payMoney(object.optJSONArray("object"));
                                    ShoppingCartController.getInstance().removeSelectedProducts(DataBaseHelper.tableCarPlatform);
                                    return;
                                }
                                Notify.show(object.optString("message"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
    }
}
