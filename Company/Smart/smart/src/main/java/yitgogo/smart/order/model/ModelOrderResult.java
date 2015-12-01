package yitgogo.smart.order.model;

import org.json.JSONObject;

/**
 * @author Tiger
 * @JsonObject {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[ {
 * "zhekouhou"
 * :"486.0","zongzhekou":"0.0","fuwuZuoji":"028-2356895623"
 * ,"zongjine":"486.0","productInfo":
 * "[{\"spname\":\"玖尼 2015年夏新款修身印花无袖圆领连体裤 XY0182\",\"price\":\"194.0\",\"Amount\":\"194.0\",\"num\":\"1\"},{\"spname\":\"玖尼 2015年夏新款荷叶袖圆领雪纺衫短袖套头上衣  XY0172\",\"price\":\"115.0\",\"Amount\":\"115.0\",\"num\":\"1\"},{\"spname\":\"2015新款短袖修身OL职业连衣裙L0594\",\"price\":\"177.0\",\"Amount\":\"177.0\",\"num\":\"1\"}]"
 * ,"ordernumber":"YT9577743800","fuwushang":"测试运营中心一","shijian" :
 * "2015-07-24"
 * ,"fuwuPhone":"15878978945"}],"totalCount":1,"dataMap":
 * {},"object":null}
 * @Description 下单成功返回订单信息
 */
public class ModelOrderResult {

    String fuwuZuoji = "", productInfo = "", ordernumber = "", fuwushang = "",
            shijian = "", fuwuPhone = "";
    double zhekouhou = 0, zongzhekou = 0, zongjine = 0, freight = 0;

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
            if (object.has("productInfo")) {
                if (!object.optString("productInfo").equalsIgnoreCase("null")) {
                    productInfo = object.optString("productInfo");
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

    public String getProductInfo() {
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

    public double getFreight() {
        return freight;
    }

    public double getZhekouhou() {
        return zhekouhou;
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
