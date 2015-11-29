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
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.ApplicationTool;
import yitgogo.consumer.tools.RequestParam;
import yitgogo.consumer.user.model.User;

public class PayPasswordChangeFragment extends BaseNetworkFragment {

    TextView phoneTextView, getCodeButton;
    EditText smsCodeEditText;
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
        setContentView(R.layout.fragment_money_password_change);
        findViews();
    }

    @Override
    protected void init() {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(PayPasswordChangeFragment.class.getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(PayPasswordChangeFragment.class.getName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFinish = true;
    }

    @Override
    protected void findViews() {
        phoneTextView = (TextView) contentView
                .findViewById(R.id.change_pay_password_phone);
        getCodeButton = (TextView) contentView
                .findViewById(R.id.change_pay_password_smscode_get);
        smsCodeEditText = (EditText) contentView
                .findViewById(R.id.change_pay_password_smscode);
        button = (Button) contentView.findViewById(R.id.change_pay_password_ok);
        initViews();
        registerViews();
    }

    @Override
    protected void initViews() {
        phoneTextView.setText(User.getUser().getPhone());
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
                changePassword();
            }
        });
    }

    private void changePassword() {
        if (TextUtils.isEmpty(smsCodeEditText.getText().toString().trim())) {
            ApplicationTool.showToast("请输入您收到的验证码");
        } else {
            requestParams.add(new RequestParam("mcode", smsCodeEditText.getText().toString().trim()));
            PayPasswordDialog oldPasswordDialog = new PayPasswordDialog(
                    "请输入旧支付密码", false) {
                public void onDismiss(DialogInterface dialog) {
                    if (!TextUtils.isEmpty(payPassword)) {
                        requestParams.add(new RequestParam("paypwd", payPassword));
                        PayPasswordDialog newPasswordDialog = new PayPasswordDialog("请输入新支付密码", false) {
                            public void onDismiss(DialogInterface dialog) {
                                if (!TextUtils.isEmpty(payPassword)) {
                                    requestParams.add(new RequestParam("newpaypwd", payPassword));
                                    changePayPassword();
                                }
                                super.onDismiss(dialog);
                            }
                        };
                        newPasswordDialog.show(getFragmentManager(), null);
                    }
                    super.onDismiss(dialog);
                }
            };
            oldPasswordDialog.show(getFragmentManager(), null);
        }
    }

    private void changePayPassword() {
        post(API.MONEY_PAY_PASSWORD_MODIFY, requestParams, new OnNetworkListener() {
            @Override
            public void onSuccess(String result) {
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject object = new JSONObject(result);
                        if (object.optString("state").equalsIgnoreCase("success")) {
                            JSONObject databody = object.optJSONObject("databody");
                            if (databody != null) {
                                if (databody.optString("modpwd").equalsIgnoreCase("ok")) {
                                    ApplicationTool.showToast("修改支付密码成功");
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
                                    ApplicationTool.showToast("已将验证码发送至尾号为 " + databody.optString("mobile") + " 的手机");
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
