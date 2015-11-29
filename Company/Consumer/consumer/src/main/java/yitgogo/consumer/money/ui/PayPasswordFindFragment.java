package yitgogo.consumer.money.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;

public class PayPasswordFindFragment extends BaseNetworkFragment {

    TextView getCodeButton;
    EditText idcardEditText, smsCodeEditText;
    Button button;
    List<RequestParam> requestParams = new ArrayList<>();
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.obj != null) {
                getCodeButton.setText(msg.obj + "s");
            } else {
                getCodeButton.setClickable(true);
                getCodeButton.setTextColor(getResources().getColor(
                        R.color.textColorSecond));
                getCodeButton.setText("获取验证码");
            }
        }

        ;
    };
    boolean isFinish = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_password_find);
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
        MobclickAgent.onPageStart(PayPasswordFindFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PayPasswordFindFragment.class.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFinish = true;
    }

    @Override
    protected void findViews() {
        idcardEditText = (EditText) contentView
                .findViewById(R.id.find_pay_password_idcard);
        getCodeButton = (TextView) contentView
                .findViewById(R.id.find_pay_password_smscode_get);
        smsCodeEditText = (EditText) contentView
                .findViewById(R.id.find_pay_password_smscode);
        button = (Button) contentView.findViewById(R.id.find_pay_password_ok);
        registerViews();
    }

    @Override
    protected void registerViews() {
        getCodeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getSmsCode();
            }
        });
        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                findPassword();
            }
        });
    }

    private void findPassword() {
        requestParams.clear();
        if (TextUtils.isEmpty(idcardEditText.getText().toString().trim())) {
            ApplicationTool.showToast("请输入您的身份证号码");
        } else if (TextUtils.isEmpty(smsCodeEditText.getText().toString()
                .trim())) {
            ApplicationTool.showToast("请输入您收到的验证码");
        } else {
            requestParams.add(new RequestParam("seckey", MoneyAccount.getMoneyAccount().getSeckey()));
            requestParams.add(new RequestParam("cardid", idcardEditText.getText().toString().trim()));
            requestParams.add(new RequestParam("mcode", smsCodeEditText.getText().toString().trim()));
            PayPasswordDialog newPasswordDialog = new PayPasswordDialog("请输入新支付密码", false) {
                public void onDismiss(DialogInterface dialog) {
                    if (!TextUtils.isEmpty(payPassword)) {
                        requestParams.add(new RequestParam("newpaypwd", payPassword));
                        findPayPassword();
                    }
                    super.onDismiss(dialog);
                }
            };
            newPasswordDialog.show(getFragmentManager(), null);
        }
    }

    private void findPayPassword() {
        post(API.MONEY_PAY_PASSWORD_FIND, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                requestParams.clear();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject databody = object.optJSONObject("databody");
                            if (databody != null) {
                                if (databody.optString("paypwd").equalsIgnoreCase(
                                        "ok")) {
                                    ApplicationTool.showToast("修改支付密码成功");
                                    getActivity().finish();
                                    return;
                                }
                            }
                            ApplicationTool.showToast("修改支付密码失败");
                            return;
                        }
                        ApplicationTool.showToast(object.optString("msg"));
                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ApplicationTool.showToast("修改支付密码失败");
            }
        });
    }

    private void getSmsCode() {
        post(API.MONEY_SMS_CODE, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject databody = object.optJSONObject("databody");
                            if (databody != null) {
                                if (databody.optString("send").equalsIgnoreCase(
                                        "ok")) {
                                    getCodeButton.setClickable(false);
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
                                    ApplicationTool.showToast("已将验证码发送至尾号为 "
                                            + databody.optString("mobile") + " 的手机");
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
