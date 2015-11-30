package yitgogo.consumer.money.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.money.task.GetBankCards;
import yitgogo.consumer.money.task.LoginMoneyTask;
import yitgogo.consumer.money.task.VerifyPayPasswordTask;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.user.ui.UserLoginFragment;
import yitgogo.consumer.view.CodeEditDialog;
import yitgogo.consumer.view.InnerListView;
import yitgogo.consumer.view.Notify;

public class PayFragment extends BaseNotifyFragment {

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

    LoginMoneyTask loginMoneyTask;
    VerifyPayPasswordTask verifyPayPasswordTask;
    GetBankCards getBankCards;

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
    public void onDestroy() {
        stopAsyncTasks();
        super.onDestroy();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("orderNumbers")) {
                List<String> orderNumbers = bundle
                        .getStringArrayList("orderNumbers");
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
        bankCards = new ArrayList<ModelBankCard>();
        bandCardAdapter = new BandCardAdapter();
    }

    protected void findViews() {
        orderNumberTextView = (TextView) contentView
                .findViewById(R.id.pay_order_number);
        bankCardListView = (InnerListView) contentView
                .findViewById(R.id.pay_bankcards);
        amountTextView = (TextView) contentView.findViewById(R.id.pay_amount);
        payButton = (TextView) contentView.findViewById(R.id.pay_pay);
        addBankCardButton = (LinearLayout) layoutInflater.inflate(
                R.layout.list_pay_bank_card_add, null);
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
                        Notify.show("请选择付款方式");
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
        serialNumber = serialNumberFormat.format(new Date(System
                .currentTimeMillis()));
        new GetSmsCode().execute();
    }

    /**
     * @param type 1:银行卡支付
     *             <p/>
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
                            new PayBalance().execute(payPassword);
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

    /**
     * @author Tiger
     * @Url http://192.168.8.98:8086/api/settlement/kuaiqian/getAuthCodeApi
     * @Parameters [pan=6225881285953427, expiredDate=, cvv2=, amount=0.01,
     * externalRefNumber=20150818051557895, customerId=zhaojin1992,
     * cardHolderName=赵晋, cardHolderId=510823199201163922,
     * phoneNO=18584182653]
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[],"totalCount":1,"dataMap":{"responseCode":"00", "customerId"
     * :"zhaojin1992","token":"1133738","merchantId": "104110045112012"
     * ,"storablePan":"6225883427"},"object":null}
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[],"totalCount":1,"dataMap":{"responseCode":"L5", "customerId"
     * :"13032889558","responseTextMessage":"卡号无效/卡号输入错误" ,"merchantId"
     * :"104110045112012","storablePan":"6210986422"} ,"object":null}
     * @Result {"message":"ok","state"
     * :"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
     * :1,"dataMap":{"errorMessage":
     * "Data length of element[expiredDate] is incorrect!"
     * ,"errorCode":"B.MGW.0120","version":"1.0"},"object":null}
     */
    class GetSmsCode extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("pan", selectedBankCard
                    .getBanknumber()));
            // nameValuePairs.add(new BasicNameValuePair("pan",
            // "6225881285953427"));
            nameValuePairs.add(new BasicNameValuePair("expiredDate",
                    selectedBankCard.getExpiredDate()));
            nameValuePairs.add(new BasicNameValuePair("cvv2", selectedBankCard
                    .getCvv2()));
            nameValuePairs.add(new BasicNameValuePair("amount", decimalFormat
                    .format(totalMoney)));
            nameValuePairs.add(new BasicNameValuePair("externalRefNumber",
                    serialNumber));
            nameValuePairs.add(new BasicNameValuePair("customerId",
                    selectedBankCard.getOrg()));
            // nameValuePairs.add(new BasicNameValuePair("customerId",
            // "HY048566511863"));
            nameValuePairs.add(new BasicNameValuePair("cardHolderName",
                    selectedBankCard.getCradname()));
            nameValuePairs.add(new BasicNameValuePair("cardHolderId",
                    selectedBankCard.getIdCard()));
            nameValuePairs.add(new BasicNameValuePair("phoneNO",
                    selectedBankCard.getMobile()));
            nameValuePairs.add(new BasicNameValuePair("bankCode",
                    selectedBankCard.getBank().getCode()));
            if (selectedBankCard.getCardType().equalsIgnoreCase("储蓄卡")) {
                nameValuePairs.add(new BasicNameValuePair("isBankType", "1"));
            } else {
                nameValuePairs.add(new BasicNameValuePair("isBankType", "2"));
            }
            return netUtil.postWithoutCookie(API.API_PAY_BIND,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                        JSONObject dataMap = object.optJSONObject("dataMap");
                        if (dataMap != null) {
                            bindResult = new BindResult(dataMap);
                            if (bindResult.getResponseCode().equals("00")) {
                                new SmsDialog()
                                        .show(getFragmentManager(), null);
                                return;
                            }
                            if (TextUtils.isEmpty(bindResult
                                    .getResponseTextMessage())) {
                                Notify.show("获取验证码失败");
                            } else {
                                Notify.show(bindResult.getResponseTextMessage());
                            }
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Notify.show("获取验证码失败");
        }
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
                        new PaySecondTime().execute(code);
                    } else {
                        new PayFirstTime().execute(code);
                    }
                }
                super.onDismiss(dialog);
            }
        };
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.98:8086/api/settlement/kuaiqian/payDataFirstApi
     * @Parameters [payInfoType=1, orderNumber=YT54654564,
     * cardNo=6225881285953427, externalRefNumber=20150818060530114,
     * storableCardNo=6210982, expiredDate=, cvv2=, amount=0.01,
     * customerId=zhaojin1992, cardHolderName=赵晋,
     * cardHolderId=510823199201163922, phone=18584182653,
     * validCode=157871, token=1133850]
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[],"totalCount":1,"dataMap":{"responseCode":"00",
     * "responseTextMessage"
     * :"会员不存在","externalRefNumber":"20150818060530114" },"object":null}
     * @Result {"message":"ok","state"
     * :"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
     * :1,"dataMap":{"responseCode":"T6","responseTextMessage":"验证码不匹配",
     * "externalRefNumber":"20150818061712246"},"object":null}
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
     * "totalCount"
     * :1,"dataMap":{"responseCode":"OT","responseTextMessage"
     * :"交易金额太小","externalRefNumber":"20150819063754516"},"object":null}
     * @Url http://192.168.8.98:8086/api/settlement/kuaiqian/getAuthCodeApi
     * @Parameters [pan=6217003810000524929, expiredDate=, cvv2=, amount=1.00,
     * externalRefNumber=20150819064740036, customerId=18615760358,
     * cardHolderName=肖宗其, cardHolderId=510525198705013057,
     * phoneNO=18615760358]
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[],"totalCount":1,"dataMap":{"responseCode":"00", "customerId"
     * :"18615760358","token":"299196900","merchantId"
     * :"812310053990815" ,"storablePan":"6217004929"},"object":null}
     * @Url http://192.168.8.98:8086/api/settlement/kuaiqian/payDataFirstApi
     * @Parameters [payInfoType=1, orderNumber=YT3420562512,
     * cardNo=6217003810000524929,
     * externalRefNumber=20150819064740036,
     * storableCardNo=6217004929, expiredDate=, cvv2=, amount=1.00,
     * customerId=18615760358, cardHolderName=肖宗其,
     * cardHolderId=510525198705013057, phone=18615760358,
     * validCode=777159, token=299196900]
     * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList"
     * :[],"totalCount":1,"dataMap":{"responseCode":"00",
     * "responseTextMessage"
     * :"交易成功","externalRefNumber":"20150819064740036" },"object":null}
     */
    class PayFirstTime extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("payInfoType", orderType
                    + ""));
            nameValuePairs.add(new BasicNameValuePair("orderNumber",
                    orderNumbers));
            nameValuePairs.add(new BasicNameValuePair("cardNo",
                    selectedBankCard.getBanknumber()));
            nameValuePairs.add(new BasicNameValuePair("externalRefNumber",
                    serialNumber));
            nameValuePairs.add(new BasicNameValuePair("storableCardNo",
                    getShortCardNumber(selectedBankCard.getBanknumber())));
            nameValuePairs.add(new BasicNameValuePair("expiredDate",
                    selectedBankCard.getExpiredDate()));
            nameValuePairs.add(new BasicNameValuePair("cvv2", selectedBankCard
                    .getCvv2()));
            nameValuePairs.add(new BasicNameValuePair("amount", decimalFormat
                    .format(totalMoney)));
            nameValuePairs.add(new BasicNameValuePair("customerId",
                    selectedBankCard.getOrg()));
            nameValuePairs.add(new BasicNameValuePair("cardHolderName",
                    selectedBankCard.getCradname()));
            nameValuePairs.add(new BasicNameValuePair("cardHolderId",
                    selectedBankCard.getIdCard()));
            nameValuePairs.add(new BasicNameValuePair("phone", selectedBankCard
                    .getMobile()));
            nameValuePairs.add(new BasicNameValuePair("validCode", params[0]));
            if (!TextUtils.isEmpty(bindResult.getToken())) {
                nameValuePairs.add(new BasicNameValuePair("token", bindResult
                        .getToken()));
            }
            nameValuePairs.add(new BasicNameValuePair("bankCode",
                    selectedBankCard.getBank().getCode()));
            if (selectedBankCard.getCardType().equalsIgnoreCase("储蓄卡")) {
                nameValuePairs.add(new BasicNameValuePair("isBankType", "1"));
            } else {
                nameValuePairs.add(new BasicNameValuePair("isBankType", "2"));
            }
            return netUtil.postWithoutCookie(API.API_PAY_FIRST_TIME,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
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
                                Notify.show("付款失败");
                            } else {
                                Notify.show(payResult.getResponseTextMessage());
                            }
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Notify.show("付款失败");
        }
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.98:8086/api/settlement/kuaiqian/payDataTwiceApi
     * @Parameters [payInfoType=1, orderNumber=YT1311081879,
     * storableCardNo=6228489814,
     * externalRefNumber=20150818083055214, amount=771.00,
     * customerId=HY612813352788, phone=, validCode=, token=]
     * @Result {"message":"ok","state":"SUCCESS"
     * ,"cacheKey":null,"dataList":[],"totalCount"
     * :1,"dataMap":{"responseCode"
     * :"00","responseTextMessage":"交易成功","externalRefNumber"
     * :"20150818083055214"},"object":null}
     */
    class PaySecondTime extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("payInfoType", orderType
                    + ""));
            nameValuePairs.add(new BasicNameValuePair("orderNumber",
                    orderNumbers));
            nameValuePairs.add(new BasicNameValuePair("storableCardNo",
                    getShortCardNumber(selectedBankCard.getBanknumber())));
            nameValuePairs.add(new BasicNameValuePair("externalRefNumber",
                    serialNumber));
            nameValuePairs.add(new BasicNameValuePair("amount", decimalFormat
                    .format(totalMoney)));
            nameValuePairs.add(new BasicNameValuePair("customerId",
                    selectedBankCard.getOrg()));
            nameValuePairs.add(new BasicNameValuePair("phone", selectedBankCard
                    .getMobile()));
            nameValuePairs.add(new BasicNameValuePair("validCode", params[0]));
            nameValuePairs.add(new BasicNameValuePair("token", bindResult
                    .getToken()));
            return netUtil.postWithoutCookie(API.API_PAY_SECOND_TIME,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
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
            Notify.show("付款失败");
        }
    }

    /**
     * @author Tiger
     * @Result {"message":"ok","state"
     * :"SUCCESS","cacheKey":null,"dataList":[],"totalCount"
     * :1,"dataMap":{"status":"error","msg":"订单及类型不能为空"},"object":null}
     */
    class PayBalance extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("memberAccount", User
                    .getUser().getUseraccount()));
            nameValuePairs.add(new BasicNameValuePair("orderType", orderType
                    + ""));
            nameValuePairs.add(new BasicNameValuePair("orderNumbers",
                    orderNumbers));
            nameValuePairs.add(new BasicNameValuePair("customerName", User
                    .getUser().getRealname()));
            nameValuePairs.add(new BasicNameValuePair("apAmount", decimalFormat
                    .format(totalMoney)));
            nameValuePairs.add(new BasicNameValuePair("pwd", params[0]));
            return netUtil.postWithoutCookie(API.API_PAY_BALANCE,
                    nameValuePairs, false, false);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (result.length() > 0) {
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
                                Notify.show(dataMap.optString("msg"));
                                return;
                            }
                        }
                    } else {
                        Notify.show(object.optString("message"));
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Notify.show("付款失败");
        }
    }

    private void paySuccess() {
        Notify.show("付款成功");
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
                        Notify.show("请输入验证码");
                    } else {

                        if (selectedBankCard.isValidation()) {
                            new PaySecondTime().execute(smscodeEditText
                                    .getText().toString().trim());
                        } else {
                            new PayFirstTime().execute(smscodeEditText
                                    .getText().toString().trim());
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
                    new GetSmsCode().execute();
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
                        Notify.show("请输入信用卡有效期");
                    } else if (TextUtils.isEmpty(codeEditText.getText()
                            .toString().trim())) {
                        Notify.show("请输入信用卡校验码");
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

    private void stopAsyncTasks() {
        if (loginMoneyTask != null) {
            if (loginMoneyTask.getStatus() == Status.RUNNING) {
                loginMoneyTask.cancel(true);
            }
        }
        if (verifyPayPasswordTask != null) {
            if (verifyPayPasswordTask.getStatus() == Status.RUNNING) {
                verifyPayPasswordTask.cancel(true);
            }
        }
        if (getBankCards != null) {
            if (getBankCards.getStatus() == Status.RUNNING) {
                getBankCards.cancel(true);
            }
        }
    }

    private void loginMoney() {
        if (loginMoneyTask != null) {
            if (loginMoneyTask.getStatus() == Status.RUNNING) {
                return;
            }
        }
        loginMoneyTask = new LoginMoneyTask() {

            @Override
            protected void onPreExecute() {
                showLoading();
            }

            @Override
            protected void onPostExecute(String result) {
                hideLoading();
                super.onPostExecute(result);
                if (MoneyAccount.getMoneyAccount().isLogin()) {
                    getBankCards();
                } else {
                    getActivity().finish();
                }
            }
        };
        loginMoneyTask.execute();
    }

    private void verifyPayPassword(String payPassword) {
        if (verifyPayPasswordTask != null) {
            if (verifyPayPasswordTask.getStatus() == Status.RUNNING) {
                return;
            }
        }
        verifyPayPasswordTask = new VerifyPayPasswordTask() {

            @Override
            protected void onPreExecute() {
                showLoading();
            }

            @Override
            protected void onPostExecute(String result) {
                hideLoading();
                super.onPostExecute(result);
                if (passwordIsRight) {
                    pay();
                } else {
                    Notify.show("支付密码错误");
                }
            }
        };
        verifyPayPasswordTask.execute(payPassword);
    }

    private void getBankCards() {
        if (getBankCards != null) {
            if (getBankCards.getStatus() == Status.RUNNING) {
                return;
            }
        }
        getBankCards = new GetBankCards() {
            @Override
            protected void onPreExecute() {
                showLoading();
            }

            @Override
            protected void onPostExecute(String result) {
                hideLoading();
                super.onPostExecute(result);
                if (MoneyAccount.getMoneyAccount().isGetBankCardFailed()) {
                    getActivity().finish();
                } else {
                    bankCards = MoneyAccount.getMoneyAccount().getBankCards();
                    ModelBankCard bankCard = new ModelBankCard("钱袋子余额");
                    bankCards.add(0, bankCard);
                    bandCardAdapter.notifyDataSetChanged();
                }
            }
        };
        getBankCards.execute();
    }

}
