package yitgogo.smart.order.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.model.ModelMachineArea;
import yitgogo.smart.order.model.User;
import yitgogo.smart.task.OrderTask;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.Notify;

public class ProductPlatformBuyFragment extends BaseNotifyFragment {

    TextView productNameTextView, priceTextView, countTextView, freightTextView, additionTextView, totalMoneyTextView;
    ImageView productImageView;

    EditText userPhoneEditText, userNameEditText, userAddressEditText;
    TextView userAreaTextView;

    TextView buyButton;

    User user;
    ModelMachineArea machineArea = new ModelMachineArea();

    String supplierId = "";
    String supplierName = "";
    String productId = "";
    String name = "";
    String image = "";
    int isIntegralMall = 0;
    double price = 0;
    int buyCount = 0;
    double freight = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_single_product_buy);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ProductPlatformBuyFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ProductPlatformBuyFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArea();
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
            if (bundle.containsKey("name")) {
                name = bundle.getString("name");
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
            if (bundle.containsKey("freight")) {
                freight = bundle.getDouble("freight");
            }
        }
    }

    protected void findViews() {
        productNameTextView = (TextView) contentView.findViewById(R.id.buy_product_name);
        priceTextView = (TextView) contentView.findViewById(R.id.buy_product_price);
        countTextView = (TextView) contentView.findViewById(R.id.buy_product_count);
        freightTextView = (TextView) contentView.findViewById(R.id.buy_product_freight);
        additionTextView = (TextView) contentView.findViewById(R.id.buy_product_addition);
        totalMoneyTextView = (TextView) contentView.findViewById(R.id.buy_product_count_total);
        productImageView = (ImageView) contentView.findViewById(R.id.buy_product_image);

        userPhoneEditText = (EditText) contentView.findViewById(R.id.buy_user_phone);
        userNameEditText = (EditText) contentView.findViewById(R.id.buy_user_name);
        userAddressEditText = (EditText) contentView.findViewById(R.id.buy_user_address);
        userAreaTextView = (TextView) contentView.findViewById(R.id.buy_user_area);

        buyButton = (TextView) contentView.findViewById(R.id.buy_confirm);

        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        imageLoader.displayImage(getSmallImageUrl(image), productImageView);
        productNameTextView.setText(name);
        priceTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(price));
        countTextView.setText(String.valueOf(buyCount));
        freightTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format(freight));
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB + decimalFormat.format((buyCount * price) + freight));
    }

    @Override
    protected void registerViews() {
        buyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buy();
            }
        });
        userPhoneEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

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

    private void buy() {
        if (buyCount * price > 0) {
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
        } else {
            Notify.show("商品价格有误，不能购买");
        }
    }

    private void registerUser() {
        OrderTask.registerUser(getActivity(), userPhoneEditText.getText()
                        .toString(), userNameEditText.getText().toString(),
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
                                if (object.optString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
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
                        if (object.optString("state").equalsIgnoreCase(
                                "SUCCESS")) {
                            JSONObject userJsonObject = object
                                    .optJSONObject("object");
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

    private void showUserInfo() {
        if (user != null) {
            userNameEditText.setText(user.getRealname());
            userAddressEditText.setText(user.getAddress());
        }
    }

    private void buyProduct() {
        NetworkContent networkContent = new NetworkContent(API.API_ORDER_ADD);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters("userNumber", user.getUseraccount());
        networkContent.addParameters("customerName", userNameEditText.getText().toString());
        networkContent.addParameters("phone", userPhoneEditText.getText().toString());
        networkContent.addParameters("shippingaddress", userAddressEditText.getText().toString());
        networkContent.addParameters("totalMoney", decimalFormat.format(buyCount * price));
        try {

            JSONArray dataArray = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("productIds", productId);
            object.put("shopNum", buyCount);
            object.put("price", price);
            object.put("isIntegralMall", isIntegralMall);
            dataArray.put(object);
            networkContent.addParameters("data", dataArray.toString());

            JSONArray freightArray = new JSONArray();
            JSONObject freightObject = new JSONObject();
            freightObject.put("supplyId", supplierId);
            freightObject.put("freight", freight);
            freightArray.put(freightObject);
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
                                if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                                    Notify.show("下单成功");
                                    payMoney(object.optJSONArray("object"));
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
