package yitgogo.smart.suning.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
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
import yitgogo.smart.suning.model.API_SUNING;
import yitgogo.smart.suning.model.GetNewSignature;
import yitgogo.smart.suning.model.ModelProductClass;
import yitgogo.smart.suning.model.ModelProductDetail;
import yitgogo.smart.suning.model.ModelProductPool;
import yitgogo.smart.suning.model.ModelProductPrice;
import yitgogo.smart.suning.model.SuningManager;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.view.InnerGridView;
import yitgogo.smart.view.Notify;


public class SuningProductFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerGridView productGridView;

    TextView productAreaTextView;
    LinearLayout productAreaSelectButton;

    ModelProductClass productClass = new ModelProductClass();

    List<ModelProductPool> productPools = new ArrayList<>();
    List<ModelProductDetail> productDetails = new ArrayList<>();
    HashMap<String, ModelProductPrice> priceHashMap = new HashMap<>();

    ProductAdapter productAdapter;

    SuningClassesFragment classesFragment = new SuningClassesFragment() {

        @Override
        public void onClassSelected(ModelProductClass selectedProductClass) {
            if (productClass == selectedProductClass) return;
            productClass = selectedProductClass;
            new GetSuningProducts().execute();
        }

    };

    int pageSize = 12, pageNo = 0;
    int totalPage = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_suning_product_list);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SuningProductFragment.class.getName());
        if (!TextUtils.isEmpty(SuningManager.getSuningAreas().getTown().getCode())) {
            showSuningAreas();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SuningProductFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (TextUtils.isEmpty(SuningManager.getSuningAreas().getTown().getCode())) {
            SuningAreaDialog suningAreaDialog = new SuningAreaDialog() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    super.onDismiss(dialog);
                    if (TextUtils.isEmpty(SuningManager.getSuningAreas().getTown().getCode())) {
                        getActivity().finish();
                    } else {
                        showSuningAreas();
                        getFragmentManager().beginTransaction().replace(R.id.suning_product_list_class, classesFragment).commit();
                    }
                }
            };
            suningAreaDialog.show(getFragmentManager(), null);
        } else {
            showSuningAreas();
            getFragmentManager().beginTransaction().replace(R.id.suning_product_list_class, classesFragment).commit();
        }
    }

    private void init() {
        measureScreen();
        productAdapter = new ProductAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.suning_product_list_scroll);
        productGridView = (InnerGridView) contentView
                .findViewById(R.id.suning_product_list);
        productAreaTextView = (TextView) contentView
                .findViewById(R.id.suning_product_area);
        productAreaSelectButton = (LinearLayout) contentView
                .findViewById(R.id.suning_product_area_select);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        refreshScrollView.setMode(Mode.BOTH);
        productGridView.setAdapter(productAdapter);
    }

    @Override
    protected void registerViews() {
        refreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetSuningProducts().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (pageNo >= totalPage) {
                    refreshScrollView.setMode(Mode.PULL_FROM_START);
                    refreshScrollView.onRefreshComplete();
                    return;
                }
                new GetSuningProductDetail().execute();
            }
        });
        productGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (priceHashMap.containsKey(productDetails.get(arg2).getSku())) {
                    if (priceHashMap.get(productDetails.get(arg2).getSku()).getPrice() > 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("product", productDetails.get(arg2).getJsonObject().toString());
                        bundle.putString("price", priceHashMap.get(productDetails.get(arg2).getSku()).getJsonObject().toString());
                        openWindow(ProductDetailFragment.class.getName(), productDetails.get(arg2).getName(), bundle);
                    } else {
                        Notify.show("此商品暂未设置价格");
                    }
                } else {
                    Notify.show("此商品暂未设置价格");
                }
            }
        });
        productAreaSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SuningAreaDialog suningAreaDialog = new SuningAreaDialog() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        super.onDismiss(dialog);
                        if (TextUtils.isEmpty(SuningManager.getSuningAreas().getTown().getCode())) {
                            getActivity().finish();
                        } else {
                            showSuningAreas();
                            new GetSuningProvince().execute();
                        }
                    }
                };
                suningAreaDialog.show(getFragmentManager(), null);
            }
        });
    }

    class GetSuningProducts extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            refreshScrollView.setMode(Mode.BOTH);
            productPools.clear();
            priceHashMap.clear();
            productDetails.clear();
            productAdapter.notifyDataSetChanged();
            pageNo = 0;
            totalPage = 0;
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONObject data = new JSONObject();
            try {
                data.put("accessToken", SuningManager.getSignature().getToken());
                data.put("appKey", SuningManager.appKey);
                data.put("v", SuningManager.version);
                data.put("categoryId", productClass.getCategoryId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
            return MissionController.post(API_SUNING.API_PRODUCT_LIST, nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            refreshScrollView.onRefreshComplete();
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
                            new GetSuningProducts().execute();
                        }
                    }
                };
                getNewSignature.execute();
                return;
            }
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optBoolean("isSuccess")) {
                        JSONArray array = object.optJSONArray("prods");
                        if (array != null) {
                            for (int i = 0; i < array.length(); i++) {
                                productPools.add(new ModelProductPool(array.optJSONObject(i)));
                            }
                            if (productPools.isEmpty()) {
                                //无商品
                                loadingEmpty();
                            } else {
                                //有商品
                                if (productPools.size() % pageSize == 0) {
                                    totalPage = productPools.size() / pageSize;
                                } else {
                                    totalPage = productPools.size() / pageSize + 1;
                                }
                                new GetSuningProductDetail().execute();
                            }
                            return;
                        }
                    }
                    Notify.show(object.optString("returnMsg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            loadingEmpty();
        }
    }

    class GetSuningProductDetail extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            showLoading();
            pageNo++;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            List<ModelProductPool> pools = new ArrayList<>();
            if (pageNo < totalPage) {
                pools = productPools.subList((pageNo - 1) * 12, pageNo * 12);
            } else {
                pools = productPools.subList((pageNo - 1) * 12, productPools.size());
            }
            for (int i = 0; i < pools.size(); i++) {
                JSONObject data = new JSONObject();
                try {
                    data.put("accessToken", SuningManager.getSignature().getToken());
                    data.put("appKey", SuningManager.appKey);
                    data.put("v", SuningManager.version);
                    data.put("sku", pools.get(i).getSku());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("data", data.toString()));
                String result = MissionController.post(API_SUNING.API_PRODUCT_DETAIL, nameValuePairs);
                //令牌过期
                if (SuningManager.isSignatureOutOfDate(result)) {
                    return 2;
                }
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optBoolean("isSuccess")) {
                            ModelProductDetail productDetail = new ModelProductDetail(object);
                            if (productDetail.getState() == 1) {
                                productDetails.add(productDetail);
                            }
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
            refreshScrollView.onRefreshComplete();
            switch (result) {

                case 0:
                    if (productDetails.isEmpty()) {
                        //无商品
                        loadingEmpty();
                    } else {
                        //有商品
                        productAdapter.notifyDataSetChanged();
                        new GetSuningProductPrice().execute();
                    }
                    break;

                case 1:
                    loadingEmpty();
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
                                new GetSuningProductDetail().execute();
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

    class GetSuningProductPrice extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONArray dataArray = new JSONArray();
            List<ModelProductPool> pools = new ArrayList<>();
            if (pageNo < totalPage) {
                pools = productPools.subList((pageNo - 1) * 12, pageNo * 12);
            } else {
                pools = productPools.subList((pageNo - 1) * 12, productPools.size());
            }
            for (int i = 0; i < pools.size(); i++) {
                dataArray.put(pools.get(i).getSku());
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
            return MissionController.post(API_SUNING.API_PRODUCT_PRICE, nameValuePairs);
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
                            productAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return productDetails.size();
        }

        @Override
        public Object getItem(int i) {
            return productDetails.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_product_grid, null);
                viewHolder = new ViewHolder();
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.list_product_image);
                viewHolder.priceTextView = (TextView) convertView
                        .findViewById(R.id.list_product_price);
                viewHolder.nameTextView = (TextView) convertView
                        .findViewById(R.id.list_product_name);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 256);
                viewHolder.imageView.setLayoutParams(layoutParams);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ModelProductDetail productDetail = productDetails.get(i);
            ImageLoader.getInstance().displayImage(productDetail.getImage(), viewHolder.imageView);
            viewHolder.nameTextView.setText(productDetail.getName());
            if (priceHashMap.containsKey(productDetail.getSku())) {
                if (priceHashMap.get(productDetail.getSku()).getPrice() > 0) {
                    viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
                            + decimalFormat.format(priceHashMap.get(productDetail.getSku()).getPrice()));
                } else {
                    viewHolder.priceTextView.setHint("暂未设置价格");
                }
            } else {
                viewHolder.priceTextView.setHint("暂未设置价格");
            }
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView nameTextView;
            TextView priceTextView;
        }

    }


    private void showSuningAreas() {
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
        productAreaTextView.setText(stringBuilder.toString());
    }

}
