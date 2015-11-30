package yitgogo.consumer.money.ui;

import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.model.ModelTrade;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.Notify;

public class TradeHistoryFragment extends BaseNotifyFragment {

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
        new GetTradeHistory().execute();
    }

    private void init() {
        trades = new ArrayList<ModelTrade>();
        tradeHistoryAdapter = new TradeHistoryAdapter();
    }

    @Override
    protected void findViews() {
        listView = (InnerListView) contentView.findViewById(R.id.trade_list);
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.trade_refresh);
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
                        refreshScrollView.setMode(Mode.BOTH);
                        pagenum = 0;
                        trades.clear();
                        tradeHistoryAdapter.notifyDataSetChanged();
                        new GetTradeHistory().execute();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        new GetTradeHistory().execute();
                    }
                });
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/cash/listdetail
     * @Parameters [pageindex=1, pagecount=10]
     * @Put_Cookie JSESSIONID=09DC823F27F1756D01E2D0FD9CC3ED03
     * @Result {"state":"success","msg":"操作成功","databody":{"currentPageNo":1,"data"
     * :[{"account":"HY048566511863","amount":116.00,"amountFlow":true,
     * "datatime":"2015-08-19 21:25:16","description":"参与活动中奖","id":
     * "15081921250003"
     * ,"realname":"赵晋","sourceid":"YT1519063489"},{"account"
     * :"HY048566511863","amount":350.00,"amountFlow":true,"datatime":
     * "2015-08-19 21:27:00"
     * ,"description":"参与活动中奖","id":"15081921270002"
     * ,"realname":"赵晋","sourceid"
     * :"YT1519162417"},{"account":"HY048566511863"
     * ,"amount":261.00,"amountFlow"
     * :true,"datatime":"2015-08-19 21:27:25"
     * ,"description":"参与活动中奖","id"
     * :"15081921270004","realname":"赵晋","sourceid"
     * :"YT1519266519"},{"account"
     * :"HY048566511863","amount":348.00,"amountFlow"
     * :true,"datatime":"2015-08-19 21:30:07"
     * ,"description":"参与活动中奖","id"
     * :"15081921300003","realname":"赵晋","sourceid"
     * :"YT1519689582"},{"account"
     * :"HY048566511863","amount":237.00,"amountFlow"
     * :true,"datatime":"2015-08-19 21:35:01"
     * ,"description":"参与活动中奖","id"
     * :"15081921350002","realname":"赵晋","sourceid"
     * :"YT5248796282"},{"account"
     * :"HY048566511863","amount":401.00,"amountFlow"
     * :true,"datatime":"2015-08-19 21:38:44"
     * ,"description":"参与活动中奖","id"
     * :"15081921380004","realname":"赵晋","sourceid"
     * :"YT5249071584"},{"account"
     * :"HY048566511863","amount":428.00,"amountFlow"
     * :true,"datatime":"2015-08-19 21:38:55"
     * ,"description":"参与活动中奖","id"
     * :"15081921380006","realname":"赵晋","sourceid"
     * :"YT5249146239"},{"account"
     * :"HY048566511863","amount":387.00,"amountFlow"
     * :true,"datatime":"2015-08-19 21:39:01"
     * ,"description":"参与活动中奖","id"
     * :"15081921390002","realname":"赵晋","sourceid"
     * :"YT5249220214"},{"account"
     * :"HY048566511863","amount":305.00,"amountFlow"
     * :true,"datatime":"2015-08-19 21:39:10"
     * ,"description":"参与活动中奖","id"
     * :"15081921390004","realname":"赵晋","sourceid"
     * :"YT5249333139"},{"account"
     * :"HY048566511863","amount":10.00,"amountFlow"
     * :true,"datatime":"2015-08-19 21:46:56"
     * ,"description":"参与活动中奖","id"
     * :"15081921460002","realname":"赵晋","sourceid"
     * :"YT5249440321"}],"hasNextPage"
     * :true,"hasPreviousPage":false,"pageSize"
     * :10,"totalCount":14,"totalPageCount":2}}
     */
    class GetTradeHistory extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
            pagenum++;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs
                    .add(new BasicNameValuePair("pageindex", pagenum + ""));
            nameValuePairs.add(new BasicNameValuePair("pagecount", pagesize
                    + ""));
            return netUtil.postWithCookie(API.MONEY_TRADE_DETAIL,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
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
                    Notify.show(object.optString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            refreshScrollView.setMode(Mode.PULL_FROM_START);
            if (trades.isEmpty()) {
                loadingEmpty();
            }
        }
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
