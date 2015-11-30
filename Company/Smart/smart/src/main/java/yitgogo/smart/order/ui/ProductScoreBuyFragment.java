package yitgogo.smart.order.ui;

import android.content.Context;
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
import yitgogo.smart.home.model.ModelScoreProductDetail;
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

public class ProductScoreBuyFragment extends BaseNotifyFragment {

    TextView productNameTextView, priceTextView, countTextView,
            additionTextView, totalMoneyTextView;
    ImageView productImageView, countDeleteButton, countAddButton;

    EditText userPhoneEditText, userNameEditText, userAddressEditText;
    TextView userAreaTextView;

    TextView buyButton;

    ModelScoreProductDetail productDetail = new ModelScoreProductDetail();
    User user;
    ModelMachineArea machineArea = new ModelMachineArea();

    double totalMoney = 0;
    int buyCount = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_local_goods_buy);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ProductScoreBuyFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ProductScoreBuyFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArea();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("object")) {
                try {
                    productDetail = new ModelScoreProductDetail(new JSONObject(
                            bundle.getString("object")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void findViews() {
        productNameTextView = (TextView) contentView
                .findViewById(R.id.buy_product_name);
        priceTextView = (TextView) contentView
                .findViewById(R.id.buy_product_price);
        countTextView = (TextView) contentView
                .findViewById(R.id.buy_product_count);
        additionTextView = (TextView) contentView
                .findViewById(R.id.buy_product_count_addition);
        totalMoneyTextView = (TextView) contentView
                .findViewById(R.id.buy_product_count_total);
        productImageView = (ImageView) contentView
                .findViewById(R.id.buy_product_image);
        countDeleteButton = (ImageView) contentView
                .findViewById(R.id.buy_product_count_delete);
        countAddButton = (ImageView) contentView
                .findViewById(R.id.buy_product_count_add);

        userPhoneEditText = (EditText) contentView
                .findViewById(R.id.buy_user_phone);
        userNameEditText = (EditText) contentView
                .findViewById(R.id.buy_user_name);
        userAddressEditText = (EditText) contentView
                .findViewById(R.id.buy_user_address);
        userAreaTextView = (TextView) contentView
                .findViewById(R.id.buy_user_area);

        buyButton = (TextView) contentView.findViewById(R.id.buy_confirm);

        initViews();
        registerViews();
    }

    private void countTotalMoney() {
        totalMoney = buyCount * productDetail.getJifenjia();
        countTextView.setText(buyCount + "");
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalMoney));
    }

    protected void initViews() {
        productNameTextView.setText(productDetail.getName());
        priceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(productDetail.getJifenjia()));
        additionTextView.setText("每件商品将扣除 " + productDetail.getJifen() + " 积分");
        if (!productDetail.getImgs().isEmpty()) {
            imageLoader.displayImage(getSmallImageUrl(productDetail.getImgs()
                    .get(0)), productImageView);
        }
        countTotalMoney();
    }

    @Override
    protected void registerViews() {
        countDeleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (buyCount > 1) {
                    buyCount--;
                    countTotalMoney();
                }
            }
        });
        countAddButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buyCount++;
                countTotalMoney();
            }
        });
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
        if (totalMoney > 0) {
            if (!isPhoneNumber(userPhoneEditText.getText().toString().trim())) {
                Notify.show("请填写正确的收货人电话");
            } else if (userNameEditText.length() == 0) {
                Notify.show("请填写收货人姓名");
            } else if (userAddressEditText.length() == 0) {
                Notify.show("请填写收货地址");
            } else {
                if (user.isLogin()) {
                    buyScoreProduct();
                } else {
                    registerUser();
                }
            }
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
                                    buyScoreProduct();
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

    public void buyProduct(Context context, String userNumber,
                           String customerName, String phone, String shippingaddress,
                           double totalMoney, String productIds, int shopNum, double price,
                           int isIntegralMall, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_ORDER_ADD);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters("userNumber", userNumber);
        networkContent.addParameters("customerName", customerName);
        networkContent.addParameters("phone", phone);
        networkContent.addParameters("shippingaddress", shippingaddress);
        networkContent.addParameters("totalMoney", totalMoney + "");
        try {
            JSONArray orderArray = new JSONArray();
            JSONObject object = new JSONObject();
            object.put("productIds", productIds);
            object.put("shopNum", shopNum + "");
            object.put("price", price);
            object.put("isIntegralMall", isIntegralMall);
            orderArray.put(object);
            networkContent.addParameters("data", orderArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    private void buyScoreProduct() {
        buyProduct(getActivity(), user.getUseraccount(),
                userNameEditText.getText().toString(), userPhoneEditText
                        .getText().toString(), userAddressEditText.getText()
                        .toString(), totalMoney, productDetail.getProductId(),
                buyCount, productDetail.getJifenjia(), 1,
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
                                if (object.getString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
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
