package yitgogo.consumer.product.ui;

import android.os.AsyncTask;
import android.os.Bundle;
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
import com.smartown.controller.shoppingcart.DataBaseHelper;
import com.smartown.controller.shoppingcart.ModelShoppingCart;
import com.smartown.controller.shoppingcart.ShoppingCartController;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.local.ui.ShoppingCarLocalFragment;
import yitgogo.consumer.order.ui.ShoppingCarPlatformBuyFragment;
import yitgogo.consumer.product.model.ModelProduct;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.Notify;

public class ShoppingCarFragment extends BaseNotifyFragment {

    LinearLayout normalLayout;
    ListView carList;
    List<ModelShoppingCart> shoppingCarts;
    HashMap<String, ModelListPrice> priceMap;
    CarAdapter carAdapter;
    TextView selectAllButton;
    boolean allSelected = true;

    TextView totalPriceTextView, buyButton;
    double totalMoney = 0;

    boolean confirm = false;

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
        //如果不是跳转到确认订单，只是屏幕关闭
        if (!confirm) {
            ShoppingCartController.getInstance().saveChangedShoppingCart(DataBaseHelper.tableCarPlatform, shoppingCarts);
        }
        confirm = false;
    }

    private void init() {
        shoppingCarts = new ArrayList<>();
        priceMap = new HashMap<>();
        carAdapter = new CarAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ShoppingCarFragment.class.getName());
        initShoppingCart();
    }

    protected void findViews() {

        carList = (ListView) contentView.findViewById(R.id.car_list);
        normalLayout = (LinearLayout) contentView.findViewById(R.id.normal_layout);
        selectAllButton = (TextView) contentView.findViewById(R.id.car_selectall);
        totalPriceTextView = (TextView) contentView.findViewById(R.id.car_total);
        buyButton = (TextView) contentView.findViewById(R.id.car_buy);

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
                        deleteSelectedCarts();
                    }
                });
    }

    @Override
    protected void registerViews() {
        selectAllButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAll();
            }
        });
        buyButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmOrder();
            }
        });
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
            new GetPriceList().execute(stringBuilder.toString());
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

    private void confirmOrder() {
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
            if (User.getUser().isLogin()) {
                //跳转到确认订单界面，confirm设为true，跳过onpause的数据保存，使用异步任务保存数据后在跳转，延时较长
                confirm = true;
                new SavaCar().execute();
            } else {
                Notify.show("请先登录");
                jump(UserLoginFragment.class.getName(), "登录");
            }
        } else {
            Notify.show("请勾选要购买的商品");
        }
    }

    private void errorProductInfo(ModelShoppingCart shoppingCart) {
        try {
            ModelProduct product = new ModelProduct(new JSONObject(shoppingCart.getProductObject()));
            Notify.show("商品“" + product.getProductName() + "”信息有误，不能购买。");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

            ModelProduct product = null;
            try {
                product = new ModelProduct(new JSONObject(shoppingCarts.get(position).getProductObject()));

                holder.countText.setText(String.valueOf(shoppingCarts.get(position).getBuyCount()));
                holder.selection.setChecked(shoppingCarts.get(position).isSelected());

                holder.goodNameText.setText(product.getProductName());
                holder.guigeText.setText(product.getAttName());
                ImageLoader.getInstance().displayImage(product.getImg(), holder.goodsImage);

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
            TextView goodNameText, goodsPriceText, guigeText, countText, stateText;
            FrameLayout selectButton;
            CheckBox selection;
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
            jump(ShoppingCarPlatformBuyFragment.class.getName(), "确认订单");
        }
    }

    class GetPriceList extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... value) {
            List<NameValuePair> valuePairs = new ArrayList<>();
            valuePairs.add(new BasicNameValuePair("jmdId", Store.getStore().getStoreId()));
            valuePairs.add(new BasicNameValuePair("productId", value[0]));
            return netUtil.postWithoutCookie(API.API_PRICE_LIST, valuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray priceArray = object.optJSONArray("dataList");
                        if (priceArray != null) {
                            for (int i = 0; i < priceArray.length(); i++) {
                                ModelListPrice priceList = new ModelListPrice(priceArray.getJSONObject(i));
                                priceMap.put(priceList.getProductId(), priceList);
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
    }

}
