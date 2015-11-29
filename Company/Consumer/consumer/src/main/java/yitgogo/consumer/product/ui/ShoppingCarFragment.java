package yitgogo.consumer.product.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.local.ui.ShoppingCarLocalFragment;
import yitgogo.consumer.order.ui.PlatformOrderConfirmFragment;
import yitgogo.consumer.product.model.ModelCar;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;

public class ShoppingCarFragment extends BaseNetworkFragment implements
        OnClickListener {

    LinearLayout normalLayout;
    ListView carList;
    List<ModelCar> modelCars;
    HashMap<String, ModelListPrice> priceMap;
    CarAdapter carAdapter;
    TextView selectAllButton, totalPriceTextView, addOrderButton;
    JSONArray carArray;
    boolean allSelected = true;

    double totalMoney = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shopping_car);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ShoppingCarFragment.class.getName());
    }

    @Override
    protected void init() {
        modelCars = new ArrayList<>();
        priceMap = new HashMap<>();
        carAdapter = new CarAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShoppingCarFragment.class.getName());
        refresh();
    }

    protected void findViews() {
        carList = (ListView) contentView.findViewById(R.id.car_list);
        normalLayout = (LinearLayout) contentView
                .findViewById(R.id.normal_layout);
        selectAllButton = (TextView) contentView
                .findViewById(R.id.car_selectall);
        totalPriceTextView = (TextView) contentView
                .findViewById(R.id.car_total);
        addOrderButton = (TextView) contentView.findViewById(R.id.car_buy);
        initViews();
        registerViews();
    }

    protected void initViews() {
        carList.setAdapter(carAdapter);
        addTextButton("云商城", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(yitgogo.consumer.suning.ui.ShoppingCarFragment.class.getName(), "云商城购物车");
                getActivity().finish();
            }
        });
        addTextButton("本地商品", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ShoppingCarLocalFragment.class.getName(), "本地商品购物车");
                getActivity().finish();
            }
        });
        addImageButton(R.drawable.get_goods_delete, "删除",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        delete();
                    }
                });
    }

    @Override
    protected void registerViews() {
        selectAllButton.setOnClickListener(this);
        addOrderButton.setOnClickListener(this);
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
            normalLayout.setVisibility(View.VISIBLE);
            if (priceMap.isEmpty()) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < modelCars.size(); i++) {
                    if (i > 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(modelCars.get(i).getProduct().getId());
                }
                getPriceList(stringBuilder.toString());
            } else {
                countTotalPrice();
                carAdapter.notifyDataSetChanged();
            }
        } else {
            normalLayout.setVisibility(View.GONE);
            missionNodata();
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
                    ApplicationTool.showToast("库存不足");
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

    private void confirmOrder() {
        int selectedCount = 0;
        JSONArray orderArray = new JSONArray();
        for (int i = 0; i < modelCars.size(); i++) {
            if (modelCars.get(i).isSelected()) {
                selectedCount++;
                if (modelCars.get(i).isSelected()) {
                    ModelProduct product = modelCars.get(i).getProduct();
                    if (priceMap.containsKey(product.getId())) {
                        ModelListPrice price = priceMap.get(product.getId());
                        if (price.getPrice() > 0) {
                            if (price.getNum() < modelCars.get(i)
                                    .getProductCount()) {
                                ApplicationTool.showToast("商品库存不足，无法下单");
                                return;
                            }
                        } else {
                            ApplicationTool.showToast("有商品信息错误，无法下单");
                            return;
                        }
                    } else {
                        ApplicationTool.showToast("有商品信息错误，无法下单");
                        return;
                    }
                    orderArray.put(modelCars.get(i).getJsonObject());
                }
            }
        }
        if (orderArray.length() > 0) {
            Content.saveStringContent(Parameters.CACHE_KEY_ORDER_PRODUCT,
                    orderArray.toString());
            if (selectedCount > orderArray.length()) {
                ApplicationTool.showToast("已过滤异常产品");
            }
            if (User.getUser().isLogin()) {
                jump(PlatformOrderConfirmFragment.class.getName(), "确认订单");
            } else {
                ApplicationTool.showToast("请先登录");
                jump(UserLoginFragment.class.getName(), "登录");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.car_selectall:
                selectAll();
                break;

            case R.id.car_buy:
                confirmOrder();
                break;

            default:
                break;
        }
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
                holder.selectButton = (FrameLayout) convertView
                        .findViewById(R.id.list_car_select);
                holder.selection = (CheckBox) convertView
                        .findViewById(R.id.list_car_selected);
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
            holder.selection.setChecked(modelCar.isSelected());

            holder.goodNameText.setText(product.getProductName());
            holder.guigeText.setText(product.getAttName());
            ImageLoader.getInstance().displayImage(product.getImg(),
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
            FrameLayout selectButton;
            CheckBox selection;
        }
    }

    private void getPriceList(String productId) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("jmdId", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("productId", productId));
        post(API.API_PRICE_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray priceArray = object.optJSONArray("dataList");
                            if (priceArray != null) {
                                for (int i = 0; i < priceArray.length(); i++) {
                                    ModelListPrice priceList = new ModelListPrice(
                                            priceArray.getJSONObject(i));
                                    priceMap.put(priceList.getProductId(),
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

}
