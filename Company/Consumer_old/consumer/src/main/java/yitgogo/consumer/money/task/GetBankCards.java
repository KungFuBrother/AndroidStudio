package yitgogo.consumer.money.task;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.money.model.ModelBankCard;
import yitgogo.consumer.money.model.MoneyAccount;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.user.model.User;
import yitgogo.consumer.view.Notify;

/**
 * @author Tiger
 * @Url http://192.168.8.2:8030/member/bank/listbindbankcard
 * @Parameters [sn=9b8b9af4f22fe0e87f8cdccecd07d819, memberid=13032889558]
 * @Put_Cookie JSESSIONID=A1594836FB21E3875CB2704731502406
 * @Result {"state":"success","msg":"操作成功","databody":[]}
 * @Result {"state":"success","msg":"操作成功","databody":[{"bandnameadds":"南充支行"
 * ,"bank":{"code":"PSBC","icon":"17","id":17,"name":"中国邮政储蓄"},
 * "banknumber" :"6210986731007566422","cardType":"储蓄卡","cradname":"雷小武"
 * ,"id":5,"idCard" :"513030199311056012","mobile":"13032889558","org"
 * :"13032889558","validation":false}]}
 */

public class GetBankCards extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("sn", User.getUser()
                .getCacheKey()));
        nameValuePairs.add(new BasicNameValuePair("memberid", User.getUser()
                .getUseraccount()));
        return NetUtil.getInstance().postWithCookie(API.MONEY_BANK_BINDED,
                nameValuePairs);
    }

    @Override
    protected void onPostExecute(String result) {
        MoneyAccount.getMoneyAccount().getBankCards().clear();
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("success")) {
                    JSONArray array = object.optJSONArray("databody");
                    if (array != null) {
                        List<ModelBankCard> bankCards = new ArrayList<ModelBankCard>();
                        for (int i = 0; i < array.length(); i++) {
                            bankCards.add(new ModelBankCard(array
                                    .optJSONObject(i)));
                        }
                        MoneyAccount.getMoneyAccount().setGetBankCardFailed(
                                false);
                        MoneyAccount.getMoneyAccount().setBankCards(bankCards);
                        return;
                    }
                }
                MoneyAccount.getMoneyAccount().setGetBankCardFailed(true);
                Notify.show(object.optString("msg"));
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        MoneyAccount.getMoneyAccount().setGetBankCardFailed(true);
        Notify.show("获取绑定的银行卡信息失败！");
        return;
    }
}