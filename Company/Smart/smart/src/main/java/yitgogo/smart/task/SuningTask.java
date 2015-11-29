package yitgogo.smart.task;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import yitgogo.smart.suning.model.API_SUNING;
import yitgogo.smart.suning.model.ModelProductPool;
import yitgogo.smart.suning.model.SuningManager;
import yitgogo.smart.tools.MissionController;
import yitgogo.smart.tools.NetworkContent;
import yitgogo.smart.tools.OnNetworkListener;

public class SuningTask {

    public static void getProductClass(Context context, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API_SUNING.API_PRODUCT_CALSSES);
        JSONObject data = new JSONObject();
        try {
            data.put("accessToken", SuningManager.getSignature().getToken());
            data.put("appKey", SuningManager.appKey);
            data.put("v", SuningManager.version);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkContent.addParameters("data", data.toString());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getProducts(Context context, String categoryId, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API_SUNING.API_PRODUCT_LIST);
        JSONObject data = new JSONObject();
        try {
            data.put("accessToken", SuningManager.getSignature().getToken());
            data.put("appKey", SuningManager.appKey);
            data.put("v", SuningManager.version);
            data.put("categoryId", categoryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkContent.addParameters("data", data.toString());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

    public static void getProductState(Context context, List<ModelProductPool> productPools, OnNetworkListener onNetworkListener) {
        NetworkContent networkContent = new NetworkContent(
                API_SUNING.API_PRODUCT_STATE);
        JSONArray array = new JSONArray();
        for (int i = 0; i < productPools.size(); i++) {
            array.put(productPools.get(i).getSku());
        }
        JSONObject data = new JSONObject();
        try {
            data.put("accessToken", SuningManager.getSignature().getToken());
            data.put("appKey", SuningManager.appKey);
            data.put("v", SuningManager.version);
            data.put("sku", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        networkContent.addParameters("data", data.toString());
        MissionController.startNetworkMission(context, networkContent,
                onNetworkListener);
    }

//    public static void buy(Context context, String , OnNetworkListener onNetworkListener) {
//        NetworkContent networkContent = new NetworkContent(
//                API_SUNING.API_PRODUCT_STATE);
//        JSONArray array = new JSONArray();
//        for (int i = 0; i < productPools.size(); i++) {
//            array.put(productPools.get(i).getSku());
//        }
//        JSONObject data = new JSONObject();
//        try {
//            data.put("accessToken", SuningManager.getSignature().getToken());
//            data.put("appKey", SuningManager.appKey);
//            data.put("v", SuningManager.version);
//            data.put("sku", array);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        networkContent.addParameters("data", data.toString());
//        MissionController.startNetworkMission(context, networkContent,
//                onNetworkListener);
//    }

}
