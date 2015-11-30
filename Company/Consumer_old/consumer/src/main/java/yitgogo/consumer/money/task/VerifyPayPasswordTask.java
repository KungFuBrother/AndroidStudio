package yitgogo.consumer.money.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.user.model.User;

/**
 * @author Tiger
 * @Url http://192.168.8.2:8030/api/member/account/validatepaypwd
 * @Parameters [sn=3473404ae106951e8e3e9244b5f3d80d, payaccount=15081818130001,
 * paypwd=8e0b70065cf4ed0016d3e68c6ab7ea19]
 * @Put_Cookie JSESSIONID=D2AF3DDA2FE8EDEB9F4308E0DD126B38
 * @Result {"state":"success","msg":"操作成功","databody":{"vli":true}}
 */
public class VerifyPayPasswordTask extends AsyncTask<String, Void, String> {

    public boolean passwordIsRight = false;

    @Override
    protected String doInBackground(String... params) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("sn", User.getUser()
                .getCacheKey()));
        nameValuePairs.add(new BasicNameValuePair("payaccount", MoneyAccount
                .getMoneyAccount().getPayaccount()));
        nameValuePairs.add(new BasicNameValuePair("paypwd", params[0]));
        return NetUtil.getInstance().postWithCookie(
                API.MONEY_PAY_PASSWORD_VALIDATE, nameValuePairs);
    }

    @Override
    protected void onPostExecute(String result) {
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("success")) {
                    JSONObject jsonObject = object.optJSONObject("databody");
                    if (jsonObject != null) {
                        passwordIsRight = jsonObject.optBoolean("vli");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
