package yitgogo.consumer.suning.ui;

import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.local.ui.ShoppingCarLocalFragment;
import yitgogo.consumer.suning.model.GetNewSignature;
import yitgogo.consumer.suning.model.ModelProductPrice;
import yitgogo.consumer.suning.model.ModelSuningCar;
import yitgogo.consumer.suning.model.SuningCarController;
import yitgogo.consumer.suning.model.SuningManager;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.Notify;

public class ShoppingCarFragment extends BaseNotifyFragment {

    LinearLayout normalLayout;
    ListView carList;
    List<ModelSuningCar> suningCars;
    HashMap<String, ModelProductPrice> priceHashMap = new HashMap<>();
    CarAdapter carAdapter;
    TextView selectAllButton, totalPriceTextView, addOrderButton;

    double goodsMoney = 0;

    String priceResult = "";
    HashMap<String, String> states = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_shopping_car_suning);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ShoppingCarFragment.class.getName());
    }

    private void init() {
        suningCars = new ArrayList<>();
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
        addTextButton("易商城", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(yitgogo.consumer.product.ui.ShoppingCarFragment.class.getName(), "易商城购物车");
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
                        SuningCarController.deleteSelectedCars();
                        refresh();
                    }
                });
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
        addOrderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrder();
            }
        });
    }

    private void refresh() {
        suningCars = SuningCarController.getSuningCars();
        carAdapter.notifyDataSetChanged();
        totalPriceTextView.setText("");
        if (suningCars.size() > 0) {
            new GetProductStock().execute();
        } else {
            loadingEmpty("购物车还没有添加商品");
        }
    }

    private void addOrder() {
        if (User.getUser().isLogin()) {
            if (!SuningCarController.getSelectedCars().isEmpty()) {
                if (goodsMoney > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("price", priceResult);
                    jump(ShoppingCarBuyFragment.class.getName(), "确认订单", bundle);
                } else {
                    Notify.show("有商品信息有误，暂不能购买，请取消勾选后再试");
                }
            } else {
                Notify.show("请勾选要购买的商品");
            }
        } else {
            Notify.show("请先登录");
            jump(UserLoginFragment.class.getName(), "登录");
        }
    }

    private void countTotalPrice() {
        List<ModelSuningCar> errorCars = new ArrayList<>();
        goodsMoney = 0;
        double sendMoney = 0;
        for (int i = 0; i < suningCars.size(); i++) {
            if (priceHashMap.containsKey(suningCars.get(i).getProductDetail().getSku())) {
                //如果查询到价格
                double price = priceHashMap.get(suningCars.get(i).getProductDetail().getSku()).getPrice();
                if (price > 0) {
                    //如果价格正常
                    if (states.containsKey(suningCars.get(i).getProductDetail().getSku())) {
                        //如果有库存状态
                        String state = states.get(suningCars.get(i).getProductDetail().getSku());
                        if (state.equals("00")) {
                            //如果有货
                            if (suningCars.get(i).isSelected()) {
                                long count = suningCars.get(i).getProductCount();
                                goodsMoney += count * price;
                            }
                        } else {
                            //无货
                            errorCars.add(suningCars.get(i));
                        }
                    } else {
                        //为查询到库存状态
                        errorCars.add(suningCars.get(i));
                    }
                } else {
                    //价格异常
                    errorCars.add(suningCars.get(i));
                }
            } else {
                //如果没查询到价格
                errorCars.add(suningCars.get(i));
            }
        }
        if (goodsMoney > 0 & goodsMoney < 69) {
            sendMoney = 5;
        }
        totalPriceTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(goodsMoney + sendMoney));
        carAdapter.notifyDataSetChanged();
        if (!errorCars.isEmpty()) {
            for (int i = 0; i < errorCars.size(); i++) {
                SuningCarController.delete(errorCars.get(i).getProductDetail().getSku());
            }
            refresh();
        }
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
            final ModelSuningCar suningCar = suningCars.get(position);
            ImageLoader.getInstance().displayImage(suningCar.getProductDetail().getImage(),
                    holder.goodsImage);
            holder.countText.setText(suningCar.getProductCount() + "");
            holder.selection.setChecked(suningCar.isSelected());
            holder.goodNameText.setText(suningCar.getProductDetail().getName());
            if (priceHashMap.containsKey(suningCar.getProductDetail().getSku())) {
                if (priceHashMap.get(suningCar.getProductDetail().getSku()).getPrice() > 0) {
                    holder.goodsPriceText.setText(Parameters.CONSTANT_RMB
                            + decimalFormat.format(priceHashMap.get(suningCar.getProductDetail().getSku()).getPrice()));
                } else {
                    holder.goodsPriceText.setHint("暂未设置价格");
                }
            } else {
                holder.goodsPriceText.setHint("暂未设置价格");
            }
            if (states.containsKey(suningCar.getProductDetail().getSku())) {
                String state = states.get(suningCar.getProductDetail().getSku());
                if (state.equals("00")) {
                    holder.stateText.setText("有货");
                } else if (state.equals("01")) {
                    holder.stateText.setHint("暂不销售");
                } else {
                    holder.stateText.setHint("无货");
                }
            } else {
                holder.stateText.setHint("无货");
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
                    if (suningCar.getProductCount() > 1) {
                        SuningCarController.deleteCount(suningCar.getProductDetail().getSku());
                        refresh();
                    }
                }
            });
            holder.selectButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (priceHashMap.containsKey(suningCar.getProductDetail().getSku())) {
                        if (priceHashMap.get(suningCar.getProductDetail().getSku()).getPrice() > 0) {
                            if (states.containsKey(suningCar.getProductDetail().getSku())) {
                                String state = states.get(suningCar.getProductDetail().getSku());
                                if (state.equals("00")) {
                                    SuningCarController.select(suningCar.getProductDetail().getSku());
                                    refresh();
                                } else if (state.equals("01")) {
                                    Notify.show("此商品暂不销售");
                                } else {
                                    Notify.show("此商品无货");
                                }
                            } else {
                                Notify.show("此商品无货");
                            }
                        } else {
                            Notify.show("此商品暂未设置价格");
                        }
                    } else {
                        Notify.show("此商品暂未设置价格");
                    }
                }
            });
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

    class GetSuningProductPrice extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONArray dataArray = new JSONArray();
            for (int i = 0; i < suningCars.size(); i++) {
                dataArray.put(suningCars.get(i).getProductDetail().getSku());
            }
            JSONObject data = new JSONObject();
            try {
                data.put("accessToken", SuningManager.getSignature().getToken());
                data.put("appKey", SuningManager.appKey);
                data.put("v", SuningManager.version);
                data.put("cityId", SuningManager.getSuningAreas().getCity().getCode());
                data.put("sku", dataArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
            return netUtil.postWithoutCookie(API.API_SUNING_PRODUCT_PRICE, nameValuePairs, false, false);
        }

        /**
         * {"result":[{"skuId":"108246148","price":15000.00}],"isSuccess":true,"returnMsg":"查询成功。"}
         */
        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (SuningManager.isSignatureOutOfDate(result)) {
                GetNewSignature getNewSignature = new GetNewSignature() {
                    @Override
                    protected void onPreExecute() {
                        showLoading();
                    }

                    @Override
                    protected void onPostExecute(Boolean isSuccess) {
                        hideLoading();
                        if (isSuccess) {
                            new GetSuningProductPrice().execute();
                        }
                    }
                };
                getNewSignature.execute();
                return;
            }
            if (!TextUtils.isEmpty(result)) {
                priceResult = result;
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
                            carAdapter.notifyDataSetChanged();
                            countTotalPrice();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class GetProductStock extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            for (int i = 0; i < suningCars.size(); i++) {
                JSONObject data = new JSONObject();
                try {
                    data.put("accessToken", SuningManager.getSignature().getToken());
                    data.put("appKey", SuningManager.appKey);
                    data.put("v", SuningManager.version);
                    data.put("cityId", SuningManager.getSuningAreas().getCity().getCode());
                    data.put("countyId", SuningManager.getSuningAreas().getDistrict().getCode());
                    data.put("sku", suningCars.get(i).getProductDetail().getSku());
                    data.put("num", suningCars.get(i).getProductCount());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
                String result = netUtil.postWithoutCookie(API.API_SUNING_PRODUCT_STOCK, nameValuePairs, false, false);
                //令牌过期
                if (SuningManager.isSignatureOutOfDate(result)) {
                    return 2;
                }
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optBoolean("isSuccess")) {
                            states.put(suningCars.get(i).getProductDetail().getSku(), object.optString("state"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return 1;
                    }
                }
            }
            //获取数据成功
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            hideLoading();
            switch (result) {

                case 0:
                    if (states.isEmpty()) {
                        //无商品库存状态
                        suningCars.clear();
                        carAdapter.notifyDataSetChanged();
                        loadingEmpty("查询商品库存状态失败");
                    } else {
                        //有状态信息
                        carAdapter.notifyDataSetChanged();
                        if (priceHashMap.isEmpty()) {
                            new GetSuningProductPrice().execute();
                        } else {
                            countTotalPrice();
                        }
                    }
                    break;

                case 1:
                    suningCars.clear();
                    carAdapter.notifyDataSetChanged();
                    loadingEmpty("查询商品库存状态失败");
                    break;

                case 2:
                    GetNewSignature getNewSignature = new GetNewSignature() {
                        @Override
                        protected void onPreExecute() {
                            showLoading();
                        }

                        @Override
                        protected void onPostExecute(Boolean isSuccess) {
                            hideLoading();
                            if (isSuccess) {
                                new GetProductStock().execute();
                            }
                        }
                    };
                    getNewSignature.execute();
                    break;

                default:
                    loadingEmpty();
                    break;
            }
        }
    }

}
