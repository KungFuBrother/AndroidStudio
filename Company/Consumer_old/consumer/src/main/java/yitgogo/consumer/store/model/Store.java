package yitgogo.consumer.store.model;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import yitgogo.consumer.tools.Content;
import yitgogo.consumer.tools.Parameters;

public class Store {

    public static Store store;

    String storeArea = "", storeId = "", storeNumber = "", storeName = "",
            storeContactor = "", storeAddess = "", phone = "";

    public static void init(Context context) {
        if (Content.getIntContent(Parameters.CACHE_KEY_STORE_TYPE, -1) == Parameters.CACHE_VALUE_STORE_TYPE_LOCATED) {
            // 定位获取的店铺数据
            try {
                ModelStoreLocated storeLocated = new ModelStoreLocated(
                        new JSONObject(Content.getStringContent(
                                Parameters.CACHE_KEY_STORE_JSONSTRING, "{}")));
                store = new Store(storeLocated);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (Content.getIntContent(Parameters.CACHE_KEY_STORE_TYPE, -1) == Parameters.CACHE_VALUE_STORE_TYPE_SELECTED) {
            // 手动选择的店铺类型
            try {
                ModelStoreSelected storeSelected = new ModelStoreSelected(
                        new JSONObject(Content.getStringContent(
                                Parameters.CACHE_KEY_STORE_JSONSTRING, "{}")));
                store = new Store(storeSelected, Content.getStringContent(
                        Parameters.CACHE_KEY_STORE_AREA, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Store() {
    }

    public Store(ModelStoreLocated storeLocated) {
        storeAddess = storeLocated.getAddress();
        storeArea = storeLocated.getProvince() + ">" + storeLocated.getCity()
                + ">" + storeLocated.getDistrict();
        storeContactor = storeLocated.getBossName();
        storeId = storeLocated.getJmdId();
        storeName = storeLocated.getTitle();
        storeNumber = storeLocated.getJmdNo();
        phone = storeLocated.getPhone();
    }

    public Store(ModelStoreSelected storeSelected, String area) {
        storeAddess = storeSelected.getServiceaddress();
        storeArea = area;
        storeContactor = storeSelected.getContacts();
        storeId = storeSelected.getId();
        storeName = storeSelected.getServicename();
        storeNumber = storeSelected.getNo();
        phone = storeSelected.getContactphone();
    }

    public String getStoreArea() {
        return storeArea;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getStoreNumber() {
        return storeNumber;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getStoreContactor() {
        return storeContactor;
    }

    public String getPhone() {
        return phone;
    }

    public String getStoreAddess() {
        return storeAddess;
    }

    public static Store getStore() {
        return store;
    }

}
