package yitgogo.consumer.order.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tiger
 * @JsonObject {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],"totalCount":1,"dataMap":{},"object":[{"zhekouhou":"560.0","zongzhekou":"0.0","orderType":"0","fuwuZuoji":"028-12345678","zongjine":"560.0","freight":"0.0","productInfo":"[{\"spname\":\"韩国现代AV-1000专业大功率舞台功放机-网上易田，省心省钱\",\"price\":\"560.0\",\"Amount\":\"560.0\",\"num\":\"1\"}]","ordernumber":"YT5568585072","totalIntegral":"0","fuwushang":"易田测试加盟店四","shijian":"2015-12-01","fuwuPhone":"13228116626"}]}
 * @Description 下单成功返回订单信息
 */
public class ModelOrderResult {

    String fuwuZuoji = "", ordernumber = "", fuwushang = "", shijian = "",
            fuwuPhone = "";
    double zhekouhou = 0, zongzhekou = 0, zongjine = 0, freight = 0;
    List<ModelOrderResultProduct> productInfo = new ArrayList<ModelOrderResultProduct>();

    public ModelOrderResult() {
    }

    public ModelOrderResult(JSONObject object) {
        if (object != null) {
            if (object.has("zhekouhou")) {
                if (!object.optString("zhekouhou").equalsIgnoreCase("null")) {
                    zhekouhou = object.optDouble("zhekouhou");
                }
            }
            if (object.has("zongzhekou")) {
                if (!object.optString("zongzhekou").equalsIgnoreCase("null")) {
                    zongzhekou = object.optDouble("zongzhekou");
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
            if (object.has("fuwuZuoji")) {
                if (!object.optString("fuwuZuoji").equalsIgnoreCase("null")) {
                    fuwuZuoji = object.optString("fuwuZuoji");
                }
            }
            JSONArray array = object.optJSONArray("productInfo");
            if (array != null) {
                for (int i = 0; i < array.length(); i++) {
                    productInfo.add(new ModelOrderResultProduct(array
                            .optJSONObject(i)));
                }
            }
            if (object.has("ordernumber")) {
                if (!object.optString("ordernumber").equalsIgnoreCase("null")) {
                    ordernumber = object.optString("ordernumber");
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
        }
    }

    public String getFuwuZuoji() {
        return fuwuZuoji;
    }

    public List<ModelOrderResultProduct> getProductInfo() {
        return productInfo;
    }

    public String getOrdernumber() {
        return ordernumber;
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

    public double getZhekouhou() {
        return zhekouhou;
    }

    public double getFreight() {
        return freight;
    }

    public double getZongzhekou() {
        return zongzhekou;
    }

    public double getZongjine() {
        return zongjine;
    }

    @Override
    public String toString() {
        return "ModelOrderResult [fuwuZuoji=" + fuwuZuoji + ", productInfo="
                + productInfo + ", ordernumber=" + ordernumber + ", fuwushang="
                + fuwushang + ", shijian=" + shijian + ", fuwuPhone="
                + fuwuPhone + ", zhekouhou=" + zhekouhou + ", zongzhekou="
                + zongzhekou + ", zongjine=" + zongjine + "]";
    }

}
