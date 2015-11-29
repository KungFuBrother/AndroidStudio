package yitgogo.consumer.money.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.activity.ActivityFragment;
import yitgogo.consumer.bianmin.game.ui.GameFilterFragment;
import yitgogo.consumer.bianmin.phoneCharge.ui.PhoneChargeFragment;
import yitgogo.consumer.bianmin.qq.ui.QQChargeFragment;
import yitgogo.consumer.bianmin.telephone.ui.TelePhoneChargeFragment;
import yitgogo.consumer.bianmin.traffic.ui.TraffictSearchFragment;
import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class MoneyHomeFragment extends BaseNetworkFragment {

    FrameLayout cashLayout, bankCardLayout, takeOutButton, bianminPhoneButton,
            bianminKuandaiButton, bianminQQButton, bianminGameButton,
            bianminTrafficButton, changePayPassword, findPayPassword,
            shakeButton;
    LinearLayout accountLayout, bianminLayout;
    TextView cashTextView, bankCardTextView, passwordTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_home);
        init();
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(MoneyHomeFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(MoneyHomeFragment.class.getName());
        loginMoney();
    }

    @Override
    protected void findViews() {
        cashLayout = (FrameLayout) contentView
                .findViewById(R.id.money_account_cash_layout);
        accountLayout = (LinearLayout) contentView
                .findViewById(R.id.money_account_layout);
        cashTextView = (TextView) contentView
                .findViewById(R.id.money_account_cash);
        bankCardLayout = (FrameLayout) contentView
                .findViewById(R.id.money_account_bankcard_layout);
        bankCardTextView = (TextView) contentView
                .findViewById(R.id.money_account_bankcard);
        takeOutButton = (FrameLayout) contentView
                .findViewById(R.id.money_home_take_out);
        bianminPhoneButton = (FrameLayout) contentView
                .findViewById(R.id.money_bianmin_phone);
        bianminKuandaiButton = (FrameLayout) contentView
                .findViewById(R.id.money_bianmin_kuandai);
        bianminQQButton = (FrameLayout) contentView
                .findViewById(R.id.money_bianmin_qq);
        bianminGameButton = (FrameLayout) contentView
                .findViewById(R.id.money_bianmin_game);
        bianminTrafficButton = (FrameLayout) contentView
                .findViewById(R.id.money_bianmin_traffic);
        changePayPassword = (FrameLayout) contentView
                .findViewById(R.id.money_pay_password_change);
        passwordTextView = (TextView) contentView
                .findViewById(R.id.money_pay_password_change_lable);
        findPayPassword = (FrameLayout) contentView
                .findViewById(R.id.money_pay_password_find);
        bianminLayout = (LinearLayout) contentView
                .findViewById(R.id.money_bianmin_layout);
        shakeButton = (FrameLayout) contentView.findViewById(R.id.money_shake);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth());
        bianminLayout.setLayoutParams(layoutParams);
        LayoutParams accountLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ApplicationTool.getScreenWidth() / 2);
        accountLayout.setLayoutParams(accountLayoutParams);
    }

    @Override
    protected void registerViews() {
        cashLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(TradeHistoryFragment.class.getName(), "余额交易明细");
            }
        });
        bankCardLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(BankCardFragment.class.getName(), "我的银行卡");
            }
        });
        takeOutButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(TakeOutFragment.class.getName(), "提现");
            }
        });
        bianminPhoneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(PhoneChargeFragment.class.getName(), "手机充值");
            }
        });
        bianminKuandaiButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(TelePhoneChargeFragment.class.getName(), "固话宽带充值");
            }
        });
        bianminQQButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(QQChargeFragment.class.getName(), "QQ充值");
            }
        });
        bianminGameButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(GameFilterFragment.class.getName(), "游戏充值");
            }
        });
        bianminTrafficButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(TraffictSearchFragment.class.getName(), "违章查询");
            }
        });
        shakeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(ActivityFragment.class.getName(), "摇一摇");
            }
        });
    }

    private void loginMoney() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("sn", User.getUser().getCacheKey()));
        post(API.MONEY_LOGIN, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                initMoney(result);
                if (MoneyAccount.getMoneyAccount().isLogin()) {
                    cashTextView.setText(Parameters.CONSTANT_RMB
                            + decimalFormat.format(MoneyAccount
                            .getMoneyAccount().getBalance()));
                    getBankCards();
                    havePayPassword();
                } else {
                    getActivity().finish();
                }
            }
        });
    }

    private void initMoney(String result) {
        MoneyAccount.init(null);
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("success")) {
                    MoneyAccount.init(object.optJSONObject("databody"));
                    return;
                }
                ApplicationTool.showToast(object.optString("msg"));
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ApplicationTool.showToast("暂无法进入钱袋子，请稍候再试");
        return;
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
                                    findPayPassword.setVisibility(View.VISIBLE);
                                    findPayPassword.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            jump(PayPasswordFindFragment.class.getName(), "找回支付密码");
                                        }
                                    });
                                    passwordTextView.setText("修改支付密码");
                                    changePayPassword.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            jump(PayPasswordChangeFragment.class.getName(), "修改支付密码");
                                        }
                                    });
                                } else {
                                    // 未设置支付密码
                                    findPayPassword.setVisibility(View.INVISIBLE);
                                    findPayPassword.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                        }
                                    });
                                    passwordTextView.setText("设置支付密码");
                                    changePayPassword.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            jump(PayPasswordSetFragment.class.getName(), "设置支付密码");
                                        }
                                    });
                                }
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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
                bankCardTextView.setText(MoneyAccount.getMoneyAccount().getBankCards().size() + "");
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
