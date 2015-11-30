package yitgogo.consumer.bianmin.order;

import org.json.JSONObject;

/**
 * 便民服务订单
 * 
 * @author Tiger
 * 
 * @Json { "id": 566, "orderNumber": "YT2935954710", "cardid": "151899",
 *       "rechargeNum": null, "rechargeMoney": "10", "rechargeType": "手机",
 *       "rechargeAccount": "13032889558", "orderState": "未付款", "orderTime":
 *       "2015-09-01 11:46:28", "rechargeRemark": null, "sellprice": "10.30",
 *       "mctype": null, "memberAccount": "13032889558", "teltype": null,
 *       "chargeType": null, "game_area": null, "game_server": null,
 *       "game_userpsw": null, "gasCardName": null, "gasCardTel": null,
 *       "invoiceFlag": null, "provId": null, "cityId": null, "processModeId":
 *       null, "penaltyNumber": null, "frameNumber": null, "engineNumber": null,
 *       "vehicleTypeId": null, "illegalTime": null, "lateFees": null }
 */
public class ModelBianminOrder {

	String id = "", orderNumber = "", cardid = "", rechargeNum = "",
			rechargeType = "", rechargeAccount = "", orderState = "",
			orderTime = "", rechargeRemark = "", teltype = "", chargeType = "",
			game_area = "", game_server = "", game_userpsw = "",
			gasCardName = "", gasCardTel = "", invoiceFlag = "", provId = "",
			cityId = "", processModeId = "", penaltyNumber = "",
			frameNumber = "", engineNumber = "", vehicleTypeId = "",
			illegalTime = "", lateFees = "";
	double rechargeMoney = 0, sellprice = 0;
	JSONObject jsonObject = new JSONObject();

	public ModelBianminOrder(JSONObject object) {
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
			if (object.has("cardid")) {
				if (!object.optString("cardid").equalsIgnoreCase("null")) {
					cardid = object.optString("cardid");
				}
			}
			if (object.has("rechargeNum")) {
				if (!object.optString("rechargeNum").equalsIgnoreCase("null")) {
					rechargeNum = object.optString("rechargeNum");
				}
			}
			if (object.has("rechargeType")) {
				if (!object.optString("rechargeType").equalsIgnoreCase("null")) {
					rechargeType = object.optString("rechargeType");
				}
			}
			if (object.has("rechargeAccount")) {
				if (!object.optString("rechargeAccount").equalsIgnoreCase(
						"null")) {
					rechargeAccount = object.optString("rechargeAccount");
				}
			}
			if (object.has("orderState")) {
				if (!object.optString("orderState").equalsIgnoreCase("null")) {
					orderState = object.optString("orderState");
				}
			}
			if (object.has("orderTime")) {
				if (!object.optString("orderTime").equalsIgnoreCase("null")) {
					orderTime = object.optString("orderTime");
				}
			}
			if (object.has("rechargeRemark")) {
				if (!object.optString("rechargeRemark")
						.equalsIgnoreCase("null")) {
					rechargeRemark = object.optString("rechargeRemark");
				}
			}
			if (object.has("teltype")) {
				if (!object.optString("teltype").equalsIgnoreCase("null")) {
					teltype = object.optString("teltype");
				}
			}
			if (object.has("chargeType")) {
				if (!object.optString("chargeType").equalsIgnoreCase("null")) {
					chargeType = object.optString("chargeType");
				}
			}
			if (object.has("game_area")) {
				if (!object.optString("game_area").equalsIgnoreCase("null")) {
					game_area = object.optString("game_area");
				}
			}
			if (object.has("game_server")) {
				if (!object.optString("game_server").equalsIgnoreCase("null")) {
					game_server = object.optString("game_server");
				}
			}
			if (object.has("game_userpsw")) {
				if (!object.optString("game_userpsw").equalsIgnoreCase("null")) {
					game_userpsw = object.optString("game_userpsw");
				}
			}
			if (object.has("gasCardName")) {
				if (!object.optString("gasCardName").equalsIgnoreCase("null")) {
					gasCardName = object.optString("gasCardName");
				}
			}
			if (object.has("gasCardTel")) {
				if (!object.optString("gasCardTel").equalsIgnoreCase("null")) {
					gasCardTel = object.optString("gasCardTel");
				}
			}
			if (object.has("invoiceFlag")) {
				if (!object.optString("invoiceFlag").equalsIgnoreCase("null")) {
					invoiceFlag = object.optString("invoiceFlag");
				}
			}
			if (object.has("provId")) {
				if (!object.optString("provId").equalsIgnoreCase("null")) {
					provId = object.optString("provId");
				}
			}
			if (object.has("cityId")) {
				if (!object.optString("cityId").equalsIgnoreCase("null")) {
					cityId = object.optString("cityId");
				}
			}
			if (object.has("processModeId")) {
				if (!object.optString("processModeId").equalsIgnoreCase("null")) {
					processModeId = object.optString("processModeId");
				}
			}
			if (object.has("penaltyNumber")) {
				if (!object.optString("penaltyNumber").equalsIgnoreCase("null")) {
					penaltyNumber = object.optString("penaltyNumber");
				}
			}
			if (object.has("frameNumber")) {
				if (!object.optString("frameNumber").equalsIgnoreCase("null")) {
					frameNumber = object.optString("frameNumber");
				}
			}
			if (object.has("engineNumber")) {
				if (!object.optString("engineNumber").equalsIgnoreCase("null")) {
					engineNumber = object.optString("engineNumber");
				}
			}
			if (object.has("vehicleTypeId")) {
				if (!object.optString("vehicleTypeId").equalsIgnoreCase("null")) {
					vehicleTypeId = object.optString("vehicleTypeId");
				}
			}
			if (object.has("illegalTime")) {
				if (!object.optString("illegalTime").equalsIgnoreCase("null")) {
					illegalTime = object.optString("illegalTime");
				}
			}
			if (object.has("lateFees")) {
				if (!object.optString("lateFees").equalsIgnoreCase("null")) {
					lateFees = object.optString("lateFees");
				}
			}
			if (object.has("rechargeMoney")) {
				if (!object.optString("rechargeMoney").equalsIgnoreCase("null")) {
					rechargeMoney = object.optDouble("rechargeMoney");
				}
			}
			if (object.has("sellprice")) {
				if (!object.optString("sellprice").equalsIgnoreCase("null")) {
					sellprice = object.optDouble("sellprice");
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

	public String getCardid() {
		return cardid;
	}

	public String getRechargeNum() {
		return rechargeNum;
	}

	public String getRechargeType() {
		return rechargeType;
	}

	public String getRechargeAccount() {
		return rechargeAccount;
	}

	public String getOrderState() {
		return orderState;
	}

	public String getOrderTime() {
		return orderTime;
	}

	public String getRechargeRemark() {
		return rechargeRemark;
	}

	public String getTeltype() {
		return teltype;
	}

	public String getChargeType() {
		return chargeType;
	}

	public String getGame_area() {
		return game_area;
	}

	public String getGame_server() {
		return game_server;
	}

	public String getGame_userpsw() {
		return game_userpsw;
	}

	public String getGasCardName() {
		return gasCardName;
	}

	public String getGasCardTel() {
		return gasCardTel;
	}

	public String getInvoiceFlag() {
		return invoiceFlag;
	}

	public String getProvId() {
		return provId;
	}

	public String getCityId() {
		return cityId;
	}

	public String getProcessModeId() {
		return processModeId;
	}

	public String getPenaltyNumber() {
		return penaltyNumber;
	}

	public String getFrameNumber() {
		return frameNumber;
	}

	public String getEngineNumber() {
		return engineNumber;
	}

	public String getVehicleTypeId() {
		return vehicleTypeId;
	}

	public String getIllegalTime() {
		return illegalTime;
	}

	public String getLateFees() {
		return lateFees;
	}

	public double getRechargeMoney() {
		return rechargeMoney;
	}

	public double getSellprice() {
		return sellprice;
	}

	public JSONObject getJsonObject() {
		return jsonObject;
	}

}
