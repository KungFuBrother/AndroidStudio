package yitgogo.consumer.money.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;

public class PayPasswordChangeFragment extends BaseNotifyFragment {

    TextView phoneTextView, getCodeButton;
    EditText smsCodeEditText;
    Button button;
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
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
                new GetSmsCode().execute();
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
            Notify.show("请输入您收到的验证码");
        } else {
            nameValuePairs.add(new BasicNameValuePair("mcode", smsCodeEditText
                    .getText().toString().trim()));
            PayPasswordDialog oldPasswordDialog = new PayPasswordDialog(
                    "请输入旧支付密码", false) {
                public void onDismiss(DialogInterface dialog) {
                    if (!TextUtils.isEmpty(payPassword)) {
                        nameValuePairs.add(new BasicNameValuePair("paypwd",
                                payPassword));
                        PayPasswordDialog newPasswordDialog = new PayPasswordDialog(
                                "请输入新支付密码", false) {
                            public void onDismiss(DialogInterface dialog) {
                                if (!TextUtils.isEmpty(payPassword)) {
                                    nameValuePairs.add(new BasicNameValuePair(
                                            "newpaypwd", payPassword));
                                    new ChangePayPassword().execute();
                                }
                                super.onDismiss(dialog);
                            }

                            ;
                        };
                        newPasswordDialog.show(getFragmentManager(), null);
                    }
                    super.onDismiss(dialog);
                }

                ;
            };
            oldPasswordDialog.show(getFragmentManager(), null);
        }
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/account/modpaypwd
     * @Parameters [mcode=133, paypwd=5854acf38caa01d136aa12e81164937e,
     * newpaypwd=e10adc3949ba59abbe56e057f20f883e]
     * @Put_Cookie JSESSIONID=9CEE0E45972BAC20D9E128CC41A8B074
     * @Result {"state":"success","msg":"操作成功","databody":{"modpwd":"ok"}}
     */
    class ChangePayPassword extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            return netUtil.postWithCookie(API.MONEY_PAY_PASSWORD_MODIFY,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("success")) {
                        JSONObject databody = object.optJSONObject("databody");
                        if (databody != null) {
                            if (databody.optString("modpwd").equalsIgnoreCase(
                                    "ok")) {
                                Notify.show("修改支付密码成功");
                                getActivity().finish();
                                return;
                            }
                        }
                    }
                    Notify.show(object.optString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/account/sendsms
     * @Parameters []
     * @Put_Cookie JSESSIONID=9CEE0E45972BAC20D9E128CC41A8B074
     * @Result {"state":"success","msg":"操作成功","databody":{"mobile":"9558"
     * ,"send":"ok"}}
     */
    class GetSmsCode extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(Void... params) {
            return netUtil.postWithCookie(API.MONEY_SMS_CODE, null);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
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
                                Notify.show("已将验证码发送至尾号为 "
                                        + databody.optString("mobile") + " 的手机");
                                return;
                            }
                        }
                    }
                    Notify.show(object.optString("msg"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
