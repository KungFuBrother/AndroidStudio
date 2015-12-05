package yitgogo.consumer.store.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ModelStoreArea {

    String id = "", name = "";
    int type;

    public ModelStoreArea() {
    }

    public ModelStoreArea(String id, String name, int type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public ModelStoreArea(JSONObject object) {
        if (object != null) {
            if (object.has("id")) {
                if (!object.optString("id").equalsIgnoreCase("null")) {
                    id = object.optString("id");
                }
            }
            if (object.has("name")) {
                if (!object.optString("name").equalsIgnoreCase("null")) {
                    name = object.optString("name");
                }
            }
            if (object.has("type")) {
                if (!object.optString("type").equalsIgnoreCase("null")) {
                    type = object.optInt("type");
                }
            }
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
