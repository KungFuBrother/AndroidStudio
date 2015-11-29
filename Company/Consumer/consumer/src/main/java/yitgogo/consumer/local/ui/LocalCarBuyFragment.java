package yitgogo.consumer.local.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.local.model.LocalCarController;
import yitgogo.consumer.local.model.ModelLocalCar;
import yitgogo.consumer.local.model.ModelLocalCarGoods;
import yitgogo.consumer.money.ui.PayFragment;
import yitgogo.consumer.order.model.ModelDiliver;
import yitgogo.consumer.order.model.ModelLocalGoodsOrderResult;
import yitgogo.consumer.order.model.ModelPayment;
import yitgogo.consumer.order.ui.OrderConfirmPartAddressFragment;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerListView;

public class LocalCarBuyFragment extends BaseNetworkFragment {

    InnerListView listView;
    TextView totalMoneyTextView, confirmButton;
    List<ModelLocalCar> localCars;
    CarAdapter carAdapter;
    double totalMoney = 0;

    OrderConfirmPartAddressFragment addressFragment = new OrderConfirmPartAddressFragment();
    ;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_confirm_order_local_car);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(LocalCarBuyFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(LocalCarBuyFragment.class.getName());
    }

    @Override
    protected void init() {
        localCars = LocalCarController.getSelectedLocalCars();
        carAdapter = new CarAdapter();
    }

    @Override
    protected void findViews() {
        listView = (InnerListView) contentView
                .findViewById(R.id.order_confirm_products);
        totalMoneyTextView = (TextView) contentView
                .findViewById(R.id.order_confirm_total_money);
        confirmButton = (TextView) contentView
                .findViewById(R.id.order_confirm_ok);

        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        getFragmentManager().beginTransaction()
                .replace(R.id.order_confirm_part_address, addressFragment)
                .commit();
        listView.setAdapter(carAdapter);
        countTotalPrice();
    }

    @Override
    protected void registerViews() {
        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                confirmOrder();
            }
        });
    }

    private void countTotalPrice() {
        totalMoney = 0;
        for (int i = 0; i < localCars.size(); i++) {
            totalMoney += localCars.get(i).getTotalMoney();
        }
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalMoney));
    }

    private void confirmOrder() {
        if (totalMoney <= 0) {
            ApplicationTool.showToast("商品信息有误");
        } else if (addressFragment.getAddress() == null) {
            ApplicationTool.showToast("收货人地址有误");
        } else {
            addLocalGoodsOrder();
        }
    }

    private void addLocalGoodsOrder() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("serviceProvidID", Store
                .getStore().getStoreId()));
        requestParams.add(new RequestParam("memberAccount", User
                .getUser().getUseraccount()));
        requestParams.add(new RequestParam("customerName",
                addressFragment.getAddress().getPersonName()));
        requestParams.add(new RequestParam("customerPhone",
                addressFragment.getAddress().getPhone()));
        requestParams.add(new RequestParam("retailOrderPrice",
                totalMoney + ""));

        JSONArray data = new JSONArray();
        JSONArray deliveryInfo = new JSONArray();
        try {
            for (int i = 0; i < localCars.size(); i++) {
                JSONObject deliveryInfoObject = new JSONObject();
                deliveryInfoObject.put("supplyId", localCars.get(i)
                        .getStore().getId());
                deliveryInfoObject.put("deliveryType", localCars.get(i)
                        .getDiliver().getName());
                switch (localCars.get(i).getDiliver().getType()) {
                    case ModelDiliver.TYPE_HOME:
                        deliveryInfoObject.put("address", addressFragment
                                .getAddress().getAreaAddress()
                                + addressFragment.getAddress()
                                .getDetailedAddress());
                        break;
                    case ModelDiliver.TYPE_SELF:
                        deliveryInfoObject.put("address", localCars.get(i)
                                .getStore().getServiceaddress());
                        break;
                    default:
                        break;
                }
                deliveryInfoObject.put("paymentType", localCars.get(i)
                        .getPayment().getType());
                deliveryInfo.put(deliveryInfoObject);
                for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
                    JSONObject dataObject = new JSONObject();
                    ModelLocalCarGoods dataGoods = localCars.get(i)
                            .getCarGoods().get(j);
                    dataObject.put("retailProductManagerID", dataGoods
                            .getGoods().getId());
                    dataObject.put("orderType", "0");
                    dataObject.put("shopNum", dataGoods.getGoodsCount());
                    dataObject.put("productPrice", dataGoods.getGoods()
                            .getRetailPrice());
                    data.put(dataObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        requestParams.add(new RequestParam("data", data.toString()));
        requestParams.add(new RequestParam("deliveryInfo", deliveryInfo
                .toString()));

        post(API.API_LOCAL_BUSINESS_GOODS_ORDER_ADD, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (result.length() > 0) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            LocalCarController.deleteSelectedGoods();
                            ApplicationTool.showToast("下单成功");
                            JSONArray orderArray = object.optJSONArray("dataList");
                            if (orderArray != null) {
                                double payPrice = 0;
                                ArrayList<String> orderNumbers = new ArrayList<String>();
                                for (int i = 0; i < orderArray.length(); i++) {
                                    ModelLocalGoodsOrderResult orderResult = new ModelLocalGoodsOrderResult(
                                            orderArray.optJSONObject(i));
                                    if (orderResult.getPaymentType() == ModelPayment.TYPE_ONLINE) {
                                        orderNumbers.add(orderResult
                                                .getOrdernumber());
                                        payPrice += orderResult.getOrderPrice();
                                    }
                                }
                                if (orderNumbers.size() > 0) {
                                    if (payPrice > 0) {
                                        payMoney(orderNumbers, payPrice,
                                                PayFragment.ORDER_TYPE_LP);
                                        getActivity().finish();
                                        return;
                                    }
                                }
                            }
                            showOrder(PayFragment.ORDER_TYPE_LP);
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

    class CarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return localCars.size();
        }

        @Override
        public Object getItem(int position) {
            return localCars.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_local_car_buy, null);
                holder = new ViewHolder();
                holder.storeTextView = (TextView) convertView
                        .findViewById(R.id.local_car_store_name);
                holder.goodsListView = (InnerListView) convertView
                        .findViewById(R.id.local_car_goods);
                holder.diliverTextView = (TextView) convertView
                        .findViewById(R.id.local_car_store_diliver);
                holder.paymentTextView = (TextView) convertView
                        .findViewById(R.id.local_car_store_pay);
                holder.moneyTextView = (TextView) convertView
                        .findViewById(R.id.local_car_store_money);
                holder.moneyDetailTextView = (TextView) convertView
                        .findViewById(R.id.local_car_store_money_detail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelLocalCar localCar = localCars.get(position);
            holder.storeTextView.setText(localCar.getStore().getServicename());
            holder.goodsListView.setAdapter(new CarGoodsAdapter(localCar));
            holder.diliverTextView.setText(localCar.getDiliver().getName());
            holder.paymentTextView.setText(localCar.getPayment().getName());
            holder.moneyTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(localCar.getTotalMoney()));
            holder.moneyDetailTextView.setText(getMoneyDetailString(
                    localCar.getGoodsMoney(), localCar.getPostFee()));
            return convertView;
        }

        class ViewHolder {
            TextView storeTextView, moneyTextView, moneyDetailTextView,
                    diliverTextView, paymentTextView;
            InnerListView goodsListView;
        }
    }

    class CarGoodsAdapter extends BaseAdapter {

        ModelLocalCar localCar = new ModelLocalCar();

        public CarGoodsAdapter(ModelLocalCar localCar) {
            this.localCar = localCar;
        }

        @Override
        public int getCount() {
            return localCar.getCarGoods().size();
        }

        @Override
        public Object getItem(int position) {
            return localCar.getCarGoods().get(position);
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
            ModelLocalCarGoods goods = localCar.getCarGoods().get(position);
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(goods.getGoods().getBigImgUrl()),
                    holder.goodsImageView);
            holder.goodNameText.setText(goods.getGoods()
                    .getRetailProdManagerName());
            holder.goodsPriceText.setText("¥"
                    + decimalFormat.format(goods.getGoods().getRetailPrice()));
            holder.stateText.setText("×" + goods.getGoodsCount());
            return convertView;
        }

        class ViewHolder {
            ImageView goodsImageView;
            TextView goodNameText, goodsPriceText, guigeText, stateText;
        }
    }

}
