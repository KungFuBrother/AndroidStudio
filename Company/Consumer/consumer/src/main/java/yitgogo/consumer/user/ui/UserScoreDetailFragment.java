package yitgogo.consumer.user.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.ModelScoreDetail;
import yitgogo.consumer.user.model.User;

public class UserScoreDetailFragment extends BaseNetworkFragment {

    PullToRefreshListView refreshListView;
    List<ModelScoreDetail> scoreDetails;
    ScoreDetailAdapter scoreDetailAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_score_detail);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UserScoreDetailFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UserScoreDetailFragment.class.getName());
    }

    @Override
    protected void init() {
        scoreDetails = new ArrayList<>();
        scoreDetailAdapter = new ScoreDetailAdapter();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reload();
    }

    @Override
    protected void findViews() {
        refreshListView = (PullToRefreshListView) contentView.findViewById(R.id.score_detail_list);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        refreshListView.setAdapter(scoreDetailAdapter);
        refreshListView.setMode(Mode.BOTH);
    }

    @Override
    protected void registerViews() {
        refreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                reload();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                pagenum++;
                getScoreDetial();
            }
        });
    }

    @Override
    protected void reload() {
        super.reload();
        refreshListView.setMode(Mode.BOTH);
        pagenum = 0;
        scoreDetails.clear();
        scoreDetailAdapter.notifyDataSetChanged();
        pagenum++;
        getScoreDetial();
    }

    private void getScoreDetial() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("pagenum", pagenum + ""));
        requestParams.add(new RequestParam("pagesize", pagesize + ""));
        requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        post(API.API_USER_JIFEN_DETAIL, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                refreshListView.onRefreshComplete();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONArray array = object.optJSONArray("dataList");
                            if (array != null) {
                                if (array.length() > 0) {
                                    if (array.length() < pagesize) {
                                        refreshListView
                                                .setMode(Mode.PULL_FROM_START);
                                    }
                                    for (int i = 0; i < array.length(); i++) {
                                        scoreDetails.add(new ModelScoreDetail(array
                                                .optJSONObject(i)));
                                    }
                                    scoreDetailAdapter.notifyDataSetChanged();
                                    return;
                                } else {
                                    refreshListView.setMode(Mode.PULL_FROM_START);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (scoreDetails.size() == 0) {
                        missionNodata();
                    }
                } else {
                    missionFailed();
                }
            }
        });
    }

    class ScoreDetailAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return scoreDetails.size();
        }

        @Override
        public Object getItem(int position) {
            return scoreDetails.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(
                        R.layout.list_score_detail, null);
                holder = new ViewHolder();
                holder.amountTextView = (TextView) convertView
                        .findViewById(R.id.list_score_amount);
                holder.dateTextView = (TextView) convertView
                        .findViewById(R.id.list_score_date);
                holder.detailTextView = (TextView) convertView
                        .findViewById(R.id.list_score_detail);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (scoreDetails.get(position).getBonusType().contains("收入")) {
                holder.amountTextView.setTextColor(getResources().getColor(
                        R.color.blue));
                holder.amountTextView.setText("+"
                        + scoreDetails.get(position).getBonusAmount());
            } else if (scoreDetails.get(position).getBonusType().contains("支出")) {
                holder.amountTextView.setTextColor(getResources().getColor(
                        R.color.red));
                holder.amountTextView.setText("-"
                        + scoreDetails.get(position).getBonusAmount());
            } else {
                holder.amountTextView.setTextColor(getResources().getColor(
                        R.color.textColorSecond));
                holder.amountTextView.setText(""
                        + scoreDetails.get(position).getBonusAmount());
            }
            holder.dateTextView.setText(scoreDetails.get(position)
                    .getRecordTime());
            holder.detailTextView.setText(scoreDetails.get(position)
                    .getDetails());
            return convertView;
        }

        class ViewHolder {
            TextView detailTextView, amountTextView, dateTextView;
        }

    }

}
