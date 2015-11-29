package yitgogo.consumer.money.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNetworkFragment;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;

public class PayPasswordSetFragment extends BaseNetworkFragment {

    EditText idCardEditText, nameEditText;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_password_set);
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initViews() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PayPasswordSetFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PayPasswordSetFragment.class.getName());
    }

    @Override
    protected void findViews() {
        idCardEditText = (EditText) contentView
                .findViewById(R.id.set_password_idcard);
        nameEditText = (EditText) contentView
                .findViewById(R.id.set_password_name);
        button = (Button) contentView.findViewById(R.id.set_password_ok);
        initViews();
        registerViews();
    }

    @Override
    protected void registerViews() {
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setPassword();
            }
        });
    }

    private void setPassword() {
        if (TextUtils.isEmpty(idCardEditText.getText().toString().trim())) {
            ApplicationTool.showToast("请输入您的身份证号");
        } else if (TextUtils.isEmpty(nameEditText.getText().toString().trim())) {
            ApplicationTool.showToast("请输入您的真实姓名");
        } else {
            PayPasswordDialog passwordDialog = new PayPasswordDialog("设置支付密码",
                    true) {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!TextUtils.isEmpty(payPassword)) {
                        setPayPassword(payPassword);
                    }
                    super.onDismiss(dialog);
                }
            };
            passwordDialog.show(getFragmentManager(), null);
        }
    }

    private void setPayPassword(String paypwd) {
        List<RequestParam> requestParams = new ArrayList<>();
        requestParams.add(new RequestParam("idcard", idCardEditText.getText().toString().trim()));
        requestParams.add(new RequestParam("realname", nameEditText.getText().toString().trim()));
        requestParams.add(new RequestParam("paypwd", paypwd));
        requestParams.add(new RequestParam("payaccount", MoneyAccount.getMoneyAccount().getPayaccount()));
        requestParams.add(new RequestParam("seckey", MoneyAccount.getMoneyAccount().getSeckey()));
        post(API.MONEY_PAY_PASSWORD_SET, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject jsonObject = object
                                    .optJSONObject("databody");
                            if (jsonObject != null) {
                                if (jsonObject.optString("setpwd")
                                        .equalsIgnoreCase("ok")) {
                                    ApplicationTool.showToast("设置支付密码成功");
                                    getActivity().finish();
                                    return;
                                }
                            }
                        }
                        ApplicationTool.showToast(object.optString("msg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
