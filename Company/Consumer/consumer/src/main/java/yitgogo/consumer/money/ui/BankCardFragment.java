package yitgogo.consumer.money.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.InnerListView;

public class BankCardFragment extends BaseNetworkFragment {

    LinearLayout addButton;
    InnerListView cardListView;

    List<ModelBankCard> bankCards;
    BandCardAdapter bandCardAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_backcard_list);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(BankCardFragment.class.getName());
    }

    @Override
    protected void init() {
        bankCards = new ArrayList<>();
        bandCardAdapter = new BandCardAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(BankCardFragment.class.getName());
        getBankCards();
    }

    @Override
    protected void findViews() {
        cardListView = (InnerListView) contentView
                .findViewById(R.id.bank_card_list);
        addButton = (LinearLayout) contentView.findViewById(R.id.bank_card_add);
        addImageButton(R.drawable.address_add, "添加银行卡", new OnClickListener() {

            @Override
            public void onClick(View v) {
                havePayPassword();
            }
        });
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        cardListView.setAdapter(bandCardAdapter);
    }

    @Override
    protected void registerViews() {
        addButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                havePayPassword();
            }
        });
        cardListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Bundle bundle = new Bundle();
                bundle.putString("bankCard", bankCards.get(arg2)
                        .getJsonObject().toString());
                jump(BankCardDetailFragment.class.getName(), "我的银行卡", bundle);
            }
        });
    }

    class BandCardAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bankCards.size();
        }

        @Override
        public Object getItem(int position) {
            return bankCards.get(position);
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
                        R.layout.list_money_bank_card, null);
                viewHolder.imageView = (ImageView) convertView
                        .findViewById(R.id.bank_card_bank_image);
                viewHolder.cardNumberTextView = (TextView) convertView
                        .findViewById(R.id.bank_card_number);
                viewHolder.cardTypeTextView = (TextView) convertView
                        .findViewById(R.id.bank_card_type);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            ImageLoader.getInstance().displayImage(
                    bankCards.get(position).getBank().getIcon(),
                    viewHolder.imageView);
            viewHolder.cardNumberTextView.setText(getSecretCardNuber(bankCards
                    .get(position).getBanknumber()));
            viewHolder.cardTypeTextView.setText(bankCards.get(position)
                    .getBank().getName()
                    + "  " + bankCards.get(position).getCardType());
            return convertView;
        }

        class ViewHolder {
            ImageView imageView;
            TextView cardNumberTextView, cardTypeTextView;
        }
    }

    private void havePayPassword() {
        post(API.MONEY_PAY_PASSWORD_STATE, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject jsonObject = object.optJSONObject("databody");
                            if (jsonObject != null) {
                                if (jsonObject.optBoolean("pwd")) {
                                    // 已设置支付密码
                                    jump(BankCardBindFragment.class.getName(), "添加银行卡");
                                } else {
                                    // 未设置支付密码
                                    ApplicationTool.showToast("请先设置支付密码");
                                    jump(PayPasswordSetFragment.class.getName(), "设置支付密码");
                                }
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("访问服务器失败，请稍候再试！");
            }
        });
    }

    private void getBankCards() {
        MoneyAccount.getMoneyAccount().getBankCards().clear();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("sn", User.getUser().getCacheKey()));
        requestParams.add(new RequestParam("memberid", User.getUser().getUseraccount()));
        post(API.MONEY_BANK_BINDED, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                initbankCards(result);
                bankCards = MoneyAccount.getMoneyAccount().getBankCards();
                bandCardAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initbankCards(String result) {
        MoneyAccount.getMoneyAccount().getBankCards().clear();
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("success")) {
                    JSONArray array = object.optJSONArray("databody");
                    if (array != null) {
                        List<ModelBankCard> bankCards = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            bankCards.add(new ModelBankCard(array.optJSONObject(i)));
                        }
                        MoneyAccount.getMoneyAccount().setGetBankCardFailed(
                                false);
                        MoneyAccount.getMoneyAccount().setBankCards(bankCards);
                        return;
                    }
                }
                MoneyAccount.getMoneyAccount().setGetBankCardFailed(true);
                ApplicationTool.showToast(object.optString("msg"));
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        MoneyAccount.getMoneyAccount().setGetBankCardFailed(true);
        ApplicationTool.showToast("获取绑定的银行卡信息失败！");
        return;
    }

}
