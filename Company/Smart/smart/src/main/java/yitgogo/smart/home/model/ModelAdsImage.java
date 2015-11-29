package yitgogo.smart.home.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Tiger
 * @Json { "id": 1546, "adverImg":
 * "http://images.yitos.net/images/public/20150823/6821440316098677.png"
 * , "adverUrl": "196" }
 */
public class ModelAdsImage {
    String id = "", adverImg = "", adverUrl = "";

    public ModelAdsImage(String adverImg) {
        this.adverImg = adverImg;
    }

    public ModelAdsImage(JSONObject object) throws JSONException {
        if (object != null) {
            if (object.has("id")) {
                if (!object.optString("id").equalsIgnoreCase("null")) {
                    id = object.optString("id");
                }
            }
            if (object.has("adverImg")) {
                if (!object.optString("adverImg").equalsIgnoreCase("null")) {
                    adverImg = object.optString("adverImg");
                }
            }
            if (object.has("adverUrl")) {
                if (!object.optString("adverUrl").equalsIgnoreCase("null")) {
                    adverUrl = object.optString("adverUrl");
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getAdverImg() {
        return adverImg;
    }

    public String getAdverUrl() {
        return adverUrl;
    }

}