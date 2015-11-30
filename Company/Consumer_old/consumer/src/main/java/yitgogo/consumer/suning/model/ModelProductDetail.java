package yitgogo.consumer.suning.model;

import org.json.JSONObject;

/**
 * Created by Tiger on 2015-10-19.
 */
public class ModelProductDetail {

    int state = 0;
    String sku = "", image = "", brand = "", name = "", model = "", weight = "", productArea = "", upc = "", saleUnit = "", category = "", introduction = "", externalEanCode = "", param = "", service = "";
    JSONObject jsonObject = new JSONObject();

    public ModelProductDetail() {
    }

    public ModelProductDetail(JSONObject object) {
        if (object != null) {
            jsonObject = object;
            if (object.has("sku")) {
                if (!object.optString("sku").equalsIgnoreCase("null")) {
                    sku = object.optString("sku");
                }
            }
            if (object.has("image")) {
                if (!object.optString("image").equalsIgnoreCase("null")) {
                    image = object.optString("image");
                }
            }
            if (object.has("brand")) {
                if (!object.optString("brand").equalsIgnoreCase("null")) {
                    brand = object.optString("brand");
                }
            }
            if (object.has("name")) {
                if (!object.optString("name").equalsIgnoreCase("null")) {
                    name = object.optString("name");
                }
            }
            if (object.has("model")) {
                if (!object.optString("model").equalsIgnoreCase("null")) {
                    model = object.optString("model");
                }
            }
            if (object.has("weight")) {
                if (!object.optString("weight").equalsIgnoreCase("null")) {
                    weight = object.optString("weight");
                }
            }
            if (object.has("state")) {
                if (!object.optString("state").equalsIgnoreCase("null")) {
                    state = object.optInt("state");
                }
            }
            if (object.has("productArea")) {
                if (!object.optString("productArea").equalsIgnoreCase("null")) {
                    productArea = object.optString("productArea");
                }
            }
            if (object.has("upc")) {
                if (!object.optString("upc").equalsIgnoreCase("null")) {
                    upc = object.optString("upc");
                }
            }
            if (object.has("saleUnit")) {
                if (!object.optString("saleUnit").equalsIgnoreCase("null")) {
                    saleUnit = object.optString("saleUnit");
                }
            }
            if (object.has("category")) {
                if (!object.optString("category").equalsIgnoreCase("null")) {
                    category = object.optString("category");
                }
            }
            if (object.has("introduction")) {
                if (!object.optString("introduction").equalsIgnoreCase("null")) {
                    introduction = object.optString("introduction");
                }
            }
            if (object.has("externalEanCode")) {
                if (!object.optString("externalEanCode").equalsIgnoreCase("null")) {
                    externalEanCode = object.optString("externalEanCode");
                }
            }
            if (object.has("param")) {
                if (!object.optString("param").equalsIgnoreCase("null")) {
                    param = object.optString("param");
                }
            }
            if (object.has("service")) {
                if (!object.optString("service").equalsIgnoreCase("null")) {
                    service = object.optString("service");
                }
            }
        }
    }


    public String getSku() {
        return sku;
    }

    public String getImage() {
        return image;
    }

    public String getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public String getWeight() {
        return weight;
    }

    public int getState() {
        return state;
    }

    public String getProductArea() {
        return productArea;
    }

    public String getUpc() {
        return upc;
    }

    public String getSaleUnit() {
        return saleUnit;
    }

    public String getCategory() {
        return category;
    }

    public String getIntroduction() {
        return introduction;
    }

    public String getExternalEanCode() {
        return externalEanCode;
    }

    public String getParam() {
        return param;
    }

    public String getService() {
        return service;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
