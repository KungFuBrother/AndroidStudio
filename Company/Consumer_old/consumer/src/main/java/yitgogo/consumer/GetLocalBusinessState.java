package yitgogo.consumer;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import yitgogo.consumer.store.model.Store;
import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.NetUtil;

/**
 * Created by Tiger on 2015-10-29.
 *
 * @Result {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{"returnNum":0},"object":null}
 */
public class GetLocalBusinessState extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("organizationId", Store.getStore().getStoreId()));
        String result = NetUtil.getInstance().postWithoutCookie(API.API_LOCAL_BUSINESS_STATE, nameValuePairs, false, false);
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                    JSONObject dataMap = object.optJSONObject("dataMap");
                    if (dataMap != null) {
                        return dataMap.optInt("returnNum") != 0;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}