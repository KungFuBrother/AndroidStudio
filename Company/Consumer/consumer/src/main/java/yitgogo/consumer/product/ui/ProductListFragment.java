package yitgogo.consumer.product.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dtr.zxing.activity.CaptureActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yitgogo.consumer.BaseFragment;
import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.home.model.ModelListPrice;
import yitgogo.consumer.home.model.ModelProduct;
import yitgogo.consumer.product.model.ModelAttrType;
import yitgogo.consumer.product.model.ModelAttrValue;
import yitgogo.consumer.product.model.ModelProductFilter;
import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerListView;

public class ProductListFragment extends BaseNetworkFragment {

    PullToRefreshListView productList;
    DrawerLayout drawerLayout;
    FrameLayout selectorLayout, attrSelectorLayout;

    List<RequestParam> parameters;
    List<RequestParam> selectParameters;
    List<ModelProduct> products;
    HashMap<String, ModelListPrice> priceMap;
    ProductAdapter productAdapter;
    ModelProductFilter productFilter;

    String value = "";
    int type = 0;
    public final static int TYPE_CLASS = 0;
    public final static int TYPE_BRAND = 1;
    public final static int TYPE_NAME = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(ProductListFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(ProductListFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setDefaultParameters();
        reload();
    }

    @Override
    protected void findViews() {
        productList = (PullToRefreshListView) contentView
                .findViewById(R.id.product_list);
        drawerLayout = (DrawerLayout) contentView
                .findViewById(R.id.product_drawer);
        selectorLayout = (FrameLayout) contentView
                .findViewById(R.id.product_selector);
        attrSelectorLayout = (FrameLayout) contentView
                .findViewById(R.id.product_selector2);
        initViews();
        registerViews();
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle.containsKey("value")) {
            value = bundle.getString("value");
        }
        if (bundle.containsKey("type")) {
            type = bundle.getInt("type");
        }
        parameters = new ArrayList<>();
        selectParameters = new ArrayList<>();
        products = new ArrayList<>();
        priceMap = new HashMap<>();
        productAdapter = new ProductAdapter();
    }

    /**
     * 通过type类型设置基础请求参数
     */
    private void setDefaultParameters() {
        parameters.clear();
        switch (type) {
            case TYPE_CLASS:
                // 分类查询
                parameters.add(new RequestParam("classValueId", value));
                setSelector(new AttrFilter());
                break;

            case TYPE_BRAND:
                // 品牌查询
                parameters.add(new RequestParam("brandId", value));
                break;

            case TYPE_NAME:
                // 关键字搜索
                parameters.add(new RequestParam("productName", value));
                break;

            default:
                break;
        }
    }

    @Override
    protected void initViews() {
        addImageButton(R.drawable.iconfont_cart, "购物车", new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ShoppingCarFragment.class.getName(), "购物车");
            }
        });
        if (type == TYPE_CLASS) {
            addImageButton(R.drawable.iconfont_filter, "筛选",
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (drawerLayout.isDrawerOpen(attrSelectorLayout)) {
                                drawerLayout.closeDrawer(attrSelectorLayout);
                            } else if (drawerLayout
                                    .isDrawerOpen(selectorLayout)) {
                                drawerLayout.closeDrawer(selectorLayout);
                            } else {
                                drawerLayout.openDrawer(selectorLayout);
                            }
                        }
                    });
        }
        productList.setAdapter(productAdapter);
        productList.setMode(Mode.BOTH);
    }

    @Override
    protected void registerViews() {
        onBackButtonClick(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(attrSelectorLayout)) {
                    drawerLayout.closeDrawer(attrSelectorLayout);
                } else if (drawerLayout.isDrawerOpen(selectorLayout)) {
                    drawerLayout.closeDrawer(selectorLayout);
                } else {
                    getActivity().finish();
                }
            }
        });
        productList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                reload();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                pagenum++;
                getProduct();
            }
        });
    }

    @Override
    protected void reload() {
        super.reload();
        pagenum = 0;
        products.clear();
        productAdapter.notifyDataSetChanged();
        productList.setMode(Mode.BOTH);
        pagenum++;
        getProduct();
    }

    private void getProduct() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("pageNo", pagenum + ""));
        requestParams.add(new RequestParam("jmdId", Store.getStore().getStoreId()));
        requestParams.add(new RequestParam("pageSize", pagesize + ""));
        if (parameters != null) {
            requestParams.addAll(parameters);
        }
        if (selectParameters != null) {
            requestParams.addAll(selectParameters);
        }
        post(API.API_PRODUCT_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                productList.onRefreshComplete();
                if (result.length() > 0) {
                    JSONObject info;
                    try {
                        info = new JSONObject(result);
                        if (info.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray productArray = info.optJSONArray("dataList");
                            if (productArray != null) {
                                if (productArray.length() > 0) {
                                    if (productArray.length() < pagesize) {
                                        productList.setMode(Mode.PULL_FROM_START);
                                    }
                                    StringBuilder stringBuilder = new StringBuilder();
                                    for (int i = 0; i < productArray.length(); i++) {
                                        ModelProduct product = new ModelProduct(
                                                productArray.getJSONObject(i));
                                        products.add(product);
                                        if (i > 0) {
                                            stringBuilder.append(",");
                                        }
                                        stringBuilder.append(product.getId());
                                    }
                                    productAdapter.notifyDataSetChanged();
                                    getPriceList(stringBuilder.toString());
                                    return;
                                } else {
                                    productList.setMode(Mode.PULL_FROM_START);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (products.size() == 0) {
                        missionNodata();
                    }
                } else {
                    missionFailed();
                }
            }
        });
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
                                if (priceArray.length() > 0) {
                                    for (int i = 0; i < priceArray.length(); i++) {
                                        ModelListPrice priceList = new ModelListPrice(
                                                priceArray.getJSONObject(i));
                                        priceMap.put(priceList.getProductId(),
                                                priceList);
                                    }
                                    productAdapter.notifyDataSetChanged();
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
            final int index = position;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_product,
                        null);
                holder.image = (ImageView) convertView
                        .findViewById(R.id.list_product_image);
                holder.name = (TextView) convertView
                        .findViewById(R.id.list_product_name);
                holder.price = (TextView) convertView
                        .findViewById(R.id.list_product_price);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelProduct product = products.get(position);
            ImageLoader.getInstance().displayImage(
                    getSmallImageUrl(product.getImg()), holder.image);
            holder.name.setText(product.getProductName());
            if (priceMap.containsKey(product.getId())) {
                holder.price.setText(Parameters.CONSTANT_RMB
                        + decimalFormat.format(priceMap.get(product.getId())
                        .getPrice()));
            }
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showProductDetail(products.get(index).getId(), products
                                    .get(index).getProductName(),
                            CaptureActivity.SALE_TYPE_NONE);
                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView price, name;
        }

    }

    public class AttrFilter extends BaseFragment {

        ModelProductFilter productFilter;
        LinearLayout titleLayout;
        Button clearButton, selectButton;
        TextView brandSelectionText;
        InnerListView attrList;
        AttrAdapter attrAdapter;

        /**
         * 记录每次点击属性列表的位置
         */
        int attrPosition = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
        }

        @Override
        protected void init() {
            productFilter = new ModelProductFilter();
            attrAdapter = new AttrAdapter();
            getAttributes();
        }

        @Override
        public View onCreateView(LayoutInflater inflater,
                                 @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.selector_product_class, null);
            findViews(view);
            return view;
        }

        protected void findViews(View view) {
            titleLayout = (LinearLayout) view
                    .findViewById(R.id.product_selector_title);
            clearButton = (Button) view
                    .findViewById(R.id.product_selector_clear);
            selectButton = (Button) view
                    .findViewById(R.id.product_selector_select);
            attrList = (InnerListView) view
                    .findViewById(R.id.product_selector_attr);
            initViews();
            registerViews();
        }

        protected void initViews() {
            attrList.setAdapter(attrAdapter);
        }

        @Override
        protected void registerViews() {
            titleLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    drawerLayout.closeDrawer(selectorLayout);
                }
            });
            clearButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    clearSelection();
                }
            });
            selectButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    select();
                }
            });
            attrList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    attrPosition = arg2;
                    showAttrValueSelector();
                }
            });
        }

        /**
         * 分类商品列表会有较为复杂的筛选，需要两个筛选器
         */
        private void showAttrValueSelector() {
            getFragmentManager().beginTransaction()
                    .replace(R.id.product_selector2, new AttrSelector())
                    .commit();
            drawerLayout.openDrawer(attrSelectorLayout);
        }

        /**
         * 清除选项
         */
        private void clearSelection() {
            for (int i = 0; i < productFilter.getAllAttrs().size(); i++) {
                productFilter.getAllAttrs().get(i).setSelection(0);
            }
            selectParameters.clear();
            attrAdapter.notifyDataSetChanged();
        }

        /**
         * 筛选
         */
        private void select() {
            selectParameters.clear();
            String brandSelection = "", attrSelection = "", attrExtendSelection = "";
            for (int i = 0; i < productFilter.getAllAttrs().size(); i++) {
                ModelAttrType attrType = productFilter.getAllAttrs().get(i);
                if (attrType.getSelection() > 0) {
                    switch (attrType.getType()) {
                        case ModelAttrType.TYPE_BRAND:
                            brandSelection = attrType.getAttrValues()
                                    .get(attrType.getSelection()).getId();
                            break;

                        case ModelAttrType.TYPE_ATTR:
                            if (attrSelection.length() > 0) {
                                attrSelection += ",";
                            }
                            attrSelection += attrType.getAttrValues()
                                    .get(attrType.getSelection()).getId();
                            break;

                        case ModelAttrType.TYPE_ATTR_EXTEND:
                            if (attrExtendSelection.length() > 0) {
                                attrExtendSelection += ",";
                            }
                            attrExtendSelection += attrType.getAttrValues()
                                    .get(attrType.getSelection()).getId();
                            break;

                        default:
                            break;
                    }
                }
            }
            if (brandSelection.length() > 0) {
                selectParameters.add(new RequestParam("brandId", brandSelection));
            }
            if (attrSelection.length() > 0) {
                selectParameters.add(new RequestParam("avId", attrSelection));
            }
            if (attrExtendSelection.length() > 0) {
                selectParameters.add(new RequestParam("aveIds", attrExtendSelection));
            }
            drawerLayout.closeDrawer(selectorLayout);
            reload();
        }

        class AttrSelector extends BaseFragment {

            ListView valueList;
            LinearLayout titleLayout;
            TextView attrTitleText;
            /**
             * 属性值选择器数据
             */
            List<ModelAttrValue> attrValues;
            AttrValueAdapter attrValueAdapter;

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                init();
            }

            @Override
            public View onCreateView(LayoutInflater inflater,
                                     @Nullable ViewGroup container,
                                     @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(
                        R.layout.selector_product_class_attr, null);
                findViews(view);
                return view;
            }

            @Override
            protected void init() {
                attrValues = productFilter.getAllAttrs().get(attrPosition)
                        .getAttrValues();
                attrValueAdapter = new AttrValueAdapter();
            }

            protected void findViews(View view) {
                titleLayout = (LinearLayout) view
                        .findViewById(R.id.attr_selector_title);
                attrTitleText = (TextView) view
                        .findViewById(R.id.attr_selector_name);
                valueList = (ListView) view
                        .findViewById(R.id.attr_selector_value);
                initViews();
                registerViews();
            }

            protected void initViews() {
                attrTitleText.setText(productFilter.getAllAttrs()
                        .get(attrPosition).getName());
                valueList.setAdapter(attrValueAdapter);
            }

            @Override
            protected void registerViews() {
                titleLayout.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        drawerLayout.closeDrawer(attrSelectorLayout);
                    }
                });
                valueList.setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1,
                                            int arg2, long arg3) {
                        productFilter.getAllAttrs().get(attrPosition)
                                .setSelection(arg2);
                        attrAdapter.notifyDataSetChanged();
                        drawerLayout.closeDrawer(attrSelectorLayout);
                    }
                });
            }

            class AttrValueAdapter extends BaseAdapter {

                @Override
                public int getCount() {
                    // TODO Auto-generated method stub
                    return attrValues.size();
                }

                @Override
                public Object getItem(int position) {
                    return attrValues.get(position);
                }

                @Override
                public long getItemId(int position) {
                    // TODO Auto-generated method stub
                    return position;
                }

                @Override
                public View getView(int position, View convertView,
                                    ViewGroup parent) {
                    // TODO Auto-generated method stub
                    ViewHolder holder;
                    if (convertView == null) {
                        holder = new ViewHolder();
                        convertView = layoutInflater.inflate(
                                R.layout.list_class_attr, null);
                        holder.value = (TextView) convertView
                                .findViewById(R.id.class_attr_name);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    ModelAttrValue attrValue = attrValues.get(position);
                    holder.value.setText(attrValue.getName());
                    return convertView;
                }

                class ViewHolder {
                    TextView value;
                }
            }

        }

        class AttrAdapter extends BaseAdapter {
            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return productFilter.getAllAttrs().size();
            }

            @Override
            public Object getItem(int position) {
                return productFilter.getAllAttrs().get(position);
            }

            @Override
            public long getItemId(int position) {
                // TODO Auto-generated method stub
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = layoutInflater.inflate(
                            R.layout.list_attr_class, null);
                    holder.name = (TextView) convertView
                            .findViewById(R.id.attr_class_name);
                    holder.value = (TextView) convertView
                            .findViewById(R.id.attr_class_selection);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                ModelAttrType attrType = productFilter.getAllAttrs().get(
                        position);
                holder.name.setText(attrType.getName());
                holder.value.setText(attrType.getAttrValues()
                        .get(attrType.getSelection()).getName());
                return convertView;
            }

            class ViewHolder {
                TextView name, value;
            }

        }

        private void getAttributes() {
            List<RequestParam> requestParams = new ArrayList<>();
            requestParams.add(new RequestParam("jmdId", Store.getStore().getStoreId()));
            requestParams.add(new RequestParam("minClassId", value));
            post(API.API_PRODUCT_CLASS_ATTR, requestParams, new OnNetworkListener() {
                @Override
                public void onSuccess(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        JSONObject object;
                        try {
                            object = new JSONObject(result);
                            if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                                JSONObject attrObject = object.optJSONObject("dataMap");
                                if (attrObject != null) {
                                    productFilter = new ModelProductFilter(attrObject);
                                }
                                attrAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    /**
     * 设置筛选器，不同的商品列表筛选器也会不同，通过子类设置相应的筛选器
     *
     * @param selector
     */
    protected void setSelector(Fragment selector) {
        getFragmentManager().beginTransaction()
                .replace(R.id.product_selector, selector).commit();
    }

    /**
     * 按键事件处理
     *
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(attrSelectorLayout)) {
                drawerLayout.closeDrawer(attrSelectorLayout);
            } else if (drawerLayout.isDrawerOpen(selectorLayout)) {
                drawerLayout.closeDrawer(selectorLayout);
            } else {
                return false;
            }
            return true;
        }
        return false;
    }

}
