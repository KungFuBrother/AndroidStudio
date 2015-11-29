package yitgogo.smart.product.ui;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.ModelCar;
import yitgogo.smart.home.model.ModelListPrice;
import yitgogo.smart.model.ModelMachineArea;
import yitgogo.smart.order.model.User;
import yitgogo.smart.product.model.ModelProduct;
import yitgogo.smart.task.OrderTask;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Content;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.Notify;

public class ShoppingCarPlatformBuyFragment extends BaseNotifyFragment
        implements OnClickListener {
    // 购物车部分
    ListView carList;
    List<ModelCar> modelCars;
    HashMap<String, ModelListPrice> priceMap;
    CarAdapter carAdapter;
    TextView selectAllButton, deleteButton;
    JSONArray carArray;
    boolean allSelected = true;

    EditText userPhoneEditText, userNameEditText, userAddressEditText;
    TextView userAreaTextView;

    TextView totalPriceTextView, buyButton;
    double totalMoney = 0;
    User user = new User();
    ModelMachineArea machineArea = new ModelMachineArea();

    List<ModelCar> orderProducts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_platform_car_buy);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ShoppingCarPlatformBuyFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getArea();
    }

    private void init() {
        modelCars = new ArrayList<ModelCar>();
        orderProducts = new ArrayList<ModelCar>();
        priceMap = new HashMap<String, ModelListPrice>();
        carAdapter = new CarAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShoppingCarPlatformBuyFragment.class.getName());
        refresh();
    }

    protected void findViews() {
        carList = (ListView) contentView.findViewById(R.id.car_list);
        selectAllButton = (TextView) contentView
                .findViewById(R.id.car_selectall);
        deleteButton = (TextView) contentView.findViewById(R.id.car_delete);
        totalPriceTextView = (TextView) contentView
                .findViewById(R.id.car_total);

        userNameEditText = (EditText) contentView
                .findViewById(R.id.order_user_name);
        userPhoneEditText = (EditText) contentView
                .findViewById(R.id.order_user_phone);
        userAreaTextView = (TextView) contentView
                .findViewById(R.id.order_user_area);
        userAddressEditText = (EditText) contentView
                .findViewById(R.id.order_user_address);
        buyButton = (TextView) contentView
                .findViewById(R.id.order_user_confirm);

        initViews();
        registerViews();
    }

    protected void initViews() {
        carList.setAdapter(carAdapter);
    }

    @Override
    protected void registerViews() {
        selectAllButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        buyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                addOrder();
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

    private void refresh() {
        modelCars.clear();
        carAdapter.notifyDataSetChanged();
        totalPriceTextView.setText("");
        try {
            carArray = new JSONArray(Content.getStringContent(
                    Parameters.CACHE_KEY_CAR, "[]"));
            for (int i = 0; i < carArray.length(); i++) {
                modelCars.add(new ModelCar(carArray.getJSONObject(i)));
            }
            carAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (modelCars.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < modelCars.size(); i++) {
                if (i > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(modelCars.get(i).getProduct().getId());
            }
            getProductPrice(stringBuilder.toString());
        } else {
            loadingEmpty("购物车还没有添加商品");
        }
    }

    private void addCount(int position) {
        ModelProduct product = modelCars.get(position).getProduct();
        long originalCount = modelCars.get(position).getProductCount();
        try {
            if (priceMap.containsKey(product.getId())) {
                if (product.getNum() > originalCount) {
                    carArray.getJSONObject(position).remove("productCount");
                    carArray.getJSONObject(position).put("productCount",
                            originalCount + 1);
                    Content.saveStringContent(Parameters.CACHE_KEY_CAR,
                            carArray.toString());
                    refresh();
                } else {
                    Notify.show("库存不足");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void deleteCount(int position) {
        long originalCount = modelCars.get(position).getProductCount();
        if (originalCount > 1) {
            try {
                carArray.getJSONObject(position).remove("productCount");
                carArray.getJSONObject(position).put("productCount",
                        originalCount - 1);
                Content.saveStringContent(Parameters.CACHE_KEY_CAR,
                        carArray.toString());
                refresh();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void select(int position) {
        boolean originalSelection = modelCars.get(position).isSelected();
        try {
            carArray.getJSONObject(position).remove("isSelected");
            carArray.getJSONObject(position).put("isSelected",
                    !originalSelection);
            Content.saveStringContent(Parameters.CACHE_KEY_CAR,
                    carArray.toString());
            refresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void countTotalPrice() {
        allSelected = true;
        totalMoney = 0;
        for (int i = 0; i < modelCars.size(); i++) {
            if (modelCars.get(i).isSelected()) {
                ModelProduct product = modelCars.get(i).getProduct();
                if (priceMap.containsKey(product.getId())) {
                    double price = priceMap.get(product.getId()).getPrice();
                    long count = modelCars.get(i).getProductCount();
                    if (price > 0) {
                        totalMoney += count * price;
                    }
                }
            } else {
                allSelected = false;
            }
        }
        if (allSelected) {
            selectAllButton.setText("全不选");
        } else {
            selectAllButton.setText("全选");
        }
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalMoney));
    }

    private void selectAll() {
        try {
            // 当前已全选，改为全不选
            if (allSelected) {
                for (int i = 0; i < carArray.length(); i++) {
                    carArray.getJSONObject(i).remove("isSelected");
                    carArray.getJSONObject(i).put("isSelected", false);
                }
            } else {
                for (int i = 0; i < carArray.length(); i++) {
                    carArray.getJSONObject(i).remove("isSelected");
                    carArray.getJSONObject(i).put("isSelected", true);
                }
            }
            Content.saveStringContent(Parameters.CACHE_KEY_CAR,
                    carArray.toString());
            refresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void delete() {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < modelCars.size(); i++) {
            if (!modelCars.get(i).isSelected()) {
                try {
                    jsonArray.put(carArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        Content.saveStringContent(Parameters.CACHE_KEY_CAR,
                jsonArray.toString());
        refresh();
    }

    private void addOrder() {
        orderProducts.clear();
        for (int i = 0; i < modelCars.size(); i++) {
            if (modelCars.get(i).isSelected()) {
                ModelProduct product = modelCars.get(i).getProduct();
                if (priceMap.containsKey(product.getId())) {
                    ModelListPrice price = priceMap.get(product.getId());
                    if (price.getPrice() > 0) {
                        if (price.getNum() < modelCars.get(i).getProductCount()) {
                            Notify.show("商品库存不足，无法下单");
                            return;
                        }
                    } else {
                        Notify.show("有商品信息错误，无法下单");
                        return;
                    }
                } else {
                    Notify.show("有商品信息错误，无法下单");
                    return;
                }
                orderProducts.add(modelCars.get(i));
            }
        }
        if (orderProducts.size() > 0) {
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
        }
    }

    private void showUserInfo() {
        if (user != null) {
            userNameEditText.setText(user.getRealname());
            userAddressEditText.setText(user.getAddress());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.car_selectall:
                selectAll();
                break;

            case R.id.car_delete:
                delete();
                break;

            default:
                break;
        }
    }

    private void getProductPrice(String productIds) {
        ProductTask.getProductPrice(getActivity(), productIds,
                new OnNetworkListener() {

                    @Override
                    public void onSuccess(NetworkMissionMessage requestMessage) {
                        super.onSuccess(requestMessage);
                        if (!TextUtils.isEmpty(requestMessage.getResult())) {
                            JSONObject object;
                            try {
                                object = new JSONObject(requestMessage
                                        .getResult());
                                if (object.getString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
                                    JSONArray priceArray = object
                                            .optJSONArray("dataList");
                                    if (priceArray != null) {
                                        for (int i = 0; i < priceArray.length(); i++) {
                                            ModelListPrice priceList = new ModelListPrice(
                                                    priceArray.getJSONObject(i));
                                            priceMap.put(
                                                    priceList.getProductId(),
                                                    priceList);
                                        }
                                        countTotalPrice();
                                        carAdapter.notifyDataSetChanged();
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
            return modelCars.size();
        }

        @Override
        public Object getItem(int position) {
            return modelCars.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int index = position;
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_car, null);
                holder = new ViewHolder();
                holder.addButton = (ImageView) convertView
                        .findViewById(R.id.list_car_count_add);
                holder.countText = (TextView) convertView
                        .findViewById(R.id.list_car_count);
                holder.deleteButton = (ImageView) convertView
                        .findViewById(R.id.list_car_count_delete);
                holder.goodNameText = (TextView) convertView
                        .findViewById(R.id.list_car_title);
                holder.goodsImage = (ImageView) convertView
                        .findViewById(R.id.list_car_image);
                holder.goodsPriceText = (TextView) convertView
                        .findViewById(R.id.list_car_price);
                holder.guigeText = (TextView) convertView
                        .findViewById(R.id.list_car_guige);
                holder.stateText = (TextView) convertView
                        .findViewById(R.id.list_car_state);
                holder.selectButton = (LinearLayout) convertView
                        .findViewById(R.id.list_car_select);
                holder.selection = (ImageView) convertView
                        .findViewById(R.id.list_car_selection);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.addButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    addCount(index);
                }
            });
            holder.deleteButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    deleteCount(index);
                }
            });
            holder.selectButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    select(index);
                }
            });
            ModelCar modelCar = modelCars.get(position);
            ModelProduct product = modelCar.getProduct();

            holder.countText.setText(modelCar.getProductCount() + "");
            if (modelCar.isSelected()) {
                holder.selection
                        .setImageResource(R.drawable.iconfont_check_checked);
            } else {
                holder.selection
                        .setImageResource(R.drawable.iconfont_check_normal);
            }

            holder.goodNameText.setText(product.getProductName());
            holder.guigeText.setText(product.getAttName());
            imageLoader.displayImage(getSmallImageUrl(product.getImg()),
                    holder.goodsImage);

            if (priceMap.containsKey(product.getId())) {
                ModelListPrice price = priceMap.get(product.getId());
                holder.goodsPriceText.setText("¥"
                        + decimalFormat.format(price.getPrice()));
                if (price.getNum() > 0) {
                    if (price.getNum() < 5) {
                        holder.stateText.setText("仅剩" + price.getNum()
                                + product.getUnit());
                    } else {
                        holder.stateText.setText("有货");
                    }
                } else {
                    holder.stateText.setText("无货");
                }
            }
            return convertView;
        }

        class ViewHolder {
            ImageView goodsImage, addButton, deleteButton;
            TextView goodNameText, goodsPriceText, guigeText, countText,
                    stateText;
            LinearLayout selectButton;
            ImageView selection;
        }
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

    private void buyProduct() {
        NetworkContent networkContent = new NetworkContent(API.API_ORDER_ADD);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters("userNumber", user.getUseraccount());
        networkContent.addParameters("customerName", userNameEditText.getText()
                .toString());
        networkContent.addParameters("phone", userPhoneEditText.getText()
                .toString());
        networkContent.addParameters("shippingaddress", userAddressEditText
                .getText().toString());
        networkContent.addParameters("totalMoney", totalMoney + "");
        try {
            JSONArray orderArray = new JSONArray();
            for (int i = 0; i < orderProducts.size(); i++) {
                ModelProduct product = orderProducts.get(i).getProduct();
                if (priceMap.containsKey(product.getId())) {
                    JSONObject object = new JSONObject();
                    object.put("productIds", product.getId());
                    object.put("shopNum", orderProducts.get(i)
                            .getProductCount());
                    object.put("price", product.getPrice());
                    object.put("isIntegralMall", 0);
                    orderArray.put(object);
                }
            }
            networkContent.addParameters("data", orderArray.toString());
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
                                if (object.optString("state").equalsIgnoreCase(
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
}
