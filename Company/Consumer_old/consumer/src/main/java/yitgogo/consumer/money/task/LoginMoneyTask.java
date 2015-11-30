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
import yitgogo.consumer.view.Notify;

/**
 * 登录钱袋子
 *
 * @author Tiger
 * @Url http://192.168.8.2:8030/api/member/login
 * @Parameters [sn=b1a48a96fbf9e1f6b849fa221cefca18]
 * @Save_Cookie JSESSIONID=10EADAE42BE2AD954EEAE02CB1CC2176
 * @Result {"state":"success","msg":"操作成功","databody"
 * :{"balance":"0","payaccount" :"15081617050001", "seckey":
 * "a61ede39d7ae871588b5e7aae4431762" }}
 */
public class LoginMoneyTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("sn", User.getUser()
                .getCacheKey()));
        return NetUtil.getInstance().postAndSaveCookie(API.MONEY_LOGIN,
                nameValuePairs);
    }

    @Override
    protected void onPostExecute(String result) {
        MoneyAccount.init(null);
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("success")) {
                    MoneyAccount.init(object.optJSONObject("databody"));
                    return;
                }
                Notify.show(object.optString("msg"));
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Notify.show("暂无法进入钱袋子，请稍候再试");
        return;
    }
}