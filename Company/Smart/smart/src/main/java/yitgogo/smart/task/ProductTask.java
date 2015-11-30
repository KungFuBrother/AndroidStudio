package yitgogo.smart.task;

import android.content.Context;

import org.apache.http.NameValuePair;

import java.util.List;

import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.OnNetworkListener;

public class ProductTask {

    public static void getProducts(Context context,
                                   List<NameValuePair> nameValuePairs,
                                   OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_PRODUCT_LIST);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters(nameValuePairs);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getProductPrice(Context context, String productIds,
                                       OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_PRICE_LIST);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters("productId", productIds);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getLocalGoods(Context context,
                                     List<NameValuePair> nameValuePairs,
                                     OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_GOODS);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters(nameValuePairs);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getLocalService(Context context,
                                       List<NameValuePair> nameValuePairs,
                                       OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_SERVICE);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters(nameValuePairs);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    /**
     * 获取设备所在区域
     *
     * @author Tiger
     * @Json {"message":"ok","state":"SUCCESS","cacheKey":null,"dataList":[],
     * "totalCount"
     * :1,"dataMap":{"id":2421,"address":"四川成成都市金牛区解放路二段六号","areas"
     * :"中国,四川省,成都市,金牛区"},"object":null}
     */
    public static void getMachineArea(Context context,
                                      OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_MACHINE_AREA);
        networkContent.addParameters("machine", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getProductDetail(Context context, String productId,
                                        OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_PRODUCT_DETAIL);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        networkContent.addParameters("productId", productId);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getProductSaleMiaoshaDetail(Context context,
                                                   String productId, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_SALE_MIAOSHA_DETAIL);
        networkContent.addParameters("productId", productId);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getProductSaleTimeDetail(Context context,
                                                String productId, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_SALE_TIME_DETAIL);
        networkContent.addParameters("productId", productId);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getProductSaleTejiaDetail(Context context,
                                                 String productId, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_SALE_TEJIA_DETAIL);
        networkContent.addParameters("productId", productId);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getFreight(Context context, String productNumber, String areaid, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_PRODUCT_FREIGHT);
        networkContent.addParameters("productNumber", productNumber);
        networkContent.addParameters("areaid", areaid);
        networkContent.addParameters("machine", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent, onNetworkListener);
    }

}
