package yitgogo.smart.order.model;

import org.json.JSONArray;

public class OrderData {

	double totalMoney = 0;
	JSONArray jsonArray = new JSONArray();

	public OrderData(double totalMoney, JSONArray jsonArray) {
		this.totalMoney = totalMoney;
		this.jsonArray = jsonArray;
	}

	public OrderData() {
	}

	public double getTotalMoney() {
		return totalMoney;
	}

	public JSONArray getJsonArray() {
		return jsonArray;
	}

}
