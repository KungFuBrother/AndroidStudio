package com.smartown.controller.shoppingcart;

/**
 * Created by Tiger on 2015-11-27.
 */
public class ModelShoppingCart {

    public final static String columnIsSelected = "isSelected";
    public final static String columnBuyCount = "buyCount";
    public final static String columnProviderId = "providerId";
    public final static String columnProviderName = "providerName";
    public final static String columnProductId = "productId";
    public final static String columnProductObject = "productObject";

    private boolean isSelected=false;
    private int buyCount=1;
    private String providerId="";
    private String providerName="";
    private String productId="";
    private String productObject="";

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public int getBuyCount() {
        return buyCount;
    }

    public void setBuyCount(int buyCount) {
        this.buyCount = buyCount;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductObject() {
        return productObject;
    }

    public void setProductObject(String productObject) {
        this.productObject = productObject;
    }
}
