package yitgogo.consumer.order.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "id": 53253, "orderNumber": "YT5843864770", "warehouse": null,
 *       "customerName": "雷小武", "phone": "13032889558", "shippingaddress":
 *       "中国>四川省>成都市>金牛区解放路二段6号", "sellStaff": "易田测试加盟店四", "sellArea": null,
 *       "posName": "易田测试加盟店四", "providerId": "6", "sellTime":
 *       "2015-09-11 15:52:29", "payType": "未付款", "paymentMannerBean": { "id":
 *       1, "paymentMannerName": "全款" }, "totalMoney": 6698, "totalDiscount": 0,
 *       "totalMoney_Discount": 6698, "operator": null, "handleTime": null,
 *       "orderStatusBean": { "id": 1, "orderStatusName": "新订单" }, "remarks":
 *       null, "productInfoSet": [ { "id": 61386, "productNumber":
 *       "YT94998853644", "productName":
 *       "格力（GREE）KFR-50LW/(50551)FNAa-A3 2匹 立柜式I酷系列家用冷暖变频空调（深银色）网上易田，省心省钱",
 *       "attName": "功率:2匹", "productUnit": "台", "productQuantity": 1,
 *       "unitPrice": 0, "salesPrice": 0, "discount": 0, "purchasePrice": 0,
 *       "remarks": null, "img":
 *       "http://images.yitos.net/images/public/20150824/66821440393780937.jpg",
 *       "state": "努力发货中", "huoyuan": "四川易田商贸有限公司供应商", "promotionalProduct":
 *       "0", "brandName": "格力", "className": "空调", "addTime": 1441957949000,
 *       "providerId": "6", "serviceOCId": "1", "serviceProductOnId": null,
 *       "oparetionCenterId": "6", "yiDianId": null, "supplierId": "1195",
 *       "userAccount": "13032889558", "productType": "普通商品", "integral": 0,
 *       "unitSellPrice": 6698, "unitWholesalePrice": 6400, "unitPurchasePrice":
 *       6300, "unitSupplyPrice": 0, "unitCostPrice": 2, "iclassValueBean": {
 *       "id": 7, "name": "空调", "classTypeBean": { "id": 3, "name": "小类" },
 *       "classValueBean": { "id": 6, "name": "大家电", "classTypeBean": { "id": 2,
 *       "name": "中类" }, "classValueBean": { "id": 1, "name": "家用电器",
 *       "classTypeBean": { "id": 1, "name": "大类" }, "classValueBean": null,
 *       "paExtendSet": [], "brandSet": [], "pcSet": [], "img":
 *       "http://images.yitos.net/images/public/20150727/46911437966899483.png"
 *       }, "paExtendSet": [], "brandSet": [], "pcSet": [], "img": null },
 *       "paExtendSet": [], "brandSet": [], "pcSet": [], "img": null } } ],
 *       "yes": 1, "userNumber": "13032889558", "orNumber": null, "yinhangName":
 *       null, "yinhangId": 0, "numA": 1, "serialNum": null, "tradingData":
 *       null, "jiqima": "BFEBFBFF000306A9321077558", "huoyuan":
 *       "四川易田商贸有限公司:028-83222680", "orNumberFC": null, "orderSourceType":
 *       "消费者", "onlyOne": "YT5843864770", "totalIntegral": 0, "isIntegralMall":
 *       "0", "isRecordStock": null, "versionNumber": "2", "orderType": "0",
 *       "totalSellPrice": 6698, "totalWholesalePrice": 6400,
 *       "totalPurchasePrice": 6300, "totalSupplyPrice": 0, "totalCostPrice": 2,
 *       "serviceOCId": "1", "serviceProductOnId": null, "oparetionCenterId":
 *       "6", "yiDianId": null, "supplierId": "1195" }
 */
public class ModelOrder {

	String id = "", orderNumber = "", customerName = "", phone = "",
			shippingaddress = "", sellTime = "", onlyOne = "", huoyuan = "";
	double totalSellPrice = 0;
	int numA = 0;
	ModelOrderState orderState = new ModelOrderState(new JSONObject());
	List<ModelProductOrder> products = new ArrayList<ModelProductOrder>();

	public ModelOrder(JSONObject object) throws JSONException {
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
		if (object.has("phone")) {
			if (!object.getString("phone").equalsIgnoreCase("null")) {
				phone = object.optString("phone");
			}
		}
		if (object.has("shippingaddress")) {
			if (!object.getString("shippingaddress").equalsIgnoreCase("null")) {
				shippingaddress = object.optString("shippingaddress");
			}
		}
		if (object.has("sellTime")) {
			if (!object.getString("sellTime").equalsIgnoreCase("null")) {
				sellTime = object.optString("sellTime");
			}
		}
		if (object.has("totalSellPrice")) {
			if (!object.getString("totalSellPrice").equalsIgnoreCase("null")) {
				totalSellPrice = object.optDouble("totalSellPrice");
			}
		}
		if (object.has("onlyOne")) {
			if (!object.getString("onlyOne").equalsIgnoreCase("null")) {
				onlyOne = object.optString("onlyOne");
			}
		}
		if (object.has("orderStatusBean")) {
			if (!object.getString("orderStatusBean").equalsIgnoreCase("null")) {
				orderState = new ModelOrderState(
						object.getJSONObject("orderStatusBean"));
			}
		}
		if (object.has("huoyuan")) {
			if (!object.getString("huoyuan").equalsIgnoreCase("null")) {
				huoyuan = object.optString("huoyuan");
			}
		}
		if (object.has("productInfoSet")) {
			if (!object.getString("productInfoSet").equalsIgnoreCase("null")) {
				JSONArray productArray = object.getJSONArray("productInfoSet");
				for (int i = 0; i < productArray.length(); i++) {
					products.add(new ModelProductOrder(productArray
							.getJSONObject(i)));
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

	public String getPhone() {
		return phone;
	}

	public String getShippingaddress() {
		return shippingaddress;
	}

	public String getSellTime() {
		return sellTime;
	}

	public String getOnlyOne() {
		return onlyOne;
	}

	public double getTotalSellPrice() {
		return totalSellPrice;
	}

	public int getNumA() {
		return numA;
	}

	public String getHuoyuan() {
		return huoyuan;
	}

	public ModelOrderState getOrderState() {
		return orderState;
	}

	public List<ModelProductOrder> getProducts() {
		return products;
	}

	public class ModelOrderState {
		int id = 1;
		String orderStatusName = "";

		public ModelOrderState(JSONObject object) throws JSONException {
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optInt("id");
				}
			}
			if (object.has("orderStatusName")) {
				if (!object.getString("orderStatusName").equalsIgnoreCase(
						"null")) {
					orderStatusName = object.optString("orderStatusName");
				}
			}
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getOrderStatusName() {
			return orderStatusName;
		}

		public void setOrderStatusName(String orderStatusName) {
			this.orderStatusName = orderStatusName;
		}

	}
}
