package yitgogo.smart.local;

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

import java.util.ArrayList;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.model.ModelLocalGoods;
import yitgogo.smart.model.ModelMachineArea;
import yitgogo.smart.order.model.ModelDiliver;
import yitgogo.smart.order.model.ModelPayment;
import yitgogo.smart.order.model.ModelStorePostInfo;
import yitgogo.smart.order.model.User;
import yitgogo.smart.order.ui.OrderDiliverPaymentFragment;
import yitgogo.smart.order.ui.PayFragment;
import yitgogo.smart.task.OrderTask;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.Notify;

public class LocalGoodsBuyFragment extends BaseNotifyFragment {

    TextView productNameTextView, priceTextView, countTextView,
            additionTextView, totalMoneyTextView, totalMoneyDetailTextView;
    ImageView productImageView, countDeleteButton, countAddButton;

    EditText userPhoneEditText, userNameEditText, userAddressEditText;
    TextView userAreaTextView;

    TextView buyButton;

    ModelLocalGoods goods = new ModelLocalGoods();
    User user;
    ModelMachineArea machineArea = new ModelMachineArea();

    double totalMoney = 0;
    int buyCount = 1;

    ModelStorePostInfo storePostInfo = new ModelStorePostInfo();
    OrderDiliverPaymentFragment diliverPaymentFragment = new OrderDiliverPaymentFragment() {
        public void onDiliverChange() {
            countTotalMoney();
        }

        ;
    };

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
        MobclickAgent.onPageStart(LocalGoodsBuyFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LocalGoodsBuyFragment.class.getName());
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
                    goods = new ModelLocalGoods(new JSONObject(
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
        totalMoneyDetailTextView = (TextView) contentView
                .findViewById(R.id.buy_product_count_total_detail);
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
        double goodsMoney = buyCount * goods.getRetailPrice();
        double postFee = 0;
        if (diliverPaymentFragment.getSelectedDiliver().getType() != ModelDiliver.TYPE_SELF
                & goodsMoney > 0
                & goodsMoney < storePostInfo.getHawManyPackages()) {
            postFee = storePostInfo.getPostage();
        }
        totalMoney = goodsMoney + postFee;
        countTextView.setText(buyCount + "");
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalMoney));
        totalMoneyDetailTextView.setText(getMoneyDetailString(goodsMoney,
                postFee));
    }

    protected void initViews() {
        productNameTextView.setText(goods.getRetailProdManagerName());
        priceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(goods.getRetailPrice()));
        imageLoader.displayImage(getSmallImageUrl(goods.getBigImgUrl()),
                productImageView);
        getStoreInfo();
    }

    private void initDiliverPayment() {

        diliverPaymentFragment.addDiliverType(ModelDiliver.TYPE_SELF,
                ModelDiliver.NAME_SELF);
        diliverPaymentFragment.addDiliverType(ModelDiliver.TYPE_HOME,
                ModelDiliver.NAME_HOME);

        diliverPaymentFragment.addPaymentType(ModelPayment.TYPE_ONLINE,
                ModelPayment.NAME_ONLINE);
        if (storePostInfo.isSupportForDelivery()) {
            diliverPaymentFragment.addPaymentType(ModelPayment.TYPE_RECEIVED,
                    ModelPayment.NAME_RECEIVED);
        }

        additionTextView.setText(getStorePostInfoString(storePostInfo));

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.buy_product_diliver_payment,
                        diliverPaymentFragment).commit();
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
                    try {
                        buyLocalGoods();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                                    buyLocalGoods();
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

    /**
     * @throws JSONException
     * @NetworkContent [url=http://192.168.8.80:8088/api/localEBusiness/retail/orderManager
     * /addRetailOrderMan,
     * nameValuePairs=[shebei=8888888888888888,
     * memberAccount=E47C76968E52027AAAB0F58957F8AF82,
     * customerName=雷小武, customerPhone=18048831447,
     * retailOrderPrice=20.0,
     * data=[{"retailProductManagerID":"12011"
     * ,"orderType":0,"shopNum":1,"productPrice":20}],
     * deliveryInfo
     * =[{"supplyId":"1445","deliveryType":"自取","address"
     * :"四川省成都市金牛区解放路二段六号","paymentType":2}]]]
     * @RequestMessage [progressStatus=3, message=访问服务器返回数据成功, ={"message":
     * "ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[{"servicetelephone"
     * :"028-02020202","deliveryType":"自取","paymentType"
     * :"2","orderDate"
     * :"2015-10-15","servicename":"易田测试加盟店","productInfo":
     * "[{\"spname\":\"产品组-测试产品\",\"price\":\"20.0\",\"Amount\":\"20.0\",\"num\":\"1\"}]"
     * ,"ordernumber":"YT9164085134","postagePrice":"无",
     * "orderPrice"
     * :"20.0","servicePhone":"13228116626"}],"totalCount"
     * :1,"dataMap":{},"object":null}]
     * @NetworkContent [url=http://192.168.8.80:8088/api/localEBusiness/retail/orderManager
     * /addRetailOrderMan,
     * nameValuePairs=[shebei=8888888888888888,
     * memberAccount=E47C76968E52027AAAB0F58957F8AF82,
     * customerName=雷小武, customerPhone=18048831447,
     * retailOrderPrice=30.0,
     * data=[{"retailProductManagerID":"12011"
     * ,"orderType":0,"shopNum":1,"productPrice":20}],
     * deliveryInfo
     * =[{"supplyId":"1445","deliveryType":"送货上门","address"
     * :"成都市金牛区","paymentType":2}]]]
     * @RequestMessage [progressStatus=3, message=访问服务器返回数据成功,
     * result={"message":
     * "ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[{"servicetelephone"
     * :"028-02020202","deliveryType":"送货上门"
     * ,"paymentType":"2","orderDate"
     * :"2015-10-15","servicename":"易田测试加盟店","productInfo":
     * "[{\"spname\":\"产品组-测试产品\",\"price\":\"20.0\",\"Amount\":\"20.0\",\"num\":\"1\"}]"
     * ,"ordernumber":"YT9164132638","postagePrice":"10.0",
     * "orderPrice"
     * :"30.0","servicePhone":"13228116626"}],"totalCount"
     * :1,"dataMap":{},"object":null}]
     */
    private void buyLocalGoods() throws JSONException {
        OrderTask.buyLocalGoods(getActivity(), user.getUseraccount(),
                userNameEditText.getText().toString(), userPhoneEditText
                        .getText().toString(), userAddressEditText.getText()
                        .toString(), diliverPaymentFragment
                        .getSelectedDiliver(), diliverPaymentFragment
                        .getSelectedPayment(), goods.getProviderBean().getId(),
                goods.getProviderBean().getServiceaddress(), totalMoney, goods
                        .getId(), buyCount, goods.getRetailPrice(), 0,
                new OnNetworkListener() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showLoading("下单中,请稍候...");
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
                                if (object.optString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
                                    Notify.show("下单成功");
                                    JSONArray orderArray = object
                                            .optJSONArray("dataList");
                                    if (orderArray != null) {
                                        double payPrice = 0;
                                        ArrayList<String> orderNumbers = new ArrayList<String>();
                                        for (int i = 0; i < orderArray.length(); i++) {
                                            JSONObject orderObject = orderArray
                                                    .optJSONObject(i);
                                            if (orderObject != null) {
                                                orderNumbers.add(orderObject
                                                        .optString("ordernumber"));
                                                payPrice += orderObject
                                                        .optDouble("orderPrice");
                                            }
                                        }
                                        payMoney(orderNumbers, payPrice,
                                                PayFragment.ORDER_TYPE_LP);
                                    }
                                    return;
                                }
                                Notify.show(object.optString("message"));
                                return;
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

    /**
     * @result { "message": "ok", "state": "SUCCESS", "cacheKey": null,
     * "dataList": [], "totalCount": 1, "dataMap": {
     * "whetherToUseStockSystem": false, "hawManyPackages": 50,
     * "autoPurchase": false, "supportForDelivery": false, "postage": 10
     * }, "object": null }
     */
    private void getStoreInfo() {
        OrderTask.getStoreInfo(getActivity(), goods.getProviderBean().getNo(),
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
                                    storePostInfo = new ModelStorePostInfo(
                                            object.optJSONObject("dataMap"));
                                    initDiliverPayment();
                                    countTotalMoney();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
    }

}
