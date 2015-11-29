package yitgogo.smart.suning.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitgogo.smart.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.model.ModelMachineArea;
import yitgogo.smart.order.model.User;
import yitgogo.smart.order.ui.PayFragment;
import yitgogo.smart.suning.model.ModelProductPrice;
import yitgogo.smart.suning.model.ModelSuningCar;
import yitgogo.smart.suning.model.ModelSuningOrderResult;
import yitgogo.smart.suning.model.SuningCarController;
import yitgogo.smart.suning.model.SuningManager;
import yitgogo.smart.task.OrderTask;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.Notify;

public class ShoppingCarBuyFragment extends BaseNotifyFragment {
    // 购物车部分
    ListView carList;
    List<ModelSuningCar> suningCars = new ArrayList<>();
    HashMap<String, ModelProductPrice> priceHashMap = new HashMap<>();

    CarAdapter carAdapter;
    TextView selectAllButton, deleteButton;

    EditText userPhoneEditText, userNameEditText, userAddressEditText;
    TextView userAreaTextView;

    TextView totalPriceTextView, buyButton;
    double goodsMoney = 0;

    User user = new User();
    ModelMachineArea machineArea = new ModelMachineArea();

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
        MobclickAgent.onPageEnd(ShoppingCarBuyFragment.class.getName());
    }

    private void init() {
        initPrice();
        suningCars = new ArrayList<>();
        carAdapter = new CarAdapter();
    }

    private void initPrice() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("price")) {
                String result = bundle.getString("price");
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optBoolean("isSuccess")) {
                            JSONArray array = object.optJSONArray("result");
                            if (array != null) {
                                for (int j = 0; j < array.length(); j++) {
                                    ModelProductPrice productPrice = new
                                            ModelProductPrice(array.optJSONObject(j));
                                    priceHashMap.put(productPrice.getSkuId(), productPrice);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShoppingCarBuyFragment.class.getName());
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
        TextView headerTextView = new TextView(getActivity());
        headerTextView.setText("运费5元，云商城购物满69元免运费");
        headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, 16);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setTextColor(getResources().getColor(R.color.textColorSecond));
        carList.addHeaderView(headerTextView);
        showArea();
    }

    @Override
    protected void registerViews() {
        selectAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SuningCarController.selectAll();
                refresh();
            }
        });
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SuningCarController.deleteSelectedCars();
                refresh();
            }
        });
        userAreaTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SuningAreaDialog suningAreaDialog = new SuningAreaDialog() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        super.onDismiss(dialog);
                        getActivity().finish();
                        Notify.show("请重新下单");
                    }
                };
                suningAreaDialog.show(getFragmentManager(), null);
            }
        });
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

    private void refresh() {
        suningCars = SuningCarController.getSuningCars();
        carAdapter.notifyDataSetChanged();
        totalPriceTextView.setText("");
        if (suningCars.size() > 0) {
            countTotalPrice();
        } else {
            loadingEmpty("购物车还没有添加商品");
        }
    }

    private void countTotalPrice() {
        goodsMoney = 0;
        double sendMoney = 0;
        for (int i = 0; i < suningCars.size(); i++) {
            if (suningCars.get(i).isSelected()) {
                long count = suningCars.get(i).getProductCount();
                if (priceHashMap.containsKey(suningCars.get(i).getProductDetail().getSku())) {
                    double price = priceHashMap.get(suningCars.get(i).getProductDetail().getSku()).getPrice();
                    if (price > 0) {
                        goodsMoney += count * price;
                    }
                }
            }
        }
        if (goodsMoney > 0 & goodsMoney < 69) {
            sendMoney = 5;
        }
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(goodsMoney + sendMoney));
    }

    private void addOrder() {
        if (suningCars.size() > 0) {
            if (!isPhoneNumber(userPhoneEditText.getText().toString().trim())) {
                Notify.show("请填写正确的收货人电话");
            } else if (userNameEditText.length() == 0) {
                Notify.show("请填写收货人姓名");
            } else if (userAddressEditText.length() == 0) {
                Notify.show("请填写收货地址");
            } else {
                if (goodsMoney > 0) {
                    if (user.isLogin()) {
                        new Buy().execute();
                    } else {
                        registerUser();
                    }
                } else {
                    Notify.show("请勾选要购买的商品");
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

    class CarAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return suningCars.size();
        }

        @Override
        public Object getItem(int position) {
            return suningCars.get(position);
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
            final ModelSuningCar suningCar = suningCars.get(position);
            if (suningCar.isSelected()) {
                holder.selection
                        .setImageResource(R.drawable.iconfont_check_checked);
            } else {
                holder.selection
                        .setImageResource(R.drawable.iconfont_check_normal);
            }
            ImageLoader.getInstance().displayImage(suningCar.getProductDetail().getImage(),
                    holder.goodsImage);
            holder.goodNameText.setText(suningCar.getProductDetail().getName());
            holder.countText.setText(suningCar.getProductCount() + "");
            if (priceHashMap.containsKey(suningCar.getProductDetail().getSku())) {
                holder.goodsPriceText.setText(Parameters.CONSTANT_RMB
                        + decimalFormat.format(priceHashMap.get(suningCar.getProductDetail().getSku()).getPrice()));
            }
            holder.addButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    SuningCarController.addCount(suningCar.getProductDetail().getSku());
                    refresh();
                }
            });
            holder.deleteButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    SuningCarController.deleteCount(suningCar.getProductDetail().getSku());
                    refresh();
                }
            });
            holder.selectButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    SuningCarController.select(suningCar.getProductDetail().getSku());
                    refresh();
                }
            });
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
            for (int i = 0; i < suningCars.size(); i++) {
                if (priceHashMap.containsKey(suningCars.get(i).getProductDetail().getSku())) {
                    try {
                        JSONObject skuObject = new JSONObject();
                        skuObject.put("number", suningCars.get(i).getProductDetail().getSku());
                        skuObject.put("num", suningCars.get(i).getProductCount());
                        skuObject.put("price", priceHashMap.get(suningCars.get(i).getProductDetail().getSku()).getPrice());
                        skuObject.put("name", suningCars.get(i).getProductDetail().getName());
                        skuObject.put("attr", suningCars.get(i).getProductDetail().getModel());
                        skuArray.put(skuObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
                        SuningCarController.deleteSelectedCars();
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

}
