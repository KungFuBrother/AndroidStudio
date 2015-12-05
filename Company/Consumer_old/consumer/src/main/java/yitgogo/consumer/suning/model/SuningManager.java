package yitgogo.consumer.suning.model;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;


/**
 * Created by Tiger on 2015-10-21.
 */
public class SuningManager {

    public static final String appKey = "SCYT";
    public static final String version = "2.0";

    public static ModelSignature getSignature() {
        JSONObject object = new JSONObject();
        try {
            object = new JSONObject(Content.getStringContent(Parameters.CACHE_KEY_SUNING_SIGNATURE, "{}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ModelSignature(object);
    }

    public static ModelSuningAreas getSuningAreas() {
        JSONObject object = new JSONObject();
        try {
            object = new JSONObject(Content.getStringContent(Parameters.CACHE_KEY_SUNING_AREAS, "{}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ModelSuningAreas(object);
    }

    public static boolean isSignatureOutOfDate(String result) {
        if (!TextUtils.isEmpty(result)) {
            try {
                JSONObject object = new JSONObject(result);
                if (!object.optBoolean("isSuccess")) {
                    if (object.optString("returnMsg").equals("令牌校验失败")) {
                        Content.removeContent(Parameters.CACHE_KEY_SUNING_SIGNATURE);
                        return true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
