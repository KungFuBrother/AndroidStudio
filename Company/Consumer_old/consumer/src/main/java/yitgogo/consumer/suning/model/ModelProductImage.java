package yitgogo.consumer.suning.model;

import org.json.JSONObject;

/**
 * Created by Tiger on 2015-10-19.
 */
public class ModelProductImage {

    String path = "";
    int primary = 0;

    public ModelProductImage() {
    }

    public ModelProductImage(JSONObject object) {
        if (object != null) {
            if (object.has("path")) {
                if (!object.optString("path").equalsIgnoreCase("null")) {
                    path = object.optString("path");
                }
            }
            if (object.has("primary")) {
                if (!object.optString("primary").equalsIgnoreCase("null")) {
                    primary = object.optInt("primary");
                }
            }
        }
    }

    public String getPath() {
        return path;
    }

    public int getPrimary() {
        return primary;
    }
}
