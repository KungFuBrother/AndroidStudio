package yitgogo.consumer.home.model;

import org.json.JSONObject;

/**
 * {"id":198731,"price":290.0,"originalPrice":300.0}
 */
public class ModelKillPrice {

    String id = "";
    double price = 0, originalPrice = 0;

    public ModelKillPrice(JSONObject object) {
        if (object != null) {
            if (object.has("id")) {
                if (!object.optString("id").equalsIgnoreCase("null")) {
                    id = object.optString("id");
                }
            }
            if (object.has("price")) {
                if (!object.optString("price").equalsIgnoreCase("null")) {
                    price = object.optDouble("price");
                }
            }
            if (object.has("originalPrice")) {
                if (!object.optString("originalPrice").equalsIgnoreCase("null")) {
                    originalPrice = object.optDouble("originalPrice");
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }
}
