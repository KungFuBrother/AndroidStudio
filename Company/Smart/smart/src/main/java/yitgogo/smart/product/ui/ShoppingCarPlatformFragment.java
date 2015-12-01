package yitgogo.smart.product.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import java.util.Iterator;
import java.util.List;

import smartown.controller.shoppingcart.DataBaseHelper;
import smartown.controller.shoppingcart.ModelShoppingCart;
import smartown.controller.shoppingcart.ShoppingCartController;
import yitgogo.smart.BaseNotifyFragment;
import yitgogo.smart.home.model.ModelListPrice;
import yitgogo.smart.product.model.ModelProduct;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.Notify;

public class ShoppingCarPlatformFragment extends BaseNotifyFragment {

    // 购物车部分
    ListView carList;
    List<ModelShoppingCart> shoppingCarts;
    HashMap<String, ModelListPrice> priceMap;
    CarAdapter carAdapter;
    TextView selectAllButton, deleteButton;
    boolean allSelected = true;

    TextView totalPriceTextView, buyButton;
    double totalMoney = 0;

    boolean confirm = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shopping_car_platform);
        init();
        findViews();
    }

    private void init() {
        shoppingCarts = new ArrayList<>();
        priceMap = new HashMap<>();
        carAdapter = new CarAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShoppingCarPlatformFragment.class.getName());
        initShoppingCart();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ShoppingCarPlatformFragment.class.getName());
        //如果不是跳转到确认订单，只是屏幕关闭
        if (!confirm) {
            ShoppingCartController.getInstance().saveChangedShoppingCart(DataBaseHelper.tableCarPlatform, shoppingCarts);
        }
        confirm = false;
    }

    protected void findViews() {
        carList = (ListView) contentView.findViewById(R.id.car_list);
        selectAllButton = (TextView) contentView
                .findViewById(R.id.car_selectall);
        deleteButton = (TextView) contentView.findViewById(R.id.car_delete);
        totalPriceTextView = (TextView) contentView
                .findViewById(R.id.car_total);

        buyButton = (TextView) contentView.findViewById(R.id.car_buy);

        initViews();
        registerViews();
    }

    protected void initViews() {
        carList.setAdapter(carAdapter);
    }

    @Override
    protected void registerViews() {
        selectAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll();
            }
        });
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteSelectedCarts();
            }
        });
        buyButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                int selectedCount = 0;
                for (int i = 0; i < shoppingCarts.size(); i++) {
                    if (shoppingCarts.get(i).isSelected()) {
                        if (priceMap.containsKey(shoppingCarts.get(i).getProductId())) {
                            double price = priceMap.get(shoppingCarts.get(i).getProductId()).getPrice();
                            if (price > 0) {
                                selectedCount++;
                            } else {
                                errorProductInfo(shoppingCarts.get(i));
                                return;
                            }
                        } else {
                            errorProductInfo(shoppingCarts.get(i));
                            return;
                        }
                    }
                }
                if (selectedCount > 0) {
                    //跳转到确认订单界面，confirm设为true，跳过onpause的数据保存，使用异步任务保存数据后在跳转，延时较长
                    confirm = true;
                    new SavaCar().execute();
                } else {
                    Notify.show("请勾选要购买的商品");
                }
            }
        });
    }

    private void errorProductInfo(ModelShoppingCart shoppingCart) {
        try {
            ModelProduct product = new ModelProduct(new JSONObject(shoppingCart.getProductObject()));
            Notify.show("商品“" + product.getProductName() + "”信息有误，不能购买。");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initShoppingCart() {
        shoppingCarts = ShoppingCartController.getInstance().getAllProducts(DataBaseHelper.tableCarPlatform);
        carAdapter.notifyDataSetChanged();
        totalPriceTextView.setText("");
        if (shoppingCarts.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < shoppingCarts.size(); i++) {
                if (i > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(shoppingCarts.get(i).getProductId());
            }
            getProductPrice(stringBuilder.toString());
        } else {
            loadingEmpty("购物车还没有添加商品");
        }
    }

    private void addCount(int position) {
        if (priceMap.containsKey(shoppingCarts.get(position).getProductId())) {
            ModelListPrice price = priceMap.get(shoppingCarts.get(position).getProductId());
            int originalCount = shoppingCarts.get(position).getBuyCount();
            if (price.getNum() > originalCount) {
                shoppingCarts.get(position).setBuyCount(originalCount + 1);
                carAdapter.notifyDataSetChanged();
                countTotalPrice();
            } else {
                Notify.show("库存不足");
            }
        }
    }

    private void deleteCount(int position) {
        int originalCount = shoppingCarts.get(position).getBuyCount();
        if (originalCount > 1) {
            shoppingCarts.get(position).setBuyCount(originalCount - 1);
            carAdapter.notifyDataSetChanged();
            countTotalPrice();
        }
    }

    private void select(int position) {
        shoppingCarts.get(position).setIsSelected(!shoppingCarts.get(position).isSelected());
        carAdapter.notifyDataSetChanged();
        countTotalPrice();
    }

    private void countTotalPrice() {
        allSelected = true;
        totalMoney = 0;
        for (int i = 0; i < shoppingCarts.size(); i++) {
            if (shoppingCarts.get(i).isSelected()) {
                if (priceMap.containsKey(shoppingCarts.get(i).getProductId())) {
                    double price = priceMap.get(shoppingCarts.get(i).getProductId()).getPrice();
                    int count = shoppingCarts.get(i).getBuyCount();
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
        // 当前已全选，改为全不选
        if (allSelected) {
            for (int i = 0; i < shoppingCarts.size(); i++) {
                shoppingCarts.get(i).setIsSelected(false);
            }
        } else {
            for (int i = 0; i < shoppingCarts.size(); i++) {
                shoppingCarts.get(i).setIsSelected(true);
            }
        }
        carAdapter.notifyDataSetChanged();
        countTotalPrice();
    }

    private void deleteSelectedCarts() {
        Iterator<ModelShoppingCart> shoppingCartIterator = shoppingCarts.iterator();
        while (shoppingCartIterator.hasNext()) {
            ModelShoppingCart shoppingCart = shoppingCartIterator.next();
            if (shoppingCart.isSelected()) {
                shoppingCartIterator.remove();
            }
        }
        carAdapter.notifyDataSetChanged();
        countTotalPrice();
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
            return shoppingCarts.size();
        }

        @Override
        public Object getItem(int position) {
            return shoppingCarts.get(position);
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
            ModelProduct product = null;
            try {
                product = new ModelProduct(new JSONObject(shoppingCarts.get(position).getProductObject()));
                holder.countText.setText(String.valueOf(shoppingCarts.get(position).getBuyCount()));
                if (shoppingCarts.get(position).isSelected()) {
                    holder.selection.setImageResource(R.drawable.iconfont_check_checked);
                } else {
                    holder.selection.setImageResource(R.drawable.iconfont_check_normal);
                }

                holder.goodNameText.setText(product.getProductName());
                holder.guigeText.setText(product.getAttName());
                imageLoader.displayImage(getSmallImageUrl(product.getImg()), holder.goodsImage);

                if (priceMap.containsKey(product.getId())) {
                    ModelListPrice price = priceMap.get(product.getId());
                    holder.goodsPriceText.setText("¥" + decimalFormat.format(price.getPrice()));
                    if (price.getNum() > 0) {
                        if (price.getNum() < 5) {
                            holder.stateText.setText("仅剩" + price.getNum() + product.getUnit());
                        } else {
                            holder.stateText.setText("有货");
                        }
                    } else {
                        holder.stateText.setText("无货");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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

    class SavaCar extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ShoppingCartController.getInstance().saveChangedShoppingCart(DataBaseHelper.tableCarPlatform, shoppingCarts);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideLoading();
            openWindow(ShoppingCarPlatformBuyFragment.class.getName(), "确认订单");
        }
    }

}
