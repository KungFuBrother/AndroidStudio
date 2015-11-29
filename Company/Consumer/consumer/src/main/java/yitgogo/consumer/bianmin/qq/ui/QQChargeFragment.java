package yitgogo.consumer.bianmin.qq.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.bianmin.ModelBianminOrderResult;
import yitgogo.consumer.bianmin.ModelChargeInfo;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class QQChargeFragment extends BaseNetworkFragment {

    TextView priceTextView;
    EditText accountEditText, amountEditText;
    Button chargeButton;

    ModelChargeInfo chargeInfo = new ModelChargeInfo();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_bianmin_qq_charge);
        init();
        findViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(QQChargeFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(QQChargeFragment.class.getName());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void init() {
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void findViews() {
        priceTextView = (TextView) contentView
                .findViewById(R.id.qq_charge_price);
        accountEditText = (EditText) contentView
                .findViewById(R.id.qq_charge_account);
        amountEditText = (EditText) contentView
                .findViewById(R.id.qq_charge_amount);
        chargeButton = (Button) contentView.findViewById(R.id.qq_charge_charge);
        initViews();
        registerViews();
    }

    @Override
    protected void registerViews() {
        amountEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getPrice();
            }
        });
        chargeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                charge();
            }
        });
    }

    private void getPrice() {
        if (amountEditText.length() > 0) {
            getChargePrice();
        } else {
            priceTextView.setText("");
        }
    }

    private void charge() {
        if (amountEditText.length() <= 0) {
            ApplicationTool.showToast("请输入充值数量");
        } else if (accountEditText.length() <= 0) {
            ApplicationTool.showToast("请输入要充值的QQ号");
        } else {
            if (chargeInfo.getSellprice() > 0) {
                qqCharge();
            }
        }
    }

    private void getChargePrice(){
        List<RequestParam> requestParams = new ArrayList<>();
        if (amountEditText.length() > 0) {
            requestParams.add(new RequestParam("num", amountEditText.getText().toString().trim()));
        } else {
            return;
        }
        post(API.API_BIANMIN_QQ_INFO, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject infoObject = object.optJSONObject("object");
                            chargeInfo = new ModelChargeInfo(infoObject);
                            if (amountEditText.length() > 0) {
                                if (chargeInfo.getSellprice() > 0) {
                                    priceTextView.setText(Parameters.CONSTANT_RMB
                                            + decimalFormat.format(chargeInfo
                                            .getSellprice()));
                                    return;
                                }
                            }
                        }
                        priceTextView.setText("");
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                priceTextView.setText("");
            }
        });
    }

    private void qqCharge() {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("cardid", chargeInfo
                .getCardid()));
        requestParams.add(new RequestParam("game_userid", accountEditText.getText().toString().trim()));
        requestParams.add(new RequestParam("cardnum", amountEditText.getText().toString().trim()));
        if (User.getUser().isLogin()) {
            requestParams.add(new RequestParam("memberAccount", User.getUser().getUseraccount()));
        }
        post(API.API_BIANMIN_GAME_QQ_CHARGE, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                            JSONObject dataMap = object.optJSONObject("dataMap");
                            ModelBianminOrderResult orderResult = new ModelBianminOrderResult(
                                    dataMap);
                            if (orderResult != null) {
                                if (orderResult.getSellPrice() > 0) {
                                    payMoney(orderResult);
                                    getActivity().finish();
                                    return;
                                }
                            }
                        }
                        ApplicationTool.showToast(object.optString("message"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("充值失败");
            }
        });
    }

}
