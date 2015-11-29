package yitgogo.smart.home.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 广告
 *
 * @author Tiger
 * @Json { "id": 51, "adverName": "ecess", "area": null, "onTime":
 * 1438358400000, "endTime": 1440950400000, "adverPosition": "头部广告",
 * "adverSize": "", "adverType": "图片广告", "operator": "平台管理员", "addTime":
 * "2015-08-23 15:48:48", "defaultadver":
 * "http://images.yitos.net/images/public/20150823/64101440316108001.png"
 * , "adverUrl": "196", "sbtype": "3", "servp": null, "state": "启用",
 * "imgSet": [ { "id": 1546, "adverImg":
 * "http://images.yitos.net/images/public/20150823/6821440316098677.png" ,
 * "adverUrl": "196" } ] }
 */
public class ModelAds {

    long onTime = 0, endTime = 0;
    private String id = "", adverName = "", area = "", adverPosition = "",
            adverSize = "", adverType = "", operator = "", addTime = "",
            defaultadver = "", adverUrl = "", sbtype = "", servp = "",
            state = "";
    List<ModelAdsImage> adsImages = new ArrayList<>();

    public ModelAds() {
    }

    public ModelAds(JSONObject object) throws JSONException {
        if (object != null) {
            if (object.has("id")) {
                if (!object.optString("id").equalsIgnoreCase("null")) {
                    id = object.optString("id");
                }
            }
            if (object.has("adverName")) {
                if (!object.optString("adverName").equalsIgnoreCase("null")) {
                    adverName = object.optString("adverName");
                }
            }
            if (object.has("area")) {
                if (!object.optString("area").equalsIgnoreCase("null")) {
                    area = object.optString("area");
                }
            }
            if (object.has("adverPosition")) {
                if (!object.optString("adverPosition").equalsIgnoreCase("null")) {
                    adverPosition = object.optString("adverPosition");
                }
            }
            if (object.has("adverSize")) {
                if (!object.optString("adverSize").equalsIgnoreCase("null")) {
                    adverSize = object.optString("adverSize");
                }
            }
            if (object.has("adverType")) {
                if (!object.optString("adverType").equalsIgnoreCase("null")) {
                    adverType = object.optString("adverType");
                }
            }
            if (object.has("operator")) {
                if (!object.optString("operator").equalsIgnoreCase("null")) {
                    operator = object.optString("operator");
                }
            }
            if (object.has("addTime")) {
                if (!object.optString("addTime").equalsIgnoreCase("null")) {
                    addTime = object.optString("addTime");
                }
            }
            if (object.has("defaultadver")) {
                if (!object.optString("defaultadver").equalsIgnoreCase("null")) {
                    defaultadver = object.optString("defaultadver");
                }
            }
            if (object.has("adverUrl")) {
                if (!object.optString("adverUrl").equalsIgnoreCase("null")) {
                    adverUrl = object.optString("adverUrl");
                }
            }
            if (object.has("sbtype")) {
                if (!object.optString("sbtype").equalsIgnoreCase("null")) {
                    sbtype = object.optString("sbtype");
                }
            }
            if (object.has("servp")) {
                if (!object.optString("servp").equalsIgnoreCase("null")) {
                    servp = object.optString("servp");
                }
            }
            if (object.has("state")) {
                if (!object.optString("state").equalsIgnoreCase("null")) {
                    state = object.optString("state");
                }
            }
            onTime = object.optLong("onTime");
            endTime = object.optLong("endTime");
            JSONArray imgSet = object.optJSONArray("imgSet");
            if (imgSet != null) {
                for (int i = 0; i < imgSet.length(); i++) {
                    adsImages.add(new ModelAdsImage(imgSet.optJSONObject(i)));
                }
            }
        }
    }

    public long getOnTime() {
        return onTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getId() {
        return id;
    }

    public String getAdverName() {
        return adverName;
    }

    public String getArea() {
        return area;
    }

    public String getAdverPosition() {
        return adverPosition;
    }

    public String getAdverSize() {
        return adverSize;
    }

    public String getAdverType() {
        return adverType;
    }

    public String getOperator() {
        return operator;
    }

    public String getAddTime() {
        return addTime;
    }

    public String getDefaultadver() {
        return defaultadver;
    }

    public String getAdverUrl() {
        return adverUrl;
    }

    public String getSbtype() {
        return sbtype;
    }

    public String getServp() {
        return servp;
    }

    public String getState() {
        return state;
    }

    public List<ModelAdsImage> getAdsImages() {
        return adsImages;
    }

}
