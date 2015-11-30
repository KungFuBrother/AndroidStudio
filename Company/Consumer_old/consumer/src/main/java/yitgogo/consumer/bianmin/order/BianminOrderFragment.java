package yitgogo.consumer.bianmin.order;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;

public class BianminOrderFragment extends BaseNotifyFragment {

    PullToRefreshListView orderList;

    static List<ModelBianminOrder> bianminOrders;
    static OrderAdapter orderAdapter;
    static int selection = 0;

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
        MobclickAgent.onPageStart(BianminOrderFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(BianminOrderFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetBianminOrder().execute();
    }

    private void init() {
        measureScreen();
        bianminOrders = new ArrayList<ModelBianminOrder>();
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
        orderList.setMode(Mode.BOTH);
        orderList.setAdapter(orderAdapter);
    }

    @Override
    protected void registerViews() {
        orderList.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                bianminOrders.clear();
                orderAdapter.notifyDataSetChanged();
                pagenum = 0;
                orderList.setMode(Mode.BOTH);
                new GetBianminOrder().execute();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                new GetBianminOrder().execute();
            }
        });

    }

    class GetBianminOrder extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            pagenum++;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> valuePairs = new ArrayList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("pagenum", pagenum + ""));
            valuePairs.add(new BasicNameValuePair("pagesize", pagesize + ""));
            valuePairs.add(new BasicNameValuePair("memberAccount", User
                    .getUser().getUseraccount()));
            return netUtil.postWithoutCookie(API.API_BIANMIN_ORDER, valuePairs,
                    false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            orderList.onRefreshComplete();
            if (result.length() > 0) {
                JSONObject object;
                try {
                    object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONArray array = object.optJSONArray("dataList");
                        if (array != null) {
                            if (array.length() > 0) {
                                if (array.length() < pagesize) {
                                    orderList.setMode(Mode.PULL_FROM_START);
                                }
                                for (int i = 0; i < array.length(); i++) {
                                    bianminOrders.add(new ModelBianminOrder(
                                            array.optJSONObject(i)));
                                }
                                orderAdapter.notifyDataSetChanged();
                                return;
                            } else {
                                orderList.setMode(Mode.PULL_FROM_START);
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (bianminOrders.size() == 0) {
                    loadingEmpty();
                }
            } else {
                loadingFailed();
            }
        }

    }

    private void showOrderDetail(int position) {
        selection = position;
        Bundle bundle = new Bundle();
        bundle.putString("object", bianminOrders.get(position).getJsonObject()
                .toString());
        jump(BianminOrderDetailFragment.class.getName(), "订单详情", bundle);
    }

    class OrderAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bianminOrders.size();
        }

        @Override
        public Object getItem(int position) {
            return bianminOrders.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (bianminOrders != null) {
                if (bianminOrders.isEmpty()) {
                    loadingEmpty();
                }
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.list_order_bianmin, null);
                holder.orderStateText = (TextView) convertView
                        .findViewById(R.id.list_order_state);
                holder.orderMoneyText = (TextView) convertView
                        .findViewById(R.id.list_order_money);
                holder.orderTimeText = (TextView) convertView
                        .findViewById(R.id.list_order_date);
                holder.typeTextView = (TextView) convertView
                        .findViewById(R.id.list_order_type);
                holder.accountTextView = (TextView) convertView
                        .findViewById(R.id.list_order_account);
                holder.detailTextView = (TextView) convertView
                        .findViewById(R.id.list_order_detail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ModelBianminOrder bianminOrder = bianminOrders.get(position);
            holder.orderStateText.setText(bianminOrder.getOrderState());
            holder.orderTimeText.setText(bianminOrder.getOrderTime());
            holder.orderMoneyText.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(bianminOrder.getSellprice()));
            holder.typeTextView.setText(bianminOrder.getRechargeType());
            holder.accountTextView.setText(bianminOrder.getRechargeAccount());
            if (bianminOrder.getRechargeType().equals("手机")) {
                holder.detailTextView.setText("为"
                        + bianminOrder.getRechargeAccount() + "充值"
                        + decimalFormat.format(bianminOrder.getRechargeMoney())
                        + "元");
            } else if (bianminOrder.getRechargeType().equals("固话/宽带")) {
                holder.detailTextView.setText("为"
                        + bianminOrder.getRechargeAccount() + "充值"
                        + decimalFormat.format(bianminOrder.getRechargeMoney())
                        + "元");
            } else if (bianminOrder.getRechargeType().equals("游戏/Q币")) {
                if (TextUtils.isEmpty(bianminOrder.getGame_area())) {
                    holder.detailTextView.setText("为"
                            + bianminOrder.getRechargeAccount() + "充值"
                            + bianminOrder.getRechargeNum() + "Q币");
                } else {
                    holder.detailTextView.setText(bianminOrder.getGame_area()
                            + "/" + bianminOrder.getGame_server() + "(单价:"
                            + bianminOrder.getRechargeMoney() + "/数量:"
                            + bianminOrder.getRechargeNum() + ")");
                }
            } else {

            }
            final int index = position;
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    showOrderDetail(index);
                }
            });
            return convertView;
        }

        class ViewHolder {
            TextView orderStateText, orderMoneyText, orderTimeText,
                    typeTextView, accountTextView, detailTextView;
        }
    }

    public static void removeOrder() {
        if (bianminOrders != null) {
            if (bianminOrders.size() > selection) {
                bianminOrders.remove(selection);
                orderAdapter.notifyDataSetChanged();
            }
        }
    }

}
