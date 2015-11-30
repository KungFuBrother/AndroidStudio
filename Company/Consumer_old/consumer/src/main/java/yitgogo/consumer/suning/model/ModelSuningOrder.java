package yitgogo.consumer.suning.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiger on 2015-10-19
 *
 * @Json {
 * "customerName": "李红霞",
 * "spName": "广安武胜县舒洪武服务中心",
 * "orderType": "已发货",
 * "paymentType": "支付方式不正确，请检查后重新下单！",
 * "remarks": null,
 * "customerPhone": "18166468550",
 * "product": [
 * {
 * "price": 1198,
 * "no": 1,
 * "name": "小天鹅洗衣机TB65-8168H",
 * "img": [
 * "http://image4.suning.cn/content/catentries/00000000012841/000000000128410606/fullimage/000000000128410606_4.jpg",
 * "http://image1.suning.cn/content/catentries/00000000012841/000000000128410606/fullimage/000000000128410606_2.jpg",
 * "http://image5.suning.cn/content/catentries/00000000012841/000000000128410606/fullimage/000000000128410606_3.jpg",
 * "http://image2.suning.cn/content/catentries/00000000012841/000000000128410606/fullimage/000000000128410606_1.jpg",
 * "http://image3.suning.cn/content/catentries/00000000012841/000000000128410606/fullimage/000000000128410606_5.jpg"
 * ],
 * "attr": "TB65-8168H",
 * "number": "128410606"
 * }
 * ],
 * "suNingOrderNumber": "6021394830",
 * "address": "万隆场镇",
 * "orderNumber": "SN241374564552",
 * "freight": 0,
 * "account": 1198,
 * "posName": "广安武胜县舒洪武服务中心",
 * "spPhone": "13541889621",
 * "spArea": "中国",
 * "sellTime": "2015-11-14 11:54:14"
 * }
 */
public class ModelSuningOrder {


    String customerName = "", customerPhone = "", spName = "", orderType = "", paymentType = "", remarks = "", address = "", orderNumber = "", posName = "", spPhone = "", spArea = "", sellTime = "",suNingOrderNumber="";
    double freight = 0, account = 0;
    List<ModelSuningOrderProduct> products = new ArrayList<>();
    JSONObject jsonObject = new JSONObject();

    public ModelSuningOrder() {
    }

    public ModelSuningOrder(JSONObject object) {
        if (object != null) {
            jsonObject = object;
            if (object.has("customerName")) {
                if (!object.optString("customerName").equalsIgnoreCase("null")) {
                    customerName = object.optString("customerName");
                }
            }
            if (object.has("customerPhone")) {
                if (!object.optString("customerPhone").equalsIgnoreCase("null")) {
                    customerPhone = object.optString("customerPhone");
                }
            }
            if (object.has("spName")) {
                if (!object.optString("spName").equalsIgnoreCase("null")) {
                    spName = object.optString("spName");
                }
            }
            if (object.has("orderType")) {
                if (!object.optString("orderType").equalsIgnoreCase("null")) {
                    orderType = object.optString("orderType");
                }
            }
            if (object.has("paymentType")) {
                if (!object.optString("paymentType").equalsIgnoreCase("null")) {
                    paymentType = object.optString("paymentType");
                }
            }
            if (object.has("address")) {
                if (!object.optString("address").equalsIgnoreCase("null")) {
                    address = object.optString("address");
                }
            }
            if (object.has("orderNumber")) {
                if (!object.optString("orderNumber").equalsIgnoreCase("null")) {
                    orderNumber = object.optString("orderNumber");
                }
            }
            if (object.has("posName")) {
                if (!object.optString("posName").equalsIgnoreCase("null")) {
                    posName = object.optString("posName");
                }
            }
            if (object.has("spPhone")) {
                if (!object.optString("spPhone").equalsIgnoreCase("null")) {
                    spPhone = object.optString("spPhone");
                }
            }
            if (object.has("sellTime")) {
                if (!object.optString("sellTime").equalsIgnoreCase("null")) {
                    sellTime = object.optString("sellTime");
                }
            }
            if (object.has("freight")) {
                if (!object.optString("freight").equalsIgnoreCase("null")) {
                    freight = object.optDouble("freight");
                }
            }
            if (object.has("account")) {
                if (!object.optString("account").equalsIgnoreCase("null")) {
                    account = object.optDouble("account");
                }
            }
            if (object.has("suNingOrderNumber")) {
                if (!object.optString("suNingOrderNumber").equalsIgnoreCase("null")) {
                    suNingOrderNumber = object.optString("suNingOrderNumber");
                }
            }
            JSONArray productArray = object.optJSONArray("product");
            if (productArray != null) {
                for (int i = 0; i < productArray.length(); i++) {
                    products.add(new ModelSuningOrderProduct(productArray.optJSONObject(i)));
                }
            }
        }
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getSpName() {
        return spName;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getAddress() {
        return address;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getPosName() {
        return posName;
    }

    public String getSpPhone() {
        return spPhone;
    }

    public String getSpArea() {
        return spArea;
    }

    public String getSellTime() {
        return sellTime;
    }

    public double getFreight() {
        return freight;
    }

    public double getAccount() {
        return account;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getSuNingOrderNumber() {
        return suNingOrderNumber;
    }

    public List<ModelSuningOrderProduct> getProducts() {
        return products;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
