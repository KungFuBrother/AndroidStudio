package yitgogo.consumer.order.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Tiger
 * 
 * @Json { "servicetelephone"
 *       :"028-12345678","deliveryType":"送货上门","paymentType" :"1","orderDate"
 *       :"2015-10-16","servicename":"易田测试加盟店四","productInfo":
 *       "[{\"spname\":\"产品组-测试产品\",\"price\":\"33.0\",\"Amount\":\"66.0\",\"num\":\"2\"}]"
 *       ,"ordernumber":"YT5966059962","postagePrice":"满50.0包邮", "orderPrice"
 *       :"66.0","servicePhone":"13228116626"}
 */
public class ModelLocalGoodsOrderResult {

	String servicetelephone = "", deliveryType = "", orderDate = "",
			servicename = "", ordernumber = "", postagePrice = "",
			servicePhone = "";
	double orderPrice = 0;
	int paymentType = ModelPayment.TYPE_ONLINE;
	List<ModelLocalGoodsOrderResultGoods> orderResultGoods = new ArrayList<ModelLocalGoodsOrderResultGoods>();

	public ModelLocalGoodsOrderResult(JSONObject object) {
		if (object != null) {
			if (object.has("servicetelephone")) {
				if (!object.optString("servicetelephone").equalsIgnoreCase(
						"null")) {
					servicetelephone = object.optString("servicetelephone");
				}
			}
			if (object.has("deliveryType")) {
				if (!object.optString("deliveryType").equalsIgnoreCase("null")) {
					deliveryType = object.optString("deliveryType");
				}
			}
			if (object.has("orderDate")) {
				if (!object.optString("orderDate").equalsIgnoreCase("null")) {
					orderDate = object.optString("orderDate");
				}
			}
			if (object.has("servicename")) {
				if (!object.optString("servicename").equalsIgnoreCase("null")) {
					servicename = object.optString("servicename");
				}
			}
			if (object.has("ordernumber")) {
				if (!object.optString("ordernumber").equalsIgnoreCase("null")) {
					ordernumber = object.optString("ordernumber");
				}
			}
			if (object.has("postagePrice")) {
				if (!object.optString("postagePrice").equalsIgnoreCase("null")) {
					postagePrice = object.optString("postagePrice");
				}
			}
			if (object.has("servicePhone")) {
				if (!object.optString("servicePhone").equalsIgnoreCase("null")) {
					servicePhone = object.optString("servicePhone");
				}
			}
			if (object.has("orderPrice")) {
				if (!object.optString("orderPrice").equalsIgnoreCase("null")) {
					orderPrice = object.optDouble("orderPrice");
				}
			}
			if (object.has("paymentType")) {
				if (!object.optString("paymentType").equalsIgnoreCase("null")) {
					paymentType = object.optInt("paymentType");
				}
			}
			JSONArray array = object.optJSONArray("productInfo");
			if (array != null) {
				for (int i = 0; i < array.length(); i++) {
					orderResultGoods.add(new ModelLocalGoodsOrderResultGoods(
							array.optJSONObject(i)));
				}
			}
		}
	}

	public String getServicetelephone() {
		return servicetelephone;
	}

	public String getDeliveryType() {
		return deliveryType;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public String getServicename() {
		return servicename;
	}

	public String getOrdernumber() {
		return ordernumber;
	}

	public String getPostagePrice() {
		return postagePrice;
	}

	public String getServicePhone() {
		return servicePhone;
	}

	public double getOrderPrice() {
		return orderPrice;
	}

	public int getPaymentType() {
		return paymentType;
	}

	public List<ModelLocalGoodsOrderResultGoods> getOrderResultGoods() {
		return orderResultGoods;
	}

}
