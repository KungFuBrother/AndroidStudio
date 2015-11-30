package yitgogo.consumer.local.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @JsonObject "retailOrderProductInfoSet": [ { "id": 23, "retailProductName":
 *             "赵家大龙虾", "retailProductNumber": "YT25049887999",
 *             "retailProductType": "", "productQuantity": 1,
 *             "retailProductUnitPrice": 28.7, "retailProductTypeValue": { "id":
 *             2, "retailProdTypeValueName": "生鲜", "retailClassTypeBean": {
 *             "id": 2, "retailProductType": "小类" },
 *             "retailClassValueParentBean": { "id": 1,
 *             "retailProdTypeValueName": "农副产品", "retailClassTypeBean": { "id":
 *             1, "retailProductType": "大类" }, "retailClassValueParentBean":
 *             null, "retailBrandSet": [], "retailProductTypeValueSet": [],
 *             "img":
 *             "http://images.yitos.net/images/public/20150714/70591436867787046.jpg"
 *             , "providerBean": { "id": 1, "no": "YT613630259926",
 *             "brevitycode": "scytsmyxgs", "servicename": "四川易田商贸有限公司",
 *             "businessno": "VB11122220000", "contacts": "易田", "cardnumber":
 *             "111111111111111111", "serviceaddress": "成都市金牛区", "contactphone":
 *             "13076063079", "contacttelephone": "028-83222680", "email":
 *             "qqqqq@qq.com", "reva": { "id": 3253, "valuename": "中国",
 *             "valuetype": { "id": 1, "typename": "国" }, "onid": 0, "onname":
 *             null, "brevitycode": null }, "contractno": "SC11111100000",
 *             "contractannex": "", "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null } }, "retailBrandSet": [],
 *             "retailProductTypeValueSet": [], "img": null, "providerBean": {
 *             "id": 1, "no": "YT613630259926", "brevitycode": "scytsmyxgs",
 *             "servicename": "四川易田商贸有限公司", "businessno": "VB11122220000",
 *             "contacts": "易田", "cardnumber": "111111111111111111",
 *             "serviceaddress": "成都市金牛区", "contactphone": "13076063079",
 *             "contacttelephone": "028-83222680", "email": "qqqqq@qq.com",
 *             "reva": { "id": 3253, "valuename": "中国", "valuetype": { "id": 1,
 *             "typename": "国" }, "onid": 0, "onname": null, "brevitycode": null
 *             }, "contractno": "SC11111100000", "contractannex": "",
 *             "onservice": null, "state": "启用", "addtime":
 *             "2014-09-04 16:01:36", "starttime": 1409760000000, "sptype": "1",
 *             "endtime": 1457712000000, "supply": true, "imghead": "",
 *             "longitude": null, "latitude": null } }, "consumptionInfo": "
 *             <p style=\"text-align: center; \">
 *             <img align=\"absmiddle\" src=\
 *             "http://gd4.alicdn.com/imgextra/i4/831642084/TB2Gs4mapXXXXbyXXXXXXXXXXXX_!!83164208
 *             4 . p n g \ " style=\"margin: 0px; padding: 0px; vertical-align:
 *             top; color: rgb(0, 0, 0); font-family: tahoma, arial, ������,
 *             sans-serif; font-size: 14px; line-height: 21px; orphans: 2;
 *             white-space: normal; widows: 2;\"/>&nbsp;
 *             </p>
 *             ", "createDate": "2015-07-20 17:03:44", "state": "努力发货中",
 *             "content": "" } ]
 * 
 */
public class ModelLocalGoodsOrderGoods {

	String id = "", retailProductName = "", retailProductNumber = "",
			retailProductType = "", state = "", content = "", img = "";
	int productQuantity = 0;
	double retailProductUnitPrice = 0;
	JSONObject jsonObject = new JSONObject();

	public ModelLocalGoodsOrderGoods() {
	}

	public ModelLocalGoodsOrderGoods(JSONObject object) throws JSONException {

		if (object != null) {
			this.jsonObject = object;
			if (object.has("id")) {
				if (!object.getString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("retailProductName")) {
				if (!object.getString("retailProductName").equalsIgnoreCase(
						"null")) {
					retailProductName = object.optString("retailProductName");
				}
			}
			if (object.has("retailProductNumber")) {
				if (!object.getString("retailProductNumber").equalsIgnoreCase(
						"null")) {
					retailProductNumber = object
							.optString("retailProductNumber");
				}
			}
			if (object.has("retailProductType")) {
				if (!object.getString("retailProductType").equalsIgnoreCase(
						"null")) {
					retailProductType = object.optString("retailProductType");
				}
			}
			if (object.has("retailProductUnitPrice")) {
				if (!object.getString("retailProductUnitPrice")
						.equalsIgnoreCase("null")) {
					retailProductUnitPrice = object
							.optDouble("retailProductUnitPrice");
				}
			}
			if (object.has("productQuantity")) {
				if (!object.getString("productQuantity").equalsIgnoreCase(
						"null")) {
					productQuantity = object.optInt("productQuantity");
				}
			}
			if (object.has("state")) {
				if (!object.getString("state").equalsIgnoreCase("null")) {
					state = object.optString("state");
				}
			}
			if (object.has("content")) {
				if (!object.getString("content").equalsIgnoreCase("null")) {
					content = object.optString("content");
				}
			}
			if (object.has("img")) {
				if (!object.getString("img").equalsIgnoreCase("null")) {
					img = object.optString("img");
				}
			}
		}

	}

	public String getId() {
		return id;
	}

	public String getRetailProductName() {
		return retailProductName;
	}

	public String getRetailProductNumber() {
		return retailProductNumber;
	}

	public String getRetailProductType() {
		return retailProductType;
	}

	public String getState() {
		return state;
	}

	public String getContent() {
		return content;
	}

	public int getProductQuantity() {
		return productQuantity;
	}

	public double getRetailProductUnitPrice() {
		return retailProductUnitPrice;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

	public String getImg() {
		return img;
	}

	@Override
	public String toString() {
		return "ModelLocalGoodsOrderGoods [id=" + id + ", retailProductName="
				+ retailProductName + ", retailProductNumber="
				+ retailProductNumber + ", retailProductType="
				+ retailProductType + ", state=" + state + ", content="
				+ content + ", productQuantity=" + productQuantity
				+ ", retailProductUnitPrice=" + retailProductUnitPrice
				+ ", jsonObject=" + jsonObject + "]";
	}

}