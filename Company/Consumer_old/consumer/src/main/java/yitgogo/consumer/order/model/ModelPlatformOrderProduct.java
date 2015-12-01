package yitgogo.consumer.order.model;

import org.json.JSONObject;

public class ModelPlatformOrderProduct {

    String id = "", productNumber = "", productName = "", attName = "", productUnit = "", img = "", providerId = "", supplierId = "", userAccount = "";
    int productQuantity = 0;
    double unitSellPrice = 0;
    int displayReturnButton = 0;
    int returnState = 0;
    JSONObject jsonObject = new JSONObject();

    public ModelPlatformOrderProduct(JSONObject object) {
        if (object != null) {
            this.jsonObject = object;
            if (object.has("id")) {
                if (!object.optString("id").equalsIgnoreCase("null")) {
                    id = object.optString("id");
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
            if (object.has("attName")) {
                if (!object.optString("attName").equalsIgnoreCase("null")) {
                    attName = object.optString("attName");
                }
            }
            if (object.has("productUnit")) {
                if (!object.optString("productUnit").equalsIgnoreCase("null")) {
                    productUnit = object.optString("productUnit");
                }
            }
            if (object.has("img")) {
                if (!object.optString("img").equalsIgnoreCase("null")) {
                    img = object.optString("img");
                }
            }
            if (object.has("providerId")) {
                if (!object.optString("providerId").equalsIgnoreCase("null")) {
                    providerId = object.optString("providerId");
                }
            }
            if (object.has("supplierId")) {
                if (!object.optString("supplierId").equalsIgnoreCase("null")) {
                    supplierId = object.optString("supplierId");
                }
            }
            if (object.has("userAccount")) {
                if (!object.optString("userAccount").equalsIgnoreCase("null")) {
                    userAccount = object.optString("userAccount");
                }
            }
            if (object.has("displayReturnButton")) {
                if (!object.optString("displayReturnButton").equalsIgnoreCase("null")) {
                    displayReturnButton = object.optInt("displayReturnButton");
                }
            }
            if (object.has("returnState")) {
                if (!object.optString("returnState").equalsIgnoreCase("null")) {
                    returnState = object.optInt("returnState");
                }
            }
            if (object.has("productQuantity")) {
                if (!object.optString("productQuantity").equalsIgnoreCase("null")) {
                    productQuantity = object.optInt("productQuantity");
                }
            }
            if (object.has("unitSellPrice")) {
                if (!object.optString("unitSellPrice").equalsIgnoreCase("null")) {
                    unitSellPrice = object.optDouble("unitSellPrice");
                }
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

    public String getProviderId() {
        return providerId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public int getDisplayReturnButton() {
        return displayReturnButton;
    }

    public int getReturnState() {
        return returnState;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public double getUnitSellPrice() {
        return unitSellPrice;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
