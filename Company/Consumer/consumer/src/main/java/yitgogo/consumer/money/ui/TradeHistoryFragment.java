package yitgogo.consumer.money.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.model.ModelTrade;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerListView;

public class TradeHistoryFragment extends BaseNetworkFragment {

    PullToRefreshScrollView refreshScrollView;
    ListView listView;

    List<ModelTrade> trades;
    TradeHistoryAdapter tradeHistoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_trade_list);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TradeHistoryFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TradeHistoryFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reload();
    }

    @Override
    protected void init() {
        trades = new ArrayList<>();
        tradeHistoryAdapter = new TradeHistoryAdapter();
    }

    @Override
    protected void findViews() {
        listView = (InnerListView) contentView.findViewById(R.id.trade_list);
        refreshScrollView = (PullToRefreshScrollView) contentView.findViewById(R.id.trade_refresh);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        listView.setAdapter(tradeHistoryAdapter);
        refreshScrollView.setMode(Mode.BOTH);
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
                        getTradeHistory();
                    }
                });
    }

    @Override
    protected void reload() {
        super.reload();
        refreshScrollView.setMode(Mode.BOTH);
        pagenum = 0;
        trades.clear();
        tradeHistoryAdapter.notifyDataSetChanged();
        pagenum++;
        getTradeHistory();
    }

    private void getTradeHistory() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("pageindex", pagenum + ""));
        requestParams.add(new RequestParam("pagecount", pagesize + ""));
        post(API.MONEY_TRADE_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshScrollView.onRefreshComplete();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject jsonObject = object
                                    .optJSONObject("databody");
                            if (jsonObject != null) {
                                JSONArray array = jsonObject.optJSONArray("data");
                                if (array != null) {
                                    if (array.length() < pagesize) {
                                        refreshScrollView
                                                .setMode(Mode.PULL_FROM_START);
                                    }
                                    for (int i = 0; i < array.length(); i++) {
                                        trades.add(new ModelTrade(array
                                                .optJSONObject(i)));
                                    }
                                    if (trades.size() > 0) {
                                        tradeHistoryAdapter.notifyDataSetChanged();
                                        return;
                                    }
                                }
                            }
                        }
                        ApplicationTool.showToast(object.optString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                refreshScrollView.setMode(Mode.PULL_FROM_START);
                if (trades.isEmpty()) {
                    missionNodata();
                }
            }
        });
    }

    class TradeHistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return trades.size();
        }

        @Override
        public Object getItem(int position) {
            return trades.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.list_money_trade_history, null);
                viewHolder.amountTextView = (TextView) convertView
                        .findViewById(R.id.list_trade_amount);
                viewHolder.detailTextView = (TextView) convertView
                        .findViewById(R.id.list_trade_detail);
                viewHolder.dateTextView = (TextView) convertView
                        .findViewById(R.id.list_trade_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ModelTrade trade = trades.get(position);
            viewHolder.amountTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(trade.getAmount()));
            viewHolder.detailTextView.setText(trade.getDescription());
            viewHolder.dateTextView.setText(trade.getDatatime());
            return convertView;
        }

        class ViewHolder {
            TextView amountTextView, detailTextView, dateTextView;
        }
    }

}
