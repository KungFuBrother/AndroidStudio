package yitgogo.consumer.order.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.order.model.ModelOrder;
import yitgogo.consumer.order.model.ModelProductOrder;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerGridView;

public class OrderPlatformFragment extends BaseNetworkFragment {

    PullToRefreshListView orderList;
    OrderAdapter orderAdapter;
    List<ModelOrder> orders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_order_platform);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(OrderPlatformFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(OrderPlatformFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getOrders();
    }

    @Override
    protected void init() {
        orders = new ArrayList<>();
        orderAdapter = new OrderAdapter();
    }

    @Override
    protected void findViews() {
        orderList = (PullToRefreshListView) contentView
                .findViewById(R.id.order_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        orderList.setAdapter(orderAdapter);
        orderList.setMode(Mode.PULL_FROM_START);
    }

    @Override
    protected void registerViews() {
        orderList.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getOrders();
            }
        });
    }

    private void showOrderDetail(int index) {
        Bundle bundle = new Bundle();
        bundle.putString("orderNumber", orders.get(index).getOrderNumber());
        jump(OrderDetailFragment.class.getName(), "订单详情", bundle);
    }

    private void getOrders() {
        orders.clear();
        orderAdapter.notifyDataSetChanged();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("userNumber", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("timeslot", "0"));
        post(API.API_ORDER_LIST, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                orderList.onRefreshComplete();
                if (result.length() > 0) {
                    JSONObject object;
                    try {
                        object = new JSONObject(result);
                        if (object.getString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                for (int i = 0; i < array.length(); i++) {
                                    orders.add(new ModelOrder(array
                                            .getJSONObject(i)));
                                }
                                if (orders.size() > 0) {
                                    orderAdapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (orders.size() == 0) {
                    missionNodata();
                }
            }
        });
    }

    class OrderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return orders.size();
        }

        @Override
        public Object getItem(int position) {
            return orders.get(position);
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
                convertView = layoutInflater.inflate(R.layout.list_order, null);
                holder.orderProducts = (InnerGridView) convertView
                        .findViewById(R.id.list_order_product);
                holder.orderStateText = (TextView) convertView
                        .findViewById(R.id.list_order_state);
                holder.orderMoneyText = (TextView) convertView
                        .findViewById(R.id.list_order_money);
                holder.orderTimeText = (TextView) convertView
                        .findViewById(R.id.list_order_date);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelOrder order = orders.get(position);
            holder.orderStateText.setText(order.getOrderState()
                    .getOrderStatusName());
            holder.orderTimeText.setText(order.getSellTime());
            holder.orderMoneyText.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(order.getTotalSellPrice()));
            if (order.getProducts().size() > 1) {
                holder.orderProducts.setNumColumns(4);
            } else {
                holder.orderProducts.setNumColumns(1);
            }
            holder.orderProducts.setAdapter(new OrderProductAdapter(order
                    .getProducts()));
            final int index = position;
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showOrderDetail(index);
                }
            });
            holder.orderProducts
                    .setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1,
                                                int arg2, long arg3) {
                            showOrderDetail(index);
                        }
                    });
            return convertView;
        }

        class ViewHolder {
            InnerGridView orderProducts;
            TextView orderStateText, orderMoneyText, orderTimeText;
        }
    }

    class OrderProductAdapter extends BaseAdapter {

        List<ModelProductOrder> products;

        public OrderProductAdapter(List<ModelProductOrder> products) {
            this.products = products;
        }

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
                convertView = layoutInflater.inflate(
                        R.layout.list_product_order, null);
                holder.image = (ImageView) convertView
                        .findViewById(R.id.list_product_order_image);
                holder.productNameText = (TextView) convertView
                        .findViewById(R.id.list_product_order_name);
                holder.productPriceText = (TextView) convertView
                        .findViewById(R.id.list_product_order_price);
                if (products.size() > 1) {
                    holder.productNameText.setVisibility(View.GONE);
                    holder.productPriceText.setVisibility(View.GONE);
                    holder.image.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    convertView.setLayoutParams(new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT,
                            ApplicationTool.getScreenWidth() / 4));
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelProductOrder product = products.get(position);
            holder.productNameText.setText(product.getProductName());
            holder.productPriceText.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(product.getUnitSellPrice()));
            ImageLoader.getInstance().displayImage(product.getImg(),
                    holder.image);
            return convertView;
        }

        class ViewHolder {
            ImageView image;
            TextView productNameText, productPriceText;
        }

    }

}