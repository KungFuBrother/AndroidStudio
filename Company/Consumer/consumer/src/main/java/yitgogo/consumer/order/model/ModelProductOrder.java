package yitgogo.consumer.order.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class ModelProductOrder {
    String id = "", productNumber = "", productName = "", attName = "",
            productUnit = "", img = "", providerId = "";
    int productQuantity = 0;
    double unitSellPrice = 0;
    JSONObject jsonObject = new JSONObject();

    public ModelProductOrder(JSONObject object) throws JSONException {
        if (object != null) {

            this.jsonObject = object;
            if (object.has("id")) {
                if (!object.getString("id").equalsIgnoreCase("null")) {
                    id = object.optString("id");
                }
            }
            if (object.has("productNumber")) {
                if (!object.getString("productNumber").equalsIgnoreCase("null")) {
                    productNumber = object.optString("productNumber");
                }
            }
            if (object.has("productName")) {
                if (!object.getString("productName").equalsIgnoreCase("null")) {
                    productName = object.optString("productName");
                }
            }
            if (object.has("attName")) {
                if (!object.getString("attName").equalsIgnoreCase("null")) {
                    attName = object.optString("attName");
                }
            }
            if (object.has("productUnit")) {
                if (!object.getString("productUnit").equalsIgnoreCase("null")) {
                    productUnit = object.optString("productUnit");
                }
            }
            if (object.has("img")) {
                if (!object.getString("img").equalsIgnoreCase("null")) {
                    img = object.optString("img");
                }
            }
            if (object.has("providerId")) {
                if (!object.getString("providerId").equalsIgnoreCase("null")) {
                    providerId = object.optString("providerId");
                }
            }
            if (object.has("productQuantity")) {
                if (!object.getString("productQuantity").equalsIgnoreCase("null")) {
                    productQuantity = object.optInt("productQuantity");
                }
            }
            if (object.has("unitSellPrice")) {
                if (!object.getString("unitSellPrice").equalsIgnoreCase("null")) {
                    unitSellPrice = object.optDouble("unitSellPrice");
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public String getProductName() {
        return productName;
    }

    public String getAttName() {
        return attName;
    }

    public String getProductUnit() {
        return productUnit;
    }

    public String getImg() {
        return img;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public double getUnitSellPrice() {
        return unitSellPrice;
    }

    public String getProviderId() {
        return providerId;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
