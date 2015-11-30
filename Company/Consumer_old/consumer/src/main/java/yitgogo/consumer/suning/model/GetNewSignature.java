package yitgogo.consumer.suning.model;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.tools.API;
import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.NetUtil;
import yitgogo.consumer.tools.Parameters;
import yitgogo.consumer.view.Notify;


/**
 * Created by Tiger on 2015-10-22.
 */
public class GetNewSignature extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Void... voids) {
        String result = NetUtil.getInstance().postWithoutCookie(API.API_SUNING_SIGNATURE, null, false, false);
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (object.optString("state").equalsIgnoreCase("SUCCESS")) {
                    JSONObject dataMap = object.optJSONObject("dataMap");
                    Content.saveStringContent(Parameters.CACHE_KEY_SUNING_SIGNATURE, dataMap.toString());
                    return true;
                }
                Notify.show(object.optString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}

