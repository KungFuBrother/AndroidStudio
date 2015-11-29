package yitgogo.consumer.money.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.money.model.ModelTakeOutHistory;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.view.InnerListView;

public class TakeOutHistoryFragment extends BaseNetworkFragment {

    DrawerLayout drawerLayout;
    PullToRefreshScrollView refreshScrollView;
    InnerListView dataListView, bankCardsListView;
    TextView clearButton, selectButton;

    List<ModelTakeOutHistory> takeOutHistories;
    TakeOutHistoryAdapter historyAdapter;

    BandCardAdapter bandCardAdapter;
    ModelBankCard selectedBankCard = new ModelBankCard();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_takeout_list);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TakeOutHistoryFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TakeOutHistoryFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        reload();
    }

    @Override
    protected void init() {
        takeOutHistories = new ArrayList<>();
        historyAdapter = new TakeOutHistoryAdapter();
        bandCardAdapter = new BandCardAdapter();
    }

    @Override
    protected void findViews() {
        drawerLayout = (DrawerLayout) contentView
                .findViewById(R.id.takeout_drawer);
        refreshScrollView = (PullToRefreshScrollView) contentView
                .findViewById(R.id.takeout_refresh);
        dataListView = (InnerListView) contentView
                .findViewById(R.id.takeout_list);
        bankCardsListView = (InnerListView) contentView
                .findViewById(R.id.takeout_list_selector_bankcards);
        clearButton = (TextView) contentView
                .findViewById(R.id.takeout_list_selector_clear);
        selectButton = (TextView) contentView
                .findViewById(R.id.takeout_list_selector_select);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        dataListView.setAdapter(historyAdapter);
        bankCardsListView.setAdapter(bandCardAdapter);
        refreshScrollView.setMode(Mode.BOTH);
    }

    @Override
    protected void registerViews() {
        addTextButton("筛选", new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
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
                        getTakeOutHistory();
                    }
                });
        bankCardsListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                selectedBankCard = MoneyAccount.getMoneyAccount()
                        .getBankCards().get(arg2);
                bandCardAdapter.notifyDataSetChanged();
            }
        });
        clearButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                selectedBankCard = new ModelBankCard();
                bandCardAdapter.notifyDataSetChanged();

                reload();
            }
        });
        selectButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
                reload();
            }
        });
    }

    @Override
    protected void reload() {
        refreshScrollView.setMode(Mode.BOTH);
        pagenum = 0;
        takeOutHistories.clear();
        historyAdapter.notifyDataSetChanged();
        pagenum++;
        getTakeOutHistory();
    }

    private void getTakeOutHistory() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("pageindex", pagenum + ""));
        requestParams.add(new RequestParam("pagecount", pagesize + ""));
        if (!TextUtils.isEmpty(selectedBankCard.getId())) {
            requestParams.add(new RequestParam("bankcardid", selectedBankCard.getId()));
        }
        post(API.MONEY_BANK_TAKEOUT_HISTORY, requestParams, new OnNetworkListener() {
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
                                        takeOutHistories
                                                .add(new ModelTakeOutHistory(array
                                                        .optJSONObject(i)));
                                    }
                                    if (!takeOutHistories.isEmpty()) {
                                        historyAdapter.notifyDataSetChanged();
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
                if (takeOutHistories.isEmpty()) {
                    missionNodata();
                }
            }
        });
    }

    class TakeOutHistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return takeOutHistories.size();
        }

        @Override
        public Object getItem(int position) {
            return takeOutHistories.get(position);
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
                        R.layout.list_money_take_out_history, null);
                viewHolder.amountTextView = (TextView) convertView
                        .findViewById(R.id.list_takeout_amount);
                viewHolder.stateTextView = (TextView) convertView
                        .findViewById(R.id.list_takeout_state);
                viewHolder.bankTextView = (TextView) convertView
                        .findViewById(R.id.list_takeout_bank);
                viewHolder.dateTextView = (TextView) convertView
                        .findViewById(R.id.list_takeout_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ModelTakeOutHistory takeOutHistory = takeOutHistories.get(position);
            viewHolder.amountTextView.setText(Parameters.CONSTANT_RMB
                    + decimalFormat.format(takeOutHistory.getAmount()));
            viewHolder.bankTextView.setText(takeOutHistory.getUserbank()
                    + "(尾号"
                    + takeOutHistory.getUserbankid().substring(
                    takeOutHistory.getUserbankid().length() - 4,
                    takeOutHistory.getUserbankid().length()) + ")");
            viewHolder.dateTextView.setText(takeOutHistory.getDatatime());
            viewHolder.stateTextView.setText(takeOutHistory.getState());
            return convertView;
        }

        class ViewHolder {
            TextView amountTextView, stateTextView, bankTextView, dateTextView;
        }
    }

    class BandCardAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return MoneyAccount.getMoneyAccount().getBankCards().size();
        }

        @Override
        public Object getItem(int position) {
            return MoneyAccount.getMoneyAccount().getBankCards().get(position);
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
                        R.layout.list_pay_bank_card, null);
                viewHolder.selected = (ImageView) convertView
                        .findViewById(R.id.bank_card_bank_selection);
                viewHolder.bankImageView = (ImageView) convertView
                        .findViewById(R.id.bank_card_bank_image);
                viewHolder.cardNumberTextView = (TextView) convertView
                        .findViewById(R.id.bank_card_number);
                viewHolder.cardTypeTextView = (TextView) convertView
                        .findViewById(R.id.bank_card_type);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ModelBankCard bankCard = MoneyAccount.getMoneyAccount()
                    .getBankCards().get(position);
            if (bankCard.getId().equals(selectedBankCard.getId())) {
                viewHolder.selected
                        .setImageResource(R.drawable.iconfont_check_checked);
            } else {
                viewHolder.selected
                        .setImageResource(R.drawable.iconfont_check_normal);
            }
            ImageLoader.getInstance().displayImage(
                    bankCard.getBank().getIcon(), viewHolder.bankImageView);
            viewHolder.cardNumberTextView.setText(getSecretCardNuber(bankCard
                    .getBanknumber()));
            viewHolder.cardTypeTextView.setText(bankCard.getBank().getName()
                    + "  " + bankCard.getCardType());
            return convertView;
        }

        class ViewHolder {
            ImageView selected, bankImageView;
            TextView cardNumberTextView, cardTypeTextView;
        }
    }

}
