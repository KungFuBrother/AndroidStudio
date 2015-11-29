package yitgogo.smart.local;

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

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.local.model.LocalCarController;
import yitgogo.smart.local.model.ModelLocalCar;
import yitgogo.smart.local.model.ModelLocalCarGoods;
import yitgogo.smart.model.ModelMachineArea;
import yitgogo.smart.order.model.ModelDiliver;
import yitgogo.smart.order.model.User;
import yitgogo.smart.order.ui.PayFragment;
import yitgogo.smart.task.OrderTask;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerListView;
import yitgogo.smart.view.Notify;

public class LocalCarBuyFragment extends BaseNotifyFragment {

    ListView listView;
    TextView totalMoneyTextView;
    List<ModelLocalCar> localCars;
    CarAdapter carAdapter;
    double totalMoney = 0;

    EditText userPhoneEditText, userNameEditText, userAddressEditText;
    TextView userAreaTextView;

    TextView buyButton;

    User user;
    ModelMachineArea machineArea = new ModelMachineArea();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_local_car_buy);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArea();
    }

    private void init() {
        localCars = LocalCarController.getSelectedLocalCars();
        carAdapter = new CarAdapter();
    }

    protected void findViews() {
        listView = (ListView) contentView.findViewById(R.id.car_list);
        totalMoneyTextView = (TextView) contentView
                .findViewById(R.id.car_total);

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

    @Override
    protected void initViews() {
        listView.setAdapter(carAdapter);
        countTotalPrice();
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

    private void countTotalPrice() {
        totalMoney = 0;
        for (int i = 0; i < localCars.size(); i++) {
            totalMoney += localCars.get(i).getTotalMoney();
        }
        totalMoneyTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalMoney));
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

    private void buyLocalGoods() throws JSONException {
        JSONArray data = new JSONArray();
        JSONArray deliveryInfo = new JSONArray();
        for (int i = 0; i < localCars.size(); i++) {
            JSONObject deliveryInfoObject = new JSONObject();
            deliveryInfoObject.put("supplyId", localCars.get(i).getStore()
                    .getId());
            deliveryInfoObject.put("deliveryType", localCars.get(i)
                    .getDiliver().getName());
            switch (localCars.get(i).getDiliver().getType()) {
                case ModelDiliver.TYPE_HOME:
                    deliveryInfoObject.put("address", userAddressEditText.getText()
                            .toString());
                    break;
                case ModelDiliver.TYPE_SELF:
                    deliveryInfoObject.put("address", localCars.get(i).getStore()
                            .getServiceaddress());
                    break;
                default:
                    break;
            }
            deliveryInfoObject.put("paymentType", localCars.get(i).getPayment()
                    .getType());
            deliveryInfo.put(deliveryInfoObject);
            for (int j = 0; j < localCars.get(i).getCarGoods().size(); j++) {
                JSONObject dataObject = new JSONObject();
                ModelLocalCarGoods dataGoods = localCars.get(i).getCarGoods()
                        .get(j);
                dataObject.put("retailProductManagerID", dataGoods.getGoods()
                        .getId());
                dataObject.put("orderType", "0");
                dataObject.put("shopNum", dataGoods.getGoodsCount());
                dataObject.put("productPrice", dataGoods.getGoods()
                        .getRetailPrice());
                data.put(dataObject);
            }
        }
        OrderTask.buyLocalCarGoods(getActivity(), user.getUseraccount(),
                userNameEditText.getText().toString(), userPhoneEditText
                        .getText().toString(), totalMoney, data.toString(),
                deliveryInfo.toString(), new OnNetworkListener() {

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
                                    LocalCarController.deleteSelectedGoods();
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
                holder.storeImageView = (ImageView) convertView
                        .findViewById(R.id.local_car_store_image);
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
            imageLoader.displayImage(localCar.getStore().getImghead(),
                    holder.storeImageView);
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
            ImageView storeImageView;
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
            imageLoader.displayImage(getSmallImageUrl(goods.getGoods()
                    .getBigImgUrl()), holder.goodsImageView);
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
