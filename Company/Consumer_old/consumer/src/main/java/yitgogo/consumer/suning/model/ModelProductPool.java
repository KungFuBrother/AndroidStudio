package yitgogo.consumer.suning.model;

import org.json.JSONObject;

/**
 * Created by Tiger on 2015-10-19.
 */
public class ModelProductPool {

    String commonPartnum = "", sku = "";
    JSONObject jsonObject = new JSONObject();

    public ModelProductPool() {
    }

    public ModelProductPool(JSONObject object) {
        if (object != null) {
            jsonObject = object;
            if (object.has("commonPartnum")) {
                if (!object.optString("commonPartnum").equalsIgnoreCase("null")) {
                    commonPartnum = object.optString("commonPartnum");
                }
            }
            if (object.has("sku")) {
                if (!object.optString("sku").equalsIgnoreCase("null")) {
                    sku = object.optString("sku");
                }
            }
        }
    }

    public String getCommonPartnum() {
        return commonPartnum;
    }

    public String getSku() {
        return sku;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
