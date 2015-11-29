package yitgogo.smart.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
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
import yitgogo.smart.home.model.ModelListPrice;
import yitgogo.smart.home.model.ModelProduct;
import yitgogo.smart.task.ProductTask;
import yitgogo.smart.tools.NetworkMissionMessage;
import yitgogo.smart.tools.OnNetworkListener;
import yitgogo.smart.tools.Parameters;
import yitgogo.smart.tools.QrCodeTool;
import yitgogo.smart.view.InnerGridView;

public class ProductSearchPlatformFragment extends BaseNotifyFragment {

    PullToRefreshScrollView refreshScrollView;
    InnerGridView productGridView;

    List<ModelProduct> products;
    HashMap<String, ModelListPrice> priceMap;
    ProductAdapter productAdapter;

    int pageSize = 12, pageNo = 0;

    String searchName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_search_products);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ProductSearchPlatformFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ProductSearchPlatformFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        refresh();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("searchName")) {
                searchName = bundle.getString("searchName");
            }
        }
        products = new ArrayList<ModelProduct>();
        priceMap = new HashMap<String, ModelListPrice>();
        productAdapter = new ProductAdapter();
    }

    @Override
    protected void findViews() {
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.product_search_scroll);
        productGridView = (InnerGridView) contentView
                .findViewById(R.id.product_search_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        productGridView.setAdapter(productAdapter);
        refreshScrollView.setMode(Mode.BOTH);
    }

    @Override
    protected void registerViews() {
        refreshScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        refresh();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        getProducts();
                    }
                });
        productGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                showProductDetail(products.get(arg2).getId(),
                        QrCodeTool.SALE_TYPE_NONE);
            }
        });
    }

    private void refresh() {
        refreshScrollView.setMode(Mode.BOTH);
        pageNo = 0;
        products.clear();
        productAdapter.notifyDataSetChanged();
        getProducts();
    }

    private void getProducts() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        pageNo++;
        nameValuePairs.add(new BasicNameValuePair("pageNo", pageNo + ""));
        nameValuePairs.add(new BasicNameValuePair("pageSize", pageSize + ""));
        nameValuePairs.add(new BasicNameValuePair("productName", searchName));
        ProductTask.getProducts(getActivity(), nameValuePairs,
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
                        refreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onSuccess(NetworkMissionMessage requestMessage) {
                        super.onSuccess(requestMessage);
                        if (!TextUtils.isEmpty(requestMessage.getResult())) {
                            JSONObject info;
                            try {
                                info = new JSONObject(requestMessage
                                        .getResult());
                                if (info.getString("state").equalsIgnoreCase(
                                        "SUCCESS")) {
                                    JSONArray productArray = info
                                            .getJSONArray("dataList");
                                    if (productArray != null) {
                                        if (productArray.length() > 0) {
                                            if (productArray.length() < pageSize) {
                                                refreshScrollView
                                                        .setMode(Mode.PULL_FROM_START);
                                            }
                                            StringBuilder stringBuilder = new StringBuilder();
                                            for (int i = 0; i < productArray
                                                    .length(); i++) {
                                                ModelProduct product = new ModelProduct(
                                                        productArray
                                                                .getJSONObject(i));
                                                products.add(product);
                                                if (i > 0) {
                                                    stringBuilder.append(",");
                                                }
                                                stringBuilder.append(product
                                                        .getId());
                                            }
                                            productAdapter
                                                    .notifyDataSetChanged();
                                            getProductPrice(stringBuilder
                                                    .toString());
                                            return;
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        refreshScrollView.setMode(Mode.PULL_FROM_START);
                        if (products.isEmpty()) {
                            loadingEmpty();
                        }
                    }
                });
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
                                            .getJSONArray("dataList");
                                    if (priceArray.length() > 0) {
                                        for (int i = 0; i < priceArray.length(); i++) {
                                            ModelListPrice priceList = new ModelListPrice(
                                                    priceArray.getJSONObject(i));
                                            priceMap.put(
                                                    priceList.getProductId(),
                                                    priceList);
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
            ModelProduct product = products.get(position);
            imageLoader.displayImage(getSmallImageUrl(product.getImg()),
                    viewHolder.imageView);
            if (priceMap.containsKey(product.getId())) {
                viewHolder.priceTextView.setText(Parameters.CONSTANT_RMB
                        + decimalFormat.format(priceMap.get(product.getId())
                        .getPrice()));
            }
            viewHolder.nameTextView.setText(product.getProductName());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView priceTextView, nameTextView;
        }
    }

}