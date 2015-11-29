package yitgogo.smart.print;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 打印数据
 * 
 * @author Tiger
 * 
 * @json {"machineCode":"5E799AF4-486A-4EB5-BCFB-4BDE928E4E68"
 *       ,"shopname":"安岳测试","date":"2015-09-07,21","id":"YT2954050621",
 *       "consignee"
 *       :"蒋丰裕","telphone":"18227754003","address":"四川省成都市青羊区西货站路清水路苑"
 *       ,"total":135.0,"servicephone1":"15680786288","servicephone2":
 *       "02888888888" ,"zhekou":0.0,"shifu":135.0,"goodsArr":[{"spname":
 *       "外贸童鞋 2015春款儿童运动鞋全能鞋休闲跑步鞋韩版男女童鞋B3175[颜色:红色-尺码:26码]"
 *       ,"price":135.0,"num":1,"Amount":135.0}]}
 */
public class PrintData {
	String machineCode = "", shopname = "", date = "", id = "", consignee = "",
			telphone = "", address = "", servicephone1 = "",
			servicephone2 = "";
	double total = 0, zhekou = 0, shifu = 0;
	List<PrintDataProduct> products = new ArrayList<PrintDataProduct>();

	public PrintData() {
	}

	public PrintData(JSONObject object) {
		if (object != null) {
			if (object.has("machineCode")) {
				if (!object.optString("machineCode").equalsIgnoreCase("null")) {
					machineCode = object.optString("machineCode");
				}
			}
			if (object.has("shopname")) {
				if (!object.optString("shopname").equalsIgnoreCase("null")) {
					shopname = object.optString("shopname");
				}
			}
			if (object.has("date")) {
				if (!object.optString("date").equalsIgnoreCase("null")) {
					date = object.optString("date");
				}
			}
			if (object.has("id")) {
				if (!object.optString("id").equalsIgnoreCase("null")) {
					id = object.optString("id");
				}
			}
			if (object.has("consignee")) {
				if (!object.optString("consignee").equalsIgnoreCase("null")) {
					consignee = object.optString("consignee");
				}
			}
			if (object.has("telphone")) {
				if (!object.optString("telphone").equalsIgnoreCase("null")) {
					telphone = object.optString("telphone");
				}
			}
			if (object.has("address")) {
				if (!object.optString("address").equalsIgnoreCase("null")) {
					address = object.optString("address");
				}
			}
			if (object.has("servicephone1")) {
				if (!object.optString("servicephone1").equalsIgnoreCase("null")) {
					servicephone1 = object.optString("servicephone1");
				}
			}
			if (object.has("servicephone2")) {
				if (!object.optString("servicephone2").equalsIgnoreCase("null")) {
					servicephone2 = object.optString("servicephone2");
				}
			}
			if (object.has("total")) {
				if (!object.optString("total").equalsIgnoreCase("null")) {
					total = object.optDouble("total");
				}
			}
			if (object.has("zhekou")) {
				if (!object.optString("zhekou").equalsIgnoreCase("null")) {
					zhekou = object.optDouble("zhekou");
				}
			}
			if (object.has("shifu")) {
				if (!object.optString("shifu").equalsIgnoreCase("null")) {
					shifu = object.optDouble("shifu");
				}
			}
			JSONArray goodsArr = object.optJSONArray("goodsArr");
			if (goodsArr != null) {
				for (int i = 0; i < goodsArr.length(); i++) {
					JSONObject goodsObject = goodsArr.optJSONObject(i);
					if (goodsObject != null) {
						products.add(new PrintDataProduct(goodsObject));
					}
				}
			}
		}
	}

	public String getMachineCode() {
		return machineCode;
	}

	public String getShopname() {
		return shopname;
	}

	public String getDate() {
		return date;
	}

	public String getId() {
		return id;
	}

	public String getConsignee() {
		return consignee;
	}

	public String getTelphone() {
		return telphone;
	}

	public String getAddress() {
		return address;
	}

	public String getServicephone1() {
		return servicephone1;
	}

	public String getServicephone2() {
		return servicephone2;
	}

	public double getTotal() {
		return total;
	}

	public double getZhekou() {
		return zhekou;
	}

	public double getShifu() {
		return shifu;
	}

	public List<PrintDataProduct> getProducts() {
		return products;
	}

}
