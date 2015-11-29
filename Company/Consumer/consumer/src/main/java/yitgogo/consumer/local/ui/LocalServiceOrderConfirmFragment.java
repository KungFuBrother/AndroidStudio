package yitgogo.consumer.local.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.local.model.ModelLocalService;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartAddressFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartDeliverFragment;
import yitgogo.consumer.order.ui.OrderConfirmPartPaymentFragment;
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
public class LocalServiceOrderConfirmFragment extends BaseNetworkFragment {

    String productId = "";
    int productCount = 1;
    double totalPrice = 0;
    ModelLocalService service = new ModelLocalService();

    ImageView imageView;
    TextView nameTextView, priceTextView, countTextView, countAddButton,
            countDeleteButton;

    TextView totalMoneyTextView;
    Button confirmButton;

    OrderConfirmPartAddressFragment addressFragment;
    OrderConfirmPartDeliverFragment deliverFragment;
    OrderConfirmPartPaymentFragment paymentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_buy_local_service);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalServiceOrderConfirmFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LocalServiceOrderConfirmFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getServiceDetail();
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
        deliverFragment = new OrderConfirmPartDeliverFragment();
        paymentFragment = new OrderConfirmPartPaymentFragment(false);
    }

    @Override
    protected void findViews() {
        imageView = (ImageView) contentView
                .findViewById(R.id.local_goods_order_goods_image);
        nameTextView = (TextView) contentView
                .findViewById(R.id.local_goods_order_goods_name);
        priceTextView = (TextView) contentView
                .findViewById(R.id.local_goods_order_goods_price);
        countTextView = (TextView) contentView
                .findViewById(R.id.local_goods_order_goods_count);
        countDeleteButton = (TextView) contentView
                .findViewById(R.id.local_goods_order_goods_count_delete);
        countAddButton = (TextView) contentView
                .findViewById(R.id.local_goods_order_goods_count_add);
        totalMoneyTextView = (TextView) contentView
                .findViewById(R.id.local_goods_order_total_money);
        confirmButton = (Button) contentView
                .findViewById(R.id.local_goods_order_confirm);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        getFragmentManager().beginTransaction()
                .replace(R.id.local_goods_order_part_address, addressFragment)
                .replace(R.id.local_goods_order_part_deliver, deliverFragment)
                .replace(R.id.local_goods_order_part_payment, paymentFragment)
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
        deliverFragment.setBuyCount(productCount);
        totalPrice = productCount * service.getProductPrice();
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalPrice));
    }

    private void confirmOrder() {
        if (totalPrice <= 0) {
            ApplicationTool.showToast("商品信息有误");
        } else if (addressFragment.getAddress() == null) {
            ApplicationTool.showToast("收货人地址有误");
        } else {
            addLocalServiceOrder();
        }
    }

    private void showServiceInfo() {
        ImageLoader.getInstance().displayImage(service.getImg(), imageView);
        nameTextView.setText(service.getProductName());
        priceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(service.getProductPrice()));
        deliverFragment.initDeliverType(service.isDeliverYN(),
                service.getDeliverNum());
        paymentFragment.setCanPaySend(service.isDeliveredToPaidYN());
        countTotalPrice();
    }

    private void getServiceDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_LOCAL_BUSINESS_SERVICE_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject object2 = object.optJSONObject("object");
                            if (object2 != null) {
                                service = new ModelLocalService(object2);
                                showServiceInfo();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void addLocalServiceOrder() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("customerName", addressFragment.getAddress().getPersonName()));
        requestParams.add(new RequestParam("customerPhone", addressFragment.getAddress().getPhone()));
        requestParams.add(new RequestParam("deliveryType", deliverFragment.getDeliverTypeName()));
        switch (deliverFragment.getDeliverType()) {
            case OrderConfirmPartDeliverFragment.DELIVER_TYPE_SELF:
                requestParams.add(new RequestParam("mustAddress", Store.getStore().getStoreAddess()));
                break;

            default:
                requestParams.add(new RequestParam("deliveryAddress",
                        addressFragment.getAddress().getAreaAddress()
                                + addressFragment.getAddress()
                                .getDetailedAddress()));
                break;
        }
        requestParams.add(new RequestParam("paymentType", paymentFragment.getPaymentName()));
        requestParams.add(new RequestParam("orderType", service.getProductType()));
        requestParams.add(new RequestParam("orderPrice", totalPrice + ""));
        requestParams.add(new RequestParam("productId", service.getId()));
        requestParams.add(new RequestParam("productNum", productCount + ""));
        requestParams.add(new RequestParam("memberNumber", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("providerId", Store.getStore().getStoreId()));
        post(API.API_LOCAL_BUSINESS_SERVICE_ORDER_ADD, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            Toast.makeText(getActivity(), "下单成功",
                                    Toast.LENGTH_SHORT).show();
                            // 如果选择了在线支付
                            if (paymentFragment.getPaymentType() == OrderConfirmPartPaymentFragment.PAY_TYPE_CODE_ONLINE) {
                                JSONObject orderObject = object
                                        .optJSONObject("dataMap");
                                if (orderObject != null) {
                                    double payPrice = orderObject
                                            .optDouble("zongjine");
                                    ArrayList<String> orderNumbers = new ArrayList<String>();
                                    orderNumbers.add(orderObject
                                            .optString("ordernumber"));
                                    if (orderNumbers.size() > 0) {
                                        if (payPrice > 0) {
                                            payMoney(orderNumbers, payPrice,
                                                    PayFragment.ORDER_TYPE_LS);
                                            getActivity().finish();
                                            return;
                                        }
                                    }
                                }
                            }
                            showOrder(PayFragment.ORDER_TYPE_LS);
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
