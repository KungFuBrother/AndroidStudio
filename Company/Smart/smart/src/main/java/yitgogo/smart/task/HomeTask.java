package yitgogo.smart.task;

import yitgogo.smart.tools.API;
import yitgogo.smart.tools.Device;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.OnNetworkListener;

import android.content.Context;

public class HomeTask {

    public static void getAds(Context context,
                              OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_ADS);
        networkContent.addParameters("machine", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getHomeBrand(Context context,
                                    OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_HOME_BRAND);
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getLocalGoods(Context context,
                                     OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_GOODS);
        networkContent.addParameters("pageNo", "1");
        networkContent.addParameters("pageSize", "6");
        networkContent.addParameters("shebei", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getLocalService(Context context,
                                       OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_SERVICE);
        networkContent.addParameters("pageNo", "1");
        networkContent.addParameters("pageSize", "6");
        networkContent.addParameters("shebei", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getLocalMiaosha(Context context,
                                       OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_SALE_MIAOSHA);
        networkContent.addParameters("jqm", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getLocalTejia(Context context,
                                     OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_SALE_TEJIA);
        networkContent.addParameters("jqm", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getLocalStore(Context context,
                                     OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_STORE_LIST);
        networkContent.addParameters("machine", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getLoveFresh(Context context, int pageNo, int pageSize, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_SERVICE_FRESH);
        networkContent.addParameters("pageNo", pageNo + "");
        networkContent.addParameters("pageSize", pageSize + "");
        networkContent.addParameters("shebei", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getMainClasses(Context context,
                                      OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_PRODUCT_CLASS_MAIN);
        networkContent.addParameters("shebei", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getMiaoshaProduct(Context context,
                                         OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_SALE_MIAOSHA);
        networkContent.addParameters("machine", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getNongfu(Context context,
                                 OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_LOCAL_BUSINESS_NONGFU);
        networkContent.addParameters("pagenum", "1");
        networkContent.addParameters("pagesize", "10");
        networkContent.addParameters("shebei", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getSaleTejia(Context context,
                                    OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_SALE_TEJIA);
        networkContent.addParameters("machine", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getSaleTheme(Context context,
                                    OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_SALE_ACTIVITY);
        networkContent.addParameters("machine", Device.getDeviceCode());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getSaleTimes(Context context,
                                    OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(API.API_SALE_CLASS);
        networkContent.addParameters("machine", Device.getDeviceCode());
        networkContent.addParameters("flag", "1");
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getScoreProduct(Context context,
                                       OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API.API_SCORE_PRODUCT_LIST);
        networkContent.addParameters("jqm", Device.getDeviceCode());
        networkContent.addParameters("pagenum", "1");
        networkContent.addParameters("pagesize", "8");
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }
}
