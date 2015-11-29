package yitgogo.consumer.money.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.CodeEditDialog;
import yitgogo.consumer.view.InnerListView;

public class PayFragment extends BaseNetworkFragment {

    /**
     * 订单数据
     */
    String orderNumbers = "";
    double totalMoney = 0;
    int orderType = 1;
    int productCount = 1;
    String serialNumber = "";

    public static final int ORDER_TYPE_YY = 1;
    public static final int ORDER_TYPE_YD = 2;
    public static final int ORDER_TYPE_LP = 3;
    public static final int ORDER_TYPE_LS = 4;
    public static final int ORDER_TYPE_BM = 5;
    public static final int ORDER_TYPE_SN = 6;

    InnerListView bankCardListView;
    TextView orderNumberTextView, amountTextView, payButton;
    SimpleDateFormat serialNumberFormat = new SimpleDateFormat(
            "yyyyMMddHHmmssSSS");
    ModelBankCard selectedBankCard = new ModelBankCard();
    BindResult bindResult = new BindResult();

    List<ModelBankCard> bankCards;
    BandCardAdapter bandCardAdapter;

    LinearLayout addBankCardButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pay);
        init();
        findViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PayFragment.class.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PayFragment.class.getName());
        if (User.getUser().isLogin()) {
            loginMoney();
        } else {
            jump(UserLoginFragment.class.getName(), "会员登录");
            getActivity().finish();
        }
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("orderNumbers")) {
                List<String> orderNumbers = bundle.getStringArrayList("orderNumbers");
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < orderNumbers.size(); i++) {
                    if (i > 0) {
                        builder.append(",");
                    }
                    builder.append(orderNumbers.get(i));
                }
                this.orderNumbers = builder.toString();
            }
            if (bundle.containsKey("totalMoney")) {
                totalMoney = bundle.getDouble("totalMoney");
            }
            if (bundle.containsKey("orderType")) {
                orderType = bundle.getInt("orderType");
            }
            if (bundle.containsKey("productCount")) {
                productCount = bundle.getInt("productCount");
            }
        }
        bankCards = new ArrayList<>();
        bandCardAdapter = new BandCardAdapter();
    }

    protected void findViews() {
        orderNumberTextView = (TextView) contentView.findViewById(R.id.pay_order_number);
        bankCardListView = (InnerListView) contentView.findViewById(R.id.pay_bankcards);
        amountTextView = (TextView) contentView.findViewById(R.id.pay_amount);
        payButton = (TextView) contentView.findViewById(R.id.pay_pay);
        addBankCardButton = (LinearLayout) layoutInflater.inflate(R.layout.list_pay_bank_card_add, null);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        orderNumberTextView.setText(orderNumbers.toString());
        amountTextView.setText(Parameters.CONSTANT_RMB
                + decimalFormat.format(totalMoney));
        bankCardListView.addFooterView(addBankCardButton);
        bankCardListView.setAdapter(bandCardAdapter);
    }

    @Override
    protected void registerViews() {
        bankCardListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                selectedBankCard = bankCards.get(arg2);
                bandCardAdapter.notifyDataSetChanged();
            }
        });
        payButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (totalMoney > 0) {
                    if (TextUtils.isEmpty(selectedBankCard.getId())) {
                        ApplicationTool.showToast("请选择付款方式");
                    } else {
                        if (selectedBankCard.getCardType().contains("储蓄")) {
                            inputPayPassword(1);
                        } else if (selectedBankCard.getCardType()
                                .contains("信用")) {
                            new CreditInfoDialog().show(getFragmentManager(),
                                    null);
                        } else if (selectedBankCard.getCardType().contains(
                                "钱袋子")) {
                            inputPayPassword(2);
                        }
                    }
                }
            }
        });
        addBankCardButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                jump(BankCardBindFragment.class.getName(), "绑定银行卡");
            }
        });
    }

    private void pay() {
        serialNumber = serialNumberFormat.format(new Date(System.currentTimeMillis()));
        getSmsCode();
    }

    /**
     * @param type 1:银行卡支付
     *             <p>
     *             2:钱袋子月支付
     */
    private void inputPayPassword(int type) {
        final int payType = type;
        PayPasswordDialog passwordDialog = new PayPasswordDialog("请输入支付密码",
                false) {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (!TextUtils.isEmpty(payPassword)) {
                    switch (payType) {
                        case 1:
                            verifyPayPassword(payPassword);
                            break;
                        case 2:
                            payBalance(payPassword);
                            break;
                        default:
                            break;
                    }
                }
                super.onDismiss(dialog);
            }
        };
        passwordDialog.show(getFragmentManager(), null);
    }

    private void getSmsCode() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("pan", selectedBankCard.getBanknumber()));
        // requestParams.add(new RequestParam("pan",
        // "6225881285953427"));
        requestParams.add(new RequestParam("expiredDate", selectedBankCard.getExpiredDate()));
        requestParams.add(new RequestParam("cvv2", selectedBankCard.getCvv2()));
        requestParams.add(new RequestParam("amount", decimalFormat.format(totalMoney)));
        requestParams.add(new RequestParam("externalRefNumber", serialNumber));
        requestParams.add(new RequestParam("customerId", selectedBankCard.getOrg()));
        // requestParams.add(new RequestParam("customerId",
        // "HY048566511863"));
        requestParams.add(new RequestParam("cardHolderName", selectedBankCard.getCradname()));
        requestParams.add(new RequestParam("cardHolderId", selectedBankCard.getIdCard()));
        requestParams.add(new RequestParam("phoneNO", selectedBankCard.getMobile()));
        requestParams.add(new RequestParam("bankCode", selectedBankCard.getBank().getCode()));
        if (selectedBankCard.getCardType().equalsIgnoreCase("储蓄卡")) {
            requestParams.add(new RequestParam("isBankType", "1"));
        } else {
            requestParams.add(new RequestParam("isBankType", "2"));
        }
        post(API.API_PAY_BIND, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
                            if (dataMap != null) {
                                bindResult = new BindResult(dataMap);
                                if (bindResult.getResponseCode().equals("00")) {
                                    new SmsDialog().show(getFragmentManager(), null);
                                    return;
                                }
                                if (TextUtils.isEmpty(bindResult
                                        .getResponseTextMessage())) {
                                    ApplicationTool.showToast("获取验证码失败");
                                } else {
                                    ApplicationTool.showToast(bindResult.getResponseTextMessage());
                                }
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("获取验证码失败");
            }
        });
    }

    private void showSmsCodeDialog() {
        CodeEditDialog codeEditDialog = new CodeEditDialog("请输入验证码", false) {

            @Override
            @NonNull
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                return super.onCreateDialog(savedInstanceState);
            }

            @Override
            public void onDismiss(DialogInterface dialog) {
                if (ok) {
                    if (selectedBankCard.isValidation()) {
                        paySecondTime(code);
                    } else {
                        payFirstTime(code);
                    }
                }
                super.onDismiss(dialog);
            }
        };
    }

    private void payFirstTime(String validCode) {
        List<RequestParam> requestParams = new ArrayList<RequestParam>();
        requestParams.add(new RequestParam("payInfoType", orderType + ""));
        requestParams.add(new RequestParam("orderNumber", orderNumbers));
        requestParams.add(new RequestParam("cardNo", selectedBankCard.getBanknumber()));
        requestParams.add(new RequestParam("externalRefNumber", serialNumber));
        requestParams.add(new RequestParam("storableCardNo", getShortCardNumber(selectedBankCard.getBanknumber())));
        requestParams.add(new RequestParam("expiredDate", selectedBankCard.getExpiredDate()));
        requestParams.add(new RequestParam("cvv2", selectedBankCard.getCvv2()));
        requestParams.add(new RequestParam("amount", decimalFormat.format(totalMoney)));
        requestParams.add(new RequestParam("customerId", selectedBankCard.getOrg()));
        requestParams.add(new RequestParam("cardHolderName", selectedBankCard.getCradname()));
        requestParams.add(new RequestParam("cardHolderId", selectedBankCard.getIdCard()));
        requestParams.add(new RequestParam("phone", selectedBankCard.getMobile()));
        requestParams.add(new RequestParam("validCode", validCode));
        if (!TextUtils.isEmpty(bindResult.getToken())) {
            requestParams.add(new RequestParam("token", bindResult.getToken()));
        }
        requestParams.add(new RequestParam("bankCode", selectedBankCard.getBank().getCode()));
        if (selectedBankCard.getCardType().equalsIgnoreCase("储蓄卡")) {
            requestParams.add(new RequestParam("isBankType", "1"));
        } else {
            requestParams.add(new RequestParam("isBankType", "2"));
        }
        post(API.API_PAY_FIRST_TIME, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
                            if (dataMap != null) {
                                BindResult payResult = new BindResult(dataMap);
                                if (payResult.getResponseCode().equals("00")) {
                                    paySuccess();
                                    return;
                                }
                                if (TextUtils.isEmpty(payResult
                                        .getResponseTextMessage())) {
                                    ApplicationTool.showToast("付款失败");
                                } else {
                                    ApplicationTool.showToast(payResult.getResponseTextMessage());
                                }
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("付款失败");
            }
        });
    }

    private void paySecondTime(String validCode) {
        List<RequestParam> requestParams = new ArrayList<RequestParam>();
        requestParams.add(new RequestParam("payInfoType", orderType + ""));
        requestParams.add(new RequestParam("orderNumber", orderNumbers));
        requestParams.add(new RequestParam("storableCardNo", getShortCardNumber(selectedBankCard.getBanknumber())));
        requestParams.add(new RequestParam("externalRefNumber", serialNumber));
        requestParams.add(new RequestParam("amount", decimalFormat.format(totalMoney)));
        requestParams.add(new RequestParam("customerId", selectedBankCard.getOrg()));
        requestParams.add(new RequestParam("phone", selectedBankCard.getMobile()));
        requestParams.add(new RequestParam("validCode", validCode));
        requestParams.add(new RequestParam("token", bindResult.getToken()));
        post(API.API_PAY_SECOND_TIME, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {

                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
                            if (dataMap != null) {
                                BindResult payResult = new BindResult(dataMap);
                                if (payResult.getResponseCode().equals("00")) {
                                    paySuccess();
                                    return;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("付款失败");
            }
        });
    }

    private void payBalance(String pwd) {
        List<RequestParam> requestParams = new ArrayList<RequestParam>();
        requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        requestParams.add(new RequestParam("orderType", orderType + ""));
        requestParams.add(new RequestParam("orderNumbers", orderNumbers));
        requestParams.add(new RequestParam("customerName", User.getUser().getRealname()));
        requestParams.add(new RequestParam("apAmount", decimalFormat.format(totalMoney)));
        requestParams.add(new RequestParam("pwd", pwd));
        post(API.API_PAY_BALANCE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
                            if (dataMap != null) {
                                if (dataMap.optString("status").equalsIgnoreCase(
                                        "ok")) {
                                    paySuccess();
                                    return;
                                } else {
                                    ApplicationTool.showToast(dataMap.optString("msg"));
                                    return;
                                }
                            }
                        } else {
                            ApplicationTool.showToast(object.optString("message"));
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("付款失败");
            }
        });
    }

    private void paySuccess() {
        ApplicationTool.showToast("付款成功");
        showOrder(orderType);
        getActivity().finish();
    }

    private String getShortCardNumber(String cardNumber) {
        if (cardNumber.length() > 10) {
            return cardNumber.substring(0, 6)
                    + cardNumber.substring(cardNumber.length() - 4,
                    cardNumber.length());
        }
        return "";
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
            ModelBankCard bankCard = bankCards.get(position);
            if (bankCard.getId().equals(selectedBankCard.getId())) {
                viewHolder.selected
                        .setImageResource(R.drawable.iconfont_check_checked);
            } else {
                viewHolder.selected
                        .setImageResource(R.drawable.iconfont_check_normal);
            }
            ImageLoader.getInstance().displayImage(
                    bankCard.getBank().getIcon(), viewHolder.bankImageView);
            if (bankCard.getCardType().contains("钱袋子")) {
                viewHolder.cardNumberTextView.setText("钱袋子余额");
                viewHolder.cardTypeTextView.setText("剩余:"
                        + Parameters.CONSTANT_RMB
                        + decimalFormat.format(MoneyAccount.getMoneyAccount()
                        .getBalance()));
            } else {
                viewHolder.cardNumberTextView
                        .setText(getSecretCardNuber(bankCard.getBanknumber()));
                viewHolder.cardTypeTextView.setText(bankCard.getBank()
                        .getName() + "  " + bankCard.getCardType());
            }
            return convertView;
        }

        class ViewHolder {
            ImageView selected, bankImageView;
            TextView cardNumberTextView, cardTypeTextView;
        }
    }

    /**
     * @author Tiger
     * @Json "dataMap":{"responseCode":"00", "customerId"
     * :"zhaojin1992","token":"1133738","merchantId": "104110045112012"
     * ,"storablePan":"6225883427"}
     */
    class BindResult {

        String responseCode = "", responseTextMessage = "", customerId = "",
                token = "", merchantId = "", storablePan = "";

        public BindResult() {
        }

        public BindResult(JSONObject object) {
            if (object != null) {
                if (object.has("responseCode")) {
                    if (!object.optString("responseCode").equalsIgnoreCase(
                            "null")) {
                        responseCode = object.optString("responseCode");
                    }
                }
                if (object.has("responseTextMessage")) {
                    if (!object.optString("responseTextMessage")
                            .equalsIgnoreCase("null")) {
                        responseTextMessage = object
                                .optString("responseTextMessage");
                    }
                }
                if (object.has("customerId")) {
                    if (!object.optString("customerId")
                            .equalsIgnoreCase("null")) {
                        customerId = object.optString("customerId");
                    }
                }
                if (object.has("token")) {
                    if (!object.optString("token").equalsIgnoreCase("null")) {
                        token = object.optString("token");
                    }
                }
                if (object.has("merchantId")) {
                    if (!object.optString("merchantId")
                            .equalsIgnoreCase("null")) {
                        merchantId = object.optString("merchantId");
                    }
                }
                if (object.has("storablePan")) {
                    if (!object.optString("storablePan").equalsIgnoreCase(
                            "null")) {
                        storablePan = object.optString("storablePan");
                    }
                }
            }
        }

        public String getResponseCode() {
            return responseCode;
        }

        public String getCustomerId() {
            return customerId;
        }

        public String getToken() {
            return token;
        }

        public String getMerchantId() {
            return merchantId;
        }

        public String getStorablePan() {
            return storablePan;
        }

        public String getResponseTextMessage() {
            return responseTextMessage;
        }
    }

    class SmsDialog extends DialogFragment {

        View dialogView;
        TextView okButton, getCodeButton;
        EditText smscodeEditText;
        ImageView closeButton;
        Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.obj != null) {
                    getCodeButton.setText(msg.obj + "s");
                } else {
                    getCodeButton.setEnabled(true);
                    getCodeButton.setTextColor(getResources().getColor(
                            R.color.textColorSecond));
                    getCodeButton.setText("获取验证码");
                }
            }

            ;
        };
        boolean isFinish = false;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
            findViews();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            isFinish = true;
        }

        private void init() {
            setCancelable(false);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    int time = 60;
                    while (time > -1) {
                        if (isFinish) {
                            break;
                        }
                        try {
                            Message message = new Message();
                            if (time > 0) {
                                message.obj = time;
                            }
                            handler.sendMessage(message);
                            Thread.sleep(1000);
                            time--;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_pay_smscode,
                    null);
            closeButton = (ImageView) dialogView
                    .findViewById(R.id.pay_sms_close);
            smscodeEditText = (EditText) dialogView
                    .findViewById(R.id.pay_sms_code);
            getCodeButton = (TextView) dialogView
                    .findViewById(R.id.pay_sms_get);
            okButton = (TextView) dialogView.findViewById(R.id.pay_sms_ok);
            getCodeButton.setEnabled(false);
            okButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(smscodeEditText.getText().toString()
                            .trim())) {
                        ApplicationTool.showToast("请输入验证码");
                    } else {

                        if (selectedBankCard.isValidation()) {
                            paySecondTime(smscodeEditText.getText().toString());
                        } else {
                            payFirstTime(smscodeEditText.getText().toString());
                        }
                        dismiss();
                    }
                }
            });
            closeButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            getCodeButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    getSmsCode();
                    dismiss();
                }
            });
        }
    }

    class CreditInfoDialog extends DialogFragment {

        View dialogView;
        TextView okButton, cancelButton;
        EditText timeEditText, codeEditText;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            init();
            findViews();
        }

        private void init() {
            setCancelable(false);
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = new Dialog(getActivity());
            dialog.getWindow().setBackgroundDrawableResource(R.color.divider);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dialogView, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return dialog;
        }

        private void findViews() {
            dialogView = layoutInflater.inflate(R.layout.dialog_credit_edit,
                    null);
            timeEditText = (EditText) dialogView
                    .findViewById(R.id.pay_credit_card_time);
            codeEditText = (EditText) dialogView
                    .findViewById(R.id.pay_credit_card_code);
            cancelButton = (TextView) dialogView
                    .findViewById(R.id.pay_credit_card_cancel);
            okButton = (TextView) dialogView
                    .findViewById(R.id.pay_credit_card_ok);
            okButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(timeEditText.getText().toString()
                            .trim())) {
                        ApplicationTool.showToast("请输入信用卡有效期");
                    } else if (TextUtils.isEmpty(codeEditText.getText()
                            .toString().trim())) {
                        ApplicationTool.showToast("请输入信用卡校验码");
                    } else {
                        selectedBankCard.setExpiredDate(timeEditText.getText()
                                .toString().trim());
                        selectedBankCard.setCvv2(codeEditText.getText()
                                .toString().trim());
                        inputPayPassword(1);
                        dismiss();
                    }
                }
            });
            cancelButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
    }

    private void verifyPayPassword(String paypwd) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("sn", User.getUser().getCacheKey()));
        requestParams.add(new RequestParam("payaccount", MoneyAccount.getMoneyAccount().getPayaccount()));
        requestParams.add(new RequestParam("paypwd", paypwd));
        post(API.MONEY_PAY_PASSWORD_VALIDATE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (passwordIsRight(result)) {
                    pay();
                } else {
                    ApplicationTool.showToast("支付密码错误");
                }
            }
        });
    }

    private boolean passwordIsRight(String result) {
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("success")) {
                    JSONObject jsonObject = object.optJSONObject("databody");
                    if (jsonObject != null) {
                        return jsonObject.optBoolean("vli");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void loginMoney() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("sn", User.getUser().getCacheKey()));
        post(API.MONEY_LOGIN, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                initMoney(result);
                if (MoneyAccount.getMoneyAccount().isLogin()) {
                    getBankCards();
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

    private void getBankCards() {
        MoneyAccount.getMoneyAccount().getBankCards().clear();
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("sn", User.getUser().getCacheKey()));
        requestParams.add(new RequestParam("memberid", User.getUser().getUseraccount()));
        post(API.MONEY_BANK_BINDED, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                initbankCards(result);
                if (MoneyAccount.getMoneyAccount().isGetBankCardFailed()) {
                    getActivity().finish();
                } else {
                    bankCards = MoneyAccount.getMoneyAccount().getBankCards();
                    ModelBankCard bankCard = new ModelBankCard("钱袋子余额");
                    bankCards.add(0, bankCard);
                    bandCardAdapter.notifyDataSetChanged();
                }
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
