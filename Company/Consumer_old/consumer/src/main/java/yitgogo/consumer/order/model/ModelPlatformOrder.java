package yitgogo.consumer.order.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tiger
 * @Json {
 * "message": "ok",
 * "state": "SUCCESS",
 * "cacheKey": null,
 * "dataList": [],
 * "totalCount": 1,
 * "dataMap": {},
 * "object": {
 * "id": 14143,
 * "orderNumber": "YT3134695706",
 * "warehouse": "",
 * "customerName": "Tiger",
 * "phone": "13032889558",
 * "shippingaddress": "四川省>成都市>金牛区>凤凰大厦",
 * "sellStaff": "易田测试加盟店五",
 * "sellArea": null,
 * "posName": "易田测试加盟店五",
 * "providerId": "7",
 * "sellTime": "2015-04-07 14:43:37",
 * "payType": "现金支付",
 * "paymentMannerBean": {
 * "id": 1,
 * "paymentMannerName": "全款"
 * },
 * "totalMoney": 2599,
 * "totalDiscount": 0,
 * "totalMoney_Discount": 2599,
 * "operator": null,
 * "handleTime": null,
 * "orderStatusBean": {
 * "id": 7,
 * "orderStatusName": "已收货"
 * },
 * "remarks": null,
 * "productInfoSet": [
 * {
 * "id": 17944,
 * "productNumber": "YT33490031117",
 * "productName": "美的（Midea）KFR-32GW/WPAD3 小1.5匹 易田英雄 迅猛冷暖定速挂机",
 * "attName": null,
 * "productUnit": "台",
 * "productQuantity": 1,
 * "unitPrice": 2599,
 * "salesPrice": 2599,
 * "discount": 0,
 * "purchasePrice": 2250,
 * "remarks": null,
 * "img": "http://images.yitos.net/images/public/20150121/39681421804392105.png",
 * "state": "努力发货中",
 * "huoyuan": "所属服务商",
 * "promotionalProduct": null,
 * "brandName": "美的",
 * "className": "空调",
 * "addTime": 1428389017000,
 * "providerId": "7",
 * "serviceOCId": "1",
 * "serviceProductOnId": null,
 * "oparetionCenterId": "7",
 * "yiDianId": null,
 * "supplierId": null,
 * "userAccount": "13032889558",
 * "productType": null,
 * "integral": 0,
 * "unitSellPrice": 2599,
 * "unitWholesalePrice": 2098,
 * "unitPurchasePrice": 1998,
 * "unitSupplyPrice": 0,
 * "unitCostPrice": 1,
 * "orderSourceType": "现金支付",
 * "isReportStatistics": null,
 * "serviceStationProportion": "3",
 * "serviceCenterProportion": null,
 * "oparetionCenterProportion": null,
 * "serviceStationProfit": null,
 * "oparetionCenterProfit": null,
 * "displayReturnButton": null,
 * "returnState": null,
 * "serviceCenterProfit": null
 * }
 * ],
 * "yes": 1,
 * "userNumber": "13032889558",
 * "orNumber": null,
 * "yinhangName": null,
 * "yinhangId": 0,
 * "numA": 1,
 * "serialNum": null,
 * "tradingData": null,
 * "jiqima": "BFEBFBFF000306A91932489519",
 * "huoyuan": "四川易田商贸有限公司:028-83222680",
 * "orNumberFC": null,
 * "orderSourceType": "消费者",
 * "onlyOne": null,
 * "totalIntegral": 0,
 * "isIntegralMall": "0",
 * "isRecordStock": null,
 * "versionNumber": null,
 * "orderType": null,
 * "totalSellPrice": 2599,
 * "totalWholesalePrice": 2098,
 * "totalPurchasePrice": 1998,
 * "totalSupplyPrice": 0,
 * "totalCostPrice": 1,
 * "serviceOCId": "1",
 * "serviceProductOnId": null,
 * "oparetionCenterId": "7",
 * "yiDianId": null,
 * "supplierId": null,
 * "receivedGoodsProviderId": null,
 * "totalProfit": null,
 * "settlementTypeBig": "1",
 * "settlementTypeSmall": "4",
 * "pass": null,
 * "returnApplicant": null,
 * "reasonForReturn": null,
 * "supplierReasonForReturn": null,
 * "freight": null
 * }
 * }
 */
public class ModelPlatformOrder {

    String id = "", orderNumber = "", customerName = "", phone = "", shippingaddress = "", providerId = "", sellTime = "", userNumber = "", supplierId = "", onlyOne = "", huoyuan = "";
    double totalMoney = 0, totalDiscount = 0, totalMoney_Discount = 0, freight = 0;
    int yes = 0;
    ModelOrderState orderState = new ModelOrderState(new JSONObject());
    List<ModelPlatformOrderProduct> products = new ArrayList<>();
    JSONObject jsonObject = new JSONObject();

    public ModelPlatformOrder(JSONObject object) {
        if (object != null) {
            this.jsonObject = object;
            if (object.has("id")) {
                if (!object.optString("id").equalsIgnoreCase("null")) {
                    id = object.optString("id");
                }
            }
            if (object.has("orderNumber")) {
                if (!object.optString("orderNumber").equalsIgnoreCase("null")) {
                    orderNumber = object.optString("orderNumber");
                }
            }
            if (object.has("customerName")) {
                if (!object.optString("customerName").equalsIgnoreCase("null")) {
                    customerName = object.optString("customerName");
                }
            }
            if (object.has("phone")) {
                if (!object.optString("phone").equalsIgnoreCase("null")) {
                    phone = object.optString("phone");
                }
            }
            if (object.has("shippingaddress")) {
                if (!object.optString("shippingaddress").equalsIgnoreCase("null")) {
                    shippingaddress = object.optString("shippingaddress");
                }
            }
            if (object.has("providerId")) {
                if (!object.optString("providerId").equalsIgnoreCase("null")) {
                    providerId = object.optString("providerId");
                }
            }
            if (object.has("sellTime")) {
                if (!object.optString("sellTime").equalsIgnoreCase("null")) {
                    sellTime = object.optString("sellTime");
                }
            }
            if (object.has("userNumber")) {
                if (!object.optString("userNumber").equalsIgnoreCase("null")) {
                    userNumber = object.optString("userNumber");
                }
            }
            if (object.has("supplierId")) {
                if (!object.optString("supplierId").equalsIgnoreCase("null")) {
                    supplierId = object.optString("supplierId");
                }
            }
            if (object.has("onlyOne")) {
                if (!object.optString("onlyOne").equalsIgnoreCase("null")) {
                    onlyOne = object.optString("onlyOne");
                }
            }
            if (object.has("huoyuan")) {
                if (!object.optString("huoyuan").equalsIgnoreCase("null")) {
                    huoyuan = object.optString("huoyuan");
                }
            }
            if (object.has("totalMoney")) {
                if (!object.optString("totalMoney").equalsIgnoreCase("null")) {
                    totalMoney = object.optDouble("totalMoney");
                }
            }
            if (object.has("totalMoney_Discount")) {
                if (!object.optString("totalMoney").equalsIgnoreCase("null")) {
                    totalMoney_Discount = object.optDouble("totalMoney_Discount");
                }
            }
            if (object.has("totalDiscount")) {
                if (!object.optString("totalDiscount").equalsIgnoreCase("null")) {
                    totalDiscount = object.optDouble("totalDiscount");
                }
            }
            if (object.has("freight")) {
                if (!object.optString("freight").equalsIgnoreCase("null")) {
                    freight = object.optDouble("freight");
                }
            }
            if (object.has("yes")) {
                if (!object.optString("yes").equalsIgnoreCase("null")) {
                    yes = object.optInt("yes");
                }
            }
            if (object.has("orderStatusBean")) {
                if (!object.optString("orderStatusBean").equalsIgnoreCase("null")) {
                    orderState = new ModelOrderState(object.optJSONObject("orderStatusBean"));
                }
            }
            if (object.has("productInfoSet")) {
                if (!object.optString("productInfoSet").equalsIgnoreCase("null")) {
                    JSONArray productArray = object.optJSONArray("productInfoSet");
                    for (int i = 0; i < productArray.length(); i++) {
                        products.add(new ModelPlatformOrderProduct(productArray.optJSONObject(i)));
                    }
                }
            }
        }

    }

    public class ModelOrderState {
        int id = 1;
        String orderStatusName = "";

        public ModelOrderState(JSONObject object) {
            if (object != null) {
                if (object.has("id")) {
                    if (!object.optString("id").equalsIgnoreCase("null")) {
                        id = object.optInt("id");
                    }
                }
                if (object.has("orderStatusName")) {
                    if (!object.optString("orderStatusName").equalsIgnoreCase("null")) {
                        orderStatusName = object.optString("orderStatusName");
                    }
                }
            }
        }

        public int getId() {
            return id;
        }

        public String getOrderStatusName() {
            return orderStatusName;
        }
    }

    public String getId() {
        return id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhone() {
        return phone;
    }

    public String getShippingaddress() {
        return shippingaddress;
    }

    public String getProviderId() {
        return providerId;
    }

    public String getSellTime() {
        return sellTime;
    }

    public String getUserNumber() {
        return userNumber;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getOnlyOne() {
        return onlyOne;
    }

    public String getHuoyuan() {
        return huoyuan;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public double getTotalMoney_Discount() {
        return totalMoney_Discount;
    }

    public double getFreight() {
        return freight;
    }

    public int getYes() {
        return yes;
    }

    public ModelOrderState getOrderState() {
        return orderState;
    }

    public List<ModelPlatformOrderProduct> getProducts() {
        return products;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
