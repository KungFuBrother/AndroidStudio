package com.dtr.zxing.model;

import org.json.JSONObject;

public class ModelQRCodeProduct {

	String productId = "", productNumber = "", productName = "";
	int productType = 0, saleType = 0;

	public ModelQRCodeProduct(JSONObject object) {
		if (object != null) {
			productType = object.optInt("productType");
			if (object.has("productId")) {
				if (!object.optString("productId").equalsIgnoreCase("null")) {
					productId = object.optString("productId");
				}
			}
			if (object.has("productNumber")) {
				if (!object.optString("productNumber").equalsIgnoreCase("null")) {
					productNumber = object.optString("productNumber");
				}
			}
			if (object.has("productName")) {
				if (!object.optString("productName").equalsIgnoreCase("null")) {
					productName = object.optString("productName");
				}
			}
			saleType = object.optInt("saleType");
		}
	}

	public int getProductType() {
		return productType;
	}

	public String getProductId() {
		return productId;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public String getProductName() {
		return productName;
	}

	public int getSaleType() {
		return saleType;
	}

}
