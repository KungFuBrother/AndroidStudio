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

import com.dtr.zxing.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.product.model.ModelSaleDetailMiaosha;
import yitgogo.consumer.product.model.ModelSaleDetailTejia;
import yitgogo.consumer.product.model.ModelSaleDetailTime;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class SaleProductOrderConfirmFragment extends BaseNetworkFragment {

    ImageView imageView;
    TextView nameTextView, priceTextView, countTextView, countAddButton,
            countDeleteButton, additionTextView;

    FrameLayout addressLayout, paymentLayout;
    TextView totalPriceTextView, confirmButton;

    String productId = "";
    int saleType = CaptureActivity.SALE_TYPE_NONE;

    ModelProduct product = new ModelProduct();
    ModelOrderProduct orderProduct = new ModelOrderProduct();

    OrderConfirmPartAddressFragment addressFragment;
    OrderConfirmPartPaymentFragment paymentFragment;

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
        MobclickAgent.onPageStart(SaleProductOrderConfirmFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SaleProductOrderConfirmFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getProductDetail();
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("productId")) {
                productId = bundle.getString("productId");
            }
            if (bundle.containsKey("saleType")) {
                saleType = bundle.getInt("saleType");
            }
        }
        addressFragment = new OrderConfirmPartAddressFragment();
        paymentFragment = new OrderConfirmPartPaymentFragment(true, false);
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
                addOrder();
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
        ImageLoader.getInstance().displayImage(product.getImg(), imageView);
        nameTextView.setText(product.getProductName());
        if (orderProduct.isSale()) {
            priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(orderProduct.getSalePrice()));
        } else {
            priceTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(product.getPrice()));
        }
        countTextView.setText(orderProduct.getBuyCount() + "");
        additionTextView.setText(orderProduct.getAddition());
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(countTotalPrice()));
    }

    private void deleteCount() {
        if (orderProduct.getBuyCount() > 1) {
            orderProduct.setBuyCount(orderProduct.getBuyCount() - 1);
        }
        if (orderProduct.getBuyCount() == 1) {
            countDeleteButton.setClickable(false);
        }
        countAddButton.setClickable(true);
        countTextView.setText(orderProduct.getBuyCount() + "");
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(countTotalPrice()));
    }

    private void addCount() {
        if (orderProduct.isSale()) {
            if (orderProduct.getMaxBuyCount() > 0) {
                if (orderProduct.getMaxBuyCount() > orderProduct.getStock()) {
                    if (orderProduct.getBuyCount() < orderProduct.getStock()) {
                        orderProduct
                                .setBuyCount(orderProduct.getBuyCount() + 1);
                    }
                    if (orderProduct.getBuyCount() == orderProduct.getStock()) {
                        countAddButton.setClickable(false);
                    }
                } else {
                    if (orderProduct.getBuyCount() < orderProduct
                            .getMaxBuyCount()) {
                        orderProduct
                                .setBuyCount(orderProduct.getBuyCount() + 1);
                    }
                    if (orderProduct.getBuyCount() == orderProduct
                            .getMaxBuyCount()) {
                        countAddButton.setClickable(false);
                    }
                }
            } else if (orderProduct.getStock() > 0) {
                if (orderProduct.getBuyCount() < orderProduct.getStock()) {
                    orderProduct.setBuyCount(orderProduct.getBuyCount() + 1);
                }
                if (orderProduct.getBuyCount() == orderProduct.getStock()) {
                    countAddButton.setClickable(false);
                }
            } else {
                if (orderProduct.getBuyCount() < product.getNum()) {
                    orderProduct.setBuyCount(orderProduct.getBuyCount() + 1);
                }
                if (orderProduct.getBuyCount() == product.getNum()) {
                    countAddButton.setClickable(false);
                }
            }
        } else {
            if (orderProduct.getBuyCount() < product.getNum()) {
                orderProduct.setBuyCount(orderProduct.getBuyCount() + 1);
            }
            if (orderProduct.getBuyCount() == product.getNum()) {
                countAddButton.setClickable(false);
            }
        }
        countDeleteButton.setClickable(true);
        countTextView.setText(orderProduct.getBuyCount() + "");
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(countTotalPrice()));
    }

    private void getProductSaleInfo() {
        switch (saleType) {
            case CaptureActivity.SALE_TYPE_MIAOSHA:
                getMiaoshaSaleDetail();
                break;

            case CaptureActivity.SALE_TYPE_TEJIA:
                getTejiaSaleDetail();
                break;

            case CaptureActivity.SALE_TYPE_TIME:
                getTimeSaleDetail();
                break;

            default:
                showProductInfo();
                break;
        }
    }

    private double countTotalPrice() {
        double totalPrice = 0;
        if (orderProduct.isSale()) {
            totalPrice = orderProduct.getSalePrice()
                    * orderProduct.getBuyCount();
        } else {
            totalPrice = product.getPrice() * orderProduct.getBuyCount();
        }
        return totalPrice;
    }

    private void addOrder() {
        if (addressFragment.getAddress() == null) {
            ApplicationTool.showToast("收货人信息有误");
        } else {
            buy();
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
        requestParams.add(new RequestParam("totalMoney",
                countTotalPrice() + ""));
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
            JSONObject object = new JSONObject();
            object.put("productIds", productId);
            object.put("shopNum", orderProduct.getBuyCount());
            if (orderProduct.isSale()) {
                object.put("price", orderProduct.getSalePrice());
            } else {
                object.put("price", product.getPrice());
            }
            switch (saleType) {
                case CaptureActivity.SALE_TYPE_MIAOSHA:
                    object.put("isIntegralMall", 2);
                    break;
                default:
                    object.put("isIntegralMall", 0);
                    break;
            }
            orderArray.put(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    private void getProductDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("jmdId", Store.getStore()
                .getStoreId()));
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_PRODUCT_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    {
                        JSONObject object;
                        try {
                            object = new JSONObject(result);
                            if (object.getString("state").equalsIgnoreCase(
                                    "SUCCESS")) {
                                JSONObject detailObject = object
                                        .optJSONObject("dataMap");
                                if (detailObject != null) {
                                    product = new ModelProduct(detailObject);
                                    getProductSaleInfo();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void getMiaoshaSaleDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_SALE_MIAOSHA_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    ModelSaleDetailMiaosha saleDetailMiaosha = new ModelSaleDetailMiaosha(
                            result);
                    if (saleDetailMiaosha != null) {
                        orderProduct = new ModelOrderProduct(saleDetailMiaosha);
                        showProductInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getTimeSaleDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_SALE_TIME_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    ModelSaleDetailTime saleDetailTime = new ModelSaleDetailTime(
                            result);
                    if (saleDetailTime != null) {
                        orderProduct = new ModelOrderProduct(saleDetailTime);
                        showProductInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getTejiaSaleDetail() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_SALE_TEJIA_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    ModelSaleDetailTejia saleDetailTejia = new ModelSaleDetailTejia(
                            result);
                    if (saleDetailTejia != null) {
                        orderProduct = new ModelOrderProduct(saleDetailTejia);
                        showProductInfo();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    class ModelOrderProduct {

        double salePrice = -1, price = -1;
        boolean isSale = false;
        long buyCount = 1, stock = -1, maxBuyCount = -1;
        String addition = "";

        public ModelOrderProduct(ModelSaleDetailMiaosha saleDetailMiaosha) {
            if (saleDetailMiaosha != null) {
                salePrice = saleDetailMiaosha.getSeckillPrice();
                price = saleDetailMiaosha.getPrice();
                if (saleDetailMiaosha.getSeckillPrice() > 0) {
                    // 开始时间<=当前时间，活动已开始
                    if (saleDetailMiaosha.getStartTime() <= Calendar
                            .getInstance().getTime().getTime()) {
                        // 剩余秒杀数量>0，显示秒杀信息
                        if (saleDetailMiaosha.getSeckillNUmber() > 0) {
                            isSale = true;
                            stock = saleDetailMiaosha.getSeckillNUmber();
                            maxBuyCount = saleDetailMiaosha.getMemberNumber();
                            addition = "剩余" + stock + "件，限购" + maxBuyCount
                                    + "件";
                        } else {
                            isSale = false;
                            addition = "秒杀结束，按原价购买";
                        }
                    } else {
                        // 开始时间>当前时间，活动未开始，显示预告
                        isSale = false;
                        addition = "秒杀未开始，按原价购买";
                    }
                }
            }
        }

        public ModelOrderProduct(ModelSaleDetailTejia saleDetailTejia) {
            if (saleDetailTejia != null) {
                salePrice = saleDetailTejia.getSalePrice();
                price = saleDetailTejia.getPrice();
                if (saleDetailTejia.getSalePrice() > 0) {
                    if (saleDetailTejia.getNumbers() > 0) {
                        isSale = true;
                        stock = saleDetailTejia.getNumbers();
                    } else {
                        isSale = false;
                        addition = "活动已结束，按原价购买";
                    }
                }
            }
        }

        public ModelOrderProduct(ModelSaleDetailTime saleDetailTime) {
            if (saleDetailTime != null) {
                salePrice = saleDetailTime.getPromotionPrice();
                price = saleDetailTime.getPrice();
                if (saleDetailTime.getPromotionPrice() > 0) {
                    // 开始时间>当前时间，未开始，显示活动预告
                    if (saleDetailTime.getStartTime() > Calendar.getInstance()
                            .getTime().getTime()) {
                        isSale = false;
                        addition = "活动未开始，按原价购买";
                    } else if (saleDetailTime.getEndTime() > Calendar
                            .getInstance().getTime().getTime()) {
                        // 开始时间<=当前时间，结束时间>当前时间，已开始未结束，活动进行时
                        isSale = true;
                    } else {
                        // 活动结束
                        isSale = false;
                        addition = "活动已结束，按原价购买";
                    }
                }
            }
        }

        public ModelOrderProduct() {
        }

        public double getSalePrice() {
            return salePrice;
        }

        public double getPrice() {
            return price;
        }

        public boolean isSale() {
            return isSale;
        }

        public long getBuyCount() {
            return buyCount;
        }

        public long getStock() {
            return stock;
        }

        public long getMaxBuyCount() {
            return maxBuyCount;
        }

        public String getAddition() {
            return addition;
        }

        public void setBuyCount(long buyCount) {
            this.buyCount = buyCount;
        }

    }

}
