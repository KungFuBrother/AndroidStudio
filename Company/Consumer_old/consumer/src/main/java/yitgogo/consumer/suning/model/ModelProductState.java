package yitgogo.consumer.suning.model;

import org.json.JSONObject;

/**
 * Created by Tiger on 2015-10-19.
 */
public class ModelProductState {

    String skuId = "";
    int listState = 0;
    JSONObject jsonObject=new JSONObject();

    public ModelProductState() {
    }

    public ModelProductState(JSONObject object) {
        if (object != null) {
            jsonObject=object;
            if (object.has("skuId")) {
                if (!object.optString("skuId").equalsIgnoreCase("null")) {
                    skuId = object.optString("skuId");
                }
            }
            if (object.has("listState")) {
                if (!object.optString("listState").equalsIgnoreCase("null")) {
                    listState = object.optInt("listState");
                }
            }
        }
    }

    public String getSkuId() {
        return skuId;
    }

    public int getListState() {
        return listState;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
