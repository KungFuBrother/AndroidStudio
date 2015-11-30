package yitgogo.consumer.order.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelProductOrder {
	/**
	 * { "id": 61386, "productNumber": "YT94998853644", "productName":
	 * "格力（GREE）KFR-50LW/(50551)FNAa-A3 2匹 立柜式I酷系列家用冷暖变频空调（深银色）网上易田，省心省钱",
	 * "attName": "功率:2匹", "productUnit": "台", "productQuantity": 1,
	 * "unitPrice": 0, "salesPrice": 0, "discount": 0, "purchasePrice": 0,
	 * "remarks": null, "img":
	 * "http://images.yitos.net/images/public/20150824/66821440393780937.jpg",
	 * "state": "努力发货中", "huoyuan": "四川易田商贸有限公司供应商", "promotionalProduct": "0",
	 * "brandName": "格力", "className": "空调", "addTime": 1441957949000,
	 * "providerId": "6", "serviceOCId": "1", "serviceProductOnId": null,
	 * "oparetionCenterId": "6", "yiDianId": null, "supplierId": "1195",
	 * "userAccount": "13032889558", "productType": "普通商品", "integral": 0,
	 * "unitSellPrice": 6698, "unitWholesalePrice": 6400, "unitPurchasePrice":
	 * 6300, "unitSupplyPrice": 0, "unitCostPrice": 2, "iclassValueBean": {
	 * "id": 7, "name": "空调", "classTypeBean": { "id": 3, "name": "小类" },
	 * "classValueBean": { "id": 6, "name": "大家电", "classTypeBean": { "id": 2,
	 * "name": "中类" }, "classValueBean": { "id": 1, "name": "家用电器",
	 * "classTypeBean": { "id": 1, "name": "大类" }, "classValueBean": null,
	 * "paExtendSet": [], "brandSet": [], "pcSet": [], "img":
	 * "http://images.yitos.net/images/public/20150727/46911437966899483.png" },
	 * "paExtendSet": [], "brandSet": [], "pcSet": [], "img": null },
	 * "paExtendSet": [], "brandSet": [], "pcSet": [], "img": null } }
	 */

	String id = "", productNumber = "", productName = "", attName = "",
			productUnit = "", img = "";
	int productQuantity = 0;
	double unitSellPrice = 0;

	public ModelProductOrder(JSONObject object) throws JSONException {
		if (object.has("id")) {
			if (!object.getString("id").equalsIgnoreCase("null")) {
				id = object.optString("id");
			}
		}
		if (object.has("productNumber")) {
			if (!object.getString("productNumber").equalsIgnoreCase("null")) {
				productNumber = object.optString("productNumber");
			}
		}
		if (object.has("productName")) {
			if (!object.getString("productName").equalsIgnoreCase("null")) {
				productName = object.optString("productName");
			}
		}
		if (object.has("attName")) {
			if (!object.getString("attName").equalsIgnoreCase("null")) {
				attName = object.optString("attName");
			}
		}
		if (object.has("productUnit")) {
			if (!object.getString("productUnit").equalsIgnoreCase("null")) {
				productUnit = object.optString("productUnit");
			}
		}
		if (object.has("img")) {
			if (!object.getString("img").equalsIgnoreCase("null")) {
				img = object.optString("img");
			}
		}
		if (object.has("productQuantity")) {
			if (!object.getString("productQuantity").equalsIgnoreCase("null")) {
				productQuantity = object.optInt("productQuantity");
			}
		}
		if (object.has("unitSellPrice")) {
			if (!object.getString("unitSellPrice").equalsIgnoreCase("null")) {
				unitSellPrice = object.optDouble("unitSellPrice");
			}
		}
	}

	public String getId() {
		return id;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getProductName() {
		return productName;
	}

	public String getAttName() {
		return attName;
	}

	public String getProductUnit() {
		return productUnit;
	}

	public String getImg() {
		return img;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public double getUnitSellPrice() {
		return unitSellPrice;
	}

}
