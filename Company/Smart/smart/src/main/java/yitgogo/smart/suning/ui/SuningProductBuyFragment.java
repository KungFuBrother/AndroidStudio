package yitgogo.smart.suning.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.order.model.User;
import yitgogo.smart.order.ui.PayFragment;
import yitgogo.smart.suning.model.API_SUNING;
import yitgogo.smart.suning.model.GetNewSignature;
import yitgogo.smart.suning.model.ModelProductDetail;
import yitgogo.smart.suning.model.ModelProductPrice;
import yitgogo.smart.suning.model.ModelSuningOrderResult;
import yitgogo.smart.suning.model.SuningManager;
import yitgogo.smart.task.OrderTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.Notify;

public class SuningProductBuyFragment extends BaseNotifyFragment {

    TextView productNameTextView, priceTextView, countTextView,
            additionTextView, totalMoneyTextView, totalMoneyDetailTextView;
    ImageView productImageView, countDeleteButton, countAddButton;

    EditText userPhoneEditText, userNameEditText, userAddressEditText;
    TextView userAreaTextView;

    TextView buyButton;

    ModelProductDetail productDetail = new ModelProductDetail();
    ModelProductPrice productPrice = new ModelProductPrice();

    User user;

    double goodsMoney = 0;
    int buyCount = 1;

    String state = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment_suning_product_buy);
        try {
            init();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SuningProductBuyFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SuningProductBuyFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetSuningProductPrice().execute();
        new GetProductStock().execute();
    }

    private void init() throws JSONException {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("product")) {
                productDetail = new ModelProductDetail(new JSONObject(bundle.getString("product")));
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
        goodsMoney = 0;
        double sendMoney = 0;
        goodsMoney = productPrice.getPrice() * buyCount;
        if (goodsMoney > 0 & goodsMoney < 69) {
            sendMoney = 5;
        }
        countTextView.setText(buyCount + "");
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(goodsMoney + sendMoney));
    }

    protected void initViews() {
        productNameTextView.setText(productDetail.getName());
        imageLoader.displayImage(productDetail.getImage(),
                productImageView);
        showArea();
    }

    private void showPrice() {
        priceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(productPrice.getPrice()));
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
                    new GetProductStock().execute();
                }
            }
        });
        countAddButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buyCount++;
                countTotalMoney();
                new GetProductStock().execute();
            }
        });
        userAreaTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SuningAreaDialog suningAreaDialog = new SuningAreaDialog() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        super.onDismiss(dialog);
                        showArea();
                        new GetSuningProductPrice().execute();
                        new GetProductStock().execute();
                    }
                };
                suningAreaDialog.show(getFragmentManager(), null);
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

    private void showArea() {
        StringBuilder stringBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(SuningManager.getSuningAreas().getProvince().getName())) {
            stringBuilder.append(SuningManager.getSuningAreas().getProvince().getName());
            if (!TextUtils.isEmpty(SuningManager.getSuningAreas().getCity().getName())) {
                stringBuilder.append(">");
                stringBuilder.append(SuningManager.getSuningAreas().getCity().getName());
                if (!TextUtils.isEmpty(SuningManager.getSuningAreas().getDistrict().getName())) {
                    stringBuilder.append(">");
                    stringBuilder.append(SuningManager.getSuningAreas().getDistrict().getName());
                    if (!TextUtils.isEmpty(SuningManager.getSuningAreas().getTown().getName())) {
                        stringBuilder.append(">");
                        stringBuilder.append(SuningManager.getSuningAreas().getTown().getName());
                    }
                }
            }
        }
        userAreaTextView.setText(stringBuilder.toString());
    }

    private void buy() {
        if (goodsMoney > 0) {
            if (!isPhoneNumber(userPhoneEditText.getText().toString().trim())) {
                Notify.show("请填写正确的收货人电话");
            } else if (userNameEditText.length() == 0) {
                Notify.show("请填写收货人姓名");
            } else if (userAddressEditText.length() == 0) {
                Notify.show("请填写收货地址");
            } else {
                if (state.equals("00")) {
                    if (goodsMoney > 0) {
                        if (user.isLogin()) {
                            new Buy().execute();
                        } else {
                            registerUser();
                        }
                    } else {
                        Notify.show("商品信息有误，暂不能购买");
                    }
                } else {
                    Notify.show("此商品暂不能购买");
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
                                    new Buy().execute();
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
     * {
     * "message": "ok",
     * "state": "SUCCESS",
     * "cacheKey": null,
     * "dataList": [],
     * "totalCount": 1,
     * "dataMap": {
     * "fuwuZuoji": "028-02020202",
     * "orderType": "新订单",
     * "orderNumber": "SN551533714581",
     * "zongjine": "30.0",
     * "productInfo": {
     * "num": 1,
     * "Amount": 30,
     * "price": 30,
     * "spname": "NEW17"
     * },
     * "fuwushang": "易田测试加盟店",
     * "shijian": "2015-10-27 15:19:36",
     * "fuwuPhone": "13228116626"
     * },
     * "object": null
     * }
     */
    class Buy extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... voids) {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("menberAccount", user.getUseraccount()));
            nameValuePairs.add(new BasicNameValuePair("name", userNameEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("mobile", userPhoneEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("address", userAddressEditText.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("jqm", Device.getDeviceCode()));
            nameValuePairs.add(new BasicNameValuePair("amount", goodsMoney + ""));
            nameValuePairs.add(new BasicNameValuePair("provinceId", SuningManager.getSuningAreas().getProvince().getCode()));
            nameValuePairs.add(new BasicNameValuePair("cityId", SuningManager.getSuningAreas().getCity().getCode()));
            nameValuePairs.add(new BasicNameValuePair("countyId", SuningManager.getSuningAreas().getDistrict().getCode()));
            nameValuePairs.add(new BasicNameValuePair("townId", SuningManager.getSuningAreas().getTown().getCode()));
            JSONArray skuArray = new JSONArray();
            JSONObject skuObject = new JSONObject();
            try {
                skuObject.put("number", productDetail.getSku());
                skuObject.put("num", buyCount);
                skuObject.put("price", productPrice.getPrice());
                skuObject.put("name", productDetail.getName());
                skuObject.put("attr", productDetail.getModel());
                skuArray.put(skuObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            nameValuePairs.add(new BasicNameValuePair("sku", skuArray.toString()));
            return MissionController.post(API.API_SUNING_ORDER_ADD, nameValuePairs);
        }

        @Override
        protected void onPostExecute(String s) {
            hideLoading();
            if (!TextUtils.isEmpty(s)) {
                try {
                    JSONObject object = new JSONObject(s);
                    if (object.optString("state").equals("SUCCESS")) {
                        Notify.show("下单成功");
                        ModelSuningOrderResult orderResult = new ModelSuningOrderResult(object.optJSONObject("dataMap"));
                        payMoney(orderResult.getOrderNumber(), orderResult.getZongjine() + orderResult.getFreight(), PayFragment.ORDER_TYPE_SN);
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

    class GetProductStock extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONObject data = new JSONObject();
            try {
                data.put("accessToken", SuningManager.getSignature().getToken());
                data.put("appKey", SuningManager.appKey);
                data.put("v", SuningManager.version);
                data.put("cityId", SuningManager.getSuningAreas().getCity().getCode());
                data.put("countyId", SuningManager.getSuningAreas().getDistrict().getCode());
                data.put("sku", productDetail.getSku());
                data.put("num", buyCount);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
            return MissionController.post(API_SUNING.API_PRODUCT_STOCK, nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (SuningManager.isSignatureOutOfDate(result)) {
                GetNewSignature getNewSignature = new GetNewSignature() {

                    @Override
                    protected void onPreExecute() {
                        showLoading();
                    }

                    @Override
                    protected void onPostExecute(Boolean isSuccess) {
                        hideLoading();
                        if (isSuccess) {
                            new GetProductStock().execute();
                        }
                    }
                };
                getNewSignature.execute();
                return;
            }
            /**
             * {"sku":null,"state":null,"isSuccess":false,"returnMsg":"无货"}
             */
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optBoolean("isSuccess")) {
                        state = object.optString("state");
                        if (state.equals("00")) {
                            additionTextView.setText("有货");
                        } else if (state.equals("01")) {
                            additionTextView.setText("暂不销售");
                        } else {
                            additionTextView.setText("无货");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    class GetSuningProductPrice extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONArray dataArray = new JSONArray();
            dataArray.put(productDetail.getSku());
            JSONObject data = new JSONObject();
            try {
                data.put("accessToken", SuningManager.getSignature().getToken());
                data.put("appKey", SuningManager.appKey);
                data.put("v", SuningManager.version);
                data.put("cityId", SuningManager.getSuningAreas().getCity().getCode());
                data.put("sku", dataArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
            return MissionController.post(API_SUNING.API_PRODUCT_PRICE, nameValuePairs);
        }

        /**
         * {"result":[{"skuId":"108246148","price":15000.00}],"isSuccess":true,"returnMsg":"查询成功。"}
         */
        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (SuningManager.isSignatureOutOfDate(result)) {
                GetNewSignature getNewSignature = new GetNewSignature() {
                    @Override
                    protected void onPreExecute() {
                        showLoading();
                    }

                    @Override
                    protected void onPostExecute(Boolean isSuccess) {
                        hideLoading();
                        if (isSuccess) {
                            new GetSuningProductPrice().execute();
                        }
                    }
                };
                getNewSignature.execute();
                return;
            }
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optBoolean("isSuccess")) {
                        JSONArray array = object.optJSONArray("result");
                        if (array != null) {
                            productPrice = new ModelProductPrice(array.optJSONObject(0));
                            showPrice();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
