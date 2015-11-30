package yitgogo.consumer.suning.model;

import org.json.JSONObject;

/**
 * Created by Tiger on 2015-10-19.
 */
public class ModelProductPrice {

    String skuId = "";
    double price = -1;
    JSONObject jsonObject=new JSONObject();

    public ModelProductPrice() {
    }

    public ModelProductPrice(JSONObject object) {
        if (object != null) {
            jsonObject=object;
            if (object.has("skuId")) {
                if (!object.optString("skuId").equalsIgnoreCase("null")) {
                    skuId = object.optString("skuId");
                }
            }
            if (object.has("price")) {
                if (!object.optString("price").equalsIgnoreCase("null")) {
                    price = object.optDouble("price");
                }
            }
        }
    }

    public String getSkuId() {
        return skuId;
    }

    public double getPrice() {
        return price;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
