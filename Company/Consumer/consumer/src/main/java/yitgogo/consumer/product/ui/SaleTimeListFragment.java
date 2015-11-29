package yitgogo.consumer.product.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.product.model.ModelSaleTimeProduct;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerGridView;

public class SaleTimeListFragment extends BaseNetworkFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerGridView productGridView;
    List<ModelSaleTimeProduct> products;
    HashMap<String, Double> prices;
    ProductAdapter productAdapter;

    String saleClassId = "";

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sale_time_list);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(SaleTimeListFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(SaleTimeListFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reload();
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("saleClassId")) {
                saleClassId = bundle.getString("saleClassId");
            }
        }
        products = new ArrayList<>();
        prices = new HashMap<>();
        productAdapter = new ProductAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.sale_time_refresh);
        productGridView = (InnerGridView) contentView
                .findViewById(R.id.sale_time_list);
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
        refreshScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        reload();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        pagenum++;
                        getSaleTime();
                    }
                });
        productGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                try {
                    Date startTime = dateFormat.parse(products.get(arg2).getStarttime());
                    if (Calendar.getInstance().getTime().before(startTime)) {
                        ApplicationTool.showToast("活动还没有开始");
                    } else {
                        showProductDetail(products.get(arg2).getProductId(), products
                                        .get(arg2).getProductName(),
                                CaptureActivity.SALE_TYPE_TIME);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void reload() {
        super.reload();
        refreshScrollView.setMode(Mode.BOTH);
        pagenum = 0;
        products.clear();
        productAdapter.notifyDataSetChanged();
        pagenum++;
        getSaleTime();
    }

    class ProductAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_sale_time,
                        null);
                holder.imageView = (ImageView) convertView
                        .findViewById(R.id.grid_product_image);
                holder.timeTextView = (TextView) convertView
                        .findViewById(R.id.grid_product_promotion);
                holder.nameTextView = (TextView) convertView
                        .findViewById(R.id.grid_product_name);
                holder.priceTextView = (TextView) convertView
                        .findViewById(R.id.grid_product_price);
                LayoutParams params = new LayoutParams(
                        LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth() / 25 * 16);
                convertView.setLayoutParams(params);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelSaleTimeProduct product = products.get(position);
            holder.nameTextView.setText(product.getProductName());
            holder.timeTextView.setText("开始时间:" + product.getStarttime());
            if (prices.containsKey(product.getProductId())) {
                holder.priceTextView.setText(Parameters.CONSTANT_RMB
                        + decimalFormat.format(prices.get(product
                        .getProductId())));
            }
            ImageLoader.getInstance()
                    .displayImage(getSmallImageUrl(product.getProtionImg()),
                            holder.imageView);
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView priceTextView, nameTextView, timeTextView;
        }
    }

    private void getSaleTime() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("pageNo", pagenum + ""));
        requestParams.add(new RequestParam("pageSize", pagesize + ""));
        requestParams.add(new RequestParam("flag", "1"));
        requestParams.add(new RequestParam("pcid", saleClassId));
        requestParams.add(new RequestParam("strno", Store.getStore()
                .getStoreNumber()));
        post(API.API_SALE_TIME_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshScrollView.onRefreshComplete();
                if (result.length() > 0) {
                    JSONObject info;
                    try {
                        info = new JSONObject(result);
                        if (info.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray productArray = info.optJSONArray("dataList");
                            if (productArray != null) {
                                if (productArray.length() > 1) {
                                    if (productArray.length() < pagesize) {
                                        refreshScrollView
                                                .setMode(Mode.PULL_FROM_START);
                                    }
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < productArray.length() - 1; i++) {
                                        ModelSaleTimeProduct product = new ModelSaleTimeProduct(
                                                productArray.optJSONObject(i));
                                        products.add(product);
                                        if (i > 0) {
                                            stringBuilder.append(",");
                                        }
                                        stringBuilder
                                                .append(product.getProductId());
                                    }
                                    productAdapter.notifyDataSetChanged();
                                    getSalePrice(stringBuilder.toString());
                                    return;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                refreshScrollView.setMode(Mode.PULL_FROM_START);
                if (products.size() == 0) {
                    missionNodata();
                }
            }
        });
    }

    private void getSalePrice(String productId) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("productId", productId));
        requestParams.add(new RequestParam("type", "0"));
        post(API.API_SALE_PRICE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array.optJSONObject(i);
                                    if (jsonObject != null) {
                                        prices.put(jsonObject.optString("id"),
                                                jsonObject.optDouble("price"));
                                    }
                                }
                                productAdapter.notifyDataSetChanged();
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
