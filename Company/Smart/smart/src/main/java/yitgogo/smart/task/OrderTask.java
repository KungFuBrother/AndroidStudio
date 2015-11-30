package yitgogo.smart.task;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.smart.order.model.ModelDiliver;
import yitgogo.smart.order.model.ModelPayment;
import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.OnNetworkListener;

public class OrderTask {

    /**
     * 添加本地产品订单
     *
     * @throws JSONException
     * @author Tiger
     * @Result {"message":"ok","state"
     * :"SUCCESS","cacheKey":null,"dataList":[{"servicetelephone"
     * :"028-32562356"
     * ,"orderDate":"2015-08-04","servicename":null,"productInfo":
     * "[{\"spname\":\"测试运营中心一-测试小吃产品一\",\"price\":\"10.0\",\"Amount\":\"10.0\",\"num\":\"1\"}]"
     * ,"ordernumber":"YT3765246787","orderPrice":"10.0","servicePhone":
     * "15821346521"}],"totalCount":1,"dataMap":{},"object":null}
     */
    public static void buyLocalGoods(Context context, String memberAccount,
                                     String customerName, String customerPhone, String customerAddress,
                                     ModelDiliver diliver, ModelPayment payment, String supplyId,
                                     String storeAddress, double retailOrderPrice,
                                     String retailProductManagerID, int shopNum, double productPrice,
                                     int orderType, OnNetworkListener onNetworkListener)
            throws JSONException {

        JSONArray data = new JSONArray();

        JSONObject dataObject = new JSONObject();
        dataObject.put("retailProductManagerID", retailProductManagerID);
        dataObject.put("orderType", orderType);
        dataObject.put("shopNum", shopNum);
        dataObject.put("productPrice", productPrice);
        data.put(dataObject);

        JSONArray deliveryInfo = new JSONArray();

        JSONObject deliveryInfoObject = new JSONObject();
        deliveryInfoObject.put("supplyId", supplyId);
        deliveryInfoObject.put("deliveryType", diliver.getName());
        switch (diliver.getType()) {
            case ModelDiliver.TYPE_HOME:
                deliveryInfoObject.put("address", customerAddress);
                break;
            case ModelDiliver.TYPE_SELF:
                deliveryInfoObject.put("address", storeAddress);
                break;
            default:
                break;
        }
        deliveryInfoObject.put("paymentType", payment.getType());
        deliveryInfo.put(deliveryInfoObject);
        buyLocalCarGoods(context, memberAccount, customerName, customerPhone,
                retailOrderPrice, data.toString(), deliveryInfo.toString(),
                onNetworkListener);
    }

    public static void buyLocalService(Context context, String memberNumber,
                                       String customerName, String customerPhone, String mustAddress, String deliveryAddress,
                                       String orderType, double orderPrice, String productId,
                                       int productNum, ModelDiliver diliver, ModelPayment payment,
                                       OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_SERVICE_ORDER_ADD);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters("memberNumber", memberNumber);
        networkContent.addParameters("customerName", customerName);
        networkContent.addParameters("customerPhone", customerPhone);
        networkContent.addParameters("orderType", orderType);
        networkContent.addParameters("orderPrice", orderPrice + "");
        networkContent.addParameters("productId", productId);
        networkContent.addParameters("productNum", productNum + "");
        networkContent.addParameters("deliveryType", diliver.getName());
        networkContent.addParameters("paymentType", payment.getName());
        switch (diliver.getType()) {
            case ModelDiliver.TYPE_SELF:
                networkContent.addParameters("mustAddress", mustAddress);
                break;

            case ModelDiliver.TYPE_HOME:
                networkContent.addParameters("deliveryAddress", deliveryAddress);
                break;

            default:
                networkContent.addParameters("mustAddress", mustAddress);
                break;
        }
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getUserInfo(Context context, String customerPhone,
                                   OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_USER_INFO_GET);
        networkContent.addParameters("machine", Device.getDeviceCode());
        networkContent.addParameters("phone", customerPhone);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    /**
     * @author Tiger
     * @Url http://beta.yitos.net
     * /api/member/memberManage/member/findOrAddMember
     * @Parameters [machine=bb7663f96e6ac6fda80ba8a1818613a6, phone=18048831449,
     * realname=雷小武, address=解放路二段六号凤凰大厦, idCard=]
     * @Result {"message":"ok","state":"SUCCESS","cacheKey" :null,"dataList":
     * [],"totalCount":1,"dataMap":{},"object":null}
     */
    public static void registerUser(Context context, String customerPhone,
                                    String customerName, String customerAddress,
                                    OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_USER_REGISTER);
        networkContent.addParameters("machine", Device.getDeviceCode());
        networkContent.addParameters("phone", customerPhone);
        networkContent.addParameters("realname", customerName);
        networkContent.addParameters("address", customerAddress);
        networkContent.addParameters("idCard", "");
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    /**
     * 添加本地产品订单
     *
     * @author Tiger
     * @Result {"message":"ok","state"
     * :"SUCCESS","cacheKey":null,"dataList":[{"servicetelephone"
     * :"028-32562356"
     * ,"orderDate":"2015-08-04","servicename":null,"productInfo":
     * "[{\"spname\":\"测试运营中心一-测试小吃产品一\",\"price\":\"10.0\",\"Amount\":\"10.0\",\"num\":\"1\"}]"
     * ,"ordernumber":"YT3765246787","orderPrice":"10.0","servicePhone":
     * "15821346521"}],"totalCount":1,"dataMap":{},"object":null}
     */
    public static void buyLocalCarGoods(Context context, String memberAccount,
                                        String customerName, String customerPhone, double retailOrderPrice,
                                        String data, String deliveryInfo,
                                        OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_GOODS_ORDER_ADD);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters("memberAccount", memberAccount);
        networkContent.addParameters("customerName", customerName);
        networkContent.addParameters("customerPhone", customerPhone);
        networkContent.addParameters("retailOrderPrice", retailOrderPrice + "");
        networkContent.addParameters("data", data);
        networkContent.addParameters("deliveryInfo", deliveryInfo);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getStoreInfo(Context context, String no,
                                    OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_STORE_SEND_FEE);
        networkContent.addParameters("no", no);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

}