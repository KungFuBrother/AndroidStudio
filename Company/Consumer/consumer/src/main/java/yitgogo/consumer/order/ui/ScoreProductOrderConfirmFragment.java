package yitgogo.consumer.order.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.home.model.ModelScoreProductDetail;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelOrderResult;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

/**
 * @author Tiger
 * @description 本地服务确认订单
 */
public class ScoreProductOrderConfirmFragment extends BaseNetworkFragment {

    String productId = "";
    int productCount = 1;
    double totalPrice = 0;
    ModelScoreProductDetail productDetail = new ModelScoreProductDetail();

    ImageView imageView;
    TextView nameTextView, priceTextView, countTextView, countAddButton,
            countDeleteButton, additionTextView;

    FrameLayout addressLayout, paymentLayout;
    TextView totalPriceTextView, confirmButton;

    OrderConfirmPartAddressFragment addressFragment;
    OrderConfirmPartPaymentFragment paymentFragment;

    List<ModelOrderResult> orderResults;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm_order_sale);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ScoreProductOrderConfirmFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ScoreProductOrderConfirmFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getScoreProductDetail();
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("productId")) {
                productId = bundle.getString("productId");
            }
        }
        addressFragment = new OrderConfirmPartAddressFragment();
        paymentFragment = new OrderConfirmPartPaymentFragment(true, false);
        orderResults = new ArrayList<>();
    }

    @Override
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
        getFragmentManager().beginTransaction()
                .replace(R.id.order_confirm_sale_address, addressFragment)
                .replace(R.id.order_confirm_sale_payment, paymentFragment)
                .commit();
        countTextView.setText(productCount + "");
        countTotalPrice();
    }

    @Override
    protected void registerViews() {
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
        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }

    private void deleteCount() {
        if (productCount > 1) {
            productCount--;
        }
        if (productCount == 1) {
            countDeleteButton.setClickable(false);
        }
        countAddButton.setClickable(true);
        countTextView.setText(productCount + "");
        countTotalPrice();
    }

    private void addCount() {
        if (productCount < 100) {
            productCount++;
        }
        if (productCount == 100) {
            countAddButton.setClickable(false);
        }
        countDeleteButton.setClickable(true);
        countTextView.setText(productCount + "");
        countTotalPrice();
    }

    private void countTotalPrice() {
        totalPrice = productCount * productDetail.getJifenjia();
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalPrice));
        additionTextView.setText("将扣除"
                + (productCount * productDetail.getJifen() + "积分"));
    }

    private void confirmOrder() {
        if (totalPrice > 0) {
            if (addressFragment.getAddress() == null) {
                ApplicationTool.showToast("收货人信息有误");
            } else {
                addScoreProductOrder();
            }
        }
    }

    private void showDetail() {
        if (!productDetail.getImgs().isEmpty()) {
            ImageLoader.getInstance().displayImage(
                    productDetail.getImgs().get(0), imageView);
        }
        nameTextView.setText(productDetail.getName());
        priceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(productDetail.getJifenjia()));
        countTotalPrice();
    }

    private void getScoreProductDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("id", productId));
        post(API.API_SCORE_PRODUCT_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            productDetail = new ModelScoreProductDetail(
                                    object.optJSONObject("dataMap"));
                            showDetail();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void addScoreProductOrder() {
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
        JSONObject object = new JSONObject();
        try {
            object.put("productIds", productDetail.getProductId());
            object.put("shopNum", productCount + "");
            object.put("price", productDetail.getJifenjia());
            object.put("isIntegralMall", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        orderArray.put(object);
        requestParams.add(new RequestParam("data", orderArray
                .toString()));
        post(API.API_ORDER_ADD_CENTER, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            Toast.makeText(getActivity(), "下单成功",
                                    Toast.LENGTH_SHORT).show();
                            if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
                                payMoney(object.optJSONArray("object"));
                                getActivity().finish();
                                return;
                            }
                            showOrder(PayFragment.ORDER_TYPE_YY);
                            getActivity().finish();
                            return;
                        } else {
                            ApplicationTool.showToast(object.optString("message"));
                            return;
                        }
                    } catch (JSONException e) {
                        ApplicationTool.showToast("下单失败");
                        e.printStackTrace();
                        return;
                    }
                }
                ApplicationTool.showToast("下单失败");
            }
        });
    }

}
