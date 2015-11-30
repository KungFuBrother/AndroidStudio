package yitgogo.consumer.money.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.smartown.yitian.gogo.R;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.BaseNotifyFragment;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.view.Notify;

public class PayPasswordSetFragment extends BaseNotifyFragment {

    EditText idCardEditText, nameEditText;
    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_money_password_set);
        findViews();
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
            Notify.show("请输入您的身份证号");
        } else if (TextUtils.isEmpty(nameEditText.getText().toString().trim())) {
            Notify.show("请输入您的真实姓名");
        } else {
            PayPasswordDialog passwordDialog = new PayPasswordDialog("设置支付密码",
                    true) {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (!TextUtils.isEmpty(payPassword)) {
                        new SetPayPassword().execute(payPassword);
                    }
                    super.onDismiss(dialog);
                }
            };
            passwordDialog.show(getFragmentManager(), null);
        }
    }

    /**
     * 设置支付密码
     *
     * @author Tiger
     * @Url http://192.168.8.2:8030/member/account/setpaypwd
     * @Parameters [idcard=513030199311056012, realname=雷小武,
     * paypwd=5854acf38caa01d136aa12e81164937e,
     * payaccount=15081711040001,
     * seckey=ad7c836d487d719c621cd4ce2b5d4b16]
     * @Put_Cookie JSESSIONID=48EC063AA0847C06A6D20F60D1DE8BC4
     * @Result {"state":"success","msg":"操作成功","databody":{"setpwd":"ok"}}
     * @Result {"state":"berror","msg":"不能重复设定支付密码!","databody":{}}
     */
    class SetPayPassword extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            showLoading();
        }

        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("idcard", idCardEditText
                    .getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("realname", nameEditText
                    .getText().toString().trim()));
            nameValuePairs.add(new BasicNameValuePair("paypwd", params[0]));
            nameValuePairs.add(new BasicNameValuePair("payaccount",
                    MoneyAccount.getMoneyAccount().getPayaccount()));
            nameValuePairs.add(new BasicNameValuePair("seckey", MoneyAccount
                    .getMoneyAccount().getSeckey()));
            return netUtil.postWithCookie(API.MONEY_PAY_PASSWORD_SET,
                    nameValuePairs);
        }

        @Override
        protected void onPostExecute(String result) {
            hideLoading();
            if (!TextUtils.isEmpty(result)) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optString("state").equalsIgnoreCase("success")) {
                        JSONObject jsonObject = object
                                .optJSONObject("databody");
                        if (jsonObject != null) {
                            if (jsonObject.optString("setpwd")
                                    .equalsIgnoreCase("ok")) {
                                Notify.show("设置支付密码成功");
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

}
