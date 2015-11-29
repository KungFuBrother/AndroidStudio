package yitgogo.consumer.suning.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartAddressFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartPaymentFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.suning.model.ModelProductDetail;
import yitgogo.consumer.suning.model.ModelProductPrice;
import yitgogo.consumer.suning.model.ModelSuningOrderResult;
import yitgogo.consumer.suning.model.SuningManager;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class SuningProductBuyFragment extends BaseNetworkFragment {

    ImageView imageView;
    TextView nameTextView, priceTextView, countTextView, countAddButton,
            countDeleteButton, additionTextView;

    FrameLayout addressLayout, paymentLayout;
    TextView totalPriceTextView, confirmButton;

    ModelProductDetail productDetail = new ModelProductDetail();
    ModelProductPrice productPrice = new ModelProductPrice();

    OrderConfirmPartAddressFragment addressFragment;
    OrderConfirmPartPaymentFragment paymentFragment;

    int buyCount = 1;
    double goodsMoney = 0;

    String state = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm_order_suning);
        init();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProductStock();
    }

    @Override
    protected void init() {
        try {
            Bundle bundle = getArguments();
            if (bundle != null) {
                if (bundle.containsKey("product")) {
                    productDetail = new ModelProductDetail(new JSONObject(bundle.getString("product")));
                }
                if (bundle.containsKey("price")) {
                    productPrice = new ModelProductPrice(new JSONObject(bundle.getString("price")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        addressFragment = new OrderConfirmPartAddressFragment();
        paymentFragment = new OrderConfirmPartPaymentFragment(true, true, false);
    }

    protected void findViews() {
        imageView = (ImageView) contentView
                .findViewById(R.id.order_confirm_sale_image);
        nameTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_sale_name);
        priceTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_sale_price);
        countTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_sale_count);
        countDeleteButton = (TextView) contentView
                .findViewById(R.id.order_confirm_sale_count_delete);
        countAddButton = (TextView) contentView
                .findViewById(R.id.order_confirm_sale_count_add);
        additionTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_sale_addition);
        addressLayout = (FrameLayout) contentView
                .findViewById(R.id.order_confirm_sale_address);
        paymentLayout = (FrameLayout) contentView
                .findViewById(R.id.order_confirm_sale_payment);
        totalPriceTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_sale_total_money);
        confirmButton = (TextView) contentView
                .findViewById(R.id.order_confirm_sale_confirm);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        showProductInfo();
        getFragmentManager().beginTransaction()
                .replace(R.id.order_confirm_sale_address, addressFragment)
                .replace(R.id.order_confirm_sale_payment, paymentFragment)
                .commit();
    }

    @Override
    protected void registerViews() {
        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                buy();
            }
        });
        countDeleteButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteCount();
            }
        });
        countAddButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addCount();
            }
        });
    }

    private void showProductInfo() {
        ImageLoader.getInstance().displayImage(productDetail.getImage(), imageView);
        nameTextView.setText(productDetail.getName());
        priceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(productPrice.getPrice()));
        countTextView.setText(buyCount + "");
        countTotalPrice();
    }

    private void deleteCount() {
        if (buyCount > 1) {
            buyCount--;
            countTotalPrice();
            getProductStock();
        }
    }

    private void addCount() {
        buyCount++;
        countTotalPrice();
        getProductStock();
    }

    private void countTotalPrice() {
        goodsMoney = 0;
        double sendMoney = 0;
        goodsMoney = productPrice.getPrice() * buyCount;
        if (goodsMoney > 0 & goodsMoney < 69) {
            sendMoney = 5;
        }
        countTextView.setText(buyCount + "");
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(goodsMoney + sendMoney));
    }

    private void buy() {
        if (addressFragment.getAddress() == null) {
            ApplicationTool.showToast("收货人信息有误");
        } else {
            if (state.equals("00")) {
                if (goodsMoney > 0) {
                    addSuningOrder();
                } else {
                    ApplicationTool.showToast("商品信息有误，暂不能购买");
                }
            } else {
                ApplicationTool.showToast("此商品暂不能购买");
            }
        }
    }

    private void addSuningOrder() {
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
        try {
            JSONObject skuObject = new JSONObject();
            skuObject.put("number", productDetail.getSku());
            skuObject.put("num", buyCount);
            skuObject.put("price", productPrice.getPrice());
            skuObject.put("name", productDetail.getName());
            skuObject.put("attr", productDetail.getModel());
            skuArray.put(skuObject);
        } catch (JSONException e) {
            e.printStackTrace();
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

    private void getProductStock() {
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
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("data", data.toString()));
        post(API.API_SUNING_PRODUCT_STOCK, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (SuningManager.isSignatureOutOfDate(result)) {
                    post(API.API_SUNING_SIGNATURE, new OnNetworkListener() {
                        @Override
                        public void onSuccess(String result) {
                            if (!TextUtils.isEmpty(result)) {
                                try {
                                    JSONObject object = new JSONObject(result);
                                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                                        JSONObject dataMap = object.optJSONObject("dataMap");
                                        Content.saveStringContent(Parameters.CACHE_KEY_SUNING_SIGNATURE, dataMap.toString());
                                        getProductStock();
                                        return;
                                    }
                                    ApplicationTool.showToast(object.optString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
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
        });
    }

}
