package yitgogo.consumer.local.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.store.model.ModelStoreSelected;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject { "message": "ok", "state": "SUCCESS", "cacheKey": null,
 *             "dataList": [], "totalCount": 1, "dataMap": {}, "object": { "id":
 *             28, "orderNumber": "YT2299879024", "customerName": "雷小武",
 *             "customerPhone": "13032889558", "deliveryType": "自取",
 *             "deliveryAddress": null, "mustAddress": "解放路二段六号凤凰大厦",
 *             "orderDate": "2015-09-25 15:15:20", "orderType": "服务",
 *             "orderPrice": 998, "localOrderProductInfoSet": [ { "id": 28,
 *             "productName": "测试服务二", "productNumber": "YT93928048760",
 *             "productType": "服务", "productUnitPrice": 998, "productNum": 1,
 *             "consumptionInfo": "斯蒂芬敢死队股份第三个份第三个房顶上", "img":
 *             "http://images.yitos.net/images/public/20150919/96381442649264935.jpg"
 *             , "huoyuanId": "1448" } ], "orderState": "新订单", "paymentType":
 *             "在线支付", "payment": "未付款", "providerBean": { "id": 6, "no":
 *             "YT445571890374", "brevitycode": "mrfwzx", "servicename":
 *             "默认服务中心", "businessno": "sadfsfsf", "contacts": "阿斯顿发送到",
 *             "cardnumber": "123456854585632514", "serviceaddress":
 *             "解放路二段六号凤凰大厦", "contactphone": "15923564512", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "易田总运营中心", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
 *             "13076063079", "contacttelephone": "028-83222680", "email":
 *             "qqqqq@qq.com", "reva": { "id": 3253, "valuename": "中国",
 *             "valuetype": { "id": 1, "typename": "国" }, "onid": 0, "onname":
 *             null, "brevitycode": null }, "contractno": "SC11111100000",
 *             "contractannex": "", "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-09-16 20:00:02", "starttime": 1410796800000, "sptype": "2",
 *             "endtime": 1411747200000, "supply": false, "imghead": "",
 *             "longitude": "104.08660070628", "latitude": "30.683978160118" },
 *             "supplyBean": { "id": 1448, "no": "YT355565434291",
 *             "brevitycode": "ytcsfwz", "servicename": "易田测试服务站", "businessno":
 *             "41464644646446", "contacts": "陶贵生", "cardnumber":
 *             "510503199210136611", "serviceaddress": "四川省成都市金牛区解放路二段",
 *             "contactphone": "18004095121", "contacttelephone": "0284564646",
 *             "email": "654651464654@qq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "541456464641", "contractannex": "", "onservice": { "id": 6,
 *             "no": "YT445571890374", "brevitycode": "mrfwzx", "servicename":
 *             "默认服务中心", "businessno": "sadfsfsf", "contacts": "阿斯顿发送到",
 *             "cardnumber": "123456854585632514", "serviceaddress":
 *             "解放路二段六号凤凰大厦", "contactphone": "15923564512", "contacttelephone":
 *             "028-12345678", "email": "saaa@qqq.com", "reva": { "id": 2421,
 *             "valuename": "金牛区", "valuetype": { "id": 4, "typename": "区县" },
 *             "onid": 269, "onname": null, "brevitycode": null }, "contractno":
 *             "sdfsdsda", "contractannex":
 *             "http://images.yitos.net:88/images/public/20140916/35871410868771346.doc"
 *             , "onservice": { "id": 1, "no": "YT613630259926", "brevitycode":
 *             "scytsmyxgs", "servicename": "易田总运营中心", "businessno":
 *             "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
 *             "13076063079", "contacttelephone": "028-83222680", "email":
 *             "qqqqq@qq.com", "reva": { "id": 3253, "valuename": "中国",
 *             "valuetype": { "id": 1, "typename": "国" }, "onid": 0, "onname":
 *             null, "brevitycode": null }, "contractno": "SC11111100000",
 *             "contractannex": "", "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null }, "state": "启用", "addtime":
 *             "2014-09-16 20:00:02", "starttime": 1410796800000, "sptype": "2",
 *             "endtime": 1411747200000, "supply": false, "imghead": "",
 *             "longitude": "104.08660070628", "latitude": "30.683978160118" },
 *             "state": "启用", "addtime": "2015-09-14 11:35:31", "starttime":
 *             1442160000000, "sptype": "3", "endtime": 1568390400000, "supply":
 *             false, "imghead": "", "longitude": "104.08902889614", "latitude":
 *             "30.686814874166" }, "memberNumber": "13032889558",
 *             "localOrderPosType": "默认服务中心", "fahuoDate": 0, "jiqima": null } }
 * 
 */
public class ModelLocalServiceOrder {

	String id = "", orderNumber = "", customerName = "", customerPhone = "",
			deliveryType = "", deliveryAddress = "", mustAddress = "",
			orderDate = "", orderType = "", orderState = "", paymentType = "",
			memberNumber = "", localOrderPosType = "";
	double orderPrice = 0;
	List<ModelLocalServiceOrderGoods> orderGoods = new ArrayList<ModelLocalServiceOrderGoods>();
	ModelStoreSelected supplyBean = new ModelStoreSelected();
	JSONObject jsonObject = new JSONObject();

	public ModelLocalServiceOrder() {
	}

	public ModelLocalServiceOrder(JSONObject object) throws JSONException {

		if (object != null) {
			this.jsonObject = object;
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("orderNumber")) {
				if (!object.getString("orderNumber").equalsIgnoreCase("null")) {
					orderNumber = object.optString("orderNumber");
				}
			}
			if (object.has("customerName")) {
				if (!object.getString("customerName").equalsIgnoreCase("null")) {
					customerName = object.optString("customerName");
				}
			}
			if (object.has("customerPhone")) {
				if (!object.getString("customerPhone").equalsIgnoreCase("null")) {
					customerPhone = object.optString("customerPhone");
				}
			}
			if (object.has("deliveryType")) {
				if (!object.getString("deliveryType").equalsIgnoreCase("null")) {
					deliveryType = object.optString("deliveryType");
				}
			}
			if (object.has("deliveryAddress")) {
				if (!object.getString("deliveryAddress").equalsIgnoreCase(
						"null")) {
					deliveryAddress = object.optString("deliveryAddress");
				}
			}
			if (object.has("mustAddress")) {
				if (!object.getString("mustAddress").equalsIgnoreCase("null")) {
					mustAddress = object.optString("mustAddress");
				}
			}
			if (object.has("orderDate")) {
				if (!object.getString("orderDate").equalsIgnoreCase("null")) {
					orderDate = object.optString("orderDate");
				}
			}
			if (object.has("orderType")) {
				if (!object.getString("orderType").equalsIgnoreCase("null")) {
					orderType = object.optString("orderType");
				}
			}
			if (object.has("orderPrice")) {
				if (!object.getString("orderPrice").equalsIgnoreCase("null")) {
					orderPrice = object.optDouble("orderPrice");
				}
			}
			if (object.has("orderState")) {
				if (!object.getString("orderState").equalsIgnoreCase("null")) {
					orderState = object.optString("orderState");
				}
			}
			if (object.has("paymentType")) {
				if (!object.getString("paymentType").equalsIgnoreCase("null")) {
					paymentType = object.optString("paymentType");
				}
			}
			if (object.has("memberNumber")) {
				if (!object.getString("memberNumber").equalsIgnoreCase("null")) {
					memberNumber = object.optString("memberNumber");
				}
			}
			if (object.has("localOrderPosType")) {
				if (!object.getString("localOrderPosType").equalsIgnoreCase(
						"null")) {
					localOrderPosType = object.optString("localOrderPosType");
				}
			}
			JSONObject supplyBeanJsonObject = object
					.optJSONObject("supplyBean");
			if (supplyBeanJsonObject != null) {
				supplyBean = new ModelStoreSelected(supplyBeanJsonObject);
			}
			JSONArray localOrderProductInfoSet = object
					.optJSONArray("localOrderProductInfoSet");
			if (localOrderProductInfoSet != null) {
				for (int i = 0; i < localOrderProductInfoSet.length(); i++) {
					orderGoods.add(new ModelLocalServiceOrderGoods(
							localOrderProductInfoSet.optJSONObject(i)));
				}
			}
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

	public String getCustomerPhone() {
		return customerPhone;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	public String getMustAddress() {
		return mustAddress;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public String getOrderType() {
		return orderType;
	}

	public String getOrderState() {
		return orderState;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public String getMemberNumber() {
		return memberNumber;
	}

	public String getLocalOrderPosType() {
		return localOrderPosType;
	}

	public double getOrderPrice() {
		return orderPrice;
	}

	public List<ModelLocalServiceOrderGoods> getOrderGoods() {
		return orderGoods;
	}

	public ModelStoreSelected getSupplyBean() {
		return supplyBean;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	@Override
	public String toString() {
		return "ModelLocalServiceOrder [id=" + id + ", orderNumber="
				+ orderNumber + ", customerName=" + customerName
				+ ", customerPhone=" + customerPhone + ", deliveryType="
				+ deliveryType + ", deliveryAddress=" + deliveryAddress
				+ ", mustAddress=" + mustAddress + ", orderDate=" + orderDate
				+ ", orderType=" + orderType + ", orderState=" + orderState
				+ ", paymentType=" + paymentType + ", memberNumber="
				+ memberNumber + ", localOrderPosType=" + localOrderPosType
				+ ", orderPrice=" + orderPrice + ", orderGoods=" + orderGoods
				+ ", supplyBean=" + supplyBean + ", jsonObject=" + jsonObject
				+ "]";
	}

}
