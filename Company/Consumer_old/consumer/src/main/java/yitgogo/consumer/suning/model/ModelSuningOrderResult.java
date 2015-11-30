package yitgogo.consumer.suning.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Result: {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{"fuwuZuoji":"028-66133688","orderType":"新订单","orderNumber":"SN464351824310","zongjine":"30.0","freight":"5","productInfo":[{"num":1"Amount":30.0,"price":30.0,"spname":"NEW17"}],"fuwushang":"成都成华区罗飞加盟商","shijian":"2015-10-28 09:46:43","fuwuPhone":"13880353588"},"object":null}
 */
public class ModelSuningOrderResult {

    String fuwuZuoji = "", orderType = "", orderNumber = "", fuwushang = "", shijian = "", fuwuPhone = "";
    double zongjine = 0,freight=0;
    List<ModelSuningOrderResultProduct> products = new ArrayList<>();

    public ModelSuningOrderResult(JSONObject object) {
        if (object != null) {
            if (object.has("fuwuZuoji")) {
                if (!object.optString("fuwuZuoji").equalsIgnoreCase("null")) {
                    fuwuZuoji = object.optString("fuwuZuoji");
                }
            }
            if (object.has("orderType")) {
                if (!object.optString("orderType").equalsIgnoreCase("null")) {
                    orderType = object.optString("orderType");
                }
            }
            if (object.has("orderNumber")) {
                if (!object.optString("orderNumber").equalsIgnoreCase("null")) {
                    orderNumber = object.optString("orderNumber");
                }
            }
            if (object.has("fuwushang")) {
                if (!object.optString("fuwushang").equalsIgnoreCase("null")) {
                    fuwushang = object.optString("fuwushang");
                }
            }
            if (object.has("shijian")) {
                if (!object.optString("shijian").equalsIgnoreCase("null")) {
                    shijian = object.optString("shijian");
                }
            }
            if (object.has("fuwuPhone")) {
                if (!object.optString("fuwuPhone").equalsIgnoreCase("null")) {
                    fuwuPhone = object.optString("fuwuPhone");
                }
            }
            if (object.has("zongjine")) {
                if (!object.optString("zongjine").equalsIgnoreCase("null")) {
                    zongjine = object.optDouble("zongjine");
                }
            }
            if (object.has("freight")) {
                if (!object.optString("freight").equalsIgnoreCase("null")) {
                    freight = object.optDouble("freight");
                }
            }
            JSONArray productArray = object.optJSONArray("productInfo");
            if (productArray != null) {
                for (int i = 0; i < productArray.length(); i++) {
                    products.add(new ModelSuningOrderResultProduct(productArray.optJSONObject(i)));
                }
            }
        }
    }

    public String getFuwuZuoji() {
        return fuwuZuoji;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getFuwushang() {
        return fuwushang;
    }

    public String getShijian() {
        return shijian;
    }

    public String getFuwuPhone() {
        return fuwuPhone;
    }

    public double getZongjine() {
        return zongjine;
    }

    public double getFreight() {
        return freight;
    }

    public List<ModelSuningOrderResultProduct> getProducts() {
        return products;
    }
}

