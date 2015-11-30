package yitgogo.consumer.money.task;

import android.os.AsyncTask;

import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;

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

public class HavePayPasswordTask extends AsyncTask<Void, Void, String> {

    @Override
    protected String doInBackground(Void... params) {
        return NetUtil.getInstance().postWithCookie(
                API.MONEY_PAY_PASSWORD_STATE, null);
    }

}
