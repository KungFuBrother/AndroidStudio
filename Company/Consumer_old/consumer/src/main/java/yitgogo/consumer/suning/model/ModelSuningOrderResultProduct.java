package yitgogo.consumer.suning.model;

import org.json.JSONObject;

public class ModelSuningOrderResultProduct {

    int num = 0;
    double Amount = 0, price = 0;
    String spname = "";

    public ModelSuningOrderResultProduct() {
    }

    public ModelSuningOrderResultProduct(JSONObject object) {
        if (object != null) {
            if (object.has("spname")) {
                if (!object.optString("spname").equalsIgnoreCase("null")) {
                    spname = object.optString("spname");
                }
            }
            if (object.has("num")) {
                if (!object.optString("num").equalsIgnoreCase("null")) {
                    num = object.optInt("num");
                }
            }
            if (object.has("Amount")) {
                if (!object.optString("Amount").equalsIgnoreCase("null")) {
                    Amount = object.optDouble("Amount");
                }
            }
            if (object.has("price")) {
                if (!object.optString("price").equalsIgnoreCase("null")) {
                    price = object.optDouble("price");
                }
            }
        }
    }
}